<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.discovery.*"%>
<%@page import="com.afunms.polling.node.IfEntity"%>   
<%@page import="java.util.*"%>
<%
List linkList = (List)request.getAttribute("linkList");
String linkstr = "";
String ip1 = (String)request.getAttribute("ipaddress1");
String name1 = (String)request.getAttribute("name1");
String version1 = (String)request.getParameter("version1");
String ip2 = (String)request.getAttribute("ipaddress2");
String name2 = (String)request.getAttribute("name2");
String version2 = (String)request.getParameter("version2");
Iterator it1 = (Iterator)request.getAttribute("start_if");  
Iterator it2 = (Iterator)request.getAttribute("end_if");  
if(linkList == null || linkList.size()==0)linkstr=ip1+" 与 "+ip2+" 之间不存在连接关系！";

String v1sel1 = "";
String v2sel1 = "";
String v1sel2 = "";
String v2sel2 = "";
if(version1.equals("0"))
{
 version1 ="SNMP V1";
 v1sel1="selected";
}else if(version1.equals("1"))
{
 version1="SNMP V2c";
 v2sel1="selected";
}else
{
 version1="SNMP V3";
}
if(version2.equals("0"))
{
 version2 ="SNMP V1";
 v1sel2="selected";
}else if(version2.equals("1"))
{
 version2="SNMP V2c";
 v2sel2="selected";
}else
{
 version2="SNMP V3";
}
 String rootPath = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="UTF-8" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="UTF-8"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">
window.name = "winName"; 
function doDiscover()
{
    var chk1 = checkinput("ipaddress1","ip","IP地址1",15,false);
    var chk2 = checkinput("ipaddress2","ip","IP地址2",15,false);
    var chk3 = checkinput("name1","string","团体名",30,false);
    var chk4 = checkinput("name2","string","团体名",30,false);
    if(chk1&&chk2&&chk3&&chk4){
        Ext.MessageBox.wait('链路分析中，请稍后.. '); 
        mainForm.action = "<%=rootPath%>/linkanalytics.do?action=linkanalytics";
        mainForm.submit();
    }    
}
     
function save(){
    mainForm.action = "<%=rootPath%>/linkanalytics.do?action=linkanalytics";
    mainForm.submit();
}

function fromCore()
{
    mainForm.core_ip.disabled = false;
    mainForm.community.disabled = false;
    //mainForm.net_ip.disabled = true;
    //mainForm.netmask.disabled = true;
    initmenu();
}

function fromNet()
{
    mainForm.core_ip.disabled = true;
    mainForm.community.disabled = true;
    //mainForm.net_ip.disabled = false;
    //mainForm.netmask.disabled = false;
}
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
<form name="mainForm" method="post" target="winName">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td align="center" valign=top>
			<table width="98%"  cellpadding="0" cellspacing="0" algin="center">
				<tr>
					<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
						<table width="100%" cellspacing="0" cellpadding="0">
		                  <tr>
		                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
		                    <td class="layout_title"><b>链路分析</b></td>
		                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
		                  </tr>
		              </table>
				  	</td>
				 </tr>				
				<tr>
					<td>
						<table width="100%" border=0 cellpadding=0 cellspacing=1>
							<tr>
								<td height=300 bgcolor="#FFFFFF" valign="top">				
									<table cellSpacing="0" cellPadding="0" width="100%" border="0" align='left'>
							<tr>
				    <td>
				       <table border="0" id="table1" cellpadding="0" cellspacing="1"width="100%">
			<TBODY>
			<tr>						
			<TD nowrap align="right" height="24" width="15%">IP地址&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="ipaddress1"  name="ipaddress1" size="20" value="<%=ip1%>"class="formStyle"></TD>															
			<TD nowrap align="right" height="24" width="15%">团体名&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="name1" name="name1" size="20" value="<%=name1 %>" class="formStyle"></TD>				<TD nowrap align="right" height="24" width="15%">SNMP版本&nbsp;</TD>				
			<TD nowrap width="30%" >&nbsp;
								<select   name="version1"  class="formStyle">
									<option value=0 <%=v1sel1%>>SNMP&nbsp;V1</option>
									<option value=1 <%=v2sel1%>>SNMP&nbsp;V2c</option>
									
								</select>
			</TD>					
			</tr>
			<tr style="background-color: #ECECEC;">						
			<TD nowrap align="right" height="24" width="15%">IP地址&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="ipaddress2"  name="ipaddress2" size="20" value="<%=ip2%>"class="formStyle"></TD>															
			<TD nowrap align="right" height="24" width="15%">团体名&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="name2" name="name2" size="20" value="<%=name2 %>" class="formStyle"></TD>				<TD nowrap align="right" height="24" width="15%">SNMP版本&nbsp;</TD>				
			<TD nowrap width="30%" >&nbsp;
								<select   name="version2"  class="formStyle">
									<option value=0 <%=v1sel2%>>SNMP&nbsp;V1</option>
									<option value=1 <%=v2sel2%>>SNMP&nbsp;V2c</option>
									
								</select>
			</TD>					
			</tr>
			<tr>	
				<TD nowrap width="100%" colspan=6 align=center>&nbsp;<input type="button" value="开始检测" style="width:100"class="formStylebutton" onclick="doDiscover()"></TD>
			</tr>		
			</table>			
		</td>
	</tr>					
						
	<tr align="center"> 
                    			<td height="28" align="center">
                    				<table border="1" width="100%">
                    					<tr>
                    					    <th align='center' width='5%'>序号</th>
                    						<td>起点IP</td>
                    						<td>起点索引</td>
                    						<td>终点IP</td>
                    						<td>终点索引</td>
                    					</tr>
                    					<%
                    						if(linkList != null && linkList.size()>0){
                    					%>
                    					
                    					<%
                    							for(int i=0;i<linkList.size();i++){
                    								Link link = (Link)linkList.get(i);
                    					%>
                    					<tr bgcolor="DEEBF7">
                    					    <td  align='center'><INPUT type="checkbox" class=noborder name=radio value="<%=link%>" class=noborder ><font color='blue'><%=i+1%></font></td> 
                    						<td><%=link.getStartIp()%></td>
                    						<td><select size=1 name='start_index' style='width:200px;'>
											<%
											  while(it1.hasNext())
											  {
											     IfEntity ifObj = (IfEntity)it1.next();
											     String selected = "";
											     if(ifObj.getIndex().equals(link.getStartIndex())) selected = "selected";
											%> 			
													<option value='<%=ifObj.getIndex()%>' <%=selected%> title="<%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)"><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
											<%}%></select>
											</td>
                    						<td><%=link.getEndIp()%></td>
                    						<td><select size=1 name='start_index' style='width:200px;'>
											<%
											  while(it2.hasNext())
											  {
											     IfEntity ifObj = (IfEntity)it2.next();
											     String selected = "";
											     if(ifObj.getIndex().equals(link.getEndIndex())) selected = "selected";
											%> 			
													<option value='<%=ifObj.getIndex()%>' <%=selected%> title="<%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)"><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
											<%}%></select></td>
                    					</tr>
                    					<%
                    						}
                    					}else{
                    					%>
                    					<tr><td colspan=5><%=linkstr %></td></tr>
                    					
                    					<%
                    					}
                    					%>
                    				</table>
						<!--<textarea id="resultofping" name="showList" rows="15" cols="60" readonly="readonly"><%=linkstr %></textarea> -->                   			
                    			</td>
				</tr>      
				<tr>
				<TD nowrap colspan="4" align=center>
				      <div align=center><br>
				        <input type=button value="保 存" onclick="save()" >
            			<input type=button value="关 闭" onclick="window.close()" >
            			<!--<input type=button value="" onClick='window.open("<%=rootPath%>/tool/snmpping.jsp","oneping","height=400, width= 500, top=300, left=100")' >-->
            		</div> 
					</TD>
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