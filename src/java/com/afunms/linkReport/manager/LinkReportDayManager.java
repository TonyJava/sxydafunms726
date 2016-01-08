package com.afunms.linkReport.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class LinkReportDayManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("list".equals(action)){
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
		return "/linkReport/day.jsp";
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
				filename="/temp/linkDay_report.xls";
			}else if (exportType.equals("doc")) {
				filename="/temp/linkDay_report.doc";
			}else if (exportType.equals("pdf")) {
				filename="/temp/linkDay_report.pdf";
			}
		}
		String filePath=ResourceCenter.getInstance().getSysPath()+filename;
		String startTime=getParaValue("startdate");
		String toTime=startTime;
		if (startTime == null) {
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + " 00:00:00";
		}
		if (toTime == null) {
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + " 23:59:59";
		}
		LinkReportExport export = new LinkReportExport();
		export.exportReportByDay(ids, type,reportType, filePath, startTime, toTime,exportType,terms);
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
		String sendTime = "每天:"+lri.getSendTime()+":00";
		
		request.setAttribute("linkReport", lri);
		request.setAttribute("listList", linkList);
		request.setAttribute("sendTime", sendTime);
		return "/linkReport/linkdayDetail.jsp";
	}
}
