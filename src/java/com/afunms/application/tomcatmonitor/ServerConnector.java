/*
 * Created on 2005-1-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.afunms.application.tomcatmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class ServerConnector {
	static Logger logger = Logger.getLogger(ServerConnector.class);

	//web服务器地址
	String webServerHost = "localhost";

	int webServerPort = 8080;

	String statusPath = "/jkstatus";

	//管理员用户名密码
	String user = "admin";

	String pass = "";

	//域名
	String domain;

	String qry;

	HashMap mStream;

	//时间参数
	long accessInterval = 0;

	HashMap mbeans = new HashMap();

	boolean isConnect = true;

	public ServerConnector() {

	}

	//取得地址
	public String getWebServerHost() {
		return webServerHost;
	}

	//设置地址
	public void setWebServerHost(String webServerHost) {
		this.webServerHost = webServerHost;
	}

	//取得服务器端口号
	public int getWebServerPort() {
		return webServerPort;
	}

	//设置服务器端口号
	public void setWebServerPort(int webServerPort) {
		this.webServerPort = webServerPort;
	}

	//取得访问周期
	public long getAccessInterval() {
		return accessInterval;
	}

	//设置访问周期
	public void setAccessInterval(long accessInterval) {
		this.accessInterval = accessInterval;
	}

	//取得用户名
	public String getUser() {
		return user;
	}

	//设置用户名
	public void setUser(String user) {
		this.user = user;
	}

	//取得密码
	public String getPass() {
		return pass;
	}

	//设置密码
	public void setPass(String pass) {
		this.pass = pass;
	}

	//取得状态路径
	public String getStatusPath() {
		return statusPath;
	}

	//设置状态路径
	public void setStatusPath(String statusPath) {
		this.statusPath = statusPath;
	}

	//取得过滤器
	public String getQry() {
		return qry;
	}

	//设置过滤器
	public void setQry(String qry) {
		this.qry = qry;
	}

	//取得stream
	public HashMap getMStream() {
		return mStream;
	}

	//保存stream
	public void setMStream(HashMap mStream) {
		this.mStream = mStream;
	}

	//销毁访问
	public void destroy() {
		try {
			setMStream(null);
		} catch (Throwable t) {
			System.out.println("销毁错误：" + t);
		}
	}

	//初始化
	public void init() throws IOException {
		try {
			PropertyConfigurator.configure(getClass().getClassLoader()
					.getResource("log4j.properties"));
			System.out.println("初始化：" + webServerHost + " " + webServerPort);
			setMStream(null);
			streamToVector(getStream(getQry()));
		} catch (Throwable t) {
			System.out.println("初始化错误：" + t);
		}
	}

	//开始
	public void start() throws IOException {
		System.out.println("监控开始...");
		init();
	}

	protected InputStream getStream(String qry) throws Exception {
		InputStream is = null;
		try {
			HTTPResponse rsp = getConnect(webServerHost, webServerPort,
					"/manager/status", "Tomcat Manager Application", user,
					pass, true);
			is = rsp.getInputStream();
			return is;
		} catch (IOException e) {
			System.out.println("不能连接:" + webServerHost + ":" + webServerPort
					+ " " + e.toString());
			System.exit(0);
			return is;

		}
	}

	public void WriteToLog(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				logger.info("Log:" + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void streamToVector(InputStream is) {
		HashMap map = new HashMap();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		int i = 0;
		try {
			while ((line = br.readLine()) != null && "" != line) {
				//System.out.println(line+"----");
				map.put(String.valueOf(i), line);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setMStream(map);
	}

	public HTTPResponse getConnect(String ip, int port, String target,
			String realm, String user, String password, boolean isAuth) {
		HTTPResponse rsp = null;
		try {
			URL url = new URL("http://" + ip + ":" + port);
			System.out.println("连接：" + url);
			HTTPConnection con = new HTTPConnection(url);
			if (isAuth == true) {
				con.addBasicAuthorization(realm, user.trim(), password.trim());
			}
			rsp = con.Get(target);
			//System.out.println(rsp.toString());
		} catch (Exception e) {
			isConnect = false;
			System.out.println("服务器连接被拒绝");
			//e.printStackTrace();
		}
		return rsp;
	}

	/**
	 * @return Returns the isConnect.
	 */
	public boolean isConnect() {
		return isConnect;
	}

	/**
	 * @param isConnect
	 *            The isConnect to set.
	 */
	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}
}