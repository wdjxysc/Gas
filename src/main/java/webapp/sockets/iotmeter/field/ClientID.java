package webapp.sockets.iotmeter.field;

import webapp.sockets.util.Protocol;

import java.io.Serializable;

/**
 * 从站ID
 * @author Administrator
 *
 */
public class ClientID implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] id = null;
    public int length;

    Protocol p = Protocol.getInstance();

    public ClientID(byte[] ssid) {
        if (ssid.length == 7) id = ssid;
        else
            id = null;
    }

    public ClientID(String ssid) {
        if (ssid.length() != 14) {
            id = null;
        }
        this.id = p.hexStringToByte(ssid);
    }

    public void setSubStationId(String s) {

    }

    public byte[] getSubStationIdByte() {
        return id;
    }
}
