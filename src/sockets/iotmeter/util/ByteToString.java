package sockets.iotmeter.util;

/**
 * 
 * @author Administrator
 *
 */
public class ByteToString {
	public String getByteArrayToString(byte[] b) {

        String result = "";

        int len = b.length;
        int i = 0;
        while (i < b.length) {
            result = result + getByteToString(b[i]);
            i++;
        }
        return result;
    }


    public String getByteToString(byte b) {
        int i = b;
        String result;
        result = getIntToString(i);
        return result;
    }

    public String getIntToString(int i) {
        String result;
        result = Integer.toString(i);
        while (result.length() < 2) {
            result = "0" + result;
        }
        return result;
    }
}
