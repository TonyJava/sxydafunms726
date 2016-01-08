package com.afunms.capreport.manage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.ShareData;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class DiskIncreaseRateManager extends BaseManager implements ManagerInterface{
	DateE datemanager = new DateE();

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String hostdisk() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String[] ids = getParaArrayValue("checkbox");
		Hashtable allcpuhash = new Hashtable();

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}
		
		String runmodel = PollingEngine.getCollectwebflag();
	
		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(ids[i]));
				dao.close();
				if (node == null)
					continue;
				Hashtable diskhash = null;
				
				try {
					if ("0".equals(runmodel)) {
						// 采集与访问是集成模式
						//diskhash = this.getDisk_share(node.getIpAddress(), "Disk", starttime, totime);
					} else {
						// 采集与访问是分离模式
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// 取出当前的硬盘信息
						DiskInfoService diskInfoService = new DiskInfoService(String.valueOf(nodeDTO.getId()), nodeDTO
								.getType(), nodeDTO.getSubtype());
						diskhash = diskInfoService.getCurrDiskListInfo(starttime, totime);
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("node", node);
				ipmemhash.put("diskhash", diskhash);
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("allsize") || orderflag.equalsIgnoreCase("utilization")) {
			returnList = (List) session.getAttribute("disklist");
		} else {
			// 对orderList根据theValue进行排序

			// **********************************************************

			// I_MonitorIpList monitorManager=new MonitoriplistManager();
			List disklist = orderList;
			if (disklist != null && disklist.size() > 0) {
				for (int i = 0; i < disklist.size(); i++) {
					Hashtable _diskhash = (Hashtable) disklist.get(i);
					HostNode node = (HostNode) _diskhash.get("node");
					String osname = node.getType();
					Hashtable diskhash = (Hashtable) _diskhash.get("diskhash");
					if (diskhash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String[] diskItem = { "AllSize", "UsedSize", "Utilization" };
					for (int k = 0; k < diskhash.size(); k++) {
						Hashtable dhash = (Hashtable) (diskhash.get(new Integer(k)));
						String name = "";
						if (dhash.get("name") != null) {
							name = (String) dhash.get("name");
						}
						String allsizevalue = "";
						String usedsizevalue = "";
						String utilization = "";
						String increasevalue = "";
						if (dhash.get("AllSize") != null) {
							allsizevalue = (String) dhash.get("AllSize");
						}
						if (dhash.get("UsedSize") != null) {
							usedsizevalue = (String) dhash.get("UsedSize");
						}
						if (dhash.get("Utilization") != null) {
							utilization = (String) dhash.get("Utilization");
						}
						if (dhash.get("increasevalue") != null) {
							increasevalue = (String) dhash.get("increasevalue");
						}
						
						
						List ipdiskList = new ArrayList();
						ipdiskList.add(ip);
						ipdiskList.add(equname);
						ipdiskList.add(node.getType());
						ipdiskList.add(name);
						ipdiskList.add(allsizevalue);
						ipdiskList.add(usedsizevalue);
						ipdiskList.add(utilization);
						ipdiskList.add(increasevalue);
						returnList.add(ipdiskList);

					}
				}
			}
		}
		// **********************************************************

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("allsize")) {
						String allsizevalue = "";
						if (ipdiskList.get(4) != null) {
							allsizevalue = (String) ipdiskList.get(4);
						}
						String _allsizevalue = "";
						if (ipdiskList.get(4) != null) {
							_allsizevalue = (String) _ipdiskList.get(4);
						}
						if (new Double(allsizevalue.substring(0, allsizevalue.length() - 2)).doubleValue() < new Double(
								_allsizevalue.substring(0, _allsizevalue.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("utilization")) {
						String utilization = "";
						if (ipdiskList.get(6) != null) {
							utilization = (String) ipdiskList.get(6);
						}
						String _utilization = "";
						if (ipdiskList.get(6) != null) {
							_utilization = (String) _ipdiskList.get(6);
						}
						if (new Double(utilization.substring(0, utilization.length() - 2)).doubleValue() < new Double(
								_utilization.substring(0, _utilization.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}

		// setListProperty(capReportForm, request, list);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("disklist", list);
		session.setAttribute("disklist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		return "/capreport/host/hostdisk1.jsp";
	}
	
	private String disklist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/disklist.jsp";
	}
	
	private String list() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadHostByFlag(1));
		return "/capreport/host/diskIncreaseRate.jsp";
	}
	
	
	
	
	private String downloaddiskreport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable allcpuhash = new Hashtable();

		List returnList = new ArrayList();
		List memlist = (List) session.getAttribute("disklist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("disklist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hostdiskin("/temp/hostdisk_report.xls");
		
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}
	
	private String topAlarm() {
		String jp = getParaValue("jp");
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		String per = "";
		if (jp.equals("0")) {
			per = "CPU利用率";
		} else if (jp.equals("1")) {
			per = "文件系统利用率";
		} else if (jp.equals("2")) {
			per = "内存";
		} else {
			per = "errpt";
		}
		String sql = "select t.ip_address, t.alias, count(*) m ,t.content,t.eventlocation ,t.nodeid from (select distinct content,substr(recordtime,1,10) collecttime,eventlocation" 
+",nodeid,ip_address,alias from system_eventlist as s , topo_host_node d where recordtime >='"+starttime+"'"
+" and recordtime <='"+totime+"' and content like '%"+per+"%' and subtype='host' and s.nodeid = d.id) t group by eventlocation order by m desc";
		System.out.println(sql);
		rs = dbmanager.executeQuery(sql);
		String ip = "";
		String sysname = "";
		String count = "";
		List returnList = new ArrayList();
		try {
			while (rs.next()) {
				ip = rs.getString(1);
				sysname = rs.getString(2);
				count = rs.getString(3);
				
				List list = new ArrayList();
				list.add(ip);
				list.add(sysname);
				list.add(count);
				returnList.add(list);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			dbmanager.close();
		}

		request.setAttribute("alarmlist", returnList);
		session.setAttribute("alarmlist", returnList);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("jp", jp);
		return "/capreport/host/alarmlist.jsp";
   }
	
	private String downloadtopreport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String jp = getParaValue("jp");
		List returnList = new ArrayList();
		List memlist = (List) session.getAttribute("alarmlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("disklist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("jp", jp);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_hosttop("/temp/hostdisk_report.xls");
		
		request.setAttribute("filename", report.getFileName());
		return "/capreport/net/download.jsp";
	}
	
	
	private String dofloat(String num){
		String snum="0.0";
		if(num!=null){
			int inum=(int)(Float.parseFloat(num)*100);
			snum=Double.toString(inum/100.0);
		}
		return snum;
	}
	
	public String execute(String action) {
		if (action.equals("disklist"))
			return disklist();
		if(action.equals("hostdisk"))
		return hostdisk();
		if(action.equals("list"))
			return list();
		if(action.equals("downloaddiskreport"))
			return downloaddiskreport();
		if(action.equals("topAlarm"))
			return topAlarm();
		if(action.equals("downloadtopreport"))
			return downloadtopreport();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
