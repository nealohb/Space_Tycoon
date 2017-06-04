package com.picke.space;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
	
	protected static final int NOTIF_ID = 532416;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
	    String title = bundle.getString("title");
	    String text = bundle.getString("text");
	    int type = bundle.getInt("type");
	    
	    int icon = R.drawable.ic_launcher;
	    
		Intent inten = new Intent(context, AndroidLauncher.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, inten, 0);
        
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), icon);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        .setContentIntent(pIntent)
        .setContentTitle(title)
        .setContentText(text)
        .setLargeIcon(bm)
        .setLights(Color.YELLOW, 500, 500)
        .setSmallIcon(icon)
        
        .setSound(alarmSound)
        .setAutoCancel(true);
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID, builder.build());
	}

}