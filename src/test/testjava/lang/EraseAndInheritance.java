/**
 * 
 */
package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author HP
 *
 */
public class EraseAndInheritance {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Derived2  d2 = new Derived2();
		Object obj = d2.get();
		d2.set(obj);
		Derived1 d1  = new Derived1();
		GenericBase g = new GenericBase();
		g.set("");;
		String[] s = new Object[9];
	}

}
class GenericBase<T>{
	T e;
	public void set(T e){
		this.e = e;
	}
	public T get(){
		return e;
	}
}

class Derived1<T> extends GenericBase<T>{
	
}

class Derived2 extends GenericBase{
	
}
class Derived3 extends GenericBase<?>{
	
}