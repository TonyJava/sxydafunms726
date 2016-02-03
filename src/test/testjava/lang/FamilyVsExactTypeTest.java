package testjava.lang;

import static org.junit.Assert.*;
import static java.lang.System.out;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FamilyVsExactTypeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		test(new Base());
		
		test(new Derived());
	}

	private void test(Object x){
		out.println("type of x = "+ x.getClass());
		out.println("Base.isInstance(x)="+Base.class.isInstance(x));
		out.println("Derived.isInstance(x)="+Derived.class.isInstance(x));
		
		out.println("x.getClass() == Base.class = "+(x.getClass() == Base.class));
		out.println("x.getClass() == Derived.class = "+(x.getClass() == Derived.class));
		
		out.println("x.getClass().equals(Base.class) = "+(x.getClass().equals( Base.class)));
		out.println("x.getClass().equals(Derived.class) = "+(x.getClass().equals( Derived.class)));

	}
}

class Base {
	
}
class Derived extends Base {
	
}
