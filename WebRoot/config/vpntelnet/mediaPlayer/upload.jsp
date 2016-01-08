<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="com.jspsmart.upload.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%

String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";

String fname = "";
String oname = "";
String mypath=".."+request.getContextPath()+"/config/vpntelnet/mediaPlayer/flv/";
		System.out.println("=====================开始上传=====================");
    	SmartUpload mySmartUpload=new SmartUpload();//初始化
    	mySmartUpload.initialize(pageContext);//初始化
    	mySmartUpload.setTotalMaxFileSize(50*1024*1024);
    	mySmartUpload.setMaxFileSize(50*1024*1024);
    	mySmartUpload.setAllowedFilesList("flv,avi,asx,asf,mp4,mov,3gp,wmv,mpg,rmvb,rm,wmv9");//限制文件类型
    	
    	mySmartUpload.upload();//上传
    	System.out.println(mySmartUpload);
    	mySmartUpload.save(mypath);//保存路径
    	for(int i=0;i<mySmartUpload.getFiles().getCount();i++){
    		File myfile=mySmartUpload.getFiles().getFile(i);//获得文件
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar date=Calendar.getInstance();
	 		String time = sdf.format(date.getTime());
    		if(!myfile.isMissing()){
    			oname = myfile.getFileName();//获得文件名
    			fname = oname.substring(0,oname.lastIndexOf("."))+ "_" + time + oname.substring(oname.lastIndexOf("."));
    			System.out.println(fname);
    			myfile.saveAs("\\config\\vpntelnet\\mediaPlayer\\flv\\"+fname);
    			//转换格式
    			String realPath = request.getRealPath("");
    			if(!fname.substring(fname.lastIndexOf(".")).equals("flv")){
    				String path = realPath + "\\config\\vpntelnet\\mediaPlayer\\flv\\";
    				com.afunms.application.util.ConvertVideoUtil cvu = new com.afunms.application.util.ConvertVideoUtil();
    				System.out.println("111!!!!!!!!!!!!!!!!!"+path+"!!!"+fname);
    				fname = cvu.Convert(path,fname);
    				System.out.println("222!!!!!!!!!!!!!!!!!"+path+"!!!"+fname);
    			}
    		}
    		System.out.println("^^^^^^^^"+fname);
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
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<!-- 
	<script type="text/javascript">
		setTimeout('window.close()','2000') 
	</script>
	 -->
  </head>
  
  <body onload="show()">
  </body>
  	 <script language="JavaScript" type="text/javascript"> 
	 	function show(){
	 		parent.opener.document.getElementById("aaa").value="<%=fname%>";
	 		window.parent.close();
	 	}
	</script> 
	
	
	<script type="text/javascript">
		setTimeout('window.close()','1');
	</script>
</html>
