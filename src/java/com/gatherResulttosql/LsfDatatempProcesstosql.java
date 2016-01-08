package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.gatherdb.GathersqlListManager;

public class LsfDatatempProcesstosql {

	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *           LSF 采集结果
	 *           
	 */
	public  void CreateResultTosql(Hashtable dataresult) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		String classid = "", nodeid = "", logcoud = "", master = "", alarm = "";
		String deleteSql = "delete from lsf_data_temp where nodeid='"
				+ dataresult.get("nodeid") + "'";
		StringBuffer sql = new StringBuffer();
		Vector list = new Vector();
		sql
				.append("insert into lsf_data_temp(classid,nodeid,logcoud,master,alarm,collecttime)values(");
		if (dataresult.get("classid") != null) {
			classid = dataresult.get("classid") + "";
			sql.append(classid);
		}
		sql.append(",");
		if (dataresult.get("nodeid") != null) {
			nodeid = dataresult.get("nodeid") + "";
			sql.append(nodeid);
		}
		sql.append(",");
		if (dataresult.get("logcoud") != null) {
			logcoud = dataresult.get("logcoud") + "";
			sql.append(logcoud);
		}
		sql.append(",");
		if (dataresult.get("master") != null) {
			master = dataresult.get("master") + "";
			sql.append(master);
		}
		sql.append(",");
		if (dataresult.get("alarm") != null) {
			alarm = dataresult.get("alarm") + "";
			sql.append(alarm);
		}
		sql.append(",'");
		sql.append(time);
		sql.append("')");
		list.add(sql.toString());
		//System.out.println("&&&&&#####--LSF数据采集入库--#####&&&&&"+sql);
		sql = null;
		GathersqlListManager.AdddateTempsql(deleteSql, list);
		list = null;
	}
}
