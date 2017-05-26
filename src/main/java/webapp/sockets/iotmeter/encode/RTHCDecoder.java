package webapp.sockets.iotmeter.encode;


import webapp.sockets.iotmeter.field.*;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.HexToInt;

/**
 * 
 * @author Administrator
 *
 */
public class RTHCDecoder extends AbsDecoder{
	private static final long serialVersionUID = 1L;

    protected Protocol protocol = Protocol.getInstance();

    public static RTHCDecoder instance = null;

    public static RTHCDecoder getInstance() {
        if (instance == null) {
            instance = new RTHCDecoder();
        }
        return instance;
    }

    /**
     * 获取逻辑域的字节数组
     *
     * @param frame 待处理的帧
     * @param i     帧域的逻辑标识
     * @return
     */
    public Object getLogicFieldByteArray(byte[] frame, int i) {
        switch (i) {
            case 1:/**获得开始符*/
                return getBeginByte(frame);
            case 2:/**获得帧长度*/
                return getFrameLengthByte(frame);
            case 3:/**获得控制码*/
                return getControlCodeByte(frame);
            case 4:/**获得发送方向标识和响应标识*/
                return getDirectionAndResponseFlagByte(frame);
            case 5:/**获得从站ID*/
                return getSubStationIDByte(frame);
            case 6:/**获得报文ID*/
                return getMessageIDByte(frame);
            case 7:/**获得数据长度*/
                return getDataLenByte(frame);
            case 8:/**获得响应码*/
                if ((getDirectionAndResponseFlagByte(frame)[0] & 0xff) == 1) return getResponseFlagByte(frame);
            case 9:/**获得数据域*/
                return getDataFieldByte(frame);
            case 10:/**获得CRC16校验码*/
                return getCrcCodeByte(frame);
            case 11:/**获得结束符*/
                return getEndByte(frame);
            default:/**并没有什么*/
                return null;
        }

    }

    public Object getLogicFieldByteArray2(byte[] frame, int i) {
        switch (i) {
            case 1:/**获得开始符*/
                return getBeginByte(frame);
            case 2:/**获得帧长度*/
                return getFrameLengthByte(frame);
            case 3:/**获得控制码*/
                return getControlCodeByte(frame);
            case 4:/**获得传送方向标识*/
                return getDirectionFlagByte(frame);
            case 5:/**获得响应标识*/
                return getRequiredFlagByte(frame);
            case 6:/**获得从站ID*/
                return getSubStationIDByte(frame);
            case 8:/**获得报文ID*/
                return getMessageIDByte(frame);
            case 7:/**获得数据长度*/
                return getDataLenByte(frame);
            case 9:/**获得响应码：只有响应帧才有响应码*/
                if ((getRequiredFlagByte(frame) & 0xff) == 1) return getResponseFlagByte(frame);
                return null;
            case 10:/**获得数据域*/
                return getDataFieldByte(frame);
            case 11:/**获得CRC16校验码*/
                return getCrcCodeByte(frame);
            case 12:/**获得结束符*/
                return getEndByte(frame);
            default:/**并没有什么*/
                return null;
        }
    }

    public Object getLogicFieldByteObject(byte[] frame, int i) {
        switch (i) {
            case 1:/**获得开始符*/
                return getBeginByte(frame);
            case 2:/**获得帧长度*/
                return getFrameLength(frame);
            case 3:/**获得控制码*/
                return getControlCode(frame);
            case 4:/**获得传送方向标识*/
                return getDirectionFlag(frame);
            case 5:/**获得响应标识*/
                return getRequiredFlag(frame);
            case 6:/**获得从站ID*/
                return getSubStationID(frame);
            case 8:/**获得报文ID*/
                return getMessageID(frame);
            case 7:/**获得数据长度*/
                return getDataLen(frame);
            case 9:/**获得响应码：只有响应帧才有响应码*/
                if ((getRequiredFlagByte(frame) & 0xff) == 1) return getResponseFlagByte(frame);
                return null;
            case 10:/**获得数据域*/
                return getDataField(frame);
            case 11:/**获得CRC16校验码*/
                return getCrcCodeByte(frame);
            case 12:/**获得结束符*/
                return getEndByte(frame);
            default:/**并没有什么*/
                return null;
        }
    }


    public LengthClass getFrameLength(byte[] frame) {
        LengthClass len = new LengthClass(getFrameLengthByte(frame));
        return len;
    }

    public FrameLength getFrameLength1(byte[] frame) {
        FrameLength len = new FrameLength(getFrameLengthByte(frame));
        return len;
    }


    public CtrlCode getControlCode(byte[] frame) {
        CtrlCode cc = null;
        cc = new CtrlCode(getControlCodeByte(frame));
        return cc;
    }


    public DirectionResponseFlag getDirectionAndResponseFlag(byte[] frame) {

        DirectionResponseFlag darf = new DirectionResponseFlag(getDirectionAndResponseFlagByte(frame));
        return darf;

    }

    public DirectionFlag getDirectionFlag(byte[] frame) {
        DirectionFlag df = new DirectionFlag(getDirectionFlagByte(frame));
        return df;
    }

    public RequiredFlag getRequiredFlag(byte[] frame) {
        RequiredFlag rf = new RequiredFlag(getRequiredFlagByte(frame));
        return rf;
    }

    public ClientID getSubStationID(byte[] frame) {
        ClientID ssid = new ClientID(getSubStationIDByte(frame));
        return ssid;
    }


    public MessageID getMessageID(byte[] frame) {
        MessageID mid = null;
        mid = new MessageID(getMessageIDByte(frame));
        return mid;
    }


    public LengthClass getDataLen(byte[] frame) {
        LengthClass len = new LengthClass(getDataLenByte(frame));
        return len;
    }

    public DataFieldLength getDataLen1(byte[] frame) {
        DataFieldLength dfl = new DataFieldLength(getDataFieldByte(frame));
        return dfl;
    }


    public DataField getDataField(byte[] frame) {

        DirectionResponseFlag darf = new DirectionResponseFlag(getDirectionAndResponseFlagByte(frame));
        CtrlCode cc = new CtrlCode(getControlCodeByte(frame));
        DataField df = new DataField(frame);
        return df;
    }

    public CrcCode getCrcCode(byte[] frame) {
        CrcCode cc = new CrcCode(getCrcCodeByte(frame));
        return cc;
    }

    /**
     * 
     * @param frame
     * @return
     */
    public boolean checkFrame(byte[] frame) {
        //check begin byte and end byte
        if (frame[0] != 0x68 || frame[frame.length - 1] != 0x16) {
            System.out.println("INVALID begin byte or end byte");
            return false;
        }
        System.out.println("begin byte and end byte OK");
        //check CS
        if (!checkCRC(frame)) {
            System.out.println("INVALID CRC code");
            return false;
        }
        System.out.println("CRC is OK");
        //check length of frame
        if (!checkLen(frame))
            return false;
        return true;
    }

    /**
     * 
     * @param frame
     * @return
     */
    public boolean checkCRC(byte[] frame) {
        byte[] cs = getCrcCodeByte(frame);
        byte[] cs2 = protocol.crc(frame, 0, frame.length - 4);
        if (cs == cs2) {
            System.out.println("CRC is OK");
            return true;
        }
        return false;
    }

    /**
     * 
     * @param frame
     * @return
     */
    public boolean checkLen(byte[] frame) {
        if (frame == null) {
            System.out.println("null frame");
            return false;
        }
        
        byte[] fl = getFrameLengthByte(frame);
        if (HexToInt.byteArrayToInt(fl) != frame.length) {
            System.out.println("invalid length of frame");
            //return false;
        }

        System.out.println("length of frame OK");
        return true;
    }
}
