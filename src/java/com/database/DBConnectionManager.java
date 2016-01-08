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
 * ���ݿ⽨������ʵ����
 * 
 * @author itims
 *
 */

public class DBConnectionManager {

	private String namedConfig = null;

	/**
	 * ȡ�����ݿ���������
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
		// û����ȷָ������ȥȡĬ�����ݿ����ӳ��е�����
		if (namedConfig == null || "".equals(namedConfig))
			namedConfig = DEFAULT_NAMED_CONFIG;
		synchronized(namedConnectionPoolMap){
			 cpds = namedConnectionPoolMap.get(namedConfig);
			if (cpds == null) {
				String Validate = SystemConfig.getConfigInfomation(
						"SystemConfigResources", "Validate");// ÿ��������֤�����Ƿ����

					// û��ָ�������ݿ����ӳأ��򴴽���Ӧ�����ӳ�
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
	 * ��ȡ�������ӳ�
	 * 
	 * @return
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			if (cpds == null) {
				init();
			}
/*			logger.info(namedConfig+"------���ӳ���������=" 
					+ cpds.getNumConnectionsDefaultUser()
					+ "------------����ʹ�õ���������="
					+ cpds.getNumBusyConnectionsDefaultUser() 
					+ "-----------û��ʹ�õ���������="
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
	 * �ر����ݿ����ӣ��ر�����Դ��
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
		ResultSet rs = stmt.executeQuery(SQL);// ���ݿ��ѯ
		boolean flag = false;

		while (rs.next()) {
			// ����м�¼����ִ�д˶δ���
			// �û��ǺϷ��ģ����Ե�½
			flag = true;

		}
		db.release();

	}

}