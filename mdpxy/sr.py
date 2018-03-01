import serial
import time


class SerialConnector(object):
        def __init__(self):
                self.port = '/dev/ttyUSB0'
                self.baud_rate = 115200
                self.service = None
                self.sr_is_connect = False


        def connect_serial(self):

                try:
                        self.service = serial.Serial(self.port, self.baud_rate)
                        self.sr_is_connect = True
                        print "Serial link connected"

                except Exception, e:
                        print "\nError (Serial): %s " % str(e)


        def sr_is_connected(self):
                return self.sr_is_connect


        def close_sr_socket(self):
                if (self.service):
                        self.service.close()
                        self.sr_is_connect = False
                        print "Closing serial socket"


        def write_to_serial(self, msg):

                try:
                        self.service.write(msg)
                        # print "Write to arduino: %s " % msg

                except Exception, e:
                        print "\nError Serial Write: %s " % str(e)
                        self.close_sr_socket()
                        time.sleep(2)
                        self.connect_serial()


        def read_from_serial(self):

                try:
                        received_data = self.service.readline()
                        # print "Received from arduino: %s " % received_data
                        return received_data

                except Exception, e:
                        print "\nError Serial Read: %s " % str(e)
                        self.close_sr_socket()
                        time.sleep(2)
                        self.connect_serial()


# Below is for testing purpose

# if __name__ == "__main__":
#         print "Running Main"
#         sr = SerialAPI()
#         sr.connect_serial()
#         print "serial connection successful"

#         while True:
                
#                 send_msg = raw_input()
#                 print "Writing [%s] to arduino" % send_msg
#                 sr.write_to_serial(send_msg)

#                 print "read"
#                 print "data received '%s' from serial" % sr.read_from_serial()

#         print "closing sockets"
#         sr.close_sr_socket()


