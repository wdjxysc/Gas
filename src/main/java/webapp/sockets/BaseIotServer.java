package webapp.sockets;

import org.apache.log4j.Logger;
import webapp.servlet.BaseWebSocket;
import webapp.sockets.iotmeter.IotMeterServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/16.
 */
public class BaseIotServer {
    private static Logger log = Logger.getLogger(BaseIotServer.class);
    private int port = 19999;//端口默认19999
    private ServerSocket serverSocket;
    public ExecutorService executorService;
    private final int POOL_SIZE = 10;

    /*连接超时时间 未获取数据时间*/
    public int connectTimeout = 0;

    boolean isStop = false;

    /**
     * 连接列表 可能是表的 也可能是其他连接的客户端
     */
    public static ArrayList<Socket> socketArrayList = new ArrayList<>();

    /**
     * 所有在线注册设备信息列表
     */
    public static ArrayList<HashMap<String, Object>> onlineDeviceMapList = new ArrayList<>();

    private static BaseIotServer baseIotServer;


    public BaseIotServer() throws IOException {
        this(19999);
    }

    /**
     *
     * @param port 端口
     * @throws IOException
     */
    public BaseIotServer(int port) throws IOException {
        this(port,0);
    }

    /**
     *
     * @param port
     * @param connectTimeout 连接超时时间 未获取数据时间
     * @throws IOException
     */
    public BaseIotServer(int port, int connectTimeout) throws IOException{
        this.connectTimeout = connectTimeout;
        this.port = port;
        serverSocket = new ServerSocket(port);
        //返回cup数目
        int cupCount = Runtime.getRuntime().availableProcessors();
        log.info("CPU数目：" + cupCount);
        executorService = Executors.newFixedThreadPool(cupCount * POOL_SIZE);
        log.info("服务器已启动......" + port);
    }

    /**
     * 启动server 开始监听
     */
    public void startServer() {
        while (true) {
            if(isStop) break;
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                messageHandler(socket);
                socketArrayList.add(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新的线程里处理数据收发 重写该方法实现不同设备的数据处理
     * @param socket
     */
    public void messageHandler(Socket socket){
        executorService.execute(new BaseIotMessageHandler(socket));
    }


    /**
     * 关闭server
     */
    public void stopServer() {
        System.out.print("stopServer BaseIotServer");
        isStop = true;
        for (Socket aSocketArrayList : socketArrayList) {
            try {
                aSocketArrayList.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
            onlineDeviceMapList.clear();//清空在线列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据设备号查找已接入的socket
     *
     * @param deviceId
     * @return
     */
    public static Socket getSocketByDeviceId(String deviceId) {
        return getMessageHandlerByDeviceId(deviceId).socket;
    }

    /**
     * 根据设备号查找已接入的对应的messageHandler
     *
     * @param deviceId
     * @return
     */
    public static BaseIotMessageHandler getMessageHandlerByDeviceId(String deviceId) {

        BaseIotMessageHandler baseIotMessageHandler = null;
        for (HashMap<String, Object> anOnlineMeterMapList : onlineDeviceMapList) {
            if (anOnlineMeterMapList.get(KEY_DEVICE_ID).toString().equals(deviceId)) {
                baseIotMessageHandler = (BaseIotMessageHandler) anOnlineMeterMapList.get(KEY_MESSAGE_HANDLER);
            }
        }
        return baseIotMessageHandler;
    }

    /**
     * 添加在线表 若已存在更新 不存在则增加
     *
     * @param meterId
     * @param socket
     */
    public static void addDeviceToOnlineList(String meterId, Socket socket, BaseIotMessageHandler baseIotMessageHandler) {
        for (HashMap<String, Object> anOnlineDeviceMapList : onlineDeviceMapList) {
            if (anOnlineDeviceMapList.get(KEY_DEVICE_ID).equals(meterId)) {
                anOnlineDeviceMapList.put(KEY_SOCKET, socket);
                return;
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(KEY_DEVICE_ID, meterId);
        map.put(KEY_SOCKET, socket);
        map.put(KEY_MESSAGE_HANDLER, baseIotMessageHandler);
        onlineDeviceMapList.add(map);

        BaseWebSocket.meterListChanged();
//        RequesterConcentrator requestIotMeter = RequesterConcentrator.getInstance();
//        requestIotMeter.readMeter("20011607000001");
    }

    /**
     * 移除在线表
     *
     * @param socket
     */
    public static void removeDeviceFromOnlineList(Socket socket) {
        socketArrayList.remove(socket);
        for (int i = 0; i < onlineDeviceMapList.size(); i++) {
            if (onlineDeviceMapList.get(i).get(KEY_SOCKET).equals(socket)) {
                onlineDeviceMapList.remove(i);
                BaseWebSocket.meterListChanged();
                return;
            }
        }
    }


    /**
     * 根据deviceId查找该设备是否存在于在线list中
     * @param deviceId
     * @return
     */
    public boolean isDeviceOnline(String deviceId) {
        boolean b = false;
        for (HashMap<String, Object> map : BaseIotServer.onlineDeviceMapList) {
            if (map.get(KEY_DEVICE_ID).equals(deviceId)) {
                return true;
            }
        }
        return b;
    }


    static final String KEY_DEVICE_ID = "DeviceId";
    static final String KEY_SOCKET = "Socket";
    static final String KEY_MESSAGE_HANDLER = "MessageHandler";


    public static void main(String[] args) throws Exception {
        new BaseIotServer().startServer();
    }
}
