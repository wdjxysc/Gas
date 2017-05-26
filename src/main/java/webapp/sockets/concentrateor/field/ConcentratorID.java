package webapp.sockets.concentrateor.field;

import webapp.sockets.util.Protocol;
import webapp.sockets.util.Tools;

import java.io.Serializable;

/**
 * 集中器编号 5字节
 * @author Administrator
 *
 */
public class ConcentratorID implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] id = null;
    public int length;

    Protocol p = Protocol.getInstance();

    public ConcentratorID(byte[] cId) {
        if (cId.length == 5) id = cId;
        else
            id = null;
    }

    public ConcentratorID(String cId) {
        if (cId.length() != 10) {
            id = null;
        }
        this.id = p.hexStringToByte(cId);
    }

    public void setConcentratorId(String s) {

    }

    public byte[] getConcentratorIdByte() {
        return id;
    }

    public String getConcentratorIdStr() {
        return Tools.Bytes2HexString(id,id.length);
    }
}
