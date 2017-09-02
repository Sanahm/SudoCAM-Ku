package com.example.mohamed.sudoku;

import java.io.FileOutputStream;
import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
//import android.hardware.camera2.*;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class CameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "Sample::CameraView";
    private String mPictureFileName;
    private Mat mImage;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();

    }
    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            /*FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();*/

            Bitmap bmp = BitmapFactory.decodeByteArray(data , 0, data.length);
            Mat orig = new Mat(bmp.getHeight(),bmp.getWidth(),CvType.CV_8UC3);
            Bitmap myBitmap32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(myBitmap32, orig);
            mImage = new Mat();
            Imgproc.cvtColor(orig,mImage,Imgproc.COLOR_RGB2GRAY);
            /*Imgproc.cvtColor(orig, orig, Imgproc.COLOR_BGR2RGB,4);
            Mat frame = new Mat(mFrameHeight+mFrameHeight/2,mFrameWidth, CvType.CV_8UC1);
            frame.put(0,0,data);
            //Imgcodecs.imdecode(frame,0);
            Imgproc.cvtColor(frame,mImage,Imgproc.COLOR_YUV2RGBA_NV21);//frame.submat(0, mFrameHeight, 0, mFrameWidth);*/

        } catch (Exception e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }

    public Mat getImage(){
        return mImage;
    }
}
