package com.afunms.comprehensivereport.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.comprehensivereport.dao.CompreReportUtilDao;
import com.afunms.comprehensivereport.model.CompreReportInfo;
import com.afunms.comprehensivereport.util.CompreReportExport;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

public class CompreReportWeekBusiManager extends BaseManager implements
ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if (action.equals("reportWeekList")) {
			return reportWeekList();
		}
		if(action.equals("compreReportWeekConfig")){
			return compreReportWeekConfig();
		}
		if (action.equals("downloadReportWeek")){
			return downloadReportWeek();
		}
		return null;
	}
	public String reportWeekList(){
		StringBuffer s = new StringBuffer();
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int ii = 0; ii < bids.length; ii++) {
						if (bids[ii].trim().length() > 0) {
							s.append(" bid like '%").append(bids[ii]).append("%' ");
							if (ii != bids.length - 1)
								s.append(" or ");
						}
					}
				}
			}
		}
		// InterfaceTempDao interfaceDao = new InterfaceTempDao();
		List list = new ArrayList();
		HostNodeDao dao = new HostNodeDao();
		list = dao.loadNetworkByBid(4, current_user.getBusinessids());
		
		List interfaceList = new ArrayList();
		PortconfigDao portconfigDao = new PortconfigDao();
		try {
			interfaceList = portconfigDao.getAllBySms();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portconfigDao.close();
		}
		BusinessDao businessDao = new BusinessDao();
		List allbusiness = null;
		try {
			allbusiness = businessDao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("allbusiness", allbusiness);
		request.setAttribute("list", list);
		request.setAttribute("interfaceList", interfaceList);
		return "/capreport/comprehensive/compreReportWeekBusi.jsp";
	}
	public String compreReportWeekConfig(){
		String id = this.getParaValue("id");
		CompreReportUtilDao cru = new CompreReportUtilDao();
		CompreReportInfo cri = cru.findById(Integer.valueOf(id));
		cru.close();
		String ids = cri.getIds();
		String[] idValue = null;
		if (ids != null && !ids.equals("null") && !ids.equals("")) {
			idValue = new String[ids.split(",").length];
			idValue = ids.split(",");
		}
		List<String> list = new ArrayList<String>();
		if (idValue != null) {
			for (int i = 0; i < idValue.length; i++) {
				list.add(idValue[i]);
			}
		}
		String week = cri.getSendTime2();
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
		String sendTime = "每周 "+week+":"+cri.getSendTime()+":00";
		
		request.setAttribute("compreReport", cri);
		request.setAttribute("list", list);
		request.setAttribute("sendTime", sendTime);
		return "/capreport/comprehensive/compreReportWeekBusiDetail.jsp";
	}
	public String downloadReportWeek(){
		String ids=getParaValue("ids");
		String type=getParaValue("type");
		String reportType=getParaValue("reportType");
		String business=getParaValue("business");
		String exportType=getParaValue("exportType");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (ids==null||ids.equals("")||ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return null;
    		CompreReportInfo report=new CompreReportInfo();
    		CompreReportUtilDao dao=new CompreReportUtilDao();
    		report=(CompreReportInfo) dao.findByID(id);
    		ids=report.getIds();
		}
		if(business!=null){
			String[] busi = business.split(",");
			business = "";
			for(int i=0;i<busi.length;i++){
				if(!"".equals(busi[i])){
					business = business + busi[i]+",";
				}
			}
			business = business.substring(0, business.length()-1);
		}
		String filename="";
		if ("hostNet".equalsIgnoreCase(type)) {
			if (exportType.equals("xls")) {
				filename="/temp/compreWeekBusi_report.xls";
			}else if (exportType.equals("doc")) {
				filename="/temp/compreWeekBusi_report.doc";
			}else if (exportType.equals("pdf")) {
				filename="/temp/compreWeekBusi_report.pdf";
			}
		}
		String filePath=ResourceCenter.getInstance().getSysPath()+filename;
		String startTime=getParaValue("startdate");
		String[][] weekDay = getWeekDate(startTime);
		CompreReportExport export=new CompreReportExport();
		export.exportReportByDay(ids, type,reportType, filePath, weekDay,exportType,business);
		request.setAttribute("filename", filePath);
		return "/capreport/net/download.jsp";
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
