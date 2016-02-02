package testjava.lang;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnboundedWildcardTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		UnboundedWildcard.assign1(new ArrayList());
		UnboundedWildcard.assign2(new ArrayList());
		UnboundedWildcard.assign3(new ArrayList());
		
		UnboundedWildcard.assign1(new ArrayList<String>());
		UnboundedWildcard.assign2(new ArrayList<String>());
		UnboundedWildcard.assign3(new ArrayList<String>());
		
		UnboundedWildcard.assign3(new ArrayList<String>());

		
		List<?> wildList = new ArrayList();
		wildList = new ArrayList<String>();
		UnboundedWildcard.assign1(wildList);
		UnboundedWildcard.assign2(wildList);
		UnboundedWildcard.assign3(wildList);
		new ArrayList<?>();
	}

}

class UnboundedWildcard{
	static List list1;
	static List<?> list2;
	static List<? extends Object> list3;
	
	static void assign1(List list){
		list1 = list;
		list2 = list;
		list3 = list;
	}
	
	static void assign2(List<?> list){

		list1 = list;
		list2 = list;
		list3 = list;
	
	}
	
	static void assign3(List<? extends Object> list){

		list1 = list;
		list2 = list;
		list3 = list;
	
	}
}