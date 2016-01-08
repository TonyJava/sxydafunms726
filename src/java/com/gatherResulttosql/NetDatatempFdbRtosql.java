package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.gatherdb.GathersqlListManager;

public class NetDatatempFdbRtosql {
	
	/**
	 * 把结果生成sql
	 * 
	 * @param dataresult
	 *            采集结果
	 * @param node
	 *            网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {
	
	if(dataresult != null && dataresult.size()>0){
		NodeDTO nodeDTO = null;
		String ip = null;
		IpMac vo = null;
		Calendar tempCal = null;
		Date cc = null;
		String time = null;
		
		Vector fdbVector = null;
    	
		SimpleDateFormat sdf = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
        NodeUtil nodeUtil = new NodeUtil();
		
 			nodeDTO = nodeUtil.creatNodeDTOByNode(node);
 			fdbVector = (Vector)dataresult.get("fdb");
			
			String deleteSql = "delete from nms_fdb_data_temp where nodeid='" +node.getId() + "'";
			
			
			if(fdbVector != null && fdbVector.size()>0){
				
				 tempCal=Calendar.getInstance();
				 time = sdf.format(tempCal.getTime());
				 Vector list=new Vector();
				for(int i=0;i<fdbVector.size();i++){
					vo = (IpMac) fdbVector.elementAt(i);
					String mac = vo.getMac();
					if(mac != null && !mac.contains(":")){// 排除mac为乱码的情况
						mac = "--";
					}
					
					    StringBuffer sql = new StringBuffer(200);
					    sql.append("insert into nms_fdb_data_temp(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak)values('");
					    sql.append(node.getId());
					    sql.append("','");
					    sql.append(node.getIpAddress());
					    sql.append("','");
					    sql.append(nodeDTO.getType());
					    sql.append("','");
					    sql.append(nodeDTO.getSubtype());
					    sql.append("','");
					    sql.append(vo.getIfindex());
					    sql.append("','");
					    sql.append(vo.getIpaddress());
					    sql.append("','");
					    sql.append(mac);
					    sql.append("','");
					    sql.append(vo.getIfband());
					    sql.append("','");
					    sql.append(vo.getIfsms());
					    sql.append("','");
					    sql.append(time);
					    sql.append("','"+vo.getBak()+"')");
					    list.add(sql.toString());
					    sql=null;
					    vo=null;
					    
				}
				
				GathersqlListManager.AdddateTempsql(deleteSql, list);
				list=null;
				
			}
		}


}

}
