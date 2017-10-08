package com.remainders.alarma.smsscheduler;


import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.remainders.alarma.FragmentActivity;
import com.remainders.alarma.database.PendingIntentsDataSource;

public class SendSMSAlarmService extends Service {
 private String mNumberToSend;
 private String mSMSMessage;
 private String mFrequency;
 private int mId;
 private PendingIntentsDataSource mDatasource;
 private FragmentActivity factivity;
 
 @Override
 public void onCreate() {
  // TODO Auto-generated method stub
	 super.onCreate();
	 factivity = FragmentActivity.getFragActivity();
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
  	Log.v("In", "Service");
  	mNumberToSend = intent.getStringExtra("com.sms.schedule.number");
  	
  	mSMSMessage = intent.getStringExtra("com.sms.schedule.message");
  	mFrequency = intent.getStringExtra("com.sms.schedule.frequency");
  	mId = intent.getIntExtra("com.sms.schedule.id", 0);
  	SmsManager sms = SmsManager.getDefault();
  	
  	if(!mSMSMessage.equals(null)){
  		
  		ArrayList<String> msgStringArray = sms.divideMessage(mSMSMessage);   
  		sms.sendMultipartTextMessage(mNumberToSend, null, msgStringArray, null, null);
  	}
  	
  	if(mFrequency.equalsIgnoreCase("Once")){
  	mDatasource = new PendingIntentsDataSource(getApplicationContext());
    mDatasource.open();
    mDatasource.deletePendingIntent(mId);
    }
  	return START_STICKY_COMPATIBILITY;
 }

 @Override
 public boolean onUnbind(Intent intent) {
  // TODO Auto-generated method stub
  return super.onUnbind(intent);
  
 }

}