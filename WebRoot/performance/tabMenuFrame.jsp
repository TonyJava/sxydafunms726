<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
  String rootPath = request.getContextPath();  
  System.out.println(rootPath+"/img/xnzy.gif");
  %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css" rel="stylesheet" type="text/css"/>
<script language="JavaScript" type="text/JavaScript"><!--
function MM_preloadimg() { //v3.0
  var d=document; if(d.img){ 
  if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadimg.arguments; 
    for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ 
   	 d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];
    }
  }
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ 
  if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; 
    for(i=0; i<a.length; i++)
    	if (a[i].indexOf("#")!=0){ 
    		d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];
    	}
    }
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; 
  for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++){ 
  	x.src=x.oSrc;
  }
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; 
  if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
  }
  if(!(x=d[n])&&d.all) x=d.all[n]; 
  for (i=0;!x&&i<d.forms.length;i++) {
  	x=d.forms[i][n];
  }
  for(i=0;!x&&d.layers&&i<d.layers.length;i++){
   x=MM_findObj(n,d.layers[i].document);
  }
  if(!x && d.getElementById){
  	x=d.getElementById(n); 
  }
  return x;
}

function MM_swapImage() { //v3.0
	
}

function refreshTab(imgId){
	//window.location.reload();
	if(imgId == "xnzy"){//性能资源
		window.parent.document.getElementById("tabMenuContent").src = "tabMenu.do?action=xnzy";	
	}
	if(imgId == "llxn"){//链路信息
		window.parent.document.getElementById("tabMenuContent").src = "tabMenu.do?action=llxn";
	}
	if(imgId == "toppx"){//top排序
		window.parent.document.getElementById("tabMenuContent").src = "tabMenu.do?action=toppx";
	}
	if(imgId == "xnmb"){//性能面板
		window.parent.document.getElementById("tabMenuContent").src = "performancePanel.do?action=panelList";
	}
	if(imgId == "dkdb"){//端口对比
		window.parent.document.getElementById("tabMenuContent").src = "tabMenu.do?action=portcomparelist";
	}
	if(imgId == "zjst"){//主机视图
		window.parent.document.getElementById("tabMenuContent").src = "hostApply.do?action=serverview";
	}
}

</script>

<style type="text/css">
ul {
	list-style-type: none;
}
ul , li{
	padding:0;
	margin:0;
	border: 0px;
	background-image: url('<%=rootPath %>/img/tabMenuBg.gif');
	text-align: left;
}
ul, li,A{
	border: 0;
	background-repeat: no-repeat;
}
ul, li,A,img{
	border: 0;
}
/*
ul, li,A,img{
	width: 30px;
	height: 72px;
	border: 0;
}
*/

.menu-title{
	width: 25px;
	height: 72px;
	font-size: 14px; 
	text-align : center;
	table-layout:inherit;
	background-image: url('<%=rootPath %>/img/tabMenuBg.gif');
	color:#000000;
	layout-flow : vertical-ideographic;
}

.menu-title-over{
	width: 25px;
	height: 72px;
	font-size: 14px; 
	text-align : center;
	background-image: url('<%=rootPath %>/img/tabMenuBg_2.gif');
	color:#000000;
	layout-flow : vertical-ideographic;
}
.span_text{
	margin-bottom: 40px;
}
</style>
</head>
<body id="body-container" style="scroll:no;overflow:hidden;overflow-y:hidden;overflow-x:hidden;">
	<ul>  
		<li id="xnzy" class="menu-title">
			<A onclick="refreshTab('xnzy')" href="#"><span class="span_text">性能资源</span></A>
		</li>
		<li id="llxn" class="menu-title">
			<A onclick="refreshTab('llxn')" href="#"><span class="span_text">链路信息</span></A>
		</li>
		<li id="toppx" class="menu-title">
			<A onclick="refreshTab('toppx')" href="#"><span class="span_text">TOP排序</span></A>
		</li>
		<li id="xnmb" class="menu-title">
			<A onclick="refreshTab('xnmb')" href="#"><span class="span_text">性能面板</span></A>
		</li>
		<li id="dkdb" class="menu-title">
			<A onclick="refreshTab('dkdb')" href="#"><span class="span_text">端口对比</an></A>
		</li>
		<li id="zjst" class="menu-title">
			<A onclick="refreshTab('zjst')" href="#"><span class="span_text">主机视图</an></A>
		</li>
	</ul>
</body>
</html>