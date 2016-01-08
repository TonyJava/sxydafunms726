package com.afunms.polling.snmp.system;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SystemSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public SystemSnmp() {
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
		Vector systemVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		try {
			Systemcollectdata systemdata=null;
			Calendar date=Calendar.getInstance();
//			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------system start			
			  try{
					//final String[] desc={"sysDescr","sysUpTime","sysContact","sysName","sysLocation","sysServices","MacAddr"};
					//final String[] chname={"描述","运行时间","联系人","设备名称","设备位置","服务类型","MacAddr"};				
				final String[] desc=SnmpMibConstants.NetWorkMibSystemDesc;
				final String[] chname=SnmpMibConstants.NetWorkMibSystemChname;
						  String[] oids =                
							  new String[] {               
								"1.3.6.1.2.1.1.1" ,
								"1.3.6.1.2.1.1.3" ,
								"1.3.6.1.2.1.1.4" ,
								"1.3.6.1.2.1.1.5" ,
								"1.3.6.1.2.1.1.6" ,
								"1.3.6.1.2.1.1.7"
								//"1.3.6.1.2.1.2.2.1.6"
								  };
						  
				String[][] valueArray = null;   	  
				try {
					valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
				} catch(Exception e){
					valueArray = null;
					//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
				}
				if(valueArray != null){
			   	  for(int i=0;i<valueArray.length;i++)
			   	  {
			   		 for(int j=0;j<6;j++){
			   			systemdata=new Systemcollectdata();
						systemdata.setIpaddress(node.getIpAddress());
						systemdata.setCollecttime(date);
						systemdata.setCategory("System");
						systemdata.setEntity(desc[i]);
						systemdata.setSubentity(desc[j]);
						systemdata.setChname(chname[j]);
						systemdata.setRestype("static");
						systemdata.setUnit("");
						String value = valueArray[i][j];
						//SysLogger.info(value+"=================");
						if (j==0){
							//if (value.length()>100 && value.split(":").length>5){
								systemdata.setThevalue(value);
							//}
						}else
							systemdata.setThevalue(value);
						systemVector.addElement(systemdata);
			   		 }
			   	  }
				}
			  }
			  catch(Exception e){
				  //e.printStackTrace();
			}
			  //-------------------------------------------------------------------------------------------system end
			  
			  
			//-------------------------------------------------------------------------------------------mac start			
//				try{
//				
//				  String[] oids =                
//					  new String[] {                
//						  "1.3.6.1.2.1.2.2.1.6"
//						  };
//				  String[][] valueArray = null;
//					try {
//						valueArray = snmp.getTableData(node.getIpAddress(),node.getCommunity(),oids);
//					} catch(Exception e){
//						valueArray = null;
//						//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
//					}
//					systemdata=new Systemcollectdata();
//					systemdata.setIpaddress(node.getIpAddress());
//					systemdata.setCollecttime(date);
//					systemdata.setCategory("System");
//					systemdata.setEntity("MacAddr");
//					systemdata.setSubentity("MacAddr");
//					systemdata.setRestype("static");
//					systemdata.setUnit(" ");
//					if(valueArray != null){
//						for(int i=0;i<valueArray.length;i++){
//							String value=valueArray[i][0];
//							if (value == null || value.length()==0)continue;
//							systemdata.setThevalue(value);
//							break;
//						}
//					}				
//					systemVector.addElement(systemdata);
//		
//				}
//				catch(Exception e){
//					//e.printStackTrace();
//				}
				//-------------------------------------------------------------------------------------------mac end	
			  
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("system",systemVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("system", systemVector);
	    
			if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
				Hashtable ipAllData = new Hashtable();
				if (ipAllData == null) ipAllData = new Hashtable();
				if (systemVector != null && systemVector.size() > 0) ipAllData.put("system", systemVector);
					ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			} else{
				if (systemVector != null && systemVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("system", systemVector);
			}	
			returnHash.put("system",systemVector);			
			
			
			
//	    ipAllData=null;
	    systemVector=null;
	    
	    NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
	    tosql.CreateResultTosql(returnHash, node);
	    
	    
	    return returnHash;
	}
}





