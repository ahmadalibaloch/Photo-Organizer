package com.baloch.photoorganizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import models.Directory;
import models.Photo;
import providers.DirectoriesProvider;
import utils.UIUtilities;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewGallery extends Activity {
	ListView lst_GalleryView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_gallery);

		lst_GalleryView = (ListView) findViewById(R.id.listView_gallery);

		ArrayList<File> files = DirectoriesProvider.getAppDirectory()
				.getAllFiles();

		// Create a customized ArrayAdapter
		GalleryArrayAdapter adapter = new GalleryArrayAdapter(
				getApplicationContext(), R.layout.list_row, files);

		// Set the ListView adapter
		lst_GalleryView.setAdapter(adapter);
	}

	public ArrayList<Directory> getDirectories() {
		return DirectoriesProvider.getAppDirectory().getSubDirectories();
	}

	public class GalleryArrayAdapter extends ArrayAdapter<File> {
		private Context context;
		private ImageView gallery_image;
		private TextView gallery_name;
		private TextView gallery_path;
		private ArrayList<File> files = new ArrayList<File>();

		public GalleryArrayAdapter(Context context, int textViewResourceId,
				ArrayList<File> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.files = objects;
		}

		public int getCount() {
			return this.files.size();
		}

		public File getItem(int index) {
			return this.files.get(index);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.list_row, parent, false);
			}
			File file = getItem(position);
			gallery_image = (ImageView) row.findViewById(R.id.photo_thumb);
			gallery_name = (TextView) row.findViewById(R.id.photo_name);
			gallery_path = (TextView) row.findViewById(R.id.photo_place);
			gallery_name.setText(file.getName());
			Bitmap bitmap = null;
			if (!file.isDirectory())
				if (file.exists())
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				else
					try {
						bitmap = BitmapFactory.decodeStream(this.context
								.getResources().getAssets().open("folder.png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
			gallery_image.setImageBitmap(bitmap);
			// gallery_path.setText(dir.getPath());
			return row;
		}
	}
}
