<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.cabinet.model.CabinetEquipment"%>
<%@page import="com.afunms.cabinet.model.MachineCabinet"%>
<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  List list2 = (List)request.getAttribute("list2");
  Hashtable cabinethash = (Hashtable)request.getAttribute("cabinethash");
  Hashtable roomhash = (Hashtable)request.getAttribute("roomhash");
   String room_id = (String)request.getAttribute("room_id");
  String cabinet_id = (String)request.getAttribute("cabinet_id");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  Object obj = request.getAttribute("isTreeView");
  String menuTable = (String)request.getAttribute("menuTable");
  
  System.out.println("=====menuTable===="+menuTable);
  %>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/cabinetequipment.do?action=delete";
  var listAction = "<%=rootPath%>/cabinetequipment.do?action=list";
  
  function doQuery()
  {  
     mainForm.action = "<%=rootPath%>/cabinetequipment.do?action=find";
     mainForm.submit();
  }
  function doFromlastoconfig()
  {  
     mainForm.action = "<%=rootPath%>/cabinetequipment.do?action=fromlasttoconfig";
     mainForm.submit();
  }  
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/cabinetequipment.do?action=ready_add";
      mainForm.submit();
  }
  function editNodePort1(d){alert(d);}
  
function editNodePort(id,sms)
  {
 
      $.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=editnodeport&id="+id+"&sms="+sms+"&nowtime="+(new Date()),
			success:function(data){
			
			if(data.flagStr==3){
			alert("修改失败！！！");
			}else if(data.flagStr==0){
			var smsId =document.getElementById("smsFlag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePort("+id+",1)'><font color=#skyblue>监视</font></span>";
			}else if(data.flagStr==1){
			var smsId =document.getElementById("smsFlag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePort("+id+",0)'><font color=#green>取消监视</font></span>";
			}
			}
		});
  }
  function editNodePortForReport(id,reportFlag)
  {
  
      $.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=editnodeport&id="+id+"&reportflag="+reportFlag+"&nowtime="+(new Date()),
			success:function(data){
		
			if(data.flagStr==3){
			alert("修改失败！！！");
			}else if(data.flagStr==0){
			var smsId =document.getElementById("reportflag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePortForReport("+id+",1)'><font color=#skyblue>显示于报表</font></span>";
			}else if(data.flagStr==1){
			var smsId =document.getElementById("reportflag"+id);
		    smsId.innerHTML ="<span style='cursor:hand' onclick='editNodePortForReport("+id+",0)'><font color=#green>取消显示于报表</font></span>";
			}
			}
		});
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

//修改警告级别
function modifyalarmlevelajax(id){
	var t = document.getElementById("alarmlevel"+id);
	var alarmvalue = t.selectedIndex;//获取下拉框的值
	$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=modifyAlarmlevel&id="+id+"&alarmvalue="+alarmvalue+"&nowtime="+(new Date()),
			success:function(data){
				window.alert("修改成功！");
			}
		});
}

var http_request=false;

 function send_request(url){
 
     http_request=false;
     
     if(window.XMLHttpRequest){
         http_request=new XMLHttpRequest();
         
         if(http_request.overrideMimeType){
             http_request.overrideMimeType("text/xml");
         }
     }
     else if(window.ActiveXObject){
         try{
             http_request=new ActiveXObject("Msxml2.XMLHTTP");
         }
         catch(e){
             try{
                 http_request=new ActiveXObject("Microsoft.XMLHTTP");
             }
             catch(e){}
         }
     }
    
     if(!http_request){
         window.alert("不能创建XMLHttpRequest对象实例!");
         return false;
     }
     
     http_request.onreadystatechange=processRequest;
     http_request.open("GET",url,true);
     http_request.send(null);
 }

 function processRequest(){
     if(http_request.readyState==4){
         if(http_request.status==200){
           // document.getElementById("cabinet").innerHTML=http_request.responseText;
           $("#cabinet").html(http_request.responseText);
         }
         else{
             alert("您所请求的页面有异常!错误状态:"+http_request.status);
         }
     }
 }

 function select(){
     var roomid=document.getElementById("eqproom").value;
     send_request("maccabinet.do?action=toSelect&roomid="+roomid+"&r="+Math.random());
 }

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"  onload="initmenu();">
<form method="post" name="mainForm">
<table id="body-container" class="body-container">
	<tr>
				<%
					if(null == obj){
				%>
					<td class="td-container-menu-bar">
						<table id="container-menu-bar" class="container-menu-bar">
							<tr>
								<td>
									<%=menuTable%>
								</td>
							</tr>
						</table>
					</td>
				<%
					}else{
						String isTreeView = (String)obj;
				%>	
				<input type="hidden" name="isTreeView" value="<%=isTreeView %>">
				<%
					}
				%>	
	
		<td align="center" valign=top>
			<table cellpadding="0" cellspacing="0" algin="center" style="width:98%">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title"><b>3D机房 >> 机房配置管理 >> 机柜设备配置</b></td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>				
				<tr>           
								<td>
								<table width="100%" cellpadding="0" cellspacing="1" >
								<tr>
								<td>
								<table width="100%" cellpadding="0" cellspacing="0" >
								<tr>
								<td bgcolor="#ECECEC" width="50%" align='left'>
									&nbsp;&nbsp;&nbsp;&nbsp;<B>查询:</B>所属机房
        									<SELECT name="eqproom" id="eqproom" style="width=100" onChange="select()">
        								<OPTION value="-1">--全部--</OPTION>
        									<%
        									if(roomhash != null && roomhash.size()>0){
        										Set set = roomhash.keySet();
        											for(Iterator iter = set.iterator(); iter.hasNext();)
														{
															Integer roomid = (Integer)iter.next();
															String roomname = (String)roomhash.get(roomid);
        									%> 
          									<OPTION value="<%=roomid%>" <%if(room_id!=null&&room_id.equals(roomid.toString())){%> selected="selected"<%}%>><%=roomname%></OPTION>
          									<%
          											    }
          										   }
          									%>        
          									
          								</SELECT>&nbsp;
          								所属机柜
        								<SELECT name="cabinet" id="cabinet" style="width=100">
        								 	<%
        								 		if(cabinet_id==null){
        								 	 %>
        									  <OPTION value="-1">--全部--</OPTION>
        									  <%}else{ %>
        									  <OPTION value="-1">--全部--</OPTION>
											<% 
													for(int i=0;i<list2.size();i++){
													
											%>	
											<OPTION value="<%=((MachineCabinet)list2.get(i)).getId()%>" <%if(cabinet_id.equals(((MachineCabinet)list2.get(i)).getId()+"")){%> selected="selected"<%}%>><%=((MachineCabinet)list2.get(i)).getName()%></OPTION>
											<%
											}
        									  }
        									   %>
          								</SELECT>&nbsp;
          								<INPUT type="button" class="formStyle" value="查询" onclick=" return doQuery()">
          							</td> 
          								<td bgcolor="#ECECEC" width="50%" align='right'>
															<a href="<%=rootPath%>/network.do?action=downloadnetworklistfuck" target="_blank"><img name="selDay1" alt='导出EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">导出EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
															<a href="#" onclick="toAdd()">添加</a>
															<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
							<table width="100%" cellpadding="0" cellspacing="1" >
							<tr>
						    <td bgcolor="#ECECEC" width="80%" align="center">
    <jsp:include page="../../common/page.jsp">
     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
   </jsp:include>
           </td>
           </tr>
		   </table>
		   </td>
		   </tr>
		      				<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
	  									<tr class="microsoftLook0">
	  											<th align="center" class="body-data-title">
												<INPUT type="checkbox" class=noborder name="checkall"
													onclick="javascript:chkall()">
												</th>
      											<th width='5%'>序号</th>				
      											<th width='10%'>所属机房</th>
      											<th width='10%'>所属机柜</th>
      											<th width='10%'>U占用</th>
      											<th width='15%'>设备名称</th>   
      											<th width='15%'>设备描述</th>
      											<th style="display:none" width='10%'>所属业务</th>
      											
      											<th width='10%'>联系人</th>
      											<th width='10%'>联系电话</th>    											
      											<th width='5%'>操作</th>
      											
										</tr>
<%
    CabinetEquipment vo = null;
    int startRow = jp.getStartRow();
    for(int i=0;i<rc;i++)
    {
       vo = (CabinetEquipment)list.get(i);  
       String cabinetname = "";
       if(cabinethash.containsKey(vo.getRoomid()+":"+vo.getCabinetid()))  cabinetname = (String) cabinethash.get(vo.getRoomid()+":"+vo.getCabinetid());  
       String roomname = "";
       if(roomhash.containsKey(vo.getRoomid()))  roomname = (String) roomhash.get(vo.getRoomid()); 
%>
   										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>  
   												<td align="center" class="body-data-list">
												<INPUT type="checkbox" class=noborder name=checkbox
													value="<%=vo.getId()%>">
												</td> 
    											<td >
    												<font color='blue'>&nbsp;<%=startRow + i%></font></td>
    											<td  align='center'height=23><%=roomname%></td>
    											<td  align='center' height=23><%=cabinetname%></td>
    											<td  align='center' height=23><%=vo.getUnmubers()%></td>
    											<td  align='center' height=23><%=vo.getNodename()%></td>
    											<td  align='center' height=23><%=vo.getNodedescr()%></td>
    											<td  style="display:none" align='center' height=23><%=vo.getBusinessName()%></td>   											
    											
    											<td  align='center' height=23><%=vo.getContactname()%></td>
    										    <td  align='center' height=23><%=vo.getContactphone()%></td>											
												<td  align='center' height=23>
        										<a
													href="<%=rootPath%>/cabinetequipment.do?action=showedit&id=<%=vo.getId()%>">
													<img src="<%=rootPath%>/resource/image/editicon.gif" border="0" />
												</a></td>
        										
  										</tr>			
<% }%>
									</table>
								</td>
							</tr>	
						<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="11" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="11" /></td>
                  </tr>
              </table></td>
            </tr>	
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>
