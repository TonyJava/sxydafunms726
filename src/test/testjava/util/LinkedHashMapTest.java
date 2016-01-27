package testjava.util;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LinkedHashMapTest {

	private final Log logger = LogFactory.getLog(LinkedHashMapTest.class);
	static Map<String,String> linkedMap = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		linkedMap = new LinkedHashMap();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public void testPut() {
		linkedMap.put("1","a");
		linkedMap.put("2","a");
		linkedMap.put("3","a");
		linkedMap.put("1","b");

	}
	@Test
	public void testGet() {
		testPut();
		for(String key:linkedMap.keySet()){
			logger.info(key+"="+linkedMap.get(key));
		}
	}


}
