<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.afunms.config.model.Portconfig"%>
<%@page import="com.afunms.capreport.model.UtilReport"%>
<%@page import="com.afunms.capreport.dao.UtilReportDao"%>
<%@page import="com.afunms.capreport.model.SubscribeResources"%>
<%@page import="com.afunms.application.model.DBVo"%>
<%@page import="com.afunms.application.dao.DBDao"%>
<%@page import="com.afunms.application.dao.TomcatDao"%>
<%@page import="com.afunms.application.dao.IISConfigDao"%>
<%@page import="com.afunms.application.model.Tomcat"%>
<%@page import="com.afunms.application.model.IISConfig"%>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	Vector vec = (Vector) request.getAttribute("vector");
	String sendDate = (String) request.getAttribute("sendDate");
	UtilReport report = (UtilReport) vec.get(0);
	SubscribeResources sub = (SubscribeResources) vec.get(1);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String startdate = sdf.format(new Date());
	String todate = sdf.format(new Date());
%>
<html>
<head>
 
<meta http-equiv="pragma" content="no-cache">   
<meta http-equiv="cache-control" content="no-cache">   
<meta http-equiv="expires" content="0"> 
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<style type="text/css">
</style>
<script type="text/javascript" src="<%=rootPath%>/js/tree/Tree.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/tree/common.js"></script>

<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script> 
<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
<script>
////////////////初始化树/////////////////////////
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
	ddtree.insertNewItem("","root","网络设备资源树", 0, "", "","", "checked");
	ddtree.setCheck("root",0);
	<% 
	String item="";
	Vector<String> vector=new Vector<String>();
	Vector<String> portvector=new Vector<String>();
	if(list!=null&&list.size()>0)
	{
	
		for(int i=0;i<list.size();i++){
			item=(String)list.get(i);
			if(item.indexOf("port")>-1){
				String[] idRelValue=new String[item.split("\\*").length];
				idRelValue=item.split("\\*");
				if (idRelValue.length<4) {
					continue;
				}
				String ip=idRelValue[1];
				String portname=idRelValue[3];//port*222.90.66.20*3*GigabitEthernet0/0
				if(!vector.contains(ip)){
%>
					ddtree.insertNewItem("root","<%=ip%>","<%=ip%>", 0, "", "","", "");
				  //ddtree.insertNewItem("<%=ip%>","port"+"<%=ip%>","端口", 0, "", "","", "");
					ddtree.insertNewItem("<%=ip%>","port"+"<%=portname%>","<%=portname%>", 0, "", "","", "");
<%
					portvector.add("port"+portname);
					vector.add(ip);
				}else {
					if(!portvector.contains("port"+portname)){
%>
					//	ddtree.insertNewItem("<%=ip%>","port"+"<%=ip%>","端口", 0, "", "","", "");
						ddtree.insertNewItem("<%=ip%>","port"+"<%=portname%>","<%=portname%>", 0, "", "","", "");
<%
						portvector.add("port"+portname);
					}else{
%>
						ddtree.insertNewItem("<%=ip%>","port"+"<%=portname%>","<%=portname%>", 0, "", "","", "");
<%
					}
				}
			}
			
		}
	}
	%>
}

</script>

<script language="JavaScript" type="text/JavaScript">
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	
});

 
</script>


<script language="JavaScript" type="text/javascript">


function CreateDeviceWindow(url)
{
	
msgWindow=window.open(url,"_blank","toolbar=no,width=900,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}


function CreateWindow(url)
{
	
 msgWindow=window.open(url,"_blank","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}


</script>
</head>
<body id="body" class="body" onLoad="init();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 189px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
	
	<form id="mainForm" method="post" name="mainForm">
		<div id="loading">
			<div class="loading-indicator">
				<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
			</div>
		<div id="loading-mask" style=""></div>
		<table id="body-container" class="body-container">
			<tr>
				
				<td valign=top style="margin: 10px auto;position :relative;">
				<table >
                <tr>
                <td colspan="2">
                <%
                		String imgpath=com.afunms.common.util.CommonAppUtil.getSkinPath();
                		System.out.println(imgpath);
                		if(imgpath == null || "null".equals(imgpath)){
                			imgpath = "image/";
                		}else{
                			imgpath = imgpath+"image";
                		} 
                %>
     <div style=" height:36px; background:url('<%=rootPath%>/resource/<%=imgpath%>/tree/tit_bg.gif'); padding-left:30px; line-height:36px; font-size:20px; font-weight:bold; color:#fff;">报表模板详细信息</div></td>
</tr>
                    
                    
							<tr>
								<td  height="100%" align="left" valign="top">
				<div id="sorttree" style="margin-top: 0px; background-color: #FFFFFF;OVERFLOW-y:auto;OVERFLOW-x:auto;height:400;width:250;"></div>
							</td>
							<td width="80%" height="100%" valign="top">
							
                            <table border="0" cellpadding="0" cellspacing="0" class="win-content" id="win-content">
																
																<tr >
                                                                 
                                                                      <td width=100% >
                                                                      
                                                                         <table cellpadding="0" cellspacing="0" bgcolor="#FFFFFF" border=1>
																			   <tr>
																				   <td width="100%" align=left>
																				       <div id="editmodel" >
																				      <table border="0" id="table1" cellpadding="0" width="100%">
																		<tr style="background-color: #FFFFFF;">
																				<TD nowrap align="right" height="24" width="10%">
																					报表名称&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="report_name" id="report_name" size="50" class="formStyle" size="50" value="<%=report.getName()%>">
																					
																					
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					报表接收人&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="recievers_name" id="recievers_name" size="50" class="formStyle" size="50" value="<%=sub.getUsername()%>" readonly>
																					
																					<input type="hidden" id="recievers_id" name="recievers_id" value="">
																					
																					<font color='red'>*</font>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					邮件标题:&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="mailTile" id="mailTile"
																						size="50" class="formStyle" value="<%=sub.getEmailtitle()%>">
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					邮件描述:&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="devices_name"
																						id="devices_name" size="50" class="formStyle" value="<%=sub.getEmailcontent()%>">
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="10%">
																					所属业务&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<input type="text" name="bidtext" id="bidtext" size="50" class="formStyle" value="<%=sub.getBidtext()%>">
																					
																					<input type="hidden" id="bid" name="bid" value="">
																					<font color='red'>*</font>
																				</TD>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="10%">
																					报表类型:&nbsp;
																				</TD>
																				<TD nowrap width="40%">
																					&nbsp;
																					<SELECT id="reporttype" name="reporttype">
																					<%
																						if (sub.getReport_type().equals("day")) {
																					%>
																						<OPTION value="day" selected>
																							日报
																						</OPTION>
																						<%
																							} else if (sub.getReport_type().equals("week")) {
																						%>
																						<OPTION value="week">
																							周报
																						</OPTION>
																						<%
																							} else if (sub.getReport_type().equals("month")) {
																						%>
																						<OPTION value="month">
																							月报
																						</OPTION>
																						<%
																							} else if (sub.getReport_type().equals("season")) {
																						%>
																						<OPTION value="season">
																							季报
																						</OPTION>
																						<%
																							} else if (sub.getReport_type().equals("year")) {
																						%>
																						<OPTION value="year">
																							年报
																						</OPTION>
																						<%
																							}
																						%>
																					</SELECT>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24">报表发送时间:&nbsp;</TD>
																				<td nowrap  colspan="3">
																			         <%=sendDate%>
																				</td>
																			</tr>
																			<tr>
																				<TD nowrap colspan="4" align=center>
																					<br>
																					<input type="button" value="关闭" style="width: 50" class="formStylebutton" id="close" onclick='window.close()'>
																					</TD>
																			</tr>
																		</TABLE>
							                                                           </div>
						                                                            </td>
																			    </tr>
																		</table>
																		
                                                                      </td>
                                                                      
                                                                  </tr>
																<tr>
                                                                 
                                                                      <td width=94%>
                                                                         <table cellpadding="0" cellspacing="0" >
																			   <tr>
																				   <td width="100%" align=center>
																				       <div id="netpingReport">
							                                                           </div>
						                                                            </td>
																			    </tr>
																		</table>
                                                                      </td>
                                                                      
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td  align=right>
                                                                          <div id="pingDiv">
                                                                          </div>
                                                                      </td>
                                                                      
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td width=94%>
                                                                         <table cellpadding="0" cellspacing="0" >
																			   <tr>
																				   <td width="100%" align=center>
																				       <div id="netcpuReport">
							                                                           </div>
						                                                            </td>
																			    </tr>
																		</table>
                                                                      </td>
                                                                      
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td width=94%>
                                                                          <div id="cpuDiv">
                                                                          </div>
                                                                      </td>
                                                                     
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td width=94%>
                                                                         <table cellpadding="0" cellspacing="0" >
																			   <tr>
																				   <td width="100%" align=center>
																				       <div id="netmemReport">
							                                                           </div>
						                                                            </td>
																			    </tr>
																		</table>
                                                                      </td>
                                                                      
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td width=94%>
                                                                          <div id="memDiv">
                                                                          </div>
                                                                      </td>
                                                                     
                                                                  </tr>
                                                                   <tr>
                                                                  
                                                                      <td width=94%>
                                                                         <table cellpadding="0" cellspacing="0" >
																			   <tr>
																				   <td width="100%" align=center>
																				       <div id="netportReport">
							                                                           </div>
						                                                            </td>
																			    </tr>
																		</table>
                                                                      </td>
                                                                      
                                                                  </tr>
                                                                  <tr>
                                                                  
                                                                      <td width=94%>
                                                                          <div id="portDiv">
                                                                          </div>
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