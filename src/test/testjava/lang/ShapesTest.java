package testjava.lang;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShapesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		List<Shape> list = Arrays.asList(new Circle(),new Triangle(),new Square());
		for(Shape s : list)
			s.draw();
	}

}


abstract class Shape{
	final Log logger = LogFactory.getLog(Shape.class);
	void draw(){
		logger.info(this+".draw()");;
	}
	public abstract String toString();
}

class Circle extends Shape{

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Circle";
	}
	
}
class Triangle extends Shape{

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Triangle";
	}
	
}
class Square extends Shape{

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Square";
	}
	
}