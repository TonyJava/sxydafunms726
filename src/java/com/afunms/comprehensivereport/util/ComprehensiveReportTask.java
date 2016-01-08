package com.afunms.comprehensivereport.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.mail.MailInfo;
import com.afunms.capreport.mail.MailSender;
import com.afunms.comprehensivereport.dao.CompreReportUtilDao;
import com.afunms.comprehensivereport.model.CompreReportInfo;
import com.afunms.initialize.ResourceCenter;

public class ComprehensiveReportTask extends TimerTask {
	private static Logger log = Logger.getLogger(ComprehensiveReportTask.class);

	private final String hms = " 00:00:00";

	@Override
	public void run() {
		// TODO Auto-generated method stub
		readMailMessage();
	}

	private void readMailMessage(){
		DateTime dt = new DateTime();
		CompreReportUtilDao curDao = new CompreReportUtilDao();
		List list = curDao.loadAll();
		curDao.close();
		if(list!=null&&list.size()>0){
			try {
				for(int i=0;i<list.size();i++){
					CompreReportInfo cri = (CompreReportInfo)list.get(i);
					boolean istrue = false;
					if("day".equals(cri.getReportType())){
						if(cri.getSendTime().contains("" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours())
								+ "")){
							istrue = true;
						}
					}else if("busi".equals(cri.getReportType())){
						if(cri.getSendTime().contains("" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours())
								+ "")){
							istrue = true;
						}
					}else if("week".equals(cri.getReportType())){
						if(cri.getSendTime2().contains(""+(dt.getDay() - 1)+"")
								&& cri.getSendTime().contains(""+(dt.getHours() < 10 ? "0"+dt.getHours():dt.getHours()))){
							istrue = true;
						}
					}else if("weekBusi".equals(cri.getReportType())){
						if(cri.getSendTime2().contains(""+(dt.getDay() - 1)+"")
								&& cri.getSendTime().contains(""+(dt.getHours() < 10 ? "0"+dt.getHours():dt.getHours()))){
							istrue = true;
						}
					}
					if (istrue) {
						String filePath = ResourceCenter.getInstance().getSysPath() + "temp" + File.separator
								+ "report" + "_" + System.currentTimeMillis() + "." + cri.getAttachmentFormat();
						if (doSubscribe(cri, filePath)) {
							sendMail(cri, filePath);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private boolean doSubscribe(CompreReportInfo crInfo,String filePath){
		CompreReportExport cre = new CompreReportExport();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = sdf.format(new Date())+" 00:00:00";
		String toTime = sdf.format(new Date())+ " 23:59:59";
		try{
			if("weekBusi".equals(crInfo.getReportType())||"week".equals(crInfo.getReportType())){
				String[][] weekDay = getWeekDate(sdf.format(new Date()));
				String business = crInfo.getNodeid();
				if(business!=null){
					if(business.equals("")){
						business = null;
					}else{
						String[] busi = business.split(",");
						business = "";
						for(int i=0;i<busi.length;i++){
							if(!"".equals(busi[i])){
								business = business + busi[i]+",";
							}
						}
						business = business.substring(0, business.length()-1);
					}
				}
				cre.exportReportByDay(crInfo.getIds(), crInfo.getType(),crInfo.getReportType(), filePath, weekDay, crInfo.getAttachmentFormat(),business);
			}
			else{
				String business = crInfo.getNodeid();
				if(business!=null){
					if(business.equals("")){
						business = null;
					}else{
						String[] busi = business.split(",");
						business = "";
						for(int i=0;i<busi.length;i++){
							if(!"".equals(busi[i])){
								business = business + busi[i]+",";
							}
						}
						business = business.substring(0, business.length()-1);
					}
				}
				cre.exportReportByDay(crInfo.getIds(), crInfo.getType(),crInfo.getReportType(), filePath, startTime, toTime, crInfo.getAttachmentFormat(),business);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private void sendMail(CompreReportInfo crInfo, String filePath) {
		MailInfo mailinfo = new MailInfo();
		mailinfo.setAffixPath(filePath);
		mailinfo.setContent(crInfo.getEmailContent());
		mailinfo.setSubject(crInfo.getEmailTitle());
		mailinfo.setReceiver(crInfo.getEmail());
		if (MailSender.send(mailinfo)) {
			File f = new File(filePath);
			if (f != null && f.canWrite()) {
				boolean b = f.delete();
				if (!b) {
					log.warn("------发送文件:" + filePath + " 失败!------");
				}
			}
			log.info("------发送邮件成功！------");
		} 
	}
	
	public String getWeekDate(String time,int week){
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try{
		cal.setTime(sdFormat.parse(time));
		}catch(ParseException e){
			e.printStackTrace();
		}
		cal.set(Calendar.DAY_OF_WEEK, week);
		return sdFormat.format(cal.getTime());
	}
	private String[][] getWeekDate(String time){
		String[][] week = new String[7][3];
		for(int i=0;i<7;i++){
			String weekday = getWeekDate(time, i+1);
			week[i][0] = weekday;
			week[i][1] = weekday + " 00:00:00";
			week[i][2] = weekday + " 23:59:59";
		}	
		return week;
	}
}
