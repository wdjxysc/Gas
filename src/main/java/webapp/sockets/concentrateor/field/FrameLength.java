package webapp.sockets.concentrateor.field;

import webapp.sockets.util.Tools;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18.
 *
 * 报文长度
 *
 */
public class FrameLength implements Serializable {

    private byte[] frameLength = {0x00, 0x00};

    public FrameLength(byte[] frameLength) {
        this.frameLength = frameLength;
    }

    public FrameLength(int length) throws Exception {
        if (length >= 0x10000) throw new Exception("数据长度设置超过范围！");
        this.frameLength[0] = (byte) (length % 0x100);
        this.frameLength[1] = (byte) (length / 0x100);
    }

    public int getLengthInt() {
        return Tools.bytesToInt(frameLength);
    }

    public byte[] getFrameLength() {
        return frameLength;
    }
}
