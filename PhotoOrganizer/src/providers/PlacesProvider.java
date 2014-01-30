package providers;

import java.text.ParseException;
import java.util.ArrayList;

import models.Photo;
import models.Place;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PlacesProvider {
	// table places info
	public static final String TABLE_PLACE = "tbl_places";
	public static final String COL_PLACE_NAME = "name";
	public static final String COL_PLACE_LAT = "lat";
	public static final String COL_PLACE_LON = "lon";
	public static final String COL_PLACE_DESCR = "descr";
	// columns of the table, used many times
	public static String[] columns_tbl_place = new String[] { "_id",
			COL_PLACE_NAME, COL_PLACE_LAT, COL_PLACE_LON, COL_PLACE_DESCR };
	static Context context;

	public static long save(Context context, Place place) {
		ContentValues cv = new ContentValues(7);
		cv.put(COL_PLACE_NAME, place.name);
		cv.put(COL_PLACE_LAT, place.lat);
		cv.put(COL_PLACE_LON, place.lon);
		cv.put(COL_PLACE_DESCR, place.descr);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.insert(TABLE_PLACE, null, cv);
	}

	public static boolean update(Context context, Place place) {
		ContentValues cv = new ContentValues(7);
		cv.put(COL_PLACE_NAME, place.name);
		cv.put(COL_PLACE_LAT, place.lat);
		cv.put(COL_PLACE_LON, place.lon);
		cv.put(COL_PLACE_DESCR, place.descr);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.update(TABLE_PLACE, cv, "_id = " + place.id, null) > 0;
	}

	public static boolean delete(Context context, Place place) {
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.delete(TABLE_PLACE, "_id = " + place.id, null) > 0;
	}

	/**
	 * @return
	 * @throws ParseException
	 */
	public static ArrayList<Place> selectAll(Context context, String append)
			throws ParseException {
		ArrayList<Place> placesList = new ArrayList<Place>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PLACE, columns_tbl_place, append, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				placesList.add(new Place(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor
								.getString(4)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return placesList;
	}

	public static Place getOne(Context context, String append) {
		Place place = new Place();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PLACE, columns_tbl_place, append, null, null,
						null, null);

		if (cursor.moveToFirst()) {
			place = new Place(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return place;
	}

	public static Place getOne(Context context, Photo photo) {
		return getOne(context, "WHERE id=" + photo.placeId);
	}

	public static boolean deleteAllWithoutPhoto(Context context2) {
		return DatabaseHelper
				.getInstance(context)
				.getMyWritableDatabase()
				.delete(TABLE_PLACE,
						"_id NOT IN(SELECT placeId FROM tbl_photos)", null) > 0;
	}

	public static boolean deleteIfWithoutPhoto(Context context2, Place place) {
		return DatabaseHelper
				.getInstance(context)
				.getMyWritableDatabase()
				.delete(TABLE_PLACE,
						"_id NOT IN(SELECT placeId FROM tbl_photos) AND _id = "
								+ place.id, null) > 0;
	}
}
