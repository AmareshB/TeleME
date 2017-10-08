package com.remainders.alarma.smsscheduler;

import java.util.List;

import com.remainders.alarma.R;
import com.remainders.alarma.database.PendingIntentsDataSource;

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
import android.widget.Toast;

public class CancelAlarmAdapter extends ArrayAdapter<SMSSchedulerPendingIntent> /*implements OnClickListener*/{
	private List<SMSSchedulerPendingIntent> mPendingIntentList;
	String newL = System.getProperty("line.separator");
	String messageAlertDialog;
	long mIdOfAnAlarm;
	List<SMSSchedulerPendingIntent>   data; 
	Context context;
	int layoutResID;
	private PendingIntentsDataSource mDatasource;
	
	public CancelAlarmAdapter(Context context, int layoutResourceId,List<SMSSchedulerPendingIntent> data) {
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
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResID, parent, false);
	            
	            holder = new NewsHolder();
	           
	            holder.itemName = (TextView)row.findViewById(R.id.contactName);
	            holder.message = (TextView)row.findViewById(R.id.remaindermess);
	            holder.schdate = (TextView)row.findViewById(R.id.schdate);
	            holder.schtime = (TextView)row.findViewById(R.id.schtime);
	            //holder.schfreq = (TextView)row.findViewById(R.id.schfreq);
	            holder.del = (Button) row.findViewById(R.id.delete);
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (NewsHolder)row.getTag();
	        }
	        
	        final SMSSchedulerPendingIntent itemdata = data.get(position);
	        String receiverNumber = itemdata.getNumberToSend();
		    String receiverName = itemdata.getReceiverName();
		    String message = itemdata.getMessage();
		    String frequency = itemdata.getFrequency();
		    final int id = (int)itemdata.getId();
	        holder.itemName.setText(receiverName);
	        holder.message.setText("Message: "+itemdata.getMessage());
	        holder.schdate.setText("Date: "+itemdata.getDay()+"/"+itemdata.getMonth()+"/"+itemdata.getYear());
	        holder.schtime.setText("Time: "+itemdata.getHour()+":"+itemdata.getMinutes()+":"+itemdata.getSeconds());
	        //holder.schfreq.setText("Frequency: "+itemdata.getFrequency());
	        holder.del.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//int mIdOfAnPendingIntent = pos;
					mDatasource = new PendingIntentsDataSource(context);
					mDatasource.open();
					AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
					Intent i = new Intent(context, SendSMSAlarmService.class);
				    PendingIntent pi = PendingIntent.getService(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
				    am.cancel(pi);
				    mDatasource.deletePendingIntent(id);
				    mDatasource.close();
				    data.remove(pos);
				    notifyDataSetChanged();
				    
/*				    Intent refresh = new Intent();
				    refresh.setClassName("com.sms.schedule", "com.sms.schedule.CancelAlarmActivity");
				    context.startActivity(refresh);
*/
					
				}
			});
	        return row;
	}
	
	public List<SMSSchedulerPendingIntent> getPendingIntentList(){
		return mPendingIntentList;
	}

	static class NewsHolder{
		TextView itemName;
		TextView message;
		TextView schdate,schtime;
		Button del;
		}
		
	

}
