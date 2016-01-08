<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.topology.model.ConnectTypeConfig"%>
<%@page import="com.afunms.config.model.Huaweitelnetconf"%>
<%@page import="com.afunms.config.model.Hua3VPNFileConfig"%>
<%@page import="com.afunms.config.model.VPNFileConfig"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.system.util.*"%>
<%@page import="com.afunms.config.model.ConfiguringDevice"%>


<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	List list = (List) request.getAttribute("list");
	// JspPage jp = (JspPage) request.getAttribute("page");
	String ip = (String) request.getAttribute("ip");
	String runImg = rootPath + "/img/Config-Running.gif";
	String startupImg = rootPath + "/img/Config-Startup.gif";
	String baselineImg = rootPath + "/img/Config-Baseline.gif";
	String type = (String) request.getAttribute("type");
	String baskfilename="";
	
%>


<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css"
			rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css"
			rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css"
			type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet"
			type="text/css">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript">
			var listAction = "<%=rootPath%>/vpntelnetconf.do?action=configlist";
	  		var delAction = "<%=rootPath%>/vpntelnetconf.do?action=delete";
		
		</script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
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
		<script language="JavaScript">

			//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        popMenu(itemMenu,100,"100");
			    }
			    else
			    {
			        popMenu(itemMenu,100,"11111");
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    return false;
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
			            this.style.background="#397DBD";
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
			    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
			    return true;
			}
			
			function edit()
			{
				//window.open("<%=rootPath%>/tool/ping.jsp?ipaddress="+ipaddress,"oneping", "height=400, width= 500, top=300, left=100");
				//window.open('/nms/netutil/ping.jsp?ipaddress='+ipaddress);
				mainForm.action="<%=rootPath%>/vpntelnetconf.do?action=ready_edit&id="+node;
				mainForm.submit();
			}
			
			function backup()
			{
				window.open("<%=rootPath%>/vpntelnetconf.do?action=readyBackupConfig&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 500, top=300, left=100,scrollbars=yes");
				//window.open('<%=rootPath%>/network.do?action=telnet&ipaddress='+ipaddress,'onetelnet', 'height=0, width= 0, top=0, left= 0');
				//window.open('/nms/netutil/tracerouter.jsp?ipaddress='+ipaddress);
			}	
			function setupcfg()
			{
				window.open("<%=rootPath%>/vpntelnetconf.do?action=readySetupConfig&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
				//window.open('<%=rootPath%>/network.do?action=telnet&ipaddress='+ipaddress,'onetelnet', 'height=0, width= 0, top=0, left= 0');
			}
			function telnetModify()
			{
				window.open("<%=rootPath%>/vpntelnetconf.do?action=readyTelnetModify&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
			function uploadCfgFile()
			{
				window.open("<%=rootPath%>/vpntelnetconf.do?action=readyuploadCfgFile&&id="+node+"&ipaddress="+ipaddress,"oneping", "height=400, width= 800, top=300, left=100,scrollbars=yes");
			}
		</script>
		<script type="text/javascript">
		
			function toAdd(){
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_add";
			    mainForm.submit();
			}
			function addForBatch()
			{
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_addForBatch";
				mainForm.submit();
			}
			function modifypwForBatch()
			{
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_multi_modify";
				mainForm.submit();
			}
			
			
			function modifyForBatch()
			{
				var check_obj = document.getElementsByName('checkbox');    
				var temp_check = false;    
				for(var i = 0; i < check_obj.length; i ++)
				{    
					if(check_obj[i].checked)
					{    
						temp_check = true;    
						break;
					}    
				}    
				if(temp_check == false)
				{    
					alert('至少勾选一项');    
					return false;    
				}
				//window.open("<%=rootPath%>/vpntelnetconf.do?action=ready_multi_modify","portScanWindow","width=800,height=400,scrollbars=yes,resizable=yes")
				
				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=ready_multi_modify";
				mainForm.submit();
			}
		
	  		
	  		function toDelete(){  
     				mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=deleteFile";
     				mainForm.submit();
	  		}
	  		function compareFile(){  
	  			

      		
	  			
	  			
     				var filename=$("input[name=baskcheckbox2][checked]").val();
     				//alert(filename);
     	           //var baskcheckbox=$("input[name=baskcheckbox][checked]").val();
     				//var filename='';
     				
     				
                  var checkboxs = document.getElementsByName("baskcheckbox");
					var num=0;
					var strs="";
					for(var i=0;i<checkboxs.length;i++){
						if(checkboxs[i].checked){
							num++;
							strs+=$(checkboxs[i]).val()+";";  

					 	}
					}
				 	//alert(strs);

	  
     			window.open("<%=rootPath%>/vpntelnetconf.do?action=compareFile&filename="+filename +"&baskcheckbox="+strs ,"_blank", "height=650, width=1240, top=20, left=10");
			
	  		}
	  		function toFindIp()
			{
			     mainForm.action = "<%=rootPath%>/vpntelnetconf.do?action=findip";
			     mainForm.submit();
			}
	  		function showFileContent(id)
			{
			    window.open("<%=rootPath%>/vpntelnetconf.do?action=showFileContent&id="+id,"onetelnet", "height=650, width=100%, top=20, left=120");
			}
			
	  		function setBaseLine(id)
			{
			 $.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=setBaseLine&id="+id+"&f="+Math.random(),
			success:function(data){
			
			if(data.result==0){
			 if(confirm("你确定要更改基线吗？")) 
              location.href="<%=rootPath%>/vpntelnetconf.do?action=editBaseLine&id="+id+"&ip=<%=ip%>"+"&type=<%=type%>"; 
             } else{ 
              alert(data.result); 
              document.getElementById(id).innerHTML="<font color='#33CC00'>基线文件</font>";
            } 
			
			}
			
		});
			}
	  		
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
				style="border: thin; font-size: 12px" cellspacing="0">

				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.backup();">
						备份
					</td>
				</tr>
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.setupcfg()">
						管理备份
					</td>
				</tr>

				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.uploadCfgFile()">
						上传备份
					</td>
				</tr>
			</table>
		</div>
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
															自动化 >> 配置文件管理 >> 定时备份列表
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
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td bgcolor="#ECECEC" width="60%">
																		&nbsp;&nbsp;
																		<!-- ip地址：<input type="text" size="30" name="ipfindaddress">
																		<input type="button" value="查询" onclick="toFindIp()">-->
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;
																		<input type="hidden" id="ip" name="ip"
																			value="<%=ip%>">
																		<input type="hidden" id="type" name="type"
																			value="<%=type%>">
																	</td>

																	<td bgcolor="#ECECEC" width="40%" align='right'>
																		<a href="#" onclick="compareFile()">配置文件比对</a>&nbsp;&nbsp;

																	</td>
																</tr>
															</table>
														</td>
													</tr>

													<tr>
														<td>
															<table cellspacing="0" border="1" bordercolor="#ababab">
																<tr height=28 style="background: #ECECEC" align="center"
																	class="content-title">
																	<td align="center">
																		<INPUT type="checkbox" id="checkall" name="checkall"
																			onclick="javascript:chkall()" class=noborder>
																	</td>
																	<td align="center">
																		序号
																	</td>
																	<td align="center" width="20%">
																		IP地址
																	</td>
																	<td align="center">
																		设备类型
																	</td>
																	<td align="center">
																		备份时间
																	</td>

																	<td align="center" >
																		备份类型
																	</td>
																	<td align="center" >
																		比对文件
																	</td>
																	<td align="center">
																		操作
																	</td>

																</tr>
																<%
																	Hua3VPNFileConfig vo = null;
																	//int startRow = jp.getStartRow();
																	if (list != null) {
																		String checked = "";
																		boolean flag=true;
																		for (int i = 0; i < list.size(); i++) {
																			
																			vo = (Hua3VPNFileConfig) list.get(i);
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align='center'>
																		<INPUT type="checkbox" class=noborder name="checkbox"
																			value="<%=vo.getId()%>">
																	</td>
																	<td align='center'>
																		<font color='blue'><%=i + 1%></font>
																	</td>
																	<td align='center'>
																		<%=vo.getIpaddress()%>
																	</td>
																	<td align='center'><%=type%></td>
																	<td align='center'><%=vo.getBackupTime()%></td>
																	<%
																		String bkpType = vo.getBkpType();
																				String bkpTypeName = "";
																				String bkpImg = "";
																				if (bkpType.equals("run")) {
																					bkpTypeName = "备份运行文件";
																					bkpImg = "<img src='" + runImg + "'>";
																				}
																				if (bkpType.equals("startup")) {
																					bkpTypeName = "备份启动文件";
																					bkpImg = "<img src='" + startupImg + "'>";
																				}
																				if (bkpType.equals("all")) {
																					bkpTypeName = "全部备份";
																				}
																				if (vo.getBaseline() == 1) {
																					bkpTypeName = "基线备份文件";
																					bkpImg = "<img src='" + baselineImg + "'>";

																				}
																	%>
																	<td align='center'><%=bkpImg%><%=bkpTypeName%></td>
																	<%
																		String baseline = "";
																				if (vo.getBaseline() == 1) {
																					
																				  
																					
																					baseline = "<span ><font color='#33CC00'>基线文件</font></span>";
																					
																	%>
																	<input type="hidden" name="Baskfilename" value="<%=vo.getFileName()%> "/>
																	<td align='center' >
																		
																	<input type="checkbox" class=noborder
																			value="<%=vo.getFileName()%>" id ="baskcheckbox2" name="baskcheckbox2" 
																			checked = "checked"; disabled="disabled" >	
																	</td>
																	<td align='center' id="<%=vo.getId()%>">
																	
																		<%=baseline%></td>
																	<%
																		} else {
																					baseline = "<span ><font color='blue'>设置基线</font></span>";
																					if(flag){
																					checked = "checked";
																					flag=false;
																					}else{
																					checked="";
																					}
																	%>
																	<td align='center'>
																	
																		<input type="checkbox" class=noborder
																			value="<%=vo.getFileName()%>" id ="baskcheckbox" name="baskcheckbox" 
																			<%=checked%>>
																		
																	</td>
																	<td align='center' id="<%=vo.getId()%>">
																		
																		<a href="#" onclick="setBaseLine('<%=vo.getId()%>')"><%=baseline%></a>
																	</td>
																	
																	<%
																		}
																	%>
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
	</body>
</html>
