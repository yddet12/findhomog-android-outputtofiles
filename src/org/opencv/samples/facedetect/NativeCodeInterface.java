//3 places where need to change args
package org.opencv.samples.facedetect;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class NativeCodeInterface
{
    /*Constructor, called on startup "Part One"*/
    public NativeCodeInterface(String haarfile, String idfile) {
        mNativeObj = nativeCreateObject();//haarfile, idfile);
    }

  
    public void setMinFaceSize(int size) {
        //nativeSetFaceSize(mNativeObj, size);
    }
   
    /*Loop, called with onCameraFrame "Part Two"*/
    public void nativeLoopInterface(Mat imgGrayPrev, Mat imgGrayCurrent) {
        nativeLoop(mNativeObj, imgGrayPrev.getNativeObjAddr(), imgGrayCurrent.getNativeObjAddr());
    }

    public void release() {
        nativeDestroyObject(mNativeObj); //this line was originally commented out
        //mNativeObj = 0;
    }
    public void start() {
        //nativeStart(mNativeObj);
    }

    public void stop() {
        //nativeStop(mNativeObj);
    }

    private long mNativeObj = 0;

    private static native long nativeCreateObject();//String haarfile, String identityfile);
    private static native void nativeDestroyObject(long thiz);
    private static native void nativeStart(long thiz);
    private static native void nativeStop(long thiz);
    private static native void nativeLoop(long homogfile, long inputImage1, long inputImage2);
}