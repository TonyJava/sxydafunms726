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

	private static final LoginMessage NOT_SUPPORT_GET_LOGINURL = new LoginMessage(0,"使用HTTP GET方式登录暂时不被支持'");
	private static final LoginMessage UNAUTHENTICATED = new LoginMessage(2,"未登录，不能访问资源");
	
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
	/*	任何URL请求，只要登录过（包括登录和记住我），放行
		但是登录操作，需要踢掉以前登录的用户，然后登录
		通过记住我的URL请求，则放行*/
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
	/*      没有登录过，如果是登录操作（loginURL上的POST请求），则登录
	      如果不是登录页面的POST请求（loginURL上的get请求），则拒绝提供服务，发送501响应*/
	      HttpServletRequest httpRequest = WebUtils.toHttp(request);
	      HttpServletResponse httpResponse = WebUtils.toHttp(response);
	      httpResponse.setCharacterEncoding("utf-8");
	      PrintWriter out = httpResponse.getWriter();
	     
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
               
                return executeLogin(request, response);
            } else {
            	httpResponse.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
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
	private static final LoginMessage LOGIN_SUCCESS = new LoginMessage(2,"登录成功");
	private static final LoginMessage UNKNOWN_ACCOUNT = new LoginMessage(3,"登录的用户名不存在");
	private static final LoginMessage INCORRECT_CREDENTIALS = new LoginMessage(4,"密码错误");
	private static final LoginMessage UNKNOWN_AUTHENTICATION_ERROR = new LoginMessage(5,"未知的认证错误");
	
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
	 * 登录失败后，不继续执行剩余的filterchain，而是发送响应给客户端，其中包含相应的错误信息
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
	
//		登录失败，中断过滤器链
		return false;
	}
	/* 
	 * 登录成功，不继续执行剩余的filterchain，而是发送响应给客户端，包含登录成功的信息
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
