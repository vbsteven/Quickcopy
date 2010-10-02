package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.admob.android.ads.AdView;

public class EntryListActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//load ui
		setContentView(R.layout.entrylistactivity);
		
		DBHelper db = DBHelper.get(this);
		
		ArrayList<Entry> entries = db.getEntries();
		
		Entry p = new Entry(33, "This is a very long entry text, let's see how the application handles this.", false, "Longtext");
		entries.add(p);
		EntryListAdapter adapter = new EntryListAdapter(this, entries);
		
		ListView lv = (ListView)findViewById(R.id.listview_entrylist);
		lv.setAdapter(adapter);
		
		fillGroupList();
		
		AdView adview;
		adview = (AdView)findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);
		
	}
	
	private void fillGroupList() {
		DBHelper db = DBHelper.get(this);
		ArrayList<Group> groups = db.getGroups();
		groups.add(0, db.getDummyGroup());
		
		Spinner s = (Spinner)findViewById(R.id.spinner_category);
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
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
		
	}
}
