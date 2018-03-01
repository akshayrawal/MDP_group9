
Network drive: pi 
password: raspberry

WIFI NETWORK:
Raspberry Pi 1:
Network name: MdpGrp05 
password:     raspberry05
Raspberry Pi 2:
Network name: MdpGrpTest05 
password:     raspberry05


MDPGrpTest05					MDPGrp05							
------                          -------
rpi address: 192.168.5.5		rpi address: 192.168.5.1
range start: 192.168.5.5		range start: 192.168.5.1
range end  : 192.168.5.60		range end  : 192.168.5.60


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
sudo hciconfig hci0 piscan
------------------------------