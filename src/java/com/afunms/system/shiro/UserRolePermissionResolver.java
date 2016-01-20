package com.afunms.system.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.session.Session;

import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.model.Function;
import com.afunms.system.model.RoleFunction;

public class UserRolePermissionResolver implements RolePermissionResolver {

	private final Log logger = LogFactory.getLog(UserRolePermissionResolver.class);
	@Override
	public Collection<Permission> resolvePermissionsInRole(String roleId) {
		// TODO Auto-generated method stub
		List<Permission> permissionList = new ArrayList();
		// ������ֲ������﷨�����������Ժ��޸�

		// ����ɫ��Ӧ�Ĳ˵���ת��Ϊshiro�˵�Ȩ�ޣ��Ա������˵�Ȩ�޵���֤
		Session session = SecurityUtils.getSubject().getSession();
		List<Function> functionList = (List<Function>)session.getAttribute("roleFunction");
		for (Function function : functionList) {
			logger.debug(function.getId());
			permissionList.add(new WildcardPermission("menu:*:"
					+ function.getId()));
		}
		return permissionList;
	}

}
