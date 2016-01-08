package com.oss.services.client;

import java.net.MalformedURLException;
import java.util.Random;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;

import com.database.config.SystemConfig;
import com.oss.services.client.WebServicesServer;

public class WebServicesClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Service serviceModel = new ObjectServiceFactory().create(WebServicesServer.class);
		XFire xf = XFireFactory.newInstance().getXFire();
		XFireProxyFactory factory = new XFireProxyFactory(xf);
		
		String[] randomType = {"cpu","mem","ping","disk"}; 
		String[] randomValue = {"1","2","3"};
		
		//String serviceUrl = "http://192.168.2.93:8080/oamn/services/MyWebService";
		String serviceUrl = "http://192.168.2.44:8080/oss/services/MyWebService";
		WebServicesServer client = null;
		try {
			client = (WebServicesServer) factory.create(serviceModel,serviceUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String result = client.example("CCCCCCCCCC");
		client.ServicesReceiveAlarmMessage("78ac1c03bc4c4ac28da5f84f84c6d251", "15", "警告信息",
				randomType[Math.abs(new Random().nextInt())%randomType.length], 
				randomValue[Math.abs(new Random().nextInt())%randomValue.length]);
	}
	
	public void sendRandomAlarmData(){
		Service serviceModel = new ObjectServiceFactory().create(WebServicesServer.class);
		XFire xf = XFireFactory.newInstance().getXFire();
		XFireProxyFactory factory = new XFireProxyFactory(xf);
		
		String[] randomType = {"cpu","mem","ping","disk"}; 
		String[] randomValue = {"1","2","3"};
		
		//String serviceUrl = "http://192.168.2.93:8080/oamn/services/MyWebService";
		String serviceUrl = "http://192.168.2.44:8080/oss/services/MyWebService";
		WebServicesServer client = null;
		try {
			client = (WebServicesServer) factory.create(serviceModel,serviceUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String result = client.example("CCCCCCCCCC");
		client.ServicesReceiveAlarmMessage("78ac1c03bc4c4ac28da5f84f84c6d251", "15", "警告信息",
				randomType[Math.abs(new Random().nextInt())%randomType.length], 
				randomValue[Math.abs(new Random().nextInt())%randomValue.length]);
	}
	
	
	/**
	 * 
	 * 发送告警信息实现类
	 * @param url 连接webservice的url
	 * @param bsid  业务系统id
	 * @param nodeid  节点id
	 * @param name  告警内容
	 * @param alarmType 告警类型
	 * @param alarmValue 告警级别
	 */
	public void sendAlarmToServiceServer(String url,String bsid,String nodeid,String name,String alarmType,String alarmValue){
		Service serviceModel = new ObjectServiceFactory().create(WebServicesServer.class);
		XFire xf = XFireFactory.newInstance().getXFire();
		XFireProxyFactory factory = new XFireProxyFactory(xf);
		//String serviceUrl = "http://192.168.2.44:8080/oss/services/MyWebService";
		String serviceUrl = url;
		WebServicesServer client = null;
		try {
			client = (WebServicesServer) factory.create(serviceModel,serviceUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// bSystemConfig.getConfigInfomation("alarmWebservice", "bsid")
		client.ServicesReceiveAlarmMessage(bsid,nodeid,name,alarmType,alarmValue);
	}
}
