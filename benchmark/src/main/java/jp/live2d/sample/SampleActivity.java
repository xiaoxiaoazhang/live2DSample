/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.sample;

import jp.live2d.Live2D;
import jp.live2d.live2dsdk_sample.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;


public class SampleActivity extends Activity
{
	private FpsTextView fpsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fpsTextView = new FpsTextView(this);
        fpsTextView.setTextColor(Color.WHITE);

        Live2D.init();
        SampleGLSurfaceView view = new SampleGLSurfaceView(this, fpsTextView) ;
        setContentView( R.layout.activity_sample ) ;

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        frameLayout.addView(view);
        frameLayout.addView(fpsTextView);

    }
}
