package RPIConnection;

public class RpiConnect implements RpiToRobotData{
	
	private String receivedMessage = "";
	private volatile boolean hasReceived = false, isReadyToSend = false;
	private RpiSocket socket;

	public RpiConnect(){
		this.socket = new RpiSocket("192.168.9.9", 5182,this);
			socket.connect();  
			while (true) {
				if (receivedMessage.equals("RPI Ready")) {
						hasReceived = false;
						isReadyToSend = true;
					break;
				}
	
			}
			sendCommand("rReady");
	}
	
	public void messageReceived(String message) {
		receivedMessage = message.replace("\n", "").replace("\r", "");
		System.out.println("Received:  "+ receivedMessage);
		hasReceived = true;
	}

	//retrieve sensor reading
	public int[] getSensorReading(String response){
//		double[] raw = new double[6];
		int[] reading = new int[6];
		String[] readingArray = response.split("@");
		
		for(int i = 0; i < reading.length; i++)
			reading[i] = Integer.parseInt(readingArray[i]);
		return reading;
	}
	
//get message from android
	public String waitAndroidResponds(){
		while (true) {
			if (hasReceived){
				isReadyToSend = true;
				hasReceived = false;
				return receivedMessage;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public String[] getStartPositionFromAndroid(){
		String androidMessage = "";
			do
			{
				androidMessage = waitAndroidResponds();
			}while(!androidMessage.contains("@"));
			String[] startPosition = androidMessage.split("@");
			return startPosition;
	}
	
	public void getFastestPathCommandFromAndroid(){
		String strstart;
			strstart = waitAndroidResponds();
			while(!strstart.equals("fp"))
				 strstart = waitAndroidResponds();
		
		return;
	}
	
//	public void finishExplore(){
//		System.out.println("android->robot: finishExplore");
//			sendCommand("B<finish");
//	}
		
	
	public String sendCommand(String command){
		while (true) {
			
			// Wait to send
			if (isReadyToSend) {
				isReadyToSend = false;
				socket.sendMessage(command);
			}
			
			// Waiting for reply
			if (hasReceived){
				isReadyToSend = true;
				hasReceived = false;
				return receivedMessage;
			}
			//this may solve the data race problem
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
