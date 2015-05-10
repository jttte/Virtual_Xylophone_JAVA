package main;

import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;



public class MainFunction {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        int match_method = 3;
        //load in template
        Stick stick1 = new Stick("./res/test_case2/template.jpg");
        Vector<Integer> myqueue = new Vector<Integer>();
//        PlaySound sound = new PlaySound();
        
        //offline: use first frame to detect color area
        Mat hsv_frame = new Mat();
        Mat redMask = new Mat();
        Mat orangeMask = new Mat();
        Mat yellowMask = new Mat();
        Mat greenMask = new Mat();
        Mat blueMask = new Mat();
        Mat frame = Highgui.imread("./res/test_case2/0.jpg");
        Imgproc.cvtColor(frame, hsv_frame, Imgproc.COLOR_BGR2HSV);
        redMask = Util.redFilter(hsv_frame);
        orangeMask = Util.orangeFilter(hsv_frame);
        yellowMask = Util.yellowFilter(hsv_frame);
        greenMask = Util.greenFilter(hsv_frame);
        blueMask = Util.blueFilter(hsv_frame);

        
        for (int i = 1; i<150; i++)
        {
            //load in new frame
            Mat img = Highgui.imread("./res/test_case2/"+Integer.toString(i)+".jpg");
            Point coor = stick1.match(i,img, match_method);
            //only push back correct detection
            if(coor.y != 0)
                myqueue.add((int)coor.y);
            if(Util.process_queue(myqueue)) {
//                int partition = 
                myqueue.subList(0, myqueue.size()*2/3).clear();
                System.out.println("hit!");
//                go through masks
//                if(redMask.get(coor.y, coor.x)>0) {
//                    sound.play(0);
//                    System.out.println("red");
//                }
//                else if(orangeMask.get(coor.y, coor.x)>0) {
//                    sound.play(1);
//                    System.out.println("orange");
//                }
//                else if(yellowMask.get(coor.y, coor.x)>0) {
//                    sound.play(2);
//                    System.out.println("yellow");
//                }
//                else if(greenMask.get(coor.y, coor.x)>0) {
//                    sound.play(3);
//                    System.out.println("green");
//                }
//                else if(blueMask.get(coor.y, coor.x)>0) {
//                    sound.play(4);
//                    System.out.println("blue");
//                }
            
            }
        }

    }
}

