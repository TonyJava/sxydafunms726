package testjava.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class IterableTest {
	private final Log logger = LogFactory.getLog(Generator.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void testIterator(){
		for(List list:new Generator(3) ){
			logger.info(list);
		}
	}
}

class Generator implements Iterable<List>,Iterator<List>{

	private final Log logger = LogFactory.getLog(Generator.class);
	private final Class[] types = {ArrayList.class,LinkedList.class,Stack.class};
	private Random rand = new Random(47);
	private int size;
	public Generator(){
		
	}
	public Generator(int size){
		this.size = size;
	}

	@Override
	public Iterator<List> iterator() {
		// TODO Auto-generated method stub
		logger.info("iterating");
		return this;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		logger.info("hasNext");
		return size>0;
	}

	@Override
	public List next() {
		// TODO Auto-generated method stub
		try {
			logger.info("next");
			size--;
			return (List)types[rand.nextInt(types.length)].newInstance();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}