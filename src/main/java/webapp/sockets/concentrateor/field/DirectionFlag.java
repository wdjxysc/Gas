package webapp.sockets.concentrateor.field;

import java.io.Serializable;

/**
 * 发送方向标识
 * @author Administrator
 *
 */
public class DirectionFlag implements Serializable{
	private static final long serialVersionUID = 1L;

    private byte directionFlag;

    public static final byte FLAG_CENTER_TO_CLIENT = 0;
    public static final byte FLAY_CLINET_TO_CENTER = 1;

    public DirectionFlag(byte df) {
        this.directionFlag = df;
    }

    public byte getDirectionFlag() {
        return directionFlag;
    }

    public void setDirectionFlag(byte directionFlag) {
        this.directionFlag = directionFlag;
    }
}
