package webapp.sockets.util;

public class HexToInt {
	private static byte[] byteToTransfer = new byte[10];

	public HexToInt() {
		
	}

	public static void HexToInt(byte[] bytearray) {
		if (bytearray.length >= 10)
			System.out.println("the byte to be transferred is out of range: 10 bytes");
		System.arraycopy(bytearray, 0, byteToTransfer, 0, bytearray.length);
		System.out.println("transfered");
	}

	public byte[] getByteArray() {
		return this.byteToTransfer;
	}

	
	public static int byteArrayToInt(byte[] array){
		int result = 0;
		int length = array.length;
		for(int i = 0; i<length; i++){
			result *= 0x100;
			result += array[i];
		}
		
		return result;
	}

	public static int hexToNumber(String hex) {

		return 0;
	}
}
