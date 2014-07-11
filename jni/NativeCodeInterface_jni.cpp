#include <NativeCodeInterface_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/contrib/detection_based_tracker.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/contrib/contrib.hpp"

#include "opencv2/features2d/features2d.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <string>
#include <fstream>
#include <vector>
#include <ctime>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

// This is the datatype that the PrepareFiles function will return
// PrepareFiles loads csv file, training images, and haar cascade file
struct modelandcascade {
   Ptr<FaceRecognizer> themodel;
   CascadeClassifier thecc;
   int w,h;
};

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat)
{
    mat = Mat(v_rect, true);
}// This is the datatype that the PrepareFiles function will return
// PrepareFiles loads csv file, training images, and haar cascade file

struct homogandtimer{
	ofstream writehomogm;
	ofstream speedrecord;
};

JNIEXPORT jlong JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeCreateObject
(JNIEnv * jenv, jclass)
{
	homogandtimer * hat = new homogandtimer;

	hat->speedrecord.open("/data/data/org.opencv.samples.facedetect/speedd.txt");
	hat->speedrecord << "testing write to speedtest\n";
	hat->speedrecord.flush();
	LOGD("passed just streamed to speedd.txt");
	hat->writehomogm.open("/data/data/org.opencv.samples.facedetect/homog.txt");
	hat->writehomogm << "testing write to homogfile\n";
	hat->writehomogm.flush();
	LOGD("passed just streamed to homog.txt");


    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject exit");
    return (jlong)hat;
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeDestroyObject
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject enter");
    try
    {
        if(thiz != 0)
        {

            //((DetectionBasedTracker*)thiz)->stop();
            //delete (DetectionBasedTracker*)thiz;
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDestroyObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
    }
    homogandtimer *thehat = (homogandtimer *)thiz;
    thehat->speedrecord.close();
    LOGD("passed closed speed");
    thehat->writehomogm.close();
    LOGD("passed closed homog");

    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeStart
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart enter");
    try
    {
        //((DetectionBasedTracker*)thiz)->run();
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStart caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStart caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStart()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeStop
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop enter");
    try
    {
        //((DetectionBasedTracker*)thiz)->stop();
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeStop caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStop()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop exit");
}

JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeSetFaceSize
(JNIEnv * jenv, jclass, jlong thiz, jint faceSize)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize enter");
    try
    {
        if (faceSize > 0)
        {
            DetectionBasedTracker::Parameters DetectorParams = \
            ((DetectionBasedTracker*)thiz)->getParameters();
            DetectorParams.minObjectSize = faceSize;
            ((DetectionBasedTracker*)thiz)->setParameters(DetectorParams);
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeStop caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeSetFaceSize caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeSetFaceSize()");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize exit");
}


JNIEXPORT void JNICALL Java_org_opencv_samples_facedetect_NativeCodeInterface_nativeLoop
(JNIEnv * jenv, jclass, jlong hataddr, jlong gray1, jlong gray2)
{
	clock_t t1, t2;
	t1 = clock();
	homogandtimer *hatinloop = (homogandtimer *) hataddr;
    LOGD("passed just entered nativeloop b4 trying");
    try
    {
    	LOGD("passed just entered the try in nativeloop");
    	LOGD("passed char jenv getutfchars");
    	string homogstring;//(jidentitystr); // <--this one
    	LOGD("passed making jidentitystr");

    	//output the matrices to the Log
    	Mat frame1 = *((Mat *)gray1);
    	Mat frame2 = *((Mat *)gray2);
    	LOGD("passed making mats");

    	int minHessian = 400;

    	//initial variable declaration
    	OrbFeatureDetector detector(minHessian);
    	LOGD("passed making detector");
    	std::vector<KeyPoint> keypoints1, keypoints2;
    	LOGD("passed making keypoints");
    	OrbDescriptorExtractor extractor;
    	LOGD("passed making extractor");
    	Mat descriptors1, descriptors2;
    	LOGD("passed making descriptors");

    	//process first frame
    	detector.detect(frame1, keypoints1);
    	LOGD("passed detecting1");
    	extractor.compute(frame1, keypoints1, descriptors1);
    	LOGD("passed computing1");

    	//process second frame
    	detector.detect(frame2, keypoints2);
    	LOGD("passed detecting2");
    	extractor.compute(frame2, keypoints2, descriptors2);
    	LOGD("passed computing2");

    	//in case frame has no features (eg if all-black from finger blocking lens)
    	if (keypoints1.size() == 0){
    		LOGD("passed keypointssize was zero!!");
			frame1 = frame2.clone();
			keypoints1 = keypoints2;
			descriptors1 = descriptors2;
			//go back to the javacode and continue with the next frame
			return;
    	}

    	LOGD("passed keypointssize not zero!");
    	//Now match the points on the successive images
    	//FlannBasedMatcher matcher;
    	BFMatcher matcher;
    	LOGD("passed creating matcher");
    	std::vector<DMatch> matches;
    	LOGD("passed creating matches");
    	if(descriptors1.empty()){
    		LOGD("passed descriptors1 is empty!");
    	}
    	if(descriptors2.empty()){
    		LOGD("passed descriptors2 is empty!");
    	}
    	LOGD("passed key1 size %d", keypoints1.size());
    	LOGD("passed key2 size %d", keypoints2.size());

    	matcher.match(descriptors1, descriptors2, matches);
    	LOGD("passed doing the matching");

    	//eliminate weaker matches
    	double maxdist = 0;
		double mindist = 100;
		for (int j = 0; j < descriptors1.rows; j++){
			DMatch match = matches[j];
			double dist = match.distance;
			if( dist < mindist ) mindist = dist;
			if( dist > maxdist ) maxdist = dist;
		}

		//build the list of "good" matches
		std::vector<DMatch> goodmatches;
		for( int k = 0; k < descriptors1.rows; k++ ){
			DMatch amatch = matches[k];
			if( amatch.distance <= 3*mindist ){
				goodmatches.push_back(amatch);
			}
		}

	//Now compute homography matrix between the stronger matches
		//-- Localize the object
		std::vector<Point2f> obj;
		std::vector<Point2f> scene;
		if (goodmatches.size() < 4){
			frame1 = frame2.clone();
			keypoints1 = keypoints2;
			descriptors1 = descriptors2;
			return;
		}

		for(int l = 0; l < goodmatches.size(); l++){
		//-- Get the keypoints from the good matches
			DMatch gm1 = goodmatches[l];
			KeyPoint kp1 = keypoints1[gm1.queryIdx];
			obj.push_back(kp1.pt);

			KeyPoint kp2 = keypoints1[gm1.trainIdx];
			scene.push_back(kp2.pt);
		}

		Mat hmatrix = findHomography(obj,scene,CV_RANSAC);

		hatinloop->writehomogm << hmatrix.at<double>(0,0) << " ";
		LOGD("passed el00  %f",hmatrix.at<double>(0,0));
		LOGD("  ");
		hatinloop->writehomogm << hmatrix.at<double>(0,1) << " ";
		LOGD("passed el01  %f",hmatrix.at<double>(0,1));
		LOGD("  ");
		hatinloop->writehomogm << hmatrix.at<double>(0,2) << " ";

		hatinloop->writehomogm << hmatrix.at<double>(1,0) << " ";
		hatinloop->writehomogm << hmatrix.at<double>(1,1) << " ";
		hatinloop->writehomogm << hmatrix.at<double>(1,2) << " ";

		hatinloop->writehomogm << hmatrix.at<double>(2,0) << " ";
		hatinloop->writehomogm << hmatrix.at<double>(2,1) << " ";
		hatinloop->writehomogm << hmatrix.at<double>(2,2) << " endmatrix\n";

		t2 = clock();
		hatinloop->speedrecord << (float) (t2 - t1)/CLOCKS_PER_SEC << "\n";
		LOGD("passed timingstuff %f",(float) (t2 - t1)/CLOCKS_PER_SEC);

		hatinloop->writehomogm.flush();
		hatinloop->speedrecord.flush();

    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDetect caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI nativeloop's code");
    }
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect exit");

}
