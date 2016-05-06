package com.afunms.system.shiro;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.afunms.system.manage.UserManager;
import com.afunms.system.model.User;

public class PostFormAuthenticationFilter extends FormAuthenticationFilter {

	/* 
	 * ��ȡ��Ҫ��֤���û���
	 * ����û�����null���򷵻ؿ��ַ���
	 */
	@Override
	protected String getUsername(ServletRequest request) {
		// TODO Auto-generated method stub
        String value = "";
		 if(request instanceof HttpServletRequest) {
			 value =  WebUtils.toHttp(request).getParameter(this.getUsernameParam());
				if(value != null){
					return value;
				}
		 }	
		 return value;
	}

	/* 
	 * ��ȡ��Ҫ��֤������
	 * ���������null���򷵻ؿ��ַ���
	 * 
	 */
	@Override
	protected String getPassword(ServletRequest request) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
        String value = "";
		 if(request instanceof HttpServletRequest) {
			 value =  WebUtils.toHttp(request).getParameter(this.getPasswordParam());
				if(value != null){
					return value;
				}
		 }	
		 return value;
	}


	/* (non-Javadoc)
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#createToken(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected AuthenticationToken createToken(ServletRequest request,
			ServletResponse response) {
		// TODO Auto-generated method stub
		 String username = getUsername(request);
	     String password = getPassword(request);
	     System.out.println("username="+username+" password="+password);
		String hashedPassword =  (new Md5Hash(password)).toString().toUpperCase();
		return createToken(username, hashedPassword, request, response);
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#isLoginSubmission(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean isLoginSubmission(ServletRequest request,
			ServletResponse response) {
		// TODO Auto-generated method stub
        return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(this.GET_METHOD);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.shiro.web.filter.AccessControlFilter#onPreHandle(javax.servlet
	 * .ServletRequest, javax.servlet.ServletResponse, java.lang.Object)
	 */
	@Override
	public boolean onPreHandle(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		// TODO Auto-generated method stub
	/*	�κ�URL����ֻҪ��¼����������¼�ͼ�ס�ң�������
		���ǵ�¼��������Ҫ�ߵ���ǰ��¼���û���Ȼ���¼
		ͨ����ס�ҵ�URL���������*/
		  Subject subject = getSubject(request, response);
		  if(subject.isAuthenticated()||subject.isRemembered())		    	  UserManager.addUserMenuToSubject(subject, (User)subject.getPrincipal());

	      if(subject.isAuthenticated()){
	    		if (!(isLoginRequest(request, response)&&isLoginSubmission(request, response))) {
	    			return true;
	    		}
	      }else if(subject.isRemembered()){
	    		if (isLoginRequest(request, response)) {
	    			this.issueSuccessRedirect(request, response);
	    			return false;
	    		}
	    	  return true;
	      }
	/*      û����֤���������֤������loginURL�ϵ�GET���󣩣��������֤
	      ���������֤����
	      1loginURL�ϵ�POST������ܾ��ṩ���񣬷���400��Ӧ
	      2��������������401����ʾ�û�û��Ȩ�ޣ���Ҫ��֤
	      */
	      HttpServletRequest httpRequest = WebUtils.toHttp(request);
	      HttpServletResponse httpResponse = WebUtils.toHttp(response);
	      httpResponse.setCharacterEncoding("utf-8");
	      PrintWriter out = httpResponse.getWriter();
	     
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
               
                return executeLogin(request, response);
            } else {
            	httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	httpResponse.setContentType("application/json");
            	out.print(NOT_SUPPORT_GET_LOGINURL+"");
                return false;
            }
        } else {

//            saveRequestAndRedirectToLogin(request, response);
        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	httpResponse.setContentType("application/json");
        	out.print(UNAUTHENTICATED+"");
            return false;
        }
	}
	private static final LoginMessage NOT_SUPPORT_GET_LOGINURL = new LoginMessage(0,"ʹ��HTTP POST��ʽ��¼��ʱ����֧��'");
	private static final LoginMessage UNAUTHENTICATED = new LoginMessage(2,"δ��¼�����ܷ�����Դ");

	private static final LoginMessage UNKNOWN_ACCOUNT = new LoginMessage(3,"��¼���û���������");
	private static final LoginMessage INCORRECT_CREDENTIALS = new LoginMessage(4,"�������");
	private static final LoginMessage UNKNOWN_AUTHENTICATION_ERROR = new LoginMessage(5,"δ֪����֤����");
	
	public static  class LoginMessage{
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return JSONObject.fromObject(this).toString();
		}
		private int id;
		private String msg;
		/**
		 * @param id
		 * @param msg
		 */
		public LoginMessage(int id, String msg) {
			super();
			this.id = id;
			this.msg = msg;
		}
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}
		/**
		 * @return the msg
		 */
		public String getMsg() {
			return msg;
		}
		/**
		 * @param msg the msg to set
		 */
		public void setMsg(String msg) {
			this.msg = msg;
		}
	
	}
	/* 
	 * ��¼ʧ�ܺ󣬲�����ִ��ʣ���filterchain�����Ƿ�����Ӧ���ͻ��ˣ����а�����Ӧ�Ĵ�����Ϣ
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginFailure(org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationException, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token,
			AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		// TODO Auto-generated method stub
		  HttpServletRequest httpRequst = WebUtils.toHttp(request);
	      HttpServletResponse httpResponse = WebUtils.toHttp(response);
	      httpResponse.setCharacterEncoding("utf-8");
	      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      	httpResponse.setContentType("application/json");
      	
	      try {
			PrintWriter out = httpResponse.getWriter();
			if(e instanceof UnknownAccountException){
				out.print(this.UNKNOWN_ACCOUNT+"");
			}else if(e instanceof IncorrectCredentialsException){
				out.print(this.INCORRECT_CREDENTIALS+"");
			}else{
				out.print(this.UNKNOWN_AUTHENTICATION_ERROR+"");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
//		��¼ʧ�ܣ��жϹ�������
		return false;
	}
	/* 
	 * ��¼�ɹ���������ִ��ʣ���filterchain�����Ƿ�����Ӧ���ͻ��ˣ�������¼�ɹ�����Ϣ
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.subject.Subject, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
			Subject subject, ServletRequest request, ServletResponse response)  {
		// TODO Auto-generated method stub
		 HttpServletRequest httpRequst = WebUtils.toHttp(request);
	      HttpServletResponse httpResponse = WebUtils.toHttp(response);
	 
	      User user = (User)subject.getPrincipal();

	      httpResponse.setCharacterEncoding("utf-8");
	      httpResponse.setStatus(HttpServletResponse.SC_OK);
     	httpResponse.setContentType("application/json");
     	PrintWriter out;
		try {
			out = response.getWriter();
			out.print(JSONObject.fromObject(user).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
		return false;
	}
	
	
}
