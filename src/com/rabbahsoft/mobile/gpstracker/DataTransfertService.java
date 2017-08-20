package com.rabbahsoft.mobile.gpstracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class DataTransfertService extends Service {

	private GeoDataSource dataSource;
	
	/*
	 * Called befor service onStart method is called.All Initialization part
	 * goes here
	 */
	@Override
	public void onCreate() {		
		Log.i("info", "TRANSFERT SERVICE ON CREATE");
		dataSource = GeoDataSource.getInstance(this);
	}
	/*
	 * You need to write the code to be executed on service start. Sometime due
	 * to memory congestion DVM kill the running service but it can be restarted
	 * when the memory is enough to run the service again.
	 */

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("info", "Transfert des positions au serveur");
		int start = Service.START_STICKY;
		dataSource.open();
		List<GeoPosition> geoPositions = dataSource.getAll();
		dataSource.close();
		for (Iterator<GeoPosition> iterator = geoPositions.iterator(); iterator.hasNext();) {
			GeoPosition geoPosition = iterator.next();
			String latLng = geoPosition.getLatLng();
			String cellLocation = geoPosition.getCellLocation();
			String netOperator = geoPosition.getNetOperator();
			String netOperatorName = geoPosition.getNetOperatorName();
			String phoneId = geoPosition.getPhoneId();
			Long date = geoPosition.getDate();
			String speed = geoPosition.getSpeed();
			String degree = geoPosition.getDegree();
			String precision = geoPosition.getPrecision();
			Long id = geoPosition.getId();
			PostDataTask pd = new PostDataTask(dataSource);
			pd.execute(CommonUtils.TRACK_URL, latLng,
					cellLocation, netOperator, netOperatorName, phoneId, Long.toString(date), Long.toString(id), speed, degree, precision);			
		}				
		Log.i("info", "Fin transfert positions");
	}
	
	private class PostDataTask extends AsyncTask<String, Void, String> {
		
		private GeoDataSource dataSource;
		
		public PostDataTask(GeoDataSource dataSource) {
			this.dataSource = dataSource;
		}
		
		@Override
		protected String doInBackground(String... params) {
			Log.i("info", "TRANSFERT DO IN BACKGROUD HTTP POST");
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(params[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						6);
				nameValuePairs.add(new BasicNameValuePair("latLng", params[1]));
				nameValuePairs.add(new BasicNameValuePair("cellLocation",
						params[2]));
				nameValuePairs.add(new BasicNameValuePair("netOperator",
						params[3]));
				nameValuePairs.add(new BasicNameValuePair("netOperatorName",
						params[4]));
				nameValuePairs
						.add(new BasicNameValuePair("phoneId", params[5]));
				nameValuePairs
				.add(new BasicNameValuePair("date", params[6]));
				nameValuePairs
				.add(new BasicNameValuePair("speed", params[8]));
				nameValuePairs
				.add(new BasicNameValuePair("degree", params[9]));
				nameValuePairs
				.add(new BasicNameValuePair("precision", params[10]));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(httppost);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

					Log.i("info", "Server Responded OK");
					dataSource.open();
					dataSource.delete(params[7]);
					dataSource.close();
					//failCompteur = 0;
				} else {
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				//failCompteur++;
				Log.e("error", e.getMessage());
				//checkConnectionError();
			} catch (IOException e) {
				//failCompteur++;
				Log.e("error", e.getMessage());
				//checkConnectionError();
			} catch (Exception ex) {
				//failCompteur++;
				Log.e("error", ex.getMessage());
				//checkConnectionError();
			}
			return "finish send data";
		}
		
		/**
		 * Uses the logging framework to display the output of the fetch
		 * operation in the log fragment.
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.i("info", result);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/*
	 * Called when Sevice running in backgroung is stopped. Remove location
	 * upadate to stop receiving gps reading
	 */
	@Override
	public void onDestroy() {
		Log.i("info", "Service data transfert is destroyed");
		dataSource.close();
		super.onDestroy();
	}

}
