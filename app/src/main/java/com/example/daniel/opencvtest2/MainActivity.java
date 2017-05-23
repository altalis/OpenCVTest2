package com.example.daniel.opencvtest2;

import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
//import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CvCameraViewFrame inputFrame;

    //private JavaCameraView javaCameraView;
    private Mat rawImage, grayImage, dst, dst_norm;
    private double threshold1, threshold2;
    public static int hough_threshold = 50;

    private Button HoughLinesPScaleButton, normalScaleButton, cannyScaleButton, cornerHarrisButton;
    private int i=0, HoughLinesPScale=0, RGBA=1, CannyScale=0, CornerHarrisScale=0;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    mOpenCvCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HoughLinesPScaleButton = (Button) findViewById(R.id.HoughLinesPScaleButton);
        HoughLinesPScaleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button1 clicked.");
                RGBA=0;
                HoughLinesPScale=1;
                CannyScale=0;
                CornerHarrisScale=0;
            };
        });

        normalScaleButton = (Button) findViewById(R.id.normalScaleButton);
        normalScaleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button2 clicked.");
                RGBA=1;
                HoughLinesPScale=0;
                CannyScale=0;
                CornerHarrisScale=0;
            };
        });

        cannyScaleButton = (Button) findViewById(R.id.cannyScaleButton);
        cannyScaleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button3 clicked.");
                RGBA=0;
                HoughLinesPScale=0;
                CannyScale=1;
                CornerHarrisScale=0;
            };
        });

        cornerHarrisButton = (Button) findViewById(R.id.cornerHarrisScaleButton);
        cornerHarrisButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button4 clicked.");
                RGBA=0;
                HoughLinesPScale=0;
                CannyScale=0;
                CornerHarrisScale=1;
            };
        });

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV successfully loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            Log.d(TAG, "OpenCV not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        rawImage = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

        rawImage.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        if (HoughLinesPScale == 1)
        {
            rawImage = inputFrame.rgba();
            grayImage = rawImage.clone();

       	    /*Convert to gray and apply canny*/
            Imgproc.cvtColor(rawImage, grayImage, Imgproc.COLOR_RGB2GRAY);
            Imgproc.Canny(grayImage, rawImage, 80, 100);

            int minLineSize = 20;
            int lineGap = 20;
            Mat lines = rawImage.clone();

            threshold1 = 50;

            Imgproc.HoughLinesP(rawImage, lines, 1, Math.PI/180, hough_threshold, minLineSize, lineGap);
            for (int x = 0; x < lines.cols(); x++)
            {
                double[] vec = lines.get(0, x);
                double x1 = vec[0],
                        y1 = vec[1],
                        x2 = vec[2],
                        y2 = vec[3];
                Point start = new Point(x1, y1);
                Point end = new Point(x2, y2);

                Core.line(rawImage, start, end, new Scalar(255,0,0), 3);
            }
        }
        if (RGBA == 1)
        {
            rawImage = inputFrame.rgba();
        }
        if (CannyScale == 1)
        {
            threshold1 = 500;
            threshold2 = 500;

            rawImage = inputFrame.rgba();
            Imgproc.Canny(rawImage, rawImage, threshold1, threshold2);
        }
        if (CornerHarrisScale == 1)
        {
            grayImage = rawImage.clone();
            Imgproc.cvtColor(rawImage, grayImage, Imgproc.COLOR_RGB2GRAY);
            dst = rawImage.clone();
            dst_norm = rawImage.clone();
            dst = Mat.zeros(rawImage.size(), CvType.CV_32FC1);

            /*OTSU Threshold*/
            //Imgproc.threshold(grayImage, grayImage, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

           /* Detecting corners*/
            Imgproc.cornerHarris(grayImage, dst, 7, 5, 0.05, Imgproc.BORDER_DEFAULT);

           /* Normalizing */
            Core.normalize(dst, dst_norm, 0, 255, Core.NORM_MINMAX, CvType.CV_32FC1);
            Core.convertScaleAbs(dst_norm, dst );

            int thresh = 100;
           /* Drawing a circle around corners */
            for( int j = 0; j < dst_norm.rows() ; j++ )
            {
                for( int i = 0; i < dst_norm.cols(); i++ )
                {
                    if( (int) dst_norm.get(j, i)[0] > thresh )
                    {
                        Core.circle(rawImage, new Point( i, j ), 5, new Scalar(255), 2, 8, 0 );
                    }
                }
            }

        }
    return rawImage;
    }

}
