package webapp.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
	private String JDBC_DRIVER;
	private String DB_URL;
	private String DB_USER;
	private String DB_PASSWORD;

	private static Logger log = Logger.getLogger(ConnectionProvider.class);
	
	public ConnectionProvider(){
		JDBC_DRIVER = PropertyReader.get("driverClassName");
		DB_URL = PropertyReader.get("url");
		DB_USER = PropertyReader.get("username");
		DB_PASSWORD = PropertyReader.get("password");
		
		try{
			Class<?> jdbcDriver = Class.forName(JDBC_DRIVER);
			DriverManager.registerDriver((Driver)jdbcDriver.newInstance());
		}
		catch(Exception e){
			e.printStackTrace();
			log.info("异常" + e.getMessage());
		}
	}
	
	public Connection getConnection() throws SQLException{
		Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		return con;
	}
}
