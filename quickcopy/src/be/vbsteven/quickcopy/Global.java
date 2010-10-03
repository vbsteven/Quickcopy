package be.vbsteven.quickcopy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class Global {
	
	public static final String TAG = "be.vbsteven.quickcopy";
	public static final String PREFS = "be.vbsteven.quickcopy_preferences";
	
	public static final String QUICKCOPY_ENTRY_ID = "QUICKCOPY_ENTRY_ID";
	public static final String QUICKCOPY_GROUP =  "QUICKCOPY_GROUP";
	
	public static final String PASSWORD_HASH = "*****";
	
	
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
}
