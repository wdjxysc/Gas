package sockets.iotmeter.constant;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 命令样本
 * @author Administrator
 *
 */
public class PiaoaiCommand implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获得当前所有命令所有标签
	 * @return
	 */
	public static ArrayList<String> getCommandLable() {
		ArrayList<String> commandLabelList = new ArrayList<>();
		commandLabelList.add(cmd3003);
        commandLabelList.add(cmd3004);
        commandLabelList.add(cmd3021);
        commandLabelList.add(cmd3022);

        commandLabelList.add(cmd3027);
        commandLabelList.add(cmd3028);
        commandLabelList.add(cmd3023);
        commandLabelList.add(cmd3024);
        commandLabelList.add(cmd3046);
        commandLabelList.add(cmd3042);

        commandLabelList.add(cmd3080);
        commandLabelList.add(cmd3082);
        commandLabelList.add(cmd3085);
        commandLabelList.add(cmd3086);

        commandLabelList.add(cmd3087);
        commandLabelList.add(cmd3088);
        commandLabelList.add(cmd3089);
        commandLabelList.add(cmd3090);
		return commandLabelList;
		
	}
	
	/**
	 * 获得当前所有命令的说明
	 * @return
	 */
	public static ArrayList<String> getCommandNote() {
		ArrayList<String> commandNoteList = new ArrayList<>();
		commandNoteList.add(cmd3003_name);
		commandNoteList.add(cmd3004_name);
		commandNoteList.add(cmd3021_name);
		commandNoteList.add(cmd3022_name);
		commandNoteList.add(cmd3027_name);
		commandNoteList.add(cmd3028_name);
		commandNoteList.add(cmd3023_name);
		commandNoteList.add(cmd3024_name);
		commandNoteList.add(cmd3046_name);
		commandNoteList.add(cmd3042_name);
		commandNoteList.add(cmd3080_name);
		commandNoteList.add(cmd3082_name);
		commandNoteList.add(cmd3085_name);
		commandNoteList.add(cmd3086_name);
		commandNoteList.add(cmd3087_name);
		commandNoteList.add(cmd3088_name);
		commandNoteList.add(cmd3089_name);
		commandNoteList.add(cmd3090_name);
		return commandNoteList;
	}
	
	/**
	 * 获得指定细节的命令，细节参见参数说明
	 * @param cmdFlag 命令的类型：3003到3090
	 * @param isDown 是否主站下行
	 * @return
	 */
	public static String getCommandStringSample(String cmdFlag, boolean isDown) {
		 String result = null;
		 switch (cmdFlag) {
		 	case "3021":
		 		if (isDown)
		 			result = cmd3021_down_ask;
		 		else
		 			result = cmd3021_up_reply;
		 		break;
		 	case "3022":
	             if (isDown)
	                 result = cmd3022_down_ask;
	             else
	                 result = cmd3022_up_reply;
	             break;
		 	case "3027":
	             if (isDown)
	                 result = cmd3027_down_ask;
	             else
	                 result = cmd3027_up_reply;
	             break;
		 	case "3028":
	             if (isDown)
	                 result = cmd3028_down_ask;
	             else
	                 result = cmd3028_up_reply;
	             break;
		 	case "3023":
	             if (isDown)
	                 result = cmd3023_down_ask;
	             else
	                 result = cmd3023_up_reply;
	             break;
		 	case "3024":
	             if (isDown)
	                 result = cmd3024_down_ask;
	             else
	                 result = cmd3024_up_reply;
	             break;
		 	case "3080":
	             if (isDown)
	                 result = cmd3080_down_ask;
	             else
	                 result = cmd3080_up_reply;
	             break;
		 	case "3082":
	             if (isDown)
	                 result = cmd3082_down_ask;
	             else
	                 result = cmd3082_up_reply;
	             break;
		 	case "3085":
	             if (isDown)
	                 result = cmd3085_down_ask;
	             else
	                 result = cmd3085_up_reply;
	             break;
		 	case "3086":
	             if (isDown)
	                 result = cmd3086_down_ask;
	             else
	                 result = cmd3086_up_reply;
	             break;
		 	case "3087":
	             if (isDown)
	                 result = cmd3087_down_ask;
	             else
	                 result = cmd3087_up_reply;
	             break;
		 	case "3088":
	             if (isDown)
	                 result = cmd3088_down_ask;
	             else
	                 result = cmd3088_up_reply;
	             break;
		 	case "3089":
	             if (isDown)
	                 result = cmd3089_down_ask;
	             else
	                 result = cmd3089_up_reply;
	             break;
		 	case "3090":
	             if (isDown)
	                 result = cmd3090_down_ask;
	             else
	                 result = cmd3090_up_reply;
	             break;
		 	 default:
	                result = null;
		 }
		 
		 // replace "|"
		 result = result.replace("|", "");
	     return result;
	}
	
	/**3003*/
	public static final String cmd3003 = "3003";
	public static final String cmd3003_name = "(从站向主站)发送心跳包，主站返回报文目的是方便从站确定通讯故障及时切换通讯通道";
	/**从站上行发送*/
	public static final String cmd3003_up_ask = "68|001A|3003|01|00|20011607000001|16071700000001|0000|0000|16";
	/**主站下行回复*/
	public static final String cmd3003_down_reply = "";
	public static final String cmd3003_logic = "开始符|报文长度|功能码|传送方向|请求响应标识|从站编号|报文ID|数据域长度|数据域|CRC16校验码|结束符";
	
	/**3004*/
	public static final String cmd3004 = "3004";
	public static final String cmd3004_name = "(从站向主站)建立连接后，发送注册包，注册包每次登录时发送";
	public static final String cmd3004_up_ask = "68|001A|3004|01|00|20011607000001|16071700000001|0000|0000|16";
	public static final String cmd3004_down_reply = "";
	
	/**3021*/
	public static final String cmd3021 = "3021";
	public static final String cmd3021_name = "(主站向从站)发送心跳周期设置指令";
	/**主站下行发送*/
	public static final String cmd3021_down_ask = "68|0021|3021|00|00|20011607000001|16071700000001|0002|003C|0000|16";
	/**从站上行回复*/
	public static final String cmd3021_up_reply = "68|001c|3021|00|01|20011607000001|16071700000001|0002|003c|2cba|16";
	
	/**3022*/
	public static final String cmd3022 = "3022";
	public static final String cmd3022_name = "(主站向从站)发送心跳周期查询指令";
	public static final String cmd3022_down_ask = "68|0021|3022|00|00|20011607000001|16071700000001|0000|0000|16";
	public static final String cmd3022_up_reply = "68|001a|3022|00|01|20011607000001|16071700000001|0000|8e4a|16";
	
	/**3027*/
	public static final String cmd3027 = "3027";
	public static final String cmd3027_name = "(主站向从站)发送主站IP地址设置指令";
	public static final String cmd3027_down_ask = "68|0021|3027|00|00|20011607000001|16071700000001|0009|192168001005|008080|0000|16";
	public static final String cmd3027_up_reply = "68|0023|3027|00|01|20011607000001|16071700000001|0009|192168001005|008080|56e2|16";
	
	/**3028*/
	public static final String cmd3028 = "3028";
	public static final String cmd3028_name = "(主站向从站)发送主站IP地址查询指令";
	public static final String cmd3028_down_ask = "";
	public static final String cmd3028_up_reply = "";
	
	/**3023*/
	public static final String cmd3023 = "3023";
	public static final String cmd3023_name = "(主站向从站)发送上报周期设置指令";
	public static final String cmd3023_down_ask = "";
	public static final String cmd3023_up_reply = "";
	
	/**3024*/
	public static final String cmd3024 = "3024";
	public static final String cmd3024_name = "(主站向从站)发送上报周期查询指令";
	public static final String cmd3024_down_ask = "";
	public static final String cmd3024_up_reply = "";
	
	/**3046*/
	public static final String cmd3046 = "3046";
	public static final String cmd3046_name = "(从站向主站)发送智能检测设备数据上报";
	public static final String cmd3046_up_ask = "68|0029|3046|01|00|20011607000001|16071700000001|000F|01|00|3C6300|320609|50020A|3C1C|3C1C|0000|16";
	public static final String cmd3046_down_reply = "68|0026|3046|00|01|20011607000001|16071700000001|000F|01|00|3c6300|320609|50020a|3c1c|3c1c|000001003c6300c0ab00000000000016";
	
	/**3042*/
	public static final String cmd3042 = "3042";
	public static final String cmd3042_name = "(从站向主站)当设备出现异常时，主动上报当前异常信息";
	public static final String cmd3042_up_ask = "68|0023|3042|01|00|20011607000001|16071700000001|0009|20160720|232830|1001|0000|16";
	public static final String cmd3042_down_reply = "";
	
	/**3080*/
	public static final String cmd3080 = "3080";
	public static final String cmd3080_name = "(主站向从站)发送设备复位";
	public static final String cmd3080_down_ask = "";
	public static final String cmd3080_up_reply = "";
	
	/**3082*/
	public static final String cmd3082 = "3082";
	public static final String cmd3082_name = "(主站向从站)发送获取设备软件版本及硬件版本";
	public static final String cmd3082_down_ask = "";
	public static final String cmd3082_up_reply = "";
	
	/**3085*/
	public static final String cmd3085 = "3085";
	public static final String cmd3085_name = "(主站向从站)发送固件升级格式约定";
	public static final String cmd3085_down_ask = "";
	public static final String cmd3085_up_reply = "";
	
	/**3086*/
	public static final String cmd3086 = "3086";
	public static final String cmd3086_name = "(主站向从站)发送新固件数据";
	public static final String cmd3086_down_ask = "";
	public static final String cmd3086_up_reply = "";
	
	/**3087*/
	public static final String cmd3087 = "3087";
	public static final String cmd3087_name = "(主站向从站)发送新固件存储地址";
	public static final String cmd3087_down_ask = "";
	public static final String cmd3087_up_reply = "";
	
	/**3088*/
	public static final String cmd3088 = "3088";
	public static final String cmd3088_name = "(主站向从站)发送校验新固件数据";
	public static final String cmd3088_down_ask = "";
	public static final String cmd3088_up_reply = "";
	
	/**3089*/
	public static final String cmd3089 = "3089";
	public static final String cmd3089_name = "(主站向从站)发送终止设备固件更新";
	public static final String cmd3089_down_ask = "";
	public static final String cmd3089_up_reply = "";
	
	/**3090*/
	public static final String cmd3090 = "3090";
	public static final String cmd3090_name = "(主站向从站)发送当设备应答帧序号异常时，获取设备当前帧序号";
	public static final String cmd3090_down_ask = "";
	public static final String cmd3090_up_reply = "";
	
	//----------------------------命令格式------------------------------------
	
    public static final byte[] CommunicateSendStoC = {(byte) 0x68, (byte) 0xff, (byte) 0x00, (byte) 0x20, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x05, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x16};
    public static final byte[] CommunicateBackCtoS = {(byte) 0x68, (byte) 0xff, (byte) 0x00, (byte) 0x20, (byte) 0x01,
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x16};

    //------------------------------- 命令部分----------------------------------
    
    /***起始符*/
    public static final byte beginCode = 0x68;
    
    /***传送方向 1：从站发出*/
    public static final byte conToMs = 0x01;
    
    /***传送方向 0：主站发出*/
    public static final byte msToCon = 0x00;
    
    /*** 请求响应标识 0：请求*/
    public static final byte requiry = 0x00;
    
    /*** 请求响应标识 1：响应*/
    public static final byte response = 0x01;
}
