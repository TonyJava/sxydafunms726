<%@page import="java.net.URLEncoder" %>
<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%
  String rootPath = request.getContextPath();  
  
  String menu = request.getParameter("menu");
  if(menu!=null){
     session.setAttribute(SessionConstant.CURRENT_MENU,menu); 
  }
  User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
  String bids[] = current_user.getBusinessids().split(","); 
  String rightFramePath = request.getParameter("rightFramePath");
 
  if(rightFramePath == null){
	  rightFramePath = URLEncoder.encode("/perform.do?action=monitornodelist&flag=1&category=net_router&treeBid=2","UTF-8");
  }else{
	  rightFramePath = URLEncoder.encode(rightFramePath,"UTF-8");
  }
 
%>
<html>
<head>
<title></title>
</head>  
  <frameset rows="*" cols="32,*" frameborder="no" framespacing="0">
	<frame name="tabMenuFrame" SCROLLING="no"  noresize id="tabMenuFrame"  src="tabMenuFrame.jsp"></frame>
	<frame name="tabMenuContent"  id="tabMenuContent" src="tabMenuContent.jsp?treeflag=0&rightFramePath=<%=rightFramePath%>"></frame>
  </frameset>
</html>
