package com.afunms.common.util;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class CreateTableManagerTest {

	private static DBManager conn = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 conn = new DBManager("phoenix");
	}
	@Test
	public void testCreateRootTable() {
		new CreateTableManager().createRootTable(null, "ip", "_127_0_0_2");
	}
	
    @Test
	public void testCreateHbaseTable(){
    	
		CreateTableManager ctable = new CreateTableManager();
		String allipstr = "127_0_0_1";
		ctable.createTable(conn,"pinghour",allipstr,"pinghour");
		ctable.createTable(conn,"pingday",allipstr,"pingday");
		
	
		ctable.createTable(conn,"prohour",allipstr,"prohour");//进程小时
		ctable.createTable(conn,"proday",allipstr,"proday");//进程天
		

		ctable.createTable(conn,"memoryhour",allipstr,"memhour");//内存
		ctable.createTable(conn,"memoryday",allipstr,"memday");//内存
		
		ctable.createTable(conn,"cpuhour",allipstr,"cpuhour");//CPU
		ctable.createTable(conn,"cpuday",allipstr,"cpuday");//CPU
		
		ctable.createTable(conn,"cpudtlhour",allipstr,"cpudtlhour");
		ctable.createTable(conn,"cpudtlday",allipstr,"cpudtlday");
		
		ctable.createTable(conn,"diskhour",allipstr,"diskhour");
		ctable.createTable(conn,"diskday",allipstr,"diskday");
		
		ctable.createTable(conn,"diskincrehour",allipstr,"diskincrehour");//磁盘增长率小时
		ctable.createTable(conn,"diskincreday",allipstr,"diskincreday");//磁盘增长率天 

		ctable.createTable(conn,"hdxperchour",allipstr,"hdperchour");
		ctable.createTable(conn,"hdxpercday",allipstr,"hdpercday");	
		

		ctable.createTable(conn,"utilhdxhour",allipstr,"hdxhour");
		ctable.createTable(conn,"utilhdxday",allipstr,"hdxday");	
		
		ctable.createTable(conn,"allutilhdxhour",allipstr,"allhdxhour");
		ctable.createTable(conn,"allutilhdxday",allipstr,"allhdxday");
		
		ctable.createTable(conn,"dcardperchour",allipstr,"dcardperchour");
		ctable.createTable(conn,"dcardpercday",allipstr,"dcardpercday");
		
		ctable.createTable(conn,"errperchour",allipstr,"errperchour");
		ctable.createTable(conn,"errpercday",allipstr,"errpercday");
		
		ctable.createTable(conn,"packshour",allipstr,"packshour");
		ctable.createTable(conn,"packsday",allipstr,"packsday");
		
		ctable.createTable(conn,"inpackshour",allipstr,"inpackshour");
		ctable.createTable(conn,"inpacksday",allipstr,"inpacksday");
		
		ctable.createTable(conn,"outpackshour",allipstr,"outpackshour");
		ctable.createTable(conn,"outpacksday",allipstr,"outpacksday");
		
		conn.executeBatch();
	}
}
