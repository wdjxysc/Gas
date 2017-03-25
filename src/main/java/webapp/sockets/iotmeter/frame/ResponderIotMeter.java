package webapp.sockets.iotmeter.frame;

import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.cmd.DataFieldAnalysis;
import webapp.sockets.iotmeter.cmd.bean.MeterInfo;
import webapp.sockets.iotmeter.constant.IotMeterCommand;
import webapp.sockets.iotmeter.db.dao.DeviceAnomalyDao;
import webapp.sockets.iotmeter.db.dao.IotMeterInfoDao;
import webapp.sockets.iotmeter.db.dao.MeterDataDao;
import webapp.sockets.iotmeter.db.vo.DeviceAnomalyVo;
import webapp.sockets.iotmeter.db.vo.IotMeterInfoVo;
import webapp.sockets.iotmeter.db.vo.MeterDataVo;
import webapp.sockets.iotmeter.encode.RTHCDecoder;
import webapp.sockets.iotmeter.field.DataField;
import webapp.sockets.iotmeter.protocol.Protocol;
import webapp.sockets.iotmeter.util.TimeTag;
import webapp.sockets.iotmeter.util.Tools;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/12.
 */
public class ResponderIotMeter implements Serializable {
    private static Logger log = Logger.getLogger(Responder.class);
    //返回字节
    private byte[] returnByte;

    /**
     * 构造方法
     *
     * @param receiveCommand 字节数组
     */
    public ResponderIotMeter(byte[] receiveCommand, String ip, int port) {
        log.info("the responder get the command in BYTES");
        byte[] command = receiveCommand;
        commandHandler(command, ip, port);
    }

    /**
     * 构造方法，参数为字符串
     *
     * @param receiveCommand
     */
    public ResponderIotMeter(String receiveCommand, String ip, int port) {
        log.info("the responder get the command in STRING");
        byte[] command = Protocol.getInstance().hexStringToByte(receiveCommand);
        commandHandler(command, ip, port);
    }

    /**
     * 所有命令的处理
     *
     * @param command
     */
    private void commandHandler(byte[] command, String ip, int port) {
        log.info("commandHandler(byte[] command,String ip,int port)方法开始处理...");
        if (isResponseFrame(command)) {
            //如果为响应
            responseFrame(command);
        } else {
            //如果为请求
            requiredFrame(command, ip, port);
        }
    }

    /**
     * 判断是否为回复帧。回复帧有上行回复帧和下行回复帧；请求帧有下行请求帧和上行请求帧。这里的目标是找出上行请求帧。
     * 回复帧 response frame；
     * 请求帧 required frame
     *
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
        } else {
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
        } else {
            //上行回复
            upResponseFrame(command);
        }
        log.info(Protocol.getInstance().hexToHexString(command));
        // other operations
    }

    /**
     * 表返回数据
     *
     * @param command
     */
    private void upResponseFrame(byte[] command) {
        resultHashMap = new HashMap<>();//重新初始化resultHashMap
        log.info("upResponseFrame(byte[] command) 方法开始处理...");

        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] meterIdData = decoder.getSubStationIDByte(command);
        String meterId = Tools.Bytes2HexString(meterIdData,meterIdData.length);
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        byte[] backCode = decoder.getResponseFlagByte(command);
        DataField dataField = decoder.getDataField(command);
        byte[] data = dataField.getDataField();
        resultHashMap.put(KEY_BACK_CODE, Tools.Bytes2HexString(backCode, backCode.length));
        resultHashMap.put(KEY_CMD_ID, ccString);
        if (Arrays.equals(backCode, new byte[]{0x00, 0x00})) {
            resultHashMap.put(KEY_SUCCESS, true);
        } else {
            resultHashMap.put(KEY_SUCCESS, false);
        }
        switch (ccString) {
            case "3021":
                System.out.println(IotMeterCommand.cmd3021_name);
                break;
            case "3022":
                System.out.println(IotMeterCommand.cmd3022_name);
                break;
            case "3027":
                System.out.println(IotMeterCommand.cmd3027_name);
                break;
            case "3028":
                System.out.println(IotMeterCommand.cmd3028_name);
                break;
            case "3023":
                System.out.println(IotMeterCommand.cmd3023_name);
                break;
            case "3024":
                System.out.println(IotMeterCommand.cmd3024_name);
                break;
            case "3042":
                //主站主动抄表数据
                String dataStr = Protocol.getInstance().hexToHexString(data);
                MeterDataVo meterDataVo = this.opMeterData(dataStr, meterId);
                resultHashMap.put(KEY_METER_ID,meterDataVo.getMeterId());
                resultHashMap.put(KEY_METER_VALUE,meterDataVo.getFlow());
                resultHashMap.put(KEY_METER_VALVE_STATE,meterDataVo.getValveState());
                resultHashMap.put(KEY_DATA_TIME,meterDataVo.getDataTime());

                System.out.println(IotMeterCommand.cmd3042_name);
                break;
            default:
                break;
        }
    }

    public HashMap<String, Object> getResultHashMap() {
        return resultHashMap;
    }

    /**
     * 请求帧的处理，请求帧分为上行请求帧和下行请求帧。
     *
     * @param command
     */
    private void requiredFrame(byte[] command, String ip, int port) {
        log.info("requiredFrame(byte[] command,String ip,int port)方法开始处理...");
        if (isDownFrame(command)) {
            //如果是主站发送
            requestDownFrame(command);
        } else {
            //如果是从站发送
            requestUpFrame(command, ip, port);
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
        } else {
            return true;
        }
    }

    /**
     * 主站向从站发送指令
     *
     * @param command
     */
    private void requestDownFrame(byte[] command) {
        log.info("requestDownFrame(byte[] command)方法开始处理...");
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
     * 从站向主站发送 主站进行回应
     *
     * @param command
     */
    private void requestUpFrame(byte[] command, String ip, int port) {
        log.info("requestUpFrame(byte[] command,String ip,int port)方法开始处理...");
        log.info("==================从站向主站发送指令=========================");
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        switch (ccString) {
            case "3003":
                command3003Response(command, ip, port);
                break;
            case "3004":
                command3004Response(command, ip, port);
                break;
            case "3025":
                command3025Response(command);
                break;
            case "3062":
                command3062Response(command);
                break;
            case "3043":
                command3043Response(command);
                break;
            case "3046":
                command3046Response(command);
                break;
            default:
                log.info("something wrong, get unrecognized CONTROL CODE:" + ccString);
        }
    }

    /**
     * 3003从站向主站发送心跳包
     *
     * @param command
     */
    private void command3003Response(byte[] command, String ip, int port) {
        log.info("command3003Response(byte[] command,String ip,int port) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[42];

        int pos = 0;
        // 68
        returnByte[0] = 0x68;
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x2a, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3003
        byte[] ctrlCode = new byte[]{0x30, 0x03};
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

        //物联网表号
        System.arraycopy(devNoByte, 0, returnByte, pos, 7);
        pos += 7;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = simpleDateFormat.format(new Date());
        byte[] timeBytes = Tools.HexString2Bytes(dateStr);
        System.arraycopy(timeBytes, 0, returnByte, pos, 7);
        pos += 7;

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

    private void opIotMeterInfo(String meterId, String ip, int port) {
        IotMeterInfoVo vo = new IotMeterInfoVo();
        IotMeterInfoDao dao = new IotMeterInfoDao();
        int count = dao.queryIotMeterNo(meterId);
        if (count == 0) {
            vo.setId(Protocol.getInstance().getUUID());
            vo.setMeterId(meterId);
            vo.setClientIp(ip);
            vo.setClientPort(port);
            try {
                dao.saveIotMeterInfo(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            vo.setClientIp(ip);
            vo.setClientPort(port);
            vo.setMeterId(meterId);
            try {
                dao.updateIotMeterInfo(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 3004从站向主站建立连接后发送注册包，注册包每次登录时发送
     *
     * @param command
     */
    private void command3004Response(byte[] command, String ip, int port) {
        log.info("command3004Response(byte[] command,String ip,int port) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[35];

        int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x23, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3004
        byte[] ctrlCode = new byte[]{0x30, 0x04};
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
        byte[] add_data_field_length = new byte[]{0x09, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;

        //接收报文数据域
        byte[] meterIdBytes = new byte[7];
        System.arraycopy(receive, pos, meterIdBytes, 0, 7);
        String meterId = Protocol.getInstance().hexToHexString(meterIdBytes);

        //主站返回报文数据域
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;

        //meterId
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

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

        this.opIotMeterInfo(meterId, ip, port);

        this.returnByte = returnByte;

    }


    /**
     * 3025从站向主站发送注册包后，发送对时命令
     *
     * @param command
     */
    private void command3025Response(byte[] command) {
        log.info("command3004Response(byte[] command,String ip,int port) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[42];

        int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x2A, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3025
        byte[] ctrlCode = new byte[]{0x30, 0x25};
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
        byte[] add_data_field_length = new byte[]{0x10, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;

        //接收报文数据域
        byte[] meterIdBytes = new byte[7];
        System.arraycopy(receive, pos, meterIdBytes, 0, 7);
        String meterId = Protocol.getInstance().hexToHexString(meterIdBytes);

        //主站返回报文数据域
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;

        //meterId
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = simpleDateFormat.format(new Date());
        byte[] dateBytes = Tools.HexString2Bytes(dateStr);
        System.arraycopy(dateBytes,0,returnByte,pos,7);
        pos +=7;

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
     * 3043 物联网表下主动抄表 表主动上传数据
     *
     * @param command
     */
    private void command3043Response(byte[] command) {
        log.info("command3043Response(byte[] command) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[33];

        int pos = 0;
        // 68
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x21, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3043
        byte[] ctrlCode = new byte[]{0x30, 0x43};
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
        byte[] dataFieldLength = new byte[]{0x02, 0x00};
        System.arraycopy(dataFieldLength, 0, returnByte, pos, 2);
        int dataFieldLengthDataPos = pos;
        pos += 2;

        byte[] data = new byte[21];
        System.arraycopy(receive, pos, data, 0, 21);
        String dataStr = Protocol.getInstance().hexToHexString(data);
        this.opMeterData(dataStr, devNo);

        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;


        //表id
        System.arraycopy(devNoByte, 0, returnByte, pos, 7);
        pos += 7;

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
     * 从表读数数据包解析数据 并保存到数据库
     *
     * @param dataStr
     * @param devNo
     */
    private MeterDataVo opMeterData(String dataStr, String devNo) {
        //01 00 3c6300 320609 50020a 3c1c 3c1c
        //680029304600012001160700000116071700000001000f0000fe7c16
        MeterDataVo vo = new MeterDataVo();
        MeterDataDao dao = new MeterDataDao();

        String meterIdStr = dataStr.substring(0, 14);
        vo.setMeterId(meterIdStr);

        String dateStr = dataStr.substring(14, 28);
        SimpleDateFormat simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = simpleDateFormat.parse(dateStr);
            vo.setDataTime(TimeTag.getStringDate(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String flowF = dataStr.substring(28, 32);
        int flowF_int = Protocol.getInstance().hexToIntLowInHead(Protocol.getInstance().hexStringToByte(flowF));
        //System.out.println("temperatureF:" + temperatureF_int);

        String flowI = dataStr.substring(32, 40);
        int flowI_int = Protocol.getInstance().hexToIntLowInHead(Protocol.getInstance().hexStringToByte(flowI));
        //System.out.println("temperatureI:" + temperatureI_int);
        float flow = flowI_int + flowF_int / 1000.0f;
        vo.setFlow(flow);


        String valveStateStr = dataStr.substring(40, 42);
        int valveState = Protocol.getInstance().hexToIntLowInHead(Protocol.getInstance().hexStringToByte(valveStateStr));
        vo.setValveState(valveState);

        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveMeterData(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }


    /**
     * 3046 物联网表表具信息主动上报 包含表数据
     *
     * @param command
     */
    private void command3046Response(byte[] command) {
        log.info("command3046Response(byte[] command) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[35];

        int pos = 0;
        // 68
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x23, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3046
        byte[] ctrlCode = new byte[]{0x30, 0x46};
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
        byte[] dataFieldLengthBytes = new byte[]{0x09, 0x00};
        System.arraycopy(dataFieldLengthBytes, 0, returnByte, pos, 2);
        int receiveDataFieldLength = receive[pos]&0xff + (receive[pos+1]<<8)&0xff;
        int dataFieldLengthDataPos = pos;
        pos += 2;

        //接收报文数据域
        byte[] data = new byte[receiveDataFieldLength];
        System.arraycopy(receive, pos, data, 0, receiveDataFieldLength);
        this.opMeterUploadInfo(data);

        //返回报文数据域
        //响应码
        byte[] add_response_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_response_flag, 0, returnByte, pos, 2);
        pos += 2;


        System.arraycopy(devNoByte, 0, returnByte, pos, 7);
        pos += 7;

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
     * 从表信息数据包解析数据 并保存到数据库
     *
     * @param data
     */
    private void opMeterUploadInfo(byte[] data) {
        DataFieldAnalysis dataFieldAnalysis = DataFieldAnalysis.getInstantce();
        MeterInfo meterInfo = dataFieldAnalysis.getMeterInfoByBytes(data);

        MeterDataVo vo = new MeterDataVo();
        MeterDataDao dao = new MeterDataDao();
        if (meterInfo.dataDetails.size() <= 0) {
        }else {
            vo.setFlow(meterInfo.dataDetails.get(0).MeterValue);
            vo.setMeterId(meterInfo.meterId);
            int state = 0;
            switch (meterInfo.stateValve){
                case Close:
                    state = 0;
                    break;
                case Error:
                    state = 2;
                    break;
                case Open:
                    state = 1;
                    break;
            }
            vo.setValveState(state);
            vo.setDataTime(TimeTag.getStringDate(meterInfo.detailStartDate));
            vo.setId(Protocol.getInstance().getUUID());
            try {
                dao.saveMeterData(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 反转16进制的字符串 以实现高地位顺序倒序
     *
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
     * 3062从站向主站当设备出现异常时，主动上报当前异常信息
     *
     * @param command
     */
    private void command3062Response(byte[] command) {
        log.info("command3062Response(byte[] command) 方法开始处理...");
        byte[] receive = command;
        byte[] returnByte = new byte[28];
        int pos = 0;
        // 68
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x1C, 0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3042
        byte[] ctrlCode = new byte[]{0x30, 0x42};
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
        this.opDeviceAnomaly(dataStr, devNo);

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

    private void opDeviceAnomaly(String dataStr, String devNo) {
        DeviceAnomalyVo vo = new DeviceAnomalyVo();
        DeviceAnomalyDao dao = new DeviceAnomalyDao();

        String happenDate = dataStr.substring(0, 8);
        vo.setHappenDate(happenDate);
        //System.out.println("happenDate:" + happenDate);

        String happenTime = dataStr.substring(8, 14);
        vo.setHappenTime(happenTime);
        //System.out.println("happenTime:" + happenTime);

        String anomalyType = dataStr.substring(14, 18);
        vo.setNomalyType(anomalyType);
        //System.out.println("nomalyType:" + nomalyType);

        vo.setDevId(devNo);
        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveDeviceNomaly(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param command 功能码
     * @return
     */
    private byte[] command3021DownRequired(byte[] command) {
        log.info("command3021DownRequired(byte[] command) 方法开始处理...");
        byte[] receive = command;

        byte[] sendByte = new byte[28];

        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;

        //001C
        byte[] lengthOfFrame = new byte[]{0x00, 0x1C};
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

    private byte[] command3022DownRequired(byte[] command) {
        log.info("command3022DownRequired(byte[] command) 方法开始处理...");
        byte[] receive = command;
        byte[] sendByte = new byte[30];

        int pos = 0;
        // 68
        System.arraycopy(receive, pos, sendByte, pos, 1);
        pos += 1;

        //0023
        byte[] lengthOfFrame = new byte[]{0x00, 0x1E};
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
        byte[] xt = new byte[]{0x00, 0x3C};
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

    private byte[] command3027DownRequired(byte[] command) {
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

    private byte[] command3028DownRequired(byte[] command) {
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

    private byte[] command3023DownRequired(byte[] command) {
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

    private byte[] command3024DownRequired(byte[] command) {
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

    private byte[] command3080DownRequired(byte[] command) {
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

    private byte[] command3082DownRequired(byte[] command) {

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

    private byte[] command3085DownRequired(byte[] command) {
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

    private byte[] command3086DownRequired(byte[] command) {
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

    private byte[] command3087DownRequired(byte[] command) {
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

    private byte[] command3088DownRequired(byte[] command) {
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

    private byte[] command3089DownRequired(byte[] command) {
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

    private byte[] command3090DownRequired(byte[] command) {
        log.info("command3090DownRequired(byte[] command) 方法开始处理...");
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

    /**
     * 判断这对收发数据报文ID是否相同
     *
     * @param send
     * @param receive
     * @return
     */
    public boolean isPair(byte[] send, byte[] receive) {
        byte[] sendMsgId = new byte[7];
        System.arraycopy(send, 14, sendMsgId, 0, 7);
        byte[] receiveMsgId = new byte[7];
        System.arraycopy(receive, 14, receiveMsgId, 0, 7);
        return Arrays.equals(sendMsgId, receiveMsgId);
    }

    private HashMap<String, Object> resultHashMap = new HashMap<>();


    public static final String KEY_METER_ID = "KEY_METER_ID";
    public static final String KEY_NEW_METER_ID = "KEY_NEW_METER_ID";
    public static final String KEY_CLIENT_ID = "KEY_CLIENT_ID";
    public static final String KEY_CMD_ID = "KEY_CMD_ID";
    public static final String KEY_SEND_DIRECTION = "KEY_SEND_DIRECTION";
    public static final String KEY_REQUEST_FLAG = "KEY_REQUEST_FLAG";
    public static final String KEY_MESSAGE_ID = "KEY_MESSAGE_ID";
    public static final String KEY_BACK_CODE = "KEY_BACK_CODE";
    public static final String KEY_RESULT = "KEY_RESULT";
    public static final String KEY_SUCCESS = "KEY_SUCCESS";
    public static final String KEY_ERR_MESSAGE = "KEY_ERR_MESSAGE";
    public static final String KEY_DATA_TIME = "KEY_DATA_TIME";
    public static final String KEY_METER_VALUE = "KEY_METER_VALUE";
    public static final String KEY_METER_VALVE_STATE = "KEY_METER_VALVE_STATE";
    public static final String KEY_METER_EXCEPTION_TYPE = "KEY_METER_EXCEPTION_TYPE";


    public static final String KEY_HEARTBEAT_CYCLE = "KEY_HEARTBEAT_CYCLE";
    public static final String KEY_METER_TIME = "KEY_METER_TIME";
    public static final String KEY_SERVER_IP = "KEY_SERVER_IP";
    public static final String KEY_SERVER_PORT = "KEY_SERVER_PORT";
    public static final String KEY_UPLOAD_CYCLE_TYPE = "KEY_UPLOAD_CYCLE_TYPE";
    public static final String KEY_WAKEUP_TIME = "KEY_WAKEUP_TIME";
    public static final String KEY_WAKEUP_LONG = "KEY_WAKEUP_LONG";
    public static final String KEY_WAKEUP_DELAY = "KEY_WAKEUP_DELAY";

    public static final String KEY_LAST_FLAG = "KEY_LAST_FLAG";
    public static final String KEY_HIS_DATA_FRAME_SER = "KEY_HIS_DATA_FRAME_SER";
    public static final String KEY_HIS_DATA_FRAME_DATA_COUNT = "KEY_HIS_DATA_FRAME_DATA_COUNT";
    public static final String KEY_HIS_DATA_FRAME_DATAS = "KEY_HIS_DATA_FRAME_DATAS";

    public static final String KEY_METER_INFO = "KEY_METER_INFO";

}
