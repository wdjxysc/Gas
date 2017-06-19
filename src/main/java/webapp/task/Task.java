package webapp.task;

import org.apache.log4j.Logger;
import webapp.servlet.ConcentratorSocketServerServlet;
import webapp.sockets.concentrateor.dao.ConcentratorInfoDao;
import webapp.sockets.concentrateor.dao.vo.ConcentratorInfoVo;
import webapp.sockets.concentrateor.frame.RequesterConcentrator;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/6/8.
 */
public class Task {
    Logger logger = Logger.getLogger(Task.class);

    private static Task task;

    public static Task getInstance() {
        if (task == null) {
            task = new Task();
        }
        return task;
    }

    public void startConcentratorTask() {

        Runnable runnable = () -> {
            logger.info("开始获取集抄数据");
            RequesterConcentrator requesterConcentrator = RequesterConcentrator.getInstance();

            ConcentratorInfoDao dao = new ConcentratorInfoDao();
            ArrayList<ConcentratorInfoVo> lists = dao.queryConcentrator();
            for (int i = 0; i < lists.size(); i++) {
                requesterConcentrator.readGroupReadData(lists.get(i).getConcentratorId(), new Date());
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 30, 3600 * 24, TimeUnit.SECONDS);
    }

}
