package webapp.sockets.concentrateor;

import webapp.sockets.BaseIotMessageHandler;
import webapp.sockets.concentrateor.field.CtrlCode;
import webapp.sockets.concentrateor.frame.IotFrame;
import webapp.sockets.concentrateor.frame.ResponderConcentrator;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.Tools;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/18.
 */
public class IotConcentratorMessageHandler extends BaseIotMessageHandler {


    public IotConcentratorMessageHandler(Socket socket) {
        super(socket);
    }

    public IotConcentratorMessageHandler(Socket socket, int timeout) {
        super(socket, timeout);
    }


    @Override
    public void messageHandler(byte[] message) {
        String messageStr = Protocol.getInstance().hexToHexString(message);
        ResponderConcentrator responder = new ResponderConcentrator(messageStr, socket.getInetAddress().getHostAddress(), socket.getPort());
        IotFrame iotFrame = new IotFrame(message);
        if (responder.isResponseFrame(message)) {
            //如果接收到的数据是回复帧 获取返回结果
            synchronized (lock) {
                if (iotFrame.getControlCode().getControlCodeString().equals(CtrlCode.CMD_READ_ALL_METER_DATA)) {

                    byte[] contentBytes = iotFrame.getDataField().getDataContentField().getDataContent();
                    if (contentBytes[5] == 0x01) {
                        //最后一帧
                        resultMap = responder.getResultHashMap();
                        lock.notify();
                    }

                } else if (iotFrame.getControlCode().getControlCodeString().equals(CtrlCode.CMD_READ_METER_HISTORY_MONTH)) {
                    byte[] contentBytes = iotFrame.getDataField().getDataContentField().getDataContent();
                    if (contentBytes[17] == 0x01) {
                        //最后一帧
                        resultMap = responder.getResultHashMap();
                        lock.notify();
                    }

                } else if (iotFrame.getControlCode().getControlCodeString().equals(CtrlCode.CMD_READ_METER_HISTORY_DAY)) {
                    byte[] contentBytes = iotFrame.getDataField().getDataContentField().getDataContent();
                    if (contentBytes[17] == 0x01) {
                        //最后一帧
                        resultMap = responder.getResultHashMap();
                        lock.notify();
                    }

                } else {
                    if (responder.isPair(syncsSendData, message)) {
                        resultMap = responder.getResultHashMap();
                        lock.notify();
                    }
                }
            }
        } else {
            //如果接收到的数据是请求帧 则解析得到回复帧 并回复
            byte[] responderFrame = responder.getReturnByte();
            try {
                sendMessage(responderFrame);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //如果请求帧是表注册 则添加表到在线表列表
            String device = iotFrame.getSsid().getSubStationIdStr();
            String cmdIdStr = iotFrame.getControlCode().getControlCodeString();
            if (cmdIdStr.equals(CtrlCode.CMD_REGISTER)) {
                IotConcentratorServer.addDeviceToOnlineList(device, socket, this);
            }
        }
    }
}
