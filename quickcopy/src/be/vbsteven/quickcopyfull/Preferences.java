package be.vbsteven.quickcopyfull;

import java.util.ArrayList;

import be.vbsteven.quickcopyfull.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		SharedPreferences prefs = Global.getPrefs(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		ArrayList<Group> groups = DBHelper.get(this).getGroups();
		String[] groupNames = new String[groups.size()];
		
		for (int i = 0; i < groups.size(); i++) {
			groupNames[i] = groups.get(i).name;
		}
		
		ListPreference listPreference = (ListPreference)getPreferenceScreen().findPreference("integration.defaultgroup");
		listPreference.setEntries(groupNames);
		listPreference.setEntryValues(groupNames);
		
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("integration.shownotification")) {
			if (sharedPreferences.getBoolean(key, false)) {
				startService(new Intent(this, NotificationService.class));
			} else {
				stopService(new Intent(this, NotificationService.class));
			}
		}
	}
	
}
