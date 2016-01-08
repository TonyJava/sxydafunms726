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
				System.out.println(file_path + "�����ڸ澯");
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
									+ " LSF��־�ļ��ɼ�ʱ�䳬ʱ,Ԥ��ʱ��Ϊ��10����";
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
			
			//�ж�����״̬�Ƿ�Ϊmaxter
			if(proce_tale.containsKey("mbschd") || proce_tale.containsKey("mbatchd") )
			{
				resul.put("master", "1");
	
				
			}else if(!proce_tale.containsKey("mbschd") && !proce_tale.containsKey("mbatchd"))
			{
				resul.put("master", "0");
			}
			
			
			//�ж���master�仯
			if(ShareDataLsf.getMasterlist().containsKey(host.getId()))
			{
				
			   if((String)ShareDataLsf.getMasterlist().get(host.getId())!= (String)resul.get("master"))
			    {
				   
				   String name = host.getId() + ":host:proce:master";
				   String content="";
				   if((String)ShareDataLsf.getMasterlist().get(host.getId())=="1" &&(String)resul.get("master")=="0" )
				   {
				    content =host.getAlias()+ "(" + ipaddress + ")" + " �л�����ͨ�ڵ�";
				   }else
				   {
					   content =host.getAlias()+ "(" + ipaddress + ")" + " �л���master�ڵ�";
				   }
				   
				   this.check(host, content, name);
				   
			    }
				//�ѵ�ǰ��������ڴ�
			   ShareDataLsf.getMasterlist().put(host.getId(), resul.get("master"));
				
				
				
			}else
			{//��ֵ���뵽�ڴ���			
				ShareDataLsf.getMasterlist().put(host.getId(), resul.get("master"));
				
			}
			
			
			
			
			//System.out.println("=���̵�״ֵ̬===="+proce_tale.toString());
			
			
			
			if (proce_tale.get("mbschd") != null
					|| proce_tale.get("mbatchd") != null) {
				if (!proce_tale.containsKey("lim")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " lim���̶�ʧ";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("res")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res���̶�ʧ";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("pim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim���̶�ʧ";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("sbatchd")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd���̶�ʧ";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("mbatchd")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " mbatchd���̶�ʧ";
					String name = host.getId() + ":host:proce:mbatchd";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("mbschd")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " mbschd���̶�ʧ";
					String name = host.getId() + ":host:proce:mbschd";
					this.check(host, content, name);
				}
			} else {
				if (!proce_tale.containsKey("lim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim���̶�ʧ";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("res")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " res���̶�ʧ";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("pim")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim���̶�ʧ";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (!proce_tale.containsKey("sbatchd")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd���̶�ʧ";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				}
			}
			
			
			
			if (proce_tale.get("mbschd") != null
					|| proce_tale.get("mbatchd") != null) {
				if (proce_tale.get("lim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim״̬����";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (proce_tale.get("res").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res״̬����";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (proce_tale.get("pim").equals("Z")) {
					
					//System.out.println("================aa===========proce_tale.get('pim')==========="+proce_tale.get("pim")+"=ss==");
					
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim״̬����";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (proce_tale.get("sbatchd").equals("Z")) {
					String content =host.getAlias()+ "(" + ipaddress + ")" + " sbatchd״̬����";
					String name = host.getId() + ":host:proce:sbatchd";
					this.check(host, content, name);
				} else if (proce_tale.get("mbatchd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " mbatchd״̬����";
					String name = host.getId() + ":host:proce:mbatchd";
					this.check(host, content, name);
				} else if (proce_tale.get("mbschd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " mbschd״̬����";
					String name = host.getId() + ":host:proce:mbschd";
					this.check(host, content, name);
				}
			} else {
				if (proce_tale.get("lim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " lim״̬����";
					String name = host.getId() + ":host:proce:lim";
					this.check(host, content, name);
				} else if (proce_tale.get("res").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " res״̬����";
					String name = host.getId() + ":host:proce:res";
					this.check(host, content, name);
				} else if (proce_tale.get("pim").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " pim״̬����";
					String name = host.getId() + ":host:proce:pim";
					this.check(host, content, name);
				} else if (proce_tale.get("sbatchd").equals("Z")) {
					String content = host.getAlias()+"(" + ipaddress + ")" + " sbatchd״̬����";
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
					String content = host.getAlias()+"(" + ipaddress + ")" + " jid���̶�ʧ";
					String name = host.getId() + ":host:proce:jid";
					this.check(host, content, name);
				}else if(proce_tale.get("jid").equals("Z")){
					String content = host.getAlias()+"(" + ipaddress + ")" + " jid����״̬����";
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
				String content = host.getAlias()+"(" + ipaddress + ")" + " LSF ����״̬�쳣";
				String name = host.getId() + ":host:proce:LSFjid";
				this.check(host, content, name);
			}
		//----------------------��־�������-------------------------------------
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
				//�澯���
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
		
		
		//�����ڴ��еĸ澯�б�
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
	 *            �ڵ����
	 * @param Content
	 *            �澯������ #�豸ip��192.168.0.1:�����ݣ�LSF�ɼ��ļ���ʱ
	 * @param eventname
	 *            ��nodeid��host��proce��procename�� 58��host��proce��Lsflog
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
			eventlist.setReportman("ϵͳ��ѯ");
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
				// System.out.println("========��ֵ����==="+list.size());
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
						.get(z);
				// �������ڴ�ֵ���и澯���
				CheckEvent checkevent = new CheckEvent();
				checkevent.setAlarmlevel(1);
				checkevent.setName(eventname);
				SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
				// ���͸澯
				sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,
						alarmIndicatorsnode);// ����ط�дʲô
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

		for (int i = 0; i < fileList.length; i++) // ��һ�����µ��ļ�
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
			System.out.println("###��ʼɾ���ļ���" + delFile);
			// delFile.delete();
			System.out.println("###�ɹ�ɾ���ļ���" + delFile);
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
		// ��������
		procs.setCollecttime(Calendar.getInstance());
		// ���Ѿ����͵Ķ����б����õ�ǰ��PROC�Ѿ����͵Ķ���
		lastprocs = (Procs) sendeddata.get(procs.getIpaddress() + ":"
				+ procs.getProcname());

		/*
		 * try{ if (lastprocs==null){ //�ڴ��в����� ,����û��������,�򷢶��� Equipment equipment =
		 * equipmentManager.getByip(procs.getIpaddress()); Smscontent smscontent =
		 * new Smscontent(); String time =
		 * sdf.format(procs.getCollecttime().getTime());
		 * smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
		 * //���Ͷ��� Vector tosend = new Vector(); tosend.add(smscontent);
		 * smsmanager.sendSmscontent(tosend);
		 * //�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
		 * sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		 * }else{ //���Ѿ����͵Ķ����б�������IP��PROC���� //���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
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
		 * if (subvalue/(1000*60*60*24)>=1){ //����һ�죬���ٷ���Ϣ Smscontent smscontent =
		 * new Smscontent(); String time =
		 * sdf.format(procs.getCollecttime().getTime()); Equipment equipment =
		 * equipmentManager.getByip(procs.getIpaddress()); if (equipment ==
		 * null){ return; }else
		 * smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
		 * 
		 * //���Ͷ��� Vector tosend = new Vector(); tosend.add(smscontent);
		 * smsmanager.sendSmscontent(tosend);
		 * //�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
		 * sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);
		 * }else{ //û����һ��,��ֻд�¼� Vector eventtmpV = new Vector(); EventList event =
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
		 * event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"����"+procs.getProcname()+"��ʧ&level=1");
		 * eventtmpV.add(event); try{ eventmanager.createEventlist(eventtmpV);
		 * }catch(Exception e){ e.printStackTrace(); } } } }catch(Exception e){
		 * e.printStackTrace(); }
		 */
	}

	public void createFileNotExistSMS(String ipaddress) {
		// ��������
		// ���ڴ����õ�ǰ���IP��PING��ֵ
		Calendar date = Calendar.getInstance();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(
					ipaddress);
			if (host == null)
				return;

			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
				// �����ڣ��������ţ�������ӵ������б���
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("3");
				smscontent.setObjid(host.getId() + "");
				smscontent.setMessage(host.getAlias() + " ("
						+ host.getIpAddress() + ")" + "����־�ļ��޷���ȷ�ϴ������ܷ�����");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("host");
				smscontent.setSubentity("ftp");
				smscontent.setIp(host.getIpAddress());// ���Ͷ���
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(ipaddress + ":file" + host.getId(), date);
			} else {
				// ���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
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
					// ����һ�죬���ٷ���Ϣ
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("3");
					smscontent.setObjid(host.getId() + "");
					smscontent.setMessage(host.getAlias() + " ("
							+ host.getIpAddress() + ")" + "����־�ļ��޷���ȷ�ϴ������ܷ�����");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("host");
					smscontent.setSubentity("ftp");
					smscontent.setIp(host.getIpAddress());// ���Ͷ���
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					// �޸��Ѿ����͵Ķ��ż�¼
					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
