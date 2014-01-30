package com.baloch.photoorganizer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.baloch.photoorganizer.PhotoShow.deletePhotoTask;

import models.Photo;
import models.Place;
import providers.DirectoriesProvider;
import providers.PhotosProvider;
import utils.Dialog;
import utils.UIUtilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Activity_SaveImage extends Activity {
	private static final int GALLERY_ACTIVITY = 1501;
	private static final int LOCATION_ACTIVITY = 1502;
	// GestureImageView imgPreview;
	ImageButton btn_back, menu;
	TextView txt_tags, txt_path, txt_location;
	EditText txt_tagInput, txt_name, txt_descr;
	Button btn_saveImage, btn_save_viewImage;
	ImageButton btn_selectPath, btn_selectLocation, btn_addTag;
	ArrayList<String> tags;

	File orignal_file;
	File save_file = DirectoriesProvider.getDefaultPictureDirectory().path;
	Photo photo = new Photo();
	boolean edit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_image);

		// imgPreview = (GestureImageView) findViewById(R.id.savePhotoShow);
		btn_save_viewImage = (Button) findViewById(R.id.btn_save_viewImage);
		btn_back = (ImageButton) findViewById(R.id.imgBtn_back);
		btn_selectPath = (ImageButton) findViewById(R.id.btn_save_selectPath);
		btn_selectLocation = (ImageButton) findViewById(R.id.btn_save_selectLocation);
		btn_addTag = (ImageButton) findViewById(R.id.btn_save_addTag);
		btn_saveImage = (Button) findViewById(R.id.btn_save_saveImage);
		txt_path = (TextView) findViewById(R.id.txt_save_path);
		txt_location = (TextView) findViewById(R.id.txt_save_location);
		txt_tagInput = (EditText) findViewById(R.id.txt_save_tagInput);
		txt_tags = (TextView) findViewById(R.id.txt_save_tags);
		txt_name = (EditText) findViewById(R.id.txt_save_name);
		txt_descr = (EditText) findViewById(R.id.txt_desc);
		// tags initilize variables
		tags = new ArrayList<String>();
		// loading image from previous activity
		String filePath = "";
		if (getIntent().getExtras() != null)
			if ((filePath = getIntent().getExtras().getString("imageFile")) != null) {
				orignal_file = new File(filePath);
				// set path to save file path. defaul path

				if (!(edit = getIntent().getExtras().getBoolean("edit"))) {
					save_file = DirectoriesProvider
							.getDefaultPictureDirectory().path;
					save_file = new File(save_file, orignal_file.getName());
				} else
					save_file = orignal_file;

				// set save file path i.e add name too, its differnt from
				// orignal at this point

				setPath(save_file);
				txt_name.setText(DirectoriesProvider.getBaseFileName(save_file
						.getName()));

			}
		// if editing picture
		if (edit) {
			String query = PhotosProvider.COL_PHOTO_NAME + "='"
					+ save_file.getName().trim() + "'";
			this.photo = PhotosProvider.getOne(getApplicationContext(), query);
			setFields(this.photo);
		}
		// set events===================================================
		txt_tags.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (txt_tags.getText().toString().length() > 1)
					editTagsDialog(v);
			}
		});
		txt_tagInput.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String strEnteredVal = txt_tagInput.getText().toString();

				if (strEnteredVal.endsWith(" ")) {
					strEnteredVal = (String) strEnteredVal.subSequence(0,
							strEnteredVal.length() - 1);
					txt_tagInput.setText(strEnteredVal);
				}
				if (strEnteredVal.contains(" "))
					txt_tagInput.setText(strEnteredVal.replaceAll(" ", ""));

			}
		});
		btn_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Dialog.dialog(Activity_SaveImage.this,
						"Image will be lost, are you sure?", "Image");

				Dialog.positive_listner = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				};

			}
		});

		btn_selectPath.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent int_dir = new Intent(Activity_SaveImage.this,
						Activity_GalleryFolder.class);
				int_dir.putExtra("purpose", "path");
				startActivityForResult(int_dir, GALLERY_ACTIVITY);
			}
		});

		btn_selectLocation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent int_loc = new Intent(Activity_SaveImage.this,
						AddPlace.class);
				startActivityForResult(int_loc, LOCATION_ACTIVITY);
				// startActivity(int_loc);
			}
		});

		btn_addTag.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String tag = txt_tagInput.getText().toString();
				tag = tag.trim();
				if (tag.length() < 1 || tags.contains(tag))
					return;
				tags.add(tag);
				txt_tags.setText(tags.toString());
				txt_tagInput.setText("");
			}
		});

		txt_location.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		btn_saveImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save_validate_photo();
			}

		});
		btn_save_viewImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent int_showImageFull = new Intent(Activity_SaveImage.this,
						PhotoShow.class);
				int_showImageFull.putExtra("path",
						orignal_file.getAbsolutePath());
				startActivity(int_showImageFull);
			}
		});

	}

	protected void editTagsDialog(View v) {
		int totalTags = this.tags.size();
		String tags[] = new String[totalTags];
		for (int i = 0; i < tags.length; i++)
			tags[i] = this.tags.get(i);
		// boolean array representing whether each tag is enabled
		final boolean[] tagsValid = new boolean[totalTags];
		for (int i = 0; i < tagsValid.length; ++i)
			tagsValid[i] = true;
		// create an AlertDialog Builder and set the dialog's title
		AlertDialog.Builder regionsBuilder = new AlertDialog.Builder(this);
		regionsBuilder.setTitle("Edit Tags");
		regionsBuilder.setMultiChoiceItems(tags, tagsValid,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						tagsValid[which] = isChecked;// set edit
					} // end method onClick
				} // end anonymous inner class
				); // end call to setMultiChoiceItems

		regionsBuilder.setPositiveButton("Edit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int button) {
						editTags(tagsValid);
					} // end method onClick
				} // end anonymous inner class
				); // end call to method setPositiveButton
		// create a dialog from the Builder
		AlertDialog regionsDialog = regionsBuilder.create();
		regionsDialog.show(); // display the Dialog

	}

	protected void editTags(boolean[] tagsValid) {
		// i should start from high to low to sease array out of bound when
		// first and last are removed in some case :P

		for (int i = tagsValid.length; i > 0; i--) {
			if (!tagsValid[i - 1])
				this.tags.remove(i - 1);
		}
		if (tags.size() < 1)
			txt_tags.setText("");
		else
			txt_tags.setText(tags.toString());

	}

	private void setPath(File file) {
		String parent = file.getParentFile().getName();
		String parentparent = file.getParentFile().getParentFile().getName();
		this.txt_path.setText(parentparent + "/" + parent);
	}

	private void setFields(Photo photo) {
		// txt_name.setText(photo.name); already set in intent create
		txt_location.setText(photo.place.name);
		// txt_path.setText(photo.path);
		if ((this.tags = photo.getTags()) != null) {
			String tagsLine = "";
			for (String tag : tags) {
				tagsLine += tag + " ";
			}
			if (tagsLine != null && tagsLine.length() > 0)
				txt_tags.setText(tagsLine);
		}
		txt_descr.setText(photo.descr);
	}

	public String gt(EditText e) {
		return e.getText().toString();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_ACTIVITY)
			if (resultCode == Activity.RESULT_OK) {
				// make save_file updated
				save_file = new File(data.getExtras().getString("path"));
				// show this and parent directory
				txt_path.setText(save_file.getParentFile().getName() + "/"
						+ save_file.getName());
				// now add the image file name too, ready for save
				save_file = new File(save_file, orignal_file.getName());

			} else {
			}
		else if (requestCode == LOCATION_ACTIVITY
				&& resultCode == Activity.RESULT_OK) {
			Place place = (Place) data.getExtras().getSerializable("place");
			this.photo.place.name = place.name;
			this.photo.place.lat = place.lat;
			this.photo.place.lon = place.lon;
			this.txt_location.setText(this.photo.place.name);
		}

	}

	private void save_validate_photo() {
		photo.name = gt(txt_name).trim() + ".jpg";
		if (photo.name.length() < 3) {
			UIUtilities.message(getApplicationContext(),
					"Image name must be at least 4 characters");
			return;
		}
		photo.place.name = txt_location.getText().toString();
		if (photo.place == null || photo.place.lat == null
				|| photo.place.lon == null) {
			UIUtilities.message(getApplicationContext(), "Select place");
			return;

		} else if (photo.place.lat.length() < 1 || photo.place.lon.length() < 1) {
			UIUtilities.message(getApplicationContext(),
					"Select place or latitude longitude values are not given.");

			return;
		}

		// photo.date = DirectoriesProvider.getDateString(new Date());
		String tagsLine = "";
		for (String tag : tags) {
			tagsLine += tag + " ";
		}
		if (tagsLine != null && tagsLine.length() > 0)
			photo.tags = tagsLine.trim();
		photo.descr = gt(txt_descr).trim();
		photo.auther = "user logged in";
		photo.date = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US)
				.format(new Date());

		// copy the file if save file is changed
		save_file = new File(save_file.getParentFile().getAbsoluteFile(),
				photo.name);
		if (save_file.getAbsolutePath().equalsIgnoreCase(
				orignal_file.getAbsolutePath()))
			photo.path = save_file.getAbsolutePath();
		else if (DirectoriesProvider.moveFile(orignal_file, save_file)) {
			photo.path = save_file.getAbsolutePath();
		} else {
			photo.path = orignal_file.getAbsolutePath();
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					Activity_SaveImage.this);
			dialog.setTitle("Error Saving");
			dialog.setMessage("File saving error in destination path. Saving file into default folder");
			dialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UIUtilities.message(getApplicationContext(),
									"selected");
						}
					});
			dialog.show();
		}
		if (!edit)
			new InsertDataTask().execute(photo);
		else
			new UpdateDataTask().execute(photo);

	}

	private class InsertDataTask extends AsyncTask<Photo, Integer, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(
				Activity_SaveImage.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Inserting data...");
			this.dialog.show();
		}

		@Override
		protected Boolean doInBackground(Photo... params) {
			return PhotosProvider.save(getApplicationContext(), params[0]) != -1;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			this.dialog.dismiss();
			if (result) {
				UIUtilities.message(getApplicationContext(), "save completed");
				Dialog.positive_listner = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent int_showImageFull = new Intent(
								Activity_SaveImage.this, PhotoShow.class);
						int_showImageFull.putExtra("path",
								orignal_file.getAbsolutePath());
						startActivity(int_showImageFull);
						finish();
					}
				};
				Dialog.negative_listner = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						finish();
					}
				};
				Dialog.dialog(Activity_SaveImage.this,
						"View saved image with details?", "Image");

			} else
				UIUtilities.message(getApplicationContext(),
						"uknown error in saving photo");
		}

	}

	private class UpdateDataTask extends AsyncTask<Photo, Integer, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(
				Activity_SaveImage.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Inserting data...");
			this.dialog.show();
		}

		@Override
		protected Boolean doInBackground(Photo... params) {
			return PhotosProvider.update(getApplicationContext(), params[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			this.dialog.dismiss();
			if (result)
				UIUtilities.message(getApplicationContext(), "save completed");
			else
				UIUtilities.message(getApplicationContext(),
						"uknown error in saving photo");
		}

	}
}
