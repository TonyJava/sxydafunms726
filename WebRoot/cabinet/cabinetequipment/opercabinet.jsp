<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.model.EquipmentReport"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>
<%@page import="com.afunms.cabinet.model.MachineCabinet"%>
<%@page import="com.afunms.cabinet.model.OperCabinet"%>
<%@page import="com.afunms.config.model.BusinessSystem "%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	List businesslist = (List) request.getAttribute("businesslist");
	String opername=(String)session.getAttribute("opername");
	String contactname=(String)session.getAttribute("contactname");
	String contactphone=(String)session.getAttribute("contactphone");
	
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>
		<script>
    function backList()
   {  
     mainForm.action = "<%=rootPath%>/cabinetequipment.do?action=list";
     mainForm.submit();
    }
       function doQuery()
   {  
     mainForm.action = "<%=rootPath%>/opercabinet.do?action=querylist";
     mainForm.submit();
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
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															资源 &gt;&gt; 机柜设备配置 &gt;&gt; 机柜使用报表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
														</td>
													</tr>
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															所属业务<select id="oper" name="oper" style="width: 150px"> 
															<option value="0" selected>--请选择--</option>
													<%
														if (businesslist != null) {
															for (int i = 0; i < businesslist.size(); i++) {
															
																String sname = ((BusinessSystem ) businesslist.get(i)).getName();
																int sid = ((BusinessSystem ) businesslist.get(i)).getId();
													%>
													<option value="<%=sid%>"><%=sname%>
													<%
														}
														}
													%>
												
												</select>
													
															<input type="button" value="查 询"
																style="width: 50; margin-left: 10" id="process"
																onclick="doQuery()">
															<a
																href="<%=rootPath%>/opercabinet.do?action=downexlreport"
																target="_blank"><img name="xls" alt='导出EXCEL'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_excel.gif"
																	width=18 border="0">导出EXCEL</a>
															<a
																href="<%=rootPath%>/opercabinet.do?action=downwordreport"
																target="_blank"><img name="doc" alt='导出WORD'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_word.gif"
																	width=18 border="0">导出WORD</a>
															<a
																href="<%=rootPath%>/opercabinet.do?action=downpdfreport"
																target="_blank"><img name="pdf" alt='导出PDF'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_pdf.gif"
																	width=18 border="0">导出PDF</a>

														</td>
														<td>
															<input type="reset" style="width: 50" value="返回"
																onclick="backList()">
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr style="align:center;width:100%">
											<td >
												<table id="content-body" class="content-body">
													<tr>
														<td width="650" style="padding: 23px, 90px, 80px, 90px;width:50%">
															<table width="100%" align="center" border="1"
																bordercolor="#000000" cellspacing="0" cellpadding="0"
																bgcolor="#ffffff">
																<tr height=40>
																	<td colspan="7" align="center" style="font-size: 16px;">
																		<B><%=opername%>业务机柜使用分布汇总表 
																	</td>
																</tr>
																<tr height=30>
																	<td colspan="2">
																		<B>业务名称：</B>
																		<%=opername %>
																	</td>
																	<td colspan="2">
																		<B>联系人：</B>
																		<%=contactname %>
																	</td>
																	<td colspan="3">
																		<B>联系电话：</B>
																		<%=contactphone %>
																	</td>
																</tr>
																<tr height=30>
																	<td width=80 align="center">
																		<B>机房名称 
																	</td>
																	<td align="center">
																		<B>机柜名称 
																	</td>
																	<td align="center">
																		<B>U单元 
																	</td>
																	<td align="center">
																		<B>设备名称 
																	</td>
																	<td align="center">
																		<B>IP地址 
																	</td>
																	<td align="center">
																		<B>联系人 
																	</td>
																	<td align="center">
																		<B>联系电话 
																	</td>
																</tr>
																<%
											if (list != null && list.size() > 0) {
												for (int i = 0; i < list.size(); i++) {
													OperCabinet vo = (OperCabinet) list.get(i);
										%>
																<tr height=30
																	style="background-color: <% if(i%2==0){%>#ECECEC;<%}else{%>#ffffff;<%} %>">
																	<td width=80 align="center">
																		<%=vo.getRoomname()%>
																	</td>
																	<td align="center">
																		<%=vo.getCabinetname()%>
																	</td>
																	<td align="center">
																		<%=vo.getUseu()%>
																	</td>
																	<td align="center">
																		<%=vo.getEquipmentname()%>
																	</td>
																	<td align="center">
																		<%=vo.getIpaddress()%>
																	</td>
																	<td align="center">
																		<%=vo.getContactname()%>
																	</td>
																	<td align="center">
																		<%=vo.getContactphone()%>
																	</td>
																</tr>
																<%
											}
											}
										%>
												<%
											if (( list.size() -15)<0) {
												for (int i = list.size(); i <(15- list.size()); i++) {
													
										%>					
										<tr height=30 style="background-color: <% if(i%2==0){%>#ECECEC;<%}else{%>#ffffff;<%} %>">
																	<td width=40>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																	<td>
																		&nbsp;
																	</td>
																</tr>
																<%
											}
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
																		<img src="<%=rootPath%>/common/images/right_b_01.jpg"
																			width="5" height="12" />
																	</td>
																	<td></td>
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
