package com.remainders.alarma.callremainder;

import android.graphics.drawable.Drawable;

public class ItemRow {
 
      String itemName;
      String message;
      Drawable icon;
      
      public ItemRow(String itemName,String msg, Drawable icon) {
            super();
            this.itemName = itemName;
            this.icon = icon;
            this.message = msg;
      }
      public String getItemName() {
            return itemName;
      }
      public void setItemName(String itemName) {
            this.itemName = itemName;
      }
      public Drawable getIcon() {
            return icon;
      }
      public void setIcon(Drawable icon) {
            this.icon = icon;
      }
      public String getMessage(){
    	  if(message!=null)
    		  return message;
    	  else
    	  return "null";
      }
 
}
