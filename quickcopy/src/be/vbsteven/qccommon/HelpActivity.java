package be.vbsteven.qccommon;
import android.app.Activity;
import android.os.Bundle;
import be.vbsteven.quickcopy.R;


public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.helpactivity);
		
	}
}
