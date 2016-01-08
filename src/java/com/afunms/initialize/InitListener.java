package com.afunms.initialize;

import com.afunms.application.model.SystemFlag;
import com.afunms.application.util.ControlServer;
import com.afunms.application.util.MachineTask;
import com.afunms.capreport.subscribe.SubscribeTimer;
import com.afunms.common.util.SendEmailWeek;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.comprehensivereport.util.ComprehensiveReportTimer;
import com.afunms.linkReport.util.LinkReportTimer;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.om.Task;
import com.afunms.polling.task.AlarmUpdateTask;
import com.afunms.polling.task.MonitorTask;
import com.afunms.polling.task.MonitorTimer;
import com.afunms.polling.task.TaskFactory;
import com.afunms.polling.task.TaskXml;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.model.SysLog;
import com.afunms.topology.dao.HostNodeDao;
import com.gathertask.TaskManager;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.apache.shiro.mgt.SecurityManager;

public class InitListener
  implements ServletContextListener
{
  private MonitorTimer nettimer = null;
  private MonitorTimer hosttimer = null;
  private MonitorTimer gsntimer = null;

  private MonitorTimer urltimer = null;
  private MonitorTimer mqtimer = null;
  private MonitorTimer dominotimer = null;
  private MonitorTask monitorTask = null;
  private MonitorTimer pingtimer = null;
  private MonitorTimer grapestimer = null;
  private MonitorTimer radartimer = null;
  private MonitorTimer plottimer = null;
  private MonitorTimer syslogtimer = null;
  private MonitorTimer soundtimer = null;
  private MonitorTimer tomcattimer = null;
  private MonitorTimer weblogictimer = null;
  private MonitorTimer ftptimer = null;
  private MonitorTimer emailtimer = null;
  private MonitorTimer iistimer = null;
  private MonitorTimer sockettimer = null;
  private MonitorTimer firewalltimer = null;
  private MonitorTimer cicstimer = null;
  private MonitorTimer iislogtimer = null;
  private MonitorTimer checklinktimer = null;
  private MonitorTimer updatexmltimer = null;
  private MonitorTimer envtimer = null;
  private MonitorTimer telnettimer = null;
  private MonitorTimer wastimer = null;
  private MonitorTimer bnodetimer = null;
  private MonitorTimer jbosstimer = null;
  private MonitorTimer dnstimer = null;
  private MonitorTimer telnetpolltimer = null;
  private MonitorTimer sshpolltimer = null;
  private MonitorTimer apachetimer = null;
  private MonitorTimer storagetimer = null;

  private MonitorTimer hostcollecthourtimer = null;

  private MonitorTimer hostcollectdaytimer = null;

  private MonitorTimer m_5_gateway_timer = null;

  private MonitorTimer m_5_f5_timer = null;

  private MonitorTimer m_5_firewall_timer = null;

  private MonitorTimer m_1_timer = null;
  private MonitorTimer m_2_timer = null;
  private MonitorTimer m_3_timer = null;
  private MonitorTimer m_4_timer = null;
  private MonitorTimer m_5_timer = null;
  private MonitorTimer m_10_timer = null;
  private MonitorTimer m_30_timer = null;

  private MonitorTimer m_1_host_timer = null;
  private MonitorTimer m_2_host_timer = null;
  private MonitorTimer m_3_host_timer = null;
  private MonitorTimer m_4_host_timer = null;
  private MonitorTimer m_5_host_timer = null;
  private MonitorTimer m_10_host_timer = null;
  private MonitorTimer m_30_host_timer = null;
  private MonitorTimer d_1_host_timer = null;

  private MonitorTimer m_5_oracle_timer = null;
  private MonitorTimer m_10_oracle_timer = null;
  private MonitorTimer m_30_oracle_timer = null;

  private MonitorTimer m_5_sqlserver_timer = null;
  private MonitorTimer m_10_sqlserver_timer = null;
  private MonitorTimer m_30_sqlserver_timer = null;

  private MonitorTimer db2_timer = null;
  private MonitorTimer m_5_db2_timer = null;
  private MonitorTimer m_10_db2_timer = null;
  private MonitorTimer m_30_db2_timer = null;

  private MonitorTimer m_5_sybase_timer = null;
  private MonitorTimer m_10_sybase_timer = null;
  private MonitorTimer m_30_sybase_timer = null;

  private MonitorTimer m_5_informix_timer = null;
  private MonitorTimer m_10_informix_timer = null;
  private MonitorTimer m_30_informix_timer = null;

  private MonitorTimer m_1_mysql_timer = null;
  private MonitorTimer m_5_mysql_timer = null;
  private MonitorTimer m_10_mysql_timer = null;
  private MonitorTimer m_30_mysql_timer = null;

  private MonitorTimer m_5_url_timer = null;
  private MonitorTimer m_10_url_timer = null;
  private MonitorTimer m_30_url_timer = null;

  private MonitorTimer m_5_socket_timer = null;
  private MonitorTimer m_10_socket_timer = null;
  private MonitorTimer m_30_socket_timer = null;

  private MonitorTimer m_5_mail_timer = null;
  private MonitorTimer m_10_mail_timer = null;
  private MonitorTimer m_30_mail_timer = null;

  private MonitorTimer m_5_ftp_timer = null;
  private MonitorTimer m_10_ftp_timer = null;
  private MonitorTimer m_30_ftp_timer = null;

  private MonitorTimer m_5_weblogic_timer = null;
  private MonitorTimer m_10_weblogic_timer = null;
  private MonitorTimer m_30_weblogic_timer = null;

  private MonitorTimer m_5_was_timer = null;
  private MonitorTimer m_10_was_timer = null;
  private MonitorTimer m_30_was_timer = null;

  private MonitorTimer m_5_tomcat_timer = null;
  private MonitorTimer m_10_tomcat_timer = null;
  private MonitorTimer m_30_tomcat_timer = null;

  private MonitorTimer m_1_telnet_timer = null;
  private MonitorTimer m_2_telnet_timer = null;
  private MonitorTimer m_3_telnet_timer = null;
  private MonitorTimer m_4_telnet_timer = null;
  private MonitorTimer m_5_telnet_timer = null;
  private MonitorTimer m_10_telnet_timer = null;
  private MonitorTimer m_30_telnet_timer = null;
  private MonitorTimer d_1_telnet_timer = null;

  private MonitorTimer m_30_backupTelnetConfigTimer = null;

  private MonitorTimer m_30_passwdBackupTelnetConfigTimer = null;

  private MonitorTimer m_5_ups_timer = null;

  private MonitorTimer h_1_timer = null;
  private MonitorTimer h_4_timer = null;
  private MonitorTimer h_8_timer = null;
  private MonitorTimer h_12_timer = null;
  private MonitorTimer d_1_timer = null;
  private MonitorTimer d_7_timer = null;
  private MonitorTimer d_30_timer = null;

  private MonitorTimer VPN_Timer = null;

  private MonitorTimer M5VPNTask_timer = null;
  private MonitorTimer m_5_telnetCfg_timer = null;
  private MonitorTimer h_1_acl_Timer = null;
  private MonitorTimer carbinetTimer = null;
  private MonitorTimer sendEmailWeek = null;

  SnmpTrapsListener trapListener = SnmpTrapsListener.getInstance();
  Hashtable task_ht = new Hashtable();

  public InitListener() {
    this.nettimer = null;
    this.hosttimer = null;
    this.gsntimer = null;

    this.urltimer = null;
    this.mqtimer = null;
    this.dominotimer = null;
    this.pingtimer = null;
    this.grapestimer = null;
    this.radartimer = null;
    this.plottimer = null;
    this.soundtimer = null;
    this.tomcattimer = null;
    this.weblogictimer = null;
    this.hostcollecthourtimer = null;
    this.hostcollectdaytimer = null;
    this.ftptimer = null;
    this.emailtimer = null;
    this.iistimer = null;
    this.sockettimer = null;
    this.firewalltimer = null;
    this.cicstimer = null;
    this.iislogtimer = null;
    this.checklinktimer = null;
    this.updatexmltimer = null;
    this.envtimer = null;
    this.telnettimer = null;
    this.wastimer = null;
    this.bnodetimer = null;
    this.jbosstimer = null;
    this.dnstimer = null;
    this.telnetpolltimer = null;
    this.sshpolltimer = null;
    this.apachetimer = null;

    this.storagetimer = null;

    this.m_1_timer = null;
    this.m_2_timer = null;
    this.m_3_timer = null;
    this.m_4_timer = null;
    this.m_5_timer = null;
    this.m_10_timer = null;
    this.m_30_timer = null;

    this.m_5_gateway_timer = null;
    this.m_5_firewall_timer = null;
    this.m_5_f5_timer = null;

    this.m_1_host_timer = null;
    this.m_2_host_timer = null;
    this.m_3_host_timer = null;
    this.m_4_host_timer = null;
    this.m_5_host_timer = null;
    this.m_10_host_timer = null;
    this.m_30_host_timer = null;
    this.d_1_host_timer = null;

    this.m_5_oracle_timer = null;
    this.m_10_oracle_timer = null;
    this.m_30_oracle_timer = null;

    this.m_5_sqlserver_timer = null;
    this.m_10_sqlserver_timer = null;
    this.m_30_sqlserver_timer = null;

    this.db2_timer = null;
    this.m_5_db2_timer = null;
    this.m_10_db2_timer = null;
    this.m_30_db2_timer = null;

    this.m_5_sybase_timer = null;
    this.m_10_sybase_timer = null;
    this.m_30_sybase_timer = null;

    this.m_5_informix_timer = null;
    this.m_10_informix_timer = null;
    this.m_30_informix_timer = null;

    this.m_1_mysql_timer = null;
    this.m_5_mysql_timer = null;
    this.m_10_mysql_timer = null;
    this.m_30_mysql_timer = null;

    this.m_5_url_timer = null;
    this.m_10_url_timer = null;
    this.m_30_url_timer = null;

    this.m_5_socket_timer = null;
    this.m_10_socket_timer = null;
    this.m_30_socket_timer = null;

    this.m_5_mail_timer = null;
    this.m_10_mail_timer = null;
    this.m_30_mail_timer = null;

    this.m_5_ftp_timer = null;
    this.m_10_ftp_timer = null;
    this.m_30_ftp_timer = null;

    this.m_5_weblogic_timer = null;
    this.m_10_weblogic_timer = null;
    this.m_30_weblogic_timer = null;

    this.m_5_was_timer = null;
    this.m_10_was_timer = null;
    this.m_30_was_timer = null;

    this.m_5_tomcat_timer = null;
    this.m_10_tomcat_timer = null;
    this.m_30_tomcat_timer = null;

    this.m_1_telnet_timer = null;
    this.m_2_telnet_timer = null;
    this.m_3_telnet_timer = null;
    this.m_4_telnet_timer = null;
    this.m_5_telnet_timer = null;
    this.m_10_telnet_timer = null;
    this.m_30_telnet_timer = null;
    this.d_1_telnet_timer = null;

    this.h_1_timer = null;
    this.h_4_timer = null;
    this.h_8_timer = null;
    this.h_12_timer = null;
    this.d_1_timer = null;
    this.d_7_timer = null;
    this.d_30_timer = null;
    this.m_30_backupTelnetConfigTimer = null;
    this.m_30_passwdBackupTelnetConfigTimer = null;
    this.VPN_Timer = null;
    this.M5VPNTask_timer = null;
    this.m_5_telnetCfg_timer = null;
    this.m_5_ups_timer = null;
    this.h_1_acl_Timer = null;
    this.carbinetTimer = null;
    this.sendEmailWeek = null;
  }

  public void contextDestroyed(ServletContextEvent event) {
    this.trapListener.close();

    if (this.nettimer != null)
      this.nettimer.canclethis(true);
    if (this.hosttimer != null)
      this.hosttimer.canclethis(true);
    if (this.gsntimer != null) {
      this.gsntimer.canclethis(true);
    }

    if (this.urltimer != null)
      this.urltimer.canclethis(true);
    if (this.mqtimer != null)
      this.mqtimer.canclethis(true);
    if (this.dominotimer != null)
      this.dominotimer.canclethis(true);
    if (this.monitorTask != null)
      this.monitorTask.destroy();
    if (this.pingtimer != null)
      this.pingtimer.canclethis(true);
    if (this.grapestimer != null)
      this.grapestimer.canclethis(true);
    if (this.radartimer != null)
      this.radartimer.canclethis(true);
    if (this.plottimer != null)
      this.plottimer.canclethis(true);
    if (this.syslogtimer != null)
      this.syslogtimer.canclethis(true);
    if (this.soundtimer != null)
      this.soundtimer.canclethis(true);
    if (this.tomcattimer != null)
      this.tomcattimer.canclethis(true);
    if (this.weblogictimer != null)
      this.weblogictimer.canclethis(true);
    if (this.ftptimer != null) {
      this.ftptimer.canclethis(true);
    }
    if (this.emailtimer != null) {
      this.emailtimer.canclethis(true);
    }
    if (this.iistimer != null) {
      this.iistimer.canclethis(true);
    }
    if (this.sockettimer != null) {
      this.sockettimer.canclethis(true);
    }
    if (this.firewalltimer != null) {
      this.firewalltimer.canclethis(true);
    }
    if (this.cicstimer != null) {
      this.cicstimer.canclethis(true);
    }
    if (this.iislogtimer != null) {
      this.iislogtimer.canclethis(true);
    }
    if (this.checklinktimer != null) {
      this.checklinktimer.canclethis(true);
    }
    if (this.updatexmltimer != null) {
      this.updatexmltimer.canclethis(true);
    }
    if (this.envtimer != null) {
      this.envtimer.canclethis(true);
    }
    if (this.telnettimer != null) {
      this.telnettimer.canclethis(true);
    }
    if (this.wastimer != null) {
      this.wastimer.canclethis(true);
    }
    if (this.bnodetimer != null) {
      this.bnodetimer.canclethis(true);
    }
    if (this.jbosstimer != null) {
      this.jbosstimer.canclethis(true);
    }
    if (this.dnstimer != null) {
      this.dnstimer.canclethis(true);
    }
    if (this.telnetpolltimer != null)
    {
      this.telnetpolltimer.canclethis(true);
    }

    if (this.sshpolltimer != null)
    {
      this.sshpolltimer.canclethis(true);
    }
    if (this.apachetimer != null) {
      this.apachetimer.canclethis(true);
    }
    if (this.hostcollecthourtimer != null)
      this.hostcollecthourtimer.canclethis(true);
    if (this.hostcollectdaytimer != null) {
      this.hostcollectdaytimer.canclethis(true);
    }
    if (this.storagetimer != null) {
      this.storagetimer.canclethis(true);
    }

    if (this.m_1_timer != null) {
      this.m_1_timer.canclethis(true);
    }
    if (this.m_2_timer != null) {
      this.m_2_timer.canclethis(true);
    }
    if (this.m_3_timer != null) {
      this.m_3_timer.canclethis(true);
    }
    if (this.m_4_timer != null) {
      this.m_4_timer.canclethis(true);
    }
    if (this.m_5_timer != null) {
      this.m_5_timer.canclethis(true);
    }
    if (this.m_10_timer != null) {
      this.m_10_timer.canclethis(true);
    }
    if (this.m_30_timer != null) {
      this.m_30_timer.canclethis(true);
    }

    if (this.m_5_gateway_timer != null) {
      this.m_5_gateway_timer.canclethis(true);
    }

    if (this.m_5_f5_timer != null) {
      this.m_5_f5_timer.canclethis(true);
    }

    if (this.m_5_firewall_timer != null) {
      this.m_5_firewall_timer.canclethis(true);
    }

    if (this.m_1_host_timer != null) {
      this.m_1_host_timer.canclethis(true);
    }
    if (this.m_2_host_timer != null) {
      this.m_2_host_timer.canclethis(true);
    }
    if (this.m_3_host_timer != null) {
      this.m_3_host_timer.canclethis(true);
    }
    if (this.m_4_host_timer != null) {
      this.m_4_host_timer.canclethis(true);
    }
    if (this.m_5_host_timer != null) {
      this.m_5_host_timer.canclethis(true);
    }
    if (this.m_10_host_timer != null) {
      this.m_10_host_timer.canclethis(true);
    }
    if (this.m_30_host_timer != null) {
      this.m_30_host_timer.canclethis(true);
    }
    if (this.d_1_host_timer != null) {
      this.d_1_host_timer.canclethis(true);
    }

    if (this.m_5_oracle_timer != null) {
      this.m_5_oracle_timer.canclethis(true);
    }
    if (this.m_10_oracle_timer != null) {
      this.m_10_oracle_timer.canclethis(true);
    }
    if (this.m_30_oracle_timer != null) {
      this.m_30_oracle_timer.canclethis(true);
    }

    if (this.m_5_sqlserver_timer != null) {
      this.m_5_sqlserver_timer.canclethis(true);
    }
    if (this.m_10_sqlserver_timer != null) {
      this.m_10_sqlserver_timer.canclethis(true);
    }
    if (this.m_30_sqlserver_timer != null) {
      this.m_30_sqlserver_timer.canclethis(true);
    }

    if (this.m_5_sybase_timer != null) {
      this.m_5_sybase_timer.canclethis(true);
    }
    if (this.m_10_sybase_timer != null) {
      this.m_10_sybase_timer.canclethis(true);
    }
    if (this.m_30_sybase_timer != null) {
      this.m_30_sybase_timer.canclethis(true);
    }

    if (this.m_5_informix_timer != null) {
      this.m_5_informix_timer.canclethis(true);
    }
    if (this.m_10_informix_timer != null) {
      this.m_10_informix_timer.canclethis(true);
    }
    if (this.m_30_informix_timer != null) {
      this.m_30_informix_timer.canclethis(true);
    }

    if (this.db2_timer != null) {
      this.db2_timer.canclethis(true);
    }
    if (this.m_5_db2_timer != null) {
      this.m_5_db2_timer.canclethis(true);
    }
    if (this.m_10_db2_timer != null) {
      this.m_10_db2_timer.canclethis(true);
    }
    if (this.m_30_db2_timer != null) {
      this.m_30_db2_timer.canclethis(true);
    }

    if (this.m_1_mysql_timer != null) {
      this.m_1_mysql_timer.canclethis(true);
    }
    if (this.m_5_mysql_timer != null) {
      this.m_5_mysql_timer.canclethis(true);
    }
    if (this.m_10_mysql_timer != null) {
      this.m_10_mysql_timer.canclethis(true);
    }
    if (this.m_30_mysql_timer != null) {
      this.m_30_mysql_timer.canclethis(true);
    }

    if (this.m_5_url_timer != null) {
      this.m_5_url_timer.canclethis(true);
    }
    if (this.m_10_url_timer != null) {
      this.m_10_url_timer.canclethis(true);
    }
    if (this.m_30_url_timer != null) {
      this.m_30_url_timer.canclethis(true);
    }

    if (this.m_5_socket_timer != null) {
      this.m_5_socket_timer.canclethis(true);
    }
    if (this.m_10_socket_timer != null) {
      this.m_10_socket_timer.canclethis(true);
    }
    if (this.m_30_socket_timer != null) {
      this.m_30_socket_timer.canclethis(true);
    }

    if (this.m_5_mail_timer != null) {
      this.m_5_mail_timer.canclethis(true);
    }
    if (this.m_10_mail_timer != null) {
      this.m_10_mail_timer.canclethis(true);
    }
    if (this.m_30_mail_timer != null) {
      this.m_30_mail_timer.canclethis(true);
    }

    if (this.m_5_ftp_timer != null) {
      this.m_5_ftp_timer.canclethis(true);
    }
    if (this.m_10_ftp_timer != null) {
      this.m_10_ftp_timer.canclethis(true);
    }
    if (this.m_30_ftp_timer != null) {
      this.m_30_ftp_timer.canclethis(true);
    }

    if (this.m_5_weblogic_timer != null) {
      this.m_5_weblogic_timer.canclethis(true);
    }
    if (this.m_10_weblogic_timer != null) {
      this.m_10_weblogic_timer.canclethis(true);
    }
    if (this.m_30_weblogic_timer != null) {
      this.m_30_weblogic_timer.canclethis(true);
    }

    if (this.m_5_was_timer != null) {
      this.m_5_was_timer.canclethis(true);
    }
    if (this.m_10_was_timer != null) {
      this.m_10_was_timer.canclethis(true);
    }
    if (this.m_30_was_timer != null) {
      this.m_30_was_timer.canclethis(true);
    }

    if (this.m_5_tomcat_timer != null) {
      this.m_5_tomcat_timer.canclethis(true);
    }
    if (this.m_10_tomcat_timer != null) {
      this.m_10_tomcat_timer.canclethis(true);
    }
    if (this.m_30_tomcat_timer != null) {
      this.m_30_tomcat_timer.canclethis(true);
    }

    if (this.m_1_telnet_timer != null) {
      this.m_1_telnet_timer.canclethis(true);
    }
    if (this.m_2_telnet_timer != null) {
      this.m_2_telnet_timer.canclethis(true);
    }
    if (this.m_3_telnet_timer != null) {
      this.m_3_telnet_timer.canclethis(true);
    }
    if (this.m_4_telnet_timer != null) {
      this.m_4_telnet_timer.canclethis(true);
    }
    if (this.m_5_telnet_timer != null) {
      this.m_5_telnet_timer.canclethis(true);
    }
    if (this.m_10_telnet_timer != null) {
      this.m_10_telnet_timer.canclethis(true);
    }
    if (this.m_30_telnet_timer != null) {
      this.m_30_telnet_timer.canclethis(true);
    }
    if (this.d_1_telnet_timer != null) {
      this.d_1_telnet_timer.canclethis(true);
    }

    if (this.h_1_timer != null) {
      this.h_1_timer.canclethis(true);
    }
    if (this.h_4_timer != null) {
      this.h_4_timer.canclethis(true);
    }
    if (this.h_8_timer != null) {
      this.h_8_timer.canclethis(true);
    }
    if (this.h_12_timer != null) {
      this.h_12_timer.canclethis(true);
    }
    if (this.d_1_timer != null) {
      this.d_1_timer.canclethis(true);
    }
    if (this.d_7_timer != null) {
      this.d_7_timer.canclethis(true);
    }
    if (this.d_30_timer != null) {
      this.d_30_timer.canclethis(true);
    }
    if (this.m_30_backupTelnetConfigTimer != null) {
      this.m_30_backupTelnetConfigTimer.canclethis(true);
    }
    if (this.m_30_passwdBackupTelnetConfigTimer != null) {
      this.m_30_passwdBackupTelnetConfigTimer.canclethis(true);
    }
    if (this.VPN_Timer != null) {
      this.VPN_Timer.canclethis(true);
    }
    if (this.M5VPNTask_timer != null) {
      this.M5VPNTask_timer.canclethis(true);
    }
    if (this.m_5_ups_timer != null) {
      this.m_5_ups_timer.canclethis(true);
    }
    if (this.m_5_telnetCfg_timer != null) {
      this.m_5_telnetCfg_timer.canclethis(true);
    }
    if (this.h_1_acl_Timer != null) {
      this.h_1_acl_Timer.canclethis(true);
    }

    if (this.carbinetTimer != null) {
      this.carbinetTimer.canclethis(true);
    }
    
    if (this.sendEmailWeek != null) {
    	this.sendEmailWeek.canclethis(true);
    }

    if (ResourceCenter.getInstance().isStartPolling())
    {
      HostNodeDao dao = new HostNodeDao();
      try {
        dao.updateInterfaceData(PollingEngine.getInstance().getNodeList());
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        dao.close();
      }
    }
    saveLog("系统关闭");
  }
  /*初始化Shiro环境
   * 
   */
  private void shiroInitialized(){
	  //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
      Factory<SecurityManager> factory =
              new IniSecurityManagerFactory("classpath:shiro-realm.ini");

      //2、得到SecurityManager实例 并绑定给SecurityUtils
      SecurityManager securityManager = factory.getInstance();
      SecurityUtils.setSecurityManager(securityManager);
      logger.info("初始化Shiro环境");
  }
  private final Log logger = LogFactory.getLog(InitListener.class);
  public void contextInitialized(ServletContextEvent event)
  {
    int numThreads = 200;
    try
    {

      System.out.println("#####################启动 远程开关机 服务器程序####################");
      ControlServer cs = new ControlServer(ShareData.getIp_clientInfoHash());
      MachineTask mt = new MachineTask(cs);
      Thread t = new Thread(mt);
      t.start();

      List numList = new ArrayList();
      TaskXml taskxml = new TaskXml();
      numList = taskxml.ListXml();
      for (int i = 0; i < numList.size(); i++) {
        Task task = new Task();
        BeanUtils.copyProperties(task, numList.get(i));
        if (task.getTaskname().equals("netthreadnum"))
          numThreads = task.getPolltime().intValue();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    SystemFlag.getInstance().setFirstStart(false);
    SysInitialize sysInit = new SysInitialize();
    sysInit.setSysPath(event.getServletContext().getRealPath("/"));
    sysInit.init();
    saveLog("系统启动");
    SysLogger.info("listener 正在启动，请稍候...........");
    this.nettimer = new MonitorTimer(true);
    this.hosttimer = new MonitorTimer(true);
    this.gsntimer = new MonitorTimer(true);

    this.urltimer = new MonitorTimer(true);
    this.mqtimer = new MonitorTimer(true);
    this.dominotimer = new MonitorTimer(true);
    this.pingtimer = new MonitorTimer(true);
    this.grapestimer = new MonitorTimer(true);
    this.radartimer = new MonitorTimer(true);
    this.plottimer = new MonitorTimer(true);
    this.soundtimer = new MonitorTimer(true);
    this.tomcattimer = new MonitorTimer(true);
    this.weblogictimer = new MonitorTimer(true);
    this.hostcollecthourtimer = new MonitorTimer(true);
    this.hostcollectdaytimer = new MonitorTimer(true);
    this.ftptimer = new MonitorTimer(true);
    this.emailtimer = new MonitorTimer(true);
    this.iistimer = new MonitorTimer(true);
    this.sockettimer = new MonitorTimer(true);
    this.firewalltimer = new MonitorTimer(true);
    this.cicstimer = new MonitorTimer(true);
    this.iislogtimer = new MonitorTimer(true);
    this.checklinktimer = new MonitorTimer(true);
    this.updatexmltimer = new MonitorTimer(true);
    this.envtimer = new MonitorTimer(true);
    this.telnettimer = new MonitorTimer(true);
    this.wastimer = new MonitorTimer(true);
    this.bnodetimer = new MonitorTimer(true);
    this.jbosstimer = new MonitorTimer(true);
    this.dnstimer = new MonitorTimer(true);
    this.telnetpolltimer = new MonitorTimer(true);
    this.sshpolltimer = new MonitorTimer(true);
    this.apachetimer = new MonitorTimer(true);
    this.storagetimer = new MonitorTimer(true);

    this.m_1_timer = new MonitorTimer(true);
    this.m_2_timer = new MonitorTimer(true);
    this.m_3_timer = new MonitorTimer(true);
    this.m_4_timer = new MonitorTimer(true);
    this.m_5_timer = new MonitorTimer(true);
    this.m_10_timer = new MonitorTimer(true);
    this.m_30_timer = new MonitorTimer(true);

    this.m_5_gateway_timer = new MonitorTimer(true);

    this.m_5_f5_timer = new MonitorTimer(true);

    this.m_5_firewall_timer = new MonitorTimer(true);

    this.m_1_host_timer = new MonitorTimer(true);
    this.m_2_host_timer = new MonitorTimer(true);
    this.m_3_host_timer = new MonitorTimer(true);
    this.m_4_host_timer = new MonitorTimer(true);
    this.m_5_host_timer = new MonitorTimer(true);
    this.m_10_host_timer = new MonitorTimer(true);
    this.m_30_host_timer = new MonitorTimer(true);
    this.d_1_host_timer = new MonitorTimer(true);

    this.m_5_oracle_timer = new MonitorTimer(true);
    this.m_10_oracle_timer = new MonitorTimer(true);
    this.m_30_oracle_timer = new MonitorTimer(true);

    this.m_5_sqlserver_timer = new MonitorTimer(true);
    this.m_10_sqlserver_timer = new MonitorTimer(true);
    this.m_30_sqlserver_timer = new MonitorTimer(true);

    this.m_5_sybase_timer = new MonitorTimer(true);
    this.m_10_sybase_timer = new MonitorTimer(true);
    this.m_30_sybase_timer = new MonitorTimer(true);

    this.m_5_informix_timer = new MonitorTimer(true);
    this.m_10_informix_timer = new MonitorTimer(true);
    this.m_30_informix_timer = new MonitorTimer(true);

    this.db2_timer = new MonitorTimer(true);
    this.m_5_db2_timer = new MonitorTimer(true);
    this.m_10_db2_timer = new MonitorTimer(true);
    this.m_30_db2_timer = new MonitorTimer(true);

    this.m_1_mysql_timer = new MonitorTimer(true);
    this.m_5_mysql_timer = new MonitorTimer(true);
    this.m_10_mysql_timer = new MonitorTimer(true);
    this.m_30_mysql_timer = new MonitorTimer(true);

    this.m_5_url_timer = new MonitorTimer(true);
    this.m_10_url_timer = new MonitorTimer(true);
    this.m_30_url_timer = new MonitorTimer(true);

    this.m_5_socket_timer = new MonitorTimer(true);
    this.m_10_socket_timer = new MonitorTimer(true);
    this.m_30_socket_timer = new MonitorTimer(true);

    this.m_5_mail_timer = new MonitorTimer(true);
    this.m_10_mail_timer = new MonitorTimer(true);
    this.m_30_mail_timer = new MonitorTimer(true);

    this.m_5_ftp_timer = new MonitorTimer(true);
    this.m_10_ftp_timer = new MonitorTimer(true);
    this.m_30_ftp_timer = new MonitorTimer(true);

    this.m_5_weblogic_timer = new MonitorTimer(true);
    this.m_10_weblogic_timer = new MonitorTimer(true);
    this.m_30_weblogic_timer = new MonitorTimer(true);

    this.m_5_was_timer = new MonitorTimer(true);
    this.m_10_was_timer = new MonitorTimer(true);
    this.m_30_was_timer = new MonitorTimer(true);

    this.m_5_tomcat_timer = new MonitorTimer(true);
    this.m_10_tomcat_timer = new MonitorTimer(true);
    this.m_30_tomcat_timer = new MonitorTimer(true);

    this.m_1_telnet_timer = new MonitorTimer(true);
    this.m_2_telnet_timer = new MonitorTimer(true);
    this.m_3_telnet_timer = new MonitorTimer(true);
    this.m_4_telnet_timer = new MonitorTimer(true);
    this.m_5_telnet_timer = new MonitorTimer(true);
    this.m_10_telnet_timer = new MonitorTimer(true);
    this.m_30_telnet_timer = new MonitorTimer(true);
    this.d_1_telnet_timer = new MonitorTimer(true);

    this.h_1_timer = new MonitorTimer(true);
    this.h_4_timer = new MonitorTimer(true);
    this.h_8_timer = new MonitorTimer(true);
    this.h_12_timer = new MonitorTimer(true);
    this.d_1_timer = new MonitorTimer(true);
    this.d_7_timer = new MonitorTimer(true);
    this.d_30_timer = new MonitorTimer(true);
    this.carbinetTimer = new MonitorTimer(true);
    this.m_30_backupTelnetConfigTimer = new MonitorTimer(true);
    this.VPN_Timer = new MonitorTimer(true);
    this.M5VPNTask_timer = new MonitorTimer(true);
    this.m_5_telnetCfg_timer = new MonitorTimer(true);
    this.m_5_ups_timer = new MonitorTimer(true);

    this.h_1_acl_Timer = new MonitorTimer(true);
    this.m_30_passwdBackupTelnetConfigTimer = new MonitorTimer(true);
    SysLogger.info("=========开始启动报警信息定时器==========");

    this.soundtimer = new MonitorTimer(true);
    this.soundtimer.schedule(new AlarmUpdateTask(), 0L, 60000L);
    this.syslogtimer = new MonitorTimer(true);
    this.syslogtimer.schedule(new ExecuteCollectSyslog(), 0L, 86400000L);

    SysLogger.info("[AlertAlarm][AlarmUpdateListener] 取最新报警信息定时器已启动");
    
  //test sendEmailWeek
	sendEmailWeek = new MonitorTimer(true);
	sendEmailWeek.schedule(new SendEmailWeek(), 0, 60*1000*60);

    System.setProperty("appDir", event.getServletContext().getRealPath("/"));
    try
    {
      long firstTime = 0L;

      this.task_ht = taskNum();
      int num = this.task_ht.size();
      TaskFactory taskF = new TaskFactory();

      for (int i = 0; i < num; i++)
      {
        String taskinfo = this.task_ht.get(String.valueOf(i)).toString();
        String[] tmp = taskinfo.split(":");
        String taskname = tmp[0];
        float interval = Float.parseFloat(tmp[1]);
        String unit = tmp[2];
        SysLogger.info(
          "interval is -- " + 
          interval + 
          "  unit is  -- " + 
          unit + 
          "taskname is -- ===" + 
          taskname + "==================");
        try
        {
          this.monitorTask = taskF.getInstance(taskname);
          if (this.monitorTask != null)
          {
            this.monitorTask.setInterval(interval, unit);
            if (taskname.equals("netcollecttask"))
              this.nettimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("hostcollecttask")) {
              this.hosttimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (taskname.equals("pingtask")) {
              this.pingtimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (taskname.equals("mqtask"))
              this.mqtimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("dominoTask")) {
              this.dominotimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (taskname.equals("hostcollectdatahourtask"))
              this.hostcollecthourtimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("hostcollectdatadaytask")) {
              this.hostcollectdaytimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (taskname.equals("iistask")) {
              this.iistimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (taskname.equals("firewalltask"))
              this.firewalltimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("cicstask"))
              this.cicstimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("iislogtask"))
              this.iislogtimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            else if (taskname.equals("checklinktask")) {
              this.checklinktimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
            }
            else if (!taskname.equals("updatepaneltask"))
            {
              if (taskname.equals("updatexmltask"))
              {
                this.updatexmltimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("bnodetask"))
                this.bnodetimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("jbosstask"))
                this.jbosstimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("dnstask"))
                this.dnstimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("sshpolltask")) {
                this.sshpolltimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("apachetask"))
                this.apachetimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("storagetask"))
              {
                this.storagetimer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              } else if (taskname.equals("m1task"))
                this.m_1_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m2task"))
                this.m_2_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m3task"))
                this.m_3_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m4task"))
                this.m_4_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5task")) {
                this.m_5_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m10task")) {
                this.m_10_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m30task"))
                this.m_30_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("d1task")) {
                this.d_1_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5gatewaytask")) {
                this.m_5_gateway_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5f5task")) {
                this.m_5_f5_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5firewalltask")) {
                this.m_5_firewall_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m1hosttask"))
                this.m_1_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m2hosttask"))
                this.m_2_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m3hosttask"))
                this.m_3_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m4hosttask"))
                this.m_4_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5hosttask")) {
                this.m_5_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m10hosttask"))
                this.m_10_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30hosttask"))
                this.m_30_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("d1hosttask")) {
                this.d_1_host_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5oracletask"))
                this.m_5_oracle_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10oracletask"))
                this.m_10_oracle_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30oracletask")) {
                this.m_30_oracle_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5sqlservertask"))
                this.m_5_sqlserver_timer.schedule(this.monitorTask, 40000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10sqlservertask"))
                this.m_10_sqlserver_timer.schedule(this.monitorTask, 40000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30sqlservertask")) {
                this.m_30_sqlserver_timer.schedule(this.monitorTask, 40000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5sybasetask"))
                this.m_5_sybase_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10sybasetask"))
                this.m_10_sybase_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30sybasetask")) {
                this.m_30_sybase_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5informixtask"))
                this.m_5_informix_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10informixtask"))
                this.m_10_informix_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30informixtask")) {
                this.m_30_informix_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("db2task"))
                this.db2_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5db2task"))
                this.m_5_db2_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10db2task"))
                this.m_10_db2_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30db2task"))
                this.m_30_db2_timer.schedule(this.monitorTask, 30000L, this.monitorTask.getInterval());
              else if (taskname.equals("m1mysqltask"))
                this.m_1_mysql_timer.schedule(this.monitorTask, 35000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5mysqltask"))
                this.m_5_mysql_timer.schedule(this.monitorTask, 35000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10mysqltask"))
                this.m_10_mysql_timer.schedule(this.monitorTask, 35000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30mysqltask")) {
                this.m_30_mysql_timer.schedule(this.monitorTask, 35000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5sockettask"))
                this.m_5_socket_timer.schedule(this.monitorTask, 0L, this.monitorTask.getInterval());
              else if (taskname.equals("m10sockettask"))
                this.m_10_socket_timer.schedule(this.monitorTask, 0L, this.monitorTask.getInterval());
              else if (taskname.equals("m30sockettask")) {
                this.m_30_socket_timer.schedule(this.monitorTask, 0L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5urltask"))
                this.m_5_url_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10urltask"))
                this.m_10_url_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30urltask")) {
                this.m_30_url_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5mailtask"))
                this.m_5_mail_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10mailtask"))
                this.m_10_mail_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30mailtask")) {
                this.m_30_mail_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5ftptask"))
                this.m_5_ftp_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10ftptask"))
                this.m_10_ftp_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30ftptask")) {
                this.m_30_ftp_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5weblogictask"))
                this.m_5_weblogic_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10weblogictask"))
                this.m_10_weblogic_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30weblogictask")) {
                this.m_30_weblogic_timer.schedule(this.monitorTask, 10000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5wastask"))
                this.m_5_was_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10wastask"))
                this.m_10_was_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30wastask")) {
                this.m_30_was_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m5tomcattask"))
                this.m_5_tomcat_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10tomcattask"))
                this.m_10_tomcat_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30tomcattask")) {
                this.m_30_tomcat_timer.schedule(this.monitorTask, 20000L, this.monitorTask.getInterval());
              }
              else if (taskname.equals("m1telnettask"))
                this.m_1_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m2telnettask"))
                this.m_2_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m3telnettask"))
                this.m_3_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m4telnettask"))
                this.m_4_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5telnettask"))
                this.m_5_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m10telnettask"))
                this.m_10_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m30telnettask"))
                this.m_30_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("d1telnettask"))
                this.d_1_telnet_timer.schedule(this.monitorTask, 25000L, this.monitorTask.getInterval());
              else if (taskname.equals("m_30_backupTelnetConfigTask"))
                this.m_30_backupTelnetConfigTimer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m_30_passwdChangeHintTask"))
                this.m_30_passwdBackupTelnetConfigTimer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("vpnTask"))
                this.VPN_Timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("M5VPNTask"))
                this.M5VPNTask_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5telnetConfgTask"))
                this.m_5_telnetCfg_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("m5upstask"))
                this.m_5_ups_timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("h1Acltask"))
                this.h_1_acl_Timer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              else if (taskname.equals("cabinettask"))
              {
                this.carbinetTimer.schedule(this.monitorTask, 1000L, this.monitorTask.getInterval());
              }
            }
            SysLogger.info(taskname + " start success...........");
          }
          else {
            throw new Exception(
              taskname + "  Task not find ,please check it!");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      }

      this.trapListener.listen();
    }
    catch (Exception e) {
      SysLogger.info("error in ExecutePing!" + e.getMessage());
    }

    TaskManager manager = new TaskManager();
    manager.CreateGCTask();
    manager.createAllTask();
    manager.CreateMaintainTask();
    manager.CreateGahterSQLTask();
    manager.CreateDataTempTask();
    manager.CreateGahterAlarmSQLTask();

    SubscribeTimer.startupSubscribe();
    SysLogger.info("listener start success...........");
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@z#####################报表发送方法执行###########################@@@@@@@@@@@@@@@@@@@@@@@@");
    ComprehensiveReportTimer.startupTimer();
    //LinkReportTimer.startupTimer();
  }

  private void saveLog(String event)
  {
    SysLog vo = new SysLog();
    vo.setEvent(event);
    vo.setLogTime(SysUtil.getCurrentTime());
    vo.setUser("Tomcat");
    vo.setIp("127.0.0.1");

    SysLogDao dao = new SysLogDao();
    try {
      dao.save(vo);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dao.close();
    }
  }

  public Hashtable taskNum()
  {
    Hashtable ht = new Hashtable();
    int index = 0;
    List list = new ArrayList();
    try {
      TaskXml taskxml = new TaskXml();
      list = taskxml.ListXml();
      for (int i = 0; i < list.size(); i++)
      {
        Task task = new Task();
        BeanUtils.copyProperties(task, list.get(i));
        String sign = task.getStartsign();
        if (("1".equals(sign)) && 
          (!task.getTaskname().equals("linktrust"))) {
          String taskname = task.getTaskname();
          Float interval = task.getPolltime();
          String polltimeunit = task.getPolltimeunit();
          ht.put(String.valueOf(index), taskname + ":" + interval + ":" + polltimeunit);
          index++;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return ht;
  }
}