package com.remainders.alarma.callremainder;

import java.util.List;

import com.remainders.alarma.R;
import com.remainders.alarma.database.dbforsmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class ItemAdapter extends ArrayAdapter<ItemRow> {

	List<ItemRow>   data; 
	Context context;
	int layoutResID;
	String num;

public ItemAdapter(Context context, int layoutResourceId,List<ItemRow> data) {
	super(context, layoutResourceId, data);
	
	this.data=data;
	this.context=context;
	this.layoutResID=layoutResourceId;


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
            holder.call = (ImageButton)row.findViewById(R.id.callphone);
            holder.itemName = (TextView)row.findViewById(R.id.example_itemname);
            holder.delete = (Button)row.findViewById(R.id.deleteswipe);
            holder.message = (TextView)row.findViewById(R.id.remaindermess);
            row.setTag(holder);
        }
        else
        {
            holder = (NewsHolder)row.getTag();
        }
        
        final ItemRow itemdata = data.get(position);
        holder.itemName.setText(itemdata.getItemName());
        holder.message.setText("Message: "+itemdata.getMessage());
        
        
        holder.call.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dbforsmart db = new dbforsmart(context);
				String name = data.get(pos).getItemName();
				
				db.open();
        		num = db.getNumber(name);
        		db.close();
        		Log.v("Name", num + name);
        		if(num!=""){
        		Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:"+num));
				context.startActivity(call);
        		}
			}
		});
        holder.delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dbforsmart db = new dbforsmart(context);
				db.open();
        		db.deleteEntry(itemdata.getItemName());
        		db.close();
				data.remove(pos);
				notifyDataSetChanged();
				
			}
		});
        
        return row;
	
}



static class NewsHolder{
	
	TextView itemName;
	TextView message;
	Button delete;
	ImageButton call;
	}
	
	
}



