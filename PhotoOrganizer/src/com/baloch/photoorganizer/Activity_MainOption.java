package com.baloch.photoorganizer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import com.baloch.photoorganizer.PhotoShow.deletePhotoTask;
import com.google.android.maps.GeoPoint;

import models.Place;

import providers.DirectoriesProvider;
import providers.PhotosProvider;
import utils.Dialog;
import utils.MyLocation;
import utils.MyLocation.LocationResult;
import utils.UIUtilities;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

public class Activity_MainOption extends Activity {

	ImageButton buttonTakeNewPicture, buttonViewGallery, buttonGalleryMap,
			btn_search, btn_import;

	protected static final int GET_IMAGE_ACTIVITY = 743;
	protected static final int GALLERY_ACTIVITY = 1501;
	Bitmap selectedImage;
	File defaultFile = null;
	Place defaultPlace = new Place();
	EditText search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_option);

		buttonTakeNewPicture = (ImageButton) findViewById(R.id.btn_main_newPic);
		buttonViewGallery = (ImageButton) findViewById(R.id.btn_main_gallery);
		buttonGalleryMap = (ImageButton) findViewById(R.id.btn_main_map);
		btn_search = (ImageButton) findViewById(R.id.btn_main_search);
		btn_import = (ImageButton) findViewById(R.id.btn_main_import);
		search = (EditText) findViewById(R.id.txt_main_search);

		// get location of device
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(final Location loc) {
				if (loc == null)
					return;
				defaultPlace.lat = (loc.getLatitude() / 1E6) + "";
				defaultPlace.lon = (loc.getLongitude() / 1E6) + "";
				new getAddressTask().execute(new GeoPoint((int) loc
						.getLatitude(), (int) loc.getLongitude()));
			}
		};
		MyLocation my_location = new MyLocation();
		my_location.init(this, locationResult);
		// ===set events
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (search.getText().toString() != "") {
					Intent photoSearch = new Intent(Activity_MainOption.this,
							PhotoSearch.class);
					photoSearch.putExtra("search", search.getText().toString());
					startActivity(photoSearch);
				}

			}
		});
		buttonTakeNewPicture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				captureMethod();
			}

		});
		btn_import.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				importMethod();
			}
		});
		buttonGalleryMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if (PhotosProvider.selectAll(getApplicationContext(), "")
							.size() < 1) {
						Dialog.positive_listner = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								captureMethod();
							}
						};
						Dialog.negative_listner = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								importMethod();
							}
						};
						Dialog.dialog(
								Activity_MainOption.this,
								"Gallery empty, select option below to add new?",
								"Image", "Cupture", "Import");
					} else {
						Intent int_gallery = new Intent(
								Activity_MainOption.this, GalleryMap.class);
						// /int_dir.putExtra("purpose", "view");
						// startActivityForResult(int_dir, GALLERY_ACTIVITY);
						startActivity(int_gallery);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		buttonViewGallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (PhotosProvider.selectAll(getApplicationContext(), "")
							.size() < 1) {
						Dialog.positive_listner = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								captureMethod();
							}
						};
						Dialog.negative_listner = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								importMethod();
							}
						};
						Dialog.dialog(
								Activity_MainOption.this,
								"Gallery empty, select option below to add new?",
								"Image", "Cupture", "Import");
					} else {
						Intent int_dir = new Intent(Activity_MainOption.this,
								Activity_GalleryFolder.class);
						int_dir.putExtra("purpose", "gallery");
						startActivityForResult(int_dir, GALLERY_ACTIVITY);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void captureMethod() {
		Intent int_takePic = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		// set default image path
		defaultFile = DirectoriesProvider.getDefaultImageFile();
		// set default path
		int_takePic.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		int_takePic
				.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(defaultFile));
		// start activity
		startActivityForResult(int_takePic, GET_IMAGE_ACTIVITY);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent d) {
		if (requestCode == GET_IMAGE_ACTIVITY
				&& resultCode == Activity.RESULT_OK) {
			Intent imageSave = new Intent(Activity_MainOption.this,
					Activity_SaveImage.class);
			imageSave.putExtra("imageFile", defaultFile.getAbsolutePath());
			startActivity(imageSave);
		}
		if (requestCode == GALLERY_ACTIVITY
				&& resultCode == Activity_GalleryFolder.RESULT_FILE_RETURN) {
			Intent imageSave = new Intent(Activity_MainOption.this,
					Activity_SaveImage.class);
			imageSave.putExtra("imageFile", d.getExtras().getString("file"));
			startActivity(imageSave);
		}
		// if (d != null)
		// if ((selectedImage = (Bitmap) d.getExtras().get("data")) != null) {
		// Intent imageSave = new Intent(Activity_MainOption.this,
		// Activity_SaveImage.class);
		// saveImage_temp(selectedImage, "selectedImage");
		// imageSave.putExtra("selectedImage", "selectedImage");
		// startActivity(imageSave);
		// }

		super.onActivityResult(requestCode, resultCode, d);

	}

	private void importMethod() {
		Intent int_dir = new Intent(Activity_MainOption.this,
				Activity_GalleryFolder.class);
		int_dir.putExtra("purpose", "file");
		startActivityForResult(int_dir, GALLERY_ACTIVITY);
	}

	// ==============get location address
	class getAddressTask extends AsyncTask<GeoPoint, Void, String> {
		@Override
		protected String doInBackground(GeoPoint... params) {
			Geocoder geoCoder = new Geocoder(getBaseContext(),
					Locale.getDefault());
			try {
				List<Address> addresses = geoCoder.getFromLocation(
						params[0].getLatitudeE6() / 1E6,
						params[0].getLongitudeE6() / 1E6, 1);
				// 1E6 = 100000 :P
				String add = "";
				if (addresses.size() > 0) {
					for (int i = 0; i < addresses.get(0)
							.getMaxAddressLineIndex(); i++)
						add += addresses.get(0).getAddressLine(i) + "\n";
				}
				return add;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			defaultPlace.name = result;
			if (result.length() > 0)
				UIUtilities.message(getApplicationContext(),
						"device recent location : " + result);
		}
	}

}
