package webapp.sockets.util;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/20.
 */
public class Tools {

    /**
     * Bytes2HexString
     * @param b
     * @param size
     * @return
     */
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /* byte[] Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;

    }

    /* Int byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    public static byte[] assembleBytes(byte[] b1,byte[] b2){
        byte[] resultBytes = new byte[b1.length + b2.length];
        System.arraycopy(b1,0,resultBytes,0,b1.length);
        System.arraycopy(b2,0,resultBytes,b1.length,b2.length);
        return resultBytes;
    }

    public static String getDateBcdStr(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(date);
    }


    /**
     * 和校验
     *
     * @param data 需要校验的数据
     * @return 校验结果
     */
    public static byte CheckSum(byte[] data) {
        byte sum = 0;

        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }

        return (byte) (sum % 256);
    }
}
