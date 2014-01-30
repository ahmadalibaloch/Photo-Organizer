package com.baloch.photoorganizer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import com.baloch.photoorganizer.PhotoSearch.ImageAdapter;

import providers.DirectoriesProvider;
import providers.PhotosProvider;
import utils.Dialog;
import utils.UIUtilities;

import models.Directory;
import models.Photo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_GalleryFolder extends Activity {
	private static final int ACTIVITY_SHOW_PICTURE = 1003;
	public static final int RESULT_FILE_RETURN = 1004;
	Button btn_gallery_select, btn_gallery_addFolder;
	TextView txt_gallery_path;
	ListView gallery_folders;
	String purpose = "";
	Directory dir = DirectoriesProvider.getAppDirectory();
	File selectedFile;
	ImageButton btn_back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().getString("purpose") != null)
			purpose = getIntent().getExtras().getString("purpose");
		if (purpose.equalsIgnoreCase("path"))
			setContentView(R.layout.activity_gallery__folder);
		else if (purpose.equalsIgnoreCase("gallery")) {
			setContentView(R.layout.activity_gallery_gallery);
		} else
			setContentView(R.layout.activity_gallery_file);
		btn_gallery_select = (Button) findViewById(R.id.btn_gallery_setPath);
		btn_gallery_addFolder = (Button) findViewById(R.id.btn_gallery_addFolder);
		txt_gallery_path = (TextView) findViewById(R.id.txt_gallery_path);
		gallery_folders = (ListView) findViewById(R.id.lst_gallery_folders);
		btn_back = (ImageButton) findViewById(R.id.imgBtn_back);
		// gallery_folders.setBackgroundColor(Color.BLACK);
		// input.setText("Folder is Empty");
		// lst_gallery_folders.setEmptyView(findViewById(R.id.emptyView));
		gallery_folders.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listClickHandler(position);

			}
		});
		gallery_folders
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						DeleteMethod(arg2);

						return false;
					}
				});
		btn_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		// button events
		if (btn_gallery_select != null)
			btn_gallery_select.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					finishActivity();
				}
			});
		if (btn_gallery_addFolder != null)
			btn_gallery_addFolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addFolderListener();
				}
			});

		// load initial values
		if (purpose.equalsIgnoreCase("gallery")) {
			refreshDir(DirectoriesProvider.getAppDirectory());
		} else if (purpose.equalsIgnoreCase("file")) {
			refreshDir(new Directory(
					DirectoriesProvider.getAppDirectory().path.getParentFile()));
		} else {
			refreshDir(DirectoriesProvider.getAppDirectory());
			txt_gallery_path.setText(dir.getPath());
		}

	}

	private void listClickHandler(int position) {
		selectedFile = dir.getAllFiles().get(position);
		if (selectedFile.isFile())
			if (this.purpose.equalsIgnoreCase("path"))
				refreshDir(dir.getSubDirectories().get(position));
			else if (this.purpose.equalsIgnoreCase("file"))
				finishActivity();
			else if (this.purpose.equalsIgnoreCase("view"))
				showPicture(selectedFile);
			else if (this.purpose.equalsIgnoreCase("gallery"))
				showPicture(selectedFile);
			else
				return;
		else
			refreshDir(new Directory(selectedFile));
	}

	private void showPicture(File dir2) {
		Intent int_imageShow = new Intent(Activity_GalleryFolder.this,
				PhotoShow.class);
		int_imageShow.putExtra("path", dir2.getAbsolutePath());
		startActivity(int_imageShow);
	}

	private void addFolderListener() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Activity_GalleryFolder.this);

		dialog.setTitle("Input Folder Name");
		dialog.setMessage("Enter New Folder Name");
		// Set an EditText view to get user input
		final EditText input = new EditText(Activity_GalleryFolder.this);
		dialog.setView(input);
		dialog.setCancelable(true);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				addFolder(input.getText().toString());
			}

		});
		dialog.setNegativeButton("Cancel", null);
		dialog.create();
		dialog.show();

	}

	private boolean addFolder(String name) {
		Directory newFolder = new Directory(dir, name);
		refreshDir(dir);
		return false;

	}

	@Override
	public void onBackPressed() {
		if (purpose.equalsIgnoreCase("gallery")) {
			if (dir.equals(DirectoriesProvider.getAppDirectory())) {
				finishActivity();
				return;
			}
			refreshDir(new Directory(dir.path.getParentFile()));
			return;
		} else if (purpose.equalsIgnoreCase("path")) {
			if (dir.equals(DirectoriesProvider.getAppDirectory())) {
				finishActivity();
				return;
			}
			refreshDir(new Directory(dir.path.getParentFile()));
			return;
		}
		if (dir.equals(DirectoriesProvider.getRootDirectory())) {
			finishActivity();
			return;
		}
		refreshDir(new Directory(dir.path.getParentFile()));
	}

	private void finishActivity() {
		if (this.purpose.equalsIgnoreCase("path"))
			if (dir != null) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("path", dir.getPath());
				setResult(Activity.RESULT_OK, resultIntent);
			} else {
				setResult(Activity.RESULT_CANCELED);
			}
		else if (this.purpose.equalsIgnoreCase("file")) {
			if (selectedFile != null) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("file", selectedFile.getAbsolutePath());
				setResult(RESULT_FILE_RETURN, resultIntent);
			} else {
				setResult(Activity.RESULT_CANCELED);
			}
		} else if (this.purpose.equalsIgnoreCase("view")) {
			setResult(Activity.RESULT_OK);
		} else
			setResult(Activity.RESULT_CANCELED);
		finish();
	}

	void refreshDir(Directory dir) {
		new RefreshDirTask().execute(dir);
		this.dir = dir;
		if (!purpose.equalsIgnoreCase("gallery"))
			txt_gallery_path.setText(dir.getPath());
	}

	int selectedFileItem;

	private void DeleteMethod(int arg2) {
		selectedFileItem = arg2;
		if (!purpose.equalsIgnoreCase("gallery"))
			return;
		Dialog.positive_listner = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteFile();
			}

		};
		Dialog.dialog(Activity_GalleryFolder.this, "Select an Optoin",
				"Photo Options", "Delete", "Cancel");

	}

	private void deleteFile() {
		int position = selectedFileItem;
		selectedFile = dir.getAllFiles().get(position);
		if (selectedFile.isFile())
			PhotosProvider.delete(getApplicationContext(), PhotosProvider
					.getOne(getApplicationContext(), selectedFile));
		else {
			new Directory(selectedFile).delete();
		}
		refreshDir(dir);
		PhotosProvider.deleteNonExisting(getApplicationContext(), null);
	}

	//
	public class FileArrayAdapter extends ArrayAdapter<File> {
		private Context context;
		private ImageView gallery_image;
		private TextView gallery_name;
		private TextView gallery_path;
		private ArrayList<File> directories = new ArrayList<File>();

		public FileArrayAdapter(Context context, int textViewResourceId,
				ArrayList<File> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.directories = objects;
		}

		public int getCount() {
			return this.directories.size();
		}

		public File getItem(int index) {
			return this.directories.get(index);
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
			gallery_name.setTextColor(Color.WHITE);
			int width = 128;
			int height = 128;
			String imgFilePath = "folder.png";
			if (purpose.equalsIgnoreCase("gallery")) {
				imgFilePath = "gallery.png";
				width = 200;
				height = 200;
			}
			if (file.isFile())
				imgFilePath = file.getAbsolutePath();
			try {
				Bitmap bitmap = null;
				if (imgFilePath.equalsIgnoreCase("folder.png"))
					bitmap = BitmapFactory.decodeStream(this.context
							.getResources().getAssets().open(imgFilePath));
				else if (imgFilePath.equalsIgnoreCase("gallery.png"))
					bitmap = BitmapFactory.decodeStream(this.context
							.getResources().getAssets().open(imgFilePath));
				else
					bitmap = DirectoriesProvider.getThumb(imgFilePath, width,
							height);
				gallery_image.setImageBitmap(bitmap);
				LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						128, 128);
				gallery_image.setLayoutParams(layoutParams);
				gallery_image.setMaxHeight(height);
				gallery_image.setMaxWidth(width);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (purpose.equalsIgnoreCase("gallery") && file.isDirectory()) {
				int totalPhotos = new Directory(file).getPhotos(
						getApplicationContext()).size();
				String text = totalPhotos > 0 ? totalPhotos + "" : "No";
				gallery_path.setText("(" + text + " Photos )");
			}
			return row;
		}
	}

	class RefreshDirTask extends AsyncTask<Directory, Void, FileArrayAdapter> {
		private final ProgressDialog dialog = new ProgressDialog(
				Activity_GalleryFolder.this);

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("loading files...");
			this.dialog.show();
			super.onPreExecute();
		}

		@Override
		protected FileArrayAdapter doInBackground(Directory... params) {
			Directory newDir = params[0];
			ArrayList<File> files = new ArrayList<File>();
			if (purpose.equalsIgnoreCase("gallery")) {
				if (newDir.equals(DirectoriesProvider.getAppDirectory()))
					files = newDir.getSubDirectoriesAsFiles();
				else {
					ArrayList<Photo> photos = newDir
							.getPhotos(getApplicationContext());
					for (Photo ph : photos)
						files.add(new File(ph.path));
				}

			} else if (purpose.equalsIgnoreCase("path"))
				files = newDir.getSubDirectoriesAsFiles();
			else
				files = newDir.getAllFiles();
			FileArrayAdapter adapter = new FileArrayAdapter(
					getApplicationContext(), R.layout.list_row, files);

			return adapter;
		}

		@Override
		protected void onPostExecute(FileArrayAdapter result) {
			this.dialog.dismiss();
			// Set the ListView adapter
			gallery_folders.setAdapter(result);
			super.onPostExecute(result);
		}

	}

}
