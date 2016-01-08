<%@ page language="java" contentType="text/html; charset=gbk"
    pageEncoding="gbk"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/fusionChart/Contents/Style.css" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/js/tree/common.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/tree/Tree.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<style type="text/css">
	.totalTable{
		background-color: white;
		width: 100%;
		height: 100%;
	}
	.leftLine{
		vertical-align: top;
	}

	#formTab .tr{
		width: 100%;
	}
</style>
<script type="text/javascript">
	//面板名称是否被占用
	var isExist = false;//默认不被占用 

	/**
	*初始化按钮颜色
	*/
	function initmenu(){
		refreshStyle('xnmb');
	}
	
	function refreshStyle(liId){
		var varsLi = window.parent.tabMenuFrame.document.getElementsByTagName('li');
		for(var i=0; i<varsLi.length; i++){
			var varLi = varsLi[i];
			if(varLi.id == liId){
				window.parent.tabMenuFrame.document.getElementById(varLi.id).className = "menu-title-over";
			}else{
				window.parent.tabMenuFrame.document.getElementById(varLi.id).className = "menu-title";
			}
		}
	}
	
	/**
	*创建性能面板，打开创建弹出页
	*/
	function createXnmb(){
		//var returnValue= showModalDialog('<%=rootPath%>/performance/addXnmb.jsp','','dialogWidth:520px;dialogHeight:510px;help:no;center:yes;resizable:no;status:no;scroll:no'); 
		window.getElementById("mainForm").action = "performancePanel.do?action=addperformancePanel"
		window.getElementById("mainForm").submit();
	}
	
	function changeTree(){
		
		
	}
	
	function nextPage(){
		var panelName = $("#panelName").val().trim();
		if(panelName == ''){
			alert('名称不能为空!');
			return;
		}
		$.ajax({
			type:"POST",
			dataType:"json",
			data:"panelName="+encodeURIComponent(panelName)+"&nowtime="+(new Date()),
			url:"<%=rootPath%>/performancePanelAjaxManager.ajax?action=checkPanelName",
			success:function(data){
				if(data.isExist == 'true'){
					$("#spanMsg").html("* 该名称已被占用");
					isExist = true;
				}else{
					$("#spanMsg").html("* 该名称可以使用");
					$("#spanMsg").css('color','green');
					isExist = false;
					document.getElementById("mainForm").action = "performancePanel.do?action=secondStep"
					document.getElementById("mainForm").submit();
				}
			}	
		});
	}
	
	/**
	*检查面板名称是否合法
	*/
	function checkPanelName(){
		var panelName = $("#panelName").val().trim();
		if(panelName == ''){
			alert('名称不能为空!');
			return;
		}
		$.ajax({
			type:"POST",
			dataType:"json",
			data:"panelName="+encodeURIComponent(panelName)+"&nowtime="+(new Date()),
			url:"<%=rootPath%>/performancePanelAjaxManager.ajax?action=checkPanelName",
			success:function(data){
				if(data.isExist == 'true'){
					$("#spanMsg").html("* 该名称已被占用");
					isExist = true;
				}else {
					$("#spanMsg").html("* 该名称可以使用");
					$("#spanMsg").css('color','green');
					isExist = false;
				}
			}	
		});
	}
	
	function cancel(){
		document.getElementById("mainForm").action = "performancePanel.do?action=panelList"
		document.getElementById("mainForm").submit();
	}
</script>
</head>
<body onload="initmenu();" style="background-color: #ECECEC;">
	<form id="mainForm" method="post" name="mainForm">
		<table>
			<tr>
				<td valign=top>
					<table>
						<tr>
							<td colspan="3">
								<table id="content-header" class="content-header">
				                	<tr>
					                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
					                	<td class="content-title">&nbsp;性能 >> 性能监视面板 >>新建分组  第一步 </td>
					                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
					       			</tr>
					        	</table>
				        	</td>
			        	</tr>
			        	<tr>
			        		<td>&nbsp;</td>
							<td style="width: 950px;">
								<table id="formTab">
									<TR class="menu">
                                        <TD class="TdLeftContent" align="right">分组名称：</TD>
                                        <TD class="TdRightContent" align="left">
                                            <input type="text" onblur="checkPanelName();" id="panelName" name="panelName" size="100" style="WIDTH: 150px" autocomplete="off">&nbsp;
                                            <span id="spanMsg" style="color: red;">*&nbsp;名称不能重复</span>
                                        </TD>
                                    </TR>
                                    <tr align="right" valign="middle" >
										<td align="right" class="leftLine">
											设备类别：
										</td>
										<td  align="left" valign=top>
											<select name="deviceType" autocomplete="off">
												<option value="net">网络设备</option>
												<option value="host">服务器</option>
												<option value="db">数据库</option>
											</select>
										</td>
									</tr>
                                    <TR class="menu">
                                    	<td width="50%">&nbsp;</td>
                                        <TD class="TdRightContent" align="left" width="100%">
                                           <input type="button" id="nextButton" onclick="nextPage();" value="下一步">&nbsp;&nbsp;
                                           <input id="cancel_button" type="button" onclick="cancel();" value="取  消">
                                        </TD>
                                    </TR>
								</table>
							</td>
							<td>&nbsp;</td>
			        	</tr>
			        </table>
	        	</td>
	        </tr>
	     </table>
	</form>
</body>
</html>