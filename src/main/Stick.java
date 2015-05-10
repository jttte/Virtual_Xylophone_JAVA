package main;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Stick {
        Point bound_left_up_;
        Point bound_right_buttom_;
        Point match_point_;
        Point last_detect_point_;
        int padding;
        Mat templ;
    
        public Stick(String path){
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            //initialize Points
            last_detect_point_ = new Point(0,0);
            bound_left_up_ = new Point(0,0);
            bound_right_buttom_ = new Point(0,0);
            match_point_ = new Point(0,0);
            
            padding = 60;
            templ = new Mat();
            templ = Highgui.imread(path);
            if (templ == null ) {
                System.out.println("template image not found");
                System.exit(0);
            }
            Highgui.imwrite("test.jpg", templ);
        }
        public Point match (int idx, Mat img, int match_method) {
            /// Source image to display
            int padding_x;
            int padding_y;

            
            //confine the search area
            if (last_detect_point_.x == 0 && last_detect_point_.y == 0) {//first iteration, search lower portion of the image
                bound_left_up_.x = 0;
                bound_left_up_.y = img.rows()*2/5;
                
                bound_right_buttom_.x = img.cols();
                bound_right_buttom_.y = img.rows();
            } else {
                bound_left_up_.x = last_detect_point_.x - padding > 0 ? last_detect_point_.x - padding: 0;
                bound_left_up_.y = last_detect_point_.y - padding > 0 ? last_detect_point_.y - padding: 0;
                bound_right_buttom_.x = bound_left_up_.x + templ.cols() + padding * 2;
                bound_right_buttom_.y = bound_left_up_.y + templ.rows() + padding * 2;
                
                if(bound_right_buttom_.x > img.cols()) bound_right_buttom_.x = img.cols();
                if(bound_right_buttom_.y > img.rows()) bound_right_buttom_.y = img.rows();
                
            }
            padding_x = (int) (bound_right_buttom_.x - bound_left_up_.x) ;
            padding_y = (int) (bound_right_buttom_.y - bound_left_up_.y) ;
            
            ///create image of lower half region
//            Rect region = new Rect(bound_left_up_, bound_right_buttom_);
//            Mat subImg = img.submat(region);
            Mat smallImg = new Mat(img, new Rect(bound_left_up_, bound_right_buttom_));
//            smallImg.copyTo(subImg);
//            Highgui.imwrite("./res/test_case2/"+"result_"+Integer.toString(idx)+"small.jpg", smallImg);
            int result_cols =  smallImg.cols() - templ.cols() + 1;
            int result_rows = smallImg.rows() - templ.rows() + 1;
            
            Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            // / Do the Matching and Normalize
            Imgproc.matchTemplate(smallImg, templ, result, match_method);
            Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

            
         // / Localizing the best match with minMaxLoc
            MinMaxLocResult mmr = Core.minMaxLoc(result);

            if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
                match_point_ = mmr.minLoc;
            } else {
                match_point_ = mmr.maxLoc;
            }
            
            
            // change back to the x, y in original image
//            System.out.println(match_point_.y);
//            System.out.println(bound_left_up_.y);
            match_point_.y += bound_left_up_.y;
            match_point_.x += bound_left_up_.x;
            
            // / Show me what you got
            Core.rectangle(img, match_point_, new Point( match_point_.x + templ.cols() , match_point_.y + templ.rows()), new Scalar(2, 8, 0));
            Core.rectangle(img, bound_left_up_, new Point( bound_left_up_.x + padding_x , bound_left_up_.y + padding_y), new Scalar(0, 255, 0));
//            System.out.println(idx+" "+ match_point_.x+" "+match_point_.y+" ("+bound_left_up_.x+", "+bound_left_up_.y+")");
            System.out.println(idx+" "+match_point_.y);
            // update
            if(last_detect_point_.y !=0 && Math.abs(match_point_.y - last_detect_point_.y)>25) {//wrong detection, expand search space
                last_detect_point_ = new Point(0,0);
//                System.out.println("wrong detection");
            } else if ( match_point_.y == last_detect_point_.y) {
                last_detect_point_ = new Point(0,0);
//                System.out.println("not moving");
            }
            else
                last_detect_point_ = new Point(match_point_.x, match_point_.y);
            
            Highgui.imwrite("./res/test_case2/"+"result_"+Integer.toString(idx)+".jpg", img);
 
            return match_point_;

            
        }

        public void sift_match (int idx) {
        }


}
