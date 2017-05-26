package webapp.sockets.concentrateor.field;

import webapp.sockets.util.Tools;

import java.io.Serializable;

/**
 * 数据域长度
 * @author Administrator
 *
 */
public class DataFieldLength implements Serializable{
    private byte[] frameLength = {0x00, 0x00};

    public DataFieldLength(byte[] frameLength) {
        this.frameLength = frameLength;
    }

    public DataFieldLength(int length) throws Exception {
        if (length >= 0x10000) throw new Exception("数据长度设置超过范围！");
        this.frameLength[0] = (byte) (length % 0x100);
        this.frameLength[1] = (byte) (length / 0x100);
    }

    public int getLengthInt() {
        return (frameLength[0]&0xff) + ((frameLength[1]&0xff)<<8);
    }

    public byte[] getFrameLength() {
        return frameLength;
    }
}
