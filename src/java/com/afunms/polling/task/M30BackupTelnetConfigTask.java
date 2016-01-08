package com.afunms.polling.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.BaseDaoImp;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.Hua3VPNFileConfigDao;
import com.afunms.config.manage.HaweitelnetconfManager;
import com.afunms.config.model.Hua3VPNFileConfig;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.telnet.CiscoTelnet;
import com.afunms.polling.telnet.Huawei3comvpn;
import com.database.config.SystemConfig;

/**
 * 定时备份配置文件
 * 
 * @author HONGLI
 */
public class M30BackupTelnetConfigTask extends MonitorTask {
	private static Logger log = Logger.getLogger(M30BackupTelnetConfigTask.class);

	private final String hms = " 00:00:00";

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		subscribe();
		// System.gc();
	}

	/**
	 * 定时备份配置文件
	 */
	private void subscribe() {
	
		DateTime dt = new DateTime();
		String time = dt.getMyDateTime(DateTime.Datetime_Format_14);
		
		String sql = "SELECT * FROM sys_timingbackup_telnetconfig s WHERE status = '1' and s.BACKUP_DATE > 10000 AND s.BACKUP_DATE < " + time;
		
		//在配置文件中设置是否启动定制发送报表 wxy add
		String flag= SystemConfig.getConfigInfomation("Agentconfig","Configserver");
		ArrayList<Map<String, String>> ssconfAL=null;
		if(flag!=null&&flag.equals("enable")){
			BaseDaoImp cd = new BaseDaoImp();
			ssconfAL = cd.executeQuery(sql);
		}
		Map<String, String> ssidAL = null;
		if (ssconfAL != null) {
			log.info("-------------------------------(定时备份)定时器执行时间：" + dt.getMyDateTime(DateTime.Datetime_Format_2) + "-------------------------------");
			try {
				for (int i = 0; i < ssconfAL.size(); i++) {
					ssidAL = ssconfAL.get(i);
					String status = ssidAL.get("status");
					String telnetconfigips = ssidAL.get("telnetconfigips");
					String backup_sendfrequency = ssidAL.get("BACKUP_SENDFREQUENCY");
					String backup_time_month = ssidAL.get("BACKUP_TIME_MONTH");
					String backup_time_week = ssidAL.get("BACKUP_TIME_WEEK");
					String backup_time_day = ssidAL.get("BACKUP_TIME_DAY");
					String backup_time_hou = ssidAL.get("BACKUP_TIME_HOU");
					String exportType = ssidAL.get("ATTACHMENTFORMAT");
					String bkpType = ssidAL.get("bkpType");
					String content = ssidAL.get("content");
					String id = ssidAL.get("id");
					boolean istrue = false;
					// 发送频率，0:全部发送;1:每天;2:每周;3:每月;4每季度;5每年
					if ("0".equals(backup_sendfrequency)) {
						istrue = true;
					} else if ("1".equals(backup_sendfrequency)) {
						if (backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("2".equals(backup_sendfrequency)) {
						if (backup_time_week.contains("/" + (dt.getDay() - 1) + "/") && backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("3".equals(backup_sendfrequency)) {
						if (backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("4".equals(backup_sendfrequency)) {
						if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth()) + "/")
								&& backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					} else if ("5".equals(backup_sendfrequency)) {
						if (backup_time_month.contains("/" + (dt.getMonth() < 10 ? "0" + dt.getMonth() : dt.getMonth()) + "/")
								&& backup_time_day.contains("/" + (dt.getDate() < 10 ? "0" + dt.getDate() : dt.getDate()) + "/")
								&& backup_time_hou.contains("/" + (dt.getHours() < 10 ? "0" + dt.getHours() : dt.getHours()) + "/")) {
							istrue = true;
						}
					}
					if (istrue) {
						log.info("定时备份配置文件开始--telnetconfigips=" + telnetconfigips);
						// String filePath =
						// ResourceCenter.getInstance().getSysPath() + "temp" +
						// File.separator
						// + "backup" + "_" + System.currentTimeMillis() + "." +
						// exportType;
						// if (doSubscribe(ssidAL, filePath)) {
						// sendMail(ssidAL, filePath);
						// } else {
						// log.error("订阅" + subscribe_id + "失败！");
						// }
						if (!telnetconfigips.equals("") && telnetconfigips != null) {
							String[] ips = telnetconfigips.split(",");
							HaweitelnetconfDao dao = new HaweitelnetconfDao();
							for (String ip : ips) {
								if (ip != null && !ip.equals("") && !ip.equals(",") && "1".equals(status)) {
									Huaweitelnetconf vo = (Huaweitelnetconf) dao.loadByIp(ip);
									if (vo != null) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
										String b_time = sdf.format(new Date());
										String prefix = ResourceCenter.getInstance().getSysPath().replace("\\", "/");
										String fileName = prefix + "cfg/" + vo.getIpaddress() + "_" + b_time + "cfg.cfg";
										String descri = vo.getIpaddress() + "_" + b_time;

										if (bkpType.equals("0")) {
											fileName = prefix + "cfg/" + vo.getIpaddress() + "(" + i + ")_" + b_time + "log.log";
											bkpCfg(ip, fileName, content, descri, id);
										} else {
											bkpCfg_1(ip, fileName, descri, bkpType);
										}
									}
								}
							}
							dao.close();
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	/**
	 * 备份配置文件
	 * 
	 * @param id
	 * @param fileName
	 * @param fileDesc
	 * @param bkpType
	 */
	private void bkpCfg_1(String ip, String fileName, String fileDesc, String bkpType) {
		// String id = getParaValue("id");//Huaweitelnetconf 的主键ID
		// String fileName = this.getParaValue("fileName");
		// String fileDesc = this.getParaValue("fileDesc");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String bkptime = "";
		Date bkpDate = new Date();
		String reg = "_(.*)cfg.cfg";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			bkptime = m.group(1);
		}
		try {
			bkpDate = sdf.parse(bkptime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String result = "";
		String runBackFileResult = "";// 备份运行时配置文件的结果字符串
		String startupBackFileResult = "";// 备份启动时配置文件的结果字符串
		String jsp = null;
		if (vo.getDeviceRender().equals("h3c")) {// h3c
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
			if ("run".equals(bkpType) || "startup".equals(bkpType)) {
				result = tvpn.Backupconffile(bkpType);
			} else {// bkpType 为 all的情况
				Huawei3comvpn secondTvpn = new Huawei3comvpn();
				secondTvpn.setSuuser(vo.getSuuser());// su
				secondTvpn.setSupassword(vo.getSupassword());// su密码
				secondTvpn.setUser(vo.getUser());// 用户
				secondTvpn.setPassword(vo.getPassword());// 密码
				secondTvpn.setIp(vo.getIpaddress());// ip地址
				secondTvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
				secondTvpn.setPort(vo.getPort());
				runBackFileResult = tvpn.Backupconffile("run");
				startupBackFileResult = secondTvpn.Backupconffile("startup");
			}
		} else if (vo.getDeviceRender().equals("cisco")) {// cisco
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword());
			if (telnet.login()) {
				if ("run".equals(bkpType) || "startup".equals(bkpType)) {
					result = telnet.getCfg(vo.getSupassword(), bkpType);
				} else {// bkpType 为 all的情况
					CiscoTelnet secondTelnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword());
					runBackFileResult = telnet.getCfg(vo.getSupassword(), "run");
					if (secondTelnet.login()) {
						startupBackFileResult = secondTelnet.getCfg(vo.getSupassword(), "startup");
					}
				}
			}
		}
		if ("run".equals(bkpType) || "startup".equals(bkpType)) {
			HaweitelnetconfManager.getInstance().backVpnConfig(bkpType, fileName, fileDesc, bkpDate, vo, result);
		} else {// bkpType = all
			HaweitelnetconfManager.getInstance().backVpnConfig("run", fileName, fileDesc, bkpDate, vo, runBackFileResult);
			String startupFileName = fileName.substring(0, fileName.lastIndexOf(".")).concat("(2)").concat(fileName.substring(fileName.lastIndexOf(".")));
			HaweitelnetconfManager.getInstance().backVpnConfig("startup", startupFileName, fileDesc, bkpDate, vo, startupBackFileResult);
		}
	}

	/**
	 * 备份命令配置文件
	 * 
	 * @param id
	 * @param fileName
	 * @param content
	 * @param fileDesc
	 */
	private void bkpCfg(String ip, String fileName, String content, String fileDesc, String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm");
		String bkptime = "";
		Date bkpDate = new Date();
		String reg = "_(.*)log.log";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			bkptime = m.group(1);
		}
		try {
			bkpDate = sdf.parse(bkptime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		Huaweitelnetconf vo = null;
		;
		try {
			vo = (Huaweitelnetconf) dao.loadByIp(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		String result = "";
		String runBackFileResult = "";// 备份运行时配置文件的结果字符串
		String startupBackFileResult = "";// 备份启动时配置文件的结果字符串
		String jsp = null;
		// List<String> contList=new ArrayList<String>();
		String[] commStr = new String[content.split("\r\n").length];
		commStr = content.split("\r\n");
		if (vo.getDeviceRender().equals("h3c")) {// h3c
			Huawei3comvpn tvpn = new Huawei3comvpn();
			tvpn.setSuuser(vo.getSuuser());// su
			tvpn.setSupassword(vo.getSupassword());// su密码
			tvpn.setUser(vo.getUser());// 用户
			tvpn.setPassword(vo.getPassword());// 密码
			tvpn.setIp(vo.getIpaddress());// ip地址
			tvpn.setDEFAULT_PROMPT(vo.getDefaultpromtp());// 结束标记符号
			tvpn.setPort(vo.getPort());
			result = tvpn.BackupConfFile(commStr);

		} else if (vo.getDeviceRender().equals("cisco")) {// cisco
			CiscoTelnet telnet = new CiscoTelnet(vo.getIpaddress(), vo.getUser(), vo.getPassword());
			if (telnet.login()) {
				result = telnet.getFileCfg(vo.getSupassword(), commStr);

			}
		}
		vo.setId(Integer.parseInt(id));// 暂时存放
		HaweitelnetconfManager.getInstance().backVpnConfig("0", fileName, content, bkpDate, vo, result);

	}

	/**
	 * 日期格式转换
	 * 
	 * @param startTime
	 * @return
	 */
	public String startTime(String startTime) {
		return startTime + hms;
	}

	/**
	 * 日期格式转换
	 * 
	 * @param toTime
	 * @return
	 */
	public String toTime(String toTime) {
		String Millisecond = String.valueOf(DateTime.getMillisecond(toTime, DateTime.Datetime_Format_5) - 1000);
		return DateTime.getDateFromMillisecond(Millisecond, DateTime.Datetime_Format_2);
	}
}
