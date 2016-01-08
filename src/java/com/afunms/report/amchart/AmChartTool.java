package com.afunms.report.amchart;

import java.util.List;
import java.util.Vector;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Sep 30, 2011 2:31:41 PM
 * 类说明
 */
public class AmChartTool {
	/**
	 * 组合amcharts数据格式
	 * @param dataList
	 * @param ipList
	 * @return
	 */
	public String makeAmChartData(List dataList,List ipList){
		StringBuffer sb = new StringBuffer();
		String data = "";
		if (dataList != null && dataList.size() > 0) {
			sb.append("<chart><series>");
			List eachDataList = (List) dataList.get(0);
			for (int k = 0; k < eachDataList.size(); k++) {
				Vector v = new Vector();
				v = (Vector) eachDataList.get(k);
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(v.get(1));
				sb.append("</value>");

			}
			sb.append("</series><graphs>");
			for (int j = 0; j < dataList.size(); j++) {
				List dataList1 = (List) dataList.get(j);
				sb.append("<graph title='" + (String) ipList.get(j) + "' bullet='round_outlined' bullet_size='4'>");
				if (dataList1 != null && dataList1.size() > 0) {
					for (int m = 0; m < dataList1.size(); m++) {
						Vector v = new Vector();
						v = (Vector) dataList1.get(m);
						sb.append("<value xid='");
						sb.append(m);
						sb.append("'>");
						sb.append(v.get(0));
						sb.append("</value>");
					}
				}
				sb.append("</graph>");
			}
			sb.append("</graphs></chart>");
			data = sb.toString();

		} else {
			data = "0";
		}
		return data;
	}
}
