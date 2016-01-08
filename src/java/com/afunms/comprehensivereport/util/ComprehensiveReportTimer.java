package com.afunms.comprehensivereport.util;

import java.util.Timer;

public class ComprehensiveReportTimer{

	public static void startupTimer() {
		Timer timer = new Timer("ComprehensiveReportTask", true);
		timer.schedule(new ComprehensiveReportTask(), 0, 1000 * 60 * 60);
	}
}
