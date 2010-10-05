package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class QuickcopyIME extends InputMethodService {

	private TextView noEntriesTextView;
	private ListView listview;
	private Spinner spinner;
	
	private EntryListAdapter entryAdapter;
	
	@Override
	public View onCreateInputView() {
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		QuickcopyKeyboardView qkview = new QuickcopyKeyboardView(this);
		View view = inflater.inflate(R.layout.ime, qkview);
		
		listview = (ListView)view.findViewById(R.id.listview_entrylist);
		spinner = (Spinner)view.findViewById(R.id.spinner_category);
		noEntriesTextView = (TextView)view.findViewById(R.id.tv_noentries);
		
		initAdapter();
		
		updateNoEntriesTextView();
//		qkview.addView(view);
		
		return view;
	}
	
	
	
	private void initAdapter() {
		DBHelper db = DBHelper.get(this);
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "General");
		ArrayList<Entry> entries = db.getEntriesFromGroup(DBHelper.get(this).getGroup(defaultGroup));

		entryAdapter = new EntryListAdapter(this, entries);

		listview.setAdapter(entryAdapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Entry entry = (Entry) adapterView.getAdapter()
						.getItem(position);
				onEntryClicked(entry);
				return;
			}
		});
	}



	protected void onEntryClicked(Entry entry) {
		// TODO Auto-generated method stub
	}



	private void fillGroupList() {
		DBHelper db = DBHelper.get(this);
		ArrayList<Group> groups = db.getGroups();
		
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				changeGroup((Group)adapterView.getAdapter().getItem((int)id));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "");
		setSpinnerToGroup(defaultGroup);
	}
	
	private void changeGroup(Group group) {
		ArrayList<Entry> entries = DBHelper.get(this)
				.getEntriesFromGroup(group);
		entryAdapter.updateEntries(entries);
	}
	
	/*
	 * This is ugly I know
	 */
	protected void setSpinnerToGroup(String name) {
		Adapter a = spinner.getAdapter();
		for (int i = 0; i < a.getCount(); i++) {
			Group g = (Group) a.getItem(i);
			if (g.name.equals(name)) {
				spinner.setSelection(i);
				break;
			}
		}
	}
	
	private void updateNoEntriesTextView() {
		if (entryAdapter.isEmpty()) {
			noEntriesTextView.setVisibility(View.VISIBLE);
		} else {
			noEntriesTextView.setVisibility(View.GONE);
		}
	}
}
