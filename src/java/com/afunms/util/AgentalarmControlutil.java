package com.afunms.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.SendAlarmTime;
import com.afunms.application.model.NodeIndicatorAlarm;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.SendSmsConfig;
import com.database.config.SystemConfig;
import com.gatherdb.GathersqlListManager;


/**
 * 
 * �澯���� ��澯���ƿ���ʵ����
 * ʵ�ֲ�ͨagent֮��澯���ݹ�����澯���ƵĹ���
 * @author konglq
 *
 */

public class AgentalarmControlutil {
	
     private static String agentid=SystemConfig.getConfigInfomation("Agentconfig", "AGENTID");
     
     
     
    	 //SystemConfig.getConfigInfomation("Agentconfig", "AGENTID");
	
	
//    public static String getAgentid() {
//    	
//    	SystemConfig sys=new SystemConfig();
//		AgentalarmControlutil.agentid = sys.getConfigInfomation("Agentconfig", "AGENTID");
//		return agentid;
//	}
//
//
//
//	public static void setAgentid(String agentid) {
//		AgentalarmControlutil.agentid = agentid;
//	}



	/**
     * 
    * ���ڴ��л�ȡ��ǰ�澯�ļ���
	 * @param nodeid �ڵ�id
	 * @param name  �澯���ƣ�ping��cpu��
	 * @param type  �ڵ�����
	 * @param index ���� ������̵�����
     * @return ���û��key ��0�ַ���������򷵻ظ澯1,2,3 �ĸ澯����
     */
	public static String GetAlarmlevel(String nodeid,String name,String type,String index)
	{
		String level="0";
		String key=nodeid+":"+name+":"+type;
		if(null!=index && index.length()>0)
		{
			key=key+":"+index;
		}
		if(ShareData.getAgentalarmlevellist().containsKey(key))
		{
			level=(String)ShareData.getAgentalarmlevellist().get(key);
		}
		
		
		return level;
	}
	
	
	
  /**
   * 
   *  �������ʹ��ڴ��б��в��Ҷ�Ӧ�ĸ澯���ͣ�����з���ture��û�з���false
   * @param nodeid �ڵ�id
   * @param name ����
   * @param type ����
   * @param index ����
   * @param levelvalue  �澯�ĵȼ�
   * @return
   */
	public static boolean GetAlarmlevel(String nodeid,String name,String type,String index,int levelvalue)
	{
		boolean  level=false;
		String key=nodeid+":"+type+":"+name;
		if(null!=index && index.length()>0)
		{
			key=key+":"+index;
		}
		if(ShareData.getAgentalarmlevellist().containsKey(key))
		{
			int le=Integer.parseInt((String)ShareData.getAgentalarmlevellist().get(key));
			if(levelvalue==le)
			{
				level=true;
			}
		}
		
		
		return level;
	}
	
	
	
	
	
    /**
     * 
     * ���ڴ��л�ȡ��ǰ�澯�ļ���
	 * @param nodeid �ڵ�id
	 * @param name  �澯���ƣ�ping��cpu��
	 * @param type  �ڵ�����
	 * @param index ���� ������̵�����
	 * @param flg ����ǽ��̡����� �ռ������ �Ѿ��Ѹ澯������д���ˣ�����Ҫƴдkey ����flg=false �� key==name
     * @return ���û��key ��0�ַ���������򷵻ظ澯1,2,3 �ĸ澯����
     */
	public static void PutAlarmlevel(String nodeid,String name,String type,String index,String levelvalue,boolean flg)
	{
		
		
		String key=nodeid+":"+type+":"+name;
		if(null!=index)
		{
			
			
			if(flg)
			{//
				//System.out.println("==2####=name="+name);
				//System.out.println("==2#####=fle="+flg);
			if(index.length()>0)
			key=key+":"+index;
			}else
			{
				//System.out.println("==####=name="+name);
				//System.out.println("==#####=fle="+flg);
				key=name;
				
			}
		}
		//if(ShareData.getAgentalarmlevellist().containsKey(key))
		//{
			ShareData.getAgentalarmlevellist().put(key, levelvalue);
		//}

			//System.out.println("===============put====================");
			//System.out.println(ShareData.getAgentalarmlevellist());
			//System.out.println("===============put====================");
		//��ɾ��ԭ������Ȼ���ٲ�������  nms_agent_alarm
		
		GathersqlListManager.Addsql_alarm(GetDeletesql(nodeid,key));
		GathersqlListManager.Addsql_alarm(Getsql(nodeid,key,type,levelvalue));

		
		//return level;
	}
	
	

	
    /**
     * 
     * ���ڴ���ɾ����Ӧ���Ƶĸ澯��¼����ɾ�����ݿ���м�¼
	 * @param nodeid �ڵ�id
	 * @param name  �澯���ƣ�ping��cpu��
	 * @param type  �ڵ�����
	 * @param index ���� ������̵�����

     */
	public static void DeleteAlarmlevel(String nodeid,String name,String type,String index)
	{
		
		//System.out.println("===============del====================");
		//System.out.println(ShareData.getAgentalarmlevellist());
		//System.out.println("===============del====================");
		
		
		String key=nodeid+":"+type+":"+name;
		if(null!=index && index.length()>0)
		{
			key=key+":"+index;
		}
		
		System.out.println("=====key="+key);
		
		System.out.println("==name=="+name);
		System.out.println("===============del====================");
		System.out.println(ShareData.getAgentalarmlevellist());
		System.out.println("===============del====================");
		GathersqlListManager.Addsql_alarm(GetDeletesql(nodeid,key));
		
		if(ShareData.getAgentalarmlevellist().containsKey(key))
		{
			System.out.println("=================ɾ���ڴ��еĸ澯==============");
			ShareData.getAgentalarmlevellist().remove(key);
		}
		
		
		
		
		
	}
	
	
	
	  /**
     * 
     * ���ݽڵ��id�� �澯����key���ڴ��������ɾ����Ӧ������
	 * @param nodeid �ڵ�id
	 * @param name  �澯���ƣ�ping��cpu��
     */
	public static void DeleteAlarmkeyname(String nodeid,String name)
	{
		
		
		//System.out.println("===================del2-nanme=======================");
		//System.out.println("===="+name);
		//System.out.println("===================del2-nanme=======================");
		if(ShareData.getAgentalarmlevellist().containsKey(name))
		{
			ShareData.getAgentalarmlevellist().remove(name);
		}
		GathersqlListManager.Addsql_alarm(GetDeletesql(nodeid,name));
		
		
	}
	
	
	
	/**
	 * 
	 * ��ȡ����sql
	 * @return
	 */
	private static String Getsql(String nodeid,String name,String type,String alarmlevel)
	{
		
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_agent_alarm(agentid,nodeid,alarmname,alarmtype,alarmlevel) values(");
		sql.append("'");
		sql.append(agentid);
		sql.append("',");
		sql.append("'");
		sql.append(nodeid);
		sql.append("','");
		sql.append(name);
		sql.append("','");
		sql.append(type);
		sql.append("','");
		sql.append(alarmlevel);
		sql.append("')");
		
		//System.out.println("=============alarm-sql========"+sql);
		return sql.toString();
		
	}
	
	
	
	
	/**
	 * 
	 * ��ȡ����sql
	 * @return
	 */
	private static String GetDeletesql(String nodeid,String name)
	{
		
		
		String sql="delete from nms_agent_alarm where nodeid='"+nodeid+"'" + " and alarmname='"+name+"'";
		
		//System.out.println("============alarm-del--="+sql);
		return sql;
	}
	
	
	
	
	
	  /**
     * 
     * ���ָ澯ָ��
     * @param checkEvent
     * @return
     */
    public static void GetCheckEventsql(CheckEvent baseVo)
    {
    	
    	CheckEvent vo = (CheckEvent)baseVo;
    	StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(name,alarmlevel)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getAlarmlevel());	
		sql.append(")");
    	
		GathersqlListManager.Addsql_alarm(sql.toString());
    }
	
	
    /**
     * 
     * ɾ���澯ָ��
     * @param checkEvent
     * @return
     */
    public static String GetDeleteCheckEventsql(CheckEvent baseVo)
    {
    	
    	CheckEvent vo = (CheckEvent)baseVo;
    	String sql = "delete from nms_checkevent where name='"+vo.getName()+"'";
    	
       return sql.toString();
    }
    
	
	/**
	 * 
	 * ���Ͷ�Ϣ������Ϣ��� ����sql
	 * @param baseVo
	 * @return
	 */
    public static void GetSMSsql(BaseVo baseVo) {
		// TODO Auto-generated method stub
			SendSmsConfig vo = (SendSmsConfig)baseVo;	  
		   Date d = new Date();
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String time=sdf.format(d);;
		   StringBuffer sql = new StringBuffer();
		   sql.append("insert into sms_server(name,mobilenum,eventlist,eventtime)values(");
		   sql.append("'");
		   sql.append(vo.getName());
		   sql.append("','");
		   sql.append(vo.getMobilenum());
		   sql.append("','");
		   sql.append(vo.getEventlist());
		   sql.append("','");
		   sql.append(time);
		   sql.append("'");
		   sql.append(")");
		   GathersqlListManager.Addsql_alarm(sql.toString());
	}
    
	
    
    /**
     * 
     * ɾ���澯ʱ��
     * @param name
     */
	public static void delete_nms_send_alarm_time(AlarmIndicatorsNode alarmIndicatorsNode,String name)
	{
		if(!alarmIndicatorsNode.getName().equals("diskperc") || !alarmIndicatorsNode.getName().equals("proce") || !alarmIndicatorsNode.getName().equals("diskinc") || !alarmIndicatorsNode.getName().equals("diskbusy"))
		{
			String sql="delete from nms_send_alarm_time where name='" + name + "'";
		GathersqlListManager.Addsql_alarm(sql);
		}
	}
	
	
	
	  /**
     * 
     * ɾ���澯ʱ��
     * @param name
     */
	public static void delete_nms_send_alarm_time(String name)
	{
			String sql="delete from nms_send_alarm_time where name='" + name + "'";
		GathersqlListManager.Addsql_alarm(sql);
	}
	
	
	
	
	/**
	 * ҳ��澯�������
	 * 
	 */
	
	public static void Getsystem_eventlistsql(BaseVo baseVo)
	{
		EventList vo = (EventList)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_eventlist(eventtype,eventlocation,content,level1,managesign,bak,recordtime,reportman,nodeid,businessid,oid,subtype,managetime,subentity)values(");
		sql.append("'");
		sql.append(vo.getEventtype());
		sql.append("','");
		sql.append(vo.getEventlocation());	
		sql.append("','");
		sql.append(vo.getContent());
		sql.append("',");
		sql.append(vo.getLevel1());
		sql.append(",");
		sql.append(vo.getManagesign());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("','");
		sql.append(recordtime);
		sql.append("','");
		sql.append(vo.getReportman());
		sql.append("',");
		sql.append(vo.getNodeid());
		sql.append(",'");
		sql.append(vo.getBusinessid());
		sql.append("',");
		sql.append(vo.getOid());
		sql.append(",'");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getManagetime());	
		sql.append("','");
		sql.append(vo.getSubentity());
		sql.append("')");
		//SysLogger.info(sql.toString());
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
//nms_send_alarm_time
	
	
	/**
	 * 
	 * ���͸澯���������������
	 *
	 */
	public static void Getnms_send_alarm_timetosql(BaseVo vo) {
		// TODO Auto-generated method stub
		SendAlarmTime sendAlarmTime = (SendAlarmTime)vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_send_alarm_time(name,alarm_way_detail_id,send_times," +
				"last_send_time" +
				") values('");
		sql.append(sendAlarmTime.getName());
		sql.append("','");
		sql.append(sendAlarmTime.getAlarmWayDetailId());
		sql.append("','");
		sql.append(sendAlarmTime.getSendTimes());
		sql.append("','");
		sql.append(sendAlarmTime.getLastSendTime());
		sql.append("')");
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
	/**
	 *
	 * ɾ���澯���ƴ��� 
	 * @param name
	 */
	public static void Getnms_send_alarm_timetoDeletesql(BaseVo vo){
		//return saveOrUpdate("delete from nms_send_alarm_time where name='" + name + "'"); 
		SendAlarmTime sendAlarmTime = (SendAlarmTime)vo;
		GathersqlListManager.Addsql_alarm("delete from nms_send_alarm_time where name='"
				+ sendAlarmTime.getName() + "' and alarm_way_detail_id='"+sendAlarmTime.getAlarmWayDetailId()+"'");
	}

	
	/**
	 * 
	 * ɾ���澯��������Ϣ
	 * @param vo
	 */
	public static void  Getnode_indicator_alarmDeltetSql(BaseVo vo) {
		boolean result=false;
		NodeIndicatorAlarm nodeIndicatorAlarm = (NodeIndicatorAlarm)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("delete from  node_indicator_alarm '");
		sql.append(nodeIndicatorAlarm.getAlarmDesc());
		sql.append("' where deviceId = '");
		sql.append(nodeIndicatorAlarm.getDeviceId());
		sql.append("' and indicatorName = '");
		sql.append(nodeIndicatorAlarm.getIndicatorName());
		sql.append("'");
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
	/**
	 * 
	 * ����澯��Ϣ
	 * @param vo
	 * @return
	 */
	public static void Getnode_indicator_alarmIntoSql(BaseVo vo) {
		NodeIndicatorAlarm performancePanelIndicatorsModel = (NodeIndicatorAlarm)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into node_indicator_alarm (deviceId,deviceType,indicatorName,alarmLevel,alarmDesc) values ('");
		sql.append(performancePanelIndicatorsModel.getDeviceId());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getDeviceType());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getIndicatorName());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getAlarmLevel());
		sql.append("','");
		sql.append(performancePanelIndicatorsModel.getAlarmDesc());
		sql.append("')");
		
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
	
	
	public static void Getnms_alarminfoIntosql(BaseVo baseVo)
	{
		AlarmInfo vo = (AlarmInfo)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_alarminfo(content,ipaddress,level1,recordtime,type)values(");
		sql.append("'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getIpaddress());	
		sql.append("',");
		sql.append(vo.getLevel1());
		sql.append(",'");
		sql.append(recordtime);
		sql.append("','");
		sql.append(vo.getType());
		sql.append("')");
		//SysLogger.info("��ʼд�������澯����=======================");
		//SysLogger.info(sql.toString());
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
	
	
	
	
	public static void  GetSyslog_alarmIntosql(BaseVo baseVo)
	{
		NetSyslog vo = (NetSyslog)baseVo;
		Calendar tempCal = (Calendar)vo.getRecordtime();							
		Date cc = tempCal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_netsyslog(ipaddress,hostname,message,facility,priority,facilityname,priorityname,recordtime,businessid,category)values(");
		sql.append("\"");
		sql.append(vo.getIpaddress());
		sql.append("\",\"");
		sql.append(vo.getHostname());	
		sql.append("\",\"");
		sql.append(vo.getMessage());
		sql.append("\",");
		sql.append(vo.getFacility());
		sql.append(",");
		sql.append(vo.getPriority());
		sql.append(",\"");
		sql.append(vo.getFacilityName());
		sql.append("\",\"");
		sql.append(vo.getPriorityName());
		sql.append("\",\"");
		sql.append(recordtime);
		sql.append("\",\"");
		sql.append(vo.getBusinessid());
		sql.append("\",");
		sql.append(vo.getCategory());
		sql.append(")");
		GathersqlListManager.Addsql_alarm(sql.toString());
	}
	
	
	
	
}
