package com.afunms.polling.snmp.memory;


/*
 * @author yangjun@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostMemoryRtsql;
import com.gatherResulttosql.NetmemoryResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NetscreenMemorySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public NetscreenMemorySnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {	
		//yangjun
		Hashtable returnHash=new Hashtable();
		Vector memoryVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		
		try {
			Memorycollectdata memorydata=null;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  try{
					
					String[] oids =                
						new String[] {               
							"1.3.6.1.4.1.3224.16.2.1",//All Memory Size
							"1.3.6.1.4.1.3224.16.2.2"}; //process Left Size
					String[] oids1 =                
						new String[] {               
							"1.3.6.1.4.1.3224.16.2.2" };
					String[][] valueArray = null; 
					int allMemorySize=0;
					float value=0.0f;
					int leftsize=0;
					int allUsedSize=0;
					
					try {
						valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
					} catch(Exception e){
						valueArray = null;
						SysLogger.error(node.getIpAddress() + "_NetscreenSnmp");
					}
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {
					   		  if(valueArray[i][0] != null && valueArray[i][1] != null){
					   			allMemorySize=Integer.parseInt(valueArray[i][0]);
						   		leftsize=Integer.parseInt(valueArray[i][1]); 
						   		break;
					   		  }
					   		 
					   	  }
					}
//					System.out.println("memory valueArray-->"+valueArray);
					if(allMemorySize!=0){
						value=(allMemorySize-leftsize)*100f/allMemorySize;
					}
					else{
						value = 0;
						//throw new Exception("allMemorySize is 0");
					}
					//##########HONGLI ADD  凤凰传媒集团juniper 防火墙
					String[] oids2 = new String[]{
						"1.3.6.1.4.1.2636.3.39.1.12.1.1.1.5"
					};
					if(valueArray == null || valueArray.length == 0){
						valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids2);
					}
					if(valueArray != null){
						for(int i=0; i<valueArray.length;i++){
							value = (Integer.parseInt(valueArray[0][0]) + Integer.parseInt(valueArray[1][0]))/2;
				   		  }
					}
					System.out.println("memory value --->"+value);
					//##########END
					memorydata=new Memorycollectdata();
					memorydata.setIpaddress(node.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Utilization");
					memorydata.setSubentity("PhysicalMemory");
					memorydata.setRestype("dynamic");
					memorydata.setUnit("%");
					memorydata.setThevalue((int)Math.rint(value/1)+"");
					memoryVector.addElement(memorydata);
					
					memorydata=new Memorycollectdata();
					memorydata.setIpaddress(node.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("Allocate");
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
					memorydata.setThevalue((int)Math.rint(size/1)+"");
					memoryVector.addElement(memorydata);
					memorydata=new Memorycollectdata();
					memorydata.setIpaddress(node.getIpAddress());
					memorydata.setCollecttime(date);
					memorydata.setCategory("Memory");
					memorydata.setEntity("UsedSize");
					memorydata.setRestype("static");
					memorydata.setSubentity("PhysicalMemory");
							size=(allMemorySize-leftsize)*1.0f/1024;						
							if(size>=1024.0f){
								size=size/1024;
								memorydata.setUnit("G");
							}
							else{
								memorydata.setUnit("M");
							}	
							memorydata.setThevalue((int)Math.rint(size/1)+"");
							memoryVector.addElement(memorydata);
					}
					catch(Exception e){
						e.printStackTrace();
					}
		   	  //-------------------------------------------------------------------------------------------内存 end	
		}
		catch(Exception e){
			//returnHash=null;
			//e.printStackTrace();
			//return null;
		}
		
//		Hashtable ipAllData = new Hashtable();
//		try{
//			ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		}catch(Exception e){
//			
//		}
//		if(ipAllData == null)ipAllData = new Hashtable();
//		if (memoryVector != null && memoryVector.size()>0)ipAllData.put("memory",memoryVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("memory",memoryVector);
	    
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (memoryVector != null && memoryVector.size() > 0) ipAllData.put("memory", memoryVector);
				ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			} 
		else{
			if (memoryVector != null && memoryVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("memory", memoryVector);
		}	
	    returnHash.put("memory",memoryVector);
	    
	    
	    
//	    ipAllData=null;
	    memoryVector=null;
	    
	    //把采集结果生成sql
	    NetmemoryResultTosql tosql=new NetmemoryResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
	    totempsql.CreateResultTosql(returnHash, node);
	    
	    return returnHash;
	}
}





