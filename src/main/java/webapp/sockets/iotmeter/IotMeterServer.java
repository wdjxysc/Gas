package webapp.sockets.iotmeter;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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

    static int num = 0;

    /*连接超时时间 未获取数据时间 暂设5分钟*/
    static final int ConnectTimeout = 300000;

    /**
     * 连接列表 可能是表的 也可能是其他连接的仪器
     */
    public static ArrayList<Socket> socketArrayList = new ArrayList<>();

    /**
     * 所有在线表信息列表
     */
    public static ArrayList<HashMap<String,Object>> onlineMeterMapList = new ArrayList<>();

    private IotMeterServer() throws IOException {
        serverSocket = new ServerSocket(port);
        //返回cup数目
        int cupCount = Runtime.getRuntime().availableProcessors();
        log.info("CPU数目：" + cupCount);
        executorService = Executors.newFixedThreadPool(cupCount * POOL_SIZE);
        log.info("服务器已启动......");
    }

    private static IotMeterServer iotMeterServer;
    public static IotMeterServer getInstance () {
        if(iotMeterServer == null){
            try {
                iotMeterServer = new IotMeterServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iotMeterServer;
    }


    /**
     * 启动server
     */
    public void startServer(){
        while(true){
            Socket socket = null;
            try{
                socket = serverSocket.accept();
                executorService.execute(new IotMeterMessageHandler(socket));
                socketArrayList.add(socket);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭server
     */
    public void stopServer() {
        for (Socket aSocketArrayList : socketArrayList) {
            try {
                aSocketArrayList.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据表号查找已接入的socket
     * @param meterId
     * @return
     */
    public static Socket getSocketByMeterId(String meterId){

        Socket socket = null;
        for (HashMap<String, Object> anOnlineMeterMapList : onlineMeterMapList) {
            if (anOnlineMeterMapList.get("MeterId").toString().equals(meterId)) {
                socket = (Socket) anOnlineMeterMapList.get("Socket");
            }
        }
        return socket;
    }

    /**
     * 添加在线表 若已存在更新 不存在则增加
     * @param meterId
     * @param socket
     */
    static void addMeterToOnlineList(String meterId, Socket socket){
        for (HashMap<String, Object> anOnlineMeterMapList : onlineMeterMapList) {
            if (anOnlineMeterMapList.get("MeterId").equals(meterId)) {
                anOnlineMeterMapList.put("Socket", socket);
                return;
            }
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("MeterId", meterId);
        map.put("Socket", socket);
        onlineMeterMapList.add(map);

//        RequestIotMeter requestIotMeter = RequestIotMeter.getInstance();
//        requestIotMeter.readMeter("20011607000001");
    }

    /**
     * 移除在线表
     * @param socket
     */
    public static void removeMeterFromOnlineList(Socket socket){
        for (int i = 0;i<onlineMeterMapList.size();i++){
            if(onlineMeterMapList.get(i).get("Socket").equals(socket)){
                onlineMeterMapList.remove(i);
                return;
            }
        }
    }


    public boolean isMeterOnline(String meterId){
        boolean b = false;
        for (HashMap<String,Object> map:IotMeterServer.onlineMeterMapList){
            if(map.get("MeterId").equals(meterId)){
                return true;
            }
        }
        return b;
    }


    public static void main(String[] args) throws Exception {
        new IotMeterServer().startServer();
    }
}

