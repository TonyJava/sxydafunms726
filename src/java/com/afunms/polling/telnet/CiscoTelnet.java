package com.afunms.polling.telnet;
//����Linux��telnet����
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.dao.SlaNodePropDao;
import com.afunms.config.model.CiscoSlaCfgCmdFile;
import com.afunms.config.model.CmdResult;
import com.afunms.config.model.Huaweitelnetconf;
import com.afunms.config.model.SlaNodeProp;
import com.afunms.slaaudit.dao.SlaAuditDao;
import com.afunms.slaaudit.model.SlaAudit;
import com.afunms.system.model.User;

public class CiscoTelnet
{
	private TelnetClient telnet = new TelnetClient();
	private InputStream in;
	private PrintStream out;
	private String prompt = ">";
	private String server;
	private String user;
	private String password;
	private int nummber = 2000;// ����һ����ȡ�ַ�������
	
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public CiscoTelnet(String server, String user, String password) 
	{
		this.server = server;
		this.user = user;
		this.password = password;
	}
	public boolean login()
	{
		boolean isLogin = false;
		try {
			// Connect to the specified server
			telnet.connect(server, 23);

			// Get input and output stream references
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());

			// Log the user on
			readUntil("Username:");
			write(user);
			readUntil("Password:");
			write(password);
			// Advance to a prompt
			String temp = readUntil1(prompt,"Username:");
			if(temp.endsWith(">"))
				isLogin = true;
		} catch (Exception e) {
			isLogin = false;
			this.disconnect();
			e.printStackTrace();
		}
		return isLogin;
	}
	private String readUntil1(String pattern1,String pattern2) 
	{
		StringBuffer sb = new StringBuffer();
		try {
		
			char ch = (char) in.read();
			int n = 0;
			boolean flag=true;
			while (flag) {
				
				if (ch == 0 || ch == 13 || ch == 10 || (ch >= 32)) {
					sb.append(ch);
				}
				if (sb.toString().endsWith(pattern1) ) //�涨�������pattern1��β��˵������ִ�гɹ�
				{
					// System.out.println(sb.toString());
					return pattern1;
				}
				if (sb.toString().endsWith(pattern2) ) //�����pattern1��β��˵������ִ�гɹ�
				{
					// System.out.println(sb.toString());
					return pattern2;
				}
					
				ch = (char) in.read();
				//System.out.println(ch+"==========="+n);
				n++;
				if (n > this.nummber) {// �����ȡ���ַ���������2����ַ�����û����ȷ
					sb.delete(0, sb.length());
					sb.append("user or password error");
					flag=false;
					break;
				}
				// System.out.println(n);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	/**
	 * @author wxy
	 * ��֤Su�û���������
	 * @param pattern
	 * @return
	 */
	public String readSuUntil(String pattern) 
	{
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();

			while (true) 
			{
				
				sb.append(ch);
				if (sb.toString().indexOf("Password:")>-1) {
					disconnect();
					return "user or password error";
				
				}
				if(sb.toString().endsWith(" --More-- "))
				{
					out.write(32);
					out.flush();
				}
				
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) 
					{
						
						return sb.toString();
					}
				}
				ch = (char) in.read();
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String  sendSuPwd(String command) {
		write(command);
		return readSuUntil(prompt);
	}
	public void su(String password) 
	{
		try {
			write("su");
			readUntil("Password: ");
			write(password);
			prompt = "#";
			readUntil(prompt + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readUntil(String pattern) 
	{
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;
			char ch = (char) in.read();
			while (flag) 
			{
				
				
				sb.append(ch);
				if(sb.toString().indexOf("user or password error")>-1)
				{
					flag=false;
					disconnect();
					return "user or password error";
				}
				if(sb.toString().endsWith(" --More-- "))
				{
					out.write(32);
					out.flush();
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) 
					{
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String readUntil(String pattern,String[] command) 
	{
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;
			
			char ch = (char) in.read();
			while (flag) 
			{
				sb.append(ch);
				if(sb.toString().indexOf("user or password error")>-1)
				{
					flag=false;
					disconnect();
					return "user or password error";
				}
				if(sb.toString().endsWith(" --More-- "))
				{
					out.write(32);
					out.flush();
				}
				if(sb.toString().indexOf("Configuring from terminal, memory, or network [terminal]?")>-1){
					out.write(13);
					out.flush();
					if(command.length>1){
						for(int i=1;i<command.length;i++){
							write(command[i]);							
						}
					}
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) 
					{
						return pattern;
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String readSLAUntil(String pattern,String[] command) 
	{
		String retStr = "1";
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;
			
			
			char ch = (char) in.read();
			while (flag) 
			{
				sb.append(ch);
				if(sb.toString().indexOf("user or password error")>-1)
				{
					flag=false;
					disconnect();
					return "user or password error";
				}
				if(sb.toString().endsWith(" --More-- "))
				{
					out.write(32);
					out.flush();
				}
				if(sb.toString().indexOf("Configuring from terminal, memory, or network [terminal]?")>-1){
					out.write(13);
					out.flush();
					if(command.length>1&&ch == lastChar){
						for(int i=0;i<command.length;i++){
							readUntil(prompt);
							write(command[i]);
							
						}
						//���в������
						SlaAudit slaaudit = new SlaAudit();
						slaaudit.setCmdcontent("");
						
					}
				}
					
				
				if (ch == lastChar) {
					//SysLogger.info(sb.toString()+"====&&&&&");
					if (sb.toString().endsWith(pattern)) 
					{
						return sb.toString();
					}
				}
				ch = (char) in.read();
				//SysLogger.info("ch======="+ch);
			}
		} catch (Exception e) {
			retStr = "0";
			e.printStackTrace();
		}
		return retStr;
	}
	public String readSLAUntil(String pattern,String[] command,String ip,List<CmdResult> list) 
	{
		String retStr = "1";
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			boolean flag = true;
			
			
			char ch = (char) in.read();
			while (flag) 
			{
				sb.append(ch);
				
				if(sb.toString().endsWith(" --More-- "))
				{
					out.write(32);
					out.flush();
				}
				if(sb.toString().indexOf("Configuring from terminal, memory, or network [terminal]?")>-1){
					out.write(13);
					out.flush();
				}
				if (ch == lastChar) {
					//SysLogger.info(sb.toString()+"====&&&&&");
					if (sb.toString().endsWith(pattern)) 
					{
						return sb.toString();
					}
				}
				ch = (char) in.read();
				//SysLogger.info("ch======="+ch);
			}
		} catch (Exception e) {
			retStr = "0";
			e.printStackTrace();
		}
		return retStr;
	}

	public void write(String value) 
	{
		try {
			out.println(value);
			out.flush();
			
			//System.out.println(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String sendCommand(String command) 
	{
		try {
			write(command);
			return readUntil(prompt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String sendCommand(String[] command) 
	{
		String result="";
		StringBuffer sb=new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date());
		try {
			for (int i = 0; i < command.length; i++) {
				write(command[i]);
				result=readUntil(prompt);
				if (null != result && !result.equals("user or password error")) {
					String[] st = result.split("\r\n");
					StringBuffer buff = new StringBuffer();
					buff.append("\r\n-----------------Date("+time+")-----------------\r\n");
					buff.append("-----------------begin("+command[i]+")-----------------\r\n");
					for (int j = 1; j < st.length-1; j++) { 
						
						if(!st[j].contains("--More--"))
						{
							buff.append(st[j]).append("\r\n");
						}
					}
					buff.append("-----------------end("+command[i]+")-----------------\r\n");

					result = buff.toString();
					
				}
				sb.append(result);
			}
			
			//return readUntil(prompt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public String[] sendSLACommand(String[] command) 
	{
		String result="";
		String[] results = new String[command.length];
		StringBuffer sb=new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date());
		try {
			for (int i = 0; i < command.length; i++) {
				write(command[i]);
				result=readUntil(prompt);
				if (null != result && !result.equals("user or password error")) {
					String[] st = result.split("\r\n");
					StringBuffer buff = new StringBuffer();
					for (int j = 1; j < st.length-1; j++) { 
						
						if(!st[j].contains("--More--"))
						{
							buff.append(st[j]).append("\r\n");
						}
					}

					result = buff.toString();
					
				}
				results[i] = result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public void disconnect() 
	{
		try {
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getCfg(String enPasswd,String bkptype)
	{
		String result = null;
		
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		sendCommand(enPasswd);
		if(bkptype.equals("run"))
			result = sendCommand("show run");
		else
			result = sendCommand("show startup");
		// �Խ�����и�ʽ��
		if (null != result && !result.equals("user or password error")) {
			String[] st = result.split("\r\n");
			StringBuffer buff = new StringBuffer();
			for (int i = 1; i < st.length-1; i++) { 
				
				if(!st[i].contains("--More--"))
				{
					buff.append(st[i]).append("\r\n");
				}
			}
			result = buff.toString();

		}
		disconnect();
		return result;
	}
	/**
	 * ���������ȡ��Ϣ
	 * @param enPasswd
	 * @param content
	 * @return
	 */
	public String getFileCfg(String enPasswd,String[] content)
	{
		String result = "";
		
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		String enPassword=sendSuPwd(enPasswd);
		if(enPassword.equals("user or password error"))
			return enPassword;
		//result = sendCommand(content);
		System.out.println(":"+enPassword+":");
		// �Խ�����и�ʽ��
//		if (null != result && !result.equals("user or password error")) {
//			String[] st = result.split("\r\n");
//			StringBuffer buff = new StringBuffer();
//			for (int i = 1; i < st.length-1; i++) { 
//				
//				if(!st[i].contains("--More--"))
//				{
//					buff.append(st[i]).append("\r\n");
//				}
//			}
//			result = buff.toString();
//			
//		}
		disconnect();
		return result;
	}
	
	/**
	 * ���������ȡSLA��Ϣ
	 * @param enPasswd
	 * @param content
	 * @return
	 */
	public String[] getSlaResult(String enPasswd,String[] commands)
	{
		String[] results = new String[commands.length];
		
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		String enPassword=sendSuPwd(enPasswd);
		if(enPassword.equals("user or password error"))
			return null;
		results = sendSLACommand(commands);
		disconnect();
		return results;
	}
	
	/**
	 * �ֹ����������
	 * @param enPasswd
	 * @param content
	 * @return
	 */
	
	public String getCommantValue(String enPasswd,String[] command)
	{
		
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		sendCommand(enPasswd);
		String result="";
		StringBuffer sb=new StringBuffer();
		try {
			for (int i = 0; i < command.length; i++) {
				write(command[i]);
				result=readUntil(prompt);
				if (null != result && !result.equals("user or password error")) {
					String[] st = result.split("\r\n");
					StringBuffer buff = new StringBuffer();
					for (int j = 0; j < st.length; j++) { 
						if(j==st.length-1){
							buff.append(st[j]);	
						}else if(!st[j].contains("--More--"))
						{
							buff.append(st[j]).append("\r\n");
						}
					}

					result = buff.toString();
					
				}
				sb.append(result);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnect();
		return result;
	}
	/**
	 * �ֹ���������
	 * @param enPasswd
	 * @param command
	 * @param list
	 * @param ip
	 */
	public void getCommantValue(String enPasswd,String[] command,List<CmdResult> list,String ip)
	{
		
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		sendCommand(enPasswd);
		String result="";
		try {
			for (int i = 0; i < command.length; i++) {
				boolean isSucess=true;
				CmdResult cmdResult=new CmdResult();
				String cmdstr = command[i];
				//write(command[i]);
				write(cmdstr);
				if("config".equalsIgnoreCase(cmdstr)){
					result= readUntil(prompt,command);
					for(int k=0;k<command.length;k++){
						cmdResult=new CmdResult();
						cmdResult.setIp(ip);
						cmdResult.setCommand(command[k]);
						cmdResult.setResult("ִ�гɹ�!");
						list.add(cmdResult);
						//SysLogger.info(cmdResult.getCommand()+"==========");
					}
					
					break;
				}
				result=readUntil(prompt);
				if (null != result && !result.equals("user or password error")) {
					String[] st = result.split("\r\n");
					for (int j = 0; j < st.length; j++) { 
						 if(!st[j].contains("--More--"))
						{
							 if (st[j].indexOf("% Ambiguous command:") > -1
										|| st[j].indexOf("% Unknown command or computer name, or unable to find computer address") > -1
										|| st[j].indexOf("% Invalid input detected at '^' marker.") > -1
										|| st[j].indexOf("% Unrecognized host or address") > -1) {
									cmdResult.setIp(ip);
									cmdResult.setCommand(command[i]);
									cmdResult.setResult("ִ��ʧ��!");
									isSucess=false;
								} 
						}
					}
					
					
				}else {
					cmdResult.setIp(ip);
					cmdResult.setCommand("------");
					cmdResult.setResult("ִ������ʧ��!");
					list.add(cmdResult);
					break;
				}
				if(isSucess){
				cmdResult.setIp(ip);
				cmdResult.setCommand(command[i]);
				cmdResult.setResult("ִ�гɹ�!");
				}
				list.add(cmdResult);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnect();
	}
	
	/**
	 * �ֹ�����SLA����
	 * @param enPasswd
	 * @param command
	 * @param list
	 * @param ip
	 */
	public void getSlaCommantValue(String enPasswd,String[] command,List<CmdResult> list,String ip,User operator,CiscoSlaCfgCmdFile slaconfig,Hashtable slaParamHash)
	{
		String result="";
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		sendCommand(enPasswd);
		
		
		try {
			//for (int i = 0; i < command.length; i++) {
				boolean isSucess=true;
				CmdResult cmdResult=new CmdResult();
				write("config");
					HaweitelnetconfDao dao = new HaweitelnetconfDao();
					Huaweitelnetconf vo = null;
					SlaNodePropDao nodepropdao = new SlaNodePropDao();
					try{
						vo = (Huaweitelnetconf)dao.loadByIp(ip);
						
						int nextentry = 0;
						try{
							nextentry = nodepropdao.getNextEntryNumberByNodeId(vo.getId());
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							//nodepropdao.close();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						if("tcpconnect-noresponder".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[6];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type tcpConnect dest-ipaddr "+(String)slaParamHash.get("tcpconnectnoresponder_destip")+" dest-port "+(String)slaParamHash.get("tcpconnectnoresponder_destport")+" control disable";
							command[3] = "rtr sch "+nextentry+" life forever start-time now";
							command[4] = "exit";
							command[5] = "write";
						}else if("icmp".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[8];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type echo protocol ipicmpecho "+(String)slaParamHash.get("icmp_destip");
							command[3] = "request-data-size "+(String)slaParamHash.get("icmp_datapacket");
							command[4] = "tos "+(String)slaParamHash.get("icmp_tos");
							command[5] = "rtr sch "+nextentry+" life forever start-time now";
							command[6] = "exit";
							command[7] = "write";
						}else if("icmppath".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[10];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type pathEcho protocol ipicmpEcho "+(String)slaParamHash.get("icmppath_destip");
							command[3] = "frequency  "+(String)slaParamHash.get("icmppath_rate");
							command[4] = "lives-of-history-kept  "+(String)slaParamHash.get("icmppath_history");
							command[5] = "buckets-of-history-kept  "+(String)slaParamHash.get("icmppath_buckets");
							command[6] = "filter-for-history all ";
							command[7] = "rtr sch "+nextentry+" life "+(String)slaParamHash.get("icmppath_life")+" start-time now";
							command[8] = "exit";
							command[9] = "write";
						}else if("udp".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[6];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type udpEcho dest-ipaddr "+(String)slaParamHash.get("udp_destip")+" dest-port "+(String)slaParamHash.get("udp_destport")+" control disable";
							command[3] = "rtr sch "+nextentry+" life forever start-time now";
							command[4] = "exit";
							command[5] = "write";
						}else if("tcpconnectwithresponder".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[7];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type tcpConnect dest-ipaddr "+(String)slaParamHash.get("tcpconnectwithresponder_destip")+" dest-port "+(String)slaParamHash.get("tcpconnectwithresponder_destport")+" control disable";
							command[3] = "tos "+(String)slaParamHash.get("tcpconnectwithresponder_tos");
							command[4] = "rtr sch "+nextentry+" life forever start-time now";
							command[5] = "exit";
							command[6] = "write";
						}else if("jitter".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[6];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type jitter dest-ipaddr "+(String)slaParamHash.get("jitter_destip")+" dest-port "+(String)slaParamHash.get("jitter_destport")
								+" num-packets "+(String)slaParamHash.get("jitter_numpacket")+" interval "+(String)slaParamHash.get("jitter_interval");
							command[3] = "rtr sch "+nextentry+" life forever start-time now";
							command[4] = "exit";
							command[5] = "write";
						}else if("http".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[6];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type http operation get url "+(String)slaParamHash.get("http_urlconnect");
							command[3] = "rtr sch "+nextentry+" life forever start-time now";
							command[4] = "exit";
							command[5] = "write";
						}else if("dns".equalsIgnoreCase(slaconfig.getSlatype())){
							command = new String[6];
							command[0] = "config";
							command[1] = "rtr "+nextentry;
							command[2] = "type dns target-addr "+(String)slaParamHash.get("dns_destip")+" name-server "+(String)slaParamHash.get("dns_dnsserver");
							command[3] = "rtr sch "+nextentry+" life forever start-time now";
							command[4] = "exit";
							command[5] = "write";
						}
						result= readUntil(prompt,command);
						
						
						SlaAudit slaaudit = new SlaAudit();
						slaaudit.setUserid(operator.getId());
						slaaudit.setSlatype(slaconfig.getSlatype());
						slaaudit.setTelnetconfigid(vo.getId());
						slaaudit.setOperation("add");
						slaaudit.setDotime(sdf.format(date));
						String content = "";
						for(int i=0;i<command.length;i++){
							String temp = command[i];
							content = content + temp +"\r\n";
						}
						slaaudit.setCmdcontent(content);
						if("0".equals(result) || result.contains("error")||result.contains("user or password error")){
							//���ɹ�
							slaaudit.setDostatus(0);
							cmdResult=new CmdResult();
							cmdResult.setIp(ip);
							cmdResult.setCommand("-------");
							cmdResult.setResult("����ʧ��!");
							cmdResult.setTime(sdf.format(date));
							list.add(cmdResult);
						}else {
							slaaudit.setDostatus(1);
							cmdResult=new CmdResult();
							cmdResult.setIp(ip);
							cmdResult.setCommand("-------");
							cmdResult.setResult("ִ�гɹ�!");
							cmdResult.setTime(sdf.format(date));
							list.add(cmdResult);
						}
						SlaAuditDao slaauditdao = new SlaAuditDao();
						try{
							slaauditdao.save(slaaudit);
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							slaauditdao.close();
						}	
						//}

						//��ִ�гɹ����豸����SLA�ڵ����Ա�					
						
						SlaNodeProp slanodeprop = new SlaNodeProp();
						slanodeprop.setTelnetconfigid(vo.getId());
						slanodeprop.setCreatetime(sdf.format(date));
						slanodeprop.setBak(vo.getDeviceRender());//�ݴ��豸����
						slanodeprop.setOperatorid(operator.getId());
						slanodeprop.setEntrynumber(nextentry);
						slanodeprop.setSlatype(slaconfig.getSlatype());
						nodepropdao.save(slanodeprop);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						dao.close();
						nodepropdao.close();
					}

				
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnect();
	}
	
	/**
	 * �ֹ�ȡ��SLA����
	 * @param enPasswd
	 * @param command
	 * @param list
	 * @param ip
	 */
	public void cancelSlaCommantValue(String enPasswd,List<CmdResult> list,Huaweitelnetconf telnetconfig,User operator,SlaNodeProp slanodeprop)
	{
		String[] command = null;
		setPrompt("Password:");
		sendCommand("en");
		setPrompt("#");
		sendCommand(enPasswd);
		String result="";
		try {
			//for (int i = 0; i < command.length; i++) {
				boolean isSucess=true;
				CmdResult cmdResult=new CmdResult();
				write("config");
					HaweitelnetconfDao dao = new HaweitelnetconfDao();
					Huaweitelnetconf vo = null;
					SlaNodePropDao nodepropdao = new SlaNodePropDao();
					try{
						vo = telnetconfig;
						
						int nextentry = 0;
//						try{
//							nextentry = nodepropdao.getNextEntryNumberByNodeId(vo.getId());
//						}catch(Exception e){
//							e.printStackTrace();
//						}finally{
//							//nodepropdao.close();
//						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						if("tcpconnect-noresponder".equalsIgnoreCase(slanodeprop.getSlatype())){
							command = new String[4];
							command[0] = "config";
							command[1] = "no rtr "+slanodeprop.getEntrynumber();
							command[2] = "exit";
							command[3] = "write";							
							result= readSLAUntil(prompt,command);
							//SysLogger.info("result==============="+result);
							for(int k=0;k<command.length;k++){
								cmdResult=new CmdResult();
								cmdResult.setIp(telnetconfig.getIpaddress());
								cmdResult.setCommand(command[k]);
								cmdResult.setResult("ִ�гɹ�!");
								list.add(cmdResult);
							}
							SlaAudit slaaudit = new SlaAudit();
							slaaudit.setUserid(operator.getId());
							slaaudit.setSlatype(slanodeprop.getSlatype());
							slaaudit.setTelnetconfigid(vo.getId());
							slaaudit.setOperation("delete");
							slaaudit.setDotime(sdf.format(date));
							String content = "";
							for(int i=0;i<command.length;i++){
								String temp = command[i];
								content = content + temp +"\r\n";
							}
							slaaudit.setCmdcontent(content);
							
							if("0".equals(result) || result.contains("error")){
								//���ɹ�
								slaaudit.setDostatus(0);
							}else{
								//�ɹ�
								slaaudit.setDostatus(1);
							}
							SlaAuditDao slaauditdao = new SlaAuditDao();
							try{
								slaauditdao.save(slaaudit);
							}catch(Exception e){
								e.printStackTrace();
							}finally{
								slaauditdao.close();
							}
						}

//						//��ִ�гɹ����豸����SLA�ڵ����Ա�					
//						
//						SlaNodeProp slanodeprop = new SlaNodeProp();
//						slanodeprop.setTelnetconfigid(vo.getId());
//						slanodeprop.setCreatetime(sdf.format(date));
//						slanodeprop.setBak("");
//						slanodeprop.setOperatorid(operator.getId());
//						slanodeprop.setEntrynumber(nextentry);
//						slanodeprop.setSlatype(slaconfig.getSlatype());
						String[] ids = new String[1];
						ids[0] = slanodeprop.getId()+"";
						nodepropdao.delete(ids);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						dao.close();
						nodepropdao.close();
					}

				result=readUntil(prompt);
				if (null != result && !result.equals("user or password error")) {
//					String[] st = result.split("\r\n");
//					for (int j = 0; j < st.length; j++) { 
//						 if(!st[j].contains("--More--"))
//						{
//							 if (st[j].indexOf("% Ambiguous command:") > -1
//										|| st[j].indexOf("% Unknown command or computer name, or unable to find computer address") > -1
//										|| st[j].indexOf("% Invalid input detected at '^' marker.") > -1
//										|| st[j].indexOf("% Unrecognized host or address") > -1) {
//									cmdResult.setIp(ip);
//									cmdResult.setCommand("");
//									cmdResult.setResult("ִ��ʧ��!");
//									isSucess=false;
//								} 
//						}
//					}
//					
//					
				}else {
//					cmdResult.setIp(ip);
//					cmdResult.setCommand("------");
//					cmdResult.setResult("ִ������ʧ��!");
//					list.add(cmdResult);
					//break;
				}
//				if(isSucess){
//				cmdResult.setIp(ip);
//				cmdResult.setCommand("");
//				cmdResult.setResult("ִ�гɹ�!");
//				}
//				list.add(cmdResult);
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		disconnect();
	}
	
	public void setupCfg(String enPasswd)
	{
		setPrompt("Password:");
		sendCommand("en");
		disconnect();
	}
	public boolean modifyPasswd(String enPasswd,String newUser,String newPasswd)
	{
		boolean isSuccess = false;
		try{
		String temp = null;
		setPrompt("Password:");
		temp =sendCommand("en");
		if(!isContainInvalidateWords(temp))
		{
			setPrompt("#");
			temp = sendCommand(enPasswd);
			if(!isContainInvalidateWords(temp))
			{
				temp = sendCommand("conf t");
				if(!isContainInvalidateWords(temp))
				{
					temp = sendCommand("username "+newUser+" password 0 "+ newPasswd);
					if(!isContainInvalidateWords(temp))
					{
						isSuccess = true;
					}
				}
			}
			
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			disconnect();
		}
		return isSuccess;
	}
	private boolean isContainInvalidateWords(String content)
	{
		boolean isContained = false;
		if(content.contains("invalid") || content.contains("Unknown"))
		{
			isContained = true;
		}
		return isContained;
	}
	public String writeCfgFile(String content)
	{
		return "";
	}
	public static void main(String[] args)
	{
		CiscoTelnet telnet = new CiscoTelnet("172.25.25.240", "1","2");
		if(telnet.login())
		{
			telnet.modifyPasswd("2", "1", "2");
		}
		
		//telnet.getCfg(2+"");
	}
}