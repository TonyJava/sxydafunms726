package com.gathertask;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.ShareDataLsf;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.loader.HostLoader;
import com.database.DBManager;
import com.gathertask.dao.TaskResDao;

public class GcTask extends TimerTask{
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("===垃圾回收===");
		System.gc();
		//-----维护端口配置----
		List portconfiglist = new ArrayList();
		PortconfigDao configdao = new PortconfigDao(); 			
		Portconfig portconfig = null;
		Hashtable portconfigHash = new Hashtable();
		try {
			portconfiglist = configdao.getAllBySms();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		if(portconfiglist != null && portconfiglist.size()>0){
			for(int i=0;i<portconfiglist.size();i++){
				portconfig = (Portconfig)portconfiglist.get(i);
				if(portconfigHash.containsKey(portconfig.getIpaddress())){
					List portlist = (List)portconfigHash.get(portconfig.getIpaddress());
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}else{
					List portlist = new ArrayList();
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}
			}
		} 
		ShareData.setPortConfigHash(portconfigHash);
	    //----------------------
		//加载业务hash
		DBManager dbm =new DBManager();
		//Hashtable businessHash = new Hashtable();
		try {
			ShareData.setBusinessHash( dbm.executeQuerykeytwoListHashMap("select * from system_message", "ip","devtype"));
			//System.out.println("====================gc-businessHash====================");
			//System.out.println("=="+businessHash.toString());
			//System.out.println("====================gc-businessHash=====================");
			
			dbm.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		//--------------------------------------------------
		
		
		
		//--------------------加载告警关联信息---------------------------
		
		//加载业务hash
        dbm =new DBManager();
		
		try {
			ShareData.setAlarmcorrelations(dbm.executeQuerykeyoneHashtable("select * from nms_alarm_correlations", "locanode", "fathernode"));
			dbm.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		
	   //-----------------------------------------------------------
		
		
		//---------------关键进程-----------------------
		
		
		try {
			dbm =new DBManager();
			ShareData.setMainprocessHashtable(dbm.executeQuerykeytwoListHashMap("select * from nms_mainprocess", "ipaddress","proname"));
			dbm.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		
		//---------------关键进程-----------------------
		
		
		
		
		//----------------------Lsf----------------------------
		
		Hashtable lsfHash = new Hashtable();
		try {
			 dbm =new DBManager();
			lsfHash = dbm.executeQuerykeyoneListHashMap("select * from lsf_class_node", "nodeid");
			dbm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		ShareDataLsf.setHashLsf(lsfHash);
		//----------------------Lsf----------------------------
		
		
		AlarmIndicatorsUtil.loadAlarmIndicatorsNode();
		
		
		
		
		//--------------------agent alarm--------------------------------
		try {
			 dbm =new DBManager();
			//lsfHash = ;
			
			ShareData.setAgentalarmlevellist(dbm.executeQuerykeyoneHashtable("select * from nms_agent_alarm", "alarmname","alarmlevel"));
			dbm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		//-----------------------agent alarm-------------------------
		
		//--------------------syslog--------------------------------
		try {
			 dbm =new DBManager();
			//lsfHash = ;
			
			ShareData.setSyslogHash(dbm.executeQuerykeyoneHashtable("select * from nms_netsyslogrule_node", "nodeid","facility"));
			dbm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		//-----------------------syslog-------------------------
		
		
		
		//------------------------nms_proces----------------------------
		try {
			 dbm =new DBManager();
			//lsfHash = ;
			
			ShareData.setProcessconfigHashtable(dbm.executeQuerykeytwoListHashMap("select * from nms_procs where flag='1'", "ipaddress","procname"));
			dbm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		//-----------------------nms_proces-------------------------
		
		//------------------------node_submap_relation----------------------------
		try {
			 dbm =new DBManager();
			//lsfHash = ;
			
			ShareData.setSubmapnodeHashtable(dbm.executeQuerykeyoneListHashMap("select * from node_submap_relation", "node_id"));
			dbm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			dbm.close();
		}
		
		//-----------------------node_submap_relation-------------------------
		
		
		
		
		//System.out.println("=============开始定时加载节点=================");
		HostLoader hostloader=new HostLoader();
		hostloader.loading();
		//-----------把需要维护的采集任务id 放入到内存中-----------------------------------
		//------------------jhl 2012 add resourceConf-----------------
		TaskResDao taskrdao=new TaskResDao();
//		TaskResDao resDao = new TaskResDao();
//		Hashtable _table = resDao.queryResConf();//设置维护列表
//		ShareData.setResourceConfHashtable(_table);
		if(taskrdao.queryRes()){
			taskrdao.updateState();//设置资源维护状态为失效
		}
		ShareData.setResourceConfHashtable(taskrdao.queryResConf());
//		System.out.println("资源维护数据列表-------------------------------"+ShareData.getResourceConfHashtable().toString());
		//------------------jhl 2012-04 end resourceConf-----------------
	}
}
