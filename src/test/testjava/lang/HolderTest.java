package testjava.lang;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HolderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Holder<Apple> h = new Holder<Apple>(new Apple());
		Apple a =h.get();
		h.set(new Apple());
		
		Holder<? extends Fruit> hf = h;
		a = (Apple)hf.get();
		hf.set(new Apple());
		hf.equals(new Apple());
	}

}

class Holder<T>{
	T value;
	Holder(){}
	Holder(T value){
		this.value = value;
	}
	T get(){
		return value;
	}
	void set(T value){
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o ){
		return this.value.equals(o);
	}
}