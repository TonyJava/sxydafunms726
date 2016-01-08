<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.config.model.Supper"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="java.util.List"%>
<%
   MediaPlayer vo = (MediaPlayer)request.getAttribute("vo");
   String rootPath = request.getContextPath();
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->


<script language="JavaScript">
	 Ext.onReady(function()
	{ 
		Ext.get("process").on("click",function(){
			Ext.MessageBox.wait('数据加载中，请稍后.. ');
    		mainForm.action = "<%=rootPath%>/mediaPlayer.do?action=update";
    		mainForm.submit();
 		});
	});
	function CreateWindow(url)
	{
		msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}
	
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
	        popMenu(itemMenu,100,"1111");
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
	<script type="text/javascript">
		function showup(){
			CreateWindow("<%=rootPath%>/config/vpntelnet/mediaPlayer/up.jsp");
		}
	</script>
</head>
<body id="body" class="body" onload="initmenu();">
	<form name="mainForm" method="post">
		<input type=hidden name="id" value="<%=vo.getId()%>">
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
						<td class="td-container-main-add">
							<table id="container-main-add" class="container-main-add">
								<tr>
									<td>
										<table id="add-content" class="add-content">
											<tr>
												<td>
													<table id="add-content-header" class="add-content-header">
									                	<tr>
										                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
										                	<td class="add-content-title">自动化 >> 自动化控制 >> 执行演示视频管理 >> 修改视频</td>
										                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
										       			</tr>
										        	</table>
			        							</td>
			        						</tr>
			        						<tr>
			        							<td>
			        								<table id="detail-content-body" class="detail-content-body">
			        									<tr>
			        										<td>
			        											<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																	<tr>
																		<td nowrap align="right" height="24">
																			文件名&nbsp;
																		</td>
																		<td>
																			&nbsp;<input type="text" name="name" size="40" value="<%=vo.getName() %>">
																			<font color="red">&nbsp;*</font>
																		</td>
																	</tr>
																	<tr style="background-color: #ECECEC;">
																		<td nowrap align="right" height="24">
																			所属业务系统&nbsp;
																		</td>
																		<td>
																			&nbsp;<select name="bsid">
																				<option value="-1" >----请选择----</option>
																				<%
																					BusinessSystemDao bd = new BusinessSystemDao();
																					List list = bd.loadAll();
																					for(int i = 0; i<list.size();i++){
																					BusinessSystem busi = (BusinessSystem)list.get(i);
																				 %>
																				 	<option value="<%=busi.getId() %>" <%if(vo.getBsid() == busi.getId()){ %>selected<%} %>><%=busi.getName() %></option>
																				 <%
																				 	}
																				  %>
																			</select>
																			<font color="red">&nbsp;*</font>
																		</td>
																	</tr>
																	<tr >
																		<td nowrap align="right" height="24" width="10%">
																			附件&nbsp;
																		</td>
																		<td nowrap width="40%">
																		&nbsp;
																			<input type="text" id="aaa" size="20" readonly="readonly" value="<%=vo.getFileName() %>" name="fileName"><input type="button" value="添加附件" onclick="showup()"><font color="red">&nbsp;*</font>
																		</td>
																	</tr>
																	<tr style="background-color: #ECECEC;">
																		<td nowrap align="right" height="24">
																			备注信息&nbsp;
																		</td>
																		<td>
																			&nbsp;<input type="text" name="desc" value="<%=vo.getDesc() %>" size="80">
																		</td>
																	</tr>
																	<tr>
																		<td nowrap colspan="4" align=center>
																			<br><input type="button" value="修改" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																			<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
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
			        								<table id="detail-content-footer" class="detail-content-footer">
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
								<tr>
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
</form>
</body>
</HTML>