package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.admob.android.ads.AdView;

public class EntryListActivity extends Activity {

	private static final int RESULT_NEW_ENTRY = 0;
	
	private EntryListAdapter entryAdapter;
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//load ui
		setContentView(R.layout.entrylistactivity);
		
		DBHelper db = DBHelper.get(this);
		
		ArrayList<Entry> entries = db.getEntriesFromGroup(db.getDummyGroup());
		
		entryAdapter = new EntryListAdapter(this, entries);
		
		ListView lv = (ListView)findViewById(R.id.listview_entrylist);
		lv.setAdapter(entryAdapter);
		
		fillGroupList();
		
		AdView adview;
		adview = (AdView)findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "New entry");
		menu.add(0, 1, 0, "Preferences");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 0: startActivityForResult(new Intent(this, NewEntryActivity.class), RESULT_NEW_ENTRY); break;
		case 1: startActivity(new Intent(this, Preferences.class)); break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RESULT_NEW_ENTRY && resultCode == RESULT_OK) {
			ArrayList<Entry> entries = DBHelper.get(this).getEntriesFromGroup((Group)spinner.getSelectedItem());
			entryAdapter.updateEntries(entries);
		}
	}
	
	private void fillGroupList() {
		DBHelper db = DBHelper.get(this);
		ArrayList<Group> groups = db.getGroups();
		groups.add(0, db.getDummyGroup());
		
		spinner = (Spinner)findViewById(R.id.spinner_category);
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
	}
	
	private void changeGroup(Group group) {
		ArrayList<Entry> entries = DBHelper.get(this).getEntriesFromGroup(group);
		entryAdapter.updateEntries(entries);
	}
	
	
	public class EntryListAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<Entry> entries;
		
		private LayoutInflater inflater;
		
		private TextView titleView;
		private TextView valueView;
		
		public EntryListAdapter(Context context, ArrayList<Entry> entries) {
			this.context = context;
			this.entries = entries;
			inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return entries.size();
		}

		@Override
		public Object getItem(int index) {
			if (index < entries.size()) {
				return entries.get(index);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			// error check
			if (index >= entries.size()) {
				return null;
			}
			
			Entry entry = entries.get(index);
			View result;

			// init or convert view
			if (convertView != null) {
				result = convertView;
			} else {
				result = inflater.inflate(R.layout.entrylistitem, parent, false);
			}
			
			// fill up values
			titleView = (TextView)result.findViewById(R.id.entrylistitem_title);
			valueView = (TextView)result.findViewById(R.id.entrylistitem_value);
			
			titleView.setText(entry.key);
			if (entry.hidden) {
				valueView.setText(Global.PASSWORD_HASH);
			} else {
				valueView.setText(entry.value);
			}
			
			return result;
		}
		
		public void updateEntries(ArrayList<Entry> entries) {
			this.entries = entries;
			notifyDataSetChanged();
		}
		
	}
}
