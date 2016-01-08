 <%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>
<%@page import="com.afunms.cabinet.dao.*"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@ include file="/include/globe.inc"%>
<%
	//List list = (List) request.getAttribute("list");
	//int rc = list.size();

	//JspPage jp = (JspPage) request.getAttribute("page");
	String rootPath = request.getContextPath();
	List list = null;
  EqpRoomDao dao = new EqpRoomDao();
	list = dao.loadAll();
	if (list == null) {
		list = new ArrayList();
	}
  
	
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
	String roomId = (String)request.getAttribute("roomId");
%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
	String menu = request.getParameter("menu");
  	if(menu!=null){
     session.setAttribute(SessionConstant.CURRENT_MENU,menu); 
     }
  String rightFramePath = request.getParameter("rightFramePath");
 %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="javascript">
  
  var alertInfo = "确实要删除吗?";
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/eqproom.do?action=ready_add";
     mainForm.submit();
  }
</script>
		<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}

}
 function SelectChange(){
 	var value = document.getElementById("roomId").value;
 	mainForm.action = "<%=rootPath%>/cabinetshow.do?action=list&roomId="+value+"&jp=1";
    mainForm.submit();
 }
</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">

										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td>
														<div align="center">
											              <table cellpadding="0" cellspacing="0" width=99%>
											              							<tr><td><select id="roomId" name="roomId" onchange = "SelectChange()">
											              							<option value="">--请选择机房--</option>
		<%
			for(int i = 0;i<list.size();i++){
				EqpRoom room = (EqpRoom)list.get(i);
		%>
		<option value="<%=i+1 %>"><%=room.getName() %></option>
		<%
			}
		%>
	</select></td></tr>
											              							<tr> 
											              								<td width="100%" align=center> 
											                							<EMBED  src="<%=rootPath%>/fanganswf/fn<%=roomId %>/JFItem<%=roomId %>.swf" width=1200 height=550 autostart=true  wmode=transparent> 
											                											
																		                </td>
																					</tr>             
															</table>
											            </div>														
														
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
		        									<tr>
		        										<td>
		        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
									                  			<tr>
									                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
									                    			<td></td>
									                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
									                  			</tr>
									              			</table>
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        					</table>
		        				</td>
		        			</tr>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</BODY>
</HTML>
