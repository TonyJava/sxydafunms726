<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();

	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css" />

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

  function toAdd()
  {
    mainForm.action = "<%=rootPath%>/resourceAllocation.do?action=ready_add";
    mainForm.submit();
  }
</script>
	</head>

	<body id="body" class="body" onload="initmenu();">
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td>
									<table id="content-header" class="content-header">
										<tr>
											<td align="left" width="5">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="content-title">
												系统管理 >> 系统配置 >> 资源配置
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
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
															<a href="#" onclick="toAdd()">添加</a>
															<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table>
													<tr>
														<td align="center" class="body-data-title">
															<INPUT type="checkbox" class="noborder" name="checkall"
																onclick="javascript:chkall()">
														</td>
														<td align="center" class="body-data-title">
															序号
														</td>
														<td align="center" class="body-data-title">
															标题
														</td>
														<td align="center" class="body-data-title">
															维护人（当前登录人员）
														</td>
														<td align="center" class="body-data-title">
															开启时间
														</td>
														<td align="center" class="body-data-title">
															结束时间
														</td>
														<td align="center" class="body-data-title">
															记录状态
														</td>
														<td align="center" class="body-data-title">
															描述
														</td>
														<td align="center" class="body-data-title">
															资源配置（资源列表）
														</td>
														<td align="center" class="body-data-title">
															编辑（当前记录）
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
