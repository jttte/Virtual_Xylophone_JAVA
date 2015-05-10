package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class Util {
    public static Boolean process_queue(Vector<Integer> myqueue) {
        if ( myqueue.size() < 8 )
            return false;
        if ( myqueue.size() > 15 )
            myqueue.remove(myqueue.remove(0));
        
        int partition = myqueue.size()/3;
        int part1 = 0;
        int part2 = 0;
        int part3 = 0;
        for (int i = 0; i<partition; i++)
            part1 += myqueue.elementAt(i);
        for (int i = partition; i< 2 * partition; i++)
            part2 += myqueue.elementAt(i);
        for (int i = 2 * partition; i < myqueue.size(); i++)
            part3 += myqueue.elementAt(i);
        if(part3 > part2 && part1 > part2) {
            return true;
        }
        return false;
    }
    
    public static Mat processMask(Mat mask, int W, int H) {
        Highgui.imwrite("before.jpg",mask);
        for (int row=0;row<H*2/5;row++) {
            for (int col=0;col<W;col++) {
                int zero[] = new int[1];
                zero[0] = 0;
                mask.put(row, col, 0);
            }
        }
        
        // remove small regions in mask
        Imgproc.erode(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)), new Point(-1,-1), 5);
        Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)), new Point(-1,-1), 5);
        
        // remove small holes in mask
        Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)), new Point(-1,-1), 5);
        Imgproc.erode(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)), new Point(-1,-1), 5);
        
        Highgui.imwrite("after.jpg",mask);
        return mask;
    }
    
    
    public static Mat redFilter(Mat src) {
        Mat upperRed = new Mat();
        Mat lowerRed = new Mat();
        Mat redImage = new Mat();
        Core.inRange(src, new Scalar(170,100,120), new Scalar(255,255,255), upperRed);
        Core.inRange(src, new Scalar(0,100,120), new Scalar(10,255,255), lowerRed);
        Core.bitwise_or(upperRed, lowerRed, redImage);

        redImage = processMask(redImage,src.cols(),src.rows());
        
        return redImage;
    }
    public static Mat orangeFilter(Mat src) {
        Mat orangeImage = new Mat();
        Core.inRange(src, new Scalar(10,140,130), new Scalar(20,255,255), orangeImage);
        
        orangeImage = processMask(orangeImage,src.cols(),src.rows());
        
        return orangeImage;
    }
    public static Mat yellowFilter(Mat src) {
        Mat yellowImage = new Mat();
        Core.inRange(src, new Scalar(30,50,100), new Scalar(30,255,255), yellowImage);
        //Core.inRange(src, new Scalar(0,0,0), new Scalar(255,255,255), yellowImage);
        
        yellowImage = processMask(yellowImage,src.cols(),src.rows());
        
        return yellowImage;
    }
    public static Mat greenFilter(Mat src) {
        Mat greenImage = new Mat();
        Core.inRange(src, new Scalar(35,50,50), new Scalar(75,255,255), greenImage);
        
        greenImage = processMask(greenImage,src.cols(),src.rows());
        
        return greenImage;
    }
    public static Mat blueFilter(Mat src) {
        Mat blueImage = new Mat();
        Core.inRange(src, new Scalar(85,30,50), new Scalar(130,255,255), blueImage);
        
        blueImage = processMask(blueImage,src.cols(),src.rows());
        
        return blueImage;
    }
    
    
    
    public static int[][] getProperty(Mat mask) {
        int[][] vertex = new int[4][2];
        
        //Size WH = mask.size();
        Mat edges = new Mat();
        int minThreshold = 2;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        
        // find the polygon inside the mask
        Imgproc.Canny(mask, edges, minThreshold, minThreshold*3);
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        //List<MatOfPoint2f> contours_poly = new ArrayList<MatOfPoint2f>(contours.size());
        //System.out.println(contours.size());
        
        // find the four corners and store them in vertex
        int UL=5000,UR=-5000,DL=5000,DR=-5000;
        int sumXY, diffXY;
        for (int i=0;i<contours.size();i++) {
            MatOfPoint2f temp_contour = new MatOfPoint2f(contours.get(i).toArray());
            //MatOfPoint2f temp_poly = contours_poly.get(i);
            MatOfPoint2f temp_poly = new MatOfPoint2f();
            Imgproc.approxPolyDP(temp_contour, temp_poly, 3, false);
            Point[] poly = temp_poly.toArray();
            for (int j=0;j<poly.length;j++) {
                sumXY = (int)poly[j].x + (int)poly[j].y;
                diffXY = (int)poly[j].x - (int)poly[j].y;
                if(sumXY<UL){
                    UL = sumXY;
                    vertex[0][0] = (int)poly[j].x;
                    vertex[0][1] = (int)poly[j].y;
                }
                if(sumXY>DR){
                    DR = sumXY;
                    vertex[3][0] = (int)poly[j].x;
                    vertex[3][1] = (int)poly[j].y;
                }
                if(diffXY>UR){
                    UR = diffXY;
                    vertex[1][0] = (int)poly[j].x;
                    vertex[1][1] = (int)poly[j].y;
                }
                if(diffXY<DL){
                    DL = diffXY;
                    vertex[2][0] = (int)poly[j].x;
                    vertex[2][1] = (int)poly[j].y;
                }
                //System.out.println(sumXY+","+diffXY);
            }
        }
        
        return vertex;
    }


}
