package com.afunms.linkReport.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.afunms.common.util.Arith;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereport.util.CompreReportStatic;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.task.CheckLinkTask;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.LinkPerformanceDTO;

public class LinkReportHelper {

	public HashMap getLinkReportDay(String linkids,String terms,String startdate,String todate){
		HashMap map = new HashMap();
		
		List<LinkReportStatic> upList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> downList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> bandwidthList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> bandtrendList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> usabilityList = new ArrayList<LinkReportStatic>();
		
		LinkDao linkdao = new LinkDao();
    	List<Link> linkList = null;
    	try{
    		linkList = linkdao.loadListByIds(linkids);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(linkdao != null){
    			linkdao.close();
    		}
    	}
    	HashMap<String,List> linkMap = getLinkList(linkList,startdate,todate);
    	if(terms.indexOf("shangxing")>-1){
    		upList = TopLink5(linkMap.get("up"),"shangxing",5);
    	}
    	if(terms.indexOf("xiaxing")>-1){
    		downList = TopLink5(linkMap.get("down"),"xiaxing",5);
    	}
    	if(terms.indexOf("keyongxin")>-1){
    		usabilityList = TopLink5(linkMap.get("usability"),"keyongxin",5);
    	}
    	if(terms.indexOf("daikuan")>-1){
    		bandwidthList = TopLink5(linkMap.get("bandwidth"),"daikuan",5);
    	}
    	if(terms.indexOf("qushitu")>-1){
    		bandtrendList = TopLink5(linkMap.get("bandtrend"),"qushitu",5);
    	}
    	map.put("up", upList);
    	map.put("down", downList);
    	map.put("usability", usabilityList);
    	map.put("bandwidth", bandwidthList);
    	map.put("bandtrend", bandtrendList);
    	return map;
	}
	
	public HashMap getLinkReportWeek(String linkids,String terms,String[][] weekday){
		HashMap map = new HashMap();
		
		List<LinkReportStatic> upList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> downList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> bandwidthList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> bandtrendList = new ArrayList<LinkReportStatic>();
		List<LinkReportStatic> usabilityList = new ArrayList<LinkReportStatic>();
		
		LinkDao linkdao = new LinkDao();
    	List<Link> linkList = null;
    	try{
    		linkList = linkdao.loadListByIds(linkids);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		if(linkdao != null){
    			linkdao.close();
    		}
    	}
    	HashMap<String,List> linkMap = getLinkList(linkList,weekday);
    	if(terms.indexOf("shangxing")>-1){
    		upList = TopLink5(linkMap.get("up"),"shangxing",5);
    	}
    	if(terms.indexOf("xiaxing")>-1){
    		downList = TopLink5(linkMap.get("down"),"xiaxing",5);
    	}
    	if(terms.indexOf("keyongxin")>-1){
    		usabilityList = TopLink5(linkMap.get("usability"),"keyongxin",5);
    	}
    	if(terms.indexOf("daikuan")>-1){
    		bandwidthList = TopLink5(linkMap.get("bandwidth"),"daikuan",5);
    	}
    	if(terms.indexOf("qushitu")>-1){
    		bandtrendList = TopLink5(linkMap.get("bandtrend"),"qushitu",5);
    	}
    	map.put("up", upList);
    	map.put("down", downList);
    	map.put("usability", usabilityList);
    	map.put("bandwidth", bandwidthList);
    	map.put("bandtrend", bandtrendList);
    	return map;
	}
	
	public HashMap<String,List> getLinkList(List<Link> linkList,String startdate,String todate){
		HashMap<String,List> back = new HashMap<String,List>();
		
		Hashtable<String,Link> linkHash = new Hashtable();
    	for(int i=0; i<linkList.size(); i++){
    		linkHash.put(linkList.get(i).getId()+"", linkList.get(i));
    	}
    	List<LinkReportStatic> upList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> downList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> usabilityList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> bandwidthList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> banktrendList = new ArrayList<LinkReportStatic>();
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkUpDataByLinkId(link.getId()+"", startdate, todate));
    		linkdao.close();
    		lrs.setUnit("Kb/취");
    		upList.add(lrs);    		
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkDownDataByLinkId(link.getId()+"", startdate, todate));
    		linkdao.close();
    		lrs.setUnit("Kb/취");
    		downList.add(lrs);
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkUsabilityDataByLinkId(link.getId()+"", startdate, todate));
    		linkdao.close();
    		lrs.setUnit("%");
    		usabilityList.add(lrs);
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkBandWidthDataByLinkId(link.getId()+"", startdate, todate));
    		linkdao.close();
    		lrs.setUnit("%");
    		bandwidthList.add(lrs);
    		banktrendList.add(lrs);
    	}
    	back.put("up", upList);
    	back.put("down", downList);
    	back.put("usability", usabilityList);
    	back.put("bandwidth", bandwidthList);
    	back.put("bandtrend", banktrendList);
		return back;
	}
	
	public HashMap<String,List> getLinkList(List<Link> linkList,String[][] weekday){
		HashMap<String,List> back = new HashMap<String,List>();
		
		Hashtable<String,Link> linkHash = new Hashtable();
    	for(int i=0; i<linkList.size(); i++){
    		linkHash.put(linkList.get(i).getId()+"", linkList.get(i));
    	}
    	List<LinkReportStatic> upList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> downList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> usabilityList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> bandwidthList = new ArrayList<LinkReportStatic>();
    	List<LinkReportStatic> banktrendList = new ArrayList<LinkReportStatic>();
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		Map<String,Double> weekValues = new TreeMap<String,Double>();
			for(int n=0;n<weekday.length;n++){
				for(int f=0;f<24;f++){
					if(f/10<1){
						weekValues.put(weekday[n][0]+"-0"+f, 0d);
					}else{
						weekValues.put(weekday[n][0]+"-"+f, 0d);
					}
				}
			}
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkUpDataByLinkId(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMax(linkdao.getLinkUpDataByLinkIdTop(link.getId()+"","max", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMin(linkdao.getLinkUpDataByLinkIdTop(link.getId()+"","min", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		weekValues = linkdao.getLinkUpDataByLinkIdForWeek(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2], weekValues);
    		linkdao.close();
    		lrs.setWeekValues(weekValues);
    		lrs.setUnit("Kb/취");
    		upList.add(lrs);    		
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		Map<String,Double> weekValues = new TreeMap<String,Double>();
			for(int n=0;n<weekday.length;n++){
				for(int f=0;f<24;f++){
					if(f/10<1){
						weekValues.put(weekday[n][0]+"-0"+f, 0d);
					}else{
						weekValues.put(weekday[n][0]+"-"+f, 0d);
					}
				}
			}
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkDownDataByLinkId(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMax(linkdao.getLinkDownDataByLinkIdTop(link.getId()+"","max", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMin(linkdao.getLinkDownDataByLinkIdTop(link.getId()+"","min", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		weekValues = linkdao.getLinkDownDataByLinkIdForWeek(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2], weekValues);
    		linkdao.close();
    		lrs.setWeekValues(weekValues);
    		lrs.setUnit("Kb/취");
    		downList.add(lrs);
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		Map<String,Double> weekValues = new TreeMap<String,Double>();
			for(int n=0;n<weekday.length;n++){
				for(int f=0;f<24;f++){
					if(f/10<1){
						weekValues.put(weekday[n][0]+"-0"+f, 0d);
					}else{
						weekValues.put(weekday[n][0]+"-"+f, 0d);
					}
				}
			}
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkUsabilityDataByLinkId(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMax(linkdao.getLinkUsabilityDataByLinkIdTop(link.getId()+"","max", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMin(linkdao.getLinkUsabilityDataByLinkIdTop(link.getId()+"","min", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		weekValues = linkdao.getLinkUsabilityDataByLinkIdForWeek(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2], weekValues);
    		linkdao.close();
    		lrs.setWeekValues(weekValues);
    		lrs.setUnit("%");
    		usabilityList.add(lrs);
    	}
    	for(int i = 0; i<linkList.size();i++){
    		Link  link = linkList.get(i);
    		Map<String,Double> weekValues = new TreeMap<String,Double>();
			for(int n=0;n<weekday.length;n++){
				for(int f=0;f<24;f++){
					if(f/10<1){
						weekValues.put(weekday[n][0]+"-0"+f, 0d);
					}else{
						weekValues.put(weekday[n][0]+"-"+f, 0d);
					}
				}
			}
    		LinkReportStatic lrs = new LinkReportStatic();
    		lrs.setId(link.getId()+"");
    		lrs.setLinkName(link.getLinkName());
    		LinkDao linkdao = new LinkDao();
    		lrs.setValue(linkdao.getLinkBandWidthDataByLinkId(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMax(linkdao.getLinkBandWidthDataByLinkIdTop(link.getId()+"","max", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		lrs.setMin(linkdao.getLinkBandWidthDataByLinkIdTop(link.getId()+"","min", weekday[0][1], weekday[weekday.length-1][2]));
    		linkdao.close();
    		linkdao = new LinkDao();
    		weekValues = linkdao.getLinkBandWidthDataByLinkIdForWeek(link.getId()+"", weekday[0][1], weekday[weekday.length-1][2], weekValues);
    		linkdao.close();
    		lrs.setWeekValues(weekValues);
    		lrs.setUnit("%");
    		bandwidthList.add(lrs);
    		banktrendList.add(lrs);
    	}
    	back.put("up", upList);
    	back.put("down", downList);
    	back.put("usability", usabilityList);
    	back.put("bandwidth", bandwidthList);
    	back.put("bandtrend", banktrendList);
		return back;
	}
	
	private List TopLink5(List temp,String term,int top){
		List list = new ArrayList();
		if(top >= temp.size()){
			top = temp.size();
			for(int i=0;i<top;i++){
				if(i==top){
					list.add((LinkReportStatic)temp.get(i));
				}else{
					for(int j = i+1;j<temp.size(); j++){
						Double m = 0d;
						Double n = 0d;
						m = Double.valueOf(((LinkReportStatic)temp.get(i)).getValue()==null?"0":((LinkReportStatic)temp.get(i)).getValue());
						n = Double.valueOf(((LinkReportStatic)temp.get(j)).getValue()==null?"0":((LinkReportStatic)temp.get(j)).getValue());
						if( m < n ){
							LinkReportStatic lrs = null;
							lrs = (LinkReportStatic)temp.get(i);
							temp.set(i, (LinkReportStatic)temp.get(j));
							temp.set(j, lrs);
						}
					}
					list.add(temp.get(i));
				}
			}
		}
		else{
			for(int i=0;i<top;i++){
				for(int j = i+1;j<temp.size(); j++){
					Double m = 0d;
					Double n = 0d;
					m = Double.valueOf(((LinkReportStatic)temp.get(i)).getValue()==null?"0":((LinkReportStatic)temp.get(i)).getValue());
					n = Double.valueOf(((LinkReportStatic)temp.get(j)).getValue()==null?"0":((LinkReportStatic)temp.get(j)).getValue());
					if( m < n ){
						LinkReportStatic lrs = null;
						lrs = (LinkReportStatic)temp.get(i);
						temp.set(i, (LinkReportStatic)temp.get(j));
						temp.set(j, lrs);
					}
				}
				list.add(temp.get(i));
			}
		}
		return list;
	}
}
