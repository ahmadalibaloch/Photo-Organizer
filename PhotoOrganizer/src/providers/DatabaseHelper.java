package providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper mInstance;
	private static SQLiteDatabase myWritableDb;

	//
	private static final String DB_NAME = "db_photos";
	private static final int DATABASE_VERSION = 1;

	/**
	 * Constructor takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 *            the application context
	 */
	private DatabaseHelper(Context context) {

		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Get default instance of the class to keep it a singleton
	 * 
	 * @param context
	 *            the application context
	 */
	public static DatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}

	/**
	 * Returns a writable database instance in order not to open and close many
	 * SQLiteDatabase objects simultaneously
	 * 
	 * @return a writable instance to SQLiteDatabase
	 */
	public SQLiteDatabase getMyWritableDatabase() {
		if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
			myWritableDb = this.getWritableDatabase();
		}

		return myWritableDb;
	}

	@Override
	public void close() {
		super.close();
		if (myWritableDb != null) {
			myWritableDb.close();
			myWritableDb = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + PhotosProvider.TABLE_PHOTO);
		db.execSQL(STRING_CREATE_TABLE_PHOTOS);
		db.execSQL("DROP TABLE IF EXISTS " + PlacesProvider.TABLE_PLACE);
		db.execSQL(STRING_CREATE_TABLE_PLACES);
		db.execSQL("DROP TABLE IF EXISTS " + UsersProvider.TABLE_USER);
		db.execSQL(STRING_CREATE_TABLE_USERS);
		// // add two simple records first time when db created
		// ContentValues sample_photo = new ContentValues(
		// PhotosProvider.columns_tbl_photo.length - 1);// -1 for id
		// sample_photo.put(PhotosProvider.COL_PHOTO_NAME, "photo");
		// sample_photo.put(PhotosProvider.COL_PHOTO_AUTHER, "auther");
		// sample_photo.put(PhotosProvider.COL_PHOTO_DATE, "20-01-12");
		// sample_photo.put(PhotosProvider.COL_PHOTO_DESCR, "simple photo");
		// sample_photo.put(PhotosProvider.COL_PHOTO_PATH, "all");
		// sample_photo.put(PhotosProvider.COL_PHOTO_PLACEID, "1");
		// sample_photo.put(PhotosProvider.COL_PHOTO_TAGS, "simple photo tag");
		// db.insert(PhotosProvider.TABLE_PHOTO, null, sample_photo);
		//
		// ContentValues sample_place = new ContentValues(
		// PlacesProvider.columns_tbl_place.length - 1);
		// sample_place.put(PlacesProvider.COL_PLACE_NAME, "place1");
		// sample_place.put(PlacesProvider.COL_PLACE_LAT, "12.2131");
		// sample_place.put(PlacesProvider.COL_PLACE_LON, "24.1231");
		// sample_place.put(PlacesProvider.COL_PLACE_DESCR, "simple place");
		// db.insert(PlacesProvider.TABLE_PLACE, null, sample_place);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (oldVersion >= newVersion)
		// return;
		//
		// String sql = null;
		// if (oldVersion == 1)
		// sql = "alter table " + TABLE_NAME + " add COLUMN_2 text;";
		// if (oldVersion == 2)
		// sql = "";
		//
		// Log.d("EventsData", "onUpgrade	: " + sql);
		// if (sql != null)
		// db.execSQL(sql);

	}

	// data===========================

	// basic PHOTO TABLE build query
	private static final String STRING_CREATE_TABLE_PHOTOS = "CREATE TABLE "
			+ PhotosProvider.TABLE_PHOTO
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PhotosProvider.COL_PHOTO_NAME + " TEXT, "
			+ PhotosProvider.COL_PHOTO_TAGS + " TEXT, "
			+ PhotosProvider.COL_PHOTO_AUTHER + " TEXT, "
			+ PhotosProvider.COL_PHOTO_DATE + " TEXT, "
			+ PhotosProvider.COL_PHOTO_PLACEID + " INTEGER, "
			+ PhotosProvider.COL_PHOTO_PATH + " TEXT, "
			+ PhotosProvider.COL_PHOTO_DESCR + " TEXT);";
	// basic PLACE TABLE build query
	private static final String STRING_CREATE_TABLE_PLACES = "CREATE TABLE "
			+ PlacesProvider.TABLE_PLACE
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PlacesProvider.COL_PLACE_NAME + " TEXT, "
			+ PlacesProvider.COL_PLACE_LAT + " REAL, "
			+ PlacesProvider.COL_PLACE_LON + " REAL, "
			+ PlacesProvider.COL_PLACE_DESCR + " TEXT);";
	// basic PLACE TABLE build query
	private static final String STRING_CREATE_TABLE_USERS = "CREATE TABLE "
			+ UsersProvider.TABLE_USER
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ UsersProvider.COL_USER_NAME + " TEXT, "
			+ UsersProvider.COL_USER_USERNAME + " TEXT, "
			+ UsersProvider.COL_USER_PASSWORD + " TEXT);";
}