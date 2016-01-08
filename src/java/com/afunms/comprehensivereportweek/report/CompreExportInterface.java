package com.afunms.comprehensivereportweek.report;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.lowagie.text.DocumentException;

/**
 * �����ӿ�
 */
public interface CompreExportInterface {

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
	 * �����ļ�
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception;

}
