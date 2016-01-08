package com.afunms.linkReport.report;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.lowagie.text.DocumentException;

public interface LinkExportInterface {
	/**
	 * 插入标题
	 * 
	 * @param title
	 *            标题
	 * @param cols
	 *            列数
	 * @param timefromto
	 *            时间
	 */
	public void insertTitle(String title, int cols, String timefromto) throws Exception;

	/**
	 * 插入表格
	 * 
	 * @param tableal
	 *            表格ArrayList<String[]>
	 * @throws IOException
	 */
	public void insertTable(ArrayList<String[]> tableal) throws Exception;

	/**
	 * 插入图片
	 * 
	 * @param path
	 *            图片路径
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void insertChart(String path) throws Exception;

	/**
	 * 插入段落
	 * @param content
	 * 				内容
	 * @param fontSize
	 * 				字体大小
	 * @param fontBold
	 * 				字体类型
	 * @param el
	 * 				位置
	 * @throws Exception
	 */
	public void insertContent(String content, int fontSize, int fontBold, int el) throws Exception ;
	/**
	 * 保存文件
	 * 
	 * @throws Exception
	 */
	
	public void save() throws Exception;
}
