package com.baloch.photoorganizer;

import java.io.File;
import java.util.ArrayList;

import models.Directory;
import models.Photo;

import providers.DirectoriesProvider;
import utils.UIUtilities;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class FolderGallery extends Activity {
	Gallery gallery;
	ArrayList<Directory> directories;
	ArrayList<Directory> galleryFolders;
	ArrayList<Drawable> thumbs;
	boolean mainFolder = true;
	Directory folderSelected;
	ArrayList<Photo> inFolderPhotos;
	ImageAdapter imgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder_gallery);
		gallery = (Gallery) findViewById(R.id.galleryFolder);
		directories = DirectoriesProvider.getDirectoryTree(DirectoriesProvider
				.getDefaultPictureDirectory().path);
		galleryFolders = DirectoriesProvider.getPhotoDirectories(
				getApplicationContext(), directories);
		thumbs = getThumbs(galleryFolders);
		imgs = new ImageAdapter(this);
		imgs.setImages(thumbs);
		gallery.setAdapter(imgs);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				if (folderSelected == null)
					return;
				folderSelected = galleryFolders.get(position);
				String path = folderSelected.getPath();
				Intent int_imageShow = new Intent(FolderGallery.this,
						PhotoSearch.class);
				int_imageShow.putExtra("gallery", true);
				int_imageShow.putExtra("path", path);
				startActivity(int_imageShow);

			}

			public void onNothingSelected(AdapterView<?> parent) {
				// mySelection.setText("Nothing selected");

			}

		});

	}

	private ArrayList<Drawable> getThumbs(ArrayList<Directory> galleryFolders) {
		ArrayList<Drawable> thumbs = new ArrayList<Drawable>();
		for (Directory dir : galleryFolders) {
			ArrayList<Photo> photos = dir.getPhotos(getApplicationContext());
			ArrayList<Drawable> photoThumbs = new ArrayList<Drawable>();
			// for (int i = 0; i < photos.size(); i++) {
			// photoThumbs.add(new BitmapDrawable(DirectoriesProvider
			// .getThumb(photos.get(i).path, 100, 100)));
			// }
			// Canvas c = new Canvas();
			// Bitmap b = Bitmap.createBitmap(400, 400, Bitmap.Config.ALPHA_8);
			// Rect r2 = new Rect
			// c.
			// c.setBitmap(bitmap)
			// thumbDraw.draw()
			// thumbDraw.thumbs.add(thumbDraw);
			thumbs.add(new BitmapDrawable(DirectoriesProvider.getThumb(
					photos.get(0).path, 100, 100)));
		}
		return thumbs;
	}

	private ArrayList<Drawable> getThumbs(Directory folder) {
		ArrayList<Drawable> thumbs = new ArrayList<Drawable>();
		inFolderPhotos = folder.getPhotos(getApplicationContext());
		for (Photo photo : inFolderPhotos) {
			thumbs.add(new BitmapDrawable(DirectoriesProvider.getThumb(
					photo.path, 100, 100)));
		}

		return thumbs;
	}

	@Override
	public void onBackPressed() {
		if (!mainFolder) {
			directories = DirectoriesProvider
					.getDirectoryTree(DirectoriesProvider.getAppDirectory().path);
			galleryFolders = DirectoriesProvider.getPhotoDirectories(
					getApplicationContext(), directories);
			thumbs = getThumbs(galleryFolders);
			imgs.setSize(400, 400);
			imgs.setImages(thumbs);
			gallery.setAdapter(imgs);
			//
			mainFolder = true;
		} else
			finish();
		super.onBackPressed();
	}


	public class ImageAdapter extends BaseAdapter {
		/** The parent context */
		private Context myContext;
		int width = 400, height = 400;
		// Put some images to project-folder: /res/drawable/
		// format: jpg, gif, png, bmp, ...
		private ArrayList<Drawable> myImageIds;

		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) {
			this.myContext = c;
		}

		public void setSize(int i, int j) {
			this.width = i;
			this.height = j;

		}

		public void setImages(ArrayList<Drawable> thumbs) {
			this.myImageIds = thumbs;
		}

		// inherited abstract methods - must be implemented
		// Returns count of images, and individual IDs
		public int getCount() {
			return this.myImageIds.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// Returns a new ImageView to be displayed,
		public View getView(int position, View convertView, ViewGroup parent) {

			// Get a View to display image data
			ImageView iv = new ImageView(this.myContext);
			iv.setBackgroundDrawable(this.myImageIds.get(position));

			// iv.setScaleType(ImageView.ScaleType.CENTER);
			// iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			// iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iv.setPadding(15, 15, 15, 15);

			// Set the Width & Height of the individual images
			iv.setLayoutParams(new Gallery.LayoutParams(this.width, this.height));

			return iv;
		}
	}// ImageAdapter

}
