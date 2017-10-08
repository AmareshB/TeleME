package com.remainders.alarma;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.remainders.alarma.callremainder.CallDetectService;
import com.remainders.alarma.callremainder.ItemAdapter;
import com.remainders.alarma.callremainder.ItemRow;
import com.remainders.alarma.database.dbforsmart;

@SuppressWarnings("deprecation")
public class FragmentCall extends Fragment{
	private final int PICK_CONTACT = 1;
	 String contactname = "";
	 String contactnumber= "";
	 EditText mes;
	 TextView phoneInput,nocall;
	 Button add,refresh,del,slideButton;
	 String message = "";
	 int Delete = 1;
	 String TAG = "Nothing";
	 String name;
	 int pos,detect=0;
	 SharedPreferences sharedpreferences;
	 TextView left,right,contact;
	 Editor editor;
	 View rootView;
	 SwipeListView swipelistview;
	 ItemAdapter adapter;
	SlidingDrawer slidingDrawer;
	 List<ItemRow> itemData;
	 dbforsmart db;
	 //Switch sw;
	 Bundle state;

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
			final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragmentcall, container, false);
		sharedpreferences = getActivity().getSharedPreferences("TurnOnOff", Context.MODE_PRIVATE);
		editor = sharedpreferences.edit();
		state = savedInstanceState;
		//sw = (Switch)rootView.findViewById(R.id.turnonoff);
		db = new dbforsmart(getActivity().getApplicationContext());
		nocall = (TextView)rootView.findViewById(R.id.nocall);
		slideButton = (Button) rootView.findViewById(R.id.slideButton);
		swipelistview=(SwipeListView)rootView.findViewById(R.id.callswipelist); 
        slidingDrawer = (SlidingDrawer) rootView.findViewById(R.id.SlidingDrawer); 
        itemData=new ArrayList<ItemRow>();
        adapter=new ItemAdapter(getActivity(),R.layout.callremainder_customrow,itemData);
		contact = (TextView)rootView.findViewById(R.id.contact);
		mes = (EditText) rootView.findViewById(R.id.message);
		add = (Button) rootView.findViewById(R.id.add);
		refresh = (Button) rootView.findViewById(R.id.refresh);
		left = (TextView)rootView.findViewById(R.id.left);
		right = (TextView)rootView.findViewById(R.id.right);
		Intent intent = new Intent(getActivity(), CallDetectService.class);
   		getActivity().startService(intent);
		
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refresh();
			}
		});
		
		contact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openContacts();
			}
		});
		
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			message = mes.getText().toString();
			
			if(message == ""){
				Toast.makeText(getActivity().getApplicationContext(), "Enter the remainder", Toast.LENGTH_SHORT).show();
			}
			else if(contactnumber == ""){
					Toast.makeText(getActivity().getApplicationContext(), "Please pick a contact", Toast.LENGTH_SHORT).show();
				}
			else{
				boolean diditwork = true;
				try
				{
				dbforsmart obj = new dbforsmart(getActivity());
				obj.open();
				obj.createentry(contactname,contactnumber,message);
				obj.close();
				diditwork = true;
				}
				catch(Exception e){
					diditwork = false;
					Toast.makeText(getActivity().getApplicationContext(), "Failed to set remainder", Toast.LENGTH_SHORT).show();
					Log.e("Error in insertion","thu");
				}finally{
					if(diditwork)
					{
						Toast.makeText(getActivity().getApplicationContext(), "Remainder set", Toast.LENGTH_SHORT).show();
						refresh();
					}
				}

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
		                
		            }

		            @Override
		            public void onClickBackView(int position) {
		                Log.d("swipe", String.format("onClickBackView %d", position));
		                
		            }

		            @Override
		            public void onDismiss(int[] reverseSortedPositions) {
		            }

		        });
		        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
		        swipelistview.setOffsetRight(convertDpToPixel(200f));
		        swipelistview.setAnimationTime(300); 
		        swipelistview.setAdapter(adapter);
		        swipelistview.setLongClickable(false);
		        db.open();
		        ArrayList<String> rems = db.getdata();
		        db.close();
		        if(rems.size()>0){
		        for(int i=0;i<rems.size();i++)
		        {
		        	String received = rems.get(i).toString();
		        	StringTokenizer st = new StringTokenizer(received,"###");
		        	String name = st.nextElement().toString();
		        	String msg = st.nextElement().toString();
		        	itemData.add(new ItemRow(name,msg,getResources().getDrawable(R.drawable.ic_launcher) ));
		        	
		        }
		        }
		        else
		        	nocall.setVisibility(View.VISIBLE);
		        adapter.notifyDataSetChanged();
		        
			}
		       
		});
        
        slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				itemData.clear();
				adapter.notifyDataSetChanged();
				nocall.setVisibility(View.GONE);
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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}


	


	public void openContacts(){
		//Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/all"));
		Intent intent = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,PICK_CONTACT);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
	            phoneInput = (TextView) rootView.findViewById(R.id.contact);
	            Cursor cursor = null;  
	            String phoneNumber = "";
	            
	            List<String> allNumbers = new ArrayList<String>();
	            int phoneIdx = 0;
	            int name = 0;
	            try {  
	                Uri result = data.getData();  
	                String id = result.getLastPathSegment();  
	                cursor = getActivity().getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
	                phoneIdx = cursor.getColumnIndex(Phone.DATA);
	                name = cursor.getColumnIndex(Phone.DISPLAY_NAME);
	                if (cursor.moveToFirst()) {
	                    while (cursor.isAfterLast() == false) {
	                        phoneNumber = cursor.getString(phoneIdx);
	                        contactname = cursor.getString(name);
	                        allNumbers.add(phoneNumber);
	                        cursor.moveToNext();
	                    }
	                } else {
	                    //no results actions
	                }  
	            } catch (Exception e) {  
	               //error actions
	            } finally {  
	                if (cursor != null) {  
	                    cursor.close();
	                }

	                final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
	                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	                builder.setTitle("Choose a number");
	                builder.setItems(items, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int item) {
	                        String selectedNumber = items[item].toString();
	                        selectedNumber = selectedNumber.replace("-", "");
	                        contactnumber = selectedNumber.replace("-", "");
	                        phoneInput.setText(contactname);
	                    }
	                });
	                AlertDialog alert = builder.create();
	                if(allNumbers.size() > 1) {
	                    alert.show();
	                } else {
	                    String selectedNumber = phoneNumber.toString();
	                    selectedNumber = selectedNumber.replace("-", "");
	                    contactnumber = selectedNumber.replace("-", "");
	                    phoneInput.setText(contactname);
	                }

	                if (phoneNumber.length() == 0) {  
	                	Toast.makeText(getActivity().getApplicationContext(), "No numbers found", Toast.LENGTH_LONG).show();
	                }  
	            }  
	    
	}
	
	public void refresh(){
		if(contactname!="")
		{
		mes.setText("");
		phoneInput.setText("");
		contactname="";
		contactnumber="";
		message="";
		}
	}
}
