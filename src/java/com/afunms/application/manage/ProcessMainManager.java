package com.afunms.application.manage;

import java.util.List;

import com.afunms.application.course.dao.LsfClassNodeDao;
import com.afunms.application.course.dao.Lsfclassdao;
import com.afunms.application.dao.ProcessMainDao;
import com.afunms.application.model.ProcessMainModel;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.DiskconfigDao;
import com.afunms.config.model.Diskconfig;

public class ProcessMainManager extends BaseManager implements ManagerInterface{

	public String execute(String action) {
		if(action.equals("list")){
	 		return list();  
	 	}
		if(action.equals("showedit")){
			return readyEdit();
		}if(action.equals("save")){
			return save();
		}if(action.equals("find")){
			return list();
		}if(action.equals("delete")){
			return delete();
		}
		return null;
	}
	public String save(){
		String ip = getParaValue("ip");
		String nodeid = getParaValue("nodeid");//设备id
		String proname =  getParaValue("proname");//进程名
		String mon_flag =  getParaValue("mon_flag");//是否主要进程
		ProcessMainDao dao = new ProcessMainDao();
		ProcessMainModel vo = new ProcessMainModel();
		vo.setNodeid(nodeid);
		vo.setProcessName(proname);
		vo.setMon_flag(mon_flag);
		vo.setIpaddress(ip);
		dao.save(vo);
		System.out.println("==vo.getIpaddress="+vo.getIpaddress());
		return "/processMain.do?action=list&ipaddress="+vo.getIpaddress();
		
	}
	private String readyEdit(){
		String ipaddress = getParaValue("ip");//ip
		String thevalue = getParaValue("thevalue");//进程名称
		String nodeid = getParaValue("nodeid");//设备id
		String alias =  getParaValue("Alias");//设备别名
		request.setAttribute("ip", ipaddress);
		request.setAttribute("thevalue", thevalue);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("Alias", alias);
		request.setAttribute("Ismain", getParaValue("Ismain"));
		ProcessMainDao dao = new ProcessMainDao();
		int flag = dao.findByNodeidMainProcess(nodeid,thevalue);
		request.setAttribute("flag", flag);
	    return "/topology/process_main/edit.jsp";
	}
//	private String list(){
//		String ipaddress = getParaValue("ipaddress");
//		ProcessMainDao dao = new ProcessMainDao();
//		List ls = dao.getProcessList(ipaddress);
//		request.setAttribute("ips", ls);
//		//List ls_ip = dao.listByPage(curpage,id);
//		//request.setAttribute("ls_ip", ls_ip);
//		dao = new ProcessMainDao();
//		setTarget("/application/process_main/list.jsp");
//        return list(dao);
//	}
	
	
	/**
	 * 在页面中进行列表展示
	 * @return
	 */
		public String list() {
			ProcessMainDao dao = new ProcessMainDao();
			setTarget("/topology/process_main/list.jsp");
			return list2(dao);
		}
	 /**
	    * 分页显示记录
	    * targetJsp:目录jsp
	    */
	   protected String list2(ProcessMainDao dao)
	   {
		   //System.out.println("=================list2======================123123");
		   String targetJsp = null;
		   String ip = getParaValue("ipaddress");
		   if(null !=ip){
		   System.out.println("==ip=="+ip.toString());
		   }
		   int perpage = getPerPagenum();
	       List list = dao.listByPage2(getCurrentPage(),perpage,ip);
	       List ls_ip = dao.getProcessList(null);
	       
	       if(list==null) return null; 
	       request.setAttribute("page",dao.getPage());
	       request.setAttribute("list",list);
	     
	       request.setAttribute("ls_ip", ls_ip);
	       targetJsp = getTarget(); 
	      // dao.close();
		   return targetJsp;
	   }
	
	public String delete(){		
	   
		//System.out.println("=================deltet===");
		String[] ids = getParaArrayValue("checkbox");
		ProcessMainDao dao = new ProcessMainDao();
		try
		{
			if(ids!=null && ids.length>0){
				dao.delete(ids);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
			
        return "/processMain.do?action=list"; 
	}
}