package com.example.mohamed.sudoku;

import android.content.res.AssetManager;

import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by Mohamed on 26/06/2017.
 */
public class RunModelThread extends Thread{
    private String inputName;
    private String[] outputNames;
    private String modelFileName;
    private ArrayList<Mat> imBox;
    private AssetManager asset;

    public RunModelThread(AssetManager asset, String inputName, String[] outputNames, String modelFileName, ArrayList<Mat> imBox){
        this.inputName = inputName;
        this.outputNames = outputNames;
        this.imBox = imBox;
        this.modelFileName = modelFileName;
        this.asset = asset;
    }

    public void run(){
        if(this.imBox != null)
            RunModel.runModel(asset,this.inputName,this.outputNames,this.modelFileName,this.imBox);
    }
    public int[][] getGrid(){return RunModel.getGrid();}
}
