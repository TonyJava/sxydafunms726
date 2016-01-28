package org.apache.shiro.web.filter.mgt;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultFilterEnumTest {
	private final Log logger = LogFactory.getLog(DefaultFilterEnumTest.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testNewInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFilterClass() {
		List<? extends Filter> filterString =null;

		logger.info(DefaultFilterEnum.anon.filterClass);
	}

	@Test
	public void testCreateInstanceMap() {
		fail("Not yet implemented");
	}
	 
}
