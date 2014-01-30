package providers;

import java.text.ParseException;
import java.util.ArrayList;

import models.Photo;
import models.Place;
import models.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UsersProvider {
	// table places info
	public static final String TABLE_USER = "tbl_users";
	public static final String COL_USER_NAME = "name";
	public static final String COL_USER_USERNAME = "username";
	public static final String COL_USER_PASSWORD = "password";
	// columns of the table, used many times
	public static String[] columns_tbl_user = new String[] { "_id",
			COL_USER_NAME, COL_USER_USERNAME, COL_USER_PASSWORD };
	static Context context;

	public static long save(Context context, User user) {
		ContentValues cv = new ContentValues(7);
		cv.put(COL_USER_NAME, user.name);
		cv.put(COL_USER_USERNAME, user.username);
		cv.put(COL_USER_PASSWORD, user.passwrod);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.insert(TABLE_USER, null, cv);
	}

	public static boolean update(Context context, User user) {
		ContentValues cv = new ContentValues(7);
		cv.put(COL_USER_NAME, user.name);
		cv.put(COL_USER_USERNAME, user.username);
		cv.put(COL_USER_PASSWORD, user.passwrod);
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.update(TABLE_USER, cv, "_id = " + user.id, null) > 0;
	}

	public static boolean delete(Context context, User user) {
		return DatabaseHelper.getInstance(context).getMyWritableDatabase()
				.delete(TABLE_USER, "_id = " + user.id, null) > 0;
	}

	/**
	 * @return
	 * @throws ParseException
	 */
	public static ArrayList<User> selectAll(Context context, String append)
			throws ParseException {
		ArrayList<User> usersList = new ArrayList<User>();
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_USER, columns_tbl_user, append, null, null, null,
						null);
		if (cursor.moveToFirst()) {
			do {
				usersList.add(new User(cursor.getInt(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return usersList;
	}

	public static User getOne(Context context, String append) {
		User user = null;
		Cursor cursor = DatabaseHelper
				.getInstance(context)
				.getReadableDatabase()
				.query(TABLE_USER, columns_tbl_user, append, null, null, null,
						null);

		if (cursor.moveToFirst()) {
			user = new User(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return user;
	}

}
