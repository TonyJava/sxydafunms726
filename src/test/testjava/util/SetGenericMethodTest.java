package testjava.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



public class SetGenericMethodTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	private  final Log logger = LogFactory.getLog(Sets.class);

	@Test
	public void test() {
		Set<String> a = new HashSet(),b = new HashSet();;
		a.add("1");a.add("2");a.add("3");
		b.add("1");b.add("1");b.add("4");
		logger.info(Sets.union(a, b));
	}

}
class Sets {
	private static final Log logger = LogFactory.getLog(Sets.class);
	public static <T> Set<T> union(Set<T> a,Set<T> b){
		Set<T> set = new HashSet<T>(a);
		logger.info("a="+a);
		logger.info("set="+set);
		set.addAll(b);
		return set;
	}
}
