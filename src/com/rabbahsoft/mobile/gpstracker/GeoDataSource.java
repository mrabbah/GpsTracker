package com.rabbahsoft.mobile.gpstracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class GeoDataSource {

	private SQLiteDatabase database;
	private GeoOpenHelper dbHelper;
	private String[] allColumns = {"id", "lat_lng", "cell_location", "net_operator", "net_operator_name", "phone_id", "date", "speed", "degree", "precision"};	
	private static GeoDataSource instance;	
	private int nbAccess = 0;
	
	public static synchronized GeoDataSource getInstance(Context context) {
		if(instance == null) {
			instance = new GeoDataSource(context);			
		}		
		return instance;
	}

	private GeoDataSource(Context context) {
		dbHelper = new GeoOpenHelper(context);		
	}

	public synchronized void open() throws SQLException {
		Log.i("info", "Ouverture de la connexion Db");
		if(nbAccess == 0) {
			nbAccess++;
			database = dbHelper.getWritableDatabase();
		}		
	}

	public synchronized void close() {
		Log.i("info", "fermeture de la conexion Db");
		if(nbAccess == 1) {
			nbAccess--;
			dbHelper.close();
		}		
	}	


	public GeoPosition create(String latLng, String speed, String degree, String precision, String cellLocation, 
			String netOperator, String netOperatorName, String phoneId) {

		ContentValues values = new ContentValues();
		values.put("lat_lng", latLng);
		values.put("speed", speed);
		values.put("degree", degree);
		values.put("precision", precision);
		values.put("cell_location", cellLocation);
		values.put("net_operator", netOperator);
		values.put("net_operator_name", netOperatorName);
		values.put("phone_id", phoneId);
		long time = new Date().getTime();
		values.put("date", time);
		long insertId = database.insert(GeoOpenHelper.GEO_TABLE_NAME, null, values);
		Log.i("info", "insertion d une nouvelle position au niveau Db avec succes");
		Cursor cursor = database.query(GeoOpenHelper.GEO_TABLE_NAME, allColumns, "id = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		GeoPosition geoPosition = cursorToGeoPosition(cursor);
		cursor.close();
		return geoPosition;
	}

	public void delete(GeoPosition geoPosition) {
		Log.i("info", "suppression de la postion id = " + geoPosition.getId());
		long id = geoPosition.getId();
		database.delete(GeoOpenHelper.GEO_TABLE_NAME, " id = " + id, null);	
	}
	
	public void delete(String id) {
		Log.i("info", "suppression de la postion id = " + id);
		database.delete(GeoOpenHelper.GEO_TABLE_NAME, " id = " + id, null);	
	}
	
	public List<GeoPosition> getAll() {		
		List<GeoPosition> geoPositions = new ArrayList<GeoPosition>();
		Cursor cursor = database.query(GeoOpenHelper.GEO_TABLE_NAME,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		int nb = 0;
		while (!cursor.isAfterLast()) {
			nb++;
			GeoPosition geoPosition = cursorToGeoPosition(cursor);
			geoPositions.add(geoPosition);
			cursor.moveToNext();
		}
		Log.i("info", "nombre position au niveau de la BdD = " + nb);
		cursor.close();
		return geoPositions;	
	}

	private GeoPosition cursorToGeoPosition(Cursor cursor) {
		Log.i("info", "La date de l enrigestrement = " + cursor.getDouble(6));
		GeoPosition geoPosition = new GeoPosition();
		geoPosition.setCellLocation(cursor.getString(2));
		geoPosition.setDate(cursor.getLong(6));
		geoPosition.setId(cursor.getLong(0));
		geoPosition.setLatLng(cursor.getString(1));
		geoPosition.setNetOperator(cursor.getString(3));
		geoPosition.setNetOperatorName(cursor.getString(4));
		geoPosition.setPhoneId(cursor.getString(5));
		geoPosition.setSpeed(cursor.getString(7));
		geoPosition.setDegree(cursor.getString(8));
		geoPosition.setPrecision(cursor.getString(9));
		return geoPosition;
	}

	
}
