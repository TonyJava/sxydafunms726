package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;

public class TestSendMessage {
	
	public Hashtable sendNum = new Hashtable();
	public void run() {
		List list = this.todayList();
		for (int i = 0; i < list.size(); i++) {
			EventList eventlist = (EventList)list.get(i);
			
		}
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
					+ "and recordtime<='" + toTime + "' and content like '%文件系统利用率%'");
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
    
	public static void main(String[] args) {
		
	}
}
