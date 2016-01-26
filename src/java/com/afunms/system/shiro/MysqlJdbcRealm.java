package com.afunms.system.shiro;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;

import com.afunms.common.base.ErrorMessage;
import com.afunms.common.util.CommonAppUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.manage.UserManager;
import com.afunms.system.model.Function;
import com.afunms.system.model.RoleFunction;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateRoleFunctionTable;

import java.util.List;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-25
 * <p>Version: 1.0
 */
public class MysqlJdbcRealm extends AuthorizingRealm {

	 @Override
	    public void clearCache(PrincipalCollection principals) {
	        super.clearCache(principals);
	    }

	    public void clearAllCachedAuthorizationInfo() {
	        getAuthorizationCache().clear();
	    }

	    public void clearAllCachedAuthenticationInfo() {
	        getAuthenticationCache().clear();
	    }

	    public void clearAllCache() {
	        clearAllCachedAuthenticationInfo();
	        clearAllCachedAuthorizationInfo();
	    }
	/* (non-Javadoc)
	 * @see org.apache.shiro.realm.AuthorizingRealm#clearCachedAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
	public void clearCachedAuthorizationInfo(PrincipalCollection arg0) {
		// TODO Auto-generated method stub
		super.clearCachedAuthorizationInfo(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.realm.AuthenticatingRealm#clearCachedAuthenticationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
	public void clearCachedAuthenticationInfo(PrincipalCollection arg0) {
		// TODO Auto-generated method stub
		super.clearCachedAuthenticationInfo(arg0);
	}

	private final Log logger = LogFactory.getLog(MysqlJdbcRealm.class);
    @Override
    public String getName() {
        return "MysqlJdbcRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken; //��֧��UsernamePasswordToken���͵�Token
    }

   
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		// TODO Auto-generated method stub
		logger.info("��Ȩ");
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		User user = (User)principalCollection.getPrimaryPrincipal();
		authorizationInfo.addRole(user.getRole()+"");
		//����PermissionResolver��ִ��ʱ��
//		authorizationInfo.addStringPermission("menu:*:222"); 
		return authorizationInfo;
	}
/**
 * 
 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();
        
        String password =  new String((char[])token.getCredentials());
     
      
      
        UserDao dao = new UserDao();
    	User vo = dao.findByUserId(username);
    
    	dao.close();
    	
    	if (vo == null) 
    	{
    		throw new  UnknownAccountException();
    	}

    	logger.info("�û�"+username+"��¼�ɹ�");
    	
        //��������֤��֤�ɹ�������һ��AuthenticationInfoʵ�֣�
        return new SimpleAuthenticationInfo(vo, vo.getPassword(), getName());
    }
}
