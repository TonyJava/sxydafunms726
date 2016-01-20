package com.afunms.system.shiro;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.AfterClass;
import org.junit.Test;

public class UrlAndWildcardPermissionResolverTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testResolvePermission() {

		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm-test.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
		Subject subject = SecurityUtils.getSubject();
		//存放的是英文字母大写的hash密码
		String hashedPassword =  (new Md5Hash("jinzhong")).toString().toUpperCase();
		UsernamePasswordToken token = new UsernamePasswordToken("jinzhong",hashedPassword);
		try{
			subject.login(token);
		}catch(AuthenticationException e){
			e.printStackTrace();
		}
		assertEquals(true, subject.isPermitted( "menu:*:165"));
		assertEquals(true, subject.isPermitted("+network.do?action=list&jp=1"));
		
	}

}
