<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.model.RoomLaw"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.cabinet.model.RoomVideo"%>
<%@page import="java.util.Iterator"%>
<%@ include file="/include/globe.inc"%>
<%
	List list = (List) request.getAttribute("list");
	int rc = list.size();
	JspPage jp = (JspPage) request.getAttribute("page");
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	 Hashtable<Integer,EqpRoom> eqpRoomHash =(Hashtable<Integer,EqpRoom> )request.getAttribute("eqpRoomHash");
	 Integer cabinetid=(Integer)request.getAttribute("cabinetid");
	 String startdate = (String) request.getAttribute("startdate");
	 String todate = (String) request.getAttribute("todate");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="javascript">
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/roomvideo.do?action=delete";
  var listAction = "<%=rootPath%>/roomvideo.do?action=listView";
  
  var alertInfo = "确实要删除吗?";
 
</script>
		<script language="JavaScript" type="text/JavaScript">

	 function toAdd()
  {
     mainForm.action = "<%=rootPath%>/roomvideo.do?action=ready_add";
     mainForm.submit();
  }
 
	
	function edit(){
		mainForm.action = "<%=rootPath%>/roomvideo.do?action=ready_edit&id=" + node;
		mainForm.submit();
	}
	function createWindow(name){
	
	}
	function search()
  {
	mainForm.action = "<%=rootPath%>/roomvideo.do?action=listView";
    mainForm.submit();
	
  }
</script>
	</head>
	<body id="body" class="body" >
	   <IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 3D机房 >> 机房配置 >> 机房安全视频管理 </td>
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
																	<td class="body-data-title" style="text-align: right;">
																		<td class="body-data-title" style="text-align:left;">
																		&nbsp;&nbsp;机房位置：<select id="cabinetid" name="cabinetid">
																			<option value="-1">不限</option>
																	    <%
																	    if(eqpRoomHash!=null){
																	    	String selected="";
																	    	
																	    for   (Iterator   it   =   eqpRoomHash.keySet().iterator();   it.hasNext();   )   { 
																	    	Integer   key   =   (Integer)   it.next(); 
																	        Object   value   =   eqpRoomHash.get(key);
																	        EqpRoom eqpRoom=eqpRoomHash.get(key);
																			String room=eqpRoom.getName();
																			if(cabinetid!=null&&(cabinetid==key)){
																				selected="selected";
																			}else{
																				selected="";
																			}
																			
																	        %>
																	        <option value="<%=key%>" <%=selected %>><%=room%></option>
																	        <%
																	    } 
																	    }
                                                                  %>
                                                                       </select>
																		 &nbsp;开始日期<input type="text" name="startdate" value="<%=startdate%>" size="10">
																				 <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																									<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0> </a> 
																		       截止日期<input type="text" name="todate" value="<%=todate%>" size="10" />
																				     <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																									<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0> </a> 
																	     &nbsp;&nbsp;<input type="button" value="查  询" onclick="search()">
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
																	<td align="center" class="body-data-title" width="5%">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	<td align="center" class="body-data-title" width="5%">
																		序号
																	</td>
																	<td align="center" class="body-data-title" width="17%">
																		视频名称
																	</td>
																	<td align="center" class="body-data-title" width="15%">
																		机房位置
																	</td>
																	<td align="center" class="body-data-title" width="25%">
																		描述
																	</td>
																	<td align="center" class="body-data-title" width="17%">
																		操作时间
																	</td>
																	<td align="center" class="body-data-title" width="6%">
																		操作
																	</td>
																</tr>
																<%
																	int startRow = jp.getStartRow();
																	for (int i = 0; i < rc; i++) {
																		RoomVideo vo = (RoomVideo) list.get(i);
																		if(vo==null)continue;
																		String room="";
																		EqpRoom eqpRoom=null;
																		
																		if(eqpRoomHash.containsKey(vo.getCabinetid())){
																			eqpRoom=eqpRoomHash.get(vo.getCabinetid());
																			room=eqpRoom.getName();
																		}
																		
																%>
																<tr<%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>">
																	</td>
																	<td align="center" class="body-data-list">
																		<font color='blue'><%=startRow + i%></font>
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getName()%></td>
																	<td align="center" class="body-data-list"><%=room%></td>
																	<td align="center" class="body-data-list"><%=vo.getDescription()%></td>
																	<td align="center" class="body-data-list"><%=vo.getDotime()%></td>
																	<td align="center" class="body-data-list">
																	   <a href="#" onclick="window.open('<%=rootPath%>/cabinet/roomvideo/show.jsp?attachfiles=<%=vo.getFilename() %>', 'protypeWindow','toolbar=no,width=600,height=420,directories=no,status=no,scrollbars=yes,menubar=no')"> 播放</a>
															           <a href='<%=rootPath%>/roomvideo.do?action=download&fileName=<%=vo.getFilename() %>'>下载</a>
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
