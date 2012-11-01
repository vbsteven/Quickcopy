package be.vbsteven.qccommon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import be.vbsteven.quickcopyfull.R;

public class GroupManagementActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setTitle("Groups");
		init();
	}

	private void init() {
		ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, R.layout.simple_list_item_1);
		ArrayList<Group> groups = DBHelper.get(this).getGroups();
		for (Group g: groups){
			adapter.add(g);
		}
		setListAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		init();
		super.onResume();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Group g = (Group)getListView().getItemAtPosition(position);
		Intent i = new Intent(this, NewGroupActivity.class);
		i.putExtra(Global.QUICKCOPY_GROUP, g.id);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "New group").setIcon(R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		showAddGroupDialog();
		return true;
	}
	
	private void showAddGroupDialog() {
		final View v = View.inflate(this, R.layout.addgroupdialog, null);
		final EditText text = (EditText) v.findViewById(R.id.et_groupname);
		new AlertDialog.Builder(this).setTitle("Add group").setView(text)
				.setPositiveButton("Add", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = text.getText().toString();
						DBHelper.get(GroupManagementActivity.this).addGroup(name);
						init();
					}
				}).setNegativeButton("Cancel", null).show();
	}
}
