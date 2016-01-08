package com.afunms.linkReport.util;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.afunms.initialize.ResourceCenter;
import com.afunms.linkReport.report.*;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.lowagie.text.Element;
import com.lowagie.text.Font;

public class LinkReportExport {
	
	private final int xlabel = 12;

	private LinkReportHelper reportHelper = null;

	private int chartWith = 768;

	private int chartHigh = 338;
	
	public void exportReportByDay(String ids, String type, String reportType,String filePath,
			String startTime, String toTime, String exportType,String terms){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = null;
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// ��ű���
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new LinkReportHelper();
		String title = "����";
		if ("link".equalsIgnoreCase(type)) {
			// �����豸��������
			if("day".equals(reportType)){
				title = "�ؼ���·�ձ���";
			}else if("week".equals(reportType)){
				title = "�ؼ���·�ܱ���";
			}
			hm = exportLinkReportDay(ids, type, filePath, startTime, toTime,terms);
//			tableList = (ArrayList<ArrayList<String[]>>) hm.get("table");
//			chartList = (ArrayList<String>) hm.get("chart");
//			headList = (ArrayList<String>) hm.get("head");
		}
		String time = startTime + "~" + toTime;
		LinkExportInterface export = null;
		if ("xls".equals(exportType)) {
			chartWith = 768;
			chartHigh = 338;
			export = new LinkExcel(filePath);
		} else if ("pdf".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new LinkPdf(filePath);
		} else if ("doc".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new LinkWord(filePath);
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
	private HashMap<?, ArrayList<?>> exportLinkReportDay(String ids, String type,String filePath, String startTime, String toTime,String terms) {
		HashMap hash = new HashMap();//���ĵ�
		ArrayList<String> headList = new ArrayList<String>();//Ŀ¼
		ArrayList<ArrayList<String[]>> linkTableList = new ArrayList<ArrayList<String[]>>();//���
		ArrayList<String> chartList = new ArrayList<String>();//ͼƬ
		HashMap valueMap = reportHelper.getLinkReportDay(ids,terms,startTime,toTime);
		List linkUpList = new ArrayList();
    	List linkDownList = new ArrayList();
    	List linkBandwidthList = new ArrayList();
    	List linkBandTrendList = new ArrayList();
    	List linkUsabilityList = new ArrayList();
    	//��������
    	linkUpList = (List)valueMap.get("up");
    	ArrayList<String[]> linkUpal = new ArrayList<String[]>();
		String[] linkUpTitle = { "��·����", "��������(KB/��)"};
    	if(linkUpList!=null&&linkUpList.size()>0){
    		headList.add("TOP �ؼ���·��������");
    		linkUpal.add(linkUpTitle);
    		for(int i=0;i<linkUpList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkUpList.get(i);
				String[] arry = {lrs.getLinkName(),lrs.getValue()+""};
				linkUpal.add(arry);
			}
    		linkTableList.add(linkUpal);
			String linkUppath = makeJfreeChartDataForLinkDay(linkUpList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkUppath);
    	}
		//��������
    	linkDownList = (List)valueMap.get("down");
    	ArrayList<String[]> linkDownal = new ArrayList<String[]>();
		String[] linkDownTitle = { "��·����", "��������(KB/��)"};
    	if(linkDownList!=null&&linkDownList.size()>0){
    		headList.add("TOP �ؼ���·��������");
    		linkDownal.add(linkDownTitle);
    		for(int i=0;i<linkDownList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkDownList.get(i);
				String[] arry = {lrs.getLinkName(),lrs.getValue()+""};
				linkDownal.add(arry);
			}
    		linkTableList.add(linkDownal);
			String linkDownpath = makeJfreeChartDataForLinkDay(linkDownList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkDownpath);
    	}
    	//������
    	linkUsabilityList = (List)valueMap.get("usability");
    	ArrayList<String[]> linkUsabilityal = new ArrayList<String[]>();
		String[] linkUsabilityTitle = { "��·����", "������"};
    	if(linkUsabilityList!=null&&linkUsabilityList.size()>0){
    		headList.add("TOP �ؼ���·������");
    		linkUsabilityal.add(linkUsabilityTitle);
    		for(int i=0;i<linkUsabilityList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkUsabilityList.get(i);
				String[] arry = {lrs.getLinkName(),lrs.getValue()+""};
				linkUsabilityal.add(arry);
			}
    		linkTableList.add(linkUsabilityal);
			String linkUsabilitypath = makeJfreeChartDataForLinkDay(linkUsabilityList,"TOP �ؼ���·������","��·����", "������");
			chartList.add(linkUsabilitypath);
    	}
    	//����
    	linkBandwidthList = (List)valueMap.get("bandwidth");
    	ArrayList<String[]> linkBandwidthal = new ArrayList<String[]>();
		String[] linkBandwidthTitle = { "��·����", "����%"};
    	if(linkBandwidthList!=null&&linkBandwidthList.size()>0){
    		headList.add("TOP �ؼ���·����");
    		linkBandwidthal.add(linkBandwidthTitle);
    		for(int i=0;i<linkBandwidthList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkBandwidthList.get(i);
				String[] arry = {lrs.getLinkName(),lrs.getValue()+""};
				linkBandwidthal.add(arry);
			}
    		linkTableList.add(linkBandwidthal);
			String linkBandwidthpath = makeJfreeChartDataForLinkDay(linkBandwidthList,"TOP �ؼ���·����","��·����", "����");
			chartList.add(linkBandwidthpath);
    	}
    	//��������
    	linkBandTrendList = (List)valueMap.get("bandtrend");
    	ArrayList<String[]> linkBandTrendal = new ArrayList<String[]>();
		String[] linkBandTrendTitle = { "��·����", "��������"};
    	if(linkBandTrendList!=null&&linkBandTrendList.size()>0){
    		headList.add("TOP �ؼ���·����");
    		linkBandTrendal.add(linkBandTrendTitle);
    		for(int i=0;i<linkBandTrendList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkBandTrendList.get(i);
				String[] arry = {lrs.getLinkName(),lrs.getValue()+""};
				linkBandTrendal.add(arry);
			}
    		linkTableList.add(linkBandTrendal);
			String linkBandTrendpath = makeJfreeChartDataForLinkDay(linkBandTrendList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkBandTrendpath);
    	}
    	
		hash.put("catalog", headList);
		hash.put("content",linkTableList);
		hash.put("chart", chartList);
		return hash;
	}
	private void export(LinkExportInterface export,HashMap hm, String title, String time) throws Exception{
		ArrayList<String> headList = (ArrayList<String>) hm.get("catalog");
		ArrayList<ArrayList<String[]>> linkTableList = (ArrayList<ArrayList<String[]>>)hm.get("content");
		ArrayList<String> chartList = (ArrayList<String>)hm.get("chart");
		
		export.insertTitle(title,0,time);
		
		if (linkTableList != null) {
			for(int i=0;i<linkTableList.size();i++){
				export.insertContent((i+1)+". "+headList.get(i),16,Font.BOLD,Element.ALIGN_LEFT);
				if(chartList!=null){
					String chartal = chartList.get(i);
					if (chartal != null && !"".equals(chartal.trim())) {
						export.insertChart(chartal);
					}
				}
				ArrayList<String[]> tableal = linkTableList.get(i);
				if (tableal != null && tableal.size() > 0) {
					export.insertTable(tableal);
				}
			}
		}
		
		try {
			export.save();
		} catch (Exception e) {
			SysLogger.error("------�����ļ�����ʧ�ܣ�------", e);
		}
		
	}
	
	public void exportReportByWeek(String ids, String type, String reportType,String filePath,
			String[][] weekday, String exportType,String terms){
		// startTime = "2011-05-31 00:00:00";
		// toTime = "2011-05-31 23:59:59";
		HashMap<?, ArrayList<?>> hm = null;
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = null;
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// ��ű���
		ArrayList<String> headList = new ArrayList<String>();
		reportHelper = new LinkReportHelper();
		String title = "����";
		if ("link".equalsIgnoreCase(type)) {
			// �����豸��������
			if("day".equals(reportType)){
				title = "�ؼ���·�ձ���";
			}else if("week".equals(reportType)){
				title = "�ؼ���·�ܱ���";
			}
			hm = exportLinkReportWeek(ids, type, filePath, weekday,terms);
//			tableList = (ArrayList<ArrayList<String[]>>) hm.get("table");
//			chartList = (ArrayList<String>) hm.get("chart");
//			headList = (ArrayList<String>) hm.get("head");
		}
		String time = weekday[0][1] + "~" + weekday[weekday.length-1][2];
		LinkExportInterface export = null;
		if ("xls".equals(exportType)) {
			chartWith = 768;
			chartHigh = 338;
			export = new LinkExcel(filePath);
		} else if ("pdf".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new LinkPdf(filePath);
		} else if ("doc".equals(exportType)) {
			chartWith = 540;
			chartHigh = 340;
			export = new LinkWord(filePath);
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
	
	private HashMap<?, ArrayList<?>> exportLinkReportWeek(String ids, String type,String filePath, String[][] weekday,String terms) {
		HashMap hash = new HashMap();//���ĵ�
		ArrayList<String> headList = new ArrayList<String>();//Ŀ¼
		ArrayList<ArrayList<String[]>> linkTableList = new ArrayList<ArrayList<String[]>>();//���
		ArrayList<String> chartList = new ArrayList<String>();//ͼƬ
		HashMap valueMap = reportHelper.getLinkReportWeek(ids,terms,weekday);
		List linkUpList = new ArrayList();
    	List linkDownList = new ArrayList();
    	List linkBandwidthList = new ArrayList();
    	List linkBandTrendList = new ArrayList();
    	List linkUsabilityList = new ArrayList();
    	//��������
    	linkUpList = (List)valueMap.get("up");
    	ArrayList<String[]> linkUpal = new ArrayList<String[]>();
    	String[] linkUpTitle = { "��·����", "ƽ��ֵ","���ֵ","��Сֵ"};
		if(linkUpList!=null&&linkUpList.size()>0){
    		headList.add("TOP �ؼ���·��������");
    		linkUpal.add(linkUpTitle);
    		for(int i=0;i<linkUpList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkUpList.get(i);
    			String[] arry = {lrs.getLinkName(),lrs.getValue(),lrs.getMax(),lrs.getMin()};
    			linkUpal.add(arry);
			}
    		linkTableList.add(linkUpal);
			String linkUppath = makeJfreeChartDataForWeek(linkUpList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkUppath);
    	}
		//��������
    	linkDownList = (List)valueMap.get("down");
    	ArrayList<String[]> linkDownal = new ArrayList<String[]>();
    	String[] linkDownTitle = { "��·����", "ƽ��ֵ","���ֵ","��Сֵ"};
		if(linkDownList!=null&&linkDownList.size()>0){
    		headList.add("TOP �ؼ���·��������");
    		linkDownal.add(linkDownTitle);
    		for(int i=0;i<linkDownList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkDownList.get(i);
    			String[] arry = {lrs.getLinkName(),lrs.getValue(),lrs.getMax(),lrs.getMin()};
    			linkDownal.add(arry);
			}
    		linkTableList.add(linkDownal);
			String linkDownpath = makeJfreeChartDataForWeek(linkDownList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkDownpath);
    	}
    	//������
    	linkUsabilityList = (List)valueMap.get("usability");
    	ArrayList<String[]> linkUsabilityal = new ArrayList<String[]>();
    	String[] linkUsabilityTitle = { "��·����", "ƽ��ֵ","���ֵ","��Сֵ"};
    	if(linkUsabilityList!=null&&linkUsabilityList.size()>0){
    		headList.add("TOP �ؼ���·������");
    		linkUsabilityal.add(linkUsabilityTitle);
    		for(int i=0;i<linkUsabilityList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkUsabilityList.get(i);
    			String[] arry = {lrs.getLinkName(),lrs.getValue(),lrs.getMax(),lrs.getMin()};
    			linkUsabilityal.add(arry);
			}
    		linkTableList.add(linkUsabilityal);
			String linkUsabilitypath = makeJfreeChartDataForWeek(linkUsabilityList,"TOP �ؼ���·������","��·����", "������");
			chartList.add(linkUsabilitypath);
    	}
    	//����
    	linkBandwidthList = (List)valueMap.get("bandwidth");
    	ArrayList<String[]> linkBandwidthal = new ArrayList<String[]>();
    	String[] linkBandwidthTitle = { "��·����", "ƽ��ֵ","���ֵ","��Сֵ"};
    	if(linkBandwidthList!=null&&linkBandwidthList.size()>0){
    		headList.add("TOP �ؼ���·����");
    		linkBandwidthal.add(linkBandwidthTitle);
    		for(int i=0;i<linkBandwidthList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkBandwidthList.get(i);
    			String[] arry = {lrs.getLinkName(),lrs.getValue(),lrs.getMax(),lrs.getMin()};
    			linkBandwidthal.add(arry);
			}
    		linkTableList.add(linkBandwidthal);
			String linkBandwidthpath = makeJfreeChartDataForWeek(linkBandwidthList,"TOP �ؼ���·����","��·����", "����");
			chartList.add(linkBandwidthpath);
    	}
    	//��������
    	linkBandTrendList = (List)valueMap.get("bandtrend");
    	ArrayList<String[]> linkBandTrendal = new ArrayList<String[]>();
    	String[] linkBandTrendTitle =  { "��·����", "ƽ��ֵ","���ֵ","��Сֵ"};
    	if(linkBandTrendList!=null&&linkBandTrendList.size()>0){
    		headList.add("TOP �ؼ���·����");
    		linkBandTrendal.add(linkBandTrendTitle);
    		for(int i=0;i<linkBandTrendList.size();i++){
    			LinkReportStatic lrs = (LinkReportStatic)linkBandTrendList.get(i);
    			String[] arry = {lrs.getLinkName(),lrs.getValue(),lrs.getMax(),lrs.getMin()};
    			linkBandTrendal.add(arry);
			}
    		linkTableList.add(linkBandTrendal);
			String linkBandTrendpath = makeJfreeChartDataForWeek(linkBandTrendList,"TOP �ؼ���·��������","��·����", "��������");
			chartList.add(linkBandTrendpath);
    	}
    	
		hash.put("catalog", headList);
		hash.put("content",linkTableList);
		hash.put("chart", chartList);
		return hash;
	}
	
	private String makeJfreeChartDataForLinkDay(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				LinkReportStatic lrs = (LinkReportStatic)list.get(j);
				dataset.addValue(Double.valueOf(lrs.getValue()), lrs.getUnit(), lrs.getLinkName());
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
//	private String makeJfreeChartDataForWeek(List list,String title, String xdesc, String ydesc){
//		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
//			+ File.separator + System.currentTimeMillis() + ".png";
//		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		if (list != null && list.size() > 0) {
//			for (int j = 0; j < list.size(); j++) {
//				LinkReportStatic lrs = (LinkReportStatic)list.get(j);
//				Map<String,Double> maps = lrs.getWeekValues();
//				if(maps.size()>0){
//					Set<Map.Entry<String, Double>> set = maps.entrySet();
//					for (Iterator<Map.Entry<String,Double>> it = set.iterator(); it.hasNext();) {
//						Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
//						dataset.addValue(entry.getValue(),lrs.getLinkName(),entry.getKey());
//					}
//				}else{
//					dataset.addValue(Double.valueOf(lrs.getValue()), lrs.getId(), lrs.getLinkName());
//				}
//			}
//			String chartkey = ChartCreator.createLineChart(title,xdesc,ydesc,dataset,chartWith,chartHigh);
//			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
//					.getInstance().getChartStorage().get(chartkey);
//			CategoryPlot plot = chart.getChart().getCategoryPlot();
//			
//			CategoryAxis domainAxis = plot.getDomainAxis();
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
			LinkReportStatic crs = (LinkReportStatic)list.get(j);
			Map<String,Double> maps = crs.getWeekValues();
			TimeSeries series = new TimeSeries(crs.getLinkName(),Hour.class);
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
