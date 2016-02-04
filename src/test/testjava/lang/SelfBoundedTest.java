package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SelfBoundedTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		
		A a = new A();
		a.set(new A());
		SelfBoundingMethods.f(new A());
		SelfBoundingMethods.f(new C());
		SelfBoundingMethods.f(new C());	
		SelfBoundingMethods.f(new D());


		}

}

class SelfBounded<T extends SelfBounded<T>>{
	T element;
	SelfBounded<T> set(T arg){
		element = arg;
		return this;
	}
	
	T get(){
		return element;
	}
}

class A extends SelfBounded<A>{
	
}
class B extends SelfBounded<A>{
	
}
class C extends SelfBounded<C>{
	
}
class D{
	
}
/*class E extends SelfBounded<D>{
	
}*/

class SelfBoundingMethods{
	static <T extends SelfBounded<T>> T f(T arg){
		return arg.set(arg).get();
	}
}