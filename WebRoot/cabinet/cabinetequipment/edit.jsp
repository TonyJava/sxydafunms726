<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.cabinet.model.CabinetEquipment"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%  
    String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  CabinetEquipment vo = (CabinetEquipment)request.getAttribute("vo");
  Hashtable cabinethash = (Hashtable)request.getAttribute("cabinethash");
  Hashtable roomhash = (Hashtable)request.getAttribute("roomhash");
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->


<!-- snow add for gatherTime at 2010-5-12 start -->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/addTimeConfig.js" charset="gb2312"></script>
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jquery-1.4.2.js" charset="gb2312"></script>
<script type="text/javascript">
	$(addTimeConfigRow);//addTimeConfigRow函数在application/resource/js/addTimeConfig.js中 
</script>
<script type="text/javascript">
$(document).ready(function(){
	$('#collecttype').bind('change',function(){
		if($('#collecttype').val()=='1')
		{
			$('#community_tr').show();
			$('#collectversion').show();
		}
		else
		{
			$('#community_tr').hide();
			$('#collectversion').hide();
		}
		//alert('abc');
	});
});
</script>
<!-- snow add for gatherTime at 2010-5-12 end -->






<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

	
 Ext.get("process").on("click",function(){
  	 //alert($('#collecttype').val()+'eee');
     var chk1 = checkinput("roomid","string","机房编号",15,false);
     var chk2 = checkinput("cabinetid","string","机柜编号",30,false);
     var chk3 = checkinput("unumbers","string","机柜U单元编号",30,false);
     	var cabinetid = document.getElementById("cabinetid");
		var roomid = document.getElementById("roomid");
		var unumbers = document.getElementById("unumbers");
     if(chk1&&chk2&&chk3)
     {
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
         mainForm.action = "<%=rootPath%>/cabinetequipment.do?action=edit";
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

//-- nielin add at 2010-07-27 start ----------------
function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBid&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}
//-- nielin add at 2010-07-27 end ----------------

//-- hukelei add at 2011-12-13 start ----------------
function setU(eventTextId , eventId){
	var cabinetid = document.getElementById("cabinetid");
	var roomid = document.getElementById("roomid");
	return CreateWindow('<%=rootPath%>/cabinetequipment.do?action=setunumber&cabinetid='+cabinetid.value+'&roomid='+roomid.value);
}
//-- hukelei add at 2010-12-13 end ----------------

//function toAllAdd(){
        // mainForm.target="alladd";
		// window.open("","alladd","toolbar=no,height=400, width= 500, top=200, left= 200,scrollbars=no"+"screenX=0,screenY=0")
		// mainForm.action='<%=rootPath%>/network.do?action=alladd';
		// mainForm.submit();
//}

function toAllAdd(){
	return CreateWindow('<%=rootPath%>/cabinetequipment.do?action=setBid');
}

function setNode(){
	
	return CreateWindow('<%=rootPath%>/cabinetequipment.do?action=setNode');
}

function setUser(){
	
	return CreateWindow('<%=rootPath%>/cabinetequipment.do?action=setUser');
}

function setBusinessSystem(){

	return CreateWindow('<%=rootPath%>/cabinetequipment.do?action=setBis');
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
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>

<script>
//-- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------
	/*
	* 此方法用于短信分时详细信息
	* 需引入 /application/resource/js/timeShareConfigdiv.js 
	*/
	//接受用户的列表
	var action = "<%=rootPath%>/user.do?action=setReceiver";
	// 获取短信分时详细信息的div
	function timeShareConfiginit(){
		var rowNum = document.getElementById("rowNum");
		rowNum.value = "0";
		// 获取设备或服务的分时数据列表,
		var timeShareConfigs = new Array();
		var smsConfigs = new Array();
		var phoneConfigs = new Array();
		<%	
			List timeShareConfigList = (List) request.getAttribute("timeShareConfigList");
			if(timeShareConfigList!=null&&timeShareConfigList.size()>=0){
			for(int i = 0 ; i < timeShareConfigList.size(); i++){	        
	            TimeShareConfig timeShareConfig = (TimeShareConfig) timeShareConfigList.get(i);
	            int timeShareConfigId = timeShareConfig.getId();
	            String timeShareType = timeShareConfig.getTimeShareType();
	            String timeShareConfigbeginTime = timeShareConfig.getBeginTime();
	            String timeShareConfigendTime = timeShareConfig.getEndTime();
	            String timeShareConfiguserIds = timeShareConfig.getUserIds();
	            
	    %>
	            timeShareConfigs.push({
	                timeShareConfigId:"<%=timeShareConfigId%>",
	                timeShareType:"<%=timeShareType%>",
	                beginTime:"<%=timeShareConfigbeginTime%>",
	                endTime:"<%=timeShareConfigendTime%>",
	                userIds:"<%=timeShareConfiguserIds%>"
	            });
	    <%
	        }
	        }
	    %>   
	    for(var i = 0; i< timeShareConfigs.length; i++){
	    	var item = timeShareConfigs[i];
	    	if(item.timeShareType=="sms"){
	    		smsConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    	if(item.timeShareType=="phone"){
	    		phoneConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    }
		timeShareConfig("smsConfigTable",smsConfigs);
		timeShareConfig("phoneConfigTable",phoneConfigs);
	}
//---- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------------------
</script>


</head>
<body id="body" class="body" onload="initmenu();">

<!-- 这里用来定义需要显示的右键菜单 -->
	<div id="itemMenu" style="display: none";>
	<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
		style="border: thin;font-size: 12px" cellspacing="0">
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.edit()">修改信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.detail();">监视信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.cancelmanage()">取消监视</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.addmanage()">添加监视</td>
		</tr>		
	</table>
	</div>
	<!-- 右键菜单结束-->

	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id" value="<%=vo.getId()%>">
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
											                	<td class="add-content-title">3D机房 >> 机柜设备配置 >> 编辑</td>
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
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
					<TBODY>
						<%
    
       
       String cabinetname = "";
       if(cabinethash.containsKey(vo.getRoomid()+":"+vo.getCabinetid()))  cabinetname = (String) cabinethash.get(vo.getRoomid()+":"+vo.getCabinetid());  
       String roomname = "";
       if(roomhash.containsKey(vo.getRoomid()))  roomname = (String) roomhash.get(vo.getRoomid()); 
       %>
						<tr >						
							<TD nowrap align="right" height="24" width="10%">所属机柜&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="cabinetname" id="cabinetname" maxlength="50" size="20" class="formStyle" value="<%=cabinetname %>">
								<input type="hidden" name="cabinetid"  id="cabinetid" maxlength="50" size="20" class="formStyle" value="<%=vo.getCabinetid() %>">
								<input type="hidden" name="roomid" id="roomid" maxlength="50" size="20" class="formStyle" value="<%=vo.getRoomid() %>">
								<font color="red">&nbsp;*</font>&nbsp;&nbsp;<a href="#" onclick="toAllAdd()">选择</a></TD>						
							<TD nowrap align="right" height="24" width="10%">U占用&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="unumbers" id="unumbers" maxlength="50" size="20" class="formStyle" value="<%=vo.getUnmubers() %>">
								<font color="red">&nbsp;*</font>&nbsp;&nbsp;<a href="#" onclick="setU()">选择</a></TD>															
						</tr>
						<tr style="background-color: #ECECEC;" id='community_tr'>				
							<TD nowrap align="right" height="24" width="10%">设备名称&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="nodename" id="nodename" maxlength="50" size="20" class="formStyle" value="<%=vo.getNodename() %>">
								<input type="hidden" name="nodeid" id="nodeid" maxlength="50" size="20" class="formStyle" value="<%=vo.getNodeid() %>">
								<font color="red">&nbsp;*</font>&nbsp;&nbsp;<a href="#" onclick="setNode()">选择</a></TD>						
							<TD nowrap align="right" height="24" width="10%">设备描述&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="nodedescr" id="nodedescr" maxlength="50" size="20" class="formStyle" value="<%=vo.getNodedescr() %>">
								</TD>															
						</tr>
						<tr  id='community_tr'>				
							<TD nowrap align="right" height="24" width="10%">联系人&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="contactname" id="contactname" maxlength="50" size="20" class="formStyle" value="<%=vo.getContactname() %>">
								<input type="hidden" name="operid" id="operid" maxlength="50" size="20" class="formStyle" value="<%=vo.getOperid() %>">
								&nbsp;&nbsp;<a href="#" onclick="setUser()">选择</a></TD>						
							<TD nowrap align="right" height="24" width="10%">联系人电话&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="contactphone" id="contactphone" maxlength="50" size="20" class="formStyle" value="<%=vo.getContactphone() %>">
								</TD>															
						</tr>
						<tr style="background-color: #ECECEC;" id='community_tr'>				
							<TD nowrap align="right" height="24" width="10%">联系人电子邮箱&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="contactemail" id="contactemail" maxlength="50" size="20" class="formStyle" value="<%=vo.getContactemail()%>">
							</TD>
							<TD nowrap style="display:none" align="right" height="24" width="10%">所属业务系统&nbsp;</TD>				
							<TD nowrap style="display:none"  width="40%" >&nbsp;
								<input type="text" name="businessName" id="businessName" maxlength="50" size="20" class="formStyle" value="<%=vo.getBusinessName() %>">
								<input type="hidden" name="bid" id="bid" maxlength="50" size="20" class="formStyle" value="<%=vo.getBusinessid() %>">
								<font color="red">*</font>&nbsp;&nbsp;<a href="#" onclick="setBusinessSystem()">选择</a>
							</TD>																		
						</tr>
						<tr >
							<td colspan="4" align='center'>
								<div id="msg" style="display:none"><font color="#FF0000">需要一点时间,请稍候...</font></div>
							</td>
						</tr>			
						<tr>
							<td nowrap colspan="4" height="1" bgcolor="#8EADD5"></td>
						</tr>
															
															<tr>
																<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																<td><input type="hidden" id="rowNum" name="rowNum"></td>
																<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
															
															</tr>
															<tr>
																<TD nowrap colspan="4" align=center>
																<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																	<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
																</TD>	
															</tr>	
							</TBODY>
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
</BODY>

<script>

function unionSelect(){
	var type = document.getElementById("type");
	var ostype = document.getElementById("ostype");
	
	var typeValue = type.value;
	
	
	ostype.length = 0;
	
	if(typeValue == "1" || typeValue == "2" || typeValue == "3"){
		ostype.options[1] = new Option( "Cisco" ,"1");
		ostype.options[2] = new Option("H3C" , "2" );
		ostype.options[3] = new Option("Entrasys", "3" );
		ostype.options[4] = new Option("Radware", "4" );
		ostype.options[5] = new Option("MaiPu", "10");
		ostype.options[6] = new Option("RedGiant" , "11");
		ostype.options[7] = new Option("NorthTel", "12");
		ostype.options[8] = new Option("D-Link", "13");
		ostype.options[9] = new Option("D-BDCom" , "14");
		ostype.options[10] = new Option("ZTE" , "16");
		ostype.options[11] = new Option("Motorola " , "17");
		ostype.options[12] = new Option("Brocade " , "28");
	}else if(typeValue == "4"){
		ostype.options[1] = new Option("Windows", "5" );
		ostype.options[2] = new Option("AIX", "6" );
		ostype.options[3] = new Option("HP UNIX", "7" );
		ostype.options[4] = new Option("SUN Solaris", "8" );
		ostype.options[5] = new Option("Linux" ,"9" );
		ostype.options[6] = new Option("AS400", "15");
		ostype.options[7] = new Option("SCOUNIXWARE", "20");
		ostype.options[8] = new Option("ScoOpenServer", "21");
	}else if(typeValue == "8"){
		ostype.options[1] = new Option("NOKIA", "23" );
		ostype.options[2] = new Option("netscreen", "24" );
		ostype.options[3] = new Option("pix", "25" );
		ostype.options[4] = new Option("hillstone", "26" );
	}else if(typeValue == "9"){
		ostype.options[1] = new Option("ATM", "17" );
	}else if(typeValue == "12"){
		ostype.options[1] = new Option("Array Networks", "18" );
	}else if(typeValue == "11"){
		ostype.options[1] = new Option("F5", "27" );
	}else if(typeValue == "10"){
		ostype.options[1] = new Option("Cisco", "22" );
	}
	
}

unionSelect();

</script>

</HTML>                                                     