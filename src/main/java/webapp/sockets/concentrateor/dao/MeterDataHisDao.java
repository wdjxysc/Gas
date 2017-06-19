package webapp.sockets.concentrateor.dao;


import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.dbcp.DbcpProvider;
import webapp.sockets.concentrateor.dao.vo.MeterDataVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MeterDataHisDao {

    private static Logger log = Logger.getLogger(MeterDataHisDao.class);
//    ConnectionPool pool = new ConnectionPoolImpl();

    public enum HisType{
        Month,
        Day
    }

    /**
     * 查询数据列表
     * @return
     * @throws Exception
     */
    public ArrayList<MeterDataVo> queryMeterData(String meterId, HisType hisType){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        int type = 1;
        if(hisType == HisType.Day){
            type = 2;
        }

        ArrayList<MeterDataVo> arrayList = new ArrayList();
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("select * from meter_data_his where meter_id = ? and type_id = ? order by create_date desc");
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1,meterId);
            ps.setInt(2,type);
            rs = ps.executeQuery();
            while (rs.next()) {
                MeterDataVo meterDataVo = new MeterDataVo();
                meterDataVo.setId(rs.getString(1));
                meterDataVo.setMeterId(rs.getString(2));
                meterDataVo.setFlow(rs.getFloat(3));
                meterDataVo.setDataTime(TimeTag.getStringDate(rs.getTimestamp(4)));
                meterDataVo.setCreateDate(TimeTag.getStringDate(rs.getTimestamp(5)));
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
//                    pool.releaseConnection(conn);
                    conn.close();
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
     * 保存历史数据
     * @param vo
     * @return
     * @throws Exception
     */
    public int saveMeterData(MeterDataVo vo, HisType hisType) throws Exception{
        log.info("MeterDataDao saveMeterData(MeterDataVo vo) 方法开始处理...");

        int type = 0;
        if(hisType == HisType.Day){
            type = 2;
        }else if(hisType == HisType.Month){
            type = 1;
        }


        Connection conn = null;
        PreparedStatement ps = null;
        try{
//            conn = pool.getConnection();
            conn = DbcpProvider.getDataSource().getConnection();
            StringBuffer sql = new StringBuffer();
            sql.append("insert into meter_data_his (id, meter_id, flow, type_id, data_date, create_date)");
            sql.append("values(?,?,?,?,?,?)");
            ps = conn.prepareStatement(sql.toString());
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getMeterId());
            ps.setFloat(3, vo.getFlow());
            ps.setInt(4, type);
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
//                    pool.releaseConnection(conn);
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e) {
                log.error(e);
            }
        }
    }
}
