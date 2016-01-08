package com.afunms.alarm.send;

import java.io.*; 
import java.net.*; 
import java.text.SimpleDateFormat;
import java.util.Scanner;

import com.database.config.SystemConfig;

public class SmsSocketClient {


	public String sendMsg(String smscenter,String phone){
	
		String result="";
		
		try{
			//System.out.println("111111");
			// new Socket() ���ӵ�ָ���ķ������˿ڣ���ǰ�õ��Ǳ����Ķ˿� 
			
			String  ip=SystemConfig.getConfigInfomation("smsconfig", "IPaddress");
			int  port=Integer.parseInt(SystemConfig.getConfigInfomation("smsconfig", "Port"));
			Socket s = new Socket(ip, port);
			//����s�������ӵ��˷�����
			//s����Է�����������
				
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();
            String sTxt = this.getStr(smscenter, phone);
            //System.out.println(sTxt);
//            System.out.println(sTxt1);
			out.write(sTxt.getBytes("gbk"));
			out.flush();	//�����壬ȷ�����͵������
			Scanner sc = new Scanner(in);	
			result = sc.nextLine();		
			//System.out.println(str);	//�Ѵӷ��������ص���Ϣ����ӡ������̨��
			out.flush();
			out.close(); 
			in.close(); 
			s.close(); 
			
			return result;
			
		}
		catch (Exception e){
			System.out.println("sendMsg.err:"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 
	 * ���Ͷ��ŵĸ�ʽ
	 * 
	 * @param smscenter ��������
	 * @param phone �绰����
	 * @return
	 */
	public String getStr(String smscenter,String phone){
		
        StringBuffer str = new StringBuffer();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date =  new java.util.Date();
        String time = sdf.format(date);
        str.append("EMPI041200000100D11"+time+"                                             ");
        str.append("                                    ");
        str.append(phone);
        for(int i=0;i<51-phone.length();i++){
            str.append(" ");
        }
        str.append(smscenter);
        for(int j=0; j<255-smscenter.trim().getBytes().length;j++){
        	str.append(" ");
        }
        String st = str.toString();
        return st; 
	}

	
}