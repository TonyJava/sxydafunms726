<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="UTF-8"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.afunms.topology.model.*" %>
<%
	String rootPath = request.getContextPath();
  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String	startdate = sdf.format(new Date());
	String	todate = sdf.format(new Date());
	String linkids = ","+(String)request.getAttribute("linkids")+",";
	String field = (String)request.getAttribute("field");
  	String sorttype = (String)request.getAttribute("sorttype");
  	if(sorttype == null || sorttype.trim().length() == 0){
  	  	sorttype = "";
  	}
  	if(field == null || field.trim().length() == 0){
  	  	field = "";
  	}
  	List list = (ArrayList)request.getAttribute("linkPerformanceList");
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="<%=rootPath%>/fusionChart/Contents/Style.css" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/js/tree/Tree.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/tree/common.js"></script>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<script language="JavaScript" src="<%=rootPath%>/fusionChart/JSClass/FusionCharts.js"></script>
<script type="text/javascript">
	var ddtree;
	/**
	*初始化按钮颜色

	*/
	function initmenu(){
		linktree();
		//增加树的checkbox回显
		addReCheck();
		refreshStyle('llxn');
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
	
	
	
	function linktree(){
		ddtree = new Tree("linktree","100%","100%",0);
		ddtree.setImagePath("<%=rootPath%>/resource/image/tree/");
		ddtree.setDelimiter(",");
		ddtree.enableCheckBoxes(1);
		ddtree.insertNewItem("","root","链路资源树", 0, "", "base.gif","base.gif", "base.gif");
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
	
	function addReCheck(){
		var allLeafsIds = ddtree.getAllLeafs();
		var allLeafsIdsArry = allLeafsIds.split(',');
		var linkids = '<%=linkids%>';
		//ddtree.setCheck('2',1);
		for(var i=0; i<allLeafsIdsArry.length; i++){
			if(linkids.indexOf(','+allLeafsIdsArry[i]+',') != -1){
				ddtree.setCheck(allLeafsIdsArry[i],1);
			}
		}
	}	
    
    /**
    *根据资源树的选中项，查询链路列表
    */
    function query_linklist(){
		var	startdate=document.getElementById('startdate').value;
	    var	todate=document.getElementById('todate').value;
		var	ids = ddtree.getAllChecked().toString();
		var id=null;
		if(ids.length<=0|| ids.length == "")
		{
			alert("未选中链路！！！");
			return;
		}
 		//loadIds(id,ids,startdate,todate);
 		document.getElementById("linkids").value = ids;
 		document.getElementById('mainForm').action = "<%=rootPath%>/tabMenu.do?action=linkList";
 		document.getElementById('mainForm').submit();
    }
    
    function showSort(fieldValue){  
		var field = document.getElementById('field');
		field.value = fieldValue;
		mainForm.action = "<%=rootPath%>/tabMenu.do?action=linkList";
  		mainForm.submit();
	}
	
	//创建链路信息统计图

	function createLinkPic(){
		var	startdate=document.getElementById('startdate').value;
	    var	todate=document.getElementById('todate').value;
		var	ids = ddtree.getAllChecked().toString();
		var id=null;
		if(ids.length<=0|| ids.length == "")
		{
			alert("未选中链路！！！");
			return;
		}
		
		//document.all.linkids.value=ids;
		document.getElementById("linkids").value = ids;
		$.ajax({
			type:"POST",
			dataType:"json",
			data:"linkids="+ids+"&startdate="+startdate+"&todate="+todate+"&nowtime="+(new Date()),
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=createLinkRoadPic",
			success:function(data){
			 	 if(data.linkUtilhdxda==0){ 
				  	var linkUtilhdxdiv =document.getElementById("linkUtilhdxdiv");
				  	linkUtilhdxdiv.innerHTML="";
			     }else{
			     	var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "780", "8", "#FFFFFF");
		         	so.addVariable("path", "<%=rootPath%>/amchart/");
		         	so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/linkRoadUtilhdx_setting.xml"));
			     	so.addVariable("chart_data",data.linkUtilhdxdivData);  
				 	so.write("linkUtilhdxdiv");
				}
			 }	
		});
	}
	
	//生成链路带宽利用率统计图
	function createLinkBandwidthPecPic(){
		var	startdate=document.getElementById('startdate').value;
	    var	todate=document.getElementById('todate').value;
		var	ids = ddtree.getAllChecked().toString();
		var id=null;
		if(ids.length<=0|| ids.length == "")
		{
			alert("未选中链路！！！");
			return;
		}
		
		//document.all.linkids.value=ids;
		document.getElementById("linkids").value = ids;
		$.ajax({
			type:"POST",
			dataType:"json",
			data:"linkids="+ids+"&startdate="+startdate+"&todate="+todate+"&nowtime="+(new Date()),
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=createLinkBandwidthPecPic",
			success:function(data){
			 	 if(data.linkUtilhdxda==0){ 
				  	var linkBandwidthdiv =document.getElementById("linkBandwidthdiv");
				  	linkBandwidthdiv.innerHTML="";
			     }else{
			     	var so = new SWFObject("<%=rootPath%>/amchart/amline.swf", "ampie","885", "580", "8", "#FFFFFF");
		         	so.addVariable("path", "<%=rootPath%>/amchart/");
		         	so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/linkRoadBandwidthPec_setting.xml"));
			     	so.addVariable("chart_data",data.linkBandwidthdivData);  
				 	so.write("linkBandwidthdiv");
				}
			 }	
		});
	}
	
	function submitSelect(){
		var selectoption = document.getElementById('selectoption').value;
		if(selectoption == 1){//链路列表
			query_linklist();
		}
		if(selectoption == 2){//链路流速统计图
			createLinkPic();
		}
		if(selectoption == 3){//链路带宽利用率统计图
			createLinkBandwidthPecPic();
		}
	}
</script>
</head>

<body onload="initmenu();" id="body-container" style="overflow:scroll;">
	<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	<form id="mainForm" method="post" name="mainForm" >
		<table>
			<tr>
				<td height="100%" width="300px" align="left" valign="top">
					<div id="linktree"
						style="margin:0 2px 0 0; padding:10px; background:white; height:550px;"></div>
				</td>
				<td height="100%" align="left" valign="top" width="6px">&nbsp;
				</td>
				<td valign=top>
					<table>
						<tr>
							<td>
								<table id="content-header" class="content-header">
				                	<tr>
					                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
					                	<td class="content-title">&nbsp;性能 >> 链路性能 </td>
					                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
					       			</tr>
					        	</table>
				        	</td>
			        	</tr>
			        	<tr>
							<td>
							<table bgcolor="#ECECEC">
								<tr align="left" valign="middle">
									<td height="21" align="left" valign=top>
										&nbsp;&nbsp;&nbsp;开始日期

										<input type="text" id="startdate"
											name="startdate" value="<%=startdate%>" size="10">
										<a onClick="event.cancelBubble=true;"
											href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
											<img id=imageCalendar1 width=34 height=21
												src="<%=rootPath%>/include/calendar/button.gif"
												border=0> </a> 选择日期
										<input type="text" id="todate" name="todate"
											value="<%=todate%>" size="10" />
										<a onClick="event.cancelBubble=true;"
											href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
											<img id=imageCalendar2 width=34 height=21
												src="<%=rootPath%>/include/calendar/button.gif"
												border=0> </a> &nbsp;&nbsp;
												
										<select id="selectoption" onchange="submitSelect();">
											<option value='0'>-----选择-----</option>
											<option value='1'>链路列表</option>
											<option value='2'>链路流速统计图</option>
											<option value='3'>链路带宽利用率统计图</option>
										</select>
									</td>
								</tr>
							</table>
							</td>
			        	</tr>
			        	<tr>
							<td>
								<table style="background-color: white;">
										<tr>
										<td align="center" class="body-data-title">序号
										<input type="hidden" name="linkids" id="linkids" value="<%=linkids %>"/>
					        			<input type="hidden" id="field" name="field" value="<%=field%>"/>
		        						<input type="hidden" id="sorttype" name="sorttype" value="<%=sorttype%>"/></td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('name')">名称</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('startIp')">起始设备IP</td>
										<td align="center" class="body-data-title">起始设备端口</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('endIp')">终止设备IP</td>
										<td align="center" class="body-data-title">终止设备端口</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('uplinkSpeed')">上行流速(KB/秒)</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('downlinkSpeed')">下行流速(KB/秒)</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('ping')">可用性(%)</td>
										<td align="center" class="body-data-title"><a href="#" onclick="showSort('allSpeedRate')">带宽利用率(%)</td>
										</tr>
										<%
											if(list != null && list.size() > 0){
												for(int i = 0; i < list.size() ; i++){
													LinkPerformanceDTO linkPerformanceDTO = (LinkPerformanceDTO)list.get(i);
													if(linkids != null && !linkids.equals(",,") && !linkids.contains(",null,") && !linkids.contains(","+linkPerformanceDTO.getId()+",")){
														continue;
													}
												%>
									<tr <%=onmouseoverstyle%>>
										<td align="center" class="body-data-list"><%=i+1%></td>
										<td align="center" class="body-data-list"><%=linkPerformanceDTO.getName()%></td>
										<td align="center" class="body-data-list"><%=linkPerformanceDTO.getStartNode()%></td>
										<td align="center" class="body-data-list"><%=linkPerformanceDTO.getStratIndex()%></td>
										<td align="center" class="body-data-list"><%=linkPerformanceDTO.getEndNode()%></td>
										<td align="center" class="body-data-list"><%=linkPerformanceDTO.getEndIndex()%></td>
										<td align="center" class="body-data-list">
											<table>
		                						<tr>
		                    						<td align="center" width="50%"><%=linkPerformanceDTO.getUplinkSpeed()%></td>
		                    						<td align="center" width="50%"><img src="<%=rootPath%>/resource/image/a_xn.gif" onclick='showDetail("<%=linkPerformanceDTO.getId()%>")' width=15></td>
		                    					</tr>
		                      				</table>
		                      			</td>
		 								<td align="center" class="body-data-list">
		 									<table>
			                      				<tr>
			                      					<td align="center" width="50%"><%=linkPerformanceDTO.getDownlinkSpeed()%></td>
			                      					<td align="center" width="50%"><img src="<%=rootPath%>/resource/image/a_xn.gif" onclick='showDetail("<%=linkPerformanceDTO.getId()%>" )' width=15></td>
			                      				</tr>
		                      				</table>
		                      			</td>
										<td align="center" class="body-data-list">
											<table>
			                      				<tr>
			                      					<td align="center" width="50%"><%=linkPerformanceDTO.getPingValue()%></td>
			                      					<td align="center" width="50%"><img src="<%=rootPath%>/resource/image/a_xn.gif" onclick='showDetail("<%=linkPerformanceDTO.getId()%>")' width=15></td>
			                      				</tr>
		                      				</table>
		                      			</td>
										<td align="center" class="body-data-list">
											<table>
			                      				<tr>
			                      					<td align="center" width="50%"><%=linkPerformanceDTO.getAllSpeedRate()%></td>
			                      					<td align="center" width="50%"><img src="<%=rootPath%>/resource/image/a_xn.gif" onclick='showDetail("<%=linkPerformanceDTO.getId()%>")' width=15></td>
			                      				</tr>
		                      				</table>
		                      			</td>
		 									</tr>
											<%
										}
									}
								 %>
							</table>
							</td>
			       	 	</tr>
						<tr>
							<td>
					        	<table style="background-color: white;overflow: visible;">
									<tr>
										<td valign="top" class="text" align="center"> 
											<div id="linkUtilhdxdiv" style="overflow: visible;"></div>
								     	</td>
									</tr>
								</table>
							</td>
			        	</tr>
			        	<tr>
							<td>
								<table style="background-color: white;overflow: visible;">
									<tr>
										<td valign="top" class="text" align="center"> 
											<div id="linkBandwidthdiv" style="overflow: visible;"></div>
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

<script type="text/javascript">
	//Tab页

	Ext.onReady(function(){
     	var tabs = new Ext.TabPanel({
	     	id: 'ext-tab-report',
	        renderTo: 'tabs',  
	        width: 985, 
	        activeTab: 0,
	        frame:true,
	        defaults:{autoHeight: true},
	        items:[
	            {contentEl:'linklist', title: '链路资源一览表'}
	        ]
    	});
    });
</script>
</html>