package com.example.mohamed.sudoku;

import android.content.res.AssetManager;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by Mohamed on 20/05/2017.
 */
public class RunModel {
    private static boolean logStat = false;
    private static int[][] output = new int[9][9];
    private static float[] layer;

    public static int[][] getGrid(){
        return RunModel.output;
    }

    public static  void runModel(AssetManager assetManager, String inputName, String[] outputNames, String modelFileName, ArrayList<Mat> imBox){
        TensorFlowInferenceInterface inferenceInterface = new TensorFlowInferenceInterface(assetManager,modelFileName);
        //final Operation operation = inferenceInterface.graphOperation(outputNames[0]);
        //final int numClasses = (int) operation.output(0).shape().size(1);
        //Mat mat = imBox.reshape(0,1);
        int l = imBox.get(0).width();
        int h = imBox.size();
        float[] FloatValues = new float[h*l];
        for(int i = 0; i < h; i++) {
            Mat mat = imBox.get(i);
            for (int j = 0; j < l; j++) {
                FloatValues[i * l + j] = (float) (255 - mat.get(0, j)[0]) / 255;
            }
        }
        inferenceInterface.feed(inputName,FloatValues,l,h);
        
        inferenceInterface.run(outputNames,logStat);
        int numClasses = 10;
        int[]output = new int[h];
        float[] layer = new float[h*numClasses];
        inferenceInterface.fetch(outputNames[0],output);
        //inferenceInterface.fetch(outputNames[1],layer);
        //inferenceInterface.fetch(outputNames[0],output[1]);
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                RunModel.output[j][i] = output[i*9+j];
            }
        }

        RunModel.layer = layer;
    }

    public static String showGrid(){
        String s="";
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                s +=RunModel.output[i][j]+"\t";
            }
            s +="\n";
        }
        return s;
    }
}
