package com.afunms.comprehensivereport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereport.model.CompreReportInfo;

public class CompreReportUtilDao extends BaseDao implements DaoInterface {

	public CompreReportUtilDao()
	{
		super("nms_compreReport_resources");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CompreReportInfo cri = new CompreReportInfo();
		try {
			cri.setId(rs.getInt("id"));
			cri.setName(rs.getString("name"));
			cri.setType(rs.getString("type"));
			cri.setUserName(rs.getString("userName"));
			cri.setEmail(rs.getString("email"));
			cri.setEmailTitle(rs.getString("emailTitle"));
			cri.setEmailContent(rs.getString("emailContent"));
			cri.setAttachmentFormat(rs.getString("attachmentFormat"));
			cri.setIds(rs.getString("ids"));
			cri.setReportType(rs.getString("reportType"));
			cri.setSendTime(rs.getString("sendTime"));
			cri.setSendTime2(rs.getString("sendTime2"));
			cri.setNodeid(rs.getString("nodeid"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cri;
	}

	public CompreReportInfo findById(int id){
		CompreReportInfo cri = new CompreReportInfo();
		try
	     {
	         rs = conn.executeQuery("select * from nms_compreReport_resources where id="+id);
	         while(rs.next())
	        	cri=(CompreReportInfo) loadFromRS(rs);
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportUtilDao:findById()",e);
	         
	     }
	     finally
	     {
	    	 if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	 if (conn!=null) {
	    		 conn.close();
			}
	         
	     }
	     return cri;
	}
	
	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
