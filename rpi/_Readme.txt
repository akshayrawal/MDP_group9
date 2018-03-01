
Network drive: pi 
password: raspberry

WIFI NETWORK:
Network name: MDPGrp9 
password:     akshay2018


MDPGrp9												
------                          
rpi address: 192.168.9.9		
range start: 192.168.9.1		
range end  : 192.168.9.20	

PC
--------
tcpkill -i wlan0 port 5182


Bluetooth
--------
**Inside python script**
subprocess.Popen(['sh','./blueReset.sh'])    

**Inside RPI**
sudo python blueReset.sh 
------------------------------
sudo rfcomm release 4

**Make RPi visible to bluetooth device**
------------------------------
sudo hciconfig hci0 piscan

**Listen for connections on channel 4**
------------------------------
sudo rfcomm listen /dev/rfcomm4 4


Minicom
------------------------------
**open minicom interface**
minicom

