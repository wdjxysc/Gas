package webapp.dbcp;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by Administrator on 2017/6/13.
 */
public class DbcpProvider {
    private static Logger logger = Logger.getLogger(DbcpProvider.class);
    private static DataSource ds;

    private static void CreateDataSource (){
        try {
            InputStream in = DbcpProvider.class.getResourceAsStream("/dbcp.properties");
            Properties props = new Properties();
            props.load(in);
            ds = BasicDataSourceFactory.createDataSource(props);
            logger.info("创建数据源成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource(){
        if(ds == null){
            CreateDataSource();
        }
        return ds;
    }
}
