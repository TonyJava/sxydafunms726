<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.application.course.model.LsfClassComprehensiveModel"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	//HashMap img_map = request.getAttribute("img_path_map");
	Hashtable roomhash = (Hashtable) request.getAttribute("roomhash");
	Object obj = request.getAttribute("isTreeView");
	JspPage jp = (JspPage) request.getAttribute("page");
	if (roomhash == null)
		roomhash = new Hashtable();
	
%>
<html>
	<head>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery.qtip-1.0.0-rc3.min.js"></script>
		<style>  
			.detailInfo{
				position: absolute;
				width:250px;
				height:142px;
				background: url('<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>/image/detailInfobg.gif');
				background-repeat:no-repeat;
				display:none;
				filter: progid:DXImageTransform.Microsoft.Shadow(color=gray,direction=135); 
				z-index:100;
			}
			.detailInfoHead{
				height: 30px;
				color: white;
			}
			.detailInfoHeadLeft{
				width: 36px;
				margin: 3px;
			}
			.selectCss{  
				width:115px;
				z-index: -1;
			}
			#zindexDiv{
				position:absolute;
				z-index:50;
				margin-left:33px;
				margin-top:2px;
				width:expression(this.nextSibling.offsetWidth-45);
				height:expression(this.nextSibling.offsetHeight-10);
				top:expression(this.nextSibling.offsetTop);
				left:expression(this.nextSibling.offsetLeft);
				background-color:transparent;
				display: none;
			}
		</style>
		<script type="text/javascript">
			$(document).ready(function(){
				//$("#testbtn").bind("click",function(){
				//	gzmajax();
				//});
			//setInterval(modifyIpAliasajax,60000);
			});
			
			//鼠标的坐标
			var x;
			var y;
			/**********************************
			获取鼠标的xy的值
			**********************************/
			if (navigator.appName == 'Netscape')
			 {
				document.captureEvents(Event.MOUSEMOVE);
				document.onmousemove = netscapeMouseMove;
			}
			
			function netscapeMouseMove(e) {
				if (e.screenX != x && e.screenY != y)
				 {
					x = e.screenX;
					y = e.screenY;
				} 
			}
			
			function micro$oftMouseMove() {
				if (window.event.x != x && window.event.y != y){
					x = window.event.x;
					y = window.event.y;
				}
			}
			
			/**********************************************************
			得到利用率的进度条形式的字符串
			参数:
				used：使用率
				unused:未使用率
				color:已使用情况的告警级别颜色
			**********************************************************/
			function getPercentTableStr(used, unused ,color){
				var tableStr = '<table><tr><td>'+used+'%</td><td width=80><table height=15 width=\"100%\" border=1 bgcolor=#ffffff><tr><td width='+used+'% bgcolor='+color
					+'></td><td width='+unused+'% bgcolor=#ffffff ></td></tr></table></td><td>&nbsp;</td></table>';
				return tableStr;						                      					
			}
			
			/**********************************************************
			鼠标移动到ip地址上之后，显示设备的详细信息漂浮窗口
			参数：
				设备类别和设备ID组成的字符串  如net10  表示id为10的网络设备
			***********************************************************/
			function showDetailInfo(typeAndId,Class_id,Class_name,Ip_address,Master,Alarm,Jid){ 
				var htmlTable = '资料正在加载中，请稍侯';
				var lay = document.getElementById('deviceDetailInfo');
				var detailType_flag = document.getElementById('detailTypeflag_net'+typeAndId);
				var tt = getoffset(detailType_flag);
	           	var x = tt[1];
	           	var y = tt[0];
	           	lay.style.left = x+10;
	           	lay.style.top = y+10;
	           	htmlTable = '<div><table cellspacing=\"2\" cellpadding=\"0\"><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td colspan=\'2\' class=\'detailInfoHead\'>'+Class_name+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>IP地址：</td><td>'
								+Ip_address+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>集群名称：</td><td>'
								+Class_name+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>Alarm：</td><td>'+Alarm+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>Master：</td><td>'
								+Master+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>集群ID：</td><td>'+Class_id+'</td></tr><tr><td class=\'detailInfoHeadLeft\'>&nbsp;</td><td>JID：</td><td>'+Jid+'</td></tr></table></div>';
	           	lay.innerHTML = htmlTable;
				lay.style.display = "block";           	
				var zindexDiv = document.getElementById('zindexDiv');
				zindexDiv.style.display = "block";
			}
			
			/**********************************************************
			鼠标移动到ip地址上之后，隐藏设备的详细信息漂浮窗口
			***********************************************************/
			function unShowDetailInfo(){
				var lay = document.getElementById('deviceDetailInfo');
				var zindexDiv = document.getElementById('zindexDiv');
				lay.style.display = "none";
				zindexDiv.style.display = "none";
			}
			
			/**********************************************************
			得到元素的偏移量，即坐标
			**********************************************************/
			function getoffset(e){  
				var rec = new Array(1); 
				//rec[0]  = y - 40; 
				//rec[1] = x;
				var x = e.offsetLeft;
				var y = e.offsetTop;   
   				while(e = e.offsetParent){ 
       				x += e.offsetLeft;   
       				y += e.offsetTop; 
    			} 
    			rec[0]  = y - 40; 
				rec[1] = x + 60;
				return rec;
			} 
			
			/*******************************************************
			鼠标点击 ,隐藏提示信息div
			******************************************************/
			document.onclick = function(){ 
				unShowDetailInfo();
			} 
			
		</script>
	</head>
	<!-- onMousemove="micro$oftMouseMove()" -->
	<body id="body" class="body"  leftmargin="0" topmargin="0" >
		<!-- 鼠标移动到IP地址上之后，设备详细 -->
		<iframe id="zindexDiv" frameborder="0" ></iframe>
		<div id="deviceDetailInfo" class="detailInfo"></div>
		<div id="deviceDetailInfoTest" class="detailInfo"></div>
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container" height="100%">
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
									                	<td class="content-title">&nbsp;LSF管理 &gt;&gt; LSF集群监控 &gt;&gt; 集群列表 </td>
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
		        												<%
					        									    if(list!=null&& list.size()>0){
																        for(int i = 0 ; i < list.size() ; i++){
																        	String gifimig="";
																	        LsfClassComprehensiveModel model =(LsfClassComprehensiveModel)list.get(i);
																	       	System.out.println("Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
																        	if(model.getMaster().equals("1")&& model.getAlarm().equals("1")){
																        		System.out.println("1111____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());	
																				gifimig="a_level_3.gif";
																				System.out.println("111____gifimig:"+gifimig);	
																			}
																			//没有告警
																			if(model.getMaster().equals("1")&& model.getAlarm().equals("0")){
																				System.out.println("2222____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
																				gifimig="a_level_0.gif";
																				System.out.println("222____gifimig:"+gifimig);	
																			}
																		 	//普通节点有告警
																			if(model.getMaster().equals("0")&& model.getAlarm().equals("1")){
																				System.out.println("3333____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
																				gifimig="a_level_1.gif";
																				System.out.println("333____gifimig:"+gifimig);	
																			}
																			// 没有告警 
																			if(model.getMaster().equals("0")&& model.getAlarm().equals("0")){
																				System.out.println("4444____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
																				gifimig="a_level_0.gif";
																				System.out.println("444____gifimig:"+gifimig);
																			}
					        									            %>
					        									            <tr>
					        													<td align="left" class="body-data-list">
					        															
					        															<span id='detailType_net<%=model.getClass_id()%>' onmouseover="showDetailInfo('<%=model.getNodeid()%>','<%=model.getClass_id()%>','<%=model.getClass_name()%>','<%=model.getIp_address()%>','<%=model.getMaster()%>','<%=model.getAlarm()%>','<%=model.getJid()%>');" onmouseout="unShowDetailInfo();">
					        																<img src="<%=rootPath%>/resource/image/topo/<%=gifimig%>"/>&nbsp;
					        																<%System.out.println("-----------"+"ImagePath:"+rootPath+"/application/course/dtree/img/"+gifimig); %>
					        															</span>
					        															<%if(model.getMaster().equals("1")){
					        																System.out.println(rootPath+"/application/course/dtree/img/host_black.gif");
																						 %>
					        																<img src="<%=rootPath%>/application/course/dtree/img/host_black.gif"/>&nbsp;
					        															<%} %>
					        															<%System.out.println(model.getNodeid()); %>
					        															<span id='detailTypeflag_net<%=model.getNodeid()%>'></span><%-- 提示信息坐标的标志位置--%>
					        													</td>
					        													<%if(model.getMaster().equals("1")){
					        													 %>
					        													<td align="center" class="body-data-list"><%=model.getClass_name()%></td>
					        													<%} else{%>
					        													<td align="center" class="body-data-list"><%=model.getClass_name()%></td>
					        													<%} %>

					        													<!-- <td align="center" class="body-data-list"><%=model.getMaster()%></td> <td align="center" class="body-data-list" style="font-size:12   px;color:#FF78fd">Master/Yes</td>-->
					        													<%if(model.getMaster().equals("1")){
					        													 %>
					        													<td align="center" class="body-data-list">Master/Yes</td>
					        													<%} else{%>
					        													<td align="center" class="body-data-list">Master/No</td>
					        													<%} %>

					        													<%if(model.getJid().equals("1")){
					        													 %>
					        													<td align="center" class="body-data-list">Jid/Yes</td>
					        													<%} else{%>
					        													<td align="center" class="body-data-list">Jid/No</td>
					        													<%} %>
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
