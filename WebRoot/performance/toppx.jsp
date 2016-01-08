<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.flex.TopNService"%>
<%@page import="com.afunms.polling.om.Avgcollectdata"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%     
    String rootPath = request.getContextPath();
    String reporttype = (String)request.getParameter("reporttype");
    String topn = (String)request.getParameter("topn");
    if(topn==null){topn="5";}
    String title = "(实时)";
    if(reporttype==null){
        reporttype = "now";
    }
    if(reporttype!=null&&"now".equals(reporttype)){
        title = "(实时)";
    }
    if(reporttype!=null&&"day".equals(reporttype)){
        title = "(日报)";
    }
    if(reporttype!=null&&"week".equals(reporttype)){
        title = "(周报)";
    }
    if(reporttype!=null&&"month".equals(reporttype)){  
        title = "(月报)";
    }
    String b_time ="";
    String t_time = "";
    b_time = (String)request.getParameter("startdate");
    t_time = (String)request.getParameter("todate");
    if (b_time == null){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		b_time = sdf.format(new Date());
		if("week".equals(reporttype)){
		    b_time = sdf.format(getDateBefore(new Date(),6));
		}
		if("month".equals(reporttype)){
		    b_time = sdf.format(getDateBefore(new Date(),30));
		}
	}
	if (t_time == null){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		t_time = sdf.format(new Date());
	}
	List pingList = new ArrayList();
	List responseList = new ArrayList();
	List cpuList = new ArrayList();
	List memoryList = new ArrayList();
	List utilinList = new ArrayList();
	List utiloutList = new ArrayList();
	List utilperinList = new ArrayList();
	List utilperoutList = new ArrayList();
	TopNService topNService = new TopNService();
	User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	String bids = vo.getBusinessids();
	if (vo.getRole() == 0 || vo.getRole() == 1) {
	    bids = "-1";
	}
	if("day".equals(reporttype)||"week".equals(reporttype)||"month".equals(reporttype)){
	    try{
	        pingList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"ping",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        responseList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"ResponseTime",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        cpuList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"cpu",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        memoryList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"memory",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilinList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"inutil",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utiloutList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"oututil",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilperinList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"inutilper",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilperoutList = topNService.getNetworkList(1,Integer.parseInt(topn),b_time,t_time,"oututilper",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	} else {
	    try{
	        pingList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"ping",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        responseList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"ResponseTime",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        cpuList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"cpu",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        memoryList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"memory",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilinList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"inutil",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utiloutList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"oututil",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilperinList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"inutilper",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilperoutList = topNService.getNetworkTempList(1,Integer.parseInt(topn),"oututilper",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
%>   
<%!
public Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>   
<head>        
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">  
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="UTF-8" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="UTF-8"></script>
<title>Insert title here</title>
<script type="text/javascript">
/**
*初始化按钮颜色
*/
function initmenu(){
	refreshStyle('toppx');
}
var showdate = "<%=reporttype%>";
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
$(document).ready(function(){
    if(showdate=="null"||showdate=="now"){
        document.getElementById("dateselect").style.display="none";
    } else {
        document.getElementById("dateselect").style.display="";
    }
    //$('#ping-detail').hide();//默认让连通率模块可见
	$('#cpumemory-detail').hide();
	$('#utilhdx-detail').hide();
	$('#utilhdxper-detail').hide();
	$('#ping').bind('click',function(){
			$('#ping-detail').slideToggle('slow',function(){
        		if ($(this).is(':hidden')) {
        			$("#img_ping").attr("src","<%=rootPath%>/resource/image/microsoftLook/desc.gif"); 
    			} else {
    				$("#img_ping").attr("src","<%=rootPath%>/resource/image/microsoftLook/asc.gif"); 
    			}        
   				return false;
   			});        
		});
	$('#cpumemory').bind('click',function(){
			$('#cpumemory-detail').slideToggle('slow',function(){
        		if ($(this).is(':hidden')) {
        			$("#img_cpu").attr("src","<%=rootPath%>/resource/image/microsoftLook/desc.gif"); 
    			} else {
    				$("#img_cpu").attr("src","<%=rootPath%>/resource/image/microsoftLook/asc.gif"); 
    			}        
   				return false;
   			});        
		});
	$('#utilhdx').bind('click',function(){
			$('#utilhdx-detail').slideToggle('slow',function(){
        		if ($(this).is(':hidden')) {
        			$("#img_utilhdx").attr("src","<%=rootPath%>/resource/image/microsoftLook/desc.gif"); 
    			} else {
    				$("#img_utilhdx").attr("src","<%=rootPath%>/resource/image/microsoftLook/asc.gif"); 
    			}        
   				return false;
   			}); 
		});
	$('#utilhdxper').bind('click',function(){
			$('#utilhdxper-detail').slideToggle('slow',function(){
        		if ($(this).is(':hidden')) {
        			$("#img_utilhdxper").attr("src","<%=rootPath%>/resource/image/microsoftLook/desc.gif"); 
    			} else {
    				$("#img_utilhdxper").attr("src","<%=rootPath%>/resource/image/microsoftLook/asc.gif"); 
    			}        
   				return false;
   			}); 
		});
});

	/***********************************************************************************************************
	箭头符号点击后 更改图标
	参数
		tableId 需要更改图标的ID
		imgId   链接（箭头符号的ID）
	***********************************************************************************************************/
	function display(tableId,imgId){
		alert(document.getElementById(tableId).style.display);
		if(document.getElementById(tableId).style.display != 'none'){
			document.getElementById(imgId).src = '<%=rootPath%>/resource/image/microsoftLook/desc.gif';
		}else{
			document.getElementById(imgId).src = '<%=rootPath%>/resource/image/microsoftLook/asc.gif';
		}
	}
function refresh(){
     var topn = document.getElementById("topn").value;
     if(topn=='---请选择或输入---'){
         topn = 5;
     }
     var startdate = document.getElementById("startdate").value;
     var todate = document.getElementById("todate").value;
     Ext.MessageBox.wait('数据加载中，请稍后.. '); 
     window.location = "toppx.jsp?topn="+topn+"&startdate="+startdate+"&todate="+todate+"&reporttype="+"<%=reporttype%>";
}
</script>
</head>
<body onload="initmenu();" id="body" class="body" style="overflow: auto;">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form id="mainForm" method="post" name="mainForm">
<table id="content-header" class="content-header">
    <tr>
       	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
       	<td class="content-title" align="left" width="10%"><a href="toppx.jsp">网络设备</a> | <a href="host_toppx.jsp">服务器</a></td>
       	<td align="center" width="90%">网络设备性能TOP<%=topn%><%=title%></td>
        <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
	</tr>
</table>
<table>       
    <tr>
        <td align="right">TOPN：</td>   
        <td align="left" width="120"> 
		<span style="position:absolute;border-top:1pt solid #336699;border-left:1pt solid #336699;border-bottom:1pt solid #336699;width:114px;height:21px;top:32px;"> 
		<input type="text" name="topn" id="topn" style="width:100px;height:15px;border:0pt;" onFocus="javascript:if(this.value=='---请选择或输入---')this.value='';" value="---请选择或输入---"> 
		</span>      
		<span style="position:absolute; border:1pt solid #336699;  width:188px; height:21px; clip:rect(-1px 120px 120px 100px); top:32px;"> 
		<select name="aabb" id="aabb" style="width:120px;height:21px;margin:-2px;" onChange="javascript:topn.value=aabb.value;"> 
		    <option value="">---自己输入---</option> 
		    <option value="5">5</option> 
		    <option value="10">10</option> 
		    <option value="15">15</option> 
		    <option value="20">20</option>
		</select> 
		</span> 
		</td>
        <td height="21" id="dateselect" style="display:none">
		<input type="text" name="startdate" value="<%=b_time%>" size="10">   
	    <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	    <img id="imageCalendar1" width="34" height="21" src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	     至
		<input type="text" name="todate" value="<%=t_time%>" size="10"/>
	    <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	    <img id="imageCalendar2" width="34" height="21" src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	    </td>
	    <td><button onclick="javascript:refresh();">刷新</button></td>
       	<td align="right"><a href="toppx.jsp?reporttype=now">性能TOPN(实时)</a> <b>|</b><a href="toppx.jsp?reporttype=day">性能TOPN(日报)</a> <b>|</b> <a href="toppx.jsp?reporttype=week">性能TOPN(周报)</a> <b>|</b> <a href="toppx.jsp?reporttype=month">性能TOPN(月报)</a></td>
	</tr>
</table>
<table width="98%" align='center' class="body-container">
<% 
    //if(networklist != null && networklist.size()>0){
%>
<tr id="ping">
	<td>
		<table id="add-content-header" class="add-content-header">
			<tr id="ping">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
				<td class="add-content-title">
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="img_ping" src="<%=rootPath%>/resource/image/microsoftLook/asc.gif"/>&nbsp;网络设备通断情况</b>
				</td>
				<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td width="100%" align=center>
	    <div id="ping-detail">
		    <table width="100%">
		        <tr>
		            <td width="48%">
		                <table width="100%" border="1">
		                    <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="50%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="50%">   
									连通率(%)
								</td>
							</tr>
							<% 
							    if(pingList!=null&&pingList.size()>0){
							        for(int i=0;i<pingList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) pingList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"%" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
		                    <tr>
		                        <td  align=center width="100%" height="220" colspan="2"> 
					        		<div id="flashcontent1">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=ping&bids=<%=bids%>", "Column_Network_Ping_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent1");
									</script>
								</td>
		                    </tr>
		                </table>
		            </td>
		            <td width="1%"></td>
		            <td width="48%">
		                <table width="100%" border="1">
		                    <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									响应时间(ms)
								</td>
							</tr>
							<% 
							    if(responseList!=null&&responseList.size()>0){
							        for(int i=0;i<responseList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) responseList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"ms" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
		                    <tr> 
								<td  align=center width="100%" height="220" colspan="2"> 
					        		<div id="flashcontent2">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=ResponseTime&bids=<%=bids%>", "Column_Network_Response_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent2");
									</script>
								</td>
							</tr>
		                </table>
		            </td>
		        </tr>
		    </table>
	    </div>
	</td>
</tr>
<tr id="cpumemory">
	<td colspan=2>
		<table id="add-content-header" class="add-content-header">
			<tr id="cpuliyonglv1">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
				<td class="add-content-title">
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="img_cpu" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif"/>&nbsp;网络设备CPU内存利用率</b>
				</td>
				<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
			</tr>
		</table>  
	</td>
</tr>
<tr>
	<td width="100%" align=center>
	    <div id="cpumemory-detail">
		    <table width="100%">
		        <tr> 
		            <td  align=center width="48%" > 
		                <table width="100%" border="1">
		                    <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									CPU利用率(%)
								</td>
							</tr>
							<% 
							    if(cpuList!=null&&cpuList.size()>0){
							        for(int i=0;i<cpuList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) cpuList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"%" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
		                    <tr>
		                        <td height="220" width="100%" colspan="2">
		                            <div id="flashcontent3">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=cpu&bids=<%=bids%>", "Column_Network_Cpu_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent3");
									</script>
		                        </td>
		                    </tr>
		                </table>
					</td>
					<td width="1%"></td>
					<td  align=center width="48%" > 
					    <table width="100%" border="1">
					        <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									内存利用率(%)
								</td>
							</tr>
							<% 
							    if(memoryList!=null&&memoryList.size()>0){
							        for(int i=0;i<memoryList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) memoryList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"%" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
					        <tr>
		                        <td height="220" width="100%" colspan="2">
		                            <div id="flashcontent4">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=memory&bids=<%=bids%>", "Column_Network_Memory_TopN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent4");
									</script>
		                        </td>
		                    </tr>
					    </table>
					</td>
				</tr>
		    </table>
	    </div>
	</td>
</tr>
<tr id="utilhdx">
	<td colspan=2>
		<table id="add-content-header" class="add-content-header">
			<tr id="utilhdx1">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
			<td class="add-content-title">
				<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="img_utilhdx" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif"/>&nbsp;网络设备端口流速</b>
			</td>
			<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td width="100%" align=center>
	    <div id="utilhdx-detail">
			<table width=100%>
				<tr> 
					<td width="48%"> 
					    <table width="100%" border="1">
					        <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									入口流速(KB/s)
								</td>
							</tr>
							<% 
							    if(utilinList!=null&&utilinList.size()>0){
							        for(int i=0;i<utilinList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) utilinList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"KB/s" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
					        <tr>
					            <td height="220" width=100% colspan=2>
					                <div id="flashcontent5">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=inutil&bids=<%=bids%>", "Column_Network_Inutil_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent5");
									</script>
					            </td>
					        </tr>
					    </table>
					</td>
					<td width="1%"></td>
		            <td width="48%"> 
		                <table width="100%" border="1">
		                    <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									出口流速(KB/s)
								</td>
							</tr>
							<% 
							    if(utiloutList!=null&&utiloutList.size()>0){
							        for(int i=0;i<utiloutList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) utiloutList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"KB/s" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
					        <tr>
					            <td height="220" width=100% colspan=2>
					                <div id="flashcontent6">
							            <strong>You need to upgrade your Flash Player</strong>
						            </div>
						            <script type="text/javascript">
							            var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=oututil&bids=<%=bids%>", "Column_Network_Oututil_TOPN", "99%", "99%", "8", "#ffffff");
							            so.write("flashcontent6");
						            </script>	
					            </td>
					        </tr>
					    </table>
		            </td>
		        </tr>             
		    </table> 
	    </div>
	</td>
</tr>
<tr id="utilhdxper">
	<td colspan=2>
		<table id="add-content-header" class="add-content-header">
			<tr id="utilhdxper">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
				<td class="add-content-title">
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img id="img_utilhdxper" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif"/>&nbsp;网络设备端口带宽利用率</b>
				</td>
				<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
			</tr>
		</table>
	</td>
</tr>
<tr>
	<td width="100%" align=center>
	    <div id="utilhdxper-detail">
			<table width=100%>
				<tr> 
					<td width="48%"> 
					    <table width="100%" border="1">
					        <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									入口带宽利用率(%)
								</td>
							</tr>
							<% 
							    if(utilperinList!=null&&utilperinList.size()>0){
							        for(int i=0;i<utilperinList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) utilperinList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"%" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
					        <tr>
					            <td height="220" width=100% colspan=2>
					                <div id="flashcontent7">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=inutilper&bids=<%=bids%>", "Column_Network_Inutilper_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent7");
									</script>
					            </td>
					        </tr>
					    </table>
					</td>
					<td width="1%"></td>
		            <td width="48%"> 
		                <table width="100%" border="1">
		                    <tr bgcolor="#FFFFFF">
								<td height="29"
									class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									IP地址
								</td>
								<td class="detail-data-body-title"
									style="height: 29; align: center" width="49%">
									出口带宽利用率(%)
								</td>
							</tr>
							<% 
							    if(utilperoutList!=null&&utilperoutList.size()>0){
							        for(int i=0;i<utilperoutList.size();i++){
							            Avgcollectdata hdata = (Avgcollectdata) utilperoutList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getIpaddress() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getThevalue()+"%" %>
								</td>
							</tr>
							<%
							        }
							    }
							%>
						</table>
						<table width="100%">
					        <tr>
					            <td height="220" width=100% colspan=2>
					                <div id="flashcontent8">
							            <strong>You need to upgrade your Flash Player</strong>
						            </div>
						            <script type="text/javascript">
							            var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=1&type=oututilper&bids=<%=bids%>", "Column_Network_oututilper_TOPN", "99%", "99%", "8", "#ffffff");
							            so.write("flashcontent8");
						            </script>	
					            </td>
					        </tr>
					    </table>
		            </td>
		        </tr>             
		    </table> 
	    </div>
	</td>
</tr>
<%
//}
%>
</table>
</form>
</body>
</html>