<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SystemConstant"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.dao.HostInterfaceDao"%>
<%
  	String rootPath = request.getContextPath();
  	List list = (List)request.getAttribute("list");
  	int rc = list.size();
%>
<html>
	<head>
	<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script language="JavaScript" type="text/javascript">
			function check(){
				var eventids = ''; 
				var formItem=document.forms["mainForm"];
				
				
				var formElms=formItem.elements;
				var l=formElms.length;
				while(l--){
				if(formElms[l].type=="checkbox"){
					var checkbox=formElms[l];
					if(checkbox.name == "checkbox" && checkbox.checked==true){
	 					if (eventids==""){
	 						eventids=checkbox.value;
	 					}else{
	 						eventids=eventids+","+checkbox.value;
	 					}
 					}
				}
			}
       		 if(eventids == ""){
        			alert("δѡ��");
        			return ;
        		}

			}
		</script>
</head>
<body id="body" class="body" onload="initmenu();" leftmargin="0" topmargin="0">
		
		<form id="mainForm" name="mainForm" method="post">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title">&nbsp;��Դ�б� &gt;&gt; ��Դά�� </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        								
													
		        									<tr>
		        										<td>
		        											<table>
		        												<tr>
		        													<td align="center" class="body-data-title" width="6%"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()" class="noborder">���</td>
		        													<td align="center" class="body-data-title" width='15%'>
																		<table width="100%" height="100%">
																			<tr>
																				<td width="85%" align='center' style="padding-left:6px;font-weight:bold;"><a href="#" onclick="toListByNameasc()">����</a></td>
																				<td>
																					<img id="nameasc" src="<%=rootPath%>/resource/image/microsoftLook/asc.gif" border="0" onclick="toListByNameasc()" style="CURSOR:hand;margin-left:4px;" />
																					<img id="namedesc" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif" border="0" onclick="toListByNamedesc()" style="CURSOR:hand;margin-left:4px;" />
																				</td>
																			</tr>
																		</table>
																	</td>
		        													<td align="center" class="body-data-title" width="13%">
																		<table width="100%" height="100%">
																			<tr>
																				<td width="85%" align='center' style="padding-left:6px;font-weight:bold;"><a href="#" onclick="toListByIpasc()">IP��ַ</a></td>
																				<td>
																					<img id="ipasc" src="<%=rootPath%>/resource/image/microsoftLook/asc.gif" border="0" onclick="toListByIpasc()" style="CURSOR:hand;margin-left:4px;" />
																					<img id="ipasc" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif" border="0" onclick="toListByIpdesc()" style="CURSOR:hand;margin-left:4px;" />
																				</td>
																			</tr>
																		</table>
																	</td>
		        													<td align="center" class="body-data-title" width="12%">
		        														<table width="100%" height="100%">
																			<tr>
																				<td width="85%" align='center' style="padding-left:6px;font-weight:bold;"><a href="#" onclick="toListByBrIpasc()">MAC��ַ</a></td>
																				<td>
																					<img id="bripasc" src="<%=rootPath%>/resource/image/microsoftLook/asc.gif" border="0" onclick="toListByBrIpasc()" style="CURSOR:hand;margin-left:4px;" />
																					<img id="bripdesc" src="<%=rootPath%>/resource/image/microsoftLook/desc.gif" border="0" onclick="toListByBrIpdesc()" style="CURSOR:hand;margin-left:4px;" />
																				</td>
																			</tr>
																		</table>
		        													</td>
		        													<td align="center" class="body-data-title" width="10%">��������</td>
		        													<td align="center" class="body-data-title" width="10%">�ͺ�</td>
		        													<td align="center" class="body-data-title" width="10%">�豸����</td>
		        													<td align="center" class="body-data-title" width="8%">�ӿ�����</td>
		        													<td align="center" class="body-data-title" width="4%">����</td> 
		        													<td align="center" class="body-data-title" width="6%">�ɼ�Э��</td>
		        												</tr>
		        												<%
					        									    HostNode vo = null;
																	for (int i = 0; i < rc; i++) {
																		String showItemMenu = "";
																		vo = (HostNode) list.get(i);
																		if(vo.getCategory()!=4&&vo.getEndpoint()==0){
																			showItemMenu = "111111000";
																		}else if(vo.getEndpoint()!=0){
																			showItemMenu = "111110001";
																		}else{
																			showItemMenu = "111111110";
																		}
																		String mac="--";
																		if(vo.getBridgeAddress() != null && !vo.getBridgeAddress().equals("null"))mac = vo.getBridgeAddress();
					        									            %>
					        									            <tr>
						        									            <td align="center" class="body-data-list"><INPUT type="checkbox" id="checkbox" name="checkbox" value="<%=vo.getId()%>" class="noborder"></td>						
					        													<td align="center" class="body-data-list"><%=vo.getAlias()%></td>
					        													<td align="center" class="body-data-list"><%=vo.getIpAddress()%></td>
					        													<td align="center" class="body-data-list">
					        														<%
					        															if(mac.length()>17){
					        																String newmac=mac.replaceAll(",", "</option><option>");
					        																out.print("<select>");
					        																out.print("<option>");
					        																out.print(newmac);
					        																out.print("</option>");
					        																out.print("</select>");
					        															}
					        															else{
					        																out.print(mac);
					        															}
					        														 %>
					        													</td>
					        													<td align="center" class="body-data-list"><%=vo.getNetMask()%></td>
					        													<td align="center" class="body-data-list"><%=vo.getType()%></td>
					        													   <%
					        													       String sysdescrforshow="";//������ʾ�豸��Ϣ���
					        													       String sysdescr=vo.getSysDescr();
                  									                                   if(sysdescr!=""&&sysdescr!=null){
														                                  if(sysdescr.length()>20){
															                             sysdescrforshow=sysdescr.substring(0,20)+"...";
													 	                                } else{
															                                sysdescrforshow=sysdescr;
														                                }
													                                 } 
													                               %>
					        													<td align="center" class="body-data-list" nowrap>
					        													 <acronym title="<%=sysdescr%>"><%=sysdescrforshow%></acronym>
					        													</td>
					        													<td align="center" class="body-data-list">
					        														<%//�ӿ�����
																						int entityNumber = 0;
																						HostInterfaceDao hostInterfaceDao = null;  
																						try {
																							hostInterfaceDao = new HostInterfaceDao();
																							entityNumber = hostInterfaceDao.getEntityNumByNodeid(vo.getId());
																						} catch (RuntimeException e1) {
																							e1.printStackTrace();
																						} finally{
																							hostInterfaceDao.close();
																						}
																					%>
																					<%=entityNumber %>
					        													</td>
					        													<%
																				if (vo.isManaged() == true) {
																				%>
																				<td align="center" class="body-data-list">
																					��
																				</td>
																				<%
																				} else {
																				%>
																				<td align="center" class="body-data-list">
																					��
																				</td>
																				<%
																				}
																				%>
																				
																				<%
																					String collectType = "";
																					if(SystemConstant.COLLECTTYPE_SNMP == vo.getCollecttype()){
																						collectType = "SNMP";
																					}else if(SystemConstant.COLLECTTYPE_PING == vo.getCollecttype()){
																					collectType = "PING";
																				}else if(SystemConstant.COLLECTTYPE_REMOTEPING == vo.getCollecttype()){
																					collectType = "REMOTEPING";
																				}else if(SystemConstant.COLLECTTYPE_SHELL == vo.getCollecttype()){
																					//collectType = "SHELL";
																					collectType = "����";
																				}else if(SystemConstant.COLLECTTYPE_SSH == vo.getCollecttype()){
																					collectType = "SSH";
																				}else if(SystemConstant.COLLECTTYPE_TELNET == vo.getCollecttype()){
																					collectType = "TELNET";
																				}else if(SystemConstant.COLLECTTYPE_WMI == vo.getCollecttype()){
																					collectType = "WMI";
																				}else if(SystemConstant.COLLECTTYPE_DATAINTERFACE == vo.getCollecttype()){
																					collectType = "�ӿ�";
																				}
																				%>
					        													<td align="center" class="body-data-list"><%=collectType%></td>  
					        													
				        													</tr>
					        									            <% 
					        									            	}
					        									 			%>
					        									<tr>
																	<TD nowrap colspan="10" align=center>
																		<br><input type="button" value="ȷ ��" style="width:50" id="process" onclick="check()">&nbsp;&nbsp;
																		<input type="reset" style="width:50" value="�ر�" onclick="window.close()">
																	</TD>	
																</tr>
		        											</table>
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
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
		</form>
	</body>
</html>
