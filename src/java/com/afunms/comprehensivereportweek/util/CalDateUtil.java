package com.afunms.comprehensivereportweek.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * ���ڼ���
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
//		// �����ַ������ڵ�Date����
//		Date d = fmt.parse(str);// str -> Date
//		// �����ȥһ�ܣ���һ������
//		Date date = expDate(d, 0);
//		getEndWeek(date);
//		// ��ʽ�����������.
//		String exp = fmt.format(date);
//		System.out.println("��ȥ����:" + exp);
//		System.out.println("��ǰ���ڣ�"+getNowDate());
//		System.out.println("��ǰСʱ��"+getNowDayWeekHour());
//		System.out.println("���������ڣ�"+getNowDayWeek());
		
	}
	/**
	 * ��ȡ��ǰСʱ��24Сʱ�ƣ�
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
	 * ��ȡ��ǰ���ڼ�
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
	 *            ��ǰ����
	 * @param months
	 *            ��ȥ�·�����
	 * @return ��ȥ����
	 */
	public //static 
	Date expDate(Date create, int months) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(create);// ��ǰ����
		cal.add(Calendar.MONTH, months);// �����������
		cal.add(Calendar.WEEK_OF_YEAR, -1);// ��ǰһ��
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// ���µ���������ǰ���գ���һSUNDAY��
		// cal.set(Calendar.DAY_OF_YEAR, 0);//һ��ĵ�һ��
		return cal.getTime();
	}
	
	/**
	 * ��õ�ǰ�·�
	 * 
	 * @return �·�
	 */
	public String getMonth() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String str = df.format(date);
		String month = str.substring(4, 6);// �����ȡ����
		System.out.println(month);
		return month;
	}

	/**
	 * ����"date"�����һ��
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
		System.out.println("�������һ��>>>" + week_end);
		
		return week_end;
	}
	
	/**
	 * ��ȡ��ǰ����
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
	 * ��������ת��
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

