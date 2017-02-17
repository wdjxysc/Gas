package sockets.iotmeter.encode;


import sockets.iotmeter.field.*;

import java.io.Serializable;


public abstract class AbsDecoder implements Serializable{
	public byte getBeginByte(byte[] frame) {
        return (byte) frame[0];
    }


    public byte[] getFrameLengthByte(byte[] frame) {
        byte[] fl = new byte[2];
        fl[0] = frame[1];
        fl[1] = frame[2];
        return fl;
    }


    public byte[] getControlCodeByte(byte[] frame) {
        byte[] cc = new byte[2];
        cc[0] = frame[3];
        cc[1] = frame[4];
        return cc;
    }


    public byte[] getDirectionAndResponseFlagByte(byte[] frame) {
        byte[] darf = new byte[2];
        darf[0] = frame[5];
        darf[1] = frame[6];
        return darf;
    }

    public byte getDirectionFlagByte(byte[] frame) {
        return frame[5];
    }

    public byte getRequiredFlagByte(byte[] frame) {
        return frame[6];
    }

    public byte[] getSubStationIDByte(byte[] frame) {
        byte[] ssid = new byte[7];
        System.arraycopy(frame, 7, ssid, 0, ssid.length);
        return ssid;
    }

    public byte[] getMessageIDByte(byte[] frame) {
        byte[] mi = new byte[7];
        System.arraycopy(frame, 14, mi, 0, mi.length);
        return mi;
    }

    public byte[] getDataLenByte(byte[] frame) {
        byte[] dl = new byte[2];
        dl[0] = frame[21];
        dl[1] = frame[22];
        return dl;
    }

    public byte[] getResponseFlagByte(byte[] frame) {
        byte[] rf = new byte[2];
        rf[0] = frame[23];
        rf[1] = frame[24];
        return rf;
    }


    public byte[] getDataFieldByte(byte[] frame) {
        int len = frame.length - 23 - 2 - 1;
        byte[] df = new byte[len];
        if (len <= 0)
            return null;
        else
            System.arraycopy(frame, 23, df, 0, df.length);
        return df;
    }


    public byte[] getCrcCodeByte(byte[] frame) {
        if (frame == null) return null;
        byte[] cs = new byte[2];
        cs[0] = frame[frame.length - 3];
        cs[1] = frame[frame.length - 2];
        return cs;
    }

    public byte getEndByte(byte[] frame) {
        return (byte) frame[frame.length - 1];
    }


    /**
     * 获取逻辑结构
     */
    public abstract LengthClass getFrameLength(byte[] frame);

    public abstract CtrlCode getControlCode(byte[] frame);

    public abstract DirectionResponseFlag getDirectionAndResponseFlag(byte[] frame);

    public abstract SubStationID getSubStationID(byte[] frame);

    public abstract MessageID getMessageID(byte[] frame);

    public abstract LengthClass getDataLen(byte[] frame);

    public abstract DataField getDataField(byte[] frame);

    public abstract CrcCode getCrcCode(byte[] frame);

    /**
     * 检测帧的结构
     */
    public abstract boolean checkFrame(byte[] frame);
}
