package com.baloch.photoorganizer;

import providers.PhotosProvider;
import providers.PlacesProvider;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Settings extends Activity {
	Button delete_photos, delete_places, backup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		delete_photos = (Button) findViewById(R.id.btn_setting_deletePhotos);
		delete_photos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PhotosProvider.deleteNonExisting(getApplicationContext(), null);

			}
		});
		delete_places = (Button) findViewById(R.id.btn_settings_deletePlaces);
		delete_places.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlacesProvider.deleteAllWithoutPhoto(getApplicationContext());
			}
		});
	}

}
