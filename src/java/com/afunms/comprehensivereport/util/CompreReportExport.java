package com.afunms.comprehensivereport.util;

import java.awt.Color;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereport.report.CompreExcel;
import com.afunms.comprehensivereport.report.CompreExportInterface;
import com.afunms.comprehensivereport.report.ComprePdf;
import com.afunms.comprehensivereport.report.CompreWord;
import com.afunms.comprehensivereport.util.CompreReportHelper;
import com.afunms.initialize.ResourceCenter;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.lowagie.text.Font;
import com.lowagie.text.Element;

public class CompreReportExport {
	private final int xlabel = 12;

	private CompreReportHelper reportHelper = null;

	private int chartWith = 768;

	private int chartHigh = 338;

	/**
	 * 导出报表
	 * 
	 * @param ids
	 *            指标id
	 * @param type
	 *            类型
	 * @param filePath
	 *            保存路径
	 * @param startTime
	 *            开始时间
	 * @param toTime
	 *            结束时间
	 * @param exportType
	 *            导出文件类型
	 * @throws Exception 
	 */
	public void exportReportByDay(String ids, String type, String reportType,String filePath,
			String startTime, String toTime, String exportType,String business){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// 存放表格信息
		ArrayList<ArrayList<String[]>> tableList = null;
		// 存放图片路径
		ArrayList<String> chartList = new ArrayList<String>();
		// 存放标题
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new CompreReportHelper();
		String title = "报表";
		if ("hostNet".equalsIgnoreCase(type)) {
			// 网络设备、服务器
			if("day".equals(reportType)){
				title = "设备运行信息日报表";
			}else if("busi".equals(reportType)){
				title = "设备运行信息日报表(业务)";
			}else if("week".equals(reportType)){
				title = "设备运行信息周报表";
			}else if("weekBusi".equals(reportType)){
				title = "设备运行信息周报表(业务)";
			}
			hm = exportNetHostByDay1(ids, type, filePath, startTime, toTime,business);
//			tableList = (ArrayList<ArrayList<String[]>>) hm.get("table");
//			chartList = (ArrayList<String>) hm.get("chart");
//			headList = (ArrayList<String>) hm.get("head");
		}
		String time = startTime + "~" + toTime;
		CompreExportInterface export = null;
		if ("xls".equals(exportType)) {
			chartWith = 768;
			chartHigh = 338;
			export = new CompreExcel(filePath);
		} else if ("pdf".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new ComprePdf(filePath);
		} else if ("doc".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new CompreWord(filePath);
		}
		try {
			export(export,hm,title, time);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//export(export, tableList, chartList,headList, title, time);
		if (chartList != null) {
			for (int i = 0; i < chartList.size(); i++) {
				String chartal = chartList.get(i);
				try {
					File f = new File(chartal);
					f.delete();
				} catch (Exception e) {
					SysLogger.error("删除图片：" + chartal + "失败！", e);
				}
			}
		}
	}
	
	public void exportReportByDay(String ids, String type, String reportType,String filePath,
			String[][] weekDay,String exportType,String business){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// 存放表格信息
		ArrayList<ArrayList<String[]>> tableList = null;
		// 存放图片路径
		ArrayList<String> chartList = new ArrayList<String>();
		// 存放标题
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new CompreReportHelper();
		String title = "报表";
		if ("hostNet".equalsIgnoreCase(type)) {
			// 网络设备、服务器
			if("day".equals(reportType)){
				title = "设备运行信息日报表";
			}else if("busi".equals(reportType)){
				title = "设备运行信息日报表(业务)";
			}else if("week".equals(reportType)){
				title = "设备运行信息周报表";
			}else if("weekBusi".equals(reportType)){
				title = "设备运行信息周报表(业务)";
			}
			hm = exportNetHostByDay1(ids, type, filePath, weekDay,business);
//			tableList = (ArrayList<ArrayList<String[]>>) hm.get("table");
//			chartList = (ArrayList<String>) hm.get("chart");
//			headList = (ArrayList<String>) hm.get("head");
		}
		String time = weekDay[0][1] + "~" + weekDay[weekDay.length-1][2];
		CompreExportInterface export = null;
		if ("xls".equals(exportType)) {
			chartWith = 768;
			chartHigh = 338;
			export = new CompreExcel(filePath);
		} else if ("pdf".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new ComprePdf(filePath);
		} else if ("doc".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new CompreWord(filePath);
		}
		try {
			export(export,hm,title, time);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//export(export, tableList, chartList,headList, title, time);
		if (chartList != null) {
			for (int i = 0; i < chartList.size(); i++) {
				String chartal = chartList.get(i);
				try {
					File f = new File(chartal);
					f.delete();
				} catch (Exception e) {
					SysLogger.error("删除图片：" + chartal + "失败！", e);
				}
			}
		}
	}
	/**
	 * 导出
	 * 
	 * @param export
	 * @param tableList
	 * @param chartList
	 * @param title
	 * @param time
	 */
	
	private void export(CompreExportInterface export,HashMap hm, String title, String time) throws Exception{
		ArrayList<String> headList = (ArrayList<String>) hm.get("catalog");
		ArrayList<HashMap> contentList = (ArrayList<HashMap>) hm.get("content");
		if (contentList != null) {
			export.insertTitle(title,0,time);
			for(int i=0;i<contentList.size();i++){
				export.insertContent((i+1)+" "+headList.get(i),16,Font.BOLD,Element.ALIGN_LEFT);
				HashMap content = contentList.get(i);
				if(i == 0){
					ArrayList<String> headList1 = (ArrayList<String>) content.get("catalog");
					ArrayList<HashMap> content1 =  (ArrayList<HashMap>) content.get("content");
					for(int j=0;j<content1.size();j++){
						export.insertContent((i+1)+"."+(j+1)+" "+headList1.get(j),14,Font.BOLD,Element.ALIGN_LEFT);
						if(j==0){
							HashMap contentAlarm = (HashMap<String,String>)content1.get(j);
							export.insertContent("  事件总数"+contentAlarm.get("sum")+"次,其中:普通告警"+contentAlarm.get("level1")+
									"次,严重告警"+contentAlarm.get("level2")+"次,紧急告警"+contentAlarm.get("level3")+"次.",12,Font.NORMAL,Element.ALIGN_BASELINE);
						}
						if(j==1){
							HashMap contentDev = (HashMap<String,List<String[][]>>)content1.get(j);
							ArrayList<ArrayList<String[]>> devTable = (ArrayList<ArrayList<String[]>>)contentDev.get("table");
							if (devTable != null && devTable.size() > 0) {
								for(int k=0;k<devTable.size();k++){
									ArrayList<String[]> tableal = (ArrayList<String[]>)devTable.get(k);
									if (tableal != null && tableal.size() > 0) {
										export.insertTable(tableal);
									}
								}
							}
						}
						if(j==2){
							HashMap contentDevTra = content1.get(j);
							ArrayList<String[]> tableal = (ArrayList<String[]>) contentDevTra.get("table");
							if (tableal != null && tableal.size() > 0) {
								export.insertTable(tableal);
							}								
							String chartal = (String)contentDevTra.get("chart");
							if (chartal != null && !"".equals(chartal.trim())) {
								export.insertChart(chartal);
							}
						}
					}	
				}
				if(i==1){
					ArrayList<String> headList2 = (ArrayList<String>) content.get("catalog");
					ArrayList<HashMap> content2 =  (ArrayList<HashMap>) content.get("content");
					for(int j=0;j<content2.size();j++){
						export.insertContent((i+1)+"."+(j+1)+" "+headList2.get(j),14,Font.BOLD,Element.ALIGN_LEFT);
						HashMap hostOrNet = content2.get(j);
						ArrayList<ArrayList<String[]>> tableList = (ArrayList<ArrayList<String[]>>) hostOrNet.get("table");
						ArrayList<String> chartList = (ArrayList<String>) hostOrNet.get("chart");
						ArrayList<String> hostNetheadList =(ArrayList<String>) hostOrNet.get("head");
						if(tableList!=null && tableList.size()>0){
							for(int k=0;k<tableList.size();k++){
								String head = hostNetheadList.get(k);
								export.insertContent((i+1)+"."+(j+1)+"."+(k+1)+" "+head,12,Font.BOLD,Element.ALIGN_LEFT);
								if(chartList!=null){
									String chartal = chartList.get(k);
									if (chartal != null && !"".equals(chartal.trim())) {
										export.insertChart(chartal);
									}
								}
								ArrayList<String[]> tableal = tableList.get(k);
								if (tableal != null && tableal.size() > 0) {
									export.insertTable(tableal);
								}
							}
						}
					}
				}
			}
			try {
				export.save();
			} catch (Exception e) {
				SysLogger.error("------导出文件保存失败！------", e);
			}
		}
	}
	/**
	 * @param ids
	 * @param type
	 * @param filePath
	 * @param startTime
	 * @param toTime
	 * @return
	 */
	
	private HashMap<?, ArrayList<?>> exportNetHostByDay1(String ids, String type,String filePath, String startTime, String toTime,String business) {
		CompreReportDataCreate crc = new CompreReportDataCreate();
		
		HashMap hash = new HashMap();//总文档
		ArrayList<String> headList = new ArrayList<String>(); //1级目录
		headList.add("设备事件统计");
		headList.add("设备TOP分析");
		ArrayList<HashMap> contentList = new ArrayList<HashMap>();//1级内容List
		//设备事件统计
		HashMap contentMap1 = new HashMap();
			//设备事件统计目录
			ArrayList<String> headList1 = new ArrayList<String>(); //目录1
			headList1.add("设备故障告警统计");
			headList1.add("设备告警明细");
			headList1.add("事件告警趋势");
			contentMap1.put("catalog", headList1);
			//设备事件统计内容
			ArrayList<HashMap> contentList1 = new ArrayList<HashMap>();
				//设备故障告警统计
				HashMap contentAlarm = new HashMap();
					Map<String,String> map = crc.getLevelPieDataForMap(startTime,toTime,business);
				contentAlarm.put("sum", Integer.valueOf(map.get("1"))+Integer.valueOf(map.get("2"))+Integer.valueOf(map.get("3"))+"");
				contentAlarm.put("level1", map.get("1"));
				contentAlarm.put("level2", map.get("2"));
				contentAlarm.put("level3", map.get("3"));
			contentList1.add(contentAlarm);
				//设备告警明细
				HashMap contentDevDet = new HashMap();
				ArrayList<ArrayList<String[]>> devTableList = new ArrayList<ArrayList<String[]>>();
					String[][] DevHost = crc.getDevTableData("host",startTime,toTime,business);
					String[][] DevNet = crc.getDevTableData("net",startTime,toTime,business);
					ArrayList<String[]> devHostal = new ArrayList<String[]>();
					ArrayList<String[]> devNetal = new ArrayList<String[]>();
					if(DevHost!=null&&DevHost.length>0){
						for(int i=0;i<DevHost.length;i++){
							devHostal.add(DevHost[i]);
						}
						devTableList.add(devHostal);
					}
					if(DevNet!=null&&DevNet.length>0){
						for(int i=0;i<DevNet.length;i++){
							devNetal.add(DevNet[i]);
						}
						devTableList.add(devNetal);
					}
				contentDevDet.put("table", devTableList);
			contentList1.add(contentDevDet);
				//事件告警趋势
				HashMap contentDevTra = new HashMap();
					String[][] tableData = crc.gettableData(startTime,toTime,business);
					ArrayList<String[]> DevTra = new ArrayList<String[]>();
					for(int i=0;i<tableData.length;i++){
						DevTra.add(tableData[i]);
					}
					contentDevTra.put("table", DevTra);
					List dayHoursList = crc.getDayAlarmDataForList(startTime,toTime,business);
					String dayHourspath = makeJfreeChartDataForDayHours(dayHoursList,"今日每小时告警数","时间","条数");
				contentDevTra.put("chart", dayHourspath);
			contentList1.add(contentDevTra);
			contentMap1.put("content", contentList1);
		contentList.add(contentMap1);
		//设备TOP分析
		HashMap contentMap2 = new HashMap();
		HashMap all = reportHelper.getAllValue(ids, startTime, toTime,business);
		HashMap netMap = (HashMap)all.get("net");
    	HashMap hostMap = (HashMap)all.get("host");
			ArrayList<String> headList2 = new ArrayList<String>(); //目录2
			ArrayList<HashMap> contentList2 = new ArrayList<HashMap>(); 
			//服务器Top分析
			HashMap host = new HashMap();
			List hostPingList=new ArrayList();
	    	List hostResponseList=new ArrayList();
	    	List hostCpuList=new ArrayList();
	    	List hostMemList=new ArrayList();
	    	List hostDiskList=new ArrayList();
	    	// 存放表格信息
			ArrayList<ArrayList<String[]>> hostTableList = new ArrayList<ArrayList<String[]>>();
			// 存放图片路径
			ArrayList<String> hostChartList = new ArrayList<String>();
			//存放标题
			ArrayList<String> hostHeadList = new ArrayList<String>();
			
			hostCpuList = (List)hostMap.get("cpu");
			ArrayList<String[]> hostCpual = new ArrayList<String[]>();
			String[] hostCpuTitle = { "ip", "当前利用率"};
			if(hostCpuList.size() > 0){
				hostHeadList.add("服务器CPU Top");
				hostCpual.add(hostCpuTitle);
				for(int i=0;i<hostCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostCpual.add(arry);
				}
				hostTableList.add(hostCpual);
				String hostCpupath = makeJfreeChartDataForDay(hostCpuList,"服务器 CPU TOP","IP", "利用率");
				hostChartList.add(hostCpupath);
			}
			hostMemList = (List)hostMap.get("mem");
			ArrayList<String[]> hostMemal = new ArrayList<String[]>();
			String[] hostMemTitle = { "ip", "当前利用率"};
			if(hostMemList.size() > 0){
				hostHeadList.add("服务器内存 Top");
				hostMemal.add(hostMemTitle);
				for(int i=0;i<hostMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostMemList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostMemal.add(arry);
				}
				hostTableList.add(hostMemal);
				String hostMempath = makeJfreeChartDataForDay(hostMemList,"服务器 内存 TOP","IP", "利用率");
				hostChartList.add(hostMempath);
			}
			hostPingList = (List)hostMap.get("ping");
			ArrayList<String[]> hostPingal = new ArrayList<String[]>();
			String[] hostPingTitle = { "ip", "当前连通率"};
			if(hostPingList.size() > 0){
				hostHeadList.add("服务器连通性 Top");
				hostPingal.add(hostPingTitle);
				for(int i=0;i<hostPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostPingList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostPingal.add(arry);
				}
				hostTableList.add(hostPingal);
				String hostPingpath = makeJfreeChartDataForDay(hostPingList,"服务器 连通性 TOP","IP", "连通率");
				hostChartList.add(hostPingpath);
			}
			hostResponseList = (List)hostMap.get("response");
			ArrayList<String[]> hostResponseal = new ArrayList<String[]>();
			String[] hostResponseTitle = { "ip", "响应时间"};
			if(hostResponseList.size() > 0){
				hostHeadList.add("服务器响应度 Top");
				hostResponseal.add(hostResponseTitle);
				for(int i=0;i<hostResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostResponseList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostResponseal.add(arry);
				}
				hostTableList.add(hostResponseal);
				String hostResponsepath = makeJfreeChartDataForDay(hostResponseList,"服务器 响应时间 TOP","IP", "响应时间");
				hostChartList.add(hostResponsepath);
			}
			hostDiskList = (List)hostMap.get("disk");
			ArrayList<String[]> hostDiskal = new ArrayList<String[]>();
			String[] hostDiskTitle = { "ip", "当前利用率"};
			if(hostDiskList.size() > 0){
				hostHeadList.add("服务器磁盘 Top");
				hostDiskal.add(hostDiskTitle);
				for(int i=0;i<hostDiskList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostDiskList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostDiskal.add(arry);
				}
				hostTableList.add(hostDiskal);
				String hostDiskpath = makeJfreeChartDataForDay(hostDiskList,"服务器 磁盘 TOP","IP", "利用率");
				hostChartList.add(hostDiskpath);
			}
			host.put("head",hostHeadList);
			host.put("table", hostTableList);
			host.put("chart", hostChartList);
			if(hostHeadList.size()>0){
				headList2.add("服务器Top分析");
				contentList2.add(host);
			}
			//网络Top分析
			HashMap net = new HashMap();
			List netPingList=new ArrayList();
	    	List netResponseList=new ArrayList();
	    	List netCpuList=new ArrayList();
	    	List netMemList=new ArrayList();
			List netUtilInList=new ArrayList();
			List netUtilOutList=new ArrayList();
			// 存放表格信息
			ArrayList<ArrayList<String[]>> netTableList = new ArrayList<ArrayList<String[]>>();
			// 存放图片路径
			ArrayList<String> netChartList = new ArrayList<String>();
			//存放标题
			ArrayList<String> netHeadList = new ArrayList<String>();
			netCpuList = (List)netMap.get("cpu");
			ArrayList<String[]> netCpual = new ArrayList<String[]>();
			String[] netCpuTitle = { "ip", "当前利用率"};
			if(netCpuList.size() > 0){
				netHeadList.add("网络设备CPU Top");
				netCpual.add(netCpuTitle);
				for(int i=0;i<netCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netCpual.add(arry);
				}
				netTableList.add(netCpual);
				String netCpupath = makeJfreeChartDataForDay(netCpuList,"网络设备 CPU TOP","IP", "利用率");
				netChartList.add(netCpupath);
			}
			netMemList = (List)netMap.get("mem");
			ArrayList<String[]> netMemal = new ArrayList<String[]>();
			String[] netMemTitle = { "ip", "当前利用率"};
			if(netMemList.size() > 0){
				netHeadList.add("网络设备内存 Top");
				netMemal.add(netMemTitle);
				for(int i=0;i<netMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netMemList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netMemal.add(arry);
				}
				netTableList.add(netMemal);
				String netMempath = makeJfreeChartDataForDay(netMemList,"网络设备 内存 TOP","IP", "利用率");
				netChartList.add(netMempath);
			}
			netPingList = (List)netMap.get("ping");
			ArrayList<String[]> netPingal = new ArrayList<String[]>();
			String[] netPingTitle = { "ip", "当前连通率"};
			if(netPingList.size() > 0){
				netHeadList.add("网络设备连通性 Top");
				netPingal.add(netPingTitle);
				for(int i=0;i<netPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netPingList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netPingal.add(arry);
				}
				netTableList.add(netPingal);
				String netPingpath = makeJfreeChartDataForDay(netPingList,"网络设备 连通性 TOP","IP", "连通率");
				netChartList.add(netPingpath);
			}
			netResponseList = (List)netMap.get("response");
			ArrayList<String[]> netResponseal = new ArrayList<String[]>();
			String[] netResponseTitle = { "ip", "响应时间"};
			if(netResponseList.size() > 0){
				netHeadList.add("网络设备响应度 Top");
				netResponseal.add(netResponseTitle);
				for(int i=0;i<netResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netResponseList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netResponseal.add(arry);
				}
				netTableList.add(netResponseal);
				String netResponsepath = makeJfreeChartDataForDay(netResponseList,"网络设备 响应度 TOP","IP", "响应时间");
				netChartList.add(netResponsepath);
			}
			netUtilInList = (List)netMap.get("utilIn");
			ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
			String[] netUtilInTitle = { "ip", "入口流速"};
			if(netUtilInList.size() > 0){
				netHeadList.add("网络设备入口流速 Top");
				netUtilInal.add(netUtilInTitle);
				for(int i=0;i<netUtilInList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilInList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netUtilInal.add(arry);
				}
				netTableList.add(netUtilInal);
				String netUtilInpath = makeJfreeChartDataForDay(netUtilInList,"网络设备 入口流速 TOP","IP", "入口流速");
				netChartList.add(netUtilInpath);
			}
			netUtilOutList = (List)netMap.get("utilOut");
			ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
			String[] netUtilOutTitle = { "ip", "出口流速"};
			if(netUtilOutList.size() > 0){
				netHeadList.add("网络设备出口流速 Top");
				netUtilOutal.add(netUtilOutTitle);
				for(int i=0;i<netUtilOutList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilOutList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netUtilOutal.add(arry);
				}
				netTableList.add(netUtilOutal);
				String netUtilOutpath = makeJfreeChartDataForDay(netUtilOutList,"网络设备 出口流速 TOP","IP", "出口流速");
				netChartList.add(netUtilOutpath);
			}
			net.put("head",netHeadList);
			net.put("table", netTableList);
			net.put("chart", netChartList);
			if(netHeadList.size()>0){
				headList2.add("网络Top分析");
				contentList2.add(net);
			}
			//进程Top分析
			HashMap pro = new HashMap();
			List proCpuList=new ArrayList();
	    	List proMemList=new ArrayList();
	    	List proTimeList=new ArrayList();
	    	
	    	HashMap proMap = reportHelper.getProValue(startTime, toTime, business);
			
			ArrayList<String> proHeadList = new ArrayList<String>();
			ArrayList<ArrayList<String[]>> proTableList = new ArrayList<ArrayList<String[]>>();//存放标题
			proCpuList = (List)proMap.get("cpu");
			ArrayList<String[]> proCpual = new ArrayList<String[]>();
			String[] proCpuTitle = { "ip", "进程名", "平均使用率"};
			if(proCpuList.size() > 0){
				proHeadList.add("CPU平均使用率 Top");
				proCpual.add(proCpuTitle);
				for(int i=0;i<proCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proCpual.add(arry);
				}
				proTableList.add(proCpual);
			}
			proMemList = (List)proMap.get("mem");
			ArrayList<String[]> proMemal = new ArrayList<String[]>();
			String[] proMemTitle = { "ip", "进程名", "平均使用率"};
			if(proMemList.size() > 0){
				proHeadList.add("内存平均使用率 Top");
				proMemal.add(proMemTitle);
				for(int i=0;i<proMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proMemList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proMemal.add(arry);
				}
				proTableList.add(proMemal);
			}
			proTimeList = (List)proMap.get("time");
			ArrayList<String[]> proTimeal = new ArrayList<String[]>();
			String[] proTimeTitle = { "ip", "进程名", "CPU时间"};
			if(proTimeList.size() > 0){
				proHeadList.add("CPU用时 Top");
				proTimeal.add(proTimeTitle);
				for(int i=0;i<proTimeList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proTimeList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proTimeal.add(arry);
				}
				proTableList.add(proTimeal);
			}
			pro.put("head", proHeadList);
			pro.put("table", proTableList);
			if(proHeadList.size()>0){
				headList2.add("进程Top分析");
				contentList2.add(pro);
			}
			
			if(business!=null){
				//峰值Top分析
				
				List topCpuList=new ArrayList();
		    	List topResList=new ArrayList();
		    	HashMap topMap = reportHelper.getTopValue(ids, startTime, toTime, business);
		    	HashMap hostTopMap = (HashMap)topMap.get("host");
		    	HashMap netTopMap = (HashMap)topMap.get("net");
		    	if(hostTopMap!=null){
		    		HashMap topValue = new HashMap();
					ArrayList<String> topHeadList = new ArrayList<String>();
					ArrayList<ArrayList<String[]>> topTableList = new ArrayList<ArrayList<String[]>>();//存放标题
					topCpuList = (List)hostTopMap.get("cpu");
					ArrayList<String[]> topCpual = new ArrayList<String[]>();
					String[] topCpuTitle = { "ip", "峰值","次数","第一次时间"};
					if(topCpuList.size() > 0){
						topHeadList.add("CPU峰值 Top");
						topCpual.add(topCpuTitle);
						for(int i=0;i<topCpuList.size();i++){
							CompreReportStatic crs = (CompreReportStatic)topCpuList.get(i);
							String[] arry = {crs.getIp(),crs.getValue()+"",crs.getType(),crs.getUnit()};
							topCpual.add(arry);
						}
						topTableList.add(topCpual);
					}
					topResList = (List)hostTopMap.get("res");
					ArrayList<String[]> topResal = new ArrayList<String[]>();
					String[] topResTitle = { "ip", "峰值","次数","第一次时间"};
					if(topResList.size() > 0){
						topHeadList.add("响应时间峰值 Top");
						topResal.add(topResTitle);
						for(int i=0;i<topResList.size();i++){
							CompreReportStatic crs = (CompreReportStatic)topResList.get(i);
							String[] arry = {crs.getIp(),crs.getValue()+"",crs.getType(),crs.getUnit()};
							topResal.add(arry);
						}
						topTableList.add(topResal);
					}
					topValue.put("head", topHeadList);
					topValue.put("table", topTableList);
					if(topHeadList.size()>0){
						headList2.add("主机峰值Top分析");
						contentList2.add(topValue);
					}
		    	}
				if(netTopMap!=null){
					HashMap topValue = new HashMap();
					ArrayList<String> topHeadList = new ArrayList<String>();
					ArrayList<ArrayList<String[]>> topTableList = new ArrayList<ArrayList<String[]>>();//存放标题
					topCpuList = (List)netTopMap.get("cpu");
					ArrayList<String[]> topCpual = new ArrayList<String[]>();
					String[] topCpuTitle = { "ip", "峰值","次数","第一次时间"};
					if(topCpuList.size() > 0){
						topHeadList.add("CPU峰值 Top");
						topCpual.add(topCpuTitle);
						for(int i=0;i<topCpuList.size();i++){
							CompreReportStatic crs = (CompreReportStatic)topCpuList.get(i);
							String[] arry = {crs.getIp(),crs.getValue()+"",crs.getType(),crs.getUnit()};
							topCpual.add(arry);
						}
						topTableList.add(topCpual);
					}
					topResList = (List)netTopMap.get("res");
					ArrayList<String[]> topResal = new ArrayList<String[]>();
					String[] topResTitle = { "ip", "峰值","次数","第一次时间"};
					if(topResList.size() > 0){
						topHeadList.add("响应时间峰值 Top");
						topResal.add(topResTitle);
						for(int i=0;i<topResList.size();i++){
							CompreReportStatic crs = (CompreReportStatic)topResList.get(i);
							String[] arry = {crs.getIp(),crs.getValue()+"",crs.getType(),crs.getUnit()};
							topResal.add(arry);
						}
						topTableList.add(topResal);
					}
					topValue.put("head", topHeadList);
					topValue.put("table", topTableList);
					if(topHeadList.size()>0){
						headList2.add("网络设备峰值Top分析");
						contentList2.add(topValue);
					}
				}
			}
			contentMap2.put("catalog", headList2);
			contentMap2.put("content", contentList2);
		contentList.add(contentMap2);
		hash.put("catalog", headList);
		hash.put("content",contentList);
		return hash;
	}
	private HashMap<?, ArrayList<?>> exportNetHostByDay1(String ids, String type,String filePath, String[][] weekDay,String business) {
		CompreReportDataCreate crc = new CompreReportDataCreate();
		
		HashMap hash = new HashMap();//总文档
		ArrayList<String> headList = new ArrayList<String>(); //1级目录
		headList.add("设备事件统计");
		headList.add("设备TOP分析");
		ArrayList<HashMap> contentList = new ArrayList<HashMap>();//1级内容List
		//设备事件统计
		HashMap contentMap1 = new HashMap();
			//设备事件统计目录
			ArrayList<String> headList1 = new ArrayList<String>(); //目录1
			headList1.add("设备故障告警统计");
			headList1.add("设备告警明细");
			headList1.add("事件告警趋势");
			contentMap1.put("catalog", headList1);
			//设备事件统计内容
			ArrayList<HashMap> contentList1 = new ArrayList<HashMap>();
				//设备故障告警统计
				HashMap contentAlarm = new HashMap();
					Map<String,String> map = crc.getLevelPieDataForMap(weekDay[0][1],weekDay[weekDay.length-1][2],business);
				contentAlarm.put("sum", Integer.valueOf(map.get("1"))+Integer.valueOf(map.get("2"))+Integer.valueOf(map.get("3"))+"");
				contentAlarm.put("level1", map.get("1"));
				contentAlarm.put("level2", map.get("2"));
				contentAlarm.put("level3", map.get("3"));
			contentList1.add(contentAlarm);
				//设备告警明细
				HashMap contentDevDet = new HashMap();
				ArrayList<ArrayList<String[]>> devTableList = new ArrayList<ArrayList<String[]>>();
					String[][] DevHost = crc.getDevTableData("host",weekDay[0][1],weekDay[weekDay.length-1][2],business);
					String[][] DevNet = crc.getDevTableData("net",weekDay[0][1],weekDay[weekDay.length-1][2],business);
					ArrayList<String[]> devHostal = new ArrayList<String[]>();
					ArrayList<String[]> devNetal = new ArrayList<String[]>();
					if(DevHost!=null&&DevHost.length>0){
						for(int i=0;i<DevHost.length;i++){
							devHostal.add(DevHost[i]);
						}
						devTableList.add(devHostal);
					}
					if(DevNet!=null&&DevNet.length>0){
						for(int i=0;i<DevNet.length;i++){
							devNetal.add(DevNet[i]);
						}
						devTableList.add(devNetal);
					}
				contentDevDet.put("table", devTableList);
			contentList1.add(contentDevDet);
				//事件告警趋势
				HashMap contentDevTra = new HashMap();
					String[][] tableData = crc.gettableData(weekDay[0][1],weekDay[weekDay.length-1][2],business);
					ArrayList<String[]> DevTra = new ArrayList<String[]>();
					for(int i=0;i<tableData.length;i++){
						DevTra.add(tableData[i]);
					}
					contentDevTra.put("table", DevTra);
					List dayHoursList = crc.getWeekAlarmData(weekDay);
					String dayHourspath = makeJfreeChartDataForDayHours(dayHoursList,"本周每天告警数","时间","条数");
				contentDevTra.put("chart", dayHourspath);
			contentList1.add(contentDevTra);
			contentMap1.put("content", contentList1);
		contentList.add(contentMap1);
		//设备TOP分析
		HashMap contentMap2 = new HashMap();
		HashMap all = reportHelper.getAllValueWeek(ids, weekDay,business);
		HashMap netMap = (HashMap)all.get("net");
    	HashMap hostMap = (HashMap)all.get("host");
			ArrayList<String> headList2 = new ArrayList<String>(); //目录2
			ArrayList<HashMap> contentList2 = new ArrayList<HashMap>(); 
			//服务器Top分析
			HashMap host = new HashMap();
			List hostPingList=new ArrayList();
	    	List hostResponseList=new ArrayList();
	    	List hostCpuList=new ArrayList();
	    	List hostMemList=new ArrayList();
	    	List hostDiskList=new ArrayList();
	    	// 存放表格信息
			ArrayList<ArrayList<String[]>> hostTableList = new ArrayList<ArrayList<String[]>>();
			// 存放图片路径
			ArrayList<String> hostChartList = new ArrayList<String>();
			//存放标题
			ArrayList<String> hostHeadList = new ArrayList<String>();
			
			hostCpuList = (List)hostMap.get("cpu");
			ArrayList<String[]> hostCpual = new ArrayList<String[]>();
			String[] hostTitle = { "ip", "平均值","最大值","最小值"};
			if(hostCpuList.size() > 0){
				hostHeadList.add("服务器CPU Top");
				hostCpual.add(hostTitle);
				for(int i=0;i<hostCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostCpuList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostCpual.add(arry);
				}
				hostTableList.add(hostCpual);
				String hostCpupath = makeJfreeChartDataForWeek(hostCpuList,"服务器 CPU TOP","时间", "利用率");
				hostChartList.add(hostCpupath);
			}
			hostMemList = (List)hostMap.get("mem");
			ArrayList<String[]> hostMemal = new ArrayList<String[]>();
			if(hostMemList.size() > 0){
				hostHeadList.add("服务器内存 Top");
				hostMemal.add(hostTitle);
				for(int i=0;i<hostMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostMemList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostMemal.add(arry);
				}
				hostTableList.add(hostMemal);
				String hostMempath = makeJfreeChartDataForWeek(hostMemList,"服务器 内存 TOP","时间", "利用率");
				hostChartList.add(hostMempath);
			}
			hostPingList = (List)hostMap.get("ping");
			ArrayList<String[]> hostPingal = new ArrayList<String[]>();
			if(hostPingList.size() > 0){
				hostHeadList.add("服务器连通性 Top");
				hostPingal.add(hostTitle);
				for(int i=0;i<hostPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostPingList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostPingal.add(arry);
				}
				hostTableList.add(hostPingal);
				String hostPingpath = makeJfreeChartDataForWeek(hostPingList,"服务器 连通性 TOP","时间", "连通率");
				hostChartList.add(hostPingpath);
			}
			hostResponseList = (List)hostMap.get("response");
			ArrayList<String[]> hostResponseal = new ArrayList<String[]>();
			if(hostResponseList.size() > 0){
				hostHeadList.add("服务器响应度 Top");
				hostResponseal.add(hostTitle);
				for(int i=0;i<hostResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostResponseList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostResponseal.add(arry);
				}
				hostTableList.add(hostResponseal);
				String hostResponsepath = makeJfreeChartDataForWeek(hostResponseList,"服务器 响应时间 TOP","时间", "响应时间");
				hostChartList.add(hostResponsepath);
			}
			hostDiskList = (List)hostMap.get("disk");
			ArrayList<String[]> hostDiskal = new ArrayList<String[]>();
			if(hostDiskList.size() > 0){
				hostHeadList.add("服务器磁盘 Top");
				hostDiskal.add(hostTitle);
				for(int i=0;i<hostDiskList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostDiskList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostDiskal.add(arry);
				}
				hostTableList.add(hostDiskal);
				String hostDiskpath = makeJfreeChartDataForWeek(hostDiskList,"服务器 磁盘 TOP","时间", "利用率");
				hostChartList.add(hostDiskpath);
			}
			host.put("head",hostHeadList);
			host.put("table", hostTableList);
			host.put("chart", hostChartList);
			if(hostHeadList.size()>0){
				headList2.add("服务器Top分析");
				contentList2.add(host);
			}
			//网络Top分析
			HashMap net = new HashMap();
			List netPingList=new ArrayList();
	    	List netResponseList=new ArrayList();
	    	List netCpuList=new ArrayList();
	    	List netMemList=new ArrayList();
			List netUtilInList=new ArrayList();
			List netUtilOutList=new ArrayList();
			// 存放表格信息
			ArrayList<ArrayList<String[]>> netTableList = new ArrayList<ArrayList<String[]>>();
			// 存放图片路径
			ArrayList<String> netChartList = new ArrayList<String>();
			//存放标题
			ArrayList<String> netHeadList = new ArrayList<String>();
			netCpuList = (List)netMap.get("cpu");
			ArrayList<String[]> netCpual = new ArrayList<String[]>();
			String[] netTitle =  { "ip", "平均值","最大值","最小值"};
			if(netCpuList.size() > 0){
				netHeadList.add("网络设备CPU Top");
				netCpual.add(netTitle);
				for(int i=0;i<netCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netCpuList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netCpual.add(arry);
				}
				netTableList.add(netCpual);
				String netCpupath = makeJfreeChartDataForWeek(netCpuList,"网络设备 CPU TOP","时间", "利用率");
				netChartList.add(netCpupath);
			}
			netMemList = (List)netMap.get("mem");
			ArrayList<String[]> netMemal = new ArrayList<String[]>();
			if(netMemList.size() > 0){
				netHeadList.add("网络设备内存 Top");
				netMemal.add(netTitle);
				for(int i=0;i<netMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netMemList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netMemal.add(arry);
				}
				netTableList.add(netMemal);
				String netMempath = makeJfreeChartDataForWeek(netMemList,"网络设备 内存 TOP","时间", "利用率");
				netChartList.add(netMempath);
			}
			netPingList = (List)netMap.get("ping");
			ArrayList<String[]> netPingal = new ArrayList<String[]>();
			if(netPingList.size() > 0){
				netHeadList.add("网络设备连通性 Top");
				netPingal.add(netTitle);
				for(int i=0;i<netPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netPingList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netPingal.add(arry);
				}
				netTableList.add(netPingal);
				String netPingpath = makeJfreeChartDataForWeek(netPingList,"网络设备 连通性 TOP","时间", "连通率");
				netChartList.add(netPingpath);
			}
			netResponseList = (List)netMap.get("response");
			ArrayList<String[]> netResponseal = new ArrayList<String[]>();
			if(netResponseList.size() > 0){
				netHeadList.add("网络设备响应度 Top");
				netResponseal.add(netTitle);
				for(int i=0;i<netResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netResponseList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netResponseal.add(arry);
				}
				netTableList.add(netResponseal);
				String netResponsepath = makeJfreeChartDataForWeek(netResponseList,"网络设备 响应度 TOP","时间", "响应时间");
				netChartList.add(netResponsepath);
			}
			netUtilInList = (List)netMap.get("utilIn");
			ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
			if(netUtilInList.size() > 0){
				netHeadList.add("网络设备入口流速 Top");
				netUtilInal.add(netTitle);
				for(int i=0;i<netUtilInList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilInList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netUtilInal.add(arry);
				}
				netTableList.add(netUtilInal);
				String netUtilInpath = makeJfreeChartDataForWeek(netUtilInList,"网络设备 入口流速 TOP","时间", "入口流速");
				netChartList.add(netUtilInpath);
			}
			netUtilOutList = (List)netMap.get("utilOut");
			ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
			if(netUtilOutList.size() > 0){
				netHeadList.add("网络设备出口流速 Top");
				netUtilOutal.add(netTitle);
				for(int i=0;i<netUtilOutList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilOutList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netUtilOutal.add(arry);
				}
				netTableList.add(netUtilOutal);
				String netUtilOutpath = makeJfreeChartDataForWeek(netUtilOutList,"网络设备 出口流速 TOP","时间", "出口流速");
				netChartList.add(netUtilOutpath);
			}
			net.put("head",netHeadList);
			net.put("table", netTableList);
			net.put("chart", netChartList);
			if(netHeadList.size()>0){
				headList2.add("网络Top分析");
				contentList2.add(net);
			}
			//进程Top分析
			HashMap pro = new HashMap();
			List proCpuList=new ArrayList();
	    	List proMemList=new ArrayList();
	    	List proTimeList=new ArrayList();
	    	HashMap proMap = reportHelper.getProWeekValue(weekDay,business);
			
			ArrayList<String> proHeadList = new ArrayList<String>();
			ArrayList<ArrayList<String[]>> proTableList = new ArrayList<ArrayList<String[]>>();//存放标题
			ArrayList<String> proChartList = new ArrayList<String>();
			proCpuList = (List)proMap.get("cpu");
			ArrayList<String[]> proCpual = new ArrayList<String[]>();
			String[] proCpuTitle = { "ip", "进程名", "平均使用率"};
			if(proCpuList.size() > 0){
				proHeadList.add("CPU平均使用率 Top");
				proCpual.add(proCpuTitle);
				for(int i=0;i<proCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proCpual.add(arry);
				}
				proTableList.add(proCpual);
				String proCpuPath = makeJfreeChartDataForWeekByPro(proCpuList,"进程Top Cpu","时间","使用率");
				proChartList.add(proCpuPath);
			}
			proMemList = (List)proMap.get("mem");
			ArrayList<String[]> proMemal = new ArrayList<String[]>();
			String[] proMemTitle = { "ip", "进程名", "平均使用率"};
			if(proMemList.size() > 0){
				proHeadList.add("内存平均使用率 Top");
				proMemal.add(proMemTitle);
				for(int i=0;i<proMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proMemList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proMemal.add(arry);
				}
				proTableList.add(proMemal);
				String proMemoryPath = makeJfreeChartDataForWeekByPro(proMemList,"进程Top 内存","时间","使用率");
				proChartList.add(proMemoryPath);
			}
			proTimeList = (List)proMap.get("time");
			ArrayList<String[]> proTimeal = new ArrayList<String[]>();
			String[] proTimeTitle = { "ip", "进程名", "CPU时间"};
			if(proTimeList.size() > 0){
				proHeadList.add("CPU用时 Top");
				proTimeal.add(proTimeTitle);
				for(int i=0;i<proTimeList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proTimeList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proTimeal.add(arry);
				}
				proTableList.add(proTimeal);
				String proTimePath = makeJfreeChartDataForWeekByPro(proTimeList,"进程Top 用时","时间","使用率");
				proChartList.add(proTimePath);
			}
			pro.put("head", proHeadList);
			pro.put("table", proTableList);
			pro.put("chart", proChartList);
			if(proHeadList.size()>0){
				headList2.add("进程Top分析");
				contentList2.add(pro);
			}
			contentMap2.put("catalog", headList2);
			contentMap2.put("content", contentList2);
		contentList.add(contentMap2);
		hash.put("catalog", headList);
		hash.put("content",contentList);
		return hash;
	}
	private String makeJfreeChartDataForDay(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CompreReportStatic crs = (CompreReportStatic)list.get(j);
				dataset.addValue(crs.getValue(), crs.getType(), crs.getIp());
			}
			String chartkey = ChartCreator.createStackeBarChart(dataset, xdesc, ydesc, title, chartWith, chartHigh);
			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
					.getInstance().getChartStorage().get(chartkey);
			CategoryPlot plot = chart.getChart().getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
			StackedBarRenderer renderer = new StackedBarRenderer();
			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			renderer.setSeriesPaint(0, new Color(0, 255, 255));
			renderer.setItemLabelsVisible(true);
			renderer.setBaseItemLabelsVisible(false);
			plot.setRenderer(renderer);
			FileOutputStream fos = null;
			try {
				File f = new File(chartPath);
				if (!f.exists()) {
					f.createNewFile();
				}
				fos = new FileOutputStream(f);
				ChartUtilities.writeChartAsPNG(fos, chart.getChart(), chart
						.getWidth(), chart.getHeight());
				fos.flush();
			} catch (IOException ioe) {
				chartPath = "";
				SysLogger.error("", ioe);
			} finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return chartPath;
	}
	private String makeJfreeChartDataForDayHours(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
		+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (list != null && list.size() > 0) {
		for (int j = 0; j < list.size(); j++) {
			CompreReportStatic crs = (CompreReportStatic)list.get(j);
			dataset.addValue(crs.getValue(), crs.getType(), crs.getIp());
		}
		String chartkey = ChartCreator.createLineChart(title, xdesc, ydesc, dataset, chartWith, chartHigh);
		JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
				.getInstance().getChartStorage().get(chartkey);
		CategoryPlot plot = chart.getChart().getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		LineAndShapeRenderer renderer = new LineAndShapeRenderer();
		renderer.setSeriesPaint(0, new Color(0, 255, 255));
		renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setItemLabelsVisible(true);
		plot.setRenderer(renderer);
		FileOutputStream fos = null;
		try {
			File f = new File(chartPath);
			if (!f.exists()) {
				f.createNewFile();
			}
			fos = new FileOutputStream(f);
			ChartUtilities.writeChartAsPNG(fos, chart.getChart(), chart
					.getWidth(), chart.getHeight());
			fos.flush();
		} catch (IOException ioe) {
			chartPath = "";
			SysLogger.error("", ioe);
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	return chartPath;
	}
	
//	private String makeJfreeChartDataForWeek(List list,String title, String xdesc, String ydesc){
//		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
//			+ File.separator + System.currentTimeMillis() + ".png";
//		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		if (list != null && list.size() > 0) {
//			for (int j = 0; j < list.size(); j++) {
//				CompreReportStatic crs = (CompreReportStatic)list.get(j);
//				Map<String,Double> maps = crs.getWeekValues();
//				if(maps.size()>0){
//					Set<Map.Entry<String, Double>> set = maps.entrySet();
//					for (Iterator<Map.Entry<String,Double>> it = set.iterator(); it.hasNext();) {
//						Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
//						dataset.addValue(entry.getValue(),crs.getIp(),entry.getKey());
//					}
//				}else{
//					dataset.addValue(crs.getValue(), crs.getType(), crs.getIp());
//				}
//			}
//			String chartkey = ChartCreator.createLineChart(title,xdesc,ydesc,dataset,chartWith,chartHigh);
//			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
//					.getInstance().getChartStorage().get(chartkey);
//			
//			FileOutputStream fos = null;
//			try {
//				File f = new File(chartPath);
//				if (!f.exists()) {
//					f.createNewFile();
//				}
//				fos = new FileOutputStream(f);
//				ChartUtilities.writeChartAsPNG(fos, chart.getChart(), chart
//						.getWidth(), chart.getHeight());
//				fos.flush();
//			} catch (IOException ioe) {
//				chartPath = "";
//				SysLogger.error("", ioe);
//			} finally{
//				if(fos != null){
//					try {
//						fos.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return chartPath;
//	}
	private String makeJfreeChartDataForWeek(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		TimeSeriesCollection timedataset = new TimeSeriesCollection();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CompreReportStatic crs = (CompreReportStatic)list.get(j);
				Map<String,Double> maps = crs.getWeekValues();
				TimeSeries series = new TimeSeries(crs.getIp(),Hour.class);
				if(maps.size()>0){
					Set<Map.Entry<String, Double>> set = maps.entrySet();
					for (Iterator<Map.Entry<String,Double>> it = set.iterator(); it.hasNext();) {
						Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
						String dt = (String)entry.getKey();//yyyy-mm-dd-HH
						int year = Integer.parseInt(dt.substring(0,4));  
						int month = Integer.parseInt(dt.substring(5, 7));
						int day = Integer.parseInt(dt.substring(8,10));
						int hour = Integer.parseInt(dt.substring(11,13));
			   			Hour h = new Hour(hour,day,month,year);
			   			series.add(h,entry.getValue());  
					}
				}
				timedataset.addSeries(series);
			}
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
		               title /*题目*/, xdesc /*X坐标描述*/, ydesc /*Y坐标描述*/,timedataset,true,false,false);

			chart.setTitle(new TextTitle(title,new java.awt.Font("宋体", 1, 13)));
			  
			chart.setBackgroundPaint(Color.white);
			XYPlot xyplot = chart.getXYPlot();
			xyplot.setBackgroundPaint(Color.white);  
			xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
			
		    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		    //这儿要进行选择 分日、月、年、小时
		    dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,6));
	        dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH"));
	        
	        dateaxis.setVerticalTickLabels(true);	        
			NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
			numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
			
	        //对各条线进行着色			
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
			xylineandshaperenderer.setShapesVisible(true);
	        xylineandshaperenderer.setShapesFilled(true);
	        LegendTitle legend = chart.getLegend();
	        legend.setItemFont(new java.awt.Font("Verdena", 0, 9));
			
			
			FileOutputStream fos = null;
			try {
				File f = new File(chartPath);
				if (!f.exists()) {
					f.createNewFile();
				}
				fos = new FileOutputStream(f);
				ChartUtilities.writeChartAsPNG(fos, chart,chartWith,chartHigh);
				fos.flush();
			} catch (IOException ioe) {
				chartPath = "";
				SysLogger.error("", ioe);
			} finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return chartPath;
	}
	private String makeJfreeChartDataForWeekByPro(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		TimeSeriesCollection timedataset = new TimeSeriesCollection();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CompreReportStatic crs = (CompreReportStatic)list.get(j);
				Map<String,Double> maps = crs.getWeekValues();
				TimeSeries series = new TimeSeries(crs.getIp()+"-"+crs.getType(),Hour.class);
				if(maps.size()>0){
					Set<Map.Entry<String, Double>> set = maps.entrySet();
					for (Iterator<Map.Entry<String,Double>> it = set.iterator(); it.hasNext();) {
						Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
						String dt = (String)entry.getKey();//yyyy-mm-dd-HH
						int year = Integer.parseInt(dt.substring(0,4));  
						int month = Integer.parseInt(dt.substring(5, 7));
						int day = Integer.parseInt(dt.substring(8,10));
						int hour = Integer.parseInt(dt.substring(11,13));
			   			Hour h = new Hour(hour,day,month,year);
			   			series.add(h,entry.getValue());  
					}
				}
				timedataset.addSeries(series);
			}
			JFreeChart chart = ChartFactory.createTimeSeriesChart(
		               title /*题目*/, xdesc /*X坐标描述*/, ydesc /*Y坐标描述*/,timedataset,true,false,false);

			chart.setTitle(new TextTitle(title,new java.awt.Font("宋体", 1, 13)));
			  
			chart.setBackgroundPaint(Color.white);
			XYPlot xyplot = chart.getXYPlot();
			xyplot.setBackgroundPaint(Color.white);  
			xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
			
		    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		    //这儿要进行选择 分日、月、年、小时
		    dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,6));
	        dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH"));
	        
	        dateaxis.setVerticalTickLabels(true);	        
			NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
			numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
			
	        //对各条线进行着色			
			XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
			xylineandshaperenderer.setShapesVisible(true);
	        xylineandshaperenderer.setShapesFilled(true);
	        LegendTitle legend = chart.getLegend();
	        legend.setItemFont(new java.awt.Font("Verdena", 0, 9));
			
			
			FileOutputStream fos = null;
			try {
				File f = new File(chartPath);
				if (!f.exists()) {
					f.createNewFile();
				}
				fos = new FileOutputStream(f);
				ChartUtilities.writeChartAsPNG(fos, chart,chartWith,chartHigh);
				fos.flush();
			} catch (IOException ioe) {
				chartPath = "";
				SysLogger.error("", ioe);
			} finally{
				if(fos != null){
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return chartPath;
	}
}
