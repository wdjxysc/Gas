package sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 数据域长度
 * @author Administrator
 *
 */
public class DataFieldLength implements Serializable{
	private static final long serialVersionUID = 1L;

    private byte[] dataFieldLength = null;

    public DataFieldLength(byte[] dflb) {
        this.dataFieldLength = dflb;
    }

    public byte[] getDataFieldLength() {
        return dataFieldLength;
    }

    public void setDataFieldLength(byte[] dataFieldLength) {
        this.dataFieldLength = dataFieldLength;
    }
}
