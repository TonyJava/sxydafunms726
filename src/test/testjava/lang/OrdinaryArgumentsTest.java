package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OrdinaryArgumentsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Base b = new Base();
		Derived d = new Derived();
		
		DerivedSetter ds = new DerivedSetter();
		ds.set(b);
		ds.set(d);
		Tester<Tester2> t = new Tester();
	}

}

class OrdinarySetter{
	void set(Base o){
		System.out.println("OrdinarySetter.set(Base )");
	}
}
class DerivedSetter extends OrdinarySetter{
	void set(Derived d){
		System.out.println("DerivedSetter.set(Derived )");
	}
}
interface SelfBoundSetter<T extends SelfBoundSetter<T>>{
	void set(T arg);
}
interface Setter extends SelfBoundSetter<Setter>{
	void set(Setter s);
}

class Tester<T extends Tester<T>>{
	T element;
	Tester(T e){
		element = e;
	}
	T get(){
		System.out.println("Tester");
		return element;
	}
	void set(T t){
		System.out.println("Tester2");
	}
}
class Tester2 extends Tester<Tester2>{
	Tester2(Tester2 e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	Tester2 get(){
		System.out.println("Tester2");

		return element;
	}
	void set(Tester t ){
		
	}
}