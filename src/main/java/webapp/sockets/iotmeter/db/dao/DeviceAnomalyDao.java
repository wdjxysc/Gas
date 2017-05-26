package webapp.sockets.iotmeter.db.dao;


import org.apache.log4j.Logger;
import webapp.db.ConnectionPool;
import webapp.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.DeviceAnomalyVo;
import webapp.sockets.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author Administrator
 *
 */
public class DeviceAnomalyDao {
	private static Logger log = Logger.getLogger(DeviceAnomalyDao.class);
	ConnectionPool pool = new ConnectionPoolImpl();
	
	/**
	 * 保存设备异常
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public int saveDeviceNomaly(DeviceAnomalyVo vo) throws Exception{
		log.info("DeviceAnomalyDao saveDeviceAnomaly(DeviceAnomalyVo vo) 方法开始处理...");
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = pool.getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("insert into t_p_device_nomaly(id,dev_id,happen_date,happen_time,nomaly_type,");
			sql.append("create_name,create_date,update_name,update_date,delete_status) values(?,?,?,");
			sql.append("?,?,?,?,?,?,?)");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, vo.getId());
			ps.setString(2, vo.getDevId());
			ps.setString(3, vo.getHappenDate());
			ps.setString(4, vo.getHappenTime());
			ps.setString(5, vo.getNomalyType());
			ps.setString(6, vo.getCreateName());
			ps.setString(7, TimeTag.getStringDate());
			ps.setString(8, vo.getUpdateName());
			ps.setString(9, TimeTag.getStringDate());
			ps.setString(10, vo.getDeleteStatus());
			
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
