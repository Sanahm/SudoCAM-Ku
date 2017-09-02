package com.example.mohamed.sudoku;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Size;
import org.opencv.core.RotatedRect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Created by Mohamed on 15/05/2017.
 */
public class Thresholding {
	private static Mat grid;
	private static Mat approx;
	private static RotatedRect rect;
	
	public static Mat getGrid(){
		return Thresholding.grid;
	}
	
	public static Mat getApprox(){
		return Thresholding.approx;
	}
	
	public static RotatedRect getRect(){
		return Thresholding.rect;
	}
	
    public static Mat adaptativeThresholding(Mat img){
        Mat im = new Mat();
        Imgproc.medianBlur(img,im,5);
        Imgproc.adaptiveThreshold(im,img,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,21,2);
        return img;
    }

    public static Mat normalThresholding(Mat img){
        Mat im = new Mat();
        Imgproc.threshold(img,im,120,255,Imgproc.THRESH_TRUNC);
        Imgproc.threshold(im,img,0,255,Imgproc.THRESH_OTSU+Imgproc.THRESH_BINARY);
        return img;
    }
    public static Mat orderPoints(Mat pts){
        Mat rect = Mat.zeros(4,2,CvType.CV_32F);
        Mat s = Mat.zeros(1,4,CvType.CV_32F);
        Mat diff = Mat.zeros(1,4,CvType.CV_32F);
        for(int i = 0; i < s.width(); i++){
            s.put(0,i,pts.get(i,0)[1] + pts.get(i,0)[0]);
        }
        double min = pts.get(0,0)[1] + pts.get(0,0)[0];
		double max = pts.get(0,0)[1] + pts.get(0,0)[0];
        int id_min = 0, id_max = 0;
        for(int i = 0; i < s.width(); i++){
			double val = pts.get(i,0)[1] + pts.get(i,0)[0];
            if(val <= min){
                min = val;
                id_min = i;
            }
            if(val > max){
                max = val;
                id_max = i;
            }
        }
		
		rect.put(1,0,pts.get(id_min,0)[0]);
		rect.put(1,1,pts.get(id_min,0)[1]);
		rect.put(3,0,pts.get(id_max,0)[0]);
		rect.put(3,1,pts.get(id_max,0)[1]);

        for(int i = 0; i < diff.width(); i++){
            diff.put(0,i,pts.get(i,0)[1] - pts.get(i,0)[0]);
        }
        min = pts.get(0,0)[1] - pts.get(0,0)[0];
        max = pts.get(0,0)[1] - pts.get(0,0)[0];
		id_min = 0; id_max = 0;
        for(int i = 0; i < diff.width(); i++){
			double val = pts.get(i,0)[1] - pts.get(i,0)[0];
            if(val <= min){
                min = val;
                id_min = i;
            }
            if(val > max){
                max = val;
                id_max = i;
            }
        }

		rect.put(2,0,pts.get(id_min,0)[0]);
		rect.put(2,1,pts.get(id_min,0)[1]);
		rect.put(0,0,pts.get(id_max,0)[0]);
		rect.put(0,1,pts.get(id_max,0)[1]);
        return rect;
    }
	
	public static void gridDetection(Mat img){
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(img,contours,new Mat(),Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = 0;
        MatOfPoint max_contour = new MatOfPoint();

        Iterator<MatOfPoint> iterator = contours.iterator();
        while (iterator.hasNext()){
            MatOfPoint contour = iterator.next();
            double area = Imgproc.contourArea(contour);
            if(area > maxArea){
                maxArea = area;
                max_contour = contour;
            }
        }

        double epsilon = 0.1*Imgproc.arcLength(new MatOfPoint2f(max_contour.toArray()),true);
        MatOfPoint2f approx = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(max_contour.toArray()),approx,epsilon,true);
		
		RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(max_contour.toArray()));
		
		Mat grid = Thresholding.orderPoints(approx);
		
		Thresholding.approx = approx;
		Thresholding.grid = grid;
		Thresholding.rect = rect;
	}
	
	public static Mat InvertImageColor(Mat img){
		Mat im = new Mat();;
		Core.bitwise_not(normalThresholding(img),im);
	    Core.multiply(im,new Scalar(255),im);
		return im;
	}
	public static Mat normalProcess(Mat img){
		Mat threshImg = Thresholding.InvertImageColor(img);
		Thresholding.gridDetection(threshImg);
		Mat mat = Mat.zeros(4,2,CvType.CV_32F);
		mat.put(0,0,0); mat.put(0,1,512);
		mat.put(1,0,0); mat.put(1,1,0);
		mat.put(2,0,512); mat.put(2,1,0);
		mat.put(3,0,512); mat.put(3,1,512);
		
		mat = Imgproc.getPerspectiveTransform(Thresholding.grid,mat);
		
		Mat M = new Mat();
		
		Imgproc.warpPerspective(threshImg,M,mat, new Size(512,512));
		return Thresholding.InvertImageColor(M);
	}
	

	public static Mat adaptativeProcess(Mat img){
		Mat im = new Mat();
		Imgproc.threshold(img,im,120,255,Imgproc.THRESH_TRUNC);
		im = Thresholding.adaptativeThresholding(im);
		Imgproc.medianBlur(im,im,7);
		Mat threshImg = Thresholding.InvertImageColor(im);
		Thresholding.gridDetection(threshImg);
		
 		Mat mat = Mat.zeros(4,2,CvType.CV_32F);
		mat.put(0,0,0); mat.put(0,1,512);
		mat.put(1,0,0); mat.put(1,1,0);
		mat.put(2,0,512); mat.put(2,1,0);
		mat.put(3,0,512); mat.put(3,1,512);
		
		mat = Imgproc.getPerspectiveTransform(Thresholding.grid,mat);
		
		Mat M = new Mat();
		
		Imgproc.warpPerspective(threshImg,M,mat, new Size(512,512));
		
		Imgproc.medianBlur(M,M,3);
		Imgproc.threshold(M,M,254,255,Imgproc.THRESH_BINARY);
		
		return Thresholding.InvertImageColor(M);
	}
	
	public static Mat threshold(Mat img, int method){
		if(method == 1)
			return normalProcess(img);
		else
			return adaptativeProcess(img);
	}

	/*public static Boolean isGridOk(Mat img){
		
	}*/
	
}
