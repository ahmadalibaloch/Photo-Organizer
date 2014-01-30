package models;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import providers.PhotosProvider;

public class Photo implements Serializable{
	public Photo(int id, String name, String tags, String auther, String date,
			String path, Place place, String descr) {
		this.id = id;
		this.name = name;
		this.tags = tags;
		this.auther = auther;
		this.date = date;
		this.path = path;
		this.place = place;
		this.descr = descr;
	}

	public Photo() {
		this.place = new Place();
	};

	// public
	public String name, tags, auther, path, descr, date;
	public long placeId, id;
	public Place place;

	public boolean exists() {
		return new File(path).exists() ? true : false;
	}

	public Date getDateModified() {
		try {
			return new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US)
					.parse(this.date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public ArrayList<String> getTags() {
		return PhotosProvider.getTags(this.tags);
	}

	public String getDateString() {
		try {
			return new SimpleDateFormat("MMM, dd yyyy  hh:mm:ss", Locale.US)
					.format(new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US)
							.parse(this.date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-";
	}
}
