/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import com.afunms.system.dao.*;
import com.afunms.system.model.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SendMailManager 
{
	private String test;
    private MimeMessage mimeMessage = null;
    private String saveAttachPath = "";//附件下载后的存放目录
    private StringBuffer bodytext = new StringBuffer(); //存放邮件内容的StringBuffer对象
	private String dateformat = "yy-MM-ddHH:mm"; //默认的日前显示格式
	
	public boolean SendMail(String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			//String fromAddr = "supergzm@sina.com";
			String fromAddr="";
			sendMail sendmail = new sendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmail();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	public boolean SendMailWithFile(String fromAddress,String receivemailaddr,String body,String fileName)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = fromAddress;
			//String fromAddr="";
			sendMail sendmail = new sendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmailWithFile(fileName);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean SendMailNoFile(String fromAddress,String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = fromAddress;
			//String fromAddr="";
//			System.out.println("in the send Mail manager page get the body is:"+body);
			sendMail sendmail = new sendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmailNoFile();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean SendMyMail(String receivemailaddr,String body)
	{
		String reporttime = ProjectProperties.getDayReporTime();
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try{
			list = emaildao.getByFlage(1);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emaildao.close();
		}
		if(list != null && list.size()>0)
		{
			vo = (AlertEmail)list.get(0);
		}
		if(vo == null)return false;
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		SysLogger.info(mailaddr+"==="+mailpassword+"==="+mailsmtp);
		boolean flag = false;
		try{
			Address[] ccAddress = {new InternetAddress("hukelei@dhcc.com.cn"),new InternetAddress("rhythm333@163.com")};
			String fromAddr = "";
			sendMail sendmail = new sendMail(mailsmtp,mailaddr,
					mailpassword,receivemailaddr, "网管服务告警邮件", body,fromAddr,ccAddress);
			
			try{
				flag = sendmail.sendmail();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return flag;
	}
	
	/*
	 * 得到收件人的地址
	 */
	public Address[] geReceivemailaddr(String alarmwayId) {
		List<User> userList = new ArrayList<User>();
		UserDao userdao = new UserDao();
		DBManager db = null;
		ResultSet rs = null;
		String userIds = "";
		String[] address = null;
		try {
			db = new DBManager();
			String sql = "select a.user_ids from nms_alarm_way_detail a  where a.alarm_way_id in (select id from nms_alarm_way where is_mail_alarm='1') and  a.alarm_category='mail' and  a.alarm_way_id = '"+alarmwayId+"'";
			rs = db.executeQuery(sql);
			while (rs.next()) {
				userIds += rs.getString("user_ids")+"','";
			}
			System.out.println(userIds.substring(0,userIds.length()-3));
			userIds = userIds.substring(0,userIds.length()-3);
			userList = userdao.findbyIDs(userIds);
			address = new String[userList.size()];
			for (int i = 0; i < userList.size() ; i++) {
				User user = userList.get(i);
				address[i] = user.getEmail();
				System.out.println(user.getEmail());
			}
		} catch (Exception e) {
			SysLogger.error("getReceivemailaddr", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rs = null;
			}
			if (db != null) {
				db.close();
				db = null;
			}
		}
		
		return getAddress(address);
	}
	
	public Address[] getAddress(String[] address) {  
	      Address[] addrs = new InternetAddress[address.length];  
		   for (int i = 0; i < address.length; i++) {
			   try {
				addrs[i] = new InternetAddress(address[i]);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	      return addrs;  
   } 
	
	/*
	 * 发送邮件
	 */
	public boolean SendMail(Address[] receivemailaddr, String body) {
		// 取消每次采集任务是直接发送邮件 修改为定时发送邮件
		//return true;
		 String reporttime = ProjectProperties.getDayReporTime();
		 AlertEmail vo = null;
		 AlertEmailDao emaildao = new AlertEmailDao();
		 List list = null;
		 try {
		 list = emaildao.getByFlage(1);
		 } catch (Exception e) {
		 e.printStackTrace();
		 } finally {
		 emaildao.close();
		 }
		 if (list != null && list.size() > 0) {
		 vo = (AlertEmail) list.get(0);
		 }
		 if (vo == null)
		 return false;
		 String mailaddr = vo.getUsername();
		 String mailpassword = vo.getPassword();
		 String mailsmtp = vo.getSmtp();
		 String mail_address = vo.getMailAddress(); // 新添加
		 boolean flag = false;
		 try {
		 Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
		 //String fromAddr = mail_address;
		 sendMail sendmail = new sendMail(mailsmtp, mailaddr, mailpassword, receivemailaddr, "模拟告警巡检邮件", body, mail_address, ccAddress);
		
		 try {
		 flag = sendmail.sendmail(receivemailaddr);
		 } catch (Exception ex) {
		 ex.printStackTrace();
		 }
		 } catch (Exception ex) {
		 ex.printStackTrace();
		 }
		 return flag;
	}

	public static void main(String args[]) throws AddressException {
		SendMailManager mailManager = new SendMailManager();
		Address[] add = mailManager.geReceivemailaddr("253");
		mailManager.SendMail(add, "test");

	}
	
	/**
	*构造函数
	*/
	public SendMailManager(){}

}