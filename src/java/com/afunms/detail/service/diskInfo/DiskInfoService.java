package com.afunms.detail.service.diskInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DBManager;
import com.afunms.detail.reomte.model.DiskInfo;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.temp.dao.DiskTempDao;
import com.afunms.temp.dao.MemoryTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.DiskForAS400Dao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.DiskForAS400;
import com.afunms.topology.model.HostNode;


public class DiskInfoService {
	
	private String type;
	
	private String subtype;
	
	private String nodeid;
	

	/**
	 * @param type
	 * @param subtype
	 * @param nodeid
	 */
	public DiskInfoService(String nodeid, String type, String subtype) {
		this.nodeid = nodeid;
		this.type = type;
		this.subtype = subtype;
	}
	
	public DiskInfoService(String nodeid) {
		super();
		this.nodeid = nodeid;
	}

	public List<DiskInfo> getCurrDiskInfo(){
		String[] subentities = null;
		return getCurrDiskInfo(subentities);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DiskForAS400> getCurrDiskForAS400Info(){
		List<DiskForAS400> diskForAS400List = null;
		DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
		try {
			diskForAS400List = diskForAS400Dao.findByNodeid(nodeid);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			diskForAS400Dao.close();
		}
		return diskForAS400List;
	}
	
	public List<DiskInfo> getCurrDiskInfo(String[] subentities){
		DiskTempDao diskTempDao = new DiskTempDao();
		List<DiskInfo> list = null;
		try {
			list = diskTempDao.getDiskInfoList(nodeid, type, subtype, subentities);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			diskTempDao.close();
		}
		return list;
	}
	
	/**
	 * 获取当前磁盘利用率图，并返回该图全路径名称，如果图片名称未指定，
	 * 则生成一个默认的图片名称
	 * @param diskInfoList	--- 磁盘数据
	 * @param imgTitle		--- 图片标题
	 * @param imgName		--- 图片名称
	 * @param width			--- 图片宽度
	 * @param heigth		--- 图片高度
	 * @return imgPath      --- 图片全路径
	 */
	public String getCurrDiskInfoUtilizationImg(List<DiskInfo> diskInfoList, String imgTitle, String imgName, int width, int heigth){
		String rowKeys[]={""};					// 纵坐标行关键字
		String[] columnKeys = null;				// 横坐标列关键字
		double[][] data = null;					// 数据
		if(diskInfoList == null){
			diskInfoList = new ArrayList<DiskInfo>();
		}
		int size = diskInfoList.size();
		columnKeys = new String[size];
		data = new double[1][size];
		for(int i = 0 ; i < size; i++){
			DiskInfo diskInfo = diskInfoList.get(i);
			columnKeys[i] = diskInfo.getSindex();
			//System.out.println(diskInfo.getSindex() + "=================================" + diskInfo.getUtilization());
			data[0][i] = Double.valueOf(diskInfo.getUtilization());
		}
		return drawDiskInfoImg(rowKeys, columnKeys, data, imgTitle, imgName, width, heigth);
	}
	
	
	private String drawDiskInfoImg(String[] rowKeys, String[] columnKeys, double[][] data, String imgTitle, String imgName, int width, int heigth){
		if(imgName == null){
			imgName = this.nodeid + "-" + this.type + "-" + this.subtype;
		}
		ChartGraph cg = new ChartGraph();
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);//.createCategoryDataset(rowKeys, columnKeys, data);
		cg.zhu(imgTitle, imgName, dataset, width, heigth);
		return "resource/image/jfreechart/" + imgName + ".png";
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 得到所有sindex
	 * @return
	 */
	public List getCurrDiskSindex(){
		List sindexList = null;
		DiskTempDao diskTempDao = new DiskTempDao();
		try {
				sindexList = diskTempDao.getCurrDiskSindex(nodeid, type, subtype);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			diskTempDao.close();
		}
		return sindexList;
	}
	
	public List<NodeTemp> getCurrDiskInfo(String sindex){
		List<NodeTemp> nodeTempList = null;
		DiskTempDao diskTempDao = new DiskTempDao();
		try {
			nodeTempList = diskTempDao.getCurrDiskListInfo(nodeid, type, subtype,sindex);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			diskTempDao.close();
		}
		return nodeTempList;
	}
	
	public List<NodeTemp> getCurrDiskInfo(String sindex,String starttime, String endtime) throws Exception{
		List<NodeTemp> nodeTempList = null;
		DiskTempDao diskTempDao = new DiskTempDao();
		try {
			nodeTempList = diskTempDao.getCurrDiskListInfo(nodeid, type, subtype,sindex);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			diskTempDao.close();
		}
		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		HostNodeDao dao = new HostNodeDao();
		HostNode node = dao.loadHost(Integer.parseInt(nodeid));
		dao.close();
		String ip = node.getIpAddress();
		String id = ip.replaceAll("\\.", "_");
		String laststarttime = getlastMonth(starttime);
		String lastendtiem = getlastMonth(endtime);
		String sql = "select h.subentity as sindex,h.entity as subentity ,AVG(h.thevalue),avg(h.thevalue)-(SELECT AVG(thevalue) FROM disk"+id+" WHERE collecttime >= '"+laststarttime+"' AND collecttime <= '"+lastendtiem+"' AND subentity = '"+sindex+"' )  as thevalue,h.unit" +
				" from disk"+id+" h where h.collecttime >='"+starttime+"' and h.collecttime <='"+endtime+"' and subentity = '"+sindex+"'"  ;
		rs = dbmanager.executeQuery(sql);
		System.out.println(sql);
		String subentity = null;
		String theva = null;
		String increasevalue = null;
		try {
			while (rs.next()) {
				 subentity = rs.getString(2);
				 theva = rs.getString(3);
				 increasevalue = rs.getString(4);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}
		for(int j=0;j<nodeTempList.size();j++){
			NodeTemp nodeTemp = nodeTempList.get(j);
			if (nodeTemp.getSubentity().equals(subentity)) {
				nodeTemp.setThevalue(theva);
				nodeTemp.setIncreasevalue(increasevalue);
			}
		}
		
		return nodeTempList;
	}
	
	//得到上月日期
		public String getlastMonth(String date) {
			Date d;
			try {
				d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				c.add(Calendar.MONTH, -1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String preMonth = dateFormat.format(c.getTime());
				return preMonth;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}
	
	/**
	 * 得到当前硬盘的信息
	 * @return
	 */
	public Hashtable getCurrDiskListInfo(){
		Hashtable currDiskHashtable = new Hashtable();
		List sindexsList = getCurrDiskSindex();
		DecimalFormat df=new DecimalFormat("#.##");
		for(int i=0;i<sindexsList.size();i++){
			Hashtable diskItemHashtable = new Hashtable();
			List<NodeTemp> diskList = getCurrDiskInfo(String.valueOf(sindexsList.get(i)));
			for(int j=0;j<diskList.size();j++){
				NodeTemp nodeTemp = diskList.get(j);
				String subentity = nodeTemp.getSubentity();
				String thevalue = nodeTemp.getThevalue();
				String unit = nodeTemp.getUnit();
				diskItemHashtable.put(subentity, df.format(Double.valueOf(thevalue))+unit);
				if("Utilization".equals(subentity)){
					diskItemHashtable.put("Utilizationvalue", df.format(Double.valueOf(thevalue)));
				}
			}
			diskItemHashtable.put("name", String.valueOf(sindexsList.get(i)));
			currDiskHashtable.put(i, diskItemHashtable);
		}
		return currDiskHashtable;
	}
	
	public Hashtable getCurrDiskListInfo(String starttime, String endtime) throws Exception{
		Hashtable currDiskHashtable = new Hashtable();
		List sindexsList = getCurrDiskSindex();
		DecimalFormat df=new DecimalFormat("#.##");
		String diskname[] = {"/dev","/etc" ,"/exprot","/home","/lost+found","/mnt","/sbin","/tmp","/usr","/usr/bin","/usr/include","/usr/lib","/usr/sbin","/usr/share","/usr/share/dict" ,"/var","/var/adm" ,"/var/preserve","/var/spool"};
		for (int k = 0 ; k < sindexsList.size(); k++) {
			
			for (int m = 0; m < diskname.length;m++) {
				if (sindexsList.get(k).equals(diskname[m])) {
					sindexsList.remove(k);
					k--;
					break;
				}
			}	
		}
		for(int i=0;i<sindexsList.size();i++){
			Hashtable diskItemHashtable = new Hashtable();
			List<NodeTemp> diskList = getCurrDiskInfo(String.valueOf(sindexsList.get(i)),starttime, endtime);
			for(int j=0;j<diskList.size();j++){
				NodeTemp nodeTemp = diskList.get(j);
				String subentity = nodeTemp.getSubentity();
				String thevalue = nodeTemp.getThevalue();
				String unit = nodeTemp.getUnit();
				String increasevalue = nodeTemp.getIncreasevalue();
				
				diskItemHashtable.put(subentity, df.format(Double.valueOf(thevalue))+unit);
				if("Utilization".equals(subentity)){
					diskItemHashtable.put("Utilizationvalue", df.format(Double.valueOf(thevalue)));
					if (increasevalue==null) {
						increasevalue = "0";
					}
					diskItemHashtable.put("increasevalue", df.format(Double.valueOf(increasevalue))+unit);
					
				}
				
			}
			if (diskItemHashtable.isEmpty()) {
				return currDiskHashtable;
			}
			diskItemHashtable.put("name", String.valueOf(sindexsList.get(i)));
			currDiskHashtable.put(i, diskItemHashtable);
		}
		return currDiskHashtable;
	}

	
	public List getDiskperflistInfo(){
		List diskInfoList = null;
		DiskTempDao diskTempDao = new DiskTempDao();
		try {
			diskInfoList = diskTempDao.getDiskperflistInfo(nodeid);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			diskTempDao.close();
		}
		return diskInfoList;
	} 
	
	public Vector<Diskcollectdata> getDiskInfoVector(){
		Vector<Diskcollectdata> diskInfoVector = null;
		DiskTempDao diskTempDao = new DiskTempDao();
		try {
			diskInfoVector = diskTempDao.getDiskInfoVector(nodeid, type, subtype);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}finally{
			diskTempDao.close();
		}
		return diskInfoVector;
	}
}
