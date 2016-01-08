package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.afunms.application.dao.PerformancePanelDao;
import com.afunms.application.dao.PerformancePanelIndicatorsDao;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version ����ʱ�䣺Aug 18, 2011 10:53:00 AM
 * ��˵��
 */
public class PerformancePanelAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {

	public void execute(String action) {
		if("editPanelDevice".equals(action)){
			editPanelDevice();
		}else if("editPanelIndicators".equals(action)){
			editPanelIndicators();
		}else if("checkPanelName".equals(action)){
			checkPanelName();
		}
		return;
	}
	
	/**
	 * �����������Ƿ�Ϸ������Ǵ��ڣ�
	 */
	private void checkPanelName(){
		//�����������
		String panelName = getParaValue("panelName");
		try {
			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//����������Ƿ��Ѿ���ռ��
		boolean isExist = false;
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			isExist = performancePanelDao.checkPanelName(panelName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelDao.close();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("isExist",String.valueOf(isExist));
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
		out.close();
	}
	
	/**
	 * �༭���ļ��ָ��
	 * @return
	 */
	private void editPanelIndicators(){
		//�����������
		String panelName = getParaValue("panelName");
		String indicatorNames = getParaValue("indicatorNames");
		try {
			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
			indicatorNames = new String(indicatorNames.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//�õ����и澯ָ������
		if(indicatorNames != null && !"null".equals(indicatorNames)){
			indicatorNames = indicatorNames.replaceAll("root,", "");
		}
		//����ָ��
		//���������豸id
		PerformancePanelIndicatorsDao panelIndicatorsDao = new PerformancePanelIndicatorsDao();
		try {
			panelIndicatorsDao.updatePanelIndicators(panelName, indicatorNames);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			panelIndicatorsDao.close();
		}
	}
	/**
	 * �༭�����豸
	 * @return
	 */
	private void editPanelDevice(){
		String deviceType = request.getParameter("deviceType");
		String panelName = request.getParameter("panelName");
		try {
			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String deviceIds = getParaValue("deviceIds");
		if(deviceIds != null && !deviceIds.equals("null")){
			deviceIds = deviceIds.replaceAll("root,", "");
		}
		//���������豸id
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			performancePanelDao.updatePanelDevices(panelName, deviceIds, deviceType);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			performancePanelDao.close();
		}
	}
}
