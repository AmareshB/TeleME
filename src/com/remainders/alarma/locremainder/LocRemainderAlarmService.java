package com.remainders.alarma.locremainder;


import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.remainders.alarma.FragmentActivity;
import com.remainders.alarma.R;
import com.remainders.alarma.database.LocRemainderDataSource;

public class LocRemainderAlarmService extends Service implements com.google.android.gms.location.LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
 double lat,lng,lat1,lng1,currentLat,currentLon;
 
 protected LocationManager lm;
 String provider;
 LocationRequest locationRequest;
 ConnectionResult connectionResult;
 NotificationManager notificationManager;
 Boolean notified = false;

 
Location l;
 protected LocationListener locationListener;
 private LocRemainderDataSource mDatasource;
 List<LocRemainderPendingIntent> pending;
 private static final long UPDATE_INTERVAL = 300000;
	// Fastest update interval in milliseconds for location services
	private static final long FASTEST_INTERVAL = 120000;	
	private LocationClient locationClient;
	private Location currentLocation;
 @Override
 public void onCreate() {
  // TODO Auto-generated method stub
	 super.onCreate();
	 	
	  	Log.v("Service :", "Inside");
	    lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
	    //Criteria c=new Criteria();
	   locationClient = new LocationClient(this, this, this);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		locationClient.connect();

 }

 @Override
 public IBinder onBind(Intent arg0) {
  // TODO Auto-generated method stub
	 
  return null;
 }
 
 @Override
 public void onDestroy() {
  // TODO Auto-generated method stub
   super.onDestroy();
   mDatasource.close();
 }

@Override
 public int onStartCommand(Intent intent, int startId, int arg) {
  // TODO Auto-generated method stub
  super.onStartCommand(intent, startId, arg);
   
    lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    Criteria c=new Criteria();
    provider=lm.getBestProvider(c, false);
    l=lm.getLastKnownLocation(provider);
  	return START_STICKY_COMPATIBILITY;
 }

 @Override
 public boolean onUnbind(Intent intent) {
  // TODO Auto-generated method stub
	// mDatasource.close();
  return super.onUnbind(intent);
  
 }

@Override
public void onLocationChanged(Location loc) {
	// TODO Auto-generated method stub
	double lng2=loc.getLongitude();
    double lat2=loc.getLatitude();
    mDatasource = new LocRemainderDataSource(getApplicationContext());
    mDatasource.open();
    pending = mDatasource.getAllPendingIntents();
    
    Log.v("OnLocation", "Insise");
    if(pending.size()>0)
    {
    	if(!notified)
    	{
    	for(int i=0;i<pending.size();i++)
    	{
    		lat1 = pending.get(i).getLat();
    		lng1 = pending.get(i).getLon();
    		int notify = pending.get(i).getNotified();
    		if (distance(lat1, lng1, lat2, lng2) < 1.0) {
    			//Toast.makeText(getApplicationContext(), "Remainder = "+pending.get(i).getNotified() , Toast.LENGTH_LONG).show();
    			if(notify==0)
    			Notify(pending.get(i).getMessage(),pending.get(i).getId());
    			//showNotification(pending.get(i).getMessage(),pending.get(i).getId());
    	    }
    		Log.v("Location On changed :", lat1 + "="+lng1 + "="+lat2 + "="+lng2 + "="+distance(lat1, lng1, lat2, lng2));
    	}
    	}
    	
    }
    mDatasource.close();
    
}

private void Notify(String msg,long id){
	mDatasource.open();
	int suc =  mDatasource.updateNotification(1, id);
	mDatasource.close();
	Log.v("Success = ", ""+suc);
	Intent intent = new Intent(this, FragmentActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    long[] vibrationPattern = {0, 200, 800, 200, 600, 200, 400, 200, 200, 200, 100, 200, 50, 200, 50, 200, 50, 200, 50, 200};
	NotificationCompat.Builder mBuilder =
	        new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle("teleME")
	        .setVibrate(vibrationPattern)
	        .setContentText(msg)
	        .setAutoCancel(true);
	
	Intent resultIntent = new Intent(this, FragmentActivity.class);
	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	stackBuilder.addParentStack(FragmentActivity.class);
	stackBuilder.addNextIntent(resultIntent);
	PendingIntent resultPendingIntent =
	        stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_UPDATE_CURRENT
	        );
	mBuilder.setContentIntent(resultPendingIntent);
	NotificationManager mNotificationManager =
	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	mNotificationManager.notify((int)id, mBuilder.build());
	
	
}

private double distance(double lat1, double lng1, double lat2, double lng2) {

    double earthRadius = 3958.75; // in miles, change to 6371 for kilometers

    double dLat = Math.toRadians(lat2-lat1);
    double dLng = Math.toRadians(lng2-lng1);

    double sindLat = Math.sin(dLat / 2);
    double sindLng = Math.sin(dLng / 2);

    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
        * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    double dist = earthRadius * c;

    return dist;
}

@Override
public void onConnectionFailed(ConnectionResult arg0) {
	
	// TODO Auto-generated method stub
	
	
}

@Override
public void onConnected(Bundle arg0) {
	// TODO Auto-generated method stub
	// Indicate that a connection has been established
			currentLocation = locationClient.getLastLocation();
			currentLat = currentLocation.getLatitude();
			currentLon = currentLocation.getLongitude();
			locationClient.requestLocationUpdates(locationRequest, this);
	
}

@Override
public void onDisconnected() {
	// TODO Auto-generated method stub
	locationClient.disconnect();
	
}
}
