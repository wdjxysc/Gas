package webapp.sockets.iotmeter.protocol;


import webapp.sockets.iotmeter.util.TimeTag;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 协议计算服务类
 * @author Administrator
 *
 */
public class Protocol implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * BCD码
	 */
	public static final int MPDATATYPE_PARSE_BCD = 1;
	/** 有符号的BCD */
	public static final int MPDATATYPE_PARSE_SIGNBCD = 2;
	/** BIN码 */
	public static final int MPDATATYPE_PARSE_BIN = 3;
	/**有符号的BIN码 */
	public static final int MPDATATYPE_PARSE_SIGNBIN = 4;             
	/**BS */
	public static final int MPDATATYPE_PARSE_BS = 5;
	/**ASCII码 */
	public static final int MPDATATYPE_PARSE_ASCII = 6;
	/**16进制IC卡号 */
	public static final int MPDATATYPE_PARSE_ICCARD = 85;
	
	
	/**描述字节内BCD码的高低位：高 */
	public static final byte BCD_HIGH_DIGIT = 1;
	/**描述字节内BCD码的高低位：低*/
	public static final byte BCD_LOW_DIGIT = 2;

	/**位移方向：右 */
	public static final byte BITMOVE_DIR_RIGHT = 1;
	/**位移方向：左 */
	public static final byte BITMOVE_DIR_LEFT = 2;
	
	
	private static Protocol instance = null;

	public Protocol() {
		
	}
	
	/**
	 * 说明：取得Protocol类的实例(单例).
	 * @return Protocol
	 */
	public static Protocol getInstance() {
		if (instance == null)
			instance = new Protocol();
		return instance;
	}
	
	/**
	 * 组织ASCII类型数据格式
	 * @param value 待组织十进制格式字符串
	 * @param len   Ascii格式字节长度
	 * @return
	 */
	public byte[] organizeAscii(String value, int len) {
		if (value == null || value.length() > len) {
			//value每一个十进制字符对应一个字节
			return null;
		}
		//encodes this String into a array of bytes
		byte[] temp = value.getBytes();
		byte[] data = new byte[len];
		System.arraycopy(temp, 0, data, 0, temp.length);
		return data;
	}
	
	/**
	 * 组织BS类型数据格式
	 * @param value 待组织二进制格式字符串
	 * @param len   Bs格式长度---字节数[有1，8，16，24，64，248--->见文档]
	 * @return
	 */
	public byte[] organizeBs(String value, int len) {
		//BS:独立位组合
		if (value == null || value.equals("") || value.length() > 8 * len) {
			//value每8个二进制字符对应一个字节
			return null;
		}
		while (value.length() < 8 * len) { 
			//1byte = 8bit
			value = "0" + value; 
			//不足位在前面补0
		}
		byte[] data = new byte[len]; 
		//Bs格式数据
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.equals("1")) {
				int dataTemp = data[i / 8];
				dataTemp = dataTemp + (1 << (i % 8));
				data[i / 8] = (byte) dataTemp;
			}
		}
		return data;
	}
	
	/**
	 * 组织BCD数据格式
	 * @param value    待组织数据
	 * @param digitLen 长度
	 * @return
	 */
	public byte[] organizeBcd(String value, int digitLen) {
		//二-十进制编码
		if (value == null || value.equals("") || digitLen % 2 != 0) {
			return null;
		}

		int len = digitLen / 2;

		boolean isNega = false; 
		//是否是负数
		if (value.substring(0, 1).equals("-")) {
			isNega = true;
			value = value.substring(1);
			//取数字部分
		}

		byte[] data = null;  
		//待返回的BCD格式数据
		String pattern = "";
		if (digitLen > 0) {
			pattern = "\\d{1," + digitLen + "}";
		} else {
			pattern = "\\d{1}";
			//1个连续的数字位
		}
		if (Pattern.matches(pattern, value)) {
			data = new byte[len];
			String[] subValues = value.split("\\.");
			//从小数点分割
			if (digitLen > 0) {
				while (subValues[0].length() < digitLen) {
					subValues[0] = "0" + subValues[0];
				}
			} else {
				subValues[0] = "";
			}
			if (subValues.length == 2) {
				subValues[0] += subValues[1];
			}
			while (subValues[0].length() < digitLen) {
				subValues[0] += "0";
			}

			for (int i = 0; i < digitLen; i += 2) {
				String tempStr = subValues[0].substring(i, i + 2);
				byte tempBcd = this.hexToBcd(Byte.parseByte(tempStr, 10), 0);
				data[i / 2] = tempBcd;
			}
			if (isNega == true) {
				data[0] = (byte) (data[0] & 0x0f);
				data[0] = (byte) (data[0] | 0x10);
			}
			data = this.reverseByteOrder(data);
		}
		return data;
	}
	
	/**
	 * 将16进制转换为16进制字符串,并且在每字节之间加上'-'字符
	 */
	public String hexToSepHexString(byte[] data) {
		if (data != null) {
			StringBuffer  result = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				result.append(hexToHexString(data[i]));
				if (i < data.length - 1) {
					result.append("-");
					//每追加一个新字节，追加之后就在后面再追加一个"-"符号
				}
			}
			return result.toString();
		}
		return "";
	}
	
	/**
	 * 将16进制字节数组转换为16进制字符串
	 */
	public String hexToHexString(byte[] data) {
		if (data != null) {
			String result = "";
			for (int i = 0; i < data.length; i++) {
				result += hexToHexString(data[i]);
			}
			return result;
		}
		return "";
	}
	
	/**
	 * 16进制分隔字符串转换为16进制数组
	 * @param srcStr 操作字符串
	 * @param separator 分隔符
	 * @return
	 */
	public byte[] hexStringToByte(String srcStr,String separator) {
		if (srcStr==null||separator==null)
			return null;
		srcStr=srcStr.replaceAll(separator, "");
		return hexStringToByte(srcStr);
	}
	
	/**
	 * 16进制字符串转换为16进制数组
	 * @param srcStr 操作字符串
	 * @return
	 */
	public byte[] hexStringToByte(String srcStr) {
		if (srcStr == null || srcStr.equals("") || srcStr.length() % 2 != 0) {
			return null;
		}
		
		if(srcStr.length() % 2 != 0){
			srcStr = '0' + srcStr;
		}

		int len = srcStr.length() / 2;   
		//每字节对应十进制数含2字节，所以字节数 = 字符数/2
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			String temp = srcStr.substring(i * 2, i * 2 + 2);
			//0-2，2-4，4-6，6-8
			int val = Integer.parseInt(temp, 16);
			//将取得的字符串temp转换成16进制格式整数
			data[i] = (byte) val; 
			//依次赋值给字节数组元素
		}
		return data;
	}

	/**
	 * 将单字节转换成16进制字符串
	 * @param input
	 * @return
	 */
	public String hexToHexString(byte input) {
		String str = input > 15 ? "" : "0";
		str += Integer.toHexString(input);
		if (str.length() > 2) {
			str = str.substring(str.length() - 2);
		}
		return str;
	}

	/**
	 * 将16进制字节转换为BCD码字节
	 * @param hexVal 十六进制字节
	 * @param addVal 增加数
	 * @return
	 */
	public byte hexToBcd(byte hexVal, int addVal) {
		return (byte) (hexVal % 10 + ((hexVal / 10) % 10) * 16 + addVal);
	}

	/**
	 * 将16进制转换为10进制short类型.将16进制转换为10进制整型，字节数组高字节在前低字节在后，字节长度必须小于等于2.
	 * @param data byte
	 * @return int
	 */
	public int hexToShort(byte[] data) {
		if (data == null || data.length > 2) {
			return -1;
		}
		for (int i = 0; i < 2; i++) {
			int intVal = hexToInt(data[i]);
			data[i] = (byte) intVal;
		}

		int highValue = hexToInt(data[0]) << 8;
		int lowValue = hexToInt(data[1]);

		return highValue + lowValue;
	}

	/**
	 * 将16进制转换为10进制short类型.short类型占用2个字节
	 * @param data
	 * @param minusVal
	 * @return
	 */
	public int hexToShort(byte[] data, byte minusVal) {
		if (data == null || data.length > 2) {
			return -1;
		}

		for (int i = 0; i < 2; i++) {
			int intVal1 = hexToInt(data[i]);
			int intVal2 = hexToInt(minusVal);
			int intVal = intVal1 - intVal2;
			if (intVal < 0) {
				intVal += 256;
			}
			data[i] = (byte) intVal;
		}
		return hexToShort(data);
	}

	/**
	 * 将16进制转换为10进制整型{单个字节转换}.防止负数位扩展
	 * @param data byte
	 * @return int
	 */
	public int hexToInt(byte data) {
		return (data & 0xff);
	}

	/**
	 * 将16进制转换为10进制整型，字节数组高字节在前低字节在后，字节长度必须小于等于4.
	 * @param data byte[]   小于或等于4的字节数组
	 * @return int
	 */
	public int hexToInt(byte[] data) {
		if (data == null || data.length < 1 || data.length > 4)
			return -1;
		int result = 0;
		int len = data.length;
		//逐次对每个字节进行转换
		for (int i = 0; i < len; i++) {
			int ch = hexToInt(data[len - 1 - i]); 
			//从最末元素开始转换
			result += ch << (8 * i);
		}
		if (data.length == 1 && result < 0) 
			//若仅含一个字节并且是负数
			result += 256;                  
		//返回负数无符号形式表达值
		return result;
	}


	/**
	 * 将16进制转换为10进制整型，字节数组低字节在前高字节在后，字节长度必须小于等于4.
	 * @param data byte[]   小于或等于4的字节数组
	 * @return int
	 */
	public int hexToIntLowInHead(byte[] data) {
		byte[] dataR = new byte[data.length];
		for (int i=0;i<data.length;i++){
			dataR[i] = data[data.length-i-1];
		}

		return hexToInt(dataR);
	}

	/**
	 * 将16进制转换为10进制整型。int类型占用4个字节
	 * @param data
	 * @param minusVal
	 * @return
	 */
	public int hexToInt(byte[] data, byte minusVal) {
		if (data == null || data.length != 4) {
			//int类型占用4个字节
			return -1;
		}
		for (int i = 0; i < 4; i++) {
			int intVal1 = hexToInt(data[i]);
			int intVal2 = hexToInt(minusVal);
			int intVal = intVal1 - intVal2;
			if (intVal < 0) {
				intVal += 256;
				//若是负数，则用正数形势表示
			}
			data[i] = (byte) intVal;
		}
		return hexToInt(data);
	}

	/**
	 * 将16进制转换为8字节的长整型数{若干字节组合在一起后对应的16进制的10进制数}.
	 * @param data byte
	 * @return int
	 */
	public long hexToLong(byte[] data) {
		if (data == null || data.length != 8)
			return -1;
		long result = 0;
		int len = data.length;
		for (int i = 0; i < len; i++) {
			int ch = hexToInt(data[len - 1 - i]);
			result += (long) ch << (8 * i); 
		}
		return result;
	}

	/**
	 * 将16进制转换为5字节的长整型数{若干字节组合在一起后对应的16进制的10进制数}.
	 * @param data byte
	 * @return int
	 */
	public long hexToInt5(byte[] data) {
		if (data == null || data.length != 5)
			return -1;
		long result = 0;
		int len = data.length;
		for (int i = 0; i < len; i++) {
			int ch = hexToInt(data[len - 1 - i]);
			result += (long) ch << (8 * i); 
		}
		return result;
	}

	/**
	 * 将16进制转换为6字节的长整型数{若干字节组合在一起后对应的16进制的10进制数}.
	 * @param data byte
	 * @return int
	 */
	public long hexToInt6(byte[] data) {
		if (data == null || data.length != 6)
			return -1;
		long result = 0;
		int len = data.length;
		for (int i = 0; i < len; i++) {
			int ch = hexToInt(data[len - 1 - i]);
			result += (long) ch << (8 * i); 
		}
		return result;
	}

	/**
	 * 将BCD码转换为10进制整型
	 * @param bcdVal   bcd码值
	 * @param minusVal 减去值
	 * @return
	 */
	public int bcdToInt(byte bcdVal, int minusVal) {
		int highVal = (bcdVal >> 4) & 0x0f; 
		//右移4位，移出bcd个位，得到bcd十位(高4位值)
		int lowVal = bcdVal & 0x0f;         
		//& 0x0f，移出bcd十位，得到bcd个位(低4位值)
		int value = highVal * 10 + lowVal - minusVal;
		return value;
	}

	/**
	 * 将高(低)4位BCD码转换为10进制整型
	 * @param hexValue
	 * @param digit
	 * @return
	 */
	public int halfBcdToInt(byte hexValue, int digit) {
		if (digit == BCD_HIGH_DIGIT) { 
			//BCD高4位
			return (hexValue >> 4) & 0x0f;
		} 
		else {
			//BCD低4位
			return hexValue & 0x0f;
		}
	}

	/**
	 * 说明：16进制转换为Double
	 * @param data byte[]
	 * @return long
	 */
	public Double hexToDouble(byte[] data) {
		if (data == null || data.length != 8)
			return null;
		byte[] newdata=new byte[data.length];
		System.arraycopy(data, 0, newdata, 0, data.length);
		this.reverseByteOrder(newdata);
		ByteArrayInputStream bais = new ByteArrayInputStream(newdata);
		DataInputStream dis = new DataInputStream(bais);
		try {
			return dis.readDouble();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
		finally{
			try {
				bais.close();
				dis.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将BCD码转换为16进制字符串
	 * @param bcdVal
	 * @return
	 */
	public String bcdToHexStr(byte bcdVal) {
		int highVal = (bcdVal >> 4) & 0x0f;
		int lowVal = bcdVal & 0x0f;
		String result = Integer.toString(highVal, 16) + Integer.toString(lowVal, 16);
		return result;
	}

	/**
	 * 将十进制整数转换成BCD码格式，转换是倒序结果。如2523转换为03-02-05-02
	 * @param num
	 * @return
	 */
	public byte[] intToBcd(int num){
		String str = String.valueOf(num);
		boolean isNega = false;
		if (str.substring(0, 1).equals("-")) {
			isNega = true;
			str = str.substring(1);
		}
		byte[] data = new byte[str.length()];
		for(int i=0;i<str.length();i++){
			data[i] = Byte.valueOf(str.substring(i, i+1));
		}
		if (isNega == true) {
			data[0] = (byte) (data[0] & 0x0f);
			data[0] = (byte) (data[0] | 0x10);
		}
		data = this.reverseByteOrder(data);
		return data;
	}

	/**
	 * 将int类型转换为16进制字节数组
	 * @param data
	 * @return
	 */
	public byte[] intToHex(long data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt((int) data);
			baos.close();
			dos.close();
			byte[] ret = baos.toByteArray();
			if (ret != null && ret.length == 4) {
				ret = reverseByteOrder(ret);
				return ret;
			} 
			else {
				return null;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将short类型转换为16进制字节数组,结果是倒序
	 * @param data
	 * @return
	 */
	public byte[] shortToHex(int data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeShort(data);
			baos.close();
			dos.close();
			byte[] ret = baos.toByteArray();
			if (ret != null && ret.length == 2) {
				byte temp = ret[0];
				ret[0] = (byte) (ret[1]);
				ret[1] = (byte) (temp);
				return ret;
			} 
			else {
				return null;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将6字节的长整型数转换为16进制
	 * @param data
	 * @return
	 */
	public byte[] int6ToHex(long data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeLong(data);
			baos.close();
			dos.close();
			byte[] temp = baos.toByteArray();
			if (temp != null && temp.length > 6) {
				byte[] ret = new byte[6];
				temp = reverseByteOrder(temp);
				System.arraycopy(temp, 0, ret, 0, 6);
				return ret;
			} 
			else {
				return null;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将8字节的Long型数转换为16进制，获取的字节是倒序的
	 * @param data
	 * @return
	 */
	public byte[] longToHex(long data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeLong(data);
			baos.close();
			dos.close();
			byte[] temp = baos.toByteArray();
			if (temp != null && temp.length <= 8) {
				byte[] ret = new byte[8];
				temp = reverseByteOrder(temp);
				System.arraycopy(temp, 0, ret, 0, 8);
				return ret;
			} 
			else {
				return null;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串转换为BCD码
	 * @param hexStr
	 * @return
	 */
	public byte hexStrToBcd(String hexStr) {
		if (hexStr == null || hexStr.length() != 2) {
			return 0;
		}
		String temp = hexStr.substring(0, 1);
		int result = Integer.parseInt(temp, 16) * 16;
		temp = hexStr.substring(1, 2);
		result += Integer.parseInt(temp, 16);
		return (byte) result;
	}

	/**
	 * bcd字符串转换为16进制数组
	 */
	public byte[] bcdStringToByte(String str){ 
		// 待增加
		if (str == null || str.equals("") || str.length() % 2 != 0)
			return null;

		int len = str.length() / 2;
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			String temp = str.substring(i * 2, i * 2 + 2);
			try {
				int val = Integer.parseInt(temp);
				data[i] = (byte) val;
			} 
			catch (NumberFormatException e) {
				return null;
			}
		}
		return data;
	}

	/**
	 * 字符串转换为BCD数组
	 * @param str
	 * @return
	 */
	public byte[] stringToBcdByte(String str) {
		if (str == null || str.equals("") || str.length() % 2 != 0)
			return null;

		int len = str.length() / 2;
		byte[] frame = new byte[len];
		for (int i = 0; i < len; i++) {
			String temp = str.substring(i * 2, i * 2 + 2);
			int val = Integer.parseInt(temp, 16);
			frame[i] = (byte) val;
		}
		return frame;
	}

	/**
	 * 倒序字节数组
	 * @param data
	 * @return
	 */
	public byte[] reverseByteOrder(byte[] data) {
		if (data != null && data.length != 0) {
			for (int i = 0; i < data.length / 2; i++) {
				byte temp = data[i];
				data[i] = data[data.length - i - 1];
				data[data.length - i - 1] = temp;
			}
		}
		return data;
	}

	/**
	 * 位移函数,每次移动一位
	 * @param data 位移字节
	 * @param direction 位移方向
	 * @return
	 */
	public byte bitMove(byte data, int direction) {
		int intData = hexToInt(data);
		if (direction == BITMOVE_DIR_LEFT) {
			intData = intData << 1;
		} 
		else {
			intData = intData >> 1;
		}
		return (byte) intData;
	}


	/**
	 * 位移函数,移动num位
	 * @param data 位移字节
	 * @param direction 位移方向
	 * @param num 位移位数
	 * @return
	 */
	public byte bitMove(byte data, int direction, int num) {
		for (int i = 0; i < num; i++) {
			data = bitMove(data, direction);
		}
		return data;
	}
	
	
	/*********************************************************************************
	 * 计算函数区
	 *********************************************************************************/
	/**
	 * 计算函数-10次方
	 * @param data
	 * @param powerNum 平方数
	 * @return
	 */
	public double tenPower(int data, int powerNum) {
		double result = data;
		if (powerNum > 0) {
			//正数次方
			for (int i = 0; i < powerNum; i++) {
				result *= 10;
			}
		} 
		else if (powerNum < 0) {
			//负数次方
			for (int i = powerNum; i < 0; i++) {
				result /= 10;
			}
		}
		return result;
	}

	/**
	 * 计算函数-2的n次方
	 * @param num
	 * @return
	 */
	public int pow2(int num) {
		int value = 1;
		for (int i = 0; i < num; i++) {
			value *= 2;
		}
		return value;
	}

	/**
	 * 组织BCD
	 * @param value
	 * @param digitLen
	 * @param decLen
	 * @return
	 */
	public byte[] organizeBcd(String value, int digitLen, int decLen) {
		if (value == null || value.equals("") || decLen < 0 || (digitLen + decLen) % 2 != 0) {
			return null;
		}

		int len = (digitLen + decLen) / 2;

		boolean isNega = false;
		if (value.substring(0, 1).equals("-")) {
			isNega = true;
			value = value.substring(1);
		}

		byte[] data = null;
		String pattern = "";
		if (digitLen > 0) {
			pattern = "\\d{1," + digitLen + "}";
		} else {
			pattern = "\\d{1}";
		}
		if (decLen > 0) {
			pattern += "(\\.\\d{1," + decLen + "})?";
		}
		if (Pattern.matches(pattern, value)) {
			data = new byte[len];
			String[] subValues = value.split("\\.");
			if (digitLen > 0) {
				while (subValues[0].length() < digitLen) {
					subValues[0] = "0" + subValues[0];
				}
			} else {
				subValues[0] = "";
			}
			if (subValues.length == 2) {
				subValues[0] += subValues[1];
			}
			while (subValues[0].length() < digitLen + decLen) {
				subValues[0] += "0";
			}

			for (int i = 0; i < digitLen + decLen; i += 2) {
				String tempStr = subValues[0].substring(i, i + 2);
				byte tempBcd = hexToBcd(Byte.parseByte(tempStr, 10), 0);
				data[i / 2] = tempBcd;
			}
			if (isNega == true) {
				data[0] = (byte) (data[0] & 0x0f);
				data[0] = (byte) (data[0] | 0x10);
			}
			data = reverseByteOrder(data);
		}
		return data;
	}

	/**
	 * 将指定数据按照指定生成多项式进行CRC校验运算
	 * @param data 指定缓存区
	 * @param len 从缓存区0位置开始计数，校验指定len长度的数据
	 * @param polynomial 生成多项式，如多项式X15+X13+1用0xA001表示
	 * X16 + X15 + X2 + 1 => 1 1000 0000 0000 0101
	 * @return
	 */
	public byte[] crc16(byte[] data, int len,int polynomial) {
		byte crc16Low = (byte)0xFF;
		byte crc16High = (byte)0xFF;

		byte[] polynomialArr=shortToHex(polynomial);
		byte polynomialLow = polynomialArr[0];
		byte polynomialHigh = polynomialArr[1];

		for (int i = 0; i < len; i++) {
			crc16Low ^= data[i];
			for (int j = 0; j < 8; j++) {
				byte temp16Low = crc16Low;
				byte temp16High = crc16High;

				crc16Low = bitMove(crc16Low, Protocol.BITMOVE_DIR_RIGHT);
				crc16High = bitMove(crc16High, Protocol.BITMOVE_DIR_RIGHT);
				if ((temp16High & 0x01) == 0x01) {
					crc16Low = (byte) (crc16Low | 0x80);
				}
				
				if ((temp16Low & 0x01) == 0x01) {
					crc16High ^= polynomialHigh;
					crc16Low ^= polynomialLow;
				}
			}
		}

		return new byte[] { crc16Low, crc16High };
	}
	
	public static byte[] cut33(byte[] data){
		if(data==null || data.length==0)
			return data;
		byte[] cd = new byte[data.length];
		System.arraycopy(data, 0, cd, 0, cd.length);
		for(int i=0;i<cd.length;i++){
			cd[i] = (byte)(cd[i] - 0x33);
		}
		return cd;
	}
	
	public static byte[] add33(byte[] data){
		if(data==null || data.length==0)
			return data;
		byte[] cd = new byte[data.length];
		System.arraycopy(data, 0, cd, 0, cd.length);
		for(int i=0;i<cd.length;i++){
			cd[i] = (byte)(cd[i] + 0x33);
		}
		return cd;
	}
	
	public static boolean setSystemTime(Calendar calendar){
		int year=calendar.get(Calendar.YEAR);
		int mon=calendar.get(Calendar.MONTH)+1;
		int day=calendar.get(Calendar.DAY_OF_MONTH);
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		int min=calendar.get(Calendar.MINUTE);
		int second=calendar.get(Calendar.SECOND);
		String osName = System.getProperty("os.name");
		try {
			if (osName.matches("^(?i)Windows.*$")) {// Window 系统
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
				if(Locale.getDefault().getLanguage().equalsIgnoreCase("zh")){
					sdf = new SimpleDateFormat("yy/MM/dd");
				}
				String date = sdf.format(calendar.getTime());
				//String date=year+"-"+mon+"-"+day;// 格式：yyyy-MM-dd
				String time = hour + ":" + min + ":" + second;// 格式 HH:mm:ss
				Runtime.getRuntime().exec(" cmd /c time "+time);
				Runtime.getRuntime().exec(" cmd /c date "+date);
				return true;
			} 
			else {
				// Linux 系统
				NumberFormat nf = new DecimalFormat("00");
				String date = year + nf.format(mon) + nf.format(day);// 格式：yyyyMMdd
				String time = hour + ":" + min + ":" + second;// 格式 HH:mm:ss
				Runtime.getRuntime().exec(" date -s "+date);
				Runtime.getRuntime().exec(" date -s "+time);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean setSystemTime(TimeTag time){
		return setSystemTime(time.getCalendar());
	}
	
	public  String parseHexToInt(byte[] data,int decNum){
		Protocol protocol = Protocol.getInstance();
		String str="";
		for(int i=0;i<data.length;i++){
			str += protocol.hexToHexString(data[i]);
		}
		String valueStr = Long.valueOf(str, 16).toString();
		while(valueStr.length()<=decNum){
			valueStr = "0"+valueStr;
		}
		
		valueStr = valueStr.substring(0, (valueStr.length()-decNum))+"."+valueStr.substring((valueStr.length()-decNum));
			
		return valueStr;
	}
	
	
	/******************************************************************************
     * Compilation:  javac CRC16.java
     * Execution:    java CRC16 s
     * <p>
     * Reads in a string s as a command-line argument, and prints out
     * its 16-bit Cyclic Redundancy Check (CRC16). Uses a lookup table.
     * <p>
     * Reference:  http://www.gelato.unsw.edu.au/lxr/source/lib/crc16.c
     * <p>
     * % java CRC16 123456789
     * CRC16 = bb3d
     * <p>
     * Uses irreducible polynomial:  1 + x^2 + x^15 + x^16
     ******************************************************************************/
	public static byte[] crc(byte[] frame, int beginPos, int endPos) {
		int[] table = {
                0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
                0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
                0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
                0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
                0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
                0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
                0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
                0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
                0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
                0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
                0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
                0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
                0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
                0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
                0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
                0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
                0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
                0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
                0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
                0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
                0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
                0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
                0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
                0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
                0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
                0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
                0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
                0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
                0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
                0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
                0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
                0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
        };

        byte[] bytes = new byte[endPos - beginPos + 1];
        System.arraycopy(frame, beginPos, bytes, 0, endPos - beginPos + 1);
        int crc = 0x0000;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
        }
        String crc1 = Integer.toHexString(crc);
        while (crc1.length() < 4) {
            crc1 = "0" + crc1;
        }

        System.out.println("CRC16 = " + crc1);
        Protocol protocol = Protocol.getInstance();
        return protocol.shortToHex(crc);
    }
	
	/**
	 * 获取mac地址
	 * @param seperator
	 * @return
	 */
	public static List<String> getMacAddress(String seperator) {
		List<String> rst=new ArrayList<String>();
        String os = System.getProperty("os.name").toLowerCase();
        Pattern pattern = Pattern.compile("([0-9a-fA-F]{2})(([\\s:-][0-9a-fA-F]{2}){5})");
        if (os != null && os.startsWith("windows")) {
            try {
                String command = "cmd.exe /c ipconfig /all";
                Process p = Runtime.getRuntime().exec(command);
                BufferedReader br =new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                	Matcher m = pattern.matcher(line);
					if (line.indexOf("Physical Address") > 0 || line.indexOf("物理地址") > 0 || m.find()) {
						int index = line.indexOf(":");
						index += 2;
						String address = line.substring(index).toUpperCase();
						if (checkValidMac(address)) {
							address=address.replaceAll("-", seperator);
                        	if (!rst.contains(address)){
                        		rst.add(address);
                        	}
                        }
                    }
                }
                br.close();
            } catch (IOException e) {}
        }
        return rst;
    }
	
	/**
	 * 校验mac地址
	 * @param mac
	 * @return
	 */
	private static boolean checkValidMac(String mac){
		String[] segs=mac.split("-");
		if (segs.length==6){
			for (String seg:segs){
				if (!seg.equals("00"))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param paras
	 * @return
	 */
	public byte[] organizeBytes(List<byte[]> paras){
		if (paras==null||paras.isEmpty())
			return null;
		int len = 0;
		for (byte[] b:paras) {
			if (b != null) {
				len += b.length;
			}
		}
		byte[] data = new byte[len];
		int pos = 0;
		for (byte[] b:paras) {
			if (b != null) {
				System.arraycopy(b, 0, data, pos, b.length);
				pos += b.length;
			}
		}
		return data;
	}
	
	/** 
     * 获得一个UUID 
     * @return String UUID 
     */ 
    public String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    }
    
    static byte[] crc16_tab_h = { (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40 };

    static byte[] crc16_tab_l = { (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC, (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,
            (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,
            (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0, (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,
            (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54, (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,
            (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40 };
    
    /**
     * 计算CRC16校验
     *
     * @param data
     *            需要计算的数组
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data) {
        return calcCrc16(data, 0, data.length);
    }

    /**
     * 计算CRC16校验
     *
     * @param data
     *            需要计算的数组
     * @param offset
     *            起始位置
     * @param len
     *            长度
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len) {
        return calcCrc16(data, offset, len, 0xffff);
    }

    /**
     * 计算CRC16校验
     *
     * @param data
     *            需要计算的数组
     * @param offset
     *            起始位置
     * @param len
     *            长度
     * @param preval
     *            之前的校验值
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len, int preval) {
        int ucCRCHi = (preval & 0xff00) >> 8;
        int ucCRCLo = preval & 0x00ff;
        int iIndex;
        for (int i = 0; i < len; ++i) {
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];
            ucCRCHi = crc16_tab_l[iIndex];
        }
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
    }


    /**
     *
     * @param data 需要计算的数组
     * @param offset 起始位置
     * @param len 长度
     * @return
     */
    public static boolean checkCrc16(byte[] data, int offset, int len){
        if(calcCrc16(data, offset, len) == 0)
        	return true;
        else 
        	return false;
    }
}
