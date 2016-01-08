/**
 * @author sunqichang/������
 * Created on May 20, 2011 3:07:48 PM
 */
package com.afunms.comprehensivereportweek.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;

import com.afunms.common.util.SysLogger;
import com.afunms.initialize.ResourceCenter;

/**
 * @author sunqichang/������
 * 
 */
public class CompreExcel implements CompreExportInterface {
	private static Logger log = Logger.getLogger(CompreExcel.class);

	private WritableWorkbook wb = null;

	private WritableSheet sheet = null;
	
//	public void createReport_hostevent(String filename) {
//		if (impReport.getTable() == null) {
//			fileName = null;
//			return;
//		}
//		WritableWorkbook wb = null;
//		try {
//			fileName = ResourceCenter.getInstance().getSysPath() + filename;
//			wb = Workbook.createWorkbook(new File(fileName));
//
//			String starttime = (String) reportHash.get("starttime");
//			String totime = (String) reportHash.get("totime");
//			WritableSheet sheet = wb.createSheet("�¼�ͳ�Ʊ���", 0);
//			List memlist = (List) reportHash.get("eventlist");
//
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//			// WritableFont labelFont = new
//			// WritableFont(WritableFont.createFont("����"), 12,
//			// WritableFont.BOLD, false);
//			// WritableCellFormat labelFormat = new
//			// WritableCellFormat(labelFont);
//			//
//			// WritableCellFormat _labelFormat = new WritableCellFormat();
//			// _labelFormat.setBackground(jxl.format.Colour.GRAY_25);
//			//
//			// WritableCellFormat p_labelFormat = new WritableCellFormat();
//			// p_labelFormat.setBackground(jxl.format.Colour.ICE_BLUE);
//			//
//			// WritableCellFormat b_labelFormat = new WritableCellFormat();
//			// b_labelFormat.setBackground(jxl.format.Colour.GRAY_50);
//
//			Label tmpLabel = null;
//			tmpLabel = new Label(0, 0, "�����������¼�ͳ�Ʊ���", labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(0, 1, "��������ʱ��:" + impReport.getTimeStamp(), labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(0, 2, "����ͳ��ʱ���: " + starttime + " �� " + totime, labelFormat);
//			sheet.addCell(tmpLabel);
//			sheet.mergeCells(0, 0, 11, 0);
//			sheet.mergeCells(0, 1, 11, 1);
//			sheet.mergeCells(0, 2, 11, 2);
//			tmpLabel = new Label(0, 4, "���", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(1, 4, "IP��ַ", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(2, 4, "�豸����", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(3, 4, "����ϵͳ", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(4, 4, "�¼�����", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(5, 4, "��ͨ", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(6, 4, "����", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(7, 4, "����", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(8, 4, "��ͨ���¼�", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(9, 4, "�ڴ��¼�", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(10, 4, "�����¼�", _labelFormat);
//			sheet.addCell(tmpLabel);
//			tmpLabel = new Label(11, 4, "CPU�¼�", _labelFormat);
//
//			sheet.addCell(tmpLabel);
//
//			// I_MonitorIpList monitorManager=new MonitoriplistManager();
//			int row = 5;
//			if (memlist != null && memlist.size() > 0) {
//				for (int i = 0; i < memlist.size(); i++) {
//					p_labelFormat = super.colorChange(i);
//					List mlist = (List) memlist.get(i);
//					row = row + (i);
//					tmpLabel = new Label(0, 5 + i, i + 1 + "", p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(1, 5 + i, (String) mlist.get(0), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(2, 5 + i, (String) mlist.get(1), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(3, 5 + i, (String) mlist.get(2), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(4, 5 + i, (String) mlist.get(3), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(5, 5 + i, (String) mlist.get(4), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(6, 5 + i, (String) mlist.get(5), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(7, 5 + i, (String) mlist.get(6), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(8, 5 + i, (String) mlist.get(7), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(9, 5 + i, (String) mlist.get(8), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(10, 5 + i, (String) mlist.get(9), p_labelFormat);
//					sheet.addCell(tmpLabel);
//					tmpLabel = new Label(11, 5 + i, (String) mlist.get(10), p_labelFormat);
//					sheet.addCell(tmpLabel);
//				}
//			}
//
//			if (impReport.getChart() != null) {
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				try {
//					ChartUtilities.writeChartAsPNG(baos, impReport.getChart().getChart(), impReport.getChart()
//							.getWidth(), impReport.getChart().getHeight());
//				} catch (IOException ioe) {
//				}
//				WritableImage wi = new WritableImage(2, 10000 + 5, 8, 12, baos.toByteArray());
//				sheet.addImage(wi);
//			}
//
//			wb.write();
//		} catch (Exception e) {
//			// SysLogger.error("Error in ExcelReport.createReport()",e);
//			SysLogger.error("", e);
//		} finally {
//			try {
//				if (wb != null)
//					wb.close();
//			} catch (Exception e) {
//			}
//		}
//	}
	
	

	/**
	 * ������ʽ
	 */
	private WritableFont titlefont = new WritableFont(WritableFont.createFont("����"), 14, WritableFont.BOLD, false);

	/**
	 * ʱ����ʽ
	 */
	private WritableFont timefont = new WritableFont(WritableFont.createFont("����"), 12, WritableFont.NO_BOLD, false);

	private WritableFont tableTitleFont = new WritableFont(WritableFont.createFont("����"), 11, WritableFont.BOLD, false);

	private WritableFont tableFont = new WritableFont(WritableFont.createFont("����"), 11, WritableFont.NO_BOLD, false);

	private int x = 0;

	private int y = 0;

	/**
	 * ռexcel��������
	 */
	private int excelCols = 12;

	/**
	 * @param path
	 */
	public CompreExcel(String path) {
		try {
			wb = Workbook.createWorkbook(new File(path));
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertTitle(java.lang.String,
	 *      int, java.lang.String)
	 */
	public void insertTitle(String title, int cols, String timefromto) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet(title, 0);
		}
		WritableCellFormat titleFormat = new WritableCellFormat(titlefont);
		WritableCellFormat timeFormat = new WritableCellFormat(timefont);
		titleFormat.setBackground(Colour.GRAY_25);
		titleFormat.setAlignment(Alignment.CENTRE);
		titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		timeFormat.setBackground(Colour.GRAY_25);
		timeFormat.setAlignment(Alignment.CENTRE);
		timeFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		Label tmpLabel = new Label(0, 0, title, titleFormat);
		Label timeLabel = new Label(0, 1, "ʱ�䣺" + timefromto, timeFormat);
		y += 2;
		sheet.addCell(tmpLabel);
		sheet.addCell(timeLabel);
		if (cols > 0) {
			sheet.mergeCells(0, 0, excelCols - 1, 0);
			sheet.mergeCells(0, 1, excelCols - 1, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertChart(java.lang.String)
	 */
	public void insertChart(String path) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet("sheet", 1);
		}
		File file = new File(path);
		// ��sheet��������ͼƬ,0, 10, 10, 12�ֱ������,��,ͼƬ���ռ������,�߶�ռλ������
		if (file != null && file.exists()) {
			x = 0;
			sheet.addImage(new WritableImage(x, y, excelCols, 14, file));
			x += excelCols;
			y += 14;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#insertTable(java.util.ArrayList)
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception {
		if (sheet == null) {
			sheet = wb.createSheet("sheet", 1);
		}
		try {
			WritableCellFormat labelFormat0 = new WritableCellFormat(tableTitleFont);
			labelFormat0.setShrinkToFit(true);
			labelFormat0.setBackground(jxl.format.Colour.GRAY_25);
			labelFormat0.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat0.setAlignment(Alignment.CENTRE);
			labelFormat0.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat labelFormat = new WritableCellFormat(tableFont);
			labelFormat.setShrinkToFit(true);
			labelFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormat.setAlignment(Alignment.CENTRE);
			labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat labelFormats = new WritableCellFormat(tableFont);
			labelFormats.setShrinkToFit(true);
			labelFormats.setBackground(Colour.ICE_BLUE);
			labelFormats.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
			labelFormats.setAlignment(Alignment.CENTRE);
			labelFormats.setVerticalAlignment(VerticalAlignment.CENTRE);
			Label tmpLabel = null;
			for (int k = 0; k < tableal.size(); k++) {
				String[] row = tableal.get(k);
				int colspan = excelCols / row.length;
				x = 0;
				for (int j = 0; j < row.length; j++) {
					if (k == 0) {
						tmpLabel = new Label(x, y, row[j], labelFormat0);
					} else {
						if (k % 2 == 0) {
							tmpLabel = new Label(x, y, row[j], labelFormats);
						} else {
							tmpLabel = new Label(x, y, row[j], labelFormat);
						}
					}
					if (j == row.length - 1) {
						sheet.addCell(tmpLabel);
						sheet.mergeCells(x, y, x + colspan + excelCols % row.length - 1, y);
						x += colspan + excelCols % row.length;
					} else {
						sheet.addCell(tmpLabel);
						sheet.mergeCells(x, y, x + colspan - 1, y);
						x += colspan;
					}
				}
				y++;
			}
			// CellView cv = new CellView();
			// cv.setAutosize(true);
			// for (int i = 0; i < tableal.get(0).length; i++) {
			// // �����п�����Ӧ
			// sheet.setColumnView(i, cv);
			// }
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.afunms.report.export.ExportInterface#save()
	 */
	public void save() throws Exception {
		wb.write();
		wb.close();
		log.info("------Excel saved successfully!------");
	}

}
