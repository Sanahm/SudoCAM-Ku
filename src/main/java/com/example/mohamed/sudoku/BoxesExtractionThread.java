package com.example.mohamed.sudoku;


import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Mohamed on 26/06/2017.
 */
public class BoxesExtractionThread extends Thread {
    private Mat stat;
    private Mat img;
    private Mat enhance;
    private ArrayList<Mat> imBox;
    //private Context context;

    BoxesExtractionThread(Context context, Mat img, Mat enhance){
        //img: image to be processed and enhance and eventual image to enhance the line of sudoku grid
        this.img = img;
        this.enhance = enhance;
        //this.context = context;
    }

    @Override
    public void run() throws IllegalArgumentException{
        try {
            this.img = Thresholding.threshold(this.img,1);
            this.stat = Processing.ExtractBoxes(this.img,this.enhance,8);
            if(stat == null){
                //then try another method to extract the boxes (adaptative thresholding)
                try{
                    this.img = Thresholding.threshold(this.img,2);
                    this.stat = Processing.ExtractBoxes(this.img,this.enhance,8);
                }catch (Exception e){
                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"The picture took is not processable", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    throw new IllegalArgumentException("Non-processable picture");

                }
            }
            this.imBox = Processing.ExtractImageBoxes(this.stat,this.img.mul(this.enhance), Imgproc.INTER_LINEAR);
        }catch (Exception e){
            /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"The picture took is not processable", Toast.LENGTH_SHORT).show();
                }
            });*/
            throw new IllegalArgumentException("Non-processable picture");
        }
    }

    public ArrayList<Mat> getImbox(){ return this.imBox;}

}
