package com.afunms.linkReport.report;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.lowagie.text.DocumentException;

public interface LinkExportInterface {
	/**
	 * �������
	 * 
	 * @param title
	 *            ����
	 * @param cols
	 *            ����
	 * @param timefromto
	 *            ʱ��
	 */
	public void insertTitle(String title, int cols, String timefromto) throws Exception;

	/**
	 * ������
	 * 
	 * @param tableal
	 *            ���ArrayList<String[]>
	 * @throws IOException
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception;

	/**
	 * ����ͼƬ
	 * 
	 * @param path
	 *            ͼƬ·��
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void insertChart(String path) throws Exception;

	/**
	 * �������
	 * @param content
	 * 				����
	 * @param fontSize
	 * 				�����С
	 * @param fontBold
	 * 				��������
	 * @param el
	 * 				λ��
	 * @throws Exception
	 */
	public void insertContent(String content, int fontSize, int fontBold, int el) throws Exception ;
	/**
	 * �����ļ�
	 * 
	 * @throws Exception
	 */
	
	public void save() throws Exception;
}
