package com.afunms.polling.snmp.upsoutput;

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
public class EmsOutputSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public EmsOutputSnmp() {   
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
		Vector outputVector=new Vector();
		UPSNode node = (UPSNode)PollingEngine.getInstance().getUpsByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		if(node == null)return null;
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		MgeUps mgeUps = null;
		try{
			mgeUps = (MgeUps)mgeUpsDao.findByID(node.getId()+"");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mgeUpsDao.close();
		}
		Systemcollectdata systemdata=null;
		Calendar date=Calendar.getInstance();
		try{
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getUpsByIP(node.getIpAddress());
			Date cc = date.getTime();
			String time = sdf.format(cc);
			snmpnode.setLastTime(time);
		}catch(Exception e){
			  e.printStackTrace();
		}
		try{
		    final String[] desc=SnmpMibConstants.UpsMibOutputDesc;
			final String[] chname=SnmpMibConstants.UpsMibOutputChname;
			final String[] unit=SnmpMibConstants.UpsMibOutputUnit;
			String[] oids = new String[] {
					"1.3.6.1.4.1.13400.2.5.2.4.1.0",
					"1.3.6.1.4.1.13400.2.5.2.4.2.0",
					"1.3.6.1.4.1.13400.2.5.2.4.3.0",
					"1.3.6.1.4.1.13400.2.5.2.4.4.0",
					"1.3.6.1.4.1.13400.2.5.2.4.5.0",
					"1.3.6.1.4.1.13400.2.5.2.4.6.0",
					"1.3.6.1.4.1.13400.2.5.2.4.7.0",
					"1.3.6.1.4.1.13400.2.5.2.4.8.0",
					"1.3.6.1.4.1.13400.2.5.2.4.9.0",
					"1.3.6.1.4.1.13400.2.5.2.4.10.0",
					"1.3.6.1.4.1.13400.2.5.2.4.11.0",
					"1.3.6.1.4.1.13400.2.5.2.4.12.0",
					"1.3.6.1.4.1.13400.2.5.2.4.13.0",
					"1.3.6.1.4.1.13400.2.5.2.4.14.0",
					"1.3.6.1.4.1.13400.2.5.2.4.15.0",
					"1.3.6.1.4.1.13400.2.5.2.4.16.0",
					"1.3.6.1.4.1.13400.2.5.2.4.17.0",
					"1.3.6.1.4.1.13400.2.5.2.4.18.0",
					"1.3.6.1.4.1.13400.2.5.2.4.19.0",
					"1.3.6.1.4.1.13400.2.5.2.4.20.0",
					"1.3.6.1.4.1.13400.2.5.2.4.21.0",
					"1.3.6.1.4.1.13400.2.5.2.4.22.0",
					"1.3.6.1.4.1.13400.2.5.2.4.23.0",
					"1.3.6.1.4.1.13400.2.5.2.4.24.0",
					"1.3.6.1.4.1.13400.2.5.2.4.25.0"
					};
					  
			String[] valueArray = new String[25];  		  
			for(int j=0;j<oids.length;j++){
				try {
					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
				} catch(Exception e){
					valueArray = null;
					e.printStackTrace();
				}
			}
			if(mgeUps.getSysOid().startsWith("1.3.6.1.4.1.2021.250.10")){//AdapterPM150��UPS
				/**
				 * 	public static final String[] UpsMibOutputChname={"����ߵ�ѹA","����ߵ�ѹB","����ߵ�ѹC",
				 * "A���������","B���������","C���������",
				 * "���Ƶ��",
				 * "A�������������","B�������������","C�������������",
       			 * 	"A������й�����","B������й�����","C������й�����",
       			 * 	"A��������ڹ���","B��������ڹ���","C��������ڹ���",
        		 * 	"A������޹�����","B������޹�����","C������޹�����",
         		 * 	"A��������ذٷֱ�","B��������ذٷֱ�","C��������ذٷֱ�",
				 * 	"A�������ֵ��","B�������ֵ��","C�������ֵ��"};
				 * 
				 */
				oids = new String[] {
						"1.3.6.1.4.1.13400.2.20.2.4.16.0",//����ߵ�ѹA
						"1.3.6.1.4.1.13400.2.20.2.4.17.0",
						"1.3.6.1.4.1.13400.2.20.2.4.18.0",
						"1.3.6.1.4.1.13400.2.20.2.4.19.0",//A���������
						"1.3.6.1.4.1.13400.2.20.2.4.20.0",
						"1.3.6.1.4.1.13400.2.20.2.4.21.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.22.0",//���Ƶ��
						"1.3.6.1.4.1.13400.2.20.2.4.23.0",//A�������������
						"1.3.6.1.4.1.13400.2.20.2.4.24.0",
						"1.3.6.1.4.1.13400.2.20.2.4.25.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.26.0",//A������й�����
						"1.3.6.1.4.1.13400.2.20.2.4.27.0",
						"1.3.6.1.4.1.13400.2.20.2.4.28.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.32.0",//A��������ڹ���
						"1.3.6.1.4.1.13400.2.20.2.4.33.0",
						"1.3.6.1.4.1.13400.2.20.2.4.34.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.29.0",//A������޹�����
						"1.3.6.1.4.1.13400.2.20.2.4.30.0",
						"1.3.6.1.4.1.13400.2.20.2.4.31.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.35.0",//A��������ذٷֱ�
						"1.3.6.1.4.1.13400.2.20.2.4.36.0",
						"1.3.6.1.4.1.13400.2.20.2.4.37.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.38.0",//A�������ֵ��
						"1.3.6.1.4.1.13400.2.20.2.4.39.0",
						"1.3.6.1.4.1.13400.2.20.2.4.40.0"
						};
				for(int j=0;j<oids.length;j++){
					try {
						valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
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
					systemdata.setCategory("Output");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
//					System.out.println("EmsOutputSnmp:value====="+value);
					if(value!=null && !value.equals("noSuchObject")){
						systemdata.setThevalue((Float.parseFloat(value)/100.0)+"");
					} else {
						systemdata.setThevalue("0");
					}
					outputVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("output",outputVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("output", outputVector);
		
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (outputVector != null && outputVector.size() > 0) ipAllData.put("output", outputVector);
				ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else{
			if (outputVector != null && outputVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("output", outputVector);
		}	
		returnHash.put("output",outputVector);
		
		
		
		
	    return returnHash;
	}
}





