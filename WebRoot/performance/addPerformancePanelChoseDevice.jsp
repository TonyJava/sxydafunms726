<%@ page language="java" contentType="text/html; charset=gbk"
    pageEncoding="gbk"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.application.model.DBVo"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%
	String rootPath = request.getContextPath();
	String[][] nodeAlarmsArry = (String[][])request.getAttribute("nodeAlarmsArry");
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
	/**
	*初始化按钮颜色
	*/
	function initmenu(){
		//refreshStyle('xnmb');
		deviceTree();
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
	
	function deviceTree(){
		ddtree = new Tree("deviceTree","100%","100%",0);
		ddtree.setImagePath("<%=rootPath%>/resource/image/tree/");
		ddtree.setDelimiter(",");
		ddtree.setOnCheckHandler(onNodeCheck);
		ddtree.enableCheckBoxes(1);
		ddtree.insertNewItem("","root","设备资源树", 0, "", "base.gif","base.gif", "base.gif");
		<%
			List deviceList = (ArrayList)request.getAttribute("deviceList");
			if(deviceList != null){
				for(int i=0; i<deviceList.size(); i++){
					Object obj = deviceList.get(i);
					if(obj instanceof HostNode){
						HostNode node = (HostNode)obj;
		%>
						ddtree.insertNewItem("root","<%=node.getId()%>","<%=node.getAlias()%>", 0, "", "","", "");
		<%
					}else if(obj instanceof DBVo){
						DBVo node = (DBVo)obj;
		%>
						ddtree.insertNewItem("root","<%=node.getId()%>","<%=node.getAlias()%>", 0, "", "","", "");
		<%
					}
				}
			}
		%>
		
	}
	
	function onNodeCheck(nodeid){  
		var selectedDevice = document.getElementById("selectedDevice");
		//selectedDevice清空option
		selectedDevice.length = 0;
		var nodeids = ddtree.getAllChecked().toString();
		var nodeidsArry = nodeids.split(',');
		for(var i=0; i<nodeidsArry.length; i++){
			var nodeid = nodeidsArry[i];
			if(nodeid == 'root'){
				continue;
			}
			//将子节点加到右侧的select框中
			var itemText = ddtree.getItemText(nodeid);//根据treeid得到text文本
			var item=document.createElement("option"); 											
			item.text=itemText;   
			item.value=nodeid;                       					
			selectedDevice.add(item);  
		}		
		document.getElementById('deviceIds').value = nodeids
	} 

	function nextPage(){
		var nodeids = ddtree.getAllChecked().toString();
		if(nodeids.trim() == ''){
			alert('至少选中一条数据!');
			return;
		}
		document.getElementById("mainForm").action = "performancePanel.do?action=thirdStep"
		document.getElementById("mainForm").submit();
	}
	
	function lastPage(){
		history.go(-1);
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
					                	<td class="content-title">&nbsp;性能 >> 性能监视面板 >>新建分组  第二步 </td>
					                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
					       			</tr>
					        	</table>
				        	</td>
			        	</tr>
			        	<tr>
			        		<td>&nbsp;</td>
							<td style="width: 100%;">
								<table id="formTab">
									<tr align="left" valign="middle">
										<td align="left"  colspan="2" class="leftLine">添加监控设备-类型（网络设备）</td>
									</tr>
									<tr align="right" valign="top">
                                        <TD style="height: 100%" align="left" width="60%">
                                            <fieldset style="WIDTH:100%;vertical-align:top;height: 100%;overflow: auto;"  align="center">
                                                <legend>
                                                		设备选择
                                                </legend>
                                                <div id="deviceTree"></div>
                                            </fieldset>
                                            <input type="hidden" id="panelName" name="panelName" value="<%=(String)request.getAttribute("panelName") %>"/>
                                            <input type="hidden" id="deviceIds" name="deviceIds"/>
                                            <input type="hidden" id="deviceType" name="deviceType" value="<%= (String)request.getAttribute("deviceType")%>"/>
                                        </TD>
                                        <TD class="TdRightContent">&nbsp;</TD>
                                        <TD  style="height: 100%" align="center" width="40%">
                                            <fieldset style="WIDTH:100%" align="center">
                                                <legend>选择的设备</legend>
                                                <select id="selectedDevice" multiple name="selectedDevice" size="22" style="WIDTH:expression(document.body.clientWidth / 2 - 50)"></select>
                                            </fieldset>
                                        </TD>
                                    </TR>
                                    <TR class="menu">
                                        <TD colspan="3" class="TdRightContent" width="100%" align="center">
                                            <input type="button" id="lastButton" onclick="lastPage();"value="上一步">&nbsp;&nbsp;
											<input type="button" id="nextButton" onclick="nextPage();" value="下一步">
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