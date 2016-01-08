<%@ page language="java" import="java.util.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.detail.reomte.model.ProcessInfo;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.temp.dao.DiskTempDao;
import com.afunms.temp.dao.InterfaceTempDao;
import com.afunms.temp.dao.MemoryTempDao;
import com.afunms.temp.dao.ProcessTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostLastCollectDataDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'test.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <%
    Hashtable sharedata = ShareData.getSharedata();
	Hashtable ipdata = (Hashtable)sharedata.get(ip);
	if (ipdata !=null && ipdata.size()>0){
		Vector sdata = (Vector)ipdata.get(category.toLowerCase());
		if (category.toLowerCase().equalsIgnoreCase("Process")){
			if (sdata != null && sdata.size()>0){
				for (int i=0;i<sdata.size();i++){
				Processcollectdata hdata = (Processcollectdata)sdata.get(i);
			%>
				<%=hdata.getProcessname() %>
			<%	}
			}
		}
	}
     %>
  </body>
</html>
