package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.Pingcollectdata;
import com.gatherdb.GathersqlListManager;

public class HostnetPingResultTosql {
	
	
	/**
	 * 
	 *���ݲɼ�������ɶ�Ӧ��sql���뵽�ڴ��б���
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
	
		if(ipdata.containsKey("ping")){
			//ping
			String allipstr = SysUtil.doip(ip);
			Vector pingVector=null;
			Pingcollectdata pingdata=null;
			//Hashtable pinghash = (Hashtable)ipdata.get("ping");
			pingVector = (Vector) ipdata.get("ping");	
			//pinghash = null;
			if (pingVector != null && pingVector.size() > 0) {
				for(int i=0;i<pingVector.size();i++){
					pingdata = (Pingcollectdata) pingVector.elementAt(i);
					if (pingdata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) pingdata.getCollecttime();
						Date cc = tempCal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = sdf.format(cc);
						String tablename = "ping" + allipstr;
						String sequenceName = tablename+"SEQ";
						String sql = "upsert  into " + tablename
						+ "(id,ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values(next value for "+sequenceName+",'"  + ip + "','" + pingdata.getRestype() + "','" + pingdata.getCategory() + "','"
						+ pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit() + "','"
						+ pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'"
						+ pingdata.getThevalue() + "','" + time + "')";
						//System.out.println(sql);
						GathersqlListManager.Addsql(sql);
						sql=null;
						//tablename=null;
						///time=null;
						//sdf=null;
						//tempCal=null;
					}
					pingdata = null;
				}
				
			}
			pingVector = null;
		}
		
	}
	

	

}