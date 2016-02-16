package com.afunms.config.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.afunms.config.model.Business;

public class GenericBusinessDaoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testFindByID() {
		GenericBusinessDao <Business> dao = new GenericBusinessDao(Business.class);
		Business business = dao.findByID("1");
		System.out.println(business);
	}

	@Test
	public void testLoadAll() {
		GenericBusinessDao <Business> dao = new GenericBusinessDao(Business.class);
		List<Business> list = dao.loadAll();
		for(Business business : list)
			System.out.println(business);
	}

	@Test
	public void testSave(){

		GenericBusinessDao <Business> dao = new GenericBusinessDao(Business.class);
		
		dao.save(new Business());
	
	}
}
