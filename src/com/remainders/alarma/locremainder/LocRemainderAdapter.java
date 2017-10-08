package com.remainders.alarma.locremainder;


import java.util.List;

import com.remainders.alarma.R;
import com.remainders.alarma.database.LocRemainderDataSource;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class LocRemainderAdapter extends ArrayAdapter<LocRemainderPendingIntent> /*implements OnClickListener*/{
	private List<LocRemainderPendingIntent> mPendingIntentList;
	String newL = System.getProperty("line.separator");
	String messageAlertDialog;
	long mIdOfAnAlarm;
	List<LocRemainderPendingIntent>   data; 
	Context context;
	int layoutResID;
	private LocRemainderDataSource mDatasource;
	
	public LocRemainderAdapter(Context context, int layoutResourceId,List<LocRemainderPendingIntent> data) {
		super(context, layoutResourceId, data);
		
		this.data=data;
		this.context=context;
		this.layoutResID=layoutResourceId;

		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		NewsHolder holder = null;
		final int pos = position;
		   View row = convertView;
		    holder = null;
	        
	        if(row == null)
	        {
	        	LayoutInflater inflater = LayoutInflater.from(context);
	            row = inflater.inflate(layoutResID, parent, false);
	            
	            holder = new NewsHolder();
	           
	            holder.itemName = (TextView)row.findViewById(R.id.placeName);
	            holder.message = (TextView)row.findViewById(R.id.remaindermessloc);
	            holder.del = (Button) row.findViewById(R.id.deleteloc);
	            //holder.latitude = (TextView)row.findViewById(R.id.latitude);
	            //holder.longitude = (TextView)row.findViewById(R.id.longitude);
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (NewsHolder)row.getTag();
	        }
	        
	        final LocRemainderPendingIntent itemdata = data.get(position);
		    final int id = (int)itemdata.getId();
	        holder.message.setText("Message: "+itemdata.getMessage());
	        holder.itemName.setText(itemdata.getPlace());
	        //holder.latitude.setText(""+itemdata.getLat());
	        //holder.longitude.setText(""+itemdata.getLon());
	        holder.del.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//int mIdOfAnPendingIntent = pos;
					mDatasource = new LocRemainderDataSource(context);
					mDatasource.open();
					@SuppressWarnings("static-access")
					AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
					Intent i = new Intent(context, LocRemainderAlarmService.class);
				    PendingIntent pi = PendingIntent.getService(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
				    am.cancel(pi);
				    mDatasource.deletePendingIntent(id);
				    mDatasource.close();
				    data.remove(pos);
				    notifyDataSetChanged();
				}
			});
	        return row;
	}
	
	public List<LocRemainderPendingIntent> getPendingIntentList(){
		return mPendingIntentList;
	}

	static class NewsHolder{
		TextView itemName;
		TextView message;
		TextView latitude,longitude,schfreq;
		Button del;
		}
		
	

}
