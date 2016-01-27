package org.apache.shiro.web.filter.mgt;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultFilterTest {
	private final Log logger = LogFactory.getLog(DefaultFilterTest.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		   for (DefaultFilter defaultFilter : DefaultFilter.values()) {
	            logger.info(defaultFilter.name());
	        }
	}

}
