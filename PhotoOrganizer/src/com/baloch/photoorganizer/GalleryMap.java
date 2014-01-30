package com.baloch.photoorganizer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import providers.DirectoriesProvider;
import providers.PhotosProvider;
import providers.PlacesProvider;

import models.Photo;
import models.Place;

import utils.UIUtilities;

import com.baloch.photoorganizer.Activity_SelectLocation.getAddressTask;
import com.baloch.photoorganizer.Activity_SelectLocation.myOverlays;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class GalleryMap extends MapActivity {
	private MapView mapView;
	private MapController mapController;
	Spinner sp_places;
	Button view;
	Location location;
	GeoPoint mapPoint;
	List<Overlay> mapOverlays;
	myOverlays itemizedoverlay;
	Drawable drawable;

	private static final int OVERLAY_WIDTH = 80;
	private static final int OVERLAY_HEIGHT = 80;
	ArrayList<Photo> photosList;
	int sp_selected_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_map);
		// LocationManager lm = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// String provider = LocationManager.NETWORK_PROVIDER;
		// Location current = lm.getLastKnownLocation(provider);
		// if (current != null)
		// updateWithNewLocation(current);
		// Get a reference to the MapView
		mapView = (MapView) findViewById(R.id.map_GalleryMap);
		sp_places = (Spinner) findViewById(R.id.spinner1);
		view = (Button) findViewById(R.id.btn_mapgallery_view);

		// Get the Map View’s controller
		mapController = mapView.getController();
		// Configure the map display options
		mapView.setSatellite(true);
		mapView.setBuiltInZoomControls(true);
		// Zoom in
		mapController.setZoom(14);
		// set other controls
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
		itemizedoverlay = new myOverlays(drawable);
		// itemizedoverlay.addOverlay(new OverlayItem(drawable, "", ""))
		// mapOverlays.add(itemizedoverlay);
		// new addOverlays().execute(getPlaces(""));
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent photoShow = new Intent(GalleryMap.this, PhotoShow.class);
				photoShow.putExtra("path", photosList.get(sp_selected_id).path);
				startActivity(photoShow);

			}
		});

		// places = getPlaces("");
		try {
			photosList = PhotosProvider.selectAllWithPlace(
					getApplicationContext(), "");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] placeNames = new String[photosList.size()];
		for (int i = 0; i < placeNames.length; i++) {
			placeNames[i] = DirectoriesProvider.getBaseFileName(photosList
					.get(i).name);
		}
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, placeNames);
		sp_places.setAdapter(adapter);
		ArrayList<Drawable> thumbs = getPhotoThumbsFromPhotos(photosList,
				OVERLAY_HEIGHT, OVERLAY_WIDTH);
		itemizedoverlay = addPhotosOverlays(photosList);
		itemizedoverlay.setThumbs(thumbs);
		itemizedoverlay.populateNow();
		mapOverlays.add(itemizedoverlay);

		sp_places.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				locationToPhoto(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		Photo photo = null;
		if (getIntent().getExtras() != null)
			if ((photo = (Photo) getIntent().getExtras().getSerializable(
					"photo")) != null) {
				int position = getPosition(photo);
				if (position != -1)
					locationToPhoto(position);
			}

	}

	private int getPosition(Photo photo) {
		int totalPHotos = photosList.size();
		for (int i = 0; i < totalPHotos; i++) {
			if (photo.id == photosList.get(i).id) {
				return i;
			}
		}
		return -1;
	}

	private ArrayList<Place> getPlaces(String append) {
		try {
			return PlacesProvider.selectAll(getApplicationContext(), append);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private myOverlays addPhotosOverlays(ArrayList<Photo> photos) {
		myOverlays overlayClass = new myOverlays(drawable);
		for (int i = 0; i < photos.size(); i++) {
			mapPoint = new GeoPoint(
					(int) (Double.parseDouble(photos.get(i).place.lat) * 1E6),
					(int) (Double.parseDouble(photos.get(i).place.lon) * 1E6));
			OverlayItem overlayitem = new OverlayItem(mapPoint,
					+photos.get(i).id + "", photos.get(i).place.name);
			overlayClass.addOverlay(overlayitem);
		}
		return overlayClass;

	}

	private myOverlays addPlacesOverlays(ArrayList<Place> places) {
		myOverlays overlayClass = new myOverlays(drawable);
		int totalPlaces = places.size();
		for (int i = 0; i < totalPlaces; i++) {
			String lat = places.get(i).lat;
			String lon = places.get(i).lon;
			double latt = Double.parseDouble(lat);
			double lonn = Double.parseDouble(lon);
			latt = latt * 1E6;
			lonn = lonn * 1E6;
			int lattt = (int) latt;
			int lonnn = (int) lonn;
			mapPoint = new GeoPoint(lattt, lonnn);
			OverlayItem overlayitem = new OverlayItem(mapPoint, i + "",
					PhotosProvider.getByPlace(getApplicationContext(),
							places.get(i)).size()
							+ "");
			overlayClass.addOverlay(overlayitem);
		}
		return overlayClass;

	}

	private ArrayList<Drawable> getPhotoThumbs(ArrayList<Place> places_list,
			int height, int width) {
		ArrayList<Photo> photosList = new ArrayList<Photo>();
		ArrayList<Drawable> drawables = new ArrayList<Drawable>();
		for (Place p : places_list) {
			try {
				photosList.addAll(PhotosProvider.selectAll(
						getApplicationContext(), "placeId = " + p.id));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		for (Photo ph : photosList) {
			drawables.add(new BitmapDrawable(DirectoriesProvider.getThumb(
					ph.path, width, height)));
		}
		return drawables;
	}

	private ArrayList<Drawable> getPhotoThumbsFromPhotos(
			ArrayList<Photo> places_list, int height, int width) {
		ArrayList<Drawable> drawables = new ArrayList<Drawable>();
		for (int i = 0; i < places_list.size(); i++) {
			drawables.add(
					i,
					new BitmapDrawable(DirectoriesProvider.getThumb(
							places_list.get(i).path, width, height)));
		}
		return drawables;
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

		mapController.animateTo(mapPoint);
		mapController.setZoom(14);
		OverlayItem overlayitem = new OverlayItem(mapPoint, "Selected City",
				"Selected City");
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);
		// new getAddressTask().execute(mapPoint);
	}

	class myOverlays extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		ArrayList<Drawable> placesThum = new ArrayList<Drawable>();

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
		}

		public void setThumbs(ArrayList<Drawable> thumbs) {
			placesThum = thumbs;
			while (mOverlays.size() > thumbs.size())
				mOverlays.remove(mOverlays.size() - 1);

		}

		public void populateNow() {
			populate();
		}

		public myOverlays(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		@Override
		protected OverlayItem createItem(int i) {
			OverlayItem item = mOverlays.get(i);
			Drawable thumb = boundCenterBottom(placesThum.get(i));
			item.setMarker(thumb);
			return item;
		}

		public void clear() {
			this.mOverlays.clear();
		}

		@Override
		public int size() {
			return mOverlays.size();

		}

		public OverlayItem getItemAt(int i) {
			return mOverlays.get(i);
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			int photoId = Integer.parseInt(item.getTitle());
			String itemPlace = item.getSnippet();
			// UIUtilities
			// .message(getApplicationContext(), "PLocation  :" + itemPlace);

			Intent photoShow = new Intent(GalleryMap.this, PhotoShow.class);
			photoShow.putExtra("path", photosList.get(photoId - 1).path);
			startActivity(photoShow);

			// GeoPoint p = mapView.getProjection().fromPixels(
			// (int) event.getX(), (int) event.getY());
			// new getAddressTask().execute(item.getPoint());
			return true;
		}
	}

	class addOverlays extends AsyncTask<ArrayList<Place>, Void, myOverlays> {
		private final ProgressDialog dialog = new ProgressDialog(
				GalleryMap.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Adding places to map...");
			this.dialog.show();
		}

		@Override
		protected myOverlays doInBackground(ArrayList<Place>... params) {
			return addPlacesOverlays(params[0]);
		}

		@Override
		protected void onPostExecute(myOverlays result) {
			mapOverlays.clear();
			mapOverlays.add(result);

			// /String city = "no address found. try again";
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
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void locationToPhoto(int position) {
		mapPoint = new GeoPoint(
				(int) (Double.parseDouble(photosList.get(position).place.lat) * 1E6),
				(int) (Double.parseDouble(photosList.get(position).place.lon) * 1E6));
		mapController.animateTo(mapPoint);
	}
}
