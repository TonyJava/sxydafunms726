package com.afunms.polling.snmp.battery;

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
import com.afunms.polling.node.UPSNode;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;
import com.afunms.topology.model.HostNode;


/**   
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  
public class EmsBatterySnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public EmsBatterySnmp() {        
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
		Vector batteryVector=new Vector();
		UPSNode node = (UPSNode)PollingEngine.getInstance().getUpsByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		MgeUps mgeUps = null;
		try{
			mgeUps = (MgeUps)mgeUpsDao.findByID(node.getId()+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mgeUpsDao.close();
		}
		
		if(node == null)return null;
		Systemcollectdata systemdata=null;
		Calendar date=Calendar.getInstance();  
		try {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getUpsByIP(node.getIpAddress());
			Date cc = date.getTime();
			String time = sdf.format(cc);
			snmpnode.setLastTime(time);
		} catch (Exception e) {
			  e.printStackTrace();
		}
		try {
		    final String[] desc=SnmpMibConstants.UpsMibBatteryDesc;
			final String[] chname=SnmpMibConstants.UpsMibBatteryChname;
			final String[] unit=SnmpMibConstants.UpsMibBatteryUnit;
			String[] oids = new String[] {
					"1.3.6.1.4.1.13400.2.5.2.5.1.0",//电池电压
					"1.3.6.1.4.1.13400.2.5.2.5.2.0",//电池电流
					"1.3.6.1.4.1.13400.2.5.2.5.3.0",//电池剩余后备时间
					"1.3.6.1.4.1.13400.2.5.2.5.4.0",//电池温度
					"1.3.6.1.4.1.13400.2.5.2.1.2.0",//环境温度
					"1.3.6.1.4.1.13400.2.5.1.1.0",//设备描述
					"1.3.6.1.4.1.13400.2.5.1.2.0"//设备名称
					};//UPS电池信息
					  
			String[] valueArray = new String[7];   	   
			for(int j=0;j<oids.length;j++){
				try {
					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
//					SysLogger.info("valueArray[j]=============="+valueArray[j]);
				} catch(Exception e){
					valueArray = null;
					e.printStackTrace();
				}
			}
			if(mgeUps.getSysOid().startsWith("1.3.6.1.4.1.2021.250.10")){//AdapterPM150型UPS
				oids = new String[] {
						"1.3.6.1.4.1.13400.2.20.2.4.45.0",//电池电压    
						"1.3.6.1.4.1.13400.2.20.2.4.46.0",//电池电流
						"1.3.6.1.4.1.13400.2.20.2.4.50.0",//电池剩余后备时间 
						"1.3.6.1.4.1.13400.2.20.2.4.51.0",//电池温度
						"1.3.6.1.4.1.13400.2.20.2.4.52.0",//环境温度
						"1.3.6.1.2.1.1.5.0",//设备描述  
						"1.3.6.1.4.1.13400.2.20.1.2.0"//设备名称  
						};//UPS电池信息
						  
				for(int j=0;j<oids.length;j++){
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
//						SysLogger.info("valueArray[j]=============="+valueArray[j]);
					} catch(Exception e){
						valueArray = null;
						e.printStackTrace();
					}
				}
			}
			if(valueArray != null&&valueArray.length>0){
			    for(int i=0;i<valueArray.length;i++) {
			    	systemdata=new Systemcollectdata();
					systemdata.setIpaddress(node.getIpAddress());
					systemdata.setCollecttime(date);
					systemdata.setCategory("Battery");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("static");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
					SysLogger.info("******************EmsBatterySnmp:value====="+value);
					if(i==5||i==6){
						systemdata.setThevalue(value);
					} else {
						if(value!=null && !value.equals("noSuchObject")){
							systemdata.setThevalue((Float.parseFloat(value)/100)+"");
						} else {
							systemdata.setThevalue("");
						}
					}
					batteryVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		ipAllData.put("battery",batteryVector);
	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
	    returnHash.put("battery", batteryVector);
	    return returnHash;
	}
}





