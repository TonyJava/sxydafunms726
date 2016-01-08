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
import com.afunms.topology.model.HostNode;
import com.afunms.polling.impl.*" pageEncoding="UTF-8"%>
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
    	String order = "MemoryUtilization";
    	String starttime = "";
    	String endtime = "";
   		Hashtable processhash = new Hashtable();
   		I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		processhash = hostlastmanager.getProcess_share("10.204.7.86","Process",order,starttime,endtime);
		if(processhash!=null){
			for(int m=0;m<processhash.size();m++)
			{
				phash=(Hashtable)processhash.get(new Integer(m));
				ProcessInfo processInfo = new ProcessInfo();
						String Name = (String)phash.get("Name");
						processInfo.setName(Name);
						processInfo.setUSER((String)phash.get("USER"));
						processInfo.setType((String)phash.get("Type"));
						processInfo.setStatus((String)phash.get("Status"));
						
						String CpuTime = (String)phash.get("CpuTime");
						if(CpuTime != null){
							if(CpuTime.indexOf(":")!=-1){
								Matcher matcher = p1.matcher(CpuTime);
								if(matcher.find())
								{
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1)*60 + Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}
						
						String MemoryUtilization = (String)phash.get("MemoryUtilization");
						Float sumOfMemUtilization = Float.valueOf("0"); 
						if(MemoryUtilization.trim().length() >1){
							sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length()-1));
						}
						processInfo.setMemoryUtilization(sumOfMemUtilization);
						
						String Memory = (String)phash.get("Memory");
						Float sumOfMem = Float.valueOf("0"); 
						if(Memory.trim().length() > 1){
							 sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length()-1));
						}
						processInfo.setMemory(sumOfMem);
						
						//processInfo.setUSER((String)phash.get("USER"));
						String CpuUtilization = (String)phash.get("CpuUtilization");
						if(CpuUtilization != null && CpuUtilization.trim().length()>0){
							Float sumOfCpuUtilization = Float.parseFloat(CpuUtilization.substring(0, CpuUtilization.length()-1));
							processInfo.setCpuUtilization(sumOfCpuUtilization);
						}else{
							processInfo.setCpuUtilization("-");
						}
						processInfo.setPid((String)phash.get("process_id"));
						String threadCount = (String)phash.get("ThreadCount");
						processInfo.setThreadCount(threadCount);
						String handleCount = (String)phash.get("HandleCount");
						processInfo.setHandleCount(handleCount);
						ProcessInfo newProcessInfo = processInfo.clone();
						newProcessHash.put(Name, processInfo);
						Vector detailVect = new Vector();
						detailVect.add(newProcessInfo);
						detailHash.put(Name, detailVect);
			}
		}	
     %>
  </body>
</html>
