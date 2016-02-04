package testjava;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnumsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		for(int i = 0;i<10;i++)
			System.out.println(Enums.random(EnumsTest.class));
	}

}

class Enums{
	static Random rand = new Random(47);
	static <T extends Enum<T>> T random(Class<T> c){
		return random(c.getEnumConstants());
	}
	static <T>  T random(T[] value){
		return value[rand.nextInt(value.length)];
	}
}
enum Activity{
	BASKETBALL,FOOTBALL,SITTING,STANDING,HOPPING,RUNNING,FLYING;
}