package be.vbsteven.qccommon;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import be.vbsteven.quickcopyfull.R;


public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.helpactivity);
		
		
		
		Button shareButton = (Button)findViewById(R.id.button_share);
		shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.putExtra(Intent.EXTRA_TEXT, "I am using Quickcopy. An #Android app that allows me to easily save and reuse text snippets. http://bit.ly/bWoKjW");
				startActivity(share);
			}
		});
	}
}
