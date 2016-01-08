<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.afunms.sysset.model.DBBackupAuto"%>

<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
	List backList = (ArrayList) request.getAttribute("backList");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
	<script language="javascript">
	 
	 //开始备份
	 function addbackup(obj)
	 {
	 	mainForm.action = "<%=rootPath%>/dbbackup.do?action=addbackup";
	    mainForm.submit();
	 }
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
/***************
下载备份的数据库文件
****************/
function downloadDBFile(id){
	mainForm.action = "<%=rootPath%>/dbbackup.do?action=downloadDBFile&id="+id;
    mainForm.submit();
}

function deleteDBFile(){
	if(!confirm("确认删除该文件？")){
 		return false;
 	}
 	mainForm.action = "<%=rootPath%>/dbbackup.do?action=deleteDBFile";
    mainForm.submit();
}
</script>
	</head>
	<BODY id="body" class="body" onload="initmenu();if( ${result } ) alert('${msg }');">
		<form id="mainForm" method="post" name="mainForm">
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
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title">系统管理 >> 数据库维护 >> 数据库列表 </td>
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
																	<td class="body-data-title" style="text-align: right;">
																		[<a href="#" onclick="addbackup(this);">添加</a>&nbsp;|&nbsp;<a href="#" onclick="deleteDBFile();">删除</a>]&nbsp;&nbsp;
																	</td>
																</tr>
																<tr>
					        										<td>
					        											<table>
					        												<tr>
					        													<td align="center" class="body-data-title" width="5%"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()">序号</td>
					        													<td align="center" class="body-data-title" width="11%">文件名称</td>
					        													<td align="center" class="body-data-title" width="11%">备份日期</td>
					        												</tr>
					        												<%
					        												if(backList != null){
					        													for(int i=0; i<backList.size(); i++){
					        														DBBackupAuto dBBackupAuto = (DBBackupAuto)backList.get(i);
					        												 %>
					        												<tr  <%=onmouseoverstyle%>>
					        													<td align="center" class="body-data-list"><INPUT type="checkbox" id="checkbox" name="checkbox" value="<%=dBBackupAuto.getId()%>"><%=i+1 %></td>
					        													<td align="center" class="body-data-list"><a href="#" onclick="downloadDBFile('<%=dBBackupAuto.getId()%>');"><%=dBBackupAuto.getFilename() %></a></td>
					        													<td align="center" class="body-data-list"><%=dBBackupAuto.getTime() %></td>
					        												</tr>
					        												<%
					        													}
					        												}
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
		        				</tr>
		        			</td>
						</table>
					</td>
				</tr>
			</table>
		</form>
	</BODY>
</HTML>
