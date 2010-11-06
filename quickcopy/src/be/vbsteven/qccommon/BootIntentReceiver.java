package be.vbsteven.qccommon;

import be.vbsteven.quickcopy.Global;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			// check if start-on-boot is enabled in preferences
			SharedPreferences prefs = Global.getPrefs(context);
			if (prefs.getBoolean("integration.startonboot", false)
					&& prefs.getBoolean("integration.shownotification", false)) {
				context.startService(new Intent(context, NotificationService.class));
			} 
		}
	}

}
