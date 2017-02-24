package webapp.sockets.iotmeter.field;

import java.io.Serializable;

/**
 * 数据域
 * @author Administrator
 *
 */
public class DataField implements Serializable{
	private static final long serialVersionUID = 1L;
    byte[] dataField = null;


    public DataField(byte[] df) {
        dataField = df;
    }

    public byte[] getDataField() {
        return dataField;
    }
}
