package com.afunms.polling.snmp.memory;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsPhysicalMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public WindowsPhysicalMemorySnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector memoryVector=new Vector();
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Memorycollectdata memorydata=new Memorycollectdata();
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------memory start			
				try{
				
				String[] oids =                
					new String[] {               
						"1.3.6.1.2.1.25.5.1.1.2" };
				String[] oids1 =                
					new String[] {               
						"1.3.6.1.2.1.25.2.2" };
				
				String[][] valueArray = null;
				try {
					valueArray = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids, host.getSnmpversion(), 3, 1000*30);
				} catch(Exception e){
					valueArray = null;
					//SysLogger.error(host.getIpAddress() + "_WindowsMemorySnmp");
				}
				
				String[][] valueArray1 = null;
				try {
					valueArray1 = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids1, host.getSnmpversion(), 3, 1000);
				} catch(Exception e){
					valueArray1 = null;
					//SysLogger.error(host.getIpAddress() + "_WindowsMemorySnmp");
				}

				int allMemorySize=0;
				if(valueArray1 != null){
					for(int i=0;i<valueArray1.length;i++){
						if(valueArray1[i][0]==null)continue;
						allMemorySize = Integer.parseInt(valueArray1[i][0]);
					}
				}
				//System.out.println("list "+list.size());
				float value=0.0f;
				int allUsedSize=0;
				if(valueArray != null){
					for(int i=0;i<valueArray.length;i++){
						if(valueArray[i][0] == null)continue;
						int processUsedSize = Integer.parseInt(valueArray[i][0]);
						allUsedSize=allUsedSize+processUsedSize;
					}
				}
				if(allMemorySize!=0){
					value=allUsedSize*100f/allMemorySize;
				}
				else{
					throw new Exception("allMemorySize is 0");
				}
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(host.getIpAddress());
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Utilization");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("dynamic");
				memorydata.setUnit("%");
				DecimalFormat df=new DecimalFormat("#.##");//yangjun 
				//utilhdx.setThevalue(df.format(l*8/1000));
				memorydata.setThevalue(df.format(value));
				memoryVector.addElement(memorydata);
				
				
				
				
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(host.getIpAddress());
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Capability");
				memorydata.setRestype("static");
				memorydata.setSubentity("PhysicalMemory");
			
				float size=0.0f;
				size=allMemorySize*1.0f/1024;			
				if(size>=1024.0f){
					size=size/1024;
					memorydata.setUnit("G");
				}
				else{
					memorydata.setUnit("M");
				}				
				memorydata.setThevalue(Float.toString(size));
				memoryVector.addElement(memorydata);
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(host.getIpAddress());
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("UsedSize");
				memorydata.setRestype("static");
				memorydata.setSubentity("PhysicalMemory");
						size=allUsedSize*1.0f/1024;						
						if(size>=1024.0f){
							size=size/1024;
							memorydata.setUnit("G");
						}
						else{
							memorydata.setUnit("M");
						}	
						memorydata.setThevalue(Float.toString(size));
						memoryVector.addElement(memorydata);
				}
				catch(Exception e){
					//System.out.println(e.getMessage());
					//e.printStackTrace();
				}
				//-------------------------------------------------------------------------------------------memory end
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		Vector toAddVector = new Vector();
		Hashtable formerHash = new Hashtable();
		if(ipAllData.containsKey("memory")){
			Vector formerMemoryVector = (Vector)ipAllData.get("memory");
			if(formerMemoryVector != null && formerMemoryVector.size()>0){
				for(int i=0;i<formerMemoryVector.size();i++){
					Memorycollectdata memorydata=(Memorycollectdata)formerMemoryVector.get(i);
					formerHash.put(memorydata.getSubentity()+":"+memorydata.getEntity(), memorydata);
				}
			}
			
		}
		if(memoryVector != null && memoryVector.size()>0){
			for(int j=0;j<memoryVector.size();j++){
				Memorycollectdata memorydata=(Memorycollectdata)memoryVector.get(j);
				if(formerHash.containsKey(memorydata.getSubentity()+":"+memorydata.getEntity())){
					//若存在,则要用新的替换原来的数据
					//SysLogger.info("存在   "+memorydata.getSubentity()+":"+memorydata.getEntity());
					formerHash.remove(memorydata.getSubentity()+":"+memorydata.getEntity());
					formerHash.put(memorydata.getSubentity()+":"+memorydata.getEntity(), memorydata);
				}else{
					//若不存在,在直接加入
					//SysLogger.info("添加----windows    "+memorydata.getSubentity()+":"+memorydata.getEntity());
					toAddVector.add(memorydata);
				}
			}
		}
		if(formerHash.elements() != null && formerHash.size()>0){
			for(Enumeration enumeration = formerHash.keys(); enumeration.hasMoreElements();){
				String keys = (String)enumeration.nextElement();
				Memorycollectdata memorydata=(Memorycollectdata)formerHash.get(keys);
				//SysLogger.info("WINDOWS 添加   "+memorydata.getSubentity()+":"+memorydata.getEntity()+"----------"+memorydata.getThevalue());
				toAddVector.add(memorydata);
			}
		}
		ipAllData.put("memory",toAddVector);
	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
	    returnHash.put("memory", toAddVector);
	    
	    Hashtable collectHash = new Hashtable();
		collectHash.put("physicalmem", toAddVector);
		
		//对物理内存值进行告警检测
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "windows","physicalmemory");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//对物理内存值进行告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(host,collectHash,"host","windows",alarmIndicatorsnode);
				//}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    ipAllData=null;
	    memoryVector=null;
	    toAddVector=null;
	    
	    //把采集结果生成sql
	    HostPhysicalMemoryResulttosql  tosql=new HostPhysicalMemoryResulttosql();
	    tosql.CreateResultTosql(returnHash, host.getIpAddress());
	    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
	    totempsql.CreateResultTosql(returnHash, host,"PhysicalMemory");
	    return returnHash;
	}
}





