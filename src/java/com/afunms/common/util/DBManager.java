/**
 * <p>Description:DBManager,used to connect database</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.sql.DataSource;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afunms.initialize.ResourceCenter;
import com.afunms.util.DataGate;
import com.database.DBConnectionManager;


public class DBManager {
	protected Connection conn;
	protected Statement stmt;
	protected PreparedStatement pstmt;
	protected ResultSet rs;
	protected String preparesql;

	/**
	 * 我们默认系统数据库是可以正常连接的，所以不抛出错误
	 */

	public Connection getConn() {
		return conn;
	}

	public DBManager() {
		try {
			init(null);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("Can not connect system DB!", e);
		}
	}

	public DBManager(String namedConfig)  {
		init(namedConfig);
	}

	/**
	 * 初始化
	 */
	public void init(String jndi) {
		// Context initCtx = null;

		// DataSource ds = null;
		try {
			conn = DataGate.getCon(jndi);

			// initCtx = new InitialContext();
			// if("tomcat".equals(ResourceCenter.getInstance().getAppServer()))
			// ds = (DataSource)initCtx.lookup("java:comp/env/" + jndi);
			// //tomcat
			// else
			// ds = (DataSource)initCtx.lookup(jndi); //weblogic
			// conn = ds.getConnection();

			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			// pstmt = conn.prepareStatement(sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// try{
			// initCtx.close();
			// }catch(Exception ex){
			// ex.printStackTrace();
			// }
		}

	}

	/**
	 * 查询
	 */
	public ResultSet executeQuery(String sql) {
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.executeQuery(),SQL=\n" + sql);
		}
		return rs;
	}

	/**
	 * 更新,并提交
	 */
	public void executeUpdate(String sql) {
		// SysLogger.info("executeUpdate(sql)==="+sql);
		executeUpdate(sql, true);
	}

	/**
	 * 更新
	 */
	public void executeUpdate(String sql, boolean bCommit) {

		// SysLogger.info("executeUpdate(sql,boolean)==="+sql);
		try {

			stmt.executeUpdate(sql);

			if (bCommit)
				conn.commit();
		} catch (SQLException se) {
			se.printStackTrace();

			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			SysLogger.error("DBManager.executeUpdate():更新数据出错:\n" + sql, se);
		} finally {

		}
	}

	/**
	 * 提交
	 */
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException se) {
			SysLogger.error("DBManager.commit():", se);
		}
	}

	/**
	 * 回滚
	 */
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException se) {
			SysLogger.error("DBManager.rollback():", se);
		}
	}

	private static Log logger = LogFactory.getLog(DBManager.class);
	/**
	 * 加入批处理
	 */
	public void addBatch(String sql) {
		logger.info(sql);
		try {
			stmt.addBatch(sql);
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!" + sql, se);
		}
	}

	/**
	 * 执行批处理
	 */
	public void executeBatch() {
		try {
			stmt.executeBatch();
			conn.commit();
		} catch (BatchUpdateException bse) {
			bse.printStackTrace();
			SysLogger.error("-------至少有一条SQL语句错误-------", bse);
		} catch (SQLException se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.executeBatch()!", se);
		} catch (Exception se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.executeBatch()!", se);
		} finally {
			try {
				stmt.clearBatch();
			} catch (SQLException xe) {
				xe.printStackTrace();
			}
		}
	}

	/**
	 * 设置批处理SQL
	 */
	public void setPrepareSql(String sql) {
		preparesql = sql;
		try {
			pstmt = conn.prepareStatement(preparesql);
		} catch (SQLException xe) {
			xe.printStackTrace();
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setString(4, (String) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setString(9, (String) list.get(8));
			pstmt.setString(10, (String) list.get(9));
			pstmt.setString(11, (String) list.get(10));
			pstmt.setString(12, (String) list.get(11));
			pstmt.setString(13, (String) list.get(12));
			pstmt.addBatch();
			// SysLogger.info((String)list.get(4)+"====="+(String)list.get(5)+"==="+(String)list.get(6));
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareProcBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setString(4, (String) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setString(9, (String) list.get(8));
			pstmt.setString(10, (String) list.get(9));
			pstmt.setString(11, (String) list.get(10));
			pstmt.addBatch();
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareProcLongBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setString(4, (String) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setLong(9, (Long) list.get(8));
			pstmt.setString(10, (String) list.get(9));
			pstmt.setString(11, (String) list.get(10));
			pstmt.addBatch();
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareSoftwareBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setString(4, (String) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setString(9, (String) list.get(8));
			pstmt.addBatch();
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareServiceBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setString(4, (String) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setString(9, (String) list.get(8));
			pstmt.setString(10, (String) list.get(9));
			pstmt.addBatch();
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 加入批处理
	 */
	public void addPrepareErrptBatch(List list) {
		try {
			pstmt.setString(1, (String) list.get(0));
			pstmt.setString(2, (String) list.get(1));
			pstmt.setString(3, (String) list.get(2));
			pstmt.setInt(4, (Integer) list.get(3));
			pstmt.setString(5, (String) list.get(4));
			pstmt.setString(6, (String) list.get(5));
			pstmt.setString(7, (String) list.get(6));
			pstmt.setString(8, (String) list.get(7));
			pstmt.setString(9, (String) list.get(8));
			pstmt.setString(10, (String) list.get(9));
			pstmt.setString(11, (String) list.get(10));
			pstmt.setString(12, (String) list.get(11));
			pstmt.setString(13, (String) list.get(12));
			pstmt.setString(14, (String) list.get(13));
			pstmt.setString(15, (String) list.get(14));
			pstmt.addBatch();
		} catch (SQLException se) {
			SysLogger.error("Error in DBManager.addBatch()!", se);
		}
	}

	/**
	 * 执行批处理
	 */
	public void executePreparedBatch() {
		try {
			pstmt.executeBatch();
			conn.commit();
		} catch (BatchUpdateException bse) {
			bse.printStackTrace();
			SysLogger.error("-------至少有一条SQL语句错误-------", bse);
		} catch (SQLException se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.executeBatch()!", se);
		} catch (Exception se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.executeBatch()!", se);
		} finally {
			try {
				pstmt.clearBatch();
			} catch (SQLException xe) {
			}
		}
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			try {
				conn.commit();
				stmt.close();
				// DataGate.freeCon(conn);
			} catch (Exception ex) {

			}
			if (conn != null && !conn.isClosed()) {
				// if(conn!=null){
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
			SysLogger.error("Error in DBManager.close()!", se);
		} finally {
			rs = null;
			stmt = null;
			conn = null;
		}
	}

	/**
	 * 
	 * 通过 onekye与twokey组合成一个唯一的key，并把数据库的查询结果 放到hashtable 中;onekey-towkey 为主键
	 * 
	 * @author konglq
	 * @创建时间 2010-8-24 上午12:58:26
	 * @方法名 executeQueryListHashMap
	 * @param sql
	 * @param onekey
	 *            代表一条记录的唯一key
	 * @param twokey
	 *            第二个key
	 * @return
	 * @throws SQLException
	 *             List
	 * @功能如下 执行数据库查询语句返回向量类型的数据,向量的每一个值是哈希表 每个哈希表代表一条记录
	 */
	public Hashtable executeQuerykeytwoListHashMap(String sql, String onekey,
			String twokey) throws SQLException {
		ResultSetMetaData rsmd = null;
		Hashtable list = new Hashtable();
		int columnCount = 0;
		try {
			// logger.info("执行sql如下：" + sql);
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // 得到字段数量
			if (columnCount == 0) {
				return null;
			}

			// 向量的第一个值是列名集合
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // 获得字段名
			}
			// vct.add(keys);
			while (rs.next()) {
				Hashtable hm = new Hashtable();
				hm.clear();
				String key1 = null;
				String key2 = null;
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}

					if (keys[i - 1].equals(onekey)) {
						key1 = result;
					}

					if (keys[i - 1].equals(twokey)) {
						key2 = result;
					}

					hm.put(keys[i - 1], result); // 将每条记录保存到一个哈希表中，key为字段名，result为值
				}
				list.put(key1 + "-" + key2, hm); // 将数据集的每一行插入向量
			}
		} catch (SQLException e) {
			// logger.error("执行sql出现问题：" + sql + e);
			return null;
		}

		return list; // 返回SQL语言的查询结果集。
	}

}
