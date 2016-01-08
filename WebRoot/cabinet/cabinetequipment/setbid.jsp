<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.cabinet.model.*"%>
<%@page import="com.afunms.config.model.Business"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<% 
	String rootPath = request.getContextPath(); 
	List allbusiness = (List)request.getAttribute("allbusiness"); 
	List bidIsSelected = (List)request.getAttribute("bidIsSelected");
	String eventText = (String)request.getAttribute("eventText");
	String event = (String)request.getAttribute("event");
	List roomlist = (List)request.getAttribute("roomlist"); 
	Hashtable roomhash = (Hashtable)request.getAttribute("roomhash"); 

%>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>选择机柜窗口</title>

<link type="text/css" href="<%=rootPath%>/config/business/businessTree.css" rel="stylesheet"/>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/cabinet/cabinetequipment/checkboxTree.js"></script>

<script>
	var tree;
	var dataArray = new Array();
	var intnum = 0;
		dataArray[0] = ['1' ,'所有机房',0,  false,'10'];
		<%
			if(roomlist != null && roomlist.size()>0){
				for(int i=0;i<roomlist.size();i++){
					EqpRoom room = (EqpRoom)roomlist.get(i);
					%>
					intnum = intnum + 1;
					dataArray[intnum] = ['<%=room.getId()+10000%>' ,'<%=room.getName()%>',1,  false,'<%=room.getId()%>'];
					<%
					if(roomhash.containsKey(room.getId()+"")){
						List cabinetlists = (List)roomhash.get(room.getId()+"");
						if(cabinetlists != null && cabinetlists.size()>0){
						//添加机房
							for(int k=0;k<cabinetlists.size();k++){
								MachineCabinet machineCabinet = (MachineCabinet)cabinetlists.get(k);
								%>
								intnum = intnum + 1;
								dataArray[intnum] = ['<%=machineCabinet.getId()%>' ,'<%=machineCabinet.getName()%>',<%=room.getId()+10000%>,  false,'<%=machineCabinet.getMotorroom()%>'];
								
								<%
							}
						}
					}
				}
				
			}
		%>
		

	
	function showTree(){
		tree = new Tree();
		tree.init(dataArray);
		tree.show("businessTree");
	}

	function allChoose(){
		tree.allChoose();
	}

	function allNotChoose(){
		tree.allNotChoose();
	}
	
	function antiChoose(){
		tree.antiChoose();
	}

	function confirm(){
		var isSelectedNodeArray = tree.getIsSelectedNode(true);
		var eventTextvalue = '';
		var eventValue = '';
		var roomidValue = '';
		if(isSelectedNodeArray){
			for(var i = 0 ; i< isSelectedNodeArray.length ; i++){
				var pernode = isSelectedNodeArray[i];
				eventValue = pernode.id;
				eventTextvalue =  pernode.name;
				roomidValue = pernode.cabinetid;
				break;
			}
		}
		var cabinetname = parent.opener.document.getElementById("cabinetname");
		var cabinetid = parent.opener.document.getElementById("cabinetid");
		var roomid = parent.opener.document.getElementById("roomid");
 		cabinetname.value=eventTextvalue; 
 		cabinetid.value = eventValue;   
 		roomid.value =  roomidValue;
		window.close();
	}
	
	function cancel(){
		window.close();
	}
	
	
	function reset(){
		tree.reset();
	}
</script>
<%
	String imgpath = com.afunms.common.util.CommonAppUtil.getSkinPath();
	if(imgpath == null){
		imgpath="images";
	} else {
	  if( "null".equals(imgpath) || "".equals(imgpath))imgpath="images";
	 }
	 //System.out.println(imgpath);
%>
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/<%=imgpath %>/menubg_report.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
</head>
<body onload="showTree()" style="margin:0px">
<!--	<div id="div"><input type="button" value="showTree" onclick="show('businessTree')"></div>-->
	<div>
		<div style="margin-bottom: 15px">
		<br>
			<input type="button" value="确  定" onclick="confirm()">
			<input type="button" value="关  闭" onclick="cancel()">
			<input type="button" value="重  置" onclick="reset()">
		</div>
		<div id="businessTree"  class="businessTree" style="background-color: white; width: 575px; height: 330px; margin-left: 2px;margin-bottom: 0px;TEXT-ALIGN: left;"></div>
		
	</div>
</body>
</html>