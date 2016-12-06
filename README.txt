http://andrewmichaelsmith.com/2013/08/raspberry-pi-wi-fi-honeypot/
https://frillip.com/using-your-raspberry-pi-3-as-a-wifi-access-point-with-hostapd/

https://github.com/sbidolach/video-live-streaming/blob/master/public/js/jsmpg.js

http://phoboslab.org/log/2013/09/html5-live-video-streaming-via-websockets


http://raspberrypi.stackexchange.com/questions/32129/controlling-a-fan-on-a-relay-with-python


http://www.akadia.com/services/ssh_test_certificate.html

https://github.com/Pakhee/Cross-platform-AES-encryption/blob/master/Android/Usage.txt
http://stackoverflow.com/questions/12524994/encrypt-decrypt-using-pycrypto-aes-256


https://github.com/elabs/mobile-websocket-example/blob/master/android/WebsocketExampleClientProject/WebsocketExampleClient/src/main/java/se/elabs/websocketexampleclient/MainActivity.java

74518996294114573589564
LINIO COM MX
12239.00
6359760199


================================================================================
Client for Android
https://github.com/CoolMicApp/CoolMicApp-Android/tree/master/app/src/main/jni
================================================================================

Install hostapd

apt-get remove hostapd dnsmasq -y
apt-get install hostapd dnsmasq -y
apt-get install nginx -y


systemctl disable hostapd
systemctl enaable hostapd

sudo vi /etc/network/interfaces
================================================================================
allow-hotplug wlan0
iface wlan0 inet static
    address 172.24.1.1
    netmask 255.255.255.0
    network 172.24.1.0
    broadcast 172.24.1.255
#    wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf

================================================================================

sudo vi /etc/hostapd/hostapd.conf

================================================================================
# This is the name of the WiFi interface we configured above
interface=wlan0
# Use the nl80211 driver with the brcmfmac driver
driver=nl80211
# This is the name of the network
ssid=Pi3-AP
# Use the 2.4GHz band
hw_mode=g
# Use channel 6
channel=6
# Enable 802.11n
ieee80211n=1
# Enable WMM
wmm_enabled=1
# Enable 40MHz channels with 20ns guard interval
ht_capab=[HT40][SHORT-GI-20][DSSS_CCK-40]
# Accept all MAC addresses
macaddr_acl=0
# Use WPA authentication
auth_algs=1
# Require clients to know the network name
ignore_broadcast_ssid=0
# Use WPA2
wpa=2
# Use a pre-shared key
wpa_key_mgmt=WPA-PSK
# The network passphrase
wpa_passphrase=raspberry
# Use AES, instead of TKIP
rsn_pairwise=CCMP
================================================================================


sudo mv /etc/dnsmasq.conf /etc/dnsmasq.conf.orig

sudo vi /etc/dnsmasq.conf
================================================================================
interface=wlan0      # Use interface wlan0
listen-address=172.24.1.1 # Explicitly specify the address to listen on
bind-interfaces      # Bind to the interface to make sure we aren't sending things elsewhere
server=8.8.8.8       # Forward DNS requests to Google DNS
domain-needed        # Don't forward short names
bogus-priv           # Never forward addresses in the non-routed address spaces.
dhcp-range=172.24.1.50,172.24.1.150,12h # Assign IP addresses between 172.24.1.50 and 172.24.1.150 with a 12 hour lease time
================================================================================

sudo vi /etc/dhcpcd.conf
================================================================================
# A sample configuration for dhcpcd.
# See dhcpcd.conf(5) for details.
# Allow users of this group to interact with dhcpcd via the control socket.
#controlgroup wheel
# Inform the DHCP server of our hostname for DDNS.
hostname
# Use the hardware address of the interface for the Client ID.
clientid
# or
# Use the same DUID + IAID as set in DHCPv6 for DHCPv4 ClientID as per RFC4361.
#duid
# Persist interface configuration when dhcpcd exits.
persistent
# Rapid commit support.
# Safe to enable by default because it requires the equivalent option set
# on the server to actually work.
option rapid_commit
# A list of options to request from the DHCP server.
option domain_name_servers, domain_name, domain_search, host_name
option classless_static_routes
# Most distributions have NTP support.
option ntp_servers
# Respect the network MTU.
# Some interface drivers reset when changing the MTU so disabled by default.
#option interface_mtu
# A ServerID is required by RFC2131.
require dhcp_server_identifier
# Generate Stable Private IPv6 Addresses instead of hardware based ones
slaac private
# A hook script is provided to lookup the hostname if not set by the DHCP
# server, but it should not be run by default.
nohook lookup-hostname
denyinterfaces wlan0
================================================================================


sudo service hostapd start
sudo service dnsmasq start





================================================================================
================================================================================
================================================================================
================================================================================

source /etc/lsb-release && echo "deb http://download.rethinkdb.com/apt $DISTRIB_CODENAME main" | sudo tee /etc/apt/sources.list.d/rethinkdb.list
wget -qO- http://download.rethinkdb.com/apt/pubkey.gpg | sudo apt-key add -
sudo apt-get update
sudo apt-get install rethinkdb
sudo cp /etc/rethinkdb/default.conf.sample /etc/rethinkdb/instances.d/instance1.conf
sudo /etc/init.d/rethinkdb restart


================================================================================
================================================================================
================================================================================
================================================================================




https://github.com/RNCryptor/RNCryptor
