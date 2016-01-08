package com.afunms.polling.task;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.SlaNodeConfigDao;
import com.afunms.application.model.SlaNodeConfig;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Task;
import com.afunms.topology.dao.RemotePingHostDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.RemotePingHost;

public class M5SLATelnetTask extends MonitorTask 
{
	
	public M5SLATelnetTask()
	{
		super();
	}
	
	public void run()
	{
		try{
			SlaNodeConfigDao configdao = new SlaNodeConfigDao();
			//得到被监视的SLA列表
			List nodeList = new ArrayList();
			Hashtable nodeHash = new Hashtable();
			
	    	try{
	    		nodeList = configdao.getConfigByIntervalAndUnitAndFlag(5,"m",1);	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		configdao.close();
	    	}
	    	SysLogger.info("nodeList size======="+nodeList.size());
	    	if(nodeList != null)
	    	{
		    	for(int i=0;i<nodeList.size();i++)
		    	{    		
		    		SlaNodeConfig nodeconfig = (SlaNodeConfig)nodeList.get(i);	
		    		if(nodeHash.containsKey(nodeconfig.getTelnetconfig_id()+"")){
		    			List entrylist = (List)nodeHash.get(nodeconfig.getTelnetconfig_id()+"");
		    			entrylist.add(nodeconfig);
		    			nodeHash.put(nodeconfig.getTelnetconfig_id()+"", entrylist);
		    		}else{
		    			List entrylist = new ArrayList();
		    			entrylist.add(nodeconfig);
		    			nodeHash.put(nodeconfig.getTelnetconfig_id()+"", entrylist);
		    		}
		    	}
	    	}
    	
    	
    	
    	if(nodeList != null && nodeList.size() > 0){
    		
    		//System.out.println("there have " + nodeList.size() + " node to collect by telnet");
    		
    		int numTasks = nodeList.size();
    		int numThreads = 200;
    		
    		try {
    			List numList = new ArrayList();
    			TaskXml taskxml = new TaskXml();
    			numList = taskxml.ListXml();
    			for (int i = 0; i < numList.size(); i++) {
    				Task task = new Task();
    				BeanUtils.copyProperties(task, numList.get(i));
    				if (task.getTaskname().equals("netthreadnum")){
    					numThreads = task.getPolltime().intValue();
    				}
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		

//    		// 生成线程池
//    		ThreadPool threadPool = null;	
//    		if(nodeList != null && nodeList.size()>0){
//    			threadPool = new ThreadPool(nodeList.size());	
//    			// 运行任务
//        		for (int i=0; i<numTasks; i++) {
//        			HostNode node = (HostNode)nodeList.get(i);
//            		threadPool.runTask(createTask(node));
//        		}
//        		// 关闭线程池并等待所有任务完成
//        		threadPool.join();
//        		threadPool.close();
//        		threadPool = null;
//    		}
    		
    		
    		// 生成线程池,数目依据被监视的设备多少
    		ThreadPool threadPool = null;	
    		final Hashtable alldata = new Hashtable();
    		// 运行任务
    		if(nodeHash != null && nodeHash.size()>0){
//    			Hashtable paramhash = new Hashtable();
//    			RemotePingHostDao hostDao = new RemotePingHostDao(); 
//    			List paramlist = null;
//    			try{
//    				paramlist = hostDao.loadAll(); 
//    			}catch(Exception e){
//    				
//    			}finally{
//    				hostDao.close();
//    			}
//    			if(paramlist != null && paramlist.size()>0){
//    				for(int i=0;i<paramlist.size();i++){
//    					RemotePingHost params = (RemotePingHost)paramlist.get(i);
//    					paramhash.put(params.getNode_id(), params);
//    				}
//    				
//    			}
//    			if(paramhash != null && paramhash.size()>0)ShareData.setParamsHash(paramhash);
//    			
    			threadPool = new ThreadPool(nodeHash.size());	
    			Enumeration newProEnu = nodeHash.keys();
    			while(newProEnu.hasMoreElements())
    			{
    				String telnetconfig_id = (String)newProEnu.nextElement();
    				List nodelist = (List)nodeHash.get(telnetconfig_id);
 		    	   HaweitelnetconfDao haweitelnetconfDao = new HaweitelnetconfDao();
		    	   Huaweitelnetconf telconf = new Huaweitelnetconf();
		    	   try{
		    		   telconf = (Huaweitelnetconf)haweitelnetconfDao.findByID(telnetconfig_id);
		    	   }catch(Exception e){
		    		   e.printStackTrace();
		    	   }finally{
		    		   haweitelnetconfDao.close();
		    	   }
    				
    				//List dolist = (List)newProEnu.nextElement();;
    				threadPool.runTask(createTask(telconf,nodelist,alldata));
    	
    			}
    			// 关闭线程池并等待所有任务完成
        		threadPool.join();             		
        		threadPool.close();
        		HostCollectDataManager hostdataManager=new HostCollectDataManager(); 
        		try{
        			SysLogger.info("alldata size============"+alldata.size());
        			hostdataManager.createSLAData(alldata); 
        		}catch(Exception e){
        			
        		}
        		hostdataManager = null;
        		alldata.clear();
    		}
    		threadPool = null;
    		  		        		
										
    	}
		}catch(Exception e){					 	
			e.printStackTrace();
		}finally{
			SysLogger.info("********M5SLATelnet Thread Count : "+Thread.activeCount());
		}
	}
	
	
	
	
	 /**
    创建任务
*/	
private static Runnable createTask(final Huaweitelnetconf telconf,final List nodelist,final Hashtable alldata) {
    return new Runnable() {
        public void run() {
            try {  
    	    	SLATelnetDataCollector telnetdatacollector = new SLATelnetDataCollector();
    	    	try{
    	    		if(nodelist.size()>0){
    	    			alldata.put(telconf.getId()+"", telnetdatacollector.collect_data(telconf, nodelist));
    	    		}
    	    	}catch(Exception e){
    	    		//e.printStackTrace();
    	    	}

            }catch(Exception exc){
            	
            }
            
        }
    };
}
	
	
	
	
	

}
