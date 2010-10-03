package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.admob.android.ads.AdView;

public class EntryListActivity extends Activity {

	private static final int REQUEST_NEW_ENTRY = 0;
	private static final int REQUEST_EDIT_ENTRY = 2;

	private static final int CONTEXT_MENU_EDIT = 3;
	private static final int CONTEXT_MENU_DELETE = 4;

	private EntryListAdapter entryAdapter;
	private Spinner spinner;

	private Entry entryForContextMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Quickcopy - entry list");
		
		if (Global.getPrefs(this).getBoolean("integration.shownotification", false)) {
			startService(new Intent(this, NotificationService.class));
		}
		
		setContentView(R.layout.entrylistactivity);

		DBHelper db = DBHelper.get(this);

		ArrayList<Entry> entries = db.getEntriesFromGroup(db.getDummyGroup());

		entryAdapter = new EntryListAdapter(this, entries);

		ListView lv = (ListView) findViewById(R.id.listview_entrylist);
		lv.setAdapter(entryAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Entry entry = (Entry) adapterView.getAdapter()
						.getItem(position);
				onEntryClicked(entry);
				return;
			}
		});

		registerForContextMenu(lv);

		fillGroupList();

		AdView adview;
		adview = (AdView) findViewById(R.id.ad);
		adview.setVisibility(AdView.VISIBLE);

	}

	protected void onEntryClicked(Entry entry) {
		QuickcopyUtils.copyToClipBoard(this, entry.value);
		Toast.makeText(
				this,
				"The value of item \"" + entry.key
						+ "\" is copied to the clipboard", Toast.LENGTH_LONG)
				.show();
		finish();
	}

	protected void onEntryLongClicked(Entry entry) {

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		entryForContextMenu = (Entry) entryAdapter.getItem(info.position);
		menu.setHeaderTitle(entryForContextMenu.key);
		menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit entry");
		menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete entry");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_MENU_EDIT:
			Intent i = new Intent(this, NewEntryActivity.class);
			i.putExtra(Global.QUICKCOPY_ENTRY_ID, entryForContextMenu.id);
			startActivityForResult(i, REQUEST_EDIT_ENTRY);
			break;
		case CONTEXT_MENU_DELETE:
			DBHelper.get(this).deleteEntry(entryForContextMenu.id);
			refreshList();
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "New entry").setIcon(R.drawable.ic_menu_add);
		menu.add(0, 1, 0, "New group").setIcon(R.drawable.ic_menu_add);
		menu.add(0, 2, 1, "Preferences")
				.setIcon(R.drawable.ic_menu_preferences);
		menu.add(0, 3, 1, "Help").setIcon(R.drawable.ic_menu_info_details);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this, NewEntryActivity.class);
			i.putExtra(Global.QUICKCOPY_GROUP, ((Group)spinner.getSelectedItem()).name);
			startActivityForResult(i,
					REQUEST_NEW_ENTRY);
			break;
		case 1:
			showAddGroupDialog();
			break;
		case 2:
			startActivity(new Intent(this, Preferences.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_NEW_ENTRY && resultCode == RESULT_OK) {
			refreshList();
		}
	}

	private void refreshList() {
		ArrayList<Entry> entries = DBHelper.get(this).getEntriesFromGroup(
				(Group) spinner.getSelectedItem());
		entryAdapter.updateEntries(entries);
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
		
		String defaultGroup = Global.getPrefs(this).getString("integration.defaultgroup", "");
		setSpinnerToGroup(defaultGroup);
	}

	private void changeGroup(Group group) {
		ArrayList<Entry> entries = DBHelper.get(this)
				.getEntriesFromGroup(group);
		entryAdapter.updateEntries(entries);
		updateNoEntriesTextView();
	}

	private void updateNoEntriesTextView() {
		TextView tv = (TextView)findViewById(R.id.tv_noentries);
		if (entryAdapter.isEmpty()) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	private void showAddGroupDialog() {
		final View v = View.inflate(this, R.layout.addgroupdialog, null);
		final EditText text = (EditText) v.findViewById(R.id.et_groupname);
		new AlertDialog.Builder(this).setTitle("Add group").setView(text)
				.setPositiveButton("Add", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = text.getText().toString();
						addGroup(name);
						fillGroupList();
						setSpinnerToGroup(name);
					}
				}).setNegativeButton("Cancel", null).show();
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

	protected void addGroup(String string) {
		DBHelper.get(this).addGroup(string);
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
			inflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
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
				result = inflater
						.inflate(R.layout.entrylistitem, parent, false);
			}

			// fill up values
			titleView = (TextView) result
					.findViewById(R.id.entrylistitem_title);
			valueView = (TextView) result
					.findViewById(R.id.entrylistitem_value);

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
