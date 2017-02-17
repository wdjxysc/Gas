package sockets.iotmeter;


import org.apache.log4j.Logger;
import sockets.iotmeter.frame.ResponderIotMeter;
import sockets.iotmeter.protocol.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/12.
 */

public class IotMeterServer {
    private static Logger log = Logger.getLogger(IotMeterServer.class);
    private int port = 20007;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final int POOL_SIZE = 10;

    public static int num = 0;

    public IotMeterServer() throws IOException {
        serverSocket = new ServerSocket(port);
        //返回cup数目
        int cupCount = Runtime.getRuntime().availableProcessors();
        log.info("CPU数目：" + cupCount);
        executorService = Executors.newFixedThreadPool(cupCount * POOL_SIZE);
        log.info("服务器已启动......");

    }

    public void startServer(){
        while(true){
            Socket socket = null;
            try{
                socket = serverSocket.accept();
                executorService.execute(new IotMeterMessageHandler(socket));
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new IotMeterServer().startServer();
    }
}

class IotMeterMessageHandler implements Runnable{
    private static Logger log = Logger.getLogger(Handler.class);
    private Socket socket;


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

                messageHandler(ucArrDat);
            }

        }
        catch(IOException e){
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     */
    public void messageHandler(byte[] message){
        String messageStr = Protocol.getInstance().hexToHexString(message);
        ResponderIotMeter responder = new ResponderIotMeter(messageStr,socket.getInetAddress().getHostAddress(),socket.getPort());
        byte[] sendFrame = responder.getReturnByte();
        try {
            sendMessage(socket, sendFrame);
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        finally{
            try{
                if(socket!=null){
                    socket.close();
                    EchoServer.num--;
                    log.info("关闭连接:"+socket.getInetAddress()+":"+socket.getPort()+"-->连接数：" + EchoServer.num);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param socket
     * @param sendByte
     * @throws IOException
     */
    public void sendMessage(Socket socket, byte[] sendByte) throws IOException {
        if(sendByte == null){
            log.info("sendByte NULL;");
            return;
        }
        OutputStream os = socket.getOutputStream();
        //OutputStreamWriter osw = new OutputStreamWriter(os);
        //BufferedWriter bw = new BufferedWriter(osw);
        String writeContent = Protocol.getInstance().hexToHexString(sendByte);
        os.write(sendByte);
        log.info("Message send to the client is：" + writeContent);
        os.flush();
    }

}

