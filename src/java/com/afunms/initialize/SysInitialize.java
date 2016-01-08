/**
 * <p>Description:system initialize,loads system resources when server starting</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.initialize;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.manage.PerformancePanelManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.IpAlias;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.inform.dao.AlarmDao;
import com.afunms.polling.PollingEngine;
import com.afunms.sysset.dao.ServiceDao;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.ConnectTypeConfig;

public class SysInitialize
{
	private ResourceCenter res;
	private SAXBuilder builder;
	
    public SysInitialize()
    {    
        res = ResourceCenter.getInstance();	
        builder = new SAXBuilder();
    } 
    
    public void init()
    {
    	loadSystemConfigXml();
    	loadManagerXml();    	
    	loadActionXml(); 
    	loadAjaxManagerXml();
    	loadMenuXml();
    	loadService();
    	loadCfgBackup();
    	PollingEngine.getInstance().doPolling();
    	//初始化性能面板中的数据
    	PerformancePanelManager.getInstance().init(); 
    	if(res.hasDiscovered())  //发现过了
    	{
    		
    		//loadTasks();
    		deleteAllAlarm(); 
    		/*
            //================轮询初始化===========================       	    	
        	if(res.hasDiscovered() && res.isStartPolling()) //如果在1或2区则轮询
        		PollingEngine.getInstance().doPolling();   
        	*/
    	}
    	//告警阀值初始化加载
    	AlarmIndicatorsUtil alarmIndicatorsUtil=new AlarmIndicatorsUtil();
    	alarmIndicatorsUtil.loadAlarmIndicatorsNode(); 

        //清除告警信息
    	CheckEventDao checkeventdao = new CheckEventDao();
		try{
			checkeventdao.empty();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			checkeventdao.close();
		}
		
        ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
        Hashtable connectConfigHashtable = new Hashtable();
		List configList = new ArrayList();
		try{
			configList = connectTypeConfigDao.loadAll();
		}catch(Exception e){
			
		}finally{
			connectTypeConfigDao.close();
			connectTypeConfigDao = null;
		}
		if(configList != null && configList.size()>0){
			for(int i=0;i<configList.size();i++){
				ConnectTypeConfig connectTypeConfig = (ConnectTypeConfig)configList.get(i);
				connectConfigHashtable.put(connectTypeConfig.getNode_id(), connectTypeConfig);
			}
		}		
		ShareData.getConnectConfigHashtable().put("connectConfigHashtable", connectConfigHashtable);
		
		//刷新内存中采集指标
		NodeGatherIndicatorsUtil gatherutil = new NodeGatherIndicatorsUtil();
		gatherutil.refreshShareDataGather();
		
		IpaddressPanelDao paneldao = new IpaddressPanelDao();
		List list = null;
		try{
			list = paneldao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			paneldao.close();
		}
		PollingEngine.getInstance().setPanelList(list);
		
		//装栽内存中IP与别名IP对照表
		ShareData.setAllipalias(new Hashtable());
		IpAliasDao ipaliasdao = new IpAliasDao();
		List allist = new ArrayList();
		try{
			//delte后,conn已经关闭
			allist = ipaliasdao.loadAll();
		}catch(Exception e){
				e.printStackTrace();
		}finally{
			ipaliasdao.close();
		}
		if(allist != null && allist.size()>0){
			for(int i=0;i<allist.size();i++){
				IpAlias vo = (IpAlias)allist.get(i);
				if(ShareData.getAllipalias() != null){
					if(ShareData.getAllipalias().containsKey(vo.getIpaddress())){
						//已经存在主键，则先获取，然后在追加进去
						((List)ShareData.getAllipalias().get(vo.getIpaddress())).add(vo.getAliasip());
					}else{
						List aliaslist = new ArrayList();
						aliaslist.add(vo.getAliasip());
						ShareData.getAllipalias().put(vo.getIpaddress(), aliaslist);
					}
				}
			}
		}
		
		//将端口配置装载到内存中
		ShareData.setAllportconfigs(new Hashtable());
		PortconfigDao portconfigdao = new PortconfigDao();
		List portconfglist = new ArrayList();
		try{
			portconfglist = portconfigdao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			portconfigdao.close();
		}
		if(portconfglist != null && portconfglist.size()>0){
			for(int i=0;i<portconfglist.size();i++){
				Portconfig portconfig = (Portconfig)portconfglist.get(i);
				if(ShareData.getAllportconfigs() != null){
					ShareData.getAllportconfigs().put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
				}else{
					Hashtable hash = new Hashtable();
					hash.put(portconfig.getIpaddress()+":"+portconfig.getPortindex(), portconfig);
					ShareData.setAllportconfigs(hash);
				}
			}
		}
		
		ManageXmlDao subMapDao = new ManageXmlDao();
		List subfileList = null;
		try{
			subfileList = subMapDao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			subMapDao.close();
		}
		PollingEngine.getInstance().setXmlList(subfileList);
		
//		NodeGatherIndicatorsDao gatherDao = new NodeGatherIndicatorsDao();
//        Hashtable gatherHashtable = new Hashtable();
//		try{
//			gatherHashtable = gatherDao.getAllGather();
//		}catch(Exception e){
//			
//		}finally{
//			gatherDao.close();
//			gatherDao = null;
//		}
//		if(gatherHashtable == null)gatherHashtable = new Hashtable();
//		ShareData.setGatherHash(gatherHashtable);
    } 
		public void loadCfgBackup()
    {
    	Hashtable cfgHas = new Hashtable();
    	Hashtable h3cHas = new Hashtable();
    	Hashtable ciscoHas = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/cfg-backup.xml"));          
            List ciscoList = doc.getRootElement().getChildren("cisco");
            Iterator it = ciscoList.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	ciscoHas.put(element.getChild("name").getText(), element.getChild("class").getText()); 
            }       
            List list = doc.getRootElement().getChildren("h3c");
            it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	String type = element.getChild("type").getText();
            	String method = element.getChild("method").getText();
            	h3cHas.put(type,method); 
            }
            cfgHas.put("h3c", h3cHas);
            cfgHas.put("cisco", ciscoHas);
            res.setCfgHash(cfgHas);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }
    /**
     * 系统路径
     */
    public void setSysPath(String path)
    {
    	res.setSysPath(path);
    }

    /**
     * 加载系统配置信息
     */
    private void loadSystemConfigXml()
    {
        SAXBuilder builder = new SAXBuilder();
        try
		{        	
           Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/system-config.xml"));  
           
           res.setAppServer(doc.getRootElement().getChildText("app-server"));   
           res.setSnmpversion(doc.getRootElement().getChildText("snmpversion"));
           res.setJndi(doc.getRootElement().getChildText("jndi"));
           
           String temp1 = doc.getRootElement().getChildText("log-info");
           res.setLogInfo(Boolean.parseBoolean(temp1));
          
           String temp2 = doc.getRootElement().getChildText("log-error");
           res.setLogError(Boolean.parseBoolean(temp2));
           
           String temp3 = doc.getRootElement().getChildText("poll_per_thread_nodes");
           res.setPerThreadNodes(Integer.parseInt(temp3));

           String temp4 = doc.getRootElement().getChildText("poll_thread_interval");
           res.setPollingThreadInterval(Integer.parseInt(temp4) * 60 * 1000);                      

           String temp5 = doc.getRootElement().getChildText("max_threads");
           res.setMaxThreads(Integer.parseInt(temp5));
           
           String temp6 = doc.getRootElement().getChildText("start_polling");
           res.setStartPolling(Boolean.parseBoolean(temp6));
                      
           String temp8 = doc.getRootElement().getChildText("has_discoverd");
           res.setHasDiscovered(Boolean.parseBoolean(temp8));           
		}
        catch(Exception e)
		{
      	    SysLogger.error("SysInitializtion.loadSystemConfigXml()",e);
		}
    }
    
    private void loadAjaxManagerXml()
    {        
        Hashtable ajaxManagerMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/ajax.xml"));          
            List list = doc.getRootElement().getChildren("manager");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	String name = element.getChild("name").getText();
            	String theclass = element.getChild("class").getText();
            	ajaxManagerMap.put(name, Class.forName(theclass).newInstance()); 
            }    
            res.setAjaxManagerMap(ajaxManagerMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }
    
    /**
     * 加载Manager信息
     */    
    private void loadManagerXml()
    {        
        Hashtable managerMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/manager.xml"));          
            List list = doc.getRootElement().getChildren("manager");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	managerMap.put(element.getChild("name").getText(), 
            			Class.forName(element.getChild("class").getText()).newInstance()); 
            }    
            res.setManagerMap(managerMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadManagerXml()",e);
		}
    }

    /**
     * 加载Action信息
     */
    private void loadActionXml()
    {
        Hashtable actionMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/action.xml"));          
            List list = doc.getRootElement().getChildren("action");
            Iterator it = list.iterator();
                        
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	actionMap.put(element.getAttributeValue("tag"),
            		new Integer(element.getAttributeValue("operate"))); 
            }    
            res.setActionMap(actionMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadActionXml()",e);
		}
    } 
    
    /**
     * 加载服务信息
     */    
    private void loadService()
    {
 	    ServiceDao dao = new ServiceDao();
 	    try{
 	    	res.setServiceList(dao.loadService(1));
 	    }catch(Exception e){
 	    	
 	    }finally{
 	    	dao.close();
 	    }
    }
    
    private void deleteAllAlarm()
    {
    	AlarmDao dao = new AlarmDao();
    	try{
    		dao.deleteAll();
    	}catch(Exception e){
    		
    	}finally{
    		dao.close();
    	}
    } 
    
    private void loadMenuXml()
    {        
        Hashtable menuMap = new Hashtable();
        try
		{        	
            Document doc = builder.build(new File(res.getSysPath() + "WEB-INF/classes/menu.xml"));          
            List list = doc.getRootElement().getChildren("menu");
            Iterator it = list.iterator();
            while(it.hasNext())
            {
            	Element element = (Element)it.next();
            	menuMap.put(element.getChild("filename").getText(), 
            			element.getChild("menuId").getText()); 
            }
            res.setMenuMap(menuMap);
		}
        catch(Exception e)
		{
        	SysLogger.error("SysInitializtion.loadMenuXml()",e);
		}
    }
}
