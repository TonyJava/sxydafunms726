package com.afunms.cabinet.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.system.model.User;

public class CabinetAlarManager extends BaseManager implements ManagerInterface{
	
	public String execute(String action) {
		if (action.equals("hostevent")) {
			
			//System.out.println("=====================+++++++hostvent========================");
			return hostevent();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	  private String hostevent()
	    {
			String tmp ="";
			List list = new ArrayList();
			int status =99;
			int level1 = 99;
			String b_time ="";
			String t_time = "";
			tmp = request.getParameter("id");
			System.out.println("===tmp===="+tmp);
			Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			System.out.println("===host===="+host);
			try{
	    	status = getParaIntValue("status");
	    	level1 = getParaIntValue("level1");
	    	if(status == -1)status=99;
	    	if(level1 == -1)level1=99;
	    	request.setAttribute("status", status);
	    	request.setAttribute("level1", level1);	    	
	    	b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String[] time = {"",""};
			String starttime = time[0];
			String endtime = time[1];	
			String time1 = request.getParameter("begindate");
			if(time1 == null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}										
			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";										
			try{
				User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);  
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime2,totime2,status+"",level1+"",
						vo.getBusinessids(),host.getId());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			request.setAttribute("list",list);
			request.setAttribute("id", tmp);
			}catch(Exception e){
				e.printStackTrace();
			}
			return "/cabinet/cabinetalarm.jsp";
	    }
		    
	
}
