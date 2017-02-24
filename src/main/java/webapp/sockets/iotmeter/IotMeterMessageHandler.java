package webapp.sockets.iotmeter;

import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.frame.ReceivedFrame;
import webapp.sockets.iotmeter.frame.ResponderIotMeter;
import webapp.sockets.iotmeter.protocol.Protocol;
import webapp.sockets.iotmeter.util.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import static webapp.sockets.iotmeter.IotMeterServer.addMeterToOnlineList;

public class IotMeterMessageHandler implements Runnable{
    private static Logger log = Logger.getLogger(Handler.class);
    private Socket socket;

    private Date lastReceiveDataTime = new Date();

    public IotMeterMessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try{

            log.info("新连接：" + socket.getInetAddress().getHostName() + ":" + socket.getPort());
            IotMeterServer.num++;
            log.info("连接数：" + IotMeterServer.num);

            InputStream inputStream = socket.getInputStream();

            while (socket.isConnected() && !socket.isClosed()) {
                //若超时还未接收任何数据 关闭当前socket连接
                if((new Date()).getTime()-lastReceiveDataTime.getTime() >= IotMeterServer.ConnectTimeout){
                    closeSocket();
                    continue;
                }

                int i=0;
                int k=0;
                int n=0;
                int length=0;
                int ucTmrCnt=0;

                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i = inputStream.available();

                if (i < 22) {
                    continue;
                }

                byte[] dataStr = new byte[i];

                int read = inputStream.read(dataStr);
                /* Frame Header */
                for (i=0; i<dataStr.length; i++) {
                    if (dataStr[i] == 0x68) {
                        break;
                    }
                }

                k = (dataStr.length - i);
				/*	68 20 00 */
                if (k < 3) {
                    continue;
                }
                length = dataStr[i + 2];

                length = (length << 8);

                length +=  dataStr[i + 1];

                n = k;

                for (; n<length; ) {
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    n = inputStream.available();
                    n += k;
                    if (++ucTmrCnt > 19)
                        //timeout
                        break;
                }

                if (n < length) {
                    continue;
                }

                byte[] ucArrDat = new byte[n];

                System.arraycopy(dataStr, i, ucArrDat, 0, k);

                if (n > k) {

                    n = (n - k);

                    byte[] ucArrRestDat = new byte[n];

                    inputStream.read(ucArrRestDat);

                    System.arraycopy(ucArrRestDat, 0, ucArrDat, k, n);
                }

                if (ucArrDat[length - 1] != 0x16) {	//
                    continue;
                }

                if (Protocol.getInstance().calcCrc16(ucArrDat, 0, (length - 1)) != 0 ) {
                    continue;
                }

                log.info("get " + read + " bytes");
                log.info("Message get from devices: " + Protocol.getInstance().hexToHexString(ucArrDat));

                try {
                    messageHandler(ucArrDat);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

        }
        catch(IOException e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private static HashMap<String,Object> resultMap = new HashMap<>();
    /**
     *
     * @param message
     */
    public void messageHandler(byte[] message) {
        String messageStr = Protocol.getInstance().hexToHexString(message);
        ResponderIotMeter responder = new ResponderIotMeter(messageStr, socket.getInetAddress().getHostAddress(), socket.getPort());
        if(responder.isResponseFrame(message)){

            //如果接收到的数据是回复帧 获取返回结果
            synchronized (lock){
                if(responder.isPair(syncsSendData,message)) {
                    resultMap = responder.getResultHashMap();
                    lock.notify();
                }
            }
        }else {
            //如果接收到的数据是请求帧 则解析得到回复帧 并回复
            byte[] responderFrame = responder.getReturnByte();
            try {
                sendMessage(socket, responderFrame);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //如果请求帧是表注册 则添加表到在线表列表
            ReceivedFrame receivedFrame = new ReceivedFrame(message);
            String meterId = Tools.Bytes2HexString(receivedFrame.getSsid().getSubStationIdByte(),receivedFrame.getSsid().getSubStationIdByte().length);
            String cmdIdStr = Tools.Bytes2HexString(receivedFrame.getControlCode().getControlCodeByte(),receivedFrame.getControlCode().getControlCodeByte().length);
            if(cmdIdStr.equals("3004")){
                addMeterToOnlineList(meterId,socket);
            }
        }
    }

    /**
     * 向指定socket发送数据
     * @param socket
     * @param sendByte
     * @throws IOException
     */
    public static void sendMessage(Socket socket, byte[] sendByte) throws IOException {
        if (sendByte == null) {
            log.info("sendByte NULL;");
            return;
        }
        OutputStream os = null;
        os = socket.getOutputStream();
        //OutputStreamWriter osw = new OutputStreamWriter(os);
        //BufferedWriter bw = new BufferedWriter(osw);
        String writeContent = Protocol.getInstance().hexToHexString(sendByte);
        os.write(sendByte);
        log.info("Message send to the client is：" + writeContent);
        os.flush();
    }

    private static final Object lock = new Object();
    private static final int msgTimeout = 20000;//同步超时时间
    private static byte[] syncsSendData = null;

    /**
     * 向指定socket发送数据并获取返回 同步
     * @param socket
     * @param sendByte
     * @throws IOException
     */
    public static HashMap<String,Object> syncsSendMessage(Socket socket, byte[] sendByte) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        if (sendByte == null) {
            log.info("sendByte NULL;");
            return null;
        }
        synchronized (lock) {
            resultMap = null;
            syncsSendData = sendByte;
            OutputStream os = null;
            os = socket.getOutputStream();
            //OutputStreamWriter osw = new OutputStreamWriter(os);
            //BufferedWriter bw = new BufferedWriter(osw);
            String writeContent = Protocol.getInstance().hexToHexString(sendByte);
            os.write(sendByte);
            log.info("Message send to the client is：" + writeContent);
            os.flush();

            try {
                lock.wait(msgTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            map = resultMap;
        }

        return map;
    }


    /**
     * 关闭当前socket连接
     */
    public void closeSocket(){

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        IotMeterServer.num--;
        IotMeterServer.socketArrayList.remove(socket);
        IotMeterServer.removeMeterFromOnlineList(socket);
        log.info("关闭连接:"+socket.getInetAddress()+":"+socket.getPort()+"-->连接数：" + IotMeterServer.num);
    }

}
