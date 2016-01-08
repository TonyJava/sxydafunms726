<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String startdate = sdf.format(new Date());
	String todate = sdf.format(new Date());
	List allbusiness = (List)request.getAttribute("allbusiness");
	
	String[] hostItems={"CPU","内存","连通性","响应度","磁盘"};
	String[] hostItemsId={"cpu","mem","ping","resp","disk"};
	String[] netItems={"CPU","内存","连通性","响应度","入口流速","出口流速"};
	String[] netItemsId={"cpu","mem","ping","resp","utilIn","utilOut"};
	
%>
<html>
<head>
	
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<style type="text/css"></style>
	<script type="text/javascript" src="<%=rootPath%>/js/tree/Tree.js"></script>
	<script type="text/javascript" src="<%=rootPath%>/js/tree/common.js"></script>
	<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
	<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
	<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
	<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
	<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
	<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
	<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
	<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
	<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
	<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<style>
			.x-tab-strip-top .x-tab-right {
				background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
			}
			
			.x-tab-strip-top .x-tab-left {
				background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
			}
			
			.x-tab-strip-top .x-tab-strip-inner {
				background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
			}
			
			.x-tab-panel-body {
				border-bottom-color: #EAEAEA;
				border-left-color: #EAEAEA;
				border-right-color: #EAEAEA;
				border-top-color: #EAEAEA;
			}
			
			.x-tab-panel-header {
				background-color: #EAEAEA;
				border-bottom-color: #EAEAEA;
				border-left-color: #EAEAEA;
				border-right-color: #EAEAEA;
				border-top-color: #EAEAEA;
			}
			
			.x-tab-panel-header-plain .x-tab-strip-spacer {
				background-color: #EAEAEA;
				border-bottom-color: #EAEAEA;
				border-left-color: #EAEAEA;
				border-right-color: #EAEAEA;
				border-top-color: #EAEAEA;
			}
			
			.x-panel {
				border-bottom-color: #EAEAEA;
				border-left-color: #EAEAEA;
				border-right-color: #EAEAEA;
				border-top-color: #EAEAEA;
			}
			
			.x-panel-body {
				border-bottom-color: #EAEAEA;
				border-left-color: #EAEAEA;
				border-right-color: #EAEAEA;
				border-top-color: #EAEAEA;
			}
			
			UL.x-tab-strip-top {
				background-color: #EAEAEA;
				background-image: url("<%=rootPath%>/resource/image/global/content_header_background.jpg");
			}
</style>
<script>
var ddtree = null;
function init()
{
	parseDataForTree();
}
function dayAlarmForHour(piedata,dayalarmData){
	var dah = new SWFObject("<%=rootPath%>/amchart/amline.swf", "amline","280", "300", "8", "#FFFFFF");
	dah.addVariable("path", "<%=rootPath%>/amchart/");
	dah.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/dayalarmXml_setting.xml"));
	dah.addVariable("chart_data", dayalarmData);
	dah.write("dayAlarmForHour");
	
	if( piedata != null){
		var sop = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","380", "280", "8", "#FFFFFF");
		sop.addVariable("path", "<%=rootPath%>/amchart/");
		sop.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/alarmpie_settings.xml"));
		sop.addVariable("chart_data",piedata);
		sop.write("pie");
	}else{
		var _div=document.getElementById("pie");
		var img=document.createElement("img");
		img.setAttribute("src","<%=rootPath%>/resource/image/nodata.gif");
		img.setAttribute("valign","center");
		img.setAttribute("width","380");
		img.setAttribute("height","280");
		_div.appendChild(img);
	}
}

function parseDataForTree()
{
	ddtree = new Tree("sorttree","100%","100%",0);
	ddtree.setImagePath("<%=rootPath%>/resource/image/tree/");
	ddtree.setDelimiter(",");
	ddtree.enableCheckBoxes(1);
	ddtree.setOnClickHandler(onclick);
	ddtree.insertNewItem("","root","设备", 0, "", "","", "");
	ddtree.setCheck("root",0);
	ddtree.insertNewItem("root","host","服务器", 0, "", "","", "");
	ddtree.insertNewItem("root","net","网络设备", 0, "", "","", "");
	<%
	for(int j=0;j<hostItems.length;j++){
	%>
		ddtree.insertNewItem("host","host|"+"<%=hostItemsId[j]%>","<%=hostItems[j]%>", 0, "", "","", "");
	<%
	}
	%>
	<%
	for(int j=0;j<netItems.length;j++){
	%>
		ddtree.insertNewItem("net","net|"+"<%=netItemsId[j]%>","<%=netItems[j]%>", 0, "", "","", "");
	<%
	}
	%>
}

	function loadIds(id,ids,startdate,todate,busiId){
		$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/compreReportAjaxManager.ajax?action=executeReportWeek&id="+id+"&ids="+ids+"&startdate="+startdate+"&todate="+todate+"&business="+busiId+"&nowtime="+(new Date()),
			success:function(data){
				if(data.alarmTableHtml != 0){
					var div = document.getElementById("alarmTotal");
					div.innerHTML = data.alarmTableHtml;
				}
				if(data.alarmPie != 0 ||data.alarmDayHour != 0 ){
					dayAlarmForHour(data.alarmPie,data.alarmDayHour);
				}
				if(data.alarmTableHostHtml !=0){
					var div = document.getElementById("alarmHost");
					div.innerHTML = data.alarmTableHostHtml;
				}
				if(data.alarmTableNetHtml !=0){
					var div = document.getElementById("alarmNet");
					div.innerHTML = data.alarmTableNetHtml;
				}
				//host CPU
				if(data.hostCpu == 0){ 
					var hostCpuDiv =document.getElementById("hostCpuDiv");
				  	hostCpuDiv.innerHTML="";
				  	var hostCpuReport =document.getElementById("hostCpuReport");
				  	hostCpuReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_cpu_week_settings.xml"));
			     	so.addVariable("chart_data",data.hostCpu);
				 	so.write("hostCpuReport");
				 	var hostCpuDiv =document.getElementById("hostCpuDiv");
					hostCpuDiv.innerHTML=data.hostCpuHtml;
				}
				//host MEM
				if(data.hostMem == 0){ 
					var hostMemDiv =document.getElementById("hostMemDiv");
				  	hostMemDiv.innerHTML="";
				  	var hostMemReport =document.getElementById("hostMemReport");
				  	hostMemReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_memory_week_settings.xml"));
			     	so.addVariable("chart_data",data.hostMem);
				 	so.write("hostMemReport");
				 	var hostMemDiv =document.getElementById("hostMemDiv");
					hostMemDiv.innerHTML=data.hostMemHtml;
				}
				//host Ping
				if(data.hostPing == 0){ 
					var hostPingDiv =document.getElementById("hostPingDiv");
				  	hostPingDiv.innerHTML="";
				  	var hostPingReport =document.getElementById("hostPingReport");
				  	hostPingReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_ping_week_settings.xml"));
			     	so.addVariable("chart_data",data.hostPing);
				 	so.write("hostPingReport");
				 	var hostPingDiv =document.getElementById("hostPingDiv");
					hostPingDiv.innerHTML=data.hostPingHtml;
				}
				//host Response
				if(data.hostResponse == 0){ 
					var hostResponseDiv =document.getElementById("hostResponseDiv");
				  	hostResponseDiv.innerHTML="";
				  	var hostResponseReport =document.getElementById("hostResponseReport");
				  	hostResponseReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_response_week_settings.xml"));
			     	so.addVariable("chart_data",data.hostResponse);
				 	so.write("hostResponseReport");
				 	var hostResponseDiv =document.getElementById("hostResponseDiv");
					hostResponseDiv.innerHTML=data.hostResponseHtml;
				}
				//host Disk
				if(data.hostDisk == 0){ 
					var hostDiskDiv =document.getElementById("hostDiskDiv");
				  	hostDiskDiv.innerHTML="";
				  	var hostDiskReport =document.getElementById("hostDiskReport");
				  	hostDiskReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_disk_week_settings.xml"));
			     	so.addVariable("chart_data",data.hostDisk);
				 	so.write("hostDiskReport");
				 	var hostDiskDiv =document.getElementById("hostDiskDiv");
				 	hostDiskDiv.innerHTML=data.hostDiskHtml;
				}
				//net CPU
				if(data.netCpu == 0){ 
					var netCpuDiv =document.getElementById("netCpuDiv");
				  	netCpuDiv.innerHTML="";
				  	var netCpuReport =document.getElementById("netCpuReport");
				  	netCpuReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_cpu_week_settings.xml"));
			     	so.addVariable("chart_data",data.netCpu);
				 	so.write("netCpuReport");
				 	var netCpuDiv =document.getElementById("netCpuDiv");
				 	netCpuDiv.innerHTML=data.netCpuHtml;
				}
				//net MEM
				if(data.netMem == 0){ 
					var netMemDiv =document.getElementById("netMemDiv");
				  	netMemDiv.innerHTML="";
				  	var netMemReport =document.getElementById("netMemReport");
				  	netMemReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_memory_week_settings.xml"));
			     	so.addVariable("chart_data",data.netMem);
				 	so.write("netMemReport");
				 	var netMemDiv =document.getElementById("netMemDiv");
					netMemDiv.innerHTML=data.netMemHtml;
				}
				//net Ping
				if(data.netPing == 0){ 
					var netPingDiv =document.getElementById("netPingDiv");
				  	netPingDiv.innerHTML="";
				  	var netPingReport =document.getElementById("netPingReport");
				  	netPingReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_ping_week_settings.xml"));
			     	so.addVariable("chart_data",data.netPing);
				 	so.write("netPingReport");
				 	var netPingDiv =document.getElementById("netPingDiv");
					netPingDiv.innerHTML=data.netPingHtml;
				}
				//net Response
				if(data.netResponse == 0){ 
					var netResponseDiv =document.getElementById("netResponseDiv");
				  	netResponseDiv.innerHTML="";
				  	var netResponseReport =document.getElementById("netResponseReport");
				  	netResponseReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_host_response_week_settings.xml"));
			     	so.addVariable("chart_data",data.netResponse);
				 	so.write("netResponseReport");
				 	var netResponseDiv =document.getElementById("netResponseDiv");
					netResponseDiv.innerHTML=data.netResponseHtml;
				}
				//net UtilIn
				if(data.netUtilIn == 0){ 
					var netUtilInDiv =document.getElementById("netUtilInDiv");
				  	netUtilInDiv.innerHTML="";
				  	var netUtilInReport =document.getElementById("netUtilInReport");
				  	netUtilInReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_net_utilIn_week_settings.xml"));
			     	so.addVariable("chart_data",data.netUtilIn);
				 	so.write("netUtilInReport");
				 	var netUtilInDiv =document.getElementById("netUtilInDiv");
					netUtilInDiv.innerHTML=data.netUtilInHtml;
				}
				//net UtilOut
				if(data.netUtilOut == 0){ 
					var netUtilOutDiv =document.getElementById("netUtilOutDiv");
				  	netUtilOutDiv.innerHTML="";
				  	var netUtilOutReport =document.getElementById("netUtilOutReport");
				  	netUtilOutReport.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/compre_net_utilOut_week_settings.xml"));
			     	so.addVariable("chart_data",data.netUtilOut);
				 	so.write("netUtilOutReport");
				 	var netUtilOutDiv =document.getElementById("netUtilOutDiv");
					netUtilOutDiv.innerHTML=data.netUtilOutHtml;
				}
			}
		});
	}
//预览
	function preview(id){
		var	startdate=document.all.startdate.value;
   		var	todate=document.all.todate.value;
		var tab = Ext.getCmp('ext-tab-report');
 		tab.setActiveTab(0);
		document.getElementById('editmodel').style.display='none';
		var ids=null;
		var busiId =null;
		document.all.id_perform.value=id;
		document.all.ids_perform.value=ids;
		loadIds(id,ids,startdate,todate,busiId);
	}
	function onclick(id,e)
	{
		ddtree.setItemCloseable(1);	
	}
</script>

<script language="JavaScript" type="text/JavaScript">
	Ext.onReady(function(){  
		setTimeout(function(){
	    	Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
			}, 250);
	});

//报表展示
	function query_ok(){
		var	startdate=document.all.startdate.value;
		var todate=document.all.todate.value;
		var	ids = ddtree.getAllChecked().toString();
		var busiId = document.all.business.value;
		if(ids.length<=0|| ids.length == ""){
			alert("请选择设备的性能选项！！！");
			return;
		}
		if(busiId.length<=0|| busiId.length == ""){
			alert("请选择业务！！！");
			return;
		}
  		var id=null;
  		document.all.ids_perform.value=ids;
  		document.all.id_perform.value=id;
 		loadIds(id,ids,startdate,todate,busiId);
	}
</script>

<!-- Tab -->
<script language="JavaScript" type="text/JavaScript">
Ext.onReady(function(){
	var	ids = ddtree.getAllChecked().toString();
	var tabs = new Ext.TabPanel({
     	id: 'ext-tab-report',
        renderTo: 'tabs1',
        width:888,
        activeTab: 0,
        frame:true,
        defaults:{autoHeight: true},
        items:[
			{contentEl:'script', title: '报表设置'},
			{contentEl:'model', title: '报表模板列表',listeners: {activate: handleActivate}}
        ]
    });
	function handleActivate(tab){
		$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/compreReportAjaxManager.ajax?action=loadCompreReportDayList&type=hostNet&reportType=weekBusi&nowtime="+(new Date()),
			success:function(data){
				var modelDiv =document.getElementById("model");
				modelDiv.innerHTML=data.dataStr;
			}
		});
    }
});

</script>

<!-- 模板 -->
<script>
	$(document).ready(function(){
		$('#saveBtn').bind('click',function(){
			var report_name=$('#report_name').val();
			var name=$('#recievers_name').val();
			var tile=$('#tile').val();
			var desc=$('#desc').val();
			var business =$('#business_id').val();
			var exporttype=$('#exporttype').val();
			var hou=$('#sendtimehou').val();
			var week=$('#sendtimeweek').val();
			var re_id=$('#recievers_id').val();
			if($('#recievers_name').val()==null || $('#recievers_name').val()==''){
				alert("请填写接收人!!!");
				return;
			}
			if($('#business_name').val()==null || $('#business_name').val()==''){
				alert("请填写报表所属业务!!!");
				return;
			}
			var ids = ddtree.getAllChecked().toString();
			if(ids.length<=0|| ids.length == "")
			{
	   			alert("请选择设备选项!!!");
	   			return;
			}
        	$.ajax({
				type:"POST",
				data:"ids="+ids+"&tile="+tile+"&desc="+desc+"&exporttype="+exporttype+"&report_name="+report_name+"&recievers_name="+name+"&sendtimehou="+hou+"&sendtimeweek="+week+"&recievers_id="+re_id+"&business="+business+"&nowtime="+(new Date()),
				dataType:"json",
				url:"<%=rootPath%>/compreReportAjaxManager.ajax?action=saveCompreReportDayOption&type=hostNet&reportType=weekBusi",
				success:function(data){
					alert(data.dataStr);
			 		var tab = Ext.getCmp('ext-tab-report');
           			tab.setActiveTab(1);	
				}
			});
        });
	});
		
//展开模板
function editModel(){
	document.getElementById('editmodel').style.display='block';
}
//隐藏模板
function hiddenModel(){
	document.getElementById('editmodel').style.display='none';
}
//删除模板
function deleteItem(id){
	if(window.confirm("您确定要删除吗？")){
		$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/compreReportAjaxManager.ajax?action=loadCompreReportDayList&type=hostNet&id="+id+"&reportType=weekBusi&nowtime="+(new Date()),
			success:function(data){
				var modelDiv =document.getElementById("model");
				modelDiv.innerHTML=data.dataStr;
			}
		});
	}
} 
//模板信息的加载
function createWin(id){
	return CreateDeviceWindow("<%=rootPath%>/comprereportweek.do?action=compreReportWeekConfig&id="+id);
}
</script>
	
<script language="JavaScript" type="text/javascript">
function CreateDeviceWindow(url)
{
	msgWindow=window.open(url,"_blank","toolbar=no,width=900,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
function setReciever(ctrlId,hideCtrlId)
{
	return CreateWindow("<%=rootPath%>/subscribeReportConfig.do?action=user_list&&ctrlId="+ctrlId+"&&hideCtrlId="+hideCtrlId);
}
function setDevices(ctrlId,hideCtrlId)
{
	return CreateDeviceWindow("<%=rootPath%>/subscribeReportConfig.do?action=device_list&&ctrlId="+ctrlId+"&&hideCtrlId="+hideCtrlId);
}
function CreateWindow(url)
{
	msgWindow=window.open(url,"_blank","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
function exportReport(exportType){
	var startdate=document.all.startdate.value;
	var todate=document.all.todate.value;
	var ids=document.all.ids_perform.value;
	var id=document.all.id_perform.value;
	var business = document.all.business.value;
	if(ids.length<=0||ids.length == ""){
		ids = ddtree.getAllChecked().toString();
		if((ids.length<=0|| ids.length == "")&&(id.length<=0|| id.length == "")){
			alert("请选择设备的性能选项或模板！！！");
			return;
		}
	}
	if(business.length<=0||business.length == ""){
		alert("请选择业务！！！");
		return;
	}
	window.open('<%=rootPath%>/comprereportweekBusi.do?action=downloadReportWeek&type=hostNet&reportType=weekBusi&exportType='+exportType+'&id='+id+'&ids='+ids+'&startdate='+startdate+'&todate='+todate+'&business='+business+'&nowtime='+(new Date()),"_blank","toolbar=no,width=1,height=1,top=2000,left=3000,directories=no,status=no,menubar=no,alwaysLowered=yes");
}

</script>
</head>
<body id="body" class="body" onLoad="init();">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm">
		<div id="loading">
			<div class="loading-indicator">
				<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />
				Loading...
			</div>
		</div>
		<div id="loading-mask" style=""></div>
		<table id="body-container" class="body-container">
			<tr>
				<td valign=top style="margin: 10px auto;position :relative;">
					<table>
                		<tr>
               				<td colspan="2">
     							<div style="height:60px;margin-bottom:10px; background: url('<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>/image/tree/tit_bg.gif'); padding-left:40px; line-height:60px; font-size: 24px; font-weight: bold; color: #fff;">
									综合统计周报表(业务)
								</div>
							</td>
						</tr>
	                    <tr>
							<td height="100%" align="left" valign="top">
								<div id="sorttree" style="margin:0 8px 0 0; padding:10px; background:#dce9f2; height:600px; width: 240px; overflow:auto;"></div>
							</td>
							<td width="80%" height="100%" valign="top">
								<div id='tabs1'>
									<div id="script" class="x-hide-display">
                            			<table border="0" cellpadding="0" cellspacing="0" class="win-content" id="win-content">
											<tr>
												<td width="94%">
													<table id="win-content-body" class="win-content-body">
														<tr>
															<td>
																<div>
																	<table bgcolor="#ECECEC">
																		<tr align="left" valign="middle">
																			<td height="21" align="left" valign=top>
																				&nbsp;&nbsp;
																				选择日期:
																				<input type="text" id="mystartdate" name="startdate" value="<%=startdate %>" size="10">
																				<a onClick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
																				<img id=imageCalendar1  width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0> </a>
																				&nbsp;
																				<input type="hidden" id="mystartdate" name="todate" value="<%=todate %>" size="10">
																				
																				<input type="button" name="doNode" value="选择设备" onClick="setDevices('nodeName','business')">
																				<input type="text" id="nodeName" name="nodeName" class="formStyle" size="20" readonly></input>
																				<input type="hidden" id="business" name="business" value=""></input>
																				&nbsp;
																				<input type="button" name="doprocess" value="预  览" onClick="query_ok()">
																				&nbsp;
																				<input type="button" name="edit" value="编辑模板" onClick="editModel()"> 
																				<input type="hidden" name="ids_perform" id="ids_perform" value=""/>
																				<input type="hidden" name="id_perform" id="id_perform" value=""/>
																				<span style="CURSOR:hand" onclick="exportReport('doc')" ><img name="doc" alt='导出WORD' src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0" >导出WORD</span>&nbsp;&nbsp;
                                                                                <span style="CURSOR:hand" onclick="exportReport('xls')" ><img name="xls" alt='导出EXCEL' src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0" >导出EXCEL</span>&nbsp;&nbsp;
                                                                           		<span style="CURSOR:hand" onclick="exportReport('pdf')" ><img name="pdf" alt='导出PDF' src="<%=rootPath%>/resource/image/export_pdf.gif" width=18  border="0" >导出PDF</span>&nbsp;&nbsp;
																			</td>
																		</tr>
																	</table>
																</div>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
                                            	<td width="94%" >
                                               		<jsp:include page="../busireport/compreSubscribeAddWeekBusi.jsp"></jsp:include>
                                            	</td>
                                    		</tr>
                                    		<tr>
                                    			<td width="94%">
                                    				<div id = "alarmInfo">
                                    					<table>
                                    						<tr>
                                    							<td width = "100%" align=center>
                                    								<div id="alarmTotal">
                                    								</div>
                                    							</td>
                                    						</tr>
                                    						<tr>
                                    							<td width = "100%" align=center>
                                    								<table width = "100%">
                                    									<tr>
                                    										<td width="50%" align=center>
                                    											<div id="pie">
																				</div>
                                    										</td>
                                    										<td width="50%" align=center>
                                    											<div id="dayAlarmForHour">
																				</div>
                                    										</td>
                                    									</tr>
                                    								</table>
                                    							</td>
                                    						</tr>
                                    						<tr>
                                    							<td width = "100%" align=center>
                                    								<div id = "alarmHost">
                                    								</div>
                                    							</td>
                                    						</tr>
                                    						<tr>
                                    							<td width = "100%" align=center>
                                    								<div id = "alarmNet">
                                    								</div>
                                    							</td>
                                    						</tr>
                                    					</table>
                                    				</div>
                                    			</td>
                                    		</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="hostCpuReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="hostCpuDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="hostMemReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="hostMemDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="hostPingReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="hostPingDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="hostResponseReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="hostResponseDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="hostDiskReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="hostDiskDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netCpuReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netCpuDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netMemReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netMemDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netPingReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netPingDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netResponseReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netResponseDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netUtilInReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netUtilInDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=94%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="netUtilOutReport">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="netUtilOutDiv">
													</div>
												</td>
											</tr>
										</table>
									</div>
									<div id="model" class="x-hide-display">
									</div>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>
</BODY>
</HTML>