<%@ page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="java.lang.*"%>

<html>
<head>
<%
  String rootPath = request.getContextPath();
  boolean isOK = (Boolean)request.getAttribute("isOK");
  String name = (String)request.getAttribute("name");
  String urlStatus= "";
  if(isOK )
  	urlStatus = "当前状态可用";
  else
  	urlStatus = "当前状态不可用";
  String str = (String)request.getAttribute("str");
  
%>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<title>WAS可用性检测</title>
<!-- snow add 2010-5-28 -->
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/resource/image/bg4.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
<!-- snow add end -->
  <script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
  <script type='text/javascript' src='/afunms/dwr/engine.js'></script>
<script type='text/javascript' src='/afunms/dwr/util.js'></script>
<script language="javascript" src="/afunms/js/tool.js"></script>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript">
Ext.onReady(function()
{  
setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
});
</script>
</head>
<body id="body" class="body">
	<table id="container-main" class="container-main">
		<tr>
			<td>
				<table id="container-main-win" class="container-main-win">
					<tr>
						<td>
							<table id="win-content" class="win-content">
								<tr>
									<td>
										<table id="win-content-header" class="win-content-header">
				                			<tr>
							                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
							                	<td class="win-content-title">&nbsp;可用性检测</td>
							                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>											   
											</tr>
									    
					       				</table>
				       				</td>
				       			</tr>
						       	<tr>
						       		<td>
						       			<table id="win-content-body" class="win-content-body">
											<tr>
						       					<td>
													<table bgcolor="#ECECEC">
														<tr align="left" valign="center"> 
															<td><b>名称：</b><%=name%></td>
															<td height="28" align="left"><b>&nbsp;地址：</b><%=str %></td>
														</tr>
													</table>
						       					</td>
						       				</tr>
											<tr>
							                	<td class="win-data-title" style="height: 29px;" >&nbsp;</td>
							       			</tr>
							       			<tr>
							       				<td align="center">
							       					<div id="loading">
														<div class="loading-indicator">
															<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...
														</div>
													</div>
							       				</td>
							       			</tr>
							       			<tr align="left" valign="center"> 
			             						<td height="28" align="center" border="0">
													<%=urlStatus %>	                    			
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
			
            		<div align=center>
            			<input type=button value="关闭窗口" onclick="window.close()">
            		</div>  
					<br>
</body>         		 
</html>