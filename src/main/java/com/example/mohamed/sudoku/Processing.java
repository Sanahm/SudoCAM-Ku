package com.example.mohamed.sudoku;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
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
 * @author M. Sana
 */
public class Processing{
	private static Scalar numberColor = new Scalar(0,0,255); //default value

	/**
	* Method for image binarization
	* @parm img image to process
	* @parm flag precize which method to 
	* @return imB binarized image
	* @see org.opencv.imgproc.Imgproc #threshold(Mat,int)
	*/
	public static Mat binarize(Mat img, int flag){
		Mat imB = new Mat();
		if(flag == Imgproc.THRESH_BINARY)
			Imgproc.threshold(img,imB,0,255,Imgproc.THRESH_BINARY);
		if(flag == Imgproc.THRESH_OTSU)
			Imgproc.threshold(img,imB,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		if(flag == Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C)
			Imgproc.adaptiveThreshold(img,imB,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,7,2);
		if(flag == Imgproc.ADAPTIVE_THRESH_MEAN_C)
			Imgproc.adaptiveThreshold(img,imB,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,7,2);
		return imB;
	}
	
	/**
	* This is a method to sort the stat Mat return by opencv function connectedComponentsWithStats
	* Recall that connectedComponentsWithStats find all connected pixels in the image so you have to sort to let only those which are sudoku boxes
	* you have to sort to arrange detected boxes in image to maintain order the grid
	* @parm stats Mat of none x 5
	* @parm axes sort according to specified axes
	*/
	public static Mat statSorted(Mat stats,int axes){
		assert(axes == 0 || axes == 1);
		for(int k = 0; k < 9; k++){
			int j = 0;
			for(int i = 9*k; i < 9 + 9*k; i++){
				j = i - 1;
				while(j >= 9*k){
					if(stats.get(j,axes)[0] > stats.get(j+1,axes)[0]){
						double v1 = stats.get(j,0)[0], v2 = stats.get(j,1)[0], v3 = stats.get(j,2)[0],
						v4 = stats.get(j,3)[0], v5 = stats.get(j,4)[0];
						
						stats.put(j,0,stats.get(j+1,0)[0]);
						stats.put(j,1,stats.get(j+1,1)[0]);
						stats.put(j,2,stats.get(j+1,2)[0]);
						stats.put(j,3,stats.get(j+1,3)[0]);
						stats.put(j,4,stats.get(j+1,4)[0]);
						
						stats.put(j+1,0,v1);
						stats.put(j+1,1,v2);
						stats.put(j+1,2,v3);
						stats.put(j+1,3,v4);
						stats.put(j+1,4,v5);					
					}
					j -=1;
				}
				
			}
		}
		
		return stats;
	
	}
	
	
	
	/**
	*/
	public static Mat ExtractBoxes(Mat im, Mat enhance, int connectiviy){
		try{
			Mat stats = new Mat();
			Mat imB = Processing.binarize(im,Imgproc.THRESH_OTSU);
			Imgproc.connectedComponentsWithStats(imB.mul(enhance),new Mat(),stats,new Mat(),connectiviy,CvType.CV_32S);
			
			Mat stat = Mat.zeros(81,5,CvType.CV_32F);
			double max_area = stats.get(0,2)[0] * stats.get(0,3)[0];
			int j = 0;
			for(int i = 1; i < stats.height(); i++){
				double area = stats.get(i,2)[0] * stats.get(i,3)[0];
				if(area < max_area/70 && area > max_area/120){
					stat.put(j,0,stats.get(i,0)[0]);
					stat.put(j,1,stats.get(i,1)[0]);
					stat.put(j,2,stats.get(i,2)[0]);
					stat.put(j,3,stats.get(i,3)[0]);
					stat.put(j,4,stats.get(i,4)[0]);
					j +=1;
						
				}
				
			}
			stat = Processing.statSorted(stat,0);
			if(stat.get(0,0)[0] == 0 && stat.get(0,1)[0] ==0 && (stat.get(0,2)[0] ==0 || stat.get(0,3)[0] ==0))
				return null;
			return stat;
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static Mat ExtractBoxes(Mat im, int connectiviy){
		try{
			Mat stats = new Mat();
			Mat imB = Processing.binarize(im,Imgproc.THRESH_OTSU);
			Imgproc.connectedComponentsWithStats(imB,new Mat(),stats,new Mat(),connectiviy,CvType.CV_32S);
			
			Mat stat = Mat.zeros(81,5,CvType.CV_32F);
			double mcw = stats.get(0,2)[0]; //max contour width
			double mch = stats.get(0,3)[0]; // max contour height
			int j=0;
			for(int i = 1; i < stats.height(); i++){
				double area = stats.get(i,2)[0] * stats.get(i,3)[0];
				if(area < (mcw/8)*(mch/8) && area > (mcw/25)*(mch/25)){
					stat.put(j,0,stats.get(i,0)[0]);
					stat.put(j,1,stats.get(i,1)[0]);
					stat.put(j,2,stats.get(i,2)[0]);
					stat.put(j,3,stats.get(i,3)[0]);
					stat.put(j,4,stats.get(i,4)[0]);
					j +=1;
						
				}
				
			}
			stat = Processing.statSorted(stat,0);
			/*if(stat.get(0,0)[0] == 0 && stat.get(0,1)[0] ==0 && (stat.get(0,2)[0] ==0 || stat.get(0,3)[0] ==0))
				return null;*/
			return stat;
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static ArrayList<Mat> ExtractImageBoxes(Mat boxes, Mat img, int method){
		ArrayList<Mat> imBox = new ArrayList<Mat>();
		for(int i = 0; i < boxes.height(); i++){
			Mat imR = img.submat((int)boxes.get(i,0)[0]+2,(int)boxes.get(i,0)[0] + (int)boxes.get(i,2)[0]-4,
								 (int)boxes.get(i,1)[0]+2,(int)boxes.get(i,1)[0] + (int)boxes.get(i,3)[0]-4);
								 
								 
			Imgproc.resize(imR,imR,new Size(28,28),0,0,method);
			imR = Processing.binarize(imR,Imgproc.THRESH_BINARY);
	
			imBox.add(imR.reshape(0,1));
		}
		
		return imBox;
	}
	
	public static void drawNumbers(Mat img, int[][] emptyGrid, int[][] filledGrid, Mat positions, Scalar color){
		if(positions.height() == 81) {
			double fontscale = positions.get(0, 3)[0] / 20;
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (emptyGrid[i][j] == 0) {
						//if the box is empty then ...
						String k = "" + filledGrid[i][j]; //the value of this box in te filled grid
						//the position of each box is localized on "positions" by i*9+j
						double x = positions.get(i * 9 + j, 0)[0] + positions.get(i * 9 + j, 2)[0] / 3;
						double y = positions.get(i * 9 + j, 1)[0] + 3 * positions.get(i * 9 + j, 3)[0] / 4;
						Point pt = new Point(x, y);
						Imgproc.putText(img, k, pt, Core.FONT_HERSHEY_PLAIN, fontscale, color, 2);
					}
				}
			}
		}
	}

	public static void setNumberColor(int id){
		switch (id){
			case 0:
				Processing.numberColor = new Scalar(255,0,0);
				break;
			case 1:
				Processing.numberColor = new Scalar(0,255,0);
				break;
			case 2:
				Processing.numberColor = new Scalar(0,0,255);
				break;
		}
	}

	public static Scalar getNumberColor(){
		return Processing.numberColor;
	}
}