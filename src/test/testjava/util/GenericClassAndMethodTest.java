package testjava.util;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenericClassAndMethodTest {
	private final Log logger = LogFactory.getLog(Generators.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		GeneratorInt gg = BasicGenerator.create();
		GeneratorInt<CountedObject> g = BasicGenerator.create(CountedObject.class);
		
		for(int i = 0;i<5;i++){
			logger.info(g.next());
		}
	}

}
class CountedObject{
	private static int count = 0;
	private final int id = count++;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CountedObject [id=" + id + "]";
	}
	
}
class BasicGenerator<T1> implements GeneratorInt<T1>{

	private Class<T1> type;
	public BasicGenerator(){
		
	}
	public  BasicGenerator(Class<T1> type) {
		this.type = type;
	}
	@Override
	public T1 next() {
		// TODO Auto-generated method stub
		try {
			return (T1)type.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}  
	}
	public static <K> GeneratorInt<K> create(){
		return new BasicGenerator<K>();
	}
	public static <E> GeneratorInt<E> create(Class<E> type){
		return new BasicGenerator<E>(type);
	}
}