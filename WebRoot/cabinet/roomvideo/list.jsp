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
	 Hashtable<Integer,User> userHash =(Hashtable<Integer,User> )request.getAttribute("userHash");
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
  var listAction = "<%=rootPath%>/roomvideo.do?action=list";
  
  var alertInfo = "确实要删除吗?";
 
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
//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip,showItemMenu)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        return false;
			    }
			    else{
			    	popMenu(itemMenu,100,showItemMenu);
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    
			}
			/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
			function popMenu(menuDiv,width,rowControlString)
			{
			    //创建弹出菜单
			    var pop=window.createPopup();
			    //设置弹出菜单的内容
			    pop.document.body.innerHTML=menuDiv.innerHTML;
			    var rowObjs=pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
			    var rowCount=rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
			    for(var i=0;i<rowObjs.length;i++)
			    {
			        //如果设置该行不显示，则行数减一
			        var hide=rowControlString.charAt(i)!='1';
			        if(hide){
			            rowCount--;
			        }
			        //设置是否显示该行
			        rowObjs[i].style.display=(hide)?"none":"";
			        //设置鼠标滑入该行时的效果
			        rowObjs[i].cells[0].onmouseover=function()
			        {
			            this.style.background="#99CCFF";
			            this.style.color="white";
			        }
			        //设置鼠标滑出该行时的效果
			        rowObjs[i].cells[0].onmouseout=function(){
			            this.style.background="#F1F1F1";
			            this.style.color="black";
			        }
			    }
			    //屏蔽菜单的菜单
			    pop.document.oncontextmenu=function()
			    {
			            return false; 
			    }
			    //选择右键菜单的一项后，菜单隐藏
			    pop.document.onclick=function()
			    {
			        pop.hide();
			    }
			    //显示菜单
			    pop.show(event.clientX-1,event.clientY,width,rowCount*30,document.body);
			    return true;
			}
   </script>
   
   <script type="text/javascript">
	 function toAdd()
  {
     mainForm.action = "<%=rootPath%>/roomvideo.do?action=ready_add";
     mainForm.submit();
  }
 
	
	function edit(){
		mainForm.action = "<%=rootPath%>/roomvideo.do?action=ready_edit&id=" + node;
		mainForm.submit();
	}
	function search()
  {
	mainForm.action = "<%=rootPath%>/roomvideo.do?action=list";
    mainForm.submit();
	
  }
  function exportReport(exportType){

 var	startdate=document.all.startdate.value;
 var	todate=document.all.todate.value;
 var cabinetid=document.all.cabinetid.value;
 window.open('<%=rootPath%>/roomvideo.do?action=downloadReport&type=video&exportType='+exportType+'&cabinetid='+cabinetid+'&startdate='+startdate+'&todate='+todate+'&nowtime='+(new Date()),"_blank","toolbar=no,width=1,height=1,top=2000,left=3000,directories=no,status=no,menubar=no,alwaysLowered=yes");
 
}
</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
				style="border: thin;font-size: 12px" cellspacing="0">
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.edit()">
						编辑
					</td>
				</tr>
				
			</table>
		</div>
		<!-- 右键菜单结束-->
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
																	<td class="body-data-title" style="text-align: right;">
																	    <span style="CURSOR: hand" onclick="exportReport('doc')"><img name="selDay1" alt='导出WORD' src="<%=rootPath%>/resource/image/export_word.gif" width=18 border="0">导出WORD</span>&nbsp;&nbsp;&nbsp;
																		<span style="CURSOR: hand" onclick="exportReport('xls')"><img name="selDay1" alt='导出EXCEL' src="<%=rootPath%>/resource/image/export_excel.gif" width=18 border="0">导出EXCEL</span>&nbsp;&nbsp;&nbsp;
																		<span style="CURSOR: hand" onclick="exportReport('pdf')"><img name="selDay1" alt='导出PDF' src="<%=rootPath%>/resource/image/export_pdf.gif" width=18 border="0">导出PDF</span>&nbsp;&nbsp;&nbsp;&nbsp;
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
																	<td align="center" class="body-data-title" width="10%">
																		上传人
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
																		User user=null;
																		String userName="";
																		String room="";
																		EqpRoom eqpRoom=null;
																		if(userHash.containsKey(vo.getUserid())){
																		 user=userHash.get(vo.getUserid());
																		 userName=user.getName();
																		}
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
																	<td align="center" class="body-data-list"><a href="#" onclick="window.open('<%=rootPath%>/cabinet/roomvideo/show.jsp?attachfiles=<%=vo.getFilename() %>',
															              'protypeWindow','toolbar=no,width=600,height=420,directories=no,status=no,scrollbars=yes,menubar=no')"><%=vo.getName()%></a></td>
																	<td align="center" class="body-data-list"><%=room%></td>
																	<td align="center" class="body-data-list"><%=userName%></td>
																	<td align="center" class="body-data-list"><%=vo.getDescription()%></td>
																	<td align="center" class="body-data-list"><%=vo.getDotime()%></td>
																	<td align="center" class="body-data-list">
																		<img src="<%=rootPath%>/resource/image/status.gif"
																			border="0" width=15 oncontextmenu=showMenu('2','<%=vo.getId()%>','','1111') title="右键菜单">
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
