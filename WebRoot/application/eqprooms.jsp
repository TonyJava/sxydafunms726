<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%

	String rootPath = request.getContextPath();
	
  String menu = request.getParameter("menu");
  if(menu!=null){
     session.setAttribute(SessionConstant.CURRENT_MENU,menu); 
  }
  //String rightFramePath = request.getParameter("rightFramePath");
%>
<html>
<head>
<title>资源列表</title>  
</head>  
	<frameset  id="search" name="search"  rows="*" cols="199,*" framespacing="0" rows="*" frameborder="no">
	 <!-- <frame name="leftFrame" frameborder="no" src="tree.jsp?treeflag=0&rightFramePath=%>"> --> 
	   <frame name="leftFrame" frameborder="no"  src="<%=rootPath%>/application/course/tree.jsp">
	  <frame name="rightFrame" frameborder="no" id="rightFrame" src="#">
</html>
