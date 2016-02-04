package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CovariantReturnTypesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		DerivedGetterI d = new DerivedGetterI(){

			@Override
			public Derived get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(Base b) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void set(Derived arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
		Derived de = d.get();
	}

}
class Base{
	
}
class Derived extends Base{
	
}

interface OrdinaryGetterI{
	Base get();
	void set(Base b);
}
interface DerivedGetterI extends OrdinaryGetterI{
	Derived get();
	void set(Derived d);
	
}