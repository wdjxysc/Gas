package webapp.sockets.concentrateor.field;


import webapp.sockets.util.Protocol;
import webapp.sockets.util.StringTool;
import webapp.sockets.util.TimeTag;
import webapp.sockets.util.Tools;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 报文 ID
 *
 * @author Administrator
 */
public class MessageID implements Serializable {
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

    private static int serialNum = 0;

    public static MessageID createMessageID() {
        String dateStr = Tools.getDateBcdStr(new Date());
        if (serialNum > 99999999) serialNum = 0;
        serialNum++;//保证每条命令messageId不同
        String serialStr = StringTool.padLeft(serialNum + "", 8, '0');


        return new MessageID(dateStr.substring(2, 8) + serialStr);
    }
}
