package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
		EntryListAdapter adapter = new EntryListAdapter(this, entries);
		
		ListView lv = (ListView)findViewById(R.id.listview_entrylist);
		lv.setAdapter(adapter);
		
		AdView adview;
		adview = (AdView)findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);
		
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
				result = inflater.inflate(R.layout.entrylistitem, null);
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
