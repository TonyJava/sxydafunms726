<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.system.dao.*"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="com.afunms.polling.*"%>


<%
  String rootPath = request.getContextPath();
  List<MediaPlayer> mediaPlayerList = (List<MediaPlayer>)request.getAttribute("list");
  JspPage jp = (JspPage) request.getAttribute("page");
  List bslist = (List) request.getAttribute("bslist");
  Hashtable userhash = (Hashtable) request.getAttribute("userhash");
  Hashtable bsHash = (Hashtable) request.getAttribute("bshash");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		
		<script language="JavaScript" type="text/javascript">
			var listAction = "<%=rootPath%>/mediaPlayer.do?action=list";
	  		var delAction = "<%=rootPath%>/mediaPlayer.do?action=delete";
	  		var curpage= <%=jp.getCurrentPage()%>;
	  		var totalpages = <%=jp.getPageTotal()%>;
	  		function toAdd()
	  		{
	     		mainForm.action = "<%=rootPath%>/mediaPlayer.do?action=ready_add";
	     		mainForm.submit();
	  		}
	  		function toFind(){
	  			mainForm.action = "<%=rootPath%>/mediaPlayer.do?action=find";
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
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();" leftmargin="0" topmargin="0">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
												<table width="100%" cellspacing="0" cellpadding="0">
													<tr>
														<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    									<td class="layout_title"><b>自动化 >> 自动化控制 >> 执行演示视频管理</b></td>
                    									<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>           
											<td>
												<table width="100%" cellpadding="0" cellspacing="1" >
        											<tr>
        												<td bgcolor="#ECECEC" align="left" >
															&nbsp;&nbsp;业务系统&nbsp;&nbsp;<select name="bsid">
															<option value="-1" >---请选择---</option>
															<%
																for(int i = 0; i<bslist.size();i++){
																BusinessSystem busi = (BusinessSystem)bslist.get(i);
															 %>
															 	<option value="<%=busi.getId() %>"><%=busi.getName() %></option>
															 <%
															 	}
															  %>
															</select>
															&nbsp;&nbsp;
															<input type="button" name="find" value="查询" onclick="toFind()"/>
														</td>
														<td bgcolor="#ECECEC" align='center'>
					            							<a href="#" onclick="toAdd()">添加</a>
															<a href="#" onclick="toDelete()">删除</a>
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
															<jsp:include page="../../../common/page.jsp">
																<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
																<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
															</jsp:include>
														</td>
													</tr>
												</table>
											</td>
										</tr>
        								<tr>
											<td colspan="2">
												<table cellspacing="1" cellpadding="0" width="100%" >
													<tr class="microsoftLook0" height=28>
														<th width='5%' class="body-data-title">&nbsp;<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()"></th>
														<th width='5%' class="body-data-title">序号</th>
														<th width='20%' class="body-data-title">名称</th>
														<th width='25%' class="body-data-title">描述</th>
														<th width='15%' class="body-data-title">所属系统</th>
														<th width='10%' class="body-data-title">上传人</th>
														<th width='15%' class="body-data-title">修改时间</th>
    													<th width='5%' class="body-data-title">编辑</th>
													</tr>
													<%
													MediaPlayer vo = null;
													int startRow = jp.getStartRow();
													if(mediaPlayerList != null){
										    			for(int i=0;i<mediaPlayerList.size();i++){
										       				vo = mediaPlayerList.get(i);

										       				String oper = null;
										       				try{
										       					oper = ((User)userhash.get(vo.getOperid())).getName();
															}catch(Exception e){
																oper = "";
															}
															String business = null;
															try{
																business = ((BusinessSystem)bsHash.get(vo.getBsid())).getName();
															}catch(Exception e){
																business = "";
															}
													%>
											       	<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> class="microsoftLook">
											    		<td height=25 align=center>&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=vo.getId()%>"></td>
											    		<td height=25 align=center>&nbsp;<font color='blue'><%=startRow + i%></font></td>
														<td align="center" >
															<a href="#" onclick="window.open('<%=rootPath%>/config/vpntelnet/mediaPlayer/show.jsp?attachfiles=<%=vo.getFileName() %>',
															'protypeWindow','toolbar=no,width=600,height=420,directories=no,status=no,scrollbars=yes,menubar=no')"><%=vo.getName()%></a>
														</td>
														<td align="center"><%=vo.getDesc() %></td>
														<td align="center" ><%=business %></td>
														<td align="center" ><%=oper %></td>
														<td align="center"><%=vo.getDotime() %></td>
														<td align="center">
															<a href="<%=rootPath%>/mediaPlayer.do?action=ready_edit&id=<%=vo.getId()%>"><img
																src="<%=rootPath%>/resource/image/editicon.gif" border="0" />
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
		</form>
	</body>
</html>
