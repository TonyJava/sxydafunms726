package testjava.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public  class ArrayListTest {
	private static final Log logger = LogFactory.getLog(ArrayListTest.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAddE() {/*
		List<? extends A> list = new ArrayList();
		list.add(new A());
		list.add(new C());
	*/}
	@Test
	public void testClassTruple(){
		Truple t = new Truple(0,1);
//		t.setKey("A");
		logger.info(t.getKey()+"="+t.getValue());
	}

	public static void main(String[] args){
		RandomList<String> randomList = new RandomList<String>();
		for(String s:"xx dd dd gg dd".split(" ")){
			randomList.add(s);
			
		}
		logger.info(randomList.select());
	}
}
class A<E>{
	E a;
	public A(){
		
	}
	public A(E a){
		this.a = a;
	}
	public E getA(){
		return this.a;
	}
}
class B<E> extends A<E>{}
class C<E> extends A<E>{}

class Truple<K,V>{
	private K key;
	private V value;
	public Truple(){}
	public Truple(K key,V value){
		this.key = key;
		this.value = value;
	}
	public Truple(K key){
		this.key = key;
	}
	
	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}
	
}
class ThreeTruple<K,V,V2> extends Truple<K,V>{
	private V2 value2;
	public ThreeTruple(K key,V value,V2 value2){
		super(key,value);
		this.value2 = value2;
	}
}

 class RandomList<T>{
	List<T> list = new ArrayList<T>();
	Random rand = new Random(47);
	public void add(T item){
		list.add(item);
	}
	public T select(){
		return list.get(rand.nextInt(list.size()));
	}
	
}