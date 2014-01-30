package com.baloch.photoorganizer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import providers.PlacesProvider;
import utils.UIUtilities;
import models.Place;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddPlace extends Activity {
	protected static final int LOCATION_ACTIVITY = 1505;
	EditText txt_name, txt_lat, txt_lon;
	Button btn_select, btn_showMap;
	Spinner sp_places;

	Place place = new Place();
	ArrayList<Place> places = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_select);
		txt_name = (EditText) findViewById(R.id.txt_place_name);
		txt_lat = (EditText) findViewById(R.id.txt_place_lat);
		txt_lon = (EditText) findViewById(R.id.txt_place_lon);

		btn_select = (Button) findViewById(R.id.btn_place_select);
		btn_showMap = (Button) findViewById(R.id.btn_place_map);

		sp_places = (Spinner) findViewById(R.id.sp_place_list);
		// values

		try {
			places = PlacesProvider.selectAll(getApplicationContext(), "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// making array unique
		LinkedHashSet<Place> set = new LinkedHashSet<Place>(places);
		ArrayList<Place> uniqueArray = new ArrayList(new HashSet(set));
		// ======
		String[] placeNames = new String[uniqueArray.size()];
		for (int i = 0; i < uniqueArray.size(); i++) {

			placeNames[i] = uniqueArray.get(i).name;

		}
		ArrayAdapter adapter = new ArrayAdapter(this, R.layout.sp_item,
				placeNames);
		sp_places.setAdapter(adapter);

		sp_places.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				place.name = places.get(arg2).name;
				place.lat = places.get(arg2).lat;
				place.lon = places.get(arg2).lon;
				place.descr = places.get(arg2).descr;
				txt_name.setText(place.name);
				txt_lat.setText(place.lat);
				txt_lon.setText(place.lon);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// events
		btn_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validateFields()) {
					place.name = txt_name.getText().toString();
					place.lat = txt_lat.getText().toString();
					place.lon = txt_lon.getText().toString();
					Intent resultIntent = new Intent();
					resultIntent.putExtra("place", place);
					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
		});
		btn_showMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent locationSelect = new Intent(AddPlace.this,
						Activity_SelectLocation.class);
				locationSelect.putExtra("place", place);
				startActivityForResult(locationSelect, LOCATION_ACTIVITY);
			}
		});

	}

	private boolean validateFields() {
		if (txt_name.getText().toString().length() > 1)
			if (txt_lat.getText().toString().length() > 1)
				if (txt_lon.getText().toString().length() > 1)
					return true;
				else
					UIUtilities.message(getApplicationContext(),
							"please enter place latitude");
			else
				UIUtilities.message(getApplicationContext(),
						"please enter place longitude");
		else
			UIUtilities.message(getApplicationContext(),
					"please enter place name");
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOCATION_ACTIVITY)
			if (resultCode == Activity.RESULT_OK) {
				place = (Place) data.getExtras().getSerializable("place");
				txt_name.setText(place.name);
				txt_lat.setText(place.lat);
				txt_lon.setText(place.lon);
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
