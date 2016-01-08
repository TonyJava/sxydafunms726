<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.system.vo.AlarmCorrelationVo"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	JspPage jp = (JspPage) request.getAttribute("page");
	UserView view = new UserView();
	//request.setAttribute("id2",request.getAttribute("son_id"));
	String son_id=(String)request.getAttribute("id");
	String category1=(String)request.getAttribute("category");
	
	//son_id=(String)request.getSession().getAttribute("id_s");
	//String son_id2=(String)request.getAttribute("id_s2");
	//String category2=(String)request.getAttribute("category2");	
	//System.out.println("=======================123category2="+category2);
	//System.out.println("=======================123id2="+son_id2);
	System.out.println("=======================123category1="+category1);
	System.out.println("=======================123id2="+son_id);
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript">
</script>
		<script language="JavaScript" type="text/JavaScript">
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var listAction = "<%=rootPath%>/alarmcorrelation.do?action=fadd&id=<%=son_id%>&category= <%=category1%>";
var show = true
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

</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>	
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title">系统管理 >> 系统配置 >> 告警关联 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
										<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									
													<tr>
														<td>
															<table>
																<tr>
									    							<td class="body-data-title">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
														    		</td>
			        											</tr>
															</table>
														</td>
													</tr>
													<tr>
		        										<td>
		        											<table>
		        												<tr>
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	 
																	<td align="center" class="body-data-title">
																		Ip地址
																	</td>
																	
																	<td align="center" class="body-data-title">
																		名称
																	</td>
																	
																	<td align="center" class="body-data-title">
																		类型
																	</td>
																	<td align="center" class="body-data-title">
																		保存
																	</td>															
																</tr>
																<%
																AlarmCorrelationVo vo = null;
																	int startRow = jp.getStartRow();
																	if(null!=list)
																	for (int i = 0; i < list.size(); i++) {
																		vo = (AlarmCorrelationVo) list.get(i);
																		int id=vo.getId();
																		String ip_address=vo.getIp_address();
																		String alias=vo.getAlias();
																	    String type=vo.getType();
																	    String category=vo.getCategory(); 
																%>
																<tr<%=onmouseoverstyle%>>
																	<!--  
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=id%>">
																	</td>
																	-->
																	<td align='center'>
											        							<input type="radio" name="id" class="noborder" value="<%=vo.getId()%>">
																			</td>
																	<td align="center" class="body-data-list"><%=ip_address %></td>
																	<td align="center" class="body-data-list"><%=alias %></td>
																	<td align="center" class="body-data-list"><%=type %></td>
																	<td align="center" class="body-data-list">
																		<a href="<%=rootPath%>/alarmcorrelation.do?action=save&id=<%=id%>">
																			<img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/>
																		</a>
																	</td>
																</tr>
																<%
																
																	}
																%>
															</table>
														</td>
													</tr>