<%@page language="java" contentType="text/html;charset=gb2312" %>

<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>

<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.indicators.model.NodeDTO"%>
<%@page import="com.afunms.indicators.util.NodeUtil"%>

<%@page import="com.afunms.detail.service.systemInfo.SystemInfoService"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>

<%
	String runmodel = PollingEngine.getCollectwebflag();
  	String tmp = request.getParameter("id"); 
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String _flag = (String)request.getAttribute("flag");//左侧树形栏是否显示标志 1：显示    

	String collecttime = "";
	String sysuptime = "";
	String sysservices = "";

	
   	Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp)); 
   	System.out.println("===host====jsp"+host);
    String[] time = {"",""};
    DateE datemanager = new DateE();
    Calendar current = new GregorianCalendar();
    current.set(Calendar.MINUTE,59);
    current.set(Calendar.SECOND,59);
    time[1] = datemanager.getDateDetail(current);
    current.add(Calendar.HOUR_OF_DAY,-1);
    current.set(Calendar.MINUTE,0);
    current.set(Calendar.SECOND,0);
    time[0] = datemanager.getDateDetail(current);
    Vector systemV = null;
    try{
    	
		//得到系统启动时间
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
								
	
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		
       	request.setAttribute("id", tmp);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
	}catch(Exception e){
		e.printStackTrace();
	}



  	String rootPath = request.getContextPath();  
  
  	//sString chart1 = null,chart2 = null,chart3 = null,responseTime = null;
  	//DefaultPieDataset dpd = new DefaultPieDataset();

  	//chart1 = ChartCreator.createPieChart(dpd,"",130,130);  

   // chart3 = ChartCreator.createMeterChart(40.0,"",120,120);    
	String startdate = (String)request.getAttribute("startdate");
	String todate = (String)request.getAttribute("todate");
	
	int level1 = Integer.parseInt(request.getAttribute("level1")+"");
	int _status = Integer.parseInt(request.getAttribute("status")+"");
	
	String level0str="";
	String level1str="";
	String level2str="";
	String level3str="";
	if(level1 == 0){
		level0str = "selected";
	}else if(level1 == 1){
		level1str = "selected";
	}else if(level1 == 2){
		level2str = "selected";
	}else if(level1 == 3){
		level3str = "selected";	
	}
	
	String status0str="";
	String status1str="";
	String status2str="";
	if(_status == 0){
		status0str = "selected";
	}else if(_status == 1){
		status1str = "selected";
	}else if(_status == 2){
		status2str = "selected";	
	}        
       
%>

<html>
<head>


<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
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
<script>

</script>
<script language="javascript">	
  
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	
	
});
  function query()
  {  
  	mainForm.id.value = <%=tmp%>;
     mainForm.action = "<%=rootPath%>/cabinetalar.do?action=hostevent&flag=<%=_flag%>";
     mainForm.submit();
  }
</script>
<script language="JavaScript" type="text/JavaScript">
$(document).ready(function(){
	//$("#testbtn").bind("click",function(){
	//	gzmajax();
	//});
//setInterval(modifyIpAliasajax,60000);
});
</script>


</head>
<body id="body" class="body" onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>


	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="orderflag">
		<input type=hidden name="id">
		
		<table id="body-container" class="body-container">
			<tr>
	
				<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-detail" width=85%>
								<table id="container-main-detail" class="container-main-detail">
							
											  <tr>
											    	<td>
											    		<table class="detail-data-body">
												      		<tr> 
                			
                  				</tr>
  						<tr>
  						<td colspan=5 bgcolor="#ECECEC" height="28">
	

		&nbsp;&nbsp;&nbsp;&nbsp;开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0></a>
		事件等级
		<select name="level1">
		<option value="99">不限</option>
		<option value="0" <%=level0str%>>提示信息</option>
		<option value="1" <%=level1str%>>普通事件</option>
		<option value="2" <%=level2str%>>严重事件</option>
		<option value="3" <%=level3str%>>紧急事件</option>
		</select>
					
	<input type="button" name="submitss" value="查询" onclick="query()"><hr/>
						</td>						
                    <tr> 
                      <td>
  <table class="tab3"  cellSpacing="0"  cellPadding="0" border=0>
	
  <tr bgcolor="#ECECEC" height="28">
    	<td height="28" class="detail-data-body-title" width="5%"><INPUT type="checkbox" name="checkall" onclick="javascript:chkall()"></td>
        <td width="10%" align="center" class="detail-data-body-title"><strong>事件等级</strong></td>
    	<td width="40%" align="center" class="detail-data-body-title"><strong>事件描述</strong></td>
		<td align="center" class="detail-data-body-title"><strong>登记日期</strong></td>
   </tr>
<%
	int index = 0;
  	java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
  	List list = (List)request.getAttribute("list")==null?(new ArrayList()):(List)request.getAttribute("list");//可能获取的list为空
  	for(int i=0;i<list.size();i++){
 	index++;
  	EventList eventlist = (EventList)list.get(i);
  	Date cc = eventlist.getRecordtime().getTime();
  	Integer eventid = eventlist.getId();
  	String eventlocation = eventlist.getEventlocation();
  	String content = eventlist.getContent();
  	String level = String.valueOf(eventlist.getLevel1());
  	String status = String.valueOf(eventlist.getManagesign());
  	String s = status;
	String bgcolor = "";
  	String act="处理报告";

	if("0".equals(level)){
  		level="提示信息";
  		bgcolor = "bgcolor='blue'";
  	}
	if("1".equals(level)){
  		level="普通告警";
  		bgcolor = "bgcolor='yellow'";
  	}
  	if("2".equals(level)){
  		level="严重告警";
  		bgcolor = "bgcolor='orange'";
  	}
  	if("3".equals(level)){
  		level="紧急告警";
  		bgcolor = "bgcolor='red'";
  	}
  	String rptman = eventlist.getReportman();
  	String rtime1 = _sdf.format(cc);
%>

<tr   bgcolor="#FFFFFF" <%//=onmouseoverstyle%>>

      <td class="detail-data-body-list" ><INPUT type="checkbox" name="checkbox" value="<%=eventlist.getId()%>"><%=i+1%></td>
   	  <td class="detail-data-body-list" <%=bgcolor%>><%=level%></td>
      <td class="detail-data-body-list">
      <%=content%></td>
       <td class="detail-data-body-list">
      <%=rtime1%></td>
      
    
 </tr>


 <%}

 %>  
   
 </table>
                        </td>
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
					</table>
				</td>
			</tr>
		</table>
		
	</form>
</BODY>
</HTML>