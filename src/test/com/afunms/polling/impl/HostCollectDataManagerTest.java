package com.afunms.polling.impl;

import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.AfterClass;
import org.junit.Test;

import com.afunms.polling.api.I_HostCollectData;

public class HostCollectDataManagerTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetCategoryStringStringStringStringString() {
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
//			Hashtable cpuhash = hostmanager.getCategory("127.0.0.1", "CPU", "Utilization", "2015-12-01 12:00:00", "2015-12-31 12:00:00");
			Hashtable ConnectUtilizationhash = hostmanager.getCategory("127.0.0.1", "Ping", "ConnectUtilization", "2015-12-01 12:00:00", "2015-12-31 12:00:00");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetMemory(){
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			Hashtable[] memoryhash = hostmanager.getMemory("127.0.0.1", "Memory",  "2015-12-01 12:00:00", "2015-12-31 12:00:00");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
