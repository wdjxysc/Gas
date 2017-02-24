package webapp.sockets.iotmeter.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
	/**
	 * 从连接池中取出连接
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;
	
	/**
	 * 把连接放回连接池
	 * @param con
	 * @throws SQLException
	 */
	public void releaseConnection(Connection con) throws SQLException;
	
	/**
	 * 关闭连接池
	 */
	public void close();
}
