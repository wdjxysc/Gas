package webapp.sockets.iotmeter.db.dao;

import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.db.ConnectionPool;
import webapp.sockets.iotmeter.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.IotMeterInfoVo;
import webapp.sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/2/21.
 */
public class IotMeterInfoDao {
    private static Logger log = Logger.getLogger(DeviceDao.class);
    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询设备编号
     * @param meterId
     * @return
     * @throws Exception
     */
    public int queryIotMeterNo(String meterId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select count(*) from iot_meter_info where meter_id = ?");
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, meterId);
            rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
                break;
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
        return count;
    }

    /**
     * 保存设备表
     * @param vo
     * @return
     * @throws Exception
     */
    public int saveIotMeterInfo(IotMeterInfoVo vo) throws Exception{
        log.info("IotMeterInfoDao saveIotMeterInfo(IotMeterInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("insert into iot_meter_info (id,meter_id,ip,port,last_connect_date, create_date,");
            sql.append("update_date) ");
            sql.append("values (?,?,?,?,?,?,?)");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getMeterId());
            ps.setString(3, vo.getClientIp());
            ps.setInt(4, vo.getClientPort());
            ps.setString(5, TimeTag.getStringDate());
            ps.setString(6, TimeTag.getStringDate());
            ps.setString(7, TimeTag.getStringDate());


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

    /**
     * 更新设备表
     * @param vo
     * @return
     * @throws Exception
     */
    public int updateIotMeterInfo(IotMeterInfoVo vo) throws Exception{
        log.info("IotMeterInfoDao updateIotMeterInfo(IotMeterInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("update iot_meter_info set update_date = ?,ip=?,port=? where meter_id = ?");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, TimeTag.getStringDate());
            ps.setString(2, vo.getClientIp());
            ps.setInt(3, vo.getClientPort());
            ps.setString(4, vo.getMeterId());

            int result = ps.executeUpdate();

            return result;
        }
        catch(Exception e){
            log.error("更新出错！", e);
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
