package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.TrapConf;

public class TrapConfDao extends BaseDao implements DaoInterface{

	 
	/**
	 * ±£´æ¹ýÂËµÄOIDÅäÖÃ
	 * @author HP
	 * @date 2013-04-15
	 */
	//@Override
	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		TrapConf conf =(TrapConf)vo;
		StringBuffer sql = new StringBuffer( "insert into nms_throwabletrap(oid,flag,des) values('");
		sql.append(conf.getOid());
		sql.append("','");
		sql.append(conf.getFlag());
		sql.append("','");
		sql.append(conf.getDes()).append("'");
		sql.append(")");
		return saveOrUpdate(sql.toString());
		
	}
	//@Override
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		boolean result = false;
		TrapConf t = (TrapConf)vo;
		StringBuffer sql = new StringBuffer("update nms_throwabletrap set oid ='");
		sql.append(t.getOid());
		sql.append("',des ='");
		sql.append(t.getDes());
		sql.append("',flag ='");
		sql.append(t.getFlag());
		sql.append("'");
		sql.append(" where id = '");
		sql.append(t.getId());
		sql.append("'");
		try
	     {
	         conn.executeUpdate(sql.toString());
	         result = true;
	     }
	     catch(Exception e)
	     {
	    	 result = false;
	         SysLogger.error("EventListDao:update()",e);
	     }
	     finally
	     {
	    	 conn.close();
	     }     
	     return result;
	}
	public TrapConfDao(){
		super("nms_throwabletrap");
	}
	//@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		TrapConf vo = new TrapConf();
		try {
			vo.setId(rs.getInt("id"));
			vo.setOid(rs.getString("oid"));
			vo.setDes(rs.getString("des"));
			vo.setFlag(rs.getString("flag"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
	
	public List<TrapConf> loadAll(){
		List<TrapConf> list = new ArrayList();
		rs = conn.executeQuery("select * from nms_throwabletrap by id desc");
		try {
			while(rs.next()){
				list.add((TrapConf)loadFromRS(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
	         conn.close();
	     }
		return list;
	}
	
	public List<TrapConf> loadThrowable(){
		List<TrapConf> list = new ArrayList();
		rs = conn.executeQuery("select * from nms_throwabletrap where flag = '1'");
		try {
			while(rs.next()){
				list.add((TrapConf)loadFromRS(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
	         conn.close();
	     }
		return list;
	}
	public List<TrapConf> loadAppendDes(){
		List<TrapConf> list = new ArrayList();
		rs = conn.executeQuery("select * from nms_throwabletrap where flag = '0'");
		try {
			while(rs.next()){
				list.add((TrapConf)loadFromRS(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			conn.close();
		}
		return list;
	}
}
