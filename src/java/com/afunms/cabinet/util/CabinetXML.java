package com.afunms.cabinet.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.afunms.cabinet.dao.CabinetEquipmentDao;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NodeMonitorDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.NodeMonitor;


public class CabinetXML {

	private static Element getElementForAttr(String name, String[] keyfield, String[] key){
		Element item = new Element(name);
		if(keyfield != null && keyfield.length > 0){
			for(int i = 0; i < keyfield.length; i++){
				item.setAttribute(keyfield[i],key[i]);
			}
		}
		return item;
	}

	public static long WriteXMLDoc(Document Doc, String filepath, String filename){
		try{
			org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("    "); //缩进4个空格后换行
			XMLOutputter XMLOut = new XMLOutputter(format); 
			// 输出 XML 文件； 
			File file=new File(filepath);
			boolean s=false;
			if(file.exists() == false){
				s=file.mkdirs();
			}
			
			FileOutputStream fops = new FileOutputStream(filepath+filename);
			
			XMLOut.output(Doc, fops);  
			fops.close();
			return 0;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}  
	} 
	public void CreateCabinetXML(){
		EqpRoomDao roomDao = new EqpRoomDao();
		//查询机房
		List<EqpRoom> roomList = roomDao.loadAll();
		roomDao.close();
		
		
		for(int i=0;i<roomList.size();i++){
			//创建节点
			Element root = new Element("DataResult");
			root.setAttribute("label","root");// 设置根节点属性
			int number = 0;
			//根节点添加到文档中
			Document Doc = new Document(root);
			
			List list = root.getChildren();
			Element item = getElementForAttr("item",null,null);
			MachineCabinetDao machineDao = new MachineCabinetDao();
			int roomId = roomList.get(i).getId();
			
//			CopyFile cf = new CopyFile();
//			File resFilefn = new File(ResourceCenter.getInstance().getSysPath()+"fn"+roomId);
//			File resFilely = new File(ResourceCenter.getInstance().getSysPath()+"cabinetroom"+roomId);
//			if(!resFilefn.exists()){
//				cf.copy(ResourceCenter.getInstance().getSysPath()+"fangan", ResourceCenter.getInstance().getSysPath()+"fn"+roomId);
//			}
//			if(!resFilely.exists()){
//				cf.copy(ResourceCenter.getInstance().getSysPath()+"fanganswf", ResourceCenter.getInstance().getSysPath()+"cabinetroom"+roomId);
//			}
			
			Hashtable<String,Integer> ht = machineDao.getCountByU(roomId);
			machineDao.close();
			int alarmcount=0;
			
			if( ht != null && ht.size() > 0 ){
				for(Iterator it = ht.keySet().iterator(); it.hasNext();){ 
			        String key = (String)it.next(); 
			        Integer value = ht.get(key);
			        machineDao = new MachineCabinetDao();
			        List<MachineCabinet> machineList = machineDao.loadByRoomIDAndU(roomId, key);
			        machineDao.close();
			        //String attr1 = key + "u";
			        //item.setAttribute("label",attr1);
			        item.setAttribute("label","32u");
			        Element num = getElementForAttr("num32U",new String[]{"type"},new String[]{"int"});
			        num.setAttribute("label",machineList.size()+"");
			        item.addContent(num);
			        for(int j=0;j < machineList.size(); j++){
			        	MachineCabinet mc = machineList.get(j);
			        	Element jglocality = new Element("jglocality"+ ((i+1)==1?(IntToEnglish(number+1)):number+1+""));
			        	number += 1;
			        	Element jgid = getElementForAttr("jgid",null,null).addContent(mc.getId()+"");
						Element jgx = getElementForAttr("jgx",null,null).addContent(mc.getMachinex());
						Element jgy = getElementForAttr("jgy",null,null).addContent(mc.getMachiney());
						Element jgz = getElementForAttr("jgz",null,null).addContent(mc.getMachinez());
						Element jgname = getElementForAttr("jgname",null,null).addContent(mc.getName());
						jglocality.addContent(jgid);
						jglocality.addContent(jgx);
						jglocality.addContent(jgy);
						jglocality.addContent(jgz);
						jglocality.addContent(jgname);
						
						CabinetEquipmentDao equipmentDao = new CabinetEquipmentDao();	
						List<CabinetEquipment> equipmentList = equipmentDao.loadByCabinetID(mc.getId());
						equipmentDao.close();
						for(int k=0;k<equipmentList.size();k++){
							CabinetEquipment ce = equipmentList.get(k);
							Host host = (Host) PollingEngine.getInstance().getNodeByID(ce.getNodeid());
							String hintstr = "";
							if(ce.getNodeid() != -1){
								//设备被监视
								if (host == null) {
									HostNode node = null;  
									HostNodeDao hostdao = new HostNodeDao();
									try {
										node = (HostNode) hostdao.findByID(ce.getNodeid()+"");
										
									} catch (Exception e) {
									} finally {
										hostdao.close();
									}
									if(node == null){
										continue;
									}
									HostLoader loader = new HostLoader();
									loader.loadOne(node);
									loader.close();
									host = (Host) PollingEngine.getInstance().getNodeByID(ce.getNodeid());
								}else{
									hintstr = getInfo(host);
								}
							}else{
								//设备不被监视
								host = new Host();
								host.setAlarm(false);
								host.setType("未监视类型");
								host.setIpAddress("");
								host.setCategory(-1);
								hintstr = "连通率:##CPU利用率:##内存利用率:"; 
							}
							//SysLogger.info("host=="+host.getIpAddress()+"=info=="+info);
							
							//机房有告警
							if(host.isAlarm())
							{
								alarmcount++;
								
							}
							
							
							Element serverelement = getElementForAttr("serverelement"+(k+1),new String[]{"test"},new String[]{""+(k+1)});
							Element serverid = getElementForAttr("serverid",null,null).addContent(host.getId()+"");
							Element serveralarm = getElementForAttr("serveralarm",null,null).addContent(host.isAlarm()?"1":"0");
							Element servername = getElementForAttr("servername",null,null).addContent(ce.getNodename());
							Element servertype = getElementForAttr("servertype",null,null).addContent(host.getType());
							Element serverapp = getElementForAttr("serverapp",null,null).addContent(ce.getBusinessName());
							Element serverip = getElementForAttr("serverip",null,null).addContent(host.getIpAddress());
							Element serverlocal = getElementForAttr("serverlocal",null,null).addContent(ce.getUnmubers());
//							Element serverhint = getElementForAttr("serverhint",null,null).addContent(host.getWritecommunity());
							Element serverhint = getElementForAttr("serverhint",null,null).addContent(hintstr);
							Element servercategory = getElementForAttr("servercategory",null,null).addContent(host.getCategory()+"");
							serverelement.addContent(serverid);
							serverelement.addContent(serveralarm);
							serverelement.addContent(servername);
							serverelement.addContent(servertype);
							serverelement.addContent(serverapp);
							serverelement.addContent(serverip);
							serverelement.addContent(serverlocal);
							serverelement.addContent(serverhint);
							serverelement.addContent(servercategory);
							jglocality.addContent(serverelement);
							
						}
						item.addContent(jglocality);
			        }
			        if(alarmcount>0)
			        ShareData.getRoomAlarmHashtable().put(roomId+"",alarmcount+"");
			        //item.addContent(num);
				}
			}
			list.add(item);
			String file = "data.xml";// 保存到项目文件夹下的指定文件夹
			String filePath = ResourceCenter.getInstance().getSysPath() + "fn" + (i+1) + "/org/dhc/";// 获取系统文件夹路径
			WriteXMLDoc(Doc,filePath,file);
		}
	}
	
	/**
	 * 获取ping、cpu和内存信息
	 * @param host  网络设备或服务器model
	 * @return
	 */
	private String getInfo(Host host){
		StringBuffer infoBuffer = new StringBuffer();
    	//net10
//    	String nodeType = null;
//    	int category = host.getCategory();
    	NodeUtil nodeUtil = new NodeUtil();
    	NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
//    	if(nodedto.getType().equals("net")){
//    		nodeType = "网络设备";
//    	}else if(nodedto.getType().equals("host")){
//    		nodeType = "服务器";
//    	} 
    	//取出当前连通率和平均响应时间
    	//ping和响应时间begin
//        String avgresponse = "0";
    	String pingValue ="0";
		Hashtable connectUtilizationhash = new Hashtable();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String time = sdf.format(new Date());	
//		String starttime = time + " 00:00:00";
//		String totime = time + " 23:59:59";
		Vector memoryVector = new Vector();
		//cpu利用率
		String cpuValue = "0";			
		//内存利用率
		String memoryValue="0";
		double memeryValueDouble = 0;
		double cpuValueDouble = 0;
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);
		String runmodel = PollingEngine.getCollectwebflag();//采集与访问模式
//		I_HostCollectData hostmanager = new HostCollectDataManager();
//		try {
//			connectUtilizationhash = hostmanager.getCategory(host .getIpAddress(), "Ping", "ResponseTime", starttime, totime);
//			if (connectUtilizationhash.get("avgpingcon") != null) {
//				avgresponse = (String) connectUtilizationhash.get("avgpingcon");
//				avgresponse = avgresponse.replace("毫秒", "").replaceAll("%","");
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		if("0".equals(runmodel)){
			//采集与访问是集成模式
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipAllData = (Hashtable)sharedata.get(host.getIpAddress());
			Hashtable allpingdata = ShareData.getPingdata();
			if(ipAllData!=null){
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
					if(cpu != null && cpu.getThevalue() != null){
						cpuValueDouble = Double.valueOf(cpu.getThevalue());
						cpuValue = numberFormat.format(cpuValueDouble);
					}
				}
				memoryVector = (Vector) ipAllData.get("memory");
			}
			if(allpingdata!= null){
				Vector pingData = (Vector)allpingdata.get(host.getIpAddress());
				if(pingData != null && pingData.size() > 0 ){
					Pingcollectdata pingcollectdata = (Pingcollectdata) pingData.get(0);
					pingValue = pingcollectdata.getThevalue();
				}
			}
		}else{
			//采集与访问是分离模式
			CpuInfoService cpuInfoService = new CpuInfoService(nodedto.getId()+ "", nodedto.getType(), nodedto.getSubtype());
    		Vector cpuV = cpuInfoService.getCpuInfo();
    		if (cpuV != null && cpuV.size() > 0) {
    			CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
    			if (cpu != null && cpu.getThevalue() != null) {
    				cpuValueDouble = new Double(cpu.getThevalue());
    			}
    		}
			cpuValue = Double.valueOf(cpuValueDouble).intValue()+"";
			List memoryList = null;
			//获取内存信息
			memoryList = new NetService(host.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrMemoryInfo();
			if (memoryList != null && memoryList.size() > 0) {
				for (int i = 0; i < memoryList.size(); i++) {
					Memorycollectdata memorycollectdata = new Memorycollectdata();
					NodeTemp nodetemp = (NodeTemp) memoryList.get(i);
					memorycollectdata.setEntity(nodetemp.getSubentity());
					memorycollectdata.setSubentity(nodetemp.getSindex());
					memorycollectdata.setThevalue(nodetemp.getThevalue());
					memoryVector.add(memorycollectdata);
				}
			}
			//取出可用性
			PingInfoService pingInfoService = new PingInfoService(nodedto.getId()+"",nodedto.getType(),nodedto.getSubtype());
			String[] pingSubentitis = new String[]{"Utilization"};
			List<NodeTemp> pingInfoList = pingInfoService.getCurrPingInfo(pingSubentitis);
			if(pingInfoList != null){
				for(int j=0; j<pingInfoList.size(); j++){
					NodeTemp nodeTemp = pingInfoList.get(j);
					if((nodedto.getId()+"").equals(nodeTemp.getNodeid())){
						pingValue = nodeTemp.getThevalue();
					}
				}
			}
		}
		
		//根据设备的类型的不同，存储集合的结构也不一样
		if(nodedto.getType().equals("net")){
			double allmemoryvalue = 0;
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int i = 0; i < memoryVector.size(); i++) {
					Memorycollectdata memorycollectdata = (Memorycollectdata) memoryVector.get(i);
					allmemoryvalue = allmemoryvalue + Integer.parseInt(memorycollectdata.getThevalue());
				}
				memeryValueDouble = Math.round(allmemoryvalue / memoryVector.size());
				memoryValue = numberFormat.format(memeryValueDouble);
			}
    	}else if(nodedto.getType().equals("host")){
    		//得到内存利用率
    		if (memoryVector != null && memoryVector.size() > 0) {
    			for (int i = 0; i < memoryVector.size(); i++) {
    				Memorycollectdata memorydata = (Memorycollectdata) memoryVector.get(i);
    				if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
    					memoryValue = Math.round(Float.parseFloat(memorydata.getThevalue())) + "";
    				}
    			}
    		}
    	}
		infoBuffer.append("连通率:");
		infoBuffer.append(pingValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
//		infoBuffer.append("响应时间:");
//		infoBuffer.append(avgresponse);
//		infoBuffer.append("ms");
//		infoBuffer.append("##");
		infoBuffer.append("CPU利用率:");
		infoBuffer.append(cpuValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
		infoBuffer.append("内存利用率:");
		infoBuffer.append(memoryValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
		return infoBuffer.toString();
	}
	
	private String IntToEnglish(int i){
		if(i<20){
			return IntToEnglishTeen(i);
		}
		if(i<100){
			if(i%10==0){
				return IntToEnglishDecade(i);
			}else{
				return IntToEnglishDecade(i) + "-" + IntToEnglishTeen(i%10);
			}
		}
		if(i<1000){
			String back ="";
			back = IntToEnglishTeen(i/100) + " hundred";
			if(i%100==0){
				return back;
			}else{
				if(i%100 < 20){
					return back + " and " + IntToEnglishTeen(i%100);
				}else if(i%10==0){
					return back + " and " + IntToEnglishDecade(i%100);
				}else{
					return back + " and " + IntToEnglishDecade(i%100) + "-" + IntToEnglishTeen(i%10);
				}
			}
		}
		if(i<10000){
			String back ="";
			back = IntToEnglishTeen(i/1000) + " thousand";
			if(i%1000==0){
				return back;
			}else{
				if(i%1000 < 20){
					return back + " and " + IntToEnglishTeen(i%1000);
				}else if(i%1000 < 100){
					if(i%1000%10 == 0){
						return back + " and " + IntToEnglishDecade(i%1000);
					}else{
						return back + " and " + IntToEnglishDecade(i%1000) + "-" + IntToEnglishTeen(i%10);
					}
				}else{
					if(i%100==0){
						return back + " and " + IntToEnglishTeen(i%1000/100) + " hundred";
					}else{
						back = back + " " + IntToEnglishTeen(i%1000/100) + " hundred";
						if(i%100 < 20){
							return back + " and " + IntToEnglishTeen(i%100);
						}else if(i%10 == 0){
							return back + " and " + IntToEnglishDecade(i%100);
						}else{
							return back + " and " + IntToEnglishDecade(i%100) + "-" + IntToEnglishTeen(i%10);
						}
					}
				}
			}
		}
		return null;
	}
	//0~9
	private String IntToEnglishTeen(int i){
		if(i==0){
			return "zero";
		}
		if(i==1){
			return "one";
		}
		if(i==2){
			return "two";
		}
		if(i==3){
			return "three";
		}
		if(i==4){
			return "four";
		}
		if(i==5){
			return "five";
		}
		if(i==6){
			return "six";
		}
		if(i==7){
			return "seven";
		}
		if(i==8){
			return "eight";
		}
		if(i==9){
			return "nine";
		}
		if(i==10){
			return "ten";
		}
		if(i==11){
			return "eleven";
		}
		if(i==12){
			return "twelve";
		}
		if(i==13){
			return "thirteen";
		}
		if(i==14){
			return "fourteen";
		}
		if(i==15){
			return "fifteen";
		}
		if(i==16){
			return "sixteen";
		}
		if(i==17){
			return "seventeen";
		}
		if(i==18){
			return "eighteen";
		}
		if(i==19){
			return "nineteen";
		}
		return null;
	}
	
	private String IntToEnglishDecade(int i){
		
		if(i==20||i/10==2){
			return "twenty";
		}
		if(i==30||i/10==3){
			return "thirty";
		}
		if(i==40||i/10==4){
			return "forty";
		}
		if(i==50||i/10==5){
			return "fifty";
		}
		if(i==60||i/10==6){
			return "sixty";
		}
		if(i==70||i/10==7){
			return "seventy";
		}
		if(i==80||i/10==8){
			return "eighty";
		}
		if(i==90||i/10==9){
			return "ninety";
		}
		return null;
	}
	public static void main(String[] args){
		CabinetXML cx = new CabinetXML();
		for(int i=0;i<10000;i++){
		System.out.println(cx.IntToEnglish(i));
		}
	}
}