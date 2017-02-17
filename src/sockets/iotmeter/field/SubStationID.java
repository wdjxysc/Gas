package sockets.iotmeter.field;

import sockets.iotmeter.protocol.Protocol;

import java.io.Serializable;

/**
 * 从站ID
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

    public byte[] getSubStationIdByte() {
        return id;
    }
}
