  <%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.afunms.application.manage.PerformancePanelManager"%>
<%@page import="com.afunms.application.model.PerformancePanelModel"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	String pageFlag = request.getParameter("pageFlag");
	String freshTimeMinute = (String)request.getAttribute("freshTimeMinute");
	if(freshTimeMinute == null){
		freshTimeMinute = "300";//页面默认为60秒刷新一次
	}
	List<String> tableList = null;;
	if(("1").equals(pageFlag)){
		freshTimeMinute = request.getParameter("freshTimeMinute");
		
		//监控管理列表
		//性能监控面板的table表格
		rootPath = request.getContextPath();
		tableList = PerformancePanelManager.getInstance().getPerformanceList(); 
		//返回监控管理的界面
	}else{
		tableList = (ArrayList<String>)request.getAttribute("tableList");
	}
	String m = (Integer.parseInt(freshTimeMinute) / 60) + ":" ;//分
    String s = Integer.parseInt(freshTimeMinute) % 60 +""; //秒
    if (Integer.parseInt(s) < 10) {
    	s = "0" + s;
    }
	String spanTimeValue = m+s;
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
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
	#全局表格样式
	.totalTable{
		background-color: white;
		width: 100%;
		height: 100%;
	}
.data_content{
	height:20px;
	border-top:1px dotted #999999;
	color:#333333;
	padding:2px;
}
.data{
	border:1px solid #679AD4;
	width:100%;
}
</style>

<script type="text/javascript">
	/***********************************************************************************************************
	初始化按钮颜色
	***********************************************************************************************************/
	function initmenu(){
		refreshStyle('xnmb');
		initRefTime();
	}
	
	/***********************************************************************************************************
	刷新左侧按钮超链接的样式
	参数
		liId 左侧列表链接的ID
	************************************************************************************************************/
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
	
	/***********************************************************************************************************
	创建性能面板，打开创建弹出页
	************************************************************************************************************/
	function createXnmb(){
		//var returnValue= showModalDialog('<%=rootPath%>/performance/addXnmb.jsp','','dialogWidth:520px;dialogHeight:510px;help:no;center:yes;resizable:no;status:no;scroll:no'); 
		document.getElementById("mainForm").action = "<%=rootPath%>/performancePanel.do?action=addperformancePanel";
		document.getElementById("mainForm").submit();
	}
	
	/***********************************************************************************************************
	加减符号点击后，隐藏/显示表格
	参数
		tableId 需要隐藏或者显示表格的ID
		imgId   链接（加减符号的ID）
	***********************************************************************************************************/
	function display(tableId,imgId){
		if(document.getElementById(tableId).style.display != 'none'){
			document.getElementById(tableId).style.display = 'none';
			document.getElementById(imgId).src = '<%=rootPath%>/resource/image/tree/plus.gif';
		}else{
			document.getElementById(tableId).style.display = '';
			document.getElementById(imgId).src = '<%=rootPath%>/resource/image/tree/minus.gif';
		}
	}
	
	/***********************************************************************************************************
	删除面板
	***********************************************************************************************************/
	function deletePanel(){
		document.getElementById("mainForm").action = "<%=rootPath%>/performancePanel.do?action=delete";
		document.getElementById("mainForm").submit();
	}
	
	/***********************************************************************************************************
	配置监控的设备  弹出框
	参数：
		deviceType 设备类型
		panelName  面板名称
	***********************************************************************************************************/
	function editPanelDevice(deviceType, panelName){
		 var result = showModalDialog("<%=rootPath%>/performancePanel.do?action=toEditPanelDevice&deviceType="+deviceType+"&panelName="+encodeURIComponent(panelName), "", "dialogWidth:700px;dialogHeight:500px;dialogLeft:200px;dialogTop:150px;center:yes;help:yes;resizable:yes;status:yes","");
		 if(result == 1) {
			window.location.reload(); 
		 }
	}
	
	/***********************************************************************************************************
	配置面板的监控指标 
	参数
		deviceType 设备类型
		panelName  面板名称
	***********************************************************************************************************/
	function editPanelIndicators(deviceType, panelName){
		 var result = showModalDialog("<%=rootPath%>/performancePanel.do?action=toEditPanelIndicators&deviceType="+deviceType+"&panelName="+encodeURIComponent(panelName), "", "dialogWidth:700px;dialogHeight:500px;dialogLeft:200px;dialogTop:150px;center:yes;help:yes;resizable:yes;status:yes");
		 if(result == 1) {
			window.location.reload(); 
		 }
	}
</script>
<script language="javascript"> 
	var baseTime = <%=freshTimeMinute%>;//默认5分钟
    var refTime;
        
	/***********************************************************************************************************
	修改定时刷新页面的值
	参数：
		timeMinute  定时刷新的时间，单位为毫秒
	***********************************************************************************************************/
	function refreshPage(){
		var refreshTime = $('#refreshtime').val();
		document.getElementById('freshTimeMinute').value = refreshTime;
		window.location.href = "<%=rootPath%>/performance/xnmb.jsp?freshTimeMinute="+refreshTime+"&pageFlag=1"; 
	}
	
	/***********************************************************************************************************
	倒计时显示功能
	***********************************************************************************************************/
	function refreshTime() {
	    var m = parseInt(baseTime / 60) + ":" //分
	    var s = baseTime % 60; //秒
	    if (s < 10) {
	    	s = "0" + s;
	    }
	    $('#spanTime').html(m + s);
	    baseTime--;
	    if (baseTime == 0) {
	        refreshPage();
	    }
	}
	
	function initRefTime() {
	    refTime = setInterval("refreshTime()", 1000);
	}
	
	/***********************************************************************************************************
	回显复选框
	***********************************************************************************************************/
	function setSelectItem(objId,strValue){
		var options = document.getElementById(objId);
	    var option=options.getElementsByTagName("option");  
	    var str = "" ;  
	    for(var i=0;i<option.length;++i){ 
	    	if(option[i].value == baseTime){  
	    		option[i].selected = true; 
	    		//alert('aaa'); 
	    	}  
	    }
	}
	
	/***********************************************************************************************************
	确认删除当前面板
	参数：
		panelName :面板名称
	***********************************************************************************************************/
	function confrimDeletePanel(panelName){
		var bln = window.confirm("确定删除面板："+panelName+"?");
		if(bln == true){
			document.getElementById('mainForm').action = 'performancePanel.do?action=delete&panelName='+encodeURIComponent(panelName);
			document.getElementById('mainForm').submit();
		}
	}
	
	/***********************************************************************************************************
	跳转到详细页面
	参数:
		url 跳转到详细页的超链接
	***********************************************************************************************************/
	function toDetailPage(url){
		window.parent.parent.window.document.getElementById('mainFrame').src = url;
	}
</script>
</head>
<body onload="initmenu();" style="background-color: white;">
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
					                	<td class="content-title">&nbsp;性能 >> 性能监视面板 </td>
					                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
					       			</tr>
					        	</table>
				        	</td>
			        	</tr>
			        	<tr>
							<td colspan="3">
								<table bgcolor="#ECECEC">
									<tr align="right" valign="middle">
										<td align="left" class="content-title">&nbsp;&nbsp;系统快照性能数据浏览</td>
										<td height="21" align="right" valign="middle">
											<a href="#"onClick="createXnmb();">新建监控分组</a>
											&nbsp;&nbsp;<font class="title">|</font>&nbsp;&nbsp;
	                                        <font class="title">更新周期：</font>
	                                        <input type="hidden" id="freshTimeMinute" name="freshTimeMinute"/>
	                                        <select id="refreshtime" name="refreshtime" onchange="refreshPage();" style="WIDTH: 76px">
	                                            <option value="60">1分钟</option>
	                                            <option value="180">3分钟</option>
	                                            <option value="300">5分钟</option>
	                                            <option value="600">10分钟</option>
	                                            <option value="1200">20分钟</option>
	                                            <option value="1800">30分钟</option>
	                                        </select>&nbsp;
	                                        <script language="javascript">
                                           	 	setSelectItem("refreshtime", "300");
                                        	</script>
	                                        <font class="title">倒计时：</font><span id="spanTime"><%=spanTimeValue %></span>
	                                        &nbsp;&nbsp;<font class="title">|</font>&nbsp;&nbsp;
	                                        <font class="title">切换呈现模板：</font>
	                                        <select name="templateid" onchange="javascript:changeTemplate(options[selectedIndex].value);">
	                                            <option value="0" selected>默认模板</option>
	                                        </select>&nbsp;&nbsp;&nbsp;
										</td>
									</tr>
								</table>
							</td>
			        	</tr>
			        	<tr>
							<td colspan="3">
								<table>
									<tr align="right" valign="middle">
										<td height="21" align="right" valign="bottom">
											<font class="title">
						                                            性能灯示例：
						                                            紧急告警：<img src="<%=PerformancePanelManager.getCurrentStatusImage(3)%>" alt="紧急告警">&nbsp;
						                                            严重告警：<img src="<%=PerformancePanelManager.getCurrentStatusImage(2)%>" alt="严重告警">&nbsp;
						                                            普通告警：<img src="<%=PerformancePanelManager.getCurrentStatusImage(1)%>" alt="普通告警">&nbsp;
						                                            正常状态：<img src="<%=PerformancePanelManager.getCurrentStatusImage(0)%>" alt="正常状态">
						                    </font>&nbsp;&nbsp;
										</td>
									</tr>
								</table>
							</td>
			        	</tr>
			        	<%
			        		for(int i=0; i<tableList.size(); i++){
			        		String table = tableList.get(i);
			        	%>
			        		<tr>
			        			<td width="10px;">&nbsp;</td>
			        			<td><%=table %></td>
			        			<td width="10px;">&nbsp;</td>
			        		</tr>
			        	<%
			        		}
			        	 %>
			        </table>
	        	</td>
	        </tr>
	     </table>
	</form>
</body>
</html>