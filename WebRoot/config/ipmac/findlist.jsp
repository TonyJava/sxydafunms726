<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.om.IpMac"%>
<%@ page import="com.afunms.system.model.Department"%>
<%@ page import="com.afunms.system.dao.DepartmentDao"%>
<%@ page import="com.afunms.config.model.Employee"%>
<%@ page import="com.afunms.polling.om.IpMacBase"%>
<%@ page import="com.afunms.config.dao.EmployeeDao"%>
<%@ page import="com.afunms.topology.dao.IpMacBaseDao"%>
<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  String key = (String)request.getAttribute("key");
  String value = (String)request.getAttribute("value");
  System.out.println("key==="+key+"====value:"+value);
  String relateipaddrchecked = "";
  String ipaddresschecked = "";
  String macchecked = "";
  String valuestr = "";
  if(value != null)valuestr=value;
  if(key != null && key.equals("relateipaddr")){
  	relateipaddrchecked = "selected";
  }
  if(key != null && key.equals("ipaddress")){
  	ipaddresschecked = "selected";
  }
  if(key != null && key.equals("mac")){
  	macchecked = "selected";
  }
  IpMacBaseDao dao = new IpMacBaseDao(); 
  List macbaselist = dao.loadAll();
  Hashtable macbaseHash = new Hashtable();
  if(macbaselist != null&& macbaselist.size()>0){
  	for(int i=0;i<macbaselist.size();i++){
  		IpMacBase ipmacbase = (IpMacBase)macbaselist.get(i);
  		macbaseHash.put(ipmacbase.getMac(),ipmacbase);
  	}
  }
  dao.close();
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/ipmac.do?action=delete";
  var listAction = "<%=rootPath%>/ipmac.do?action=find";
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("�������ѯ����");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/ipmac.do?action=find";
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
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }

</script>
<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//�޸Ĳ˵������¼�ͷ����
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
//���Ӳ˵�	
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
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"  onload="initmenu();">
<form method="post" name="mainForm">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
	
	<%=menuTable%>				
	</td>
		<td align="center" valign=top>
			<table width="98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title"><b>��Դ >> IP/MAC��Դ >> IP/MAC</b></td>
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
				&nbsp;&nbsp;&nbsp;&nbsp;<B>��ѯ:</B>
        								<SELECT name="key" style="width=100"> 
          									<OPTION value="relateipaddr" <%=relateipaddrchecked%>>�����豸IP</OPTION>
          									<OPTION value="ipaddress" <%=ipaddresschecked%>>IP��ַ</OPTION>
          									<OPTION value="mac" <%=macchecked%>>MAC</OPTION>          
          									
          								</SELECT>&nbsp;<b>=</b>&nbsp; 
          								<INPUT type="text" name="value" width="15" class="formStyle" value="<%=valuestr%>">
          								<INPUT type="button" class="formStyle" value="��ѯ" onclick=" return doQuery()">
          							</td> 
          							<td bgcolor="#ECECEC" width="50%" align='right'>
									
									<a href="#" onclick="toDelete()">ɾ��</a>&nbsp;&nbsp;&nbsp;
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
		<td align=right bgcolor="#ECECEC">
				<a href="<%=rootPath%>/ipmac.do?action=downloadipmacfindreportall" target="_blank"><img name="selDay1" alt='����EXCEL' style="CURSOR:hand" src="<%=rootPath%>/resource/image/export_excel.gif" width=18  border="0">����EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
		
		</td>
		</tr>						

        						
							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
	  									<tr class="microsoftLook0">
      											<th width='5%'><INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()">���</th>				
      											<th width='20%'>����������(ip)</th>
      											<th width='5%'>�˿�</th>
      											<th width='15%'>IP��ַ</th>      
      											<th width='20%'>MAC</th>
      											<th width='10%'>����</th>
      											
										</tr>
<%
    IpMac vo = null;
    int startRow = jp.getStartRow();
    session.setAttribute("key",key);
    session.setAttribute("value",value);
    for(int i=0;i<rc;i++)
    {
       vo = (IpMac)list.get(i);

          
%>
   										<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> height=25>  
    											<td ><INPUT type="checkbox" class=noborder name=checkbox value="<%=vo.getId()%>">
    												<font color='blue'><%=startRow + i%></font></td>
    											<td  align='center' style="cursor:hand"><%=vo.getRelateipaddr()%></td>
    											<td  align='center'><%=vo.getIfindex()%></td>
    											<td  align='center'><%=vo.getIpaddress()%></td>
    											<td  align='center'><%=vo.getMac()%></td>
    											<%
    												int baseflag = 0;
    												String band="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifband=0>ȡ����</a>";
    												if(vo.getIfband().equals("0")){
    													band="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifband=1>��</a>";
    												}
    												String sms = "<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifsms=0>ȡ������</a>";
    												if(vo.getIfsms().equals("0")){
    													sms="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifsms=1>����</a>";
    												}
    												if(macbaseHash != null && macbaseHash.size()>0){
    													if(macbaseHash.containsKey(vo.getMac())){
    														baseflag = 1;
    													}
    												}
    												if(baseflag == 0){
    											%>
    											<td  align='left' bgcolor=#99CC00><a href="<%=rootPath%>/ipmac.do?action=selsetmacbase&relateip=<%=vo.getRelateipaddr()%>&ifindex=<%=vo.getIfindex()%>&macip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>&key=<%=key%>&value=<%=value%>"><font color=blue>��Ϊ����</font></a></td>
    											<%
    												}else{
    											%>
    											<td  align='left'><a href="<%=rootPath%>/ipmac.do?action=selcancelmacbase&relateip=<%=vo.getRelateipaddr()%>&index=<%=vo.getIfindex()%>&ip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>&key=<%=key%>&value=<%=value%>"><font color=blue>ȡ������</font></a></td>
    											<%
    												}
    											%>
        										
  										</tr>			
<% }%>
									</table>
								</td>
							</tr>	
				<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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