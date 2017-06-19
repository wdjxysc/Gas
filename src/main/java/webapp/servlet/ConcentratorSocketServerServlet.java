package webapp.servlet;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import webapp.sockets.concentrateor.IotConcentratorServer;
import webapp.sockets.concentrateor.dao.ConcentratorCollectorMeterMapDao;
import webapp.sockets.concentrateor.frame.RequesterConcentrator;
import webapp.sockets.concentrateor.frame.ResponderConcentrator;
import webapp.sockets.util.Protocol;
import webapp.sockets.util.TimeTag;
import webapp.task.Task;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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

            iotConcentratorServer = new IotConcentratorServer(12347, 300000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> iotConcentratorServer.startServer()).start();

        Task task = Task.getInstance();
        task.startConcentratorTask();

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
                case "addSubMeter":
                    resultMap = addSubMeter(map.get("concentratorId")[0], map.get("meterId")[0]);
                    break;
                case "removeSubMeter":
                    resultMap = removeSubMeter(map.get("concentratorId")[0], map.get("meterId")[0]);
                    break;
                case "setTime":
                    resultMap = setTime(map.get("concentratorId")[0], new Date(Integer.parseInt(map.get("meterId")[0])));
                    break;
                case "refreshSubMeter":
                    resultMap = querySubMeter(map.get("concentratorId")[0], "0000000000");
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
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
        } else {
            HashMap<String, Object> dataContent = (HashMap) map.get(ResponderConcentrator.KEY_DATA_CONTENT);
            result.put("meterId", dataContent.get(ResponderConcentrator.KEY_METER_ID));
            result.put("flow", dataContent.get(ResponderConcentrator.KEY_METER_VALUE));
            result.put("valveState", dataContent.get(ResponderConcentrator.KEY_METER_VALVE_STATE));
            result.put("dataTime", dataContent.get(ResponderConcentrator.KEY_DATA_TIME));
        }
        return result;
    }

    private HashMap<String, Object> openMeterValveByMeterId(String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.openMeterValve(meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }

        return result;
    }

    private HashMap<String, Object> closeMeterValveByMeterId(String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.closeMeterValve(meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }

    private HashMap<String, Object> readGroupReadDataByConcentratorId(String concentratorId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.readGroupReadData(concentratorId, new Date(10000));
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));

        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }

    private HashMap<String, Object> addSubMeter(String concentratorId, String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.addSubMeter(concentratorId, meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }

    private HashMap<String, Object> removeSubMeter(String concentratorId, String meterId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.removeSubMeter(concentratorId, meterId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        } else {
            //数据库中删除记录
            ConcentratorCollectorMeterMapDao dao = new ConcentratorCollectorMeterMapDao();
            dao.deleteMapByMeterId(meterId);
        }
        return result;
    }

    private HashMap<String, Object> setTime(String concentratorId, Date date) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.setConcentratorTime(concentratorId, date);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
            result.put("err_msg", map.get(ResponderConcentrator.KEY_ERR_MESSAGE));
        }
        return result;
    }

    private HashMap<String, Object> querySubMeter(String concentratorId, String collectorId) {
        RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();
        HashMap map = requesterConcentrator.readSubMeter(concentratorId, collectorId);
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", map.get(ResponderConcentrator.KEY_SUCCESS));
        if (!(boolean) map.get(ResponderConcentrator.KEY_SUCCESS)) {
            result.put("back_code", map.get(ResponderConcentrator.KEY_BACK_CODE));
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
