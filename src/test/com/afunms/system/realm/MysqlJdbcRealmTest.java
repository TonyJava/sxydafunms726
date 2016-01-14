package com.afunms.system.realm;

import static org.junit.Assert.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.mgt.SecurityManager;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.afunms.system.util.CreateRoleFunctionTable;

public class MysqlJdbcRealmTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testDoGetAuthorizationInfoPrincipalCollection() {
		
	}

	@Test
	public void testDoGetAuthenticationInfoAuthenticationToken() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm-test.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
		Subject subject = SecurityUtils.getSubject();
		//存放的是英文字母大写的hash密码
		String hashedPassword =  (new Md5Hash("admin")).toString().toUpperCase();
		UsernamePasswordToken token = new UsernamePasswordToken("admin",hashedPassword);
		try{
			subject.login(token);
		}catch(AuthenticationException e){
			e.printStackTrace();
		}
	/*	subject.isPermitted(new WildcardPermission("menu:*:*"));
		subject.isPermitted( "menu:*:165");
		subject.isPermitted( "menu:*:7");
		subject.isPermitted( "menu:*:8");
		subject.isPermitted( "menu:*:6");*/
		
//		Assert.assertTrue(subject.hasRole("0"));
		(new CreateRoleFunctionTable()).getRoleFunctionList();
	}

}
