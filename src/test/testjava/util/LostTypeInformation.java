package testjava.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class LostTypeInformation {

	private final Log logger = LogFactory.getLog(LostTypeInformation.class);
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		List<J>	list = new ArrayList<J>();
		Map<J,Q> map = new HashMap<J,Q>();
		K<Q> k = new K<Q>();
		logger.info(Arrays.asList(list.getClass().getTypeParameters()));
		logger.info(Arrays.asList(map.getClass().getTypeParameters()));
		logger.info(Arrays.asList(k.getClass().getTypeParameters()));
	}

}
class J{}
class Q{
	
}
class K<T>{
	
}