package com.baloch.photoorganizer;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import com.baloch.photoorganizer.PhotoShow.deletePhotoTask;

import models.Directory;
import models.Photo;

import providers.DirectoriesProvider;
import providers.PhotosProvider;
import utils.Dialog;
import utils.UIUtilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class PhotoSearch extends Activity {
	ImageButton btn_search, btn_back;
	EditText txt_search;
	GridView grd_photos;
	static final int thumb_width = 220;
	static final int thumb_height = 220;
	protected static final int PHOTO_SHOW = 1506;
	ArrayList<Photo> photosList = null;
	String searchTerm = "";
	int selected = -1;
	boolean gallery = false;
	Directory selectedFolder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_search);

		if ((gallery = getIntent().getExtras().getBoolean("gallery"))) {
			String path = getIntent().getExtras().getString("path");
			File file = new File(path);
			if ((selectedFolder = new Directory(file)) != null) {
				images = new ImageAdapter(getApplicationContext());
				images.setImages(getThumbs(selectedFolder));
				images.notifyDataSetChanged();
				grd_photos.setAdapter(images);
			}
		}
		// ==
		btn_search = (ImageButton) findViewById(R.id.btn_search_search);
		btn_back = (ImageButton) findViewById(R.id.imgBtn_back);

		txt_search = (EditText) findViewById(R.id.txt_search_search);
		grd_photos = (GridView) findViewById(R.id.grd_search_photos);
		TextView empty = new TextView(getApplicationContext());
		empty.setText("Click search to view images");
		grd_photos.setEmptyView(empty);

		// grd_photos.set
		grd_photos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent int_showImageFull = new Intent(PhotoSearch.this,
						PhotoShow.class);
				int_showImageFull.putExtra("path",
						photosList.get(position).path);
				selected = position;
				startActivityForResult(int_showImageFull, PHOTO_SHOW);
			}
		});
		grd_photos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				Dialog.positive_listner = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						deleteTask(arg2);
					}

				};
				Dialog.negative_listner = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						editTask(arg2);
					}
				};
				Dialog.dialog(PhotoSearch.this,
						"Select an Optoin for the photo", "Photo Options",
						"Delete", "Edit");
				return false;
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String searchString = txt_search.getText().toString()
						.toLowerCase().trim();
				search(searchString);
			}
		});

		// import search terms from previous activities
		String search = "";
		if (getIntent().getExtras() != null)
			if ((search = getIntent().getExtras().getString("search")) != null) {
				search = search.trim();
				txt_search.setText(search);
				search(search);
			}

	}

	private ArrayList<Drawable> getThumbs(Directory folder) {
		ArrayList<Drawable> thumbs = new ArrayList<Drawable>();
		ArrayList<Photo> photosList = folder.getPhotos(getApplicationContext());

		for (Photo photo : photosList) {
			thumbs.add(new BitmapDrawable(DirectoriesProvider.getThumb(
					photo.path, 100, 100)));
		}

		return thumbs;
	}

	private void deleteTask(final int position) {
		Dialog.positive_listner = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// new File(params[0].path).delete();
				new deletePhotoTask().execute(photosList.get(position));
				Intent result = new Intent();
				result.putExtra("delete", true);
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		};
		Dialog.dialog(PhotoSearch.this,
				"Only Image record will be deleted parmanently, are you sure?",
				"Image");

	}

	private void editTask(int position) {
		// new File(params[0].path).delete();
		Intent imageSave = new Intent(PhotoSearch.this,
				Activity_SaveImage.class);
		imageSave.putExtra("imageFile", photosList.get(position).path);
		imageSave.putExtra("edit", true);
		startActivity(imageSave);
	}

	class deletePhotoTask extends AsyncTask<Photo, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Photo... params) {
			return PhotosProvider.delete(getApplicationContext(), params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			UIUtilities.message(getApplicationContext(),
					"Image record delete..");
			super.onPostExecute(result);
		}

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent settings = new Intent(PhotoSearch.this, Settings.class);
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(settings);
			finish();
			return true;
		default:
			return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_SHOW)
			if (resultCode == Activity.RESULT_OK) {
				if (data.getExtras().getBoolean("delete")) {

					this.images.delete(selected);
					grd_photos.setAdapter(images);
				}
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void search(String searchString) {
		ArrayList<String> searchQuries;
		if (searchString.length() < 1) {
			// if nothing to search, search all
			searchQuries = new ArrayList<String>();
			searchQuries.add("");
			new SearchTastk().execute(searchQuries);
		} else {
			// search for all terms specified
			ArrayList<String> searchTerms = PhotosProvider
					.getTags(searchString);
			searchQuries = new ArrayList<String>();
			String searchQuery = "1=1 AND";
			for (String term : searchTerms) {

				searchQuery += " name LIKE '" + term
						+ ".jpg%' OR name LIKE '% " + term + ".jpg%'";
				searchQuery += "OR tags LIKE '" + term + "%' OR tags LIKE '% "
						+ term + "%'";
				searchQuery += " OR placeId IN(SELECT _id FROM tbl_places WHERE name LIKE '"
						+ term + "%' OR name LIKE '% " + term + "%') OR";

			}
			searchQuery = searchQuery.substring(0, searchQuery.length() - 2);
			searchQuries.add(searchQuery);
			new SearchTastk().execute(searchQuries);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_photo_search, menu);
		return true;
	}

	ImageAdapter images;

	class SearchTastk extends
			AsyncTask<ArrayList<String>, Integer, ArrayList<Drawable>> {
		private final ProgressDialog dialog = new ProgressDialog(
				PhotoSearch.this);

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Searching photos...");
			this.dialog.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<Drawable> doInBackground(
				ArrayList<String>... params) {

			ArrayList<Photo> MegaphotosList = new ArrayList<Photo>();
			for (String query : params[0]) {
				// get photos for this query
				try {
					photosList = PhotosProvider.selectAll(
							getApplicationContext(), query);
				} catch (ParseException e) {
				}
				// add all not already photos to mega list
				for (Photo ph : photosList) {
					if (!MegaphotosList.contains(ph))
						MegaphotosList.add(ph);
				}
			}
			// add all photos to drawable for returning
			ArrayList<Drawable> photoDrawables = new ArrayList<Drawable>();
			for (Photo ph : MegaphotosList) {
				photoDrawables.add(new BitmapDrawable(DirectoriesProvider
						.getThumb(ph.path, thumb_width, thumb_height)));
			}
			return photoDrawables;
		}

		@Override
		protected void onPostExecute(ArrayList<Drawable> result) {
			this.dialog.dismiss();
			String message = result.size() < 1 ? "no images found" : result
					.size() + " images found";
			UIUtilities.message(getApplicationContext(), message);
			images = new ImageAdapter(getApplicationContext());
			images.setImages(result);
			images.notifyDataSetChanged();
			grd_photos.setAdapter(images);

		}

	}

	// this class provides the connection with grid view to provide data
	class ImageAdapter extends BaseAdapter {
		// private image view for parameter test
		ImageView imageview;
		Context mContext;
		int columnIndex;

		public ImageAdapter(Context adapter) {
			mContext = adapter;
		}

		public ImageAdapter(OnClickListener onClickListener) {
			// TODO Auto-generated constructor stub

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return imgs.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				imageview = new ImageView(mContext);
				imageview.setBackgroundColor(Color.GRAY);
				imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageview.setPadding(5, 5, 5, 5);
				imageview.setLayoutParams(new GridView.LayoutParams(
						thumb_height, thumb_width));

			} else {
				imageview = (ImageView) convertView;
			}

			// set the image for the image view from the array of flags
			imageview.setImageDrawable(imgs.get(position));
			return imageview;
		}

		// references to our images, defualt is 4
		private ArrayList<Drawable> imgs;

		public void setImages(ArrayList<Drawable> array) {
			this.imgs = array;
		}

		public void delete(int position) {
			this.imgs.remove(position);
		}

	}
}
