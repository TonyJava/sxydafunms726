<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.model.*" %>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String)request.getAttribute("menuTable");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String startdate = sdf.format(new Date());
	String todate = sdf.format(new Date());
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

function parseDataForTree()
{
	ddtree = new Tree("sorttree","100%","100%",0);
	ddtree.setImagePath("<%=rootPath%>/resource/image/tree/");
	ddtree.setDelimiter(",");
	ddtree.enableCheckBoxes(1);
	ddtree.setOnClickHandler(onclick);
	ddtree.insertNewItem("","root","链路资源树", 0, "", "","", "");
	<%
		List linkList = (ArrayList)request.getAttribute("linkList");
		if(linkList != null){
			for(int i=0; i<linkList.size(); i++){
				Link link = (Link)linkList.get(i);
	%>
			ddtree.insertNewItem("root","<%=link.getId()%>","<%=link.getLinkName()%>", 0, "", "","", "");
	<%
			}
		}
	%>
}

	function loadIds(id,ids,terms,startdate,todate){
		$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/linkReportAjaxManager.ajax?action=executeReportWeek&id="+id+"&terms="+terms+"&ids="+ids+"&startdate="+startdate+"&todate="+todate+"&nowtime="+(new Date()),
			success:function(data){
				if(data.upChart == 0){ 
					var upDiv =document.getElementById("upDiv");
				  	upDiv.innerHTML="";
				  	var upTable =document.getElementById("upTable");
				  	upTable.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/link_report_up_week_settings.xml"));
			     	so.addVariable("chart_data",data.upChart);
				 	so.write("upDiv");
				 	var upTable =document.getElementById("upTable");
					upTable.innerHTML=data.linkUpHtml;
				}
				
				if(data.downChart == 0){ 
					var downDiv =document.getElementById("downDiv");
				  	downDiv.innerHTML="";
				  	var downTable =document.getElementById("downTable");
				  	downTable.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/link_report_down_week_settings.xml"));
			     	so.addVariable("chart_data",data.downChart);
				 	so.write("downDiv");
				 	var downTable =document.getElementById("downTable");
					downTable.innerHTML=data.linkDownHtml;
				}
				
				if(data.bandwidthChart == 0){ 
					var bandwidthDiv =document.getElementById("bandwidthDiv");
				  	bandwidthDiv.innerHTML="";
				  	var bandwidthTable =document.getElementById("bandwidthTable");
				  	bandwidthTable.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/link_report_bandwidth_week_settings.xml"));
			     	so.addVariable("chart_data",data.bandwidthChart);
				 	so.write("bandwidthDiv");
				 	var bandwidthTable =document.getElementById("bandwidthTable");
					bandwidthTable.innerHTML=data.linkBandwidthHtml;
				}
				
				if(data.bandtrendChart == 0){ 
					var bandtrendDiv =document.getElementById("bandtrendDiv");
				  	bandtrendDiv.innerHTML="";
				  	var bandtrendTable =document.getElementById("bandtrendTable");
				  	bandtrendTable.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/link_report_bandtrend_week_settings.xml"));
			     	so.addVariable("chart_data",data.bandtrendChart);
				 	so.write("bandtrendDiv");
				 	var bandtrendTable =document.getElementById("bandtrendTable");
					bandtrendTable.innerHTML=data.linkBandTrendHtml;
				}
				
				if(data.usabilityChart == 0){ 
					var usabilityDiv =document.getElementById("usabilityDiv");
				  	usabilityDiv.innerHTML="";
				  	var usabilityTable =document.getElementById("usabilityTable");
				  	usabilityTable.innerHTML="";
				}else{
					var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "338", "8", "#FFFFFF");
					so.addVariable("path", "<%=rootPath%>/amchart/");
					so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/link_report_usability_week_settings.xml"));
			     	so.addVariable("chart_data",data.usabilityChart);
				 	so.write("usabilityDiv");
				 	var usabilityTable =document.getElementById("usabilityTable");
					usabilityTable.innerHTML=data.linkUsabilityHtml;
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
		var terms = null;
		document.all.id_perform.value=id;
		document.all.ids_perform.value=ids;
		loadIds(id,ids,terms,startdate,todate);
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
		if(ids.length<=0|| ids.length == ""){
			alert("请选择链路！");
			return;
		}
		var terms = "";
		$("[name='terms'][checked]").each(function(){ 
			terms += $(this).val()+",";
		});
		if(terms==""){
			alert("请选择查询内容!");
			return;
		}
  		var id=null;
  		document.all.ids_perform.value=ids;
  		document.all.id_perform.value=id;
 		loadIds(id,ids,terms,startdate,todate);
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
			url:"<%=rootPath%>/linkReportAjaxManager.ajax?action=loadLinkReportList&type=week&nowtime="+(new Date()),
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
			var exporttype=$('#exporttype').val();
			var hou=$('#sendtimehou').val();
			var week = $('#sendtimeweek').val();
			var re_id=$('#recievers_id').val();
			if($('#recievers_name').val()==null || $('#recievers_name').val()==''){
				alert("请填写接收人!!!");
				return;
			}
			var ids = ddtree.getAllChecked().toString();
			if(ids.length<=0|| ids.length == "")
			{
	   			alert("请选择设备选项!!!");
	   			return;
			}
			var terms = "";
			$("[name='terms'][checked]").each(function(){ 
				terms += $(this).val()+",";
			});
			if(terms==""){
				alert("请选择查询内容!");
				return;
			}
        	$.ajax({
				type:"POST",
				data:"ids="+ids+"&tile="+tile+"&desc="+desc+"&exporttype="+exporttype+"&report_name="+report_name+"&recievers_name="+name+"&sendtimehou="+hou+"&sendtimeweek="+week+"&recievers_id="+re_id+"&terms="+terms+"&nowtime="+(new Date()),
				dataType:"json",
				url:"<%=rootPath%>/linkReportAjaxManager.ajax?action=saveLinkReportOption&type=week",
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
			url:"<%=rootPath%>/linkReportAjaxManager.ajax?action=loadLinkReportList&type=week&id="+id+"&nowtime="+(new Date()),
			success:function(data){
				var modelDiv =document.getElementById("model");
				modelDiv.innerHTML=data.dataStr;
			}
		});
	}
} 
//模板信息的加载
function createWin(id){
	return CreateDeviceWindow("<%=rootPath%>/linkReportWeek.do?action=linkReportConfig&id="+id);
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
function CreateWindow(url)
{
	msgWindow=window.open(url,"_blank","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
	function exportReport(exportType){
		var startdate=document.all.startdate.value;
		var todate=document.all.todate.value;
		var ids=document.all.ids_perform.value;
		var id=document.all.id_perform.value;
		if(ids.length<=0||ids.length == ""){
			ids = ddtree.getAllChecked().toString();
			if((ids.length<=0|| ids.length == "")&&(id.length<=0|| id.length == "")){
				alert("请选择链路或模板！！！");
				return;
			}
		}
		var terms = "";
		$("[name='terms'][checked]").each(function(){ 
			terms += $(this).val()+",";
		});
		if(terms==""){
			alert("请选择查询内容!");
			return;
		}
		window.open('<%=rootPath%>/linkReportWeek.do?action=downReport&type=link&reportType=week&exportType='+exportType+'&id='+id+'&terms='+terms+'&ids='+ids+'&startdate='+startdate+'&todate='+todate+'&nowtime='+(new Date()),"_blank","toolbar=no,width=1,height=1,top=2000,left=3000,directories=no,status=no,menubar=no,alwaysLowered=yes");
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
									关键链路周报
								</div>
							</td>
						</tr>
	                    <tr>
							<td height="100%" align="left" valign="top">
								<div id="sorttree" style="margin:0 8px 0 0; padding:10px; background:#dce9f2; height:600px; width: 200px; overflow:auto;"></div>
							</td>
							<td width="100%" height="100%" valign="top">
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
																				<!-- 
																				结束日期:
																				-->
																				<input type="hidden" id="mytodate" name="todate" value="<%=todate%>" size="10" />
																				<!-- 
																				<a onClick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)"> 
																				<img id=imageCalendar2  width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0> </a>
																				&nbsp;&nbsp;
																				 -->
																				<input type="button" name="doprocess" value="预  览" onClick="query_ok()">
																				&nbsp;&nbsp;
																				<input type="button" name="edit" value="编辑模板" onClick="editModel()"> 
																				<input type="hidden" name="ids_perform" id="ids_perform" value=""/>
																				<input type="hidden" name="id_perform" id="id_perform" value=""/>
																				<span style="CURSOR:hand" onclick="exportReport('doc')" ><img name="doc" alt='导出WORD' src="<%=rootPath%>/resource/image/export_word.gif" width=18  border="0" >导出WORD</span>&nbsp;&nbsp;&nbsp;&nbsp;
                                                                                <span style="CURSOR:hand" onclick="exportReport('xls')" ><img name="xls" alt='导出EXCEL' src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0" >导出EXCEL</span>&nbsp;&nbsp;&nbsp;&nbsp;
                                                                           		<span style="CURSOR:hand" onclick="exportReport('pdf')" ><img name="pdf" alt='导出PDF' src="<%=rootPath%>/resource/image/export_pdf.gif" width=18  border="0" >导出PDF</span>&nbsp;&nbsp;&nbsp;&nbsp;
																			</td>
																		</tr>
																		<tr>
																			<td align="center">
																				<input type="checkbox" name="terms" value ="shangxing" >链路上行流速&nbsp;&nbsp;
																				<input type="checkbox" name="terms" value ="xiaxing" >链路下行流速&nbsp;&nbsp;
																				<input type="checkbox" name="terms" value ="keyongxin" >链路可用性&nbsp;&nbsp;
																				<input type="checkbox" name="terms" value ="daikuan" >链路带宽&nbsp;&nbsp;
																				<input type="checkbox" name="terms" value ="qushitu" >链路带宽趋势图&nbsp;&nbsp;
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
                                            	<td width="100%" >
                                               		<jsp:include page="subscribeWeek.jsp"></jsp:include>
                                            	</td>
                                    		</tr>
                                    		<tr>
												<td width=100%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="upTable">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="upDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=100%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="downTable">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="downDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=100%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="bandwidthTable">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="bandwidthDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=100%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="bandtrendTable">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="bandtrendDiv">
													</div>
												</td>
											</tr>
											<tr>
												<td width=100%>
                                            		<table cellpadding="0" cellspacing="0" >
												   		<tr>
													   		<td width="100%" align=center>
													       		<div id="usabilityTable">
                                                           		</div>
                                                           	</td>
												    	</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align=right>
													<div id="usabilityDiv">
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