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
import com.afunms.polling.om.IpRouter;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempRouterRtosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RouterSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public RouterSnmp() {
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
		Vector iprouterVector=new Vector();
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
			  //---------------------------------------------------从ip router table中得到与该设备相连的路由器 start
			     try
			     {
			        String[] oids = new String[]
							                    {"1.3.6.1.2.1.4.21.1.2",       //0.if index
							                	 "1.3.6.1.2.1.4.21.1.1",       //1.ipRouterDest        		       
							                     "1.3.6.1.2.1.4.21.1.7",       //7.ipRouterNextHop
							                     "1.3.6.1.2.1.4.21.1.8",       //8.ipRouterType
							                     "1.3.6.1.2.1.4.21.1.9",       //9.ipRouterProto
							                     "1.3.6.1.2.1.4.21.1.11"};     //11.ipRouterMask
			        
					String[][] valueArray = null;   	  
					try {
						valueArray = SnmpUtils.getTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000*30);
					} catch(Exception e){
						valueArray = null;
						e.printStackTrace();
						//SysLogger.error(node.getIpAddress() + "_CiscoSnmp");
					}
			        
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		  if(valueArray[i][1] == null)continue;
							String _vbString = valueArray[i][1].toString();
							if (_vbString!=null){
								if (_vbString.equals("0.0.0.0")||_vbString.startsWith("127.0"))continue;
							}
							_vbString = valueArray[i][5].toString();
							if (_vbString!=null){
								if(_vbString.equals("0.0.0.0"))continue;
							}
							IpRouter iprouter = new IpRouter();
							iprouter.setRelateipaddr(node.getIpAddress());
							for(int j=0;j<6;j++){
								if(valueArray[i][j]!=null){
									String sValue=valueArray[i][j].toString();								
									if(j==0){
										iprouter.setIfindex(sValue);
									}else if (j==1){
										iprouter.setDest(sValue);
									}else if (j==2){
										iprouter.setNexthop(sValue);
									}else if (j==3){
										iprouter.setType(new Long(sValue));
									}else if (j==4){
										iprouter.setProto(new Long(sValue));
									}else if (j==5){
										iprouter.setMask(sValue);
									}
								}
							}
							iprouter.setCollecttime(new GregorianCalendar());
							iprouterVector.addElement(iprouter);
				   	  }	
			    }
			    catch (Exception e)
			    {
			    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
			        //e.printStackTrace();
			    }			    		  
			    //---------------------------------------------------从ip router table中得到与该设备相连的路由器 end	
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("iprouter",iprouterVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("iprouter", iprouterVector);
//		ShareData.setIprouterdata(node.getIpAddress(), iprouterVector);
	    if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (iprouterVector != null && iprouterVector.size() > 0) ipAllData.put("iprouter", iprouterVector);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (iprouterVector != null && iprouterVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("iprouter", iprouterVector);
		}
	    returnHash.put("iprouter",iprouterVector);		
	    ShareData.setIprouterdata(node.getIpAddress(), iprouterVector);
//	    returnHash.put("outpacks",outpksVector);
//	    ipAllData=null;
	    iprouterVector=null;
	    //把采集结果生成sql
	    NetDatatempRouterRtosql temptosql=new NetDatatempRouterRtosql();
	    temptosql.CreateResultTosql(returnHash, node);
	    return returnHash;
	}
}