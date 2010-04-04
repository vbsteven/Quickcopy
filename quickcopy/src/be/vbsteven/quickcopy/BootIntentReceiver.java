package be.vbsteven.quickcopy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			// check if start-on-boot is enabled in preferences
			SharedPreferences prefs = Global.getPrefs(context);
			if (prefs.getBoolean("integration.startonboot", false)) {
				Log.d("Quickcopy", "starting quickopy on boot");
				Intent i = new Intent();
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setClass(context, QuickCopyMain.class);
				context.startActivity(i);
			} else {
				Log.d("Quickcopy", "NOT starting quickcopy on boot");
				return;
			}
		}
	}

}
