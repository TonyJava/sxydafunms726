package testjava.util;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TypeEraseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	private final Log logger = LogFactory.getLog(HasF.class);

	@Test
	public void test() {
		HasF f = new HasF();
		Manipulator<HasF> m = new Manipulator<HasF>(f);
		m.manipulate();
		logger.info(Arrays.asList(m.getClass().getTypeParameters()));;
	}

}

class Manipulator<T extends HasF>{
	private T obj;
	public Manipulator(T obj){
		this.obj = obj;
	}
	public void manipulate(){
		obj.f();
	}
}
class HasF{
	private final Log logger = LogFactory.getLog(HasF.class);
	public void f(){
		logger.info("HasF.f()");
	}
}