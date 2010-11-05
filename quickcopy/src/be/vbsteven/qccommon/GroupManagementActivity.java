package be.vbsteven.qccommon;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import be.vbsteven.quickcopyfull.R;

public class GroupManagementActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

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
}
