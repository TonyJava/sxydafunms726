package com.afunms.system.dao;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.afunms.system.model.User;

public class UserDaoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadAllByUser() {
	}

	@Test
	public void testFindByUserId() {
		UserDao dao = new UserDao();
		User user = dao.findByUserId("admin");
		System.out.println(user);
	}

}
