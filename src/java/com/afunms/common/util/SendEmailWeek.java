package com.afunms.common.util;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.mail.Address;

import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.config.dao.BusinessDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.system.model.User;


public class SendEmailWeek extends TimerTask{
	
	private String startDate = null;
	private String endDate = null;
	private String startTime = null;
	private String endTime = null;
	private String ip = "10.204.8.90";
	private String alarmwayId = "253";
	@Override
	public void run() {
		System.out.println("模拟告警巡检邮件");
	
		
		// TODO Auto-generated method stub
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		List list = alarmWayDetailDao.findByAlarmWayId(alarmwayId);
		AlarmWayDetail alarm = null;
		for(int i = 0; i < list.size(); i++) {
			alarm = (AlarmWayDetail)list.get(i);
		}
		//startDate = alarm.getStartDate();
		//endDate   = alarm.getEndDate();
		startTime = alarm.getStartTime();
		endTime   = alarm.getEndTime();
		
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		int hours = date.getHours();
		/*String weekDay = dateFm.format(date);
		int currDay = 0;
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		for (int j = 0; j < weekDays.length; j++) {
			if (weekDay.equals(weekDays[j])) {
				currDay = j+1;
			}
		}*/
		
	   // if (Integer.parseInt(startDate) <= currDay && currDay <= Integer.parseInt(endDate)) {
	    	if (Integer.parseInt(startTime) <= hours && hours < Integer.parseInt(endTime)) {
	    		
	    		/*List todaylist = this.todayList();
	    		for (int i = 0; i < todaylist.size(); i++) {
	    			EventList eventList = (EventList)todaylist.get(i);
	    			System.out.println("告警巡检发送邮件。。。");
	    			SendMailManager mailManager = new SendMailManager();
	    			Address[] add = mailManager.geReceivemailaddr(alarmwayId);
	    			mailManager.SendMail(add, eventList.getContent());
	    		}*/
	    		System.out.println("告警巡检发送邮件。。。");
	    		SendMailManager mailManager = new SendMailManager();
    			Address[] add = mailManager.geReceivemailaddr(alarmwayId);
    			String thevalue = getThevalue();
    			mailManager.SendMail(add, "模拟告警巡检邮件(IP: 10.204.8.90)  cpu利用率超过阀值 当前值:"+thevalue+"% 阀值:80 %");
	    	}
	    //}

	}
	
	private List todayList(){
		EventListDao dao = new EventListDao();
		List list = null;
		try{
			  list = dao.loadByWhere(getSQL());
		}catch(Exception e){
		}finally{
			dao.close();
		}
		return list;
	}
	
	private String getSQL(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar sendcalen = Calendar.getInstance();
 		Date cc = sendcalen.getTime();
 		String curdate = formatter.format(cc);	
		String dateStr = CommonUtil.getCurrentDate();  //取当前日期
		String endTime = CommonUtil.getCurrentTime();  //取当前时间
		String beginTime = this.getLaterTenSecondTime();  //取过去10秒的时间
		
		String fromTime = curdate+" "+beginTime;
		String toTime = curdate+" "+endTime;
		String sql = "";
		try {
			StringBuffer s = new StringBuffer();
			s.append("where recordtime>= '" + fromTime + "' "
					+ "and recordtime<='" + toTime + "' and content like '%"+ip+"%'");
			sql = s.toString();
			sql = sql + " order by id desc";
			System.out.println(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	
	//往后延时5分钟，用于检测最新5分钟内的报警信息
    public static String getLaterTenSecondTime(){		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date1;
		String timeFormat = null;
		try {
			date1 = format1.parse(getDateAndTime());
			long Time=(date1.getTime()/1000)-60*5;
			
			date1.setTime(Time*1000);
			String mydate1 = format1.format(date1);
			
			//System.out.println("mydate1:"+mydate1);
			
			String [] strArray = mydate1.split(" ");
			timeFormat = strArray[1];
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		return timeFormat;
    }
    
    public static String getDateAndTime()
    {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		String currentTime = formatter.format(date);
		return currentTime;
    }
    
    public String getThevalue()
    {
        DBManager conn = new DBManager();
       ResultSet rs = null;
       String thevalue = null;
       try
       {
           rs = conn.executeQuery("select thevalue from cpu10_204_8_90 order by collecttime desc limit 1");
           while(rs.next()) {
        	   thevalue = rs.getString(1);
           }
          	
       }
       catch(Exception e)
       {
           SysLogger.error("EventListDao:loadAll()",e);
       
       }
       finally
       {
           conn.close();
       }
       return thevalue;
    }
    
	public static void main(String[] args) {
		SendEmailWeek s = new SendEmailWeek();
		s.todayList();
	}

}
