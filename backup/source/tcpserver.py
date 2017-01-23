import logging
import signal
import tornado.httpserver
import datetime
from tornado import gen


logger = logging.getLogger(__name__)


class Server(tornado.httpserver.TCPServer):
    def __init__(self, io_loop=None, ssl_options=None, **kwargs):

        logger.debug('tcp server started')

        if not io_loop:
            io_loop = tornado.ioloop.IOLoop.instance()

        self.io_loop = io_loop
        self.streams = []

        self.io_loop.add_timeout(datetime.timedelta(seconds=5), self.write_to_all_stream)

        tornado.httpserver.TCPServer.__init__(
            self, io_loop=io_loop, ssl_options=ssl_options, **kwargs
        )        


    def handle_stream(self, stream, address):
        logger.debug('New connection from %s' % str(address))
        self.streams.append(stream)


    def on_write_stream(self):
        logger.debug('written to stream')


    #@gen.coroutine
    def write_to_all_stream(self):
        logger.debug('writing to all streams')

        for s in self.streams:
            if s.closed():
                logger.debug('Stream is closed!')
            else:
                result = s.write('hello', self.on_write_stream)
                #result = yield s.write('hello')

                logger.debug('result: %s' % str(result))


def configure_signals():
    def bye_handler(signal, frame):
        logger.info('interrupt signal received, shutting down')

        io_loop = tornado.ioloop.IOLoop.instance()
        io_loop.stop()

    signal.signal(signal.SIGINT, bye_handler)
    signal.signal(signal.SIGTERM, bye_handler)


def configure_logging():
    logging.basicConfig(
        filename=None,
        level=logging.DEBUG,
        format='%(asctime)s: %(levelname)7s: [%(name)s]: %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S',
    )


if __name__ == '__main__':
    configure_signals()
    configure_logging()

    loop = tornado.ioloop.IOLoop.instance()

    logger.debug('hello')

    s = Server().listen(8888, '0.0.0.0')

    loop.start()

    logger.debug('bye')
