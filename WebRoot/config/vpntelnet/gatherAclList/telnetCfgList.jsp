<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>
<%@page import="com.afunms.config.model.TimingBackupTelnetConfig"%>
<%@page import="com.afunms.config.manage.HaweitelnetconfManager"%>
<%@page import="com.afunms.config.model.GatherTelnetConfig"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*" %>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List<Huaweitelnetconf> list = (ArrayList<Huaweitelnetconf>)request.getAttribute("list");
	Vector<String> ipVec=(Vector<String>)request.getAttribute("ipVec");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css"
			rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css"
			type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet"
			type="text/css">

		<!--nielin add for timeShareConfig at 2010-01-04 start-->
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>
		<!--nielin add for timeShareConfig at 2010-01-04 end-->

		<script language="JavaScript" type="text/javascript">



function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    


function showup(){
	var url="<%=rootPath%>/vpntelnetconf.do?action=multi_netip";
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
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
	//timeShareConfiginit();
	 // nielin add for time-sharing at 2010-01-04
}

</script>
		<script type="text/javascript">
		//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        popMenu(itemMenu,100,"100");
			    }
			    else
			    {
			        popMenu(itemMenu,100,"11111");
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    return false;
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
			            this.style.background="#397DBD";
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
			    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
			    return true;
			}
			
			function showTelnetNetList(){
				var url="<%=rootPath%>/vpntelnetconf.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/telnetConfig.do?action=ready_add";
			    mainForm.submit();
			}
			
			function toDelete(){  
     				mainForm.action = "<%=rootPath%>/telnetConfig.do?action=delete";
     				mainForm.submit();
	  		}
	  		
	  		function edit()
			{
				mainForm.action="<%=rootPath%>/telnetConfig.do?action=ready_edit&id="+node;
				mainForm.submit();
			}
	  		function setStarting()
			{
				mainForm.action="<%=rootPath%>/telnetConfig.do?action=aclStarting&type="+node+"&ipaddress="+ipaddress;
				mainForm.submit();
			}
	  		function setShutdown()
			{
				mainForm.action="<%=rootPath%>/telnetConfig.do?action=aclShutdown&id="+node+"&ipaddress="+ipaddress;
				mainForm.submit();
			}
		</script>

	</head>
	<body id="body" class="body" onload="initmenu();">
<div id="itemMenu" style="display: none";>
		<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
			style="border: thin;font-size: 12px" cellspacing="0">
			
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.setStarting()">启动</td>
			</tr>
			<tr>
				<td style="cursor: default; border: outset 1;" align="center"
					onclick="parent.setShutdown()">取消启动</td>
			</tr>
		</table>
	</div>

		<form name="mainForm" method="post">

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
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp;自动化  >> 自动化控制  >> ACL配置管理</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        												<table border="0" id="table1" cellpadding="0" cellspacing="0" width="100%">
				        													<tr>
				        														<td colspan="2">
						        													<table cellspacing="0" border="1" bordercolor="#ababab">
								        												<tr height=28 style="background:#ECECEC" align="center" class="content-title">
								        													<td align="center"><input type=checkbox name="checkall" id="checkall" onclick="javascript:chkall()"  class="noborder"></td>
		        													<td align="center">序号</td>
		        													<td align="center">IP地址</td>
		        													
		        													<td align="center">设备类型</td>
		        													<td align="center">监视</td>
		        													<td align="center">操作</td>
								        												</tr>
								        												<%
		        													Huaweitelnetconf vo=null;
			        												for (int i = 0; i < list.size(); i++) {
																		vo = (Huaweitelnetconf) list.get(i);
																		String isMonitor="否";
																		if(ipVec.contains(vo.getIpaddress())){
																		isMonitor="是";
																		}
			        													%>
			        													<tr <%=onmouseoverstyle%>>
											        						 
											        						<td align='center'>
											        						
											        							<input type="checkbox"  class="noborder" name="checkbox" value="<%=vo.getIpaddress()%>"/>
																			
																			</td>
																			<td align='center'>
											        							<%=i+1%>
																			</td>
											        						<td align='center'>
											        							<%=vo.getIpaddress()%>
																			</td>
			        														
			        														<td align='center'><%=vo.getDeviceRender()%></td>
			        														<td align='center'><%=isMonitor %></td>
			        														<td align='center'><img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('2','<%=vo.getDeviceRender() %>','<%=vo.getIpaddress()%>') alt="右键操作"></td>
			        													</tr>
			        													<%} %>
		        												
								        											</table>
							        											</td>
				        													</tr>
																		</TABLE>
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