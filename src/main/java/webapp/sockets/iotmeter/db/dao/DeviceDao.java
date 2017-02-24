package webapp.sockets.iotmeter.db.dao;

import org.apache.log4j.Logger;
import webapp.sockets.iotmeter.db.ConnectionPool;
import webapp.sockets.iotmeter.db.ConnectionPoolImpl;
import webapp.sockets.iotmeter.db.vo.DeviceVo;
import webapp.sockets.iotmeter.util.TimeTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceDao {
	private static Logger log = Logger.getLogger(DeviceDao.class);
	ConnectionPool pool = new ConnectionPoolImpl();
	
	/**
	 * 查询设备编号
	 * @param deviceNo
	 * @return
	 * @throws Exception
	 */
	public int queryDeviceNo(String deviceNo){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) from t_p_device where device_no = ?");
			conn = pool.getConnection();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, deviceNo);
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
	public int saveDevice(DeviceVo vo) throws Exception{
		log.info("DeviceDao saveDevice(DeviceVo vo) 方法开始处理...");
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = pool.getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("insert into t_p_device (id,create_name,create_date,update_name,update_date,");
			sql.append("delete_status,device_name,device_no,device_key,client_ip,client_port) ");
			sql.append("values (?,?,?,?,?,?,?,?,?,?,?)");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, vo.getId());
			ps.setString(2, vo.getCreateName());
			ps.setString(3, TimeTag.getStringDate());
			ps.setString(4, vo.getUpdateName());
			ps.setString(5, TimeTag.getStringDate());
			ps.setString(6, vo.getDeleteStatus());
			ps.setString(7, vo.getDeviceName());
			ps.setString(8, vo.getDeviceNo());
			ps.setString(9, vo.getDeviceKey());
			ps.setString(10, vo.getClientIp());
			ps.setInt(11, vo.getClientPort());
			
			
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
	public int updateDevice(DeviceVo vo) throws Exception{
		log.info("DeviceDao updateDevice(DeviceVo vo) 方法开始处理...");
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = pool.getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("update t_p_device set update_date = ?,client_ip=?,client_port=? where device_no = ?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, TimeTag.getStringDate());
			ps.setString(2, vo.getClientIp());
			ps.setInt(3, vo.getClientPort());
			ps.setString(4, vo.getDeviceNo());
			
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
