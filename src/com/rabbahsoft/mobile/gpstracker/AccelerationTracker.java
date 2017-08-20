package com.rabbahsoft.mobile.gpstracker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.LocationClient;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccelerationTracker extends Service implements SensorEventListener {

	private SensorManager mSensorManager;
	private boolean currentlyProcessing = false;
	private static final String TAG = "SpeedTracker";
	private Sensor mSensor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// if we are currently trying to get a location and the alarm manager has called this again,
		// no need to start processing a new location.
		if (!currentlyProcessing) {
			currentlyProcessing = true;
			startTracking();
		}

		return START_NOT_STICKY;
	}

	private void startTracking() {
		Log.d(TAG, "start Tracking Acceleration");

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this,
				mSensor ,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			// Movement
			float x = values[0];
			float y = values[1];
			float z = values[2];
			
			float accelationSquareRoot = (x * x + y * y + z * z)
			        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			
			
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
