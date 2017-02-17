package sockets.iotmeter.field;


import sockets.iotmeter.protocol.Protocol;

import java.io.Serializable;

/**
 * 报文
 * @author Administrator
 *
 */
public class MessageID implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] messageID = null;
    Protocol protocol = Protocol.getInstance();

    public MessageID(byte[] msgID) {
        if (msgID.length != 7) return;
        messageID = msgID;
    }

    public MessageID(String msgID) {
        if (msgID.length() != 14) ;
        messageID = protocol.stringToBcdByte(msgID);
    }

    public byte[] getMessageID() {
        return messageID;
    }

    public void setMessageID(byte[] msgID) {
        if (msgID.length != 7) ;
        messageID = msgID;
    }
}
