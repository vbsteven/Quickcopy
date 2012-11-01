package be.vbsteven.qccommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import be.vbsteven.quickcopyfull.DBHelper;
import be.vbsteven.quickcopyfull.Global;
import be.vbsteven.quickcopyfull.R;

public class NewGroupActivity extends Activity {
	
	Group group = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newgroupactivity);
        setTitle("New group");
		
		
		Button saveButton = (Button)findViewById(R.id.but_save_group);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et = (EditText)findViewById(R.id.et_title);
				if (et.getText().toString().length() > 0) {
					saveGroup();
					finish();
				} else {
					AlertDialog dialog = new AlertDialog.Builder(NewGroupActivity.this)
						.setTitle("Groupname is empty")
						.setPositiveButton("OK", null).create();
					dialog.show();
				}
			}
		});

		Button deleteButton = (Button)findViewById(R.id.but_delete_group);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteGroup();
				finish();
			}
		});
		
		Button cancelButton = (Button)findViewById(R.id.but_cancel_group);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if (getIntent().hasExtra(Global.QUICKCOPY_GROUP)) {
			int id = getIntent().getIntExtra(Global.QUICKCOPY_GROUP, -1);
			if (id >= 0) {
				Group g = DBHelper.get(this).getGroup(id);
				if (g != null) {
					group = g;
					EditText et = (EditText)findViewById(R.id.et_title);
					et.setText(g.name);
				}
			}
		}
	}
	
	private void saveGroup() {
		if (group != null) {
			EditText et = (EditText)findViewById(R.id.et_title);
			group.name = et.getText().toString();
			DBHelper.get(this).updateGroup(group.id, group.name);
		}
	}
	
	private void deleteGroup() {
		if (group != null) {
			DBHelper.get(this).deleteGroup(group.id);
		}
	}
	
}
