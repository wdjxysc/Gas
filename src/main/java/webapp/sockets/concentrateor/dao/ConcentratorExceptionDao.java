package webapp.sockets.concentrateor.dao;


import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.dbcp.DbcpProvider;
import webapp.sockets.concentrateor.dao.vo.ConcentratorExceptionVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dijun on 2017/3/1.
 */
public class ConcentratorExceptionDao {
    private static Logger log = Logger.getLogger(ConcentratorExceptionDao.class);
//    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询异常信息
     *
     * @param meterId
     * @return
     */
    public ArrayList<ConcentratorExceptionVo> queryConcentratorException(String meterId) {
        ArrayList<ConcentratorExceptionVo> vos = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select id, concentrator_id, collector_id, exception_code, data_date, create_date from concentrator_exception where meter_id=? order by create_date desc;");
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1,meterId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ConcentratorExceptionVo vo = new ConcentratorExceptionVo();
                vo.setId(rs.getString(1));
                vo.setCollectorId(rs.getString(2));
                vo.setCollectorId(rs.getString(3));
                vo.setExceptionId(rs.getString(4));
                vo.setDataDate(rs.getString(5));
                vo.setCreateDate(rs.getString(6));
                vos.add(vo);
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
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
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }

        }

        return vos;
    }

    public int saveConcentratorException(ConcentratorExceptionVo vo) {
        log.info("ConcentratorExceptionDao saveConcentratorException(ConcentratorExceptionVo vo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer();
        sql.append("insert into concentrator_exception (id, concentrator_id, collector_id, exception_code, data_date," +
                "create_date) values (?,?,?,?,?,?)");
        try {
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getConcentratorId());
            ps.setString(3, vo.getCollectorId());
            ps.setString(4, vo.getExceptionId());
            ps.setString(5, vo.getDataDate());
            ps.setString(6, TimeTag.getStringDate());

            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("保存出错", e);
            return -1;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
            try {
                if (conn != null) {
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }


    }
}
