package com.afunms.application.course.manage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.course.dao.LsfClassProcessMonitoringDao;
import com.afunms.application.course.dao.Lsfclassdao;
import com.afunms.application.course.model.LsfClassComprehensiveModel;
import com.afunms.application.course.util.LsfClassUtil;
import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
public class LsfClassProcessMonitoringManage extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list_read();
		}if(action.equals("list_join")){
			return loadByRoomID();
		}if(action.equals("list_join_nodeid")){
			return loadByRoomID_nodeid();
		}
		return null;
	}
	/**
	 * 指定查询返回
	 * @return
	 */
	private String loadByRoomID_nodeid() {
		String rootPath = request.getContextPath();  
	   	String id = request.getParameter("id");
	   	String nodeid = request.getParameter("nodeid");
	   	request.setAttribute("id", id);
		String jsp = "/application/course/monitoringList.jsp";
		setTarget(jsp);
		LsfClassProcessMonitoringDao dao = new LsfClassProcessMonitoringDao();
		StringBuffer sql = new StringBuffer();
//		sql.append("select a.classname as a_classnme,a.classpesc,b.enable as b_enable,b.logflg as b_logflg,c.classid as c_classid,c.nodeid as c_nodeid,c.logcoud as c_logcount,c.master,c.jid,c.alarm from lsf_class a,lsf_class_node b,lsf_data_temp c ");
//		sql.append("where b.classid=c.classid and b.nodeid=c.nodeid and a.classid=c.classid");
		sql.append("select d.ip_address,d.alias ,a.classname as a_classnme,a.classpesc,b.enable as b_enable,b.logflg as b_logflg,c.classid as c_classid,c.nodeid ");
		sql.append("as c_nodeid,c.logcoud as c_logcount,c.master,c.alarm,b.jid,d.type,d.sys_name ");
		sql.append("from lsf_class a,lsf_class_node b,lsf_data_temp c,topo_host_node d ");
		sql.append("where b.classid=c.classid and b.nodeid=c.nodeid and a.classid=c.classid ");
		sql.append("and c.nodeid=d.id and d.id= b.nodeid");
		sql.append(" and c.classid = ");
		sql.append(id+";");
		List list = dao.loadForm_join(sql.toString());
		request.setAttribute("list",list);
		return jsp;
	}
	
	
	/**
	 * 指定查询返回
	 * @return
	 */
	private String loadByRoomID() {
		String rootPath = request.getContextPath();  
	   	String id = request.getParameter("id");
	   	request.setAttribute("id", id);
		String jsp = "/application/course/monitoringList.jsp";
		setTarget(jsp);
		LsfClassProcessMonitoringDao dao = new LsfClassProcessMonitoringDao();
		StringBuffer sql = new StringBuffer();
//		sql.append("select a.classname as a_classnme,a.classpesc,b.enable as b_enable,b.logflg as b_logflg,c.classid as c_classid,c.nodeid as c_nodeid,c.logcoud as c_logcount,c.master,c.jid,c.alarm from lsf_class a,lsf_class_node b,lsf_data_temp c ");
//		sql.append("where b.classid=c.classid and b.nodeid=c.nodeid and a.classid=c.classid");
		sql.append("select d.ip_address,d.alias ,a.classname as a_classnme,a.classpesc,b.enable as b_enable,b.logflg as b_logflg,c.classid as c_classid,c.nodeid ");
		sql.append("as c_nodeid,c.logcoud as c_logcount,c.master,c.alarm,b.jid,d.type,d.sys_name from lsf_class a,lsf_class_node b,lsf_data_temp c,topo_host_node d ");
		sql.append("where b.classid=c.classid and b.nodeid=c.nodeid and a.classid=c.classid and c.nodeid=d.id");
		List list = dao.loadForm_join(sql.toString());
		request.setAttribute("list",list);
		
//		HashMap img_path_map =new LsfClassUtil().imgEdi(list);
//		request.setAttribute("img_path_map", img_path_map);
//		if(list!=null&& list.size()>0){
//	        for(int i = 0 ; i < list.size() ; i++){
//	        	String gifimig="";
//		        LsfClassComprehensiveModel model =(LsfClassComprehensiveModel)list.get(i);
//		       	System.out.println("Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
//	        	if(model.getMaster().equals("1")&& model.getAlarm().equals("1")){
//	        		System.out.println("1111____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());	
//					gifimig="a_windows.gif";
//					System.out.println("111____gifimig:"+gifimig);	
//				}
//				//没有告警
//				if(model.getMaster().equals("1")&& model.getAlarm().equals("0")){
//					System.out.println("2222____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
//					gifimig="folder.gif";
//					System.out.println("222____gifimig:"+gifimig);	
//				}
//			 	//普通节点有告警
//				if(model.getMaster().equals("0")&& model.getAlarm().equals("1")){
//					System.out.println("3333____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
//					gifimig="folder_2.gif";
//					System.out.println("333____gifimig:"+gifimig);	
//				}
//				// 没有告警 
//				if(model.getMaster().equals("0")&& model.getAlarm().equals("0")){
//					System.out.println("4444____Master:"+model.getMaster()+"__Alarm:"+model.getAlarm());
//					gifimig="db2.gif";
//					System.out.println("444____gifimig:"+gifimig);
//				}
//				
//	        }
//	       }
		
		
		
		
		
		return jsp;
	}
	
	
	/**
	 * 在页面中进行列表展示
	 * @return
	 */
	public String list_read() {
		Lsfclassdao dao = new Lsfclassdao();
//		setTarget("/application/course/monitoringList.jsp");
		setTarget("/application/course/eqprooms.jsp");
		return list(dao);
	}
}
