package testjava.lang;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;


public class ArrayMakerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	private final Log logger = LogFactory.getLog(ArrayMakerTest.class);

	@Test
	public void test() {
		ArrayMaker<String> sm = new ArrayMaker<String>(Boolean.class);
		String[] sa = sm.create(10);
		logger.info(Arrays.toString(sa));;
		
		ListMaker<String> lm = new ListMaker<String>();
		List<String> ls = lm.create();
	}

}

class ArrayMaker<T> {
	private Class<T> kind;
	public ArrayMaker(Class<T> kind){
		this.kind = kind;
	}
	public T[] create(int number){
//		return (T[])Array.newInstance(kind, number);
		return new T[number];
	}
}

class ListMaker<T> {
	List<T> create(){
		return new ArrayList();
	}
}