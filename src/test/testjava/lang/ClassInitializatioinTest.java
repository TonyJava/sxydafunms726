package testjava.lang;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClassInitializatioinTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		System.out.println("1--------------");
		Class initable = Initable.class;
		System.out.println("2--------------");

		System.out.println(Initable.STATIC_FINAL);
		System.out.println("3--------------");

		System.out.println(Initable.STATIC_FINAL2);
		System.out.println("4--------------");

		System.out.println(Initable2.noFinal);
		System.out.println("5--------------");

		System.out.println(Initable3.noFinal);

	}

}

class Initable{
	static final int STATIC_FINAL = 74;
	static final int STATIC_FINAL2 = ClassInitialization.rand.nextInt(1000);
	static{
		System.out.println("Initial Initable");
	}
}
class Initable2{
	static int noFinal = 147;
	static{
		System.out.println("Initial Initable2");
	}
}
class Initable3{
	static int noFinal = 47;
	static{
		System.out.println("Initial Initable3");
	}
}

class ClassInitialization{
	static Random rand = new Random(47);
	static{
		System.out.println("Initial ClassInitialization");
	}
}