package webapp.test;

import org.apache.commons.dbcp.BasicDataSource;
import webapp.dbcp.DbcpProvider;
import webapp.sockets.concentrateor.dao.vo.MeterDataVo;
import webapp.sockets.util.TimeTag;

import java.sql.*;

/**
 * Created by Administrator on 2017/6/10.
 */
public class DBCPTest {
    private static String url = "jdbc:mysql://localhost:3306/meterdb";
    private static String user = "root";
    private static String password = "root";
    private static String classDriver = "com.mysql.jdbc.Driver";
    private static BasicDataSource ds;

    public static void main(String[] args) {



        ds = new BasicDataSource();

        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(classDriver);

        ds.setInitialSize(5);
        ds.setMaxActive(8);
        ds.setMaxWait(3000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlThread();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlThread();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlThread();
            }
        }).start();
    }

    private static void SqlThread(){
        for (int i = 0; i < 10000; i++) {
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = ds.getConnection();
                System.out.println(conn.hashCode() + "=====" +i);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM meter_data");
                rs = ps.executeQuery();
                while (rs.next()) {
                    MeterDataVo meterDataVo = new MeterDataVo();
                    meterDataVo.setId(rs.getString(1));
                    meterDataVo.setMeterId(rs.getString(2));
                    meterDataVo.setFlow(rs.getFloat(3));
                    meterDataVo.setValveState(rs.getInt(4));
                    meterDataVo.setDataTime(TimeTag.getStringDate(rs.getTimestamp(5)));
                    meterDataVo.setCreateDate(TimeTag.getStringDate(rs.getTimestamp(6)));

                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
