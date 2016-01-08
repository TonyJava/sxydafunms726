package com.afunms.system.realm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import com.afunms.common.base.ErrorMessage;
import com.afunms.common.util.CommonAppUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.User;

import java.util.List;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-25
 * <p>Version: 1.0
 */
public class MysqlJdbcRealm extends AuthorizingRealm {

	private final Log logger = LogFactory.getLog(MysqlJdbcRealm.class);
    @Override
    public String getName() {
        return "MysqlJdbcRealm";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken; //仅支持UsernamePasswordToken类型的Token
    }

   
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		// TODO Auto-generated method stub
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		User user = (User)super.getAvailablePrincipal(principalCollection);
		authorizationInfo.addRole(user.getRole()+"");
		role_Function_list = getRoleFunctionListByRoleId(role_id);
		return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();  //得到用户名
        String password = (new Md5Hash(new String((char[])token.getCredentials()))).toString(); //得到密码
      
      
        UserDao dao = new UserDao();
    	User vo = dao.findByLogin(username, password.toUpperCase());
    
    	dao.close();
    	
    	if (vo == null) // 用户名或密码不正确
    	{
    		throw new  AuthenticationException();
    	}

    	logger.info("用户"+username+"登录成功");
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(vo, password, getName());
    }
}
