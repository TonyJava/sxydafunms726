<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%
  String rootPath = request.getContextPath();
  //System.out.println(rootPath);
  List list = (List)request.getAttribute("list");
  
String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");
String menuTable = (String)request.getAttribute("menuTable");
Hashtable allreporthash = (Hashtable) request.getAttribute("allreporthash");
int rowSize = 0;

if(allreporthash != null && !allreporthash.isEmpty()){
	rowSize = allreporthash.size();
}
String runAppraise = (String)request.getAttribute("runAppraise");
%>

<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script language="javascript">	

function accEvent(eventid){
	mainForm.eventid.value=eventid;
	mainForm.action="<%=rootPath%>/event.do?action=accit";	
	mainForm.submit();
}

function query(){
  	var startdate = mainForm.startdate.value;
  	var todate = mainForm.todate.value;      
  	var oids ="<%=(String)request.getAttribute("oids")%>";
if(oids != 0)
{
 	//window.open ("<%=rootPath%>/netreport.do?action=downloadselfnetchocereport&startdate="+startdate+"&todate="+todate+"&ids="+oids, "newwindow", "height=500, width=600, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes")    	      
	mainForm.action="<%=rootPath%>/netreport.do?action=downloadselfnetchocereport&startdate="+startdate+"&todate="+todate+"&ids="+oids;
	mainForm.submit();   
}
else{
alert("请选择设备");
}


	//mainForm.action="<%=rootPath%>/netreport.do?action=netevent";
	//mainForm.submit();
} 
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }
  
    function doDelete()
  {  
     mainForm.action = "<%=rootPath%>/event.do?action=dodelete";
     mainForm.submit();
  }  
  
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
     
     //if(chk1&&chk2&&chk3)
     //{
     
       // Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
	mainForm.action="<%=rootPath%>/netreport.do?action=downloadselfnetchocereport";
	mainForm.submit();        
        //mainForm.action = "<%=rootPath%>/network.do?action=add";
        //mainForm.submit();
     //}  
       // mainForm.submit();
 });	
	
});

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

}
</script>
<style>
	.trClass{
		background-color: white;
	}
</style>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
<form method="post" name="mainForm">
<input type=hidden name="eventid">
<table id="body-container" class="body-container">
	<tr>
	<td width="200" valign=top align=center>
	
			<%=menuTable %>
	</td>
		<td align="center" valign=top>
			<table width="98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title"><b>报表 >> 决策支持 >> 网络设备</b></td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>
  						<tr>
		
					<tr>           
								<td>
								<table width="100%" cellpadding="0" cellspacing="1" >
        			<tr>
					            <td bgcolor="#ECECEC" width="100%" align='left'>&nbsp;&nbsp;&nbsp;开始日期
		<input type="text" name="startdate" value="<%=startdate%>" size="10">
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
	<img id=imageCalendar1 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	
		截止日期
		<input type="text" name="todate" value="<%=todate%>" size="10"/>
	<a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	<img id=imageCalendar2 align=absmiddle width=34 height=21 src="<%=rootPath%>/include/calendar/button.gif" border=0></a>
	&nbsp;&nbsp;<input type="button" name="doprocess" value="导出" onclick="query()">
				</td>
									</tr>
        								</table>
										</td>
        						</tr>		
											
						
				<tr>
					<td colspan="2">
						<table cellspacing="1" cellpadding="0" width="100%">
							<tr align="center" height=28 class="microsoftLook0"> 
						    	<td colspan='15'>网络设备决策支持报表
						    		<input type="hidden"  name="ids" value="<%=request.getAttribute("oids")%>"/>
						    	</td>
					   		</tr>
					   		<tr align="center" height=28 class="microsoftLook0"> 
						    	<td>时间</td>
						    	<td colspan="14" class="trClass">&nbsp;&nbsp;从&nbsp;<%=startdate %>&nbsp;到&nbsp;<%=todate %></td>
					   		</tr>
					   		<tr align="center" height=28 class="microsoftLook0"> 
						    	<td>管理员</td>
						    	<td colspan="3" class="trClass"><%=request.getAttribute("username") %></td>
						    	<td>部门</td>
						    	<td colspan="4" class="trClass"><%=request.getAttribute("positionname") %></td>
						    	<td>运行评价</td>
						    	<td colspan="5" class="trClass">
						    		<select name="runAppraise">
						    			<option label="优" <%="优".equals(runAppraise) ? "selected" : ""%>>优</option>
						    			<option label="良" <%="良".equals(runAppraise) ? "selected" : ""%>>良</option>
						    			<option label="差" <%="差".equals(runAppraise) ? "selected" : ""%>>差</option>
						    		</select>
						    	</td>
					   		</tr>
					   		<tr align="center" height=28 class="microsoftLook0"> 
						    	<td rowspan="<%=rowSize+3 %>">网络设备</td> 
						    	<td rowspan="3">IP</td>
						    	<td rowspan="3">别名</td>
						    	<td rowspan="3">用途</td>
						    	<td colspan="2">CPU(%)</td>
						    	<td colspan="2">物理内存</td>
						    	<td colspan="4">流速(KB/S)</td>
						    	<td colspan="3">事件(个)</td>
						    </tr>
						    <tr align="center" height=28 class="microsoftLook0">
						    	<td rowspan="2">平均</td> 
						    	<td rowspan="2">最大</td> 
						    	<td rowspan="2">平均</td> 
						    	<td rowspan="2">最大</td> 
						    	<td colspan="2">上联</td>
						    	<td colspan="2">下联</td>
						    	<td rowspan="2">普通</td>
						    	<td rowspan="2">严重</td>
						    	<td rowspan="2">紧急</td>
						    </tr>
						    <tr align="center" height=28 class="microsoftLook0">
						    	<td>平均</td> 
						    	<td>最大</td> 
						    	<td>平均</td> 
						    	<td>最大</td> 
						    </tr>
						    <%
							    if (allreporthash != null && !allreporthash.isEmpty()) {
									Iterator keys = allreporthash.keySet().iterator();
									String ip = "";
									while (keys.hasNext()) {
										ip = keys.next().toString();
										Hashtable report_has = (Hashtable) allreporthash.get(ip);
										String hostname = (String) report_has.get("equipname");
										Hashtable CPU = (Hashtable) report_has.get("CPU");
										String avginput = (String) report_has.get("avginput");
										String avgoutput = (String) report_has.get("avgoutput");
										String maxinput = (String) report_has.get("maxinput");
										String maxoutput = (String) report_has.get("maxoutput");
	
										String levelone = (String) report_has.get("levelone");
										String levletwo = (String) report_has.get("levletwo");
										String levelthree = (String) report_has.get("levelthree");
						    %>
						   	<tr align="left" height=28 class="microsoftLook0">
						   		<td class="trClass"><%=ip %></td> 
						   		<td class="trClass"><%=hostname %></td> 
						   		<td class="trClass">&nbsp;</td> 
						   		<td class="trClass"><%=(String) CPU.get("avgcpu")%></td> 
						   		<td class="trClass"><%=(String) CPU.get("cpumax")%></td> 
						   		<td class="trClass">&nbsp;</td> 
						   		<td class="trClass">&nbsp;</td> 
						   		<td class="trClass"><%=avgoutput.replace(".0", "")%></td> 
						   		<td class="trClass"><%=maxoutput.replace(".0", "")%></td> 
						   		<td class="trClass"><%=avginput.replace(".0", "")%></td> 
						   		<td class="trClass"><%=maxinput.replace(".0", "")%></td>
						   		<td class="trClass"><%=levelone%></td>
						   		<td class="trClass"><%=levletwo%></td>
						   		<td class="trClass"><%=levelthree%></td>
						    </tr>
						    
						    <%
									}
							    }
						    %>
						    <tr align="center" height=28 class="microsoftLook0">
						    	<td colspan="15">业务分析</td>
						    </tr>
						    <tr align="center" height=28 class="microsoftLook0">
						   		<td colspan="15" class="trClass"><textarea rows="5" name="businessAnalytics" cols="210" style="border: 1;"></textarea></td> 
						    </tr>
		 				</table>
					</td>
				</tr>	
				<tr>
              		<td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="11" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="11" /></td>
                  </tr>
              </table></td>
            </tr>	
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>
