package jp.live2d.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class FpsTextView extends TextView {
	private int updateTime = 1000;
	private int drawCount= 0;
	private long oldTimer=-1;
	private float fps = 0.0f;


    public FpsTextView(Context context) {
        this(context, null);
    }

    public FpsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		drawCount += 1;

		if(oldTimer==-1)
		{
			oldTimer=System.currentTimeMillis();
		}
		else
		{
			long t=System.currentTimeMillis();
			if (t-oldTimer >= updateTime) {
				fps = drawCount * 1000 / (t - oldTimer);
				fps = (float) (Math.floor(fps * 10) / 10);

				Log.d("Debug", "FPS : "+ fps );
				setText("FPS : "+ fps );

				oldTimer = t;
				drawCount = 0;
			}
		}
    }


    public void update()
    {
    	invalidate ();
    }
}