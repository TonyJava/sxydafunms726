package com.afunms.system.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.Message;

/**
 * 将message数据对应表system_message中的数据进行处理
 * @author sulin
 *
 */

public class MessageDao extends BaseDao implements DaoInterface {
	public MessageDao(){
		super("system_message");
	}
/**
 * 将数据转换成model
 */
	public BaseVo loadFromRS(ResultSet rs) {
		Message vo=new Message();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIp(rs.getString("ip"));
			vo.setDevtype(rs.getString("devtype"));
			vo.setBigsys(rs.getString("bigsys"));
			vo.setSmallsys(rs.getString("smallsys"));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
/**
 * 将新的message存入数据库
 */
	public boolean save(BaseVo vo) {
		Message message=(Message) vo;
		StringBuffer sql = new StringBuffer(100);
		
		sql.append("insert into system_message(ip,devtype,bigsys,smallsys)values(");
		sql.append("'");
		sql.append(message.getIp());
		sql.append("','");
		sql.append(message.getDevtype());
		sql.append("','");
		sql.append(message.getBigsys());
		sql.append("','");
		sql.append(message.getSmallsys());
		sql.append("')");
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
/**
 * 将数据库中的message进行修改
 */
	public boolean update(BaseVo vo) {
		Message message=(Message) vo;
		StringBuffer sql = new StringBuffer(100);
		
		sql.append("update system_message set ip='");
		sql.append(message.getIp());
		sql.append("',devtype='");
		sql.append(message.getDevtype());
		sql.append("',bigsys='");
		sql.append(message.getBigsys());
		sql.append("',smallsys='");
		sql.append(message.getSmallsys());
		sql.append("'where id=");
		sql.append(message.getId());
		System.out.println(sql.toString());
		return saveOrUpdate(sql.toString());
	}
/**
 * 批量删除message记录
 * @param messageid
 * @return
 */
	public boolean deleteall(String[] messageid){
		boolean result = false;
		try{
			for(int i=0;i<messageid.length;i++){
			conn.addBatch("delete from system_message where id=" + messageid[i]);
			conn.addBatch("delete from system_message where messageid=" + messageid[i]);
			conn.executeBatch();
			result = true;
	   }
		}
	   catch(Exception ex){
		    SysLogger.error("MessageDao.delete()",ex);
	        result = false;
	   }finally{
		    conn.close();
	   }
	   return result;
	}	
}
