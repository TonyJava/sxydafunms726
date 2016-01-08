<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.CustomXml"%>
<%@ include file="/include/globe.inc"%>
<%
	List list = (List) request.getAttribute("list");
	String menuTable = (String) request.getAttribute("menuTable");
	int rc = list.size();
	String rootPath = request.getContextPath();
	JspPage jp = (JspPage) request.getAttribute("page");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/javascript">
  var listAction = "<%=rootPath%>/customxml.do?action=list";
  var delAction = "<%=rootPath%>/customxml.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;

  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/customxml.do?action=ready_add";
     mainForm.submit();
  }
  
  function setDefault()
  {
    var bExist = false;

    if ( mainForm.radio.length == null ) 
    {
      if( mainForm.radio.checked )  
         bExist = true;
    }
    else  
    {
       for( var i=0; i < mainForm.radio.length; i++ )
       {
         if(mainForm.radio[i].checked)
            bExist = true;
       }
    }
    if(bExist)
    {
        mainForm.action = "<%=rootPath%>/customxml.do?action=set_default";
        mainForm.submit();
    }
    else
    {
       alert("请选择要设置的记录");
       return false;
    }
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
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															资源 >> 视图管理 >> 视图列表
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
																		&nbsp;&nbsp;
																		<a href="#" onclick="toDelete2()">删除</a>
																		&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
																<tr>
									    							<td class="body-data-title">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
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
																		序号
																	</td>
																	<td align="center" class="body-data-title">
																		视图名
																	</td>
																	<td align="center" class="body-data-title">
																		编辑
																	</td>
																	<td align="center" class="body-data-title">
																		编辑结点
																	</td>
																	<td align="center" class="body-data-title">
																		编辑连线
																	</td>
																</tr>
																<%
																	int startRow = jp.getStartRow();
																	for (int i = 0; i < rc; i++) {
																		CustomXml vo = (CustomXml) list.get(i);
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<INPUT type="radio" class=noborder name=radio
																			value="<%=vo.getId()%>" class=noborder>
																		<font color='blue'><%=startRow + i%></font>
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getViewName()%></td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/customxml.do?action=ready_edit&id=<%=vo.getId()%>">
																			<img src="<%=rootPath%>/resource/image/editicon.gif"
																				border="0" /> </a>
																	</td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/customview.do?action=ready_edit_nodes&id=<%=vo.getId()%>"><img
																				src="<%=rootPath%>/resource/image/editicon.gif"
																				border="0" /> </a>
																	</td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/customview.do?action=ready_edit_lines&id=<%=vo.getId()%>"><img
																				src="<%=rootPath%>/resource/image/editicon.gif"
																				border="0" /> </a>
																	</td>
																</tr>
																<%
																	}
																%>
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
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_01.jpg"
																			width="5" height="12" />
																	</td>
																	<td></td>
																	<td align="right" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_03.jpg"
																			width="5" height="12" />
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
	</BODY>
</HTML>
