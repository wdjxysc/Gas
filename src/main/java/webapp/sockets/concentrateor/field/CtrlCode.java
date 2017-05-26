package webapp.sockets.concentrateor.field;


import webapp.sockets.util.Protocol;

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

    public CtrlCode(String controlCode){
        this.controlCode = Protocol.getInstance().hexStringToByte(controlCode);
    }

    public byte[] getControlCodeByte() {
        return controlCode;
    }
    
    public String getControlCodeString() {
    	 return Protocol.getInstance().hexToHexString(controlCode);
    }


    public final static String CMD_CENTER_CONNECT_TEST = "2001";
    public final static String CMD_CONCENTRATOR_CONNECT_TEST = "2002";
    public final static String CMD_HEARTBEAT = "2003";
    public final static String CMD_REGISTER = "2004";
    public final static String CMD_SET_CONCENTRATOR_HEARTBEAT_CIRCLE = "2011";
    public final static String CMD_READ_CONCENTRATOR_HEARTBEAT_CIRCLE = "2012";
    public final static String CMD_SET_CONCENTRATOR_GROUP_READ_TIME = "2013";
    public final static String CMD_READ_CONCENTRATOR_GROUP_READ_TIME = "2014";
    public final static String CMD_SET_CONCENTRATOR_TIME = "2015";
    public final static String CMD_READ_CONCENTRATOR_TIME = "2016";

    public final static String CMD_SET_METER_AWAKE_TIME = "2021";
    public final static String CMD_READ_METER_AWAKE_TIME = "2022";
    public final static String CMD_SET_METER_TIME = "2023";
    public final static String CMD_READ_METER_TIME = "2024";

    public final static String CMD_ADD_COLLECTOR = "2031";
    public final static String CMD_REMOVE_COLLECTOR = "2032";
    public final static String CMD_ADD_METER = "2033";
    public final static String CMD_REMOVE_METER = "2034";
    public final static String CMD_QUERY_COLLECTORS = "2035";
    public final static String CMD_QUERY_METERS = "2036";

    public final static String CMD_READ_ALL_METER_DATA = "2041";
    public final static String CMD_READ_METER_DATA = "2042";
    public final static String CMD_READ_METER_HISTORY_MONTH = "2043";
    public final static String CMD_READ_METER_HISTORY_DAY = "2044";

    public final static String CMD_OPEN_METER_VALVE = "2051";
    public final static String CMD_CLOSE_METER_VALVE = "2052";
    public final static String CMD_READ_METER_VALVE_STATE = "2053";

    public final static String CMD_UPLOAD_CONCENTRATOR_ERROR = "2061";
    public final static String CMD_UPLOAD_METER_ERROR = "2062";
}
