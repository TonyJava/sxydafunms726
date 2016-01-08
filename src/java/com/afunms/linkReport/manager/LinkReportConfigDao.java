package com.afunms.linkReport.manager;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereport.model.CompreReportInfo;

public class LinkReportConfigDao extends BaseDao implements DaoInterface {

	public LinkReportConfigDao(){
		super("nms_linkReport_config");
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		LinkReportInfo lri = new LinkReportInfo();
		try {
			lri.setId(rs.getInt("id"));
			lri.setName(rs.getString("name"));
			lri.setType(rs.getString("type"));
			lri.setUserName(rs.getString("userName"));
			lri.setEmail(rs.getString("email"));
			lri.setEmailTitle(rs.getString("emailTitle"));
			lri.setEmailContent(rs.getString("emailContent"));
			lri.setAttachmentFormat(rs.getString("attachmentFormat"));
			lri.setIds(rs.getString("ids"));
			lri.setTerms(rs.getString("terms"));
			lri.setSendTime(rs.getString("sendTime"));
			lri.setSendTime2(rs.getString("sendTime2"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lri;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	public LinkReportInfo findById(int id){
		LinkReportInfo lri = new LinkReportInfo();
		try
	     {
	         rs = conn.executeQuery("select * from nms_linkReport_config where id="+id);
	         while(rs.next())
	        	lri=(LinkReportInfo) loadFromRS(rs);
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("LinkReportConfigDao:findById()",e);
	         
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
	     return lri;
	}
	
}
