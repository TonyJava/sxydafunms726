package com.afunms.alarm.send;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.util.AgentalarmControlutil;
import com.database.config.SystemConfig;
import com.oss.services.client.WebServicesClient;


public class SendPageAlarm implements SendAlarm {
	
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
		//EventListDao eventListDao = new EventListDao();
		try {
			//eventListDao.save(eventList);
		  
			AgentalarmControlutil.Getsystem_eventlistsql(eventList);
			WebServicesClient sen=new WebServicesClient();
			
			System.out.println("=========================��ʼ����webservice�澯==================================");
			sen.sendAlarmToServiceServer(SystemConfig.getConfigInfomation("alarmWebservice", "url")+"", SystemConfig.getConfigInfomation("alarmWebservice", "bsid")+"", eventList.getNodeid()+"", eventList.getContent(), eventList.getSubtype(),  eventList.getLevel1()+"");
			System.out.println("=========================��ʼ����webservice�澯=====end=============================");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//eventListDao.close();
		}
	}
}
