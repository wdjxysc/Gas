package webapp.sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 方向和响应标识
 * @author Administrator
 *
 */
public class DirectionResponseFlag implements Serializable{
	private static final long serialVersionUID = 1L;

    private byte direction;

    private byte response;

    public DirectionResponseFlag(byte[] darf) {
        if (darf.length != 2) return;
        this.direction = darf[0];
        this.response = darf[1];
    }

    public DirectionResponseFlag(byte direction, byte response) {
        this.direction = direction;
        this.response = response;
    }

    public byte[] getIDirectionAndResPonseFlag() {
        return new byte[]{direction,response};
    }

    public void setIDirectionAdnResponseFlag(byte[] darf) {
        if(darf.length!= 2)return;
        direction = darf[0];
        response = darf[1];
    }

    public byte getResponse() {
        return response;
    }

    public void setResponse(byte response) {
        this.response = response;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }
}
