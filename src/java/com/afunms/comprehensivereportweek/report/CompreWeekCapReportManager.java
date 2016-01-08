package com.afunms.comprehensivereportweek.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.comprehensivereport.util.CompreReportStatic;
import com.afunms.comprehensivereportweek.dao.CompreReportWeekDao;
import com.afunms.comprehensivereportweek.manage.CompreReportWeekManager;
import com.afunms.comprehensivereportweek.model.CompreReportWeekInfo;
import com.afunms.comprehensivereportweek.util.CalDateUtil;
import com.afunms.comprehensivereportweek.util.CompreWeekReportStatic;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.flex.TopNService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HomeCollectDataManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.export.Excel;
import com.afunms.report.export.ExportInterface;
import com.afunms.report.export.Pdf;
import com.afunms.report.export.Word;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class CompreWeekCapReportManager extends BaseManager implements ManagerInterface{
	private final int xlabel = 12;
	private int chartWith = 768;
	private int chartHigh = 238;
	
	private int leven1_int;
	private  int leven2_int;
	private int  leven3_int;
	private int levencount_int; 
	private Map map;
	List hostListPing_Response;
	List netListPing_Response;
//	public void CompreWeekCapReportManager(){
//		leven1_int=0; leven2_int=0; leven3_int=0;
//	}
	public String execute(String action) {
		if(action.equals("downloadReportWeek")){
			return downloadReportWeek();
		}if(action.equals("netHostWorkReportConfig")){
			return netHostWorkReportConfig();
		}
		return null;
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
	private String[] getIdValue(String ids){
		String[] idValue=null;
		if (ids!=null&&!ids.equals("null")&&!ids.equals("")) {
			 idValue=new String[ids.split(",").length];
	    	idValue=ids.split(",");
		}
		return idValue;
	}
	/**
	 * ��ѯ�¼�����
	 * @return
	 */
	private List hostevent(String starttime,String totime,String[] hostNetIds,String[] netId,String[] hostId) {
		
		// �������־ȡ���˿����¼�¼���б�
		String orderflag = "ipaddress";
//		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
//			orderflag = getParaValue("orderflag");
//		}
		List list = new ArrayList();
		List orderList = new ArrayList();
		//�������豸
		ResultSet rs = null;
		DBManager conn = new DBManager();
//		HostNodeDao dao = new HostNodeDao();
		try {
		if (hostNetIds != null && hostNetIds.length > 0) {
			for (int i = 0; i < hostNetIds.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(hostNetIds[i]));
				dao.close();
				if (node == null)
					continue;
				EventListDao eventdao = new EventListDao();
				// �õ��¼��б�
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='"
						+ totime + "' ");
				s.append(" and nodeid=" + node.getId());
				
				StringBuffer level = new StringBuffer();
				level.append("select level1,count(level1) as cnt from system_eventlist w where nodeid in(");
				level.append(node.getId());
				level.append(")and to_days(recordtime) >= to_days('");
				level.append(starttime);
				level.append("') and to_days(recordtime) <=to_days('" );
				level.append(totime);
				level.append("')  group by level1");
				
				
				List infolist = eventdao.findByCriteria(s.toString());
				int levelone = 0,
					levletwo = 0,
					levelthree = 0,
					pingvalue = 0,
					memvalue = 0,
					diskvalue = 0, 
					cpuvalue = 0;
				
				
				rs = conn.executeQuery(level.toString());
				while(rs.next()){
					int le = rs.getInt("level1");
					if(le==1){
						levelone = rs.getInt("cnt");
					}else if(le==2){
						levletwo = rs.getInt("cnt");
					}else if(le==3){
						levelthree = rs.getInt("cnt");
					}
				}
				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getLevel1() == null)
						continue;
//					if (eventlist.getLevel1() == 1) {
//						levelone = levelone + 1;
//					} else if (eventlist.getLevel1() == 2) {
//						levletwo = levletwo + 1;
//					} else if (eventlist.getLevel1() == 3) {
//						levelthree = levelthree + 1;
//					}
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						pingvalue = pingvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("memory")) {
						memvalue = memvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("disk")) {
						diskvalue = diskvalue + 1;
					} else if (eventlist.getSubentity().equalsIgnoreCase("cpu")) {
						cpuvalue = cpuvalue + 1;
					}
				}
				String equname = node.getAlias();
				String ip = node.getIpAddress();
				List ipeventList = new ArrayList();
				ipeventList.add(ip);
				ipeventList.add(equname);
				ipeventList.add(node.getType());
				ipeventList.add((levelone + levletwo + levelthree) + "");
				ipeventList.add(levelone + "");
				ipeventList.add(levletwo + "");
				ipeventList.add(levelthree + "");
				ipeventList.add(pingvalue + "");
				ipeventList.add(memvalue + "");
				ipeventList.add(diskvalue + "");
				ipeventList.add(cpuvalue + "");
				orderList.add(ipeventList);

			}

		} 
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("one") || orderflag.equalsIgnoreCase("two")
				|| orderflag.equalsIgnoreCase("three") || orderflag.equalsIgnoreCase("ping")
				|| orderflag.equalsIgnoreCase("mem") || orderflag.equalsIgnoreCase("disk")
				|| orderflag.equalsIgnoreCase("cpu") || orderflag.equalsIgnoreCase("sum")) {
			returnList = (List) session.getAttribute("eventlist");
		} else {
			returnList = orderList;
		}

		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("sum")) {
						String sum = "";
						if (ipdiskList.get(3) != null) {
							sum = (String) ipdiskList.get(3);
						}
						String _sum = "";
						if (ipdiskList.get(3) != null) {
							_sum = (String) _ipdiskList.get(3);
						}
						if (new Double(sum).doubleValue() < new Double(_sum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("one")) {
						String downnum = "";
						if (ipdiskList.get(4) != null) {
							downnum = (String) ipdiskList.get(4);
						}
						String _downnum = "";
						if (ipdiskList.get(4) != null) {
							_downnum = (String) _ipdiskList.get(4);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("two")) {
						String downnum = "";
						if (ipdiskList.get(5) != null) {
							downnum = (String) ipdiskList.get(5);
						}
						String _downnum = "";
						if (ipdiskList.get(5) != null) {
							_downnum = (String) _ipdiskList.get(5);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("three")) {
						String downnum = "";
						if (ipdiskList.get(6) != null) {
							downnum = (String) ipdiskList.get(6);
						}
						String _downnum = "";
						if (ipdiskList.get(6) != null) {
							_downnum = (String) _ipdiskList.get(6);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("ping")) {
						String downnum = "";
						if (ipdiskList.get(7) != null) {
							downnum = (String) ipdiskList.get(7);
						}
						String _downnum = "";
						if (ipdiskList.get(7) != null) {
							_downnum = (String) _ipdiskList.get(7);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("mem")) {
						String downnum = "";
						if (ipdiskList.get(8) != null) {
							downnum = (String) ipdiskList.get(8);
						}
						String _downnum = "";
						if (ipdiskList.get(8) != null) {
							_downnum = (String) _ipdiskList.get(8);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("disk")) {
						String downnum = "";
						if (ipdiskList.get(9) != null) {
							downnum = (String) ipdiskList.get(9);
						}
						String _downnum = "";
						if (ipdiskList.get(9) != null) {
							_downnum = (String) _ipdiskList.get(9);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("cpu")) {
						String downnum = "";
						if (ipdiskList.get(10) != null) {
							downnum = (String) ipdiskList.get(10);
						}
						String _downnum = "";
						if (ipdiskList.get(10) != null) {
							_downnum = (String) _ipdiskList.get(10);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
	}catch (SQLException e) {
		
		e.printStackTrace();
	}
	finally{
//		dao.close();
		conn.close();
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
		return list;
	}
	/**
	 * eventlist 
	 */
	public void createdocEvent(String exportType,String filePath,List eventlist,
			String [][]eventDataStr,String chartPath,
			String [] hostDevicePath,HashMap hm_cpu,HashMap hm_disk,
			String []netDevicePath,HashMap nm_cpu,HashMap netPort,String contentType) {
		String fileName = filePath;
		try {
			createDocContextEvent(exportType,fileName,eventlist,eventDataStr,chartPath,hostDevicePath,hm_cpu,hm_disk,netDevicePath,nm_cpu,netPort,contentType);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 	file  �ļ�����·��
	 *  eventlist �澯��ϸ��Ϣ
	 *  eventDataStr  �澯��Ϣ�������
	 */
	public void createDocContextEvent(String exportType,String file,List eventlist,String [][]eventDataStr,
										String chartPath,String [] hostDevicePath,HashMap hm_cpu,
										HashMap hm_disk,String []netDevicePath,HashMap nm_cpu,
										HashMap netPort,String contentType) throws DocumentException, IOException {
		if(contentType=="net"){
			// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> tableList = null;
			// ���ͼƬ·��
			ArrayList<String> chartList_cpu = new ArrayList<String>();
			ArrayList<String> chartList_disk = new ArrayList<String>();
			ArrayList<String> chartList_netCpu = new ArrayList<String>();
			String cpu_utilization="",disk_utilization="",netCpu_utilization="";
			String [][]tableData = eventDataStr;
			Paragraph eventCounttitle_data;
			//--------------------------��������ʽ����-------------------------------
			// ����ֽ�Ŵ�С
			Document document = new Document(PageSize.A4);
			// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
			RtfWriter2.getInstance(document, new FileOutputStream(file));
			document.open();
			// ������������
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// ����������
			Font titleFont = new Font(bfChinese, 14, Font.BOLD);
			Font smtitleFontOne = new Font(bfChinese, 13, Font.BOLD);
			Font smtitleFont = new Font(bfChinese, 12, Font.BOLD);
			// ����������
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			//--------------------------��������ʽ���� end-------------------------------
			
			//--------------------------  ����  ---------------------------
			Paragraph title = new Paragraph("�豸��Ϣͳ���ܱ���", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("\n"));
			//-------------------------- end ------------------------------
			
			//--------------------------һ���¼�ͳ�� 	  ---------------------------
			Paragraph eventCounttitleOne = new Paragraph("һ �¼�ͳ��", smtitleFontOne);
			document.add(eventCounttitleOne);
			document.add(new Paragraph("\n"));
			Paragraph eventCounttitle = new Paragraph("1.1�豸���ϸ澯��Ϣͳ��", smtitleFont);
			document.add(eventCounttitle);
			document.add(new Paragraph("\n"));
			
			if(levencount_int>0){
				eventCounttitle_data = new Paragraph("�¼�����:"+levencount_int+"������  ���ظ澯:"
						+leven3_int+"�Ρ������澯:"+leven2_int+"�Ρ���ͨ�澯:"+leven1_int+"��", contextFont);
			}else{
				eventCounttitle_data = new Paragraph("�¼�����:"+(0)+"������  ���ظ澯:"
						+0+"�Ρ������澯:"+0+"�Ρ���ͨ�澯:"+0+"��", contextFont);
			}
			document.add(eventCounttitle_data);
			Paragraph smtitle = new Paragraph("1.2�豸�澯��ϸ", smtitleFont);
			// ���ñ����ʽ���뷽ʽ
			smtitle.setAlignment(Element.ALIGN_LEFT);
			// title.setFont(titleFont);
			document.add(smtitle);
			// ���� Table ���
			if(eventlist != null && eventlist.size() > 0){
				Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
				Table aTable = new Table(12);
				int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55};
				aTable.setWidths(width);
				aTable.setWidth(100); // ռҳ���� 90%
				aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable.setAutoFillEmptyCells(true); // �Զ�����
				aTable.setBorderWidth(1); // �߿���
				aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable.setBorder(2);// �߿�
				aTable.endHeaders();
				Cell c = new Cell("���");
				c.setHorizontalAlignment(Element.ALIGN_CENTER);
				c.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(c);
				Cell cell1 = new Cell("IP��ַ");
				Cell cell2 = new Cell("�豸����");
				Cell cell3 = new Cell("����ϵͳ");
				Cell cell4 = new Cell("�¼�����");
				Cell cell5 = new Cell("��ͨ");
				Cell cell6 = new Cell("����");
				Cell cell7 = new Cell("����");
				Cell cell8 = new Cell("��ͨ���¼�");
				Cell cell9 = new Cell("�ڴ��¼�");
				Cell cell10 = new Cell("�����¼�");
				Cell cell11 = new Cell("cpu�¼�");
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(Color.LIGHT_GRAY);
				cell2.setBackgroundColor(Color.LIGHT_GRAY);
				cell3.setBackgroundColor(Color.LIGHT_GRAY);
				cell4.setBackgroundColor(Color.LIGHT_GRAY);
				cell5.setBackgroundColor(Color.LIGHT_GRAY);
				cell6.setBackgroundColor(Color.LIGHT_GRAY);
				cell7.setBackgroundColor(Color.LIGHT_GRAY);
				cell8.setBackgroundColor(Color.LIGHT_GRAY);
				cell9.setBackgroundColor(Color.LIGHT_GRAY);
				cell10.setBackgroundColor(Color.LIGHT_GRAY);
				cell11.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(cell1);
				aTable.addCell(cell2);
				aTable.addCell(cell3);
				aTable.addCell(cell4);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell11);
				for (int i = 0; i < eventlist.size(); i++) {
					List _eventlist = (List) eventlist.get(i);
					String ip = (String) _eventlist.get(0);
					String equname = (String) _eventlist.get(1);
					String osname = (String) _eventlist.get(2);
					String sum = (String) _eventlist.get(3);
					String levelone = (String) _eventlist.get(4);
					String leveltwo = (String) _eventlist.get(5);
					String levelthree = (String) _eventlist.get(6);
					String pingvalue = (String) _eventlist.get(7);
					String memvalue = (String) _eventlist.get(8);
					String diskvalue = (String) _eventlist.get(9);
					String cpuvalue = (String) _eventlist.get(10);
					Cell cell13 = new Cell(i + 1 + "");
					Cell cell14 = new Cell(ip);
					Cell cell15 = new Cell(equname);
					Cell cell16 = new Cell(osname);
					Cell cell17 = new Cell(sum);
					Cell cell18 = new Cell(levelone);
					Cell cell19 = new Cell(leveltwo);
					Cell cell20 = new Cell(levelthree);
					Cell cell21 = new Cell(pingvalue);
					Cell cell22 = new Cell(memvalue);
					Cell cell23 = new Cell(diskvalue);
					Cell cell24 = new Cell(cpuvalue);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable.addCell(cell13);
					aTable.addCell(cell14);
					aTable.addCell(cell15);
					aTable.addCell(cell16);
					aTable.addCell(cell17);
					aTable.addCell(cell18);
					aTable.addCell(cell19);
					aTable.addCell(cell20);
					aTable.addCell(cell21);
					aTable.addCell(cell22);
					aTable.addCell(cell23);
					aTable.addCell(cell24);
				}
				document.add(aTable);
				document.add(new Paragraph("\n"));
			}
			//�¼��澯����
			Paragraph eventTrend = new Paragraph("1.3�¼��澯����", smtitleFont);
			document.add(eventTrend);
			// ���� Table ���
			if(eventlist != null && eventlist.size() > 0){
				Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
				Table aTable = new Table(12);
				int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55};
				aTable.setWidths(width);
				aTable.setWidth(100); // ռҳ���� 90%
				aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable.setAutoFillEmptyCells(true); // �Զ�����
				aTable.setBorderWidth(1); // �߿���
				aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable.setBorder(2);// �߿�
				aTable.endHeaders();
				Cell c = new Cell("���");
				c.setHorizontalAlignment(Element.ALIGN_CENTER);
				c.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(c);
				Cell cell1 = new Cell("IP��ַ");
				Cell cell2 = new Cell("�豸����");
				Cell cell3 = new Cell("����ϵͳ");
				Cell cell4 = new Cell("�¼�����");
				Cell cell5 = new Cell("��ͨ");
				Cell cell6 = new Cell("����");
				Cell cell7 = new Cell("����");
				Cell cell8 = new Cell("��ͨ���¼�");
				Cell cell9 = new Cell("�ڴ��¼�");
				Cell cell10 = new Cell("�����¼�");
				Cell cell11 = new Cell("cpu�¼�");
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(Color.LIGHT_GRAY);
				cell2.setBackgroundColor(Color.LIGHT_GRAY);
				cell3.setBackgroundColor(Color.LIGHT_GRAY);
				cell4.setBackgroundColor(Color.LIGHT_GRAY);
				cell5.setBackgroundColor(Color.LIGHT_GRAY);
				cell6.setBackgroundColor(Color.LIGHT_GRAY);
				cell7.setBackgroundColor(Color.LIGHT_GRAY);
				cell8.setBackgroundColor(Color.LIGHT_GRAY);
				cell9.setBackgroundColor(Color.LIGHT_GRAY);
				cell10.setBackgroundColor(Color.LIGHT_GRAY);
				cell11.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(cell1);
				aTable.addCell(cell2);
				aTable.addCell(cell3);
				aTable.addCell(cell4);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell11);
				for (int i = 0; i < eventlist.size(); i++) {
					List _eventlist = (List) eventlist.get(i);
					String ip = (String) _eventlist.get(0);
					String equname = (String) _eventlist.get(1);
					String osname = (String) _eventlist.get(2);
					String sum = (String) _eventlist.get(3);
					String levelone = (String) _eventlist.get(4);
					String leveltwo = (String) _eventlist.get(5);
					String levelthree = (String) _eventlist.get(6);
					String pingvalue = (String) _eventlist.get(7);
					String memvalue = (String) _eventlist.get(8);
					String diskvalue = (String) _eventlist.get(9);
					String cpuvalue = (String) _eventlist.get(10);
					Cell cell13 = new Cell(i + 1 + "");
					Cell cell14 = new Cell(ip);
					Cell cell15 = new Cell(equname);
					Cell cell16 = new Cell(osname);
					Cell cell17 = new Cell(sum);
					Cell cell18 = new Cell(levelone);
					Cell cell19 = new Cell(leveltwo);
					Cell cell20 = new Cell(levelthree);
					Cell cell21 = new Cell(pingvalue);
					Cell cell22 = new Cell(memvalue);
					Cell cell23 = new Cell(diskvalue);
					Cell cell24 = new Cell(cpuvalue);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable.addCell(cell13);
					aTable.addCell(cell14);
					aTable.addCell(cell15);
					aTable.addCell(cell16);
					aTable.addCell(cell17);
					aTable.addCell(cell18);
					aTable.addCell(cell19);
					aTable.addCell(cell20);
					aTable.addCell(cell21);
					aTable.addCell(cell22);
					aTable.addCell(cell23);
					aTable.addCell(cell24);
				}
				document.add(aTable);
				document.add(new Paragraph("\n"));
			}
			//�������Ʒ���
			Paragraph propertyCounttitleTwo = new Paragraph("�� �������Ʒ���(top10)", smtitleFontOne);
			document.add(propertyCounttitleTwo);
			document.add(new Paragraph("\n"));
			Paragraph propertyCounttitle = new Paragraph("2.1 ��������ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle);
			Paragraph propertyPingCounttitle = new Paragraph("2.1.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle);
			Paragraph propertyResCounttitle = new Paragraph("2.1.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle);
			Paragraph propertyResCounttitle_cpu = new Paragraph("2.1.3 ������ CPU TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_cpu);
			Paragraph propertyResCounttitle_disc = new Paragraph("2.1.4 ������ ����ʹ���� TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_disc);
			document.add(new Paragraph("\n"));
			////��������豸
			Paragraph propertyCounttitle_net = new Paragraph("2.2 �����豸��ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle_net);
			Paragraph propertyPingCounttitle_net = new Paragraph("2.2.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle_net);
			if(netDevicePath.length>0&&netDevicePath[0]!=null){
				Image imgNetPathPing = Image.getInstance(netDevicePath[0]);
				imgNetPathPing.setAbsolutePosition(0, 0);
				imgNetPathPing.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgNetPathPing);
				document.add(new Paragraph("\n"));
				
			}
			Paragraph propertyResCounttitle_net = new Paragraph("2.2.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle_net);
			if(netDevicePath.length>0&&netDevicePath[1]!=null){
				Image imgNetPathRespones = Image.getInstance(netDevicePath[1]);
				imgNetPathRespones.setAbsolutePosition(0, 0);
				imgNetPathRespones.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgNetPathRespones);
				document.add(new Paragraph("\n"));
				
				int width4[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
				Table aTable4 = new Table(8);
				aTable4.setWidths(width4);
				aTable4.setWidth(100); // ռҳ���� 100%
				aTable4.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable4.setAutoFillEmptyCells(true); // �Զ�����
				aTable4.setBorderWidth(1); // �߿���
				aTable4.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable4.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable4.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable4.setBorder(2);// �߿�
				aTable4.endHeaders();
				Cell c4 = new Cell("���");
				c4.setBackgroundColor(Color.LIGHT_GRAY);
				aTable4.addCell(c4);
				Cell cell121 = new Cell("IP��ַ");
				Cell cell122 = new Cell("�豸����");
				Cell cell123 = new Cell("����ϵͳ");
				Cell cell124 = new Cell("ƽ����ͨ��");
				Cell cell125 = new Cell("崻�����(��)");
				Cell cell126 = new Cell("ƽ����Ӧʱ��(ms)");
				Cell cell127 = new Cell("�����Ӧʱ��(ms)");
				cell121.setBackgroundColor(Color.LIGHT_GRAY);
				cell122.setBackgroundColor(Color.LIGHT_GRAY);
				cell123.setBackgroundColor(Color.LIGHT_GRAY);
				cell124.setBackgroundColor(Color.LIGHT_GRAY);
				cell125.setBackgroundColor(Color.LIGHT_GRAY);
				cell126.setBackgroundColor(Color.LIGHT_GRAY);
				cell127.setBackgroundColor(Color.LIGHT_GRAY);
				cell121.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell122.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell123.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell124.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell125.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell126.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell127.setHorizontalAlignment(Element.ALIGN_CENTER);
				aTable4.addCell(cell121);
				aTable4.addCell(cell122);
				aTable4.addCell(cell123);
				aTable4.addCell(cell124);
				aTable4.addCell(cell125);
				aTable4.addCell(cell126);
				aTable4.addCell(cell127);		
				for (int i = 0; i < netListPing_Response.size(); i++) {
					List net_pinglist = (List) netListPing_Response.get(i);
					String ip = (String) net_pinglist.get(0);
					String equname_net = (String) net_pinglist.get(1);
					String osname_net = (String) net_pinglist.get(2);
					String avgping_net = (String) net_pinglist.get(3);
					String downnum_net = (String) net_pinglist.get(4);
					String responseavg_net = (String) net_pinglist.get(5);
					String responsemax_net = (String) net_pinglist.get(6);
					Cell cell1_13 = new Cell(i + 1 + "");
					Cell cel1_13 = new Cell(ip);
					Cell cel17_13 = new Cell(equname_net);
					Cell cell18_13 = new Cell(osname_net);
					Cell cell9_13 = new Cell(avgping_net);
					Cell cell110_13 = new Cell(downnum_net);
					Cell cell115_13 = new Cell(responseavg_net.replace("����", ""));
					Cell cell16_13 = new Cell(responsemax_net.replace("����", ""));
					cell1_13.setHorizontalAlignment(Element.ALIGN_CENTER); // ����
					cel1_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cel17_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell110_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell115_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable4.addCell(cell1_13);
					aTable4.addCell(cel1_13);
					aTable4.addCell(cel17_13);
					aTable4.addCell(cell18_13);
					aTable4.addCell(cell9_13);
					aTable4.addCell(cell110_13);
					aTable4.addCell(cell115_13);
					aTable4.addCell(cell16_13);
				}
				document.add(aTable4);
				document.add(new Paragraph("\n"));
			}
			
			Paragraph propertyResCounttitle_Netcpu = new Paragraph("2.2.3 �����豸 CPU TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_Netcpu);
			document.add(new Paragraph("\n"));
			if(!nm_cpu.isEmpty()){
				chartList_netCpu = (ArrayList<String>) nm_cpu.get("chart");
				if(chartList_netCpu.get(0)!=null){
					netCpu_utilization = chartList_netCpu.get(0);
				}
				Image img_netCpuUtilization = Image.getInstance(netCpu_utilization);
				img_netCpuUtilization.setAbsolutePosition(0, 0);
				img_netCpuUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(img_netCpuUtilization);
				document.add(new Paragraph("\n"));
			}
			Paragraph propertyResCounttitle_netReport = new Paragraph("2.2.4 �����豸�˿ڡ�������١��������� TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_netReport);
			if(!netPort.isEmpty()){
				String flage = null;
				List netUtilList = (List)netPort.get("head");
				if(netUtilList.size()>0){
					for(int i=0;i<netUtilList.size();i++){
						 flage = (String)netUtilList.get(i);
						 if(i==0){
							 	Image img_netReportInUtilization = Image.getInstance(flage);
							 	img_netReportInUtilization.setAbsolutePosition(0, 0);
							 	img_netReportInUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
								document.add(img_netReportInUtilization);
						 }else{
							 Image img_netReportOutUtilization = Image.getInstance(flage);
							 img_netReportOutUtilization.setAbsolutePosition(0, 0);
							 img_netReportOutUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
							 document.add(img_netReportOutUtilization);
						 }
					}
				}
				document.add(new Paragraph("\n"));
			}
			document.close();
		}else if(contentType=="host"){
			// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> tableList = null;
			// ���ͼƬ·��
			ArrayList<String> chartList_cpu = new ArrayList<String>();
			ArrayList<String> chartList_disk = new ArrayList<String>();
			ArrayList<String> chartList_netCpu = new ArrayList<String>();
			String cpu_utilization="",disk_utilization="",netCpu_utilization="";
			String [][]tableData = eventDataStr;
			Paragraph eventCounttitle_data;
			//--------------------------��������ʽ����-------------------------------
			// ����ֽ�Ŵ�С
			Document document = new Document(PageSize.A4);
			// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
			RtfWriter2.getInstance(document, new FileOutputStream(file));
			document.open();
			// ������������
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// ����������
			Font titleFont = new Font(bfChinese, 14, Font.BOLD);
			Font smtitleFontOne = new Font(bfChinese, 13, Font.BOLD);
			Font smtitleFont = new Font(bfChinese, 12, Font.BOLD);
			// ����������
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			//--------------------------��������ʽ���� end-------------------------------
			
			//--------------------------  ����  ---------------------------
			Paragraph title = new Paragraph("�豸��Ϣͳ���ܱ���", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("\n"));
			//-------------------------- end ------------------------------
			
			//--------------------------һ���¼�ͳ�� 	  ---------------------------
			Paragraph eventCounttitleOne = new Paragraph("һ �¼�ͳ��", smtitleFontOne);
			document.add(eventCounttitleOne);
			document.add(new Paragraph("\n"));
			Paragraph eventCounttitle = new Paragraph("1.1�豸���ϸ澯��Ϣͳ��", smtitleFont);
			document.add(eventCounttitle);
			document.add(new Paragraph("\n"));
			if(levencount_int>0){
				eventCounttitle_data = new Paragraph("�¼�����:"+levencount_int+"������  ���ظ澯:"
						+leven3_int+"�Ρ������澯:"+leven2_int+"�Ρ���ͨ�澯:"+leven1_int+"��", contextFont);
			}else{
				eventCounttitle_data = new Paragraph("�¼�����:"+(0)+"������  ���ظ澯:"
						+0+"�Ρ������澯:"+0+"�Ρ���ͨ�澯:"+0+"��", contextFont);
			}
			document.add(eventCounttitle_data);
			//--------------------------1.1���¼�ͳ�� end---------------------------
			
			//--------------------------1.2 �豸�澯��ϸ ------------------------------
			Paragraph smtitle = new Paragraph("1.2�豸�澯��ϸ", smtitleFont);
			// ���ñ����ʽ���뷽ʽ
			smtitle.setAlignment(Element.ALIGN_LEFT);
			// title.setFont(titleFont);
			document.add(smtitle);
			// ���� Table ���
			if(eventlist != null && eventlist.size() > 0){
				Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
				Table aTable = new Table(12);
				int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55};
				aTable.setWidths(width);
				aTable.setWidth(100); // ռҳ���� 90%
				aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable.setAutoFillEmptyCells(true); // �Զ�����
				aTable.setBorderWidth(1); // �߿���
				aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable.setBorder(2);// �߿�
				aTable.endHeaders();
				Cell c = new Cell("���");
				c.setHorizontalAlignment(Element.ALIGN_CENTER);
				c.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(c);
				Cell cell1 = new Cell("IP��ַ");
				Cell cell2 = new Cell("�豸����");
				Cell cell3 = new Cell("����ϵͳ");
				Cell cell4 = new Cell("�¼�����");
				Cell cell5 = new Cell("��ͨ");
				Cell cell6 = new Cell("����");
				Cell cell7 = new Cell("����");
				Cell cell8 = new Cell("��ͨ���¼�");
				Cell cell9 = new Cell("�ڴ��¼�");
				Cell cell10 = new Cell("�����¼�");
				Cell cell11 = new Cell("cpu�¼�");
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(Color.LIGHT_GRAY);
				cell2.setBackgroundColor(Color.LIGHT_GRAY);
				cell3.setBackgroundColor(Color.LIGHT_GRAY);
				cell4.setBackgroundColor(Color.LIGHT_GRAY);
				cell5.setBackgroundColor(Color.LIGHT_GRAY);
				cell6.setBackgroundColor(Color.LIGHT_GRAY);
				cell7.setBackgroundColor(Color.LIGHT_GRAY);
				cell8.setBackgroundColor(Color.LIGHT_GRAY);
				cell9.setBackgroundColor(Color.LIGHT_GRAY);
				cell10.setBackgroundColor(Color.LIGHT_GRAY);
				cell11.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(cell1);
				aTable.addCell(cell2);
				aTable.addCell(cell3);
				aTable.addCell(cell4);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell11);
				for (int i = 0; i < eventlist.size(); i++) {
					List _eventlist = (List) eventlist.get(i);
					String ip = (String) _eventlist.get(0);
					String equname = (String) _eventlist.get(1);
					String osname = (String) _eventlist.get(2);
					String sum = (String) _eventlist.get(3);
					String levelone = (String) _eventlist.get(4);
					String leveltwo = (String) _eventlist.get(5);
					String levelthree = (String) _eventlist.get(6);
					String pingvalue = (String) _eventlist.get(7);
					String memvalue = (String) _eventlist.get(8);
					String diskvalue = (String) _eventlist.get(9);
					String cpuvalue = (String) _eventlist.get(10);
					Cell cell13 = new Cell(i + 1 + "");
					Cell cell14 = new Cell(ip);
					Cell cell15 = new Cell(equname);
					Cell cell16 = new Cell(osname);
					Cell cell17 = new Cell(sum);
					Cell cell18 = new Cell(levelone);
					Cell cell19 = new Cell(leveltwo);
					Cell cell20 = new Cell(levelthree);
					Cell cell21 = new Cell(pingvalue);
					Cell cell22 = new Cell(memvalue);
					Cell cell23 = new Cell(diskvalue);
					Cell cell24 = new Cell(cpuvalue);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable.addCell(cell13);
					aTable.addCell(cell14);
					aTable.addCell(cell15);
					aTable.addCell(cell16);
					aTable.addCell(cell17);
					aTable.addCell(cell18);
					aTable.addCell(cell19);
					aTable.addCell(cell20);
					aTable.addCell(cell21);
					aTable.addCell(cell22);
					aTable.addCell(cell23);
					aTable.addCell(cell24);
				}
				document.add(aTable);
				document.add(new Paragraph("\n"));
			}
			//--------------------------1.2 �豸�澯��ϸ end------------------------------
			
			//-------------------------- 1.3���¼��澯����-----------------------------
			//�¼��澯����
			Paragraph eventTrend = new Paragraph("1.3�¼��澯����", smtitleFont);
			document.add(eventTrend);
			
			if(tableData!=null && tableData[1][0] != null){
				Table aTable2 = new Table(5);
				int width2[] = { 75, 55, 55, 55, 55};
				aTable2.setWidths(width2);
				aTable2.setWidth(100); // ռҳ���� 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable2.setAutoFillEmptyCells(true); // �Զ�����
				aTable2.setBorderWidth(1); // �߿���
				aTable2.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable2.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable2.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable2.setBorder(2);// �߿�
				aTable2.endHeaders();
				Cell cell21 = new Cell("���");
				Cell cell22 = new Cell("��ʾ");
				Cell cell23 = new Cell("��ͨ");
				Cell cell24 = new Cell("����");
				Cell cell25 = new Cell("����");
				cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell25.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setBackgroundColor(Color.BLUE);
				cell23.setBackgroundColor(Color.YELLOW);
				cell24.setBackgroundColor(Color.ORANGE);
				cell25.setBackgroundColor(Color.RED);
				aTable2.addCell(cell21);
				aTable2.addCell(cell22);
				aTable2.addCell(cell23);
				aTable2.addCell(cell24);
				aTable2.addCell(cell25);
				 for (int i = 0; i < tableData.length; i++) {
//					 System.out.println("------------------------012345678---------------------------"+i);
					 for (int j = 0; j < tableData[i].length; j++) {
						String flag = i+"";
						String category = tableData[i][j];
						Cell cell211 = new Cell(category);
						cell211.setHorizontalAlignment(Element.ALIGN_CENTER);
						aTable2.addCell(cell211,i+1,j);	//i�У�j����
//						System.out.println("------------------------eventName---------------------------"+category);
					 }
				 }
				document.add(aTable2);
//				String pingpath = (String) session.getAttribute("pingpath");
				if(chartPath!=null){
					Image img = Image.getInstance(chartPath);
					img.setAbsolutePosition(0, 0);
					img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img);
					document.add(new Paragraph("\n"));
				}
			}
			//-------------------------- 1.3���¼��澯���� end-----------------------------
			
			//-------------------------- �� �������Ʒ���(top10)----------------------------
			//�������Ʒ���
			Paragraph propertyCounttitleTwo = new Paragraph("�� �������Ʒ���(top10)", smtitleFontOne);
			document.add(propertyCounttitleTwo);
			document.add(new Paragraph("\n"));
			Paragraph propertyCounttitle = new Paragraph("2.1 ��������ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle);
			Paragraph propertyPingCounttitle = new Paragraph("2.1.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle);
			if(hostDevicePath.length>0&&hostDevicePath[0]!=null)
			{
				Image imgHostPathPing = Image.getInstance(hostDevicePath[0]);
				imgHostPathPing.setAbsolutePosition(0, 0);
				imgHostPathPing.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgHostPathPing);

				document.add(new Paragraph("\n"));
			}
			Paragraph propertyResCounttitle = new Paragraph("2.1.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle);
			if(hostDevicePath.length>0&&hostDevicePath[0]!=null){
				Image imghostPathRespones = Image.getInstance(hostDevicePath[1]);
				imghostPathRespones.setAbsolutePosition(0, 0);
				imghostPathRespones.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imghostPathRespones);
				//2.1.2 ��Ӧʱ������
				if(hostListPing_Response!=null){
					int width3[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
					Table aTable3 = new Table(8);
					aTable3.setWidths(width3);
					aTable3.setWidth(100); // ռҳ���� 100%
					aTable3.setAlignment(Element.ALIGN_CENTER);// ������ʾ
					aTable3.setAutoFillEmptyCells(true); // �Զ�����
					aTable3.setBorderWidth(1); // �߿���
					aTable3.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
					aTable3.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
					aTable3.setSpacing(0);// ����Ԫ��֮��ļ��
					aTable3.setBorder(2);// �߿�
					aTable3.endHeaders();
					Cell c3 = new Cell("���");
					c3.setBackgroundColor(Color.LIGHT_GRAY);
					aTable3.addCell(c3);
					Cell cell1111 = new Cell("IP��ַ");
					Cell cell111 = new Cell("�豸����");
					Cell cell12 = new Cell("����ϵͳ");
					Cell cell13 = new Cell("ƽ����ͨ��");
					Cell cell14 = new Cell("崻�����(��)");
					Cell cell113 = new Cell("ƽ����Ӧʱ��(ms)");
					Cell cell114 = new Cell("�����Ӧʱ��(ms)");
					cell1111.setBackgroundColor(Color.LIGHT_GRAY);
					cell111.setBackgroundColor(Color.LIGHT_GRAY);
					cell12.setBackgroundColor(Color.LIGHT_GRAY);
					cell13.setBackgroundColor(Color.LIGHT_GRAY);
					cell14.setBackgroundColor(Color.LIGHT_GRAY);
					cell113.setBackgroundColor(Color.LIGHT_GRAY);
					cell114.setBackgroundColor(Color.LIGHT_GRAY);
					cell1111.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell111.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell113.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell114.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable3.addCell(cell1111);
					aTable3.addCell(cell111);
					aTable3.addCell(cell12);
					aTable3.addCell(cell13);
					aTable3.addCell(cell14);
					aTable3.addCell(cell113);
					aTable3.addCell(cell114);
					for (int i = 0; i < hostListPing_Response.size(); i++) {
						List _pinglist = (List) hostListPing_Response.get(i);
						String ip = (String) _pinglist.get(0);
						String equname = (String) _pinglist.get(1);
						String osname = (String) _pinglist.get(2);
						String avgping = (String) _pinglist.get(3);
						String downnum = (String) _pinglist.get(4);
						String responseavg = (String) _pinglist.get(5);
						String responsemax = (String) _pinglist.get(6);
						Cell cell1_3 = new Cell(i + 1 + "");
						Cell cel1_3 = new Cell(ip);
						Cell cel17_3 = new Cell(equname);
						Cell cell18_3 = new Cell(osname);
						Cell cell9_3 = new Cell(avgping);
						Cell cell110_3 = new Cell(downnum);
						Cell cell115_3 = new Cell(responseavg.replace("����", ""));
						Cell cell16_3 = new Cell(responsemax.replace("����", ""));
						cell1_3.setHorizontalAlignment(Element.ALIGN_CENTER); // ����
						cel1_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cel17_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell18_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell9_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell110_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell115_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell16_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						aTable3.addCell(cell1_3);
						aTable3.addCell(cel1_3);
						aTable3.addCell(cel17_3);
						aTable3.addCell(cell18_3);
						aTable3.addCell(cell9_3);
						aTable3.addCell(cell110_3);
						aTable3.addCell(cell115_3);
						aTable3.addCell(cell16_3);
					}
					document.add(aTable3);
					document.add(new Paragraph("\n"));
				}
			}
			Paragraph propertyResCounttitle_cpu = new Paragraph("2.1.3 ������ CPU TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_cpu);
			
			if(hm_cpu!=null&&!hm_cpu.isEmpty()){
				chartList_cpu = (ArrayList<String>) hm_cpu.get("chart");
				if(chartList_cpu.size()>0 && cpu_utilization!=null){
					cpu_utilization = chartList_cpu.get(0);
					Image img_cpuUtilization = Image.getInstance(cpu_utilization);
					img_cpuUtilization.setAbsolutePosition(0, 0);
					img_cpuUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img_cpuUtilization);
					document.add(new Paragraph("\n"));
				}
			}
			Paragraph propertyResCounttitle_disc = new Paragraph("2.1.4 ������ ����ʹ���� TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_disc);
			if(hm_disk!=null&&!hm_disk.isEmpty()){
				chartList_disk = (ArrayList<String>) hm_disk.get("chart");
				if(chartList_disk.size()>0 && chartList_disk!=null){
					disk_utilization = chartList_disk.get(0);
					Image img_diskUtilization = Image.getInstance(disk_utilization);
					img_diskUtilization.setAbsolutePosition(0, 0);
					img_diskUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img_diskUtilization);
					document.add(new Paragraph("\n"));
				}
			}
			//-------------------------- �� �������Ʒ���(top10) end----------------------------
			Paragraph propertyCounttitle_net = new Paragraph("2.2 �����豸��ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle_net);
			Paragraph propertyPingCounttitle_net = new Paragraph("2.2.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle_net);
			Paragraph propertyResCounttitle_net = new Paragraph("2.2.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle_net);
			Paragraph propertyResCounttitle_Netcpu = new Paragraph("2.2.3 �����豸 CPU TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_Netcpu);
			document.add(new Paragraph("\n"));
			Paragraph propertyResCounttitle_netReport = new Paragraph("2.2.4 �����豸�˿ڡ�������١��������� TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_netReport);
			document.add(new Paragraph("\n"));
			document.close();
		}else if(contentType=="nethost"){
			// ��ű����Ϣ
			ArrayList<ArrayList<String[]>> tableList = null;
			// ���ͼƬ·��
			ArrayList<String> chartList_cpu = new ArrayList<String>();
			ArrayList<String> chartList_disk = new ArrayList<String>();
			ArrayList<String> chartList_netCpu = new ArrayList<String>();
			String cpu_utilization="",disk_utilization="",netCpu_utilization="";
			String [][]tableData = eventDataStr;
			Paragraph eventCounttitle_data;
			//--------------------------��������ʽ����-------------------------------
			// ����ֽ�Ŵ�С
			Document document = new Document(PageSize.A4);
			// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
			RtfWriter2.getInstance(document, new FileOutputStream(file));
			document.open();
			// ������������
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
			// ����������
			Font titleFont = new Font(bfChinese, 14, Font.BOLD);
			Font smtitleFontOne = new Font(bfChinese, 13, Font.BOLD);
			Font smtitleFont = new Font(bfChinese, 12, Font.BOLD);
			// ����������
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			//--------------------------��������ʽ���� end-------------------------------
			
			//--------------------------  ����  ---------------------------
			Paragraph title = new Paragraph("�豸��Ϣͳ���ܱ���", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph("\n"));
			//-------------------------- end ------------------------------
			
			//--------------------------һ���¼�ͳ�� 	  ---------------------------
			Paragraph eventCounttitleOne = new Paragraph("һ �¼�ͳ��", smtitleFontOne);
			document.add(eventCounttitleOne);
			document.add(new Paragraph("\n"));
			Paragraph eventCounttitle = new Paragraph("1.1�豸���ϸ澯��Ϣͳ��", smtitleFont);
			document.add(eventCounttitle);
			document.add(new Paragraph("\n"));
			if(levencount_int>0){
				eventCounttitle_data = new Paragraph("�¼�����:"+levencount_int+"������  ���ظ澯:"
						+leven3_int+"�Ρ������澯:"+leven2_int+"�Ρ���ͨ�澯:"+leven1_int+"��", contextFont);
			}else{
				eventCounttitle_data = new Paragraph("�¼�����:"+(0)+"������  ���ظ澯:"
						+0+"�Ρ������澯:"+0+"�Ρ���ͨ�澯:"+0+"��", contextFont);
			}
			document.add(eventCounttitle_data);
			//--------------------------1.1���¼�ͳ�� end---------------------------
			
			//--------------------------1.2 �豸�澯��ϸ ------------------------------
			Paragraph smtitle = new Paragraph("1.2�豸�澯��ϸ", smtitleFont);
			// ���ñ����ʽ���뷽ʽ
			smtitle.setAlignment(Element.ALIGN_LEFT);
			// title.setFont(titleFont);
			document.add(smtitle);
			// ���� Table ���
			if(eventlist != null && eventlist.size() > 0){
				Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
				Table aTable = new Table(12);
				int width[] = { 30, 50, 55, 55, 55, 40, 40, 40, 55, 55, 55, 55};
				aTable.setWidths(width);
				aTable.setWidth(100); // ռҳ���� 90%
				aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable.setAutoFillEmptyCells(true); // �Զ�����
				aTable.setBorderWidth(1); // �߿���
				aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable.setBorder(2);// �߿�
				aTable.endHeaders();
				Cell c = new Cell("���");
				c.setHorizontalAlignment(Element.ALIGN_CENTER);
				c.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(c);
				Cell cell1 = new Cell("IP��ַ");
				Cell cell2 = new Cell("�豸����");
				Cell cell3 = new Cell("����ϵͳ");
				Cell cell4 = new Cell("�¼�����");
				Cell cell5 = new Cell("��ͨ");
				Cell cell6 = new Cell("����");
				Cell cell7 = new Cell("����");
				Cell cell8 = new Cell("��ͨ���¼�");
				Cell cell9 = new Cell("�ڴ��¼�");
				Cell cell10 = new Cell("�����¼�");
				Cell cell11 = new Cell("cpu�¼�");
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(Color.LIGHT_GRAY);
				cell2.setBackgroundColor(Color.LIGHT_GRAY);
				cell3.setBackgroundColor(Color.LIGHT_GRAY);
				cell4.setBackgroundColor(Color.LIGHT_GRAY);
				cell5.setBackgroundColor(Color.LIGHT_GRAY);
				cell6.setBackgroundColor(Color.LIGHT_GRAY);
				cell7.setBackgroundColor(Color.LIGHT_GRAY);
				cell8.setBackgroundColor(Color.LIGHT_GRAY);
				cell9.setBackgroundColor(Color.LIGHT_GRAY);
				cell10.setBackgroundColor(Color.LIGHT_GRAY);
				cell11.setBackgroundColor(Color.LIGHT_GRAY);
				aTable.addCell(cell1);
				aTable.addCell(cell2);
				aTable.addCell(cell3);
				aTable.addCell(cell4);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell11);
				for (int i = 0; i < eventlist.size(); i++) {
					List _eventlist = (List) eventlist.get(i);
					String ip = (String) _eventlist.get(0);
					String equname = (String) _eventlist.get(1);
					String osname = (String) _eventlist.get(2);
					String sum = (String) _eventlist.get(3);
					String levelone = (String) _eventlist.get(4);
					String leveltwo = (String) _eventlist.get(5);
					String levelthree = (String) _eventlist.get(6);
					String pingvalue = (String) _eventlist.get(7);
					String memvalue = (String) _eventlist.get(8);
					String diskvalue = (String) _eventlist.get(9);
					String cpuvalue = (String) _eventlist.get(10);
					Cell cell13 = new Cell(i + 1 + "");
					Cell cell14 = new Cell(ip);
					Cell cell15 = new Cell(equname);
					Cell cell16 = new Cell(osname);
					Cell cell17 = new Cell(sum);
					Cell cell18 = new Cell(levelone);
					Cell cell19 = new Cell(leveltwo);
					Cell cell20 = new Cell(levelthree);
					Cell cell21 = new Cell(pingvalue);
					Cell cell22 = new Cell(memvalue);
					Cell cell23 = new Cell(diskvalue);
					Cell cell24 = new Cell(cpuvalue);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell15.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell17.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell19.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable.addCell(cell13);
					aTable.addCell(cell14);
					aTable.addCell(cell15);
					aTable.addCell(cell16);
					aTable.addCell(cell17);
					aTable.addCell(cell18);
					aTable.addCell(cell19);
					aTable.addCell(cell20);
					aTable.addCell(cell21);
					aTable.addCell(cell22);
					aTable.addCell(cell23);
					aTable.addCell(cell24);
				}
				document.add(aTable);
				document.add(new Paragraph("\n"));
			}
			//--------------------------1.2 �豸�澯��ϸ end------------------------------
			
			//-------------------------- 1.3���¼��澯����-----------------------------
			//�¼��澯����
			Paragraph eventTrend = new Paragraph("1.3�¼��澯����", smtitleFont);
			document.add(eventTrend);
			
			if(tableData!=null && tableData[1][0] != null){
				Table aTable2 = new Table(5);
				int width2[] = { 75, 55, 55, 55, 55};
				aTable2.setWidths(width2);
				aTable2.setWidth(100); // ռҳ���� 90%
				aTable2.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable2.setAutoFillEmptyCells(true); // �Զ�����
				aTable2.setBorderWidth(1); // �߿���
				aTable2.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable2.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable2.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable2.setBorder(2);// �߿�
				aTable2.endHeaders();
				Cell cell21 = new Cell("���");
				Cell cell22 = new Cell("��ʾ");
				Cell cell23 = new Cell("��ͨ");
				Cell cell24 = new Cell("����");
				Cell cell25 = new Cell("����");
				cell21.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell24.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell25.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22.setBackgroundColor(Color.BLUE);
				cell23.setBackgroundColor(Color.YELLOW);
				cell24.setBackgroundColor(Color.ORANGE);
				cell25.setBackgroundColor(Color.RED);
				aTable2.addCell(cell21);
				aTable2.addCell(cell22);
				aTable2.addCell(cell23);
				aTable2.addCell(cell24);
				aTable2.addCell(cell25);
				 for (int i = 0; i < tableData.length; i++) {
//					 System.out.println("------------------------012345678---------------------------"+i);
					 for (int j = 0; j < tableData[i].length; j++) {
						String flag = i+"";
						String category = tableData[i][j];
						Cell cell211 = new Cell(category);
						cell211.setHorizontalAlignment(Element.ALIGN_CENTER);
						aTable2.addCell(cell211,i+1,j);	//i�У�j����
//						System.out.println("------------------------eventName---------------------------"+category);
					 }
				 }
				document.add(aTable2);
//				String pingpath = (String) session.getAttribute("pingpath");
				if(chartPath!=null){
					Image img = Image.getInstance(chartPath);
					img.setAbsolutePosition(0, 0);
					img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img);
					document.add(new Paragraph("\n"));
				}
			}
			//-------------------------- 1.3���¼��澯���� end-----------------------------
			
			//-------------------------- �� �������Ʒ���(top10)----------------------------
			//�������Ʒ���
			Paragraph propertyCounttitleTwo = new Paragraph("�� �������Ʒ���(top10)", smtitleFontOne);
			document.add(propertyCounttitleTwo);
			document.add(new Paragraph("\n"));
			Paragraph propertyCounttitle = new Paragraph("2.1 ��������ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle);
			Paragraph propertyPingCounttitle = new Paragraph("2.1.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle);
			if(hostDevicePath.length>0&&hostDevicePath[0]!=null)
			{
				Image imgHostPathPing = Image.getInstance(hostDevicePath[0]);
				imgHostPathPing.setAbsolutePosition(0, 0);
				imgHostPathPing.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgHostPathPing);

				document.add(new Paragraph("\n"));
			}
			Paragraph propertyResCounttitle = new Paragraph("2.1.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle);
			if(hostDevicePath.length>0&&hostDevicePath[0]!=null){
				Image imghostPathRespones = Image.getInstance(hostDevicePath[1]);
				imghostPathRespones.setAbsolutePosition(0, 0);
				imghostPathRespones.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imghostPathRespones);
				//2.1.2 ��Ӧʱ������
				if(hostListPing_Response!=null){
					int width3[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
					Table aTable3 = new Table(8);
					aTable3.setWidths(width3);
					aTable3.setWidth(100); // ռҳ���� 100%
					aTable3.setAlignment(Element.ALIGN_CENTER);// ������ʾ
					aTable3.setAutoFillEmptyCells(true); // �Զ�����
					aTable3.setBorderWidth(1); // �߿���
					aTable3.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
					aTable3.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
					aTable3.setSpacing(0);// ����Ԫ��֮��ļ��
					aTable3.setBorder(2);// �߿�
					aTable3.endHeaders();
					Cell c3 = new Cell("���");
					c3.setBackgroundColor(Color.LIGHT_GRAY);
					aTable3.addCell(c3);
					Cell cell1111 = new Cell("IP��ַ");
					Cell cell111 = new Cell("�豸����");
					Cell cell12 = new Cell("����ϵͳ");
					Cell cell13 = new Cell("ƽ����ͨ��");
					Cell cell14 = new Cell("崻�����(��)");
					Cell cell113 = new Cell("ƽ����Ӧʱ��(ms)");
					Cell cell114 = new Cell("�����Ӧʱ��(ms)");
					cell1111.setBackgroundColor(Color.LIGHT_GRAY);
					cell111.setBackgroundColor(Color.LIGHT_GRAY);
					cell12.setBackgroundColor(Color.LIGHT_GRAY);
					cell13.setBackgroundColor(Color.LIGHT_GRAY);
					cell14.setBackgroundColor(Color.LIGHT_GRAY);
					cell113.setBackgroundColor(Color.LIGHT_GRAY);
					cell114.setBackgroundColor(Color.LIGHT_GRAY);
					cell1111.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell111.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell113.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell114.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable3.addCell(cell1111);
					aTable3.addCell(cell111);
					aTable3.addCell(cell12);
					aTable3.addCell(cell13);
					aTable3.addCell(cell14);
					aTable3.addCell(cell113);
					aTable3.addCell(cell114);
					for (int i = 0; i < hostListPing_Response.size(); i++) {
						List _pinglist = (List) hostListPing_Response.get(i);
						String ip = (String) _pinglist.get(0);
						String equname = (String) _pinglist.get(1);
						String osname = (String) _pinglist.get(2);
						String avgping = (String) _pinglist.get(3);
						String downnum = (String) _pinglist.get(4);
						String responseavg = (String) _pinglist.get(5);
						String responsemax = (String) _pinglist.get(6);
						Cell cell1_3 = new Cell(i + 1 + "");
						Cell cel1_3 = new Cell(ip);
						Cell cel17_3 = new Cell(equname);
						Cell cell18_3 = new Cell(osname);
						Cell cell9_3 = new Cell(avgping);
						Cell cell110_3 = new Cell(downnum);
						Cell cell115_3 = new Cell(responseavg.replace("����", ""));
						Cell cell16_3 = new Cell(responsemax.replace("����", ""));
						cell1_3.setHorizontalAlignment(Element.ALIGN_CENTER); // ����
						cel1_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cel17_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell18_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell9_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell110_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell115_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell16_3.setHorizontalAlignment(Element.ALIGN_CENTER);
						aTable3.addCell(cell1_3);
						aTable3.addCell(cel1_3);
						aTable3.addCell(cel17_3);
						aTable3.addCell(cell18_3);
						aTable3.addCell(cell9_3);
						aTable3.addCell(cell110_3);
						aTable3.addCell(cell115_3);
						aTable3.addCell(cell16_3);
					}
					document.add(aTable3);
					document.add(new Paragraph("\n"));
				}
			}
			Paragraph propertyResCounttitle_cpu = new Paragraph("2.1.3 ������ CPU TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_cpu);
			
			if(hm_cpu!=null &&!hm_cpu.isEmpty()){
				chartList_cpu = (ArrayList<String>) hm_cpu.get("chart");
				if(chartList_cpu.size()>0 && chartList_cpu!=null){
					cpu_utilization = chartList_cpu.get(0);
					Image img_cpuUtilization = Image.getInstance(cpu_utilization);
					img_cpuUtilization.setAbsolutePosition(0, 0);
					img_cpuUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img_cpuUtilization);
					document.add(new Paragraph("\n"));
				}
			}
			Paragraph propertyResCounttitle_disc = new Paragraph("2.1.4 ������ ����ʹ���� TOP 10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_disc);
			if(hm_disk!=null&&!hm_disk.isEmpty()){
				chartList_disk = (ArrayList<String>) hm_disk.get("chart");
				if(chartList_disk.size()>0 && chartList_disk!=null){
					disk_utilization = chartList_disk.get(0);
					Image img_diskUtilization = Image.getInstance(disk_utilization);
					img_diskUtilization.setAbsolutePosition(0, 0);
					img_diskUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
					document.add(img_diskUtilization);
					document.add(new Paragraph("\n"));
				}
			}
			//-------------------------- �� �������Ʒ���(top10) end----------------------------
			
			//-------------------------- ��.�� �����豸��ͨ��top10 ���Ʒ��� ----------------------------
			
			Paragraph propertyCounttitle_net = new Paragraph("2.2 �����豸��ͨ��top10 ���Ʒ���", smtitleFontOne);
			document.add(propertyCounttitle_net);
			Paragraph propertyPingCounttitle_net = new Paragraph("2.2.1 ��ͨ������", smtitleFontOne);
			document.add(propertyPingCounttitle_net);
			if(netDevicePath.length>0&&netDevicePath[0]!=null){
				Image imgNetPathPing = Image.getInstance(netDevicePath[0]);
				imgNetPathPing.setAbsolutePosition(0, 0);
				imgNetPathPing.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgNetPathPing);
				document.add(new Paragraph("\n"));
				
			}
			Paragraph propertyResCounttitle_net = new Paragraph("2.2.2 ��Ӧʱ������", smtitleFontOne);
			document.add(propertyResCounttitle_net);
			if(netDevicePath.length>0&&netDevicePath[1]!=null){
				Image imgNetPathRespones = Image.getInstance(netDevicePath[1]);
				imgNetPathRespones.setAbsolutePosition(0, 0);
				imgNetPathRespones.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(imgNetPathRespones);
				document.add(new Paragraph("\n"));
				
				int width4[] = { 50, 50, 50, 70, 50, 50, 50, 50 };
				Table aTable4 = new Table(8);
				aTable4.setWidths(width4);
				aTable4.setWidth(100); // ռҳ���� 100%
				aTable4.setAlignment(Element.ALIGN_CENTER);// ������ʾ
				aTable4.setAutoFillEmptyCells(true); // �Զ�����
				aTable4.setBorderWidth(1); // �߿���
				aTable4.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
				aTable4.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
				aTable4.setSpacing(0);// ����Ԫ��֮��ļ��
				aTable4.setBorder(2);// �߿�
				aTable4.endHeaders();
				Cell c4 = new Cell("���");
				c4.setBackgroundColor(Color.LIGHT_GRAY);
				aTable4.addCell(c4);
				Cell cell121 = new Cell("IP��ַ");
				Cell cell122 = new Cell("�豸����");
				Cell cell123 = new Cell("����ϵͳ");
				Cell cell124 = new Cell("ƽ����ͨ��");
				Cell cell125 = new Cell("崻�����(��)");
				Cell cell126 = new Cell("ƽ����Ӧʱ��(ms)");
				Cell cell127 = new Cell("�����Ӧʱ��(ms)");
				cell121.setBackgroundColor(Color.LIGHT_GRAY);
				cell122.setBackgroundColor(Color.LIGHT_GRAY);
				cell123.setBackgroundColor(Color.LIGHT_GRAY);
				cell124.setBackgroundColor(Color.LIGHT_GRAY);
				cell125.setBackgroundColor(Color.LIGHT_GRAY);
				cell126.setBackgroundColor(Color.LIGHT_GRAY);
				cell127.setBackgroundColor(Color.LIGHT_GRAY);
				cell121.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell122.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell123.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell124.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell125.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell126.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell127.setHorizontalAlignment(Element.ALIGN_CENTER);
				aTable4.addCell(cell121);
				aTable4.addCell(cell122);
				aTable4.addCell(cell123);
				aTable4.addCell(cell124);
				aTable4.addCell(cell125);
				aTable4.addCell(cell126);
				aTable4.addCell(cell127);		
				for (int i = 0; i < netListPing_Response.size(); i++) {
					List net_pinglist = (List) netListPing_Response.get(i);
					String ip = (String) net_pinglist.get(0);
					String equname_net = (String) net_pinglist.get(1);
					String osname_net = (String) net_pinglist.get(2);
					String avgping_net = (String) net_pinglist.get(3);
					String downnum_net = (String) net_pinglist.get(4);
					String responseavg_net = (String) net_pinglist.get(5);
					String responsemax_net = (String) net_pinglist.get(6);
					Cell cell1_13 = new Cell(i + 1 + "");
					Cell cel1_13 = new Cell(ip);
					Cell cel17_13 = new Cell(equname_net);
					Cell cell18_13 = new Cell(osname_net);
					Cell cell9_13 = new Cell(avgping_net);
					Cell cell110_13 = new Cell(downnum_net);
					Cell cell115_13 = new Cell(responseavg_net.replace("����", ""));
					Cell cell16_13 = new Cell(responsemax_net.replace("����", ""));
					cell1_13.setHorizontalAlignment(Element.ALIGN_CENTER); // ����
					cel1_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cel17_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell18_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell110_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell115_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell16_13.setHorizontalAlignment(Element.ALIGN_CENTER);
					aTable4.addCell(cell1_13);
					aTable4.addCell(cel1_13);
					aTable4.addCell(cel17_13);
					aTable4.addCell(cell18_13);
					aTable4.addCell(cell9_13);
					aTable4.addCell(cell110_13);
					aTable4.addCell(cell115_13);
					aTable4.addCell(cell16_13);
				}
				document.add(aTable4);
				document.add(new Paragraph("\n"));
			}
			
			Paragraph propertyResCounttitle_Netcpu = new Paragraph("2.2.3 �����豸 CPU TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_Netcpu);
			document.add(new Paragraph("\n"));
			if(!nm_cpu.isEmpty()){
				chartList_netCpu = (ArrayList<String>) nm_cpu.get("chart");
				if(chartList_netCpu.get(0)!=null){
					netCpu_utilization = chartList_netCpu.get(0);
				}
				Image img_netCpuUtilization = Image.getInstance(netCpu_utilization);
				img_netCpuUtilization.setAbsolutePosition(0, 0);
				img_netCpuUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
				document.add(img_netCpuUtilization);
				document.add(new Paragraph("\n"));
			}
			Paragraph propertyResCounttitle_netReport = new Paragraph("2.2.4 �����豸�˿ڡ�������١��������� TOP10 ���Ʒ���", smtitleFontOne);
			document.add(propertyResCounttitle_netReport);
			if(!netPort.isEmpty()){
				String flage = null;
				List netUtilList = (List)netPort.get("head");
				if(netUtilList.size()>0){
					for(int i=0;i<netUtilList.size();i++){
						 flage = (String)netUtilList.get(i);
						 if(i==0){
							 	Image img_netReportInUtilization = Image.getInstance(flage);
							 	img_netReportInUtilization.setAbsolutePosition(0, 0);
							 	img_netReportInUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
								document.add(img_netReportInUtilization);
						 }else{
							 Image img_netReportOutUtilization = Image.getInstance(flage);
							 img_netReportOutUtilization.setAbsolutePosition(0, 0);
							 img_netReportOutUtilization.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
							 document.add(img_netReportOutUtilization);
						 }
					}
				}
				document.add(new Paragraph("\n"));
			}
			document.close();
			//-------------------------- ��.�� �����豸��ͨ��top10 ���Ʒ��� end----------------------------
		}
	}
	/**
	 * ��ʱִ�в�ѯ���񷽷�
	 * @return
	 */
	public String downloadReportWeek_time(CompreReportWeekInfo sr_,int i) {
		int weekDay = new CalDateUtil().getNowDayWeek();
		int weekDayHour = new CalDateUtil().getNowDayWeekHour();
//		String sql = "select * from nms_comprereportweek_resources where sendOtherDay="+weekDay+" and sendTime="+weekDayHour;
//		String sql = "select * from nms_comprereportweek_resources where sendOtherDay=1 and sendTime=03";
		String content_tyep = "";
		String filePath = null;
//		DBManager dbManager = new DBManager();
//		ResultSet rs = null;
		//�����豸���¼�����
		List eventlist = new ArrayList();
		String chartPath = null;
		String [][]eventDataStr = new String[8][3];	
		String [] hostDevicePath = new String [1];
		HashMap hm = new HashMap();
		HashMap nm = new HashMap();
		HashMap hm_disk = new HashMap();
		String [] netDevicePath = new String [1];
		HashMap netPort = new HashMap();
				String ids = sr_.getIds();
				String exportType="doc";
				String filename="/temp/compreWeek_report"+i+".doc";
				filePath=ResourceCenter.getInstance().getSysPath()+filename;//D:\zgqx\afunms-qxj\tomcat-6.0.18\webapps\afunms\/temp/compreWeek_report.doc
				//String����ϲ�
				String[] c = this.countIds(ids);
				String[] idValue=this.getIdValue(ids);
				String[] netIds = this.getIdsValues(idValue, "net|");
				String[] hostIds = this.getIdsValues(idValue, "host|");
				if(netIds==null){
					content_tyep = "host";
				}else if(hostIds==null){
					content_tyep = "net";
				}else if(hostIds!=null && netIds!=null){
					content_tyep = "nethost";
				}
				//�õ���ǰʱ��
				Date nowDate = new CalDateUtil().getNowDate();
				//�õ���ǰ���ڵ���һ���ܵ���һ
				Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
				//�õ���ǰ���ڵ���һ���ܵ����һ��
				String toTime = new CalDateUtil().getEndWeek(pastDateM);
				String startTime = new CalDateUtil().convert(pastDateM);
				//�¼�ͳ��
				if(!sr_.getEventCount().equals("0") && sr_.getEventCount()!=null){
					//�����豸���¼�����
					eventlist = this.hostevent(startTime,toTime,c,netIds,hostIds);
					//�¼�ͳ��
					this.countEvent(startTime,toTime,c);
				}
				//�¼��澯���Ʒ���
				if(!sr_.getEventAnalyze().equals("0") && sr_.getEventAnalyze()!=null){
					//�澯������Ϣͳ��
					eventDataStr = this.getTableWeekData(startTime,toTime);
					//���ɸ澯��Ϣͳ��ͼƬ--����ͼƬ·��
//					chartPath = this.makeJfreeChartDataForDay(c);
					chartPath = makeJfreeChartDataForWeek_line(c);
				}
				//����top
				if(!sr_.getHostTopAnalyze().endsWith("0") && sr_.getHostTopAnalyze()!=null){
					//�����豸 ��ͨ�ʡ���Ӧ�ʣ���Ϣ��ѯ
					hostDevicePath = this.devicePing(hostIds);
					//���� cpu top10 
					hm = this.exportReport(hostIds,"host",filePath,startTime,toTime);
					//���� disk
					hm_disk = this.exportReport_disk(hostIds,"host",filePath,startTime,toTime);
				}
				//����top
				if(!sr_.getNetTopAnalyze().endsWith("0") && sr_.getNetTopAnalyze()!=null){
					//�����豸 ��ͨ�ʡ���Ӧ�ʡ���Ϣ��ѯ
					netDevicePath = this.devicePing_net(netIds);
					//�����豸 cpu top10 
					nm = this.exportReport_netCpu(netIds,"net",filePath,startTime,toTime);
					//�����豸�˿� TOP10
					netPort = this.exportReport_netPort(netIds,startTime,toTime);
				}
				//����word�������ط���
				this.createdocEvent(exportType,filePath,eventlist,eventDataStr,chartPath,hostDevicePath,hm,hm_disk,netDevicePath,nm,netPort,content_tyep);
				//--------------------jhl end--------------------
			
			return filePath;
	}
	/**
	 * word���������ܵ�
		}
		return null;��ڷ���
	 */ 
	public String downloadReportWeek() {
//		System.out.println("��ʱִ�в���(((((((((((((((((((((((((((((((((((((((((((:"+downloadReportWeek_time());
		String ids=getParaValue("ids");
		String type=getParaValue("type");
		String exportType=getParaValue("exportType");
		String filename="/temp/compreWeek_report.doc";
		String contentType="";
		if (ids==null||ids.equals("")||ids.equals("null")) {
    		String id = request.getParameter("id");
    		if(id.equals("null"))return null;
    		CompreReportWeekInfo report=new CompreReportWeekInfo();
    		CompreReportWeekDao dao=new CompreReportWeekDao();
    		report=(CompreReportWeekInfo) dao.findByID(id);
    		ids=report.getIds();
		}
		String filePath=ResourceCenter.getInstance().getSysPath()+filename;//D:\zgqx\afunms-qxj\tomcat-6.0.18\webapps\afunms\/temp/compreWeek_report.doc
		//--------------------jhl add--------------------
		//String����ϲ�
		String[] c = this.countIds(ids);
		String[] idValue=this.getIdValue(ids);
		String[] netIds = this.getIdsValues(idValue, "net|");
		String[] hostIds = this.getIdsValues(idValue, "host|");
		if(netIds!=null && hostIds!=null){
			contentType = "nethost";
		}else if(netIds==null){
			contentType = "host";
		}else if(hostIds==null){
			contentType = "net";
		}
		//�õ���ǰʱ��
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		//�õ���ǰ���ڵ���һ���ܵ����һ��
		String toTime = new CalDateUtil().getEndWeek(pastDateM);
		String startTime = new CalDateUtil().convert(pastDateM);
		//�����豸���¼�����
		List eventlist = this.hostevent(startTime,toTime,c,netIds,hostIds);
		//�¼�ͳ��
		this.countEvent(startTime,toTime,c);
		//�澯������Ϣͳ��
		String [][]eventDataStr = this.getTableWeekData(startTime,toTime);
//		System.out.println("eventDataStr--------------------------"+eventDataStr.toString());
		//���ɸ澯��Ϣͳ��ͼƬ--����ͼƬ·��
//		String chartPath = this.makeJfreeChartDataForDay(c);
		String chartPath =  this.makeJfreeChartDataForWeek_line(c);
		//�����豸 ��ͨ�ʡ���Ӧ�ʣ���Ϣ��ѯ
		String [] hostDevicePath = this.devicePing(hostIds);
		//���� cpu top10 
		HashMap hm = this.exportReport(hostIds,"host",filePath,startTime,toTime);
		//���� disk
		HashMap hm_disk = this.exportReport_disk(hostIds,"host",filePath,startTime,toTime);
		
		//�����豸 ��ͨ�ʡ���Ӧ�ʡ���Ϣ��ѯ
		String [] netDevicePath = this.devicePing_net(netIds);
		//�����豸 cpu top10 
		HashMap nm = this.exportReport_netCpu(netIds,"net",filePath,startTime,toTime);
		//�����豸�˿� TOP10
		HashMap netPort = this.exportReport_netPort(netIds,startTime,toTime);
		//����word�������ط���
		this.createdocEvent(exportType,filePath,eventlist,eventDataStr,chartPath,hostDevicePath,hm,hm_disk,netDevicePath,nm,netPort,contentType);
		//--------------------jhl end--------------------
		request.setAttribute("filename", filePath);
		return "/capreport/comprehensiveweek/download.jsp";
	}
	//�����豸��� ���� ������Ϣͳ��
	private HashMap exportReport_netPort(String []netIds,String startTime,String toTime){
		if(netIds !=null && netIds.length>0){
			List netList = new ArrayList();
			HostNodeDao netDao = new HostNodeDao();
			netList = netDao.loadNetwork(1);
			netDao.close();
			HashMap netMap = new HashMap();
			for(int i=0;i<netList.size();i++){
				netMap.put(((HostNode)netList.get(i)).getId()+"", ((HostNode)netList.get(i)).getIpAddress());
			}
			startTime=startTime+" 00:00:00";
			toTime=toTime+" 23:59:59";
			HashMap netValueMap=new HashMap();
			List inList = new ArrayList();				//�������
			List outList = new ArrayList();		
			HomeCollectDataManager hcdm = new HomeCollectDataManager();//��������
			List<CompreWeekReportStatic> outTemp = new ArrayList<CompreWeekReportStatic>();//����
			List<CompreWeekReportStatic> inTemp = new ArrayList<CompreWeekReportStatic>();//���
			for(int j=0;j<netIds.length;j++){
				HostNodeDao netdao = new HostNodeDao();
				HostNode node = netdao.loadHost(Integer.parseInt(netIds[j]));
				netdao.close();
				String flag = netIds[j];
				String ipAddress = (String)
				netMap.get(flag);
				String invalue = hcdm.getAllInutilhdxAvg("0",ipAddress, startTime, toTime);
				invalue = invalue == null? "0":invalue;
				CompreWeekReportStatic crsin = new CompreWeekReportStatic();
				crsin.setIp(ipAddress);
				crsin.setType("inutilhdx");
				crsin.setUnit("KB/s");
				crsin.setValue(Double.valueOf(invalue));
				inTemp.add(crsin);
			//����------------------------------
//				inList = compareTop(inTemp,10);//��� top10
			//����------------------------------
				
				String outvalue = hcdm.getAllOututilhdxAvg("0",ipAddress, startTime, toTime);//����
				outvalue = outvalue == null? "0":outvalue;
				CompreWeekReportStatic crsout = new CompreWeekReportStatic();
				crsout.setIp(ipAddress);
				crsout.setType("oututilhdx");
				crsout.setUnit("KB/s");
				crsout.setValue(Double.valueOf(outvalue));
				outTemp.add(crsout);
			//����------------------------------
//				outList = compareTop(outTemp,10);//���� top10
			//����------------------------------
			}
			netValueMap.put("utilIn", inTemp);
			netValueMap.put("utilOut", outTemp);
			HashMap net = createImapPath(netValueMap);
			return net;
		}
		return null;
	}
	//��jhl aaa
	private HashMap createImapPath(HashMap netValueMap){
		List netUtilInList=new ArrayList();
		List netUtilOutList=new ArrayList();
		HashMap net = new HashMap();
		ArrayList<String> netHeadList = new ArrayList<String>();//��ű���
		ArrayList<ArrayList<String[]>> netTableList = new ArrayList<ArrayList<String[]>>();
		netUtilInList = (List)netValueMap.get("utilIn");
		netUtilOutList = (List)netValueMap.get("utilOut");
		ArrayList<String[]> netUtilInal = new ArrayList<String[]>();
		String[] netUtilInTitle = { "ip", "�������"};
		if(netUtilInList.size() > 0){
//			netHeadList.add("�����豸������� Top");
			netUtilInal.add(netUtilInTitle);
			for(int i=0;i<netUtilInList.size();i++){
				CompreWeekReportStatic crs = (CompreWeekReportStatic)netUtilInList.get(i);
				String[] arry = {crs.getIp(),crs.getValue()+""};
				netUtilInal.add(arry);
			}
			netTableList.add(netUtilInal);
			String netUtilInpath = makeJfreeChartDataForWeek(netUtilInList,"�����豸 ������� TOP","IP", "�������");
			netHeadList.add(0,netUtilInpath);
		}
		netUtilInList = (List)netValueMap.get("utilOut");
		ArrayList<String[]> netUtilOutal = new ArrayList<String[]>();
		String[] netUtilOutTitle = { "ip", "��������"};
		if(netUtilOutList.size() > 0){
//			netHeadList.add("�����豸�������� Top");
			netUtilOutal.add(netUtilOutTitle);
			for(int i=0;i<netUtilOutList.size();i++){
				CompreWeekReportStatic crs = (CompreWeekReportStatic)netUtilOutList.get(i);
				String[] arry = {crs.getIp(),crs.getValue()+""};
				netUtilOutal.add(arry);
			}
			netTableList.add(netUtilOutal);
			String netUtilOutpath = makeJfreeChartDataForWeek(netUtilOutList,"�����豸 �������� TOP","IP", "��������");
			netHeadList.add(1,netUtilOutpath);
		}
		net.put("head",netHeadList);
		net.put("table", netTableList);
		return net;
	}
	//�����豸�˿� jhl aaa
	private String makeJfreeChartDataForWeek(List list,String title, String xdesc, String ydesc){
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp" + File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				CompreWeekReportStatic crs = (CompreWeekReportStatic)list.get(j);
				dataset.addValue(crs.getValue(), crs.getType(), crs.getIp());
			}
			String chartkey = ChartCreator.createLineChart(title, xdesc, ydesc, dataset,chartWith, chartHigh);
			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
					.getInstance().getChartStorage().get(chartkey);
			CategoryPlot plot = chart.getChart().getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
			
//			StackedBarRenderer renderer = new StackedBarRenderer();
//			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
//			renderer.setItemLabelsVisible(true);
//			renderer.setBaseItemLabelsVisible(false);
//			plot.setRenderer(renderer);
			
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
	
	//�����豸 ��ͨ�ʡ���Ӧ������ͼ��ѯ
	private String[] devicePing_net(String []netIds){
		String [] devicePath;
		I_HostCollectData hostmanager = new HostCollectDataManager();
		//�õ���ǰʱ��
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		//�õ���ǰ���ڵ���һ���ܵ����һ��
		String toTime = new CalDateUtil().getEndWeek(pastDateM) + " 00:00:00";;
		String startTime = new CalDateUtil().convert(pastDateM) + " 23:59:59";

		// �������־ȡ���˿����¼�¼���б�
		////////////////////////////////��;��ȷ��//////////////////////////////////////////
		String orderflag = "ipaddress";
//		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
//			orderflag = getParaValue("orderflag");
//		}
		////////////////////////////////��;��ȷ��/////////////////////////////////////////////
		List orderList = new ArrayList();
		if (netIds != null && netIds.length > 0) {
			for (int i = 0; i < netIds.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(netIds[i]));
				dao.close();
				Hashtable pinghash = new Hashtable();
//				Hashtable cpuhash = new Hashtable();
//				Hashtable diskhash = new Hashtable();
				Hashtable responsehash = new Hashtable();
				try {
					pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", startTime,
						toTime);
					responsehash = hostmanager.getCategory(node.getIpAddress(),"Ping", "ResponseTime", startTime, 
							toTime);
//					cpuhash = hostmanager.getCategory(node.getIpAddress(), "cpu", "ConnectUtilization", startTime,
//							toTime);
//					diskhash = hostmanager.getCategory(node.getIpAddress(), "cpu", "ConnectUtilization", startTime,
//							toTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("hostnode", node);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("responsehash", responsehash);
				ipmemhash.put("ipaddress",node.getIpAddress()+"("+node.getAlias()+")");
//				ipmemhash.put("cpuhash", cpuhash);
//				ipmemhash.put("diskhash", diskhash);
				orderList.add(ipmemhash);
			}
		}
		List returnList = new ArrayList();

			// ��orderList����theValue��������
			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					if (pinghash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String pingconavg = "";
					String downnum = "";
					String responseavg = "";
					String responsemax = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					if (pinghash.get("downnum") != null)
						downnum = (String) pinghash.get("downnum");
					// ��ȡ��Ӧʱ��
					if (responsehash.get("avgpingcon") != null)
						responseavg = (String) responsehash.get("avgpingcon");
					if (responsehash.get("pingmax") != null)
						responsemax = (String) responsehash.get("pingmax");
					List ipdiskList = new ArrayList();
					ipdiskList.add(ip);
					ipdiskList.add(equname);
					ipdiskList.add(node.getType());
					ipdiskList.add(pingconavg);
					ipdiskList.add(downnum);
					ipdiskList.add(responseavg);
					ipdiskList.add(responsemax);
					returnList.add(ipdiskList);
				}
			}
//		}
		// **********************************************************
		netListPing_Response = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("avgping")) {
						String avgping = "";
						if (ipdiskList.get(3) != null) {
							avgping = (String) ipdiskList.get(3);
						}
						String _avgping = "";
						if (ipdiskList.get(3) != null) {
							_avgping = (String) _ipdiskList.get(3);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping
								.substring(0, _avgping.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("downnum")) {
						String downnum = "";
						if (ipdiskList.get(4) != null) {
							downnum = (String) ipdiskList.get(4);
						}
						String _downnum = "";
						if (ipdiskList.get(4) != null) {
							_downnum = (String) _ipdiskList.get(4);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0,
								avgping.length() - 2)).doubleValue() < new Double(
								_avgping.substring(0, _avgping.length() - 2))
								.doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("responsemax")) {
						String downnum = "";
						if (ipdiskList.get(6) != null) {
							downnum = (String) ipdiskList.get(6);
						}
						String _downnum = "";
						if (ipdiskList.get(6) != null) {
							_downnum = (String) _ipdiskList.get(6);
						}
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				netListPing_Response.add(ipdiskList);
				ipdiskList = null;
			}
		}
		ReportValue pingReportValue =  ReportHelper.getReportValue(orderList,"ping");
		ReportValue responseReportValue =  ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "��ͨ��", "ʱ��", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "��Ӧʱ��", "ʱ��", "");
		devicePath = new String[2];
		devicePath[0] = pingpath;
		devicePath[1] = responsetimepath;
		return devicePath;
	}
	
	
	//���� ��ͨ�ʡ���Ӧ������ͼ��ѯ
	private String[] devicePing(String []hostIds){
		String [] devicePath;
		I_HostCollectData hostmanager = new HostCollectDataManager();
		//�õ���ǰʱ��
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		//�õ���ǰ���ڵ���һ���ܵ����һ��
		String toTime = new CalDateUtil().getEndWeek(pastDateM) + " 00:00:00";;
		String startTime = new CalDateUtil().convert(pastDateM) + " 23:59:59";

		// �������־ȡ���˿����¼�¼���б�
		////////////////////////////////��;��ȷ��//////////////////////////////////////////
		String orderflag = "ipaddress";
//		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
//			orderflag = getParaValue("orderflag");
//		}
		////////////////////////////////��;��ȷ��/////////////////////////////////////////////
		List orderList = new ArrayList();
		if (hostIds != null && hostIds.length > 0) {
			for (int i = 0; i < hostIds.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(hostIds[i]));
				dao.close();
				Hashtable pinghash = new Hashtable();
				Hashtable responsehash = new Hashtable();
				try {
					pinghash = hostmanager.getCategory(node.getIpAddress(), "Ping", "ConnectUtilization", startTime,
						toTime);
					responsehash = hostmanager.getCategory(node.getIpAddress(),"Ping", "ResponseTime", startTime, 
							toTime);
//					cpuhash = hostmanager.getCategory(node.getIpAddress(), "cpu", "ConnectUtilization", startTime,
//							toTime);
//					diskhash = hostmanager.getCategory(node.getIpAddress(), "cpu", "ConnectUtilization", startTime,
//							toTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("hostnode", node);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("responsehash", responsehash);
				ipmemhash.put("ipaddress",node.getIpAddress()+"("+node.getAlias()+")");
				orderList.add(ipmemhash);
			}
		}
		List returnList = new ArrayList();
			// ��orderList����theValue��������
			// **********************************************************
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					HostNode node = (HostNode) _pinghash.get("hostnode");
					// String osname = monitoriplist.getOssource().getOsname();
					Hashtable responsehash = (Hashtable) _pinghash.get("responsehash");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					if (pinghash == null)
						continue;
					String equname = node.getAlias();
					String ip = node.getIpAddress();
					String pingconavg = "";
					String downnum = "";
					String responseavg = "";
					String responsemax = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					if (pinghash.get("downnum") != null)
						downnum = (String) pinghash.get("downnum");
					// ��ȡ��Ӧʱ��
					if (responsehash.get("avgpingcon") != null)
						responseavg = (String) responsehash.get("avgpingcon");
					if (responsehash.get("pingmax") != null)
						responsemax = (String) responsehash.get("pingmax");
					List ipdiskList = new ArrayList();
					ipdiskList.add(ip);
					ipdiskList.add(equname);
					ipdiskList.add(node.getType());
					ipdiskList.add(pingconavg);
					ipdiskList.add(downnum);
					ipdiskList.add(responseavg);
					ipdiskList.add(responsemax);
					returnList.add(ipdiskList);
				}
			}
//		}
		// **********************************************************
		hostListPing_Response = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("avgping")) {
						String avgping = "";
						if (ipdiskList.get(3) != null) {
							avgping = (String) ipdiskList.get(3);
						}
						String _avgping = "";
						if (ipdiskList.get(3) != null) {
							_avgping = (String) _ipdiskList.get(3);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping
								.substring(0, _avgping.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("downnum")) {
						String downnum = "";
						if (ipdiskList.get(4) != null) {
							downnum = (String) ipdiskList.get(4);
						}
						String _downnum = "";
						if (ipdiskList.get(4) != null) {
							_downnum = (String) _ipdiskList.get(4);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}else if (orderflag.equalsIgnoreCase("responseavg")) {
						String avgping = "";
						if (ipdiskList.get(5) != null) {
							avgping = (String) ipdiskList.get(5);
						}
						String _avgping = "";
						if (ipdiskList.get(5) != null) {
							_avgping = (String) _ipdiskList.get(5);
						}
						if (new Double(avgping.substring(0,
								avgping.length() - 2)).doubleValue() < new Double(
								_avgping.substring(0, _avgping.length() - 2))
								.doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("responsemax")) {
						String downnum = "";
						if (ipdiskList.get(6) != null) {
							downnum = (String) ipdiskList.get(6);
						}
						String _downnum = "";
						if (ipdiskList.get(6) != null) {
							_downnum = (String) _ipdiskList.get(6);
						}
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				hostListPing_Response.add(ipdiskList);
				ipdiskList = null;
			}
		}
		ReportValue pingReportValue =  ReportHelper.getReportValue(orderList,"ping");
		ReportValue responseReportValue =  ReportHelper.getReportValue(orderList, "responsetime");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "��ͨ��", "ʱ��", "");
		String responsetimepath = new ReportExport().makeJfreeChartData(responseReportValue.getListValue(), responseReportValue.getIpList(), "��Ӧʱ��", "ʱ��", "");
		devicePath = new String[2];
		devicePath[0] = pingpath;
		devicePath[1] = responsetimepath;
		return devicePath;
	}
	private Hashtable hostCpu(String []hostIds){
		//�õ���ǰʱ��
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		//�õ���ǰ���ڵ���һ���ܵ����һ��
		String totime = new CalDateUtil().getEndWeek(pastDateM) + " 00:00:00";;
		String starttime = new CalDateUtil().convert(pastDateM) + " 23:59:59";
		String startdate = new CalDateUtil().convert(pastDateM);
		I_HostCollectData hostmanager = new HostCollectDataManager();
		Integer[] ids = null;
		if (hostIds != null && hostIds.length > 0)
			ids = new Integer[hostIds.length];
		for (int i = 0; i < hostIds.length; i++) {
			if (hostIds[i] == null || hostIds[i].trim().length() == 0)
				continue;
			ids[i] = new Integer(hostIds[i]);
		}
		Hashtable allcpuhash = new Hashtable();
		String ip = "";
		String equipname = "";
//		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector cpuVector = new Vector();
		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable allreporthash = new Hashtable();
		try {
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					HostNodeDao dao = new HostNodeDao();
					HostNode node = (HostNode) dao.loadHost(ids[i]);
					dao.close();
					ip = node.getIpAddress();
					equipname = node.getAlias() + "(" + ip + ")";
					String remoteip = request.getRemoteAddr();
					String newip = doip(ip);
					String[] time = { "", "" };
					// ��lastcollectdata��ȡ���µ�cpu�����ʣ��ڴ������ʣ���������������
					String[] item = { "CPU" };
					if ("0".equals(runmodel)) {
//						// �ɼ�������Ǽ���ģʽ
						Hashtable sharedata = ShareData.getSharedata();
						// CPU
						Hashtable hdata = (Hashtable) sharedata.get(ip);
						if (hdata == null)
							hdata = new Hashtable();
						cpuVector = (Vector) hdata.get("cpu");
					} else {
						// �ɼ�������Ƿ���ģʽ
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.creatNodeDTOByNode(node);
						// CPU��Ϣ
						CpuInfoService cpuInfoService = new CpuInfoService(String.valueOf(nodeDTO.getId()), nodeDTO.getType(), nodeDTO.getSubtype());
						cpuVector = cpuInfoService.getCpuInfo();
					}
					// ��collectdata��ȡһ��ʱ���cpu�����ʣ��ڴ������ʵ���ʷ�����Ի�����ͼ��ͬʱȡ�����ֵ
					Hashtable cpuhash = hostmanager.getCategory(ip, "CPU", "Utilization", starttime, totime);
					// cpu���ֵ
					maxhash = new Hashtable();
					String cpumax = "";
					String avgcpu = "";
					if (cpuhash.get("max") != null) {
						cpumax = (String) cpuhash.get("max");
					}
					if (cpuhash.get("avgcpucon") != null) {
						avgcpu = (String) cpuhash.get("avgcpucon");
					}
					maxhash.put("cpumax", cpumax);
					maxhash.put("avgcpu", avgcpu);
					String fileName_CpuPath = p_draw_line(cpuhash, "", newip + "cpu", 750, 150);//��ͼ
					Hashtable reporthash = new Hashtable();
					// CPU
					if (cpuVector != null && cpuVector.size() > 0) {
						for (int si = 0; si < cpuVector.size(); si++) {
							CPUcollectdata cpudata = (CPUcollectdata) cpuVector.elementAt(si);
							maxhash.put("cpu", cpudata.getThevalue());
							reporthash.put("CPU", maxhash);
						}
					} else {
						reporthash.put("CPU", maxhash);
					}
					reporthash.put("equipname", equipname);
					allreporthash.put(ip, reporthash);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allreporthash;
	}
	/**
	 *�����豸 cpu ��������
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
	 */
	public HashMap exportReport_netCpu(String[] ids, String type, String filePath,
			String startTime, String toTime) {
		if(ids != null && ids.length > 0){
			int chartWith,chartHigh;
			HashMap<?, ArrayList<?>> nm = null;
			nm = exportHostCpuTop_net(ids, type, filePath, startTime, toTime);
			return nm;
		}
		return null;
	}
	/**
	 *���� cpu ��������
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
	 */
	public HashMap exportReport(String[] ids, String type, String filePath,
			String startTime, String toTime) {
		if(ids != null && ids.length > 0){
			int chartWith,chartHigh;
			HashMap<?, ArrayList<?>> hm = null;
			hm = exportHostCpuTop(ids, type, filePath, startTime, toTime);
			if(hm!=null){
				return hm;
			}
			
		}
		return null;
	}
	/**
	 *���� disk ��������
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
	 */
	public HashMap exportReport_disk(String[] ids, String type, String filePath,
			String startTime, String toTime) {
		int chartWith,chartHigh;
		HashMap<?, ArrayList<?>> hm = null;
		hm = exportHostDiskTop(ids, type, filePath, startTime, toTime);		
		return hm;
	}
	/**����
	 * @param ids
	 * @param type
	 * @param filePath
	 * @param startTime
	 * @param toTime
	 * @return
	 */
	private HashMap<?, ArrayList<?>> exportHostDiskTop(String[] ids, String type,
			String filePath, String startTime, String toTime) {
		if(ids!=null && ids.length>0){
			
		
		HashMap hm = new HashMap();
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = new ArrayList<ArrayList<String[]>>();
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// �����豸��������
		HashMap all = this.getAllValue_disk(ids, startTime, toTime);
		ReportValue disk = (ReportValue) all.get("disk");
		List<StatisNumer> table = (List<StatisNumer>) all.get("gridVlue");
		Iterator<StatisNumer> tableIt = table.iterator();
		ArrayList<String[]> diskal = new ArrayList<String[]>();	
		while (tableIt.hasNext()) {
			StatisNumer sn = tableIt.next();
			String tabletype = sn.getType();
			if ("gridDisk".equals(tabletype)) {
				String[] arry = { sn.getIp() + "(" + sn.getName() + ")",sn.getCurrent() };
				diskal.add(arry);
			} 
		}
		// ����
		if (diskal != null && diskal.size() > 0) {
			tableList.add(diskal);
			String diskpath = makeDiskJfreeChartData(diskal, disk.getIpList(),"����������", "ʱ��", "");
			chartList.add(diskpath);
			if ("host".equalsIgnoreCase(type)) {
				String[] disktitle = { "IP(��������)", "��ǰ������" };
				diskal.add(0, disktitle);
			}
		}
		hm.put("table", tableList);
		hm.put("chart", chartList);
		return hm;
		}
		return null;
	}
	/**
	 * ����disk top10
	 * @param idValue
	 * @param startTime
	 * @param toTime
	 * @return
	 */
	public HashMap getAllValue_disk(String[] idValue,String startTime,String toTime) {
		HashMap allValueMap=new HashMap();
		List<StatisNumer> gridList=new ArrayList<StatisNumer>();
		List<ReportValue> valueList=new ArrayList<ReportValue>();
		String runmodel = PollingEngine.getCollectwebflag();//�ɼ������ģʽ
	  	I_HostCollectData hostmanager = new HostCollectDataManager();
	  	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		startTime=startTime+" 00:00:00";
		toTime=toTime+" 23:59:59";
		List<List> list=new ArrayList<List>();
		List<List> diskList=new ArrayList<List>();
		List<String> diskipList=new ArrayList<String>();
		
		if (idValue != null && idValue.length > 0) {
			for (int i = 0; i < idValue.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(idValue[i]));
				dao.close();
				String ip="";
				ip = node.getIpAddress();
					//����
					Hashtable hostdiskhash=new Hashtable();
					if ("0".equals(runmodel)) {
						try{
							hostdiskhash = hostlastmanager.getDisk_share(ip,"Disk","","");
						}catch(Exception e){
							e.printStackTrace();
						}
					}else {
						try {
							hostdiskhash = hostlastmanager.getDisk(ip,"Disk", "", "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try{
						Hashtable diskHash = hostmanager.getDiskHistroy(ip, "Disk", startTime, toTime);
						String countStr=(String)diskHash.get("count");
						double[] disk_data1 = new double[hostdiskhash.size()];
						
						int count=Integer.parseInt(countStr);
						List list2=new ArrayList();
						String diskType="";
						for (int j = 1; j <= count; j++) {
							List diskList1=(List)diskHash.get("list"+j);
							if(diskList1==null||diskList1.size()==0)
								break;
							String cur="0";
							String avg="0";
							String max="0";
						if (hostdiskhash.get(new Integer(j-1))!=null) {
							Hashtable dhash = (Hashtable) (hostdiskhash.get(new Integer(j-1)));
							cur= (String) dhash.get("Utilization");
						}
							if (diskHash.get("avg"+j)!=null)
								avg=(String)diskHash.get("avg"+j);
							if (diskHash.get("max"+j)!=null)
								max=(String)diskHash.get("max"+j);
							
								Vector vector=(Vector)diskList1.get(0);
								diskType=ip+"("+vector.get(1)+")";
								 //����
								 StatisNumer voNumerout=new StatisNumer();
								 voNumerout.setIp(ip);
								 voNumerout.setType("gridDisk");
								 voNumerout.setName((String)vector.get(1));
								 voNumerout.setCurrent(cur);
								 voNumerout.setMaximum(max);
								 voNumerout.setAverage(avg);
								 gridList.add(voNumerout);
						if(diskList1!=null&&diskList1.size()>0)
						    list2.add(diskList1);
						diskipList.add(diskType);
						}
						
						diskList.add(list2);
					}catch(Exception e){
						e.printStackTrace();
					}
				
			}
			ReportValue diskValue=new ReportValue();
			diskValue.setIpList(diskipList);
			diskValue.setListValue(diskList);
			allValueMap.put("disk", diskValue);
			allValueMap.put("gridVlue", gridList);
			
		}
		return allValueMap;
	}
	/** ���� disk top10
	 * @param dataList
	 * @param ipList
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @return
	 */
	private String makeDiskJfreeChartData(ArrayList<String[]> dataList,
			List ipList, String title, String xdesc, String ydesc) {
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
				+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (dataList != null) {
			for (int j = 0; j < dataList.size(); j++) {
				String[] dataList1 = dataList.get(j);
				if (dataList1 != null) {
					String s = dataList1[1];
					s = s.replace("%", "");
					dataset.addValue(Double.parseDouble(s), "", dataList1[0]);
				}
			}
		}

		String chartkey = ChartCreator.createBarChart(title, xdesc, ydesc,
				dataset, chartWith, chartHigh);
		JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
				.getInstance().getChartStorage().get(chartkey);
		CategoryPlot plot = chart.getChart().getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		FileOutputStream fos =null;
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
			ioe.printStackTrace();
			chartPath = "";
			SysLogger.error("", ioe);
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		return chartPath;
	}
	public HashMap getAllValue(String[] idValue,String startTime,String toTime) {
		HashMap allValueMap=new HashMap();
		List<StatisNumer> gridList=new ArrayList<StatisNumer>();
		List<ReportValue> valueList=new ArrayList<ReportValue>();
		
	  	String runmodel = PollingEngine.getCollectwebflag();//�ɼ������ģʽ
	  	I_HostCollectData hostmanager = new HostCollectDataManager();
	  	I_HostLastCollectData hostlastmanager=new HostLastCollectDataManager();
		startTime=startTime+" 00:00:00";
		toTime=toTime+" 23:59:59";
		List<List> pingList=new ArrayList<List>();
		List<List> list=new ArrayList<List>();
		List<List> memList=new ArrayList<List>();
		List<List> utilInList=new ArrayList<List>();
		List<List> utilOutList=new ArrayList<List>();
		List<List> diskList=new ArrayList<List>();
		
		List<String> pingipList=new ArrayList<String>();
		List<String> ipList=new ArrayList<String>();
		List<String> memipList=new ArrayList<String>();
		List<String> portipList=new ArrayList<String>();
		List<String> diskipList=new ArrayList<String>();
		
		if (idValue != null && idValue.length > 0) {
			for (int i = 0; i < idValue.length; i++) {
				HostNodeDao dao = new HostNodeDao();
				HostNode node = dao.loadHost(Integer.parseInt(idValue[i]));
				dao.close();
				String ip="";
				ip = node.getIpAddress();
				String cpuvalue="0";
				if ("0".equals(runmodel)) {
					Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
					if (ipAllData != null) {
						Vector cpuV = (Vector) ipAllData.get("cpu");
						if (cpuV != null && cpuV.size() > 0) {
							CPUcollectdata cpu = (CPUcollectdata) cpuV.get(0);
							cpuvalue =cpu.getThevalue();
						}
					}
				}else {
					Hashtable curCpuhash=null;
					try {
						curCpuhash = hostmanager.getCurByCategory(ip, "CPU", "Utilization",startTime,toTime);
					} catch (Exception e) {		
						e.printStackTrace();
					}
						cpuvalue=(String)curCpuhash.get("pingCur");
				}
				try{
					Hashtable cpuhash = hostmanager.getCategory(ip,"CPU","Utilization",startTime,toTime);
					List cpuList=(List)cpuhash.get("list");
					String cpumax="0";
					String cpuavg="0";
					if(cpuhash.get("max")!=null){
						cpumax = (String)cpuhash.get("max");
					}
							if(cpuhash.get("avgcpucon")!=null){
								cpuavg = (String)cpuhash.get("avgcpucon");
						       }
							 StatisNumer voNumer=new StatisNumer();
							 voNumer.setIp(ip);
							 voNumer.setType("gridCpu");
							 voNumer.setCurrent(cpuvalue);
							 voNumer.setMaximum(cpumax);
							 voNumer.setAverage(cpuavg);
							 gridList.add(voNumer);
							 if (cpuList!=null&&cpuList.size()>0)
							  list.add(cpuList);
							ipList.add(ip);
						}catch(Exception e){
							e.printStackTrace();
						}
						}
		}
		if (idValue!=null&&idValue.length>0) {
			ReportValue cpuValue=new ReportValue();
			cpuValue.setIpList(ipList);
			cpuValue.setListValue(list);
			allValueMap.put("cpu", cpuValue);
			allValueMap.put("gridVlue", gridList);
		}
		return allValueMap;
	}
	/**������
	 * @param ids
	 * @param type
	 * @param filePath
	 * @param startTime
	 * @param toTime
	 * @return
	 */
	private HashMap<?, ArrayList<?>> exportHostCpuTop_net(String[] ids, String type,
			String filePath, String startTime, String toTime) {
		HashMap hm = new HashMap();
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = new ArrayList<ArrayList<String[]>>();
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// �����豸��������
		HashMap all = this.getAllValue(ids, startTime, toTime);
		ReportValue cpu = (ReportValue) all.get("cpu");
		List<StatisNumer> table = (List<StatisNumer>) all.get("gridVlue");
		Iterator<StatisNumer> tableIt = table.iterator();
		ArrayList<String[]> cpual = new ArrayList<String[]>();
		String[] cputitle = { "ip", "��ǰ������", "���������", "ƽ��������" };
		if(cpu.getIpList().size()>0)
		cpual.add(cputitle);
		ArrayList<String[]> diskal = new ArrayList<String[]>();	
		ArrayList<StatisNumer> portin = new ArrayList<StatisNumer>();
		ArrayList<StatisNumer> portout = new ArrayList<StatisNumer>();
		while (tableIt.hasNext()) {
			StatisNumer sn = tableIt.next();
			String tabletype = sn.getType();
			if ("gridCpu".equals(tabletype)) {
				String[] arry = { sn.getIp(), sn.getCurrent(), sn.getMaximum(),sn.getAverage() };
				cpual.add(arry);
			} 
		}
		ArrayList<ArrayList<String>> portName = new ArrayList<ArrayList<String>>();
		ArrayList<String> portInName = new ArrayList<String>();
		ArrayList<String> portOutName = new ArrayList<String>();
		if(cpu.getIpList().size() > 0)tableList.add(cpual);
		// cpu������
		String cpupath = makeJfreeChartData(cpu.getListValue(),cpu.getIpList(), "cpu������", "ʱ��", "");
		if(cpu.getIpList().size() > 0) chartList.add(cpupath);
		hm.put("table", tableList);
		hm.put("chart", chartList);
		return hm;
	}
	/**����
	 * @param ids
	 * @param type
	 * @param filePath
	 * @param startTime
	 * @param toTime
	 * @return
	 */
	private HashMap<?, ArrayList<?>> exportHostCpuTop(String[] ids, String type,
			String filePath, String startTime, String toTime) {
		HashMap hm = new HashMap();
		// ��ű����Ϣ
		ArrayList<ArrayList<String[]>> tableList = new ArrayList<ArrayList<String[]>>();
		// ���ͼƬ·��
		ArrayList<String> chartList = new ArrayList<String>();
		// �����豸��������
		HashMap all = this.getAllValue(ids, startTime, toTime);
		ReportValue cpu = (ReportValue) all.get("cpu");
		List<StatisNumer> table = (List<StatisNumer>) all.get("gridVlue");
		Iterator<StatisNumer> tableIt = table.iterator();
		ArrayList<String[]> cpual = new ArrayList<String[]>();
		String[] cputitle = { "ip", "��ǰ������", "���������", "ƽ��������" };
		if(cpu.getIpList().size()>0)
		cpual.add(cputitle);
		ArrayList<String[]> diskal = new ArrayList<String[]>();	
		ArrayList<StatisNumer> portin = new ArrayList<StatisNumer>();
		ArrayList<StatisNumer> portout = new ArrayList<StatisNumer>();
		while (tableIt.hasNext()) {
			StatisNumer sn = tableIt.next();
			String tabletype = sn.getType();
			if ("gridCpu".equals(tabletype)) {
				String[] arry = { sn.getIp(), sn.getCurrent(), sn.getMaximum(),sn.getAverage() };
				cpual.add(arry);
			} 
		}
		ArrayList<ArrayList<String>> portName = new ArrayList<ArrayList<String>>();
		ArrayList<String> portInName = new ArrayList<String>();
		ArrayList<String> portOutName = new ArrayList<String>();
		if(cpu.getIpList().size() > 0)tableList.add(cpual);
		// cpu������
		String cpupath = makeJfreeChartData(cpu.getListValue(),cpu.getIpList(), "cpu������", "ʱ��", "");
		//////////////////////////////////
//		if(cpu.getIpList().size() > 0) chartList.add(cpupath);
//		hm.put("table", tableList);
//		hm.put("chart", chartList);
//		return hm;
		///////////////////////////////////
		/////////////////////////////////�޸�Ϊ
		if(cpupath != null) {
			chartList.add(cpupath);
			hm.put("table", tableList);
			hm.put("chart", chartList);
			return hm;
		}
		return null;
	}
	//����ids��������
	private String[] countIds(String ids){
		String[] idValue = this.getIdValue(ids);
		String[] netIds = this.getIdsValues(idValue, "net|");
		String[] hostIds = this.getIdsValues(idValue, "host|");
		if(netIds!=null && hostIds!=null){
			String[] c = new String[netIds.length + hostIds.length];
			System.arraycopy(netIds, 0, c, 0, netIds.length);
			System.arraycopy(hostIds, 0, c, netIds.length, hostIds.length);
			return c;
		}else if(netIds == null){
			String[] c = new String[ hostIds.length];
			System.arraycopy(hostIds, 0, c,0, hostIds.length);
			return c;
		}else if(hostIds == null){
			String[] c = new String[ netIds.length];
			System.arraycopy(netIds, 0, c, 0, netIds.length);
			return c;
		}
		
		return null;
	}
	/**
	 * ����jfreechartͼƬ
	 * 
	 * @param dataList
	 * @param ipList
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @return ͼƬ·��
	 */
	public String makeJfreeChartData(List dataList, List ipList, String title,
			String xdesc, String ydesc) {   
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp" + File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (dataList != null && dataList.size() > 0) {
			int xcount = 0;
			String x = "";
			for (int j = 0; j < dataList.size(); j++) {
				x = "";
				List xList = (List) dataList.get(0);
				xcount = xList.size();
				List dataList1 = (List) dataList.get(j);
				for (int m = 0; m < dataList1.size(); m++) {
					// TODO:x��ʱ�����겻һ���������в���������ʾ����ʱ����Ϊ����1��x��ʱ������
					Vector v0 = null;
					if (xcount <= m) {
						continue;
					}
					v0 = (Vector) xList.get(m);
					Vector v = (Vector) dataList1.get(m);
					if (xcount > xlabel) {
						x = x + "\r";
						BigDecimal bd = new BigDecimal(xcount);
						int mod = Integer.parseInt(bd.divide(
								new BigDecimal(xlabel),
								BigDecimal.ROUND_CEILING).toString());
						if (m % mod == 0) {
							dataset.addValue(Double.parseDouble(String
									.valueOf(v.get(0))), String.valueOf(ipList
									.get(j)), String.valueOf(v0.get(1)));
						} else {
							// ���x����̫�༷��һ�𿴲���
							dataset.addValue(Double.parseDouble(String
									.valueOf(v.get(0))), String.valueOf(ipList
									.get(j)), x);
						}
					} else {
						dataset.addValue(Double.parseDouble(String.valueOf(v
								.get(0))), String.valueOf(ipList.get(j)),
								String.valueOf(v0.get(1)));
					}
				}
			}
			String chartkey = ChartCreator.createLineChart(title, xdesc, ydesc,dataset, chartWith, chartHigh);
			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(chartkey);
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
			return chartPath;
		}
		return null;
	}
	//�¼����Ʒ�������ͼ����-----2012-05-28
	public Map getDayAlarmDataForList(String[] ids){
		DBManager conn = new DBManager();
		ResultSet rs;
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		String toTime = new CalDateUtil().getEndWeek(pastDateM);
		String startTime = new CalDateUtil().convert(pastDateM);
		startTime = startTime+" 00:00:59";
		toTime = toTime+" 23:59:59";
		StringBuffer id_s = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			id_s.append(id);
			id_s.append(",");
		}
		String ss = id_s.toString();
		String str_id = ss.substring(0,ss.length()-1);
		
		String sql="select nodeid,day(recordtime) as h,count(1) as cnt from system_eventlist where nodeid in("+
				str_id+
				") and recordtime between '" +
				startTime +
				"' and '" +
				toTime +
				"' group by nodeid,h";
//		List list = new ArrayList();
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
//		for(int i=0;i<24;i++){
//			map.put(i, 0);
//		}
		try
	     {
	    	 rs = conn.executeQuery(sql);
	         while(rs.next())
	        	 map.put(rs.getInt("h"), rs.getInt("cnt"));
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportDataCreate:",e);
	     }finally{
	    	 conn.close();
	     }
	     
//	     for(int i=0;i<24;i++){
//	    	 CompreReportStatic crs = new CompreReportStatic();
//	    	 crs.setIp(i+"");
//	    	 crs.setValue(map.get(i));
//	    	 crs.setType("dayhour");
//	    	 list.add(crs);
//		}
	     return map;
	}
	private String makeJfreeChartDataForWeek_line(String[] ids){
		
		String title = "һ��ÿ��澯����";
		String xdesc = "��";
		String ydesc = "����";
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"
			+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		String toTime = new CalDateUtil().getEndWeek(pastDateM);
		String startTime = new CalDateUtil().convert(pastDateM);
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
		map= this.getDayAlarmDataForList(ids);
		if(!map.isEmpty()){
			for(Integer date : map.keySet()){
				dataset.addValue(map.get(date),"weekDay", date);
				//String title,String XCordUnitName,String YCordUnitName,DefaultCategoryDataset dataset,int width,int height
				
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
			return chartPath;
		}
		return null;
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
//			CategoryPlot plot = chart.getChart().getCategoryPlot();
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
	}
	/**
	 * ����ͼƬ
	 * @param list
	 * @param title
	 * @param xdesc
	 * @param ydesc
	 * @return
	 */
	private String makeJfreeChartDataForDay(String[] ids){
		makeJfreeChartDataForWeek_line(ids);
		int chartWith = 768;
		int chartHigh = 338;
		String title = "һ��ÿ��澯����";
		String xdesc = "��";
		 String ydesc = "��";
		String chartPath = ResourceCenter.getInstance().getSysPath() + "temp"+ File.separator + System.currentTimeMillis() + ".png";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		//�澯������Ϣͳ��--����ͼ
		//�õ���ǰ���ڵ���һ���ܵ����һ��
		//�õ���ǰʱ��
		Date nowDate = new CalDateUtil().getNowDate();
		//�õ���ǰ���ڵ���һ���ܵ���һ
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		String toTime = new CalDateUtil().getEndWeek(pastDateM);
		String startTime = new CalDateUtil().convert(pastDateM);
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
		map= this.getWeekAlarmData(startTime,toTime,ids);
		if(!map.isEmpty()){
			for(Integer date : map.keySet()){
				dataset.addValue(map.get(date), "weekDay", date);
				//String title,String XCordUnitName,String YCordUnitName,DefaultCategoryDataset dataset,int width,int height
				
			}
			String chartkey = ChartCreator.createLineChart(title, xdesc, ydesc, dataset, chartWith, chartHigh);
			JFreeChartBrother chart = (JFreeChartBrother) ResourceCenter
			.getInstance().getChartStorage().get(chartkey);
			CategoryPlot plot = chart.getChart().getCategoryPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();
//			StackedBarRenderer renderer = new StackedBarRenderer();
//			renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
//			renderer.setItemLabelsVisible(true);
//			plot.setRenderer(renderer);
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
			return chartPath;
		}
		return null;
	}
	//�¼�����ͳ��
	public void countEvent(String startTime,String toTime,String[] ids){
		Connection conn = null;
		Statement  stm = null;
		ResultSet rs;
		StringBuffer id_s =new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			id_s.append(id);
			id_s.append(",");
		}
		String ss = id_s.toString();
		String str_id = ss.substring(0,ss.length()-1);
//		System.out.println("nodeid&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+str_id);
		/**
		 *select nodeid,level1,count(w.level1) as level1 from system_eventlist w 
		 *where nodeid in(10,11,18,17,15,16) and to_days(recordtime) >= to_days('2012-05-14') 
		 *and to_days(recordtime) <=to_days('2012-05-20')  group by nodeid,level1;
		 */
		String sql = "select nodeid,level1,count(w.level1) as cnt from system_eventlist w where nodeid in(" +
					str_id
					+") and to_days(recordtime) >= to_days('" +
					startTime+
					"') and to_days(recordtime) <=to_days('" +
					toTime +
					"')  group by nodeid,level1";
		DBManager dbM = new DBManager();
		try {
			conn = dbM.getConn();
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			loadFromCountEvent(rs);
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			rs=null;
			try {
				stm.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	/**
	 * ��Ҫͳ�Ƹ����¼���
	 * @param rs
	 * @return
	 */
	public void loadFromCountEvent(ResultSet rs){
		int leven1_int_ = 0;
		int leven2_int_ = 0;
		int leven3_int_ = 0;
		int leven_count_sum = 0;
		leven1_int=0;
		leven2_int=0;
		leven3_int=0;
		levencount_int=0;
		HashMap map_leven = new HashMap();
		try {
			 while(rs.next()){
				 if(rs.getString("level1").equals("1")){
					 String cnt = rs.getString("cnt");
					 leven1_int_ = leven1_int_+(Integer.parseInt(cnt));
				 }else if(rs.getString("level1").equals("2")){
					 String cnt = rs.getString("cnt");
					 leven2_int_ = leven2_int_+(Integer.parseInt(cnt));
				 }else if(rs.getString("level1").equals("3")){
					 String cnt = rs.getString("cnt");
					 leven3_int_ = leven3_int_+(Integer.parseInt(cnt));
				 }
			 }
//			 leven_count_sum = leven1_int_+leven2_int_+leven3_int_;
//			 System.out.println("leven1_int:"+leven1_int+"_______leven2_int:"+leven2_int+"_________leven3_int:"+leven3_int);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		levencount_int = leven_count_sum;
		
		leven1_int = leven1_int_; 
		leven2_int = leven2_int_;
		leven3_int = leven3_int_;
		levencount_int = leven1_int_+leven2_int_+leven3_int_;
	}
	public String[][] getTableWeekData(String starttime,String totime){
		ResultSet rs;
		DBManager conn = new DBManager();
		String [][] dataStr=new String[][] {{"����澯","0","0","0","0"},
											{"�豸�澯","0","0","0","0"},
											{"�������澯","0","0","0","0"},
											{"���ݿ�澯","0","0","0","0"},
											{"�м���澯","0","0","0","0"},
											{"Ӧ�ø澯","0","0","0","0"},
											{"�洢�澯","0","0","0","0"},
											{"ҵ��澯","0","0","0","0"},
											{"��ȫ�澯","0","0","0","0"}};
		try{
			String subtype="";
			int level=0;
			//--�޸�Ϊֻ��ѯ��������
			StringBuilder sb=new StringBuilder();
			sb.append(" select subtype,level1,count(1) as cnt from system_eventlist ");
			sb.append(" where recordtime >= '");
			sb.append(starttime);
			sb.append("' and recordtime<= ' ");
			sb.append(totime);
			sb.append("' group by subtype,level1; ");
			rs = conn.executeQuery(sb.toString()); 
	         while(rs.next()){
	        	 subtype=rs.getString("subtype"); 
	        	 if(subtype.equalsIgnoreCase("net")||subtype.equalsIgnoreCase("dns")){//����澯
	        		 level=rs.getInt("level1");
	        		 dataStr[1][level+1]= String.valueOf((Integer.parseInt(dataStr[1][level+1])+Integer.parseInt(rs.getString("cnt"))));
	        	 }else if(subtype.equalsIgnoreCase("host")){//�������澯 
	        		 level=rs.getInt("level1");
	        		 dataStr[2][level+1]= String.valueOf((Integer.parseInt(dataStr[2][level+1])+Integer.parseInt(rs.getString("cnt"))));
	 	         }else if(subtype.equalsIgnoreCase("db")){//���ݿ�澯 
	        		 level=rs.getInt("level1");
	        		 dataStr[3][level+1]=rs.getString("cnt");
	        	 }else if(subtype.equalsIgnoreCase("domino")
	        			 ||subtype.equalsIgnoreCase("tomcat")
	        			 ||subtype.equalsIgnoreCase("cics")
	        			 ||subtype.equalsIgnoreCase("mq")
	        			 ||subtype.equalsIgnoreCase("wasserver")
	        			 ||subtype.equalsIgnoreCase("weblogic")
	        			 ||subtype.equalsIgnoreCase("iis")
	        			 ||subtype.equalsIgnoreCase("jboss")
	        			 ||subtype.equalsIgnoreCase("apache")){ //�м���澯 
	        		 level=rs.getInt("level1");
	        		 dataStr[4][level+1]= String.valueOf((Integer.parseInt(dataStr[5][level+1])+Integer.parseInt(rs.getString("cnt"))));
	        	 } 
	        	 //�д��������
	        	 //6Ӧ�ø澯 
	        	 //7�洢�澯 
	        	 //8ҵ��澯 
	        	 else if(subtype.equalsIgnoreCase("bus")){//ҵ��澯  
        		 level=rs.getInt("level1");
        		 dataStr[5][level+1]=rs.getString("cnt");
	        	 }
	         }
	     }
	     catch(Exception e) {
	         SysLogger.error("AlarmSummarize:",e);
	     }finally{
	    	 conn.close();
	     }
		return dataStr;
	}
	/**
	 *  ��ѯ��һ�ܸ澯��ͼ�������� 
	 * @return
	 */
	public Map getWeekAlarmData(String startTime,String toTime,String[] ids){
		ResultSet rs;
		DBManager conn = new DBManager();
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
		StringBuffer strB = new StringBuffer();
		strB.append("select DAY(recordtime)  as d,count(1) as cnt from system_eventlist where recordtime >='");
		strB.append(startTime);
		strB.append("' and recordtime <='");
		strB.append(toTime);
		strB.append("' group by d;");
		try{
	    	 rs = conn.executeQuery(strB.toString());
	         while(rs.next())
	        	 map.put(rs.getInt("d"), rs.getInt("cnt"));
	     }catch(Exception e){
	         SysLogger.error("AlarmSummarize:",e);
	     }finally{
	    	 conn.close(); 
	     }
	     return map;
	}
	private String doip(String ip) {
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}
	private String p_draw_line(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		String fileName =null;
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();

				TimeSeries ss = new TimeSeries(title1, Minute.class);
				TimeSeries[] s = { ss };
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				fileName = cg.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1, title2, w, h);
				System.out.println("fileName:---------------------"+fileName);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp
					.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(ʱ��)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//�鿴ģ����ϸ��Ϣ
	/**
	 * String str Ϊ���ݿ����id nms_comprereportweek_resources ���ڵ�����
	 */
	public String netHostWorkReportConfig() {	
		String str = this.getParaValue("id");
		CompreReportWeekDao dao = new CompreReportWeekDao();
		CompreReportWeekInfo report = dao.findByBid(str);
		String ids = report.getIds();//�豸id
		String email = report.getReportUserEmail();//�ʼ���ַ
		String reportName = report.getReportName();//��������---ģ������
		String sendTime = report.getSendTime();//����ʱ��
		int sendWeek = report.getSendOtherDay();//�ܼ�����
		String[] _ids=this.getIdValue(ids);
		String[] h_ids = getIdsValues(_ids,"host|");
		String[] n_ids = getIdsValues(_ids,"net|");
		HostNodeDao hdao = new HostNodeDao();
	
		String[] c= new String[h_ids.length + n_ids.length];
		List ls_node = new ArrayList();
		if(h_ids!=null&&n_ids!=null){
			System.arraycopy(h_ids, 0, c, 0, n_ids.length);
			System.arraycopy(n_ids, 0, c, n_ids.length, h_ids.length);
			for(int i=0;i<c.length;i++){
				HostNode node = new HostNode();
				node = new HostNodeDao().loadHost(Integer.parseInt(c[i]));
//				dao.close();
//				node = hdao.loadHost(Integer.parseInt(c[i]));
				if(node==null){
					continue;
				}else{
					ls_node.add(i, node);
				}
			}
			dao.close();
		}else if(n_ids==null){
			c = new String[ h_ids.length];
			System.arraycopy(h_ids, 0, c,0, h_ids.length);
			for(int i=0;i<c.length;i++){
				HostNode node = new HostNode();
				node = new HostNodeDao().loadHost(Integer.parseInt(c[i]));
				
				if(node==null){
					continue;
				}else{

					ls_node.add(i, node);
				}
			}
			dao.close();
		}else if(h_ids == null){
			c = new String[ n_ids.length];
			System.arraycopy(n_ids, 0, c, 0, n_ids.length);
			for(int i=0;i<c.length;i++){
				HostNode node = new HostNode();
				node = new HostNodeDao().loadHost(Integer.parseInt(c[i]));
				dao.close();
				if(node==null){
					continue;
				}else{

					ls_node.add(i, node);
				}
			}
		}
		String frequencyName = "ÿ��";
		String[] weekCh = {" ����һ", " ���ڶ�", " ������", " ������", " ������", " ������"," ������"};
		StringBuffer sb = new StringBuffer();
		sb.append(frequencyName);
		sb.append(":(" + (weekCh[sendWeek-1]) + ")");
		sb.append(" ʱ�䣺(" + sendTime + ")");
		String sendDate = sb.toString();
		Vector vector = new Vector();
		vector.add(0,ls_node);
		vector.add(1,report);
		request.setAttribute("sendDate",sendDate);
		request.setAttribute("vector", vector);
		return "/capreport/comprehensiveweek/compreReportWeekDetail.jsp";
	}
}
