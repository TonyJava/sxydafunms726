package com.afunms.polling.snmp.f5;

import java.util.Calendar;
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
import com.afunms.topology.model.HostNode;
import com.afunms.polling.om.F5Session;


/*
 * @author liuyuan
 */


/**
*
* @author tyxdly
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class F5SessionSnmp extends SnmpMonitor{
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public F5SessionSnmp(){
	}
	public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	   public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode){//要改为AlarmIndicatorsNode alarmIndicatorsNode
			Hashtable returnHash=new Hashtable();
			Vector powerVector=new Vector();
			Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
			if(node == null)return null;
			try {
				
				Calendar date=Calendar.getInstance();
				Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
				if(ipAllData == null)ipAllData = new Hashtable();
				  try {
					  //-------------------------------------------------------------------------------------------电源 start
			   		  String temp = "0";
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {      
			   					"1.3.6.1.4.1.89.35.1.104.1",
			   					"1.3.6.1.4.1.89.35.1.104.2",
			   					"1.3.6.1.4.1.89.35.1.104.3",
			   					"1.3.6.1.4.1.89.35.1.104.4",
			   					"1.3.6.1.4.1.89.35.1.104.5",
			   					"1.3.6.1.4.1.89.35.1.104.6",
			   					"1.3.6.1.4.1.89.35.1.104.7",
			   					"1.3.6.1.4.1.89.35.1.104.8",
			   					"1.3.6.1.4.1.89.35.1.104.9",
			   			};
			   			valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   		F5Session f5Session = new F5Session();
						   	   int id = i+1;
							 
							   String numSessions = valueArray[i][0];
							   
							   String successLogin = valueArray[i][1];
							   
							   String successLogout = valueArray[i][2];
							   
							   String failureLogin = valueArray[i][3];
							   
							   String totalBytesIn = valueArray[i][4];
							   
							   String totalBytesOut = valueArray[i][5];
							   
							   String maxActiveSessions = valueArray[i][6];
							   
							   String errorLogin = valueArray[i][7];
							   
							   String lockOutLogin = valueArray[i][8];
							   f5Session.setId(id); 
							   f5Session.setErrorLogin(Integer.parseInt(errorLogin));
							   f5Session.setFailureLogin(Integer.parseInt(failureLogin));
							   f5Session.setLockOutLogin(lockOutLogin.length());
							   f5Session.setSuccessLogout(Integer.parseInt(successLogout));
							   f5Session.setNumSessions(Integer.parseInt(numSessions));
							   f5Session.setMaxActiveSessions(Integer.parseInt(maxActiveSessions));
							   f5Session.setSuccessLogin(Integer.parseInt(successLogin));
							   f5Session.setTotalBytesIn(Long.parseLong(totalBytesIn));
							   f5Session.setTotalBytesOut(Long.parseLong(totalBytesOut));
								
							   f5Session.setCollecttime(date.getTime().toString());
							   f5Session.setIpaddress(node.getIpAddress());
							   f5Session.setType("f5");
							   f5Session.setSubtype("f5");
							  System.out.println("errorLogin:"+errorLogin+"  failureLogin:"+failureLogin
							   			+" lockOutLogin :"+lockOutLogin+" successLogout:"+successLogout 
							   			+" numSessions:"+numSessions+" maxActiveSessions:"+maxActiveSessions
							   			+" successLogin:"+successLogin+" successLogin:"+successLogin 
							   			+" totalBytesOut:"+totalBytesOut);

								
							   	powerVector.addElement(f5Session);	
						}
						}
			   	  }
			   	  catch(Exception e)
			   	  {
			   	  }	   	  
				}catch(Exception e){
				
				}
			
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			ipAllData.put("Session",powerVector);
		    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		    returnHash.put("Session", powerVector);
		    
		    return returnHash;
		}

}
