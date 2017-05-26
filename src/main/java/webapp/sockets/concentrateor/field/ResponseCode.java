package webapp.sockets.concentrateor.field;

import webapp.sockets.util.StringTool;
import webapp.sockets.util.Tools;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18.
 * <p>
 * 报文响应码
 */
public class ResponseCode implements Serializable {

    private byte[] responseCode = null;
    private int responseCodeValue = 0;
    public static final int SUCCESS = 0x0000;//成功
    public static final int ERROR_DATA_EMPTY = 0x0101;//报文为空

    public ResponseCode(byte[] responseCode) throws Exception{
        if(responseCode.length != 2) throw new Exception();
        this.responseCode = responseCode;
        this.responseCodeValue = Integer.valueOf(Tools.Bytes2HexString(responseCode, responseCode.length), 16);
    }

    public ResponseCode(int responseCodeValue) {
        this.responseCodeValue = responseCodeValue;
        String responseCodeStr =  StringTool.padLeft(Integer.toHexString(responseCodeValue),4,'0');
        this.responseCode = Tools.HexString2Bytes(responseCodeStr);
    }

    public String getResponseCodeHexStr() {
        return Tools.Bytes2HexString(responseCode, responseCode.length);
    }

    public byte[] getResponseCode() {
        return responseCode;
    }

    public int getResponseCodeInt() {
        return Integer.parseInt(getResponseCodeHexStr(),16);
    }
}
