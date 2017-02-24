package webapp.sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 帧长度
 * @author Administrator
 *
 */
public class FrameLength implements Serializable{
	private static final long serialVersionUID = 1L;

    private byte[] frameLength;

    public FrameLength(byte[] fl) {
        this.frameLength = fl;
    }


    public byte[] getFrameLength() {
        return frameLength;
    }

    public void setFrameLength(byte[] frameLength) {
        this.frameLength = frameLength;
    }
}
