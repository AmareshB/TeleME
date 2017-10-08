package com.remainders.alarma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.remainders.alarma.intro.ViewPagerAdapter;
public class Introduction extends Activity{

	// Declare Variables
		ViewPager viewPager;
		PagerAdapter adapter;
		int[] flag;
		Editor editor;
		CheckBox chk;
		SharedPreferences sharedpreferences;
		Button b;
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Get the view from viewpager_main.xml
			setContentView(R.layout.introduction);
			chk = (CheckBox)findViewById(R.id.dontshow);
			b = (Button)findViewById(R.id.get);
			sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
			editor = sharedpreferences.edit();
			flag = new int[] { R.drawable.intro1, R.drawable.intro2,
					R.drawable.intro3,R.drawable.intro4};
			viewPager = (ViewPager) findViewById(R.id.pager);
			adapter = new ViewPagerAdapter(this, flag);
			viewPager.setAdapter(adapter);
			b.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(chk.isChecked()){
						editor.putString("checked", "yes");
						editor.commit();
					}
						Intent i = new Intent("com.remainders.alarma.FragmentActivity");
						startActivity(i);
						finish();
						
					
				}
			});
		}
}
