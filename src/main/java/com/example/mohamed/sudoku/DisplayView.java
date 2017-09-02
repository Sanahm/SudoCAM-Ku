package com.example.mohamed.sudoku;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mohamed on 24/06/2017.
 */
public class DisplayView extends AppCompatActivity {
    private Bitmap bitmap = null;
    ImageView imgView = null;
    Button process = null;
    Button save = null;
    static{ System.loadLibrary("opencv_java3"); }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_view);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Uri imageUri = intent.getParcelableExtra("image-uri"); //Uri.parse(intent.getStringExtra("image-uri"));
        try {
            this.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imgView = (ImageView)findViewById(R.id.imgView);
            if(this.bitmap!=null)
                imgView.setImageBitmap(this.bitmap);

            ImageButton process = (ImageButton) findViewById(R.id.process);
            if(process != null) {
                process.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DisplayView.this, "Wait for processing", Toast.LENGTH_SHORT).show();
                        Mat im = new Mat(), img = new Mat();
                        Utils.bitmapToMat(bitmap, im);
                        Imgproc.cvtColor(im, img, Imgproc.COLOR_RGB2GRAY);
                        Mat enhance = new Mat();
                        try {
                            enhance = Utils.loadResource(DisplayView.this, R.drawable.grid, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                        } catch (Exception e) {
                            throw new IllegalArgumentException("Unable to load picture");
                        }
                        try {
                            BoxesExtractionThread t2 = new BoxesExtractionThread(DisplayView.this, img, enhance);

                            t2.run();

                            ArrayList<Mat> imBox = t2.getImbox();

                            String modelFileName = "file:///android_asset/optimized_tensor_v12.pb";
                            String[] outputNames = {"output", "Relu_3"};


                            RunModelThread t3 = new RunModelThread(getAssets(), "x", outputNames, modelFileName, imBox);
                            t3.run();

                            int[][] emptySud = t3.getGrid();

                            SudokuSolver sudo = new SudokuSolver(emptySud);

                            if (!sudo.solve()) {
                                Toast.makeText(DisplayView.this, "Can't solve the grid! your grid is perhaps either wrong or the image detection failed", Toast.LENGTH_LONG).show();
                            } else {
                                int[][] filledSud = sudo.getGrid();

                                Mat stat = Processing.ExtractBoxes(img, 8);
                                Processing.drawNumbers(im, emptySud, filledSud, stat, Processing.getNumberColor());
                                Utils.matToBitmap(im, bitmap);
                                if (bitmap != null)
                                    imgView.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            Toast.makeText(DisplayView.this, "The picture took is not processable", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            ImageButton save = (ImageButton) findViewById(R.id.save);
            if(save != null){
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Mat img = new Mat();
                        Utils.bitmapToMat(bitmap,img);
                        File path = Environment.getExternalStorageDirectory();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String currentDateandTime = sdf.format(new Date());

                        String fileName = "grid_"+currentDateandTime+".jpg";
                        File file = new File(path,fileName);
                        Imgcodecs.imwrite(file.getAbsolutePath(),img);
                        Toast.makeText(DisplayView.this,"The picture"+fileName+ "was saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch (Exception e){
            Toast.makeText(DisplayView.this,"Unable to load the picture", Toast.LENGTH_LONG).show();
        }
        //Bitmap bmp = (Bitmap) intent.getParcelableExtra("image-uri");

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void init(){
        int[][] filledSud = new int[9][9];
        int[][] emptySud = new int[9][9];
    }
}
