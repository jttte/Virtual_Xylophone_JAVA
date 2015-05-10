package main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
//import java.applet.*;
//import java.net.*;

public class Camera {
	
	private static int i = 0;
	private static int W = 1280;
	private static int H = 720;

	public static void main (String args[]) throws InterruptedException{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

		System.out.println("Hello, OpenCV");

		VideoCapture camera = new VideoCapture(0);
		
		if(!camera.isOpened()){
			System.out.println("Camera Error");
		}
		else{
			System.out.println("Camera OK");
		}

		Mat frame = new Mat();
		Mat hsv_frame = new Mat();
		Mat redMask = new Mat();
		Mat orangeMask = new Mat();
		Mat yellowMask = new Mat();
		Mat greenMask = new Mat();
		Mat blueMask = new Mat();
		int[][] redVertex = new int[4][2];
		int[][] orangeVertex = new int[4][2];
		int[][] yellowVertex = new int[4][2];
		int[][] greenVertex = new int[4][2];
		int[][] blueVertex = new int[4][2];
		
		/* For imshow
		BufferedImage image = null;
		byte[] byteArray;
		*/

		if(camera.isOpened()){
			Thread.sleep(500);
			while(i<1){
				camera.read(frame);
				//System.out.println(frame.height() + frame.width());

				i++;
				Highgui.imwrite("camera"+i+".jpg", frame);
				
				Imgproc.cvtColor(frame, hsv_frame, Imgproc.COLOR_BGR2HSV);
				redMask = Util.redFilter(hsv_frame);
				orangeMask = Util.orangeFilter(hsv_frame);
				yellowMask = Util.yellowFilter(hsv_frame);
				greenMask = Util.greenFilter(hsv_frame);
				blueMask = Util.blueFilter(hsv_frame);
				
				Highgui.imwrite("camera"+i+"_red.jpg",redMask);
				Highgui.imwrite("camera"+i+"_orange.jpg",orangeMask);
				Highgui.imwrite("camera"+i+"_yellow.jpg",yellowMask);
				Highgui.imwrite("camera"+i+"_green.jpg",greenMask);
				Highgui.imwrite("camera"+i+"_blue.jpg",blueMask);
				
				redVertex = Util.getProperty(redMask);
				orangeVertex = Util.getProperty(orangeMask);
				yellowVertex = Util.getProperty(yellowMask);
				greenVertex = Util.getProperty(greenMask);
				blueVertex = Util.getProperty(blueMask);
				
				double[] center = hsv_frame.get(H/2, W/2);
				System.out.println(center[0]+", "+center[1]+", "+center[2]);
				

				
				Thread.sleep(100);
			}
		}
	}

}
