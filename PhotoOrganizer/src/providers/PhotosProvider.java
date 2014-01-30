package providers;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import models.Photo;
import models.Place;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotosProvider {
	public static final String TABLE_PHOTO = "tbl_photos";
	public static final String COL_PHOTO_NAME = "name";
	public static final String COL_PHOTO_TAGS = "tags";
	public static final String COL_PHOTO_AUTHER = "auther";
	public static final String COL_PHOTO_DATE = "date";
	public static final String COL_PHOTO_PATH = "path";
	public static final String COL_PHOTO_PLACEID = "placeId";
	public static final String COL_PHOTO_DESCR = "descr";
	// columns of the table, used many times
	public static String[] columns_tbl_photo = new String[] { "_id",
			COL_PHOTO_NAME, COL_PHOTO_TAGS, COL_PHOTO_AUTHER, COL_PHOTO_DATE,
			COL_PHOTO_PATH, COL_PHOTO_PLACEID, COL_PHOTO_DESCR };
	//
	static Context context;

	// using activity context
	public static void setContext(Context context) {
		PhotosProvider.context = context;
	}

	public static long save(Context context, Photo photo) {
		if (photo.place != null)
			photo.placeId = PlacesProvider.save(context, photo.place);
		else
			photo.placeId = 0;
		ContentValues cv = getFromPhoto(photo);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.insert(TABLE_PHOTO, null, cv);
	}

	public static boolean delete(Context context, Photo photo) {
		if (photo == null)
			return false;
			PlacesProvider.deleteIfWithoutPhoto(context, photo.place);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.delete(TABLE_PHOTO, "_id = " + photo.id, null) > 0;
	}

	public static boolean deleteNonExisting(Context context, Photo photo) {
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.delete(TABLE_PHOTO, "path = ''", null) > 0;
	}

	public static boolean update(Context context, Photo photo) {
		boolean updated = false;
		if (photo.place != null) {
			if (photo.place.id == 0)
				photo.place.id = PlacesProvider.save(context, photo.place);
			else
				updated = PlacesProvider.update(context, photo.place);
			photo.placeId = photo.place.id;

		} else
			photo.placeId = 0;
		ContentValues cv = getFromPhoto(photo);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.update(TABLE_PHOTO, cv, "_id = " + photo.id, null) < 0 ? false
				: true;
	}

	public static ArrayList<Photo> selectAllWithTag(Context context, String tag)
			throws ParseException {
		ArrayList<Photo> photosList = new ArrayList<Photo>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PHOTO, columns_tbl_photo, "", null, null, null,
						null);
		if (cursor.moveToFirst()) {
			do {
				Photo photo = getPhotoFromCursor(cursor);
				if (photo.exists() && photo.place.name.length() > 0
						&& photo.place.lat.length() > 0
						&& photo.place.lon.length() > 0
						&& photo.getTags().contains(tag))
					photosList.add(photo);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return photosList;
	}

	public static ArrayList<String> getTags(String tagsLine) {
		if (tagsLine == null || tagsLine.length() < 1)
			return null;
		String[] array = tagsLine.split("\\s+");
		ArrayList<String> returnArray = new ArrayList<String>();
		for (String tag : array)
			returnArray.add(tag);
		return returnArray;
	}

	/**
	 * @return
	 * @throws ParseException
	 */
	public static ArrayList<Photo> selectAllWithPlace(Context context,
			String append) throws ParseException {
		ArrayList<Photo> photosList = new ArrayList<Photo>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PHOTO, columns_tbl_photo, append, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				Photo photo = getPhotoFromCursor(cursor);
				if (photo.exists() && photo.place.name.length() > 0
						&& photo.place.lat.length() > 0
						&& photo.place.lon.length() > 0)
					photosList.add(photo);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return photosList;
	}

	public static ArrayList<Photo> selectAll(Context context, String append)
			throws ParseException {
		ArrayList<Photo> photosList = new ArrayList<Photo>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PHOTO, columns_tbl_photo, append, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				Photo photo = getPhotoFromCursor(cursor);
				if (photo.exists())
					photosList.add(photo);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return photosList;
	}

	public static ArrayList<Photo> selectAllNotExisting(Context context,
			String append) throws ParseException {
		ArrayList<Photo> photosList = new ArrayList<Photo>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PHOTO, columns_tbl_photo, append, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				Photo photo = getPhotoFromCursor(cursor);
				if (!photo.exists())
					photosList.add(photo);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return photosList;
	}

	public static Photo getOne(Context context, String append) {
		
		Photo photo = null;
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_PHOTO, columns_tbl_photo, append, null, null,
						null, null);

		if (cursor.moveToFirst()) {
			photo = getPhotoFromCursor(cursor);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return photo;
	}

	public static Photo getOne(Context context, File file) {
		return getOne(context, "name =  '" + file.getName() + "'");
	}

	public static Photo getByName(Context context, String name) {
		return getOne(context, "name =  '" + name + "'");
	}

	public static Photo getById(Context context, int id) {
		return getOne(context, "id =  " + id);
	}

	public static Photo getPhotoFromCursor(Cursor cursor) {
		Place place = PlacesProvider.getOne(context,
				"_id = " + cursor.getString(6));
		return new Photo(cursor.getInt(0), cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getString(4),
				cursor.getString(5), place, cursor.getString(7));
	}

	public static ContentValues getFromPhoto(Photo photo) {
		ContentValues cv = new ContentValues(7);
		cv.put(COL_PHOTO_NAME, photo.name);
		cv.put(COL_PHOTO_TAGS, photo.tags);
		cv.put(COL_PHOTO_AUTHER, photo.auther);
		cv.put(COL_PHOTO_DATE, photo.date);
		cv.put(COL_PHOTO_PATH, photo.path);
		cv.put(COL_PHOTO_PLACEID, photo.placeId);
		cv.put(COL_PHOTO_DESCR, photo.descr);
		return cv;
	}

	public static ArrayList<Photo> getByPlace(Context context, Place p) {
		try {
			return selectAll(context, "placeId = " + p.id);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
