<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.ResourceConf"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String select = (String) session.getAttribute("select");
%>

<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>

<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
		<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
     var chk1 = checkinput("title","string","标题",50,false);
     var chk3 = checkinput("startdate","string","开始时间",50,false);
     var chk4 = checkinput("todate","string","截至时间",50,false);
     
      if(chk1&&chk3&&chk4)
     {      
            Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/resourceAllocation.do?action=update";
        	mainForm.submit();
     }
       // mainForm.submit();
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------


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
	timeShareConfiginit();
	
	 // nielin add for time-sharing at 2010-01-04
}
</script>
	</head>
	<body id="body" class="body" onload="initmenu()">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
								<td class="td-container-main-add">
									<table id="container-main-add" class="container-main-add">
										<tr>
											<td>
												<table id="add-content" class="add-content">
													<tr>
														<td>
															<table id="add-content-header" class="add-content-header">
																<tr>
																	<td align="left" width="5">
																		<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																			width="5" height="29" />
																	</td>
																	<td class="add-content-title">
																		系统管理 >> 维护期设置 >> 维护修改
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
															<table id="detail-content-body"
																class="detail-content-body">
																<tr>
																	<td>
																		<table border="0" id="table1" cellpadding="0"
																			cellspacing="1" width="100%">
																			<%
																			ResourceConf vo=(ResourceConf)request.getAttribute("vo");
																			int a =  (vo.getStartdate()).indexOf(" ");
																			String date = vo.getStartdate().substring(0,a);
																			String enddate = vo.getEnddate().substring(0,a);
																			%>
																			<input type="hidden" name="id" id="id" size="30" value="<%=vo.getId() %>">
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="40%">
																					标题
																				</TD>
																				<TD>
																					<input type="text" name="title" id="title" size="30" value="<%=vo.getConfititle() %>">
																				</TD>
																			</tr>
																			<tr >
																				<TD nowrap align="right" height="24" width="40%">
																					状态
																				</TD>
																				<TD>
																				<%
																					if(vo.getState().equals("1")){
																						%>
																						<select id="state" size=1 name='state' style='width:108px;'>
																						<option value=0>活动</option>
																						<option value=1 selected>停止</option>
																					</select>
																					<%}else {%>
																						<select id="state" size=1 name='state' style='width:108px;'>
																						<option value=0 selected>活动</option>
																						<option value=1>停止</option>
																					</select>
																					<%}%>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="40%">
																					描述
																				</TD>
																				<TD>
																					<input type="text" name="des" id="des" size="30" value="<%=vo.getResourcedesc() %>">
																				</TD>
																			</tr>
																			
																			
																			
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">开始日期&nbsp;</TD>
																				<TD nowrap width="10%"><input type="text" name="startdate" value="<%=date %>" size="21" readonly="readonly" id="startDate"/>
																				<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																				<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a><font color="red">&nbsp;*</font>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">截止日期&nbsp;</TD>
																				<TD nowrap width="40%"><input type="text" value="<%=enddate %>" name="todate" size="21" id="todate" readonly="readonly" />
																				<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
																				<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a><font color="red">&nbsp;*</font>
																				</TD>                    
					        												</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center>
																					<br>
																					<input type="button" value="保 存" style="width: 50"
																						id="process" onclick="#">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																					<input type="reset" style="width: 50" value="返回"
																						onclick="javascript:history.back(1)">
																				</TD>
																			</tr>
																		</TABLE>
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
	</body>
</HTML>