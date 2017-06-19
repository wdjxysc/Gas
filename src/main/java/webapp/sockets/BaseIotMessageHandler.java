package webapp.sockets;

import org.apache.log4j.Logger;
import webapp.sockets.util.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;

/**
 * socket数据处理线程基类
 * Created by Administrator on 2017/5/16.
 */
public class BaseIotMessageHandler implements Runnable {
    private static Logger log = Logger.getLogger(BaseIotMessageHandler.class);
    public Socket socket;

    //接收数据超时时间
    private int getMsgTimeout = 0;

    boolean isClose = false;

    //用于记录最后一条数据接收时间
    private Date lastReceiveDataTime = new Date();

    public BaseIotMessageHandler(Socket socket) {
        this(socket,0);
    }

    public BaseIotMessageHandler(Socket socket, int timeout) {
        this.socket = socket;
        this.getMsgTimeout = timeout;
        try {
            this.socket.setSoTimeout(timeout);
            this.socket.setKeepAlive(true);
            this.socket.setOOBInline(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void run() {
        try {

            log.info("新连接：" + socket.getInetAddress().getHostName() + ":" + socket.getPort());
            log.info("连接数：" + BaseIotServer.socketArrayList.size());

            InputStream inputStream = socket.getInputStream();

            while (socket.isConnected() && !socket.isClosed()) {
                if(isClose) break;

                //若设定超时时间大于0（默认为0），且超时还未接收任何数据 关闭当前socket连接
                if (getMsgTimeout > 0) {
                    if ((new Date()).getTime() - lastReceiveDataTime.getTime() >= getMsgTimeout) {
                        log.info("超时未接收客户端数据，关闭socket");
                        socket.close();
                        release();
                        break;
                    }
                }

                int i = 0;
                int k = 0;
                int n = 0;
                int length = 0;
                int ucTmrCnt = 0;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i = inputStream.available();

                if (i < 22) {
                    continue;
                }

                byte[] dataFirstPiece = new byte[i];

                int read = inputStream.read(dataFirstPiece);
                /* Frame Header */
                for (i = 0; i < dataFirstPiece.length; i++) {
                    if (dataFirstPiece[i] == 0x68) {
                        log.info("接收到数据头 0x68");
                        break;
                    }
                }

                k = (dataFirstPiece.length - i);
                /*	68 20 00 */
                if (k < 3) {
                    continue;
                }
                length = dataFirstPiece[i + 2] & 0xff;

                length = (length << 8);

                length += dataFirstPiece[i + 1] & 0xff;
                log.info("数据总长度-----" + length);

                n = k;

                for (; n < length; ) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    n = inputStream.available();
                    n += k;
                    log.info("available" + n);
                    //集中器每包数据发送间隔大约4-5s 超时时间设为6s
                    if (++ucTmrCnt > 40)
                        //timeout
                        break;
                }

                if (n < length) {
                    log.info("数据未完全接收，舍弃");
                    continue;
                }

                byte[] ucArrDat = new byte[n];

                System.arraycopy(dataFirstPiece, i, ucArrDat, 0, k);

                if (n > k) {

                    n = (n - k);

                    byte[] ucArrRestDat = new byte[n];

                    inputStream.read(ucArrRestDat);

                    System.arraycopy(ucArrRestDat, 0, ucArrDat, k, n);
                }

                if (ucArrDat[length - 1] != 0x16) {    //
                    continue;
                }

                if (Protocol.getInstance().calcCrc16(ucArrDat, 0, (length - 1)) != 0) {
                    continue;
                }

                log.info("get " + read + " bytes");
                log.info("Message get from devices: " + Protocol.getInstance().hexToHexString(ucArrDat));
//                log.info("接收到数据，更新时间");
                try {
                    lastReceiveDataTime = new Date();//接收到数据 重置该时间
                    messageHandler(ucArrDat);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            log.info(this.hashCode() + "----线程结束");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            release();
        }
    }

    public HashMap<String, Object> resultMap = new HashMap<>();

    /**
     * 处理数据 解析请求或者返回  重写该方法以处理不同设备数据
     *
     * @param message
     */
    public void messageHandler(byte[] message) {
        String messageStr = Protocol.getInstance().hexToHexString(message);
    }


    /**
     * 关闭当前socket连接
     */
    public void release() {
        isClose = true;

        BaseIotServer.removeDeviceFromOnlineList(socket);
        log.info("释放连接:" + socket.getInetAddress() + ":" + socket.getPort() + "-->连接数：" + BaseIotServer.socketArrayList.size());
        socket = null;
    }


    /**
     * 向指定socket发送数据
     *
     * @param sendByte
     * @throws IOException
     */
    public void sendMessage(byte[] sendByte) throws IOException {
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


    public final Object lock = new Object();
    /**
     * 同步通信超时时间
     */
    public final int msgTimeout = 10000;//同步通信超时时间
    public byte[] syncsSendData = null;

    /**
     * 向指定socket发送数据并获取返回 同步
     *
     * @param sendByte
     * @throws IOException
     */
    public HashMap<String, Object> syncsSendMessage(byte[] sendByte) throws IOException {
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
     * 向指定socket发送数据并获取返回 同步
     *
     * @param sendByte
     * @param timeout 接收返回数据超时时间
     * @throws IOException
     */
    public HashMap<String, Object> syncsSendMessage(byte[] sendByte, int timeout) throws IOException {
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
                lock.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            map = resultMap;
        }

        return map;
    }
}
