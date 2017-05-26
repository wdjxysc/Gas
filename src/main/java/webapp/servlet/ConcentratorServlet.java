package webapp.servlet;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import webapp.sockets.concentrateor.IotConcentratorServer;
import webapp.sockets.concentrateor.dao.ConcentratorInfoDao;
import webapp.sockets.concentrateor.dao.vo.ConcentratorInfoVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/23.
 */
@WebServlet(name = "ConcentratorServlet", urlPatterns = "/concentrator/*")
public class ConcentratorServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, Object> resultMap = new HashMap<>();

        String[] strings = request.getRequestURI().split("/");

        String methodStr = "";
        if (strings.length >= 3) {
            methodStr = strings[2];
        }

        Map<String, String[]> map = request.getParameterMap();
        switch (methodStr) {
            case "list":
                resultMap = getConcentratorList();
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
     * 查询 集中器信息状态 包括表在线状态
     *
     * @return
     */
    public static HashMap<String, Object> getConcentratorList() {
        HashMap<String, Object> map = new HashMap<>();


        ConcentratorInfoDao dao = new ConcentratorInfoDao();
        ArrayList<ConcentratorInfoVo> concentratorInfoVos = dao.queryConcentrator();

        ArrayList<HashMap<String, Object>> listMap = new ArrayList<>();
        for (ConcentratorInfoVo vo : concentratorInfoVos) {
            listMap.add(JSON.parseObject(JSON.toJSONString(vo), new TypeReference<HashMap<String, Object>>() {
            }));
        }

        IotConcentratorServer iotConcentratorServer = ConcentratorSocketServerServlet.iotConcentratorServer;
        for (HashMap<String, Object> item : listMap) {
            if (iotConcentratorServer.isDeviceOnline(item.get("concentratorId").toString())) {
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
