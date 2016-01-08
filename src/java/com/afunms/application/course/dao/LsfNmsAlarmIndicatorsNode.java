package com.afunms.application.course.dao;


import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;

public class LsfNmsAlarmIndicatorsNode {
	
	protected DBManager conn;
	
	public LsfNmsAlarmIndicatorsNode(){
		
	}
	

	public void save(String nodeid) {
		// TODO Auto-generated method stub
		
		//LsfNms vo = new LsfNms();
		 conn = new DBManager();
		 Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));

		 NodeUtil nodeutil = new NodeUtil();
		 String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
		
		 StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_alarm_indicators_node(nodeid,name,type,subtype," +
				   "datatype,moid,threshold,threshold_unit,compare,compare_type," +
				   "alarm_times,alarm_info,alarm_level,enabled,poll_interval,interval_unit," +
				   "subentity,limenvalue0,limenvalue1,limenvalue2,time0,time1,time2," +
				   "sms0,sms1,sms2,category,descr,unit,way0,way1,way2)values('");
	
		sql.append(nodeid);
		sql.append("','");
		sql.append("lsflog");
		sql.append("','");
		sql.append("host");
		sql.append("','");
		sql.append(subtype);
		sql.append("','");
		sql.append("number");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("个");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("lsf日志文件异常");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("800");
		sql.append("','");
		sql.append("900");
		sql.append("','");
		sql.append("1000");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("1");
		sql.append("','");
		sql.append("0");
		sql.append("','");
		sql.append("0");
		sql.append("','");
		sql.append("0");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("日志文件异常");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("null");
		sql.append("','");
		sql.append("null");
		sql.append("')");
		
		try{
			
			
			//System.out.println("==============xxdd==="+sql.toString());
		    conn.executeUpdate(sql.toString());
		    conn.commit();
		    
		    
		    
			
		}catch(Exception e)
		{
			
			
		}finally
		{
			conn.close();
			
		}
	}
	
	public boolean delete(String nodeid , String name , String subtype)
	   {
		   boolean result = false;
		   conn = new DBManager();
		   try
		   {
			   conn.executeUpdate("delete  from  nms_alarm_indicators_node where nodeid='"+nodeid+"' and name='"+name+" and subtype='"+subtype+"");
			   //System.out.println("delete from nms_contract where id=" +id);
			   result = true;
		   }
		   catch(Exception e)
		   {
			   //SysLogger.error("LsfNmsDao.delete()",e); 
		   }
		   finally
		   {
			   conn.close();
		   }
		   return result;
	   }
	
	/**
	    * 删除一批记录
	    */
	   public boolean nmsdelete(String[] nodeid)
	   {
		   boolean result = false;
		   try
		   {   
			   
			   System.out.println("============="+nodeid.length);
			   if(null!=nodeid)
		       for(int i=0;i<nodeid.length;i++)
		           conn.addBatch("delete  from  nms_alarm_indicators_node where  name='lsflog' and subtype='lsflog' and nodeid='" + nodeid[i].toString()+"'");
		       conn.executeBatch();
		       result = true;
		       
		       conn.close();
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("LsfNms.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	   
	   
		/**
	    * 删除一批记录
	    */
	   public String lsfdelnmsgathertosql(String nodeid)
	   {
		  String sql="delete  from  nms_alarm_indicators_node where  name='lsflog' and subtype='aix' and nodeid='" + nodeid+"'";
		  System.out.print("-------------sql===========--------------"+ sql);    
		   return sql;
	   }
}
