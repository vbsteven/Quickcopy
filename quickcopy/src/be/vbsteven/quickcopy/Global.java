package be.vbsteven.quickcopy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Global {
	
	public static final String TAG = "be.vbsteven.quickcopy";
	public static final String PREFS = "be.vbsteven.quickcopy_preferences";
	
	public static final String PASSWORD_HASH = "*****";
	
	private static GoogleAnalyticsTracker tracker = null;
	
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(PREFS, context.MODE_WORLD_WRITEABLE);
	}
	
	public static GoogleAnalyticsTracker getTracker(Context context) {
		if (tracker == null) {
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.start("UA-9261106-9", context);
		}
		return tracker;
	}

	public static String getVersionString(Context context) {
		try {
			return context.getPackageManager().getPackageInfo("be.vbsteven.quickcopy", 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}
}
