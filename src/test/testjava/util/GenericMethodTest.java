package testjava.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenericMethodTest {
	private final Log logger = LogFactory.getLog(Generator.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		GenericMethod g = new GenericMethod();
		g.<String>print("ff");
		g.print(5);
		g.print(true);;
	}

}
class GenericMethod{
	private final Log logger = LogFactory.getLog(GenericMethod.class);

	public  <T> void print(T v){
		logger.info(v);
	}
}
interface GeneratorInt<T>{
	public T next();
}
class G {
	public <T> Collection<T> fill(Collection<T> col,GeneratorInt<T> g,int n){
		for(int i=0;i<n;i++){
			col.add(g.next());
		}
		return col;
	}
}
class Generators implements Iterable<List>,Iterator<List>{

	private final Log logger = LogFactory.getLog(Generators.class);
	private final Class[] types = {ArrayList.class,LinkedList.class,Stack.class};
	public static <E>  Collection<E> fill(Collection<E> col,GeneratorInt<E> g,int n){
		for(int i=0;i<n;i++){
			col.add(g.next());
		}
		return col;
	}
	public int getSize(){
		return types.length;
	}
	private Random rand = new Random(47);
	private int size;
	private int count;
	public Generators(){
		
	}
	public Generators(int size){
		this.size =  this.count = size;
	}

	@Override
	public Iterator<List> iterator() {
		// TODO Auto-generated method stub
		logger.info("iterating");
		return this;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		logger.info("hasNext");
		return count>0;
	}

	@Override
	public List next() {
		// TODO Auto-generated method stub
		try {
			logger.info("next");
			count--;
			return (List)types[rand.nextInt(types.length)].newInstance();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
}