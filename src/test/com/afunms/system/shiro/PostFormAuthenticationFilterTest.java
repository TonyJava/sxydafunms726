package com.afunms.system.shiro;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class PostFormAuthenticationFilterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoginMessage() {

		PostFormAuthenticationFilter.LoginMessage msg = new PostFormAuthenticationFilter.LoginMessage(1,"ok");
		System.out.println(msg);
	}

}
