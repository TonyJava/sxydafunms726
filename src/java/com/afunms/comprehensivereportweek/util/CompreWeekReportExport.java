package com.afunms.comprehensivereportweek.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereport.util.CompreReportStatic;
import com.afunms.comprehensivereportweek.report.CompreExcel;
import com.afunms.comprehensivereportweek.report.CompreExportInterface;
import com.afunms.comprehensivereportweek.report.ComprePdf;
import com.afunms.comprehensivereportweek.report.CompreWord;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.DocumentException;

/**
 * @author Admin
 * 
 */
public class CompreWeekReportExport {
	private final int xlabel = 12;

	private CompreWeekReportHelper reportHelper = null;

	private int chartWith = 768;

	private int chartHigh = 338;
	
	/**
	 * ��������
	 * 
	 * @param ids
	 *            ָ��id
	 * @param type
	 *            ����
	 * @param filePath
	 *            ����·��
	 * @param startTime
	 *            ��ʼʱ��
	 * @param toTime
	 *            ����ʱ��
	 * @param exportType
	 *            �����ļ�����
	 */
	public void exportReportByWeek(String ids, String type, String filePath,
			String startTime, String toTime, String exportType, List hostEventList) {
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = null;
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		
		reportHelper = new CompreWeekReportHelper();
		String title = "����";
		if ("hostNet".equalsIgnoreCase(type)) {
			// �����豸��������
			title = "�豸������Ϣ�ܱ���";
			
			//��ȡ�������ݷ���
//			hm = exportNetHostByWeek(ids, type, filePath, startTime, toTime);
			
			tableList = (ArrayList<ArrayList<String[]>>) hm.get("table");
			chartList = (ArrayList<String>) hm.get("chart");
		}
		String time = startTime + "~" + toTime;
		CompreExportInterface export = null;
		
		//�ж����ɱ���
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
		export(export, tableList, chartList, title, time);
		if (chartList != null) {
			for (int i = 0; i < chartList.size(); i++) {
				String chartal = chartList.get(i);
				try {
					File f = new File(chartal);
					f.delete();
				} catch (Exception e) {
					SysLogger.error("ɾ��ͼƬ��" + chartal + "ʧ�ܣ�", e);
				}
			}
		}
	}

	/**
	 * ����
	 * 
	 * @param export
	 * @param tableList
	 * @param chartList
	 * @param title
	 * @param time
	 */
	private void export(CompreExportInterface export,
			ArrayList<ArrayList<String[]>> tableList,
			ArrayList<String> chartList, String title, String time) {
		if (tableList != null) {
			if (tableList.get(0) != null) {
				try {
					export.insertTitle(title, tableList.get(0).get(0).length,
							time);
				} catch (Exception e) {
					SysLogger.error("", e);
				}
			} else {
				try {
					export.insertTitle(title, 0, time);
				} catch (Exception e) {
					SysLogger.error("", e);
				}
			}
			for (int i = 0; i < tableList.size(); i++) {
				String chartal = chartList.get(i);
				if (chartal != null && !"".equals(chartal.trim())) {
					try {
						export.insertChart(chartal);
					} catch (MalformedURLException e) {
						SysLogger.error("", e);
					} catch (IOException e) {
						SysLogger.error("", e);
					} catch (DocumentException e) {
						SysLogger.error("", e);
					} catch (Exception e) {
						SysLogger.error("", e);
					}
				}
				ArrayList<String[]> tableal = tableList.get(i);
				if (tableal != null && tableal.size() > 0) {
					try {
						export.insertTable(tableal);
					} catch (IOException e) {
						SysLogger.error("", e);
					} catch (Exception e) {
						SysLogger.error("", e);
					}
				}
			}
			try {
				export.save();
			} catch (Exception e) {
				SysLogger.error("------�����ļ�����ʧ�ܣ�------", e);
			}
		}
	}
	/**
	 * ��ȡ��������
	 * @param ids
	 * @param type
	 * @param filePath
	 * @param startTime
	 * @param toTime
	 * @return
	 */
//	private HashMap<?, ArrayList<?>> exportNetHostByWeek(String ids, String type,
//			String filePath, String startTime, String toTime) {
//		HashMap hm = new HashMap();
//		// ��ű����Ϣ
//		ArrayList<ArrayList<String[]>> tableList = new ArrayList<ArrayList<String[]>>();
//		// ���ͼƬ·��
//		ArrayList<String> chartList = new ArrayList<String>();
//		// �����豸��������
//		HashMap all = reportHelper.getAllValue(ids, startTime, toTime);
//		HashMap netMap = (HashMap)all.get("net");
//    	HashMap hostMap = (HashMap)all.get("host");
//    	
//    	List hostPingList=new ArrayList();
//    	List hostResponseList=new ArrayList();
//    	List hostCpuList=new ArrayList();
//    	List hostMemList=new ArrayList();
//    	List hostDiskList=new ArrayList();
//    	List netPingList=new ArrayList();
//    	List netResponseList=new ArrayList();
//    	List netCpuList=new ArrayList();
//    	List netMemList=new ArrayList();
//		List netUtilInList=new ArrayList();
//		List netUtilOutList=new ArrayList();
//		
//		hostCpuList = (List)hostMap.get("cpu");
//		ArrayList<String[]> hostCpual = new ArrayList<String[]>();
//		String[] hostCpuTitle = { "ip", "��ǰ������"};
//		if(hostCpuList.size() > 0){
//			hostCpual.add(hostCpuTitle);
//			for(int i=0;i<hostCpuList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)hostCpuList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				hostCpual.add(arry);
//			}
//			tableList.add(hostCpual);
//			String hostCpupath = makeJfreeChartDataForDay(hostCpuList,"������ CPU TOP","IP", "������");
//			chartList.add(hostCpupath);
//		}
//		hostMemList = (List)hostMap.get("mem");
//		ArrayList<String[]> hostMemal = new ArrayList<String[]>();
//		String[] hostMemTitle = { "ip", "��ǰ������"};
//		if(hostMemList.size() > 0){
//			hostMemal.add(hostMemTitle);
//			for(int i=0;i<hostMemList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)hostMemList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				hostMemal.add(arry);
//			}
//			tableList.add(hostMemal);
//			String hostMempath = makeJfreeChartDataForDay(hostMemList,"������ �ڴ� TOP","IP", "������");
//			chartList.add(hostMempath);
//		}
//		hostPingList = (List)hostMap.get("ping");
//		ArrayList<String[]> hostPingal = new ArrayList<String[]>();
//		String[] hostPingTitle = { "ip", "��ǰ��ͨ��"};
//		if(hostPingList.size() > 0){
//			hostPingal.add(hostPingTitle);
//			for(int i=0;i<hostPingList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)hostPingList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				hostPingal.add(arry);
//			}
//			tableList.add(hostPingal);
//			String hostPingpath = makeJfreeChartDataForDay(hostPingList,"������ ��ͨ�� TOP","IP", "��ͨ��");
//			chartList.add(hostPingpath);
//		}
//		hostResponseList = (List)hostMap.get("response");
//		ArrayList<String[]> hostResponseal = new ArrayList<String[]>();
//		String[] hostResponseTitle = { "ip", "��Ӧʱ��"};
//		if(hostResponseList.size() > 0){
//			hostResponseal.add(hostResponseTitle);
//			for(int i=0;i<hostResponseList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)hostResponseList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				hostResponseal.add(arry);
//			}
//			tableList.add(hostResponseal);
//			String hostResponsepath = makeJfreeChartDataForDay(hostResponseList,"������ ��Ӧʱ�� TOP","IP", "��Ӧʱ��");
//			chartList.add(hostResponsepath);
//		}
//		hostDiskList = (List)hostMap.get("disk");
//		ArrayList<String[]> hostDiskal = new ArrayList<String[]>();
//		String[] hostDiskTitle = { "ip", "��ǰ������"};
//		if(hostDiskList.size() > 0){
//			hostDiskal.add(hostDiskTitle);
//			for(int i=0;i<hostDiskList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)hostDiskList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				hostDiskal.add(arry);
//			}
//			tableList.add(hostDiskal);
//			String hostDiskpath = makeJfreeChartDataForDay(hostDiskList,"������ ���� TOP","IP", "������");
//			chartList.add(hostDiskpath);
//		}
//			
//		netCpuList = (List)netMap.get("cpu");
//		ArrayList<String[]> netCpual = new ArrayList<String[]>();
//		String[] netCpuTitle = { "ip", "��ǰ������"};
//		if(netCpuList.size() > 0){
//			netCpual.add(netCpuTitle);
//			for(int i=0;i<netCpuList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netCpuList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netCpual.add(arry);
//			}
//			tableList.add(netCpual);
//			String netCpupath = makeJfreeChartDataForDay(netCpuList,"�����豸 CPU TOP","IP", "������");
//			chartList.add(netCpupath);
//		}
//		netMemList = (List)netMap.get("mem");
//		ArrayList<String[]> netMemal = new ArrayList<String[]>();
//		String[] netMemTitle = { "ip", "��ǰ������"};
//		if(netMemList.size() > 0){
//			netMemal.add(netMemTitle);
//			for(int i=0;i<netMemList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netMemList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netMemal.add(arry);
//			}
//			tableList.add(netMemal);
//			String netMempath = makeJfreeChartDataForDay(netMemList,"�����豸 �ڴ� TOP","IP", "������");
//			chartList.add(netMempath);
//		}
//		netPingList = (List)netMap.get("ping");
//		ArrayList<String[]> netPingal = new ArrayList<String[]>();
//		String[] netPingTitle = { "ip", "��ǰ��ͨ��"};
//		if(netPingList.size() > 0){
//			netPingal.add(netPingTitle);
//			for(int i=0;i<netPingList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netPingList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netPingal.add(arry);
//			}
//			tableList.add(netPingal);
//			String netPingpath = makeJfreeChartDataForDay(netPingList,"�����豸 ��ͨ�� TOP","IP", "��ͨ��");
//			chartList.add(netPingpath);
//		}
//		netResponseList = (List)netMap.get("response");
//		ArrayList<String[]> netResponseal = new ArrayList<String[]>();
//		String[] netResponseTitle = { "ip", "��Ӧʱ��"};
//		if(netResponseList.size() > 0){
//			netResponseal.add(netResponseTitle);
//			for(int i=0;i<netResponseList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netResponseList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netResponseal.add(arry);
//			}
//			tableList.add(netResponseal);
//			String netResponsepath = makeJfreeChartDataForDay(netResponseList,"�����豸 ��Ӧ�� TOP","IP", "��Ӧʱ��");
//			chartList.add(netResponsepath);
//		}
//		netUtilInList = (List)netMap.get("utilIn");
//		ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
//		String[] netUtilInTitle = { "ip", "�������"};
//		if(netUtilInList.size() > 0){
//			netUtilInal.add(netUtilInTitle);
//			for(int i=0;i<netUtilInList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netUtilInList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netUtilInal.add(arry);
//			}
//			tableList.add(netUtilInal);
//			String netUtilInpath = makeJfreeChartDataForDay(netUtilInList,"�����豸 ������� TOP","IP", "�������");
//			chartList.add(netUtilInpath);
//		}
//		netUtilOutList = (List)netMap.get("utilOut");
//		ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
//		String[] netUtilOutTitle = { "ip", "��������"};
//		if(netUtilOutList.size() > 0){
//			netUtilOutal.add(netUtilOutTitle);
//			for(int i=0;i<netUtilOutList.size();i++){
//				CompreReportStatic crs = (CompreReportStatic)netUtilOutList.get(i);
//				String[] arry = {crs.getIp(),crs.getValue()+""};
//				netUtilOutal.add(arry);
//			}
//			tableList.add(netUtilOutal);
//			String netUtilOutpath = makeJfreeChartDataForDay(netUtilOutList,"�����豸 �������� TOP","IP", "��������");
//			chartList.add(netUtilOutpath);
//		}
//		hm.put("table", tableList);
//		hm.put("chart", chartList);
//		return hm;
//	}
	
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
	private String makeJfreeChartDataForWeek(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CompreReportStatic crs = (CompreReportStatic)list.get(j);
				Map<String,Double> maps = crs.getWeekValues();
				if(maps.size()>0){
					Set<Map.Entry<String, Double>> set = maps.entrySet();
					for (Iterator<Map.Entry<String,Double>> it = set.iterator(); it.hasNext();) {
						Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
						dataset.addValue(entry.getValue(),crs.getIp(),entry.getKey());
					}
				}else{
					dataset.addValue(crs.getValue(), crs.getType(), crs.getIp());
				}
			}
			String chartkey = ChartCreator.createLineChart(title,xdesc,ydesc,dataset,chartWith,chartHigh);
			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
					.getInstance().getChartStorage().get(chartkey);
			CategoryPlot plot = chart.getChart().getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
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
}
