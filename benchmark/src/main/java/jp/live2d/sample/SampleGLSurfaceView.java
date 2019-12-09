/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.motion.Live2DMotion;
import jp.live2d.motion.MotionQueueManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;

public class SampleGLSurfaceView extends GLSurfaceView
{
	public int NUM_MODELS = 5;
	private SampleGLRenderer		renderer ;
	private FpsTextView fpsTextView;

	public SampleGLSurfaceView(Context context, FpsTextView fpsTextView
			)
	{
		super(context);
		this.fpsTextView = fpsTextView;
		renderer = new SampleGLRenderer() ;
		setRenderer( renderer ) ;
	}


	class SampleGLRenderer implements Renderer
	{
		private List<Live2DModelAndroid> models;
		private Live2DMotion motion;
		private MotionQueueManager motionMgr;
		private final String MODEL_PATH = "haru/haru.moc" ;
		private final String TEXTURE_PATHS[] =
			{
				"haru/haru.1024/texture_00.png" ,
				"haru/haru.1024/texture_01.png" ,
				"haru/haru.1024/texture_02.png"
			} ;
		private final String MOTION_PATH = "haru/haru_idle_01.mtn" ;

		private Handler handler = new Handler();

		@Override
		public void onDrawFrame(GL10 gl)
		{
			// FPS計測
			new Thread(new Runnable()
			{
				public void run()
				{
					handler.post(new Runnable()
					{
						public void run()
						{
							fpsTextView.update();
						}
					});
			    }
			}).start();

			gl.glMatrixMode(GL10.GL_MODELVIEW ) ;
			gl.glLoadIdentity() ;
			gl.glClear( GL10.GL_COLOR_BUFFER_BIT ) ;


			if (motionMgr.isFinished())
	        {
	            motionMgr.startMotion(motion,false);
	        }

			float d=models.get(0).getCanvasWidth()/NUM_MODELS/2.0f;
			for (int i = 0; i < NUM_MODELS; i++) {
				Live2DModelAndroid live2DModel=models.get(i);
				live2DModel.setGL( gl ) ;
				gl.glTranslatef(d, 0, 0);
				motionMgr.updateParam(live2DModel);
				live2DModel.update() ;
				live2DModel.draw() ;
			}
		}


		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			
			gl.glViewport( 0 , 0 , width , height ) ;

			
			gl.glMatrixMode( GL10.GL_PROJECTION ) ;
			gl.glLoadIdentity() ;

			float modelWidth = models.get(0).getCanvasWidth();
			float aspect = (float)width/height;

			
			gl.glOrthof(0 ,	modelWidth , modelWidth / aspect , 0 , 0.5f , -0.5f ) ;
		}


		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			models=new ArrayList<Live2DModelAndroid>(NUM_MODELS);
			try
			{
				for (int i = 0; i < NUM_MODELS; i++) {
					InputStream in = getContext().getAssets().open( MODEL_PATH ) ;
					models.add(Live2DModelAndroid.loadModel( in ) );
					in.close() ;

					for (int j = 0 ; j < TEXTURE_PATHS.length ; j++ )
					{
						InputStream tin = getContext().getAssets().open( TEXTURE_PATHS[j] ) ;
						int texNo = UtOpenGL.loadTexture(gl , tin , true ) ;
						models.get(i).setTexture( j , texNo ) ;
					}
				}
				InputStream min = getContext().getAssets().open( MOTION_PATH ) ;
				motion=Live2DMotion.loadMotion(min);
				motionMgr=new MotionQueueManager();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
