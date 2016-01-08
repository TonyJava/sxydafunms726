package com.afunms.linkReport.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.comprehensivereport.dao.CompreReportUtilDao;
import com.afunms.comprehensivereport.model.CompreReportInfo;
import com.afunms.initialize.ResourceCenter;
import com.afunms.linkReport.util.LinkReportExport;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.Link;

public class LinkReportWeekManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if(action.equals("list")){
			return list();
		}
		if (action.equals("downReport")){
			return downReport();
		}
		if(action.equals("linkReportConfig")){
			return linkReportConfig();
		}
		return null;
	}
	
	private String list(){
		String linkids = request.getParameter("linkids");
		//取出所有的链路数据
		List linkList = null;
		try {
			LinkDao linkDao = new LinkDao();
			linkList = linkDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("linkids", linkids);
		request.setAttribute("linkList", linkList);
		return "/linkReport/week.jsp";
	}
	public String downReport(){
		String ids=getParaValue("ids");
		String type=getParaValue("type");
		String reportType=getParaValue("reportType");
		String exportType=getParaValue("exportType");
		String terms = getParaValue("terms");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (ids==null||ids.equals("")||ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return null;
    		LinkReportInfo report=new LinkReportInfo();
    		LinkReportConfigDao dao=new LinkReportConfigDao();
    		report=(LinkReportInfo) dao.findByID(id);
    		ids=report.getIds();
		}
		String filename="";
		if ("link".equalsIgnoreCase(type)) {
			if (exportType.equals("xls")) {
				filename="/temp/linkWeek_report.xls";
			}else if (exportType.equals("doc")) {
				filename="/temp/linkWeek_report.doc";
			}else if (exportType.equals("pdf")) {
				filename="/temp/linkWeek_report.pdf";
			}
		}
		String filePath=ResourceCenter.getInstance().getSysPath()+filename;
		String startTime=getParaValue("startdate");
		String[][] weekday = getWeekDate(startTime);
		LinkReportExport export = new LinkReportExport();
		export.exportReportByWeek(ids, type,reportType, filePath, weekday,exportType,terms);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
	}
	public String linkReportConfig(){
		String id = this.getParaValue("id");
		LinkReportConfigDao lru = new LinkReportConfigDao();
		LinkReportInfo lri = lru.findById(Integer.valueOf(id));
		lru.close();
		String ids = lri.getIds();
		
		LinkDao linkdao = new LinkDao();
    	List linkList = null;
    	try{
    		linkList = linkdao.loadListByIds(ids);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(linkdao != null){
    			linkdao.close();
    		}
    	}
    	String week = lri.getSendTime2();
		if(week!=null&&!week.equals("")){
			if(week.indexOf('0')>-1){
				week="星期天";
			}else if(week.indexOf('1')>-1){
				week="星期一";
			}else if(week.indexOf('2')>-1){
				week="星期二";
			}else if(week.indexOf('3')>-1){
				week="星期三";
			}else if(week.indexOf('4')>-1){
				week="星期四";
			}else if(week.indexOf('5')>-1){
				week="星期五";
			}else if(week.indexOf('6')>-1){
				week="星期六";
			}
		}
		String sendTime = "每周 "+week+":"+lri.getSendTime()+":00";
		
		
		request.setAttribute("linkReport", lri);
		request.setAttribute("listList", linkList);
		request.setAttribute("sendTime", sendTime);
		return "/linkReport/linkWeekDetail.jsp";
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
}
