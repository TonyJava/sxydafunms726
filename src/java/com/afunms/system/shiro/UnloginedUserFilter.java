package com.afunms.system.shiro;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

public class UnloginedUserFilter extends AccessControlFilter {

	/* (non-Javadoc)
	 * @see org.apache.shiro.web.filter.authc.UserFilter#isAccessAllowed(javax.servlet.ServletRequest, javax.servlet.ServletResponse, java.lang.Object)
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) {
		// TODO Auto-generated method stub
		if (isLoginRequest(request, response)) {
            return true;
        } else {
            Subject subject = getSubject(request, response);
            // If principal is not null, then the user is known and should be allowed access.
            return subject.getPrincipal() != null;
        }
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.web.filter.authc.UserFilter#onAccessDenied(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Subject subject = SecurityUtils.getSubject();
		
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String password = httpRequest.getParameter("password");
		String username = httpRequest.getParameter("userid");
//如果是点击登录操作，即请求URL是/user.do?action=login
//		是否有用户名和密码
		boolean isDirectAccess = password==null || username == null?true :false;
		if(isDirectAccess){
			this.redirectToLogin(request, response);
		}else{
//登录成功则转发到/user.do?action=login
//			失败则转发到登录页面
			boolean isFailed = true;
			String hashedPassword =  (new Md5Hash(password)).toString().toUpperCase();
		    UsernamePasswordToken token = new UsernamePasswordToken(username,hashedPassword,false);
		    
		    String[] rememberMe =httpRequest.getParameterValues("rememberMe");
		    if(rememberMe != null) token.setRememberMe(true);
			
		    try{
		    	subject.login(token);
		    	isFailed = false;
		    }catch(IncorrectCredentialsException e){
		    	e.printStackTrace();
		    }catch(UnknownAccountException e){
		    	e.printStackTrace();
		    }catch(AuthenticationException e){
		    	e.printStackTrace();
		    }
		    if(isFailed){
		    	this.redirectToLogin(request, response);
		    }else{
		    	WebUtils.redirectToSavedRequest(request, response, "/user.do?action=login");
		    }
		    
		}
		return false;
	
	}

}
