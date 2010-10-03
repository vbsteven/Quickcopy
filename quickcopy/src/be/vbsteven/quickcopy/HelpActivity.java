package be.vbsteven.quickcopy;
import android.app.Activity;
import android.os.Bundle;


public class HelpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		QuickcopyUtils.setTheme(this);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.helpactivity);
		
	}
}
