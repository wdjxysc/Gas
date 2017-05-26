package webapp.sockets.concentrateor.field;

import webapp.sockets.concentrateor.field.datafield.DataContentField;
import webapp.sockets.util.Protocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据域
 *
 * @author Administrator
 */
public class DataField implements Serializable {
    private static final long serialVersionUID = 1L;
    byte[] dataField = null;
    private ResponseCode responseCode;
    private DataContentField dataContentField;

    public DataField(byte[] df){
        dataField = df;
    }

    public DataField(byte[] df, DirectionResponseFlag directionResponseFlag) {
        dataField = df;
        if(directionResponseFlag.getResponse() == 0x00){
            responseCode = null;
            dataContentField = new DataContentField(dataField);
        }else {
            byte[] responseCodeBytes = new byte[2];
            System.arraycopy(df,0,responseCodeBytes,0,responseCodeBytes.length);
            try {
                responseCode = new ResponseCode(responseCodeBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] dataContentBytes = new byte[df.length-2];
            System.arraycopy(df,2,dataContentBytes,0,dataContentBytes.length);
            dataContentField = new DataContentField(dataContentBytes);
        }
    }

    public DataField(ResponseCode responseCode, DataContentField dataContentField) {
        this.responseCode = responseCode;
        this.dataContentField = dataContentField;
        List<byte[]> list = new ArrayList<>();
        if (responseCode != null)
            list.add(responseCode.getResponseCode());
        if (dataContentField != null)
            list.add(dataContentField.getDataContent());
        dataField = Protocol.getInstance().organizeBytes(list);
    }

    public ResponseCode getResponseCode(){
        return responseCode;
    }

    public DataContentField getDataContentField(){
        return dataContentField;
    }

    public byte[] getDataField() {
        return dataField;
    }
}
