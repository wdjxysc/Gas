package sockets.iotmeter.field;


import sockets.iotmeter.protocol.Protocol;

import java.io.Serializable;


/**
 * 控制码
 * @author Administrator
 *
 */
public class CtrlCode implements Serializable{
	private static final long serialVersionUID = 1L;
    private byte[] controlCode = null;

    public CtrlCode(byte[] controlCode) {
        if (controlCode == null) return;
        this.controlCode = controlCode;
    }

    public CtrlCode(String controlCode) {
        if (controlCode.length() != 4) return;
        this.controlCode = Protocol.getInstance().hexStringToByte(controlCode);
    }

    public byte[] getControlCodeByte() {
        return controlCode;
    }
    
    public String getControlCodeString() {
    	 return Protocol.getInstance().hexToHexString(controlCode);
    }
}
