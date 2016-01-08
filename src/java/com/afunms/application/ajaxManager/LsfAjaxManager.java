package com.afunms.application.ajaxManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.afunms.application.course.model.LsfClassComprehensiveModel;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;

public class LsfAjaxManager extends AjaxBaseManager implements AjaxManagerInterface{

	
	public void execute(String action) {
		// TODO Auto-generated method stub
		// ∂Àø⁄≈‰÷√
		if (action.equals("getAlarmDetailInfo")) {
			getAlarmDetailInfo();
		}
	}
	public void getAlarmDetailInfo(){
		
//		Map<String,LsfClassComprehensiveModel> map = new HashMap<String, LsfClassComprehensiveModel>();
		Map<String,String> map = new HashMap<String, String>();
		List list = new ArrayList();
		String nodeid = getParaValue("id");
		DBManager db = new DBManager();
    	System.out.println("nodeid:"+nodeid);
    	if(nodeid == null){
    		return ;
    	}
    	StringBuffer sql = new StringBuffer();
    	sql.append("select * from system_eventlist where nodeid=");
    	sql.append(nodeid);
    	sql.append(" and subentity ='proc' group by recordtime desc limit 5");
    	ResultSet rs = db.executeQuery(sql.toString());
    	int i=0;
    	try {
			while(rs.next()){
				i++;
				String le = rs.getInt("level1")+"";
				String content = rs.getString("content");
				String recordtime = rs.getString("recordtime");
				
//				LsfClassComprehensiveModel lsf_Vo = new LsfClassComprehensiveModel();
//				lsf_Vo.setLevel1(rs.getInt("level1")+"");
//				lsf_Vo.setContent(rs.getString("content"));
//				lsf_Vo.setRecordtime(rs.getString("recordtime"));
//				map.put(nodeid+"_"+i, lsf_Vo);
				map.put("level"+i, le);
				map.put("content"+i, content);
				map.put("recordtime"+i, recordtime);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		map.put("flage", i+"");
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
}
//Iterator iter = map.entrySet().iterator();
//while(iter.hasNext()){
//	Map.Entry entrys = (Map.Entry) iter.next();
//	Object key = entrys.getKey();
//	LsfClassComprehensiveModel lsf_Vos = (LsfClassComprehensiveModel) entrys.getValue();
//	String level = lsf_Vos.getLevel1();
//	System.out.println("level:______________________________________"+level);
//	String content = lsf_Vos.getContent();
//	System.out.println("content:______________________________________"+content);
//	String recordtime = lsf_Vos.getRecordtime();
//	System.out.println("recordtime:______________________________________"+recordtime);
//}