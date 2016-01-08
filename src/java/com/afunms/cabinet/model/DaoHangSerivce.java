package com.afunms.cabinet.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.afunms.cabinet.dao.MachineCabinetDao;


public class DaoHangSerivce {
	public List<String> mcallId( String id) {
		List list=new ArrayList();
		MachineCabinetDao dao=new MachineCabinetDao();
		try
  		{
           list =dao.loadId(id);
  		}catch(Exception e){
  			
  		}finally{
  		dao.close();
  		}
		return list;
	}

}
