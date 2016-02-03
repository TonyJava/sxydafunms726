package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenericClassReferenceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Class intClass = int.class;
		Class<Integer> genericIntClass = Integer.class;
		genericIntClass = int.class;
		intClass = double.class;
		genericIntClass = double.class;
		Class<Number> genericNumClass = int.class;
		Class<? extends Number> boundedWildcardNumClass = int.class;
		boundedWildcardNumClass = double.class;
	}

}
