package com.baloch.photoorganizer;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import com.baloch.photoorganizer.PhotoSearch.ImageAdapter;

import models.Photo;
import providers.DirectoriesProvider;
import providers.PhotosProvider;
import utils.Dialog;
import utils.UIUtilities;
import utils.ImageView.GestureImageView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PhotoShow extends Activity {
	utils.ImageView.GestureImageView imageView;
	utils.WrappingSlidingDrawer slider;
	Button photoMap;
	TextView tv_name, tv_place, tv_time, tv_details, tv_tags;
	Photo photo;
	MenuItem mnu_addDetails;
	View mnu_editDetails;
	String path;// if new file is selected, not in record

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		photo = getPhoto();
		path = getIntent().getExtras().getString("path");
		if (photo != null)
			setContentView(R.layout.activity_photo_show);
		else {
			imageView = new GestureImageView(getApplicationContext());
			imageView.setImageBitmap(BitmapFactory.decodeFile(path));
			setContentView(imageView);
			return;
		}

		imageView = (GestureImageView) findViewById(R.id.photoShow);
		imageView.setBackgroundColor(Color.BLACK);

		imageView.setImageBitmap(BitmapFactory.decodeFile(photo.path));
		tv_name = (TextView) findViewById(R.id.tv11);
		tv_place = (TextView) findViewById(R.id.tv22);
		tv_time = (TextView) findViewById(R.id.tv33);
		tv_details = (TextView) findViewById(R.id.tv55);
		tv_tags = (TextView) findViewById(R.id.tv44);
		photoMap = (Button)findViewById(R.id.btn_photomap);
		photoMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent result = new Intent(PhotoShow.this,GalleryMap.class);
				result.putExtra("photo", photo);
				startActivity(result);
			}
		});

		tv_name.setText(DirectoriesProvider.getBaseFileName(photo.name));
		tv_place.setText(photo.place.name + "");
		tv_time.setText(photo.getDateString() + "");
		tv_details.setText(photo.descr);
		tv_tags.setText(photo.tags);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getPhoto();
		// Inflate the menu; this adds items to the action bar if it is present.
		if (photo != null)
			getMenuInflater().inflate(R.menu.activity_photo_edit, menu);
		else
			getMenuInflater().inflate(R.menu.activity_photo_add, menu);
		return true;
	}

	private Photo getPhoto() {
		String path = getIntent().getExtras().getString("path");
		String query = PhotosProvider.COL_PHOTO_NAME + "='"
				+ new File(path).getName().trim() + "'";
		Photo photo = PhotosProvider.getOne(getApplicationContext(), query);
		return photo;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent imageSave = new Intent(PhotoShow.this, Activity_SaveImage.class);
		imageSave.putExtra("imageFile", path);
		switch (item.getItemId()) {
		case R.id.menu_show_add:
			startActivity(imageSave);
			finish();
			return true;
		case R.id.menu_show_edit:
			imageSave.putExtra("edit", true);
			startActivity(imageSave);
			finish();
			return true;
		case R.id.menu_show_del:
			Dialog.positive_listner = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//new File(params[0].path).delete();
					new deletePhotoTask().execute(photo);
					Intent result = new Intent();
					result.putExtra("delete", true);
					setResult(Activity.RESULT_OK,result);
					finish();
				}
			};
			Dialog.dialog(PhotoShow.this,
					"Only Image record will be deleted parmanently, are you sure?", "Image");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class deletePhotoTask extends AsyncTask<Photo, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Photo... params) {
			return PhotosProvider.delete(getApplicationContext(), params[0]);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			UIUtilities.message(getApplicationContext(), "Image record delete..");
			super.onPostExecute(result);
		}

	}

}
