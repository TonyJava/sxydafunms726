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
	 * ���Ÿ澯�ж�
	 */
	public boolean checkSendAlarm(EventList eventList){
		boolean flag = true;
		int nodeid = eventList.getNodeid();//��ȡnodeid
		//�ж��Ƿ����ϼ��ڵ㣬��������£����û��ֱ�ӷ��� false ���͸澯��Ϣ
		
		//System.out.println("========================�����澯�ж�================================="+nodeid);
		//System.out.println(ShareData.getAlarmcorrelations().toString());
		
		//System.out.println("========================�����澯�ж�=================================");
		if(!ShareData.getAlarmcorrelations().containsKey(nodeid)){
			
			
			String fatherId = (String)ShareData.getAlarmcorrelations().get(nodeid);//����nodeid���ڴ���ȡ�ϼ��ڵ�nodeid
			
			StringBuffer str_host = new StringBuffer();
			str_host.append(fatherId);
			str_host.append(":host:ping");
			StringBuffer str_net = new StringBuffer();
			str_net.append(fatherId);
			str_net.append(":net:ping");
			//�����ϼ��ڵ�nodeidȡ��澯��Ϣ nodeid:host/net:ping=level
			if(ShareData.getAgentalarmlevellist().containsKey(str_host) || ShareData.getAgentalarmlevellist().containsKey(str_net)){
				flag=false;	
			}
			
		}
		//����ϼ��ڵ���ڸ澯��Ϣ���Ͷ��Ÿ澯
		return flag;
	}
	//---�й������
	///**
	// * 
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============���Ͷ��Ÿ澯��ʼ==================");
		String ip = eventList.getIpaddress();
		//String level = "6";
		String faultID = "1";
		String content = eventList.getContent();
		//String bid= eventList.getBusinessid();

		
		////network,host,db,web
		String subtype=eventList.getSubtype();
		String bigsystem="";
		String smallsystem="";
		
//		System.out.println("===������===="+subtype);
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
		//ystem.out.println("=====��ʼ���ŷ���======alarmWayDetail="+alarmWayDetail.getSendTimes());
		
		
		
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
			SysLogger.info("==============���Ͷ��Ÿ澯����==================");
		}
	}
	
	//	*/

/**
 
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============���Ͷ��Ÿ澯==================");
		//��ͻ���д�澯��Ϣ
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
				//��ʼ���Ͷ��Žӿ�
//				 ��˾����è��ʼ���Ͷ���
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
				System.out.println("���Ͷ��Ÿ澯����..........");
			}
		}			
	}
	* 
 */	
	
	
	//----�������ж�������
	
	 /**
	
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		SysLogger.info("==============���Ͷ��Ÿ澯==================");
		//��ͻ���д�澯��Ϣ
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
				//��ʼ���Ͷ��Žӿ�
//				 ��˾����è��ʼ���Ͷ���
				//SmsServer ss = new SmsServer();
				SmsSocketClient ss=new SmsSocketClient();
				String rc = "";
				try {
					
					rc=ss.sendMsg(eventList.getContent(), op.getMobile());
					
				} catch (Exception e) {
                    e.printStackTrace();
				}
				
				System.out.println("=======���ŵķ���ֵ====="+rc);
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
				System.out.println("���Ͷ��Ÿ澯����..........");
			}
		}			
	}
	
  * 
	  */
	
	
	
}
