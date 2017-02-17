package sockets.iotmeter.field;

import sockets.iotmeter.protocol.Protocol;
import sockets.iotmeter.util.ByteToString;

import java.io.Serializable;


/**
 * crc16校验码
 * @author Administrator
 *
 */
public class CrcCode implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] crc16 = new byte[2];


    public CrcCode(byte[] crc) {
        if (crc.length != 2) {

        } 
        else {
            crc16[0] = crc[0];
            crc16[1] = crc[1];
        }
    }

    public byte[] getCrcCodeByte() {
        if (crc16 == null) return null;
        return crc16;
    }

    /**
     * 获取crc16校验码
     * @return
     */
    public String getCrcCodeString() {
        Protocol protocol = Protocol.getInstance();
        ByteToString bts = new ByteToString();
        String result = bts.getByteArrayToString(crc16);
        return result;
    }
}
