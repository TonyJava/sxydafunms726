<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.List"%>
<%@page import="com.afunms.cabinet.model.RoomLaw"%>
<%@page import="com.afunms.cabinet.model.EqpRoom"%>


<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List roomList=(List)request.getAttribute("roomList");
 
  %>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.4.2.min.js"></script>

<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  
 Ext.get("process").on("click",function(){
  
	   var chk1 = checkinput("name","string","名称",30,false);
	   var chk2 = checkinput("extraFile","string","名称",30,false);
       if(chk1&&chk2){   
            Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/roomvideo.do?action=add";
        	mainForm.submit();
     }
 });	
});
  function CreateWindow(url){
		window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}
	function showup(){
		CreateWindow("<%=rootPath%>/cabinet/roomvideo/up.jsp");
	}
</script>
</head>
<body id="body" class="body">



	<form id="mainForm" method="post" name="mainForm" >
		
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
											                	<td class="add-content-title">3D机房 >> 机房管理 >> 机房管理视频管理 >> 添加</td>
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
				        										   <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																
																	<tr style="background-color: #ECECEC;">						
																		<TD nowrap align="right" height="24" width="10%">名称&nbsp;</TD>				
																		<TD nowrap width="40%">&nbsp;<input type="text" id="name" name="name" size="30" class="formStyle"><font color="red">&nbsp;*</font></TD>															
																	</tr>
																	<tr style="background-color: #ECECEC;">						
																		<TD nowrap align="right" height="24" width="10%">所属机房&nbsp;</TD>				
																		<TD nowrap width="40%">&nbsp;<select id="cabinetid" name="cabinetid">
																		       <% if(roomList!=null&&roomList.size()>0){
																		    	   for(int i=0;i<roomList.size();i++){
																		    		   EqpRoom room=(EqpRoom)roomList.get(i);
																		        %>
																		          <option value="<%=room.getId()%>"><%=room.getName()%></option>
																		       <%
																		       }}%>
																		</select></TD>															
																	</tr>
																		
																	<tr>
																		<TD nowrap align="right" height="24">描述&nbsp;</TD>				
																		<TD nowrap width="40%">&nbsp;<input type="text" id="description" name="description" size="60" class="formStyle"></TD>	
																	</tr>
																	<tr >
																		<TD nowrap align="right" height="24" width="10%">
																			附件&nbsp;
																		</TD>
																		<TD nowrap width="40%">&nbsp;<input type="text" id="extraFile" name="extraFile" size="20" readonly="readonly"><input type="button" value="添加附件" onclick="showup()">
																			<font color="red">&nbsp;*</font>
																		</TD>
																	</tr>	

																	<tr>
																		<TD nowrap colspan="4" align=center>
																		    <input type="button" value="保 存" style="width:50" id="process" >&nbsp;&nbsp;
																			<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
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
									<tr>
										<td>
											
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