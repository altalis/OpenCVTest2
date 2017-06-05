package com.example.daniel.opencvtest2;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CvCameraViewFrame inputFrame;

    private Rect rect, rect1;
    private  Mat outputImg;
    private static int width, height, x, y, RGBA, visualizarVar;
    private Button visualizarButton, atrasButton;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    mOpenCvCameraView.enableView();
                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };


    private void initializeOpenCVDependencies() throws IOException {
        mOpenCvCameraView.enableView();
        RGBA=1;
        visualizarVar=0;
        rect = new Rect();
        outputImg = new Mat();
    }

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        atrasButton = (Button) findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button1 clicked.");
                RGBA=1;
                visualizarVar=0;
            };
        });

        visualizarButton = (Button) findViewById(R.id.visualizarButton);
        visualizarButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button2 clicked.");
                RGBA=0;
                visualizarVar=1;
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

    public void dibujarRectangulo(Mat aInputFrame){

        width = aInputFrame.width();
        height = aInputFrame.height();

        x = (int) (rect.tl().x + rect.br().x)/2;
        y = (int) (rect.tl().y + rect.br().y)/2;

        rect1 = new Rect((width/12) - x, (height/8) - y, width - (width/6), height - (height/4));

        new Core().rectangle(aInputFrame, rect1.tl(), rect1.br(), new Scalar(255, 0, 0), 2, 8, 0);
    }

    public Mat recognize(Mat aInputFrame) {

        dibujarRectangulo(outputImg);

        if (RGBA == 1)
        {
            outputImg = aInputFrame;
            dibujarRectangulo(outputImg); //dibuja desde que el MAT se crea
        }//normal
        if (visualizarVar == 1)
        {
            Intent i = new Intent(MainActivity.this, PanelsActivity.class);
            Imgproc.GaussianBlur(aInputFrame, outputImg, new Size (7,7), 0);
            Bitmap bm = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outputImg, bm);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            i.putExtra("BitmapPanel",bytes);
            startActivity(i);

        }//CannyScale
        return outputImg;
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return recognize(inputFrame.rgba());
    }//onCameraFrame


    @Override
    public void onCameraViewStarted(int width, int height) {
        outputImg = new Mat(height, width, CvType.CV_8UC4);
        dibujarRectangulo(outputImg); //dibuja desde que el MAT se cre
    }

    @Override
    public void onCameraViewStopped() {

        outputImg.release();
    }

}//MainActivity
