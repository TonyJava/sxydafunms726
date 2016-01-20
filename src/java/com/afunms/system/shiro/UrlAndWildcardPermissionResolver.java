package com.afunms.system.shiro;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

import com.afunms.system.model.Function;

public class UrlAndWildcardPermissionResolver implements PermissionResolver {

	private final Log logger = LogFactory.getLog(UrlAndWildcardPermissionResolver.class);
	@Override
	public Permission resolvePermission(String permissionString) {
		// TODO Auto-generated method stub
		logger.debug(permissionString);

		//如果权限字符串以+开头，则是一个菜单项的URL串，需要通过URL找到菜单项的id，构造通配符权限来进行访问控制
		if(permissionString.startsWith("+")){
			List<Function> functionList = (List<Function>)SecurityUtils.getSubject().getSession().getAttribute("roleFunction");
			permissionString = permissionString.substring(1);

			for(Function function:functionList){

				String url = function.getUrl();
				url = url ==null?url:url.trim();
				if(permissionString.equals(url))
					return new WildcardPermission("menu:*:"+function.getId());
			}
		}
		return new WildcardPermission(permissionString);
	}

}
