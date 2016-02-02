package testjava.lang;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

 

public class GenericReadingTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		GenericReading.f1();
		GenericReading.f2();
		GenericReading.f3();
	}

}

class GenericReading{
	static <T> T readExact(List<T> list){
		return list.get(0);
	}
	static List<Apple> apples = Arrays.asList(new Apple());
	static List<Fruit> fruits = Arrays.asList(new Fruit()	);
	
	static void f1(){
		  Apple a = readExact(apples);
		  Fruit f = readExact(fruits);
		  f = readExact(apples);
	}
	
	static void f2(){
		Reader<Fruit> fruitReader = new Reader<Fruit>();
		Fruit f = fruitReader.readerExact(fruits);
	}
	
	static void f3(){
		CovariantReader<Fruit> covariantReader = new CovariantReader<Fruit>();
		Fruit f = covariantReader.readCovariant(apples);
		Fruit a = covariantReader.readCovariant(apples);
	}
	static class Reader<T>{
		T readerExact(List<T> list){
			return list.get(0);
		}
	}
	
	static class CovariantReader<T>{
		 T readCovariant(List<? extends T> list){
			return list.get(0);
		}
	}
	
}