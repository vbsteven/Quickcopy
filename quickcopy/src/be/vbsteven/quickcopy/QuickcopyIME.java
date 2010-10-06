package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.inputmethodservice.InputMethodService;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class QuickcopyIME extends InputMethodService {

	private TextView noEntriesTextView;
	private ListView listview;
	
	private EntryListAdapter entryAdapter;
	
	private ArrayList<Group> groups;
	private Group currentGroup = null;
	
	private View view;
	
	private Vibrator vibrator = null;
	
	@Override
	public View onCreateInputView() {
		LayoutInflater inflater = LayoutInflater.from(getBaseContext());
		QuickcopyKeyboardView qkview = new QuickcopyKeyboardView(this);
		view = inflater.inflate(R.layout.ime, qkview);
		
		listview = (ListView)view.findViewById(R.id.listview_entrylist);
		listview.setItemsCanFocus(true);
		noEntriesTextView = (TextView)view.findViewById(R.id.tv_noentries);
		
		Button prevGroupButton = (Button)view.findViewById(R.id.but_previous_group);
		prevGroupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vibrator.vibrate(100);
				previousGroup();
			}
		});
		
		Button nextGroupButton = (Button)view.findViewById(R.id.but_next_group);
		nextGroupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vibrator.vibrate(100);
				nextGroup();
			}
		});
		
		initAdapter();
		
		updateNoEntriesTextView();
		qkview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("quickcopy", "onClick");
			}
		});
		
		initGroups();
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		return qkview;
	}
	
	
	
	private void initGroups() {
		groups = DBHelper.get(this).getGroups();
		
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "General");
		Group group = null;
		
		for (Group g: groups) {
			if (g.name.equals(defaultGroup)) {
				group = g;
			}
		} 
		
		if (group == null) {
			group = groups.get(0);
		}
		
		currentGroup = group;
		
		updateScreen();
	}



	private void updateScreen() {
		TextView tv = (TextView)view.findViewById(R.id.tv_group);
		tv.setText(currentGroup.name);
		
		ArrayList<Entry> entries = DBHelper.get(this).getEntriesFromGroup(currentGroup);
		entryAdapter.updateEntries(entries);
	}



	protected void nextGroup() {
		int idOfGroup = groups.indexOf(currentGroup);
		idOfGroup++;
		
		if (idOfGroup >= groups.size()) {
			// end of list, take first one
			currentGroup = groups.get(0);
		} else {
			currentGroup = groups.get(idOfGroup);
		}
		
		updateScreen();
	}



	protected void previousGroup() {
		int idOfgroup = groups.indexOf(currentGroup);
		idOfgroup--;
		
		if (idOfgroup < 0) {
			// before beginning, take last one
			currentGroup = groups.get(groups.size()-1);
		} else {
			currentGroup = groups.get(idOfgroup);
		}
		
		updateScreen();
	}



	private void initAdapter() {
		DBHelper db = DBHelper.get(this);
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "General");
		ArrayList<Entry> entries = db.getEntriesFromGroup(DBHelper.get(this).getGroup(defaultGroup));

		entryAdapter = new EntryListAdapter(this, entries);

		listview.setAdapter(entryAdapter);
	}



	protected void onEntryClicked(Entry entry) {
		Log.d("quickcopy", "item is clicked");
	}


	private void updateNoEntriesTextView() {
		if (entryAdapter.isEmpty()) {
			noEntriesTextView.setVisibility(View.VISIBLE);
		} else {
			noEntriesTextView.setVisibility(View.GONE);
		}
	}
}
