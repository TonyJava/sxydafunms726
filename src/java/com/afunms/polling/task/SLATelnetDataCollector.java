package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.AS400Collection;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.TelnetWrapper;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class SLATelnetDataCollector {
	
	public SLATelnetDataCollector()
	{
	}
	public Hashtable collect_data(Huaweitelnetconf telconf,List nodelist) {
    	Vector vector=null;
    	Hashtable allslahash = new Hashtable();
    	String[] result = new String[nodelist.size()];
		try {
            		try{
            			//  首先获取协议信息
            			SysLogger.info(" ######################## ");
            			SysLogger.info(" ### 开始采集SLA " + telconf.getIpaddress() + " by telnet");
            			SysLogger.info(" ######################## ");
            			Hashtable configNodeHash = new Hashtable();
    					CiscoTelnet telnet = new CiscoTelnet(telconf.getIpaddress(), telconf
    							.getUser(), telconf.getPassword());
    					if (telnet.login()) {
    						if(nodelist != null && nodelist.size()>0){
    							String[] commStr = new String[nodelist.size()];
    							String commstr = "";
    							for(int i=0;i<nodelist.size();i++){
    								SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
    								commstr = "show rtr op "+slaconfig.getEntrynumber();
    								commStr[i] =  commstr;
    								//configNodeHash.put(i, slaconfig);
    							}
    							try{
    								result = telnet.getSlaResult(telconf.getSupassword(), commStr);
    							}catch(Exception e){
    								e.printStackTrace();
    							}
    							
    							if(result != null && result.length>0){
    								//对结果进行解析
    								
    								Pingcollectdata hostdata = null;
    								Calendar date=Calendar.getInstance();
    								for(int i=0;i<result.length;i++){
    									String entrynumber = "";
    									String content = result[i];
    									String[] st = content.split("\r\n");
    									Hashtable dataHash = new Hashtable();
//	    								_vector.add(0,null);
//	    								_vector.add(1,null);
    									if(st != null && st.length>0){
    										
    										for(int k=0;k<st.length;k++){
    											entrynumber = "";
    											String linecontent = st[k];
    											SysLogger.info(linecontent);
    											if(linecontent.contains("Entry Number")){
    												String[] splits = linecontent.split(":");
    												entrynumber = splits[1].trim();
    											}
    											if(linecontent.contains("Latest Completion Time (milliseconds)")){
    												String[] splits = linecontent.split(":");
    												String rtt = splits[1].trim();
    												//响应时间
    												hostdata=new Pingcollectdata();
    												hostdata.setIpaddress(telconf.getIpaddress());
    												hostdata.setCollecttime(date);
    												hostdata.setCategory("Ping");
    												hostdata.setEntity("ResponseTime");
    												hostdata.setSubentity("ResponseTime");
    												hostdata.setRestype("dynamic");
    												hostdata.setUnit("毫秒");
    												hostdata.setThevalue(rtt);
    												SysLogger.info("====add rtt data ====="+rtt+"毫秒");
    												dataHash.put(1, hostdata);
    												//_vector.add(1, hostdata);
    												
    											}else if(linecontent.contains("Latest Operation Return Code")){
    												
    												//状态
    												String[] splits = linecontent.split(":");
    												hostdata=new Pingcollectdata();
    						    					hostdata.setIpaddress(telconf.getIpaddress());
    						    					hostdata.setCollecttime(date);
    						    					hostdata.setCategory("Ping");
    						    					hostdata.setEntity("Utilization");
    						    					hostdata.setSubentity("ConnectUtilization");
    						    					hostdata.setRestype("dynamic");
    						    					hostdata.setUnit("%");
    												String status = splits[1].trim();
    												if("ok".equalsIgnoreCase(status)){
    													hostdata.setThevalue("100");
    												}else{
    													hostdata.setThevalue("0");
    												}
    												dataHash.put(0, hostdata);
    												//_vector.add(0, hostdata);
    											}
    										}
    									}
    									SlaNodeConfig slaconfig = (SlaNodeConfig)nodelist.get(i);
    									SysLogger.info("id:"+slaconfig.getId()+" _vector size============="+dataHash.size());
    									allslahash.put(slaconfig.getId()+"", dataHash);
    								}
    								
    							}
    							
    						}
    					}
            			

            		}catch(Exception e){
            			e.printStackTrace();
            		}
        }catch(Exception exc){
        	
        }
        return allslahash;
	}
	
	/**
	 * 创建告警数据
	 * @author nielin
	 * @param vo
	 * @param collectingData
	 */
	public void updateData(Object vo , Object collectingData){
		HostNode node = (HostNode)vo;
		Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId());
		Hashtable datahashtable = (Hashtable)collectingData;
		List jobList = (List)datahashtable.get("Jobs");
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, "as400");
		for(int i = 0 ; i < list.size() ; i ++){
			AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
			String indicators = alarmIndicatorsNode.getName();
			CheckEventUtil checkEventUtil = new CheckEventUtil();
			if("diskperc".equals(indicators)){
				//磁盘利用率
//				SysLogger.info("### 开始检测磁盘是否告警 ###");
				Vector diskVector = new Vector();
				if(datahashtable.get("disk") != null)diskVector = (Vector)datahashtable.get("disk");
				if(diskVector == null)diskVector = new Vector();
				checkEventUtil.checkDisk(host, diskVector, alarmIndicatorsNode);
			}else if("jobs".equals(indicators)){
				List jobForAS400EventList = checkEventUtil.createJobForAS400GroupEventList(node.getIpAddress() , jobList , alarmIndicatorsNode);
			}else {
				checkEventUtil.updateData(vo, collectingData, AlarmConstant.TYPE_HOST, "as400", alarmIndicatorsNode);
			}
		}
	}
}
