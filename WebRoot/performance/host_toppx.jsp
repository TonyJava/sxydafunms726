<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.flex.TopNService"%>
<%@page import="com.afunms.polling.om.Avgcollectdata"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.vo.FlexVo"%>
<% 
   String rootPath = request.getContextPath();
   String reporttype = (String)request.getParameter("reporttype");
   String topn = (String)request.getParameter("topn");
   if(topn==null){topn="5";}
   String title = "(实时)";
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
	List diskList = new ArrayList();
	List utilinList = new ArrayList();
	List utiloutList = new ArrayList();
	TopNService topNService = new TopNService();
	User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	String bids = vo.getBusinessids();
	if (vo.getRole() == 0 || vo.getRole() == 1) {
	    bids = "-1";
	}
	if("day".equals(reporttype)||"week".equals(reporttype)||"month".equals(reporttype)){
	    try{
	        pingList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"ping",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        responseList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"ResponseTime",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        cpuList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"cpu",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        memoryList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"memory",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        diskList = topNService.getHostDiskList(bids,Integer.parseInt(topn));
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilinList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"inutil",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utiloutList = topNService.getNetworkList(4,Integer.parseInt(topn),b_time,t_time,"oututil",bids,"");
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	} else {
	    try{
	        pingList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"ping",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        responseList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"ResponseTime",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        cpuList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"cpu",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        memoryList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"memory",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        diskList = topNService.getHostDiskList(bids,Integer.parseInt(topn));
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utilinList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"inutil",bids);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    try{
	        utiloutList = topNService.getNetworkTempList(4,Integer.parseInt(topn),"oututil",bids);
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
<title>Insert title here</title>
<script type="text/javascript">
/**
*初始化按钮颜色
*/
function initmenu(){
	refreshStyle('toppx');
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
var showdate = "<%=reporttype%>";
$(document).ready(function(){
    if(showdate=="null"||showdate=="now"){
        document.getElementById("dateselect").style.display="none";
    } else {
        document.getElementById("dateselect").style.display="";
    }
    $('#ping-detail').hide();
	$('#cpumemory-detail').hide();
	$('#disk-detail').hide();
	$('#utilhdx-detail').hide();
	$('#ping').bind('click',function(){
			$('#ping-detail').slideToggle('slow');
		});
	$('#cpumemory').bind('click',function(){
			$('#cpumemory-detail').slideToggle('slow');
		});
	$('#disk').bind('click',function(){
			$('#disk-detail').slideToggle('slow');
		});
	$('#utilhdx').bind('click',function(){
			$('#utilhdx-detail').slideToggle('slow');
		});
});
function refresh(){
     var topn = document.getElementById("topn").value;
     if(topn=='---请选择或输入---'){
         topn = 5;
     }
     var startdate = document.getElementById("startdate").value;
     var todate = document.getElementById("todate").value;
     window.location = "host_toppx.jsp?topn="+topn+"&startdate="+startdate+"&todate="+todate+"&reporttype="+"<%=reporttype%>";
}
</script>
</head>
<body onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form id="mainForm" method="post" name="mainForm">
<table id="content-header" class="content-header">
    <tr>
       	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
       	<td class="content-title" width="10%"><p><a href="toppx.jsp">网络设备</a> | <a href="host_toppx.jsp">服务器</a></p></td>
       	<td align="center" width="90%">服务器性能TOP<%=topn%><%=title%></td>
        <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
	</tr>
</table>
<table>       
    <tr>
        <td align="right">TOPN：</td>
        <td align="left" width="120"> 
		<span style="position:absolute;border-top:1pt solid #336699;border-left:1pt solid #336699;border-bottom:1pt solid #336699;width:114px;height:21px;top:45px;"> 
		<input type="text" name="topn" id="topn" style="width:100px;height:15px;border:0pt;" onFocus="javascript:if(this.value=='---请选择或输入---')this.value='';" value="---请选择或输入---"> 
		</span> 
		<span style="position:absolute; border:1pt solid #336699;  width:188px; height:21px; clip:rect(-1px 120px 120px 100px); top:45px;"> 
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
        <td align="left"><button onclick="javascript:refresh();">刷新</button></td>
       	<td align="right"><a href="host_toppx.jsp?reporttype=now">性能TOPN(实时)</a> <b>|</b><a href="host_toppx.jsp?reporttype=day">性能TOPN(日报)</a> <b>|</b> <a href="host_toppx.jsp?reporttype=week">性能TOPN(周报)</a> <b>|</b> <a href="host_toppx.jsp?reporttype=month">性能TOPN(月报)</a></td>
	</tr>
</table>
<table width="100%" align='center'>
<%
//if(hostlist != null && hostlist.size()>0){
%>
<tr id="ping">
	<td>
		<table id="add-content-header" class="add-content-header">
			<tr id="ping">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
				<td class="add-content-title">
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务器通断情况</b>
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=ping&bids=<%=bids%>", "Column_Network_Ping_TOPN", "99%", "99%", "8", "#ffffff");
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=ResponseTime&bids=<%=bids%>", "Column_Network_Response_TOPN", "99%", "99%", "8", "#ffffff");
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
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务器CPU内存利用率</b>
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=cpu&bids=<%=bids%>", "Column_Host_CPU_TOPN", "99%", "99%", "8", "#ffffff");
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=memory&bids=<%=bids%>", "Column_Host_Memory_TOPN", "99%", "99%", "8", "#ffffff");
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
<tr id="disk">
	<td colspan=2>
		<table id="add-content-header" class="add-content-header">
			<tr id="cpuliyonglv1">
				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
				<td class="add-content-title">
					<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务器磁盘利用率</b>
				</td>
				<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
			</tr>
		</table>  
	</td>
</tr>
<tr>
	<td width="100%" align=center>
	    <div id="disk-detail">
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
									磁盘利用率(%)
								</td>
							</tr>
							<% 
							    if(diskList!=null&&diskList.size()>0){
							        for(int i=0;i<diskList.size();i++){
							            FlexVo hdata = (FlexVo) diskList.get(i);
							%>
							 <tr>
								<td align="center" width="50%">
									<%=hdata.getObjectName() %>
								</td>
								<td align="center" width="50%">   
									<%=hdata.getObjectNumber()+"%"%>
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
		                            <div id="flashcontent5">
										<strong>You need to upgrade your Flash Player</strong>
									</div>
									<script type="text/javascript">
										var so = new SWFObject("<%=rootPath%>/flex/Column_Host_Disk_TOP10.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=disk&bids=<%=bids%>", "Column_Host_Disk_TOP10", "99%", "99%", "8", "#ffffff");
					                    so.write("flashcontent5");
									</script>
		                        </td>
		                    </tr>
		                </table>
					</td>
					<td width="1%"></td>
					<td  align=center width="48%" > 
					    
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
				<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;服务器端口流速</b>
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
										var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=inutil&bids=<%=bids%>", "Column_Network_Inutil_TOPN", "99%", "99%", "8", "#ffffff");
										so.write("flashcontent6");
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
									<%=hdata.getThevalue()+hdata.getUnit() %>
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
							            var so = new SWFObject("<%=rootPath%>/flex/Column_Network_Ping_TOPN.swf?topn=<%=topn%>&reporttype=<%=reporttype%>&b_time=<%=b_time%>&t_time=<%=t_time%>&category=4&type=oututil&bids=<%=bids%>", "Column_Network_Oututil_TOPN", "99%", "99%", "8", "#ffffff");
							            so.write("flashcontent7");
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