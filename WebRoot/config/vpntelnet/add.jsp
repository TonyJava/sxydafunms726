<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
    
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>

        <script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
    
		<script language="JavaScript" type="text/javascript">
 Ext.onReady(function()
{  
$("#loading").hide();
 Ext.get("vpnadd").on("click",function(){
   	var chk1 = checkinput("user","string","用户",50,false);
   	var chk2 = checkinput("port","string","端口",50,false);
   	var chk3 = checkinput("deviceVender","string","设备厂商",50,false);
    var chk6 = checkinput("ipaddress","string","ip地址",50,false);  
     
      if(chk1&&chk2&&chk3&&chk6)
     {      
          	Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=add";
        	mainForm.submit();
     }
 });	
});
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
function unionSelect(){
	var deviceVender = document.getElementById("deviceVender");
	var ostype = document.getElementById("ostype");
	
	var typeValue = deviceVender.value;
	
	
	ostype.length = 0;
	
	if(typeValue == "h3c"){
		ostype.options[1] = new Option("S8500", "S8500" );
		ostype.options[1] = new Option("H3C MSR3011", "H3C MSR3011" );
		ostype.options[1] = new Option("H3C MSR5060", "H3C MSR5060" );
		ostype.options[1] = new Option("H3C SR6608", "H3C SR6608" );
		ostype.options[1] = new Option("H3C S7510", "H3C S7510" );
		ostype.options[1] = new Option("Quidway S3552G", "Quidway S3552G" );
		ostype.options[1] = new Option("Quidway S3528G", "Quidway S3528G" );
	}else if(typeValue == "cisco"){
		ostype.options[1] = new Option("Cisco 2511", "Cisco 2511" );
		ostype.options[1] = new Option("Cisco Catalyst 2950T-24", "Cisco Catalyst 2950T-24" );
		ostype.options[1] = new Option("Cisco Catalyst 6509", "Cisco Catalyst 6509" );
		ostype.options[1] = new Option("Cisco Catalyst 2950-12", "Cisco Catalyst 2950-12" );
		ostype.options[1] = new Option("Cisco Catalyst 2950C-24", "Cisco Catalyst 2950C-24" );
		ostype.options[1] = new Option("Cisco WS-C3550-48-SMI", "Cisco WS-C3550-48-SMI" );
	}else if(typeValue == "zte"){
		ostype.options[1] = new Option("ZXR10 M6000", "ZXR10 M6000" );
		ostype.options[2] = new Option("ZXR10 T600", "ZXR10 T600" );
		ostype.options[3] = new Option("ZXR10 5900E", "ZXR10 5900E" );
		ostype.options[4] = new Option("ZXR10 3884", "ZXR10 3884" );
		ostype.options[5] = new Option("ZXR10 2900", "ZXR10 2900" );
	}
	
}

</script>


		<script type="text/javascript">
			function showup(){
				var url="<%=rootPath%>/vpntelnetconf.do?action=netip";
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
	<body id="body" class="body" >



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
																	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
																	<td class="add-content-title">
																		远程登录设置 添加
																	</td>
																	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
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
																				<TD nowrap align="right" height="24">
																					ip地址&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="ipaddress" type="text" size="21" class="formStyle" id="ipaddress" readonly> 
																					<input type="button" value="选择网络设备" onclick="showup()">
																					<font color="red">&nbsp;* </font>
																				</TD>
																				<td align="right" height="20">
																					端口&nbsp;
																				</td>
																				<td colspan="3" align="left">
																					&nbsp;
																					<input name="port" type="text" size="16" id="port" class="formStyle" value="23">
																					<font color="red">&nbsp;* </font>
																				</td>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					设备生产商&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<select name="deviceVender" id="deviceVender" style="width:250px"  onchange="unionSelect();">
																						<option value="">
																						</option>
																						<option value="h3c">
																							H3C/HUAWEI
																						</option>
																						<option value="cisco">
																							cisco
																						</option>
																						<option value="zte">
																							ZTE
																						</option>
																					</select>
																					<font color="red">&nbsp;* </font>
																				</TD>
																				<TD nowrap align="right" height="24" width="10%">
																					操作系统型号&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<select name="ostype" id="ostype" style="width:250px">
																					</select>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">
																					提示符号&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="defaultpromtp" type="text" size="40" id="defaultpromtp" class="formStyle" value="#">
																					<font color="red">&nbsp;* </font>
																				</TD>
																				<td align="right" height="20">
																					&nbsp;启用vpn采集
																				</td>
																				<td colspan="3" align="left">
																					&nbsp;
																					<select name="enablevpn" style="width:100px">
																						<option value="1">
																							采集
																						</option>
																						<option value="0" selected="selected">
																							不采集
																						</option>
																					</select>


																				</td>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					用户&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="user" id="user" size="40" class="formStyle">
																					<font color="red">&nbsp;*</font>
																				</TD>
																				<TD nowrap align="right" height="24">
																					密码&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input type="password" name="password" id="password" size="40" class="formStyle">
																					<font color="red">&nbsp;*
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24">
																					超级用户&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input name="suuser" type="text" size="22" id="suuser" class="formStyle" value="enable">
																				</TD>
																				<TD nowrap align="right" height="24">
																					超级用户密码&nbsp;
																				</TD>
																				<TD nowrap>
																					&nbsp;
																					<input type="password" name="supassword" id="supassword" size="24" class="formStyle">
																				</TD>
																			</tr>
																		
																			<tr>
																				<TD nowrap colspan="2" align=right>
																					<br>
			                                                                     <span class="loading-indicator" id="loading" > 正在验证...</span>
																				 <input type="button" value="验证登录" style="width: 65" id="verifyLogin" onclick="verLogin()">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																				</TD>
																				<TD  colspan=2 align=left>
																				<br>
																				 <input type="button" value="保 存" style="width: 50" id="vpnadd" onclick="#">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																				 <input type="reset" style="width: 50" value="返回" onclick="javascript:history.back(1)">
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
															<table id="detail-content-footer"
																class="detail-content-footer">
																<tr>
																	<td>
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0">
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