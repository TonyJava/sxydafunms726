/**
 * <p>Description:action center,at the same time, the control legal power</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.initialize;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.Function;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateMenuTableUtil;
import com.afunms.system.util.CreateRoleFunctionTable;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.AccreditDao;
import com.afunms.common.util.SysLogger;
import com.afunms.common.base.*;

public class Controller extends HttpServlet
{
   private static final long serialVersionUID = 541128324260833824L;
	
   public void init(ServletConfig config) throws ServletException
   {
	  
      super.init(config);
   }

   public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
   {
	   try{
		   processHttpRequest(request, response);
	   }catch(Exception e){
		   
	   }
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      processHttpRequest(request, response);
   }

   //����ҵ������
   private void processHttpRequest(HttpServletRequest request,HttpServletResponse response)
   {
      String jsp = null;
      try
      {
    	 
         request.setCharacterEncoding("gb2312");
         response.setContentType("text/html;charset=gb2312");
         //SysLogger.info("==================################");
         //�˵�����ʿ��ƣ�shiroȨ����֤���������˵���URL�޹��ɣ��޷�������Ȩ����֤
         String uri = request.getRequestURI(),rootPath=request.getContextPath();
        /* Subject subject = SecurityUtils.getSubject();
         subject.isPermitted("+"+uri.replaceFirst(rootPath+"/", ""));
         */
         StringBuffer url = request.getRequestURL();
         int lastSeparator = uri.lastIndexOf("/") + 1;
         int dotSeparator = uri.lastIndexOf(".");
         String manageClass = uri.substring(lastSeparator,dotSeparator);   //��ȡ����ӳ��

       //int auth = 1;
         int auth = 0;
         if("alarm".equals(manageClass)) //����alarm�����Ȩ��
        	auth = 1;
         
         //ʹ����shiro����¼���ʿ���
         String action = request.getParameter("action");
         ManagerInterface manager = ManagerFactory.getManager(manageClass);
           
         manager.setRequest(request);
         jsp = manager.execute(action);
         if(jsp == null)
            jsp = "/common/error.jsp?errorcode=" + manager.getErrorCode();
     
         //---------------------------------------
         /**
         *   ���ݾ���ҳ�洴���˵�menu
         **/
         CreateMenuTableUtil cmtu = new CreateMenuTableUtil();
         try{
        	 cmtu.createMenuTableUtil(jsp, request);
         }catch(Exception e){
        	 e.printStackTrace();
         }
 
         //---------------------------------------
        //SysLogger.info(jsp+"############");
         RequestDispatcher disp = getServletContext().getRequestDispatcher(jsp);
         if(disp != null && request != null && response != null )
        	 disp.forward(request, response);
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
   }

   //����Ȩ�޼��:��������ֵ -1:û�е�¼,0:û��Ȩ��,1:��Ȩ��
   public int authenticate(HttpServletRequest request)
   {
      String action = request.getParameter("action");
      if(action == null)action="";
      if(action.equals("login")||action.equals("logout")) return 1;

      int result = -1;
      try
      {
         HttpSession session = request.getSession();
         User user = (User)session.getAttribute(SessionConstant.CURRENT_USER); //��ǰ�û�
         //System.out.println(user.getName()+"---------------------------------");
         String menu = request.getParameter("menu");
         if(menu==null)
            menu = (String)session.getAttribute(SessionConstant.CURRENT_MENU);
         else
         {
            session.setAttribute(SessionConstant.CURRENT_MENU,menu);
            session.setMaxInactiveInterval(1800);
         } 
         
           if(user==null) return -1;  //û�е�¼
           result = 1;
      }
      catch(Exception e)
      {
         result = 0;
      }
      return result;
   }
   

}
