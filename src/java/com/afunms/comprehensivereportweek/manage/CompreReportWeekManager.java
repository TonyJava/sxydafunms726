package com.afunms.comprehensivereportweek.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.afunms.capreport.dao.SubscribeResourcesDao;
import com.afunms.capreport.dao.UtilReportDao;
import com.afunms.capreport.model.SubscribeResources;
import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.comprehensivereportweek.dao.CompreReportWeekDao;
import com.afunms.comprehensivereportweek.model.CompreReportWeekInfo;
import com.afunms.comprehensivereportweek.util.CalDateUtil;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

/**
 * 综合周报表
 * 1.主机设备
 * 2.网络设备 
 * @author Administrator
 * @project afunms
 * @date 2012-04-18
 * <p>Company: dhcc.com</p>
 */
public class CompreReportWeekManager extends BaseManager implements
									ManagerInterface{
	public String execute(String action) {
		//综合周报首页面
		if(action.equals("reportWeekList"))	
			return reportWeekList();
		return null;
	}
	/** 
	 * @return 
	 */
	public String reportWeekList(){
		//得到当前时间
		Date nowDate = new CalDateUtil().getNowDate();
		//得到当前日期的上一个周的周一
		Date pastDateM = new CalDateUtil().expDate(nowDate, 0);
		//得到当前日期的上一个周的最后一天
		String toTime = new CalDateUtil().getEndWeek(pastDateM);
		String startTime = new CalDateUtil().convert(pastDateM);
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		List list = new ArrayList();
		HostNodeDao dao = new HostNodeDao();
		list = dao.loadNetworkByBid(2,current_user.getBusinessids());
		dao.close();
		request.setAttribute("list", list);
		request.setAttribute("startTime", startTime);
		request.setAttribute("toTime", toTime);
		return "/capreport/comprehensiveweek/compreReportWeek.jsp";		
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
}