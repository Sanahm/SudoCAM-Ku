package com.example.mohamed.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraProcessActivity extends AppCompatActivity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCVSample::Activity";

    private CameraView mOpenCvCameraView;
    private int[][] filledSud;
    private int[][] emptySud;

    private boolean state = true;
    private boolean processState = false;
    private Mat currentFrame = null;
    private ImageButton help_cam = null;

    static{ System.loadLibrary("opencv_java3"); }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraProcessActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraProcessActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.camera_surface_view);
        ActionBar toggle = getSupportActionBar();
        if(toggle != null) {
            toggle.setDisplayHomeAsUpEnabled(true);
            toggle.setHideOnContentScrollEnabled(true);
        }
        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        help_cam = (ImageButton) findViewById(R.id.help_cam);
        help_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CameraProcessActivity.this);
                builder.setMessage(R.string.msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                        });*/
                builder.show();
            }
        });

        ImageButton save = (ImageButton) findViewById(R.id.save_frame);
        if(save != null){
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File path = Environment.getExternalStorageDirectory();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                    String currentDateandTime = sdf.format(new Date());

                    String fileName = "grid_"+currentDateandTime+".jpg";
                    File file = new File(path,fileName);
                    Imgcodecs.imwrite(file.getAbsolutePath(),CameraProcessActivity.this.currentFrame);
                    Toast.makeText(CameraProcessActivity.this,"The picture"+fileName+ "was saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void init(){
        CameraProcessActivity.this.processState = true;
        CameraProcessActivity.this.state = true;
        CameraProcessActivity.this.filledSud = new int[9][9];
        CameraProcessActivity.this.emptySud = new int[9][9];
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        init();
        CameraProcessActivity.this.processState = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        init();
        CameraProcessActivity.this.processState = false;
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        init();
        CameraProcessActivity.this.processState = false;
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat img = inputFrame.rgba();
        /*if(CameraProcessActivity.this.onTouch){
            //CameraProcessActivity.this.picture = img;
            CameraProcessActivity.this.onTouch = false;
        }*/
        this.currentFrame = img;
        if(CameraProcessActivity.this.state) return img;
        else {
            Mat im = Thresholding.normalThresholding(inputFrame.gray());
            try{
                Mat stat = Processing.ExtractBoxes(im,8);
                Processing.drawNumbers(img,CameraProcessActivity.this.emptySud,CameraProcessActivity.this.filledSud,stat,Processing.getNumberColor());
                this.currentFrame = img;
                return img;
            }catch (Exception e){
                return img;
            }
        }
    }


    public class ReadingImageThread extends Thread{
        private File file;
        private Mat image;
        ReadingImageThread(File file){
            this.file = file;
        }

        @Override
        public void run(){
            InputStream stream = null;
            try {
                stream = new FileInputStream(this.file.getAbsolutePath());
            }catch (FileNotFoundException e){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraProcessActivity.this,"Something got wrong during processing", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            Bitmap bmp = BitmapFactory.decodeStream(stream);
            Mat img = new Mat();
            Utils.bitmapToMat(bmp,img);

            Imgproc.cvtColor(img,img,Imgproc.COLOR_RGB2GRAY);
            this.image = img;
        }

        public Mat getImage(){ return this.image;}
    }


    @Override
    public synchronized boolean onTouch(View v, MotionEvent event) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"onTouch event");
                File path = Environment.getExternalStorageDirectory();

                String fileName = "grid.jpg";
                File file = new File(path,fileName);
                if(CameraProcessActivity.this.processState){
                    CameraProcessActivity.this.processState = false; //don't forget to reset to avoid processing when screen is touched another time
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraProcessActivity.this,"wait for processing", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //ReadingImageThread t1 = new ReadingImageThread(file);
                    //t1.run();
                    Mat img = mOpenCvCameraView.getImage();//t1.getImage();
                    //Imgproc.cvtColor(img,img,Imgproc.COLOR_RGB2GRAY);
                    //if(img ==null) Toast.makeText(CameraProcessActivity.this,"gdfbd", Toast.LENGTH_LONG).show();
                    Mat enhance = new Mat();
                    try{
                        enhance = Utils.loadResource(CameraProcessActivity.this, R.drawable.grid, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                    }catch (Exception e){
                        throw new IllegalArgumentException("Unable to load picture");
                    }

                    try{
                        BoxesExtractionThread t2 = new BoxesExtractionThread(CameraProcessActivity.this,img,enhance);

                        t2.run();

                        ArrayList<Mat> imBox = t2.getImbox();

                        String modelFileName ="file:///android_asset/optimized_tensor_v12.pb";
                        String[] outputNames = {"output","Relu_3"};


                        RunModelThread t3 = new RunModelThread(getAssets(),"x",outputNames,modelFileName,imBox);
                        t3.run();

                        CameraProcessActivity.this.emptySud = t3.getGrid();

                        SudokuSolver sudo = new SudokuSolver(CameraProcessActivity.this.emptySud);
                        if(!sudo.solve()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CameraProcessActivity.this,"Can't solve the grid! your grid is perhaps either wrong or the image detection failed", Toast.LENGTH_LONG).show();
                                }
                            });

                        }else{
                            CameraProcessActivity.this.filledSud = sudo.getGrid();

                            CameraProcessActivity.this.state = false;
                        }
                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraProcessActivity.this,"The picture took is not processable", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }else{
                    init();//so the second time the screen is touched it is for processing
                    mOpenCvCameraView.takePicture(file.getAbsolutePath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraProcessActivity.this,"photo was taken", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        t.start();
        try{t.join(1000);}catch (Exception e){CameraProcessActivity.this.processState = false; }

        return false;
    }
}