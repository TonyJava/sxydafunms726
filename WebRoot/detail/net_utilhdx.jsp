<%@ page language="java" contentType="text/html; charset=GBK" %>
<%@ page import="java.util.*"%>
<%@ page import="com.afunms.topology.manage.ServiceForFlex"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath(); 
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html> 
<head>
<title>端口流量信息</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/list.js"></script>
<script language="JavaScript">var tabImageBase = "<%=rootPath%>/chart/report/tabs";</script> 
<script language="JavaScript" src="<%=rootPath%>/chart/js/dhtml.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/chart/js/graph.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/print.js"></script>
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script language="JavaScript" src="<%=rootPath%>/include/validation.js"></script>
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="JavaScript" fptype="dynamicoutline">
function query(){
	subforms = document.forms[0];
	mainForm.action = "<%=rootPath%>/monitor.do?action=show_utilhdx";
	subforms.submit();
}
function checkdetail(){
	subform = document.forms[0];
	subform.operate.value = "show_utilhdx";
  subform.submit();
}
function closewin(){
	window.close();
}
function openwin(operate,category,entity,subentity) 
{	subform = document.forms[0];
  var ipaddress = subform.ipaddress.value;
  var equipname = subform.equipname.value;
  window.open ("MonitoriplistMgr.do?operate="+operate+"&category="+category+"&entity="+entity+"&subentity="+subentity+"&ipaddress="+ipaddress+"&equipname="+equipname, "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
</head>
<%

String operate = request.getParameter("operate");
if(operate == null) {operate="netsys";}
String ipaddress = (String)request.getAttribute("ipaddress");
String index = (String)request.getAttribute("index");
String ifname = (String)request.getAttribute("ifname");
ServiceForFlex serviceForFlex = new ServiceForFlex();
//Hashtable hash = serviceForFlex.getAvgPortFluxByDate(ipaddress,);
%>
<body class="WorkWin_Body">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="ipaddress" value=<%=ipaddress%>>
<input type=hidden name="ifindex" value=<%=index%>>
<input type=hidden name="ifname" value=<%=ifname%>>  

<table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td colspan="10">
    <table width="100%" border="0" cellpadding="0" cellspacing="0" id="table2">
        <tr> 
          <td>
          	<table cellpadding="0" cellspacing="0" width=98%>
         					<tr> 
           						<td width="100%" > 
           						<div id="flashcontent1">
								<strong>You need to upgrade your Flash Player</strong>
							</div>
							<script type="text/javascript">
								var so = new SWFObject("<%=rootPath%>/flex/Show_port.swf?ipadress=<%=ipaddress%>&ifindex=<%=index%>&ifname=<%=ifname%>&hostname=<%=request.getAttribute("hostname")%>", "Show_port", "100%", "400", "8", "#ffffff");
								so.write("flashcontent1");
							</script>				
		                </td>
					</tr>             
				</table> 
            </td>
        </tr>
      </table>
   </td>
  </tr>
  <tr>
  	<td align=center>
  		<!--<input type="reset"  style="width:50" value="关闭" onclick="javascript:window.close()">-->
  	</td>
  </tr>
</table>
    </form>
</body>
</html> 