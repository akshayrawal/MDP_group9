import socket
import time
import sys


class PcConnector(object):

        def __init__(self):
                self.tcp_ip = "192.168.9.9" # RPI IP address
                self.port = 5182
                self.conn = None
                self.client = None
                self.addr = None
                self.pc_is_connect = False


        def close_pc_socket(self):

                if self.conn:
                        self.conn.close()
                        print "Closing server socket"
                if self.client:
                        self.client.close()
                        print "Closing client socket"
                self.pc_is_connect = False


        def pc_is_connected(self):
                return self.pc_is_connect


        def init_pc_comm(self):
                # Create a TCP/IP socket
                try:
                        self.conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        self.conn.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  #important to allow reuse of IP
                        self.conn.bind((self.tcp_ip, self.port))
                        self.conn.listen(1)                                              #Listen for incoming connections
                        print "Listening for incoming connections from PC..."
                        self.client, self.addr = self.conn.accept()
                        print "Connected! Connection address: ", self.addr
                        self.pc_is_connect = True
                        self.write_to_PC("RPI Ready\n")

                except Exception, e:    #socket.error:
                        print "\nError: %s" % str(e)


        def write_to_PC(self, message):
                
                try:
                        self.client.sendto(message, self.addr)
                        print "Sent to PC: %s" % message

                except Exception ,e:
                        print "\nPC Write Error: %s " % str(e)
                        self.close_pc_socket()
                        self.init_pc_comm()


        def read_from_PC(self):

                try:
                        pc_data = self.client.recv(2048)
                        print "Read from PC: %s" %pc_data
                        return pc_data

                except Exception, e:
                        print "\nPC Read Error: %s " % str(e)
                        self.close_pc_socket()
                        self.init_pc_comm()




### Test wifi (Host) -- To test client use pc_test_socket.py

# if __name__ == "__main__":
#         print "main"
#         pc = PcAPI()
#         pc.init_pc_comm()

#         send_msg = "RPI Ready\n"
#         print "write_to_PC(): %s " % send_msg
#         pc.write_to_PC(send_msg)

#         while True:                

#                 print "read"
#                 msg = pc.read_from_PC()
#                 print "data received: %s " % msg
#                 print "write_to_PC(): %s " % send_msg
#                 pc.write_to_PC(send_msg)

#         print "closing sockets"
#         pc.close_pc_socket()
