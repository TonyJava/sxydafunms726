package testjava.lang;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicBoundTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		Solid<Bounded> c = new 	Solid<Bounded>(new Bounded());
		c.getColor();
		c.getX();
		Solid<Bounded2> c2 = new Solid<Bounded2>(new Bounded2());
	}

}

interface HasColor {
	Color getColor();
}

interface Weight{
	int weight();
}
class Colord<T extends HasColor>{
	T item;
	public Colord(T item){
		this.item = item;
	}
	
	Color getColor(){
		return item.getColor();
	}
}
class Dimesion{
	public int x,y,z;
}

class ColoredDimesion<T extends Dimesion & HasColor>{
	T item;
	public ColoredDimesion(T item){
		this.item = item;
	}
	Color getColor(){
		return item.getColor();
	}
	int getX(){
		return item.x;
	}
}
class Solid<T extends Dimesion & HasColor & Weight>{
	T item;
	Solid(T item){
		this.item = item;
	}
	Color getColor(){
		return item.getColor();
	}
	int getX(){
		return item.x;
	}
	
	int weight(){
		return item.weight();
	}
}
class Bounded2 extends Dimesion implements HasColor{

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
class Bounded extends Dimesion implements HasColor,Weight{

	@Override
	public int weight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}
	
}