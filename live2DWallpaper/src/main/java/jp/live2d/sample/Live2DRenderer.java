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

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.framework.L2DPhysics;
import jp.live2d.framework.L2DStandardID;
import jp.live2d.framework.L2DTargetPoint;
import jp.live2d.motion.Live2DMotion;
import jp.live2d.motion.MotionQueueManager;
import android.content.Context;
import android.content.res.AssetManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.rbgrn.android.glwallpaperservice.*;

public class Live2DRenderer implements GLWallpaperService.Renderer
{
	Context con;

	Live2DModelAndroid	live2DModel ;
	Live2DMotion motion;
	MotionQueueManager motionMgr;
	L2DTargetPoint dragMgr;
	L2DPhysics physics;

	final String MODEL_PATH = "epsilon/Epsilon.moc" ;
	final String TEXTURE_PATHS[] =
		{
			"epsilon/Epsilon.1024/texture_00.png" ,
			"epsilon/Epsilon.1024/texture_01.png" ,
			"epsilon/Epsilon.1024/texture_02.png"
		} ;
	final String MOTION_PATH="epsilon/motions/Epsilon_idle_01.mtn";
	final String PHYSICS_PATH="epsilon/Epsilon.physics.json";

	float glWidth=0;
	float glHeight=0;


	public Live2DRenderer(Context context)
	{
		con = context;
		dragMgr=new L2DTargetPoint();
		motionMgr=new MotionQueueManager();
	}



	public void onDrawFrame(GL10 gl) {
        // Your rendering code goes here
		
		gl.glMatrixMode(GL10.GL_MODELVIEW ) ;
		gl.glLoadIdentity() ;
		gl.glClear( GL10.GL_COLOR_BUFFER_BIT ) ;
		gl.glEnable( GL10.GL_BLEND ) ;
		gl.glBlendFunc( GL10.GL_ONE , GL10.GL_ONE_MINUS_SRC_ALPHA ) ;
		gl.glDisable( GL10.GL_DEPTH_TEST ) ;
		gl.glDisable( GL10.GL_CULL_FACE ) ;


		live2DModel.loadParam();

		if(motionMgr.isFinished())
		{
			motionMgr.startMotion(motion, false);
		}
		else
		{
			motionMgr.updateParam(live2DModel);
		}

		live2DModel.saveParam();

		dragMgr.update();

		float dragX=dragMgr.getX();
		float dragY=dragMgr.getY();
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X, dragX*30);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y, dragY*30);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X, dragX*10);

		physics.updateParam(live2DModel);

		live2DModel.setGL( gl ) ;

		live2DModel.update() ;
		live2DModel.draw() ;

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	
		gl.glViewport( 0 , 0 , width , height ) ;

		
		gl.glMatrixMode( GL10.GL_PROJECTION ) ;
		gl.glLoadIdentity() ;

		float modelWidth = live2DModel.getCanvasWidth();
		
		gl.glOrthof(
				0 ,
				modelWidth ,
				modelWidth * height / width,
				0 ,
				0.5f ,	-0.5f 
				) ;

		glWidth=width;
		glHeight=height;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
    	AssetManager mngr = con.getAssets();
		try
		{
			InputStream in = mngr.open( MODEL_PATH ) ;
			live2DModel = Live2DModelAndroid.loadModel( in ) ;
			in.close() ;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			//texture
			for (int i = 0 ; i < TEXTURE_PATHS.length ; i++ )
			{
				InputStream in = mngr.open( TEXTURE_PATHS[i] ) ;
				int texNo = UtOpenGL.loadTexture(gl , in , true ) ;
				live2DModel.setTexture( i , texNo ) ;
				in.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			InputStream in = mngr.open( MOTION_PATH ) ;
			motion = Live2DMotion.loadMotion( in ) ;
			in.close() ;

			in=mngr.open(PHYSICS_PATH);
			physics=L2DPhysics.load(in);
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }

    /**
     * Called when the engine is destroyed. Do any necessary clean up because
     * at this point your renderer instance is now done for.
     */
    public void release() {
    }

    public void resetDrag()
    {
    	dragMgr.set(0, 0);
    }


    public void drag(float x,float y)
    {
    	float screenX=x/glWidth*2-1;
    	float screenY=-y/glHeight*2+1;

//    	Log.i("", "x:"+screenX+" y:"+screenY);

    	dragMgr.set(screenX,screenY);
    }
}
