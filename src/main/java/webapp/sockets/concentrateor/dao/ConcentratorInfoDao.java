package webapp.sockets.concentrateor.dao;

import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.sockets.concentrateor.dao.vo.ConcentratorInfoVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/21.
 */
public class ConcentratorInfoDao {
    private static Logger log = Logger.getLogger(ConcentratorInfoDao.class);
    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询设备编号
     * @param concentratorId
     * @return
     * @throws Exception
     */
    public int queryConcentratorInfoById(String concentratorId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select count(*) from concentrator_info where concentrator_id = ? order by create_date desc");
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, concentratorId);
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
    public ArrayList<ConcentratorInfoVo> queryConcentrator(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        ArrayList<ConcentratorInfoVo> arrayList = new ArrayList();
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from concentrator_info");
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                ConcentratorInfoVo vo = new ConcentratorInfoVo();
                vo.setId(rs.getString(1));
                vo.setConcentratorId(rs.getString(2));
                vo.setClientIp(rs.getString(3));
                vo.setClientPort(rs.getInt(4));
                vo.setLastOnlineDate(TimeTag.getStringDate(rs.getTimestamp(5)));
                arrayList.add(vo);
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
    public int saveConcentratorInfo(ConcentratorInfoVo vo){
        log.info("ConcentratorInfoDao saveConcentratorInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("insert into concentrator_info (id,concentrator_id,ip,port,last_connect_date, create_date,");
            sql.append("update_date) ");
            sql.append("values (?,?,?,?,?,?,?)");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getConcentratorId());
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
    public int updateConcentratorInfo(ConcentratorInfoVo vo) throws Exception{
        log.info("ConcentratorInfoDao updateConcentratorInfo(ConcentratorInfoVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = pool.getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("update concentrator_info set last_connect_date = ?,ip=?,port=? where concentrator_id = ?");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, TimeTag.getStringDate());
            ps.setString(2, vo.getClientIp());
            ps.setInt(3, vo.getClientPort());
            ps.setString(4, vo.getConcentratorId());

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
