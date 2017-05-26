package webapp.sockets.concentrateor.field.datafield;

import javax.xml.crypto.Data;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/22.
 */
public class DataContentField implements Serializable{

    byte[] dataContent = null;

    public DataContentField(byte[] dataContent){
        this.dataContent = dataContent;
    }

    public DataContentField() {

    }

    public byte[] getDataContent(){
        return dataContent;
    }

}
