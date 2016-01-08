package com.afunms.flex;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.interfaceInfo.InterfaceInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Avgcollectdata;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.system.model.User;
import com.afunms.system.vo.FlexVo;
import com.afunms.system.vo.Vos;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

import flex.messaging.FlexContext;
import flex.messaging.FlexSession;

public class TopNService{
	   
	public TopNService(){}
	public FlexSession flexSession;
	//根据权限获取各种设备列表
	public List getNodeList(int category,String bids){
//		System.out.println("bids=="+bids);
//		System.out.println("category=="+category);      
		List networklist = new ArrayList();
		HostNodeDao nodedao = new HostNodeDao();
		try {
			networklist = nodedao.loadNetworkByBid(category, bids);
	    } catch (Exception e) {
			e.printStackTrace();
	    } finally {     
			nodedao.close();
	    }   
		return networklist;
	}
    //网络设备cpu实时数据topn
//	public List getNetworkTempCPUList(int size,String bids) {
//		List networkCPUList = new ArrayList();
//		List cpulist = new ArrayList();
//		List networklist = getNodeList(1,bids);
//		if (networklist != null && networklist.size() > 0) {
//		    for (int i = 0; i < networklist.size(); i++) {
//			    HostNode node = (HostNode) networklist.get(i);
//			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
//			    if (ipAllData != null) {
//			    	Vector cpuV = (Vector) ipAllData.get("cpu");
//					if (cpuV != null && cpuV.size() > 0) {
//					    CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
//					    Avgcollectdata avgcollectdata = new Avgcollectdata();
//					    avgcollectdata.setIpaddress(node.getIpAddress());
//					    String cpuValue = cpu.getThevalue();
//					    if (cpuValue == null) {
//						    cpuValue = "0";
//					    }
//					    cpuValue = cpuValue.replace("%", "");
//					    avgcollectdata.setThevalue(cpuValue);
//					    cpulist.add(avgcollectdata);
//					}
//			    }
//		    }
//		}
//		if (cpulist != null && cpulist.size() > 0) {
//			for (int m = 0; m < cpulist.size(); m++) {
//			    Avgcollectdata hdata = (Avgcollectdata) cpulist.get(m);
//			    for (int n = m + 1; n < cpulist.size(); n++) {
//					Avgcollectdata hosdata = (Avgcollectdata) cpulist.get(n);
//					if (new Double(hdata.getThevalue().replace("%", ""))
//						.doubleValue() < new Double(hosdata
//						.getThevalue().replace("%", "")).doubleValue()) {
//					    cpulist.remove(m);
//					    cpulist.add(m, hosdata);
//					    cpulist.remove(n);
//					    cpulist.add(n, hdata);
//					    hdata = hosdata;
//					    hosdata = null;
//					}
//			    }
//			    networkCPUList.add(hdata);
//			    if(networkCPUList.size()>=size-1){
//			    	break;
//			    }
//			    hdata = null;
//			}
//		}
//		return networkCPUList;
//    }
	/**
     * 网络设备CPU当天平均值topn
     */
//    public List getNetworkCPUList(int size,String bids) {
//		List networklist = getNodeList(1,bids);
//		List networkCPUList = new ArrayList();
//		List cpulist = new ArrayList();
//		if (networklist != null && networklist.size() > 0) {
//		    for (int i = 0; i < networklist.size(); i++) {
//				HostNode node = (HostNode) networklist.get(i);
//				I_HostCollectData hostManager = new HostCollectDataManager();
//				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+ " 00:00:00";
//				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//				// 从collectdata取cpu的历史数据,存放在表中
//				Hashtable cpuHash = new Hashtable();
//				try {
//				    cpuHash = hostManager.getCategory(node.getIpAddress(),"CPU", "Utilization", startTime, endTime);
//				} catch (Exception e) {
//				    e.printStackTrace();
//				}
//				if (cpuHash.get("avgcpucon") != null) {
//				    String avgcpucon = (String) cpuHash.get("avgcpucon");
//				    Avgcollectdata avgcollectdata = new Avgcollectdata();
//				    avgcollectdata.setIpaddress(node.getIpAddress());
//				    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
//				    cpulist.add(avgcollectdata);
//				}
//		    }
//		    if (cpulist != null && cpulist.size() > 0) {
//				for (int m = 0; m < cpulist.size(); m++) {
//				    Avgcollectdata hdata = (Avgcollectdata) cpulist.get(m);
//				    for (int n = m + 1; n < cpulist.size(); n++) {
//						Avgcollectdata hosdata = (Avgcollectdata) cpulist.get(n);
//						if (new Double(hdata.getThevalue().replace("%", ""))
//							.doubleValue() < new Double(hosdata
//							.getThevalue().replace("%", "")).doubleValue()) {
//						    cpulist.remove(m);
//						    cpulist.add(m, hosdata);
//						    cpulist.remove(n);
//						    cpulist.add(n, hdata);
//						    hdata = hosdata;
//						    hosdata = null;
//						}
//				    }
//				    // 得到排序后的Subentity的列表
//				    networkCPUList.add(hdata);
//				    if(networkCPUList.size()>=size-1){
//				    	break;
//				    }
//				    hdata = null;
//				}
//		    }
//		}
//		return (List<CPUcollectdata>) networkCPUList;
//    }
    /**
     * 获取相应ip地址的cpu在当天各个时间段的值(flex调用)
     */
//    public ArrayList<Vos> getCPUValue(String ipAddress) {
//		I_HostCollectData hostManager = new HostCollectDataManager();
//		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
//		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//		// 从collectdata取cpu的历史数据,存放在表中
//		Hashtable cpuHash = new Hashtable();
//		try {
//		    cpuHash = hostManager.getCategory(ipAddress, "CPU", "Utilization",startTime, endTime);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		List cpuList = new ArrayList();
//		if (cpuHash.get("list") != null) {
//		    ArrayList<Vos> flexDataList = new ArrayList<Vos>();
//		    cpuList = (ArrayList) cpuHash.get("list");
//		    Vos fVo;
//		    for (int i = 0; i < cpuList.size(); i++) {
//				Vector cpuVector = new Vector();
//				fVo = new Vos();
//				cpuVector = (Vector) cpuList.get(i);
//				if (cpuVector != null || cpuVector.size() > 0) {
//				    fVo.setObjectNumber((String) cpuVector.get(0));
//				    fVo.setObjectName2((String) cpuVector.get(1));
//				    fVo.setObjectName1(ipAddress);
//				    flexDataList.add(fVo);
//				}
//		    }
//		    return flexDataList;
//		}
//		return null;
//    }
    
    //网络设备出口流速topn实时数据
//    public List getNetworkTempOutList(int size,String bids){
//    	List networklist = getNodeList(1,bids);
//    	List alloututillist = new ArrayList();
//    	List oututilhdxlist = new ArrayList();
//    	if (networklist != null && networklist.size() > 0) {
//    		for (int i = 0; i < networklist.size(); i++) {
//    		    HostNode node = (HostNode) networklist.get(i);
//    		    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
//    		    if (ipAllData != null) {
//    		    	Vector allutil = (Vector) ipAllData.get("allutilhdx");
//    				if (allutil != null && allutil.size() == 3) {
//    				    Avgcollectdata avgcollectdatao = new Avgcollectdata();
//    				    AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
//    				    avgcollectdatao.setIpaddress(node.getIpAddress());
//    				    avgcollectdatao.setThevalue(oututilhdx.getThevalue());
//    				    oututilhdxlist.add(avgcollectdatao);
//    				}
//    		    }
//    		}
//    		if (oututilhdxlist != null && oututilhdxlist.size() > 0) {
//    			for (int m = 0; m < oututilhdxlist.size(); m++) {
//    			    Avgcollectdata hdata = (Avgcollectdata) oututilhdxlist.get(m);
//    			    for (int n = m + 1; n < oututilhdxlist.size(); n++) {
//	    				Avgcollectdata hosdata = (Avgcollectdata) oututilhdxlist.get(n);
//	    				if (new Double(hdata.getThevalue()).doubleValue() < new Double(
//	    					hosdata.getThevalue()).doubleValue()) {
//	    				    oututilhdxlist.remove(m);
//	    				    oututilhdxlist.add(m, hosdata);
//	    				    oututilhdxlist.remove(n);
//	    				    oututilhdxlist.add(n, hdata);
//	    				    hdata = hosdata;
//	    				    hosdata = null;
//	    				}
//    			    }
//    			    // 得到排序后的Subentity的列表
//    			    alloututillist.add(hdata);
//    			    if(alloututillist.size()>=size-1){
//				    	break;
//				    }
//    			    hdata = null;
//    			}
//    		}
//    	}
//		return oututilhdxlist;
//    }
    /**
     * 网络设备出口流速(flex调用)
     */
//    public List getNetworkOutList(int size,String bids) {
//		List networklist = getNodeList(1,bids);
//		List networkOutList = new ArrayList();
//		List oututilhdxlist = new ArrayList();
//		if (networklist != null && networklist.size() > 0) {
//		    for (int i = 0; i < networklist.size(); i++) {
//				HostNode node = (HostNode) networklist.get(i);
//				I_HostCollectData hostManager = new HostCollectDataManager();
//				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
//				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//				Hashtable oututilHash = new Hashtable();
//				try {
//				    oututilHash = hostManager.getAllutilhdx(node.getIpAddress(), "AllOutBandwidthUtilHdx",startTime, endTime, "");
//				} catch (Exception e) {
//				    e.printStackTrace();
//				}
//				if (oututilHash.get("avgput") != null) {
//				    String avgutilcon = (String) oututilHash.get("avgput");
//				    Avgcollectdata avgcollectdata = new Avgcollectdata();
//				    avgcollectdata.setIpaddress(node.getIpAddress());
//				    avgcollectdata.setThevalue(avgutilcon);
//				    oututilhdxlist.add(avgcollectdata);
//				}
//		    }
//		    if (oututilhdxlist != null && oututilhdxlist.size() > 0) {
//				for (int m = 0; m < oututilhdxlist.size(); m++) {
//				    Avgcollectdata hdata = (Avgcollectdata) oututilhdxlist
//					    .get(m);
//				    for (int n = m + 1; n < oututilhdxlist.size(); n++) {
//						Avgcollectdata hosdata = (Avgcollectdata) oututilhdxlist
//							.get(n);
//						if (new Double(hdata.getThevalue()).doubleValue() < new Double(
//							hosdata.getThevalue()).doubleValue()) {
//						    oututilhdxlist.remove(m);
//						    oututilhdxlist.add(m, hosdata);
//						    oututilhdxlist.remove(n);
//						    oututilhdxlist.add(n, hdata);
//						    hdata = hosdata;
//						    hosdata = null;
//						}
//				    }
//				    // 得到排序后的Subentity的列表
//				    networkOutList.add(hdata);
//				    if(networkOutList.size()>=size-1){
//				    	break;
//				    }
//				    hdata = null;
//				}
//		    }
//		}
//		return networkOutList;
//    }
    /**
     * 网络设备入口流速当天平均值(flex调用)
     */
//    public List getNetworkInList(int size,String bids) {
//    	List networklist = getNodeList(1,bids);
//		List networkInList = new ArrayList();
//		List inutilhdxlist = new ArrayList();
//		if (networklist != null && networklist.size() > 0) {
//		    for (int i = 0; i < networklist.size(); i++) {
//				HostNode node = (HostNode) networklist.get(i);
//				I_HostCollectData hostManager = new HostCollectDataManager();
//				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
//				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//				Hashtable inutilHash = new Hashtable();
//				try {
//				    inutilHash = hostManager.getAllutilhdx(node.getIpAddress(),"AllInBandwidthUtilHdx", startTime, endTime, "");
//				} catch (Exception e) {
//				    e.printStackTrace();
//				}
//				if (inutilHash.get("avgput") != null) {
//				    String avgutilcon = (String) inutilHash.get("avgput");
//				    Avgcollectdata avgcollectdata = new Avgcollectdata();
//				    avgcollectdata.setIpaddress(node.getIpAddress());
//				    avgcollectdata.setThevalue(avgutilcon);
//				    inutilhdxlist.add(avgcollectdata);
//				}
//		    }
//		    if (inutilhdxlist != null && inutilhdxlist.size() > 0) {
//				for (int m = 0; m < inutilhdxlist.size(); m++) {
//				    Avgcollectdata hdata = (Avgcollectdata) inutilhdxlist.get(m);
//				    for (int n = m + 1; n < inutilhdxlist.size(); n++) {
//						Avgcollectdata hosdata = (Avgcollectdata) inutilhdxlist.get(n);
//						if (new Double(hdata.getThevalue()).doubleValue() < new Double(
//							hosdata.getThevalue()).doubleValue()) {
//						    inutilhdxlist.remove(m);
//						    inutilhdxlist.add(m, hosdata);
//						    inutilhdxlist.remove(n);
//						    inutilhdxlist.add(n, hdata);
//						    hdata = hosdata;
//						    hosdata = null;
//						}
//				    }
//				    // 得到排序后的Subentity的列表
//				    networkInList.add(hdata);
//				    if(networkInList.size()>=size-1){
//				    	break;
//				    }
//				    hdata = null;
//				}
//		    }
//		}
//		return networkInList;
//    }
    //网络设备入口流速topn实时
//    public List getNetworkTempInList(int size,String bids){
//    	List networklist = getNodeList(1,bids);
//    	List inutilhdxlist = new ArrayList();
//    	List allinutillist = new ArrayList();
//    	if (networklist != null && networklist.size() > 0) {
//    		for (int i = 0; i < networklist.size(); i++) {
//    		    HostNode node = (HostNode) networklist.get(i);
//    		    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
//    		    if (ipAllData != null) {
//    		    	Vector allutil = (Vector) ipAllData.get("allutilhdx");
//    				if (allutil != null && allutil.size() == 3) {
//    				    Avgcollectdata avgcollectdatai = new Avgcollectdata();
//    				    AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
//    				    avgcollectdatai.setIpaddress(node.getIpAddress());
//    				    avgcollectdatai.setThevalue(inutilhdx.getThevalue());
//    				    inutilhdxlist.add(avgcollectdatai);
//    				}
//    		    }
//    		}
//    		if (inutilhdxlist != null && inutilhdxlist.size() > 0) {
//    			for (int m = 0; m < inutilhdxlist.size(); m++) {
//    			    Avgcollectdata hdata = (Avgcollectdata) inutilhdxlist.get(m);
//    			    for (int n = m + 1; n < inutilhdxlist.size(); n++) {
//	    				Avgcollectdata hosdata = (Avgcollectdata) inutilhdxlist.get(n);
//	    				if (new Double(hdata.getThevalue()).doubleValue() < new Double(
//	    					hosdata.getThevalue()).doubleValue()) {
//	    				    inutilhdxlist.remove(m);
//	    				    inutilhdxlist.add(m, hosdata);
//	    				    inutilhdxlist.remove(n);
//	    				    inutilhdxlist.add(n, hdata);
//	    				    hdata = hosdata;
//	    				    hosdata = null;
//	    				}
//    			    }
//    			    // 得到排序后的Subentity的列表
//    			    allinutillist.add(hdata);
//    			    if(allinutillist.size()>=size-1){
//				    	break;
//				    }
//    			    hdata = null;
//    			}
//    		}
//    	}
//		return allinutillist;
//    }
    //单个设备当天入口流速信息
//    public List<Vos> getInValue(String ipAddress) {
//		List<Vos> flexDataList = new ArrayList<Vos>();
//		I_HostCollectData hostManager = new HostCollectDataManager();
//		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
//		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//		// 从collectdata取内存的历史数据,存放在表中
//		Hashtable utilHash = null;
//		try {
//		    utilHash = hostManager.getAllutilhdx(ipAddress,"AllInBandwidthUtilHdx", startTime, endTime, "");
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		if (utilHash != null) {
//		    List utilHdxList = new ArrayList();
//		    utilHdxList = (List) utilHash.get("list");
//		    if (utilHdxList != null && utilHdxList.size() > 0) {
//				for (int i = 0; i < utilHdxList.size(); i++) {
//				    Vos fVo = new Vos();
//				    Vector utilHdxVector = new Vector();
//				    utilHdxVector = (Vector) utilHdxList.get(i);
//				    if (utilHdxVector != null) {
//					fVo.setObjectNumber((String) utilHdxVector.get(0));
//					fVo.setObjectName2((String) utilHdxVector.get(1));
//					fVo.setObjectName1(ipAddress);
//					flexDataList.add(fVo);
//				    }
//				}
//		    }
//		}
//		return flexDataList;
//    }
    //单个设备当天出口流速
//    public List<Vos> getOutValue(String ipAddress) {
//    	List<Vos> flexDataList = new ArrayList<Vos>();
//    	I_HostCollectData hostManager = new HostCollectDataManager();
//    	String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
//    	String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//    	// 从collectdata取内存的历史数据,存放在表中
//    	Hashtable utilHash = null;
//    	try {
//    	    utilHash = hostManager.getAllutilhdx(ipAddress,"AllOutBandwidthUtilHdx", startTime, endTime, "");
//    	} catch (Exception e) {
//    	    e.printStackTrace();
//    	}
//    	if (utilHash != null) {
//    	    List utilHdxList = new ArrayList();
//    	    utilHdxList = (List) utilHash.get("list");
//    	    if (utilHdxList != null && utilHdxList.size() > 0) {
//	    		for (int i = 0; i < utilHdxList.size(); i++) {
//	    		    Vos fVo = new Vos();
//	    		    Vector utilHdxVector = new Vector();
//	    		    utilHdxVector = (Vector) utilHdxList.get(i);
//	    		    if (utilHdxVector != null) {
//		    			fVo.setObjectNumber((String) utilHdxVector.get(0));
//		    			fVo.setObjectName2((String) utilHdxVector.get(1));
//		    			fVo.setObjectName1(ipAddress);
//		    			flexDataList.add(fVo);
//	    		    }
//	    		}
//    	    }
//    	}
//    	return flexDataList;
//    }
    /**
     * 服务器磁盘利用率(flex调用)
     */
    public List<FlexVo> getHostDiskList(String bids,int size) {
		List hostlist = getNodeList(4,bids);
    	List hostdisklist = new ArrayList();
    	List allhostdisklist = new ArrayList();
    	if (hostlist != null && hostlist.size() > 0) {
    		for (int i = 0; i < hostlist.size(); i++) {
    		    HostNode node = (HostNode) hostlist.get(i);
    		    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
    		    if (ipAllData != null) {
    		    	Vector diskVector = null;
    				try {
    				    diskVector = (Vector) ipAllData.get("disk");
    				} catch (RuntimeException e) {
    				    e.printStackTrace();
    				}
    				if (diskVector != null && diskVector.size() > 0) {
    				    Hashtable hostdata = ShareData.getHostdata();
    				    for (int si = 0; si < diskVector.size(); si++) {
    						Diskcollectdata diskdata = (Diskcollectdata) diskVector.elementAt(si);
    						if (diskdata.getEntity().equalsIgnoreCase(
    							"Utilization")) {
    						    // 利用率
    						    if (node.getOstype() == 4
    							    || node.getSysOid().startsWith(
    								    "1.3.6.1.4.1.311.1.1.3")) {
    							diskdata
    								.setSubentity(diskdata
    									.getSubentity()
    									.substring(0, 3));
    						    }
    						    hostdisklist.add(diskdata);
    						}
    				    }
    		        }
    		    }
    		}
    		if (hostdisklist != null && hostdisklist.size() > 0) {
        		for (int m = 0; m < hostdisklist.size(); m++) {
        		    Diskcollectdata hdata = (Diskcollectdata) hostdisklist
        			    .get(m);
        		    for (int n = m + 1; n < hostdisklist.size(); n++) {
        			Diskcollectdata hosdata = (Diskcollectdata) hostdisklist
        				.get(n);
        			if (new Double(hdata.getThevalue()).doubleValue() < new Double(
        				hosdata.getThevalue()).doubleValue()) {
        			    hostdisklist.remove(m);
        			    hostdisklist.add(m, hosdata);
        			    hostdisklist.remove(n);
        			    hostdisklist.add(n, hdata);
        			    hdata = hosdata;
        			    hosdata = null;
        			}
        		    }
        		    // 得到排序后的Subentity的列表
        		    allhostdisklist.add(hdata);
        		    if(allhostdisklist.size()>=size-1){
    			    	break;
    			    }
        		    hdata = null;
        		}
        	}
    	}
    	List<FlexVo> flexDataList = new ArrayList<FlexVo>();
		FlexVo fVo;
		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i < allhostdisklist.size(); i++) {
		    Diskcollectdata diskcollectdata = (Diskcollectdata) allhostdisklist.get(i);
		    fVo = new FlexVo();
		    fVo.setObjectName(diskcollectdata.getIpaddress() + " " + diskcollectdata.getSubentity());
		    fVo.setObjectNumber(df.format(Double.parseDouble(diskcollectdata.getThevalue())));
		    flexDataList.add(fVo);
		}
		return flexDataList;
    }
//    public List<Avgcollectdata> getHostTempCPUList() {
//    	User vo = (User) flexSession.getAttribute(SessionConstant.CURRENT_USER);
//		String bids = vo.getBusinessids();
//		if (vo.getRole() == 0 || vo.getRole() == 1) {
//		    bids = "-1";
//		}
//		HostNodeDao nodedao = new HostNodeDao();
//		List hostlist = new ArrayList();
//		try {
//		    hostlist = nodedao.loadNetworkByBid(4, bids);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		} finally {
//		    nodedao.close();
//		}
//    	List allhostcpulist = new ArrayList();
//    	List hostcpulist = new ArrayList();
//    	if (hostlist != null && hostlist.size() > 0) {
//    		for (int i = 0; i < hostlist.size(); i++) {
//    		    HostNode node = (HostNode) hostlist.get(i);
//    		    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
//    		    if (ipAllData != null) {
//    		    	Vector cpuV = (Vector) ipAllData.get("cpu");
//    				if (cpuV != null && cpuV.size() > 0) {
//    				    CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
//    				    Avgcollectdata avgcollectdata = new Avgcollectdata();
//    				    avgcollectdata.setIpaddress(node.getIpAddress());
//    				    avgcollectdata.setThevalue(cpu.getThevalue()
//    					    .replace("%", ""));
//    				    hostcpulist.add(avgcollectdata);
//    				}
//    		    }
//    		}
//    		if (hostcpulist != null && hostcpulist.size() > 0) {
//        		for (int m = 0; m < hostcpulist.size(); m++) {
//        		    Avgcollectdata hdata = (Avgcollectdata) hostcpulist.get(m);
//        		    for (int n = m + 1; n < hostcpulist.size(); n++) {
//        			Avgcollectdata hosdata = (Avgcollectdata) hostcpulist
//        				.get(n);
//        			if (new Double(hdata.getThevalue().replace("%", ""))
//        				.doubleValue() < new Double(hosdata
//        				.getThevalue().replace("%", "")).doubleValue()) {
//        			    hostcpulist.remove(m);
//        			    hostcpulist.add(m, hosdata);
//        			    hostcpulist.remove(n);
//        			    hostcpulist.add(n, hdata);
//        			    hdata = hosdata;
//        			    hosdata = null;
//        			}
//        		    }
//        		    // 得到排序后的Subentity的列表
//        		    allhostcpulist.add(hdata);
//        		    hdata = null;
//        		}
//        	}
//        }
//		return allhostcpulist;
//    }
	 /**
     * 服务器CPU利用率(flex调用)
     */
//    public List<Avgcollectdata> getHostCPUList() {
//		User vo = (User) flexSession.getAttribute(SessionConstant.CURRENT_USER);
//		String bids = vo.getBusinessids();
//		if (vo.getRole() == 0 || vo.getRole() == 1) {
//		    bids = "-1";
//		}
//		HostNodeDao nodedao = new HostNodeDao();
//		List hostlist = new ArrayList();
//		try {
//		    hostlist = nodedao.loadNetworkByBid(4, bids);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		} finally {
//		    nodedao.close();
//		}
//		List hostCPUList = new ArrayList();
//		List hostcpulist = new ArrayList();
//		if (hostlist != null && hostlist.size() > 0) {
//		    for (int i = 0; i < hostlist.size(); i++) {
//			HostNode node = (HostNode) hostlist.get(i);
//			I_HostCollectData hostManager = new HostCollectDataManager();
//			String startTime = new SimpleDateFormat("yyyy-MM-dd")
//				.format(new Date())
//				+ " 00:00:00";
//			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				.format(new Date());
//			// 从collectdata取cpu的历史数据,存放在表中
//			Hashtable cpuHash = new Hashtable();
//			try {
//			    cpuHash = hostManager.getCategory(node.getIpAddress(),
//				    "CPU", "Utilization", startTime, endTime);
//			} catch (Exception e) {
//			    e.printStackTrace();
//			}
//			if (cpuHash.get("avgcpucon") != null) {
//			    String avgcpucon = (String) cpuHash.get("avgcpucon");
//			    Avgcollectdata avgcollectdata = new Avgcollectdata();
//			    avgcollectdata.setIpaddress(node.getIpAddress());
//			    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
//			    hostcpulist.add(avgcollectdata);
//			}
//		    }
//		    if (hostcpulist != null && hostcpulist.size() > 0) {
//			for (int m = 0; m < hostcpulist.size(); m++) {
//			    Avgcollectdata hdata = (Avgcollectdata) hostcpulist.get(m);
//			    for (int n = m + 1; n < hostcpulist.size(); n++) {
//				Avgcollectdata hosdata = (Avgcollectdata) hostcpulist
//					.get(n);
//				if (new Double(hdata.getThevalue().replace("%", ""))
//					.doubleValue() < new Double(hosdata
//					.getThevalue().replace("%", "")).doubleValue()) {
//				    hostcpulist.remove(m);
//				    hostcpulist.add(m, hosdata);
//				    hostcpulist.remove(n);
//				    hostcpulist.add(n, hdata);
//				    hdata = hosdata;
//				    hosdata = null;
//				}
//			    }
//			    // 得到排序后的Subentity的列表
//			    hostCPUList.add(hdata);
//			    hdata = null;
//			}
//		    }
//		}
//		return hostCPUList;
//    }
	 /**
     * 获取相应ip地址的内存在当天各个时间段的值(flex调用)
     */
//    public ArrayList<Vos> getMemoryValue(String ipAddress) {
//		I_HostCollectData hostManager = new HostCollectDataManager();
//		String startTime = new SimpleDateFormat("yyyy-MM-dd")
//			.format(new Date())
//			+ " 00:00:00";
//		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//			.format(new Date());
//		// 从collectdata取内存的历史数据,存放在表中
//		Hashtable[] memoryHash = null;
//		DecimalFormat df = new DecimalFormat("#.##");
//		try {
//		    memoryHash = hostManager.getMemory(ipAddress, "Memory", startTime,
//			    endTime);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		if (memoryHash != null && memoryHash.length > 0) {
//		    Hashtable physicalMemoryHash = new Hashtable();
//		    physicalMemoryHash = (Hashtable) memoryHash[0];
//		    Vector physicalMemoryVector = new Vector();
//		    if (physicalMemoryHash.get("PhysicalMemory") != null) {
//			ArrayList<Vos> flexDataList = new ArrayList<Vos>();
//			physicalMemoryVector = (Vector) physicalMemoryHash
//				.get("PhysicalMemory");
//			Vos fVo;
//			for (int i = 0; i < physicalMemoryVector.size(); i++) {
//			    Vector memoryVector = new Vector();
//			    fVo = new Vos();
//			    memoryVector = (Vector) physicalMemoryVector.get(i);
//			    if (memoryVector != null || memoryVector.size() > 0) {
//				fVo.setObjectNumber(df.format(Double
//					.parseDouble((String) memoryVector.get(0))));
//				fVo.setObjectName2((String) memoryVector.get(1));
//				fVo.setObjectName1(ipAddress);
//				flexDataList.add(fVo);
//			    }
//			}
//			return flexDataList;
//		    }
//		}
//		return null;
//    }
//	public List<Memorycollectdata> getHostTempMemoryList() {
//		flexSession = FlexContext.getFlexSession();
//		User vo = (User) flexSession.getAttribute(SessionConstant.CURRENT_USER);
//		String bids = vo.getBusinessids();
//		if (vo.getRole() == 0 || vo.getRole() == 1) {
//		    bids = "-1";
//		}
//		List hostlist = new ArrayList();
//		HostNodeDao nodedao = new HostNodeDao();
//		try {
//		    hostlist = nodedao.loadNetworkByBid(4, bids);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		} finally {
//		    nodedao.close();
//		}
//		List allhostmemorylist = new ArrayList();
//		List hostmemorylist = new ArrayList();
//		DecimalFormat df = new DecimalFormat("#.##");
//		if (hostlist != null && hostlist.size() > 0) {
//			for (int i = 0; i < hostlist.size(); i++) {
//				HostNode node = (HostNode) hostlist.get(i);
//			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
//				Vector memoryVector = (Vector) ipAllData.get("memory");
//				if (memoryVector != null && memoryVector.size() > 0) {
//				    for (int si = 0; si < memoryVector.size(); si++) {
//						Memorycollectdata memorydata = (Memorycollectdata) memoryVector.elementAt(si);
//						if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
//						    // 利用率
//						    if (memorydata.getSubentity()
//							    .equalsIgnoreCase("PhysicalMemory")) {
//							Avgcollectdata avgcollectdata = new Avgcollectdata();
//							avgcollectdata.setIpaddress(node
//								.getIpAddress());
//							avgcollectdata.setThevalue(df
//								.format(Double
//									.parseDouble(memorydata
//										.getThevalue()
//										.replace("%",
//											""))));
//							hostmemorylist.add(avgcollectdata);
//						    }
//						}
//				    }
//				}
//				if (hostmemorylist != null && hostmemorylist.size() > 0) {
//					for (int m = 0; m < hostmemorylist.size(); m++) {
//					    Avgcollectdata hdata = (Avgcollectdata) hostmemorylist
//						    .get(m);
//					    for (int n = m + 1; n < hostmemorylist.size(); n++) {
//						Avgcollectdata hosdata = (Avgcollectdata) hostmemorylist
//							.get(n);
//						if (new Double(hdata.getThevalue().replace("%", ""))
//							.doubleValue() < new Double(hosdata
//							.getThevalue().replace("%", "")).doubleValue()) {
//						    hostmemorylist.remove(m);
//						    hostmemorylist.add(m, hosdata);
//						    hostmemorylist.remove(n);
//						    hostmemorylist.add(n, hdata);
//						    hdata = hosdata;
//						    hosdata = null;
//						}
//					    }
//					    // 得到排序后的Subentity的列表
//					    allhostmemorylist.add(hdata);
//					    hdata = null;
//					}
//				}
//			}
//	    }
//		return allhostmemorylist;
//	}
	/**
     * 服务器内存利用率(flex调用)
     */
//    public List<Memorycollectdata> getHostMemoryList() {
//    	flexSession = FlexContext.getFlexSession();
//		User vo = (User) flexSession.getAttribute(SessionConstant.CURRENT_USER);
//		String bids = vo.getBusinessids();
//		if (vo.getRole() == 0 || vo.getRole() == 1) {
//		    bids = "-1";
//		}
//		List hostlist = new ArrayList();
//		HostNodeDao nodedao = new HostNodeDao();
//		try {
//		    hostlist = nodedao.loadNetworkByBid(4, bids);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		} finally {
//		    nodedao.close();
//		}
//		List hostmemorylist = new ArrayList();
//		List hostMemoryList = new ArrayList();
//		if (hostlist != null && hostlist.size() > 0) {
//		    for (int i = 0; i < hostlist.size(); i++) {
//			HostNode node = (HostNode) hostlist.get(i);
//			I_HostCollectData hostManager = new HostCollectDataManager();
//			String startTime = new SimpleDateFormat("yyyy-MM-dd")
//				.format(new Date())
//				+ " 00:00:00";
//			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				.format(new Date());
//			// 从collectdata取内存的历史数据,存放在表中
//			Hashtable[] memoryHash = null;
//			try {
//			    memoryHash = hostManager.getMemory(node.getIpAddress(),
//				    "Memory", startTime, endTime);
//			} catch (Exception e) {
//			    e.printStackTrace();
//			}
//			if (memoryHash != null && memoryHash.length > 0) {
//			    Hashtable physicalMemoryHash = new Hashtable();
//			    physicalMemoryHash = (Hashtable) memoryHash[2];
//			    String avgmemory = (String) physicalMemoryHash
//				    .get("PhysicalMemory");
//			    if (avgmemory != null) {
//				Avgcollectdata avgcollectdata = new Avgcollectdata();
//				avgcollectdata.setIpaddress(node.getIpAddress());
//				avgcollectdata.setThevalue(avgmemory.replace("%", ""));
//				hostmemorylist.add(avgcollectdata);
//			    }
//			}
//		    }
//		    if (hostmemorylist != null && hostmemorylist.size() > 0) {
//			for (int m = 0; m < hostmemorylist.size(); m++) {
//			    Avgcollectdata hdata = (Avgcollectdata) hostmemorylist
//				    .get(m);
//			    for (int n = m + 1; n < hostmemorylist.size(); n++) {
//				Avgcollectdata hosdata = (Avgcollectdata) hostmemorylist
//					.get(n);
//				if (new Double(hdata.getThevalue().replace("%", ""))
//					.doubleValue() < new Double(hosdata
//					.getThevalue().replace("%", "")).doubleValue()) {
//				    hostmemorylist.remove(m);
//				    hostmemorylist.add(m, hosdata);
//				    hostmemorylist.remove(n);
//				    hostmemorylist.add(n, hdata);
//				    hdata = hosdata;
//				    hosdata = null;
//				}
//			    }
//			    // 得到排序后的Subentity的列表
//			    hostMemoryList.add(hdata);
//			    hdata = null;
//			}
//		    }
//		}
//		return hostMemoryList;
//    }
//  网络设备实时数据topn
	public List getNetworkTempList(int category,int size,String type,String bids) {
		List networkList = new ArrayList();
		List list = new ArrayList();
		List networklist = getNodeList(category,bids);
//		System.out.println("networklist.size()=="+networklist.size());
		String runmodel = PollingEngine.getCollectwebflag(); 
		Hashtable allpingdata = ShareData.getPingdata();
		if (networklist != null && networklist.size() > 0) {
			if("1".equals(runmodel)){
		       	//采集与访问是分离模式
				PingInfoService pingInfoService = new PingInfoService();
				CpuInfoService cpuInfoService = new CpuInfoService();
				MemoryInfoService memoryInfoService = new MemoryInfoService();
				InterfaceInfoService interfaceInfoService = new InterfaceInfoService();
				if("ping".equalsIgnoreCase(type)){
					List<NodeTemp> pingInfoList = pingInfoService.getPingInfo(networklist);
					if(pingInfoList!=null&&pingInfoList.size()>0){
						for(int j=0; j<pingInfoList.size(); j++){
							NodeTemp nodeTemp = pingInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
						    avgcollectdata.setIpaddress(nodeTemp.getIp());
						    avgcollectdata.setThevalue(nodeTemp.getThevalue());
						    avgcollectdata.setBak("连通率");
						    avgcollectdata.setUnit("%");
						    list.add(avgcollectdata);
						}
					}
				}else if("ResponseTime".equalsIgnoreCase(type)){
					List<NodeTemp> pingInfoList = pingInfoService.getResponseInfo(networklist);
					if(pingInfoList!=null&&pingInfoList.size()>0){
						for(int j=0; j<pingInfoList.size(); j++){
							NodeTemp nodeTemp = pingInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
						    avgcollectdata.setIpaddress(nodeTemp.getIp());
						    avgcollectdata.setThevalue(nodeTemp.getThevalue());
						    avgcollectdata.setBak("响应时间");
						    avgcollectdata.setUnit("ms");
						    list.add(avgcollectdata);
						}
					}
				}else if("cpu".equalsIgnoreCase(type)){
					List<NodeTemp> cpuInfoList = cpuInfoService.getCpuPerListInfo(networklist);
					if(cpuInfoList!=null&&cpuInfoList.size()>0){
						for(int j=0; j<cpuInfoList.size(); j++){
							NodeTemp nodeTemp = cpuInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
						    avgcollectdata.setIpaddress(nodeTemp.getIp());
						    avgcollectdata.setThevalue(nodeTemp.getThevalue());
						    avgcollectdata.setBak("CPU利用率");
						    avgcollectdata.setUnit("%");
						    list.add(avgcollectdata);
						}
					}
				}else if("memory".equalsIgnoreCase(type)){
					List<NodeTemp> memoryInfoList = memoryInfoService.getMemoryInfo(networklist);
					if(memoryInfoList!=null&&memoryInfoList.size()>0){
						for(int j=0; j<memoryInfoList.size(); j++){
							NodeTemp nodeTemp = memoryInfoList.get(j);
							Avgcollectdata avgcollectdata = new Avgcollectdata();
						    avgcollectdata.setIpaddress(nodeTemp.getIp());
						    avgcollectdata.setThevalue(nodeTemp.getThevalue());
						    avgcollectdata.setBak("内存利用率");
						    avgcollectdata.setUnit("%");
						    list.add(avgcollectdata);
						}
					}
				}else if("inutil".equalsIgnoreCase(type)){
					List<NodeTemp> inutilInfoList = interfaceInfoService.getInterfaceInfo(networklist);
					if(inutilInfoList!=null&&inutilInfoList.size()>0){
						for(int j=0; j<inutilInfoList.size(); j++){
							NodeTemp nodeTemp = inutilInfoList.get(j);
							if(nodeTemp.getSubentity() != null && nodeTemp.getSubentity().equalsIgnoreCase("AllInBandwidthUtilHdx")){
								Avgcollectdata avgcollectdata = new Avgcollectdata();
							    avgcollectdata.setIpaddress(nodeTemp.getIp());
							    avgcollectdata.setThevalue(nodeTemp.getThevalue());
							    avgcollectdata.setBak("入口流速");
							    avgcollectdata.setUnit("KB/s");
							    list.add(avgcollectdata);
							}
						}
					}
				}else if("oututil".equalsIgnoreCase(type)){
					List<NodeTemp> oututilInfoList = interfaceInfoService.getInterfaceInfo(networklist);
					if(oututilInfoList!=null&&oututilInfoList.size()>0){
						for(int j=0; j<oututilInfoList.size(); j++){
							NodeTemp nodeTemp = oututilInfoList.get(j);
							if(nodeTemp.getSubentity() != null && nodeTemp.getSubentity().equalsIgnoreCase("AllOutBandwidthUtilHdx")){
								Avgcollectdata avgcollectdata = new Avgcollectdata();
							    avgcollectdata.setIpaddress(nodeTemp.getIp());
							    avgcollectdata.setThevalue(nodeTemp.getThevalue());
							    avgcollectdata.setBak("出口流速");
							    avgcollectdata.setUnit("Kb/s");
							    list.add(avgcollectdata);
							}
						}
					}
				}
	    	} else {
	    		if("ping".equalsIgnoreCase(type)){
	    			if(allpingdata!= null){
		    			for (int i = 0; i < networklist.size(); i++) {
		    		    	HostNode node = (HostNode) networklist.get(i);
		    		    	String pingValue = "0";					// ping 默认为 0
		    		    	Vector pingData = (Vector)allpingdata.get(node.getIpAddress());
							if(pingData != null && pingData.size() > 0 ){
								Pingcollectdata pingcollectdata = (Pingcollectdata) pingData.get(0);
								pingValue = pingcollectdata.getThevalue();
								String unit = pingcollectdata.getUnit();
								Avgcollectdata avgcollectdata = new Avgcollectdata();
							    avgcollectdata.setIpaddress(node.getIpAddress());
							    avgcollectdata.setThevalue(pingValue);
							    avgcollectdata.setBak("连通率");
							    avgcollectdata.setUnit("%");
							    list.add(avgcollectdata);
							}
		    			}
					}
	    		}else if("ResponseTime".equalsIgnoreCase(type)){
	    			if(allpingdata!= null){
		    			for (int i = 0; i < networklist.size(); i++) {
		    		    	HostNode node = (HostNode) networklist.get(i);
		    		    	String pingValue = "0";					// ping 默认为 0
		    		    	Vector pingData = (Vector)allpingdata.get(node.getIpAddress());
							if(pingData != null && pingData.size() > 0 ){
								Pingcollectdata pingcollectdata = (Pingcollectdata) pingData.get(1);
								String unit = pingcollectdata.getUnit();
								pingValue = pingcollectdata.getThevalue();
								Avgcollectdata avgcollectdata = new Avgcollectdata();
							    avgcollectdata.setIpaddress(node.getIpAddress());
							    avgcollectdata.setThevalue(pingValue);
							    avgcollectdata.setBak("响应时间");
							    avgcollectdata.setUnit("ms");
							    list.add(avgcollectdata);
							}
		    			}
					}
	    		}else if("cpu".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	Vector cpuV = (Vector) ipAllData.get("cpu");
	    					if (cpuV != null && cpuV.size() > 0) {
	    					    CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
	    					    Avgcollectdata avgcollectdata = new Avgcollectdata();
	    					    avgcollectdata.setIpaddress(node.getIpAddress());
	    					    String cpuValue = cpu.getThevalue();
	    					    if (cpuValue == null) {
	    						    cpuValue = "0";
	    					    }
	    					    cpuValue = cpuValue.replace("%", "");
	    					    avgcollectdata.setThevalue(cpuValue);
							    avgcollectdata.setBak("CPU利用率");
							    avgcollectdata.setUnit("%");
							    list.add(avgcollectdata);
	    					}
	    			    }
	    			}
	    		}else if("memory".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	if(category == 1){
	    			    		Vector memoryV = (Vector) ipAllData.get("memory");
		    			    	Memorycollectdata memory = getMaxUtilization(memoryV);
		    			    	Avgcollectdata avgcollectdata = new Avgcollectdata();
	    					    avgcollectdata.setIpaddress(node.getIpAddress());
	    					    String memoryValue = "0.0";
	    					    if(memory!=null){
	    					    	memoryValue = memory.getThevalue();
	    					    }
	    					    memoryValue = memoryValue.replace("%", "");
	    					    avgcollectdata.setThevalue(memoryValue);
							    avgcollectdata.setBak("内存利用率");
							    avgcollectdata.setUnit("%");
							    list.add(avgcollectdata);
	    			    	} else if(category == 4){
	    			    		DecimalFormat df = new DecimalFormat("#.##");
	    			    		Vector memoryVector = (Vector) ipAllData.get("memory");
	    			    		if (memoryVector != null && memoryVector.size() > 0) {
	    	    				    for (int si = 0; si < memoryVector.size(); si++) {
	    	    						Memorycollectdata memorydata = (Memorycollectdata) memoryVector.elementAt(si);
	    	    						if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
	    	    						    // 利用率
	    	    						    if (memorydata.getSubentity().equalsIgnoreCase("PhysicalMemory")) {
		    	    							Avgcollectdata avgcollectdata = new Avgcollectdata();
		    	    							avgcollectdata.setIpaddress(node.getIpAddress());
		    	    							avgcollectdata.setThevalue(df.format(Double.parseDouble(memorydata.getThevalue().replace("%",""))));
		    	    							avgcollectdata.setBak("物理内存利用率");
		    								    avgcollectdata.setUnit("%");
		    								    list.add(avgcollectdata);
	    	    						    }
	    	    						}
	    	    				    }
	    	    				}
	    			    	}
	    			    }
	    			}
	    		}else if("inutil".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	Vector allutil = (Vector) ipAllData.get("allutilhdx");
	        				if (allutil != null && allutil.size() == 3) {
	        				    Avgcollectdata avgcollectdatai = new Avgcollectdata();
	        				    AllUtilHdx inutilhdx = (AllUtilHdx) allutil.get(0);
	        				    avgcollectdatai.setIpaddress(node.getIpAddress());
	        				    avgcollectdatai.setThevalue(inutilhdx.getThevalue());
	        				    avgcollectdatai.setBak("入口流速");
	        				    avgcollectdatai.setUnit("Kb/s");
							    list.add(avgcollectdatai);
	        				}
	    			    }
	    			}
	    		}else if("oututil".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	Vector allutil = (Vector) ipAllData.get("allutilhdx");
	        				if (allutil != null && allutil.size() == 3) {
	        				    Avgcollectdata avgcollectdatao = new Avgcollectdata();
	        				    AllUtilHdx oututilhdx = (AllUtilHdx) allutil.get(1);
	        				    avgcollectdatao.setIpaddress(node.getIpAddress());
	        				    avgcollectdatao.setThevalue(oututilhdx.getThevalue());
	        				    avgcollectdatao.setBak("出口流速");
	        				    avgcollectdatao.setUnit("Kb/s");
							    list.add(avgcollectdatao);
	        				}
	    			    }
	    			}
	    		}else if("inutilper".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
	        				if (utilhdxperc != null && utilhdxperc.size()>0) {
	        					for(int j = 0; j < utilhdxperc.size(); j++){
	        						UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
	        						if("InBandwidthUtilHdxPerc".equalsIgnoreCase(inutilhdx.getEntity())){
	        							Avgcollectdata avgcollectdatao = new Avgcollectdata();
	    	        				    avgcollectdatao.setIpaddress(node.getIpAddress()+"-"+inutilhdx.getSubentity());
	    	        				    avgcollectdatao.setThevalue(inutilhdx.getThevalue());
	    	        				    avgcollectdatao.setBak("入口带宽利用率");
	    	        				    avgcollectdatao.setUnit("Kb/s");
	    							    list.add(avgcollectdatao);
	        						}
	        					}
	        				}
	    			    }
	    			}
	    		}else if("oututilper".equalsIgnoreCase(type)){
	    			for (int i = 0; i < networklist.size(); i++) {
	    				HostNode node = (HostNode) networklist.get(i);
	    			    Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
	    			    if (ipAllData != null) {
	    			    	Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
	        				if (utilhdxperc != null && utilhdxperc.size() > 0) {
	        					for(int j = 0; j < utilhdxperc.size(); j++){
	        						UtilHdxPerc oututilhdx = (UtilHdxPerc) utilhdxperc.get(j);
	        						if("OutBandwidthUtilHdxPerc".equalsIgnoreCase(oututilhdx.getEntity())){
	        							Avgcollectdata avgcollectdatao = new Avgcollectdata();
			        				    avgcollectdatao.setIpaddress(node.getIpAddress()+"-"+oututilhdx.getSubentity());
			        				    avgcollectdatao.setThevalue(oututilhdx.getThevalue());
			        				    avgcollectdatao.setBak("出口带宽利用率");
			        				    avgcollectdatao.setUnit("Kb/s");
									    list.add(avgcollectdatao);
	        						}
	        					}
	        				}
	    			    }
	    			}
	    		}
	    	}
		}
		if (list != null && list.size() > 0) {
			for (int m = 0; m < list.size(); m++) {
			    Avgcollectdata hdata = (Avgcollectdata) list.get(m);
			    for (int n = m + 1; n < list.size(); n++) {
					Avgcollectdata hosdata = (Avgcollectdata) list.get(n);
					if (new Double(hdata.getThevalue().replace("%", ""))
						.doubleValue() < new Double(hosdata
						.getThevalue().replace("%", "")).doubleValue()) {
						list.remove(m);
						list.add(m, hosdata);
						list.remove(n);
						list.add(n, hdata);
					    hdata = hosdata;
					    hosdata = null;
					}
			    }
			    networkList.add(hdata);
			    if(networkList.size()>=size){
			    	break;
			    }
			    hdata = null;
			}
		}
		return networkList;
    }
    //计算网络设备内存利用率最大的内存块
	private Memorycollectdata getMaxUtilization(Vector memoryV){
		Memorycollectdata memory = null;
		Vector networkVector = new Vector();
		if (memoryV != null && memoryV.size() > 0) {
			for (int m = 0; m < memoryV.size(); m++) {
				Memorycollectdata hdata = (Memorycollectdata) memoryV.get(m);
			    for (int n = m + 1; n < memoryV.size(); n++) {
			    	Memorycollectdata hosdata = (Memorycollectdata) memoryV.get(n);
					if (new Double(hdata.getThevalue().replace("%", ""))
						.doubleValue() < new Double(hosdata
						.getThevalue().replace("%", "")).doubleValue()) {
						memoryV.remove(m);
						memoryV.add(m, hosdata);
						memoryV.remove(n);
						memoryV.add(n, hdata);
					    hdata = hosdata;
					    hosdata = null;
					}
			    }
			    networkVector.add(hdata);
			    hdata = null;
			}
			memory = (Memorycollectdata)networkVector.get(0);
		}
		return memory;
		
	}
	/**
     * 网络设备历史数据topn
     */
    public List getNetworkList(int category,int size,String b_time,String t_time,String type,String bids,String time) {
		List networklist = getNodeList(category,bids);
		List networkList = new ArrayList();
		List list = new ArrayList();
		if (networklist != null && networklist.size() > 0) {
		    for (int i = 0; i < networklist.size(); i++) {
				HostNode node = (HostNode) networklist.get(i);
				I_HostCollectData hostManager = new HostCollectDataManager();
				String startTime = b_time+ " 00:00:00";
				String endTime = t_time+ " 23:59:59";
				// 从collectdata取cpu的历史数据,存放在表中
				Hashtable networkHash = new Hashtable();
				String bak = "";
				String unit = "";
				if("ping".equalsIgnoreCase(type)){
					bak = "连通率";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization",startTime, endTime);
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}else if("ResponseTime".equalsIgnoreCase(type)){
					bak = "响应时间";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(), "Ping", "ResponseTime",startTime, endTime);
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}else if("cpu".equalsIgnoreCase(type)){
					bak = "CPU利用率";
					try {
						networkHash = hostManager.getCategory(node.getIpAddress(),"CPU", "Utilization", startTime, endTime);
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}else if("memory".equalsIgnoreCase(type)){
					bak = "内存利用率";
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
    			    if (ipAllData != null) {
    			    	Vector memoryV = (Vector) ipAllData.get("memory");
    			    	if(category == 1 && memoryV!=null){
    			    		Memorycollectdata memory = getMaxUtilization(memoryV);
        			    	try {
        			    		if(memory!=null){
        			    			networkHash = hostManager.getCategory(node.getIpAddress(),"Memory", memory.getSubentity(), startTime, endTime);
        			    		}
        					} catch (Exception e) {
        					    e.printStackTrace();
        					}
    			    	}else if(category == 4){
        			    	try {
        						networkHash = hostManager.getCategory(node.getIpAddress(),"Memory","PhysicalMemory", startTime, endTime);
        					} catch (Exception e) {
        					    e.printStackTrace();
        					}
    			    	}
    			    }
				}else if("inutil".equalsIgnoreCase(type)){
					bak = "入口流速";
					unit = "Kb/s";
					try {
						networkHash = hostManager.getAllutilhdx(node.getIpAddress(),"AllInBandwidthUtilHdx", startTime, endTime, "");
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}else if("oututil".equalsIgnoreCase(type)){
					bak = "出口流速";
					unit = "Kb/s";
					try {
						networkHash = hostManager.getAllutilhdx(node.getIpAddress(), "AllOutBandwidthUtilHdx",startTime, endTime, "");
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}else if("inutilper".equalsIgnoreCase(type)){
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
    			    if (ipAllData != null) {
    			    	Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
        				if (utilhdxperc != null && utilhdxperc.size()>0) {
        					for(int j = 0; j < utilhdxperc.size(); j++){
        						UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
        						try {
        							networkHash = hostManager.getUtilhdxper(node.getIpAddress(),inutilhdx.getSubentity(),startTime, endTime, "");
        						} catch (Exception e) {
        						    e.printStackTrace();
        						}
        						if (networkHash.get("avgin") != null) {
        						    String avgcpucon = (String) networkHash.get("avgin");
        						    Avgcollectdata avgcollectdata = new Avgcollectdata();
        						    avgcollectdata.setIpaddress(node.getIpAddress()+"-"+inutilhdx.getSubentity());
        						    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
        						    avgcollectdata.setBak(bak);
        						    avgcollectdata.setUnit("%");
        						    list.add(avgcollectdata);
        						}
        					}
        				}
        			}
				}else if("oututilper".equalsIgnoreCase(type)){
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
    			    if (ipAllData != null) {
    			    	Vector utilhdxperc = (Vector) ipAllData.get("utilhdxperc");
        				if (utilhdxperc != null && utilhdxperc.size()>0) {
        					for(int j = 0; j < utilhdxperc.size(); j++){
        						UtilHdxPerc inutilhdx = (UtilHdxPerc) utilhdxperc.get(j);
        						try {
        							networkHash = hostManager.getUtilhdxper(node.getIpAddress(),inutilhdx.getSubentity(),startTime, endTime, "");
        						} catch (Exception e) {
        						    e.printStackTrace();
        						}
        						if (networkHash.get("avgout") != null) {
        						    String avgcpucon = (String) networkHash.get("avgout");
        						    Avgcollectdata avgcollectdata = new Avgcollectdata();
        						    avgcollectdata.setIpaddress(node.getIpAddress()+"-"+inutilhdx.getSubentity());
        						    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
        						    avgcollectdata.setBak(bak);
        						    avgcollectdata.setUnit("%");
        						    list.add(avgcollectdata);
        						}
        					}
        				}
        			}
				}
				if (networkHash.get("avgpingcon") != null) {
				    String avgcpucon = (String) networkHash.get("avgpingcon");
				    unit = (String) networkHash.get("unit");
				    Avgcollectdata avgcollectdata = new Avgcollectdata();
				    avgcollectdata.setIpaddress(node.getIpAddress());
				    avgcollectdata.setThevalue(avgcpucon.replace("%", "").replace("毫秒", ""));
				    avgcollectdata.setBak(bak);
				    avgcollectdata.setUnit(unit);
				    list.add(avgcollectdata);
				}
				if ("cpu".equalsIgnoreCase(type)&&networkHash.get("avgcpucon") != null) {
				    String avgcpucon = (String) networkHash.get("avgcpucon");
				    if(networkHash.get("unit")!=null&&!"null".equals(networkHash.get("unit"))){
				    	unit = (String) networkHash.get("unit");
				    } else {
				    	unit = "%";
				    }
				    Avgcollectdata avgcollectdata = new Avgcollectdata();
				    avgcollectdata.setIpaddress(node.getIpAddress());
				    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
				    avgcollectdata.setBak(bak);
				    avgcollectdata.setUnit(unit);
				    list.add(avgcollectdata);
				}
				if ("memory".equalsIgnoreCase(type)&&networkHash.get("avgmemory") != null) {
				    String avgcpucon = (String) networkHash.get("avgmemory");
				    unit = (String) networkHash.get("unit");
				    if(networkHash.get("unit")!=null&&!"null".equals(networkHash.get("unit"))){
				    	unit = (String) networkHash.get("unit");
				    } else {
				    	unit = "%";
				    }
				    Avgcollectdata avgcollectdata = new Avgcollectdata();
				    avgcollectdata.setIpaddress(node.getIpAddress());
				    avgcollectdata.setThevalue(avgcpucon.replace("%", ""));
				    avgcollectdata.setBak(bak);
				    avgcollectdata.setUnit(unit);
				    list.add(avgcollectdata);
				}
				if (("inutil".equalsIgnoreCase(type)||"oututil".equalsIgnoreCase(type))&&networkHash.get("avgput") != null) {
				    String avgutilcon = (String) networkHash.get("avgput");
				    if(networkHash.get("unit")!=null&&!"null".equals(networkHash.get("unit"))){
				    	unit = (String) networkHash.get("unit");
				    } else {
				    	unit = "kb/s";
				    }
				    Avgcollectdata avgcollectdata = new Avgcollectdata();
				    avgcollectdata.setIpaddress(node.getIpAddress());
				    avgcollectdata.setThevalue(avgutilcon);
				    avgcollectdata.setBak(bak);
				    avgcollectdata.setUnit(unit);
				    list.add(avgcollectdata);
				}
		    }
		    if (list != null && list.size() > 0) {
				for (int m = 0; m < list.size(); m++) {
				    Avgcollectdata hdata = (Avgcollectdata) list.get(m);
				    for (int n = m + 1; n < list.size(); n++) {
						Avgcollectdata hosdata = (Avgcollectdata) list.get(n);
						if (new Double(hdata.getThevalue().replace("%", "").replace("毫秒", ""))
							.doubleValue() < new Double(hosdata
							.getThevalue().replace("%", "").replace("毫秒", "")).doubleValue()) {
							list.remove(m);
							list.add(m, hosdata);
							list.remove(n);
							list.add(n, hdata);
						    hdata = hosdata;
						    hosdata = null;
						}
				    }
				    // 得到排序后的Subentity的列表
				    networkList.add(hdata);
				    if(networkList.size()>=size-1){
				    	break;
				    }
				    hdata = null;
				}
		    }
		}
		return networkList;
    }
    /**
     * 获取相应ip地址在当天各个时间段的值(flex调用)
     */
    public ArrayList<Vos> getNetworkValue(int category,String ipAddress,String b_time,String t_time,String type) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		String startTime = b_time + " 00:00:00";
		String endTime = t_time + " 23:59:59";
		Hashtable networkHash = new Hashtable();
		String bak = "";
		String unit = "";
		if("ping".equalsIgnoreCase(type)){
			bak = "连通率";
			try {
				networkHash = hostManager.getCategory(ipAddress, "Ping", "ConnectUtilization",startTime, endTime);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("ResponseTime".equalsIgnoreCase(type)){
			bak = "响应时间";
			try {
				networkHash = hostManager.getCategory(ipAddress, "Ping", "ResponseTime",startTime, endTime);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("cpu".equalsIgnoreCase(type)){
			bak = "CPU利用率";
			try {
				networkHash = hostManager.getCategory(ipAddress,"CPU", "Utilization", startTime, endTime);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("memory".equalsIgnoreCase(type)){
			bak = "内存利用率";
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipAddress);
		    if (ipAllData != null) {
		    	Vector memoryV = (Vector) ipAllData.get("memory");
				if(category == 1){
		    		Memorycollectdata memory = getMaxUtilization(memoryV);
			    	try {
						networkHash = hostManager.getCategory(ipAddress,"Memory", memory.getSubentity(), startTime, endTime);
					} catch (Exception e) {
					    e.printStackTrace();
					}
		    	}else if(category == 4){
			    	try {
						networkHash = hostManager.getCategory(ipAddress,"Memory","PhysicalMemory", startTime, endTime);
					} catch (Exception e) {
					    e.printStackTrace();
					}
		    	}
		    }
		}else if("inutil".equalsIgnoreCase(type)){
			bak = "入口流速";
			unit = "Kb/s";
			try {
				networkHash = hostManager.getAllutilhdx(ipAddress,"AllInBandwidthUtilHdx", startTime, endTime, "");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("oututil".equalsIgnoreCase(type)){
			bak = "出口流速";
			unit = "Kb/s";
			try {
				networkHash = hostManager.getAllutilhdx(ipAddress, "AllOutBandwidthUtilHdx",startTime, endTime, "");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("inutilper".equalsIgnoreCase(type)){
			bak = "入口带宽利用率";
			unit = "%";
			String cate[] = ipAddress.split("-");
			try {
				networkHash = hostManager.getUtilhdxper(cate[0],cate[1], startTime, endTime, "");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}else if("oututilper".equalsIgnoreCase(type)){
			bak = "出口带宽利用率";
			unit = "%";
			String cate[] = ipAddress.split("-");
			try {
				networkHash = hostManager.getUtilhdxper(cate[0],cate[1],startTime, endTime, "");
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
		List networkList = new ArrayList();
		if (networkHash.get("list") != null) {
		    ArrayList<Vos> flexDataList = new ArrayList<Vos>();
		    networkList = (ArrayList) networkHash.get("list");
		    Vos fVo;
		    for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
				    fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", "").replace("毫秒", ""));
				    fVo.setObjectName2((String) networkVector.get(1));
				    fVo.setObjectName1(ipAddress);
				    fVo.setBak(bak);
				    if(networkVector.get(2)!=null||!"null".equals(networkVector.get(2))){unit = (String) networkVector.get(2);}
				    fVo.setUnit(unit);
				    flexDataList.add(fVo);
				}
		    }
		    return flexDataList;
		}
		if ("inutilper".equalsIgnoreCase(type)&&networkHash.get("inList") != null) {
		    ArrayList<Vos> flexDataList = new ArrayList<Vos>();
		    networkList = (ArrayList) networkHash.get("inList");
		    Vos fVo;
		    for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
				    fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", ""));
				    fVo.setObjectName2((String) networkVector.get(1));
				    fVo.setObjectName1(ipAddress);
				    fVo.setBak(bak);
				    fVo.setUnit(unit);
				    flexDataList.add(fVo);
				}
		    }
		    return flexDataList;
		}
		if ("oututilper".equalsIgnoreCase(type)&&networkHash.get("outList") != null) {
		    ArrayList<Vos> flexDataList = new ArrayList<Vos>();
		    networkList = (ArrayList) networkHash.get("outList");
		    Vos fVo;
		    for (int i = 0; i < networkList.size(); i++) {
				Vector networkVector = new Vector();
				fVo = new Vos();
				networkVector = (Vector) networkList.get(i);
				if (networkVector != null || networkVector.size() > 0) {
				    fVo.setObjectNumber(((String) networkVector.get(0)).replace("%", ""));
				    fVo.setObjectName2((String) networkVector.get(1));
				    fVo.setObjectName1(ipAddress);
				    fVo.setBak(bak);
				    fVo.setUnit(unit);
				    flexDataList.add(fVo);
				}
		    }
		    return flexDataList;
		}
		return null;
    }
}
