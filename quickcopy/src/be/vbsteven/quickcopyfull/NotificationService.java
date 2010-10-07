package be.vbsteven.quickcopyfull;

import be.vbsteven.quickcopyfull.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
		Notification notif = new Notification(R.drawable.icon, "Quickcopy", System.currentTimeMillis());
		notif.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, EntryListActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
		notif.setLatestEventInfo(this, "Quickcopy", "Click here to access your snippets", intent);
		nManager.notify(0, notif);
	}



	private void hideNotification() {
		nManager.cancel(0);
	}

}
