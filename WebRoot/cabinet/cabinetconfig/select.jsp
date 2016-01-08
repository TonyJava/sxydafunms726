<%@ page contentType="text/html; charset=GBK"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.cabinet.model.MachineCabinet"%>
<%
List list = (List)request.getAttribute("list");
%>
<OPTION value="-1">--È«²¿--</OPTION>
<% 
	for(int i=0;i<list.size();i++){
%>	
<OPTION value="<%=((MachineCabinet)list.get(i)).getId()%>"><%=((MachineCabinet)list.get(i)).getName()%></OPTION>
<%
}
%>