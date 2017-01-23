from urllib2 import urlopen
import time
import json
import re
import websocket
import thread
import time
import pyaudio
import base64
BITRATE = 44100
CHUNK = 1024
p = pyaudio.PyAudio()
stream = p.open(format = pyaudio.paInt16,
            channels = 1,
            rate = BITRATE,
            output = True,
            frames_per_buffer=CHUNK,
            output_device_index=3)
for i in range(0, p.get_device_count()):
    print(i, p.get_device_info_by_index(i)['name'])

def on_message(ws, message):
    message = base64.b64decode(message)
    stream.write(message)

def on_error(ws, error):
    print error

def on_close(ws):
    print "### closed ###"

def on_open(ws):
    def run(*args):
        while True:
            my_ip = data = re.search('"([0-9.]*)"',urlopen("http://ip.jsontest.com/").read()).group(1)
            #ws.send(my_ip)
            time.sleep(10)
    thread.start_new_thread(run, ())


if __name__ == "__main__":
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp("ws://server.x-io.in:8888/stream/cd69ad9f-e08a-41e0-ab9f-cbe4b953c7dc/inof",
                                on_message = on_message,
                                on_error = on_error,
                                on_close = on_close)
    ws.on_open = on_open

    ws.run_forever()
