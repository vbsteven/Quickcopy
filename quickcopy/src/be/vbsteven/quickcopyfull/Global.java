package be.vbsteven.quickcopyfull;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class Global {
	
	public static final String TAG = "be.vbsteven.quickcopy";
	public static final String PREFS = "be.vbsteven.quickcopyfull_preferences";
	
	public static final String QUICKCOPY_ENTRY_ID = "QUICKCOPY_ENTRY_ID";
	public static final String QUICKCOPY_GROUP =  "QUICKCOPY_GROUP";
	
	public static final String PASSWORD_HASH = "*****";
	
	public static final boolean QUICKCOPY_FULL = true;
	
	
	public static boolean isFullVersion() {
		return QUICKCOPY_FULL;
	}
	
	public static boolean isFreeVersion() {
		return !QUICKCOPY_FULL;
	}
	
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(PREFS, context.MODE_WORLD_WRITEABLE);
	}
	

	public static String getVersionString(Context context) {
		try {
			return context.getPackageManager().getPackageInfo("be.vbsteven.quickcopy", 0).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}


	public static boolean isLightTheme(Context context) {
		return getPrefs(context).getString("integration.theme", "Light theme").equals("Light theme");
	}


	/*
	 * returns true if the user chooses to only show titles in the list
	 */
	public static boolean userSelectedShowTitles(Context context) {
		return Global.getPrefs(context).getBoolean("integration.onlyshowtitles", false);
	}
}
