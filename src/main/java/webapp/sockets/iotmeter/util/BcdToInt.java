package webapp.sockets.iotmeter.util;

public class BcdToInt {
	/**
	 * 一个字节的byte
	 * @param bcdVal
	 * @return
	 */
	public int bcdToInt(byte bcdVal) {
		int highVal = (bcdVal >> 4) & 0x0f; 
		//右移4位，移出bcd个位，得到bcd十位(高4位值)
		int lowVal = bcdVal & 0x0f;         
		//& 0x0f，移出bcd十位，得到bcd个位(低4位值)
		int value = highVal * 10 + lowVal;
		return value;
	}
}
