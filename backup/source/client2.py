import websocket
import subprocess
import base64

HOST = 'ws://server.x-io.in:8888/stream/cd69ad9f-e08a-41e0-ab9f-cbe4b953c7dc/io'

#cmd = "ffmpeg -threads 4 -f alsa -i hw:1 -b:v 64k -bufsize 64k  -f mp3 pipe:1"
cmd = "ffmpeg -f alsa -i hw:1   -acodec pcm_s16le -ac 1 -ar 44100 -f s16le  pipe:1"

s = websocket.create_connection(HOST)



data1 = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True, universal_newlines=False)
while True:
    stdoutdata = data1.stdout.read(1024)
    if stdoutdata:
        encoded = base64.b64encode(stdoutdata)
        s.send(encoded) #, opcode=websocket.ABNF.OPCODE_BINARY)
