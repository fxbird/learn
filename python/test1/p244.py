import socket

s=socket.socket()

host=socket.gethostname()
port=1234
s.bind((host,port))

print('host : ',host)
s.listen(5)
while True:
    c,addr=s.accept()
    print('Got connection from ',addr)
    c.send(b'Thank you for connectiong')
    c.close