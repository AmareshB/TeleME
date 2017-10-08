package com.remainders.alarma.callremainder;


import com.remainders.alarma.R;
import com.remainders.alarma.database.dbforsmart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class OutgoingCallReceiver extends BroadcastReceiver{

	String phoneNumber;
	String call;
	String contactName;
	
	//Context context = null;
	
	@Override
	public void onReceive(final Context context, Intent intent){
		//TODO: Handle outgoing call event here
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		dbforsmart db = new dbforsmart(context);
		final Toast toast = new Toast(context);
		/* Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		  Cursor cursor = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, phoneNumber, null, null );
		  if(cursor.moveToFirst()){
		        contactName =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		  }
		  cursor.close();*/
		db.open();
		String msg = db.getmessage(phoneNumber);
		db.close();

		if(msg!="")
		{
		LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.callremainder_popup, null, false);
	    TextView txt = (TextView) layout.findViewById(R.id.text);
	    txt.setText(msg);
	    final ImageButton dismiss = (ImageButton) layout.findViewById(R.id.dismissImage);
	    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
	    toast.setDuration(Toast.LENGTH_LONG);
	    toast.setView(layout);
		
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
		            		for(;;){
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

		                Log.d("Exception: " + e.toString(),"");
		            }
		        }
		    };
		    t.start();
		}
		else
		{
			
		}

		Log.d(OutgoingCallReceiver.class.getSimpleName(), intent.toString());
		
		
	} 
	
}
