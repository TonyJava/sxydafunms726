package com.afunms.polling.snmp.fan;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetDatatempfanRtosql;
import com.gatherResulttosql.NetfanResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MotorolaFanSnmp extends SnmpMonitor {
	//private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public MotorolaFanSnmp() {
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
		Vector fanVector=new Vector();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		try {
			Interfacecollectdata interfacedata = new Interfacecollectdata();
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
			  try {
				  //-------------------------------------------------------------------------------------------风扇 start
		   		  String temp = "0";
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.2011.") || node.getSysOid().startsWith("1.3.6.1.4.1.25506.")){
			   			String[][] valueArray = null;
			   			String[] oids =                
							  new String[] {               
								"1.3.6.1.4.1.2011.2.23.1.9.1.1.1.1",//hwDevMFanNum
								"1.3.6.1.4.1.2011.2.23.1.9.1.1.1.2"//hwDevMFanStatus
			   			};
			   			valueArray = SnmpUtils.getTemperatureTableData(node.getIpAddress(), node.getCommunity(), oids, node.getSnmpversion(), 3, 1000);
			   			int flag = 0;
						if(valueArray != null){
						   	  for(int i=0;i<valueArray.length;i++)
						   	  {
						   		String _value = valueArray[i][1];
						   		String index = valueArray[i][2];
						   		String num = valueArray[i][0];
						   		int value=0;
						   		value=Integer.parseInt(_value);
								//if(value > 0){
									flag = flag +1;
							   		List alist = new ArrayList();
							   		alist.add(index);
							   		alist.add(_value);
							   		alist.add(num);
							   		//风扇
							   		//fanList.add(alist);				   		
							   		  interfacedata = new Interfacecollectdata();
							   		  interfacedata.setIpaddress(node.getIpAddress());
							   		  interfacedata.setCollecttime(date);
							   		  interfacedata.setCategory("Fan");
							   		  interfacedata.setEntity(index);
							   		  interfacedata.setSubentity(num);
							   		  interfacedata.setRestype("dynamic");
							   		  interfacedata.setUnit("");		
							   		  interfacedata.setThevalue(_value);
									  //SysLogger.info(host.getIpAddress()+" 索引："+index+" 风扇状态： "+_value);
									  fanVector.addElement(interfacedata);		   		
								//}
						   		//SysLogger.info(host.getIpAddress()+"  "+index+"   value="+value);
						   	  }
						}	   			  
		   		  } 
				  //fanVector.add(fanList);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
		   	  //-------------------------------------------------------------------------------------------风扇 end
			}catch(Exception e){
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("fan",fanVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("fan", fanVector);
			if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
				Hashtable ipAllData = new Hashtable();
				if (ipAllData == null) ipAllData = new Hashtable();
				if (fanVector != null && fanVector.size() > 0) ipAllData.put("fan", fanVector);
				ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
			} else {
				if (fanVector != null && fanVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("fan", fanVector);
			}
			returnHash.put("fan", fanVector);
	    fanVector=null;
//	    ipAllData=null;
	    
	    //把采集结果生成sql
	    NetfanResultTosql tosql=new NetfanResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    NetDatatempfanRtosql totempsql=new NetDatatempfanRtosql();
	    totempsql.CreateResultTosql(returnHash, node);
	    
	    
	    return returnHash;
	}
}





