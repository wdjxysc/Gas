package webapp.sockets.iotmeter.field;

/**
 * 长度类
 * @author Administrator
 *
 */
public class LengthClass {
	private byte[] length;

    public LengthClass(byte[] length) {
        this.length = length;
    }

    public byte[] getLengthByte() {
        return length;
    }

    public void setLengthCode(byte[] lc) {
        if (lc.length != 2 || lc == null)
            return;
        this.length[0] = lc[0];
        this.length[1] = lc[1];

    }

    public int getLenghInt() {
        int len = length[1] & 0xff;
        return len;
    }
}
