package webapp.test;

import webapp.sockets.util.Tools;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2017/6/9.
 */
public class NIOServerConnection {

    private static final int BUFF_SIZE = 1024;

    SelectionKey key;
    SocketChannel channel;
    ByteBuffer buffer;

    public NIOServerConnection(SelectionKey key) {
        this.key=key;
        this.channel=(SocketChannel) key.channel();
        buffer=ByteBuffer.allocate(BUFF_SIZE);
    }

    public void handleRead() throws IOException {
        long bytesRead=channel.read(buffer);

        channel.write(buffer);
        byte[] bytes = buffer.array();
        System.out.println("read:" + Tools.Bytes2HexString(bytes,bytes.length));

        if(bytesRead==-1) {
            channel.close();
        } else {
            key.interestOps( SelectionKey.OP_READ | SelectionKey.OP_WRITE );
        }
    }

    public void handleWrite() throws IOException {
        buffer.flip();
        channel.write(buffer);
        byte[] bytes = buffer.array();
        System.out.println("write:" + Tools.Bytes2HexString(bytes,bytes.length));

        if(!buffer.hasRemaining()) {
            key.interestOps( SelectionKey.OP_READ );
        }

        buffer.compact();
    }

}
