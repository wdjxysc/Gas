package webapp.test;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/6/9.
 */
public class NIOServer {
    private static final int TIMEOUT  = 1000;
    private static final int PORT     = 12112;

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel listenChannel = ServerSocketChannel.open();
            listenChannel.configureBlocking(false);
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(true) {
                if(selector.select(TIMEOUT)==0) {
                    System.out.println(".");
                    continue;
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while( iterator.hasNext() ) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    //Server socket channel has pending connection request?
                    if( key.isAcceptable() ) {
                        SocketChannel channel=listenChannel.accept();
                        channel.configureBlocking(false);
                        SelectionKey connKey=channel.register(selector, SelectionKey.OP_READ );
                        NIOServerConnection conn=new NIOServerConnection(connKey);
                        connKey.attach(conn);
                    }

                    if( key.isReadable() ) {
                        NIOServerConnection conn=(NIOServerConnection) key.attachment();
                        conn.handleRead();
                    }

                    if( key.isValid() && key.isWritable() ) {
                        NIOServerConnection conn=(NIOServerConnection) key.attachment();
                        conn.handleWrite();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
