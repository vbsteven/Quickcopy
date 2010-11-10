package be.vbsteven.qccommon;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import be.vbsteven.quickcopy.R;


public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.helpactivity);
		
		Button but = (Button)findViewById(R.id.button_buy_full);
		but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent goToMarket = null;
				goToMarket = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=be.vbsteven.quickcopyfull"));
				startActivity(goToMarket);
			}
		});
		
		
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
