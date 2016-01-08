package com.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.antlr.grammar.v3.ANTLRParser.finallyClause_return;

import com.afunms.common.util.logging.Log;
import com.afunms.common.util.logging.LogFactory;
import com.database.config.*;
import com.mchange.v2.c3p0.*;

/**
 * 
 * 
 * 数据库建立管理实现类
 * 
 * @author itims
 *
 */

public class DBConnectionManager {

	private String namedConfig = null;

	/**
	 * 取得数据库类型名称
	 * @return the namedConfig
	 */
	public String getNamedConfig() {
		return namedConfig;
	}


	public DBConnectionManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DBConnectionManager(String namedConfig) {
		super();
		this.namedConfig = namedConfig;
	}

	private ComboPooledDataSource cpds = null;
	private static final String DEFAULT_NAMED_CONFIG = "mysql";
	private static Map<String, ComboPooledDataSource> namedConnectionPoolMap = new HashMap<String, ComboPooledDataSource>();

	private void init() {
		// 没有明确指定，则去取默认数据库连接池中的连接
		if (namedConfig == null || "".equals(namedConfig))
			namedConfig = DEFAULT_NAMED_CONFIG;
		synchronized(namedConnectionPoolMap){
			 cpds = namedConnectionPoolMap.get(namedConfig);
			if (cpds == null) {
				String Validate = SystemConfig.getConfigInfomation(
						"SystemConfigResources", "Validate");// 每次连接验证连接是否可用

					// 没有指定的数据库连接池，则创建相应的连接池
					cpds = new ComboPooledDataSource(namedConfig);
					namedConnectionPoolMap.put(namedConfig, cpds);
					logger.info("c3p0.driverClass=" + cpds.getDriverClass());
					logger.info("c3p0.jdbcUrl=" + cpds.getJdbcUrl());
					logger.info("c3p0.user=" + cpds.getUser());
					logger.info("c3p0.password=" + cpds.getPassword());
			}
		}
		

	}

	private static Log logger = LogFactory.getLog(DBConnectionManager.class);

	/**
	 * 获取数据连接池
	 * 
	 * @return
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			if (cpds == null) {
				init();
			}
/*			logger.info(namedConfig+"------连接池连接数量=" 
					+ cpds.getNumConnectionsDefaultUser()
					+ "------------正在使用的连接数量="
					+ cpds.getNumBusyConnectionsDefaultUser() 
					+ "-----------没有使用的连接数量="
					+ cpds.getNumIdleConnectionsDefaultUser() + "------");
*/			
			con = cpds.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return con;
	}

	/**
	 * 
	 * 关闭数据库连接（关闭数据源）
	 * 
	 */
	private  void release() {
		try {
			if (cpds != null) {
				cpds.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String arg[]) throws SQLException {

		DBConnectionManager db = new DBConnectionManager();
		db.getConnection();
		String SQL = "select * from system_user";
		Connection con = null;
		con = db.getConnection();
		// PreparedStatement pstmt = null;
		// pstmt = con.prepareStatement(sql);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);// 数据库查询
		boolean flag = false;

		while (rs.next()) {
			// 如果有记录，则执行此段代码
			// 用户是合法的，可以登陆
			flag = true;

		}
		db.release();

	}

}