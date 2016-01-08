<%@page import="java.net.URLDecoder"%>
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
  rightFramePath = URLDecoder.decode(rightFramePath, "UTF-8");
%>
<html>
<head>
<title>设备性能</title>  
<script type="text/javascript">
	/**
	*初始化按钮颜色
	*/
	function initmenu(){
		refreshStyle('xnzy');
	}
	//  /perform.do?action=monitornodelist&flag=1&category=net_router&treeBid=2
	function refreshStyle(liId){
		var varsLi = window.parent.tabMenuFrame.document.getElementsByTagName('li');
		for(var i=0; i<varsLi.length; i++){
			var varLi = varsLi[i];
			if(varLi.id == liId){
				window.parent.tabMenuFrame.document.getElementById(varLi.id).className = "menu-title-over";
			}else{
				window.parent.tabMenuFrame.document.getElementById(varLi.id).className = "menu-title";
			}
		}
	}
	
</script>
</head>  
	<frameset onload="" id="search" name="search"  rows="*" cols="199,*" framespacing="0" rows="*" frameborder="no">
	  <frame name="leftFrame" frameborder="no" src="tree.jsp?treeflag=0&rightFramePath=<%=rightFramePath%>"></frame>
	  <frame name="rightFrame" frameborder="no" id="rightFrame" src="<%=rootPath+rightFramePath %>"></frame>
	</frameset>
</html>
