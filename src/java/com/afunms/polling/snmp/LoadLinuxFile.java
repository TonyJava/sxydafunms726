package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.course.util.LsfClassUtil;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.ShareDataLsf;
import com.afunms.common.util.SysUtil;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
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
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.gatherResulttosql.HostDatatempCollecttimeRtosql;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempiflistRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostDatatempserciceRttosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostProcessRtosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LoadLinuxFile {
	/**
	 * @param hostname
	 */
	private String ipaddress;
	 private Hashtable sendeddata = ShareData.getProcsendeddata();

	 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public LoadLinuxFile(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
	/**
	 * 2012-07-26
	 * @param host
	 *            �ڵ����
	 * @param Content
	 *            �澯������ #�豸ip��192.168.0.1:�����ݣ�LSF�ɼ��ļ���ʱ
	 * @param eventname
	 *            ��nodeid��host��proce��procename�� 58��host��proce��Lsflog
	 */
	public void check(Host host, String Content, String eventname,String alarmfile) {
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
					subtype, alarmfile);
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
	public LoadLinuxFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode)
    {
		
		
		
	
		
		
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		//yangjun
		
		ipaddress=host.getIpAddress();
		
		//���ϵͳ������ping ��ͨ�Ų��ٶ��ļ����н���
		if(ShareData.getAgentalarmlevellist().containsKey(host.getId()+":host:ping") )
		{
			
			
			//System.out.println("====1122===="+host.getId()+":host:ping"+(String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"));
			if(Integer.parseInt((String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"))>2)
			{
				
				//System.out.println("=====ping ��ͨ�˳�����ļ�============"+host.getIpAddress());
				return null;
			}
			
		}
		
		
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(ipaddress);
		if(ipAllData == null)ipAllData = new Hashtable();
		//
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector=new Vector();
		Vector systemVector=new Vector();
		Vector userVector=new Vector();
		Vector diskVector=new Vector();
		Vector processVector=new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		String collecttime = "";
		
		CPUcollectdata cpudata=null;
		Systemcollectdata systemdata=null;
		Usercollectdata userdata=null;
		Processcollectdata processdata=null;
		if(host == null)return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
    	try 
		{
    		
    		
    		String filename="";
    		if(ipaddress.equals("192.16.200.175"))
    		{
    			filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+"_date";		
    		}else
    		{
    		
    		filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+ipaddress+".log";	
    		}
			
		   // System.out.println("====�����ļ�=="+filename);
			File file=new File(filename);
			if(!file.exists()){
				//�ļ�������,������澯
				try{
					createFileNotExistSMS(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
			file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//�����ļ�����
    		while((strLine=br.readLine())!=null)
    		{
    			//System.out.println(strLine);
    			fileContent.append(strLine + "\n");
    			//SysLogger.info(strLine);
    		}
    		isr.close();
    		fis.close();
    		br.close();
    		try{
    			copyFile(ipaddress,getMaxNum(ipaddress));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
		}

    	 //
    	//System.out.println("+================================��ȡ�ļ���ɣ���");
    	//System.out.println(fileContent);
    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
    	
	       
	     //----------------�������ݲɼ�ʱ������--���������---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
//		if(mr.find())
//		{
//			collecttime = mr.group(2);
//		}
//		if (collecttime != null && collecttime.length()>0 ){
//			collecttime = collecttime.trim();
//		}
		//2012-07-26 �ϱ�ע�� ��־ʱ���ж�  �޸�Ϊ����
		if (mr.find()) {
			collecttime = mr.group(2).trim();
			//System.out.println("=============collecttime============"+collecttime);
			
			//boolean flg=false;
			if (null !=ShareDataLsf.getCollecttime_table() && !ShareDataLsf.getCollecttime_table().containsKey(host.getIpAddress()+"-log") ) {
				//System.out.println("=======1======collecttime============"+collecttime);
				ShareDataLsf.getCollecttime_table().put(host.getIpAddress()+"-log", collecttime);
				//flg=true;
			} else {
				String rm_time = (String) ShareDataLsf.getCollecttime_table().get(host.getIpAddress()+"-log");
				ShareDataLsf.getCollecttime_table().put(host.getIpAddress()+"-log", collecttime);
				
				//System.out.println("===rm_time="+rm_time);
				//System.out.println("===collecttime==="+collecttime);
				int time_flag = new LsfClassUtil().computeDateTime(rm_time,
						collecttime);
				
				//System.out.println("===time_flag=="+time_flag);
				//if(!flg){
					if (time_flag == 0 || time_flag > 10  || time_flag<=-1 ) {
						
						System.out.println("============��ʼ�澯===========");
						String content = host.getAlias() + "(" + ipaddress + ").log "
								+ " Linux��־�ļ��ɼ�ʱ�䳬ʱ,Ԥ��ʱ��Ϊ��10����";
						String name = host.getId() + ":host:log";
						this.check(host, content, name,"logfile");
					//}
				}
			}
		}
		//----------------����version����--���������---------------------
		String versionContent = "";
		tmpPt = Pattern.compile("(cmdbegin:version)(.*)(cmdbegin:cpuconfig)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			versionContent = mr.group(2);
			//System.out.println("&&&&&&&&&&&&&&&&&&=============="+versionContent);
		} 
		//SysLogger.info(versionContent+"#################################");
		if (versionContent != null && versionContent.length()>0){
			nodeconfig.setCSDVersion(versionContent.trim());
			//SysLogger.info(nodeconfig.getCSDVersion()+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7");
		}
		//SysLogger.info(versionContent+"#################################");
		
		//----------------����cpuconfig����--���������---------------------        	
		String cpuconfigContent = "";
		tmpPt = Pattern.compile("(cmdbegin:cpuconfig)(.*)(cmdbegin:disk)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpuconfigContent = mr.group(2);
			
			//System.out.println("**********cpu *****************");
		} 
		String[] cpuconfigLineArr = cpuconfigContent.split("\n");
		String[] cpuconfig_tmpData = null;
		List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		String procesors = "";
		for(int i=0; i<cpuconfigLineArr.length;i++){
			String[] result = cpuconfigLineArr[i].trim().split(":");
			if (result.length>0){
				if(result[0].trim().equalsIgnoreCase("processor")){
					nodecpuconfig.setNodeid(host.getId());
					nodecpuconfig.setProcessorId(result[1].trim());
					procesors = result[1].trim();
				}else if(result[0].trim().equalsIgnoreCase("model name")){
					nodecpuconfig.setName(result[1].trim());
				}else if(result[0].trim().equalsIgnoreCase("cpu MHz")){
					nodecpuconfig.setProcessorSpeed(result[1].trim());
				}else if(result[0].trim().equalsIgnoreCase("cache size")){
					nodecpuconfig.setL2CacheSize(result[1].trim());
					cpuconfiglist.add(nodecpuconfig);
					nodecpuconfig = new Nodecpuconfig();
				}							
			}
			
		}
		nodecpuconfig = null;
		//���ýڵ��CPU���ø���
		int procesorsnum = 0;
		if(procesors != null && procesors.trim().length()>0){
			try{
				procesorsnum = Integer.parseInt(procesors)+1;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		nodeconfig.setNumberOfProcessors(procesorsnum+"");
		
		//cpu��������澯
		//CheckEventUtil cEventUtil = new CheckEventUtil();
		//cEventUtil.hardwareInfo(host, "cpu", procesorsnum+"");
		
		//��----------------����disk����--���������---------------------
		//disk���ݼ��ϣ��仯ʱ���и澯���
		//Hashtable<String,Object> diskInfoHash = new Hashtable<String,Object>();
		//���̴�С
		float diskSize = 0;
		//�������Ƽ���
		List<String> diskNameList = new ArrayList<String>();
		
		String diskContent = "";
		String diskLabel;
		List disklist = new ArrayList();
		tmpPt = Pattern.compile("(cmdbegin:disk)(.*)(cmdbegin:diskperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskContent = mr.group(2);
		}
		String[] diskLineArr = diskContent.split("\n");
		String[] tmpData = null;
		//tmpPt = Pattern.compile("(^/[\\w/#]*)(\\s++)(\\d++)(\\s++)((\\d++)(\\s++)){2}+(\\d++)%");
		Diskcollectdata diskdata=null;
		int diskflag = 0;
		for(int i=1; i<diskLineArr.length;i++)
		{
			
			tmpData = diskLineArr[i].split("\\s++");
			if((tmpData != null) && (tmpData.length == 6))
			{
				diskLabel = tmpData[5];
				
				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Utilization");//���ðٷֱ�
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");
				diskdata.setUnit("%");
				try{
				diskdata.setThevalue(
						Float.toString(
								Float.parseFloat(
								tmpData[4].substring(
								0,
								tmpData[4].indexOf("%")))));
				}catch(Exception ex){
					continue;
				}
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("AllSize");//�ܿռ�
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float allblocksize=0;
				allblocksize=Float.parseFloat(tmpData[1]);
				float allsize=0.0f;
				allsize=allblocksize*1.0f/1024;
				//�����ܴ�С  ��λΪM
				diskSize = diskSize + allsize;
				//�������Ʒ��뼯��
				if (!diskdata.getSubentity().equals("")) {
					diskNameList.add(diskdata.getSubentity());
				}
				if(allsize>=1024.0f){
					allsize=allsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}

				diskdata.setThevalue(Float.toString(allsize));
				diskVector.addElement(diskdata);
				
				//yangjun 
				try {
					String diskinc = "0.0";
					float pastutil = 0.0f;
					Vector disk_v = (Vector)ipAllData.get("disk");
					if (disk_v != null && disk_v.size() > 0) {
						for (int si = 0; si < disk_v.size(); si++) {
							Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
							if((tmpData[5]).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
								pastutil = Float.parseFloat(disk_data.getThevalue());
							}
						}
					} else {
						pastutil = Float.parseFloat(tmpData[4].substring(0,tmpData[4].indexOf("%")));
					}
					if (pastutil == 0) {
						pastutil = Float.parseFloat(
								tmpData[4].substring(
										0,
										tmpData[4].indexOf("%")));
					}
					if(Float.parseFloat(
									tmpData[4].substring(
									0,
									tmpData[4].indexOf("%")))-pastutil>0){
						diskinc = (Float.parseFloat(
										tmpData[4].substring(
										0,
										tmpData[4].indexOf("%")))-pastutil)+"";
					}
					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UtilizationInc");// ���������ʰٷֱ�
					diskdata.setSubentity(tmpData[5]);
					diskdata.setRestype("dynamic");
					diskdata.setUnit("%");
					diskdata.setThevalue(diskinc);
					diskVector.addElement(diskdata);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("UsedSize");//ʹ�ô�С
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float UsedintSize=0;
				UsedintSize=Float.parseFloat(tmpData[2]);
				float usedfloatsize=0.0f;
				usedfloatsize=UsedintSize*1.0f/1024;
				if(usedfloatsize>=1024.0f){
					usedfloatsize=usedfloatsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}
				diskdata.setThevalue(Float.toString(usedfloatsize));
				diskVector.addElement(diskdata);
				disklist.add(diskflag,diskLabel);
				diskflag = diskflag +1;
			}
		}
		
	    //���д��̸澯���
	    //SysLogger.info("### ��ʼ���м������Ƿ�澯### ... ###");
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//SysLogger.info("alarmIndicatorsnode name ======"+alarmIndicatorsnode.getName());
				if(alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc")|| alarmIndicatorsnode.getName().equalsIgnoreCase("diskinc")){
					CheckEventUtil checkutil = new CheckEventUtil();
				    checkutil.checkDisk(host,diskVector,alarmIndicatorsnode);
				    //break;
				}
			}
			//##########�ܴ�С�Լ��̷���Ϣ�仯�����и澯�ж�
			//diskSize = diskSize/1024 ;
			//diskInfoHash.put("diskSize", diskSize+"G");
			//diskInfoHash.put("diskNameList", diskNameList);
			//CheckEventUtil checkutil = new CheckEventUtil();
	    	//checkutil.hardwareInfo(host, "disk", diskInfoHash);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		//SysLogger.info("disklist size ========================"+disklist.size());
		//----------------����diskperf����--���������---------------------        	
		String diskperfContent = "";
		String average = "";
		tmpPt = Pattern.compile("(cmdbegin:diskperf\n)(.*)(cmdbegin:cpu\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskperfContent = mr.group(2);
			
			//System.out.println("===========������Ϣ================");
			//System.out.println(diskperfContent);
			//System.out.println("===========������Ϣ================");
		} 
		String[] diskperfLineArr = diskperfContent.split("\n");
		String[] diskperf_tmpData = null;
		//Hashtable<String,Hashtable> alldiskperf = new Hashtable<String,Hashtable>();
		List<Hashtable> alldiskperf = new ArrayList<Hashtable>();
		Hashtable<String,String> diskperfhash = new Hashtable<String,String>();
		int flag = 0;
		for(int i=0; i<diskperfLineArr.length;i++){
			diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && diskperf_tmpData.length==10){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")){
					
					
					if(diskperf_tmpData[1].trim().equalsIgnoreCase("DEV")){
						//�����һ�б���
						continue;
					}else{
						//if(flag >= disklist.size())break;
						diskperfhash.put("tps", diskperf_tmpData[2].trim());
						diskperfhash.put("rd_sec/s", diskperf_tmpData[3].trim());
						diskperfhash.put("wr_sec/s", diskperf_tmpData[4].trim());
						diskperfhash.put("avgrq-sz", diskperf_tmpData[5].trim());
						diskperfhash.put("avgqu-sz", diskperf_tmpData[6].trim());
						diskperfhash.put("await", diskperf_tmpData[7].trim());
						diskperfhash.put("svctm", diskperf_tmpData[8].trim());
						diskperfhash.put("%util", diskperf_tmpData[9].trim());
						diskperfhash.put("%busy",Math.round(Float.parseFloat(diskperf_tmpData[8].trim())*100/(Float.parseFloat(diskperf_tmpData[7].trim())+Float.parseFloat(diskperf_tmpData[8].trim())))+"");
						diskperfhash.put("disklebel", diskperf_tmpData[1].trim());
						
//						//Diskcollectdata diskdata = null;
//		  				diskdata = new Diskcollectdata();
//						diskdata.setIpaddress(ipaddress);
//						diskdata.setCollecttime(date);
//						diskdata.setCategory("Disk");
//						diskdata.setEntity("Busy");// ���ðٷֱ�
//						diskdata.setSubentity((String)disklist.get(flag));
//						diskdata.setRestype("static");
//						diskdata.setUnit("%");
//						diskdata.setThevalue((String)diskperfhash.get("%busy"));
//						diskVector.addElement(diskdata);
//						//SysLogger.info("add ===========busy:"+diskdata.getThevalue());
//						//��KBytes/s
//						diskdata = new Diskcollectdata();
//						diskdata.setIpaddress(ipaddress);
//						diskdata.setCollecttime(date);
//						diskdata.setCategory("Disk");
//						diskdata.setEntity("ReadBytesPersec");//��KBytes/s
//						diskdata.setSubentity((String)disklist.get(flag));
//						diskdata.setRestype("static");
//						diskdata.setUnit("");
//						diskdata.setThevalue(Math.round(Float.parseFloat(diskperf_tmpData[3].trim())*512)+"");
//						diskVector.addElement(diskdata);
//						//дKBytes/s
//						diskdata = new Diskcollectdata();
//						diskdata.setIpaddress(ipaddress);
//						diskdata.setCollecttime(date);
//						diskdata.setCategory("Disk");
//						diskdata.setEntity("WriteBytesPersec");//дKBytes/s
//						diskdata.setSubentity((String)disklist.get(flag));
//						diskdata.setRestype("static");
//						diskdata.setUnit("");
//						diskdata.setThevalue(Math.round(Float.parseFloat(diskperf_tmpData[4].trim())*512)+"");
//						diskVector.addElement(diskdata);
						
						
						
						
						
						alldiskperf.add(diskperfhash);
						flag = flag +1;
						diskperfhash = new Hashtable();
						
					}
				}
			}				
		}
		
		//----------------����cpu����--���������---------------------        	
		String cpuperfContent = "";
		//String average = "";
		tmpPt = Pattern.compile("(cmdbegin:cpu\n)(.*)(cmdbegin:memory)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpuperfContent = mr.group(2);
		   //System.out.println("=====�ҵ�cpu ��������Ϣ======");
		   //System.out.println(cpuperfContent);
		   //System.out.println("=====�ҵ�cpu ��������Ϣ======");
		   
		   
		} 
		String[] cpuperfLineArr = cpuperfContent.split("\n");
		
		//System.out.println("------------------------------------------------");
		List cpuperflist = new ArrayList();
		Hashtable<String,String> cpuperfhash = new Hashtable<String,String>();
		for(int i=0; i<cpuperfLineArr.length;i++){
			//System.out.println("$$$$$$$$$$$$$$$$$"+cpuperfLineArr[i]);
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			//System.out.println("++++&&&&&&&&%%%%%%%%%%%="+diskperf_tmpData.length);
			
			if(diskperf_tmpData != null && diskperf_tmpData.length>=7 ){
				
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average:")){
				
					
						cpuperfhash.put("%user", diskperf_tmpData[2].trim());
						cpuperfhash.put("%nice", diskperf_tmpData[3].trim());
						cpuperfhash.put("%system", diskperf_tmpData[4].trim());
						cpuperfhash.put("%iowait", diskperf_tmpData[5].trim());
						//sar �İ汾��һ����Ҫ��������жϣ�
						//�޸��ˣ�konglq
						
						if(diskperf_tmpData.length==7)
						{
						//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&��ʽ=7"+diskperf_tmpData[6].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[6].trim());
						}
						
						if(diskperf_tmpData.length==8)
						{
						 //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&��ʽ=8"+diskperf_tmpData[7].trim());
							cpuperfhash.put("%steal", diskperf_tmpData[6].trim());
						 cpuperfhash.put("%idle", diskperf_tmpData[7].trim());
						}
						
						cpuperflist.add(cpuperfhash);
						
						
						cpudata=new CPUcollectdata();
				   		cpudata.setIpaddress(ipaddress);
				   		cpudata.setCollecttime(date);
				   		cpudata.setCategory("CPU");
				   		cpudata.setEntity("Utilization");
				   		cpudata.setSubentity("Utilization");
				   		cpudata.setRestype("dynamic");
				   		cpudata.setUnit("%");
				   		if(diskperf_tmpData.length==8)
						{
				   		cpudata.setThevalue(Arith.round((100.0-Double.parseDouble(diskperf_tmpData[7].trim())),0)+"");
						}
				   		
				   		if(diskperf_tmpData.length==7)
						{
				   		cpudata.setThevalue(Arith.round((100.0-Double.parseDouble(diskperf_tmpData[6].trim())),0)+"");
						}
				   		cpuVector.addElement(cpudata);
				   		
				   	//��CPUֵ���и澯���
				   		Hashtable collectHash = new Hashtable();
						collectHash.put("cpu", cpuVector);
					    try{
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux","cpu");
							for(int k = 0 ; k < list.size() ; k ++){
								AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
								//��CPUֵ���и澯���
								
								CheckEventUtil checkutil = new CheckEventUtil();
								checkutil.updateData(host,collectHash,"host","linux",alarmIndicatorsnode);
								//}
							}
					    }catch(Exception e){
					    	e.printStackTrace();
					    }
					
				}
			}				
		}
		//----------------����memory����--���������---------------------        	
		String memperfContent = "";
		tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:process)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			memperfContent = mr.group(2);
		} 
		String[] memperfLineArr = memperfContent.split("\n");
		List memperflist = new ArrayList();
		Vector memoryVector=new Vector();
		Memorycollectdata memorydata=null;
		Hashtable<String,String> memperfhash = new Hashtable<String,String>();
		for(int i=0; i<memperfLineArr.length;i++){
			diskperf_tmpData = memperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && diskperf_tmpData.length>=4){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Mem:")){
					memperfhash.put("total", diskperf_tmpData[1].trim());
					memperfhash.put("used", diskperf_tmpData[2].trim());
					memperfhash.put("free", diskperf_tmpData[3].trim());
					memperfhash.put("shared", diskperf_tmpData[4].trim());
					memperfhash.put("buffers", diskperf_tmpData[5].trim());
					memperfhash.put("cached", diskperf_tmpData[6].trim());
					memperflist.add(memperfhash);
					memperfhash = new Hashtable();
					//Memory
					float PhysicalMemUtilization =100- Float.parseFloat(diskperf_tmpData[3])* 100/ Float.parseFloat(diskperf_tmpData[1]);
					memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Capability");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(
							Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
		  			memoryVector.addElement(memorydata);
		  			
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("UsedSize");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(
							Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
		  			memoryVector.addElement(memorydata);
		  			
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Utilization");
		  			memorydata.setSubentity("PhysicalMemory");
		  			memorydata.setRestype("dynamic");
		  			memorydata.setUnit("%");
		  			memorydata.setThevalue(Math.round(PhysicalMemUtilization)+"");
		  			memoryVector.addElement(memorydata);
		  			
		  			
				
				    
				    
				    //�����ڴ��ܴ�С�仯�澯���
					///CheckEventUtil checkutil = new CheckEventUtil();
					//checkutil.hardwareInfo(host, "PhysicalMemory", Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024) +"M");
				}else if(diskperf_tmpData[0].trim().equalsIgnoreCase("Swap:")){
					memperfhash.put("total", diskperf_tmpData[1].trim());
					memperfhash.put("used", diskperf_tmpData[2].trim());
					memperfhash.put("free", diskperf_tmpData[3].trim());
					memperflist.add(memperfhash);
					memperfhash = new Hashtable();
					//Swap
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Capability");
		  			memorydata.setSubentity("SwapMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
		  			memoryVector.addElement(memorydata);
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("UsedSize");
		  			memorydata.setSubentity("SwapMemory");
		  			memorydata.setRestype("static");
		  			memorydata.setUnit("M");
		  			memorydata.setThevalue(
							Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
		  			memoryVector.addElement(memorydata);
					float SwapMemUtilization =(Integer.parseInt(diskperf_tmpData[2]))* 100/Integer.parseInt(diskperf_tmpData[1]);
					
		  			memorydata=new Memorycollectdata();
		  			memorydata.setIpaddress(ipaddress);
		  			memorydata.setCollecttime(date);
		  			memorydata.setCategory("Memory");
		  			memorydata.setEntity("Utilization");
		  			memorydata.setSubentity("SwapMemory");
		  			memorydata.setRestype("dynamic");
		  			memorydata.setUnit("%");
		  			memorydata.setThevalue(Math.round(SwapMemUtilization)+"");
		  			memoryVector.addElement(memorydata);
				}
			}				
		}
		
		
		
		
	    Hashtable collectHash = new Hashtable();
		collectHash.put("physicalmem", memoryVector);
		
		//�������ڴ�ֵ���и澯���
	    try{
//			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux","physicalmemory");
//			for(int k = 0 ; k < list.size() ; k ++){
//				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
//				//�������ڴ�ֵ���и澯���
//				CheckEventUtil checkutil = new CheckEventUtil();
//				checkutil.updateData(host,collectHash,"host","linux",alarmIndicatorsnode);
//				//}
//			}
	    	
	    	
	    	System.out.println("==========p--v=����===============");
	    	
	    	CheckEventUtil checkutil = new CheckEventUtil();
			checkutil.checkMemoryAndPage(host, collectHash,"physicalmemory","virtualmemory","linux");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		
		
		
		
		//��----------------����process����--���������---------------------        	
		String processContent = "";
		tmpPt = Pattern.compile("(cmdbegin:process)(.*)(cmdbegin:mac)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			processContent = mr.group(2);
		} 
		
		
		Hashtable procshash = new Hashtable();
		Vector procsV = new Vector();

		
		String[] cpu_LineArr = processContent.split("\n");
		String[] processtmpData = null;
		float cpuusage = 0.0f;
		for(int i=1; i<cpu_LineArr.length;i++)
		{    			
			processtmpData = cpu_LineArr[i].trim().split("\\s++");
			
			if((processtmpData != null) && (processtmpData.length == 12)){
				String USER=processtmpData[0];//USER
				if("USER".equalsIgnoreCase(USER))continue;
				String pid=processtmpData[1];//pid
				String vbstring1=processtmpData[10];//command
				String vbstring2="Ӧ�ó���";
				String vbstring3="";
				String vbstring4=processtmpData[5];//memsize
				if (vbstring4 == null)vbstring4="0";
				String vbstring5=processtmpData[9];//cputime
				String vbstring6=processtmpData[3];//%mem
				String vbstring7=processtmpData[7];//STAT
				String vbstring8=processtmpData[8];//STIME
				String vbstring9=processtmpData[2];//%CPU
				if("Z".equals(vbstring7)){
					vbstring3="��ʬ����";
				} else {
					vbstring3="��������";
				}
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("process_id");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit(" ");
				processdata.setThevalue(pid);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);	
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("USER");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit(" ");
				processdata.setThevalue(USER);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);	
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("MemoryUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring6);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);	
		
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Memory");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit("K");
				processdata.setThevalue(vbstring4);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Type");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring2);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Status");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring3);
				processVector.addElement(processdata);
				procshash.put(vbstring1.trim(), vbstring1.trim());//�����еĽ��̷��뵽�б���
				
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Name");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuTime");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("��");
				processdata.setThevalue(vbstring5);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("StartTime");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring8);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring9);
				processdata.setProcessname(vbstring1);
				processVector.addElement(processdata);
				/*
				//�ж��Ƿ�����Ҫ���ӵĽ��̣���ȡ�õ��б���������ӽ��̣����Vector��ȥ��
				if (procshash !=null && procshash.size()>0){
					if (procshash.containsKey(vbstring1)){
						procshash.remove(vbstring1);
						procsV.remove(vbstring1);
					}
				}
				*/
				
				
			}	
		}

		//�ж�ProcsV�ﻹ��û����Ҫ���ӵĽ��̣����У���˵����ǰû�������ý��̣������������������ý��̣�ͬʱд���¼�
		procsV=this.Getipprocesslist(ipaddress);//���ڴ��еõ���Ҫ���Ľ��̶���
		//System.out.println("========================��ʼ���̱Ƚ�==========================");
		
		
		
		//System.out.println("================��ʼlinux���̵ıȽ�================================="+procsV.size());
	     
	     if (procsV !=null && procsV.size()>0){
		     	for(int i=0;i<procsV.size();i++){	
			         
		     		Hashtable proc=(Hashtable)procsV.get(i);
		     		
		     		 //System.out.println("==procname="+proc.get("procname"));
		     		 //System.out.println(procshash.toString());
		     		 
		     		String eventContent="";
		     		boolean alarmflg=false;
		     		if(proc.get("wbstatus").equals("0"))
		     		{//������
		     			//System.out.println("==��==");
		     			
		     			
		     			if(!procshash.containsKey(proc.get("procname")))
		     			{//û���ҵ���Ӧ�Ľ��̳����澯
		     				eventContent=proc.get("procname")+"("+ipaddress+")���̶�ʧ";
		     				alarmflg=true;
		     				//System.out.println("===alarmflg===="+alarmflg);
		     				
		     			}else 
		     			{//�н��̵����ǽ�ʬ����Ҳ�澯
		     				
		     			}
		     			
		     		}else
		     		{//������
		     			//System.out.println("==��==");
		     			if(procshash.containsKey(proc.get("procname")))
		     			{//û���ҵ���Ӧ�Ľ��̳����澯
		     				eventContent=proc.get("procname")+"("+ipaddress+")�����������쳣";
		     				alarmflg=true;
		     			}
		     			
		     		}
		     		
		     	
		     		try{
		     			  
		     			
		     			if(alarmflg)
		     			{
			    		EventList eventlist = new EventList();
			    		eventlist.setEventtype("poll");
			    		eventlist.setEventlocation(host.getSysLocation());
			    		eventlist.setContent(eventContent);
			    		eventlist.setLevel1(Integer.parseInt((String )proc.get("alarmLevel")));
			    		eventlist.setManagesign(0);
			    		eventlist.setBak("");
			    		eventlist.setRecordtime(Calendar.getInstance());
			    		eventlist.setReportman("ϵͳ��ѯ");
			    		eventlist.setBusinessid(host.getBid());
			    		eventlist.setNodeid(host.getId());
			    		eventlist.setOid(0);
			    		eventlist.setSubtype("host");
			    		eventlist.setSubentity("proc");
			    		eventlist.setIpaddress(ipaddress);
						
						  try{
								AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
								List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","process");
								for(int z = 0 ; z < list.size() ; z ++){
									
									//System.out.println("========��ֵ����==="+list.size());
									AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(z);
									
																
									CheckEvent checkevent=new CheckEvent();
									
									checkevent.setAlarmlevel(Integer.parseInt((String )proc.get("alarmLevel")));
									checkevent.setName(proc.get("nodeid")+":host:proce:"+proc.get("procname"));
									
									
									SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
									
						    		//���͸澯
						    		sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,alarmIndicatorsnode);//����ط�дʲô
									
									//}
								}
						    }catch(Exception e){
						    	e.printStackTrace();
						    }
						
						}
		     		}catch(Exception e){
		     			e.printStackTrace();
		     		}
		     		
		     		
		     	}
		     }
    	
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("ProcessCount");
		systemdata.setSubentity("ProcessCount");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");
		systemdata.setThevalue(cpu_LineArr.length-1+"");
		systemVector.addElement(systemdata);	
		
		//��----------------����mac����--���������---------------------        	
		String macContent = "";
		tmpPt = Pattern.compile("(cmdbegin:mac)(.*)(cmdbegin:interface)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			macContent = mr.group(2);
		} 
		String[] macLineArr = macContent.split("\n");
		String[] mac_tmpData = null;
		String MAC = "";
		Hashtable machash = new Hashtable();
		for(int i=0; i<macLineArr.length;i++){
			mac_tmpData = macLineArr[i].trim().split("\\s++");			
			if (mac_tmpData.length==4){					
				if(mac_tmpData[0].equalsIgnoreCase("link/ether")&&mac_tmpData[2].equalsIgnoreCase("brd")){
					MAC = mac_tmpData[1];
					if(MAC.equalsIgnoreCase("00:00:00:00:00:00")){
						continue;
					}
					if(machash.containsKey(MAC))continue;
					machash.put(MAC, MAC);
					String mac_ = nodeconfig.getMac();
					if(mac_ != null && mac_.trim().length()>0){
						mac_=mac_+","+MAC;
						nodeconfig.setMac(mac_);
					}else{
						nodeconfig.setMac(MAC);
					}
				}
			}				
		}
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("MacAddr");
		systemdata.setSubentity("MacAddr");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");			  
		systemdata.setThevalue(MAC);
		systemVector.addElement(systemdata);
		
		
		
		//----------------����interface����--���������---------------------        	
		String interfaceContent = "";
		tmpPt = Pattern.compile("(cmdbegin:interface)(.*)(cmdbegin:uname)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			interfaceContent = mr.group(2);
		} 
		String[] interfaceLineArr = interfaceContent.split("\n");
		String[] interface_tmpData = null;
		
		ArrayList iflist = new ArrayList();
		Hashtable ifhash = new Hashtable();
		for(int i=0; i<interfaceLineArr.length;i++){
			Interfacecollectdata interfacedata = null;
			interface_tmpData = interfaceLineArr[i].trim().split("\\s++");	
			if(interface_tmpData != null && interface_tmpData.length==9){
				if(interfaceLineArr[i].contains("Average:")){
					if(interface_tmpData[1].trim().equalsIgnoreCase("IFACE"))continue;
					   ifhash.put("IFACE", interface_tmpData[1]);
					   ifhash.put("rxpck/s", interface_tmpData[2]);
					   ifhash.put("txpck/s", interface_tmpData[3]);
					   ifhash.put("rxbyt/s", interface_tmpData[4]);
					   ifhash.put("txbyt/s", interface_tmpData[5]);
					   ifhash.put("rxcmp/s", interface_tmpData[6]);
					   ifhash.put("txcmp/s", interface_tmpData[7]);
					   ifhash.put("rxmcst/s", interface_tmpData[8]);
					   
					   	//�˿�����
	  					interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("index");
						interfacedata.setSubentity(i+"");
						//�˿�״̬�����棬ֻ��Ϊ��̬���ݷŵ���ʱ����
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(i+"");
						interfacedata.setChname("�˿�����");
						interfaceVector.addElement(interfacedata);
	  					//�˿�����
	  					interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifDescr");
						interfacedata.setSubentity(i+"");
						//�˿�״̬�����棬ֻ��Ϊ��̬���ݷŵ���ʱ����
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(interface_tmpData[1]);
						interfacedata.setChname("�˿�����2");
						interfaceVector.addElement(interfacedata);
						//�˿ڴ���
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifSpeed");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue("");
						interfacedata.setChname("ÿ���ֽ���");
						interfaceVector.addElement(interfacedata);
						//��ǰ״̬
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue("up");
						interfacedata.setChname("��ǰ״̬");
						interfaceVector.addElement(interfacedata);
						//��ǰ״̬
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(1+"");
						interfacedata.setChname("��ǰ״̬");
						interfaceVector.addElement(interfacedata);
						//�˿��������
						UtilHdx utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						String chnameBand="";
						utilhdx.setEntity("InBandwidthUtilHdx");
						utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[4]))*8));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/��");	
						utilhdx.setChname(i+"�˿����"+"����");
						utilhdxVector.addElement(utilhdx);
						//�˿ڳ�������
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setEntity("OutBandwidthUtilHdx");
						utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[5]))*8));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/��");	
						utilhdx.setChname(i+"�˿ڳ���"+"����");
						utilhdxVector.addElement(utilhdx);
						/*
						//���������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("��վ�����������ݰ�");
						utilhdx.setEntity("ifInDiscards");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedDiscarded"));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						//��վ�������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("��վ�������ݰ�");
						utilhdx.setEntity("ifInErrors");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedErrors"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						//��ڷǵ��������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("�ǵ��������ݰ�");
						utilhdx.setEntity("ifInNUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedNonUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						//��ڵ��������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("���������ݰ�");
						utilhdx.setEntity("ifInUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						//���ڷǵ��������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("�ǵ��������ݰ�");
						utilhdx.setEntity("ifOutNUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsSentNonUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						//���ڵ��������ݰ�
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("���������ݰ�");
						utilhdx.setEntity("ifOutUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsSentUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("��");
						utilhdxVector.addElement(utilhdx);
						*/
					   iflist.add(ifhash);
					   ifhash = new Hashtable();
				}
				
			}
		}
		
		//��----------------����uname����--���������---------------------        	
		String unameContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:usergroup)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			unameContent = mr.group(2);
		} 
		String[] unameLineArr = unameContent.split("\n");
		String[] uname_tmpData = null;
		for(int i=0; i<unameLineArr.length;i++){
			uname_tmpData = unameLineArr[i].split("\\s++");				
			if (uname_tmpData.length==2){	
				  systemdata=new Systemcollectdata();
				  systemdata.setIpaddress(ipaddress);
				  systemdata.setCollecttime(date);
				  systemdata.setCategory("System");
				  systemdata.setEntity("operatSystem");
				  systemdata.setSubentity("operatSystem");
				  systemdata.setRestype("static");
				  systemdata.setUnit(" ");
				  systemdata.setThevalue(uname_tmpData[0]);
				  systemVector.addElement(systemdata);
				  
					systemdata=new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[1]);
				  systemVector.addElement(systemdata);								
				
			}				
		}
		
		//��----------------����usergroup����--���������--------------------- 
		Hashtable usergrouphash = new Hashtable();
		String usergroupContent = "";
		tmpPt = Pattern.compile("(cmdbegin:usergroup)(.*)(cmdbegin:user)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			usergroupContent = mr.group(2);
		} 
		String[] usergroupLineArr = usergroupContent.split("\n");
		String[] usergroup_tmpData = null;
		for(int i=0; i<usergroupLineArr.length;i++){
			usergroup_tmpData = usergroupLineArr[i].split(":");				
			if (usergroup_tmpData.length>=3){	
				usergrouphash.put((String)usergroup_tmpData[2], usergroup_tmpData[0]);
			}				
		}
		
		//��----------------����user����--���������---------------------        	
		String userContent = "";
		tmpPt = Pattern.compile("(cmdbegin:user)(.*)(cmdbegin:date)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			userContent = mr.group(2);
			
			//System.out.println("----(cmdbegin:user)(.*)(cmdbegin:date)----");
			//System.out.println("--------"+userContent);
			//System.out.println("-----(cmdbegin:user)(.*)(cmdbegin:date)---");
			
		} 
		String[] userLineArr = userContent.split("\n");
		String[] user_tmpData = null;
		for(int i=0; i<userLineArr.length;i++){
			//user_tmpData = userLineArr[i].split("\\s++");	
			String[] result = userLineArr[i].trim().split(":");
			
			if (result.length>4){
				//System.out.println("&&&***&&&&&&&����ǰ&&&&&&"+result[2]);
				//System.out.println(result[0]);
				//System.out.println(result[1]);
				//System.out.println(result[3]);
				//int userid = Integer.parseInt(result[2]);
				int userid=0;
				if(result[2].length() <6)
				{
					userid=Integer.parseInt(result[2]);
				}
				
				//String userid=result[2];
				
				
				
				//System.out.println("====��ȷͨ��====");
				//С��500��Ϊϵͳ���û�,����
				if(userid < 500)continue;
				
				int usergroupid = Integer.parseInt(result[3]);
				userdata=new Usercollectdata();
				userdata.setIpaddress(ipaddress);
				userdata.setCollecttime(date);
				userdata.setCategory("User");
				userdata.setEntity("Sysuser");
				String groupname = "";
				if(usergrouphash != null && usergrouphash.size()>0){
					if(usergrouphash.containsKey(usergroupid+"")){
						groupname = (String)usergrouphash.get(usergroupid+"");
					}
				}
				userdata.setSubentity(groupname+"");
				userdata.setRestype("static");
				userdata.setUnit(" ");
				userdata.setThevalue(result[0]);
				userVector.addElement(userdata);
				continue;								
			}
			
		}
		
		//��----------------����date����--���������---------------------        	
		String dateContent = "";
		tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			dateContent = mr.group(2);
		}
		if (dateContent != null && dateContent.length()>0){
//			Calendar   calendar=Calendar.getInstance(); 
//			calendar.setTime(new   Date( dateContent.trim())); 
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("Systime");
			systemdata.setSubentity("Systime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(dateContent.trim());
			systemVector.addElement(systemdata);

		}  

		//��----------------����uptime����--���������---------------------        	
		String uptimeContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:service)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			uptimeContent = mr.group(2);
		}
		if (uptimeContent != null && uptimeContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("SysUptime");
			systemdata.setSubentity("SysUptime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(uptimeContent.trim());
			systemVector.addElement(systemdata);
		}
		
		//��----------------����service����--���������---------------------  
		List servicelist = new ArrayList();
		Hashtable service = new Hashtable();
		String serviceContent = "";
		tmpPt = Pattern.compile("(cmdbegin:service)(.*)(cmdbegin:end)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			serviceContent = mr.group(2);
		}
		String[] serviceLineArr = serviceContent.split("\n");
		//String[] service_tmpData = null;
		String[] result = null;
		for(int i=0; i<serviceLineArr.length;i++){
			//user_tmpData = userLineArr[i].split("\\s++");	
			result = serviceLineArr[i].trim().split("\\s++");	
			
			if (result.length==8){
				//System.out.println("&&&***&&&&&&&����ǰ&&&&&&"+result[2]);
				//System.out.println(result[0]);
				//System.out.println(result[1]);
				//System.out.println(result[3]);
				//int userid = Integer.parseInt(result[2]);
				try{
					service.put("name", result[0]);
					String servicestatus = result[5];
					if(servicestatus.indexOf("on") >= 0 || servicestatus.indexOf("����") >= 0){
						service.put("status", "����");
					}else{
						service.put("status", "δ����");
					}
					
					servicelist.add(service);
				}catch(Exception e){
					e.printStackTrace();
				}
				service = new Hashtable();
				
												
			}
			
		}

		try{
			deleteFile(ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		System.out.println("====disk���ݿ�ʼ���===================="+diskVector.size());
		if (diskVector != null && diskVector.size()>0)
			{
			
			System.out.println("====================disk���ݿ�ʼ���=============================");
			returnHash.put("disk",diskVector);
			
			 //�Ѳɼ��������sql
		    HostdiskResultosql tosql=new HostdiskResultosql();
		    tosql.CreateResultTosql(returnHash, host.getIpAddress());
	
		    HostDatatempDiskRttosql temptosql=new HostDatatempDiskRttosql();
		    temptosql.CreateResultTosql(returnHash, host);
		    tosql=null;
		    temptosql=null;
			}
		if (cpuVector != null && cpuVector.size()>0)
			{
			returnHash.put("cpu",cpuVector);
			
			 NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
			 totempsql.CreateResultTosql(returnHash, host);
			 totempsql=null;
			}
		if (memoryVector != null && memoryVector.size()>0)
			{
			returnHash.put("memory",memoryVector);
			
			//�Ѳɼ��������sql
		    HostPhysicalMemoryResulttosql  tosql=new HostPhysicalMemoryResulttosql();
		    tosql.CreateResultTosql(returnHash, host.getIpAddress());
		    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
		    totempsql.CreateResultTosql(returnHash, host);
		    
			}
		if (userVector != null && userVector.size()>0)
		{
			returnHash.put("user",userVector);
			
			HostDatatempUserRtosql tosql=new HostDatatempUserRtosql();
			tosql.CreateResultTosql(returnHash, host);
        }
		if (processVector != null && processVector.size()>0)
			{
			returnHash.put("process",processVector);
			
			//�ѽ������sql
			HostDatatempProcessRtTosql temptosql=new HostDatatempProcessRtTosql();
			temptosql.CreateResultTosql(returnHash, host);
			
			HostProcessRtosql Rtosql=new HostProcessRtosql();
			Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
			}
		if (systemVector != null && systemVector.size()>0)
			{
			returnHash.put("system",systemVector);
			
			NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (nodeconfig != null)
			{
			returnHash.put("nodeconfig",nodeconfig);
			
			HostDatatempNodeconfRtosql tosql=new HostDatatempNodeconfRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (iflist != null && iflist.size()>0)
			{
			returnHash.put("iflist",iflist);
			
			HostDatatempiflistRtosql tosql=new HostDatatempiflistRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (utilhdxVector != null && utilhdxVector.size()>0)
			{
			returnHash.put("utilhdx",utilhdxVector);
			
			HostDatatemputilhdxRtosql tosql=new HostDatatemputilhdxRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (interfaceVector != null && interfaceVector.size()>0)
			{
			returnHash.put("interface",interfaceVector);
			
			HostDatatempinterfaceRtosql tosql=new HostDatatempinterfaceRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (alldiskperf != null && alldiskperf.size()>0)
			{
			returnHash.put("alldiskperf",alldiskperf);
			
			HostDatatempnDiskperfRtosql tosql=new HostDatatempnDiskperfRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (servicelist != null && servicelist.size()>0)
			{
			returnHash.put("servicelist",servicelist);
			
			HostDatatempserciceRttosql totempsql=new HostDatatempserciceRttosql();
			totempsql.CreateResultLinuxTosql(returnHash, host);
			}
		//System.out.println("====++++++++++alldiskperf+++++++======"+alldiskperf.size());
		if (cpuconfiglist != null && cpuconfiglist.size()>0)
			{
			returnHash.put("cpuconfiglist",cpuconfiglist);
			
			HostDatatempCpuconfiRtosql tosql=new HostDatatempCpuconfiRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		//System.out.println("====++++++++++CPU+++++++======"+cpuperflist.size());
		if (cpuperflist != null && cpuperflist.size()>0)
			{
			returnHash.put("cpuperflist",cpuperflist);
			
			HostcpuResultTosql rtosql=new HostcpuResultTosql();
			rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
			
			HostDatatempCpuperRtosql tmptosql=new HostDatatempCpuperRtosql();
			tmptosql.CreateResultTosql(returnHash, host);
			
			}
		
		returnHash.put("collecttime",collecttime);
		
		HostDatatempCollecttimeRtosql tosql=new HostDatatempCollecttimeRtosql();
		tosql.CreateResultTosql(returnHash, host);
		
		//
		 ShareData.getSharedata().put(host.getIpAddress(), returnHash);
		
		
		
		
		return returnHash;
    }	
	
    public String getMaxNum(String ipAddress){
    	String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
   		String[] fileList = logFolder.list();
   		
   		for(int i=0;i<fileList.length;i++) //��һ�����µ��ļ�
   		{
   			if(!fileList[i].startsWith(ipAddress)) continue;
   			
   			return ipAddress;
   		}
   		return maxStr;
    }
	
    public void deleteFile(String ipAddress){

			try
			{
				//File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
			//System.out.println("###��ʼɾ���ļ���"+delFile);
			//delFile.delete();
			//System.out.println("###�ɹ�ɾ���ļ���"+delFile);
			}
			catch(Exception e)		
			{}
    }
    public void copyFile(String ipAddress,String max){
	try   { 
		String currenttime = SysUtil.getCurrentTime();
		currenttime = currenttime.replaceAll("-", "");
		currenttime = currenttime.replaceAll(" ", "");
		currenttime = currenttime.replaceAll(":", "");
		String ipdir = ipAddress.replaceAll("\\.", "-");
		String filename = ResourceCenter.getInstance().getSysPath() + "linuxserver_bak/"+ipdir;
		File file=new File(filename);
		if(!file.exists())file.mkdir();
        //String cmd   =   "cmd   /c   copy   "+ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" +ipdir+"\\"+ ipAddress+"-" +currenttime+ ".log";             
        //SysLogger.info(cmd);
		String cmd   =   "cp   "+ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak/" +ipdir+"/"+ ipAddress+"-" +currenttime+ ".log";
		System.out.print(cmd);
        Process   child   =   Runtime.getRuntime().exec(cmd);   
      }catch (IOException e){    
        e.printStackTrace();
    } 
	
    }
	 public void createSMS(Procs procs){
		 	Procs lastprocs = null;
		 	//��������	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//���Ѿ����͵Ķ����б����õ�ǰ��PROC�Ѿ����͵Ķ���
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//�ڴ��в�����	,����û��������,�򷢶���
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
		 			//���Ͷ���
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//���Ѿ����͵Ķ����б�������IP��PROC����
		 			//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = (Calendar)procs.getCollecttime();
		 			cc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(cc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();			 			
		 			
		 			if (subvalue/(1000*60*60*24)>=1){
		 				//����һ�죬���ٷ���Ϣ
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
			 			
			 			//���Ͷ���
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//û����һ��,��ֻд�¼�
			 			Vector eventtmpV = new Vector();
						EventList event = new EventList();
						  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(procs.getIpaddress());
						  event.setEventtype("host");
						  event.setEventlocation(procs.getIpaddress());
						  event.setManagesign(new Integer(0));
						  event.setReportman("monitorpc");
						  event.setRecordtime(Calendar.getInstance());
						  event.setLevel1(new Integer(1));
						  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
						  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
						  String time = sdf.format(Calendar.getInstance().getTime());
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"����"+procs.getProcname()+"��ʧ&level=1");
						  eventtmpV.add(event);
						  try{
							  eventmanager.createEventlist(eventtmpV);
						  }catch(Exception e){
							  e.printStackTrace();
						  }						  
			 		}
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 	*/
		 }
	 
	 
	 
	 /**
		 * 
		 * ���ڴ�����л�ȡ��ǰ��Ҫƥ��Ľ�����
		 * @param ipaddressip��ַ
		 * @return
		 */
		 public Vector Getipprocesslist(String ipaddress)
		 {
			 Vector list=new Vector();
			 Hashtable Vector  = new Hashtable();//���̵������б�

			 Set set = ShareData.getProcessconfigHashtable().keySet(); // get set-view of keys
			   // get iterator
			   Iterator itr = set.iterator();
			   while (itr.hasNext()) {
			    String str = (String) itr.next();
			    if(str.indexOf(ipaddress+"-")>=0)
			    
			    	list.add( ShareData.getProcessconfigHashtable().get(str));
			   }

			  return list;
			 
		 }
	 
	 
	 public void createFileNotExistSMS(String ipaddress){
		 	//��������		 	
		 	//���ڴ����õ�ǰ���IP��PING��ֵ
				Calendar date=Calendar.getInstance();
				try{
					Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
					if(host == null)return;
					
					if (!sendeddata.containsKey(ipaddress+":file:"+host.getId())){
						//�����ڣ��������ţ�������ӵ������б���
						Smscontent smscontent = new Smscontent();
						String time = sdf.format(date.getTime());
						smscontent.setLevel("3");
						smscontent.setObjid(host.getId()+"");
						smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"����־�ļ��޷���ȷ�ϴ������ܷ�����");
						smscontent.setRecordtime(time);
						smscontent.setSubtype("host");
						smscontent.setSubentity("ftp");
						smscontent.setIp(host.getIpAddress());//���Ͷ���
						SmscontentDao smsmanager=new SmscontentDao();
						smsmanager.sendURLSmscontent(smscontent);	
						sendeddata.put(ipaddress+":file"+host.getId(),date);		 					 				
					}else{
						//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
						Calendar formerdate =(Calendar)sendeddata.get(ipaddress+":file:"+host.getId());		 				
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
		 			
						long subvalue = current.getTime()-last.getTime();			 			
						if (subvalue/(1000*60*60*24)>=1){
							//����һ�죬���ٷ���Ϣ
							Smscontent smscontent = new Smscontent();
							String time = sdf.format(date.getTime());
							smscontent.setLevel("3");
							smscontent.setObjid(host.getId()+"");
							smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"����־�ļ��޷���ȷ�ϴ������ܷ�����");
							smscontent.setRecordtime(time);
							smscontent.setSubtype("host");
							smscontent.setSubentity("ftp");
							smscontent.setIp(host.getIpAddress());//���Ͷ���
							SmscontentDao smsmanager=new SmscontentDao();
							smsmanager.sendURLSmscontent(smscontent);
							//�޸��Ѿ����͵Ķ��ż�¼	
							sendeddata.put(ipaddress+":file:"+host.getId(),date);	
						}	
					}	 			 			 			 			 	
				}catch(Exception e){
					e.printStackTrace();
				}
		 	}	 
	 
}






