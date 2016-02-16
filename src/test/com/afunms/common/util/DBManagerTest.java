package com.afunms.common.util;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.afunms.config.model.Business;

public class DBManagerTest {

	private final Log logger = LogFactory.getLog(DBManagerTest.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testExecuteQuery() {
		DBManager dbm = new DBManager();
		ResultSet rs = dbm.executeQuery("select * from system_business where id =1");
		ResultSetHandler<Business> handler = new BeanHandler<Business>(Business.class);
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i = 1;i<columnCount;i++){
				logger.info("catalogName = "+rsmd.getCatalogName(i)+"	ColumnClassName="+rsmd.getColumnClassName(i)+"	 ColumnName="+rsmd.getColumnName(i)+" 		ColumnType="+rsmd.getColumnType(i)+"	ColumnTypeName="+rsmd.getColumnTypeName(i));
			}
			Business business = handler.handle(rs);
			System.out.println(business);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
