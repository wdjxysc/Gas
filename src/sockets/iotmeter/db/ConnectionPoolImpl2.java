package sockets.iotmeter.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectionPoolImpl2 implements ConnectionPool{
	private ConnectionProvider provider = new ConnectionProvider();
	private final ArrayList<Connection> pool = new ArrayList<Connection>();
	private int poolSize = 15;
	
	public ConnectionPoolImpl2() {
	}
	
	public ConnectionPoolImpl2(int poolSize){
		this.poolSize = poolSize;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		synchronized (pool) {
			if(!pool.isEmpty()){
				int last = pool.size()-1;
				Connection con = pool.remove(last);
				return con;
			}
		}
		
		Connection con = provider.getConnection();
		return getProxy(con, this);
	}

	@Override
	public void releaseConnection(Connection con) throws SQLException {
		synchronized (pool) {
			int currentSize = pool.size();
			if(currentSize<poolSize){
				pool.add(con);
				return;
			}
		}
		try{
			closeJdbcConnection(con);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		Iterator<Connection> iter = pool.iterator();
		while(iter.hasNext()){
			try{
				closeJdbcConnection(iter.next());
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	private void closeJdbcConnection(Connection con) throws SQLException{
		ConnectionP conP = (ConnectionP)con;
		conP.getJdbcConnection();
	}
	
	protected void finalize(){
		close();
	}

	/**
	 * 返回动态代理
	 * @param con
	 * @param pool
	 * @return
	 */
	private Connection getProxy(final Connection con,final ConnectionPool pool){
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(method.getName().equals("close")){
					pool.releaseConnection((Connection)proxy);
					return null;
				}
				else if(method.getName().equals("getJdbcConnection")){
					return con;
				}
				else{
					//调用被代理的Connection对象的相应的方法
					return method.invoke(con, args);
				}
			}
		};
		
		return (Connection)Proxy.newProxyInstance(ConnectionP.class.getClassLoader(), 
				new Class[]{ConnectionP.class}, handler);
	}
	
	interface ConnectionP extends Connection{
		public Connection getJdbcConnection();
	}
}
