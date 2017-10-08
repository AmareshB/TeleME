package com.remainders.alarma;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.remainders.alarma.database.PendingIntentsDataSource;
import com.remainders.alarma.smsscheduler.CancelAlarmAdapter;
import com.remainders.alarma.smsscheduler.SMSSchedulerPendingIntent;
import com.remainders.alarma.smsscheduler.SendSMSAlarmService;
@SuppressWarnings("deprecation")
public class FragmentSMS extends Fragment{
	private String mNumber;
	TextView mEditTextNumber,mTimePickUp,mDatePickUp,nosms;
	EditText mEditTextMessage;
	private String mContactdisplayName;
	private long mInterval;
	private String mMessage;
	private String mFrequency="Once";
	Button mButtonContact;
	Button mButtonrefresh;
	Button mConfirm;
	RadioGroup mradioGroup;
	RadioButton mOneTime, mFifteenMinuets, mHalfHour, mHourR, mHalfDay, mDaily, mWeekly, mMonthly, mYearly; 
	private int mHour;
	private int mMinutes;
	private int mSeconds;
	int cancel = 1;
	private int mYear;
	private int mMonth;
	private int mDay;
	
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private int mCurrentHour;
    private int mCurrentMinute;
    
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;
    Calendar c;
    //private static SmsScheduler mMainActivity;
    private int mId;
    private boolean isOneTime = false;
    private PendingIntentsDataSource mDatasource;
    View rootView;
    Button slideButton;
    SlidingDrawer slidingDrawer;
    
	SwipeListView swipelistview;
	CancelAlarmAdapter adapter;
	List<SMSSchedulerPendingIntent> itemData;
	String name;
	int pos;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.about, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.about:
				Intent x = new Intent(getActivity(),AboutUs.class);
				startActivity(x);
				break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragmentsms, container, false);
		swipelistview=(SwipeListView)rootView.findViewById(R.id.swipelistviewsms); 
	    itemData=new ArrayList<SMSSchedulerPendingIntent>();
	    slideButton = (Button) rootView.findViewById(R.id.slideButtonsms);
        slidingDrawer = (SlidingDrawer) rootView.findViewById(R.id.SlidingDrawersms); 
	    adapter=new CancelAlarmAdapter(getActivity(),R.layout.smsscheduler_customrow,itemData);
		mEditTextNumber = (TextView)rootView.findViewById(R.id.editTextNumber);
        mDatePickUp = (TextView)rootView.findViewById(R.id.buttonDatePickup);
        mTimePickUp = (TextView)rootView.findViewById(R.id.buttonTimePickup);
        nosms = (TextView)rootView.findViewById(R.id.nosms);
        mConfirm = (Button)rootView.findViewById(R.id.buttonConfirm);
        mEditTextMessage = (EditText)rootView.findViewById(R.id.editTextMessage1);
        mButtonrefresh=(Button) rootView.findViewById(R.id.buttonrefresh);
        //create/open the database
        mDatasource = new PendingIntentsDataSource(getActivity());
        mDatasource.open();
        c = Calendar.getInstance();
        mCurrentYear = c.get(Calendar.YEAR);
        mCurrentMonth = c.get(Calendar.MONTH);
        mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = c.get(Calendar.MINUTE);
        
        mEditTextNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
	            startActivityForResult(intent, 1);              
			}
		});
        
        mButtonrefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetfunction();
			}
		});
        
        
        mDatePickerDialog = new DatePickerDialog(getActivity(), mDateSetListener, mCurrentYear, mCurrentMonth,mCurrentDay);
        mDatePickUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDatePickerDialog.show();
				
			}
		});
        
        mTimePickerDialog = new TimePickerDialog(getActivity(), mTimeSetListener, mCurrentHour, mCurrentMinute, true);
        mTimePickUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTimePickerDialog.show();
			}
		});
        
        mConfirm.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//startSlidingDrawerActivity();
				Log.v("valid", "on confirm click");
				mMessage = mEditTextMessage.getText().toString();
				mNumber = mEditTextNumber.getText().toString();
		if(!mEditTextMessage.getText().toString().equals(""))
		{	
			Log.v("valid", mMessage);
			Log.v("valid", "message entered 2");
			
				if(!mEditTextNumber.getText().toString().equals(""))
				{
					Log.v("valid", "number entered");
					Log.v("valid", mNumber);
						if(mFrequency!=null)
						{
							//Date selected = new Date(mYear,mMonth,mDay);
		                    Date d = new Date();
		                    if(mYear<d.getYear()+1900 || (mYear==d.getYear()+1900 && mMonth<d.getMonth()) ||
		                    		(mYear==d.getYear()+1900 && mMonth==d.getMonth() && mDay<d.getDate())){
		                    	//Log.v("datecheck", selected.toString());
		                    	Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
		                    }
		                    
		                    else
		                    {
							Log.v("valid", "freq entered");
							    c.set(Calendar.HOUR_OF_DAY, mHour);
							    c.set(Calendar.MINUTE, mMinutes);
							    c.set(Calendar.SECOND, 0);
							    c.set(mYear, mMonth, mDay);
			   // AlarmManager am = (AlarmManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().ALARM_SERVICE);
							    Intent i = new Intent(getActivity(),SendSMSAlarmService.class);
							    i.putExtra("com.sms.schedule.number",mNumber);
							    i.putExtra("com.sms.schedule.message",mMessage );
							    i.putExtra("com.sms.schedule.frequency", mFrequency);
							    
							    /*final int*/ mId = (int) System.currentTimeMillis();
							    i.putExtra("com.sms.schedule.id", mId);
							    AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
							    PendingIntent pi = PendingIntent.getService(getActivity(), mId, i, PendingIntent.FLAG_UPDATE_CURRENT);
							    
							    if(isOneTime == false){
							    	am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), mInterval, pi);
							    	addToDatabase(true);			    
							    	}
							    if(isOneTime == true){
							    	am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pi);
							    	addToDatabase(false);
							    }
							    //getActivity().startService(i);
			    
			    
			    
			    Toast.makeText(getActivity().getApplicationContext(), "SMS has been scheduled", Toast.LENGTH_LONG).show();
			  
			    resetfunction();
		                    }
		}// if mfreq			
						else
						{
							Toast.makeText(getActivity().getApplicationContext(), "Frequency not set", Toast.LENGTH_LONG).show();
							
						}
	    }//if mnumber	
					else
					{
						Toast.makeText(getActivity().getApplicationContext(), "Select contact", Toast.LENGTH_LONG).show();
						
					}
		}//if mmessage
				else
				{
					Toast.makeText(getActivity().getApplicationContext(), "Enter your message", Toast.LENGTH_LONG).show();
					
				}
			}
		});
        
        slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
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
		                //These are the swipe listview settings. you can change these
		        //setting as your requirement 
		        //swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
		        //swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions 
		        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		        //swipelistview.setOffsetLeft(convertDpToPixel(80f)); // left side offset
		        swipelistview.setOffsetRight(convertDpToPixel(100f)); // right side offset
		        swipelistview.setAnimationTime(300); // Animation time
		        //swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress
		        swipelistview.setAdapter(adapter);
		        swipelistview.setLongClickable(false);
		        mDatasource = new PendingIntentsDataSource(getActivity().getApplicationContext());
			    mDatasource.open();
			    List<SMSSchedulerPendingIntent> values = mDatasource.getAllPendingIntents();
		        mDatasource.close();
		        if(values.size()>0){
		        for(int i=0;i<values.size();i++)
		        {
		        	itemData.add(values.get(i));
		        	
		        }
		        }
		        else
		        	nosms.setVisibility(View.VISIBLE);
		        adapter.notifyDataSetChanged();				
			}
		});
        
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				itemData.clear();
				adapter.notifyDataSetChanged();
				nosms.setVisibility(View.GONE);
				
				
			}
		});

        
    
		return rootView;
	}
	
	
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
    
	public void resetfunction()
    {
    	 mEditTextMessage.setText("");
		    mEditTextNumber.setText("");
		    mDatePickUp.setText("");
		    mTimePickUp.setText("");
		    Time now=new Time();
		    now.setToNow();
		    mDatePickerDialog.updateDate(now.year, now.month,now.monthDay);
		    mTimePickerDialog.updateTime(now.hour, now.minute);
		    //mradioGroup.clearCheck();
    }
    
    
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
    public void onPause(){
    	super.onPause();
    	mDatasource.close();
    	/*mEditTextMessage.setText("");
    	mEditTextNumber.setText("");*/
    }
      @Override
    public void onResume(){
    	super.onResume();
    	mDatasource.open();
    	 c = Calendar.getInstance();
         mCurrentYear = c.get(Calendar.YEAR);
         mCurrentMonth = c.get(Calendar.MONTH);
         mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
         mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
         mCurrentMinute = c.get(Calendar.MINUTE);
         //ActivitySwitcher.animationIn(findViewById(R.id.container), getWindowManager());
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	/*mEditTextMessage.setText("");
    	mEditTextNumber.setText("");	*/
    	mDatasource.open();
    	 c = Calendar.getInstance();
         mCurrentYear = c.get(Calendar.YEAR);
         mCurrentMonth = c.get(Calendar.MONTH);
         mCurrentDay = c.get(Calendar.DAY_OF_MONTH);
         mCurrentHour = c.get(Calendar.HOUR_OF_DAY);
         mCurrentMinute = c.get(Calendar.MINUTE);
    	
    }
    @Override
    public void onStop(){
    	super.onStop();
    	/*mEditTextMessage.setText("");
    	mEditTextNumber.setText("");	*/	
    	mDatasource.close();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	/*mEditTextMessage.setText("");
    	mEditTextNumber.setText("");*/
    	mDatasource.close();
    }
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data != null) {
	        Uri uri = data.getData();

	        if (uri != null) {
	            Cursor c = null;
	            try {
	                c = getActivity().getContentResolver().query(uri, new String[]{ 
	                            ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                            ContactsContract.CommonDataKinds.Phone.TYPE,
	                            ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME},
	                        null, null, null);

	                if (c != null && c.moveToFirst()) {
	                    //mNumber = c.getString(0);
	                    //int type = c.getInt(1);
	                    mEditTextNumber.setText(c.getString(0));
	                    mContactdisplayName = c.getString(2);
	                }
	            } finally {
	                if (c != null) {
	                    c.close();
	                }
	            }
	        }
	    }
    }
    
 // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    Date d = new Date();
                    if(mYear<d.getYear()+1900 || (mYear==d.getYear()+1900 && mMonth<d.getMonth()) ||
                    		(mYear==d.getYear()+1900 && mMonth==d.getMonth() && mDay<d.getDate())){
                    	Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
                    }
                    mDatePickUp.setText(mDay+"-"+mMonth+"-"+mYear);
                }
            };

     // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            	mHour = hourOfDay;
                mMinutes = minute;
                mTimePickUp.setText(mHour+":"+mMinutes);
                //updateDisplay();
            }
        };
     
       /* public static SmsScheduler getMAinActivity(){
        	return mMainActivity;
        }*/
        
        public String getNumberToSend(){
        	return mNumber;
        }
        
        public String getSMSMessage(){
        	return mMessage;
        }
        
        public int getDay(){
        	return mDay;
        }
        
        public int getMonth(){
        	return mMonth;
        }
        public int getYear(){
        	return mYear;
        }
        
        public int getHour(){
        	return mHour;
        }
        
        public int getMinutes(){
        	return mMinutes;
        }
        
        public int getSeconds(){
        	return mSeconds;
        }
        
        public PendingIntentsDataSource getDataSource(){
        	//create/open the database
        	return mDatasource;
        }
        //mDatasource = new PendingIntentsDataSource(this);
        //mDatasource.open();

		
		@SuppressWarnings("unused")
		private void addToDatabase(boolean isRepeated){
			if(isRepeated == true){
				
				SMSSchedulerPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId, mHour, mMinutes, mSeconds, mYear, mMonth, mDay, mFrequency, mNumber, mContactdisplayName, mMessage);
			}
			if(isRepeated == false){
				SMSSchedulerPendingIntent newDatabeseEntry = mDatasource.createPendingIntents(mId, mHour, mMinutes, mSeconds, mYear, mMonth, mDay, mFrequency, mNumber, mContactdisplayName, mMessage);
			}
		}
		


}
