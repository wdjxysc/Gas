package webapp.sockets.concentrateor.encode;


import webapp.sockets.concentrateor.field.*;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.HexToInt;

/**
 * 
 * @author Administrator
 *
 */
public class RTHCDecoder extends AbsDecoder {
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
            case 7:/**获得数据域长度*/
                return getDataLenByte(frame);
            case 8:/**获得数据域*/
                return getDataFieldByte(frame);
            case 9:/**获得CRC16校验码*/
                return getCrcCodeByte(frame);
            case 10:/**获得结束符*/
                return getEndByte(frame);
            default:/**并没有什么*/
                return null;
        }

    }



    public FrameLength getFrameLength(byte[] frame) {
        return new FrameLength(getFrameLengthByte(frame));
    }

    public CtrlCode getControlCode(byte[] frame) {
        return new CtrlCode(getControlCodeByte(frame));
    }

    public DirectionResponseFlag getDirectionAndResponseFlag(byte[] frame) {
        return new DirectionResponseFlag(getDirectionAndResponseFlagByte(frame));
    }

    private DirectionFlag getDirectionFlag(byte[] frame) {
        return new DirectionFlag(getDirectionFlagByte(frame));
    }

    private RequiredFlag getRequiredFlag(byte[] frame) {
        return new RequiredFlag(getRequiredFlagByte(frame));
    }

    public SubStationID getSubStationID(byte[] frame) {
        return new SubStationID(getSubStationIDByte(frame));
    }


    public MessageID getMessageID(byte[] frame) {
        return new MessageID(getMessageIDByte(frame));
    }


    public DataFieldLength getDataLen(byte[] frame) {
        return new DataFieldLength(getDataLenByte(frame));
    }

    public DataFieldLength getDataLen1(byte[] frame) {
        return new DataFieldLength(getDataFieldByte(frame));
    }


    public DataField getDataField(byte[] frame) {
        return new DataField(frame);
    }

    public CrcCode getCrcCode(byte[] frame) {
        return new CrcCode(getCrcCodeByte(frame));
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
