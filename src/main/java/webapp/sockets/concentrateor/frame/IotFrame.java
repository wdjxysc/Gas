package webapp.sockets.concentrateor.frame;


import webapp.sockets.concentrateor.encode.RTHCDecoder;
import webapp.sockets.concentrateor.field.*;
import webapp.sockets.util.HexToInt;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.StringTool;

import java.io.Serializable;

public class IotFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    /*** 起始符*/
    private byte[] beginByte = new byte[]{0x68};
    /*** 帧的总长度*/
    private FrameLength lengthOfFrame;
    /*** 控制码*/
    private CtrlCode controlCode;
    /*** 标识为： 方向标识和响应标识*/
    private DirectionResponseFlag drf;
    /*** 从站编号*/
    private SubStationID ssid;
    /*** 报文编号*/
    private MessageID mid;
    /*** 数据域长度*/
    private DataFieldLength lengthOfDataField;
    /*** 数据域*/
    private DataField dataField;
    /*** 校验码*/
    private CrcCode crcCode;
    /*** 结束符*/
    private byte[] endByte = new byte[]{0x16};

    /**
     * 帧数据
     */
    public byte[] dataFrame = null;

    /**
     * 分解一个收到的Frame
     *
     * @param frame
     */
    public IotFrame(byte[] frame) {
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        dataFrame = frame;
        lengthOfFrame = new FrameLength((byte[]) (decoder.getLogicFieldByteArray(frame, 2)));
        controlCode = new CtrlCode((byte[]) decoder.getLogicFieldByteArray(frame, 3));
        drf = new DirectionResponseFlag((byte[]) decoder.getLogicFieldByteArray(frame, 4));
        ssid = new SubStationID((byte[]) decoder.getLogicFieldByteArray(frame, 5));
        mid = new MessageID((byte[]) decoder.getLogicFieldByteArray(frame, 6));
        lengthOfDataField = new DataFieldLength((byte[]) decoder.getLogicFieldByteArray(frame, 7));
        dataField = new DataField((byte[]) decoder.getLogicFieldByteArray(frame, 8));
        dataField = new DataField(dataField.getDataField(), drf);
        crcCode = new CrcCode((byte[]) decoder.getLogicFieldByteArray(frame, 9));
    }


    /**
     * 构造方法 自动计算长度、crc等字段
     *
     * @param ctrlCode
     * @param drf
     * @param subStationID
     * @param messageID
     * @param dataField
     */
    public IotFrame(CtrlCode ctrlCode,
                    DirectionResponseFlag drf,
                    SubStationID subStationID,
                    MessageID messageID,
                    DataField dataField) {

        int frameTotalLength = 3 * 7 + 2 + 2 + 1 + dataField.getDataField().length;
        try {
            lengthOfFrame = new FrameLength(frameTotalLength);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.controlCode = ctrlCode;
        this.drf = drf;
        this.ssid = subStationID;
        this.mid = messageID;
        try {
            this.lengthOfDataField = new DataFieldLength(dataField.getDataField().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.dataField = dataField;


        // organize frame
        this.dataFrame = this.organizeField();
    }


    public byte[] organizeField() {

        if (lengthOfFrame == null &&
                controlCode == null &&
                drf == null &&
                ssid == null &&
                mid == null &&
                lengthOfDataField == null &&
                dataField == null &&
                crcCode == null) return null;

        byte[] result = new byte[3 * 7 + 2 + 2 + 1 + dataField.getDataField().length];


        try {
            int pos = 0;
            System.arraycopy(beginByte, 0, result, pos, 1);
            pos += 1;
            System.arraycopy(lengthOfFrame.getFrameLength(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(controlCode.getControlCodeByte(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(drf.getIDirectionAndResPonseFlag(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(ssid.getSubStationId(), 0, result, pos, 7);
            pos += 7;
            System.arraycopy(mid.getMessageID(), 0, result, pos, 7);
            pos += 7;
            System.arraycopy(lengthOfDataField.getFrameLength(), 0, result, pos, 2);
            pos += 2;
            if (((drf.getIDirectionAndResPonseFlag()[1] & 0xff) == 1) && dataField != null) {
                System.arraycopy(dataField.getResponseCode().getResponseCode(), 0, result, pos, 2);
                pos += 2;
            }
            System.arraycopy(dataField.getDataContentField().getDataContent(), 0, result, pos, dataField.getDataContentField().getDataContent().length);
            pos += dataField.getDataContentField().getDataContent().length;

            int crc_int = Protocol.getInstance().calcCrc16(result, 0, pos);
            String crc_string = StringTool.reversalHexString(String.format("%04X", crc_int));
            byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
            System.arraycopy(add_crc_code, 0, result, pos, 2);
            crcCode = new CrcCode(add_crc_code);
            pos += 2;
            System.arraycopy(endByte, 0, result, pos, 1);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }


    public void setLengthOfFrame(FrameLength lengthOfFrame) {
        this.lengthOfFrame = lengthOfFrame;
    }

    public FrameLength getLengthOfFrame() {
        return lengthOfFrame;
    }

    public void setControlCode(CtrlCode controlCode) {
        this.controlCode = controlCode;
    }

    public CtrlCode getControlCode() {
        return controlCode;
    }

    public void setDrf(DirectionResponseFlag drf) {
        this.drf = drf;
    }

    public DirectionResponseFlag getDrf() {
        return drf;
    }

    public void setSsid(SubStationID ssid) {
        this.ssid = ssid;
    }

    public SubStationID getSsid() {
        return ssid;
    }

    public void setMid(MessageID mid) {
        this.mid = mid;
    }

    public MessageID getMid() {
        return mid;
    }

    public void setLengthOfDataField(DataFieldLength lengthOfDataField) {
        this.lengthOfDataField = lengthOfDataField;
    }

    public DataFieldLength getLengthOfDataField() {
        return lengthOfDataField;
    }

    public void setDataField(DataField dataField) {
        this.dataField = dataField;
    }

    public DataField getDataField() {
        return dataField;
    }

    public void setCrcCode(CrcCode crcCode) {
        this.crcCode = crcCode;
    }

    public CrcCode getCrcCode() {
        return crcCode;
    }


    /**
     * @return
     */
    public boolean checkFrame() {
        //check begin byte and end byte
        if (dataFrame[0] != 0x68 || dataFrame[dataFrame.length - 1] != 0x16) {
            System.out.println("INVALID begin byte or end byte");
            return false;
        }
        System.out.println("begin byte and end byte OK");
        //check CS
        if (!checkCRC()) {
            System.out.println("INVALID CRC code");
            return false;
        }
        System.out.println("CRC is OK");
        //check length of frame
        if (!checkLen())
            return false;
        return true;
    }


    /**
     * @return
     */
    public boolean checkCRC() {
        byte[] cs = crcCode.getCrcCodeByte();
        byte[] cs2 = Protocol.getInstance().crc(dataFrame, 0, dataFrame.length - 4);
        if (cs == cs2) {
            System.out.println("CRC is OK");
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public boolean checkLen() {
        if (dataFrame == null) {
            System.out.println("null frame");
            return false;
        }

        byte[] fl = lengthOfFrame.getFrameLength();
        if (HexToInt.byteArrayToInt(fl) != dataFrame.length) {
            System.out.println("invalid length of frame");
            //return false;
        }

        System.out.println("length of frame OK");
        return true;
    }
}
