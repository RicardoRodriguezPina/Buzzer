import tornado.httpserver
import tornado.websocket
import tornado.ioloop
import tornado.web
import os
from tornado.options import define, options
from config import *
import rethinkdb as r
from rethinkdb.errors import RqlRuntimeError, RqlDriverError
from jinja2 import Environment, FileSystemLoader
import websocket


change = "original"

listeners = {}
servers = {}
streams = {}

db_connection = r.connect(RDB_HOST,RDB_PORT)

def dbSetup():
    print PROJECT_DB,db_connection
    try:
        r.db_create(PROJECT_DB).run(db_connection)
        print 'Database setup completed.'
    except RqlRuntimeError:
        try:
            r.db(PROJECT_DB).table_create(PROJECT_TABLE).run(db_connection)
            print 'Table creation completed'
        except:
            print 'Table already exists.Nothing to do'
        print 'App database already exists.Nothing to do'

    db_connection.close()

r.set_loop_type("tornado")

class IOControler(object):
    client = None
    buzzer = None
    def __init__(self):
        pass


class StreamHandler(tornado.websocket.WebSocketHandler):
  def open(self, id, type_in):
      self._id=id
      self.type = type_in
      if not self._id in streams:
          my_io=IOControler()
          if type_in =="io":
              my_io.buzzer = self
          else:
              my_io.client = self
          streams[self._id]=my_io

      else:
          if self.type =="io":
              streams[self._id].buzzer = self
          else:
              streams[self._id].client = self
      self._parent=streams[self._id]

  @tornado.gen.coroutine
  def on_message(self, message):
      if self.type =="io":
          if streams[self._id].client is not None:
              streams[self._id].client.write_message(message)
      else:
          if streams[self._id].buzzer is not None:
              streams[self._id].buzzer.write_message(message)

  def on_close(self):
      if self.type =="io":
          streams[self._id].buzzer = None
      else:
          streams[self._id].client = None

class ServerHandler(tornado.websocket.WebSocketHandler):
  def open(self, id):
      print "opened a new websocket for Server: " + id
      self._id=id
      servers[self._id]=self
      #print listeners

  @tornado.gen.coroutine
  def on_message(self, message):
      #self.write_message(u"You Said: " + message)
      print ("in on_message " + message)
      connection = r.connect(RDB_HOST, RDB_PORT, PROJECT_DB)
      threaded_conn = yield connection
      result = r.table(PROJECT_TABLE).insert({ "id": self._id , "ip" : message}, conflict="update").run(threaded_conn)
      print 'log: %s inserted successfully'%result
      if self._id  in listeners:
          for socSrv in listeners[self._id ]:
              socSrv.writemessage(message)


  def on_close(self):
      print 'connection closed'
      connection = r.connect(RDB_HOST, RDB_PORT, PROJECT_DB)
      threaded_conn = yield connection
      result = r.table(PROJECT_TABLE).insert({ "id": self._id , "ip" : 'none'}, conflict="update").run(threaded_conn)
      print 'log: %s inserted successfully'%result
      del servers[self._id]

  def writemessage(self, message):
      self.write_message(change)

class ClientHandler(tornado.websocket.WebSocketHandler):
  @tornado.gen.coroutine
  def open(self, id):
      print "opened a new websocket for Client: " + id
      self._id=id
      if self._id not in listeners:
          listeners[self._id]=[]
          listeners[self._id].append(self)
      else:
          listeners[self._id].append(self)
      print listeners

  def on_message(self, message):
      #self.write_message(u"You Said: " + message)
      print ("in on_message " + message)
      if self._id in servers:
          servers[self._id].writemessage(message)


  def on_close(self):
      print 'connection closed'
      del listeners[self._id][self]
      #listeners.remove(self)

  def writemessage(self, message):
      print ("in write message " + message)
      self.write_message(message)


def main():
    dbSetup()
    #tornado.options.parse_command_line()
    current_dir = os.path.dirname(os.path.abspath(__file__))
    static_folder = os.path.join(current_dir, 'static')
    application = tornado.web.Application([
    (r'/client/([^/]+)', ClientHandler),
    (r'/server/([^/]+)', ServerHandler),
    (r'/stream/([^/]+)/([^/]+)', StreamHandler)
     ])
    http_server = tornado.httpserver.HTTPServer(application)
    # , ssl_options={
    #     "certfile": "/opt/buzzr/server.crt"),
    #     "keyfile": "/opt/buzzr/server.key"),
    # })
    http_server.listen(80)

    #tornado.ioloop.IOLoop.current().add_callback(send_user_alert)
    tornado.ioloop.IOLoop.instance().start()

if __name__ == "__main__":
    main()
