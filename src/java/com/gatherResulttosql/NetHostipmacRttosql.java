package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.CommonUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.Softwarecollectdata;
import com.gatherdb.GathersqlListManager;

public class NetHostipmacRttosql {
	
	/**
	 * 把结果生成sql
	 * @param dataresult 采集结果
	 * @param node 网元节点
	 */
	public void CreateResultTosql(Hashtable dataresult, Host node) {

		if ("1".equals(PollingEngine.getCollectwebflag())) {//是否启动分离模式

			
			// 处理IPMAC信息入库
			if(dataresult != null && dataresult.size()>0){
			
				
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    NodeUtil nodeUtil = new NodeUtil();										
		 			NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
					Vector ipmacVector = (Vector)dataresult.get("ipmac");
					String deleteSql = "delete from ipmac where relateipaddr='" + node.getIpAddress() + "'";
					Vector list=new Vector();
					
					if(ipmacVector != null && ipmacVector.size()>0){
						for(int i=0;i<ipmacVector.size();i++){
							try{
							IpMac ipmac = (IpMac) ipmacVector.elementAt(i);	
							String mac = ipmac.getMac();
							if(mac == null){
								mac = "";
							}
							mac = CommonUtil.removeIllegalStr(mac);
						    String sqll = "";
							String time = sdf.format(ipmac.getCollecttime().getTime());
							sqll = "insert into ipmac(relateipaddr,ifindex,ipaddress,mac,collecttime,ifband,ifsms)values('";
							sqll = sqll + ipmac.getRelateipaddr() + "','" + ipmac.getIfindex() + "','" + ipmac.getIpaddress() + "','";
							sqll = sqll + new String(mac.getBytes(),"UTF-8") + "','" + time + "','" + ipmac.getIfband() + "','" + ipmac.getIfsms() + "')";  								
							  
							list.add(sqll);
							//sqll=null;
							//ipmac=null;
							//time=null;
							mac=null;
							
						}catch(Exception e)
						{}
						}
						
					}
					
					GathersqlListManager.AdddateTempsql(deleteSql, list);
					list=null;
				}

			}

		}
	
	

}
