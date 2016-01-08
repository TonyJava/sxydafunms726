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
import com.afunms.linkReport.manager.LinkReportConfigDao;
import com.afunms.linkReport.manager.LinkReportInfo;
import com.afunms.linkReport.util.LinkReportHelper;
import com.afunms.linkReport.util.LinkReportStatic;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

public class LinkReportAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {

	public void execute(String action) {
		// TODO Auto-generated method stub
		if (action.equals("saveLinkReportOption")) {
			saveLinkReportOption();
		}
		if(action.equals("loadLinkReportList")){
			loadLinkReportList();
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
    	String terms = request.getParameter("terms");
    	
    	if(ids == null || ids.equals("") || ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return;
    		LinkReportInfo cri = new LinkReportInfo();
    		LinkReportConfigDao cru = new LinkReportConfigDao();
    		cri = (LinkReportInfo) cru.findById(Integer.valueOf(id));
    		cru.close();
    		ids = cri.getIds();
    		terms = cri.getTerms();
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
		LinkReportHelper lrh = new LinkReportHelper();
		
		
		List linkUpList = new ArrayList();
    	List linkDownList = new ArrayList();
    	List linkBandwidthList = new ArrayList();
    	List linkBandTrendList = new ArrayList();
    	List linkUsabilityList = new ArrayList();
    	
    	StringBuffer linkUpHtml = new StringBuffer();
    	StringBuffer linkDownHtml = new StringBuffer();
    	StringBuffer linkBandwidthHtml = new StringBuffer();
    	StringBuffer linkBandTrendHtml = new StringBuffer();
    	StringBuffer linkUsabilityHtml = new StringBuffer();
		
    	
    	HashMap valueMap = lrh.getLinkReportDay(ids,terms,startTime,toTime);
    	
    	linkUpList = (List)valueMap.get("up");
    	if(linkUpList!=null&&linkUpList.size()>0){
    		linkUpHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>上行流速(KB/秒)</td></tr>");
    		for(int i = 0 ; i < linkUpList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkUpList.get(i);
    			setInnerHtml(linkUpHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkUpHtml.append("</table>");
    	}
    	
    	linkDownList = (List)valueMap.get("down");
    	if(linkDownList!=null&&linkDownList.size()>0){
    		linkDownHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>下行流速(KB/秒)</td></tr>");
    		for(int i = 0 ; i < linkDownList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkDownList.get(i);
    			setInnerHtml(linkDownHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkDownHtml.append("</table>");
    	}
    	
    	linkBandwidthList = (List)valueMap.get("bandwidth");
    	if(linkBandwidthList!=null&&linkBandwidthList.size()>0){
    		linkBandwidthHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>带宽</td></tr>");
    		for(int i = 0 ; i < linkBandwidthList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkBandwidthList.get(i);
    			setInnerHtml(linkBandwidthHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkBandwidthHtml.append("</table>");
    	}
    	
    	linkBandTrendList = (List)valueMap.get("bandtrend");
    	if(linkBandTrendList!=null&&linkBandTrendList.size()>0){
    		linkBandTrendHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>带宽趋势</td></tr>");
    		for(int i = 0 ; i < linkBandTrendList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkBandTrendList.get(i);
    			setInnerHtml(linkBandTrendHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkBandTrendHtml.append("</table>");
    	}
    	
    	linkUsabilityList = (List)valueMap.get("usability");
    	if(linkUsabilityList!=null&&linkUsabilityList.size()>0){
    		linkUsabilityHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>可用性</td></tr>");
    		for(int i = 0 ; i < linkUsabilityList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkUsabilityList.get(i);
    			setInnerHtml(linkUsabilityHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkUsabilityHtml.append("</table>");
    	}
    	
    	String upChart = makeAmChartDataForColumn(linkUpList);
    	String downChart = makeAmChartDataForColumn(linkDownList);
    	String bandwidthChart = makeAmChartDataForColumn(linkBandwidthList);
    	String bandtrendChart = makeAmChartDataForColumn(linkBandTrendList);
    	String usabilityChart = makeAmChartDataForColumn(linkUsabilityList);
    	
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("upChart", upChart);
		map.put("downChart", downChart);
		map.put("bandwidthChart", bandwidthChart);
		map.put("bandtrendChart", bandtrendChart);
		map.put("usabilityChart", usabilityChart);		
		
		map.put("linkUpHtml", linkUpHtml.toString());
		map.put("linkDownHtml", linkDownHtml.toString());
		map.put("linkBandwidthHtml", linkBandwidthHtml.toString());
		map.put("linkBandTrendHtml", linkBandTrendHtml.toString());
		map.put("linkUsabilityHtml", linkUsabilityHtml.toString());
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	
	
	public void executeReportWeek() {
		String ids = request.getParameter("ids");
		
		String terms = request.getParameter("terms");
    	
    	if(ids == null || ids.equals("") || ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return;
    		LinkReportInfo cri = new LinkReportInfo();
    		LinkReportConfigDao cru = new LinkReportConfigDao();
    		cri = (LinkReportInfo) cru.findById(Integer.valueOf(id));
    		cru.close();
    		ids = cri.getIds();
    		terms = cri.getTerms();
		}
    	String startTime = request.getParameter("startdate");   	
    	
    	String[][] weekDay = getWeekDate(startTime);
    	
    	LinkReportHelper lrh = new LinkReportHelper();
		
		
		List linkUpList = new ArrayList();
    	List linkDownList = new ArrayList();
    	List linkBandwidthList = new ArrayList();
    	List linkBandTrendList = new ArrayList();
    	List linkUsabilityList = new ArrayList();
    	
    	StringBuffer linkUpHtml = new StringBuffer();
    	StringBuffer linkDownHtml = new StringBuffer();
    	StringBuffer linkBandwidthHtml = new StringBuffer();
    	StringBuffer linkBandTrendHtml = new StringBuffer();
    	StringBuffer linkUsabilityHtml = new StringBuffer();
		
    	
    	HashMap valueMap = lrh.getLinkReportWeek(ids,terms,weekDay);
    	
    	linkUpList = (List)valueMap.get("up");
    	if(linkUpList!=null&&linkUpList.size()>0){
    		linkUpHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>上行流速(KB/秒)</td></tr>");
    		for(int i = 0 ; i < linkUpList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkUpList.get(i);
    			setInnerHtml(linkUpHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkUpHtml.append("</table>");
    	}
    	
    	linkDownList = (List)valueMap.get("down");
    	if(linkDownList!=null&&linkDownList.size()>0){
    		linkDownHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>下行流速(KB/秒)</td></tr>");
    		for(int i = 0 ; i < linkDownList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkDownList.get(i);
    			setInnerHtml(linkDownHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkDownHtml.append("</table>");
    	}
    	
    	linkBandwidthList = (List)valueMap.get("bandwidth");
    	if(linkBandwidthList!=null&&linkBandwidthList.size()>0){
    		linkBandwidthHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>带宽</td></tr>");
    		for(int i = 0 ; i < linkBandwidthList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkBandwidthList.get(i);
    			setInnerHtml(linkBandwidthHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkBandwidthHtml.append("</table>");
    	}
    	
    	linkBandTrendList = (List)valueMap.get("bandtrend");
    	if(linkBandTrendList!=null&&linkBandTrendList.size()>0){
    		linkBandTrendHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>带宽趋势</td></tr>");
    		for(int i = 0 ; i < linkBandTrendList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkBandTrendList.get(i);
    			setInnerHtml(linkBandTrendHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkBandTrendHtml.append("</table>");
    	}
    	
    	linkUsabilityList = (List)valueMap.get("usability");
    	if(linkUsabilityList!=null&&linkUsabilityList.size()>0){
    		linkUsabilityHtml.append("<table border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=50% height=21>链路名称</td><td  class='body-data-title' height=21>可用性</td></tr>");
    		for(int i = 0 ; i < linkUsabilityList.size(); i++){
    			LinkReportStatic lrs = new LinkReportStatic();
    			lrs = (LinkReportStatic)linkUsabilityList.get(i);
    			setInnerHtml(linkUsabilityHtml,lrs.getLinkName(),lrs.getValue()+"",lrs.getUnit());
    		}
    		linkUsabilityHtml.append("</table>");
    	}
    	
    	String upChart = makeAmChartDataForLineWeek(linkUpList);
    	String downChart = makeAmChartDataForLineWeek(linkDownList);
    	String bandwidthChart = makeAmChartDataForLineWeek(linkBandwidthList);
    	String bandtrendChart = makeAmChartDataForLineWeek(linkBandTrendList);
    	String usabilityChart = makeAmChartDataForLineWeek(linkUsabilityList);
    	
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("upChart", upChart);
		map.put("downChart", downChart);
		map.put("bandwidthChart", bandwidthChart);
		map.put("bandtrendChart", bandtrendChart);
		map.put("usabilityChart", usabilityChart);		
		
		map.put("linkUpHtml", linkUpHtml.toString());
		map.put("linkDownHtml", linkDownHtml.toString());
		map.put("linkBandwidthHtml", linkBandwidthHtml.toString());
		map.put("linkBandTrendHtml", linkBandTrendHtml.toString());
		map.put("linkUsabilityHtml", linkUsabilityHtml.toString());
		
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	
	public void loadLinkReportList(){
		DBManager dbManager = new DBManager();
		ResultSet rs = null;
		StringBuffer html = new StringBuffer();
		
		String id = this.getParaValue("id");
		String type = this.getParaValue("type");
		String sql =null;
		
		sql = "select id,name,email,sendTime,sendTime2 from nms_linkReport_config where type='"+type+"'";
		
		List<LinkReportInfo> list = new ArrayList<LinkReportInfo>();
		try {
			if (id != null && !id.equals("")) {
				String sql1 = "delete from nms_linkReport_config where id=" + id;
				try {
					dbManager.executeUpdate(sql1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			rs = dbManager.executeQuery(sql);
			while (rs.next()) {
				LinkReportInfo lri = new LinkReportInfo();
				lri.setId(rs.getInt("id"));
				lri.setName(rs.getString("name"));
				lri.setEmail(rs.getString("email"));
				lri.setSendTime(rs.getString("sendTime"));
				lri.setSendTime2(rs.getString("sendTime2"));
				list.add(lri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();
		}
		if (list != null && list.size() > 0) {
			html.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>序号</td><td align='center' class='body-data-title' height=21>模板名称</td><td align='center' class='body-data-title' height=21>接收邮箱</td><td align='center' class='body-data-title' height=21>发送时间</td><td align='center' class='body-data-title' height=21>详情</td>");
			for (int i = 0; i < list.size(); i++) {
				LinkReportInfo lri = new LinkReportInfo();
				lri = list.get(i);
				html.append("<tr><td  align='center' height=19>");
				html.append(i + 1);
				html.append("</td><td align='center' height=19>");
				html.append(lri.getName());
				html.append("</td><td align='center' height=19>");
				html.append(lri.getEmail());
				html.append("</td><td align='center' height=19>");
				String week = lri.getSendTime2();
				if(week!=null&&!week.equals("")&&!week.equals("null")){
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
					html.append("每周 "+week+" "+lri.getSendTime()+":00");
				}else{
					html.append("每天:"+lri.getSendTime()+":00");
				}
				html.append("</td><td align='center' height=19>");
				String path = request.getContextPath();
				String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
				html.append("<img src='" + basePath + "resource/image/vcf.gif' border='0' onClick='createWin(" + lri.getId()
						+ ")' title='查看模板详细信息'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/viewreport.gif' border='0' onClick='preview("
						+ lri.getId()
						+ ")' title='预览模板报表'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/delete.gif' border='0' onClick='deleteItem("
						+ lri.getId() + ")' title='删除模板'/></td></tr>");
			}
			html.append("</table>");
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataStr", html.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
	public void saveLinkReportOption() {
		String dataStr = "保存成功！";
		String ids = request.getParameter("ids");
		
		DBManager dbManager = new DBManager();
		String terms = this.getParaValue("terms");
		String type = this.getParaValue("type");
		
		String reportname = this.getParaValue("report_name");
		String exporttype = request.getParameter("exporttype");
		String username = this.getParaValue("recievers_name");
		String tile = this.getParaValue("tile");
		String desc = this.getParaValue("desc");
		try {
			terms = new String(terms.getBytes("iso8859-1"), "UTF-8");
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
		
		StringBuffer sql = new StringBuffer();
		//sql
		sql.append("insert into nms_linkReport_config(name,type,userName,email,emailTitle,emailContent,attachmentFormat,ids,terms,sendTime,sendTime2) values('");
		
		sql.append(reportname + "','");
		sql.append(type + "','");
		sql.append(username + "','");
		sql.append(buf.toString()+"','");
		sql.append(tile+"','");
		sql.append(desc+"','");
		sql.append(exporttype+"','");
		sql.append(ids+"','");
		sql.append(terms+"','");
		sql.append(sendtimehou+"','" );
		sql.append(sendtimeweek+"')");
		
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
	
	private String makeAmChartDataForColumn(List list){
		StringBuffer sb=new StringBuffer();
		String data="";
		List value = new ArrayList();
		List ip = new ArrayList();
		if(list!=null && list.size()>0){
			for(int i=0; i<list.size();i++){
				LinkReportStatic crs = (LinkReportStatic)list.get(i);
				value.add(crs.getValue());
				ip.add(crs.getLinkName());
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
			sb.append((String)value.get(i));
			sb.append("</value>");
		}
		sb.append("</graph>");
		sb.append("</graphs></chart>");
		data = sb.toString();
		return data;
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
				LinkReportStatic lrs = (LinkReportStatic)list.get(i);
				ip.add(lrs.getLinkName());
				weekValue.add(lrs.getWeekValues());
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
