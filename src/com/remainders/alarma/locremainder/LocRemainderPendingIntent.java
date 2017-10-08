package com.remainders.alarma.locremainder;


public class LocRemainderPendingIntent {
	private long mId;
	private double mlat;
	private double mlon;
	private String placename;
	private String mMessage;
	private int notified;
	
	public int getNotified(){
		return notified;
	}
	public void setNotified(int n){
		notified = n;
	}
	public long getId(){
		return mId;
	}
	
	public void setId(long id){
		mId = id;
	}
	public String getPlace(){
		return placename;
	}
	public void setPlace(String place){
		placename = place;
	}
	public double getLat(){
		return mlat;
	}
	
	public void setLat(double d){
		mlat = d;
	}
	
	
	public double getLon(){
		return mlon;
	}
	
	public void setLon(double lon){
		mlon = lon;
	}
	
	
	public String getMessage(){
		return mMessage;
	}
	
	public void setMessage(String message){
		mMessage = message;
	}
}
