<%@page language="java" contentType="text/html;charset=GB2312" import="com.jspsmart.upload.SmartUpload"%>

<%@page import="com.afunms.discovery.*"%>
<%@page import="com.afunms.topology.manage.*"%>
<%@page import="com.afunms.initialize.ResourceCenter"%>

<%    
	  String flag = (String)request.getParameter("flag");
	  System.out.println(flag+"-----------");
	  ChartGraphics cg = new ChartGraphics(); 
  		try { 
			cg.graphicsGeneration(flag);
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
    	String fileName =ResourceCenter.getInstance().getSysPath()+"topology\\network\\network_topo.jpg";
	try
	{
	    SmartUpload download = new SmartUpload();   
	    download.initialize(pageContext);  
	    download.downloadFile(fileName);
	    	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Error in download!");
	}
%>