package sockets.iotmeter.db.dao;


import org.apache.log4j.Logger;
import sockets.iotmeter.db.ConnectionPool;
import sockets.iotmeter.db.ConnectionPoolImpl2;
import sockets.iotmeter.db.vo.MeterDataVo;
import sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MeterDataDao {

    private static Logger log = Logger.getLogger(AirIndexDao.class);
    ConnectionPool pool = new ConnectionPoolImpl2();

    /**
     * 保存表数据
     * @param vo
     * @return
     * @throws Exception
     */
    public int saveMeterData(MeterDataVo vo) throws Exception{
        log.info("MeterDataDao saveMeterData(MeterDataVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("insert into t_g_meter_data (id,meter_id,flow,valve_state,data_date,create_date)");
            sql.append("values(?,?,?,?,?,?)");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getMeterId());
            ps.setFloat(3, vo.getFlow());
            ps.setInt(4, vo.getValveState());
            ps.setString(5, TimeTag.getStringDate(vo.getDataDate()));
            ps.setString(6, TimeTag.getStringDate());

            int result = ps.executeUpdate();

            return result;
        }
        catch(Exception e){
            log.error("保存出错！", e);
            return -1;
        }
        finally{
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            }
            catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
                    pool.releaseConnection(conn);
                    conn = null;
                }
            }
            catch (SQLException e) {
                log.error(e);
            }
        }
    }
}
