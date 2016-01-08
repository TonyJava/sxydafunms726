package com.afunms.comprehensivereport.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Collections_sort {

	/**
	 * HashMap 排序
	 * @author Administrator
	 *
	 */
		public static void main(String[] args){
			Map<String, Integer> map = new HashMap<String, Integer>();
//			=
//			=
//			=
//			=
//			10.24.63.8=1.33157894736842
//			=
//			10.24.129.40=1.27567567567568
//			10.24.63.13=1.51322751322751
//			10.24.63.10=0.789189189189189
//			10.24.63.9=1.08938547486034
//			10.24.128.22=2.0
//			10.24.63.5=1.12041884816754
//			10.24.63.17=0.0

			
			
			   System.out.println("四舍五入取整:(2)=" + new BigDecimal("2").setScale(0, BigDecimal.ROUND_HALF_UP)); 
			    System.out.println("四舍五入取整:(2.1)=" + new BigDecimal("2.1").setScale(0, BigDecimal.ROUND_HALF_UP)); 
			    System.out.println("四舍五入取整:(2.5)=" + new BigDecimal("2.5").setScale(0, BigDecimal.ROUND_HALF_UP)); 
			    System.out.println("四舍五入取整:(2.9)=" + new BigDecimal("2.9").setScale(0, BigDecimal.ROUND_HALF_UP)); 

			
			
			System.out.println("==11=="+(Double.valueOf("2.74489795918367")).intValue());
			map.put("10.24.63.12", (new BigDecimal("1.6031746031746").setScale(0, BigDecimal.ROUND_HALF_UP)).intValue());
			map.put("10.24.63.20", new BigDecimal("0.282722513089005").setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			map.put("10.24.63.16", new BigDecimal("38.1578947368421").setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			map.put("10.24.128.17", new BigDecimal("5.94240837696335").setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			map.put("10.24.63.1", new BigDecimal("2.74869109947644").setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
			List <Map.Entry<String, Integer>> infoIds = 
				new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
			
			//排序前
			for(int i=0;i<infoIds.size();i++){
				String id = infoIds.get(i).toString();
				System.out.println(id);
			}
//			123.11.2.3=10
//			123.13.2.3=6
//			123.15.2.3=19
//			123.14.2.3=11
//			123.12.2.3=9
			
			//排序
			Collections.sort(infoIds,new Comparator<Map.Entry<String,Integer>>(){
				public int compare(Map.Entry<String, Integer>o1,Map.Entry<String,Integer>o2){
					return (int) (o2.getValue() - o1.getValue()); 
//			        return (o1.getKey()).toString().compareTo(o2.getKey());
			        
				}
			});
			//排序后
			System.out.println("排序后");
			for(int i=0;i<infoIds.size();i++){
				String id = infoIds.get(i).toString();
				
				System.out.println(id);
			}
		}
		
		
	   public void str(Map map)
	   {
		   
//		   System.out.println("排序前--------map；；；；；；");
//			for(int i=0;i<map.size();i++){
//				String id = map.get(i).toString();
//				
//				System.out.println(id);
//			}
//		   
//		   
		   
		   List <Map.Entry<String, Integer>> infoIds = 
				new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
		   
		   Collections.sort(infoIds,new Comparator<Map.Entry<String,Integer>>(){
				public int compare(Map.Entry<String, Integer>o1,Map.Entry<String,Integer>o2){
					return (o2.getValue() - o1.getValue()); 
//			        return (o1.getKey()).toString().compareTo(o2.getKey());
			        
				}
			});
		   
		   
		 //排序后
			System.out.println("排序后——-------infoIds：：：：");
			for(int i=0;i<infoIds.size();i++){
				String id = infoIds.get(i).toString();
				
				System.out.println(id);
			}
		   
		  
		   
	   }
		
		
		
//	public static void s(){
//		Collections.sort(infoId,new Comparator<Map.Entry<String,Double>>(){
//			public int compare(Map.Entry<String, Double>o1,Map.Entry<String,Double>o2){
//				return  (int) (o2.getValue() - o1.getValue()); 
////		        return (o1.getKey()).toString().compareTo(o2.getKey());
//		        
//			}
//		});
//		
//	}
//	
//			//排序后
////			System.out.println("排序后");
////			for(int i=0;i<infoIds.size();i++){
////				String id = infoIds.get(i).toString();
////				
////				System.out.println(id);
////			}println("排序后");
//		for(i//nt i=0;i<.size()infoIds
//			String id =// .get(i).toStriinfoIds			
//			System.out.pr//intln//(id);
//		}
//	}

}
