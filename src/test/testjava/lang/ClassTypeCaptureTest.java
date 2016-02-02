package testjava.lang;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClassTypeCaptureTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	private final Log logger = LogFactory.getLog(ArrayMakerTest.class);
	@Test
	public void test() {
		ClassTypeCapture<Building> ctc = new ClassTypeCapture<Building>(Building.class);
		logger.info(ctc.isInstance(new Building()));;
		logger.info(ctc.isInstance(new House()));
	}

}
class Building{}
class House extends Building{
	
}
class ClassTypeCapture<T>{
	Class<T> kind;
	public ClassTypeCapture(Class<T> kind){
		this.kind = kind;
	}
	public boolean isInstance(Object arg){
		return kind.isInstance(arg);
	}
}