package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.Flashcollectdata;
import com.gatherdb.GathersqlListManager;

public class NetflashResultTosql {
	
	
	
	/**
	 * 
	 * �Ѳɼ���������sql������ڴ��б���
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
		
		
	if(ipdata.containsKey("flash")){
		//����
		
		Vector flashVector = (Vector) ipdata.get("flash");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		if (flashVector != null && flashVector.size() > 0) {
			Flashcollectdata flashdata = (Flashcollectdata) flashVector.elementAt(0);
			if (flashdata.getRestype().equals("dynamic")) {
				Calendar tempCal = (Calendar) flashdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "flash" + allipstr;
				long count = 0;
				if(flashdata.getCount() != null){
					count = flashdata.getCount();
				}
				StringBuffer sBuffer = new StringBuffer(200);
				sBuffer.append("insert into ");
				sBuffer.append(tablename);
				sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
				sBuffer.append("values('");
				sBuffer.append(ip);
				sBuffer.append("','");
				sBuffer.append(flashdata.getRestype());
				sBuffer.append("','");
				sBuffer.append(flashdata.getCategory());
				sBuffer.append("','");
				sBuffer.append(flashdata.getEntity());
				sBuffer.append("','");
				sBuffer.append(flashdata.getSubentity());
				sBuffer.append("','");
				sBuffer.append(flashdata.getUnit());
				sBuffer.append("','");
				sBuffer.append(flashdata.getChname());
				sBuffer.append("','");
				sBuffer.append(flashdata.getBak());
				sBuffer.append("','");
				sBuffer.append(count);
				sBuffer.append("','");
				sBuffer.append(flashdata.getThevalue());
				sBuffer.append("','");
				sBuffer.append(time);
				sBuffer.append("')");
				//System.out.println(sql);
				
				GathersqlListManager.Addsql(sBuffer.toString());
				sBuffer = null;
				//time=null;
				//cc=null;
				//tempCal=null;
				
				
			}
			
			flashdata=null;
		}
		flashVector = null;
		
	}
	
	}
	
	
	

}
