<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.config.model.*"%>
<%
String rootPath = request.getContextPath();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>方案阅读</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="<%=rootPath%>/config/knowledges/AC_PDF.js" language="javascript"></script>

	<script language="javascript" type="text/javascript">
        check = isAcrobatPluginInstall();
		if(!check)
		{
		    if(window.confirm("您的机器上没有安装Acrobat Reader，是否现在安装？")) 
		    {
				window.open( "http://www.adobe.com/cn/products/reader/")
			} 
			else
			{
			    alert("您的机器没有安装 Acrobat Reader ， 可能就不能正常显示下面的文档");
			}
		}
    </script>

  </head>
  
  <body>
  <%
  	String name=(String)request.getParameter("attachfiles");
  	String  result = new String(name.getBytes("ISO-8859-1"),"GBK");
  %>
    <h3>3D机房 >> 机房配置 >> 机房配置管理 >>查看文件</h3>
    <hr/>
     <br>
     
     <script type="text/javascript">
        var ua = navigator.userAgent.toLowerCase();
        var isIE  = (ua.indexOf("msie") != -1) ? true : false;
        if(!isIE)
        {
            document.write("pdf内容不能正常显示，需要用IE6.0或6.0以上的浏览器<br>");
        }
     </script>
        <embed width="800" height="580" src="<%=rootPath%>/cabinet/roomlaw/pdf/<%=result%>"  vmmode="transparent"></embed>
         <br>
     
     <br>
  </body>
</html>
