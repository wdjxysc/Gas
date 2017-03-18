package webapp.servlet;


import com.alibaba.fastjson.JSON;
import webapp.sockets.iotmeter.IotMeterServer;
import webapp.sockets.iotmeter.cmd.bean.MeterInfo;
import webapp.sockets.iotmeter.frame.RequestIotMeter;
import webapp.sockets.iotmeter.frame.ResponderIotMeter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/22.
 */
@WebServlet(name = "SocketServerServlet", loadOnStartup = 0, urlPatterns = "/socket/*")
public class SocketServerServlet extends HttpServlet {

    private IotMeterServer iotMeterServer;

    @Override
    public void init() throws ServletException {
            iotMeterServer = IotMeterServer.getInstance();

            new Thread(() -> iotMeterServer.startServer()).start();

        MeterInfo meterInfo = new MeterInfo();
        meterInfo.setMeterId("dsaffasdfaf");
        System.out.println("启动");
        log("启动");
        super.init();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String,Object> resultMap = new HashMap<>();

        String[] strings = request.getRequestURI().split("/");

        String methodStr = "";
        if(strings.length >= 3){
            methodStr = strings[2];
        }

        Map<String, String[]> map = request.getParameterMap();

        if(map.containsKey("meterId")){
            switch (methodStr){
                case "read":
                    resultMap = readMeterByMeterId(map.get("meterId")[0]);
                    break;
                case "open":
                    resultMap = openMeterValveByMeterId(map.get("meterId")[0]);
                    break;
                case "close":
                    resultMap = closeMeterValveByMeterId(map.get("meterId")[0]);
                    break;
            }
        }


        //设置响应内容类型
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");//解决：跨源请求:同源策略禁止读取 问题

        //设置逻辑实现
        PrintWriter out = response.getWriter();
        out.println(JSON.toJSONString(resultMap));
    }

    private HashMap<String,Object> readMeterByMeterId(String meterId){
        RequestIotMeter requestIotMeter = RequestIotMeter.getInstance();
        HashMap map = requestIotMeter.readMeter(meterId);
        HashMap<String,Object> result = new HashMap<>();
        result.put("success", map.get(ResponderIotMeter.KEY_SUCCESS));
        if(!(boolean)map.get(ResponderIotMeter.KEY_SUCCESS)){
            result.put("err_msg",map.get(ResponderIotMeter.KEY_ERR_MESSAGE));
        }else {
            result.put("meterId",map.get(ResponderIotMeter.KEY_METER_ID));
            result.put("flow",map.get(ResponderIotMeter.KEY_METER_ID));
            result.put("valveState",map.get(ResponderIotMeter.KEY_METER_ID));
            result.put("dataTime",map.get(ResponderIotMeter.KEY_METER_ID));
        }
        return result;
    }

    private HashMap<String,Object> openMeterValveByMeterId(String meterId){
        RequestIotMeter requestIotMeter = RequestIotMeter.getInstance();
        HashMap map = requestIotMeter.openMeterValve(meterId);
        HashMap<String,Object> result = new HashMap<>();
        result.put("success", map.get(ResponderIotMeter.KEY_SUCCESS));
        if(!(boolean)map.get(ResponderIotMeter.KEY_SUCCESS)){
            result.put("err_msg",map.get(ResponderIotMeter.KEY_ERR_MESSAGE));
        }

        return result;
    }

    private HashMap<String,Object> closeMeterValveByMeterId(String meterId){
        RequestIotMeter requestIotMeter = RequestIotMeter.getInstance();
        HashMap map = requestIotMeter.closeMeterValve(meterId);
        HashMap<String,Object> result = new HashMap<>();
        result.put("success", map.get(ResponderIotMeter.KEY_SUCCESS));
        if(!(boolean)map.get(ResponderIotMeter.KEY_SUCCESS)){
            result.put("err_msg",map.get(ResponderIotMeter.KEY_ERR_MESSAGE));
        }
        return result;
    }
}
