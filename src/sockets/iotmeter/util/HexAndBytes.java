package sockets.iotmeter.util;

public class HexAndBytes {
	public HexAndBytes() {
		super();
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

	public static byte uniteBytes(byte b, byte c) {
		return 0;
	}

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
}
