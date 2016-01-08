package com.afunms.polling.snmp.cpu;

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

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetcpuResultTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MotorolaCpuSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public MotorolaCpuSnmp() {
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
		Vector cpuVector=new Vector();
		List cpuList = new ArrayList();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		
		try {
			CPUcollectdata cpudata=null;
			int result = 0;
			Calendar date=Calendar.getInstance();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			  try {
				  //-------------------------------------------------------------------------------------------cpu start
		   		  String temp = "0";
		   		  if(node.getSysOid().startsWith("1.3.6.1.4.1.388.11.1.2")){
		   			String[][] valueArray = null;
		   			String[] oids =                
						  new String[] {               
							"1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"//CPU������
		   			};
		   			String[] oids2 = new String[] {"1.3.6.1.4.1.25506.2.6.1.1.1.1.6"};
		   			valueArray = snmp.getCpuTableData(node.getIpAddress(),node.getCommunity(),oids);
		   			if(valueArray==null||valueArray.length==0){//hukelei add  
		   				valueArray = snmp.getCpuTableData(node.getIpAddress(), node.getCommunity(), oids2);
		   			}
		   			int allvalue=0;
		   			int flag = 0;
					if(valueArray != null){
					   	  for(int i=0;i<valueArray.length;i++)
					   	  {

					   		String _value = valueArray[i][0];		   		
					   		String index = valueArray[i][1];
					   		
					   		
					   		int value=0;
					   		value=Integer.parseInt(_value);
							allvalue = allvalue+Integer.parseInt(_value);
							if(value >0){
								flag = flag +1;
						   		List alist = new ArrayList();
						   		alist.add(index);
						   		alist.add(_value);
						   		cpuList.add(alist);					   		
						   		//SysLogger.info(host.getIpAddress()+" "+index+" CPU2 Value:"+_value);
							}
					   	  }
					}
					
					if(flag >0){
						int intvalue = (allvalue/flag);
						temp = intvalue+"";
						//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
					}
		   		  } 
		   		  
		   		  if(temp == null){
		   			  result = 0;
		   		  }else{
		   			  try{
		   				  if(temp.equalsIgnoreCase("noSuchObject")){
		   					result = 0;
		   				  }else
		   					  result = Integer.parseInt(temp); 
		   			  }catch(Exception ex){
		   				  ex.printStackTrace();
		   				  result = 0;
		   			  }
		   		  }
		   		  
		   		  //SysLogger.info(host.getIpAddress() + "_H3CSnmp value="+result );
				  cpudata=new CPUcollectdata();
				  cpudata.setIpaddress(node.getIpAddress());
				  cpudata.setCollecttime(date);
				  cpudata.setCategory("CPU");
				  cpudata.setEntity("Utilization");
				  cpudata.setSubentity("Utilization");
				  cpudata.setRestype("dynamic");
				  cpudata.setUnit("%");		
				  cpudata.setThevalue(result+"");
				  SysLogger.info(node.getIpAddress()+" CPU "+result+"%");
					
				  //if (cpudata != null && !cpuusage.equalsIgnoreCase("noSuchObject"))
				  cpuVector.add(0, cpudata);
				  //if(cpuList != null && cpuList.size()>0){
					  cpuVector.add(1, cpuList);
				  //}
				  //cpuVector.addElement(cpudata);
		   	  }
		   	  catch(Exception e)
		   	  {
		   	  }	   	  
//				-------------------------------------------------------------------------------------------cpu end
		}
		catch(Exception e){
			//returnHash=null;
			//e.printStackTrace();
			//return null;
		}
		
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null)
				ipAllData = new Hashtable();
			if (cpuVector != null && cpuVector.size() > 0)
				ipAllData.put("cpu", cpuVector);
			if (cpuList != null && cpuList.size() > 0)
				ipAllData.put("cpulist", cpuList);
			ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else {
			if (cpuVector != null && cpuVector.size() > 0)
				((Hashtable) ShareData.getSharedata
				().get(node.getIpAddress())).put("cpu", cpuVector);
			if (cpuList != null && cpuList.size() > 0)
				((Hashtable) ShareData.getSharedata
				().get(node.getIpAddress())).put("cpulist", cpuList);
		}
		returnHash.put("cpu", cpuVector);
	    
	    //��CPUֵ���и澯���
	    Hashtable collectHash = new Hashtable();
		collectHash.put("cpu", cpuVector);
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_NET, "h3c","cpu");
			
			for(int i = 0 ; i < list.size() ; i ++){
				 
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				SysLogger.info(alarmIndicatorsnode.getId()+"");
				//��CPUֵ���и澯���
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.updateData(node,collectHash,"net","h3c",alarmIndicatorsnode);
				//}
			}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		  //�ѽ��ת����sql
	    NetcpuResultTosql tosql=new NetcpuResultTosql();
	    tosql.CreateResultTosql(returnHash, node.getIpAddress());
	    NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
	    totempsql.CreateResultTosql(returnHash, node);
	    return returnHash;
	}
}





