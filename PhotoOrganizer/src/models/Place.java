package models;

import java.io.Serializable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Serializable {

	// public
	public String name, lat, lon, descr;
	public long id;
	//
	static Context context;
	static SQLiteDatabase db;

	public Place(int id, String name, String lat, String lon, String descr) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.id = id;
		this.descr = descr;
	}

	public Place(String name, String lat, String lon, String descr) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.descr = descr;
	}

	public Place(String name, String lat, String lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	// using activity context
	public Place create(String name, String lat, String lon, String descr) {
		if (name.length() < 1)
			return null;
		this.name = name;
		this.lat = descr;
		this.lon = descr;
		this.descr = descr;
		return this;
	}

	public Place() {
	};

}
