package webapp.sockets.iotmeter.frame;

import webapp.sockets.iotmeter.IotMeterMessageHandler;
import webapp.sockets.iotmeter.protocol.Protocol;
import webapp.sockets.iotmeter.util.Tools;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

import static webapp.sockets.iotmeter.IotMeterServer.getSocketByMeterId;
import static webapp.sockets.iotmeter.frame.ResponderIotMeter.reversalHexString;

/**
 * Created by Administrator on 2017/2/20.
 */
public class RequestIotMeter implements Serializable {
    private static RequestIotMeter requestIotMeter;

    private RequestIotMeter(){

    }

    public static RequestIotMeter getInstance(){
        if(requestIotMeter == null){
            requestIotMeter = new RequestIotMeter();
        }
        return requestIotMeter;
    }

    /**
     * 抄表
     * @param meterId
     * @return
     */
    public HashMap readMeter(String meterId) {
        byte[] sendData = getReadMeterDataCmd(meterId);

        return operationMeter(meterId, sendData);
    }

    private HashMap<String,Object> operationMeter(String meterId, byte[] sendData){
        HashMap<String, Object> map = new HashMap<String, Object>();

        Socket socket = getSocketByMeterId(meterId);
        if(socket != null) {
            try {
                map = IotMeterMessageHandler.syncsSendMessage(socket, sendData);

                if(map == null){
                    map = new HashMap<>();
                    map.put(ResponderIotMeter.KEY_SUCCESS,false);
                    map.put(ResponderIotMeter.KEY_ERR_MESSAGE, "超时");
                }
                else if(map.size() == 0){
                    map.put(ResponderIotMeter.KEY_SUCCESS,false);
                    map.put(ResponderIotMeter.KEY_ERR_MESSAGE, "数据错误");
                }
            } catch (IOException e) {
                map.put(ResponderIotMeter.KEY_SUCCESS,false);
                map.put(ResponderIotMeter.KEY_ERR_MESSAGE, e.getMessage());
                e.printStackTrace();
            }
        }else {
            map.put(ResponderIotMeter.KEY_SUCCESS,false);
            map.put(ResponderIotMeter.KEY_ERR_MESSAGE, "气表离线");
        }

        return map;
    }

    /**
     * 开阀
     * @param meterId
     * @return
     */
    public HashMap openMeterValve(String meterId) {
        byte[] sendData = getOpenMeterValveDataCmd(meterId);

        return operationMeter(meterId, sendData);
    }

    /**
     * 关阀
     * @param meterId
     * @return
     */
    public HashMap closeMeterValve(String meterId) {
        byte[] sendData = getCloseMeterValveDataCmd(meterId);

        return operationMeter(meterId, sendData);
    }



    private byte[] getReadMeterDataCmd(String meterId){
        byte[] returnByte = new byte[33];

        int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x21,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3042
        byte[] ctrlCode = new byte[]{0x30,0x42};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;

        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;

        //从站编号
        byte[] meterIdBytes = Tools.HexString2Bytes(meterId);
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //报文ID
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //数据域长度
        byte[] add_data_field_length = new byte[]{0x07, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;

        //物联网表号
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;

        returnByte[returnByte.length - 1] = 0x16;

        return returnByte;
    }


    private byte[] getOpenMeterValveDataCmd(String meterId){
        byte[] returnByte = new byte[33];

        int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x21,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3051
        byte[] ctrlCode = new byte[]{0x30,0x51};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;

        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;

        //从站编号
        byte[] meterIdBytes = Tools.HexString2Bytes(meterId);
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //报文ID
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //数据域长度
        byte[] add_data_field_length = new byte[]{0x07, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;

        //物联网表号
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;

        returnByte[returnByte.length - 1] = 0x16;

        return returnByte;
    }

    private byte[] getCloseMeterValveDataCmd(String meterId){
        byte[] returnByte = new byte[33];

        int pos = 0;
        // 68
        //System.arraycopy(receive, pos, returnByte, pos, 1);
        returnByte[0] = 0x68;
        pos += 1;

        //报文长度
        byte[] lengthOfFrame = new byte[]{0x21,0x00};
        System.arraycopy(lengthOfFrame, 0, returnByte, pos, 2);
        pos += 2;

        //功能- 3052
        byte[] ctrlCode = new byte[]{0x30,0x52};
        System.arraycopy(ctrlCode, 0, returnByte, pos, 2);
        pos += 2;

        //传送方向和请求响应标志
        byte[] add_direction_required_flag = new byte[]{0x00, 0x00};
        System.arraycopy(add_direction_required_flag, 0, returnByte, pos, 2);
        pos += 2;

        //从站编号
        byte[] meterIdBytes = Tools.HexString2Bytes(meterId);
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //报文ID
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        //数据域长度
        byte[] add_data_field_length = new byte[]{0x07, 0x00};
        System.arraycopy(add_data_field_length, 0, returnByte, pos, 2);
        pos += 2;

        //物联网表号
        System.arraycopy(meterIdBytes, 0, returnByte, pos, 7);
        pos += 7;

        int crc_int = Protocol.getInstance().calcCrc16(returnByte, 0, pos);
        String crc_string = reversalHexString(String.format("%04X", crc_int));
        byte[] add_crc_code = Protocol.getInstance().hexStringToByte(crc_string);
        System.arraycopy(add_crc_code, 0, returnByte, pos, 2);
        pos += 2;

        returnByte[returnByte.length - 1] = 0x16;

        return returnByte;
    }


//    public static void main(String[] args){
//        RequestIotMeter requestIotMeter = new RequestIotMeter();
//        requestIotMeter.readMeter("20011607000001");
//    }
}
