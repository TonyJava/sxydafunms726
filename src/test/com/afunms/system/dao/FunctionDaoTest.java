package com.afunms.system.dao;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;

public class FunctionDaoTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testFindByRole() {
		FunctionDao dao = new FunctionDao();
		dao.findByRole("4");
	}

}
