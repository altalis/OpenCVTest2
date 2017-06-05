package com.example.daniel.opencvtest2;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CvCameraViewFrame inputFrame;

    //private JavaCameraView javaCameraView;
    private double threshold1, threshold2;
    private Rect rect, rect1;
    private  Mat outputImg;
    private static int hough_threshold, width, height, x, y;

    private Button HoughLinesPScaleButton, normalScaleButton, cannyScaleButton, cornerHarrisButton;
    private static int i=0, HoughLinesPScale=0, RGBA=1, CannyScale=0, CornerHarrisScale=0;

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
        threshold1 = 0;
        threshold2 = 255;
        hough_threshold = 100; //default: 50
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

        /*HoughLinesPScaleButton = (Button) findViewById(R.id.HoughLinesPScaleButton);
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
        });*/

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

        /*cornerHarrisButton = (Button) findViewById(R.id.cornerHarrisScaleButton);
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
        });*/

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

        //outputImg = aInputFrame.clone();
        dibujarRectangulo(outputImg);

        if (RGBA == 1)
        {
            outputImg = aInputFrame;
            //dibujarRectangulo(outputImg); //dibuja desde que el MAT se crea
        }//normal
        if (CannyScale == 1)
        {
            Imgproc.cvtColor(aInputFrame, outputImg, Imgproc.COLOR_RGB2GRAY);
            /*width = aInputFrame.width();
            height = aInputFrame.height();

            x = (int) (rect.tl().x + rect.br().x)/2;
            y = (int) (rect.tl().y + rect.br().y)/2;

            rect1 = new Rect((width/8) - x , (height/8) - y , (width-160), (height-120));

            new Core().rectangle(outputImg, rect1.tl(), rect1.br(), new Scalar(255, 0, 0), 2, 8, 0);*/

            //rawImage = inputFrame.rgba();
            //blur = inputFrame.gray();
            //Imgproc.Canny(rawImage, rawImage, threshold1, threshold2);
            //Imgproc.GaussianBlur(inputFrame.gray(), blur, new Size (5,5), 0);
            //Imgproc.Canny(blur, rawImage, threshold1, threshold2);
            //Imgproc.threshold(blur,rawImage, threshold1, threshold2, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            //Imgproc.adaptiveThreshold(blur, rawImage, threshold2, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_OTSU, 11, 2);

        }//CannyScale

        /*if (HoughLinesPScale == 1)
        {
            rawImage = inputFrame.rgba();
            grayImage = rawImage.clone();

       	    *//*Convert to gray and apply canny*//*
            Imgproc.cvtColor(rawImage, grayImage, Imgproc.COLOR_RGB2GRAY);
            Imgproc.Canny(grayImage, rawImage, 50, 150);
                //default: grayImage, rawImage, 80, 100

            int minLineSize = 100;   //default: 20
            int lineGap = 10;       //default: 20
            Mat lines = rawImage.clone();

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
            }//for x
        }//HoughLinesPScale*/
        /*if (CornerHarrisScale == 1)
        {
            grayImage = rawImage.clone();
            Imgproc.cvtColor(rawImage, grayImage, Imgproc.COLOR_RGB2GRAY);
            dst = rawImage.clone();
            dst_norm = rawImage.clone();
            dst = Mat.zeros(rawImage.size(), CvType.CV_32FC1);

            *//*OTSU Threshold*//*
            //Imgproc.threshold(grayImage, grayImage, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);

           *//* Detecting corners*//*
            Imgproc.cornerHarris(grayImage, dst, 7, 5, 0.05, Imgproc.BORDER_DEFAULT);

           *//* Normalizing *//*
            Core.normalize(dst, dst_norm, 0, 255, Core.NORM_MINMAX, CvType.CV_32FC1);
            Core.convertScaleAbs(dst_norm, dst );

            int thresh = 100;
           *//* Drawing a circle around corners *//*
            for( int j = 0; j < dst_norm.rows() ; j++ )
            {
                for( int i = 0; i < dst_norm.cols(); i++ )
                {
                    if( (int) dst_norm.get(j, i)[0] > thresh )
                    {
                        Core.circle(rawImage, new Point( i, j ), 5, new Scalar(255), 2, 8, 0 );
                    }//if
                }//for i
            }//for j

        }//CornerHarrisScale*/

        /*Imgproc.cvtColor(aInputFrame, aInputFrame, Imgproc.COLOR_RGB2GRAY);
        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();
        detector.detect(aInputFrame, keypoints2);
        descriptor.compute(aInputFrame, keypoints2, descriptors2);

        // Matching
        MatOfDMatch matches = new MatOfDMatch();
        if (img1.type() == aInputFrame.type()) {
            matcher.match(descriptors1, descriptors2, matches);
        } else {
            return aInputFrame;
        }
        List<DMatch> matchesList = matches.toList();

        Double max_dist = 0.0;
        Double min_dist = 100.0;

        for (int i = 0; i < matchesList.size(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= (1.5 * min_dist))
                good_matches.addLast(matchesList.get(i));
        }

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return aInputFrame;
        }
        Features2d.drawMatches(img1, keypoints1, aInputFrame, keypoints2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);
        Imgproc.resize(outputImg, outputImg, aInputFrame.size());*/

        return outputImg;
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return recognize(inputFrame.rgba());
    }//onCameraFrame


    @Override
    public void onCameraViewStarted(int width, int height) {
        outputImg = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

        outputImg.release();
    }

}//MainActivity
