package com.afunms.alarm.send;

import java.text.SimpleDateFormat;
import java.util.Hashtable;

import montnets.SmsDao;
import montnets.SmsServer;
import java.io.*; 
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.event.model.EventList;
import com.afunms.event.model.SendSmsConfig;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.util.AgentalarmControlutil;
import com.database.config.SystemConfig;
import java.net.*; 
import java.util.Scanner;



public class SendSMSAlarm implements SendAlarm{
	/**
	 * 短信告警判断
	 */
	public boolean checkSendAlarm(EventList eventList){
		boolean flag = true;
		int nodeid = eventList.getNodeid();//获取nodeid
		//判断是否有上级节点，如果有向下，如果没有直接返回 false 发送告警信息
		
		//System.out.println("========================关联告警判断================================="+nodeid);
		//System.out.println(ShareData.getAlarmcorrelations().toString());
		
		//System.out.println("========================关联告警判断=================================");
		if(!ShareData.getAlarmcorrelations().containsKey(nodeid)){
			
			
			String fatherId = (String)ShareData.getAlarmcorrelations().get(nodeid);//根据nodeid在内存中取上级节点nodeid
			
			StringBuffer str_host = new StringBuffer();
			str_host.append(fatherId);
			str_host.append(":host:ping");
			StringBuffer str_net = new StringBuffer();
			str_net.append(fatherId);
			str_net.append(":net:ping");
			//根据上级节点nodeid取其告警信息 nodeid:host/net:ping=level
			if(ShareData.getAgentalarmlevellist().containsKey(str_host) || ShareData.getAgentalarmlevellist().containsKey(str_net)){
				flag=false;	
			}
			
		}
		//如果上级节点存在告警信息发送短信告警
		return flag;
	}
	//---中国气象局
	///**
	// * 
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============发送短信告警开始==================");
		String ip = eventList.getIpaddress();
		//String level = "6";
		String faultID = "1";
		String content = eventList.getContent();
		//String bid= eventList.getBusinessid();

		
		////network,host,db,web
		String subtype=eventList.getSubtype();
		String bigsystem="";
		String smallsystem="";
		
//		System.out.println("===子类型===="+subtype);
//		System.out.println("======="+ShareData.getBusinessHash().toString());
//		
//		if((null!=subtype && subtype.length()>0))
//		{
//			
//			if(subtype.equals("host")|| subtype.equals("net") || subtype.equals("db") || subtype.equals("db")|| subtype.equals("web"))
//			{
//				
//			}else
//			 {
//				subtype="host";
//			 }
//			
//		}
		//ystem.out.println("=====开始短信发送======alarmWayDetail="+alarmWayDetail.getSendTimes());
		
		
		
		if(subtype.equals("net") && ShareData.getBusinessHash().containsKey(ip+"-net"))
		{
			 bigsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-net")).get("bigsys");
			 smallsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-net")).get("smallsys");
		}
			
		if(subtype.equals("host")&& ShareData.getBusinessHash().containsKey(ip+"-host") )
		{
			
			//System.out.println("=ssssvvvsss======"+ShareData.getBusinessHash().toString());
			//System.out.println("===="+ip+"-host");
			
			 bigsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-host")).get("bigsys");
			 smallsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-host")).get("smallsys");
		}
		
		
		if(subtype.equals("db") && ShareData.getBusinessHash().containsKey(ip+"-db"))
		{
			 bigsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-db")).get("bigsys");
			 smallsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-db")).get("smallsys");
		}
		
		if(subtype.equals("web") && ShareData.getBusinessHash().containsKey(ip+"-other"))
		{
			 bigsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-other")).get("bigsys");
			 smallsystem =(String)((Hashtable) ShareData.getBusinessHash().get(ip+"-other")).get("smallsys");
		}
		//2012-07-27
		if(this.checkSendAlarm(eventList)){
			ShortMessage sm = new ShortMessage();
			sm.sendTxt(ip, bigsystem, smallsystem, content, faultID);
			SendSmsConfig ssc = new SendSmsConfig();
			ssc.setName("");
			ssc.setMobilenum("");
			ssc.setEventlist(eventList.getContent());
			AgentalarmControlutil.GetSMSsql(ssc);
			SysLogger.info("==============发送短信告警结束==================");
		}
	}
	
	//	*/

/**
 
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============发送短信告警==================");
		//向客户端写告警信息
		String[] ids = alarmWayDetail.getUserIds().split(",");
		if (ids != null && ids.length > 0) {
			for (int j = 0; j < ids.length; j++) {

				String oid = ids[j];
				User op = null;
				UserDao userdao = new UserDao();    
				try {
					op = (User) userdao.findByID(oid);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					userdao.close();
				}
				if (op == null) {
					continue;
				}
				//开始发送短信接口
//				 公司短信猫开始发送短信
				SmsServer ss = new SmsServer();
				int rc = -1;
				try {
					rc = ss.sendSMS(op.getMobile(), eventList.getContent());
				} catch (Exception e) {
                    e.printStackTrace();
				}
				if (rc >= 0) {
					SendSmsConfig ssc = new SendSmsConfig();
					ssc.setName(op.getName());
					ssc.setMobilenum(op.getMobile());
					ssc.setEventlist(eventList.getContent());
					SmsDao dao = new SmsDao();
					try {
						dao.save(ssc);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
				}
				System.out.println("发送短信告警结束..........");
			}
		}			
	}
	* 
 */	
	
	
	//----遂宁商行短信网关
	
	 /**
	
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============发送短信告警==================");
		//向客户端写告警信息
		String[] ids = alarmWayDetail.getUserIds().split(",");
		if (ids != null && ids.length > 0) {
			for (int j = 0; j < ids.length; j++) {

				String oid = ids[j];
				User op = null;
				UserDao userdao = new UserDao();    
				try {
					op = (User) userdao.findByID(oid);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					userdao.close();
				}
				if (op == null) {
					continue;
				}
				//开始发送短信接口
//				 公司短信猫开始发送短信
				//SmsServer ss = new SmsServer();
				SmsSocketClient ss=new SmsSocketClient();
				String rc = "";
				try {
					
					rc=ss.sendMsg(eventList.getContent(), op.getMobile());
					
				} catch (Exception e) {
                    e.printStackTrace();
				}
				
				System.out.println("=======短信的返回值====="+rc);
				if (null!=rc && rc.lastIndexOf("0000|")>=0) {
					SendSmsConfig ssc = new SendSmsConfig();
					ssc.setName(op.getName());
					ssc.setMobilenum(op.getMobile());
					ssc.setEventlist(eventList.getContent());
					SmsDao dao = new SmsDao();
					try {
						dao.save(ssc);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
				}
				System.out.println("发送短信告警结束..........");
			}
		}			
	}
	
  * 
	  */
	
	
	
}
