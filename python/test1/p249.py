from socketserver import TCPServer, ForkingMixIn, StreamRequestHandler

class Server(ForkingMixIn , TCPServer):pass

class Handler(StreamRequestHandler):
    def handle(self):
        addr=self.request.getpeername()
        print('Got connectiong from ',addr)
        self.wfile.write(b'Thank you for connecting')

server=Server(('',1234), Handler)
server.serve_forever()

