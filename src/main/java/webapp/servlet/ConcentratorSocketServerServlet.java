package webapp.servlet;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import webapp.sockets.concentrateor.IotConcentratorServer;
import webapp.sockets.concentrateor.frame.RequesterConcentrator;
import webapp.sockets.concentrateor.frame.ResponderConcentrator;

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
 * Created by Administrator on 2017/5/20.
 */
@WebServlet(name = "ConcentratorSocketServerServlet", loadOnStartup = 0, urlPatterns = "/concentratorSocket/*")
public class ConcentratorSocketServerServlet extends HttpServlet {
    private static Logger log = Logger.getLogger(ConcentratorSocketServerServlet.class);
    public static IotConcentratorServer iotConcentratorServer;

    @Override
    public void init() throws ServletException {

        try {
            iotConcentratorServer = new IotConcentratorServer(20006);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> iotConcentratorServer.startServer()).start();

        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> resultMap = new HashMap<>();

        String[] strings = request.getRequestURI().split("/");

        String methodStr = "";
        if (strings.length >= 3) {
            methodStr = strings[2];
        }

        Map<String, String[]> map = request.getParameterMap();

        if (map.containsKey("meterId")) {
            switch (methodStr) {
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

        if (map.containsKey("concentratorId")) {
            switch (methodStr) {
                case "readGroup":
                    resultMap = readGroupReadDataByConcentratorId(map.get("concentratorId")[0]);
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


    private HashMap<String, Object> readMeterByMeterId(String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.readMeter(meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        } else {
            result.put("meterId", map.get(ResponderConcentrator.KEY_METER_ID));
            result.put("flow", map.get(ResponderConcentrator.KEY_METER_VALUE));
            result.put("valveState", map.get(ResponderConcentrator.KEY_METER_VALVE_STATE));
            result.put("dataTime", map.get(ResponderConcentrator.KEY_DATA_TIME));
        }
        return result;
    }

    private HashMap<String, Object> openMeterValveByMeterId(String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.openMeterValve(meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }

        return result;
    }

    private HashMap<String, Object> closeMeterValveByMeterId(String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.closeMeterValve(meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }

    private HashMap<String, Object> readGroupReadDataByConcentratorId(String concentratorId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.readGroupReadData(concentratorId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }


    @Override
    public void destroy() {
        iotConcentratorServer.stopServer();
        super.destroy();
    }
}
