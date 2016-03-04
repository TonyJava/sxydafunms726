package com.afunms.system.shiro;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	/* (non-Javadoc)
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#createToken(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected AuthenticationToken createToken(ServletRequest request,
			ServletResponse response) {
		// TODO Auto-generated method stub
		 String username = getUsername(request);
	     String password = getPassword(request);
		String hashedPassword =  (new Md5Hash(password)).toString().toUpperCase();
		return createToken(username, hashedPassword, request, response);
	}

	private static final String NOT_SUPPORT_GET_LOGINURL = "{"
			+"id:0,"
			+"msg:'ʹ��HTTP GET��ʽ��¼��ʱ����֧��'"
			+ "}";
	private static final String UNAUTHENTICATED = "{"
			+"id:1,"
			+"msg:'δ��¼�����ܷ�����Դ'"
			+ "}";
	
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
	/*      û�е�¼��������ǵ�¼������loginURL�ϵ�POST���󣩣����¼
	      ������ǵ�¼ҳ���POST����loginURL�ϵ�get���󣩣���ܾ��ṩ���񣬷���501��Ӧ*/
	      HttpServletRequest httpRequst = WebUtils.toHttp(request);
	      HttpServletResponse httpResponse = WebUtils.toHttp(response);
	      httpResponse.setCharacterEncoding("utf-8");
	      PrintWriter out = httpResponse.getWriter();
	     
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
               
                return executeLogin(request, response);
            } else {
            	httpResponse.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            	httpResponse.setContentType("application/json");
            	out.print(NOT_SUPPORT_GET_LOGINURL);
                return false;
            }
        } else {

//            saveRequestAndRedirectToLogin(request, response);
        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	httpResponse.setContentType("application/json");
        	out.print(UNAUTHENTICATED);
            return false;
        }
	}

	private static final String UNKNOWN_ACCOUNT = "{"
			+"id:2,"
			+"msg:'��¼���û���������"
			+ "}";
	private static final String INCORRECT_CREDENTIALS = "{"
			+"id:3,"
			+"msg:'�������'"
			+ "}";
	private static final String UNKNOWN_AUTHENTICATION_ERROR = "{"
			+"id:4,"
			+"msg:'δ֪����֤����'"
			+ "}";
	
	private static final String LOGIN_SUCCESS = "{"
			+"id:5,"
			+"msg:'��¼�ɹ�'"
			+ "}";
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
	      httpResponse.setStatus(HttpServletResponse.SC_OK);
      	httpResponse.setContentType("application/json");
      	
	      try {
			PrintWriter out = httpResponse.getWriter();
			if(e instanceof UnknownAccountException){
				out.print(this.UNKNOWN_ACCOUNT);
			}else if(e instanceof IncorrectCredentialsException){
				out.print(this.INCORRECT_CREDENTIALS);
			}else{
				out.print(UNKNOWN_AUTHENTICATION_ERROR);
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
	      httpResponse.setCharacterEncoding("utf-8");
	      httpResponse.setStatus(HttpServletResponse.SC_OK);
     	httpResponse.setContentType("application/json");
     	PrintWriter out;
		try {
			out = response.getWriter();
			out.print(this.LOGIN_SUCCESS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
		return false;
	}
	
	
}
