<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@include file="/include/globe.inc"%>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.polling.PollingEngine"%>
<%
String start_id = (String)request.getParameter("start_id");
String end_id = (String)request.getParameter("end_id");   

Host host1 = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(start_id.substring(3)));
Host host2 = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(end_id.substring(3)));
String ip1 = "";   
String ip2 = "";   
String com1 = "";
String com2 = "";    
String snmp1 = "";
String snmp2 = "";
String snmp3 = "";
String snmp4 = "";
System.out.println("=================================");
if(host1!=null){
	ip1=host1.getIpAddress();
	com1 = host1.getCommunity();
	if(host1.getSnmpversion()==0){
		snmp1 = "selected";
	}
	if(host1.getSnmpversion()==1){
		snmp2 = "selected";
	}
}
if(host2!=null){
	ip2=host2.getIpAddress();
	com2 = host2.getCommunity();
	if(host2.getSnmpversion()==0){
		snmp3 = "selected";
	}
	if(host2.getSnmpversion()==1){
		snmp4 = "selected";
	}
}
System.out.println("=================================");
System.out.println(snmp1+"="+snmp2+"="+snmp3+"="+snmp4);
String rootPath = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="UTF-8" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="UTF-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="UTF-8"></script>
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
  function doDiscoverAll()
  {
        mainForm.action = "<%=rootPath%>/linkanalytics.do?action=analyticsFromNode";
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
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"  >
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
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																<TBODY>
			<tr>						
			<TD nowrap align="right" height="24" width="20%">IP地址1&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="ipaddress1"  name="ipaddress1" size="20" class="formStyle" value="<%=ip1 %>"><font color='red'>*</font></TD>															
			<TD nowrap align="right" height="24" width="20%">团体名&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="name1" name="name1" size="20" class="formStyle" value="<%=com1 %>"><font color='red'>*</font></TD>
			<TD nowrap align="right" height="24" width="20%">SNMP版本&nbsp;</TD>				
			<TD nowrap width="30%" >&nbsp;
								<select   name="version1"  class="formStyle">
									<option value=5>--请选择--</option>
									<option value=0 <%=snmp1%>>SNMP&nbsp;V1</option>
									<option value=1 <%=snmp2%>>SNMP&nbsp;V2c</option>
									
								</select>
			</TD>						
			</tr>
			<tr style="background-color: #ECECEC;">						
			<TD nowrap align="right" height="24" width="20%">IP地址2&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="ipaddress2"  name="ipaddress2" size="20" class="formStyle" value="<%=ip2 %>"><font color='red'>*</font></TD>															
			<TD nowrap align="right" height="24" width="20%">团体名&nbsp;</TD>				
			<TD nowrap width="30%">&nbsp;<input type="text" id="name2" name="name2" size="20" class="formStyle" value="<%=com2 %>"><font color='red'>*</font></TD>	
			<TD nowrap align="right" height="24" width="20%">SNMP版本&nbsp;</TD>
			<TD nowrap width="30%" >&nbsp;
				<select   name="version2"  class="formStyle">
					<option value=5>--请选择--</option>
					<option value=0 <%=snmp3%>>SNMP&nbsp;V1</option>
					<option value=1 <%=snmp4%>>SNMP&nbsp;V2c</option>
					
				</select>
			</TD>					
			</tr>
			<tr>
				<!--<TD nowrap align="right" height="24"></TD>	-->
				<TD nowrap width="50%" align=center colspan=6>&nbsp;<input type="button" value="开始分析" style="width:100" class="formStylebutton" onclick="doDiscover()"></font></TD>	
			</tr>	
			<tr>
			<td></td>				
			</tr>		
			</table>			
		</td>
	</tr>
	<tr align="center" valign="center"> 
                    			<td height="28" align="center">
						<textarea id="resultofping" name="showList" rows="15" cols="60" readonly="readonly"></textarea>                    			
                    			</td>
				</tr>  
				<tr>
				<TD nowrap colspan="4" align=center>
				      <div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div> 
					</TD>
					<td></td>	
					</tr>   
</table>
				</tr>
        							
			    </table>
			  </td>
			</tr>
	      </table>
	  </form>

</BODY>   
</HTML>