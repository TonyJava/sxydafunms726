<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.config.model.Hua3VPNFileConfig"%>
<%@page import="com.afunms.config.model.VPNFileConfig"%>
<%@page import="java.util.Iterator"%>

<html>
	<head>
		<%
			String rootPath = request.getContextPath();
			String baseContent = (String) request.getAttribute("baseContent");
			String content = (String) request.getAttribute("content");
			
			String baseCfgName = (String) request.getAttribute("baseCfgName");
			Hashtable configfile = (Hashtable) request.getAttribute("allcmpCfgName");
			
		   //System.out.println("==================================");
		   
		   //System.out.println(configfile.toString());
		   //System.out.println("==================================");
		   
		   
			
			
			
		%>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<title>备份配置文件比对</title>
		<!-- snow add 2010-5-28 -->
		<style>
<!--
body {
	background-image:url(${pageContext.request.contextPath}/resource/image/bg4.jpg);
	TEXT-ALIGN: center;
}
-->
<link rel="stylesheet" href="/afunms/resource/css/style.css" type="text/css">
</style>

		<script language="javascript" src="/afunms/js/tool.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script language="javascript">


</script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	</head>
	<body id="body" class="body">
	<DIV  style=" OVERFLOW-X: scroll; scrollbar-face-color:#B3DDF7;scrollbar-shadow-color:#B3DDF7;scrollbar-highlight-color:#B3DDF7;scrollbar-3dlight-color:#EBEBE4;scrollbar-darkshadow-color:#EBEBE4;scrollbar-track-color:#F4F4F0;scrollbar-arrow-color:#000000; width:100%;HEIGHT: 300px">
		<table id="container-main" class="container-main">
			<tr>
				<td >
					<table id="container-main-win" class="container-main-win">
						<tr>
							<td align=center>
								<table id="win-content" class="win-content">
									<tr>
										<td>
											<table id="win-content-header" class="win-content-header">
												<tr>
													<td align="left" width="5">
														<img src="<%=rootPath%>/common/images/right_t_01.jpg"
															width="5" height="29" />
													</td>
													<td class="win-content-title">
														自动化 >> 配置文件管理 >> 备份配置文件比对
													</td>
													<td align="right">
														<img src="<%=rootPath%>/common/images/right_t_03.jpg"
															width="5" height="29" />
													</td>
												</tr>

											</table>
										</td>
									</tr>
									<%
										if (!baseCfgName.equals("")) {
											
											
										

											
									%>
									<tr>
										<td>
											<table id="win-content-body" class="win-content-body">


												<tr align="left" valign="middle">
													<td align="left" width="50%">
														<table>
															<tr>
																<td align=center>
																	基线文件(<%=baseCfgName%>)
																</td>
															</tr>
															<tr>
																<td>
																	<textarea id="baseContent" name="baseContent" rows="38"
																		cols="98" readonly="readonly"><%=baseContent%></textarea>
																</td>

															</tr>
														</table>
													</td>
													
													
													<% 
													  
												
												 
												      Iterator    it=configfile.keySet().iterator();
												      while(it.hasNext())
												      {
												           Object obj=it.next();
												           //System.out.println("key:"+obj+"    value:"+configfile.get(obj));
												     
													
													%>
													<td width="50%">
														<table>
															<tr>
																<td align=center>
																	比对文件(<%=obj%>)
																</td>
															</tr>
															<tr>
																<td>
																	<textarea id="content" name="content" rows="38"
																		cols="98" readonly="readonly"><%=configfile.get(obj)%></textarea>
																</td>

															</tr>
														</table>

													</td>
													
													
													<%
													
												      }
													
													%>
												</tr>

											</table>
										</td>
									</tr>
									<%
									
										
										}else{ %>
									<tr>
									   <td width="50%">
									     <table  border=1 cellspacing=0 cellpadding=0
													align='center'>
													<tr>
														<td  align="center" height="30">
															<b>加载错误</b>
														</td>
													</tr>
													<tr class="othertr">
														<td  align="center" height="70"
															>
															<font color="red">基线文件或比对文件不存在!</font>
														</td>
													</tr>
													<tr>

													</tr>
												</table>
											</td>
										</tr>
								    <%}%>
								    
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
       
		<div align=center>
			<input type=button value="关  闭" onclick="window.close()">
		</div>
		</div>
		</body>
</html>