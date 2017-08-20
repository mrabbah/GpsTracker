package com.rabbahsoft.mobile.gpstracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.Account;
import com.google.android.gms.common.api.GoogleApiClient;

public class GpsReadingService extends Service implements 
ConnectionCallbacks,
OnConnectionFailedListener {

	private GoogleApiClient mGoogleApiClient;
	//private boolean currentlyProcessingLocation = false;
	private static final String TAG = "GpsReadingService";

	//private int failCompteur;

	public GpsReadingService() {
		Log.i("info", "appel constructeur gps reading service");
		//failCompteur = 0;
	}

	/*
	 * Called befor service onStart method is called.All Initialization part
	 * goes here
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("info", "GPS SERVICE ON CREATE");
		
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// if we are currently trying to get a location and the alarm manager has called this again,
		// no need to start processing a new location.
		Log.i("info", "GPS SERVICE ON START COMMAND "/* + currentlyProcessingLocation*/);
		buildGoogleApiClient();

		return START_NOT_STICKY;
	}
	
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}

	


	/*@Override
	public void onStart(Intent intent, int startId) {
		int start = Service.START_STICKY;		
	}*/

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * Overriden method of the interface
	 * GooglePlayServicesClient.OnConnectionFailedListener . called when
	 * connection to the Google Play Service are not able to connect
	 */

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.i("info", "GPS SERVICE ON CONNECTION FAILED");
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		try {
			if (connectionResult.hasResolution()) {
				Toast.makeText(this, "Veillez vérifier votre connection internet", Toast.LENGTH_LONG)
				.show();
				//try {
				// Start an Activity that tries to resolve the error
				Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				this.startActivity(i);
				/*connectionResult.startResolutionForResult(
							,
							CommonUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);*/

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

				/*} catch (IntentSender.SendIntentException e) {
					// Log the error
					Log.e("error", e.getMessage());
				}*/

			} else {
				// If no resolution is available, display a dialog to the user
				// with
				// the error.
				Log.i("info", "No resolution is available");
			}
			
		} catch (Exception ex) {
			Log.e("error", ex.getMessage());
		}

	}

	/*
	 * This is overriden method of interface
	 * GooglePlayServicesClient.ConnectionCallbacks which is called when
	 * locationClient is connecte to google service. You can receive GPS reading
	 * only when this method is called.So request for location updates from this
	 * method rather than onStart()
	 */
	@Override
	public void onConnected(Bundle arg0) {
		Log.i("info", "GPS SERVICE ON CONNECTED");
		try {
			Log.i("info", "Location Client is Connected");
			
			Location location = LocationServices.FusedLocationApi.getLastLocation(
	                mGoogleApiClient);
	        if (location != null) {
	        	Log.i(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
	        	if (location.getAccuracy() < CommonUtils.PRECISION) {
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					double speed = location.getSpeed();
					double degree = location.getBearing();
					double precision = location.getAccuracy();
					//stopLocationUpdates();
					saveLocationDataToDb(latitude, longitude, speed, degree, precision);
				}
	        }
			
			
			Log.i("info", "Service Connect status :: " + isServicesConnected());
		} catch(Exception ex) {
			Log.e("error", ex.getMessage());
		}

	}

	
	
	private void saveLocationDataToDb(double latitude, double longitude, double speed, double degree, double precision) {
		Log.i("info", "GPS SERVICE SAVE LOCATION TO DB");
		try {
			/**
			 * Get phone number and id
			 */
			TelephonyManager tMgr = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			String phoneId = tMgr.getDeviceId();
			CellLocation cl = tMgr.getCellLocation();
			String cellLocation = "";
			if(cl != null) {
				cellLocation = cl.toString();
			}
			String netOperator = tMgr.getNetworkOperator();
			String netOperatorName = tMgr.getNetworkOperatorName();
			String latLng = latitude + ", " + longitude;
			GeoDataSource dataSource = GeoDataSource.getInstance(this);
			dataSource.open();
			dataSource.create(latLng, Double.toString(speed), Double.toString(degree), Double.toString(precision), cellLocation, netOperator, netOperatorName, phoneId);
			dataSource.close();
			/*new PostDataTask().execute(CommonUtils.TRACK_URL, latLng,
					cellLocation, netOperator, netOperatorName, phoneId);*/

		} catch (Exception ex) {
			Log.e("error", "Erreur lors de l envoi " + ex);
		}
	}

	/*private void checkConnectionError() {
		try {
			if(failCompteur>20) {
				//Toast.makeText(this, "Veillez v�rifier votre connection internet", Toast.LENGTH_LONG)
				//.show();
				Log.i("info", "lunching wireless settings");
				Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);                
			}
		} catch(Exception ex) {
			Log.e("error", ex.getMessage());
		}
	}*/

	/** Initiates the fetch operation. */
	/*private String loadFromNetwork(String urlString) throws IOException {
		InputStream stream = null;
		String str = "";

		try {
			stream = downloadUrl(urlString);
			str = readIt(stream, 0500);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return str;
	}*/

	/**
	 * Given a string representation of a URL, sets up a connection and gets an
	 * input stream.
	 * 
	 * @param urlString
	 *            A string representation of a URL.
	 * @return An InputStream retrieved from a successful HttpURLConnection.
	 * @throws java.io.IOException
	 */
	/*private InputStream downloadUrl(String urlString) throws IOException {
		// BEGIN_INCLUDE(get_inputstream)
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 );
		conn.setConnectTimeout(15000 );
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Start the query
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
		// END_INCLUDE(get_inputstream)
	}*/

	/**
	 * Reads an InputStream and converts it to a String.
	 * 
	 * @param stream
	 *            InputStream containing HTML from targeted site.
	 * @param len
	 *            Length of string that this method returns.
	 * @return String concatenated according to len parameter.
	 * @throws java.io.IOException
	 * @throws java.io.UnsupportedEncodingException
	 */
	/*private String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}*/

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean isServicesConnected() {
		try {
			// Check that Google Play services is available
			int resultCode = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(GpsReadingService.this);

			// If Google Play services is available
			if (ConnectionResult.SUCCESS == resultCode) {
				return true;
			} else {
				return false;
			}
		} catch(Exception ex) {
			Log.e("error", ex.getMessage());
			return false;
		}		
	}

	/*
	 * Called when Sevice running in backgroung is stopped. Remove location
	 * upadate to stop receiving gps reading
	 */
	@Override
	public void onDestroy() {
		Log.i("info", "GPS SERVICE CALL DESTROY");
		
		super.onDestroy();
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}


}