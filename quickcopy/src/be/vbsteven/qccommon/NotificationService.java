package be.vbsteven.qccommon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import be.vbsteven.quickcopyfull.R;
import be.vbsteven.quickcopyfull.R.drawable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.jakewharton.notificationcompat2.NotificationCompat2;

public class NotificationService extends Service {

	
	private static NotificationManager nManager;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		hideNotification();
	}
	
	private void showNotification() {

        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, EntryListActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationCompat2.Builder builder = new NotificationCompat2.Builder(this);
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.ic_stat_notificationicon);
        builder.setContentTitle("Quickcopy");
        builder.setContentText("Snippet repository");
        builder.setContentIntent(intent);
        builder.setPriority(Notification.PRIORITY_MIN);
        Notification notif = builder.build();

        nManager.notify(0, notif);
	}



	private void hideNotification() {
		nManager.cancel(0);
	}

}
