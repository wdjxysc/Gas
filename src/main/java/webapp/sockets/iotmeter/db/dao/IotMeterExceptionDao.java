package webapp.sockets.iotmeter.db.dao;


import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.db.ConnectionPool;
import webapp.sockets.iotmeter.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.MeterExceptionVo;
import webapp.sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dijun on 2017/3/1.
 */
public class IotMeterExceptionDao {
    private static Logger log = Logger.getLogger(DeviceDao.class);
    ConnectionPool pool = new ConnectionPoolImpl();

    /**
     * 查询表的异常信息
     *
     * @param meterId
     * @return
     */
    public ArrayList<MeterExceptionVo> queryIotMeterException(String meterId) {
        ArrayList<MeterExceptionVo> meterExceptionVos = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select id,meter_id,exception_code,data_date,create_date from meter_exception order by create_data desc;");
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                MeterExceptionVo meterExceptionVo = new MeterExceptionVo();
                meterExceptionVo.setId(rs.getString(1));
                meterExceptionVo.setMeterId(rs.getString(2));
                meterExceptionVo.setExceptionId(rs.getString(3));
                meterExceptionVo.setDataDate(rs.getString(4));
                meterExceptionVo.setCreateDate(rs.getString(5));
                meterExceptionVos.add(meterExceptionVo);
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
                    pool.releaseConnection(conn);
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }

        }

        return meterExceptionVos;
    }

    public int saveMeterException(MeterExceptionVo meterExceptionVo) {
        log.info("IotMeterExceptionDao saveMeterException(MeterExceptionVo meterExceptionVo) 方法开始处理...");
        Connection conn = null;
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer();
        sql.append("insert into meter_exception (id,meter_id,exception_id,data_date," +
                "create_date) values (?,?,?,?,?)");
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, meterExceptionVo.getId());
            ps.setString(2, meterExceptionVo.getMeterId());
            ps.setString(3, meterExceptionVo.getExceptionId());
            ps.setString(4, meterExceptionVo.getDataDate());
            ps.setString(5, TimeTag.getStringDate());

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
                    pool.releaseConnection(conn);
                    conn = null;
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }


    }
}
