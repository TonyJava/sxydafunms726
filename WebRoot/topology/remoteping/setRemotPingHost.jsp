<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.topology.model.RemotePingHost"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  HostNode node = (HostNode)request.getAttribute("node");
  RemotePingHost remotePingHost = (RemotePingHost)request.getAttribute("remotePingHost");
  %>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>


<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  
	<%if(remotePingHost!=null){
		%>
			document.getElementById("userName").value = "<%=remotePingHost.getUsername()%>";
			document.getElementById("password").value = "<%=remotePingHost.getPassword()%>";
			document.getElementById("loginPrompt").value = "<%=remotePingHost.getLoginPrompt()%>";
			document.getElementById("passwordPrompt").value = "<%=remotePingHost.getPasswordPrompt()%>";
			document.getElementById("shellPrompt").value = "<%=remotePingHost.getShellPrompt()%>";
		<%
	}%>
	setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	Ext.get("process").on("click",function(){
  
     var chk1 = checkinput("userName","string","用户名",30,false);
     var chk2 = checkinput("password","string","密码",15,false);
     var chk3 = checkinput("loginPrompt","string","登陆提示符",15,false);
     var chk4 = checkinput("passwordPrompt","string","密码提示符",15,false);
     var chk5 = checkinput("shellPrompt","string","shell提示符",15,false);
          
     if(chk1&&chk2&&chk3&&chk4&&chk5)
     {      
            Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/remotePing.do?action=addRemotePingHost";
        	mainForm.submit();
     }
       // mainForm.submit();
 });
 
 	
	
});

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

<script type="text/javascript">
	function openWindow(url){
		window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}

	function setRemotePingNode(){
		var node = "<%=node.getId()%>";
		var url = "<%=rootPath%>/remotePing.do?action=setRemotePingNode&node="+node;
		openWindow(url);
	}
</script>


</head>
<body id="body" class="body" onload="initmenu();">

	<div id="loading">
		<div class="loading-indicator">
			<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />
			Loading...
		</div>
	</div>

	<form id="mainForm" method="post" name="mainForm">
		<input type="hidden" id="nodeId" name="nodeId" value="<%=node.getId()%>">
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
											                	<td class="add-content-title">应用 >> 设置为远程Ping服务器 >> 添加</td>
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
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																	
																	<tr style="background-color: #ECECEC;">						
																			<TD nowrap align="right" height="24" width="10%">设备名称&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="nodeName" size="20" class="formStyle" value="<%=node.getAlias()%>" disabled="disabled"><font color='red'>*</font></TD>															
																			<TD nowrap align="right" height="24">设备IP&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="nodeIP" size="20" class="formStyle" value="<%=node.getIpAddress()%>" disabled="disabled"><font color='red'>*</font></TD>						
																	</tr>
																	<tr>						
																			<TD nowrap align="right" height="24" width="10%">用户名&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="userName" size="20" class="formStyle"><font color='red'>*</font></TD>															
																			<TD nowrap align="right" height="24">密码&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="password" name="password" size="20" class="formStyle"><font color='red'>*</font></TD>						
																	</tr>	
																	<tr style="background-color: #ECECEC;">						
																			<TD nowrap align="right" height="24" width="10%">登录提示符&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="loginPrompt" size="20" class="formStyle"><font color='red'>*</font></TD>															
																			<TD nowrap align="right" height="24">密码提示符&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="passwordPrompt" size="20" class="formStyle"><font color='red'>*</font></TD>						
																	</tr>	
																	<tr>						
																			<TD nowrap align="right" height="24" width="10%">shell提示符&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="text" name="shellPrompt" size="20" class="formStyle"><font color='red'>*</font></TD>															
																			<TD nowrap align="right" height="24">&nbsp;</TD>				
																			<TD nowrap width="40%">&nbsp;<input type="button" onclick="setRemotePingNode()" name="setPingNode" id="setPingNode" value="设置Ping节点"></TD>						
																	</tr>										                 										                      								

									            							
															<tr>
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
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
				        								<table id="detail-content-footer" class="detail-content-footer">
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
									<tr>
										<td>
											
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