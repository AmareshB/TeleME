package com.remainders.alarma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 2000;
	TextView txt1,txt2;
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
		txt1=(TextView) findViewById(R.id.textView2);
		txt2=(TextView) findViewById(R.id.textView1);
		final String intro = sharedpreferences.getString("checked", "");
		String fontPath = "fonts/artbrush.ttf";
		
		 Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		 
	        // Applying font
	        txt1.setTypeface(tf);
	        txt2.setTypeface(tf);
	        
		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				if(intro.equals("yes")){
				Intent i = new Intent("com.remainders.alarma.FragmentActivity");
				startActivity(i);
				finish();
				}
				else{
					Intent j = new Intent("com.remainders.alarma.Introduction");
					startActivity(j);
					finish();
				}
			}
		}, SPLASH_TIME_OUT);
	}

}
