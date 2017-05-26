package webapp.sockets.concentrateor.frame;

import webapp.sockets.concentrateor.IotConcentratorMessageHandler;
import webapp.sockets.concentrateor.IotConcentratorServer;
import webapp.sockets.concentrateor.dao.ConcentratorCollectorMeterMapDao;
import webapp.sockets.concentrateor.dao.vo.ConcentratorCollectorMeterMapVo;
import webapp.sockets.concentrateor.field.*;
import webapp.sockets.concentrateor.field.datafield.ContentHeartbeat;
import webapp.sockets.concentrateor.field.datafield.DataContentField;
import webapp.sockets.util.Tools;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * 请求指令  主站->集中器
 * <p>
 * Created by Administrator on 2017/2/20.
 */
public class RequesterConcentrator implements Serializable {
    private static RequesterConcentrator requesterConcentrator;

    /**
     * 直接对rf表操作耗时较长 超时时间设为30s
     */
    private int requestMeterTimeout = 30000;

    private RequesterConcentrator() {

    }

    public static RequesterConcentrator getInstance() {
        if (requesterConcentrator == null) {
            requesterConcentrator = new RequesterConcentrator();
        }
        return requesterConcentrator;
    }


    private HashMap<String, Object> operation(String deviceId, byte[] sendData) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        IotConcentratorMessageHandler handler = (IotConcentratorMessageHandler) IotConcentratorServer.getMessageHandlerByDeviceId(deviceId);
        Socket socket = handler.socket;
        if (socket != null) {
            try {
                map = handler.syncsSendMessage(sendData);

                if (map == null) {
                    map = new HashMap<>();
                    map.put(ResponderConcentrator.KEY_SUCCESS, false);
                    map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "超时");
                } else if (map.size() == 0) {
                    map.put(ResponderConcentrator.KEY_SUCCESS, false);
                    map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "数据错误");
                }
            } catch (IOException e) {
                map.put(ResponderConcentrator.KEY_SUCCESS, false);
                map.put(ResponderConcentrator.KEY_ERR_MESSAGE, e.getMessage());
                e.printStackTrace();
            }
        } else {
            map.put(ResponderConcentrator.KEY_SUCCESS, false);
            map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "气表离线");
        }

        return map;
    }


    private HashMap<String, Object> operation(String deviceId, byte[] sendData, int timeout) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        IotConcentratorMessageHandler handler = (IotConcentratorMessageHandler) IotConcentratorServer.getMessageHandlerByDeviceId(deviceId);
        if (handler!= null && handler.socket != null) {
            try {
                map = handler.syncsSendMessage(sendData, timeout);

                if (map == null) {
                    map = new HashMap<>();
                    map.put(ResponderConcentrator.KEY_SUCCESS, false);
                    map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "超时");
                } else if (map.size() == 0) {
                    map.put(ResponderConcentrator.KEY_SUCCESS, false);
                    map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "数据错误");
                }
            } catch (IOException e) {
                map.put(ResponderConcentrator.KEY_SUCCESS, false);
                map.put(ResponderConcentrator.KEY_ERR_MESSAGE, e.getMessage());
                e.printStackTrace();
            }
        } else {
            map.put(ResponderConcentrator.KEY_SUCCESS, false);
            map.put(ResponderConcentrator.KEY_ERR_MESSAGE, "集中器离线");
        }

        return map;
    }

    /**
     * 2001 主站通信测试
     * @param concentratorId
     * @return
     */
    public HashMap testConnect2001(String concentratorId){
        byte[] sendData = getConnectTestCmd(concentratorId);

        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2011 集中器心跳周期设置
     * @param concentratorId
     * @param heartbeat
     * @return
     */
    public HashMap setConcentratorHeartbeat(String concentratorId,int heartbeat){
        byte[] sendData = getSetConcentratorHeartbeatCmd(concentratorId, heartbeat);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2012 集中器心跳周期查询
     * @param concentratorId
     * @return
     */
    public HashMap readConcentratorHeartbeat(String concentratorId){
        byte[] sendData = getReadConcentratorHeartbeatCmd(concentratorId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2013 集中器集抄时间设置
     * @param concentratorId
     * @param groupReadFlag
     * @return
     */
    public HashMap setConcentratorGroupReadTime(String concentratorId, GroupReadFlag groupReadFlag){
        byte[] sendData = getSetConcentratorGroupReadCmd(concentratorId, groupReadFlag);
        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }

    /**
     * 2014 集中器集抄时间查询
     * @param concentratorId
     * @return
     */
    public HashMap readConcentratorGroupReadTime(String concentratorId){
        byte[] sendData = getReadConcentratorGroupReadTimeCmd(concentratorId);
        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }

    /**
     * 2015 集中器时钟设置
     * @param concentratorId
     * @param date
     * @return
     */
    public HashMap setConcentratorTime(String concentratorId, Date date){
        byte[] sendData = getSetConcentratorTimeCmd(concentratorId,date);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2016 集中器时钟查询
     * @param concentratorId
     * @return
     */
    public HashMap readConcentratorTime(String concentratorId){
        byte[] sendData = getReadConcentratorGroupReadTimeCmd(concentratorId);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2021 表具唤醒周期设置
     * @param meterId
     * @return
     */
    public HashMap setMeterAwakeCircleTime(String meterId, int time){
        byte[] sendData = getSetMeterAwakeTimeCmd(meterId,time);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2022 表具唤醒周期设置
     * @param meterId
     * @return
     */
    public HashMap readMeterAwakeCircleTime(String meterId, int time){
        byte[] sendData = getSetMeterAwakeTimeCmd(meterId,time);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }


    /**
     * 2023 表具时钟设置
     * @param meterId
     * @param date
     * @return
     */
    public HashMap setMeterTime(String meterId, Date date){
        byte[] sendData = getSetMeterTimeCmd(meterId,date);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    /**
     * 2024 表具时钟查询
     * @param meterId
     * @return
     */
    public HashMap readMeterTime(String meterId){
        byte[] sendData = getReadConcentratorGroupReadTimeCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return  operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap addSubCollector(String concentratorId, String collector){
        byte[] sendData = getAddCollectorCmd(concentratorId,collector);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap removeSubCollector(String concentratorId, String collector){
        byte[] sendData = getRemoveCollectorCmd(concentratorId,collector);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap addSubMeter(String concentratorId, String meterId){
        byte[] sendData = getAddCollectorCmd(concentratorId,meterId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap removeSubMeter(String concentratorId, String meterId){
        byte[] sendData = getRemoveCollectorCmd(concentratorId,meterId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap readSubCollector(String concentratorId){
        byte[] sendData = getQueryCollectorCmd(concentratorId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap readSubMeter(String concentratorId,String collectorId){
        byte[] sendData = getQueryMeterCmd(concentratorId,collectorId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap readGroupReadData(String concentratorId){
        byte[] sendData = getReadConcentratorGroupReadTimeCmd(concentratorId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap readMeterHisMonthData(String meterId){
        byte[] sendData = getReadMeterHistoryMonthCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }

    public HashMap readMeterHisDayData(String meterId){
        byte[] sendData = getReadMeterHistoryDayCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return operation("0000" + concentratorId,sendData,requestMeterTimeout);
    }


    /**
     * 单表抄表
     *
     * @param meterId
     * @return
     */
    public HashMap readMeter(String meterId) {
        byte[] sendData = getReadMeterDataCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);

        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }

    /**
     * 单表开阀
     *
     * @param meterId
     * @return
     */
    public HashMap openMeterValve(String meterId) {
        byte[] sendData = getOpenMeterValveCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);

        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }

    /**
     * 单表关阀
     *
     * @param meterId
     * @return
     */
    public HashMap closeMeterValve(String meterId) {
        byte[] sendData = getCloseMeterValveCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);

        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }


    public HashMap readMeterValveState(String meterId){
        byte[] sendData = getMeterValveStateCmd(meterId);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        return operation("0000" + concentratorId, sendData, requestMeterTimeout);
    }

    /**
     * 2001
     *
     * @param concentratorId
     * @return
     */
    private byte[] getConnectTestCmd(String concentratorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_CENTER_CONNECT_TEST);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2011
     *
     * @param concentratorId 集中器id
     * @param time           心跳周期
     * @return
     */
    private byte[] getSetConcentratorHeartbeatCmd(String concentratorId, int time) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_SET_CONCENTRATOR_HEARTBEAT_CIRCLE);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        ContentHeartbeat.Heartbeat heartbeat = new ContentHeartbeat.Heartbeat(time);
        ContentHeartbeat contentHeartbeat = new ContentHeartbeat(new ConcentratorID(concentratorId), heartbeat);
        DataField dataField = new DataField(null, contentHeartbeat);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2012
     *
     * @param concentratorId 集中器id
     * @return
     */
    private byte[] getReadConcentratorHeartbeatCmd(String concentratorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_CONCENTRATOR_HEARTBEAT_CIRCLE);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2013
     *
     * @param concentratorId 集中器id
     * @param flag           集抄时间
     * @return
     */
    private byte[] getSetConcentratorGroupReadCmd(String concentratorId, GroupReadFlag flag) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_SET_CONCENTRATOR_GROUP_READ_TIME);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        ResponseCode responseCode = new ResponseCode(ResponseCode.SUCCESS);
        String flagStr = GROUP_READ_TIME_FLAG_H;
        switch (flag) {
            case Hour:
                flagStr = GROUP_READ_TIME_FLAG_H;
                break;
            case Day:
                flagStr = GROUP_READ_TIME_FLAG_D;
                break;
            case Month:
                flagStr = GROUP_READ_TIME_FLAG_M;
                break;
        }
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + flagStr));
        DataField dataField = new DataField(responseCode, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2012
     *
     * @param concentratorId 集中器id
     * @return
     */
    private byte[] getReadConcentratorGroupReadTimeCmd(String concentratorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_CONCENTRATOR_GROUP_READ_TIME);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 集抄时间
     */
    public static final String GROUP_READ_TIME_FLAG_H = "0011";//每小时抄表
    public static final String GROUP_READ_TIME_FLAG_D = "0021";//每自然天抄表
    public static final String GROUP_READ_TIME_FLAG_M = "0031";//每自然月抄表

    public static enum GroupReadFlag {
        Hour,
        Day,
        Month
    }


    /**
     * 2015
     * 设置集中器时钟
     * @param concentratorId 集中器id
     * @param date           时间
     * @return
     */
    private byte[] getSetConcentratorTimeCmd(String concentratorId, Date date) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_SET_CONCENTRATOR_TIME);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + Tools.getDateBcdStr(date)));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2021
     *
     * @param meterId 表id
     * @param time    唤醒周期 单位秒，两字节整数
     * @return
     */
    private byte[] getSetMeterAwakeTimeCmd(String meterId, int time) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_SET_METER_AWAKE_TIME);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        String collectorId = getCollectorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        byte[] timeBytes = new byte[2];
        timeBytes[0] = (byte) (time % 0x100);
        timeBytes[1] = (byte) (time / 0x100);
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId + Tools.Bytes2HexString(timeBytes, timeBytes.length)));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2023
     * 表具时钟设置
     *
     * @param meterId 表id
     * @param date    时间
     * @return
     */
    private byte[] getSetMeterTimeCmd(String meterId, Date date) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_SET_METER_TIME);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        String collectorId = getCollectorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId + Tools.getDateBcdStr(date)));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2031
     * 集中器增加下级采集器
     *
     * @param concentratorId 集中器id
     * @param collectorId    采集器id
     * @return
     */
    private byte[] getAddCollectorCmd(String concentratorId, String collectorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_ADD_COLLECTOR);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2032
     * 集中器删除下级采集器
     *
     * @param concentratorId 集中器id
     * @param collectorId    采集器id
     * @return
     */
    private byte[] getRemoveCollectorCmd(String concentratorId, String collectorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_REMOVE_COLLECTOR);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2033
     * 增加下级智能表
     *
     * @param concentratorId 集中器id
     * @param collectorId    采集器id 此项全为0则在集中器下增加智能表 否则在采集器下增加智能表
     * @param meterId        表id
     * @return
     */
    private byte[] getAddMeterCmd(String concentratorId, String collectorId, String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_ADD_METER);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2034
     * 集中器删除下级采集器
     *
     * @param concentratorId 集中器id
     * @param collectorId    采集器id
     * @return
     */
    private byte[] getRemoveMeterCmd(String concentratorId, String collectorId, String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_REMOVE_METER);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2035
     * 集中器下级采集器查询
     *
     * @param concentratorId 集中器id
     * @return
     */
    private byte[] getQueryCollectorCmd(String concentratorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_QUERY_COLLECTORS);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2036
     * 下级表具查询
     *
     * @param concentratorId 集中器id
     * @param collectorId    采集器id  全0：是在集中器下查询表具 非全0：是在采集器下查询表具
     * @return
     */
    private byte[] getQueryMeterCmd(String concentratorId, String collectorId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_QUERY_METERS);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2041
     * 获取集抄数据
     *
     * @param concentratorId 集中器id
     * @param date           集抄时间
     * @return
     */
    private byte[] getRemoveMeterCmd(String concentratorId, Date date) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_ALL_METER_DATA);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + Tools.getDateBcdStr(date)));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2042
     * 单表实时抄表
     * @param meterId
     * @return
     */
    private byte[] getReadMeterDataCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_METER_DATA);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2043
     * 单表月用量历史查询
     * @param meterId
     * @return
     */
    private byte[] getReadMeterHistoryMonthCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_METER_HISTORY_MONTH);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2044
     * 单表日用量历史查询
     * @param meterId
     * @return
     */
    private byte[] getReadMeterHistoryDayCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_METER_HISTORY_DAY);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 2051
     *
     * @param meterId
     * @return
     */
    private byte[] getOpenMeterValveCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_OPEN_METER_VALVE);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2052
     *
     * @param meterId
     * @return
     */
    private byte[] getCloseMeterValveCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_CLOSE_METER_VALVE);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }

    /**
     * 2053
     *
     * @param meterId
     * @return
     */
    private byte[] getMeterValveStateCmd(String meterId) {
        CtrlCode ctrlCode = new CtrlCode(CtrlCode.CMD_READ_METER_VALVE_STATE);
        DirectionResponseFlag directionResponseFlag = new DirectionResponseFlag((byte) 0x00, (byte) 0x00);
        String concentratorId = getConcentratorIdByMeterId(meterId);
        SubStationID subStationID = new SubStationID("0000" + concentratorId);
        String collectorId = getCollectorIdByMeterId(meterId);
        MessageID messageID = MessageID.createMessageID();
        DataContentField dataContentField = new DataContentField(Tools.HexString2Bytes(concentratorId + collectorId + meterId));
        DataField dataField = new DataField(null, dataContentField);
        IotFrame iotFrame = new IotFrame(ctrlCode, directionResponseFlag, subStationID, messageID, dataField);
        return iotFrame.dataFrame;
    }


    /**
     * 根据表ID从数据库中查找对应的上级集中器ID
     *
     * @param meterId
     * @return
     */
    private String getConcentratorIdByMeterId(String meterId) {
        String concentratorId = "0000000000";
        //数据库查询 燃气表对应的集中器ID
        ConcentratorCollectorMeterMapDao dao = new ConcentratorCollectorMeterMapDao();

        ArrayList<ConcentratorCollectorMeterMapVo> vos = dao.queryMapByMeterId(meterId);
        if(vos.size() >0){
            concentratorId = vos.get(0).getConcentratorId();
        }

        return concentratorId;
    }

    /**
     * 根据表ID从数据库中查找对应的上级采集器ID
     *
     * @param meterId
     * @return
     */
    private String getCollectorIdByMeterId(String meterId) {
        String collectorId = "0000000000";
        //数据库查询 燃气表对应的集中器ID
        ConcentratorCollectorMeterMapDao dao = new ConcentratorCollectorMeterMapDao();

        ArrayList<ConcentratorCollectorMeterMapVo> vos = dao.queryMapByMeterId(meterId);
        if(vos.size() >0){
            collectorId = vos.get(0).getConcentratorId();
        }

        return collectorId;
    }


    public static void main(String[] args) {
        RequesterConcentrator requesterConcentrator = new RequesterConcentrator();
        requesterConcentrator.readMeter("20011607000001");
        requesterConcentrator.testConnect2001("2301000001");
    }
}
