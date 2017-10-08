package com.remainders.alarma.callremainder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class popUp extends Activity {

	public popUp(String msg){
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
		dlgAlert.setMessage("Remainder: "+msg);
		dlgAlert.setTitle("Call Remainder");
		dlgAlert.setPositiveButton("Ok",
	    new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.cancel();
	        	}
	    });
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
        
	}
}
