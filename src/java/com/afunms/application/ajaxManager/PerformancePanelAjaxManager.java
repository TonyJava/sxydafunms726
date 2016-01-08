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
 * @version 创建时间：Aug 18, 2011 10:53:00 AM
 * 类说明
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
	 * 检测面板名称是否合法（即是存在）
	 */
	private void checkPanelName(){
		//性能面板名称
		String panelName = getParaValue("panelName");
		try {
			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//该面板名称是否已经被占用
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
	 * 编辑面板的监控指标
	 * @return
	 */
	private void editPanelIndicators(){
		//性能面板名称
		String panelName = getParaValue("panelName");
		String indicatorNames = getParaValue("indicatorNames");
		try {
			panelName = new String(panelName.getBytes("iso8859-1"),"utf-8");
			indicatorNames = new String(indicatorNames.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//得到所有告警指标名称
		if(indicatorNames != null && !"null".equals(indicatorNames)){
			indicatorNames = indicatorNames.replaceAll("root,", "");
		}
		//更新指标
		//更新面板的设备id
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
	 * 编辑面板的设备
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
		//更新面板的设备id
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
