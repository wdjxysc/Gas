package webapp.test;

import webapp.sockets.concentrateor.dao.MeterDataDao;
import webapp.sockets.concentrateor.dao.vo.MeterDataVo;
import webapp.sockets.util.Protocol;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/10.
 */
public class Test {
    public static void main(String[] args) {

        MeterDataDao meterDataDao = new MeterDataDao();

        ArrayList<MeterDataVo> meterDataArrayList = new ArrayList<>();
        for (int i = 0;i<100;i++){
            MeterDataVo vo = new MeterDataVo();
            vo.setId(Protocol.getInstance().getUUID());
            vo.setFlow(i+0.9f);
            vo.setMeterId("43051500000001");
            vo.setValveState(1);

            meterDataArrayList.add(vo);
        }

        meterDataDao.saveMeterData(meterDataArrayList);
    }
}
