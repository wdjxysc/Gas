package webapp.sockets.iotmeter.frame;


import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.constant.PiaoaiCommand;
import webapp.sockets.iotmeter.db.dao.AirIndexDao;
import webapp.sockets.iotmeter.db.dao.DeviceAnomalyDao;
import webapp.sockets.iotmeter.db.dao.DeviceDao;
import webapp.sockets.iotmeter.db.vo.AirIndexVo;
import webapp.sockets.iotmeter.db.vo.DeviceAnomalyVo;
import webapp.sockets.iotmeter.db.vo.DeviceVo;
import webapp.sockets.iotmeter.encode.RTHCDecoder;
import webapp.sockets.util.Protocol;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 *
 */
public class Responder implements Serializable{
	private static Logger log = Logger.getLogger(Responder.class);
	//返回字节
	private byte[] returnByte;
	
	/**
	 * 构造方法
	 * @param receiveCommand 字节数组
	 */
	public Responder(byte[] receiveCommand,String ip,int port) {
        log.info("the responder get the command in BYTES");
        byte[] command = receiveCommand;
        commandHandler(command,ip,port);
    }
	
	 /**
     * 构造方法，参数为字符串
     *
     * @param receiveCommand
     */
    public Responder(String receiveCommand,String ip,int port) {
    	log.info("the responder get the command in STRING");
        byte[] command = Protocol.getInstance().hexStringToByte(receiveCommand);
        commandHandler(command,ip,port);
    }
    
    /**
     * 所有命令的处理，不管是谁发出的，算是命令的一种解析
     *
     * @param command
     */
    private void commandHandler(byte[] command,String ip,int port) {
    	log.info("commandHandler(byte[] command,String ip,int port)方法开始处理...");
        if (isResponseFrame(command)) {
        	//如果为响应
            responseFrame(command);
        } 
        else {
        	//如果为请求
            requiredFrame(command,ip,port);
        }

    }
    
    /**
     * 判断是否为回复帧。回复帧有上行回复帧和下行回复帧；请求帧有下行请求帧和上行请求帧。这里的目标是找出上行请求帧。
     * 回复帧 response frame；
     * 请求帧 required frame
     * @param command
     * @return
     */
    public boolean isResponseFrame(byte[] command) {
    	log.info("isResponseFrame(byte[] command) 方法开始处理...");
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        //获得响应标识
        byte rf = (byte) decoder.getLogicFieldByteArray2(command, 5);
        //01响应，00为请求
        if (rf == 0x01) {
            return true;
        } 
        else {
            return false;
        }
    }
    
    /**
     * 一个下行回复帧，或上行回复帧
     *
     * @param command
     */
    private void responseFrame(byte[] command) {
    	log.info("responseFrame(byte[] command) 方法开始处理...");
        if (isDownFrame(command)) {
        	log.info("this frame is a down response frame to concentrator, and no need other operations");
        } 
        else {
            upResonseFrame(command);
        }
        log.info(Protocol.getInstance().hexToHexString(command));
        // other operations
    }
    
    /**
     * 
     * @param command
     */
    private void upResonseFrame(byte[] command) {
    	log.info("upResonseFrame(byte[] command) 方法开始处理...");
    	
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        switch (ccString) {
            case "3021":
                System.out.println(PiaoaiCommand.cmd3021_name);
                break;
            case "3022":
                System.out.println(PiaoaiCommand.cmd3022_name);
                break;
            case "3027":
            	System.out.println(PiaoaiCommand.cmd3027_name);
                break;
            case "3028":
            	System.out.println(PiaoaiCommand.cmd3028_name);
                break;
            case "3023":
            	System.out.println(PiaoaiCommand.cmd3023_name);
                break;
            case "3024":
            	System.out.println(PiaoaiCommand.cmd3024_name);
                break;
            case "3080":
            	System.out.println(PiaoaiCommand.cmd3080_name);
                break;
            case "3082":
            	System.out.println(PiaoaiCommand.cmd3082_name);
                break;
            case "3085":
            	System.out.println(PiaoaiCommand.cmd3085_name);
                break;
            case "3086":
            	System.out.println(PiaoaiCommand.cmd3086_name);
                break;
            case "3087":
            	System.out.println(PiaoaiCommand.cmd3087_name);
                break;
            case "3088":
            	System.out.println(PiaoaiCommand.cmd3088_name);
                break;
            case "3089":
            	System.out.println(PiaoaiCommand.cmd3089_name);
                break;
            case "3090":
            	System.out.println(PiaoaiCommand.cmd3090_name);
                break;
            default:
                break;
        }
    }
    
    /**
     * 请求帧的处理，请求帧分为上行请求帧和下行请求帧。
     *
     * @param command
     */
    private void requiredFrame(byte[] command,String ip,int port) {
    	log.info("requiredFrame(byte[] command,String ip,int port)方法开始处理...");
        if (isDownFrame(command)) {
        	//如果是主站发送
            downFrame(command);
        } 
        else {
        	//如果是从站发送
            upFrame(command,ip,port);
        }
    }
    
    /**
     * 判断是否为下行请求帧
     *
     * @param command
     * @return
     */
    private boolean isDownFrame(byte[] command) {
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        //获得传送方向标识
        byte df = (byte) decoder.getLogicFieldByteArray2(command, 4);
        //01为从站发送，00为主站发送
        if (df == 0x01) {
            return false;
        } 
        else {
            return true;
        }
    }
    
    /**
     * 主站向从站发送指令
     * @param command
     */
    private void downFrame(byte[] command) {
    	log.info("downFrame(byte[] command)方法开始处理...");
    	log.info("================主站向从站发送指令====================");
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        switch (ccString) {
	        case "3021":
	        	command3021DownRequired(command);
	            break;
	        case "3022":
	        	command3022DownRequired(command);
	            break;
	        case "3027":
	        	command3027DownRequired(command);
	            break;
	        case "3028":
	        	command3028DownRequired(command);
	            break;
	        case "3023":
	        	command3023DownRequired(command);
	            break;
	        case "3024":
	        	command3024DownRequired(command);
	            break;
	        case "3080":
	        	command3080DownRequired(command);
	            break;
	        case "3082":
	        	command3082DownRequired(command);
	            break;
	        case "3085":
	        	command3085DownRequired(command);
	            break;
	        case "3086":
	        	command3086DownRequired(command);
	            break;
	        case "3087":
	        	command3087DownRequired(command);
	            break;
	        case "3088":
	        	command3088DownRequired(command);
	            break;
	        case "3089":
	        	command3089DownRequired(command);
	            break;
	        case "3090":
	        	command3090DownRequired(command);
	            break;
	        default:
	            break;
        }
    }
    
    /**
     * 从站向主站发送
     * @param command
     */
    private void upFrame(byte[] command,String ip,int port) {
    	log.info("upFrame(byte[] command,String ip,int port)方法开始处理...");
    	log.info("==================从站向主站发送指令=========================");
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        switch (ccString) {
            // 仅有四条命令是要返回响应的。
            case "3003":
            	command3003Response(command,ip,port);
                break;
            case "3004":
            	command3004Response(command,ip,port);
                break;
            case "3046":
            	command3046Response(command);
                break;
            case "3042":
            	command3042Response(command);
                break;
            default:
                log.info("something wrong, get unrecognized CONTROL CODE:" + ccString);
        }
    }
    
    /**
     * 3003从站向主站发送心跳包
     * @param command
     */
    private void command3003Response(byte[] command,String ip,int port) {
    	log.info("command3003Response(byte[] command,String ip,int port) 方法开始处理...");
    	byte[] receive = command;
    	byte[] returnByte = new byte[28];
    	
    	int pos = 0;
        // 68
    	returnByte[0] = 0x68;
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        pos += 1;
        
        //报文长度
        byte[] lengthOfFrame = new byte[]{0x1c,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;
        
        //功能- 3003
        byte[] ctrlCode = new byte[]{0x30,0x03};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;
        
        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x01};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //从站编号
        System.arraycopy(receive, pos, returnByte, pos, 7);
        byte[] devNoByte = new byte[7];
        System.arraycopy(receive, pos, devNoByte, 0, 7);
        String devNo = Protocol.getInstance().hexToHexString(devNoByte);
        pos += 7;
        
        //报文ID
        System.arraycopy(receive, pos, returnByte, pos, 7);
        pos += 7;
        
        //数据域长度
        byte[] add_data_field_length = new byte[]{0x02, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //CRC-16的生成多项式x16+x15+x2+1， 0x8005
        //byte[] add_crc_code = Protocol.getInstance().crc16(returnByte, pos, 0x8005);
        //System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        //pos += 2;
        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;
        
        
        returnByte[returnByte.length - 1] = 0x16;
        
        this.opDevice(devNo,ip,port);

        this.returnByte = returnByte;
    }
    
    private void opDevice(String devNo,String ip,int port){
    	DeviceVo vo = new DeviceVo();
    	DeviceDao dao = new DeviceDao();
    	int count = dao.queryDeviceNo(devNo);
    	if(count==0){
    		vo.setId(Protocol.getInstance().getUUID());
    		vo.setClientIp(ip);
    		vo.setClientPort(port);
    		try{
    			dao.saveDevice(vo);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	else{
    		vo.setClientIp(ip);
    		vo.setClientPort(port);
    		vo.setDeviceNo(devNo);
    		try{
    			dao.updateDevice(vo);
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * 3004从站向主站建立连接后发送注册包，注册包每次登录时发送
     * @param command
     */
    private void command3004Response(byte[] command,String ip,int port) {
    	log.info("command3004Response(byte[] command,String ip,int port) 方法开始处理...");
    	byte[] receive = command;
    	byte[] returnByte = new byte[28];
    	
    	int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
    	returnByte[0] = 0x68;
        pos += 1;
        
        //报文长度
        byte[] lengthOfFrame = new byte[]{0x1C,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;
        
        //功能- 3004
        byte[] ctrlCode = new byte[]{0x30,0x04};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;
        
        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x01};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //从站编号
        System.arraycopy(receive, pos, returnByte, pos, 7);
        pos += 7;
        
        //报文ID
        System.arraycopy(receive, pos, returnByte, pos, 7);
        pos += 7;
        
        //数据域长度
        byte[] add_data_field_length = new byte[]{0x02, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //CRC-16的生成多项式x16+x15+x2+1， 0x8005
        //byte[] add_crc_code = Protocol.getInstance().crc16(returnByte, pos, 0x8005);
        //System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        //pos += 2;
        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;
        
        returnByte[returnByte.length - 1] = 0x16;
        

        this.returnByte = returnByte;
    	
    }
    
    /**
     * 3046从站向主站智能检测设备数据上报
     * @param command
     */
    private void command3046Response(byte[] command) {
    	log.info("command3046Response(byte[] command) 方法开始处理...");
    	byte[] receive = command;
    	byte[] returnByte = new byte[28];
    	
        int pos = 0;
        // 68
        returnByte[0] = 0x68;
        pos += 1;
        
        //报文长度
        byte[] lengthOfFrame = new byte[]{0x1C,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;
        
        //功能- 3046
        byte[] ctrlCode = new byte[]{0x30,0x46};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;
        
        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x01};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //从站编号
        System.arraycopy(receive, pos, returnByte, pos, 7);
        byte[] devNoByte = new byte[7];
        System.arraycopy(receive, pos, devNoByte, 0, 7);
        String devNo = Protocol.getInstance().hexToHexString(devNoByte);
        pos += 7;
       
        //报文ID
        System.arraycopy(receive, pos, returnByte, pos, 7);
        pos += 7;
        
        //数据域长度
        byte[] add_data_field_length = new byte[]{0x02, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;
        
        byte[] data = new byte[15];
        System.arraycopy(receive, pos, data, 0, 15);
        String dataStr = Protocol.getInstance().hexToHexString(data);
        this.opAirIndex(dataStr,devNo);
        
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //CRC-16的生成多项式x16+x15+x2+1， 0x8005
        //byte[] add_crc_code = Protocol.getInstance().crc16(returnByte, pos, 0x8005);
        //System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        //pos += 2;
        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;
        
        
        returnByte[returnByte.length - 1] = 0x16;
        
        this.returnByte = returnByte;
    }
    
    private void opAirIndex(String dataStr,String devNo){
    	//01 00 3c6300 320609 50020a 3c1c 3c1c
    	//680029304600012001160700000116071700000001000f0000fe7c16
    	AirIndexVo vo = new AirIndexVo();
    	AirIndexDao dao = new AirIndexDao();
    	String type = dataStr.substring(0, 2);
    	int t = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(type));
    	//System.out.println("type:" + t);
    	vo.setType(String.valueOf(t));
    	
    	String rssi = dataStr.substring(2,4);
    	int r = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(rssi));
    	//System.out.println("rssi:" + r);
    	vo.setRssi(String.valueOf(r));
    	
    	String pm25F = dataStr.substring(4,6);
    	int pm25F_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(pm25F));
    	//System.out.println("pm25F:" + pm25F_int);
    	
    	String pm25I = dataStr.substring(6,8);
    	int pm25I_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(pm25I));
    	//System.out.println("pm25I:" + pm25I_int);
    	
    	String pm25I2 = dataStr.substring(8,10);
    	int pm25I2_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(pm25I2));
    	//System.out.println("pm25I2:" + pm25I2_int);
    	String pm25 = String.valueOf(pm25I_int)+ "." +String.valueOf(pm25F_int);
    	vo.setPm25(pm25);
    	
    	String tvoc2 = dataStr.substring(10,12);
    	int tvoc2_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(tvoc2));
    	//System.out.println("tvoc2:" + tvoc2_int);
    	
    	String tvoc1 = dataStr.substring(12,14);
    	int tvoc1_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(tvoc1));
    	//System.out.println("tvoc1:" + tvoc1_int);
    	
    	String tvocI = dataStr.substring(14,16);
    	int tvoc_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(tvocI));
    	//System.out.println("tvoc:" + tvoc_int);
    	String tvoc = String.valueOf(tvoc_int)+"."+String.valueOf(tvoc1_int)+String.valueOf(tvoc2_int);
    	vo.setTvoc(tvoc);
    	
    	String hcho2 = dataStr.substring(16,18);
    	int hcho2_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(hcho2));
    	//System.out.println("hcho2:" + hcho2_int);
    	
    	String hcho1 = dataStr.substring(18,20);
    	int hcho1_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(hcho1));
    	//System.out.println("hcho1:" + hcho1_int);
    	
    	String hchoI = dataStr.substring(20,22);
    	int hcho_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(hchoI));
    	//System.out.println("hcho:" + hcho_int);
    	String hcho = String.valueOf(hcho_int)+"."+String.valueOf(hcho1_int)+String.valueOf(hcho2_int);
    	vo.setHcho(hcho);
    	
    	String temperatureF = dataStr.substring(22,24);
    	int temperatureF_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(temperatureF));
    	//System.out.println("temperatureF:" + temperatureF_int);
    	
    	String temperatureI = dataStr.substring(24,26);
    	int temperatureI_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(temperatureI));
    	//System.out.println("temperatureI:" + temperatureI_int);
    	String temperature = String.valueOf(temperatureI_int)+"."+String.valueOf(temperatureF_int);
    	vo.setTemperature(temperature);
    	
    	String humidityF = dataStr.substring(26,28);
    	int humidityF_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(humidityF));
    	//System.out.println("humidityF:" + humidityF_int);
    	
    	String humidityI = dataStr.substring(28,30);
    	int humidityI_int = Protocol.getInstance().hexToInt(Protocol.getInstance().hexStringToByte(humidityI));
    	//System.out.println("humidity2:" + humidityI_int);
    	String humidity = String.valueOf(humidityF_int)+"."+String.valueOf(humidityI_int);
    	vo.setHumidity(humidity);
    	
    	vo.setDevNo(devNo);
    	vo.setId(Protocol.getInstance().getUUID());
    	try {
			dao.saveAirIndex(vo);
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 反转16进制的字符串 以实现高地位顺序倒序
     * @param hexstr
     * @return
     */
    public static String reversalHexString(String hexstr) {
        String resultstr = "";

        if (hexstr.length() % 2 != 0) {
            return "";
        }

        int length = hexstr.length() / 2;

        for (int i = 0; i < length; i++) {
            int start = (length - i - 1) * 2;
            resultstr += hexstr.substring(start, start + 2);
        }

        return resultstr;
    }
    
    /**
     * 3042从站向主站当设备出现异常时，主动上报当前异常信息
     * @param command
     */
    private void command3042Response(byte[] command) {
    	log.info("command3042Response(byte[] command) 方法开始处理...");
    	byte[] receive = command;
    	byte[] returnByte = new byte[28];
    	int pos = 0;
        // 68
    	returnByte[0] = 0x68;
        pos += 1;
        
        //报文长度
        byte[] lengthOfFrame = new byte[]{0x1C,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;
        
        //功能- 3042
        byte[] ctrlCode = new byte[]{0x30,0x42};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;
        
        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x01};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //从站编号
        System.arraycopy(receive, pos, returnByte, pos, 7);
        byte[] devNoByte = new byte[7];
        System.arraycopy(receive, pos, devNoByte, 0, 7);
        String devNo = Protocol.getInstance().hexToHexString(devNoByte);
        pos += 7;
        
        //报文ID
        System.arraycopy(receive, pos, returnByte, pos, 7);
        pos += 7;
        
        //数据域长度
        byte[] add_data_field_length = new byte[]{0x02, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;
        
        
        byte[] data = new byte[9];
        System.arraycopy(receive, pos, data, 0, 9);
        String dataStr = Protocol.getInstance().hexToHexString(data);
        this.opDeviceNomaly(dataStr, devNo);
        
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;
        
        //CRC-16的生成多项式x16+x15+x2+1， 0x8005
        //byte[] add_crc_code = Protocol.getInstance().crc16(returnByte, pos, 0x8005);
        //System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        //pos += 2;
        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;
        
        returnByte[returnByte.length - 1] = 0x16;
        
        this.returnByte = returnByte;
    }
    
    private void opDeviceNomaly(String dataStr,String devNo){
    	DeviceAnomalyVo vo = new DeviceAnomalyVo();
    	DeviceAnomalyDao dao = new DeviceAnomalyDao();
    	
    	String happenDate = dataStr.substring(0, 8);
    	vo.setHappenDate(happenDate);
    	//System.out.println("happenDate:" + happenDate);
    	
    	String happenTime = dataStr.substring(8, 14);
    	vo.setHappenTime(happenTime);
    	//System.out.println("happenTime:" + happenTime);
    	
    	String nomalyType = dataStr.substring(14, 18);
    	vo.setNomalyType(nomalyType);
    	//System.out.println("nomalyType:" + nomalyType);
    	
    	vo.setDevId(devNo);
    	vo.setId(Protocol.getInstance().getUUID());
    	try {
			dao.saveDeviceNomaly(vo);
		} 
    	catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 
     * @param command 功能码
     * @return
     */
    private byte[] command3021DownRequired(byte[] command){
    	log.info("command3021DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
    	
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //001C
        byte[] lengthOfFrame = new byte[]{0x00,0x1C};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3021
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0002
        byte[] dataFieldLength = new byte[]{0x00, 0x02};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    	
    }
    
    private byte[] command3022DownRequired(byte[] command){
    	log.info("command3022DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[30];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00,0x1E};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3021
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0000
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        //心跳周期
        byte[] xt = new byte[]{0x00,0x3C};
        System.arraycopy(xt, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    	
    }
    
    private byte[] command3027DownRequired(byte[] command){
    	log.info("command3027DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[37];
        
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x20};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x09};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //ip
        System.arraycopy(receive, pos, sendByte, pos, 6);
        pos += 6;
        
        //port
        System.arraycopy(receive, pos, sendByte, pos, 3);
        pos += 3;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3028DownRequired(byte[] command){
    	log.info("command3028DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[37];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        //ip
        String ip_str = "127000000001";
        byte[] ip = Protocol.getInstance().hexStringToByte(ip_str);
        System.arraycopy(ip, 0, sendByte, pos, 6);
        pos += 6;
        
        //port
        String port_str = "008888";
        byte[] port = Protocol.getInstance().hexStringToByte(port_str);
        System.arraycopy(port, 0, sendByte, pos, 3);
        pos += 3;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    	
    }
    
    private byte[] command3023DownRequired(byte[] command){
    	log.info("command3023DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[32];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x04};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //上报周期
        System.arraycopy(receive, pos, sendByte, pos, 4);
        pos += 4;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    	
    }
    
    private byte[] command3024DownRequired(byte[] command){
    	log.info("command3024DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[32];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        //上报周期
        String sbzq = "012C0000";
        byte[] sbzq_byte = Protocol.getInstance().hexStringToByte(sbzq);
        System.arraycopy(sbzq_byte, 0, sendByte, pos, 4);
        pos += 4;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3080DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    	
    }
    
    private byte[] command3082DownRequired(byte[] command){
    	
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3085DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3086DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3087DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3088DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3089DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    private byte[] command3090DownRequired(byte[] command){
    	log.info("command3080DownRequired(byte[] command) 方法开始处理...");
    	byte[] receive = command;
        byte[] sendByte = new byte[28];
        
        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;
        
        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x25};
        System.arraycopy(lengthOfFrame, 0, sendByte, pos, 2);
        pos += 2;
        
        // 3027
        System.arraycopy(receive, pos, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] directionAndRequired = new byte[]{0x01, 0x01};
        System.arraycopy(directionAndRequired, 0, sendByte, pos, 2);
        pos += 2;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        System.arraycopy(receive, pos, sendByte, pos, 7);
        pos += 7;
        
        // 0009
        byte[] dataFieldLength = new byte[]{0x00, 0x00};
        System.arraycopy(dataFieldLength, 0, sendByte, pos, 2);
        pos += 2;
        
        //响应码
        byte[] response = new byte[]{0x00, 0x00};
        System.arraycopy(response, 0, sendByte, pos, 2);
        pos += 2;
        
        // 0000
        byte[] crcCode = Protocol.getInstance().crc16(sendByte, sendByte.length - 3, 0x8005);
        System.arraycopy(crcCode, 0, sendByte, pos, 2);
        
        // 16
        sendByte[sendByte.length - 1] = 0x16;
        
        this.returnByte = sendByte;
        
		return sendByte;
    }
    
    public byte[] getReturnByte() {
        return returnByte;
    }

    private boolean checkReceivedFrame() {
        boolean isValidFrame = true;
        return isValidFrame;
    }
    
}
