package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.comprehensivereport.dao.CompreReportUtilDao;
import com.afunms.comprehensivereport.model.CompreReportInfo;
import com.afunms.comprehensivereport.util.CompreReportHelper;
import com.afunms.comprehensivereport.util.CompreReportStatic;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class CompreReportAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {

	public void execute(String action) {
		// TODO Auto-generated method stub
		if (action.equals("saveCompreReportDayOption")) {
			saveCompreReportDayOption();
		}
		if(action.equals("loadCompreReportDayList")){
			loadCompreReportDayList();
		}
		if(action.equals("executeReport")){
			executeReport();
		}
		if(action.equals("executeReportWeek")){
			executeReportWeek();
		}
	}
	public void executeReport() {
		String ids = request.getParameter("ids");
    	String business = request.getParameter("business");
    	if(ids == null || ids.equals("") || ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return;
    		CompreReportInfo cri = new CompreReportInfo();
    		CompreReportUtilDao cru = new CompreReportUtilDao();
    		cri = (CompreReportInfo) cru.findById(Integer.valueOf(id));
    		//cri ortInfo) cru.findById(Integer.valueOf(id));
    		cru.close();
    		ids = cri.getIds();
    		business = cri.getNodeid();
		}
    	String startTime = request.getParameter("startdate");
    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String toTime = startTime;
    	if (startTime == null) {
			startTime = sdFormat.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + " 00:00:00";
		}
		if (toTime == null) {
			toTime = sdFormat.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + " 23:59:59";
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
    	
    	
    	List hostPingList=new ArrayList();
    	List hostResponseList=new ArrayList();
    	List hostCpuList=new ArrayList();
    	List hostMemList=new ArrayList();
    	List hostDiskList=new ArrayList();
    	
    	List netPingList=new ArrayList();
    	List netResponseList=new ArrayList();
    	List netCpuList=new ArrayList();
    	List netMemList=new ArrayList();
		List netUtilInList=new ArrayList();
		List netUtilOutList=new ArrayList();
		
		
		StringBuffer hostPingHtml=new StringBuffer();
		StringBuffer hostCpuHtml=new StringBuffer();
		StringBuffer hostMemHtml=new StringBuffer();
		StringBuffer hostResponseHtml=new StringBuffer();
		StringBuffer hostDiskHtml=new StringBuffer();
		
		StringBuffer netPingHtml=new StringBuffer();
		StringBuffer netCpuHtml=new StringBuffer();
		StringBuffer netMemHtml=new StringBuffer();
		StringBuffer netResponseHtml=new StringBuffer();
		StringBuffer netUtilInHtml=new StringBuffer();
		StringBuffer netUtilOutHtml=new StringBuffer();
		
		CompreReportHelper helper =new CompreReportHelper();
		HashMap valueMap=helper.getAllValue(ids, startTime, toTime,business);
    	HashMap netMap = (HashMap)valueMap.get("net");
    	HashMap hostMap = (HashMap)valueMap.get("host");
    	
    	String[][] alarmTableHost = helper.getDevTableData("host",startTime,toTime,business);
    	StringBuffer alarmTableHostHtml=new StringBuffer();
    	String[][] alarmTableNet = helper.getDevTableData("net",startTime,toTime,business);
    	StringBuffer alarmTableNetHtml=new StringBuffer();
    	alarmTableHostHtml = getAlarmDetailHtml(alarmTableHost,alarmTableHostHtml,"服务器告警详细信息");
    	alarmTableNetHtml = getAlarmDetailHtml(alarmTableNet,alarmTableNetHtml,"网络设备告警详细信息");
    	String[][] alarmTable = helper.gettableData(startTime,toTime,business);
    	StringBuffer alarmTableHtml=new StringBuffer();
    	alarmTableHtml = getAlarmDetailTotalHtml(alarmTable,alarmTableHtml,"告警汇总信息");
    	List alarmPieList = helper.getLevelPieData(startTime,toTime,business);
    	String alarmPie = makeAmChartDataForPie(alarmPieList);
    	List alarmDayHourList = helper.getDayAlarmData(startTime,toTime,business);
    	String alarmDayHour = makeAmChartDataLine(alarmDayHourList);
    	
    	hostCpuList = (List)hostMap.get("cpu");
    	if(hostCpuList!=null&&hostCpuList.size()>0){
    		hostCpuHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>利用率(%)</td></tr>");
    		for(int i = 0 ; i < hostCpuList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostCpuList.get(i);
    			setInnerHtml(hostCpuHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostCpuHtml.append("</table>");
    	}
    	hostMemList = (List)hostMap.get("mem");
    	if(hostMemList!=null&&hostMemList.size()>0){
    		hostMemHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>内存利用率(%)</td></tr>");
    		for(int i = 0 ; i < hostMemList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostMemList.get(i);
    			setInnerHtml(hostMemHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostMemHtml.append("</table>");
    	}
    	hostPingList = (List)hostMap.get("ping");
    	if(hostPingList!=null&&hostPingList.size()>0){
    		hostPingHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>连通率(%)</td></tr>");
    		for(int i = 0 ; i < hostPingList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostPingList.get(i);
    			setInnerHtml(hostPingHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostPingHtml.append("</table>");
    	}
    	hostResponseList = (List)hostMap.get("response");
    	if(hostResponseList!=null&&hostResponseList.size()>0){
    		hostResponseHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>响应时间(ms)</td></tr>");
    		for(int i = 0 ; i < hostResponseList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostResponseList.get(i);
    			setInnerHtml(hostResponseHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostResponseHtml.append("</table>");
    	}
    	hostDiskList = (List)hostMap.get("disk");
    	if(hostDiskList!=null&&hostDiskList.size()>0){
    		hostDiskHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>磁盘利用率</td></tr>");
    		for(int i = 0 ; i < hostDiskList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostDiskList.get(i);
    			setInnerHtml(hostDiskHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostDiskHtml.append("</table>");
    	}
    	
    	netCpuList = (List)netMap.get("cpu");
    	if(netCpuList!=null&&netCpuList.size()>0){
    		netCpuHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>CPU利用率(%)</td></tr>");
    		for(int i = 0 ; i < netCpuList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netCpuList.get(i);
    			setInnerHtml(netCpuHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netCpuHtml.append("</table>");
    	}
    	netMemList = (List)netMap.get("mem");
    	if(netMemList!=null&&netMemList.size()>0){
    		netMemHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>内存利用率(%)</td></tr>");
    		for(int i = 0 ; i < netMemList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netMemList.get(i);
    			setInnerHtml(netMemHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netMemHtml.append("</table>");
    	}
    	netPingList = (List)netMap.get("ping");
    	if(netPingList!=null&&netPingList.size()>0){
    		netPingHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>连通率(%)</td></tr>");
    		for(int i = 0 ; i < netPingList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netPingList.get(i);
    			setInnerHtml(netPingHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netPingHtml.append("</table>");
    	}
    	netResponseList = (List)netMap.get("response");
    	if(netResponseList!=null&&netResponseList.size()>0){
    		netResponseHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>响应时间(ms)</td></tr>");
    		for(int i = 0 ; i < netResponseList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netResponseList.get(i);
    			setInnerHtml(netResponseHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netResponseHtml.append("</table>");
    	}
    	netUtilInList = (List)netMap.get("utilIn");
    	if(netUtilInList!=null&&netUtilInList.size()>0){
    		netUtilInHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>入口流速(KB/s)</td></tr>");
    		for(int i = 0 ; i < netUtilInList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netUtilInList.get(i);
    			setInnerHtml(netUtilInHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netUtilInHtml.append("</table>");
    	}
    	netUtilOutList = (List)netMap.get("utilOut");
    	if(netUtilOutList!=null&&netUtilOutList.size()>0){
    		netUtilOutHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>出口流速(KB/s)</td></tr>");
    		for(int i = 0 ; i < netUtilOutList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netUtilOutList.get(i);
    			setInnerHtml(netUtilOutHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netUtilOutHtml.append("</table>");
    	}
    	
    	String hostPing = makeAmChartDataForColumn(hostPingList);
    	String hostCpu = makeAmChartDataForColumn(hostCpuList);
    	String hostMem = makeAmChartDataForColumn(hostMemList);
    	String hostResponse = makeAmChartDataForColumn(hostResponseList);
    	String hostDisk = makeAmChartDataForColumn(hostDiskList);
    	String netPing = makeAmChartDataForColumn(netPingList);
    	String netCpu = makeAmChartDataForColumn(netCpuList);
    	String netMem = makeAmChartDataForColumn(netMemList);
    	String netResponse = makeAmChartDataForColumn(netResponseList);
    	String netUtilIn = makeAmChartDataForColumn(netUtilInList);
    	String netUtilOut = makeAmChartDataForColumn(netUtilOutList);
    	
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("hostPing", hostPing);
		map.put("hostCpu", hostCpu);
		map.put("hostMem", hostMem);
		map.put("hostResponse", hostResponse);
		map.put("hostDisk", hostDisk);
		
		map.put("netPing", netPing);
		map.put("netCpu", netCpu);
		map.put("netMem", netMem);
		map.put("netResponse", netResponse);
		map.put("netUtilIn", netUtilIn);
		map.put("netUtilOut", netUtilOut);
		
		
		map.put("hostPingHtml", hostPingHtml.toString());
		map.put("hostCpuHtml", hostCpuHtml.toString());
		map.put("hostMemHtml", hostMemHtml.toString());
		map.put("hostResponseHtml", hostResponseHtml.toString());
		map.put("hostDiskHtml", hostDiskHtml.toString());
		
		map.put("netPingHtml", netPingHtml.toString());
		map.put("netCpuHtml", netCpuHtml.toString());
		map.put("netMemHtml", netMemHtml.toString());
		map.put("netResponseHtml", netResponseHtml.toString());
		map.put("netUtilInHtml", netUtilInHtml.toString());
		map.put("netUtilOutHtml", netUtilOutHtml.toString());
		
		map.put("alarmTableHtml", alarmTableHtml.toString());
		map.put("alarmTableHostHtml", alarmTableHostHtml.toString());
		map.put("alarmTableNetHtml", alarmTableNetHtml.toString());
		map.put("alarmPie",alarmPie);
		map.put("alarmDayHour",alarmDayHour);
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	public void executeReportWeek() {
		String ids = request.getParameter("ids");
    	String business = request.getParameter("business");
    	if(ids == null || ids.equals("") || ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return;
    		CompreReportInfo cri = new CompreReportInfo();
    		CompreReportUtilDao cru = new CompreReportUtilDao();
    		cri = (CompreReportInfo) cru.findById(Integer.valueOf(id));
    		cru.close();
    		ids = cri.getIds();
    		business = cri.getNodeid();
    	}
    	String startTime = request.getParameter("startdate");   	
    	String[][] weekDay = getWeekDate(startTime);
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
    	
    	List hostPingList=new ArrayList();
    	List hostResponseList=new ArrayList();
    	List hostCpuList=new ArrayList();
    	List hostMemList=new ArrayList();
    	List hostDiskList=new ArrayList();
    	
    	List netPingList=new ArrayList();
    	List netResponseList=new ArrayList();
    	List netCpuList=new ArrayList();
    	List netMemList=new ArrayList();
		List netUtilInList=new ArrayList();
		List netUtilOutList=new ArrayList();
		
		
		StringBuffer hostPingHtml=new StringBuffer();
		StringBuffer hostCpuHtml=new StringBuffer();
		StringBuffer hostMemHtml=new StringBuffer();
		StringBuffer hostResponseHtml=new StringBuffer();
		StringBuffer hostDiskHtml=new StringBuffer();
		
		StringBuffer netPingHtml=new StringBuffer();
		StringBuffer netCpuHtml=new StringBuffer();
		StringBuffer netMemHtml=new StringBuffer();
		StringBuffer netResponseHtml=new StringBuffer();
		StringBuffer netUtilInHtml=new StringBuffer();
		StringBuffer netUtilOutHtml=new StringBuffer();
		
		CompreReportHelper helper =new CompreReportHelper();
		HashMap valueMap=helper.getAllValueWeek(ids, weekDay,business);
    	HashMap netMap = (HashMap)valueMap.get("net");
    	HashMap hostMap = (HashMap)valueMap.get("host");
    	
    	String[][] alarmTableHost = helper.getDevTableData("host",weekDay[0][1],weekDay[weekDay.length-1][2],business);
    	StringBuffer alarmTableHostHtml=new StringBuffer();
    	String[][] alarmTableNet = helper.getDevTableData("net",weekDay[0][1],weekDay[weekDay.length-1][2],business);
    	StringBuffer alarmTableNetHtml=new StringBuffer();
    	alarmTableHostHtml = getAlarmDetailHtml(alarmTableHost,alarmTableHostHtml,"服务器告警详细信息");
    	alarmTableNetHtml = getAlarmDetailHtml(alarmTableNet,alarmTableNetHtml,"网络设备告警详细信息");
    	String[][] alarmTable = helper.gettableData(weekDay[0][1],weekDay[weekDay.length-1][2],business);
    	StringBuffer alarmTableHtml=new StringBuffer();
    	alarmTableHtml = getAlarmDetailTotalHtml(alarmTable,alarmTableHtml,"告警汇总信息");
    	List alarmPieList = helper.getLevelPieData(weekDay[0][1],weekDay[weekDay.length-1][2],business);
    	String alarmPie = makeAmChartDataForPie(alarmPieList);
    	Map<String,Integer> alarmWeekList = helper.getWeekAlarmData(weekDay,business);
    	String alarmDayHour = makeAmChartDataLineWeek(alarmWeekList);
    	
    	hostCpuList = (List)hostMap.get("cpu");
    	if(hostCpuList!=null&&hostCpuList.size()>0){
    		hostCpuHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>利用率(%)</td></tr>");
    		for(int i = 0 ; i < hostCpuList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostCpuList.get(i);
    			setInnerHtml(hostCpuHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostCpuHtml.append("</table>");
    	}
    	hostMemList = (List)hostMap.get("mem");
    	if(hostMemList!=null&&hostMemList.size()>0){
    		hostMemHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>内存利用率(%)</td></tr>");
    		for(int i = 0 ; i < hostMemList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostMemList.get(i);
    			setInnerHtml(hostMemHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostMemHtml.append("</table>");
    	}
    	hostPingList = (List)hostMap.get("ping");
    	if(hostPingList!=null&&hostPingList.size()>0){
    		hostPingHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>连通率(%)</td></tr>");
    		for(int i = 0 ; i < hostPingList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostPingList.get(i);
    			setInnerHtml(hostPingHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostPingHtml.append("</table>");
    	}
    	hostResponseList = (List)hostMap.get("response");
    	if(hostResponseList!=null&&hostResponseList.size()>0){
    		hostResponseHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>响应时间(ms)</td></tr>");
    		for(int i = 0 ; i < hostResponseList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostResponseList.get(i);
    			setInnerHtml(hostResponseHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostResponseHtml.append("</table>");
    	}
    	hostDiskList = (List)hostMap.get("disk");
    	if(hostDiskList!=null&&hostDiskList.size()>0){
    		hostDiskHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>磁盘利用率</td></tr>");
    		for(int i = 0 ; i < hostDiskList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)hostDiskList.get(i);
    			setInnerHtml(hostDiskHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		hostDiskHtml.append("</table>");
    	}
    	
    	netCpuList = (List)netMap.get("cpu");
    	if(netCpuList!=null&&netCpuList.size()>0){
    		netCpuHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>CPU利用率(%)</td></tr>");
    		for(int i = 0 ; i < netCpuList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netCpuList.get(i);
    			setInnerHtml(netCpuHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netCpuHtml.append("</table>");
    	}
    	netMemList = (List)netMap.get("mem");
    	if(netMemList!=null&&netMemList.size()>0){
    		netMemHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>内存利用率(%)</td></tr>");
    		for(int i = 0 ; i < netMemList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netMemList.get(i);
    			setInnerHtml(netMemHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netMemHtml.append("</table>");
    	}
    	netPingList = (List)netMap.get("ping");
    	if(netPingList!=null&&netPingList.size()>0){
    		netPingHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>连通率(%)</td></tr>");
    		for(int i = 0 ; i < netPingList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netPingList.get(i);
    			setInnerHtml(netPingHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netPingHtml.append("</table>");
    	}
    	netResponseList = (List)netMap.get("response");
    	if(netResponseList!=null&&netResponseList.size()>0){
    		netResponseHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>响应时间(ms)</td></tr>");
    		for(int i = 0 ; i < netResponseList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netResponseList.get(i);
    			setInnerHtml(netResponseHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netResponseHtml.append("</table>");
    	}
    	netUtilInList = (List)netMap.get("utilIn");
    	if(netUtilInList!=null&&netUtilInList.size()>0){
    		netUtilInHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>入口流速(KB/s)</td></tr>");
    		for(int i = 0 ; i < netUtilInList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netUtilInList.get(i);
    			setInnerHtml(netUtilInHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netUtilInHtml.append("</table>");
    	}
    	netUtilOutList = (List)netMap.get("utilOut");
    	if(netUtilOutList!=null&&netUtilOutList.size()>0){
    		netUtilOutHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>IP</td><td  class='body-data-title' height=21>出口流速(KB/s)</td></tr>");
    		for(int i = 0 ; i < netUtilOutList.size(); i++){
    			CompreReportStatic crs = new CompreReportStatic();
    			crs = (CompreReportStatic)netUtilOutList.get(i);
    			setInnerHtml(netUtilOutHtml,crs.getIp(),crs.getValue()+"",crs.getUnit());
    		}
    		netUtilOutHtml.append("</table>");
    	}
    	
    	String hostPing = makeAmChartDataForLineWeek(hostPingList);
    	String hostCpu = makeAmChartDataForLineWeek(hostCpuList);
    	String hostMem = makeAmChartDataForLineWeek(hostMemList);
    	String hostResponse = makeAmChartDataForLineWeek(hostResponseList);
    	String hostDisk = makeAmChartDataForLineWeek(hostDiskList);
    	String netPing = makeAmChartDataForLineWeek(netPingList);
    	String netCpu = makeAmChartDataForLineWeek(netCpuList);
    	String netMem = makeAmChartDataForLineWeek(netMemList);
    	String netResponse = makeAmChartDataForLineWeek(netResponseList);
    	String netUtilIn = makeAmChartDataForLineWeek(netUtilInList);
    	String netUtilOut = makeAmChartDataForLineWeek(netUtilOutList);
    	
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("hostPing", hostPing);
		map.put("hostCpu", hostCpu);
		map.put("hostMem", hostMem);
		map.put("hostResponse", hostResponse);
		map.put("hostDisk", hostDisk);
		
		map.put("netPing", netPing);
		map.put("netCpu", netCpu);
		map.put("netMem", netMem);
		map.put("netResponse", netResponse);
		map.put("netUtilIn", netUtilIn);
		map.put("netUtilOut", netUtilOut);
		
		
		map.put("hostPingHtml", hostPingHtml.toString());
		map.put("hostCpuHtml", hostCpuHtml.toString());
		map.put("hostMemHtml", hostMemHtml.toString());
		map.put("hostResponseHtml", hostResponseHtml.toString());
		map.put("hostDiskHtml", hostDiskHtml.toString());
		
		map.put("netPingHtml", netPingHtml.toString());
		map.put("netCpuHtml", netCpuHtml.toString());
		map.put("netMemHtml", netMemHtml.toString());
		map.put("netResponseHtml", netResponseHtml.toString());
		map.put("netUtilInHtml", netUtilInHtml.toString());
		map.put("netUtilOutHtml", netUtilOutHtml.toString());
		
		map.put("alarmTableHtml", alarmTableHtml.toString());
		map.put("alarmTableHostHtml", alarmTableHostHtml.toString());
		map.put("alarmTableNetHtml", alarmTableNetHtml.toString());
		map.put("alarmPie",alarmPie);
		map.put("alarmDayHour",alarmDayHour);
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	public void loadCompreReportDayList(){
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		StringBuffer html = new StringBuffer();
		
		String id = this.getParaValue("id");
		String type = this.getParaValue("type");
		String reportType= this.getParaValue("reportType");
		String sql =null;
		if(reportType!=null&&!"".equals(reportType)){
			sql = "select id,name,email,sendTime,sendTime2 from nms_compreReport_resources where type='"+type+"' and reportType='"+reportType+"'";
		}else{
			sql = "select id,name,email,sendTime,sendTime2 from nms_compreReport_resources where type='"+type+"'";
		}
		List<CompreReportInfo> list = new ArrayList<CompreReportInfo>();
		try {
			if (id != null && !id.equals("")) {
				String sql1 = "delete from nms_compreReport_resources where id=" + id;
				try {
					dbManager.executeUpdate(sql1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			rs = dbManager.executeQuery(sql);
			while (rs.next()) {
				CompreReportInfo cri = new CompreReportInfo();
				cri.setId(rs.getInt("id"));
				cri.setName(rs.getString("name"));
				cri.setEmail(rs.getString("email"));
				cri.setSendTime(rs.getString("sendTime"));
				cri.setSendTime2(rs.getString("sendTime2"));
				list.add(cri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();
		}
		if (list != null && list.size() > 0) {
			html.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>序号</td><td align='center' class='body-data-title' height=21>模板名称</td><td align='center' class='body-data-title' height=21>接收邮箱</td><td align='center' class='body-data-title' height=21>发送时间</td><td align='center' class='body-data-title' height=21>详情</td>");
			for (int i = 0; i < list.size(); i++) {
				CompreReportInfo cri = new CompreReportInfo();
				cri = list.get(i);
				html.append("<tr><td  align='center' height=19>");
				html.append(i + 1);
				html.append("</td><td align='center' height=19>");
				html.append(cri.getName());
				html.append("</td><td align='center' height=19>");
				html.append(cri.getEmail());
				html.append("</td><td align='center' height=19>");
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
					html.append("每周 "+week+" "+cri.getSendTime()+":00");
				}else{
					html.append("每天:"+cri.getSendTime()+":00");
				}
				html.append("</td><td align='center' height=19>");
				String path = request.getContextPath();
				String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
				html.append("<img src='" + basePath + "resource/image/vcf.gif' border='0' onClick='createWin(" + cri.getId()
						+ ")' title='查看模板详细信息'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/viewreport.gif' border='0' onClick='preview("
						+ cri.getId()
						+ ")' title='预览模板报表'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/delete.gif' border='0' onClick='deleteItem("
						+ cri.getId() + ")' title='删除模板'/></td></tr>");
			}
			html.append("</table>");
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", html.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	public void saveCompreReportDayOption() {
		String dataStr = "保存成功！";
		String ids = request.getParameter("ids");
		
		String startTime = request.getParameter("startdate");
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (startTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + " 00:00:00";
		}
		String toTime = request.getParameter("todate");
		if (toTime == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + " 23:59:59";
		}
		
		DBManager dbManager = new DBManager();
		String business = this.getParaValue("business");
		business = business==null?"":business;
		String reportname = this.getParaValue("report_name");
		String exporttype = request.getParameter("exporttype");
		String username = this.getParaValue("recievers_name");
		String tile = this.getParaValue("tile");
		String desc = this.getParaValue("desc");
		String reportType= this.getParaValue("reportType");
		try {
			business = new String(business.getBytes("iso8859-1"), "UTF-8");
			reportname = new String(reportname.getBytes("iso8859-1"), "UTF-8");
			username = new String(username.getBytes("iso8859-1"), "UTF-8");
			tile = new String(tile.getBytes("iso8859-1"), "UTF-8");
			desc = new String(desc.getBytes("iso8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sendtimeweek = request.getParameter("sendtimeweek");
		String sendtimehou = request.getParameter("sendtimehou");
		String recieversId = this.getParaValue("recievers_id");
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(recieversId.substring(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < userList.size(); i++) {
			User vo = (User) userList.get(i);
			buf.append(vo.getEmail());
			buf.append(",");
		}
		
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdFormat.format(cc);
		StringBuffer sql = new StringBuffer();
		//sql
		if(business.equals("")){
			sql.append("insert into nms_compreReport_resources(name,type,userName,email,emailTitle,emailContent,attachmentFormat,ids,reportType,sendTime,sendTime2) values('");
		}else{
			sql.append("insert into nms_compreReport_resources(name,type,userName,email,emailTitle,emailContent,attachmentFormat,ids,reportType,sendTime,sendTime2,nodeid) values('");
		}
		sql.append(reportname + "','");
		sql.append("hostNet','");
		sql.append(username + "','");
		sql.append(buf.toString()+"','");
		sql.append(tile+"','");
		sql.append(desc+"','");
		sql.append(exporttype+"','");
		sql.append(ids+"','");
		sql.append(reportType+"','");
		sql.append(sendtimehou+"','" );
		sql.append(sendtimeweek+"'");
		if(business.equals("")){
			sql.append(")");
		}else{
			sql.append(",'"+business+"')");
			
		}
		try {
			dbManager.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			dataStr = "保存失败！！！";
		} finally {
			dbManager.close();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", dataStr);
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	/**
	 * 以表格的形式显示当前的利用率
	 * @param html
	 * @param ip
	 * @param cur
	 */
	private void setInnerHtml(StringBuffer html,String ip,String cur,String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center'>"
				+ ip + "</td>");
		html.append("<td align='center' height=21>"+cur.replace("%", "") + unit+"</td></tr>");
	}
	private String makeAmChartDataForPie(List list){
		String[] color = {"#FF9E01","#FF6600","#FF0000","#FCD202","#F8FF01"};
		StringBuffer sb=new StringBuffer();
		String data="";
		List value = new ArrayList();
		List ip = new ArrayList();
		if(list!=null && list.size()>0){
			for(int i=0; i<list.size();i++){
				CompreReportStatic crs = (CompreReportStatic)list.get(i);
				value.add(crs.getValue());
				ip.add(crs.getIp());
			}
		}else{
			data = "0";
			return data;
		}
		sb.append("<pie>");
		for(int i = 0; i<ip.size(); i++){
			sb.append("<slice title='");
			sb.append((String)ip.get(i));
			sb.append("' color='"+color[i]+"'>");
			sb.append(((Double)value.get(i)).intValue());
			sb.append("</slice>");
		}
		sb.append("</pie>");
		data = sb.toString();
		return data;
	}
	
	private String makeAmChartDataForColumn(List list){
		StringBuffer sb=new StringBuffer();
		String data="";
		List value = new ArrayList();
		List ip = new ArrayList();
		if(list!=null && list.size()>0){
			for(int i=0; i<list.size();i++){
				CompreReportStatic crs = (CompreReportStatic)list.get(i);
				value.add(crs.getValue());
				ip.add(crs.getIp());
			}
		}else{
			data = "0";
			return data;
		}
		String[] colorStr=new String[] {"#FF6600","#FCD202","#B0DE09","#0D8ECF","#A52A2A","#33FF33","#FF0033","#9900FF","#FFFF00","#0000FF","#A52A2A","#23f266"};
		
		sb.append("<chart><series>");
		for(int i = 0; i<ip.size(); i++){
			sb.append("<value xid='");
			sb.append(i);
			sb.append("'>");
			sb.append((String)ip.get(i));
			sb.append("</value>");
		}
		sb.append("</series><graphs>");
		sb.append("<graph>");
		for(int i=0;i<value.size();i++){
			sb.append("<value xid='");
			sb.append(i);
			sb.append("' color='"+colorStr[i]+"'>");
			sb.append((Double)value.get(i));
			sb.append("</value>");
		}
		sb.append("</graph>");
		sb.append("</graphs></chart>");
		data = sb.toString();
		return data;
	}
	private String makeAmChartDataLine(List list){
		StringBuffer sb=new StringBuffer();
		String data="";
		if (list != null && list.size() > 0) {
			sb.append("<chart><series>");
			for (int k = 0; k < list.size(); k++) {
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(k);
				sb.append("</value>");

			}
			sb.append("</series><graphs><graph>");
			for (int k = 0; k < list.size(); k++) {
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append((Integer)list.get(k));
				sb.append("</value>");
			}
			sb.append("</graph></graphs></chart>");
			data = sb.toString();
		} else {
			data = "0";
		}
		return data;
	}
	private String makeAmChartDataLineWeek(Map<String,Integer> map){
		StringBuffer sb=new StringBuffer();
		String data="";
		List<String> date = new ArrayList<String>();
		List<Integer> value = new ArrayList<Integer>();
		Set<Map.Entry<String, Integer>> set = map.entrySet();
		for (Iterator<Map.Entry<String, Integer>> it = set.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			date.add(entry.getKey());
			value.add(entry.getValue());
		}
		if (date != null && date.size() > 0) {
			sb.append("<chart><series>");
			for (int k = 0; k < date.size(); k++) {
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(date.get(k));
				sb.append("</value>");

			}
			sb.append("</series><graphs><graph>");
			for (int k = 0; k < value.size(); k++) {
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(value.get(k));
				sb.append("</value>");
			}
			sb.append("</graph></graphs></chart>");
			data = sb.toString();
		} else {
			data = "0";
		}
		return data;
	}
	
	private StringBuffer getAlarmDetailHtml(String[][] table,StringBuffer html,String total){
		if(table!=null&& table.length>0){
			html.append("<table width='100%' cellpadding=8 height=296 border=1 bordercolor='#7599d7' style='border: 1px solid #7599d7; border-collapse: collapse;'>" +
    				"<tr><td colspan="+table[0].length+" height='25' align=center>"+total+"</td></tr>");
    		for(int i =0; i<table.length;i++){
    			html.append("<tr>");
    			for (int j = 0; j < table[i].length; j++) {
    				html.append("<td height='25' align=center>"+table[i][j]+"</td>");
    			}
    			html.append("</tr>");
    		}
    		html.append("</table>");
    	}
		return html;
	}
	private StringBuffer getAlarmDetailTotalHtml(String[][] table,StringBuffer html,String total){
		if(table!=null&& table.length>0){
			html.append("<table width='100%' cellpadding=8 height=296 border=1 bordercolor='#7599d7' style='border: 1px solid #7599d7; border-collapse: collapse;'>" +
    				"<tr><td colspan="+table[0].length+" height='25' align=center>"+total+"</td></tr>");
    		for(int i =0; i<table.length;i++){
    			html.append("<tr>");
    			if(i==0){
    				for (int j = 0; j < table[i].length; j++) {
    					String[] color = new String[]{"","blue","yellow","orange","red"};
    					html.append("<td height='25' bgcolor='"+color[j]+"' align=center>"+table[i][j]+"</td>");
    				}
    			}else{
	    			for (int j = 0; j < table[i].length; j++) {
	    				html.append("<td height='25' align=center>"+table[i][j]+"</td>");
	    			}
    			}
    			html.append("</tr>");
    		}
    		html.append("</table>");
    	}
		return html;
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
	private String makeAmChartDataForLineWeek(List list){
		String[] colorStr=new String[] {"#FF6600","#FCD202","#B0DE09","#0D8ECF","#A52A2A","#33FF33","#FF0033","#9900FF","#FFFF00","#0000FF","#A52A2A","#23f266"};
		StringBuffer sb=new StringBuffer();
		String data="";
		List weekValue = new ArrayList();
		List ip = new ArrayList();
		if(list!=null && list.size()>0){
			for(int i=0; i<list.size();i++){
				CompreReportStatic crs = (CompreReportStatic)list.get(i);
				ip.add(crs.getIp());
				weekValue.add(crs.getWeekValues());
			}
		}else{
			data = "0";
			return data;
		}
		Map<String,Double> maps = (Map<String,Double>)weekValue.get(0);
		List<String> xdata = new ArrayList<String>();
		for(Map.Entry<String,Double>  entry: maps.entrySet()){    
			xdata.add(entry.getKey());
		}		
		sb.append("<chart><series>");
		for(int i = 0; i<xdata.size(); i++){
			sb.append("<value xid='");
			sb.append(i);
			sb.append("'>");
			sb.append(xdata.get(i));
			sb.append("</value>");
		}
		sb.append("</series><graphs>");
		for(int i = 0; i<ip.size(); i++){
			sb.append("<graph title='"+(String)ip.get(i)+"' line_width='2' bullet='round' color='" + colorStr[i]+ "'>");
			for(int j=0;j<xdata.size();j++){
				sb.append("<value xid='");
				sb.append(j);
				sb.append("'>");
				sb.append(((Map<String,Double>)weekValue.get(i)).get(xdata.get(j)));
				sb.append("</value>");
			}
			sb.append("</graph>");
		}
		sb.append("</graphs></chart>");
		data = sb.toString();
		return data;
	}
}
