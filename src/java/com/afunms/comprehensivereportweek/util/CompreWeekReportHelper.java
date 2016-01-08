package com.afunms.comprehensivereportweek.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.capreport.model.StatisNumer;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HomeCollectDataManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class CompreWeekReportHelper {
	public HashMap getNetTopValue(String[] idValue,String startTime,String toTime){
		HashMap netValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List pingList = new ArrayList();			//连通性
		List responseList = new ArrayList();		//响应度
		List inList = new ArrayList();				//入口流速
		List outList = new ArrayList();				//出口流速
		
		HostNodeDao netDao = new HostNodeDao();
		List netList = netDao.loadNetwork(1);
		netDao.close();
		if(netList == null && netList.size() == 0){
			return netValueMap;
		}
		String[] ips = new String[netList.size()];
		for(int i=0;i<netList.size();i++){
			ips[i] = ((HostNode)netList.get(i)).getIpAddress();
		}
		if (idValue!=null&&idValue.length>0) {
			for (int i = 0; i < idValue.length; i++) {
				//cpu
				List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> pingTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> responseTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> inTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> outTemp = new ArrayList<CompreReportStatic>();
				
				if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//cpu
						String cpuvalue = hcdm.getCpuAvg("0", ips[j], startTime, toTime);
						cpuvalue = cpuvalue == null? "0":cpuvalue;
						CompreReportStatic crscpu = new CompreReportStatic();
						crscpu.setIp(ips[j]);
						crscpu.setType("cpu");
						crscpu.setUnit("%");
						crscpu.setValue(Double.valueOf(cpuvalue));
						cpuTemp.add(crscpu);
					}
					cpuList = compareTop(cpuTemp,10);//CPU top10
				}else if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//内存
						String memvalue = hcdm.getMemoryAvg("0", ips[j], startTime, toTime);
						memvalue = memvalue == null? "0":memvalue;
						CompreReportStatic crsmem = new CompreReportStatic();
						crsmem.setIp(ips[j]);
						crsmem.setType("mem");
						crsmem.setUnit("%");
						crsmem.setValue(Double.valueOf(memvalue));
						memTemp.add(crsmem);
					}
					memList = compareTop(memTemp,10);//内存 top10
				}else if(idValue[i].indexOf("ping")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						//ping
						String pingvalue = hcdm.getPingAvg("0", ips[j], startTime, toTime);
						pingvalue = pingvalue == null? "0":pingvalue;
						CompreReportStatic crsping = new CompreReportStatic();
						crsping.setIp(ips[j]);
						crsping.setType("ping");
						crsping.setUnit("%");
						crsping.setValue(Double.valueOf(pingvalue));
						pingTemp.add(crsping);
					}
					pingList = compareTop(pingTemp,10);//ping top10
				}else if(idValue[i].indexOf("resp")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						//response
						String responsevalue = hcdm.getResponseAvg("0", ips[j], startTime, toTime);
						responsevalue = responsevalue == null? "0":responsevalue;
						CompreReportStatic crsresponse = new CompreReportStatic();
						crsresponse.setIp(ips[j]);
						crsresponse.setType("response");
						crsresponse.setUnit("ms");
						crsresponse.setValue(Double.valueOf(responsevalue));
						responseTemp.add(crsresponse);
					}
					responseList = compareTop(responseTemp,10);//响应度 top10
				}else if(idValue[i].indexOf("utilIn")>=0){
					//入口
					for(int j=0;j<ips.length;j++){
						String invalue = hcdm.getAllInutilhdxAvg("0",ips[j], startTime, toTime);
						invalue = invalue == null? "0":invalue;
						CompreReportStatic crsin = new CompreReportStatic();
						crsin.setIp(ips[j]);
						crsin.setType("inutilhdx");
						crsin.setUnit("KB/s");
						crsin.setValue(Double.valueOf(invalue));
						inTemp.add(crsin);
					}
					inList = compareTop(inTemp,10);//入口 top10
				}else if(idValue[i].indexOf("utilOut")>=0){
					//入口
					for(int j=0;j<ips.length;j++){
						//出口
						String outvalue = hcdm.getAllOututilhdxAvg("0",ips[j], startTime, toTime);
						outvalue = outvalue == null? "0":outvalue;
						CompreReportStatic crsout = new CompreReportStatic();
						crsout.setIp(ips[j]);
						crsout.setType("oututilhdx");
						crsout.setUnit("KB/s");
						crsout.setValue(Double.valueOf(outvalue));
						outTemp.add(crsout);
					}
					outList = compareTop(outTemp,10);//出口 top10
				}
			}
		}
		netValueMap.put("cpu", cpuList);
		netValueMap.put("mem", memList);
		netValueMap.put("ping", pingList);
		netValueMap.put("response", responseList);
		netValueMap.put("utilIn", inList);
		netValueMap.put("utilOut", outList);
		return netValueMap;
	}
	//选择出Top
	private List compareTop(List<CompreReportStatic> temp, int top){
		List list = new ArrayList();
		if(top > temp.size()){
			top = temp.size();
		}
		for(int i=0;i<top;i++){
			for(int j = i+1;j<temp.size()-1; j++){
				if(temp.get(i).getValue()<temp.get(j).getValue()){
					CompreReportStatic crs = null;
					crs = (CompreReportStatic)temp.get(i);
					temp.set(i, (CompreReportStatic)temp.get(j));
					temp.set(j, crs);
				}
			}
			list.add(temp.get(i));
		}
		return list;
	}
	private String[] getIdValue(String ids){
		String[] idValue=null;
		if (ids!=null&&!ids.equals("null")&&!ids.equals("")) {
			 idValue=new String[ids.split(",").length];
	    	idValue=ids.split(",");
		}
		return idValue;
	}
	private String[] getIdsValues(String[] idValue,String type){
		String[] idsValue = null;
		List<String> idsList = new ArrayList<String>();
		for(int i=0;i<idValue.length;i++){
			if(idValue[i].contains(type)){
				idsList.add(idValue[i].replace(type, ""));
			}
		}
		if(idsList != null && idsList.size() > 0){
			idsValue = new String[idsList.size()];
			for(int i=0;i<idsList.size();i++){
				idsValue[i] = idsList.get(i);
			}
		}
		return idsValue;
	}
}
