package com.rabbahsoft.mobile.gpstracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeoOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	
    public static final String GEO_TABLE_NAME = "geodata";
    
    private static final String GEO_TABLE_CREATE =
            "CREATE TABLE " + GEO_TABLE_NAME + " (" +
            	  "	id integer primary key autoincrement, lat_lng  TEXT, " +
            	"	cell_location  TEXT, net_operator TEXT, net_operator_name TEXT, phone_id TEXT, date INTEGER, speed TEXT, degree TEXT, precision TEXT);";
    
	GeoOpenHelper(Context context) {
        super(context, GEO_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GEO_TABLE_CREATE);
        Log.i("info", "base de donnee creer avec sucees");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(GeoOpenHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + GEO_TABLE_NAME);
		    onCreate(db);
	}
}
