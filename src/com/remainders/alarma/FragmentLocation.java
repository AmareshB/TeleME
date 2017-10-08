package com.remainders.alarma;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.remainders.alarma.database.LocRemainderDataSource;
import com.remainders.alarma.locremainder.LocRemainderAdapter;
import com.remainders.alarma.locremainder.LocRemainderAlarmService;
import com.remainders.alarma.locremainder.LocRemainderPendingIntent;

@SuppressWarnings("deprecation")
public class FragmentLocation extends Fragment implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
com.google.android.gms.location.LocationListener, 
com.google.android.gms.maps.GoogleMap.OnMapClickListener,
OnMapLongClickListener, OnMarkerClickListener,
GoogleMap.OnInfoWindowClickListener {
	
	int id=0;
	// Update interval in milliseconds for location services
	private static final long UPDATE_INTERVAL = 300000;
	// Fastest update interval in milliseconds for location services
	private static final long FASTEST_INTERVAL = 5000;	
	// Google Play diagnostics constant
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;	
	private static final String TAG = "Mapper";
	private LocationClient locationClient;
	private Location currentLocation;
	private String mMessage;
	private double currentLat;
	private double currentLon;
	private GoogleMap map;
	private static LatLng map_center;
	private int zoomOffset = 5;
	private float currentZoom;
	private float bearing;
	private float speed;
	private float acc;
	private static double lon;
	private static double lat;
	static final int numberOptions = 10;
	String [] optionArray = new String[numberOptions];
	String place = null;
	int Delete = 1;
	// Define an object that holds accuracy and frequency parameters
	LocationRequest locationRequest;
	private LocRemainderDataSource mDatasource;
	MapView mapview;
	//private static LocationActivity mLocationActivity;
	Calendar c;
	// Set up shared preferences to persist data.  We will use it later
	// to save the current zoom level if user leaves this activity, and
	// restore it when she returns.
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	Button search,slideButton;
	TextView noloc;
	EditText loc;
	NotificationManager notificationManager;
	private int mId;
	private Boolean isOneTime=false;
	SwipeListView swipelistview;
	LocRemainderAdapter adapter;
	List<LocRemainderPendingIntent> itemData;
	String name;
	int pos;
	SlidingDrawer slidingDrawer;
	List<LocRemainderPendingIntent> values;
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

	View rootView;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragmentlocation, container, false);
		slideButton = (Button) rootView.findViewById(R.id.slideButtonloc);
		swipelistview=(SwipeListView)rootView.findViewById(R.id.locationswipelist); 
        slidingDrawer = (SlidingDrawer) rootView.findViewById(R.id.SlidingDrawerloc); 
        noloc = (TextView)rootView.findViewById(R.id.noloc);
        itemData=new ArrayList<LocRemainderPendingIntent>();
        adapter=new LocRemainderAdapter(getActivity().getApplicationContext(),R.layout.locremainder_customrow,itemData);
        
		c = Calendar.getInstance();
		// Get a handle to the Map Fragmentr
		map = ((MapFragment) getActivity().getFragmentManager().findFragmentByTag(getString(R.string.map)))
	          .getMap();
		//mapview = (MapView) rootView.findViewById(R.id.mapview);
        //mapview.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        //map = mapview.getMap();
		search = (Button)rootView.findViewById(R.id.search);
		loc = (EditText)rootView.findViewById(R.id.searchlocation);
		if(map != null){

			// Set the initial zoom level of the map
			currentZoom = map.getMaxZoomLevel()-zoomOffset;

			// Add a click listener to the map
			map.setOnMapClickListener(this);

			// Add a long-press listener to the map
			map.setOnMapLongClickListener(this);

			// Add Marker click listener to the map
			map.setOnMarkerClickListener(this);

			// Add marker info window click listener
			map.setOnInfoWindowClickListener(this);
			

		} else {
			Toast.makeText(getActivity(), "Map error", 
					Toast.LENGTH_LONG).show();
		}
		
search.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Geocoder.isPresent()){
								
					String placeName = loc.getText().toString();
					place = placeName;
					// Break from execution if the user has not entered anything in the field
					if(placeName.compareTo("")==0) 
						//break;
					{
						Toast.makeText(getActivity(), "Enter place to search", Toast.LENGTH_SHORT).show();
					}
					else{
					geocodeLocation(placeName);
					putMapData(lat, lon, 18, true);
					}
					/*Intent j = getIntent();
					startActivity(j);
					finish();*/
					initializeMap();
				} else {
					String noGoGeo = "FAILURE: No Geocoder on this platform.";
					//Toast.makeText(this, noGoGeo, Toast.LENGTH_LONG).show();
					loc.setText(noGoGeo);
					return;
				}
			}
		});


		locationClient = new LocationClient(getActivity(), this, this);

		// Create the LocationRequest object
		locationRequest = LocationRequest.create();
		// Set request for high accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set update interval
		locationRequest.setInterval(UPDATE_INTERVAL);
		// Set fastest update interval that we can accept
		locationRequest.setFastestInterval(FASTEST_INTERVAL);

		// Get a shared preferences
		prefs = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		prefsEditor = prefs.edit();

		// Keep screen on while this map location tracking activity is running
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener(){

			@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
		        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
		            @Override
		            public void onOpened(int position, boolean toRight) {
		            	
		            }

		            @Override
		            public void onClosed(int position, boolean fromRight) {
		            		
		            }

		            @Override
					public int onChangeSwipeMode(int position) {
						// TODO Auto-generated method stub
		            	return super.onChangeSwipeMode(position);
					}

					@Override
		            public void onListChanged() {
		            }

		            @Override
		            public void onMove(int position, float x) {
		            }

		            @Override
		            public void onStartOpen(int position, int action, boolean right) {
		                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
		            }

		            @Override
		            public void onStartClose(int position, boolean right) {
		                Log.d("swipe", String.format("onStartClose %d", position));
		            }

		            @Override
		            public void onClickFrontView(int position) {
		                Log.d("swipe", String.format("onClickFrontView %d", position));
		                //swipelistview.openAnimate(position); //when you touch front view it will open
		                //Toast.makeText(getApplicationContext(),""+position, Toast.LENGTH_SHORT).show();
		                
		            }

		            @Override
		            public void onClickBackView(int position) {
		                Log.d("swipe", String.format("onClickBackView %d", position));
		                //swipelistview.closeAnimate(position);//when you touch back view it will close
		            }

		            @Override
		            public void onDismiss(int[] reverseSortedPositions) {
		            }

		        });
		        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		        swipelistview.setOffsetRight(convertDpToPixel(220f));
		        swipelistview.setAnimationTime(300); // Animation time
		        swipelistview.setAdapter(adapter);
		        swipelistview.setLongClickable(false);
		        mDatasource = new LocRemainderDataSource(getActivity().getApplicationContext());
			    mDatasource.open();
			    values = mDatasource.getAllPendingIntents();
		        mDatasource.close();
		        if(values.size()>0){
		        for(int i=0;i<values.size();i++)
		        {
		        	itemData.add(values.get(i));
		        	
		        }
		        }
		        else
		        	noloc.setVisibility(View.VISIBLE);
		        adapter.notifyDataSetChanged();
				
			}
			
		});
		
		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				itemData.clear();
				adapter.notifyDataSetChanged();
				noloc.setVisibility(View.GONE);
			}
		});
        
		return rootView;
	}

	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    
	

	


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		FragmentManager fm = getActivity().getFragmentManager();
	    Fragment fragment = (fm.findFragmentByTag(getString(R.string.map)));
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.remove(fragment);
	    ft.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//outState.clear();
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED 
		    || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

		    Toast.makeText(getActivity(), "Not connected to internet", Toast.LENGTH_SHORT).show();
		}
		LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
		 boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		 if(!statusOfGPS){
			 Toast.makeText(getActivity(), "Turn on GPS", Toast.LENGTH_SHORT).show();
		 }
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(map == null) {
			Toast.makeText(getActivity(), "Map error", 
					Toast.LENGTH_LONG).show();
			return false;
		}
		// Handle item selection
		switch (item.getItemId()) {
			// Toggle satellite overlay
		case R.id.satellite_mapme:
			int mt = map.getMapType();
			if(mt == GoogleMap.MAP_TYPE_NORMAL){
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			} else {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
			return true;

			// Toggle 3D building display
				case R.id.track_mapme:
			if(locationClient != null){
				if(locationClient.isConnected()){
					stopTracking();
				} else {
					startTracking();
				}
			}
			return true;
		
		default:
			
		}
		return super.onOptionsItemSelected(item);
	}

	// Save the current zoom level when going into the background

	@Override
	public void onPause() {

		// Store the current map zoom level
		if(map != null){
			currentZoom = map.getCameraPosition().zoom;
			prefsEditor.putFloat("KEY_ZOOM",currentZoom);
			prefsEditor.commit();  
		}
		super.onPause();
		
		Log.i(TAG,"onPause: Zoom="+currentZoom);
	}

	@Override
	public void onResume() {
		super.onResume();

		// Restore previous zoom level (default to max zoom level if
		// no prefs stored)

		if (prefs.contains("KEY_ZOOM") && map != null){
			currentZoom = prefs.getFloat("KEY_ZOOM", map.getMaxZoomLevel());
		}
		Log.i(TAG,"onResume: Zoom="+currentZoom);

		// Keep screen on while this map location tracking activity is running
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	/* The following two lifecycle methods conserve resources by ensuring that
	 * location services are connected when the map is visible and disconnected when
	 * it is not.
	 */

	// Called by system when Activity becomes visible, so connect location client.

	@Override
	public void onStart() {
		super.onStart();
		locationClient.connect();
	}

	// Called by system when Activity is no longer visible, so disconnect location
	// client, which invalidates it.

	@Override
	public void onStop() {

		// If the client is connected, remove location updates and disconnect
		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();

		// Turn off the screen-always-on request
		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onStop();
	}

	// The following three callbacks indicate connections, disconnections, and 
	// connection errors, respectively.


	/* Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request current location or begin periodic location updates.
	 */

	@Override
	public void onConnected(Bundle dataBundle) {

		// Indicate that a connection has been established
		Toast.makeText(getActivity(), "Initializing map", 
				Toast.LENGTH_SHORT).show();

		currentLocation = locationClient.getLastLocation();
		try{
		currentLat = currentLocation.getLatitude();
		currentLon = currentLocation.getLongitude();
		}
		catch(Exception e){
			Toast.makeText(getActivity(), "GPS is off", Toast.LENGTH_LONG).show();
		}
		// Center map on current location
		map_center = new LatLng(currentLat,currentLon);

		if(map != null) {
			initializeMap();
		} else {
			Toast.makeText(getActivity(), "Map error", 
					Toast.LENGTH_LONG).show();
		}

		// Start periodic updates.  This version of requestLocationUpdates is 
		// suitable for foreground activities when connected to a LocationClient. 
		// The second argument is the LocationListener, which is "this" since the 
		// present class implements the LocationListener interface.

		locationClient.requestLocationUpdates(locationRequest, this);
	}

	// Called by Location Services if the connection to location client fails

	@Override
	public void onDisconnected() {
		Toast.makeText(getActivity(), "Disconnected",
				Toast.LENGTH_SHORT).show();
	}

	// Called by Location Services if the attempt to connect to
	// Location Services fails.

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/* Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve the error.
		 */

		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

				// Thrown if Google Play services canceled the original PendingIntent

			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			// If no resolution is available, display a dialog with the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public void showErrorDialog(int errorCode){
		Log.e(TAG, "Error_Code ="+errorCode);
		// Create an error dialog display here
	}

	// Method to initialize the map.  Check that map != null before calling.

	private void initializeMap(){

		// Enable or disable current location
		map.setMyLocationEnabled(true);

		// Move camera view and zoom to location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(map_center,currentZoom));

		// Initialize type of map to normal
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Initialize 3D buildings enabled for map view
		map.setBuildingsEnabled(false);

		// Initialize whether indoor maps are shown if available
		map.setIndoorEnabled(false);

		// Initialize traffic overlay
		map.setTrafficEnabled(false);

		// Disable rotation gestures
		map.getUiSettings().setRotateGesturesEnabled(false);
		
		
		
	}

	// Starts location tracking
	private void startTracking(){
		locationClient.connect();
		Toast.makeText(getActivity(), "Location tracking started", Toast.LENGTH_SHORT).show(); 

	}

	// Stops location tracking
	private void stopTracking(){
		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();   
		Toast.makeText(getActivity(), "Location tracking halted", Toast.LENGTH_SHORT).show();
	}

	/* Method to add map marker at give latitude and longitude.  The third arg is a float
	 * variable defining color for the marker.  Pre-defined marker colors may be found at
	 *     http://developer.android.com/reference/com/google/android/gms/maps/model
	 *     /BitmapDescriptorFactory.html
	 * and should be specified in the format BitmapDescriptorFactory.HUE_RED, which is
	 * the default color, but various other ones are defined there such as HUE_ORANGE, 
	 * HUE_BLUE, HUE_GREEN, ... The arguments title and snippet are for the window that
	 * will open if one clicks on the marker.
	 */	

	private void addMapMarker (double lat, double lon, float markerColor,
			String title, String snippet){

		if(map != null){
			Marker marker = map.addMarker(new MarkerOptions()
			.title(title)
			.snippet(snippet)
			.position(new LatLng(lat,lon))
			.icon(BitmapDescriptorFactory.defaultMarker(markerColor))
			);
			marker.setDraggable(false);
			marker.showInfoWindow();
		} else {
			Toast.makeText(getActivity(),"Map error", 
					Toast.LENGTH_LONG).show();
		}

	}

	// Decimal output formatting class that uses Java DecimalFormat. See
	// http://developer.android.com/reference/java/text/DecimalFormat.html.
	// The string "formatPattern" specifies the output formatting pattern for
	// the float or double. For example, 35.8577877288 will be returned 
	// as the string "35.85779" if formatPattern = "0.00000", and as
	// the string "3.586E01" if formatPattern = "0.000E00".

	public String formatDecimal(double number, String formatPattern){

		DecimalFormat df = new DecimalFormat(formatPattern);

		// The method format(number) with a single argument is inherited by 
		// DecimalFormat from NumberFormat.

		return df.format(number);

	}

	/* Method to change properties of camera. If your GoogleMaps instance is called map, 
	 * you can use 
	 * 
	 * map.getCameraPosition().target
	 * map.getCameraPosition().zoom
	 * map.getCameraPosition().bearing
	 * map.getCameraPosition().tilt
	 * 
	 * to get the current values of the camera position (target, which is a LatLng), 
	 * zoom, bearing, and tilt, respectively.  This permits changing only a subset of
	 * the camera properties by passing the current values for all arguments you do not
	 * wish to change.
	 * 
	 * */

	@SuppressWarnings("unused")
	private void changeCamera(GoogleMap map, LatLng center, float zoom, 
			float bearing, float tilt, boolean animate) {

		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(center)         // Sets the center of the map
		.zoom(zoom)             // Sets the zoom
		.bearing(bearing)       // Sets the bearing of the camera 
		.tilt(tilt)             // Sets the tilt of the camera relative to nadir
		.build();               // Creates a CameraPosition from the builder

		// Move (if variable animate is false) or animate (if animate is true) to new 
		// camera properties. 

		if(animate){
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		} else {
			map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}


	// Following callback associated with implementing LocationListener.
	// It fires when a location change is detected, passing in the new
	// location as the variable "newLocation".

	@Override
	public void onLocationChanged(Location newLocation) {

		bearing = newLocation.getBearing();
		speed = newLocation.getSpeed();
		acc = newLocation.getAccuracy();

		// Get latitude and longitude of updated location	
		double lat = newLocation.getLatitude();
		double lon = newLocation.getLongitude();
		new LatLng(lat, lon);

		// Return a bundle of additional location information, if available.
		// This will return null if no extras are available, so check for
		// null before using this Bundle.

		Bundle locationExtras = newLocation.getExtras();
		// If there is no satellite info, return -1 for number of satellites
		int numberSatellites = -1;
		if(locationExtras != null){
			Log.i(TAG, "Extras:"+locationExtras.toString());
			if(locationExtras.containsKey("satellites")){
				numberSatellites = locationExtras.getInt("satellites");
			}
		}

		// Log some basic location information
		Log.i(TAG,"Lat="+formatDecimal(lat,"0.00000")
				+" Lon="+formatDecimal(lon,"0.00000")
				+" Bearing="+formatDecimal(bearing, "0.0")
				+" deg Speed="+formatDecimal(speed, "0.0")+" m/s"
				+" Accuracy="+formatDecimal(acc, "0.0")+" m"
				+" Sats="+numberSatellites);
	}


	// Method to reverse-geocode location passed as latitude and longitude. It returns a string which
	// is the first reverse-geocode location in the returned list.  (The full list is output to the
	// logcat stream.) This method returns null if no geocoder backend is available.

	@SuppressLint("NewApi")
	private String reverseGeocodeLocation(double latitude, double longitude){

		// Use to suppress country in returned address for brevity
		boolean omitCountry = true;

		// String to hold single address that will be returned
		String returnString = "";

		Geocoder gcoder = new Geocoder(getActivity());

		// Note that the Geocoder uses synchronous network access, so in a serious application
		// it would be best to put it on a background thread to prevent blocking the main UI if network
		// access is slow. Here we are just giving an example of how to use it so, for simplicity, we
		// don't put it on a separate thread.  See the class RouteMapper in this package for an example
		// of making a network access on a background thread. Geocoding is implemented by a backend
		// that is not part of the core Android framework, so it is not guaranteed to be present on
		// every device.  Thus we use the static method Geocoder.isPresent() to test for presence of 
		// the required backend on the given platform.

		try{
			List<Address> results = null;
			if(Geocoder.isPresent()){
				results = gcoder.getFromLocation(latitude, longitude, numberOptions);
			} else {
				Log.i(TAG,"No geocoder accessible on this device");
				return null;
			}
			Iterator<Address> locations = results.iterator();
			String raw = "\nRaw String:\n";
			String country;
			int opCount = 0;
			while(locations.hasNext()){
				Address location = locations.next();
				if(opCount==0 && location != null){
					lat = location.getLatitude();
					lon = location.getLongitude();
				}
				country = location.getCountryName();
				if(country == null) {
					country = "";
				} else {
					country =  ", "+country;
				}
				raw += location+"\n";
				optionArray[opCount] = location.getAddressLine(0)+", "
				+location.getAddressLine(1)+country+"\n";
				if(opCount == 0){
					if(omitCountry){
						returnString = location.getAddressLine(0)+", "
						+location.getAddressLine(1)+"\n";
					} else {
						returnString = optionArray[opCount];
					}
				}
				opCount ++;
			}
			Log.i(TAG, raw);
			Log.i(TAG,"\nOptions:\n");
			for(int i=0; i<opCount; i++){
				Log.i(TAG,"("+(i+1)+") "+optionArray[i]);
			}
			Log.i(TAG,"lat="+lat+" lon="+lon);

			//Toast.makeText(this, optionArray[0], Toast.LENGTH_LONG).show();

		} catch (IOException e){
			Log.e(TAG, "I/O Failure",e);
		}

		// Return the first location entry in the list.  A more sophisticated implementation 
		// would present all location entries in optionArray to the user for choice when more 
		// than one is returned by the geodecoder.

		return returnString;	

	}


	// Callback that fires when map is tapped, passing in the latitude
	// and longitude coordinates of the tap (actually the point on the ground 
	// projected from the screen tap).  This will be invoked only if no overlays 
	// on the map intercept the click first.  Here we will just issue a Toast
	// displaying the map coordinates that were tapped.  See the onMapLongClick
	// handler for an example of additional actions that could be taken.

	@Override
	public void onMapClick(LatLng latlng) {
	}

	// This callback fires for long clicks on the map, passing in the LatLng coordinates

	@Override
	public void onMapLongClick(LatLng latlng) {

		double lat = latlng.latitude;
		double lon = latlng.longitude;

		String title = reverseGeocodeLocation(latlng.latitude, latlng.longitude);
		
		String snippet="Tap marker to add,"+"\n"+"Tap window to delete";

		// Add an orange marker on map at position of tap (default marker color is red).
		addMapMarker(lat, lon, BitmapDescriptorFactory.HUE_ORANGE, title, snippet);

		// Add a circle centered on the marker given the current position uncertainty
		// Keep a reference to the returned circle so we can remove it later.

		//localCircle = addCircle (lat, lon, acc, "#00000000", "#40ff9900");

	}


	/* Add a circle at (lat, lon) with specified radius. Stroke and fill colors are specified
	 * as strings. Valid strings are those valid for the argument of Color.parseColor(string):
	 * for example, "#RRGGBB", "#AARRGGBB", "red", "blue", ...
	 */

	

	// Process clicks on markers

	@Override
	public boolean onMarkerClick(Marker marker) {

		// Remove the marker and its info window and circle if marker clicked
		//marker.remove();
		final Context context=getActivity();
		final double lat = marker.getPosition().latitude;
		final double lon = marker.getPosition().longitude;
		final String placename = marker.getTitle();
		//LayoutInflater layoutInflater = LayoutInflater.from(context);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		final EditText input = new EditText(getActivity());
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialogBuilder.setTitle("Type in your message");
		alertDialogBuilder.setView(input);
		// setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@SuppressWarnings("static-access")
							public void onClick(DialogInterface dialog, int id) {
								// get user input and set it to result
								mMessage=input.getText().toString();
								 Intent i = new Intent(getActivity().getApplicationContext(), LocRemainderAlarmService.class);
								    i.putExtra("com.location.remainder.message",mMessage);
								    mId = (int) System.currentTimeMillis();
								    i.putExtra("com.location.remainder.id", mId);
								    AlarmManager am = (AlarmManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().ALARM_SERVICE);
								    PendingIntent pi = PendingIntent.getService(getActivity().getApplicationContext(), mId, i, PendingIntent.FLAG_UPDATE_CURRENT);
								    long mInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
								    if(isOneTime == false){
								    	addToDatabase(true,lat,lon,mMessage,placename,mId);
								    	am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), mInterval, pi);
								    	getActivity().startService(i);
								    	}
								    if(isOneTime == true){
								    	
								    	addToDatabase(false,lat,lon,mMessage,placename,mId);
								    	am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pi);
								    	getActivity().startService(i);
								    }
								    Toast.makeText(getActivity().getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertD = alertDialogBuilder.create();
		alertD.show();
		marker.remove();
		
		return true;

	}

	// Process clicks on the marker info window

	@Override
	public void onInfoWindowClick(Marker marker) {

		
				marker.remove();

	}

	@SuppressWarnings("unused")
	private void addToDatabase(boolean isRepeated,double lat,double lon,String msg,String placename,int mId){
		
		 id++;
		 mDatasource = new LocRemainderDataSource(getActivity());
	     mDatasource.open();
	        
		 Log.i(TAG,id+" = "+placename+" = "+lat+" = "+lon+" = "+isRepeated+" = "+msg);
		 if(placename==null){
			 Toast.makeText(getActivity().getApplicationContext(), "Placename = "+placename, Toast.LENGTH_LONG).show();
		 }
		 else{
		if(isRepeated == true){		
				LocRemainderPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId,lat,lon ,msg,placename,0);
			}
			if(isRepeated == false){
				LocRemainderPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId,lat,lon, msg,placename,0);
			}
		 }
		 mDatasource.close();
		}


	/*
	private void showStreetView(double lat, double lon ){		
		String uriString = "google.streetview:cbll="+lat+","+lon;
		Intent streetView = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(uriString));
		startActivity(streetView);	
	}
	*/
	public static void putMapData(double latitude, double longitude, int zoom, boolean track){
		lat = latitude;
		lon= longitude;
		map_center = new LatLng(lat,lon);
	}
	@SuppressLint("NewApi")
	private void geocodeLocation(String placeName){

		// Following adapted from Conder and Darcey, pp.321 ff.		
		Geocoder gcoder = new Geocoder(getActivity());

		// Note that the Geocoder uses synchronous network access, so in a serious application
		// it would be best to put it on a background thread to prevent blocking the main UI if network
		// access is slow. Here we are just giving an example of how to use it so, for simplicity, we
		// don't put it on a separate thread.  See the class RouteMapper in this package for an example
		// of making a network access on a background thread. Geocoding is implemented by a backend
		// that is not part of the core Android framework, so we use the static method 
		// Geocoder.isPresent() to test for presence of the required backend on the given platform.

		try{
			List<Address> results = null;
			if(Geocoder.isPresent()){
				results = gcoder.getFromLocationName(placeName,numberOptions);
			} else {
				Log.i(TAG,"No geocoder accessible on this platform");
				return;
			}
			Iterator<Address> locations = results.iterator();
			String raw = "\nRaw String:\n";
			String country;
			int opCount = 0;
			while(locations.hasNext()){
				Address location = locations.next();
				if(opCount==0 && location != null){
					lat = location.getLatitude();
					lon = location.getLongitude();
				}
				country = location.getCountryName();
				if(country == null) {
					country = "";
				} else {
					country =  ", "+country;
				}
				raw += location+"\n";
				optionArray[opCount] = location.getAddressLine(0)+", "
				+location.getAddressLine(1)+country+"\n";
				opCount ++;
			}
			Log.i(TAG, raw);
			Log.i(TAG,"\nOptions:\n");
			for(int i=0; i<opCount; i++){
				Log.i(TAG,"("+(i+1)+") "+optionArray[i]);
			}
			Log.i(TAG,"lat="+lat+" lon="+lon);

		} catch (IOException e){
			Log.e(TAG, "I/O Failure; is network available?",e);
		}
	}
	
}
