import java.util.HashMap;
import java.util.Map;



public class UMLtest {
	
	

		public static String callManifest() throws Exception {
		Service service = new Service();
			  Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL("https://192.168.1.124 /phpserver.php"));  
		call.setOperationName("QueryAccount ");

		Map map=new HashMap();
		map.put("hostname", "test");
		map.put("protocol", "telnet");
		map.put("port", "");

		call.addParameter("param",org.apache.axis.Constants.SOAP_ARRAY,javax.xml.rpc.ParameterMode.IN);
		call.setReturnType(org.apache.axis.Constants.XSD_STRING);

		Object obj=call.invoke(new Object[]{"host", map});
		return obj.toString();
		}

		public static void main(String[] args) throws Exception {
		String str = callManifest();
		System.out.println(str);
		}
	


}
