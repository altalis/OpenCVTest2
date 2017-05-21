package com.example.daniel.opencvtest2;

import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CvCameraViewFrame inputFrame;

    //private JavaCameraView javaCameraView;
    private Mat rawImage;
    //private double threshold1, threshold2;
    //private Imgproc imgprocTemp = new Imgproc();

    private Button grayScaleButton, normalScaleButton;
    private int i=0, GrayScale=0, RGBA=1;

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

        grayScaleButton = (Button) findViewById(R.id.grayScaleButton);
        grayScaleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button1 clicked.");
                RGBA=0;
                GrayScale=1;
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
                GrayScale=0;
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
        rawImage = new Mat(height, width, CvType.CV_8SC4);
    }

    @Override
    public void onCameraViewStopped() {
        rawImage.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //threshold1 = 50;
        //threshold2 = 200;
        rawImage = null;

        if (GrayScale == 1)
        {
            rawImage = inputFrame.gray();
        }
        if (RGBA == 1)
        {
            rawImage = inputFrame.rgba();
        }

        /*rawImage = inputFrame.rgba();
        grayImage = rawImage;
        cornersImage = rawImage;
        imgprocTemp.cvtColor(rawImage, grayImage, imgprocTemp.COLOR_RGB2GRAY);
        imgprocTemp.cornerHarris(grayImage, cornersImage, 2, 3, 0.04, imgprocTemp.BORDER_DEFAULT);*/

        return rawImage;
    }

}
