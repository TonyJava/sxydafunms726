<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>
<%@page import="com.afunms.config.model.TimingBackupTelnetConfig"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*" %>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>

		<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js"
			charset="gb2312"></script>

		<script language="JavaScript" type="text/javascript">

 Ext.onReady(function()
{
 Ext.get("process").on("click",function(){
    var chk1 = checkinput("ipaddress","string","�����豸",50,false);
    var chk2 = checkinput("entrynumber","string","�����豸",50,false);
    if(chk1 && chk2)
    {
    	Ext.MessageBox.wait('���ݼ����У����Ժ�.. ');
    	mainForm.action = "<%=rootPath%>/ciscosla.do?action=fileBackup";
    	mainForm.submit();
    }
 });
});

//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}  

//-- nielin add at 2010-07-27 start ----------------
function setBid(eventTextId , eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/business.do?action=setBid&event='+event.id+'&value='+event.value + '&eventText=' + eventTextId);
}
//-- nielin add at 2010-07-27 end ----------------  

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------
function showup(){
	var url="<%=rootPath%>/vpntelnetconf.do?action=multi_netip";
	window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
}
//�������
$(document).ready(function()
{
������$("#add_but").click(function()
        {
������������var $table=$("#advance_tab tr");
������������var len=$table.length;
������������$("#advance_tab").append("<tr id=advance"+(len)+"><td ><select   id='advance_config'"+(len)+" ><option value='1'>����</option><option value='0'>������</option></select>&nbsp;<input id='advance_value'"+(len)+" type='text' maxlength='200' size='50' class='formStyle'>&nbsp;<input id='advance_cut'"+(len)+"  type='button' maxlength='200' size='50' class='formStyle' value=' - 'onclick='deltr("+(len)+")'></td></tr>");����
           
����������})��
})
function deltr(index)
{
����������$table=$("#advance_tab tr");
����������{
������������$("tr[id='advance"+index+"']").remove();��

      ��
����������}��
}
</script>

		<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//�޸Ĳ˵������¼�ͷ����
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
//��Ӳ˵�	
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
	timeShareConfiginit();
	 // nielin add for time-sharing at 2010-01-04
}
function timeType(obj){
	var type = obj.value;
	document.getElementById('td_sendtimehou').style.display='none';
	document.getElementById('td_sendtimeday').style.display='none';
	document.getElementById('td_sendtimeweek').style.display='none';
	document.getElementById('td_sendtimemonth').style.display='none';
	if(type==1){
		document.getElementById('td_sendtimehou').style.display='';
	}else if(type==2){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeweek').style.display='';
	}else if(type==3){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
	}else if(type==4){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}else if(type==5){
		document.getElementById('td_sendtimehou').style.display='';
		document.getElementById('td_sendtimeday').style.display='';
		document.getElementById('td_sendtimemonth').style.display='';
	}
}
</script>
		<script type="text/javascript">
			function showTelnetNetList(){
				var url="<%=rootPath%>/ciscosla.do?action=multi_telnet_netip";
				window.open(url,"portScanWindow","toolbar=no,width=900,height=600,directories=no,status=no,scrollbars=yes,menubar=no,resizable=yes");
			}
			
			function toAdd(){
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_add";
			    mainForm.submit();
			}
			
			function toDelete(){  
     				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=deleteTimingBackupTelnetConfig";
     				mainForm.submit();
	  		}
		</script>

	</head>
	<body id="body" class="body" onload="initmenu();">


		<form name="mainForm" method="post">

			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
					<table id="container-main" class="container-main">
						<tr>
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">&nbsp;>> �Զ��� >> �Զ������� >> CISCO SLA ���</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										<input type="hidden" name="selVal" id='selVal' value=""/>
                                                                 <input type="hidden" name="textVal" id='textVal' value=""/>
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
													 						<tr>
																				
																				<TD nowrap align="right" height="24" width="20%">
																					�����豸&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<input name="ipaddress" type="text" size="40" class="formStyle" id="ipaddress" readonly="readonly" >
																					<input type="button" value="ѡ�������豸" onclick="showTelnetNetList()"><font color="red">&nbsp;* </font>
																					
																				</TD>  
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					����&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="name" maxlength="80" size="20" class="formStyle">
																				</TD>
																			</tr>
																			<tr >
																				<TD nowrap align="right" height="24" width="20%">
																					����&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="descr" maxlength="80" size="20" class="formStyle">
																				</TD>
																			</tr>	
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">SLA����&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="slatype" name="slatype">
																								<option value="echo">Echo ��ҵ</option>
																								<option value="pathecho">PathEcho ��ҵ</option>
																								<option value="tcpconnect-noresponder">TCPConnect(����Responder)</option>
																								<option value="tcpconnect-noresponder">TCPConnect(δ����Responder)</option>
																								<option value="udpecho">UDPEcho</option>
																								<option value="dns">DNS</option>
																								<option value="http">HTTP</option>
																								<option value="jitter">Jitter</option>
																						</select>
																				</td>
																			</tr>																		
																			<tr>
																				<TD nowrap align="right" height="24" width="20%">�ɼ����&nbsp;</TD>
																				<td>&nbsp;
																			    		<select id="poll_interval" name="poll_interval">
																								<option value="1-m">1����</option>
																								<option value="2-m">2����</option>
																								<option value="3-m">3����</option>
																								<option value="4-m">4����</option>
																								<option value="5-m">5����</option>
																								<option value="10-m">10����</option>
																								<option value="30-m">30����</option>
																								<option value="1-h">1Сʱ</option>
																								<option value="4-h">4Сʱ</option>
																								<option value="8-h">8Сʱ</option>
																								<option value="12-h">12Сʱ</option>
																								<option value="1-d">1��</option>
																								<option value="1-w">1��</option>
																								<option value="1-mt">1��</option>
																								<option value="1-y">1��</option>
																						</select>
																				</td>
																			</tr>
																			<tr style="background-color: #ECECEC;">
																				<TD nowrap align="right" height="24" width="20%">
																					�Ƿ�����&nbsp;
																				</TD>
																				<TD nowrap   >
																					&nbsp;
																					<select name="mon_flag">
																						<option value="1" label="��">��</option>
																						<option value="0" label="��">��</option>
																					</select>
																				</TD>
																			</tr>
																			<tr>
																				<TD nowrap align="right" height="24" width="20%">
																					������к�&nbsp;
																				</TD>
																				<TD >&nbsp;
																					<input type="text" name="entrynumber" maxlength="80" size="20" class="formStyle">
																				</TD>
																			</tr>																			
																            <tr style="background-color: #ECECEC;">	
																				<TD nowrap align="right" height="24">����ҵ��&nbsp;</TD>
																				<td nowrap colspan="3" height="1">&nbsp;
																				<input type=text readonly="readonly" onclick='setBid("bidtext" , "bid");' id="bidtext" name="bidtext" size="50" maxlength="32" value="">&nbsp;&nbsp;<input type=button value="��������ҵ��" onclick='setBid("bidtext" , "bid");'>
																				<input type="hidden" id="bid" name="bid" value="">
																				</td>
																			</tr>																			
																			<tr>
																				<TD nowrap colspan="4" align=center > 
																				<br>
																				
																					<input type="button" id="process" style="width:50" value="ȷ ��">&nbsp;&nbsp;  
																					<input type="reset" style="width: 50" value="��  ��" onclick="javascript:history.back(1)">
																				</TD>	
																			</tr>	
																		</TABLE>
										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
				</td>
				</tr>
			</table>
		</form>
	</body>
</HTML>