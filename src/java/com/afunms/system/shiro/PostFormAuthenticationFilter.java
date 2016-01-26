package com.afunms.system.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
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
//		�κ�URL����ֻҪ��¼����������¼�ͼ�ס�ң�������
//		���ǵ�¼��������Ҫ�ߵ���ǰ��¼���û���Ȼ���¼
//		ͨ����ס�ҵ�URL���������
		  Subject subject = getSubject(request, response);
		  if(subject.isAuthenticated()||subject.isRemembered())		    	  UserManager.addUserMenuToSubject(subject, (User)subject.getPrincipal());

	      if(subject.isAuthenticated()){
	    		if (isLoginRequest(request, response)&&isLoginSubmission(request, response)) {/*
	    			//��¼��������Ҫ��ջ���
	    			RealmSecurityManager securityManager = (RealmSecurityManager)SecurityUtils.getSecurityManager();
	    			MysqlJdbcRealm realm = (MysqlJdbcRealm)securityManager.getRealms().iterator().next();
	    			realm.clearAllCachedAuthenticationInfo();
	    			realm.clearAllCachedAuthorizationInfo();
	    		*/}
	    		else{     
	    			return true;
	    		}
	      }else if(subject.isRemembered()){
	    		if (isLoginRequest(request, response)) {
	    			this.issueSuccessRedirect(request, response);
	    			return false;
	    		}
	    	  return true;
	      }
//	      û�е�¼��������ǵ�¼������loginURL�ϵ�POST���󣩣����¼
//	      ����ǵ�¼ҳ������loginURL�ϵ�get���󣩣����ض��򵽵�¼ҳ��
		if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
               
                return executeLogin(request, response);
            } else {
         
                return true;
            }
        } else {
           
            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
	}
}
