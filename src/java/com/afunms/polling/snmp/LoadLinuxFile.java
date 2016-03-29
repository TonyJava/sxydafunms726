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
	 *            节点对象
	 * @param Content
	 *            告警的内容 #设备ip：192.168.0.1:（内容）LSF采集文件超时
	 * @param eventname
	 *            （nodeid：host：proce：procename） 58：host：proce：Lsflog
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
					subtype, alarmfile);
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
	public LoadLinuxFile() {
		
	}
	
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode)
    {
		
		
		
	
		
		
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		//yangjun
		
		ipaddress=host.getIpAddress();
		
		//如果系统中遇到ping 不通着不再对文件进行解析
		if(ShareData.getAgentalarmlevellist().containsKey(host.getId()+":host:ping") )
		{
			
			
			//System.out.println("====1122===="+host.getId()+":host:ping"+(String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"));
			if(Integer.parseInt((String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"))>2)
			{
				
				//System.out.println("=====ping 不通退出检查文件============"+host.getIpAddress());
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
			
		   // System.out.println("====解析文件=="+filename);
			File file=new File(filename);
			if(!file.exists()){
				//文件不存在,则产生告警
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
    		//读入文件内容
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
    	//System.out.println("+================================读取文件完成！！");
    	//System.out.println(fileContent);
    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
    	
	       
	     //----------------解析数据采集时间内容--创建监控项---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
//		if(mr.find())
//		{
//			collecttime = mr.group(2);
//		}
//		if (collecttime != null && collecttime.length()>0 ){
//			collecttime = collecttime.trim();
//		}
		//2012-07-26 上边注释 日志时间判断  修改为以下
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
						
						System.out.println("============开始告警===========");
						String content = host.getAlias() + "(" + ipaddress + ").log "
								+ " Linux日志文件采集时间超时,预定时间为：10分钟";
						String name = host.getId() + ":host:log";
						this.check(host, content, name,"logfile");
					//}
				}
			}
		}
		//----------------解析version内容--创建监控项---------------------
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
		
		//----------------解析cpuconfig内容--创建监控项---------------------        	
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
		//设置节点的CPU配置个数
		int procesorsnum = 0;
		if(procesors != null && procesors.trim().length()>0){
			try{
				procesorsnum = Integer.parseInt(procesors)+1;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		nodeconfig.setNumberOfProcessors(procesorsnum+"");
		
		//cpu个数变更告警
		//CheckEventUtil cEventUtil = new CheckEventUtil();
		//cEventUtil.hardwareInfo(host, "cpu", procesorsnum+"");
		
		//　----------------解析disk内容--创建监控项---------------------
		//disk数据集合，变化时进行告警检测
		//Hashtable<String,Object> diskInfoHash = new Hashtable<String,Object>();
		//磁盘大小
		float diskSize = 0;
		//磁盘名称集合
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
				diskdata.setEntity("Utilization");//利用百分比
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
				diskdata.setEntity("AllSize");//总空间
				diskdata.setSubentity(tmpData[5]);
				diskdata.setRestype("static");

				float allblocksize=0;
				allblocksize=Float.parseFloat(tmpData[1]);
				float allsize=0.0f;
				allsize=allblocksize*1.0f/1024;
				//磁盘总大小  单位为M
				diskSize = diskSize + allsize;
				//磁盘名称放入集合
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
					diskdata.setEntity("UtilizationInc");// 利用增长率百分比
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
				diskdata.setEntity("UsedSize");//使用大小
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
		
	    //进行磁盘告警检测
	    //SysLogger.info("### 开始运行检测磁盘是否告警### ... ###");
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
			//##########总大小以及盘符信息变化，进行告警判断
			//diskSize = diskSize/1024 ;
			//diskInfoHash.put("diskSize", diskSize+"G");
			//diskInfoHash.put("diskNameList", diskNameList);
			//CheckEventUtil checkutil = new CheckEventUtil();
	    	//checkutil.hardwareInfo(host, "disk", diskInfoHash);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		//SysLogger.info("disklist size ========================"+disklist.size());
		//----------------解析diskperf内容--创建监控项---------------------        	
		String diskperfContent = "";
		String average = "";
		tmpPt = Pattern.compile("(cmdbegin:diskperf\n)(.*)(cmdbegin:cpu\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskperfContent = mr.group(2);
			
			//System.out.println("===========磁盘信息================");
			//System.out.println(diskperfContent);
			//System.out.println("===========磁盘信息================");
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
						//处理第一行标题
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
//						diskdata.setEntity("Busy");// 利用百分比
//						diskdata.setSubentity((String)disklist.get(flag));
//						diskdata.setRestype("static");
//						diskdata.setUnit("%");
//						diskdata.setThevalue((String)diskperfhash.get("%busy"));
//						diskVector.addElement(diskdata);
//						//SysLogger.info("add ===========busy:"+diskdata.getThevalue());
//						//读KBytes/s
//						diskdata = new Diskcollectdata();
//						diskdata.setIpaddress(ipaddress);
//						diskdata.setCollecttime(date);
//						diskdata.setCategory("Disk");
//						diskdata.setEntity("ReadBytesPersec");//读KBytes/s
//						diskdata.setSubentity((String)disklist.get(flag));
//						diskdata.setRestype("static");
//						diskdata.setUnit("");
//						diskdata.setThevalue(Math.round(Float.parseFloat(diskperf_tmpData[3].trim())*512)+"");
//						diskVector.addElement(diskdata);
//						//写KBytes/s
//						diskdata = new Diskcollectdata();
//						diskdata.setIpaddress(ipaddress);
//						diskdata.setCollecttime(date);
//						diskdata.setCategory("Disk");
//						diskdata.setEntity("WriteBytesPersec");//写KBytes/s
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
		
		//----------------解析cpu内容--创建监控项---------------------        	
		String cpuperfContent = "";
		//String average = "";
		tmpPt = Pattern.compile("(cmdbegin:cpu\n)(.*)(cmdbegin:memory)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpuperfContent = mr.group(2);
		   //System.out.println("=====找到cpu 的新能信息======");
		   //System.out.println(cpuperfContent);
		   //System.out.println("=====找到cpu 的新能信息======");
		   
		   
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
						//sar 的版本不一样需要做下面的判断，
						//修改人：konglq
						
						if(diskperf_tmpData.length==7)
						{
						//System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&格式=7"+diskperf_tmpData[6].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[6].trim());
						}
						
						if(diskperf_tmpData.length==8)
						{
						 //System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&格式=8"+diskperf_tmpData[7].trim());
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
				   		
				   	//对CPU值进行告警检测
				   		Hashtable collectHash = new Hashtable();
						collectHash.put("cpu", cpuVector);
					    try{
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux","cpu");
							for(int k = 0 ; k < list.size() ; k ++){
								AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
								//对CPU值进行告警检测
								
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
		//----------------解析memory内容--创建监控项---------------------        	
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
		  			
		  			
				
				    
				    
				    //物理内存总大小变化告警检测
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
		
		//对物理内存值进行告警检测
	    try{
//			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "linux","physicalmemory");
//			for(int k = 0 ; k < list.size() ; k ++){
//				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
//				//对物理内存值进行告警检测
//				CheckEventUtil checkutil = new CheckEventUtil();
//				checkutil.updateData(host,collectHash,"host","linux",alarmIndicatorsnode);
//				//}
//			}
	    	
	    	
	    	System.out.println("==========p--v=联合===============");
	    	
	    	CheckEventUtil checkutil = new CheckEventUtil();
			checkutil.checkMemoryAndPage(host, collectHash,"physicalmemory","virtualmemory","linux");
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		
		
		
		
		//　----------------解析process内容--创建监控项---------------------        	
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
				String vbstring2="应用程序";
				String vbstring3="";
				String vbstring4=processtmpData[5];//memsize
				if (vbstring4 == null)vbstring4="0";
				String vbstring5=processtmpData[9];//cputime
				String vbstring6=processtmpData[3];//%mem
				String vbstring7=processtmpData[7];//STAT
				String vbstring8=processtmpData[8];//STIME
				String vbstring9=processtmpData[2];//%CPU
				if("Z".equals(vbstring7)){
					vbstring3="僵尸进程";
				} else {
					vbstring3="正在运行";
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
				procshash.put(vbstring1.trim(), vbstring1.trim());//把所有的进程放入到列表中
				
				
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
				processdata.setUnit("秒");
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
				//判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
				if (procshash !=null && procshash.size()>0){
					if (procshash.containsKey(vbstring1)){
						procshash.remove(vbstring1);
						procsV.remove(vbstring1);
					}
				}
				*/
				
				
			}	
		}

		//判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
		procsV=this.Getipprocesslist(ipaddress);//从内存中得到需要检查的进程对象
		//System.out.println("========================开始进程比较==========================");
		
		
		
		//System.out.println("================开始linux进程的比较================================="+procsV.size());
	     
	     if (procsV !=null && procsV.size()>0){
		     	for(int i=0;i<procsV.size();i++){	
			         
		     		Hashtable proc=(Hashtable)procsV.get(i);
		     		
		     		 //System.out.println("==procname="+proc.get("procname"));
		     		 //System.out.println(procshash.toString());
		     		 
		     		String eventContent="";
		     		boolean alarmflg=false;
		     		if(proc.get("wbstatus").equals("0"))
		     		{//白名单
		     			//System.out.println("==白==");
		     			
		     			
		     			if(!procshash.containsKey(proc.get("procname")))
		     			{//没有找到对应的进程长生告警
		     				eventContent=proc.get("procname")+"("+ipaddress+")进程丢失";
		     				alarmflg=true;
		     				//System.out.println("===alarmflg===="+alarmflg);
		     				
		     			}else 
		     			{//有进程但是是僵尸进程也告警
		     				
		     			}
		     			
		     		}else
		     		{//黑名单
		     			//System.out.println("==黑==");
		     			if(procshash.containsKey(proc.get("procname")))
		     			{//没有找到对应的进程长生告警
		     				eventContent=proc.get("procname")+"("+ipaddress+")黑名单进程异常";
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
			    		eventlist.setReportman("系统轮询");
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
									
									//System.out.println("========阀值个数==="+list.size());
									AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(z);
									
																
									CheckEvent checkevent=new CheckEvent();
									
									checkevent.setAlarmlevel(Integer.parseInt((String )proc.get("alarmLevel")));
									checkevent.setName(proc.get("nodeid")+":host:proce:"+proc.get("procname"));
									
									
									SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
									
						    		//发送告警
						    		sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,alarmIndicatorsnode);//这个地方写什么
									
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
		
		//　----------------解析mac内容--创建监控项---------------------        	
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
		
		
		
		//----------------解析interface内容--创建监控项---------------------        	
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
					   
					   	//端口索引
	  					interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("index");
						interfacedata.setSubentity(i+"");
						//端口状态不保存，只作为静态数据放到临时表里
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(i+"");
						interfacedata.setChname("端口索引");
						interfaceVector.addElement(interfacedata);
	  					//端口描述
	  					interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifDescr");
						interfacedata.setSubentity(i+"");
						//端口状态不保存，只作为静态数据放到临时表里
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(interface_tmpData[1]);
						interfacedata.setChname("端口描述2");
						interfaceVector.addElement(interfacedata);
						//端口带宽
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifSpeed");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue("");
						interfacedata.setChname("每秒字节数");
						interfaceVector.addElement(interfacedata);
						//当前状态
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue("up");
						interfacedata.setChname("当前状态");
						interfaceVector.addElement(interfacedata);
						//当前状态
						interfacedata=new Interfacecollectdata();
						interfacedata.setIpaddress(ipaddress);
						interfacedata.setCollecttime(date);
						interfacedata.setCategory("Interface");
						interfacedata.setEntity("ifOperStatus");
						interfacedata.setSubentity(i+"");
						interfacedata.setRestype("static");
						interfacedata.setUnit("");
						interfacedata.setThevalue(1+"");
						interfacedata.setChname("当前状态");
						interfaceVector.addElement(interfacedata);
						//端口入口流速
						UtilHdx utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						String chnameBand="";
						utilhdx.setEntity("InBandwidthUtilHdx");
						utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[4]))*8));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/秒");	
						utilhdx.setChname(i+"端口入口"+"流速");
						utilhdxVector.addElement(utilhdx);
						//端口出口流速
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setEntity("OutBandwidthUtilHdx");
						utilhdx.setThevalue(Long.toString(Math.round(Float.parseFloat(interface_tmpData[5]))*8));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("Kb/秒");	
						utilhdx.setChname(i+"端口出口"+"流速");
						utilhdxVector.addElement(utilhdx);
						/*
						//丢弃的数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("入站被丢弃的数据包");
						utilhdx.setEntity("ifInDiscards");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedDiscarded"));
						utilhdx.setSubentity(i+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						//入站错误数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("入站错误数据包");
						utilhdx.setEntity("ifInErrors");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedErrors"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						//入口非单向传输数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("非单向传输数据包");
						utilhdx.setEntity("ifInNUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedNonUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						//入口单向传输数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("单向传输数据包");
						utilhdx.setEntity("ifInUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsReceivedUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						//出口非单向传输数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("非单向传输数据包");
						utilhdx.setEntity("ifOutNUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsSentNonUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						//出口单向传输数据包
						utilhdx=new UtilHdx();
						utilhdx.setIpaddress(ipaddress);
						utilhdx.setCollecttime(date);
						utilhdx.setCategory("Interface");
						utilhdx.setChname("单向传输数据包");
						utilhdx.setEntity("ifOutUcastPkts");
						utilhdx.setThevalue((String)rValue.get("PacketsSentUnicastPersec"));
						utilhdx.setSubentity(k+"");
						utilhdx.setRestype("dynamic");
						utilhdx.setUnit("个");
						utilhdxVector.addElement(utilhdx);
						*/
					   iflist.add(ifhash);
					   ifhash = new Hashtable();
				}
				
			}
		}
		
		//　----------------解析uname内容--创建监控项---------------------        	
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
		
		//　----------------解析usergroup内容--创建监控项--------------------- 
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
		
		//　----------------解析user内容--创建监控项---------------------        	
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
				//System.out.println("&&&***&&&&&&&错误前&&&&&&"+result[2]);
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
				
				
				
				//System.out.println("====正确通过====");
				//小于500的为系统级用户,过滤
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
		
		//　----------------解析date内容--创建监控项---------------------        	
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

		//　----------------解析uptime内容--创建监控项---------------------        	
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
		
		//　----------------解析service内容--创建监控项---------------------  
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
				//System.out.println("&&&***&&&&&&&错误前&&&&&&"+result[2]);
				//System.out.println(result[0]);
				//System.out.println(result[1]);
				//System.out.println(result[3]);
				//int userid = Integer.parseInt(result[2]);
				try{
					service.put("name", result[0]);
					String servicestatus = result[5];
					if(servicestatus.indexOf("on") >= 0 || servicestatus.indexOf("启用") >= 0){
						service.put("status", "启用");
					}else{
						service.put("status", "未启用");
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
		
		
		System.out.println("====disk数据开始入库===================="+diskVector.size());
		if (diskVector != null && diskVector.size()>0)
			{
			
			System.out.println("====================disk数据开始入库=============================");
			returnHash.put("disk",diskVector);
			
			 //把采集结果生成sql
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
			
			//把采集结果生成sql
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
			
			//把结果生成sql
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
   		
   		for(int i=0;i<fileList.length;i++) //找一个最新的文件
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
			//System.out.println("###开始删除文件："+delFile);
			//delFile.delete();
			//System.out.println("###成功删除文件："+delFile);
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
		 	//建立短信	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//内存中不存在	,表明没发过短信,则发短信
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
		 			//发送短信
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//若已经发送的短信列表存在这个IP的PROC进程
		 			//若在，则从已发送短信列表里判断是否已经发送当天的短信		 				
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
		 				//超过一天，则再发信息
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&进程丢失&level=2");
			 			
			 			//发送短信
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//把该进程信息添加到已经发送的进程短信列表里,以IP:进程名作为key
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//没超过一天,则只写事件
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
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"进程"+procs.getProcname()+"丢失&level=1");
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
		 * 从内存类表中获取当前需要匹配的进程数
		 * @param ipaddressip地址
		 * @return
		 */
		 public Vector Getipprocesslist(String ipaddress)
		 {
			 Vector list=new Vector();
			 Hashtable Vector  = new Hashtable();//进程的所有列表

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
		 	//建立短信		 	
		 	//从内存里获得当前这个IP的PING的值
				Calendar date=Calendar.getInstance();
				try{
					Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
					if(host == null)return;
					
					if (!sendeddata.containsKey(ipaddress+":file:"+host.getId())){
						//若不在，则建立短信，并且添加到发送列表里
						Smscontent smscontent = new Smscontent();
						String time = sdf.format(date.getTime());
						smscontent.setLevel("3");
						smscontent.setObjid(host.getId()+"");
						smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
						smscontent.setRecordtime(time);
						smscontent.setSubtype("host");
						smscontent.setSubentity("ftp");
						smscontent.setIp(host.getIpAddress());//发送短信
						SmscontentDao smsmanager=new SmscontentDao();
						smsmanager.sendURLSmscontent(smscontent);	
						sendeddata.put(ipaddress+":file"+host.getId(),date);		 					 				
					}else{
						//若在，则从已发送短信列表里判断是否已经发送当天的短信
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
							//超过一天，则再发信息
							Smscontent smscontent = new Smscontent();
							String time = sdf.format(date.getTime());
							smscontent.setLevel("3");
							smscontent.setObjid(host.getId()+"");
							smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
							smscontent.setRecordtime(time);
							smscontent.setSubtype("host");
							smscontent.setSubentity("ftp");
							smscontent.setIp(host.getIpAddress());//发送短信
							SmscontentDao smsmanager=new SmscontentDao();
							smsmanager.sendURLSmscontent(smscontent);
							//修改已经发送的短信记录	
							sendeddata.put(ipaddress+":file:"+host.getId(),date);	
						}	
					}	 			 			 			 			 	
				}catch(Exception e){
					e.printStackTrace();
				}
		 	}	 
	 
}






