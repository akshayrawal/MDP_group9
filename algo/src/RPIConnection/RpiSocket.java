package RPIConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class RpiSocket {

	private int port; 
	private String ipAddress;
	private Socket socket;
	private String message = "";
	private volatile boolean isStarted = false, isConnecting = false, isConnected = false, isReconnecting = false;
	private volatile boolean isSending = false;
	public String receivedMessage="";
	BufferedReader input;
	RpiConnect response;
	SocketListener sl;
	SocketWriter sw;
	SocketHandler sh;
	RpiConnect rc;
	
	public RpiSocket(String ipAddress, int port, RpiConnect rc) {
		this.port = port;
		this.ipAddress = ipAddress;
		this.socket  = new Socket();
		this.rc = rc;
	}

	public void connect() {
		isStarted = true;
		isConnecting = true;

		// Start Connection in Separate Thread
		sh = new SocketHandler();
		sh.start();
	}

	public void reconnect() {
		if (!isReconnecting) {
			isReconnecting = true;
			isConnected = false;
			// Start Connection in Separate Thread
			sh = new SocketHandler();
			sh.start();
		}
	}

	public void sendMessage(String message) {
		this.message = message;
		isSending = true;
	}

	private void setupConnection() {
		try {
			System.out.println("Creating connection between PC and RPI");
			this.socket = new Socket();
			socket.connect(new InetSocketAddress(ipAddress, port), 30000);
			
			if (this.socket == null) {
				System.out.println("Failed to create connection");
			} else {
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				if (isConnecting) {
					isConnected = true;
					System.out.println("Connection between PC and RPI is up");
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host");
		} catch (IOException e) {
			System.out.println("Setup Connection Failed");
		}
	}

	class SocketHandler extends Thread {
		public void run() {
			while (!isConnected) {
				setupConnection();
			}

			// Is Connecting & Is Connected
			if (isConnecting) {
				// Listen in Separate Thread
				sl = new SocketListener();
				sl.start();

				// Write in Separate Thread
				sw = new SocketWriter();
				sw.start();

				isConnecting = false;
			}
		}
	}

	class SocketListener extends Thread {
		public void run() {
			String serverResponse = null;
			while (isStarted && isConnected) {
				try {
					if ((serverResponse = input.readLine()) != null) {
						if (serverResponse.equals("Reconnecting") && isConnected){
							PrintWriter dop = new PrintWriter(socket.getOutputStream(),
									true);
							dop.println("Reconnected");
							dop.flush();
						}else{
							rc.messageReceived(serverResponse);
						}
					}

				} catch (IOException e) {
					System.out.println("Reading Exception");
					break;
				}
			}
			reconnect();
		}
	}

	class SocketWriter extends Thread {
		public void run() {
			while (isStarted && isConnected) {
				try {
					if (isSending && socket.getOutputStream() != null) {
						System.out.printf("Sending: %s\n", message);
						PrintWriter dop = new PrintWriter(
								socket.getOutputStream(), true);
						dop.println(message);
						dop.flush();
						if (!dop.checkError()) {
							isSending = false;		
						}
					}
				} catch (IOException e) {
					System.out.println("Writing Exception");
					break;
				}
			}
			reconnect();
		}
	}
	
	
}
