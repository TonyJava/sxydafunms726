package com.afunms.polling.snmp.interfaces;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostipmacRttosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ArpSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public ArpSnmp() {
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
		Vector ipmacVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Diskcollectdata diskdata=null;
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip start
			     try
			     {
			        String[] oids = new String[]
			                    {"1.3.6.1.2.1.4.22.1.1",   //1.ifIndex
			        		     "1.3.6.1.2.1.4.22.1.2",   //2.mac
			                     "1.3.6.1.2.1.4.22.1.3",   //3.ip
			                     "1.3.6.1.2.1.4.22.1.4"};  //4.type
					String[][] valueArray = null;   	  
					try {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
					} catch(Exception e){
						valueArray = null;
						//e.printStackTrace();
						//SysLogger.error(node.getIpAddress() + "_WindowsSnmp");
					}
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		  IpMac ipmac = new IpMac();
				   		  for(int j=0;j<4;j++){
				   			String sValue = valueArray[i][j];
				   			//SysLogger.info("MAC===="+sValue);
				   			if(sValue == null)continue;
							if(j==0){
								ipmac.setIfindex(sValue);
							}else if (j==1){
								if(sValue != null && !sValue.contains(":")){//MAC地址如：00:d0:83:04:d5:97
//									SysLogger.info("ArpSnmp.java  MAC地址为乱码：" + sValue);
									sValue = "--";
								}
								ipmac.setMac(sValue);
							}else if (j==2){
								ipmac.setIpaddress(sValue);									
							}
				   		 }
				   		ipmac.setIfband("0");
				   		ipmac.setIfsms("0");
						ipmac.setCollecttime(new GregorianCalendar());
						ipmac.setRelateipaddr(node.getIpAddress());
						ipmacVector.addElement(ipmac);
						//SysLogger.info("ARP hostip==>"+host.getIpAddress()+"=="+ipmac.getMac()+"====="+ipmac.getIpaddress());
						//MACVSIP.put(ipmac.getMac(), ipmac.getIpaddress());
				   	  }	
				   	valueArray = null;
			    }catch (Exception e)
			    {
			    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
			        //tableValues = null;
			        e.printStackTrace();
			    }
			  
			    //---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip end	
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("ipmac",ipmacVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("ipmac", ipmacVector);
	    
	    if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (ipmacVector != null && ipmacVector.size() > 0) ipAllData.put("ipmac", ipmacVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (ipmacVector != null && ipmacVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("ipmac", ipmacVector);
		}
		returnHash.put("ipmac", ipmacVector);
		
	    ipmacVector=null;
//	    ipAllData=null;
	    ipmacVector=null;
	    
	    //把采集结果生成sql
	    NetHostipmacRttosql ipmactosql=new NetHostipmacRttosql();
	    ipmactosql.CreateResultTosql(returnHash, node);
	    return returnHash;
	}
}