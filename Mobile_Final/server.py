import base64
import socket

import cv2
import imutils

camera = cv2.VideoCapture(0) # 0 select first camera

# UDP (realtime)
BUFF_SIZE = 65536
server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, BUFF_SIZE)
host_name = socket.gethostname()
host_ip = socket.gethostbyname(host_name)
print(host_ip)
port = 9999
# socket_address = (host_ip, port)
# server_socket.bind(socket_address)
socket_address = ('192.168.1.11', port)
server_socket.bind(socket_address)
print('Listening at: ', socket_address)

while True:
    msg, client_adr = server_socket.recvfrom(BUFF_SIZE)
    print('GOT connection from', client_adr)
    WIDTH = 400
    while True:
        _, frame = camera.read() #return 2 values, True: camera can read. False: can not read. Use when read video
        frame = imutils.resize(frame, width=WIDTH)
        encoded, buffer = cv2.imencode('.jpg', frame)
        message = base64.b64encode(buffer)
        server_socket.sendto(message, client_adr)

        cv2.imshow('frame', frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break



