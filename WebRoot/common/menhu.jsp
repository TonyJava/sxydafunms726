<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="org.apache.commons.collections.functors.WhileClosure"%>
<%@page import="org.apache.commons.lang.SystemUtils"%>
<%@page import="org.apache.commons.net.DefaultDatagramSocketFactory"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="wfm.encode.MD5"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.afunms.system.dao.UserDao"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
    String rootPath = request.getContextPath();
    MD5 md = new MD5();
	String pwd = md.getMD5ofStr("admin");
	UserDao dao = new UserDao();
	User vo = null;
	try {
	    vo = dao.findByLogin("admin", pwd);
	} catch (Exception e) {
        e.printStackTrace();
	} finally {
	    dao.close();
	}

	session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
	session.setMaxInactiveInterval(1800);
%>   
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/include/swfobject.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/xml/flush/amcolumn/swfobject.js"></script>
		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<!-- GC -->
		<!-- LIBS -->
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<!-- ENDLIBS -->
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/ProgressBarPager.js"></script>
   	 	<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/PanelResizer.js"></script>
    	<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/examples/ux/PagingMemoryProxy.js"></script>
		<!-- EXT做的重点资源标签页home.js  css样式修改 -->
		<style type="text/css">
body {
	background: url(images/bg.jpg)
}

.x-tab-strip-top .x-tab-right {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-strip-top .x-tab-left {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-strip-top .x-tab-strip-inner {
	background-image: url("<%=rootPath%>/img/ext/tabs/tabs-sprite.gif");
}

.x-tab-panel-body {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-tab-panel-header {
	background-color: #EAEAEA;
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-tab-panel-header-plain .x-tab-strip-spacer {
	background-color: #EAEAEA;
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-panel {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

.x-panel-body {
	border-bottom-color: #EAEAEA;
	border-left-color: #EAEAEA;
	border-right-color: #EAEAEA;
	border-top-color: #EAEAEA;
}

UL.x-tab-strip-top {
	background-color: #EAEAEA;
}
</style>
		<script type="text/javascript" src="<%=rootPath%>/js/home.js"></script>
		<script type="text/javascript">
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

		<script type="text/javascript">
		
		function hideMenu(){
			var element = document.getElementById("container-menu-bar").parentElement;
			var display = element.style.display;
			if(display == "inline"){
				hideMenuBar();
			}else{
				showMenuBar();
			}
			//刷新tabpanel的宽度
			var tr_width = Ext.get("keybusiness_tr").getWidth()-1;
			Ext.get('tab_list_tr').setWidth(tr_width);
			Ext.get('tab_list').setWidth(tr_width);
			Ext.get('devicexn').setWidth(tr_width);
		}
		
		function showMenuBar(){
			var element = document.getElementById("container-menu-bar").parentElement;
			element.style.display = "inline";
		}
		
		function hideMenuBar(){
			var element = document.getElementById("container-menu-bar").parentElement;
			element.style.display = "none";
		}
	</script>

	</head>
	<body id="body" class="body" onLoad="parent.topFrame.location.reload();initmenu();hideMenuBar();">

		<!-- 定义一个空div -->
		<span id="rootpath" value="<%=rootPath%>"></span>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">

				<tr>
					<td class="td-container-main">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content"
										class="container-main-content" style="width: 99%" border="0">
										<tr>
											<td>
												<table id="content-body" class="content-body"
													style="border-left: #737272 0px solid; border-right: #737272 0px solid;">
													<tr>
														<td>
															<table>
																<tr>
																	<!-- 设备性能 -->
																	<td width=50% align='center' height="270">
																		<table width=100% height="100%" border="0"
																			align='center'>
																			<tr>
																				<td id='devicexn_title' align='center' height="24">
																					<table id="content-header" class="content-header">
																						<tr>
																							<td align='center' height="29"
																								class="content-title"
																								style="text-align: center;">
																								<b>性能</b>
																							</td>
																						<tr>
																					</table>
																				</td>
																			</tr>
																			<tr id="tab_list_tr">
																				<td>
																					<!-- EXT的做的TAB 详见home.js文件 -->
																					<!-- 加重点资源Tab页 -->
																					<div id="tab_list" style="width: 100%;"></div>
																				</td>
																			</tr>
																			<tr valign="bottom">
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
		<script type="text/javascript">
			Ext.onReady(tabpanel_var('<%=rootPath%>'));
			
			//构建一个JavaScript的replaceAll方法  （该方法个别客户机浏览器不支持）
			//String.prototype.replaceAll  = function(s1,s2){    
			//  	return this.replace(new RegExp(s1,"gm"),s2);   
			//} 
			
			/**
			*跳转到性能页面
			*/
			function showTree(rightFramePath){
				//将等于和and转换一下  
				//rightFramePath = rightFramePath.replaceAll("&","-and-");
				//rightFramePath = rightFramePath.replaceAll("=","-equals-");
				//使用循环，将等于和and转换一下  
				while(rightFramePath.indexOf("&") != -1){
					rightFramePath = rightFramePath.replace("&","-and-");
				}
				while(rightFramePath.indexOf("=") != -1){
					rightFramePath = rightFramePath.replace("=","-equals-");
				}
				//alert(rightFramePath);
				window.location.href =  "<%=rootPath%>/performance/index.jsp?flag=1&rightFramePath="+rightFramePath;
			}
		</script>
	</body>
</html>
