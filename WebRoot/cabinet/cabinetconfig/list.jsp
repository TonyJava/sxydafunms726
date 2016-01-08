<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>

<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.cabinet.model.MachineCabinet"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	Hashtable roomhash = (Hashtable) request.getAttribute("roomhash");
	Object obj = request.getAttribute("isTreeView");
	JspPage jp = (JspPage) request.getAttribute("page");
	if (roomhash == null)
		roomhash = new Hashtable();
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>

		<script type="text/javascript">
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
  			var delAction = "<%=rootPath%>/maccabinet.do?action=delete";
  			var listAction = "<%=rootPath%>/maccabinet.do?action=list";
		</script>
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
		<script>
			function toAdd(){
				Ext.MessageBox.wait('数据加载中，请稍后.. '); 
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=ready_add"; 
				mainForm.submit();
			}
		</script>
		<script language="JavaScript">
			//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip,showItemMenu)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        return false;
			    }
			    else{
			    	popMenu(itemMenu,100,showItemMenu);
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    
			}
			/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
			function popMenu(menuDiv,width,rowControlString)
			{
			    //创建弹出菜单
			    var pop=window.createPopup();
			    //设置弹出菜单的内容
			    pop.document.body.innerHTML=menuDiv.innerHTML;
			    var rowObjs=pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
			    var rowCount=rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
			    for(var i=0;i<rowObjs.length;i++)
			    {
			        //如果设置该行不显示，则行数减一
			        var hide=rowControlString.charAt(i)!='1';
			        if(hide){
			            rowCount--;
			        }
			        //设置是否显示该行
			        rowObjs[i].style.display=(hide)?"none":"";
			        //设置鼠标滑入该行时的效果
			        rowObjs[i].cells[0].onmouseover=function()
			        {
			            this.style.background="#99CCFF";
			            this.style.color="white";
			        }
			        //设置鼠标滑出该行时的效果
			        rowObjs[i].cells[0].onmouseout=function(){
			            this.style.background="#F1F1F1";
			            this.style.color="black";
			        }
			    }
			    //屏蔽菜单的菜单
			    pop.document.oncontextmenu=function()
			    {
			            return false; 
			    }
			    //选择右键菜单的一项后，菜单隐藏
			    pop.document.onclick=function()
			    {
			        pop.hide();
			    }
			    //显示菜单
			    pop.show(event.clientX-1,event.clientY,width,rowCount*30,document.body);
			    return true;
			}
			
			<%
			if(null != obj && "1".equals((String)obj)){
			%>
			function toDetail(){
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=toDetail&id=" + node + "&isTreeView=1";
				mainForm.submit();
			}
			
			function edit(){
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=ready_edit&id=" + node + "&isTreeView=1";
				mainForm.submit();
			}
			<%}else{%>
			function toDetail(){
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=toDetail&id=" + node;
				mainForm.submit();
			}
			
			function edit(){
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=ready_edit&id=" + node;
				mainForm.submit();
			}
			<%}%>

			function toDelete(){
				mainForm.action = "<%=rootPath%>/maccabinet.do?action=delete";
				mainForm.submit();
			}
	
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1" style="border: thin; font-size: 12px" cellspacing="0">
				<tr>
					<td style="cursor: default; border: outset 1;" align="center" onclick="parent.edit()">编辑</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center" onclick="parent.toDetail()">查看详细信息</td>
				</tr>
			</table>
		</div>
		<!-- 右键菜单结束-->
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<%
						if (null == obj) {
					%>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td><%=menuTable%></td>
							</tr>
						</table>
					</td>
					<%
						} else {
							String isTreeView = (String) obj;
					%>
					<input type="hidden" name="isTreeView" value="<%=isTreeView%>">
					<%
						}
					%>
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
														<td class="content-title">3D机房 &gt;&gt; 机房管理管理 &gt;&gt; 机柜列表</td>
														<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td style="text-align: right" colspan="12" class="body-data-title">
															<a href="#" onclick="toAdd()">添加</a>&nbsp;&nbsp;&nbsp;
															<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
														</td>
													</tr>
													<tr>
														<td class="body-data-title" colspan="12">
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td width="80%" align="center">
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
														<td align="center" class="body-data-title"><INPUT type="checkbox" id="checkall" name="checkall"onclick="javascript:chkall()">序号</td>
														<td align="center" class="body-data-title">名称</td>
														<td align="center" class="body-data-title">所属机房</td>
														<td align="center" class="body-data-title">机柜坐标位置(x,y,z)</td>
														<td align="center" class="body-data-title">U总数</td>
														<td style="display:none" align="center" class="body-data-title">机架规格</td>
														<td style="display:none" align="center" class="body-data-title">电源个数</td>
														<td style="display:none" align="center" class="body-data-title">高度</td>
														<td style="display:none" align="center" class="body-data-title">宽度</td>
														<td style="display:none" align="center" class="body-data-title">深度</td>
														<td align="center" class="body-data-title">机柜编号</td>
														<td align="center" class="body-data-title">操作</td>
													</tr>
													<%
														String room = "";
														if (list != null && list.size() > 0) {
															for (int i = 0; i < list.size(); i++) {
																MachineCabinet machineCabinet = (MachineCabinet) list
																		.get(i);
																String motoroom = machineCabinet.getMotorroom();
																if (roomhash.containsKey(motoroom))
																	room = (String) roomhash.get(motoroom);

																String machine = "(" + machineCabinet.getMachinex() + ","
																		+ machineCabinet.getMachiney() + ","
																		+ machineCabinet.getMachinez() + ")";
													%>
													<tr <%=onmouseoverstyle%>>
														<td align="center" class="body-data-list"><INPUT type="checkbox" name="checkbox"value="<%=machineCabinet.getId()%>"><%=i + jp.getStartRow()%></td>
														<td align="center" class="body-data-list"><%=machineCabinet.getName()%></td>
														<td align="center" class="body-data-list"><%=room%></td>
														<td align="center" class="body-data-list"><%=machine%></td>
														<td align="center" class="body-data-list"><%=machineCabinet.getUselect()%></td>
														<td style="display:none" align="center" class="body-data-list"><%=machineCabinet.getStandards()%></td>
														<td style="display:none" align="center" class="body-data-list"><%=machineCabinet.getPowers()%></td>
														<td style="display:none" align="center" class="body-data-list"><%=machineCabinet.getHeights()%></td>
														<td style="display:none" align="center" class="body-data-list"><%=machineCabinet.getWidths()%></td>
														<td style="display:none" align="center" class="body-data-list"><%=machineCabinet.getDepths()%></td>
														<td align="center" class="body-data-list"><%=machineCabinet.getNos()%></td>
														<td align="center" class="body-data-list"><img src="<%=rootPath%>/resource/image/status.gif"border="0" width=15 oncontextmenu=showMenu('2','<%=machineCabinet.getId()%>','','1111') title="右键菜单"></td>
													</tr>
													<%
														}
														}
													%>
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
