<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%
String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>附件上传</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<script type="text/javascript">
		/**
	*检查扩展名
	*/
		function check(){
			var upfilename=document.uploadform.files.value;
			if(upfilename==""){
			   alert("你还没有选择你要上传的文件!");
			   return false;
			  } 
			   
			  
			  var filetype=upfilename.substring(upfilename.indexOf(".")+1,upfilename.length).toLowerCase();
			  var fileName=upfilename.substring(upfilename.lastIndexOf("\\"),upfilename.indexOf("."));
			   var reg=null;
			   var exp=/.*[\u4e00-\u9fa5]+.*$/;
                     reg = fileName.match(exp);
                     if(reg!=null){
                      alert("为保证能正常播放，视频文件名不要包含中文字符");
                      return;
					 }
			   
			  /**
				*限制上传类型
				*/
			  if(filetype!="flv")
			  {
			     alert("只支持flv文件!!!");
			     return false;
			  }
			  
			  window.uploadform.action="<%=rootPath%>/cabinet/roomvideo/upload.jsp";
			  window.uploadform.submit();
		}	
	</script>
  </head>
  
  <body>
  	<form method="post" enctype="multipart/form-data" target="_self" name="uploadform">
    <h2 align="center">选择您要上传的视频</h2> <br>
    方案：<input type="file" id="files" name="files">
    	<input type="button" value="上传" align="middle" onclick="check()">
    	<br><br>
    	<div align="center"><font color="red" style="font-size: 13">&nbsp;视频格式必须为flv类型,否则无法读取</font></div>
  	</form>
  </body>
</html>
