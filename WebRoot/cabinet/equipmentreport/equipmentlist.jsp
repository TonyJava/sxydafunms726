<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.model.EquipmentReport"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>
<%@page import="com.afunms.cabinet.model.MachineCabinet"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	String roomname = (String) session.getAttribute("roomname");
	String cabinetname = (String) session.getAttribute("cabinetname");
	String uselect = (String) session.getAttribute("uselect");
	String unumber = (String) session.getAttribute("unumber");
	String rate = (String) session.getAttribute("rate");
	String roomid = (String) session.getAttribute("roomid");
	String cabinetid = (String) session.getAttribute("cabinetid");
	List roomlist = (List) request.getAttribute("roomlist");
	List cabinetlist = (List) request.getAttribute("cabinetlist");
	String findselect=(String)session.getAttribute("findselect");
	
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
     mainForm.action = "<%=rootPath%>/equipmentreport.do?action=list";
     mainForm.submit();
    }
       function doQuery()
   {  
     mainForm.action = "<%=rootPath%>/equipmentreport.do?action=querylist";
     mainForm.submit();
    }
		</script>
<script type="text/javascript"> 
     var req; 
     window.onload=function() 
     {//页面加载时的函数 
     } 

     function Change_Select(){//当第一个下拉框的选项发生改变时调用该函数 
       var room = document.getElementById('room').value; 
       var url = "select?id="+ escape(room);
       if(window.XMLHttpRequest){ 
         req = new XMLHttpRequest(); 
       }else if(window.ActiveXObject){
         req = new ActiveXObject("Microsoft.XMLHTTP"); 
       } 
       if(req){ 
         req.open("GET",url,true); 
         //指定回调函数为callback 
         req.onreadystatechange = callback; 
         req.send(null); 
       } 
     } 
     //回调函数 
     function callback(){ 
       if(req.readyState ==4){ 

         //if(req.status ==200){ 

           parseMessage();//解析XML文档 
        // }else{ 
         //  alert("不能得到描述信息:" + req.statusText); 
        // } 
       } 
     } 
     //解析返回xml的方法 
     function parseMessage(){ 
    
       var xmlDoc = req.responseXML.documentElement;//获得返回的XML文档 
       var xSel = xmlDoc.getElementsByTagName('select'); 
       //获得XML文档中的所有<select>标记 
       var select_root = document.getElementById('cabinet'); 
       //获得网页中的第二个下拉框 
       select_root.options.length=0; 
       //每次获得新的数据的时候先把每二个下拉框架的长度清0 
       for(var i=0;i<xSel.length;i++){ 
         var xValue = xSel[i].childNodes[0].firstChild.nodeValue; 

         //获得每个<select>标记中的第一个标记的值,也就是<value>标记的值 
         var xText = xSel[i].childNodes[1].firstChild.nodeValue; 
         //获得每个<select>标记中的第二个标记的值,也就是<text>标记的值 

         var option = new Option(xText, xValue); 
         //根据每组value和text标记的值创建一个option对象 

         try{ 
           select_root.add(option);//将option对象添加到第二个下拉框中 
         }catch(e){ 
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
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td class="content-title">
															3D机房 &gt;&gt; 机房报表 &gt;&gt; 机柜使用报表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" />
														</td>
													</tr>
													<tr>
														<td class="content-title" colspan=3>
															&nbsp;机房:<select id="room" name="room" style="width: 150px" onChange="Change_Select()"> 
															<option value="0" selected>--请选择--</option>
													<%
														if (roomlist != null) {
															for (int i = 0; i < roomlist.size(); i++) {
															
																String rname = ((EqpRoom) roomlist.get(i)).getName();
																int rid = ((EqpRoom) roomlist.get(i)).getId();
													%>
													<option value="<%=rid%>"><%=rname%>
													<%
														}
														}
													%>
												
												</select>
													
													        机柜:<select id="cabinet" name="cabinet" style="width: 150px">
										                          <option value="0">--请选择--</option> 
										
												</select>
													
															<input type="button" value="查 询"
																style="width: 50; margin-left: 10" id="process"
																onclick="doQuery()">
															<a
																href="<%=rootPath%>/equipmentreport.do?action=downexlreport"
																target="_blank"><img name="xls" alt='导出EXCEL'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_excel.gif"
																	width=18 border="0">导出EXCEL</a>
															<a
																href="<%=rootPath%>/equipmentreport.do?action=downwordreport"
																target="_blank"><img name="doc" alt='导出WORD'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_word.gif"
																	width=18 border="0">导出WORD</a>
															<a
																href="<%=rootPath%>/equipmentreport.do?action=downpdfreport"
																target="_blank"><img name="pdf" alt='导出PDF'
																	style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_pdf.gif"
																	width=18 border="0">导出PDF</a>

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
																	<td colspan="6" align="center" style="font-size: 16px;">
																		<B><%= roomname%>机房<%= cabinetname%>号机柜使用明细表 
																	</td>
																</tr>
																<tr height=30>
																	<td colspan="3">
																		<B>所属机房：</B>
																		<%= roomname%>
																	</td>
																	<td colspan="3">
																		<B>机柜编号：</B>
																		<%= cabinetname%>
																	</td>
																</tr>
																<tr height=30>
																	<td colspan="2">
																		<B>U总数：</B>
																		<%= uselect%>
																	</td>
																	<td colspan="2">
																		<B>已使用：</B>
																		<%= unumber%>
																	</td>
																	<td colspan="2">
																		<B>使用率：</B>
																		<%= rate%>
																	</td>
																</tr>
																<tr height=30>
																	<td width=50 align="center">
																		<B>U序号 
																	</td>
																	<td align="center">
																		<B>设备名称 
																	</td>
																	<td align="center">
																		<B>设备描述 
																	</td>
																	<td align="center">
																		<B>所属业务 
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
													EquipmentReport vo = (EquipmentReport) list.get(i);
										%>
																<tr height=30
																	style="background-color: <% if(i%2==0){%>#ECECEC;<%}else{%>#ffffff;<%} %>">
																	<td width=40 align="center">
																		<%=i + 1%>
																	</td>
																	<td align="center">
																		<%=vo.getEquipmentname()%>
																	</td>
																	<td align="center">
																		<%=vo.getEquipmentdesc()%>
																	</td>
																	<td align="center">
																		<%=vo.getOperation()%>
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
