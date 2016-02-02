package testjava.lang;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SuperTypeWildcardTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
//		SuperTypeWildcard.writeTo(new ArrayList<Jonathan>());
		GenericWritting.f1();
		GenericWritting.f2();
	}

}

class SuperTypeWildcard{
	static void writeTo(List<? super Apple> apples){
		apples.add(new Apple());
		apples.add(new Jonathan());
	}
}
class GenericWritting{
	static <T> void writeExact(List<T> list,T item){
		list.add(item);
	}
	
	static <T> void writeWithWildcard(List<? super T> list,T item){
		
		list.add(item);
	}
	static List<Apple> apples = new ArrayList();
	static List<Fruit> fruits = new ArrayList<Fruit>();
	static void f1(){
		writeExact(apples,new Apple());
		writeExact(fruits,new Apple());
		writeExact(apples,new Fruit());]
				writeExact(apples,new Jonathan());
	}
	
	static void f2(){
		writeWithWildcard(apples,new Apple());
		writeWithWildcard(fruits,new Apple());
	}
}