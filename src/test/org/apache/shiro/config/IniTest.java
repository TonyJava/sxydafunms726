package org.apache.shiro.config;

import static org.junit.Assert.*;

import java.util.Collection;

import org.apache.shiro.config.Ini.Section;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IniTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadFromPath() {
		Ini ini = new Ini();
		ini.loadFromPath("classpath:shiro-realm.ini");
		IniShiroFilterProxy filter = new IniShiroFilterProxy();
		filter.applySecurityManagerProxy(ini);
	}

}
class IniShiroFilterProxy extends IniShiroFilter{
	public void applySecurityManagerProxy(Ini ini){
		applySecurityManager(ini);
	}
}