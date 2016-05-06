package testjava.nio.charset;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CharsetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	public  static void main(String[] args) {
		/*System.out.println("Default Charset=" + Charset.defaultCharset());
    	System.setProperty("file.encoding", "Latin-1");
    	System.out.println("file.encoding=" + System.getProperty("file.encoding"));
    	System.out.println("Default Charset=" + Charset.defaultCharset());
    	System.out.println("Default Charset in Use=" + getDefaultCharSet());*/
    	try {
			Thread.currentThread().sleep(100*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	    private static String getDefaultCharSet() {
	    	OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
	    	String enc = writer.getEncoding();
	    	return enc;
	 
	}
}
