<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%
String name=(String)request.getParameter("attachfiles");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
 	<head>
    	<title>在线观看</title>
    	<script src="../../js/room/ufo.js" language="javascript"></script>
	</head>
	<body>
		<p id="player1"><a href="http://www.macromedia.com/go/getflashplayer">Get the Flash Player</a> to see this player.</p>
		<br>
     	<script type="text/javascript">
     		var FO = {movie:"flvplayer.swf",width:"480",height:"400",majorversion:"7",build:"0",bgcolor:"#FFFFFF",allowfullscreen:"true",
			flashvars:"file=flv/<%=name %>&image=preview.jpg"};
			UFO.create(FO,"player1");
		</script>
        <br>
  </body>
</html>
