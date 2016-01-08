<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.alarm.model.AlarmIndicators"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%
  	String rootPath = request.getContextPath();
    String ip = (String)request.getAttribute("ip");
    	String menuTable = (String)request.getAttribute("menuTable");
	String thevalue = (String)request.getAttribute("thevalue");
	String nodeid = (String)request.getAttribute("nodeid");
	String alias = (String)request.getAttribute("Alias");
	String Ismain=(String) request.getAttribute("Ismain");
	System.out.println("===================Ismain=="+Ismain);
	int flag = (Integer)request.getAttribute("flag");
%>
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





<script language="JavaScript" type="text/javascript">


 function toEdit()
  {
     //var chk = checkinput("linkuse","string","端口应用",50,false);

     
     //if(chk)
     //{
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/processMain.do?action=save";
        	mainForm.submit();
        	window.opener.location.reload();
        	window.close();
        }
     //}
  }

Ext.onReady(function(){  
	setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
Ext.get("process").on("click",function(){
  			var chk = checkinput("nodeid","string","设备ID",200,false);
  			var chk = checkinput("mon_flag","string","是否主要进程",200,false);
  			if(chk){
  				 Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/processMain.do?action=save";
        	mainForm.submit();
  			}
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
<body id="body" class="body" onload="initmenu();initValue();">
	<form id="mainForm" method="post" name="mainForm">
		<input type="hidden" id="nodeid" name="nodeid" value="<%=nodeid %>">
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
											                	<td class="add-content-title"> 应用 &gt;&gt; 主要进程管理 &gt;&gt; 配置 </td>
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
				        												<table>
				        													<tr>
				        														<td>
				        															<table>
																						<tr>						
																							<td align="right" height="24" width="10%">ip 地址:&nbsp;</td>				
																							<td width="40%">&nbsp;
																							<input type="text" id="ip" readonly="readonly" value="<%=ip%>" name="ip">
																							</td>
																						</tr>	
																						<!-- 											
																						<tr>						
																							<td align="right" height="24" width="10%">设备名称:&nbsp;</td>				
																							<td width="40%">&nbsp;
																							
																							<input type="text" id="alias" readonly="readonly" value="<%=alias%>" name="alias">
																							
																							</td>
																						</tr>
																						
																						 -->		
																						<tr>						
																							<td align="right" height="24" width="10%">进程名称:&nbsp;</td>				
																							<td width="40%">&nbsp;
																							<input type="text" id="proname" readonly="readonly" value="<%=thevalue%>" name="proname">
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">是否主进程:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="mon_flag" name="mon_flag">
																							
																							 		<option value="1"  <%
																							 		
																							 		  if(Ismain.equals("1"))out.print("selected");
																							 		 %>>是</option>
																									<option value="0" <%if(Ismain.equals("0"))out.print("selected");%>>否</option>
																							
																							
																							
																								</select>
																							</td>
																						</tr>
																						
																						<tr>
																							<td colspan=11 align=center><br>
							  	                                                            <input type=reset class="formStylebutton" style="width:50" value="修 改" onclick="toEdit()">&nbsp;&nbsp; 
								                                                            <input type="reset" style="width: 50" value="返回"
																						onclick="javascript:history.back(1)"> 
								                                                                 <br>             					
                  					                                                            </td>
																						</tr>
													                        	</TABLE>		
			        														</td>
			        													<tr>
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
</BODY>

<script>

function unionSelect(){
	var type = document.getElementById("type");
	var nameFont = document.getElementById("nameFont");
	var db_nameTD = document.getElementById("db_nameTD");
	var db_nameInput = document.getElementById("db_nameInput");
	var category = document.getElementById("category");
	var port  = document.getElementById("port");
	if(type.value == 2){
		nameFont.style.display="inline";
		db_nameTD.style.display="none";
		db_nameInput.style.display="none";
	}else{
		nameFont.style.display="none";
		db_nameTD.style.display="inline";
		db_nameInput.style.display="inline";
		
	}
	var categoryvalue = "";
	var portvalue = "";
	if(type.value == 1){
		categoryvalue = 53;
		portvalue = 1521;
	}else if(type.value == 2){
		categoryvalue = 54;
		portvalue = 1433;
	}else if(type.value == 4){
		categoryvalue = 52;
		portvalue = 3306;
	}else if(type.value == 5){
		categoryvalue = 59;
		portvalue = 50000;
	}else if(type.value == 6){
		categoryvalue = 55;
		portvalue = 2638;
	}else if(type.value == 7){
		categoryvalue = 60;
		portvalue = 9088;
	}
	port.value = portvalue;
	category.value = categoryvalue;
}

unionSelect();

</script>

</HTML>