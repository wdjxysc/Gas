package webapp.sockets.concentrateor.frame;

import com.sun.xml.internal.bind.v2.TODO;
import org.apache.log4j.Logger;
import webapp.sockets.concentrateor.constant.ConcentratorCommand;
import webapp.sockets.concentrateor.dao.*;
import webapp.sockets.concentrateor.dao.vo.ConcentratorExceptionVo;
import webapp.sockets.concentrateor.dao.vo.ConcentratorInfoVo;
import webapp.sockets.concentrateor.dao.vo.MeterDataVo;
import webapp.sockets.concentrateor.dao.vo.MeterExceptionVo;
import webapp.sockets.concentrateor.encode.*;
import webapp.sockets.concentrateor.field.*;
import webapp.sockets.concentrateor.field.datafield.DataContentField;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.TimeTag;
import webapp.sockets.util.Tools;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * 响应指令  主站->集中器
 * <p>
 * Created by Administrator on 2016/11/12.
 */
public class ResponderConcentrator implements Serializable {
    private static Logger log = Logger.getLogger(ResponderConcentrator.class);

    //返回字节
    private byte[] returnByte;

    /**
     * 构造方法
     *
     * @param receiveCommand 字节数组
     */
    public ResponderConcentrator(byte[] receiveCommand, String ip, int port) {
        log.info("the responder get the command in BYTES");
        byte[] command = receiveCommand;
        commandHandler(command, ip, port);
    }

    /**
     * 构造方法，参数为字符串
     *
     * @param receiveCommand
     */
    public ResponderConcentrator(String receiveCommand, String ip, int port) {
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
            //如果为响应 处理：存数据库等
            responseUpFrameHandler(command);
        } else {
            //如果为请求 处理：返回数据等
            requestUpFrameHandler(command, ip, port);
        }
    }

    /**
     * 判断是否为响应帧。
     *
     * @param command
     * @return
     */
    public boolean isResponseFrame(byte[] command) {
        log.info("isResponseFrame(byte[] command) 方法开始处理...");
        IotFrame iotFrame = new IotFrame(command);
        //获得响应标识
        byte responseCode = iotFrame.getDrf().getResponse();
        //01响应，00为请求
        if (responseCode == 0x01) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 集中器响应数据处理
     *
     * @param command
     */
    private void responseUpFrameHandler(byte[] command) {
        resultHashMap = new HashMap<>();//重新初始化resultHashMap
        log.info("upResponseFrame(byte[] command) 方法开始处理...");

        IotFrame iotFrame = new IotFrame(command);
        log.info(iotFrame.getControlCode().getControlCodeString() + " response code--" + iotFrame.getDataField().getResponseCode().getResponseCodeHexStr());
        String subStationId = iotFrame.getSsid().getSubStationIdStr();
        String ccString = iotFrame.getControlCode().getControlCodeString();
        String backCode = iotFrame.getDataField().getResponseCode().getResponseCodeHexStr();
        resultHashMap.put(KEY_BACK_CODE, backCode);
        resultHashMap.put(KEY_CMD_ID, ccString);
        resultHashMap.put(KEY_CLIENT_ID, subStationId);
        if (backCode.equals("0000")) {
            resultHashMap.put(KEY_SUCCESS, true);
        } else {
            resultHashMap.put(KEY_SUCCESS, false);
        }
        switch (ccString) {
            case "2001":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2001_name);
                break;
            case "2011":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2011_name);
                break;
            case "2012":
                cmd2012Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2012_name);
                break;
            case "2013":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2013_name);
                break;
            case "2014":
                cmd2014Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2014_name);
                break;
            case "2015":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2015_name);
                break;
            case "2016":
                cmd2016Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2016_name);
                break;
            case "2021":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2021_name);
                break;
            case "2022":
                cmd2022Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2022_name);
                break;
            case "2023":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2023_name);
                break;
            case "2024":
                cmd2024Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2024_name);
                break;
            case "2035":
                cmd2035Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2035_name);
                break;
            case "2036":
                cmd2036Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2036_name);
                break;
            case "2041":
                cmd2041Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2041_name);
                break;
            case "2042":
                cmd2042Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2042_name);
                break;
            case "2043":
                cmd2043Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2043_name);
                break;
            case "2044":
                cmd2044Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2044_name);
                break;
            case "2051":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2051_name);
                break;
            case "2052":
                cmdBaseResponse(iotFrame);
                System.out.println(ConcentratorCommand.cmd2052_name);
                break;
            case "2053":
                cmd2053Response(iotFrame);
                System.out.println(ConcentratorCommand.cmd2053_name);
                break;
            default:
                break;
        }
    }

    private void cmdBaseResponse(IotFrame iotFrame) {
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
    }

    private void cmd2012Response(IotFrame iotFrame) {
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, 0, concentratorIdBytes, 0, concentratorIdBytes.length);
        resultHashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));
        byte[] heartbeatCircleBytes = new byte[2];
        System.arraycopy(contentBytes, 5, heartbeatCircleBytes, 0, heartbeatCircleBytes.length);
        int heartbeatInt = (heartbeatCircleBytes[0] & 0xff) + ((heartbeatCircleBytes[1] & 0xff) << 8);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(KEY_HEARTBEAT_CYCLE, heartbeatInt);
        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    private void cmd2014Response(IotFrame iotFrame) {
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, 0, concentratorIdBytes, 0, concentratorIdBytes.length);
        resultHashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));
        byte[] groupReadTimeBytes = new byte[2];
        System.arraycopy(contentBytes, 5, groupReadTimeBytes, 0, groupReadTimeBytes.length);
        String groupReadTimeStr = Tools.Bytes2HexString(groupReadTimeBytes, groupReadTimeBytes.length);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(KEY_GROUP_READ_DATA_FLAG, groupReadTimeStr);
        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }


    private void cmd2016Response(IotFrame iotFrame) {
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, 0, concentratorIdBytes, 0, concentratorIdBytes.length);
        resultHashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));
        byte[] dateBytes = new byte[7];
        System.arraycopy(contentBytes, 5, dateBytes, 0, dateBytes.length);
        String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(KEY_DATE, TimeTag.getStringDate(date));
        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    private void cmd2022Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        int pos = 0;
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));
        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));
        pos += 7;

        byte[] circleTimeBytes = new byte[2];
        System.arraycopy(contentBytes, pos, circleTimeBytes, 0, circleTimeBytes.length);
        int circleTimeInt = (circleTimeBytes[0] & 0xff) + ((circleTimeBytes[1] & 0xff) << 8);
        hashMap.put(KEY_WAKEUP_CIRCLE, circleTimeInt);

        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }


    private void cmd2024Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));
        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));
        pos += 7;

        byte[] dateBytes = new byte[7];
        System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
        String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
            hashMap.put(KEY_DATE, TimeTag.getStringDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }


    private void cmd2035Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] countBytes = new byte[2];
        System.arraycopy(contentBytes, pos, countBytes, 0, countBytes.length);
        int count = (countBytes[0] & 0xff) + ((countBytes[1] & 0xff) << 8);
        hashMap.put(KEY_COUNT, count);
        pos += 2;

        ArrayList<String> collectors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte[] detailBytes = new byte[5];
            System.arraycopy(contentBytes, pos, detailBytes, 0, detailBytes.length);
            collectors.add(Tools.Bytes2HexString(detailBytes, detailBytes.length));
            pos += 5;
        }
        hashMap.put(KEY_DETAILS, collectors);


        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    private void cmd2036Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] countBytes = new byte[2];
        System.arraycopy(contentBytes, pos, countBytes, 0, countBytes.length);
        int count = (countBytes[0] & 0xff) + ((countBytes[1] & 0xff) << 8);
        hashMap.put(KEY_COUNT, count);
        pos += 2;

        ArrayList<String> meters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte[] detailBytes = new byte[7];
            System.arraycopy(contentBytes, pos, detailBytes, 0, detailBytes.length);
            meters.add(Tools.Bytes2HexString(detailBytes, detailBytes.length));
            pos += 7;
        }
        hashMap.put(KEY_DETAILS, meters);

        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    private void cmd2041Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] lastFlagBytes = new byte[1];
        System.arraycopy(contentBytes, pos, lastFlagBytes, 0, lastFlagBytes.length);
        pos += 1;
        hashMap.put(KEY_LAST_FLAG, Tools.Bytes2HexString(lastFlagBytes, lastFlagBytes.length));

        byte[] serBytes = new byte[2];
        System.arraycopy(contentBytes, pos, serBytes, 0, serBytes.length);
        pos += 2;
        hashMap.put(KEY_HIS_DATA_FRAME_SER, Tools.Bytes2HexString(serBytes, serBytes.length));


        byte[] countBytes = new byte[2];
        System.arraycopy(contentBytes, pos, countBytes, 0, countBytes.length);
        int count = (countBytes[0] & 0xff) + ((countBytes[1] & 0xff) << 8);
        hashMap.put(KEY_COUNT, count);
        pos += 2;

        ArrayList<HashMap<String,Object>> meterDatas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            HashMap<String,Object> dataDetail = new HashMap<>();

            byte[] meterIdBytes = new byte[7];
            System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
            String meterId = Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length);
            dataDetail.put(KEY_METER_ID, meterId);
            pos += 7;

            byte[] dateBytes = new byte[7];
            System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
            pos += 7;
            String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            try {
                date = simpleDateFormat.parse(dateStr);
                dataDetail.put(KEY_DATE, TimeTag.getStringDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            byte[] flowBytes = new byte[6];
            System.arraycopy(contentBytes,pos,flowBytes,0,flowBytes.length);
            pos+=6;
            float flow = MeterData.meterFlowBytesToFloat(flowBytes);
            dataDetail.put(KEY_METER_VALUE,flow);

            byte[] readDataStateBytes = new byte[1];
            System.arraycopy(contentBytes,pos,readDataStateBytes,0,readDataStateBytes.length);
            pos++;
            dataDetail.put(KEY_METER_READ_STATE,readDataStateBytes[0]);

            byte[] meterValveStateBytes = new byte[1];
            System.arraycopy(contentBytes,pos,meterValveStateBytes,0,meterValveStateBytes.length);
            pos++;
            dataDetail.put(KEY_METER_VALVE_STATE,meterValveStateBytes[0]);

            meterDatas.add(dataDetail);
            //存数据库
            opMeterData(meterId,flow,meterValveStateBytes[0]&0xff,TimeTag.getStringDate(date));
        }

        hashMap.put(KEY_DETAILS,meterDatas );


        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }


    private void cmd2042Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        String meterId =   Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, meterId);
        pos += 7;

        byte[] dateBytes = new byte[7];
        System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
        pos+=7;
        String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateStr);
            hashMap.put(KEY_DATE, TimeTag.getStringDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        byte[] flowBytes = new byte[6];
        System.arraycopy(contentBytes,pos,flowBytes,0,flowBytes.length);
        pos+=6;
        float flow = MeterData.meterFlowBytesToFloat(flowBytes);
        hashMap.put(KEY_METER_VALUE,flow);

        byte[] readDataStateBytes = new byte[1];
        System.arraycopy(contentBytes,pos,readDataStateBytes,0,readDataStateBytes.length);
        pos++;
        hashMap.put(KEY_METER_READ_STATE,readDataStateBytes[0]);

        byte[] meterValveStateBytes = new byte[1];
        System.arraycopy(contentBytes,pos,meterValveStateBytes,0,meterValveStateBytes.length);
        pos++;
        hashMap.put(KEY_METER_VALVE_STATE,meterValveStateBytes[0]);

        //存数据库
        opMeterData(meterId,flow,meterValveStateBytes[0]&0xff,TimeTag.getStringDate(date));

        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }


    private void cmd2043Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));
        pos += 7;

        byte[] lastFlagBytes = new byte[1];
        System.arraycopy(contentBytes, pos, lastFlagBytes, 0, lastFlagBytes.length);
        pos += 1;
        hashMap.put(KEY_LAST_FLAG, Tools.Bytes2HexString(lastFlagBytes, lastFlagBytes.length));

        byte[] serBytes = new byte[2];
        System.arraycopy(contentBytes, pos, serBytes, 0, serBytes.length);
        pos += 2;
        hashMap.put(KEY_HIS_DATA_FRAME_SER, Tools.Bytes2HexString(serBytes, serBytes.length));


        byte[] countBytes = new byte[2];
        System.arraycopy(contentBytes, pos, countBytes, 0, countBytes.length);
        int count = (countBytes[0] & 0xff) + ((countBytes[1] & 0xff) << 8);
        hashMap.put(KEY_COUNT, count);
        pos += 2;

        ArrayList<HashMap<String,Object>> meterDatas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            HashMap<String,Object> dataDetail = new HashMap<>();

            byte[] dateBytes = new byte[7];
            System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
            pos += 7;
            String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            try {
                date = simpleDateFormat.parse(dateStr);
                dataDetail.put(KEY_DATE, TimeTag.getStringDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            byte[] flowBytes = new byte[6];
            System.arraycopy(contentBytes,pos,flowBytes,0,flowBytes.length);
            pos+=6;
            float flow = MeterData.meterFlowBytesToFloat(flowBytes);
            dataDetail.put(KEY_METER_VALUE,flow);


            meterDatas.add(dataDetail);
            //存数据库
            opMeterHisData(Tools.Bytes2HexString(meterIdBytes,meterIdBytes.length),flow, TimeTag.getStringDate(date), MeterDataHisDao.HisType.Month);

        }

        hashMap.put(KEY_DETAILS, meterDatas );


        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    private void cmd2044Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));
        pos += 7;

        byte[] lastFlagBytes = new byte[1];
        System.arraycopy(contentBytes, pos, lastFlagBytes, 0, lastFlagBytes.length);
        pos += 1;
        hashMap.put(KEY_LAST_FLAG, Tools.Bytes2HexString(lastFlagBytes, lastFlagBytes.length));

        byte[] serBytes = new byte[2];
        System.arraycopy(contentBytes, pos, serBytes, 0, serBytes.length);
        pos += 2;
        hashMap.put(KEY_HIS_DATA_FRAME_SER, Tools.Bytes2HexString(serBytes, serBytes.length));


        byte[] countBytes = new byte[2];
        System.arraycopy(contentBytes, pos, countBytes, 0, countBytes.length);
        int count = (countBytes[0] & 0xff) + ((countBytes[1] & 0xff) << 8);
        hashMap.put(KEY_COUNT, count);
        pos += 2;

        ArrayList<HashMap<String,Object>> meterDatas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            HashMap<String,Object> dataDetail = new HashMap<>();

            byte[] dateBytes = new byte[7];
            System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
            pos += 7;
            String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            try {
                date = simpleDateFormat.parse(dateStr);
                dataDetail.put(KEY_DATE, TimeTag.getStringDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            byte[] flowBytes = new byte[6];
            System.arraycopy(contentBytes,pos,flowBytes,0,flowBytes.length);
            pos+=6;
            float flow = MeterData.meterFlowBytesToFloat(flowBytes);
            dataDetail.put(KEY_METER_VALUE,flow);


            meterDatas.add(dataDetail);
            //存数据库
            opMeterHisData(Tools.Bytes2HexString(meterIdBytes,meterIdBytes.length),flow, TimeTag.getStringDate(date), MeterDataHisDao.HisType.Day);
        }

        hashMap.put(KEY_DETAILS, meterDatas );


        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }

    //存历史用量
    private MeterDataVo opMeterHisData(String meterId, float flow, String dataDate, MeterDataHisDao.HisType type) {
        MeterDataVo vo = new MeterDataVo();
        MeterDataHisDao dao = new MeterDataHisDao();

        vo.setMeterId(meterId);
        vo.setFlow(flow);
        vo.setDataTime(dataDate);
        vo.setCreateDate(TimeTag.getStringDate());

        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveMeterData(vo, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    //存集抄数据 实时抄表数据
    private MeterDataVo opMeterData(String meterId, float flow, int valveState, String dataDate) {
        MeterDataVo vo = new MeterDataVo();
        MeterDataDao dao = new MeterDataDao();

        vo.setMeterId(meterId);
        vo.setFlow(flow);
        vo.setValveState(valveState);
        vo.setDataTime(dataDate);
        vo.setCreateDate(TimeTag.getStringDate());

        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveMeterData(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }


    private void cmd2053Response(IotFrame iotFrame) {
        HashMap<String, Object> hashMap = new HashMap<>();
        int pos = 0;
        DataContentField dataContentField = iotFrame.getDataField().getDataContentField();
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_CONCENTRATOR_ID, Tools.Bytes2HexString(concentratorIdBytes, concentratorIdBytes.length));

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;
        hashMap.put(KEY_COLLECTOR_ID, Tools.Bytes2HexString(collectorIdBytes, collectorIdBytes.length));

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        hashMap.put(KEY_METER_ID, Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));
        pos += 7;

        byte[] meterValveStateBytes = new byte[1];
        System.arraycopy(contentBytes,pos,meterValveStateBytes,0,meterValveStateBytes.length);
        pos++;
        hashMap.put(KEY_METER_VALVE_STATE,meterValveStateBytes[0]);


        resultHashMap.put(KEY_DATA_CONTENT, hashMap);
    }



    /**
     * 集中器请求帧处理  主站进行回复
     *
     * @param command
     */
    private void requestUpFrameHandler(byte[] command, String ip, int port) {
        log.info("requestUpFrame(byte[] command,String ip,int port)方法开始处理...");
        log.info("==================从站向主站发送指令=========================");
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        byte[] cc = decoder.getControlCodeByte(command);
        String ccString = Protocol.getInstance().hexToHexString(cc);
        switch (ccString) {
            case "2002":
                System.out.println(ConcentratorCommand.cmd2002_name);
                cmd2002Request(command);
                break;
            case "2003":
                System.out.println(ConcentratorCommand.cmd2003_name);
                cmd2003Request(command);
                break;
            case "2004":
                System.out.println(ConcentratorCommand.cmd2004_name);
                cmd2004Request(command,ip,port);
                break;
            case "2061":
                System.out.println(ConcentratorCommand.cmd2061_name);
                cmd2061Request(command);
                break;
            case "2062":
                System.out.println(ConcentratorCommand.cmd2062_name);
                cmd2062Request(command);
                break;
            default:
                log.info("something wrong, get unrecognized CONTROL CODE:" + ccString);
        }
    }


    private void cmd2002Request(byte[] command) {
        IotFrame receivedFrame = new IotFrame(command);
        DirectionResponseFlag responseFlag = new DirectionResponseFlag(new byte[]{0x00, 0x01});
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        DataContentField dataContentField = receivedFrame.getDataField().getDataContentField();
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame sendingFrame = new IotFrame(receivedFrame.getControlCode(), responseFlag, receivedFrame.getSsid(), receivedFrame.getMid(), dataField);
        returnByte = sendingFrame.dataFrame;
    }

    private void cmd2003Request(byte[] command) {
        IotFrame receivedFrame = new IotFrame(command);
        DirectionResponseFlag responseFlag = new DirectionResponseFlag(new byte[]{0x00, 0x01});
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        DataContentField dataContentField = receivedFrame.getDataField().getDataContentField();
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame sendingFrame = new IotFrame(receivedFrame.getControlCode(), responseFlag, receivedFrame.getSsid(), receivedFrame.getMid(), dataField);
        returnByte = sendingFrame.dataFrame;
    }

    private void cmd2004Request(byte[] command,String ip,int port) {
        IotFrame receivedFrame = new IotFrame(command);
        DirectionResponseFlag responseFlag = new DirectionResponseFlag(new byte[]{0x00, 0x01});
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        DataContentField dataContentField = receivedFrame.getDataField().getDataContentField();
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame sendingFrame = new IotFrame(receivedFrame.getControlCode(), responseFlag, receivedFrame.getSsid(), receivedFrame.getMid(), dataField);
        returnByte = sendingFrame.dataFrame;

        opConcentratorInfo(receivedFrame.getSsid().getSubStationIdStr().substring(4,14),ip,port);
    }

    private void opConcentratorInfo(String id, String ip, int port) {
        ConcentratorInfoVo vo = new ConcentratorInfoVo();
        ConcentratorInfoDao dao = new ConcentratorInfoDao();
        int count = dao.queryConcentratorInfoById(id);
        if (count == 0) {
            vo.setId(Protocol.getInstance().getUUID());
            vo.setConcentratorId(id);
            vo.setClientIp(ip);
            vo.setClientPort(port);
            try {
                dao.saveConcentratorInfo(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            vo.setClientIp(ip);
            vo.setClientPort(port);
            vo.setConcentratorId(id);
            try {
                dao.updateConcentratorInfo(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cmd2061Request(byte[] command) {
        IotFrame receivedFrame = new IotFrame(command);
        DirectionResponseFlag responseFlag = new DirectionResponseFlag(new byte[]{0x00, 0x01});
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        byte[] receivedContentBytes = receivedFrame.getDataField().getDataContentField().getDataContent();
        byte[] backContentBytes = new byte[10];
        System.arraycopy(receivedContentBytes, 0, backContentBytes, 0, backContentBytes.length);
        DataContentField dataContentField = new DataContentField(backContentBytes);
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame sendingFrame = new IotFrame(receivedFrame.getControlCode(), responseFlag, receivedFrame.getSsid(), receivedFrame.getMid(), dataField);
        returnByte = sendingFrame.dataFrame;

        int pos = 0;
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;
        String concentratorId = Tools.Bytes2HexString(concentratorIdBytes,concentratorIdBytes.length);

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;

        byte[] dateBytes = new byte[7];
        System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
        pos += 7;
        String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        byte[] exceptionBytes = new byte[4];
        System.arraycopy(contentBytes, pos, exceptionBytes, 0, exceptionBytes.length);
        pos+=4;
        String exceptionStr = Tools.Bytes2HexString(exceptionBytes,exceptionBytes.length);

        //保存数据库
        opConcentratorException(concentratorId,exceptionStr,TimeTag.getStringDate(date));

    }

    private void cmd2062Request(byte[] command) {
        IotFrame receivedFrame = new IotFrame(command);
        DirectionResponseFlag responseFlag = new DirectionResponseFlag(new byte[]{0x00, 0x01});
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        byte[] receivedContentBytes = receivedFrame.getDataField().getDataContentField().getDataContent();
        byte[] backContentBytes = new byte[17];
        System.arraycopy(receivedContentBytes, 0, backContentBytes, 0, backContentBytes.length);
        DataContentField dataContentField = new DataContentField(backContentBytes);
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame sendingFrame = new IotFrame(receivedFrame.getControlCode(), responseFlag, receivedFrame.getSsid(), receivedFrame.getMid(), dataField);
        returnByte = sendingFrame.dataFrame;


        int pos = 0;
        byte[] contentBytes = dataContentField.getDataContent();
        byte[] concentratorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, concentratorIdBytes, 0, concentratorIdBytes.length);
        pos += 5;

        byte[] collectorIdBytes = new byte[5];
        System.arraycopy(contentBytes, pos, collectorIdBytes, 0, collectorIdBytes.length);
        pos += 5;

        byte[] meterIdBytes = new byte[7];
        System.arraycopy(contentBytes, pos, meterIdBytes, 0, meterIdBytes.length);
        pos += 7;
        String meterId = Tools.Bytes2HexString(meterIdBytes,meterIdBytes.length);

        byte[] dateBytes = new byte[7];
        System.arraycopy(contentBytes, pos, dateBytes, 0, dateBytes.length);
        pos += 7;
        String dateStr = Tools.Bytes2HexString(dateBytes, dateBytes.length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        byte[] exceptionBytes = new byte[4];
        System.arraycopy(contentBytes, pos, exceptionBytes, 0, exceptionBytes.length);
        pos+=4;
        String exceptionStr = Tools.Bytes2HexString(exceptionBytes,exceptionBytes.length);

        //保存数据库
        opMeterException(meterId,exceptionStr,TimeTag.getStringDate(date));
    }

    //存表异常数据
    private MeterExceptionVo opMeterException(String meterId, String exceptionId, String dataDate) {
        MeterExceptionVo vo = new MeterExceptionVo();
        MeterExceptionDao dao = new MeterExceptionDao();

        vo.setMeterId(meterId);
        vo.setExceptionId(exceptionId);
        vo.setDataDate(dataDate);
        vo.setCreateDate(TimeTag.getStringDate());

        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveMeterException(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }
    //存集中器异常数据
    private ConcentratorExceptionVo opConcentratorException(String concentratorId, String exceptionId, String dataDate) {
        ConcentratorExceptionVo vo = new ConcentratorExceptionVo();
        ConcentratorExceptionDao dao = new ConcentratorExceptionDao();

        vo.setCollectorId(concentratorId);
        vo.setExceptionId(exceptionId);
        vo.setDataDate(dataDate);
        vo.setCreateDate(TimeTag.getStringDate());

        vo.setId(Protocol.getInstance().getUUID());
        try {
            dao.saveConcentratorException(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
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

    public HashMap<String, Object> getResultHashMap() {
        return resultHashMap;
    }

    public byte[] getReturnByte() {
        return returnByte;
    }


    public static final String KEY_METER_ID = "KEY_METER_ID";
    public static final String KEY_NEW_METER_ID = "KEY_NEW_METER_ID";
    public static final String KEY_CLIENT_ID = "KEY_CLIENT_ID";
    public static final String KEY_CONCENTRATOR_ID = "KEY_CONCENTRATOR_ID";
    public static final String KEY_COLLECTOR_ID = "KEY_COLLECTOR_ID";
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
    public static final String KEY_METER_READ_STATE = "KEY_METER_READ_STATE";
    public static final String KEY_METER_VALVE_STATE = "KEY_METER_VALVE_STATE";
    public static final String KEY_METER_EXCEPTION_TYPE = "KEY_METER_EXCEPTION_TYPE";

    public static final String KEY_DATE = "KEY_DATE";
    public static final String KEY_DATA_CONTENT = "KEY_DATA_CONTENT";
    public static final String KEY_GROUP_READ_DATA_FLAG = "KEY_GROUP_READ_DATA_FLAG";
    public static final String KEY_HEARTBEAT_CYCLE = "KEY_HEARTBEAT_CYCLE";
    public static final String KEY_METER_TIME = "KEY_METER_TIME";
    public static final String KEY_SERVER_IP = "KEY_SERVER_IP";
    public static final String KEY_SERVER_PORT = "KEY_SERVER_PORT";
    public static final String KEY_UPLOAD_CYCLE_TYPE = "KEY_UPLOAD_CYCLE_TYPE";
    public static final String KEY_WAKEUP_TIME = "KEY_WAKEUP_TIME";
    public static final String KEY_WAKEUP_LONG = "KEY_WAKEUP_LONG";
    public static final String KEY_WAKEUP_DELAY = "KEY_WAKEUP_DELAY";
    public static final String KEY_WAKEUP_CIRCLE = "KEY_WAKEUP_CIRCLE";
    public static final String KEY_COUNT = "KEY_COUNT";
    public static final String KEY_DETAILS = "KEY_DETAILS";

    public static final String KEY_LAST_FLAG = "KEY_LAST_FLAG";
    public static final String KEY_HIS_DATA_FRAME_SER = "KEY_HIS_DATA_FRAME_SER";
    public static final String KEY_HIS_DATA_FRAME_DATA_COUNT = "KEY_HIS_DATA_FRAME_DATA_COUNT";
    public static final String KEY_HIS_DATA_FRAME_DATAS = "KEY_HIS_DATA_FRAME_DATAS";

    public static final String KEY_METER_INFO = "KEY_METER_INFO";

}
