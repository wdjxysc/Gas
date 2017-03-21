package webapp.sockets.iotmeter.db.dao;


import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.db.ConnectionPool;
import webapp.sockets.iotmeter.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.IotMeterInfoVo;
import webapp.sockets.iotmeter.db.vo.MeterDataVo;
import webapp.sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MeterDataDao {

    private static Logger log = Logger.getLogger(AirIndexDao.class);
    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询设备列表
     * @return
     * @throws Exception
     */
    public ArrayList<MeterDataVo> queryMeterData(String meterId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        ArrayList<MeterDataVo> arrayList = new ArrayList();
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from meter_data where meter_id = ? order by create_date desc");
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1,meterId);
            rs = ps.executeQuery();
            while (rs.next()) {
                MeterDataVo meterDataVo = new MeterDataVo();
                meterDataVo.setId(rs.getString(1));
                meterDataVo.setMeterId(rs.getString(2));
                meterDataVo.setFlow(rs.getFloat(3));
                meterDataVo.setValveState(rs.getInt(4));
                meterDataVo.setDataTime(TimeTag.getStringDate(rs.getTime(5)));
                meterDataVo.setCreateDate(TimeTag.getStringDate(rs.getTime(6)));
                arrayList.add(meterDataVo);
            }
        }
        catch(Exception e){
            log.error(e);
        }
        finally{
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
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
                    conn=null;
                }
            }
            catch (SQLException e) {
                log.error(e);
            }
        }
        return arrayList;
    }

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
            sql.append("insert into meter_data (id, meter_id, flow, valve_state, data_date, create_date)");
            sql.append("values(?,?,?,?,?,?)");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getMeterId());
            ps.setFloat(3, vo.getFlow());
            ps.setInt(4, vo.getValveState());
            ps.setString(5, vo.getDataTime());
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
