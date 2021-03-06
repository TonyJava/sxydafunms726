<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="java.io.File"%>
<%@page import="com.afunms.topology.util.ReadFile"%>
<%@page import="com.afunms.config.dao.BusinessDao"%>
<%@page import="com.afunms.config.model.Business"%>
<%  
   String rootPath = request.getContextPath();  
   String objEntityStr = (String)request.getAttribute("objEntityStr");
   String linkStr = (String)request.getAttribute("linkStr");
   String asslinkStr = (String)request.getAttribute("asslinkStr");
   BusinessDao bussdao = new BusinessDao();
   List allbuss = bussdao.loadAll();
%>
<html>
<head>
<base target="_self">

<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="javascript">
function save(){
    if(checkinput("topo_name","string","拓扑子图名称",30,false)&&
       checkinput("topo_title","string","拓扑子图标题",50,true)){
        var args = window.dialogArguments;
        mainForm.action = "<%=rootPath%>/submap.do?action=saveSubMap";
        mainForm.submit();
        alert("子图创建成功，请在选择视图中浏览!");
        window.close(); 
        iTimerID = window.setInterval(args.location.reload(),30000);//延时刷新父页面
    }   
}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form name="mainForm" method="post">
<input type=hidden name="objEntityStr" value="<%=objEntityStr%>">
<input type=hidden name="linkStr" value="<%=linkStr%>">
<input type=hidden name="asslinkStr" value="<%=asslinkStr%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=500px>
	<tr>
		<td bgcolor="#cedefa" align="center" valign=top>
		<table width="500px" border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;&nbsp;创建子图</td>
				</tr>
			<tr>
				<td height=250 bgcolor="#FFFFFF" valign="top">
				 <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>
						<tr>
							<td nowrap colspan="2" height="3" bgcolor="#8EADD5"></td>
						</tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">拓扑子图名称&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="topo_name" size="30" class="formStyle"><font color="red">&nbsp;*</font>(限20个中文字符)
			               </TD>
			            </tr>
			            <!--
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">拓扑子图别名&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="alias_name" size="20" class="formStyle"><font color="red">&nbsp;*</font>(限6个中文字符)
			               </TD>
			            </tr>
			            -->
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">拓扑子图标题&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="topo_title" size="30" class="formStyle">&nbsp;(限32个中文字符)
			               </TD>
			            </tr>
			            <!--
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">地域&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <select name='topo_area' style='width:100px;'>
            								<option value='1' selected>北京</option>
            								<option value='2'>上海</option>
            					    </select>&nbsp;
			               </TD>
			            </tr>-->
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">背景图片&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <select name='topo_bg' style='width:100px;'>
            								<option value='0' selected>无</option>
            							<%
            							   ReadFile readFile = new ReadFile();
            							   String  path=application.getRealPath(request.getRequestURI());
                                           String  dir=new File(path).getParent(); 
            							   List<String> fileList = readFile.readfile(dir.substring(0,dir.lastIndexOf("afunms"))+"\\resource\\image\\bg");
            							   if(fileList.size()>0){
            							       for(int i = 0;i<fileList.size();i++){
            							 %>
            								<option value='<%=fileList.get(i)%>'><%=fileList.get(i)%></option>
            							<% 
            							       }
            							    }
            							%>
            					    </select>&nbsp;
			               </TD>
			            </tr>
			            <!-- 
                        <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">设备个数&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="objEntityStr" size="30" class="formStyle" value="<%=objEntityStr%>"/>
			               </TD>
			            </tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">链路信息&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="linkStr" size="30" class="formStyle" value="<%=linkStr%>"/>
			               </TD>
			            </tr> 
			            -->
			            <tr>						
			               <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">所属业务&nbsp;</TD>				
			               <TD nowrap width="80%">&nbsp;
								<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none w align=center border=1 algin="center">
                        				<tbody>  
               									<% if( allbuss.size()>0){
  												for(int i=0;i<allbuss.size();i++){
  												    int flag = 0;
  													Business buss = (Business)allbuss.get(i);
  													for(int j=0;j<allbuss.size();j++){
  													    Business vo = (Business)allbuss.get(j);
  													    if(buss.getId().equals(vo.getPid())){
  													        flag++;
  													    }
  													}
  													if(flag == 0){
               									%>                  										                        								
           										<tr align="left"> 
           											<td height="28" align="left">&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=buss.getId()%>" checked>&nbsp;<%=buss.getName()%></td>
           										</tr>  
           										<%  }
           											}
           										}
           										%>                  										                 										                      								
   										</tbody>
   								</table>
			               </TD>																			
			            </tr>			
			            <tr align="left"> 
 						    <td height="28" colspan="2">
 						    <table>
 						        <tr>
 						            <td><INPUT type="radio" class=noborder name=topo_type value="4" checked>作为子图创建(默认)</td>
 						            <td><INPUT type="radio" class=noborder name=topo_type value="1" >业务视图创建</td>
 						            <!-- 
 						            <td><INPUT type="radio" class=noborder name=topo_type value="2" >作为示意拓扑图创建</td>
 						            <td><INPUT type="radio" class=noborder name=topo_type value="3" >作为缩略拓扑图创建</td>-->
 						        </tr>
 						    </table>
 						    </td>
 						</tr>
 						<tr align="left"> 
 						    <td height="28" colspan="2">
 						    <table>
 						        <tr>
 						            <td><INPUT type="checkbox" class=noborder name="home_view" value="1">首页显示</td>
 						            <td nowrap>&nbsp;缩放比例:
								        <select name='zoom_percent' style='width:50px;'>
								            <option value='1' selected>1</option>
            								<option value='0.9'>0.9</option>
            								<option value='0.8'>0.8</option>
            								<option value='0.7'>0.7</option>
            								<option value='0.6'>0.6</option>
            								<option value='0.5'>0.5</option>
            								<option value='0.4'>0.4</option>
            								<option value='0.3'>0.3</option>
            								<option value='0.2'>0.2</option>
            								<option value='0.1'>0.1</option>
            							</select>&nbsp;
            						</td>  
 						        </tr>
 						    </table>
 						    </td>
 						</tr>
						<tr>
							<TD nowrap colspan="2" align=center>
								<br>
								<input type="button" value="保存" style="width:50" class="formStylebutton" onclick="save()">
								<input type="button" value="关闭" style="width:50" class="formStylebutton" onclick="window.close();">
							</TD>	
						</tr>
			         </TBODY>
				   </TABLE>				
				</td>
			</tr>			
		</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>