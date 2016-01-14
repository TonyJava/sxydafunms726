package com.afunms.system.realm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

import com.afunms.common.base.ErrorMessage;
import com.afunms.common.util.CommonAppUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SysUtil;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.dao.UserDao;
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
		logger.info("授权");
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		User user = (User)principalCollection.getPrimaryPrincipal();
		//authorizationInfo.addRole(user.getRole()+"");
		
		//类的名字不符合语法惯例，留作以后修改
		RoleFunctionDao roleFunctionDao = new RoleFunctionDao();
		List<RoleFunction> roleFunctionList = roleFunctionDao.findByRoleId(user.getRole()+"");
		for(RoleFunction roleFunction:roleFunctionList){
			authorizationInfo.addStringPermission("menu:*:"+roleFunction.getFuncid());
		}
		return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();
        
        String password = new String((char[])token.getCredentials());
     
      
      
        UserDao dao = new UserDao();
    	User vo = dao.findByUserId(username);
    
    	dao.close();
    	
    	if (vo == null) 
    	{
    		throw new  UnknownAccountException();
    	}

    	logger.info("用户"+username+"登录成功");
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(vo, vo.getPassword(), getName());
    }
}
