package webapp.sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 请求标识
 * @author Administrator
 *
 */
public class RequiredFlag implements Serializable{
	private static final long serialVersionUID = 1L;

    private byte requiredFlag;

    public RequiredFlag(byte rf) {
        this.requiredFlag = rf;
    }

    public byte getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(byte rf) {
        this.requiredFlag = rf;
    }
}	
