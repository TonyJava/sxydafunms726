package com.afunms.system.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.afunms.system.realm.MysqlJdbcRealmTest;

@RunWith(Suite.class)
@SuiteClasses({ MysqlJdbcRealmTest.class,CreateMenuTableUtilTest.class })
public class AllTests {

}
