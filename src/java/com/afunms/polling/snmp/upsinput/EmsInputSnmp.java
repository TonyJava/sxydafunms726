package com.afunms.polling.snmp.upsinput;

/*
 * @author yangjun@dhcc.com.cn
 * ��Ĭ��UPS ������Ϣ��
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
public class EmsInputSnmp extends SnmpMonitor {
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */    
	public EmsInputSnmp() {   
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
		Vector inputVector=new Vector();
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
		    final String[] desc=SnmpMibConstants.UpsMibInputDesc;
			final String[] chname=SnmpMibConstants.UpsMibInputChname;
			final String[] unit=SnmpMibConstants.UpsMibInputUnit;
			String[] oids = new String[] {
					"1.3.6.1.4.1.13400.2.5.2.2.1.0",
					"1.3.6.1.4.1.13400.2.5.2.2.2.0",
					"1.3.6.1.4.1.13400.2.5.2.2.3.0",
					"1.3.6.1.4.1.13400.2.5.2.2.4.0",
					"1.3.6.1.4.1.13400.2.5.2.2.5.0",
					"1.3.6.1.4.1.13400.2.5.2.2.6.0",
					"1.3.6.1.4.1.13400.2.5.2.2.7.0",
					"1.3.6.1.4.1.13400.2.5.2.2.8.0",
					"1.3.6.1.4.1.13400.2.5.2.2.9.0",
					"1.3.6.1.4.1.13400.2.5.2.2.10.0",
					"1.3.6.1.4.1.13400.2.5.2.2.11.0",
					"1.3.6.1.4.1.13400.2.5.2.2.12.0",
					"1.3.6.1.4.1.13400.2.5.2.2.13.0"
					};
			String[] valueArray = new String[13];  		  
			for(int j=0;j<oids.length;j++){
				try {
					valueArray[j] = snmp.getMibValue(node.getIpAddress(),node.getCommunity(),oids[j]);
				} catch(Exception e){
					valueArray = null;
					e.printStackTrace();
				}
			}
			/*
			 * public static final String[] UpsMibInputChname={"�����ߵ�ѹAB","�����ߵ�ѹBC","�����ߵ�ѹCA",
			 * "A�������ѹ","B�������ѹ","C�������ѹ",
			 * "A���������","B���������","C���������",
			 * "����Ƶ��",
			 * "���빦������A","���빦������B","���빦������C"};
			 */
			if(mgeUps.getSysOid().startsWith("1.3.6.1.4.1.2021.250.10")){//AdapterPM150��UPS
				oids = new String[] {
						"1.3.6.1.4.1.13400.2.20.2.4.4.0",//�����ߵ�ѹAB
						"1.3.6.1.4.1.13400.2.20.2.4.5.0",
						"1.3.6.1.4.1.13400.2.20.2.4.6.0",
						"1.3.6.1.4.1.13400.2.20.2.4.1.0",//A�������ѹ
						"1.3.6.1.4.1.13400.2.20.2.4.2.0",
						"1.3.6.1.4.1.13400.2.20.2.4.3.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.7.0",//A���������
						"1.3.6.1.4.1.13400.2.20.2.4.8.0",
						"1.3.6.1.4.1.13400.2.20.2.4.9.0",
						
						"1.3.6.1.4.1.13400.2.20.2.4.10.0",//����Ƶ��
						"1.3.6.1.4.1.13400.2.20.2.4.11.0",//���빦������A
						"1.3.6.1.4.1.13400.2.20.2.4.12.0",
						"1.3.6.1.4.1.13400.2.20.2.4.13.0"
						};
				valueArray = new String[13];  		  
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
					systemdata.setCategory("Input");
					systemdata.setEntity(desc[i]);
					systemdata.setSubentity(desc[i]);
					systemdata.setChname(chname[i]);
					systemdata.setRestype("dynamic");
					systemdata.setUnit(unit[i]);
					String value = valueArray[i];
//					System.out.println("EmsInputSnmp:value====="+value);
					if(value!=null && !value.equals("noSuchObject")){
						systemdata.setThevalue((Float.parseFloat(value)/100)+"");
					} else {
						systemdata.setThevalue("0");
					}
					inputVector.addElement(systemdata);
			    }
		    }
		} catch(Exception e){
			  e.printStackTrace();
		}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(node.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("input",inputVector);
//	    ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
//	    returnHash.put("input", inputVector);
		if (!(ShareData.getSharedata().containsKey(node.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (inputVector != null && inputVector.size() > 0) ipAllData.put("input", inputVector);
				ShareData.getSharedata().put(node.getIpAddress(), ipAllData);
		} else{
			if (inputVector != null && inputVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(node.getIpAddress())) .put("input", inputVector);
		}	
		returnHash.put("input",inputVector);
	    return returnHash;
	}
}





