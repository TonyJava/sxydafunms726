package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.Processcollectdata;
import com.gatherdb.GathersqlListManager;


/**
 * 
 * 
 * 把进程的采集信息生成sql
 * @author 
 *
 */



public class HostProcessRtosql {
	
	
	/**
	 * 
	 * 
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
		
		//System.out.println("=======================ssee========================");
		Vector proVector = null;
		StringBuffer sBuffer = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
	if(ipdata.containsKey("process")){
	
		String tablename = "pro" + allipstr;
		
		
		proVector = (Vector) ipdata.get("process");
		if (proVector != null && proVector.size() > 0) {
			
			
			//System.out.println("=====进程数据==="+ShareData.getMainprocessHashtable().toString());
			for (int i = 0; i < proVector.size(); i++) {
				Processcollectdata processdata = (Processcollectdata) proVector.elementAt(i);
				//processdata.setCount(1L);
				
				
				
				//System.out.println("==+=="+processdata.getIpaddress()+"-"+processdata.getProcessname());
				
				if (processdata.getRestype().equals("dynamic")  && ShareData.getMainprocessHashtable().containsKey(processdata.getIpaddress()+"-"+processdata.getProcessname())) {
					Calendar tempCal = (Calendar) processdata.getCollecttime();
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
				
					//System.out.println("===============pro========"+processdata.getIpaddress()+"-"+processdata.getProcessname());
					
					sBuffer = new StringBuffer(150);
					sBuffer.append("insert into ");
					sBuffer.append(tablename);
					sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
					sBuffer.append("values('");
					sBuffer.append(ip);
					sBuffer.append("','");
					sBuffer.append(processdata.getRestype());
					sBuffer.append("','");
					sBuffer.append(processdata.getCategory());
					sBuffer.append("','");
					sBuffer.append(processdata.getEntity());
					sBuffer.append("','");
					sBuffer.append(processdata.getSubentity());
					sBuffer.append("','");
					sBuffer.append(processdata.getUnit());
					sBuffer.append("','");
					sBuffer.append(processdata.getProcessname());
					sBuffer.append("','");
					sBuffer.append(processdata.getBak());
					sBuffer.append("',");
					sBuffer.append(processdata.getCount());
					sBuffer.append(",'");
					sBuffer.append(processdata.getThevalue());
					sBuffer.append("','");
					sBuffer.append(time);
					sBuffer.append("')");
				
				  
					GathersqlListManager.Addsql(sBuffer.toString());
					sBuffer = null;
					
				
				
				
				}
		
	} //end for


    }// end if
		proVector=null;
		
	}
	}
}
