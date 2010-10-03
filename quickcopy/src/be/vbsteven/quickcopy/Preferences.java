package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		
		ArrayList<Group> groups = DBHelper.get(this).getGroups();
		String[] groupNames = new String[groups.size()];
		
		for (int i = 0; i < groups.size(); i++) {
			groupNames[i] = groups.get(i).name;
		}
		
		ListPreference listPreference = (ListPreference)getPreferenceScreen().findPreference("integration.defaultgroup");
		listPreference.setEntries(groupNames);
		listPreference.setEntryValues(groupNames);
	}
	
}
