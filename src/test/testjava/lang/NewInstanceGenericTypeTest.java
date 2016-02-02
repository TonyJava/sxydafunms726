package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NewInstanceGenericTypeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		ClassAsFactory<Employ> fe = new ClassAsFactory<Employ>(Employ.class);
		ClassAsFactory<Integer> fi = new ClassAsFactory<Integer>(Integer.class);
	}

}
class Employ{}
class ClassAsFactory<T>{
	T x;
	
	public ClassAsFactory(Class<T> kind){
		try {
			x = kind.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
