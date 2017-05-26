package webapp.db;

import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件
 * @author Administrator
 *
 */
public class PropertyReader {
	static private Properties ps;
	static{
		ps = new Properties();
		try{
			InputStream in = PropertyReader.class.getResourceAsStream("/mysqldb.properties");
			ps.load(in);
			in.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String get(String key){
		return (String)ps.get(key);
	}
}
