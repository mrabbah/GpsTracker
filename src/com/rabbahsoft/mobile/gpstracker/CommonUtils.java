package com.rabbahsoft.mobile.gpstracker;

public class CommonUtils {

	public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000;
	public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = 30000;
	public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 30000;
	public static final int ALARM_MANAGER_INTERVAL = 1 * 60 * 1000; //Une minutes
	public static final float PRECISION = 100.0f;
	//37.187.218.29
	public static final String TRACK_URL = "http://expressecourse.ma/mobile/actualiserMaPosition";

}
