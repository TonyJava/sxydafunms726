<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.model.Cabinet"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String name = (String) session.getAttribute("name");
	String eqproomid = (String) session.getAttribute("roomid");
	List list = (List) request.getAttribute("list");
	List roomlist = (List) request.getAttribute("roomlist");
	JspPage jp = (JspPage) request.getAttribute("page");
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<style type="text/css">
#divcenter {
	MARGIN-RIGHT: auto;
	MARGIN-LEFT: auto;
}
</style>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript">
		
		
		  function doQuery()
   {  
     mainForm.action = "<%=rootPath%>/cabinetreport.do?action=querylist";
     mainForm.submit();
    }
    		  function backList()
   {  
     mainForm.action = "<%=rootPath%>/cabinetreport.do?action=list";
     mainForm.submit();
    }
		</script>
	</head>
	<body style="background:url(resource\skin1\image\bg.gif)">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								<br></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table align="center">
							<tr>
								<td class="td-container-main" width="100%" style="BORDER-right: #C0C0C0 1px solid; padding: 0px, 7px, 0px, 10px;">
									<table id="content-header" class="content-header">
										<tr>
											<td align="left" width="5">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="content-title">
												3D机房 &gt;&gt; 机房报表 &gt;&gt; 机房报表浏览
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
								<td class="td-container-main" width="100%" align="center" style="padding: 0px, 7px, 0px, 10px;">
									<table id="content-header" class="content-header">
										<tr>
											<td align="left" width="5">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="content-title">&nbsp;
												<select id="room" name="room" style="width: 150px">
													<%
														if (roomlist != null) {
															for (int i = 0; i < roomlist.size(); i++) {
																String roomname = ((EqpRoom) roomlist.get(i)).getName();
																int roomid = ((EqpRoom) roomlist.get(i)).getId();
													%>
													<option value="<%=roomid%>"><%=roomname%>
													
													<%
														}
														}
													%>
												</select>
												<input type="button" value="查 询"
													style="width: 50; margin-left: 10" id="process" onclick="doQuery()">
												<a
													href="<%=rootPath%>/cabinetreport.do?action=downexlreport&id=<%=eqproomid%>"
													target="_blank"><img name="xls" alt='导出EXCEL'
														style="CURSOR: hand"
														src="<%=rootPath%>/resource/image/export_excel.gif"
														width=18 border="0">导出EXCEL</a>
												<a
													href="<%=rootPath%>/network.do?action=downloadnetworklistfuck"
													target="_blank"><img name="doc" alt='导出WORD'
														style="CURSOR: hand"
														src="<%=rootPath%>/resource/image/export_word.gif"
														width=18 border="0">导出WORD</a>
												<a
													href="<%=rootPath%>/network.do?action=downloadnetworklistfuck"
													target="_blank"><img name="pdf" alt='导出PDF'
														style="CURSOR: hand"
														src="<%=rootPath%>/resource/image/export_pdf.gif" width=18
														border="0">导出PDF</a>
					
											</td>
											<td>
											<!--<input type="reset" style="width: 50" value="返回" onclick="backList()">-->&nbsp;
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							
							
							
							<tr style="align:center;width:100%">
								<td  style="align:center;" class="td-container-main" width="100%" align="center" style="padding: 0px, 7px, 0px, 10px;align:center;">
				            		<table id="content-body" class="content-body" style="align:center;width:100%">
										<tr style="align:center;">
											<td width="50%" style="padding: 23px, 0px, 80px, 0px;width:50%"  align="center">
												<table style="width:80%" width="50%"  align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="0" bgcolor="#ffffff">
													<tr height=40>
														<td colspan="6" align="center" style="font-size: 16px;">
															<b><%=name%>机柜使用汇总表<b>
														</td>
													</tr>
										<tr height=28>
											<td align="center">
												<B>序号
											</td>
											<td align="center">
												<B>机柜名称
											</td>
											<td align="center">
												<B>总U数
											</td>
											<td align="center" bordercolor="#000000">
												<B>已用U数
											</td>
											<td align="center" bordercolor="#000000">
												<B>空闲U数
											</td>
											<td align="center" bordercolor="#000000">
												<B>U使用率(%)
											</td>
										</tr>
										<%
											if (list != null && list.size() > 0) {
												for (int i = 0; i < list.size(); i++) {
													Cabinet cabinet = (Cabinet) list.get(i);
										%>
										<tr style="background-color: <% if(i%2==0){%>#ECECEC;<%}else{%>#ffffff;<%} %>" height=28>
											<td align="center" >
												<%=i + 1%>
											</td>
											<td align="center">
												<%=cabinet.getName()%>
											</td>
											<td align="center">
												<%=cabinet.getAllu()%>
											</td>
											<td align="center">
												<%=cabinet.getUseu()%>
											</td>
											<td align="center">
												<%=cabinet.getTempu()%>
											</td>
											<td align="center">
												<%=cabinet.getRateu()%>
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
										<tr  style="background-color: <% if(i%2==0){%>#ECECEC;<%}else{%>#ffffff;<%} %>" height=28>
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
						</table>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
