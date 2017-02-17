package sockets.iotmeter.frame;



import sockets.iotmeter.field.*;

import java.io.Serializable;


/**
 * 返回帧对象
 * @author Administrator
 *
 */
public class SendingFrame implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/***起始符*/
    private byte[] beginByte = new byte[]{0x68};
    /**帧的总长度*/
    private LengthClass lengthOfFrame;
    /***控制码*/
    private CtrlCode controlCode;
    /**标识为： 方向标识和响应标识*/
    private DirectionResponseFlag drf;
    /**从站编号*/
    private SubStationID ssid;
    /***报文编号*/
    private MessageID mid;
    /**数据域长度*/
    private LengthClass lengthOfDataField;
    /**响应标识*/
    private ResponseFlag rf;
    /**数据域*/
    private DataField dataField;
    /***校验码*/
    private CrcCode crcCode;
    /***结束符*/
    private byte[] endByte = new byte[]{0x16};

    /**
     * 返回帧
     */
    public byte[] sendingFrame = null;

    /**
     * 构造方法
     */
    public SendingFrame() {
    }

    /**
     * 获取所有的元素并组织
     * @param beginByte
     * @param lc
     * @param cc
     * @param drf
     * @param ssid
     * @param mid
     * @param df_lc
     * @param rf
     * @param df
     * @param crc
     * @param endByte
     */
    public SendingFrame(byte[] beginByte, LengthClass lc, CtrlCode cc, DirectionResponseFlag drf, SubStationID ssid, MessageID mid, LengthClass df_lc, ResponseFlag rf,
                        DataField df, CrcCode crc, byte[] endByte) {
        int i = 11; // the number of arguments
        this.beginByte = beginByte;
        this.lengthOfFrame = lc;
        this.controlCode = cc;
        this.drf = drf;
        this.ssid = ssid;
        this.mid = mid;
        this.lengthOfFrame = df_lc;
        this.dataField = df;
        this.crcCode = crc;
        this.endByte = endByte;
        this.rf = rf;

        // organize frame
        this.sendingFrame = this.organizeField();
    }

    public byte[] organizeField() {

        if (lengthOfFrame == null &&
                controlCode == null &&
                drf == null &&
                ssid == null &&
                mid == null &&
                lengthOfDataField == null &&
                rf == null &&
                dataField == null &&
                crcCode == null) return null;

        byte[] result = new byte[3 * 7 + 3 + 2 + lengthOfDataField.getLengthByte().length];

        try {
            int pos = 0;
            System.arraycopy(beginByte, 0, result, pos, 1);
            pos += 1;
            System.arraycopy(lengthOfFrame.getLengthByte(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(drf.getIDirectionAndResPonseFlag(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(ssid.getSubStationIdByte(), 0, result, pos, 7);
            pos += 7;
            System.arraycopy(mid.getMessageID(), 0, result, pos, 7);
            pos += 7;
            System.arraycopy(lengthOfDataField.getLengthByte(), 0, result, pos, 2);
            pos += 2;
            if (((drf.getIDirectionAndResPonseFlag()[1] & 0xff) == 1) && dataField != null) {
                System.arraycopy(rf.getRf(), 0, result, pos, 2);
                pos += 2;
            }
            System.arraycopy(dataField.getDataField(), 0, result, pos, dataField.getDataField().length);
            pos += dataField.getDataField().length;
            System.arraycopy(crcCode.getCrcCodeByte(), 0, result, pos, 2);
            pos += 2;
            System.arraycopy(endByte, 0, result, pos, 1);

        } catch (Exception exception) {
            System.out.println(exception);
        }
        return result;
    }

    public void setLengthOfFrame(LengthClass lengthOfFrame) {
        this.lengthOfFrame = lengthOfFrame;
    }

    public LengthClass getLengthOfFrame() {
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

    public void setLengthOfDataField(LengthClass lengthOfDataField) {
        this.lengthOfDataField = lengthOfDataField;
    }

    public LengthClass getLengthOfDataField() {
        return lengthOfDataField;
    }

    public void setResponseFlag(ResponseFlag rf) {
        this.rf = rf;
    }

    public ResponseFlag getResponseFlag() {
        return rf;
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
}
