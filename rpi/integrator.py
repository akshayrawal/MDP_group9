############################################################################################################
#                  Destination of different devices: a = android   h = arduino    p = pc                   #
#   PC / Android / Arduino will call the Read/Write methods from respective classes (bt.py, sr.py, pc.py)  #
############################################################################################################

import sys
import time
import Queue
import threading
from pc import *
from bt import *
from sr import *


class Main(threading.Thread):

        def __init__(self):
                threading.Thread.__init__(self)

                self.bt_thread = AndroidConnector()
                self.pc_thread = PcConnector()
                self.sr_thread = SerialConnector()

                # Initialize connections according to order below
                self.bt_thread.connect_bluetooth()
                self.pc_thread.init_pc_comm()
                self.sr_thread.connect_serial()
                time.sleep(1)

        # PC Functions #
        
        def writePC(self, msg_to_pc):
                
                if self.pc_thread.pc_is_connected() and msg_to_pc:
                        
                        self.pc_thread.write_to_PC(msg_to_pc)
                        print "Message sent to [PC]: %s" % msg_to_pc
                        return True
                return False                         

             
        def readPC(self):
                
                while True:
                        read_pc_msg = self.pc_thread.read_from_PC()

                        if self.pc_thread.pc_is_connected() and read_pc_msg:
                            f_letter = 0

                            # Msg Received when PC is Ready #
                            if(read_pc_msg[f_letter].lower() == 'r'):
                                    print "\nMessage from [PC] --> [RPI]: %s" % read_pc_msg [1:]

                            # PC to Android #                              
                            elif(read_pc_msg[f_letter].lower() == 'a'):

                                    print "\nMessage from [PC] --> [ANDROID]: %s" % read_pc_msg [1:]
                                    pc_msgSent = self.writeBT( read_pc_msg [1:] )
                                    pc_reply = self.writePC("OK\n")


                            # PC to Arduino #
                            elif(read_pc_msg[f_letter].lower() == 'h'):
                                                
                                    print "\nMessage from [PC] --> [ROBOT]: %s" % read_pc_msg [1:] 
                                    pc_msgSent = self.writeSR( read_pc_msg [1:] )

                                    # if not pc_msgSent:                                      # if message unable to be send                              
                                    #         pc_resend = False               
                                    #         while not pc_resend:                            # continue to ask resend until sender received
                                    #                 pc_resend = self.writePC("resend\n")     
                                    #         print "\nRPI sent 'resend' to [PC]"                                                        
                            else:
                                    print "Incorrect header [%s] from PC: %s" %(read_pc_msg[1:]) 



        # Android/BT functions #
        
        def writeBT(self, msg_to_bt):
                
                if self.bt_thread.bt_is_connect() and msg_to_bt:

                        self.bt_thread.write_to_bt(msg_to_bt)
                        print "Message sent to [ANDROID]: %s" % msg_to_bt
                        return True
                return False

        
        def readBT(self):
                
                while True:
                        read_bt_msg = self.bt_thread.read_from_bt()

                        if  self.bt_thread.bt_is_connect() and read_bt_msg :
                                
                                # Android to PC #
                                if(read_bt_msg[0].lower() == 'p'):      

                                        print "\nMessage from [ANDROID] --> [PC]: %s" % read_bt_msg[1:]
                                        bt_msgSent = self.writePC(read_bt_msg[1:])   
                                
                                        # if not bt_msgSent:
                                        #         bt_resend = False
                                        #         while not bt_resend:
                                        #                bt_resend = self.writeBT("resend")
                                        #         print "\nRPI sent 'resend' to [ANDROID]" 

                                # Android to Arduino       
                                elif(read_bt_msg[0].lower() == 'h'): 

                                        print "\nMessage from [ANDROID] --> [ROBOT]: %s" % read_bt_msg[1:]
                                        bt_msgSent = self.writeSR(read_bt_msg[1:])

                                        # if not bt_msgSent:
                                        #        bt_resend = False
                                        #        while not bt_resend:
                                        #                bt_resend = self.writeBT("resend")
                                        #        print "\nRPI sent 'resend' to [ANDROID]"                                        
                                else:
                                        print "Incorrect header [%s] from BT: %s" %(read_bt_msg[0] ,read_bt_msg)
                                        
        # Serial Comm functions #
        
        def writeSR(self, msg_to_sr):

                if self.sr_thread.sr_is_connected() and msg_to_sr:
                     
                        self.sr_thread.write_to_serial(msg_to_sr)
                        print "Message sent to [ROBOT]: %s" % msg_to_sr
                        return True
                return False

             
        def readSR(self):

                while True:
                        read_sr_msg = self.sr_thread.read_from_serial()

                        if self.sr_thread.sr_is_connected() and read_sr_msg:

                                # Robot to PC #
                                if(read_sr_msg[0].lower() == 'p'):      

                                        print "\nMessage from [ROBOT] --> [PC]: %s" % read_sr_msg[1:]
                                        sr_msgSent = self.writePC(read_sr_msg[1:])   

                                        # if not sr_msgSent:
                                        #         sr_resend = False
                                        #         while not sr_resend:
                                        #                sr_resend = self.writeSR("resend")
                                        #         print "\nRPI sent 'resend' to [ROBOT]" 


                                # Robot to Android #
                                elif(read_sr_msg[0].lower() == 'a'):            

                                        print "\nMessage from [ROBOT] --> [ANDROID]: %s" % read_sr_msg[1:]
                                        self.writeBT(read_sr_msg[1:])           

                                        # if not sr_msgSent:
                                        #         sr_resend = False
                                        #         while not sr_resend:
                                        #                sr_resend = self.writeSR("resend")
                                        #         print "\nRPI sent 'resend' to [ROBOT]"  
                                else:
                                        print "Incorrect header from SR: %s" % read_sr_msg       

                   

        def initialize_threads(self):

                # PC read and write thread
                readt_pc = threading.Thread(target = self.readPC, name = "pc_read_thread")
                writet_pc = threading.Thread(target = self.writePC, args = ("",), name = "pc_write_thread")
                
                # Bluetooth (BT) read and write thread
                readt_bt = threading.Thread(target = self.readBT, name = "bt_read_thread")
                writet_bt = threading.Thread(target = self.writeBT, args = ("",), name = "bt_write_thread")

                # Serial (SR) read and write thread
                readt_sr = threading.Thread(target = self.readSR, name = "sr_read_thread")
                writet_sr = threading.Thread(target = self.writeSR, args = ("",), name = "sr_write_thread")

                # Set threads as daemons
                readt_pc.daemon = True
                writet_pc.daemon = True
                readt_bt.daemon = True
                writet_bt.daemon = True
                readt_sr.daemon = True
                writet_sr.daemon = True
        
                # Start Threads
                readt_pc.start()
                writet_pc.start()
                readt_bt.start()
                writet_bt.start()
                readt_sr.start()
                writet_sr.start()
                print "All threads initialized successfully"


        def close_all_sockets(self):
                pc_thread.close_all_pc_sockets()
                bt_thread.close_all_bt_sockets()
                sr_thread.close_all_sr_sockets()
                print "End threads"


        def keep_main_alive(self):
                while True:
                        time.sleep(1)



if __name__ == "__main__":
        realRun = Main()
        realRun.initialize_threads()
        realRun.keep_main_alive()
        realRun.close_all_sockets()
