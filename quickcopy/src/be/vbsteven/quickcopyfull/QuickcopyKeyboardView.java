package be.vbsteven.quickcopyfull;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class QuickcopyKeyboardView extends LinearLayout {

	private Context context;
	
	public QuickcopyKeyboardView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public QuickcopyKeyboardView(Context context, AttributeSet attrset) {
		super(context, attrset);
		init();
	}
	
	
	private void init() {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int orientation = context.getResources().getConfiguration().orientation;
		
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			super.setMeasuredDimension(700, 160);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			super.setMeasuredDimension(400, 260);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	

	
	

}
