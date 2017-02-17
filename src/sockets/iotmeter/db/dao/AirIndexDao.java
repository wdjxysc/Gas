package sockets.iotmeter.db.dao;


import org.apache.log4j.Logger;
import sockets.iotmeter.db.ConnectionPool;
import sockets.iotmeter.db.ConnectionPoolImpl2;
import sockets.iotmeter.db.vo.AirIndexVo;
import sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Administrator
 *
 */
public class AirIndexDao {
	private static Logger log = Logger.getLogger(AirIndexDao.class);
	ConnectionPool pool = new ConnectionPoolImpl2();
	
	/**
	 * 保存空气指数
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int saveAirIndex(AirIndexVo vo) throws Exception{
		log.info("AirIndexDao saveAirIndex(AirIndexVo vo) 方法开始处理...");
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = pool.getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("insert into t_p_air_index (id,dev_no,aqi,pm25,so2,o3,co,pm10,tvoc,hcho,temperature,");
			sql.append("humidity,type,rssi,create_name,create_date,update_name,update_date,delete_status) ");
			sql.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, vo.getId());
			ps.setString(2, vo.getDevNo());
			ps.setString(3, vo.getAqi());
			ps.setString(4, vo.getPm25());
			ps.setString(5, vo.getSo2());
			ps.setString(6, vo.getO3());
			ps.setString(7, vo.getCo());
			ps.setString(8, vo.getPm10());
			ps.setString(9, vo.getTvoc());
			ps.setString(10, vo.getHcho());
			ps.setString(11, vo.getTemperature());
			ps.setString(12, vo.getHumidity());
			ps.setString(13, vo.getType());
			ps.setString(14, vo.getRssi());
			ps.setString(15, vo.getCreateName());
			ps.setString(16, TimeTag.getStringDate());
			ps.setString(17, vo.getUpdateName());
			ps.setString(18, TimeTag.getStringDate());
			ps.setString(19, vo.getDeleteStatus());
			
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
