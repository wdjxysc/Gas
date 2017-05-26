package webapp.sockets.concentrateor.field;

import webapp.sockets.util.Protocol;

import java.io.Serializable;

/**
 * 采集器编号 5字节
 * @author Administrator
 *
 */
public class CollectorID implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] id = null;
    public int length;

    Protocol p = Protocol.getInstance();

    public CollectorID(byte[] cId) {
        if (cId.length == 5) id = cId;
        else
            id = null;
    }

    public CollectorID(String cId) {
        if (cId.length() != 10) {
            id = null;
        }
        this.id = p.hexStringToByte(cId);
    }

    public void setCollectorId(String s) {

    }

    public byte[] getCollectorIdByte() {
        return id;
    }
}
