package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.afunms.capreport.model.SubscribeResources;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;
//import com.afunms.comprehensivereport.dao.CompreReportUtilDao;
//import com.afunms.comprehensivereport.model.CompreReportInfo;
//import com.afunms.comprehensivereport.util.CompreReportHelper;
//import com.afunms.comprehensivereport.util.CompreReportStatic;
import com.afunms.comprehensivereportweek.model.CompreReportWeekInfo;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;

/**
 * 综合周报表
 * @author Administrator
 * @project afunms
 * @date 2012-04-18
 * <p>Company: dhcc.com</p>
 */
public class CompreReportWeekAjaxManager extends AjaxBaseManager implements
			AjaxManagerInterface {

		public void execute(String action) {
			// TODO Auto-generated method stub
			if (action.equals("savePerforIndexWeek")) {
				savePerforIndexWeek();
			}if(action.equals("loadPeforIndex")){
				loadPeforIndex();
			}
		}
		
		/**
		 *  设备事件统计:01
			主机设备告警统计明细:02
			网络设备告警统计明细:03
			事件告警趋势分析(每小时):04
			主机设备TOP趋势分析:05
			网络设备TOP趋势分析:06
			星期日:0
			星期一:1
			星期二:2
			星期三:3
			星期四:4
			星期五:5
			星期六:6
			时间：03
		 * 模板保存
		 */
		public void savePerforIndexWeek() {
			String dataStr = "保存成功！";
			
			String startTime = request.getParameter("startdate");//开始时间
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				startTime = sdf.format(new Date()) + " 00:00:00";
			} else {
				startTime = startTime + " 00:00:00";
			}
			String toTime = request.getParameter("todate");//截至时间
			if (toTime == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				toTime = sdf.format(new Date()) + " 23:59:59";
			} else {
				toTime = toTime + " 23:59:59";
			}
			DBManager dbManager = new DBManager();
			
			//报表中内容
			String reportname = this.getParaValue("report_name");//报表名称 reportName,
			String username = this.getParaValue("recievers_name");//邮件接收人 reportUserName,
			String tile = this.getParaValue("tile");//邮件标题  emailTitle,
			String desc = this.getParaValue("desc");//邮件描述  emailContent,
			String bidtext = this.getParaValue("bidtext");//所属业务  attachmentBusiness,
			String reporttype = this.getParaValue("reporttype");//周报表   reportType,
			String exporttype = request.getParameter("exporttype");//报表类型  ExpReportType,
			String ids = request.getParameter("ids");//选择的设备  ids,
			String eventCount = this.getParaValue("eventCount");//设备事件统计
//			String hostAlarmCount = this.getParaValue("hostAlarmCount");//hostAlarmCount 主机设备告警统计明细
//			String netAlarmCount = this.getParaValue("netAlarmCount");//netAlarmCount  网络设备告警统计明细
			String eventAnalyze = this.getParaValue("eventAnalyze");//eventAnalyze  事件告警趋势分析
			String hostTopAnalyze = this.getParaValue("hostTopAnalyze");//hostTopAnalyze  主机设备TOP趋势分析
			String netTopAnalyze = this.getParaValue("netTopAnalyze");//netTopAnalyze  网络设备TOP趋势分析
			String transmitfrequency = this.getParaValue("transmitfrequency");//周 reportContentOption,
			String sendtimeweek = this.getParaValue("sendtimeweek");//星期几  sendWeek,
			String sendtimehou = request.getParameter("sendtimehou");//发生时间  sendTime,
			String startdate = this.getParaValue("mystartdate");
			String mytodate = this.getParaValue("mytodate");
			
			//报表内容
			try {
				reportname = new String(reportname.getBytes("iso8859-1"), "UTF-8");
				username = new String(username.getBytes("iso8859-1"), "UTF-8");
				tile = new String(tile.getBytes("iso8859-1"), "UTF-8");
				desc = new String(desc.getBytes("iso8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
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
			StringBuffer buf = new StringBuffer();//接收者邮件地址
			for (int i = 0; i < userList.size(); i++) {
				User vo = (User) userList.get(i);
				buf.append(vo.getEmail());
				buf.append(",");
			}
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdFormat.format(cc);
			StringBuffer sql = new StringBuffer();
			//sql  attachmentBusiness
			sql.append("insert into nms_compreReportWeek_resources" +
					"(reportName,reportUserName,emailTitle,emailContent,attachmentBusiness," +
					"reportType,ExpReportType,ids,eventCount," +
//					"hostAlarmCount,netAlarmCount," +
					"eventAnalyze,hostTopAnalyze,netTopAnalyze,sendWeek," +
					"sendOtherDay,sendTime,reportUserEmail) values('");
			sql.append(reportname + "','");
			sql.append(username + "','");
			sql.append(tile+"','");
			sql.append(desc+"','");
			sql.append(bidtext+"','");
			sql.append(reporttype+"','");
			sql.append(exporttype+"','");
			sql.append(ids+"','");
			sql.append(eventCount+"','");
//			sql.append(hostAlarmCount+"','");
//			sql.append(netAlarmCount+"','");
			sql.append(eventAnalyze+"','");
			sql.append(hostTopAnalyze+"','");
			sql.append(netTopAnalyze+"','");
			sql.append(transmitfrequency+"','");
			sql.append(sendtimeweek+"','");
			sql.append(sendtimehou+"','");
//			sql.append(startdate+"','");
//			sql.append(mytodate+"','");
			sql.append(buf.toString()+"')");
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
//		private void setInnerHtml(StringBuffer html,String ip,String cur,String unit) {
//			html.append("<tr bgcolor='#FFFFFF'><td align='center'>"
//					+ ip + "</td>");
//			html.append("<td align='center' height=21>"+cur.replace("%", "") + unit+"</td></tr>");
//		}
//		private String makeAmChartDataForColumn(List list){
//			StringBuffer sb=new StringBuffer();
//			String data="";
//			List value = new ArrayList();
//			List ip = new ArrayList();
//			if(list!=null && list.size()>0){
//				for(int i=0; i<list.size();i++){
//					CompreReportStatic crs = (CompreReportStatic)list.get(i);
//					value.add(crs.getValue());
//					ip.add(crs.getIp());
//				}
//			}else{
//				data = "0";
//				return data;
//			}
//			String[] colorStr=new String[] {"#FF6600","#FCD202","#B0DE09","#0D8ECF","#A52A2A","#33FF33","#FF0033","#9900FF","#FFFF00","#0000FF","#A52A2A","#23f266"};
//			
//			sb.append("<chart><series>");
//			for(int i = 0; i<ip.size(); i++){
//				sb.append("<value xid='");
//				sb.append(i);
//				sb.append("'>");
//				sb.append((String)ip.get(i));
//				sb.append("</value>");
//			}
//			sb.append("</series><graphs>");
//			sb.append("<graph>");
//			for(int i=0;i<value.size();i++){
//				sb.append("<value xid='");
//				sb.append(i);
//				sb.append("' color='"+colorStr[i]+"'>");
//				sb.append((Double)value.get(i));
//				sb.append("</value>");
//			}
//			sb.append("</graph>");
//			sb.append("</graphs></chart>");
//			data = sb.toString();
//			return data;
//		}
		
		// 加载模板列表（网络设备、服务器）
		private void loadPeforIndex() {
			DBManager dbManager = new DBManager();
			ResultSet rs = null;
			StringBuffer html = new StringBuffer();
			String id = this.getParaValue("id");
			String type = this.getParaValue("type");
//			String sql = "select n.id,n.name,n.ids,s.EMAIL,s.REPORT_SENDDATE from nms_userreport n,sys_subscribe_resources s where n.id=s.SUBSCRIBE_ID and n.type='"
//					+ type + "'";
			String sql = "select id,reportName,reportUserEmail,sendOtherDay,sendTime from nms_comprereportweek_resources";
			List<CompreReportWeekInfo> list = new ArrayList<CompreReportWeekInfo>();
			try {
				if (id != null && !id.equals("")) {
					String sql1 = "delete from nms_comprereportweek_resources where id=" + id;
					try {
						dbManager.executeUpdate(sql1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				rs = dbManager.executeQuery(sql);
				while (rs.next()) {
					CompreReportWeekInfo sr = new CompreReportWeekInfo();
					sr.setId(rs.getInt("id"));
					sr.setReportName(rs.getString("reportName"));// 为方便暂用字段充当模板名称
					sr.setReportUserEmail(rs.getString("reportUserEmail"));
					sr.setSendOtherDay(rs.getInt("sendOtherDay"));
					sr.setSendTime(rs.getString("sendTime"));// 为方便暂用字段充当性能指标
					list.add(sr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbManager.close();
			}
			if (list != null && list.size() > 0) {
				html.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>序号</td><td align='center' class='body-data-title' height=21>模板名称</td><td align='center' class='body-data-title' height=21>接收邮箱</td><td align='center' class='body-data-title' height=21>发送时间</td><td align='center' class='body-data-title' height=21>详情</td>");
				for (int i = 0; i < list.size(); i++) {
					CompreReportWeekInfo sr = new CompreReportWeekInfo();
					sr = list.get(i);
					html.append("<tr><td  align='center' height=19>");
					html.append(i + 1);
					html.append("</td><td align='center' height=19>");
					html.append(sr.getReportName());
					html.append("</td><td align='center' height=19>");
					html.append(sr.getReportUserEmail());
					html.append("</td><td align='center' height=19>");
					html.append("星期："+sr.getSendOtherDay()+" 时间："+sr.getSendTime());
					html.append("</td><td align='center' height=19>");
					String path = request.getContextPath();
					String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
					html.append("<img src='" + basePath + "resource/image/vcf.gif' border='0' onClick='createWin(" + sr.getId()
							+ ")' title='查看模板详细信息'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/viewreport.gif' border='0' onClick='preview("
							+ sr.getId()
							+ ")' title='预览模板报表'/>&nbsp;&nbsp;<img src='" + basePath + "resource/image/delete.gif' border='0' onClick='deleteItem("
							+ sr.getId() + ")' title='删除模板'/></td></tr>");
				}
				html.append("</table>");
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("dataStr", html.toString());
			JSONObject json = JSONObject.fromObject(map);
			out.print(json);
			out.flush();
		}
}
