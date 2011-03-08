/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.happyvampire.android.speedobar;

import com.happyvampire.android.speedobar.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SpeedoBarService extends Service {
    private NotificationManager mNM;
    LocationManager lm;
    SpeedoActionListener ll;
    Toast toast;

    Notification notification;
    
    public class LocalBinder extends Binder {
        SpeedoBarService getService() {
            return SpeedoBarService.this;
        }
    }
    
    @Override
    public void onCreate() {
        
    	mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
                
        // Set the icon, scrolling text and timestamp
        notification = new Notification();

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SpeedoBarServiceStop.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "SpeedoBar",
                       "click to end SpeedoBar", contentIntent);
        
        notification.tickerText = "Starting SpeedoBar";
        notification.icon = R.drawable.level_list;
        notification.iconLevel = 0;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNM.notify(1, notification);
        
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
        ll = new SpeedoActionListener(); 
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, ll); 
        
    }
    
    @Override
    public void onDestroy() {
    	lm.removeUpdates(ll);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();
    
    private class SpeedoActionListener implements LocationListener 
    { 
        @Override 
        public void onLocationChanged(Location location) { 
        	
        	double mySpeed = 0;
        	int mph = 0;
        	if(location!=null) { 
                if(location.hasSpeed()){ 
                    mySpeed = location.getSpeed(); 
                    mph = (int) (mySpeed * 3600 / 1609.344);
                } 
            } 
        	notification.iconLevel = mph;
        	Log.v ("SpeedoBar","Speed = " + mySpeed + " mph = " + mph);
            mNM.notify(1, notification);

            /*if (toast != null) {
            	Log.v ("SpeedoBar","cancel toast");
            	toast.cancel();
            }
            
            if( toast == null )
            {
              toast = Toast.makeText( getApplicationContext(), String.valueOf(mph), Toast.LENGTH_SHORT );
            }
            else
            {
              toast.setText( String.valueOf(mph) );
            }
            
            //toast = Toast.makeText(getApplicationContext(), String.valueOf(mph), Toast.LENGTH_SHORT);
            //toast.setText(String.valueOf(mph));
            toast.setGravity(Gravity.BOTTOM, -200, 100);
            toast.show(); */
            
        } 
        @Override 
        public void onProviderDisabled(String provider) { 
                // TODO Auto-generated method stub 
        } 
        @Override 
        public void onProviderEnabled(String provider) { 
                // TODO Auto-generated method stub 
        } 
        @Override 
        public void onStatusChanged(String provider, int status, Bundle extras) { 
                // TODO Auto-generated method stub 
        } 
    } 
}

