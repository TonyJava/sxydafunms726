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
	 * @throws Exception 
	 */
	public void exportReportByDay(String ids, String type, String reportType,String filePath,
			String startTime, String toTime, String exportType,String business){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = null;
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// ��ű���
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new CompreReportHelper();
		String title = "����";
		if ("hostNet".equalsIgnoreCase(type)) {
			// �����豸��������
			if("day".equals(reportType)){
				title = "�豸������Ϣ�ձ���";
			}else if("busi".equals(reportType)){
				title = "�豸������Ϣ�ձ���(ҵ��)";
			}else if("week".equals(reportType)){
				title = "�豸������Ϣ�ܱ���";
			}else if("weekBusi".equals(reportType)){
				title = "�豸������Ϣ�ܱ���(ҵ��)";
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
					SysLogger.error("ɾ��ͼƬ��" + chartal + "ʧ�ܣ�", e);
				}
			}
		}
	}
	
	public void exportReportByDay(String ids, String type, String reportType,String filePath,
			String[][] weekDay,String exportType,String business){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = null;
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// ��ű���
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new CompreReportHelper();
		String title = "����";
		if ("hostNet".equalsIgnoreCase(type)) {
			// �����豸��������
			if("day".equals(reportType)){
				title = "�豸������Ϣ�ձ���";
			}else if("busi".equals(reportType)){
				title = "�豸������Ϣ�ձ���(ҵ��)";
			}else if("week".equals(reportType)){
				title = "�豸������Ϣ�ܱ���";
			}else if("weekBusi".equals(reportType)){
				title = "�豸������Ϣ�ܱ���(ҵ��)";
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
							export.insertContent("  �¼�����"+contentAlarm.get("sum")+"��,����:��ͨ�澯"+contentAlarm.get("level1")+
									"��,���ظ澯"+contentAlarm.get("level2")+"��,�����澯"+contentAlarm.get("level3")+"��.",12,Font.NORMAL,Element.ALIGN_BASELINE);
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
				SysLogger.error("------�����ļ�����ʧ�ܣ�------", e);
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
		
		HashMap hash = new HashMap();//���ĵ�
		ArrayList<String> headList = new ArrayList<String>(); //1��Ŀ¼
		headList.add("�豸�¼�ͳ��");
		headList.add("�豸TOP����");
		ArrayList<HashMap> contentList = new ArrayList<HashMap>();//1������List
		//�豸�¼�ͳ��
		HashMap contentMap1 = new HashMap();
			//�豸�¼�ͳ��Ŀ¼
			ArrayList<String> headList1 = new ArrayList<String>(); //Ŀ¼1
			headList1.add("�豸���ϸ澯ͳ��");
			headList1.add("�豸�澯��ϸ");
			headList1.add("�¼��澯����");
			contentMap1.put("catalog", headList1);
			//�豸�¼�ͳ������
			ArrayList<HashMap> contentList1 = new ArrayList<HashMap>();
				//�豸���ϸ澯ͳ��
				HashMap contentAlarm = new HashMap();
					Map<String,String> map = crc.getLevelPieDataForMap(startTime,toTime,business);
				contentAlarm.put("sum", Integer.valueOf(map.get("1"))+Integer.valueOf(map.get("2"))+Integer.valueOf(map.get("3"))+"");
				contentAlarm.put("level1", map.get("1"));
				contentAlarm.put("level2", map.get("2"));
				contentAlarm.put("level3", map.get("3"));
			contentList1.add(contentAlarm);
				//�豸�澯��ϸ
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
				//�¼��澯����
				HashMap contentDevTra = new HashMap();
					String[][] tableData = crc.gettableData(startTime,toTime,business);
					ArrayList<String[]> DevTra = new ArrayList<String[]>();
					for(int i=0;i<tableData.length;i++){
						DevTra.add(tableData[i]);
					}
					contentDevTra.put("table", DevTra);
					List dayHoursList = crc.getDayAlarmDataForList(startTime,toTime,business);
					String dayHourspath = makeJfreeChartDataForDayHours(dayHoursList,"����ÿСʱ�澯��","ʱ��","����");
				contentDevTra.put("chart", dayHourspath);
			contentList1.add(contentDevTra);
			contentMap1.put("content", contentList1);
		contentList.add(contentMap1);
		//�豸TOP����
		HashMap contentMap2 = new HashMap();
		HashMap all = reportHelper.getAllValue(ids, startTime, toTime,business);
		HashMap netMap = (HashMap)all.get("net");
    	HashMap hostMap = (HashMap)all.get("host");
			ArrayList<String> headList2 = new ArrayList<String>(); //Ŀ¼2
			ArrayList<HashMap> contentList2 = new ArrayList<HashMap>(); 
			//������Top����
			HashMap host = new HashMap();
			List hostPingList=new ArrayList();
	    	List hostResponseList=new ArrayList();
	    	List hostCpuList=new ArrayList();
	    	List hostMemList=new ArrayList();
	    	List hostDiskList=new ArrayList();
	    	// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> hostTableList = new ArrayList<ArrayList<String[]>>();
			// ���ͼƬ·��
			ArrayList<String> hostChartList = new ArrayList<String>();
			//��ű���
			ArrayList<String> hostHeadList = new ArrayList<String>();
			
			hostCpuList = (List)hostMap.get("cpu");
			ArrayList<String[]> hostCpual = new ArrayList<String[]>();
			String[] hostCpuTitle = { "ip", "��ǰ������"};
			if(hostCpuList.size() > 0){
				hostHeadList.add("������CPU Top");
				hostCpual.add(hostCpuTitle);
				for(int i=0;i<hostCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostCpual.add(arry);
				}
				hostTableList.add(hostCpual);
				String hostCpupath = makeJfreeChartDataForDay(hostCpuList,"������ CPU TOP","IP", "������");
				hostChartList.add(hostCpupath);
			}
			hostMemList = (List)hostMap.get("mem");
			ArrayList<String[]> hostMemal = new ArrayList<String[]>();
			String[] hostMemTitle = { "ip", "��ǰ������"};
			if(hostMemList.size() > 0){
				hostHeadList.add("�������ڴ� Top");
				hostMemal.add(hostMemTitle);
				for(int i=0;i<hostMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostMemList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostMemal.add(arry);
				}
				hostTableList.add(hostMemal);
				String hostMempath = makeJfreeChartDataForDay(hostMemList,"������ �ڴ� TOP","IP", "������");
				hostChartList.add(hostMempath);
			}
			hostPingList = (List)hostMap.get("ping");
			ArrayList<String[]> hostPingal = new ArrayList<String[]>();
			String[] hostPingTitle = { "ip", "��ǰ��ͨ��"};
			if(hostPingList.size() > 0){
				hostHeadList.add("��������ͨ�� Top");
				hostPingal.add(hostPingTitle);
				for(int i=0;i<hostPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostPingList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostPingal.add(arry);
				}
				hostTableList.add(hostPingal);
				String hostPingpath = makeJfreeChartDataForDay(hostPingList,"������ ��ͨ�� TOP","IP", "��ͨ��");
				hostChartList.add(hostPingpath);
			}
			hostResponseList = (List)hostMap.get("response");
			ArrayList<String[]> hostResponseal = new ArrayList<String[]>();
			String[] hostResponseTitle = { "ip", "��Ӧʱ��"};
			if(hostResponseList.size() > 0){
				hostHeadList.add("��������Ӧ�� Top");
				hostResponseal.add(hostResponseTitle);
				for(int i=0;i<hostResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostResponseList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostResponseal.add(arry);
				}
				hostTableList.add(hostResponseal);
				String hostResponsepath = makeJfreeChartDataForDay(hostResponseList,"������ ��Ӧʱ�� TOP","IP", "��Ӧʱ��");
				hostChartList.add(hostResponsepath);
			}
			hostDiskList = (List)hostMap.get("disk");
			ArrayList<String[]> hostDiskal = new ArrayList<String[]>();
			String[] hostDiskTitle = { "ip", "��ǰ������"};
			if(hostDiskList.size() > 0){
				hostHeadList.add("���������� Top");
				hostDiskal.add(hostDiskTitle);
				for(int i=0;i<hostDiskList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostDiskList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					hostDiskal.add(arry);
				}
				hostTableList.add(hostDiskal);
				String hostDiskpath = makeJfreeChartDataForDay(hostDiskList,"������ ���� TOP","IP", "������");
				hostChartList.add(hostDiskpath);
			}
			host.put("head",hostHeadList);
			host.put("table", hostTableList);
			host.put("chart", hostChartList);
			if(hostHeadList.size()>0){
				headList2.add("������Top����");
				contentList2.add(host);
			}
			//����Top����
			HashMap net = new HashMap();
			List netPingList=new ArrayList();
	    	List netResponseList=new ArrayList();
	    	List netCpuList=new ArrayList();
	    	List netMemList=new ArrayList();
			List netUtilInList=new ArrayList();
			List netUtilOutList=new ArrayList();
			// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> netTableList = new ArrayList<ArrayList<String[]>>();
			// ���ͼƬ·��
			ArrayList<String> netChartList = new ArrayList<String>();
			//��ű���
			ArrayList<String> netHeadList = new ArrayList<String>();
			netCpuList = (List)netMap.get("cpu");
			ArrayList<String[]> netCpual = new ArrayList<String[]>();
			String[] netCpuTitle = { "ip", "��ǰ������"};
			if(netCpuList.size() > 0){
				netHeadList.add("�����豸CPU Top");
				netCpual.add(netCpuTitle);
				for(int i=0;i<netCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netCpual.add(arry);
				}
				netTableList.add(netCpual);
				String netCpupath = makeJfreeChartDataForDay(netCpuList,"�����豸 CPU TOP","IP", "������");
				netChartList.add(netCpupath);
			}
			netMemList = (List)netMap.get("mem");
			ArrayList<String[]> netMemal = new ArrayList<String[]>();
			String[] netMemTitle = { "ip", "��ǰ������"};
			if(netMemList.size() > 0){
				netHeadList.add("�����豸�ڴ� Top");
				netMemal.add(netMemTitle);
				for(int i=0;i<netMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netMemList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netMemal.add(arry);
				}
				netTableList.add(netMemal);
				String netMempath = makeJfreeChartDataForDay(netMemList,"�����豸 �ڴ� TOP","IP", "������");
				netChartList.add(netMempath);
			}
			netPingList = (List)netMap.get("ping");
			ArrayList<String[]> netPingal = new ArrayList<String[]>();
			String[] netPingTitle = { "ip", "��ǰ��ͨ��"};
			if(netPingList.size() > 0){
				netHeadList.add("�����豸��ͨ�� Top");
				netPingal.add(netPingTitle);
				for(int i=0;i<netPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netPingList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netPingal.add(arry);
				}
				netTableList.add(netPingal);
				String netPingpath = makeJfreeChartDataForDay(netPingList,"�����豸 ��ͨ�� TOP","IP", "��ͨ��");
				netChartList.add(netPingpath);
			}
			netResponseList = (List)netMap.get("response");
			ArrayList<String[]> netResponseal = new ArrayList<String[]>();
			String[] netResponseTitle = { "ip", "��Ӧʱ��"};
			if(netResponseList.size() > 0){
				netHeadList.add("�����豸��Ӧ�� Top");
				netResponseal.add(netResponseTitle);
				for(int i=0;i<netResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netResponseList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netResponseal.add(arry);
				}
				netTableList.add(netResponseal);
				String netResponsepath = makeJfreeChartDataForDay(netResponseList,"�����豸 ��Ӧ�� TOP","IP", "��Ӧʱ��");
				netChartList.add(netResponsepath);
			}
			netUtilInList = (List)netMap.get("utilIn");
			ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
			String[] netUtilInTitle = { "ip", "�������"};
			if(netUtilInList.size() > 0){
				netHeadList.add("�����豸������� Top");
				netUtilInal.add(netUtilInTitle);
				for(int i=0;i<netUtilInList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilInList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netUtilInal.add(arry);
				}
				netTableList.add(netUtilInal);
				String netUtilInpath = makeJfreeChartDataForDay(netUtilInList,"�����豸 ������� TOP","IP", "�������");
				netChartList.add(netUtilInpath);
			}
			netUtilOutList = (List)netMap.get("utilOut");
			ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
			String[] netUtilOutTitle = { "ip", "��������"};
			if(netUtilOutList.size() > 0){
				netHeadList.add("�����豸�������� Top");
				netUtilOutal.add(netUtilOutTitle);
				for(int i=0;i<netUtilOutList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilOutList.get(i);
					String[] arry = {crs.getIp(),crs.getValue()+""};
					netUtilOutal.add(arry);
				}
				netTableList.add(netUtilOutal);
				String netUtilOutpath = makeJfreeChartDataForDay(netUtilOutList,"�����豸 �������� TOP","IP", "��������");
				netChartList.add(netUtilOutpath);
			}
			net.put("head",netHeadList);
			net.put("table", netTableList);
			net.put("chart", netChartList);
			if(netHeadList.size()>0){
				headList2.add("����Top����");
				contentList2.add(net);
			}
			//����Top����
			HashMap pro = new HashMap();
			List proCpuList=new ArrayList();
	    	List proMemList=new ArrayList();
	    	List proTimeList=new ArrayList();
	    	
	    	HashMap proMap = reportHelper.getProValue(startTime, toTime, business);
			
			ArrayList<String> proHeadList = new ArrayList<String>();
			ArrayList<ArrayList<String[]>> proTableList = new ArrayList<ArrayList<String[]>>();//��ű���
			proCpuList = (List)proMap.get("cpu");
			ArrayList<String[]> proCpual = new ArrayList<String[]>();
			String[] proCpuTitle = { "ip", "������", "ƽ��ʹ����"};
			if(proCpuList.size() > 0){
				proHeadList.add("CPUƽ��ʹ���� Top");
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
			String[] proMemTitle = { "ip", "������", "ƽ��ʹ����"};
			if(proMemList.size() > 0){
				proHeadList.add("�ڴ�ƽ��ʹ���� Top");
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
			String[] proTimeTitle = { "ip", "������", "CPUʱ��"};
			if(proTimeList.size() > 0){
				proHeadList.add("CPU��ʱ Top");
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
				headList2.add("����Top����");
				contentList2.add(pro);
			}
			
			if(business!=null){
				//��ֵTop����
				
				List topCpuList=new ArrayList();
		    	List topResList=new ArrayList();
		    	HashMap topMap = reportHelper.getTopValue(ids, startTime, toTime, business);
		    	HashMap hostTopMap = (HashMap)topMap.get("host");
		    	HashMap netTopMap = (HashMap)topMap.get("net");
		    	if(hostTopMap!=null){
		    		HashMap topValue = new HashMap();
					ArrayList<String> topHeadList = new ArrayList<String>();
					ArrayList<ArrayList<String[]>> topTableList = new ArrayList<ArrayList<String[]>>();//��ű���
					topCpuList = (List)hostTopMap.get("cpu");
					ArrayList<String[]> topCpual = new ArrayList<String[]>();
					String[] topCpuTitle = { "ip", "��ֵ","����","��һ��ʱ��"};
					if(topCpuList.size() > 0){
						topHeadList.add("CPU��ֵ Top");
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
					String[] topResTitle = { "ip", "��ֵ","����","��һ��ʱ��"};
					if(topResList.size() > 0){
						topHeadList.add("��Ӧʱ���ֵ Top");
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
						headList2.add("������ֵTop����");
						contentList2.add(topValue);
					}
		    	}
				if(netTopMap!=null){
					HashMap topValue = new HashMap();
					ArrayList<String> topHeadList = new ArrayList<String>();
					ArrayList<ArrayList<String[]>> topTableList = new ArrayList<ArrayList<String[]>>();//��ű���
					topCpuList = (List)netTopMap.get("cpu");
					ArrayList<String[]> topCpual = new ArrayList<String[]>();
					String[] topCpuTitle = { "ip", "��ֵ","����","��һ��ʱ��"};
					if(topCpuList.size() > 0){
						topHeadList.add("CPU��ֵ Top");
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
					String[] topResTitle = { "ip", "��ֵ","����","��һ��ʱ��"};
					if(topResList.size() > 0){
						topHeadList.add("��Ӧʱ���ֵ Top");
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
						headList2.add("�����豸��ֵTop����");
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
		
		HashMap hash = new HashMap();//���ĵ�
		ArrayList<String> headList = new ArrayList<String>(); //1��Ŀ¼
		headList.add("�豸�¼�ͳ��");
		headList.add("�豸TOP����");
		ArrayList<HashMap> contentList = new ArrayList<HashMap>();//1������List
		//�豸�¼�ͳ��
		HashMap contentMap1 = new HashMap();
			//�豸�¼�ͳ��Ŀ¼
			ArrayList<String> headList1 = new ArrayList<String>(); //Ŀ¼1
			headList1.add("�豸���ϸ澯ͳ��");
			headList1.add("�豸�澯��ϸ");
			headList1.add("�¼��澯����");
			contentMap1.put("catalog", headList1);
			//�豸�¼�ͳ������
			ArrayList<HashMap> contentList1 = new ArrayList<HashMap>();
				//�豸���ϸ澯ͳ��
				HashMap contentAlarm = new HashMap();
					Map<String,String> map = crc.getLevelPieDataForMap(weekDay[0][1],weekDay[weekDay.length-1][2],business);
				contentAlarm.put("sum", Integer.valueOf(map.get("1"))+Integer.valueOf(map.get("2"))+Integer.valueOf(map.get("3"))+"");
				contentAlarm.put("level1", map.get("1"));
				contentAlarm.put("level2", map.get("2"));
				contentAlarm.put("level3", map.get("3"));
			contentList1.add(contentAlarm);
				//�豸�澯��ϸ
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
				//�¼��澯����
				HashMap contentDevTra = new HashMap();
					String[][] tableData = crc.gettableData(weekDay[0][1],weekDay[weekDay.length-1][2],business);
					ArrayList<String[]> DevTra = new ArrayList<String[]>();
					for(int i=0;i<tableData.length;i++){
						DevTra.add(tableData[i]);
					}
					contentDevTra.put("table", DevTra);
					List dayHoursList = crc.getWeekAlarmData(weekDay);
					String dayHourspath = makeJfreeChartDataForDayHours(dayHoursList,"����ÿ��澯��","ʱ��","����");
				contentDevTra.put("chart", dayHourspath);
			contentList1.add(contentDevTra);
			contentMap1.put("content", contentList1);
		contentList.add(contentMap1);
		//�豸TOP����
		HashMap contentMap2 = new HashMap();
		HashMap all = reportHelper.getAllValueWeek(ids, weekDay,business);
		HashMap netMap = (HashMap)all.get("net");
    	HashMap hostMap = (HashMap)all.get("host");
			ArrayList<String> headList2 = new ArrayList<String>(); //Ŀ¼2
			ArrayList<HashMap> contentList2 = new ArrayList<HashMap>(); 
			//������Top����
			HashMap host = new HashMap();
			List hostPingList=new ArrayList();
	    	List hostResponseList=new ArrayList();
	    	List hostCpuList=new ArrayList();
	    	List hostMemList=new ArrayList();
	    	List hostDiskList=new ArrayList();
	    	// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> hostTableList = new ArrayList<ArrayList<String[]>>();
			// ���ͼƬ·��
			ArrayList<String> hostChartList = new ArrayList<String>();
			//��ű���
			ArrayList<String> hostHeadList = new ArrayList<String>();
			
			hostCpuList = (List)hostMap.get("cpu");
			ArrayList<String[]> hostCpual = new ArrayList<String[]>();
			String[] hostTitle = { "ip", "ƽ��ֵ","���ֵ","��Сֵ"};
			if(hostCpuList.size() > 0){
				hostHeadList.add("������CPU Top");
				hostCpual.add(hostTitle);
				for(int i=0;i<hostCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostCpuList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostCpual.add(arry);
				}
				hostTableList.add(hostCpual);
				String hostCpupath = makeJfreeChartDataForWeek(hostCpuList,"������ CPU TOP","ʱ��", "������");
				hostChartList.add(hostCpupath);
			}
			hostMemList = (List)hostMap.get("mem");
			ArrayList<String[]> hostMemal = new ArrayList<String[]>();
			if(hostMemList.size() > 0){
				hostHeadList.add("�������ڴ� Top");
				hostMemal.add(hostTitle);
				for(int i=0;i<hostMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostMemList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostMemal.add(arry);
				}
				hostTableList.add(hostMemal);
				String hostMempath = makeJfreeChartDataForWeek(hostMemList,"������ �ڴ� TOP","ʱ��", "������");
				hostChartList.add(hostMempath);
			}
			hostPingList = (List)hostMap.get("ping");
			ArrayList<String[]> hostPingal = new ArrayList<String[]>();
			if(hostPingList.size() > 0){
				hostHeadList.add("��������ͨ�� Top");
				hostPingal.add(hostTitle);
				for(int i=0;i<hostPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostPingList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostPingal.add(arry);
				}
				hostTableList.add(hostPingal);
				String hostPingpath = makeJfreeChartDataForWeek(hostPingList,"������ ��ͨ�� TOP","ʱ��", "��ͨ��");
				hostChartList.add(hostPingpath);
			}
			hostResponseList = (List)hostMap.get("response");
			ArrayList<String[]> hostResponseal = new ArrayList<String[]>();
			if(hostResponseList.size() > 0){
				hostHeadList.add("��������Ӧ�� Top");
				hostResponseal.add(hostTitle);
				for(int i=0;i<hostResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostResponseList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostResponseal.add(arry);
				}
				hostTableList.add(hostResponseal);
				String hostResponsepath = makeJfreeChartDataForWeek(hostResponseList,"������ ��Ӧʱ�� TOP","ʱ��", "��Ӧʱ��");
				hostChartList.add(hostResponsepath);
			}
			hostDiskList = (List)hostMap.get("disk");
			ArrayList<String[]> hostDiskal = new ArrayList<String[]>();
			if(hostDiskList.size() > 0){
				hostHeadList.add("���������� Top");
				hostDiskal.add(hostTitle);
				for(int i=0;i<hostDiskList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)hostDiskList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					hostDiskal.add(arry);
				}
				hostTableList.add(hostDiskal);
				String hostDiskpath = makeJfreeChartDataForWeek(hostDiskList,"������ ���� TOP","ʱ��", "������");
				hostChartList.add(hostDiskpath);
			}
			host.put("head",hostHeadList);
			host.put("table", hostTableList);
			host.put("chart", hostChartList);
			if(hostHeadList.size()>0){
				headList2.add("������Top����");
				contentList2.add(host);
			}
			//����Top����
			HashMap net = new HashMap();
			List netPingList=new ArrayList();
	    	List netResponseList=new ArrayList();
	    	List netCpuList=new ArrayList();
	    	List netMemList=new ArrayList();
			List netUtilInList=new ArrayList();
			List netUtilOutList=new ArrayList();
			// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> netTableList = new ArrayList<ArrayList<String[]>>();
			// ���ͼƬ·��
			ArrayList<String> netChartList = new ArrayList<String>();
			//��ű���
			ArrayList<String> netHeadList = new ArrayList<String>();
			netCpuList = (List)netMap.get("cpu");
			ArrayList<String[]> netCpual = new ArrayList<String[]>();
			String[] netTitle =  { "ip", "ƽ��ֵ","���ֵ","��Сֵ"};
			if(netCpuList.size() > 0){
				netHeadList.add("�����豸CPU Top");
				netCpual.add(netTitle);
				for(int i=0;i<netCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netCpuList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netCpual.add(arry);
				}
				netTableList.add(netCpual);
				String netCpupath = makeJfreeChartDataForWeek(netCpuList,"�����豸 CPU TOP","ʱ��", "������");
				netChartList.add(netCpupath);
			}
			netMemList = (List)netMap.get("mem");
			ArrayList<String[]> netMemal = new ArrayList<String[]>();
			if(netMemList.size() > 0){
				netHeadList.add("�����豸�ڴ� Top");
				netMemal.add(netTitle);
				for(int i=0;i<netMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netMemList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netMemal.add(arry);
				}
				netTableList.add(netMemal);
				String netMempath = makeJfreeChartDataForWeek(netMemList,"�����豸 �ڴ� TOP","ʱ��", "������");
				netChartList.add(netMempath);
			}
			netPingList = (List)netMap.get("ping");
			ArrayList<String[]> netPingal = new ArrayList<String[]>();
			if(netPingList.size() > 0){
				netHeadList.add("�����豸��ͨ�� Top");
				netPingal.add(netTitle);
				for(int i=0;i<netPingList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netPingList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netPingal.add(arry);
				}
				netTableList.add(netPingal);
				String netPingpath = makeJfreeChartDataForWeek(netPingList,"�����豸 ��ͨ�� TOP","ʱ��", "��ͨ��");
				netChartList.add(netPingpath);
			}
			netResponseList = (List)netMap.get("response");
			ArrayList<String[]> netResponseal = new ArrayList<String[]>();
			if(netResponseList.size() > 0){
				netHeadList.add("�����豸��Ӧ�� Top");
				netResponseal.add(netTitle);
				for(int i=0;i<netResponseList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netResponseList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netResponseal.add(arry);
				}
				netTableList.add(netResponseal);
				String netResponsepath = makeJfreeChartDataForWeek(netResponseList,"�����豸 ��Ӧ�� TOP","ʱ��", "��Ӧʱ��");
				netChartList.add(netResponsepath);
			}
			netUtilInList = (List)netMap.get("utilIn");
			ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
			if(netUtilInList.size() > 0){
				netHeadList.add("�����豸������� Top");
				netUtilInal.add(netTitle);
				for(int i=0;i<netUtilInList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilInList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netUtilInal.add(arry);
				}
				netTableList.add(netUtilInal);
				String netUtilInpath = makeJfreeChartDataForWeek(netUtilInList,"�����豸 ������� TOP","ʱ��", "�������");
				netChartList.add(netUtilInpath);
			}
			netUtilOutList = (List)netMap.get("utilOut");
			ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
			if(netUtilOutList.size() > 0){
				netHeadList.add("�����豸�������� Top");
				netUtilOutal.add(netTitle);
				for(int i=0;i<netUtilOutList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)netUtilOutList.get(i);
					Map<String,Double> maps = crs.getWeekValues();
					String[] arry = {crs.getIp(),crs.getValue()+"",crs.getMax()+"",crs.getMin()+""};
					netUtilOutal.add(arry);
				}
				netTableList.add(netUtilOutal);
				String netUtilOutpath = makeJfreeChartDataForWeek(netUtilOutList,"�����豸 �������� TOP","ʱ��", "��������");
				netChartList.add(netUtilOutpath);
			}
			net.put("head",netHeadList);
			net.put("table", netTableList);
			net.put("chart", netChartList);
			if(netHeadList.size()>0){
				headList2.add("����Top����");
				contentList2.add(net);
			}
			//����Top����
			HashMap pro = new HashMap();
			List proCpuList=new ArrayList();
	    	List proMemList=new ArrayList();
	    	List proTimeList=new ArrayList();
	    	HashMap proMap = reportHelper.getProWeekValue(weekDay,business);
			
			ArrayList<String> proHeadList = new ArrayList<String>();
			ArrayList<ArrayList<String[]>> proTableList = new ArrayList<ArrayList<String[]>>();//��ű���
			ArrayList<String> proChartList = new ArrayList<String>();
			proCpuList = (List)proMap.get("cpu");
			ArrayList<String[]> proCpual = new ArrayList<String[]>();
			String[] proCpuTitle = { "ip", "������", "ƽ��ʹ����"};
			if(proCpuList.size() > 0){
				proHeadList.add("CPUƽ��ʹ���� Top");
				proCpual.add(proCpuTitle);
				for(int i=0;i<proCpuList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proCpuList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proCpual.add(arry);
				}
				proTableList.add(proCpual);
				String proCpuPath = makeJfreeChartDataForWeekByPro(proCpuList,"����Top Cpu","ʱ��","ʹ����");
				proChartList.add(proCpuPath);
			}
			proMemList = (List)proMap.get("mem");
			ArrayList<String[]> proMemal = new ArrayList<String[]>();
			String[] proMemTitle = { "ip", "������", "ƽ��ʹ����"};
			if(proMemList.size() > 0){
				proHeadList.add("�ڴ�ƽ��ʹ���� Top");
				proMemal.add(proMemTitle);
				for(int i=0;i<proMemList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proMemList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proMemal.add(arry);
				}
				proTableList.add(proMemal);
				String proMemoryPath = makeJfreeChartDataForWeekByPro(proMemList,"����Top �ڴ�","ʱ��","ʹ����");
				proChartList.add(proMemoryPath);
			}
			proTimeList = (List)proMap.get("time");
			ArrayList<String[]> proTimeal = new ArrayList<String[]>();
			String[] proTimeTitle = { "ip", "������", "CPUʱ��"};
			if(proTimeList.size() > 0){
				proHeadList.add("CPU��ʱ Top");
				proTimeal.add(proTimeTitle);
				for(int i=0;i<proTimeList.size();i++){
					CompreReportStatic crs = (CompreReportStatic)proTimeList.get(i);
					String[] arry = {crs.getIp(),crs.getType(),crs.getValue()+""};
					proTimeal.add(arry);
				}
				proTableList.add(proTimeal);
				String proTimePath = makeJfreeChartDataForWeekByPro(proTimeList,"����Top ��ʱ","ʱ��","ʹ����");
				proChartList.add(proTimePath);
			}
			pro.put("head", proHeadList);
			pro.put("table", proTableList);
			pro.put("chart", proChartList);
			if(proHeadList.size()>0){
				headList2.add("����Top����");
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
		               title /*��Ŀ*/, xdesc /*X��������*/, ydesc /*Y��������*/,timedataset,true,false,false);

			chart.setTitle(new TextTitle(title,new java.awt.Font("����", 1, 13)));
			  
			chart.setBackgroundPaint(Color.white);
			XYPlot xyplot = chart.getXYPlot();
			xyplot.setBackgroundPaint(Color.white);  
			xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
			
		    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		    //���Ҫ����ѡ�� ���ա��¡��ꡢСʱ
		    dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,6));
	        dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH"));
	        
	        dateaxis.setVerticalTickLabels(true);	        
			NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
			numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
			
	        //�Ը����߽�����ɫ			
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
		               title /*��Ŀ*/, xdesc /*X��������*/, ydesc /*Y��������*/,timedataset,true,false,false);

			chart.setTitle(new TextTitle(title,new java.awt.Font("����", 1, 13)));
			  
			chart.setBackgroundPaint(Color.white);
			XYPlot xyplot = chart.getXYPlot();
			xyplot.setBackgroundPaint(Color.white);  
			xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
			
		    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
		    //���Ҫ����ѡ�� ���ա��¡��ꡢСʱ
		    dateaxis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR,6));
	        dateaxis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH"));
	        
	        dateaxis.setVerticalTickLabels(true);	        
			NumberAxis numAxis = (NumberAxis)xyplot.getRangeAxis();
			numAxis.setNumberFormatOverride(new DecimalFormat("####.#"));
			
	        //�Ը����߽�����ɫ			
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
