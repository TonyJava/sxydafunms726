package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.CPUcollectdata;
import com.gatherdb.GathersqlListManager;

public class HostPagingResultTosql {
	
	
	
	
	/**
	 * 
	 * 把cpu的采集数据成成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata,String ip)
	{
		
		CPUcollectdata cpudata = null;
		Vector cpuVector = null;
		//StringBuffer sBuffer = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String allipstr = SysUtil.doip(ip);
		
		
	
		Hashtable paginghash = (Hashtable) ipdata.get("paginghash");
		if (paginghash != null && paginghash.size() > 0) {
			if(paginghash.get("Percent_Used") != null){
				String pused = ((String)paginghash.get("Percent_Used")).replaceAll("%", "");
				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "pgused" + allipstr;
				String sql = "insert into " + tablename
						+ "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+ "values('" + ip + "','','','','','','','',0,'"+ pused + "','" + time + "')";
				GathersqlListManager.Addsql(sql);
				sql=null;
				tablename=null;
				time=null;
				cc=null;
			}
		}
	}

}
