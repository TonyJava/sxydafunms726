<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.util.CreateMenu"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.model.Function"%>
<%@page import="org.apache.shiro.SecurityUtils " %>
<%
	List<Function> menuRoot = (List<Function>)request.getAttribute("menuRoot");
  User user = (User)SecurityUtils.getSubject().getPrincipal(); //��ǰ�û�
  if(user==null)
     response.sendRedirect("/common/error.jsp?errorcode=3003");

  String rootPath = request.getContextPath();
  CreateMenu menu = new CreateMenu(rootPath);
%>
<html>
  <head>
<title>IT��ά���ϵͳ</title>
<LINK rel=stylesheet href="<%=rootPath%>/resource/css/login.css" type="text/css" >
<link rel="stylesheet" href="<%=rootPath%>/resource/css/xmenu.css" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/cssexpr.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/xmenu.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/custom_menu.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link href="<%=rootPath%>/common/css/css.css" rel="stylesheet" type="text/css" ></link>
<script language="JavaScript" type="text/javascript">

  function toHome()
  {
     parent.mainFrame.location = "<%=rootPath%>/common/home.jsp";
  }

  function doQuit()
  {
     if (confirm("�����Ҫ�˳���?"))
     {
         parent.location = "<%=rootPath%>/user.do?action=logout";
     }
  }

  function setPerson()
  {
     parent.mainFrame.location = "<%=rootPath%>/system/user/inputpwd.jsp";     
  }
</script>

<script>
   function menu(){
   menuFrame.location.reload();
   }

</script>


</HEAD>


<frameset rows="95,*" frameborder="no" border="0" framespacing="0">
  <frame src="<%=rootPath%>/common/top.jsp" marginwidth="0" marginheight="0" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="topFrame" />
  <frame src="<%=rootPath%>/user.do?action=home" id="mainFrame" scrolling="auto" name="mainFrame" width="100%" marginwidth="0" marginheight="0" frameborder="0" />
</frameset>
<body leftmargin="0" topmargin="0" onload="window.open('test.jsp','�������ƴ���','height=50,width=100,top=0,left=0,toolbar=no, menubar=no ,scrollbars=no, resizable=no, location=no,status=no');">
</BODY>
</html>
