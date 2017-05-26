package webapp.sockets.concentrateor.field.datafield;

import webapp.sockets.concentrateor.field.ConcentratorID;
import webapp.sockets.util.Tools;

/**
 * Created by Administrator on 2017/5/23.
 */
public class ContentHeartbeat extends DataContentField {

    private ConcentratorID concentratorID;
    private Heartbeat heartbeat;

    public ContentHeartbeat(byte[] dataContent) {
        super(dataContent);

        int pos = 0;
        byte[] concentratorIDBytes = new byte[5];
        System.arraycopy(dataContent, pos, concentratorIDBytes, 0, concentratorIDBytes.length);
        concentratorID = new ConcentratorID(concentratorIDBytes);
        pos += 5;

        byte[] heartbeatBytes = new byte[2];
        System.arraycopy(dataContent, pos, heartbeatBytes, 0, heartbeatBytes.length);
        heartbeat = new Heartbeat(heartbeatBytes);
    }

    public ContentHeartbeat(ConcentratorID concentratorID, Heartbeat heartbeat) {
        dataContent = Tools.assembleBytes(concentratorID.getConcentratorIdByte(), heartbeat.getHbBytes());
        this.concentratorID = concentratorID;
        this.heartbeat = heartbeat;
    }

    public ConcentratorID getConcentratorID() {
        return concentratorID;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }


    static public class Heartbeat {
        public int getHbInt() {
            return hbInt;
        }

        public byte[] getHbBytes() {
            return hbBytes;
        }

        int hbInt;
        byte[] hbBytes = new byte[2];

        public Heartbeat(byte[] hbBytes) {
            if (hbBytes.length != 2) return;
            this.hbBytes = hbBytes;
            hbInt = ((hbBytes[1] & 0xff) << 8) + (hbBytes[0] & 0xff);
        }

        public Heartbeat(int hbInt) {
            if (hbInt < 0 || hbInt > 0xffff) return;
            this.hbInt = hbInt;
            hbBytes[0] = (byte) (hbInt % 0x100);
            hbBytes[1] = (byte) (hbInt / 0x100);
        }


    }
}
