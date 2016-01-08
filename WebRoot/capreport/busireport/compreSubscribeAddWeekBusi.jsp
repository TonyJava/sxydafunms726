<%@page language="java" contentType="text/html;charset=gb2312"%>
<html>
	<head></head>
	<body>
		<table cellpadding="0" cellspacing="0" bgcolor="#FFFFFF" border=1>
			<tr>
				<td width="100%" align=left>
					<div id="editmodel" style="display: none">
						<table border="0" id="table1" cellpadding="0" width="100%">
							<tr style="background-color: #FFFFFF;">
								<TD nowrap align="right" height="24" width="10%">
									报表名称&nbsp;
								</TD>
								<TD nowrap width="40%">
									&nbsp;
									<input type="text" name="report_name" id="report_name"
										size="50" class="formStyle" size="50">
									<font color='red'>*</font>
								</TD>
							</tr>
							<tr style="background-color: #ECECEC;">
								<TD nowrap align="right" height="24" width="10%">
									报表接收人&nbsp;
								</TD>
								<TD nowrap width="40%">
									&nbsp;
									<input type="text" name="recievers_name" id="recievers_name"
										size="50" class="formStyle" size="50" readonly>
									<input type="button" value="设置报表接收人"
										onclick="setReciever('recievers_name','recievers_id');" />
									<input type="hidden" id="recievers_id" name="recievers_id"
										value="">

									<font color='red'>*</font>
								</TD>
							</tr>
							<tr>
								<TD nowrap align="right" height="24" width="10%">
									邮件标题:&nbsp;
								</TD>
								<TD nowrap width="40%">
									&nbsp;
									<input type="text" name="tile" id="tile" size="50"
										class="formStyle">
								</TD>
							</tr>
							<tr style="background-color: #ECECEC;">
								<TD nowrap align="right" height="24" width="10%">
									邮件描述:&nbsp;
								</TD>
								<TD nowrap width="40%">
									&nbsp;
									<input type="text" name="desc" id="desc" size="50"
										class="formStyle">
								</TD>
							</tr>
							<tr>
								<td nowrap align="right" height="24" width="10%">
									报表所包含设备&nbsp;
								</td>
								<TD nowrap width="40%">
									&nbsp;
									<input type="text" name="business_name" id="business_name"
										size="50" class="formStyle" size="50" readonly>
									<input type="button" value="设置报表所包含设备"
										onclick="setDevices('business_name','business_id');" />
									<input type="hidden" id="business_id" name="business_id"
										value="">

									<font color='red'>*</font>
								</TD>
							</tr>
							<tr style="background-color: #ECECEC;">
								<TD nowrap align="right" height="24" width="10%">
									生成报表类型:&nbsp;
								</TD>
								<TD nowrap width="40%">
									&nbsp;
									<SELECT id="exporttype" name="exporttype">
										<OPTION value="xls" selected>
											Excel
										</OPTION>
										<OPTION value="doc">
											Word
										</OPTION>
										<OPTION value="pdf">
											Pdf
										</OPTION>
									</SELECT>
								</TD>
							</tr>
							<tr>
								<TD nowrap align="right" height="24">
									日报报表生成时间:&nbsp;
								</TD>
								<td nowrap colspan="3">
									<table>
										<tr>
											<td>
												<SELECT style="WIDTH: 250px" id=sendtimeweek multiple
													size=5 name=sendtimeweek>
													<OPTION value=0>
														星期天
													</OPTION>
													<OPTION selected value=1>
														星期一
													</OPTION>
													<OPTION value=2>
														星期二
													</OPTION>
													<OPTION value=3>
														星期三
													</OPTION>
													<OPTION value=4>
														星期四
													</OPTION>
													<OPTION value=5>
														星期五
													</OPTION>
													<OPTION value=6>
														星期六
													</OPTION>
												</SELECT>
											</td>
											<td>
												<SELECT style="WIDTH: 250px" id=sendtimehou multiple
													size=5 name=sendtimehou>
													<OPTION value=00>
														00AM
													</OPTION>
													<OPTION selected value=01>
														01AM
													</OPTION>
													<OPTION value=02>
														02AM
													</OPTION>
													<OPTION value=03>
														03AM
													</OPTION>
													<OPTION value=04>
														04AM
													</OPTION>
													<OPTION value=05>
														05AM
													</OPTION>
													<OPTION value=06>
														06AM
													</OPTION>
													<OPTION value=07>
														07AM
													</OPTION>
													<OPTION value=08>
														08AM
													</OPTION>
													<OPTION value=09>
														09AM
													</OPTION>
													<OPTION value=10>
														10AM
													</OPTION>
													<OPTION value=11>
														11AM
													</OPTION>
													<OPTION value=12>
														12AM
													</OPTION>
													<OPTION value=13>
														01PM
													</OPTION>
													<OPTION value=14>
														02PM
													</OPTION>
													<OPTION value=15>
														03PM
													</OPTION>
													<OPTION value=16>
														04PM
													</OPTION>
													<OPTION value=17>
														05PM
													</OPTION>
													<OPTION value=18>
														06PM
													</OPTION>
													<OPTION value=19>
														07PM
													</OPTION>
													<OPTION value=20>
														08PM
													</OPTION>
													<OPTION value=21>
														09PM
													</OPTION>
													<OPTION value=22>
														10PM
													</OPTION>
													<OPTION value=23>
														11PM
													</OPTION>
												</SELECT>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr  style="background-color: #ECECEC;">
								<TD nowrap colspan="4" align=center>
									<br>
									<input type="button" value="保存" style="width: 50"
										class="formStylebutton" id="saveBtn"">
									&nbsp;&nbsp;
									<input type=reset class="formStylebutton" style="width: 60"
										value=" 隐藏模板 " onclick="hiddenModel()">
								</TD>
							</tr>
						</TABLE>
					</div>
				</td>
			</tr>
		</table>
	</body>
</html>