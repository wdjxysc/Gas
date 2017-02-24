package webapp.sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 响应标识
 * @author Administrator
 *
 */
public class ResponseFlag implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] rf = null;

    public ResponseFlag(byte[] rfBytes) {
        if (rfBytes.length == 2) rf = null;
        else
            rf = null;
    }


    public byte[] getRf() {
        return rf;
    }

    public void setRf(byte[] rf) {
        this.rf = rf;
    }
}
