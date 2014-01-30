package com.baloch.photoorganizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import models.Place;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import utils.UIUtilities;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class Activity_SelectLocation extends MapActivity {
	TextView txtLat;
	TextView txtLon;
	TextView txt_name;
	private MapView mapView;
	private MapController mapController;

	Location location;
	GeoPoint mapPoint;
	Place place = new Place();
	List<Overlay> mapOverlays;
	myOverlays itemizedoverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
		// Get a reference to the MapView
		mapView = (MapView) findViewById(R.id.map_SelectMap);
		// Get the Map View’s controller
		mapController = mapView.getController();
		// Configure the map display options
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
		// Zoom in
		mapController.setZoom(17);
		// set other controls
		txtLat = (TextView) findViewById(R.id.txt_location_lat);
		txtLon = (TextView) findViewById(R.id.txt_location_lon);
		txt_name = (TextView) findViewById(R.id.txt_location_name);

		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_launcher);
		itemizedoverlay = new myOverlays(drawable);

		mapView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// // load default values
		// =================================================
		// LocationResult locationResult = new LocationResult() {
		// @Override
		// public void gotLocation(final Location loc) {
		// updateWithNewLocation(loc);
		// }
		// };
		// MyLocation my_location = new MyLocation();
		// my_location.init(this, locationResult);
		// overlay setting

		// below is an open source best method for location
		// i could have used simple manual method shown above but i used only
		// for perfomance
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = LocationManager.NETWORK_PROVIDER;
		Location current = lm.getLastKnownLocation(provider);
		if (current != null)
			updateWithNewLocation(current);

	}

	@Override
	public void onBackPressed() {
		if (place == null) {
			setResult(RESULT_CANCELED);
			finish();
			UIUtilities.message(getApplicationContext(), "no place selected");
		} else {
			Intent resultItent = new Intent();
			resultItent.putExtra("place", place);
			setResult(RESULT_OK, resultItent);
			UIUtilities.message(getApplicationContext(), place.name
					+ " selcted");
			finish();
		}
		super.onBackPressed();
	}

	private void updateWithNewLocation(Location loc) {
		// String lat = loc.getLatitude() + " lat";
		// String lon = loc.getLongitude() + " lat";
		// txtLat.setText("Latitude  :" + lat.length());
		// txtLat.setText("Longitude :" + lon.length());
		// Toast.makeText(Activity_SelectLocation.this,
		// "Lat: " + lat + " Lon:" + lon, Toast.LENGTH_LONG).show();
		// this.location = loc;
		mapPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
				(int) (loc.getLongitude() * 1E6));
		place.lat = mapPoint.getLatitudeE6() / 1E6 + "";
		place.lon = mapPoint.getLongitudeE6() / 1E6 + "";
		txtLat.setText(place.lat);
		txtLon.setText(place.lon);
		mapController.animateTo(mapPoint);
		mapController.setZoom(14);
		OverlayItem overlayitem = new OverlayItem(mapPoint, "Selected City",
				"Selected City");
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);
		new getAddressTask().execute(mapPoint);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int actionType = ev.getAction();
		if (actionType == MotionEvent.ACTION_MOVE) {
			return super.dispatchTouchEvent(ev);
		}

		switch (actionType) {
		case MotionEvent.ACTION_UP:
			Projection proj = mapView.getProjection();
			GeoPoint mapPoint = proj.fromPixels((int) ev.getX(),
					(int) ev.getY());
			int latitude = (int) mapPoint.getLatitudeE6();
			int longitude = (int) mapPoint.getLongitudeE6();
			GeoPoint newPoint = new GeoPoint(latitude, longitude);
			itemizedoverlay.clear();
			OverlayItem overlayitem = new OverlayItem(newPoint,
					"Selected City", "Selected City");
			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
			place.lat = latitude / 1E6 + "";
			place.lon = longitude / 1E6 + "";
			txtLat.setText(place.lat);
			txtLon.setText(place.lon);
			new getAddressTask().execute(mapPoint);
		}
		return super.dispatchTouchEvent(ev);
	}

	class myOverlays extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

		public myOverlays(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		public void clear() {
			this.mOverlays.clear();
		}

		@Override
		public int size() {
			return mOverlays.size();

		}

		@Override
		protected boolean onTap(int index) {
			// OverlayItem item = mOverlays.get(index);
			// // GeoPoint p = mapView.getProjection().fromPixels(
			// // (int) event.getX(), (int) event.getY());
			// new getAddressTask().execute(item.getPoint());
			return true;
		}

	}

	class getAddressTask extends AsyncTask<GeoPoint, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				Activity_SelectLocation.this);

		// can use UI thread here
		protected void onPreExecute() {
			// this.dialog.setMessage("Searching address...");
			// this.dialog.show();
		}

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
			// /String city = "no address found. try again";
			if (result.length() > 0) {
				place.name = result;
			}
			// this.dialog.dismiss();
			// AlertDialog.Builder dialog = new AlertDialog.Builder(
			// Activity_SelectLocation.this);
			// dialog.setTitle("Location");
			// dialog.setMessage(city);
			// dialog.setPositiveButton("Select Location",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// UIUtilities.message(getApplicationContext(),
			// "selected");
			// }
			// });
			// dialog.setNegativeButton("Cancel", null);
			// dialog.show();
			txt_name.setText(place.name);
		}
	}
	/*
	 * Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); try {
	 * List<Address> addresses = geoCoder.getFromLocationName(
	 * "empire state building", 5); String add = ""; if (addresses.size() > 0) {
	 * p = new GeoPoint( (int) (addresses.get(0).getLatitude() * 1E6), (int)
	 * (addresses.get(0).getLongitude() * 1E6)); mc.animateTo(p);
	 * mapView.invalidate(); } } catch (IOException e) { e.printStackTrace(); }
	 */
}