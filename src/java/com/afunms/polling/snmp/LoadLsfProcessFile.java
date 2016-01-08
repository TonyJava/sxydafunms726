package com.afunms.polling.snmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.course.dao.LsfClassProcessMonitoringDao;
import com.afunms.application.course.util.LsfClassUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.ShareDataLsf;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.gatherResulttosql.LsfDatatempProcesstosql;

public class LoadLsfProcessFile {
	
	
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		
		
		
		Hashtable resul=new Hashtable();
		
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		String ipaddress = host.getIpAddress();
		
		try {
			StringBuffer fileContent = new StringBuffer();
			String file_path = ResourceCenter.getInstance().getSysPath()+ "/linuxserver/LSF-" + host.getIpAddress() + ".log";
			File file = new File(file_path);
			String collecttime = "";
			String process = "";
			String count = "";
			if (!file.exists()) {
				System.out.println(file_path + "不存在告警");
				createFileNotExistSMS(ipaddress);
			}
			FileInputStream fis = new FileInputStream(file_path);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String strLine = null;
			while ((strLine = br.readLine()) != null) {
				fileContent.append(strLine + "\n");
			}
			
			isr.close();
			fis.close();
			br.close();
			Pattern tmpPt = null;
			Matcher mr = null;
			tmpPt = Pattern.compile(
					"(cmdbegin:collecttime)(.*)(cmdbegin:process)",
					Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				collecttime = mr.group(2);
				boolean flg=false;
				if (null !=ShareDataLsf.getCollecttime_table() && !ShareDataLsf.getCollecttime_table().containsKey(host.getIpAddress()) ) {
					ShareDataLsf.getCollecttime_table().put(host.getIpAddress(), collecttime);
					flg=true;
				} else {
					String rm_time = (String) ShareDataLsf.getCollecttime_table().get(host.getIpAddress());
					ShareDataLsf.getCollecttime_table().put(host.getIpAddress(), collecttime);
					int time_flag = new LsfClassUtil().computeDateTime(rm_time,
							collecttime);
					if(!flg){
						if (time_flag == 0 || time_flag > 10) {
							String content = "LSF-" + "(" + ipaddress + ").log "
									+ " LSF日志文件采集时间超时,预定时间为：10分钟";
							String name = host.getId() + ":host:proce:Lsflog";
							this.check(host, content, name);
						}
						//2012-08-31
						resul.put("master", "2");
						resul.put("classid",((Hashtable)ShareDataLsf.getHashLsf().get(host.getId()+"")).get("classid"));
						resul.put("nodeid", host.getId());
						resul.put("logcoud", "0");
						resul.put("alarm", "1");
						LsfDatatempProcesstosql Rtosql=new LsfDatatempProcesstosql();
						Rtosql.CreateResultTosql(resul);
//					return null;
						return resul;
					}
				}
			}
			tmpPt = Pattern.compile(
					"(cmdbegin:process)(.*)(cmdbegin:jidstatus)",
					Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
				process = mr.group(2);
			}
			String[] process_ = process.trim().split("\n");
			String[] processtmpData = null;
			Hashtable proce_tale = new Hashtable();
			for (int i = 1; i < process_.length; i++) {
				processtmpData = process_[i].trim().split("\\s++");
				if (processtmpData != null && processtmpData.length > 11) {
					proce_tale.put(processtmpData[11], processtmpData[7]);
				}
			}
			
			//判断主机状态是否为maxter
			if(proce_tale.containsKey("mbschd") || proce_tale.containsKey("mbatchd") )
			{
				resul.put("master", "1");
	
				
			}else if(!proce_tale.containsKey("mbschd") && !proce_tale.containsKey("mbatchd"))
			{
				resul.put("master", "0");
			}
			
			
			//判断是master变化
			if(ShareDataLsf.getMasterlist().containsKey(host.getId()))
			{
				
			   if((String)ShareDataLsf.getMasterlist().get(host.getId())!= (String)resul.get("master"))
			    {
				   
				   String name = host.getId() + ":host:proce:master";
				   String content="";
				   if((String)ShareDataLsf.getMasterlist().get(host.getId())=="1" &&(String)resul.get("master")=="0" )
				   {
				    content =host.getAlias()+ "(" + ipaddress + ")" + " 切换成普通节点";
				   }else
				   {
					   content =host.getAlias()+ "(" + ipaddress + ")" + " 切换成master节点";
				   }
				   
				   this.check(host, content, name);
				   
			    }
				//把当前结果放入内存
			   ShareDataLsf.getMasterlist().put(host.getId(), resul.get("master"));
				
				
				
			}else
			{//把值放入到内存中			
				ShareDataLsf.getMasterlist().put(host.getId(), resul.get("master"));
				
			}
			
			
			
			
			//System.out.println("=进程的状态值===="+proce_tale.toString());
			
			
			
			if (proce_tale.get("mbschd") != null
					|| proce_tale.get("mbatchd") != null) {
				if (!proce_tale.containsKey("lim")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " lim进程丢失";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("res")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res进程丢失";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("pim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim进程丢失";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("sbatchd")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd进程丢失";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("mbatchd")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " mbatchd进程丢失";
					String name = host.getId() + ":host:proce:mbatchd";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("mbschd")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " mbschd进程丢失";
					String name = host.getId() + ":host:proce:mbschd";
					this.check(host, content, name);
				}
			} else {
				if (!proce_tale.containsKey("lim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim进程丢失";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("res")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " res进程丢失";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("pim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim进程丢失";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("sbatchd")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd进程丢失";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				}
			}
			
			
			
			if (proce_tale.get("mbschd") != null
					|| proce_tale.get("mbatchd") != null) {
				if (proce_tale.get("lim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim状态僵死";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (proce_tale.get("res").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res状态僵死";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (proce_tale.get("pim").equals("Z")) {
					
					//System.out.println("================aa===========proce_tale.get('pim')==========="+proce_tale.get("pim")+"=ss==");
					
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim状态僵死";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (proce_tale.get("sbatchd").equals("Z")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " sbatchd状态僵死";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				} else if (proce_tale.get("mbatchd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " mbatchd状态僵死";
					String name = host.getId() + ":host:proce:mbatchd";
					this.check(host, content, name);
				} else if (proce_tale.get("mbschd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " mbschd状态僵死";
					String name = host.getId() + ":host:proce:mbschd";
					this.check(host, content, name);
				}
			} else {
				if (proce_tale.get("lim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim状态僵死";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (proce_tale.get("res").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res状态僵死";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (proce_tale.get("pim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim状态僵死";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (proce_tale.get("sbatchd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd状态僵死";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				}
			}
		
			
			//System.out.println("==============================nodelist====================="+ShareDataLsf.getHashLsf());
			
			//System.out.println(ShareDataLsf.getHashLsf().get(host.getId()).toString());
			
			//System.out.println("==========((Hashtable) ShareDataLsf.getHashLsf()).containsKey(host.getId())======="+((Hashtable) ShareDataLsf.getHashLsf()).containsKey(host.getId()+""));
			
			if(null!=ShareDataLsf.getHashLsf() &&((Hashtable) ShareDataLsf.getHashLsf()).containsKey(host.getId()+"")){
				
				//System.out.println("====================jid=====test====================================");
				resul.put("classid",((Hashtable)ShareDataLsf.getHashLsf().get(host.getId()+"")).get("classid"));
				resul.put("nodeid", host.getId());
				
				if (!proce_tale.containsKey("jid")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " jid进程丢失";
					String name = host.getId() + ":host:proce:jid";
					this.check(host, content, name);
				}else if(proce_tale.get("jid").equals("Z")){
					String content = host.getAlias()+"(" + ipaddress + ")" + " jid进程状态僵死";
					String name = host.getId() + ":host:proce:jid";
					this.check(host, content, name);
				}
			}
			tmpPt = Pattern.compile("(cmdbegin:jidstatus)(.*)(cmdbegin:logcount)",
					Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
//				System.out.println("(cmdbegin:logcount)(.*)(cmdbegin:end):");
				count = mr.group(2);
			}
			
			//System.out.println("=======================================");
			//System.out.println(count);
			//System.out.println("=======================================");
			if(null!=count &&count.indexOf("My Process Manager Server name")<0 && 
					count.indexOf("Platform Process Manager")<0 && count.indexOf("Copyright")<0)
			{
				String content = host.getAlias()+"(" + ipaddress + ")" + " LSF 进程状态异常";
				String name = host.getId() + ":host:proce:LSFjid";
				this.check(host, content, name);
			}
		//----------------------日志个数检查-------------------------------------
			tmpPt = Pattern.compile("(cmdbegin:logcount)(.*)(cmdbegin:end)",
					Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			if (mr.find()) {
//				System.out.println("(cmdbegin:logcount)(.*)(cmdbegin:end):");
				count = mr.group(2);
			}
			
			resul.put("logcoud", count.trim());
			
			NodeUtil nodeutil = new NodeUtil();
			String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, subtype,"lsflog");
			for(int k = 0 ; k < list.size() ; k ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
				//告警检测
				CheckEventUtil checkutil = new CheckEventUtil();
				checkutil.checkEvent(host, alarmIndicatorsnode, count);
			}
		} catch (Exception e) {
			LsfDatatempProcesstosql Rtosql=new LsfDatatempProcesstosql();
			resul.put("master", "9");
			resul.put("classid",((Hashtable)ShareDataLsf.getHashLsf().get(host.getId()+"")).get("classid"));
			resul.put("nodeid", host.getId());
			resul.put("logcoud", "0");
			resul.put("alarm", "9");
			Rtosql.CreateResultTosql(resul);
			return resul;
		}
		
		System.out.println("------------------------lsf--------------4--------------------------");
		
		
		//根据内存中的告警列表
		if(ShareDataLsf.getAlarmlist().containsKey(host.getId()))
		{
			
			resul.put("alarm", "1");
		}else
		 {
			resul.put("alarm", "0");
		 }
		
		
		System.out.println("=========================resul====================================="+resul.toString());
		
		
		LsfDatatempProcesstosql Rtosql=new LsfDatatempProcesstosql();
		
		Rtosql.CreateResultTosql(resul);
		
		
		
		return resul;
	}

	/**
	 * @param host
	 *            节点对象
	 * @param Content
	 *            告警的内容 #设备ip：192.168.0.1:（内容）LSF采集文件超时
	 * @param eventname
	 *            （nodeid：host：proce：procename） 58：host：proce：Lsflog
	 */
	public void check(Host host, String Content, String eventname) {
		NodeUtil nodeutil = new NodeUtil();
		String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
		try {
			EventList eventlist = new EventList();
			eventlist.setEventtype("poll");
			eventlist.setEventlocation(host.getSysLocation());
			eventlist.setContent(Content);
			eventlist.setLevel1(1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(host.getBid());
			eventlist.setNodeid(host.getId());
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity("proc");
			eventlist.setIpaddress(ipaddress);
			ShareDataLsf.getAlarmlist().put(host.getId(), "1");
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(
					String.valueOf(host.getId()), AlarmConstant.TYPE_HOST,
					subtype, "process");
			for (int z = 0; z < list.size(); z++) {
				// System.out.println("========阀值个数==="+list.size());
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
						.get(z);
				// 对物理内存值进行告警检测
				CheckEvent checkevent = new CheckEvent();
				checkevent.setAlarmlevel(1);
				checkevent.setName(eventname);
				SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
				// 发送告警
				sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,
						alarmIndicatorsnode);// 这个地方写什么
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		
		
		
		
		
	}


	/**
	 * @param hostname
	 */
	private String ipaddress;
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");


	public String getMaxNum(String ipAddress) {
		String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath()
				+ "linuxserver/");
		String[] fileList = logFolder.list();

		for (int i = 0; i < fileList.length; i++) // 找一个最新的文件
		{
			if (!fileList[i].startsWith(ipAddress))
				continue;

			return ipAddress;
		}
		return maxStr;
	}

	public void deleteFile(String ipAddress) {

		try {
			File delFile = new File(ResourceCenter.getInstance().getSysPath()
					+ "linuxserver/" + ipAddress + ".log");
			System.out.println("###开始删除文件：" + delFile);
			// delFile.delete();
			System.out.println("###成功删除文件：" + delFile);
		} catch (Exception e) {
		}
	}

	public void copyFile(String ipAddress, String max) {
		try {
			String currenttime = SysUtil.getCurrentTime();
			currenttime = currenttime.replaceAll("-", "");
			currenttime = currenttime.replaceAll(" ", "");
			currenttime = currenttime.replaceAll(":", "");
			String ipdir = ipAddress.replaceAll("\\.", "-");
			String filename = ResourceCenter.getInstance().getSysPath()
					+ "/linuxserver_bak/" + ipdir;
			File file = new File(filename);
			if (!file.exists())
				file.mkdir();
			String cmd = "cmd   /c   copy   "
					+ ResourceCenter.getInstance().getSysPath()
					+ "linuxserver\\" + ipAddress + ".log" + " "
					+ ResourceCenter.getInstance().getSysPath()
					+ "linuxserver_bak\\" + ipdir + "\\" + ipAddress + "-"
					+ currenttime + ".log";
			// SysLogger.info(cmd);
			Process child = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createSMS(Procs procs) {
		Procs lastprocs = null;
		// 建立短信
		procs.setCollecttime(Calendar.getInstance());
		// 从已经发送的短信列表里获得当前该PROC已经发送的短信
		lastprocs = (Procs) sendeddata.get(procs.getIpaddress() + ":"
				+ procs.getProcname());

		/*
		 * try{ if (lastprocs==null){ //内存中不存在 ,表明没发过短信,则发短信 Equipment equipment =
		 * equipmentManager.getByip(procs.getIpaddress()); Smscontent smscontent =
		 * new Smscontent(); String time =
		 * sdf.format(procs.getCollecttime().getTime());
		 * smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 * //发送短信 Vector tosend = new Vector(); tosend.add(smscontent);
		 * smsmanager.sendSmscontent(tosend);
		 * //把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 * sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		 * }else{ //若已经发送的短信列表存在这个IP的PROC进程 //若在，则从已发送短信列表里判断是否已经发送当天的短信
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); Date
		 * last = null; Date current = null; Calendar sendcalen =
		 * (Calendar)lastprocs.getCollecttime(); Date cc = sendcalen.getTime();
		 * String tempsenddate = formatter.format(cc);
		 * 
		 * Calendar currentcalen = (Calendar)procs.getCollecttime(); cc =
		 * currentcalen.getTime(); last = formatter.parse(tempsenddate); String
		 * currentsenddate = formatter.format(cc); current =
		 * formatter.parse(currentsenddate);
		 * 
		 * long subvalue = current.getTime()-last.getTime();
		 * 
		 * if (subvalue/(1000*60*60*24)>=1){ //超过一天，则再发信息 Smscontent smscontent =
		 * new Smscontent(); String time =
		 * sdf.format(procs.getCollecttime().getTime()); Equipment equipment =
		 * equipmentManager.getByip(procs.getIpaddress()); if (equipment ==
		 * null){ return; }else
		 * smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 * 
		 * //发送短信 Vector tosend = new Vector(); tosend.add(smscontent);
		 * smsmanager.sendSmscontent(tosend);
		 * //把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 * sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		 * }else{ //没超过一天,则只写事件 Vector eventtmpV = new Vector(); EventList event =
		 * new EventList(); Monitoriplist monitoriplist =
		 * (Monitoriplist)monitormanager.getByIpaddress(procs.getIpaddress());
		 * event.setEventtype("host");
		 * event.setEventlocation(procs.getIpaddress()); event.setManagesign(new
		 * Integer(0)); event.setReportman("monitorpc");
		 * event.setRecordtime(Calendar.getInstance()); event.setLevel1(new
		 * Integer(1));
		 * event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
		 * event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
		 * String time = sdf.format(Calendar.getInstance().getTime());
		 * event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
		 * eventtmpV.add(event); try{ eventmanager.createEventlist(eventtmpV);
		 * }catch(Exception e){ e.printStackTrace(); } } } }catch(Exception e){
		 * e.printStackTrace(); }
		 */
	}

	public void createFileNotExistSMS(String ipaddress) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(
					ipaddress);
			if (host == null)
				return;

			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("3");
				smscontent.setObjid(host.getId() + "");
				smscontent.setMessage(host.getAlias() + " ("
						+ host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("host");
				smscontent.setSubentity("ftp");
				smscontent.setIp(host.getIpAddress());// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(ipaddress + ":file" + host.getId(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate = (Calendar) sendeddata.get(ipaddress
						+ ":file:" + host.getId());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date last = null;
				Date current = null;
				Calendar sendcalen = formerdate;
				Date cc = sendcalen.getTime();
				String tempsenddate = formatter.format(cc);

				Calendar currentcalen = date;
				cc = currentcalen.getTime();
				last = formatter.parse(tempsenddate);
				String currentsenddate = formatter.format(cc);
				current = formatter.parse(currentsenddate);

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("3");
					smscontent.setObjid(host.getId() + "");
					smscontent.setMessage(host.getAlias() + " ("
							+ host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("host");
					smscontent.setSubentity("ftp");
					smscontent.setIp(host.getIpAddress());// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					// 修改已经发送的短信记录
					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
