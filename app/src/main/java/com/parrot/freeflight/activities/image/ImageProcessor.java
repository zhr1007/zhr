package com.parrot.freeflight.activities.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import com.parrot.freeflight.ui.hud.Image;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;


//import java.lang.Object;

/**
 * Created by shisy13 on 16/8/22.
 */
public class ImageProcessor {

    static final String LOG_TAG = ImageProcessor.class.getSimpleName();

    static Bitmap processImage(Bitmap image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
        Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, false);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        //HSV filter
//        mat = hsvFilter(mat);

//        mat = findCircles(mat);
        mat = findLines(mat);
        Utils.matToBitmap(mat, bitmap);

        PointF[] centers = centroid(bitmap);
//
//        Log.d(LOG_TAG,"center:" + centers[0].x + "," + centers[0].y + ";" + centers[1].x + "," + centers[1].y);

        return bitmap;
    }

    /**
     *
     * @param bmp  黑白两色图
     * @return
     */

    static Mat  findCircles(Mat bmp){
        Log.e("注意！","开始识别圆！");
        Mat circles = new Mat();

        Mat blackwhite = hsvFilter(bmp);
        Imgproc.HoughCircles(blackwhite, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100); //hough变换找圆

        //Imgproc.cvtColor(bmpGray,bmpMat,Imgproc.COLOR_GRAY2RGBA,4);
        Log.e("霍夫圆检测", "共检测出 " +  circles.cols()+"个圆"+circles.rows());

        for (int i=0; i < circles.cols();i++)
        {
            double circle[] = circles.get(0,i);
            if (circle==null)
                break;
            Point pt = new Point(Math.round(circle[0]),Math.round(circle[1]));
            int radius=(int)Math.round(circle[2]);

            Imgproc.circle(bmp,pt,radius,new Scalar(255,0,255,0), 8);
            Log.e("哈哈","已经画圆");
        }
       // bmp.recycle();
        return bmp;
    }


    /**
     * 路径在图像中一般呈平行四边形，计算形心的位置
     * 目的是：根据形心与图像中心的差，动态调整四旋翼的路径
     * 输入：经过处理后的二色图像（红与黑）
     * 以图形中心为坐标中心，x轴向右，y轴向上，各自范围[-1,1]
     * 返回路径中心的坐标，[-1,1]之间
     * 若返回[-2,-2]，表示图像中白点少于100个，摄像机未拍到路径
     * 上下两部分
     */

    static public PointF[] centroid(Bitmap bmp) {
        //  PointF pointF = new PointF();
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int pixColor = 0; //像素信息
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int whiteUpNum = 0; //黑白图中，白点的总个数
        int whiteDownNum = 0; //黑白图中，白点的总个数
        PointF[] pointFs = new PointF[2];
        pointFs[0] = new PointF();
        pointFs[1] = new PointF();

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);   //读取像素信息
        double centerXup = 0;  //形心的x坐标
        double centerXdown = 0;  //形心的x坐标
        double centerYup = 0; //形心的y坐标
        double centerYdown = 0; //形心的y坐标
        int halfHeight = height / 2;


        for (int i = 0; i < halfHeight; i++) {
            for (int j = 0; j < width; j++) {

                pixColor = pixels[i * width + j];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);

                //如果红色通道大于0，则为红色，则累加centerx，centery,
                //否则为黑色，不累加
                if (pixR == 255 && pixG == 255 && pixB == 255) {
                    whiteUpNum = whiteUpNum + 1;
                    centerXup += j;
                    centerYup += (height - i);
                }

            }
        }

        centerXup = 2 * centerXup / whiteUpNum / width - 1;
        centerYup = 2 * centerYup / whiteUpNum / height - 1;
        pointFs[0].x = (float) centerXup;
        pointFs[0].y = (float) centerYup;
        Log.d(LOG_TAG, "whiteUpNum" + whiteUpNum);
        if (whiteUpNum < 1000) {
            pointFs[0].x = (float) -2.0;
            pointFs[0].y = (float) -2.0;

        }

        for (int i = halfHeight; i < height; i++) {
            for (int j = 0; j < width; j++) {

                pixColor = pixels[i * width + j];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);

                //如果红色通道大于0，则为红色，则累加centerx，centery,
                //否则为黑色，不累加
                if (pixR == 255 && pixG == 255 && pixB == 255) {
                    whiteDownNum = whiteDownNum + 1;
                    centerXdown += j;
                    centerYdown += (height - i);
                }

            }
        }
        centerXdown = 2 * centerXdown / whiteDownNum / width - 1;
        centerYdown = 2 * centerYdown / whiteDownNum / height - 1;
        pointFs[1].x = (float) centerXdown;
        pointFs[1].y = (float) centerYdown;

        Log.d(LOG_TAG, "whiteDownNum" + whiteDownNum);
        if (whiteDownNum < 1000) {
            pointFs[1].x = (float) -2.0;
            pointFs[1].y = (float) -2.0;

        }

        return pointFs;
    }



    /**
     * @param origin
     * @return
     */
    static public Mat hsvFilter(Mat origin) {

        Mat originHSV = new Mat();
        Imgproc.cvtColor(origin, originHSV, Imgproc.COLOR_BGR2HSV, 3);

        Mat lower = new Mat();
        Mat upper = new Mat();
//        Core.inRange(originHSV, new Scalar(0, 80, 50), new Scalar(50, 255, 255), lower);
        Core.inRange(originHSV, new Scalar(0, 80, 70), new Scalar(0, 255, 255), lower);
//        Core.inRange(originHSV, new Scalar(120, 80, 50), new Scalar(179, 255, 255), upper);
        Core.inRange(originHSV, new Scalar(100, 80, 70), new Scalar(130, 255, 255), upper);

        Mat red = new Mat();
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, red);
        Imgproc.GaussianBlur(red, red, new Size(9, 9), 2, 2);
        return red;
    }


    static public Mat findLines(Mat bmp){
        Mat blackwhite = hsvFilter(bmp);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(blackwhite, lines,  1, Math.PI/180, 50, 200, 200);
        Point start;
        Point end;
        for (int x = 0; x < lines.cols(); x++)
        {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            start = new Point(x1, y1);
            end = new Point(x2, y2);
            Imgproc.line(bmp, start, end, new Scalar(0,255,0), 3);
        }
        return bmp;
    }

    static public Point[] findLinesP(Mat bmp){
        Mat blackwhite = hsvFilter(bmp);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(blackwhite, lines,  1, Math.PI/180, 50, 300, 200);
        Point start;
        Point end;
        double length = 0.0;
        Point[] points = new Point[2];
        if (lines.cols() == 0){
            return null;
        }
        for (int x = 0; x < lines.cols(); x++)
        {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            start = new Point(x1, y1);
            end = new Point(x2, y2);
            double tmpl = Math.pow(start.x-end.x, 2) + Math.pow(start.y-end.y, 2);
            if (tmpl > length){
                points[0] = start;
                points[1] = end;
                length = tmpl;
            }
        }
        if (points[0].y < points[1].y){
            double tmp = points[0].y;
            points[0].y = points[1].y;
            points[1].y = tmp;
            tmp = points[0].x;
            points[0].x = points[1].x;
            points[1].x = tmp;
        }
        return points;
    }
}
