package com.afunms.soa.rest.controllers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.afunms.application.dao.DBDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.inform.util.SystemSnap;
import com.afunms.system.manage.UserManager;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorNodeDTO;

@RestController
@RequestMapping(value = "/devices")
public class DevicesController {

	class SimpleDevice{
		private int category;
		private String categoryName;
		private int count;
		private int status;
		
		/**
		 * @return the status
		 */
		public int getStatus() {
			return status;
		}
		/**
		 * @param status the status to set
		 */
		public void setStatus(int status) {
			this.status = status;
		}
		/**
		 * @param category
		 * @param categoryName
		 * @param count
		 */
		public SimpleDevice(int category, String categoryName, int count,int status) {
			super();
			this.category = category;
			this.categoryName = categoryName;
			this.count = count;
			this.status = status;
		}
		/**
		 * @return the category
		 */
		public int getCategory() {
			return category;
		}
		/**
		 * @param category the category to set
		 */
		public void setCategory(int category) {
			this.category = category;
		}
		/**
		 * @return the categoryName
		 */
		public String getCategoryName() {
			return categoryName;
		}
		/**
		 * @param categoryName the categoryName to set
		 */
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		/**
		 * @param count the count to set
		 */
		public void setCount(int count) {
			this.count = count;
		}
		
	}
	@RequestMapping(value = "/simpleAll", method = RequestMethod.GET,produces="application/json")
	public List<SimpleDevice> getSimpleDevicesForHomePage() {
/*
 部分变量命名不符合命名惯例，旧代码迁移过来，目前不是关注重点
*/
		Subject subject = SecurityUtils.getSubject();

		User vo = (User) subject.getPrincipal();
		UserManager manager = new UserManager();
		// 网络
		HostNodeDao nodedao = new HostNodeDao();
		List<SimpleDevice> simpleDeviceList = new ArrayList();

		List routelist = new ArrayList();
		List switchlist = new ArrayList();
		// 服务器
		List hostlist = new ArrayList();
		// 数据库
		List dblist = new ArrayList();
		// 安全
		List seculist = new ArrayList();
		// 存储
		List storagelist = new ArrayList();
		// 服务
		int servicesize = 0;

		// 中间件
		int midsize = 0;

		int routesize = 0;
		int switchsize = 0;

		String bids = vo.getBusinessids();
		if (vo.getRole() == 0 || vo.getRole() == 1) {
			bids = "-1";
		}
		servicesize = manager.getServiceNum(vo);
		midsize = manager.getMiddleService(vo);
		seculist = nodedao.loadNetworkByBid(8, bids);
		routelist = nodedao.loadNetworkByBidAndCategory(1, bids);
		switchlist = nodedao.loadNetworkByBidAndCategory(2, bids);
		hostlist = nodedao.loadNetworkByBid(4, bids);
	

		if (routelist != null)
			routesize = routelist.size();
		if (switchlist != null)
			switchsize = switchlist.size();

		simpleDeviceList.add(new SimpleDevice(1,"路由器",routesize,SystemSnap.getRouterStatus()));
		simpleDeviceList.add(new SimpleDevice(2,"交换机",switchsize,SystemSnap.getSwitchStatus()));
		simpleDeviceList.add(new SimpleDevice(3,"数据库",dblist.size(),SystemSnap.getDbStatus()));
		simpleDeviceList.add(new SimpleDevice(4,"服务器",hostlist.size(),SystemSnap.getServerStatus()));
		simpleDeviceList.add(new SimpleDevice(5,"中间件",midsize,SystemSnap.getMiddleStatus()));
		simpleDeviceList.add(new SimpleDevice(6,"服务",servicesize,SystemSnap.getServiceStatus()));
		simpleDeviceList.add(new SimpleDevice(7,"存储",storagelist.size(),SystemSnap.getNetworkStatus()));
		simpleDeviceList.add(new SimpleDevice(8,"安全设备",seculist.size(),SystemSnap.getFirewallStatus()));

		return simpleDeviceList;
	}
}
