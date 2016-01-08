package com.afunms.application.course.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.application.course.model.LsfClassComprehensiveModel;
import com.afunms.application.course.model.Lsfclass;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class LsfClassProcessMonitoringDao extends BaseDao implements
		DaoInterface {

	public LsfClassProcessMonitoringDao() {
		super("lsf_data_temp");
	}
	
	public BaseVo loadFromRS(ResultSet rs) {
		Lsfclass vo = new Lsfclass();
		try {
			vo.setClass_id(rs.getInt("classid"));
			vo.setClass_name(rs.getString("classname"));
			vo.setClass_pesc(rs.getString("classpesc"));
		} catch (Exception ex) {
			ex.printStackTrace();
			vo = null;
		}
		return vo;
	}
	
	public BaseVo loadFormRsLsfclass(ResultSet rs){
		Lsfclass vo = new Lsfclass();
		try {
			vo.setClass_id(rs.getInt("classid"));
			vo.setClass_name(rs.getString("classname"));
			vo.setClass_pesc(rs.getString("classpesc"));
//			vo.setNodeid(rs.getString("nodeid"));
		} catch (Exception ex) {
			ex.printStackTrace();
			vo = null;
		}
		return vo;
	}
	
	
	public BaseVo loadFormRsComprehensive(ResultSet rs){
		LsfClassComprehensiveModel vo = new LsfClassComprehensiveModel();
		try {
			vo.setAlarm(rs.getString("alarm"));
			vo.setClass_id(rs.getString("c_classid"));
			vo.setClass_name(rs.getString("a_classnme"));
			vo.setClass_pesc(rs.getString("classpesc"));
			vo.setEnable(rs.getString("b_enable"));
			vo.setLogcoud(rs.getString("c_logcount"));
			vo.setLogflg(rs.getString("b_logflg"));
			vo.setMaster(rs.getString("master"));
			vo.setNodeid(rs.getString("c_nodeid"));
			vo.setAlias(rs.getString("alias"));
			vo.setIp_address(rs.getString("ip_address"));
			vo.setJid(rs.getString("jid"));
			vo.setSys_name(rs.getString("sys_name"));
			vo.setType(rs.getString("type"));
		} catch (Exception ex) {
			ex.printStackTrace();
			vo = null;
		}
		return vo;
	}

	/**
	 * 联合查询
	 * @return
	 */
	public List loadForm_join(String sql){
		List list = new ArrayList();
		try {
			rs = conn.executeQuery(sql);
			while(rs.next()){
				LsfClassComprehensiveModel comprehensive = new LsfClassComprehensiveModel();
				comprehensive = (LsfClassComprehensiveModel)loadFormRsComprehensive(rs);
				list.add(comprehensive);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		return list;
	}
	/**
	 * 查询lsf_class指定数据
	 * @return
	 */
	public List loadFrom_lsf_class_id() {
		List list = new ArrayList();
		try {
			rs = conn.executeQuery("select * from lsf_class ");
			while (rs.next()) {
				Lsfclass vo = new Lsfclass();
				vo = (Lsfclass) loadFromRS(rs);
				list.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		return list;
	}

	//树，数据加载
	public List loadFormLsfClass(){
		List list = new ArrayList();
		try{
			rs = conn.executeQuery("select * from lsf_class");
			while (rs.next()) {
				Lsfclass vo = new Lsfclass();
//				vo = (Lsfclass) loadFromRS(rs);
//				vo = (Lsfclass) loadFormRsComprehensive(rs);
				vo = (Lsfclass) loadFormRsLsfclass(rs);
				list.add(vo);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		return list;
	}
	
	/**
	 * 查询lsf_class所有数据
	 * @return
	 */
	public List loadFrom_lsf_class() {
		List list = new ArrayList();
		
		try {
//			rs = conn.executeQuery("select * from lsf_class;");
			rs = conn.executeQuery("select d.ip_address,d.alias ,a.classname as a_classnme," +
					"a.classpesc,b.enable as b_enable,b.logflg as b_logflg,c.classid as c_classid," +
					"c.nodeid as c_nodeid,c.logcoud as c_logcount,c.master,b.jid,c.alarm,d.type,d.sys_name from lsf_class a," +
					"lsf_class_node b,lsf_data_temp c,topo_host_node d where b.classid=c.classid and " +
					"b.nodeid=c.nodeid and a.classid=c.classid and c.nodeid=d.id");
			while (rs.next()) {
				LsfClassComprehensiveModel vo = new LsfClassComprehensiveModel();
//				vo = (Lsfclass) loadFromRS(rs);
				vo = (LsfClassComprehensiveModel) loadFormRsComprehensive(rs);
				list.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		return list;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
}
