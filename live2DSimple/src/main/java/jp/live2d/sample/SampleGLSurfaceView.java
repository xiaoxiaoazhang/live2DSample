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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.util.UtSystem;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class SampleGLSurfaceView extends GLSurfaceView
{
	private SampleGLRenderer		renderer ;

	public SampleGLSurfaceView(Context context )
	{
		super(context);

		renderer = new SampleGLRenderer() ;
		setRenderer( renderer ) ;
	}


	class SampleGLRenderer implements Renderer
	{
		private Live2DModelAndroid	live2DModel ;
		private final String MODEL_PATH = "haru/haru.moc" ;
		private final String TEXTURE_PATHS[] =
			{
				"haru/haru.1024/texture_00.png" ,
				"haru/haru.1024/texture_01.png" ,
				"haru/haru.1024/texture_02.png"
			} ;

		@Override
		public void onDrawFrame(GL10 gl)
		{
			gl.glMatrixMode(GL10.GL_MODELVIEW ) ;
			gl.glLoadIdentity() ;
			gl.glClear( GL10.GL_COLOR_BUFFER_BIT ) ;

			double t = (UtSystem.getUserTimeMSec()/1000.0) * 2 * Math.PI  ;
			double cycle=3.0;
			double sin=Math.sin( t/cycle );
			live2DModel.setParamFloat( "PARAM_ANGLE_X" , (float) (30 * sin) ) ;

			live2DModel.setGL( gl ) ;

			live2DModel.update() ;
			live2DModel.draw() ;
		}


		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			
			gl.glViewport( 0 , 0 , width , height ) ;

			
			gl.glMatrixMode( GL10.GL_PROJECTION ) ;
			gl.glLoadIdentity() ;

			float modelWidth = live2DModel.getCanvasWidth();
			float aspect = (float)width/height;

			
			gl.glOrthof(0 ,	modelWidth , modelWidth / aspect , 0 , 0.5f , -0.5f ) ;
		}


		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			
			try
			{
				InputStream in = getContext().getAssets().open( MODEL_PATH ) ;
				live2DModel = Live2DModelAndroid.loadModel( in ) ;
				in.close() ;

				for (int i = 0 ; i < TEXTURE_PATHS.length ; i++ )
				{
					InputStream tin = getContext().getAssets().open( TEXTURE_PATHS[i] ) ;
					int texNo = UtOpenGL.loadTexture(gl , tin , true ) ;
					live2DModel.setTexture( i , texNo ) ;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
