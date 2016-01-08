<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	SimpleDateFormat filesdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	Date d = new Date();
	String time = sdf.format(d);
	String filename = filesdf.format(d);
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
		<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="javascript">
 var number = 0;
 //全选
 function chkall(obj)
 {
 	var checkbox = document.getElementsByName("checkbox"); 
 	var selectobj = document.getElementById("selectednum");
 	if(obj.checked == true)
 	{	 
    	for( var i=0; i < checkbox.length; i++ )
    	{
        	checkbox[i].checked = true;
        }
        number = checkbox.length;
        selectobj.innerHTML = number;
    }
    else
    {
    	for( var i=0; i < checkbox.length; i++ )
    	{
        	checkbox[i].checked = false;
        }
        number = 0;
        selectobj.innerHTML = number;
    }       
 } 
 
 function checkChanged(obj)
 {
 	obj.checked ? number++ : number--;
 	if(number<0)
 	{
 		number=0;
 	}
 	var selectobj = document.getElementById("selectednum");
 	selectobj.innerHTML = number;
 }
 
 
  Ext.onReady(function()
{  
	 Ext.get("autobackup").on("click",function(){
	        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	        mainForm.action = "<%=rootPath%>/dbbackup.do?action=autobackup";
	        mainForm.submit();
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
									                	<td class="content-title">系统管理 >> 数据库维护 >> 数据备份 >> 添加 </td>
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
					        										<td>
					        											<table>
					        												<tr>
					        													<td align="right" class="body-data-title" width="11%">数据库文件名：</td>
					        													<td align="left"  class="body-data-title"  width="11%">
					        														<input type="text"  size="26" id="filename" name="filename" value="<%=filename %>">&nbsp;*请使用英文字母或数字命名
					        														<input type="hidden"  size="26" id="time" name="time" value="<%=time %>">
					        													</td>
					        												</tr>
					        												<tr>
					        												<tr>
					        													<td align="center"  width="11%" colspan="2"><input type="button" id="autobackup" name="autobackup" value="保存">
					        													<input type="button" onclick="javascript:history.go(-1);" value="返回"></td>
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
