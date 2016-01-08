<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.dao.MachineCabinetDao;"%>
<%@ include file="/include/globe.inc"%>
<%
	List list = (List) request.getAttribute("list");
	int rc = list.size();

	JspPage jp = (JspPage) request.getAttribute("page");
	String rootPath = request.getContextPath();
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

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />
<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var listAction = "<%=rootPath%>/cabinetreport.do?action=list";
  </script>
		<script language="javascript">

  
  var alertInfo = "确实要删除吗?";
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/eqproom.do?action=ready_add";
     mainForm.submit();
  }
  //导出报表
function exportReport(exportType){
 var	startdate=document.all.startdate.value;
 var	todate=document.all.todate.value;
 var ids=document.all.ids_perform.value;
 var id=document.all.id_perform.value;


 window.open('<%=rootPath%>/cabinetreport.do?action=downloadReport&type=db&exportType='+exportType+'&id='+id+'&ids='+ids+'&startdate='+startdate+'&todate='+todate+'&nowtime='+(new Date()),"_blank","toolbar=no,width=1,height=1,top=2000,left=3000,directories=no,status=no,menubar=no,alwaysLowered=yes");

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
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td background="<%=rootPath%>/common/images/right_t_02.jpg"
												width="100%">
												<table width="100%" cellspacing="0" cellpadding="0">
													<tr>
														<td>
															<table id="content-header" class="content-header">
																<tr>
																	<td align="left" width="5">
																		<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																			width="5" height="29" />
																	</td>
																	<td class="content-title">
																		3D机房 >> 机房报表 >> 机房使用报表
																	</td>
																	<td align="right">
																		<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																			width="5" height="29" />
																	</td>
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
																	<td>
																		<table>
																			<tr>

																				<td align="center" class="body-data-title">
																					序号
																				</td>
																				<td align="center" class="body-data-title">
																					名称
																				</td>
																				<td align="center" class="body-data-title">
																					描述
																				</td>
																				<td align="center" class="body-data-title">
																					备注
																				</td>
																				<td align="center" class="body-data-title">
																					操作
																				</td>
																			</tr>
																			<%
																				int startRow = jp.getStartRow();
																				for (int i = 0; i < rc; i++) {
																					EqpRoom vo = (EqpRoom) list.get(i);
																			%>
																			<tr <%=onmouseoverstyle%>>

																				<td align="center" class="body-data-list">
																					<font color='blue'><%=startRow + i%></font>
																				</td>
																				<td align="center" class="body-data-list"><%=vo.getName()%></td>
																				<td align="center" class="body-data-list"><%=vo.getDescr()%></td>
																				<td align="center" class="body-data-list"><%=vo.getBak()%></td>
																				<td align="center" class="body-data-list">

																					<a
																						href="<%=rootPath%>/cabinetreport.do?action=reportlist&id=<%=vo.getId()%>&name=<%=vo.getName()%>">报表</a>
																				</td>
																			</tr>
																			<%
																				}
																			%>
																		</table>
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
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
																			<tr>
																				<td align="left" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_01.jpg"
																						width="5" height="12" />
																				</td>
																				<td></td>
																				<td align="right" valign="bottom">
																					<img
																						src="<%=rootPath%>/common/images/right_b_03.jpg"
																						width="5" height="12" />
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
	</body>
</html>
