<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import ="com.afunms.application.course.dao.Lsfclassdao"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String select = (String) session.getAttribute("select");
	
	
	Lsfclassdao dao=new Lsfclassdao();
	
	String sel =dao.getRoleBox(-1);
	dao.close();
	
	
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

		<!--nielin add for timeShareConfig at 2010-01-04 start-->
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>
		<!--nielin add for timeShareConfig at 2010-01-04 end-->

		<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
     var chk1 = checkinput("nodeid","int","服务器ID",10,false);
     var chk2 = checkinput("enable","int",10,false);
     var chk3 = checkinput("logflg","int","检查日志个数",10,false);
     var chk4 = checkinput("jid","int",10,false);
      if(chk1&&chk2&&chk3&&chk4)
     {      
            Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/lsfprocessnode.do?action=add";
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
	return CreateWindow('<%=rootPath%>/lsfprocessnode.do?action=setReceiver&event='+event.id+'&value='+event.value);
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

<script type="text/javascript">
			function showup(){
				var url="<%=rootPath%>/lsfprocessnode.do?action=netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			function verLogin(){
			
			var username=$('#user').val();
var suuser=$('#suuser').val();
var pwd=$('#password').val();
var supassword=$('#supassword').val();
var ipaddress=$('#ipaddress').val();
var port=$('#port').val();
var promtp=$('#defaultpromtp').val();
var type=document.getElementById("deviceVender").value;
$("#loading").show();

 $.ajax({
			type:"POST",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=verifyLogin",
			data:"username="+username+"&pwd="+pwd+"&type="+type+"&suuser="+suuser+"&supassword="+supassword+"&ipaddress="+ipaddress+"&promtp="+promtp+"&port="+port+"&random="+Math.random(),
			success:function(data){
			$("#loading").hide();
			alert(data.result);
			}
		});
}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu()">
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
																		应用 >> 集群设置 >> 集群添加
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
																			
																				<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="40%">
																					 集群
																				</TD>
																				<TD>
																					<%=sel %>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="40%">
																					服务器ID
																				</TD>
																		
																				<TD nowrap>
																					&nbsp;
																					<input name="nodeid" type="text" size="21" class="formStyle" id="nodeid" readonly> 
																					<input type="button" value="选择网络设备" onclick="showup()">
																					<font color="red">&nbsp;* </font>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">是否采集&nbsp;</TD>				
																            		<TD nowrap>&nbsp;
																            			<select name="enable" id="enable">
																            				<option value="1" selected>是</option>
																            				<option value="0">否</option>
																            			</select>
																            		</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">检查日志个数&nbsp;</TD>				
																            		<TD nowrap>&nbsp;
																            			<select name="logflg" id="logflg">
																            				<option value="1" selected>是</option>
																            				<option value="0">否</option>
																            			</select>
																            		</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">jid&nbsp;</TD>				
																            		<TD nowrap>&nbsp;
																            			<select name="jid" id="jid">
																            				<option value="1" selected>是</option>
																            				<option value="0">否</option>
																            			</select>
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