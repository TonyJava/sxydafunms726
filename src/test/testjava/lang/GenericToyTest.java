package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenericToyTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Class<FancyToy> ftClass = FancyToy.class;
		try {
			FancyToy ftToy = ftClass.newInstance();
			Class<? super FancyToy> superFtClass = ftClass.getSuperclass();
			superFtClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class Toy{
	
}
class FancyToy extends Toy{
	
}