<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.application.course.model.Lsfclass"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.afunms.application.course.dao.LsfClassProcessMonitoringDao"%>
<%@page import="com.afunms.application.course.model.LsfClassComprehensiveModel"%>
<%
	JspPage jp = (JspPage) request.getAttribute("page");
	String rootPath = request.getContextPath();
	User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	List list = null;
	LsfClassProcessMonitoringDao dao = new LsfClassProcessMonitoringDao();
	list = dao.loadFormLsfClass();
	if (list == null) {
		list = new ArrayList();
	}
%>
<html>
<head>
<title></title>
<link rel="StyleSheet" href="<%=rootPath%>/application/course/dtree/dtree.css" type="text/css" />
<script type="text/javascript" src="<%=rootPath%>/application/course/dtree/dtree.js"></script>
<style>
body {
margin-left: 6px;
margin-top: 0px;
margin-right: 6px;
margin-bottom: 6px;
scrollbar-face-color: #E0E3EB;
scrollbar-highlight-color: #E0E3EB;
scrollbar-shadow-color: #E0E3EB;
scrollbar-3dlight-color: #E0E3EB;
scrollbar-arrow-color: #7ED053;
scrollbar-track-color: #ffffff;
scrollbar-darkshadow-color: #9D9DA1;
}
body,td,th {color: #666666;line-height:20px}
.div_RightMenu
{
    z-index:30000;
    text-align:left;    
    cursor: default;
    position: absolute;
    background-color:#FAFFF8;
    width:100px;
    height:auto;
    border: 1px solid #333333;
    display:none;
    filter:progid:DXImageTransform.Microsoft.Shadow(Color=#333333,Direction=120,strength=5);    
}
.divMenuItem
{
    height:17px;
    color:Black;
    font-family:宋体;
    vertical-align:middle;
    font-size:10pt;
    margin-bottom:3px;
    cursor:hand;
    padding-left:10px;
    padding-top:2px;
}
</style>
</head>
<body>
<div class="dtree" style="">
<!--  
<p><a href="javascript: d.openAll();">展开</a> | <a href="javascript: d.closeAll();">合闭</a> | <a href="javascript: window.location.reload();">刷新</a></p>
-->
<script type="text/javascript"><!--
        var currTreeNodeId = '';		// 当前树的节点 Id
        var treeNodeFatherId = '';		// 当前树的节点的父 Id	
        var imagestr = '';
        var key = 0 ;
		d = new dTree('d');
		d.add(0,-1,'集群监控','','','','rightFrame');
		//d.add(0,-1,'集群监控','<%=rootPath+ "/lsfProcessMonitoring.do?action=list&isTreeView=1&jp=1"%>','','','rightFrame');
		parent.document.getElementById("rightFrame").src='<%=rootPath+ "/lsfProcessMonitoring.do?action=list_join&isTreeView=1&jp=1"%>';//rooms
		//parent.document.getElementById("rightFrame").src='<%=rootPath+ "/eqproom.do?action=list&isTreeView=1&jp=1"%>';//rooms
		
		<%
		String img= rootPath+"/application/course/dtree/img/folder_2.gif";
		String b="2";
			for(int i = 0;i<list.size();i++){
				
				Lsfclass room = (Lsfclass)list.get(i);
				System.out.println("System.out.println(room.getClass_id())----------------------"+room.getClass_id());
				String url = rootPath + "/lsfProcessMonitoring.do?action=list_join_nodeid&id="+room.getClass_id();
				System.out.println("url----------------:"+url);
				%>
				d.add('r'+'<%=room.getClass_id()%>',0,'<%=room.getClass_name()%>','<%=url%>','','','rightFrame','<%=img%>','<%=room.getClass_id()%>');
				<%
			}
		%>
		document.write(d);
//------------search one device-------------根据选中的树节点在地图上搜索对应的设备

function SearchNode(ip)
{
	var coor = window.parent.mainFrame.mainFrame.getNodeCoor(ip);
	if (coor == null)
	{
		var msg = "没有在图中搜索到IP地址为 "+ ip +" 的设备。";
		window.alert(msg);
		return;
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}
	window.parent.mainFrame.mainFrame.moveMainLayer(coor);
}
//--------------------end--------------------
//--------------------begin选中设备显示右键菜单--------------------
var nodeid="";
var nodeip="";
var nodecategory="";
function showMenu(id,ip){
    nodeid = id.split(";")[0];
    nodecategory = id.split(";")[1];
    nodeip = ip;
    /**/
    if(document.getElementById("div_RightMenu") == null)
    {    
        CreateMenu();
        document.oncontextmenu = ShowMenu
        document.body.onclick  = HideMenu    
    }
    else
    {
        document.oncontextmenu = ShowMenu
        document.body.onclick  = HideMenu    
    } 

}
function add(){
    var nodeId = nodeid;//要保证nodeid的长度大于3
    var coor = window.parent.mainFrame.mainFrame.getNodeId(nodeId);
    if (coor == null)
	{
         window.parent.mainFrame.mainFrame.addEquip(nodeId,nodecategory);
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}
    window.parent.mainFrame.mainFrame.moveMainLayer(coor);
    window.alert("该设备已经在拓扑图中存在！");
}
function detail(){
    showalert(nodeid);
	window.parent.parent.opener.focus();
}
function showalert(id) {
	alert("performance/tree.jsp-/afunms/detail/dispatcher.jsp?id="+id);
	window.parent.parent.opener.location="/afunms/detail/dispatcher.jsp?id="+id;
}
function evtMenuOnmouseMove()
{
    this.style.backgroundColor='#8AAD77';
    this.style.paddingLeft='10px';    
}
function evtOnMouseOut()
{
    this.style.backgroundColor='#FAFFF8';
}
function CreateMenu()
{    
        var div_Menu          = document.createElement("Div");
        div_Menu.id           = "div_RightMenu";
        div_Menu.className    = "div_RightMenu";
        
        var div_Menu1         = document.createElement("Div");
        div_Menu1.id          = "div_Menu1";
        div_Menu1.className   = "divMenuItem";
        div_Menu1.onclick     = add;
        div_Menu1.onmousemove = evtMenuOnmouseMove;
        div_Menu1.onmouseout  = evtOnMouseOut;
        div_Menu1.innerHTML   = "添加到拓扑图";
        var div_Menu2         = document.createElement("Div");
        div_Menu2.id          = "div_Menu2";
        div_Menu2.className   = "divMenuItem";
        div_Menu2.onclick     = detail;
        div_Menu2.onmousemove = evtMenuOnmouseMove;
        div_Menu2.onmouseout  = evtOnMouseOut;
        div_Menu2.innerHTML   = "详细信息";
        
        div_Menu.appendChild(div_Menu1);
        div_Menu.appendChild(div_Menu2);
        document.body.appendChild(div_Menu);
}
// 判断客户端浏览器
function IsIE() 
{
    if (navigator.appName=="Microsoft Internet Explorer") 
    {
        return true;
    } 
    else 
    {
        return false;
    }
}

function ShowMenu()
{
    
    if (IsIE())
    {
        document.body.onclick  = HideMenu;
        var redge=document.body.clientWidth-event.clientX;
        var bedge=document.body.clientHeight-event.clientY;
        var menu = document.getElementById("div_RightMenu");
        if (redge<menu.offsetWidth)
        {
            menu.style.left=document.body.scrollLeft + event.clientX-menu.offsetWidth
        }
        else
        {
            menu.style.left=document.body.scrollLeft + event.clientX
            //这里有改动
            menu.style.display = "block";
        }
        if (bedge<menu.offsetHeight)
        {
            menu.style.top=document.body.scrollTop + event.clientY - menu.offsetHeight
        }
        else
        {
            menu.style.top = document.body.scrollTop + event.clientY
            menu.style.display = "block";
        }
    }
    return false;
}
function HideMenu()
{
    if (IsIE())  document.getElementById("div_RightMenu").style.display="none";    
}
--></script>
</div>
</body>
</html>