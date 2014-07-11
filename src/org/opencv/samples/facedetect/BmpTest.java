package org.opencv.samples.facedetect;

import android.content.Context;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class BmpTest extends SurfaceView implements SurfaceHolder.Callback
{
	SurfaceHolder sfh;
	public BmpTest(Context context) {
		super(context);
		getHolder().addCallback(this);
		// TODO Auto-generated constructor stub
	
	}
	
	
	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i("OCVSample", "xyza now in surfacechanged function ln30!!");
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("OCVSample", "xyza now in surfacecreated function ln30!!");
	    // draw AFTER surface created!

	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("OCVSample", "xyza now in surfacedestroyed function ln37!!");

	}

	public void displayimg(Bitmap img2display)
	{
		//Log.i("OCVSample::Activity", "1002 am the type of sfh is " + sfh.getClass().getName());
		//getHolder().addCallback(this);
		//Log.i("OCVSample::Activity","type of newly created sfh is " + sfh.getClass().getName()); 
		Log.i("OCVSample::Activity", "arrived at displayimg function b4 ifelse");
		Log.i("OCVSample::Activity", "type of getholder is " + getHolder().getClass().getName());
		//getHolder() is not null; so look to lockCanvas for the problem
		Canvas canvas = getHolder().lockCanvas();
		if (getHolder().isCreating())
        {
        	Log.i("OCVSample", "bmp the getholder() is still creating!!");
        }
        else{
        	Log.i("OCVSample", "bmp the getholder() is done creating!!");
        }
		
		//Log.i("OCVSample::Activity", "1002 am the type of sfh is " + sfh.getClass().getName());
		if (canvas != null) {
			Log.i("OCVSample::Activity", "canvas is nonnull!!!");
            canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
            

            
                canvas.drawBitmap(img2display, new Rect(0,0,img2display.getWidth(), img2display.getHeight()),
                     new Rect((int)((canvas.getWidth() - img2display.getWidth()) / 2),
                     (int)((canvas.getHeight() - img2display.getHeight()) / 2),
                     (int)((canvas.getWidth() - img2display.getWidth()) / 2 + img2display.getWidth()),
                     (int)((canvas.getHeight() - img2display.getHeight()) / 2 + img2display.getHeight())), null);
            

            
            getHolder().unlockCanvasAndPost(canvas);
            
        }
		else{
			Log.i("OCVSample::Activity", "canvas is null!!");
			
		}
	}
}