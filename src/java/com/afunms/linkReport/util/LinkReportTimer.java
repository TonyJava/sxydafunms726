package com.afunms.linkReport.util;

import java.util.Timer;

public class LinkReportTimer {

	public static void startupTimer() {
		Timer timer = new Timer("LinkReportTask", true);
		timer.schedule(new LinkReportTask(), 0, 1000 * 60 * 60);
	}
}
