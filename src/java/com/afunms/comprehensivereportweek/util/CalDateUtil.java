package com.afunms.comprehensivereportweek.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * 日期计算
 * 
 * @author Administrator
 * 
 */
public class CalDateUtil {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {
//		Scanner s = new Scanner(System.in);
//		String str = s.nextLine();// 2010-12-1
//		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
//		// 解析字符串日期到Date类型
//		Date d = fmt.parse(str);// str -> Date
//		// 计算过去一周，周一的日期
//		Date date = expDate(d, 0);
//		getEndWeek(date);
//		// 格式化并输出日期.
//		String exp = fmt.format(date);
//		System.out.println("过去日期:" + exp);
//		System.out.println("当前日期："+getNowDate());
//		System.out.println("当前小时："+getNowDayWeekHour());
//		System.out.println("今天是星期："+getNowDayWeek());
		
	}
	/**
	 * 获取当前小时（24小时制）
	 * @return
	 */
	public 
//	static 
	int getNowDayWeekHour(){
//		Calendar calendar=Calendar.getInstance();
		Calendar calendars = Calendar.getInstance(Locale.CHINA);
//		int hour=calendar.get(Calendar.HOUR_OF_DAY)+8;
		int hour=calendars.get(Calendar.HOUR_OF_DAY);
		return hour;
	}
	/**
	 * 获取当前星期几
	 * @return weekday
	 */
	public int getNowDayWeek(){
		Calendar cal = Calendar.getInstance();
//		int nowDay=0;
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		if(weekday==1){
			weekday = 7;
		}else if(weekday==2){
			weekday =1;
		}else if(weekday==3){
			weekday =2;
		}else if(weekday==4){
			weekday =3;
		}else if(weekday==5){
			weekday =4;
		}else if(weekday==6){
			weekday =5;
		}else if(weekday==7){
			weekday =6;
		}
		return weekday;
	}
	/**
	 * @param create
	 *            当前日期
	 * @param months
	 *            过去月份数量
	 * @return 过去日期
	 */
	public //static 
	Date expDate(Date create, int months) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(create);// 当前日期
		cal.add(Calendar.MONTH, months);// 计算过期日期
		cal.add(Calendar.WEEK_OF_YEAR, -1);// 提前一周
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 更新到过期日期前周日（周一SUNDAY）
		// cal.set(Calendar.DAY_OF_YEAR, 0);//一年的第一天
		return cal.getTime();
	}
	
	/**
	 * 获得当前月份
	 * 
	 * @return 月份
	 */
	public String getMonth() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String str = df.format(date);
		String month = str.substring(4, 6);// 从年截取到月
		System.out.println(month);
		return month;
	}

	/**
	 * 计算"date"周最后一天
	 * @param date 
	 * @return
	 */
	public //static 
	String getEndWeek(Date date) {
		java.text.SimpleDateFormat df = 
			new java.text.SimpleDateFormat("yyyy-MM-dd");
		Calendar c_end = Calendar.getInstance();
		c_end.setTime(date);
		c_end.add(c_end.DAY_OF_WEEK, (8 - c_end.get(Calendar.DAY_OF_WEEK)));
		String week_end = df.format(c_end.getTime());
		System.out.println("本周最后一天>>>" + week_end);
		
		return week_end;
	}
	
	/**
	 * 获取当前日期
	 * @return
	 * @throws  
	 */
	public //static
	Date getNowDate() {
		Date nowDate=null;
		try {
			Calendar cal1 = new GregorianCalendar();
			SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
			String d = theDate.format(cal1.getTime());
			nowDate = theDate.parse(d);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return nowDate;
	}
	
	/**
	 * 日期类型转换
	 * @param date
	 * @return
	 */
	public String convert(Date date){
		java.text.SimpleDateFormat df = 
			new java.text.SimpleDateFormat("yyyy-MM-dd");
		String convertDate = df.format(date);
		
		return convertDate;
	}
}

