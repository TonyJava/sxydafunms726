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
	
//System.out.println("===========================jsp list **=====1============");
	
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
//System.out.println("===========================jsp list **=====2============");
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
	/**
	*显示 删除 增加方法
	*/		
  var listAction = "<%=rootPath%>/alarmcorrelation.do?action=add";
  var delAction = "<%=rootPath%>/alarmcorrelation.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var listAction = "<%=rootPath%>/alarmcorrelation.do?action=list";
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/alarmcorrelation.do?action=add";
     mainForm.submit();
  }

</script>
		<script language="JavaScript" type="text/JavaScript">
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
		        									<tr >
		        										<td>
															<table>
																<tr>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="toAdd()">添加</a>
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
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
																		上级Ip地址
																	</td>
																	
																	<td align="center" class="body-data-title">
																		上级名称
																	</td>
																	
																	<td align="center" class="body-data-title">
																		上级类型
																	</td>
																	
																
																	  
																	<td align="center" class="body-data-title">
																		下级Ip地址
																	</td>
																	
																	 
																	<td align="center" class="body-data-title">
																		下级名称
																	</td>
																	  
																	<td align="center" class="body-data-title">
																		下级类型
																	</td> 
																	 																
																</tr>
																<%
																//System.out.println("===========================jsp list **=====2============");
																AlarmCorrelationVo vo = null;
																	int startRow = jp.getStartRow();
																	if(null!=list)
																	for (int i = 0; i < list.size(); i++) {
																		vo = (AlarmCorrelationVo) list.get(i);
																		int bid=vo.getBid();
																		String aip=vo.getAip();
																		String aalias=vo.getAalias();
																	    String atype=vo.getAtype();
																	    String acategory=vo.getAcategory();
																	    String bip=vo.getBip();
																	    String balias=vo.getBalias();
																	    String btype=vo.getBtype();
																	    String bcategory=vo.getBcategory();
																%>
																<tr<%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=bid%>">
																	</td>
																	<td align="center" class="body-data-list"><%=aip %></td>
																	<td align="center" class="body-data-list"><%=aalias %></td>
																	<td align="center" class="body-data-list"><%=atype %></td>
																	
																	<td align="center" class="body-data-list"><%=bip %></td>
																	<td align="center" class="body-data-list"><%=balias %></td>
																	<td align="center" class="body-data-list"><%=btype %></td>
																	
																</tr>
																<%
																
																System.out.println("===========================jsp list **=====3============");
																	}
																%>
															</table>
														</td>
													</tr>