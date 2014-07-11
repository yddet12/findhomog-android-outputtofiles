package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.ByteArrayOutputStream;

import org.opencv.highgui.Highgui;


public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private String				   homog;
    private File                   mCascadeFile;
    private File                   mIdentityFile;

    private CascadeClassifier      mJavaDetector;
    private NativeCodeInterface  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("native_code_interface");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                            //Log.e(TAG, "buffer1: "+  new String(buffer, "UTF-8"));
                        }
                        is.close();
                        os.close();
                       
                        InputStream is2 = getResources().openRawResource(R.raw.lbp_zhongppl);
                        File identityDir = getDir("identity", Context.MODE_PRIVATE);
                        mIdentityFile = new File(identityDir, "lbp_zhongppl.model");
                        FileOutputStream os2 = new FileOutputStream(mIdentityFile);

                        byte[] buffer2 = new byte[4096];
                        int bytesRead2;
                        while ((bytesRead2 = is2.read(buffer2)) != -1) {
                            os2.write(buffer2, 0, bytesRead2);
                            Log.e(TAG, "buffer2: "+  new String(buffer2, "UTF-8"));
                        }
                        is2.close();
                        os2.close();
                        Log.d(TAG, "mIdentityFile.getAbsolutePath(): "+mIdentityFile.getAbsolutePath() );
                        mNativeDetector = new NativeCodeInterface(mCascadeFile.getAbsolutePath(),mIdentityFile.getAbsolutePath());
                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                       
                        cascadeDir.delete();
                        identityDir.delete();
                      
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    /* */
        // Get the file with the list of training-images, and make a list of training-images and numbers
                    ArrayList<String> imgnames = new ArrayList<String>();
                    ArrayList<Integer> imgnumbers = new ArrayList<Integer>();
                    try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("faces.csv")));
                    //imgnames.add(reader.readLine());
                   
                    String myLine = reader.readLine();
                    String[] lineparts = new String[2];
                    lineparts = myLine.split(";");
                    // readLine should give me the entire first line: firstimg.jpg;first#
                    // then should split into lineparts = [firstimg.jpg, first#]
                    Log.i(TAG, "testing lineparts " + lineparts[0] + "  " + lineparts[1]);
                   
                    while (myLine != null) {
                        Log.i(TAG, "adding to imgnames:: " + lineparts[0]);
                        imgnames.add(lineparts[0]);
                        Log.i(TAG, "adding to imgnumbers:: " + Integer.parseInt(lineparts[1]));
                        imgnumbers.add(Integer.parseInt(lineparts[1]));
                        myLine = reader.readLine();
                        if (myLine != null){
                        lineparts = myLine.split(";");}
                        // seems to work thru here
                    }
                    Log.i(TAG, "got to right b4 reader.close");
                    reader.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load training-images list. Exception thrown: " + e);
                    }
        // done that part           
                    // Testing whether it read the csv file correctly
                    Log.i(TAG, "NOW OUTPUTTING FILENAMES THAT ARE 2B READ:");
                    for (int i = 0; i < imgnames.size(); i++)
                    {
                        Log.i(TAG, "filename " + imgnames.get(i));
                        Log.i(TAG, "numberrr " + imgnumbers.get(i));
                       
                    }
                    // seems to work thru here!
                   
     // Now read in those images.
                   /*
																                    for(int i= 0; i < imgnames.size(); i++)
																                    {
																                        try{
																             
																                            InputStream isimgg = getAssets().open(imgnames.get(i));
																                            byte[] fileBytes=new byte[isimgg.available()];
																                            isimgg.read( fileBytes);
																                            isimgg.close();
																                            Log.i(TAG,"succeeded, len is " + fileBytes.length);  // makesure filebytes has the jpg data and is a valid array
																                            Log.i(TAG,"succeeded! the raw image data is now in fileBytes!");
																                            //Mat thematimg = Highgui.imdecode(Mat(fileBytes),1);

																                            Mat[] images = new Mat[imgnames.size()];
																                            //for first image
																                            images[0] = new Mat(112,92,CvType.CV_8UC1);
																                            images[0].put(0,0,fileBytes);
																                           
																                            //DELIVER AND DRAW FRAME; COPY CODE FROM THAT
																                            //GET INTO Bitmap format
																                            Log.i(TAG, "ln200 columns " + images[0].cols());
																                            Log.i(TAG, "ln201 rows " + images[0].rows());
																                            Log.i(TAG, "got to right b4 the mat2bitmap");
																                            Bitmap mybmp = Bitmap.createBitmap(92,112,Bitmap.Config.ARGB_8888);
																                            Utils.matToBitmap(images[0], mybmp);
																                            //test bmp dimensions with name.getWidth() and name.getHeight()
																                            Log.i(TAG,"ln206 width of the bitmap is " + mybmp.getWidth());
																                            Log.i(TAG,"ln207 height of the bitmap is " + mybmp.getHeight());
																                            Log.i(TAG, "blahblah");
																                            Log.i(TAG, "blahblahblah");
																                           
																                            BmpTest mybmptest = new BmpTest(getApplicationContext());
																                            mybmptest.displayimg(mybmp);
																                            Log.i(TAG, "got to ln213");
																                            Log.i(TAG, "got to ln214");
																                           
																                        }
																                        catch (IOException e){
																                            e.printStackTrace();
																                            Log.e(TAG, "I failed! " + e);
																                        }
																                    }
                   */
						
                   
                    //Reading in the image
                   

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        String homog;
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    //^^returns a Mat.  each time it is called, the mat it returns will be drawn onscreen
    	if(mRgba.empty()){
    		/*if empty, then this must be the first time onCameraFrame was called
    		 * so therefore it must be the first frame.  So return the first frame
    		 * and that's it for this time that oncameraframe was called.*/
    		mRgba = inputFrame.rgba();
    		mGray = inputFrame.gray();
    		//doesn't call native code to compute homog
    		//mNativeDetector.nativeLoopInterface(null, mGray);// don't call mnativedetector
    		//just draw the very first frame with no homography matrix
    		return mRgba;    	
    	}
    	else{
    		//not the very first frame
    		Log.i(TAG,  "passed mrgba full");
    		Context context;
    		Log.i(TAG, "passed datadir " + getApplicationInfo().dataDir);
    		//String appPath = FdActivity.getApp().getApplicationContext().getFilesDir().getAbsolutePath();
    		
    		
    		
    		Mat mrgba2 = inputFrame.rgba();
    		Mat gray2 = inputFrame.gray();
    		//call native code with the 2 frames.
    		//native code will put the homog matrix into "homog". then we can somehow output the homog-matrix
    		mNativeDetector.nativeLoopInterface(mGray, gray2);
    		mGray = gray2;
    		
    		//NEED TO SWAP FRAMES (MAKE CURRENT FRAME THE PREV ONE) AFTER RUNNING
    		//NATIVE CODE!!
    		return mrgba2;
    	}
    	/*
        mRgba = inputFrame.rgba();
        if (mNativeDetector != null){
        Log.i(TAG, "oncameraframe");

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

       
        MatOfRect faces = new MatOfRect();
        mNativeDetector.nativeLoopInterface(mGray, faces);

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");
        mItemFace40 = menu.add("Face size 40%");
        mItemFace30 = menu.add("Face size 30%");
        mItemFace20 = menu.add("Face size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
}