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
		authorizationInfo.addRole(user.getRole()+"");
		//测试PermissionResolver的执行时机
//		authorizationInfo.addStringPermission("menu:*:222"); 
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
    	
    	//用户菜单
    	CreateRoleFunctionTable crft = new CreateRoleFunctionTable(); 
    	List<Function> list = crft.getRoleFunctionListByRoleId(String.valueOf(vo.getRole()));
		List<Function> menuRoot_list = crft.getAllMenuRoot(list);
		Session session = SecurityUtils.getSubject().getSession();
	
		session.setAttribute("menuRoot", menuRoot_list);
		session.setAttribute("roleFunction", list);
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(vo, vo.getPassword(), getName());
    }
}
