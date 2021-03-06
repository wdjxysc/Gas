package webapp.sockets.iotmeter.db.dao;

import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.IotMeterInfoVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
            sql.append("select count(*) from iot_meter_info where meter_id = ? order by create_date desc");
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
     * 查询设备列表
     * @return
     * @throws Exception
     */
    public ArrayList<IotMeterInfoVo> queryIotMeter(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        ArrayList<IotMeterInfoVo> arrayList = new ArrayList();
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from iot_meter_info");
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                IotMeterInfoVo iotMeterInfoVo = new IotMeterInfoVo();
                iotMeterInfoVo.setId(rs.getString(1));
                iotMeterInfoVo.setMeterId(rs.getString(2));
                iotMeterInfoVo.setClientIp(rs.getString(3));
                iotMeterInfoVo.setClientPort(rs.getInt(4));
                iotMeterInfoVo.setLastOnlineDate(TimeTag.getStringDate(rs.getTimestamp(5)));
                arrayList.add(iotMeterInfoVo);
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
     * 保存设备表
     * @param vo
     * @return
     * @throws Exception
     */
    public int saveIotMeterInfo(IotMeterInfoVo vo){
        log.info("ConcentratorInfoDao saveIotMeterInfo(ConcentratorInfoVo vo) 方法开始处理...");
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
        log.info("ConcentratorInfoDao updateIotMeterInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("update iot_meter_info set last_connect_date = ?,ip=?,port=? where meter_id = ?");
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
