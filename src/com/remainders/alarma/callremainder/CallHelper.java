package com.remainders.alarma.callremainder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.remainders.alarma.FragmentActivity;
import com.remainders.alarma.R;
import com.remainders.alarma.database.dbforsmart;

public class CallHelper {
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// called when someone is ringing to this phone
				int id=0;
				dbforsmart db = new dbforsmart(ctx);
				final Toast toast = new Toast(ctx);
				db.open();
				String msg = db.getmessage(incomingNumber);
				db.close();
				System.out.println("Inside call "+incomingNumber);
				if(msg!="")
				{
				LayoutInflater inflater = (LayoutInflater) ctx
			            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    View layout = inflater.inflate(R.layout.callremainder_popup, null, false);
			    TextView txt = (TextView) layout.findViewById(R.id.text);
			    txt.setText(msg);
			    final ImageButton dismiss = (ImageButton) layout.findViewById(R.id.dismissImage);
			    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
			    toast.setDuration(Toast.LENGTH_LONG);
			    toast.setView(layout);
			    
			    Notify(msg,id++);
			    Thread t = new Thread() {
						 int x=0;
						public void run() {
				        	
				        	dismiss.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									x++;
								}
							});  
				            try {
				            		for(int i=0;i<200;i++)
				            		{
				            		if(x==0)
				            			toast.show();
				            		else{
				            			toast.cancel();
				            			break;
				            		}
				            			
				            		}
				                    sleep(2000);
				                }
				             catch (Exception e) {
				            }
				        }
				    };
				    t.start();
				}
				else
				{
					
				}
				break;
			}
		}
	}
	
	/**
	 * Broadcast receiver to detect the outgoing calls.
	 */
	public class OutgoingReceiver extends BroadcastReceiver {
	    public OutgoingReceiver() {
	    }

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	        int id=0;
	        dbforsmart db = new dbforsmart(ctx);
			final Toast toast = new Toast(ctx);
			db.open();
			String msg = db.getmessage(number);
			db.close();
			//System.out.println(phoneNumber);
			if(msg!="")
			{
			LayoutInflater inflater = (LayoutInflater) ctx
		            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View layout = inflater.inflate(R.layout.callremainder_popup, null, false);
		    TextView txt = (TextView) layout.findViewById(R.id.text);
		    txt.setText(msg);
		    final ImageButton dismiss = (ImageButton) layout.findViewById(R.id.dismissImage);
		    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
		    toast.setDuration(Toast.LENGTH_LONG);
		    toast.setView(layout);
		    Notify(msg,id++);
			
				Thread t = new Thread() {
					 int x=0;
					public void run() {
			        	
			        	dismiss.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								x++;
							}
						});  
			            try {
			            		for(int i=0;i<200;i++){
			            		if(x==0)
			            			toast.show();
			            		else{
			            			toast.cancel();
			            			break;
			            		}
			            			
			            		}
			                    sleep(2000);
			                }
			             catch (Exception e) {

			            }
			        }
			    };
			    t.start();
			}
			else
			{
				
			}
			
	    }
  
	}
	private Context ctx;
	private TelephonyManager tm;
	private CallStateListener callStateListener;
	private OutgoingReceiver outgoingReceiver;
	public CallHelper(Context ctx) {
		this.ctx = ctx;
		
		callStateListener = new CallStateListener();
		outgoingReceiver = new OutgoingReceiver();
	}
	
	private void Notify(String msg,int id){
		
		Intent intent = new Intent(ctx, FragmentActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
	    long[] vibrationPattern = {0, 200, 800, 200, 600, 200, 400};
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(ctx)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("teleME")
		        .setVibrate(vibrationPattern)
		        .setContentText("Remainder: "+msg)
		        .setAutoCancel(true);
		
		Intent resultIntent = new Intent(ctx, FragmentActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(FragmentActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);
		mNotificationManager.notify((int)id, mBuilder.build());
		
		
	}

	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		ctx.registerReceiver(outgoingReceiver, intentFilter);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
		ctx.unregisterReceiver(outgoingReceiver);
	}

}
