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
import android.widget.ImageView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;




public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private CameraBridgeViewBase mOpenCvCameraView;
    private CvCameraViewFrame inputFrame;

    private File cacheDir;

    private Rect rect, rect1;
    private  Mat outputImg, templateImg, templateGrayImg;
    private static int width, height, x, y, RGBA, visualizarVar, evaluarVar, flag;
    private Button visualizarButton, normalButton, evaluarVarButton;
    private double threshold;

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
        evaluarVar=0;
        flag=0;
        rect = new Rect();
        outputImg = new Mat();
        templateImg = new Mat();
        templateGrayImg = new Mat();
        threshold = 0.8;
    }

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        normalButton = (Button) findViewById(R.id.normalButton);
        normalButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button1 clicked.");
                RGBA=1;
                visualizarVar=0;
                evaluarVar=0;
            };
        });

        evaluarVarButton = (Button) findViewById(R.id.evaluarVarButton);
        evaluarVarButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button2 clicked.");
                RGBA=0;
                visualizarVar=0;
                evaluarVar=1;
            };
        });

        visualizarButton = (Button) findViewById(R.id.visualizarButton);
        visualizarButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Button3 clicked.");
                RGBA=0;
                visualizarVar=1;
                evaluarVar=0;
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
            flag = 0;

        }//normal
        if (evaluarVar == 1)
        {
            try {
                matchTemplate(aInputFrame, outputImg, Imgproc.TM_SQDIFF);
                flag = 1;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }//normal
        if (visualizarVar == 1)
        {
            if(flag == 1){
                flag = 0;
                Intent i = new Intent(MainActivity.this, PanelsActivity.class);
                Imgproc.GaussianBlur(aInputFrame, outputImg, new Size (7,7), 0);
                Bitmap bm = Bitmap.createBitmap(outputImg.cols(), outputImg.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(outputImg, bm);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                i.putExtra("BitmapPanel",bytes);
                startActivity(i);
            }
            else{
                //Toast.makeText(MainActivity.this, "Por favor evalua primero la imagen!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Por favor evalua primero la imagen!");
           }

        }//CannyScale
        return outputImg;
    }

    public void matchTemplate(Mat inFile, Mat outFile, int match_method) {
        Log.i(TAG, "Running Template Matching");

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setDrawingCacheEnabled(true);
        imageView1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView1.layout(0, 0,
                imageView1.getMeasuredWidth(), imageView1.getMeasuredHeight());
        imageView1.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView1.getDrawingCache());
        imageView1.setVisibility(View.INVISIBLE);
        imageView1.setDrawingCacheEnabled(false);

        Mat img = inFile;
        Mat templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        double minVal; double maxVal;
        if (match_method == Imgproc.TM_CCOEFF
                || match_method == Imgproc.TM_CCOEFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        // / Show me what you got
        new Core().rectangle(outFile, matchLoc, new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()), new Scalar(255,255,0));

        // Save the visualized detection.
        Log.i(TAG, "Writing: " + outFile);
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
