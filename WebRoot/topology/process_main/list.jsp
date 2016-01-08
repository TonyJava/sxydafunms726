<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.application.model.ProcessMainVo"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	//List ips = (List) request.getAttribute("ips");
	List ls_ip = (List) request.getAttribute("ls_ip");
	//int rc = list.size();
	JspPage jp = (JspPage) request.getAttribute("page");
	System.out.println(jp.getPageTotal());
	
	
	//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!rootPath"+rootPath);
%>
<%
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
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/processMain.do?action=delete";
  var listAction = "<%=rootPath%>/processMain.do?action=list";
  function doQuery()
  {  
     mainForm.action = "<%=rootPath%>/processMain.do?action=find";
     mainForm.submit();
  }
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
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
														<td class="content-title"> 资源 >> 主要进程 >> 进程列表 </td>
														<td align="right"> <img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
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
																	<td class="body-data-title" style="text-align: left;">
																		&nbsp;&nbsp;
																		<B>查询:</B>
																		<SELECT name="ipaddress" style="" style="width: 130">
																			<%
																			
																				if (null!= ls_ip && ls_ip.size() > 0) {
																					for (int k = 0; k < ls_ip.size(); k++) {
																						ProcessMainVo vo = (ProcessMainVo) ls_ip.get(k);
																			%>
																			<OPTION value="<%=vo.getIp()%>"><%=vo.getIp()%></OPTION>
																			<%
																				}
																				}
																			%>

																		</SELECT>
																		&nbsp;
																		  <INPUT type="button" class="formStyle" value="查询" onclick=" return doQuery()">
																		
																	</td>
																	 
																	<td class="body-data-title" style="text-align: right;">
																		<INPUT type="button" class="formStyle" value="删 除" onclick=" return toDelete()">
																		&nbsp;&nbsp;
																	</td>
																
																</tr>
															</table>
														</td>
													</tr>
													  <tr>
														<td>
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td class="body-data-title" style="text-align: left;">
																		<jsp:include page="../../common/page.jsp">
																		<jsp:param name="curpage"
																				value="<%=jp.getCurrentPage()%>" />
																		<jsp:param name="pagetotal"
																				value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td colspan="2">
															<table cellspacing="1" cellpadding="0" width="100%">
																<tr class="microsoftLook0">
																  
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	
																	<td align="center" class="body-data-title">
																		<strong>IP地址</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>设备名称</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>设备类型</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>进程名称</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>是否主要进程</strong>
																	</td>
																  	<td align="center" class="body-data-title">
																		操作
																	</td>
																
																</tr>
																<%
																	//int startRow = jp.getStartRow();
																	if(null!=list)
																	{
																	for (int i = 0; i < list.size(); i++) {
																		ProcessMainVo vo = 	new ProcessMainVo();
																		vo = (ProcessMainVo) list.get(i);
																%>
																<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																  	
																	<td align="center" class="body-data-list">
																		<font color='blue'>&nbsp;<%=i%></font>
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>" class=noborder>
																	</td>
																	
																	<td align="center" class="body-data-list"><%=vo.getIp()%></td>
																	<td align="center" class="body-data-list"><%=vo.getAlias()%></td>
																	<td align="center" class="body-data-list"><%=vo.getSubtype()%></td>
																	<td align="center" class="body-data-list"><%=vo.getThevalue()%></td>
																	<td align="center" class="body-data-list"><%
																	if(vo.getIsmain()=="0")
																	{
																	   out.print("否");
																	   }else
																	    {
																	     out.print("是");
																	    }
																	%></td>
																	<td align="center" class="body-data-list">
																		<!--  <a href="javascript:void(null)"
																			onClick='window.open("<%=rootPath%>/processMain.do?action=showedit&nodeid=<%=vo.getNodeid()%>&ip=<%=vo.getIp()%>&thevalue=<%=vo.getThevalue()%>&Alias=<%=vo.getAlias()%>","editdiskconfig", "height=400, width= 500, top=200, left= 200")'>
																			<img src="<%=rootPath%>/resource/image/editicon.gif"
																				border="0" />
																		</a>
																		-->
																	
																		<a href="<%=rootPath%>/processMain.do?action=showedit&nodeid=<%=vo.getNodeid()%>&ip=<%=vo.getIp()%>&thevalue=<%=vo.getThevalue()%>&Alias=<%=vo.getAlias()%>&Ismain=<%=vo.getIsmain()%>","editdiskconfig", "height=400, width= 500, top=200, left= 200")'>
																			<img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/>
																		</a>
																	</td>
																	
																</tr>
																<%
																	}
																	}
																%>
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
					</td>
				</tr>
			</table>
		</form>
	</BODY>
</HTML>
