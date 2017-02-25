package webapp.servlet;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import webapp.sockets.iotmeter.IotMeterServer;
import webapp.sockets.iotmeter.db.dao.IotMeterInfoDao;
import webapp.sockets.iotmeter.db.vo.IotMeterInfoVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Administrator on 2017/2/23.
 */
@WebServlet(name = "MeterServlet", urlPatterns = "/meter/*")
public class MeterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> resultMap = new HashMap<>();

        String[] strings = request.getRequestURI().split("/");

        String methodStr = "";
        if (strings.length >= 3) {
            methodStr = strings[2];
        }


        switch (methodStr) {
            case "list":
                resultMap = getMeterList();
                break;
        }

        //设置响应内容类型
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");//解决：跨源请求:同源策略禁止读取 问题

        //设置逻辑实现
        PrintWriter out = response.getWriter();
        out.println(JSON.toJSONString(resultMap));
    }


    /**
     * 获取 表信息状态 包括表在线状态
     *
     * @return
     */
    private HashMap<String, Object> getMeterList() {
        HashMap<String, Object> map = new HashMap<>();


        IotMeterInfoDao dao = new IotMeterInfoDao();
        ArrayList<IotMeterInfoVo> iotMeterInfoVos = dao.queryIotMeter();

        ArrayList<HashMap<String, Object>> listMap = new ArrayList<>();
        for (IotMeterInfoVo vo : iotMeterInfoVos) {
            listMap.add(JSON.parseObject(JSON.toJSONString(vo), new TypeReference<HashMap<String, Object>>() {
            }));
        }

        IotMeterServer iotMeterServer = IotMeterServer.getInstance();
        for (HashMap<String, Object> item : listMap) {
            if (iotMeterServer.isMeterOnline(item.get("meterId").toString())) {
                item.put("online", true);
            } else {
                item.put("online", false);
            }
        }

        map.put("success", true);
        map.put("data", listMap);
        return map;
    }

}
