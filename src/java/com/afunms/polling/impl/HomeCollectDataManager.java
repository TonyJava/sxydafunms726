/*
 * Created on 2010-03-24
 *
 */
package com.afunms.polling.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.topology.model.HostNode;

/**
 * @author Administrator
 *
 */
public class HomeCollectDataManager{
	java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 
	 */
	public HomeCollectDataManager() {
		//super();
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 查询 设备 cpu 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getCpuAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getCpuAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 入口流速 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getAllInutilhdxAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getAllInutilhdxAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 出口流速 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getAllOututilhdxAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getAllOututilhdxAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 内存 的平均值
	 * 默认时间
	 * @param id
	 * @param ip
	 * @return
	 */
	public String getMemoryAvg(String id , String ip){
		Date date = new Date();
		
		String startTime = sdfDate.format(date) + " 00:00:00";
		String endTime = sdfTime.format(date) ;
		return getMemoryAvg(id , ip , startTime , endTime);
	}
	
	/**
	 * 
	 * 查询 设备 磁盘 
	 * 默认时间
	 * @param node
	 * @return
	 */
	public List getDisk(HostNode node){
		List hostdisklist = new ArrayList();
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(node.getIpAddress());
		if (ipAllData != null) {
			Vector diskVector = (Vector) ipAllData.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				Hashtable hostdata = ShareData.getHostdata();
				for (int si = 0; si < diskVector.size(); si++) {
					Diskcollectdata diskdata = (Diskcollectdata) diskVector.elementAt(si);
					if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
						// 利用率
						if (node.getOstype() == 4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
							diskdata.setSubentity(diskdata.getSubentity().substring(0, 3));
						}
						hostdisklist.add(diskdata);
					}
				}
			}
		}
		return hostdisklist;
	}
	
	public String getCpuAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("cpu" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTime( "cpu"+allipstr , startTime , endTime);
		
		
		return avg;
	}
	public List getDiskForIp(String ip){
		List hostdisklist = new ArrayList();
		String allipstr = praseIp(ip);
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(allipstr);
		if (ipAllData != null) {
			Vector diskVector = (Vector) ipAllData.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				Hashtable hostdata = ShareData.getHostdata();
				for (int si = 0; si < diskVector.size(); si++) {
					Diskcollectdata diskdata = (Diskcollectdata) diskVector.elementAt(si);
					if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
						hostdisklist.add(diskdata);
					}
				}
			}
		}
		return hostdisklist;
	}
	public String getPingAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		avg = getAvgByTimeAndWhere( "ping" + allipstr , startTime , endTime , "and subentity='ConnectUtilization'");
		
		return avg;
	}
	
	public String getResponseAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		avg = getAvgByTimeAndWhere( "ping" + allipstr , startTime , endTime , "and subentity='ResponseTime'");
		
		return avg;
	}
	
	public List getDisk(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		List disk = new ArrayList();
		disk = getDiskByTime("disk" + allipstr , startTime , endTime);
		return disk;
	}
	
	public String getAllInutilhdxAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTimeAndWhere( "allutilhdx" + allipstr , startTime , endTime , "and subentity='AllInBandwidthUtilHdx'");
		
		return avg;
	}
	
	public String getAllOututilhdxAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTimeAndWhere( "allutilhdx" + allipstr , startTime , endTime , "and subentity='AllOutBandwidthUtilHdx'");
		
		return avg;
	}
	
	public String getMemoryAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		
//		HostCollectDataDao hostCollectDataDao = new HostCollectDataDao("allutilhdx" + allipstr);
//		try {
//			avg = hostCollectDataDao.getAvgByTime(startTime, endTime);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		avg = getAvgByTime( "memory" + allipstr , startTime , endTime);
		
		return avg;
	}
	
	public String praseIp(String ip){
//		String ip1 ="",ip2="",ip3="",ip4="";	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}	
	private List getDiskByTime(String allipstr , String startTime , String endTime){
		List list = new ArrayList();
		String sql = "select subentity,round(max(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' group by subentity";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					Diskcollectdata dc = new Diskcollectdata();
					dc.setSubentity(rs.getString("subentity"));
					dc.setThevalue(rs.getString("thevalue"));
					list.add(dc);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			list = new ArrayList();
		} finally{
			conn.close();
		}
		return list;
	}
	
	private String getAvgByTimeAndWhere(String allipstr , String startTime , String endTime , String where) {
		String avg = "0";
		String sql = "select round(avg(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' " + where;
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			conn.close();
		}
		return avg;
	}
	
	private String getAvgByTime(String allipstr , String startTime , String endTime) {
		String avg = "0";
		String sql = "select round(avg(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"'";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			conn.close();
		}
		return avg;
	}
	private String getTopByTimeAndWhere(String allipstr , String startTime , String endTime , String where) {
		String max = "0";
		String sql = "select round(max(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' " + where;
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				max = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			max = "0";
		} finally{
			conn.close();
		}
		return max;
	}
	
//	public static void main(String[] args){
//		new HomeCollectDataManager().getCpuAvg("1","10.10.152.59");
//	}
	public Map<String,Double> getCpuAvgWeek(String id,String ip,String startTime,String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeWeek("cpuhour"+allipstr , startTime , endTime,weekValues);
		return weekValues;
	}
	public Map<String,Double> getMemoryAvgWeek(String id,String ip,String startTime,String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeWeek("memoryhour"+allipstr , startTime , endTime,weekValues);
		return weekValues;
	}
	public Map<String,Double> getPingAvgWeek(String id,String ip,String startTime,String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeAndWhereWeek( "pinghour" + allipstr , startTime , endTime , " and subentity='ConnectUtilization'",weekValues);
		return weekValues;
	}
	
	public Map<String,Double> getResponseAvgWeek(String id,String ip,String startTime,String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeAndWhereWeek( "pinghour" + allipstr,startTime,endTime," and subentity='ResponseTime'",weekValues);
		return weekValues;
	}
	public Map<String,Double> getDiskWeek(String id ,String ip , String subentity ,String startTime , String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getDiskWeekByTime("diskhour" + allipstr ,subentity, startTime , endTime,weekValues);
		return weekValues;
	}
	public Map<String,Double> getAllInutilhdxAvgWeek(String id , String ip , String startTime , String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeAndWhereWeek( "allutilhdxhour" + allipstr , startTime , endTime , "and subentity='AllInBandwidthUtilHdx'",weekValues);
		return weekValues;
	}
	
	public Map<String,Double> getAllOututilhdxAvgWeek(String id , String ip , String startTime , String endTime,Map<String,Double> weekValues){
		String allipstr = praseIp(ip);
		weekValues = getAvgByTimeAndWhereWeek( "allutilhdxhour" + allipstr , startTime , endTime , "and subentity='AllOutBandwidthUtilHdx'",weekValues);
		return weekValues;
	}
	private Map<String,Double> getAvgByTimeWeek(String allipstr , String startTime , String endTime,Map<String,Double> weekValues) {
		String sql = "select Date_format(collecttime,'%Y-%m-%d-%H') dat, round(avg(thevalue),3) thevalue from " +
				allipstr +" where collecttime between '"+startTime+"' and '"+endTime+"' group by Date_format(collecttime,'%Y-%m-%d-%H')";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					weekValues.put(rs.getString("dat"),rs.getDouble("thevalue"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return weekValues;
	}
	private Map<String,Double> getAvgByTimeAndWhereWeek(String allipstr,String startTime,String endTime,String where,Map<String,Double> weekValues) {
		String sql = "select Date_format(collecttime,'%Y-%m-%d-%H') dat, round(avg(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' " + where +" group by Date_format(collecttime,'%Y-%m-%d-%H')";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					weekValues.put(rs.getString("dat"),rs.getDouble("thevalue"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return weekValues;
	}
	private Map<String,Double> getDiskWeekByTime(String allipstr ,String subentity, String startTime , String endTime,Map<String,Double> weekValues){
		String sql = "select Date_format(collecttime,'%Y-%m-%d-%H') dat,subentity,round(max(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' and subentity = '"+ subentity +"' group by Date_format(collecttime,'%Y-%m-%d-%H') ";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					weekValues.put(rs.getString("dat"),rs.getDouble("thevalue"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return weekValues;
	}
	
	public String getDiskWeek(String id ,String ip , String subentity ,String startTime , String endTime){
		String allipstr = praseIp(ip);
		String disk ="";
		disk = getDiskWeekByTime("disk" + allipstr ,subentity, startTime , endTime);
		return disk;
	}
	private String getDiskWeekByTime(String allipstr ,String subentity, String startTime , String endTime){
		String back = "0";
		String sql = "select subentity,round(max(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' and subentity = '"+ subentity +"' group by subentity";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				back = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			back = "0";
		} finally{
			conn.close();
		}
		return back;
	}
	public String getCpuTop(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		avg = getTopByTimeAndWhere( "pro"+allipstr , startTime , endTime, "and entity='CpuUtilization'");
		return avg;
	}
	public String getResponseTop(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		avg = getTopByTimeAndWhere( "pro" + allipstr , startTime , endTime , "and entity='MemoryUtilization'");
		return avg;
	}
	public String getCpuTimeTop(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String avg = "";
		avg = getTopByTimeAndWhere( "pro" + allipstr , startTime , endTime , "and entity='CpuTime'");
		return avg;
	}
	public List<String[]> getProCpuAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		List<String[]> avg = getProsByTimeAndWhere( "pro" + allipstr , startTime , endTime, "and entity='CpuUtilization'");
		return avg;
	}
	public List<String[]> getProMemoryAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		List<String[]> avg = getProsByTimeAndWhere( "pro" + allipstr , startTime , endTime, "and entity='MemoryUtilization'");
		return avg;
	}
	public List<String[]> getProTimeAvg(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		List<String[]> avg = getProsByTimeAndWhere( "pro" + allipstr , startTime , endTime, "and entity='CpuTime'");
		return avg;
	}
	public String[] getCpuMax(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String[] avg = getTopsByTimeAndWhere( "cpu"+allipstr , startTime , endTime,"");
		return avg;
	}
	public String[] getResponseMax(String id , String ip , String startTime , String endTime){
		String allipstr = praseIp(ip);
		String[] avg = getTopsByTimeAndWhere( "ping" + allipstr , startTime , endTime , "and subentity='ResponseTime'");
		return avg;
	}
	private String[] getTopsByTimeAndWhere(String allipstr , String startTime , String endTime , String where) {
		String[] max = {"0","0","0"};
		String sql = "select round(thevalue,3) thevalue,count(1) coun,min(collecttime) collect from " + allipstr +
				" where collecttime between '"+ startTime + "' and '" + endTime + "' " + where + " and thevalue = (select max(thevalue) from " +
				 allipstr +" where collecttime between '"+ startTime + "' and '" + endTime + "') ";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				max[0] = rs.getString("thevalue");
				max[1] = rs.getString("coun");
				max[2] = rs.getString("collect");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			max = new String[] {"0","0","0"};
		} finally{
			conn.close();
		}
		return max;
	}
	private List<String[]> getProsByTimeAndWhere(String allipstr , String startTime , String endTime , String where) {
		List<String[]> val = new ArrayList<String[]>();
		
		String sql = "select chname, round(avg(thevalue),3) value from " + allipstr +
				" where collecttime between '" + startTime + "' and '" + endTime +"' " + where + " group by chname";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					String[] max = {"","0"};
					max[0] = rs.getString("chname");
					max[1] = rs.getString("value");
					val.add(max);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			val = new ArrayList<String[]>();
		} finally{
			conn.close();
		}
		return val;
	}
	public String getCpuExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTime( "cpuhour"+allipstr , startTime , endTime,ext);
		return value;
	}
	public String getMemoryExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTime( "memoryhour"+allipstr , startTime , endTime,ext);
		return value;
	}
	public String getPingExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTimeAndWhere( "pinghour" + allipstr , startTime , endTime ,ext, "and subentity='ConnectUtilization'");
		return value;
	}
	public String getResponseExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTimeAndWhere( "pinghour" + allipstr , startTime , endTime ,ext, "and subentity='ResponseTime'");
		return value;
	}
	public String getAllInutilhdxExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTimeAndWhere( "allutilhdxhour" + allipstr , startTime , endTime ,ext, "and subentity='AllInBandwidthUtilHdx'");
		return value;
	}
	
	public String getAllOututilhdxExtreme(String id , String ip , String startTime , String endTime,String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTimeAndWhere( "allutilhdxhour" + allipstr , startTime , endTime ,ext, "and subentity='AllOutBandwidthUtilHdx'");
		return value;
	}
	public String getDiskExtreme(String id ,String ip , String subentity ,String startTime , String endTime, String ext){
		String allipstr = praseIp(ip);
		String value = "";
		value = getExtremeValueByTimeAndWhere( "diskhour" + allipstr ,startTime , endTime ,ext, " and subentity = '"+subentity+"'");
		return value;
	}
	private String getExtremeValueByTime(String allipstr , String startTime , String endTime,String ext) {
		String avg = "0";
		String sql = "select round("+ext+"(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"'";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			conn.close();
		}
		return avg;
	}
	private String getExtremeValueByTimeAndWhere(String allipstr , String startTime , String endTime ,String ext , String where) {
		String avg = "0";
		String sql = "select round("+ext+"(thevalue),3) thevalue from "+ allipstr +" where collecttime between '" + startTime 
		+ "' and '" + endTime +"' " + where;
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null && rs.next()){
				avg = rs.getString("thevalue");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			avg = "0";
		} finally{
			conn.close();
		}
		return avg;
	}
	private Map<String,Double> getProHourByTime(String allipstr ,String subentity, String startTime , String endTime,String chname,Map<String,Double> weekValues){
		String sql = "select chname, Date_format(collecttime,'%Y-%m-%d-%H') dat, round(avg(thevalue),3) value from " 
			+ allipstr + " where collecttime between '" + startTime + "' and '" + endTime + 
			"' and entity='" + subentity + "' and chname = '" + chname + "'  group by chname,Date_format(collecttime,'%Y-%m-%d-%H')";
		DBManager conn = new DBManager();
		try {
			ResultSet rs = conn.executeQuery(sql);
			if(rs != null){
				while(rs.next()){
					weekValues.put(rs.getString("dat"),rs.getDouble("value"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			conn.close();
		}
		return weekValues;
	}
	public Map<String,Double> getProCpuValueHour(String id , String ip , String startTime , String endTime,String chname,Map<String,Double> weekDay){
		String allipstr = praseIp(ip);
		weekDay = getProHourByTime( "prohour" + allipstr,"CpuUtilization",startTime,endTime,chname,weekDay);
		return weekDay;
	}
	public Map<String,Double> getProMemoryValueHour(String id , String ip , String startTime , String endTime,String chname,Map<String,Double> weekDay){
		String allipstr = praseIp(ip);
		weekDay = getProHourByTime( "prohour" + allipstr,"MemoryUtilization",startTime,endTime,chname,weekDay);
		return weekDay;
	}
	public Map<String,Double> getProTimeValueHour(String id , String ip , String startTime , String endTime,String chname,Map<String,Double> weekDay){
		String allipstr = praseIp(ip);
		weekDay = getProHourByTime( "prohour" + allipstr,"CpuTime",startTime,endTime,chname,weekDay);
		return weekDay;
	}
}
