package testjava.lang;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CovariantArraysTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {

		Fruit[] fruit = new Apple[10];
		
		fruit[0] = new Apple();
		fruit[1] = new Jonathan();
		
//		fruit[0] = new Fruit();
//		
//		fruit[0] = new Orange();
//		
		List<? extends Fruit> list = new ArrayList<Apple>();
//		list.add(new Fruit());
//		list.add(new Apple());
		list.add(null);
		Fruit f = list.get(0);
		Apple a =(Apple) list.get(0);
		list.indexOf(new Apple());
		list.contains(new Fruit());
	}

}

class Fruit{}
class Apple extends Fruit{}
class Jonathan extends Apple{
	
}
class Orange extends Fruit{}