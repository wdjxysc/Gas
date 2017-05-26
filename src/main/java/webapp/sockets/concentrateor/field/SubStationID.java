package webapp.sockets.concentrateor.field;

import webapp.sockets.util.Protocol;
import webapp.sockets.util.Tools;

import java.io.Serializable;

/**
 * 从站编号  7字节
 * @author Administrator
 *
 */
public class SubStationID implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] id = null;
    public int length;

    Protocol p = Protocol.getInstance();

    public SubStationID(byte[] ssid) {
        if (ssid.length == 7) id = ssid;
        else
            id = null;
    }

    public SubStationID(String ssid) {
        if (ssid.length() != 14) {
            id = null;
        }
        this.id = p.hexStringToByte(ssid);
    }

    public void setSubStationId(String s) {

    }

    public byte[] getSubStationId() {
        return id;
    }

    public String getSubStationIdStr() {
        return Tools.Bytes2HexString(id,id.length);
    }
}
