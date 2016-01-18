package com.afunms.system.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.afunms.common.util.SysLogger;
import com.afunms.system.dao.RoleFunctionDao;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.model.Function;
import com.afunms.system.model.RoleFunction;
import com.afunms.system.model.User;

public class CreateRoleFunctionTable {
	private List<Function> allfunction = new ArrayList<Function>();//���е�function;
	private List<Function> function_table = new ArrayList<Function>();//�˽�ɫ�ڴ�ҳ��ʱ��functionlist;
	private String rootPath;//����Ϊ�˵�ͼƬ��ʾԤ���ģ����ڻ�û����˵���ͼƬ
	public CreateRoleFunctionTable(){	
	}
	public CreateRoleFunctionTable(String path){
	    rootPath = path ;  
	}
	
	/**
	 * ͨ��ҳ���һ���˵��ͽ�ɫ�����˵�
	 * @param root_Node
	 * @param role_id
	 * @return
	 */
	public String getPageFunctionTable(String root_Node,String role_id){
		FunctionDao functiondao = null ;
		Function root = null;
		try{
			functiondao = new FunctionDao();
			root = (Function)functiondao.findByID(root_Node);
		}catch(Exception e){
			SysLogger.error("Error in CreateRoleFunctionTable.getPageFunctionTable()",e);
		    e.printStackTrace();
		}finally{
			functiondao.close();
		}
		List<Function> role_Function_list  = null;
		String menuTable = null;

		role_Function_list = getRoleFunctionList();    //getRoleFunctionListByRoleId(role_id);
		getAllFuctionChildByRoot(root,role_Function_list);
		function_table.add(root);
		menuTable = createPageFunctionTable(function_table);
	
		return menuTable;
	}
	
	/**
	 * ���ݽ�ɫid����ȡ��ɫ��Ȩ���б�
	 * @param role_id
	 * @return
	 */
	public List<Function> getRoleFunctionListByRoleId(String role_id){
		FunctionDao  functiondao = null;
	    List<Function> functionList = new ArrayList<Function>();
	    //0�����ǳ����û� ֱ�Ӳ����˵�Ȩ�� �����в˵�������
		/*if("0".equals(role_id)){
			try{
				functiondao = new FunctionDao();  
				allfunction = functiondao.loadAll();
				functionList = allfunction;  
			}catch(Exception e){
				SysLogger.error("Error in CreateRoleFunctionTable.getRoleFunctionListByRoleId()",e);
			    e.printStackTrace();
			}finally{
				functiondao.close();
			}
			return functionList;
		}*/
		RoleFunctionDao roleFunctionDao = null;
		List<RoleFunction> roleFunctionList = null;
		try{
			roleFunctionDao = new RoleFunctionDao();
			roleFunctionList = roleFunctionDao.findByRoleId(role_id);
			functiondao = new FunctionDao();
	        for(int i = 0 ; i < roleFunctionList.size(); i++){
	        	RoleFunction roleFunction = roleFunctionList.get(i);
	        	Function function = (Function)functiondao.findByID(roleFunction.getFuncid());
	        	functionList.add(function);      	 
	        }
		}catch(Exception e){
			SysLogger.error("Error in CreateRoleFunctionTable.getRoleFunctionListByRoleId()",e);
		    e.printStackTrace();
		}finally{
			roleFunctionDao.close();
			functiondao.close();
		}
		return functionList;
		
	}
	
	/**
	 * 
	 * @param role_id
	 * @return
	 */
	public  List<Function> getRoleFunctionList(){
		FunctionDao  functiondao = null;
	    List<Function> functionList = new ArrayList<Function>();
	    //0�����ǳ����û� ֱ�Ӳ����˵�Ȩ�� �����в˵�������
	   Subject subject =  SecurityUtils.getSubject();
	   User user = (User)subject.getPrincipal();
	   
		if("0".equals(user.getRole())){
			try{
				functiondao = new FunctionDao();  
				allfunction = functiondao.loadAll();
				functionList = allfunction;  
			}catch(Exception e){
				SysLogger.error("Error in CreateRoleFunctionTable.getRoleFunctionListByRoleId()",e);
			    e.printStackTrace();
			}finally{
				functiondao.close();
			}
			return functionList;
		}
		
		//ȡ�����в˵�����Ȩ�޼��
		try{

			functiondao = new FunctionDao();  
			allfunction = functiondao.loadAll();
			
			for(Function function:allfunction){
				
				if(subject.isPermitted("menu:*:"+function.getId())){
					logger.info("�˵�"+function.getId()+"---"+function.getCh_desc()+"---����Ȩ");
					functionList.add(function);
				}
			}
	
	        
		}catch(Exception e){
			SysLogger.error("Error in CreateRoleFunctionTable.getRoleFunctionListByRoleId()",e);
		    e.printStackTrace();
		}finally{
			functiondao.close();
		}
		return functionList;
		
	}
	private final Log logger = LogFactory.getLog(CreateRoleFunctionTable.class);
	/**
	 * ��functionList���ҳ�root�µ�������function(���������Ӳ˵��������Ӳ˵�...)
	 * @param root
	 * @param functionList
	 * @return
	 */
	public List<Function> getAllFuctionChildByRoot(Function root,List<Function> functionList){
		for(int i = 0 ; i<functionList.size();i++){
			if(root.getId()==functionList.get(i).getFather_node()){
				function_table.add(functionList.get(i));
				getAllFuctionChildByRoot(functionList.get(i),functionList);
			}
		}
		return function_table;
	}
	
	/**
	 *  ƴ��ҳ��˵�table
	 * @param functionlist
	 * @return
	 */
	public String createPageFunctionTable(List<Function> functionlist){
		CreateMenuTable3 cmt= new CreateMenuTable3(rootPath);		
		String menuTable = cmt.createMenuTable(functionlist);
		return menuTable;
	}
	
	/**
	 * ��ȡfunctionList�еĸ�Ŀ¼root
	 * @param functionList
	 * @return
	 */
	public Function getFunctionRoot(List<Function> functionList){
		Function root = null;
		for(int i = 0 ; i<functionList.size(); i++){
			Function function =  (Function)functionList.get(i);
			if(function.getFather_node()==0){
				root = function;	
			}
		}
		return root;
	}
	
	/**
	 * ��ȡfunction_list�����и�Ŀ¼
	 * @param function_list
	 * @return
	 */
	public List<Function> getAllMenuRoot(List<Function> function_list){
		List<Function> allMenuRoot = new ArrayList<Function>();
		for(int i = 0 ; i < function_list.size() ; i++){
			if(function_list.get(i).getFather_node()==0){
				Function function = function_list.get(i) ;
				Function menuRoot = function_list.get(i) ;
				if(null ==function_list.get(i).getUrl() ){
					menuRoot = setMenuRootUrl(function, function_list);
				}
				if(null != menuRoot){
					allMenuRoot.add(menuRoot);
				}	
			}
		}
		List<Function> menuRootList = compareToByFunc_desc(allMenuRoot);
		return menuRootList;
	}
	
	/**
	 *  ��functionList�л�ȡ������rootΪ���ڵ�Ĳ˵�
	 * @param root
	 * @param functionList
	 * @return
	 */
	public List<Function> getFunctionChild(Function root , List<Function> functionList){
		List<Function> functionList_temp = new ArrayList<Function>();
		for(int i = 0 ; i < functionList.size() ; i++){
			Function function =  (Function)functionList.get(i);
			if(function.getFather_node()==root.getId()){
				functionList_temp.add(function);	
			}			
		}
		List<Function> functionChild_list = new ArrayList<Function>();
		functionChild_list = compareToByFunc_desc(functionList_temp);
			
		return functionChild_list;
	}
	
	/**
	 *  ��function_list������Function����Func_desc������
	 * @param function_list
	 * @return
	 */
	public List<Function> compareToByFunc_desc(List<Function> function_list){
		List<Function> functionList = new ArrayList<Function>();
		int length = function_list.size();
		for(int i = 0 ; i < length ; i++){
			Function function =  (Function)function_list.get(0);
			for(int j = 0 ; j< function_list.size();j++){
				int temp = function.getFunc_desc().compareTo(function_list.get(j).getFunc_desc());
				if(temp > 0 ){
					function = function_list.get(j);
					
				}
			}
			functionList.add(function);
			function_list.remove(function);
		}
		return functionList;
	}
	
	/**
	 *  ����function_list���������и�Ŀ¼menuRoot��URL
	 * @param menuRoot
	 * @param function_list
	 * @return
	 */
	public Function setMenuRootUrl(Function menuRoot, List<Function> function_list){
		List<Function> secondMenu_list = getFunctionChild(menuRoot, function_list);
		List<Function> ThirdMenu_list = new ArrayList<Function>();
		for(int i = 0 ; i < secondMenu_list.size() ; i++){
			ThirdMenu_list = getFunctionChild(secondMenu_list.get(i), function_list);
			for(int j = 0 ; j < ThirdMenu_list.size() ; j++){
				if( null != ThirdMenu_list.get(j).getUrl() && ThirdMenu_list.get(j).getIsCurrentWindow()==0){					
					menuRoot.setUrl(ThirdMenu_list.get(j).getUrl());
					return menuRoot;
				}
			}
		}
		return menuRoot;
	}
	
	
	
}
