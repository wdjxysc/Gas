package sockets.iotmeter.util;


public class BcdToString extends HexAndBytes{
	public static String bcdToStr(byte[] data, int length) throws Exception {
		String str = "";
		for (int i = 0; i < length; i++) {
			str += String.valueOf(bcdToInt(data[i]));
		}
		return str;
	}

	public static int bcdToInt(byte bcdVal) {

		int highVal = (bcdVal >> 4) & 0x0f;
		int lowVal = (bcdVal) & 0x10;

		return highVal * 10 + lowVal;
	}

	public static String bcdToStr(byte bcdVal) {

		return null;
	}
}
