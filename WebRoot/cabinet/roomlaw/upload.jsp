<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="com.jspsmart.upload.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@page import="com.afunms.initialize.ResourceCenter"%>
<%@page import="com.afunms.common.util.UploadUtil"%>


<%

String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";
String realPath=ResourceCenter.getInstance().getSysPath();
String fname = "";
String oname = "";
String mypath=".."+request.getContextPath()+"/cabinet/roomlaw/pdf/";
    	SmartUpload mySmartUpload=new SmartUpload();//初始化
    	mySmartUpload.initialize(pageContext);//初始化
    	mySmartUpload.setAllowedFilesList("pdf");//限制文件类型
    	mySmartUpload.upload();//上传
    
    	//mySmartUpload.save(mypath);//保存路径
    	for(int i=0;i<mySmartUpload.getFiles().getCount();i++){
    		File myfile=mySmartUpload.getFiles().getFile(i);//获得文件
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar date=Calendar.getInstance();
	 		String time = sdf.format(date.getTime());
    		if(!myfile.isMissing()){
    			oname = myfile.getFileName();//获得文件名
    			
    			String path = realPath + "cabinet\\roomlaw\\pdf\\" + oname;
    			UploadUtil util=new UploadUtil();
    			boolean flag=util.isExist(path);
    			if(flag){
    				fname = oname.substring(0,oname.lastIndexOf("."))+ "_" + time + oname.substring(oname.lastIndexOf("."));
    			}else{
    				fname=oname;
    			}
    			myfile.saveAs("\\cabinet\\roomlaw\\pdf\\"+fname);
    		}
    		request.setAttribute("ffname",fname);
    		session.setAttribute("fname",fname);
    	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>上传成功</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  
  <body onload="show()">
  </body>
  	 <script language="JavaScript" type="text/javascript"> 
	 	function show(){
	 		parent.opener.document.getElementById("filePath").value="<%=fname%>";
	 		window.parent.close();
	 	}
	</script> 
	
	
	<script type="text/javascript">
		setTimeout('window.close()','1');
	</script>
</html>
