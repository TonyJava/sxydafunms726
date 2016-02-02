package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SweetShopTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		new Candy();
		try {
			Class.forName("testjava.lang.Gum");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Cookie();
	}

}

class Candy{
	static{
		System.out.println("loading Candy");
	}
}
class Gum{

	static{
		System.out.println("loading Gum");
	}

}
class Cookie{

	static{
		System.out.println("loading Cookie");
	}

}