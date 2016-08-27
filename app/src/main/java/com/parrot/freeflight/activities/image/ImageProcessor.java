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

        //gaussion blur
        Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, false);
        Mat imgMat = new Mat();
        Mat imgMatBlurred = new Mat();
        Utils.bitmapToMat(bitmap, imgMat);
        Imgproc.GaussianBlur(imgMat, imgMatBlurred, new Size(7, 7), 0, 0);   //
        Utils.matToBitmap(imgMatBlurred, bitmap);
//
//
//        image = showPicRedBlack(image);//


        //HSV filter
        bitmap = hsvFilter(bitmap);
        PointF[] centers = centroid(bitmap);

        PointF center = centers[0];
        Log.d(LOG_TAG, "center:" + center.x + "," + center.y);

        Log.d(LOG_TAG,"center:" + centers[0].x + "," + centers[0].y + ";" + centers[1].x + "," + centers[1].y);

        return bitmap;
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @param bmp
     * @return，
     */
//    static private Bitmap sharpenImageAmeliorate(Bitmap bmp) {
//        long start = System.currentTimeMillis();
//        // 拉普拉斯矩阵
//        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};
//
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//
//        int pixR = 0;
//        int pixG = 0;
//        int pixB = 0;
//
//        int pixColor = 0;
//
//        int newR = 0;
//        int newG = 0;
//        int newB = 0;
//
//        int idx = 0;
//        float alpha = 0.3F;
//        int[] pixels = new int[width * height];
//        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
//        for (int i = 1, length = height - 1; i < length; i++) {
//            for (int k = 1, len = width - 1; k < len; k++) {
//                idx = 0;
//                for (int m = -1; m <= 1; m++) {
//                    for (int n = -1; n <= 1; n++) {
//                        pixColor = pixels[(i + n) * width + k + m];
//                        pixR = Color.red(pixColor);
//                        pixG = Color.green(pixColor);
//                        pixB = Color.blue(pixColor);
//
//                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
//                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
//                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
//                        idx++;
//                    }
//                }
//
//                newR = Math.min(255, Math.max(0, newR));
//                newG = Math.min(255, Math.max(0, newG));
//                newB = Math.min(255, Math.max(0, newB));
//
//                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
//                newR = 0;
//                newG = 0;
//                newB = 0;
//            }
//        }
//
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        long end = System.currentTimeMillis();
//        Log.d("may", "used time=" + (end - start));
//        return bitmap;
//    }

    /**
     * 判断红色像素点，单独显示，其他点显示为黑色
     */

//    static public Bitmap showPicRedBlack(Bitmap bmp) {
//        int pixR = 0;
//        int pixG = 0;
//        int pixB = 0;
//        double disttemp = 0;                         //临时变量，距离值
//        int pixColor = 0;
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        // int[] dist = new int[width * height];    //计算每个像素点与标准红色的欧氏距离
//        int RedDistThreshold = 80;               //若距离大于此阈值，则全设为黑色，否则原样输出
//        int[] pixelR = new int[width * height];  //R通道
//        int[] pixelG = new int[width * height];  //G通道
//        int[] pixelB = new int[width * height];  //B通道
//        int[] pixels = new int[width * height];   //记录每个像素点的rgb值
//        //  Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        bmp.getPixels(pixels, 0, width, 0, 0, width, height);  //提取图像的RGB值
//
//        /**
//         * for循环，依次读取图像的R,G,B 通道
//         * 并计算与标准红色距离
//         *大于阈值，设为黑色
//         */
//
//        for (int i = 0, length = height - 1; i < length; i++) {
//            for (int j = 0, len = width - 1; j < len; j++) {
//
//                pixColor = pixels[i * width + j];
//                pixR = Color.red(pixColor);
//                pixG = Color.green(pixColor);
//                pixB = Color.blue(pixColor);
//                pixelR[i * width + j] = pixR;
//                pixelG[i * width + j] = pixG;
//                pixelB[i * width + j] = pixB;
//                //计算与红色的欧氏距离
//                disttemp = (255 - pixR) * (255 - pixR) + pixG * pixG + pixB * pixB;
//                disttemp = Math.sqrt(disttemp);
//                //与红色距离大的设为黑色
//                if (disttemp > RedDistThreshold) {
//                    pixelR[i * width + j] = 0;
//                    pixelG[i * width + j] = 0;
//                    pixelB[i * width + j] = 0;
//
//
//                }
//
//                //将修改后的三个通道合并
//                pixels[i * width + j] = Color.argb(255, pixelR[i * width + j], pixelG[i * width + j], pixelB[i * width + j]);
//            }
//
//        }
//        //返回修改后的图像
//        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
//
//        return bmp;
//    }

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
        if (whiteUpNum < 20) {
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


        if (whiteDownNum < 20) {
            pointFs[1].x = (float) -2.0;
            pointFs[1].y = (float) -2.0;

        }

        return pointFs;
    }

    /**
     * 利用九宫格矫正图像
     * 当某个黑点周围8个点有5个红点，就把该点改为红色
     * 防止红色板块中出现黑斑
     * 进行二次迭代
     *
     * @param bmp 处理过的红黑图像
     * @return 去除黑色斑点的红黑图像
     */
//    static public Bitmap nineCorrect(Bitmap bmp) {
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        int pixColor = 0; //像素信息
//        //  int pixR=0;
//        int redNum = 0;   //九宫格中
//        int[] pixels = new int[width * height];
//        int[] pixelR = new int[width * height];   //记录每个像素点的R通道值
//        bmp.getPixels(pixels, 0, width, 0, 0, width, height);   //读取像素信息
//
//        //提取每个像素点R通道值，记在pixelR中
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                pixColor = pixels[j * width + i];
//                pixelR[j * width + i] = Color.red(pixColor);
//            }
//        }

//        //矫正黑点
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                redNum = 0; //对每个像素点初始化九宫格的红点计数
//                //只对中心的黑点矫正
//                if (pixelR[j * width + i] == 0) {
//                    //边缘点不进行矫正
//                    if (i != 0 && i != width - 1 && j != 0 && j != height) {
//
//                        //对九宫格进行循环，计数红点的个数
//                        for (int k = -1; k < 2; k++) {
//
//                            for (int m = -1; m < 2; m++) {
//
//                                if (pixelR[(j + m) * width + i + k] == 255) { //红色
//                                    redNum = redNum + 1;
//                                }
//                            }
//
//                        }
//                    }
//
//                }
//
//                if (redNum > 4) {
//                    pixels[j * width + i] = Color.argb(255, 255, 0, 0);  //设为红色
//                }
//
//
//            }
//
//        }
//
//        //重新设置RGB通道
//        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
//
//        return bmp;
//
//    }

    /**
     * @param bmp 需要用九宫格矫正的红黑图像
     * @param n   迭代利用nineCorrect进行矫正的次数
     * @return 矫正后的红黑图
     */
//    static public Bitmap IterNineCorrect(Bitmap bmp, int n) {
//        for (int i = 0; i < n; i++) {
//            bmp = nineCorrect(bmp);
//        }
//        return bmp;
//    }


    /**
     * 图像首先经高斯
     * 滤波，去除杂波
     * 然后
     *
     * @param bmp   摄像头采集的图像
     * @return 处理后的图像
     */
//    public Bitmap imagePreProces(Bitmap bmp)
//    {
//
//    }

    /**
     * @param pointf 来自函数centroid: 路径中心相对于图片中心的位置，x,y均为[-1,1]之间，
     */
//    static public void keepToPath(PointF pointf) {
//        double x = pointf.x;
//        double y = pointf.y;
//        if (x > 0) {
//            //飞行器右移
//        } else if (x < 0) {
//            //飞行器左移
//        }
//        if (y > 0) {
//            //飞行器前进
//        } else if (y < 0) {
//            //飞行器后退
//        }
//    }


    /**
     * @param bitmap
     * @return
     */
    static public Bitmap hsvFilter(Bitmap bitmap) {
        Mat origin = new Mat();
        Utils.bitmapToMat(bitmap, origin);

        Mat originHSV = new Mat();
        Imgproc.cvtColor(origin, originHSV, Imgproc.COLOR_BGR2HSV, 3);

        Mat lower = new Mat();
        Mat upper = new Mat();
        Core.inRange(originHSV, new Scalar(0, 80, 50), new Scalar(50, 255, 255), lower);
        Core.inRange(originHSV, new Scalar(120, 80, 50), new Scalar(179, 255, 255), upper);

        Mat red = new Mat();
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, red);
        Imgproc.GaussianBlur(red, red, new Size(9, 9), 2, 2);

        Utils.matToBitmap(red, bitmap);
        return bitmap;
    }
}
//    static public Bitmap hsvFilter(Bitmap bmp) {
//        Bitmap bitmap = Bitmap.createBitmap(bmp);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        Mat origin = new Mat();
//        Utils.bitmapToMat(bitmap, origin);
//
//        Mat originHSV = new Mat();
//        Imgproc.cvtColor(origin, originHSV, Imgproc.COLOR_BGR2HSV, 3);
//
//        List<Mat> HSV = new ArrayList<Mat>(3);
//        Core.split(origin, HSV);
//        Mat originH = HSV.get(0);
//        Mat originS = HSV.get(1);
//        Mat originV = HSV.get(2);
//
//        Log.d(LOG_TAG, "Mat" + originHSV);
//        System.out.println(originHSV);

//        for (int i = 0; i < originH.height(); i++){
//            for (int j = 0; j < originH.width(); j++){
//                double[] h = originH.get(i, j);
//                System.out.print(h[0]+", ");
//            }
//        }

//        Mat H1 = new Mat();
//        Imgproc.threshold(originH, H1, 25, 90, Imgproc.THRESH_BINARY);
//        Mat H2 = new Mat();
//        Imgproc.threshold(originH, H2, 155, 90, Imgproc.THRESH_BINARY_INV);
//        for (int i = 0; i < H1.height(); i++){
//            for (int j = 0; j < H1.width(); j++){
//                double[] h = H2.get(i, j);
//                if (h[0] == 0)
//                    H1.put(i, j, 0);
//            }
//        }
//
//        Mat S = new Mat();
//        Imgproc.threshold(originS, S, 100, 255, Imgproc.THRESH_BINARY);
//
//        Mat V = new Mat();
//        Imgproc.threshold(originV, V, 100, 255, Imgproc.THRESH_BINARY);
//
//
//
//        for (int i = 0; i < H1.height(); i++){
//            for (int j = 0; j < H1.width(); j++){
//                double[] h = H1.get(i,j);
//                double[] s = S.get(i, j);
//                double[] v = V.get(i, j);
//                if (h[0] == 90 || s[0] == 0 || v[0] == 0){
//                    H1.put(i, j, 90);
//                    S.put(i, j, 0);
//                    V.put(i, j, 0);
//                }
//            }
//        }
//
//        List<Mat> processedHSV = new ArrayList<>();
//        processedHSV.add(H1);
//        processedHSV.add(S);
//        processedHSV.add(V);
//
//        Mat ansMat = new Mat();
//        Core.merge(processedHSV, ansMat);
//        Mat rgbMat = new Mat();
//        Imgproc.cvtColor(ansMat, rgbMat, Imgproc.COLOR_HSV2RGB, 4);
//        Utils.matToBitmap(ansMat, bitmap);
//        return bitmap;
//    }
//
//}
//        int width = bmp.getWidth();
//              int height = bmp.getHeight();
//              int[] pixelR = new int[width * height];  //R通道
//              int[] pixelG = new int[width * height];  //G通道
//              int[] pixelB = new int[width * height];  //B通道
//              float[] hue=new float[width * height];  //HSV. hsv[0] is Hue [0 .. 360)
//              float[] satu=new float[width * height]; //hsv[1] is Saturation [0...1]
//              float[] val=new float[[width * height];  //hsv[2] is Value [0...1]
//
//              int[] pixels = new int[width * height];   //记录每个像素点的rgb值
//              int pixR = 0;
//              int pixG = 0;
//              int pixB = 0;
//              int pixColor = 0;
//              float[] hsv=new float[3];
//              bmp.getPixels(pixels, 0, width, 0, 0, width, height);  //提取图像的RGB值
//
//              //
//              for (int i = 0, length = height - 1; i < length; i++) {
//                  for (int j = 0, len = width - 1; j < len; j++) {
//
//                      pixColor = pixels[i * width + j];
//                      pixR = Color.red(pixColor);
//                      pixG = Color.green(pixColor);
//                      pixB = Color.blue(pixColor);
//                      pixelR[i * width + j] = pixR;
//                      pixelG[i * width + j] = pixG;
//                      pixelB[i * width + j] = pixB;
//                      //convert form RGB to HSV
//                      Color.RGBToHSV(pixR,pixG,pixB,hsv);
//                      hue[i*width+j] = hsv[0];
//                      satu[i*width+j] = hsv[1];
//                      val[i*width+j] = hsv[2];
//
//
//                  }
//              }
//              MatOfFloat matHue= new MatOfFloat(hue);
//              MatOfFloat matHue1= new MatOfFloat(hue);
//              MatOfFloat matHue2= new MatOfFloat(hue);
//              float[] hue1;
//              float[] hue2;
//
//
//              //MatOfFloat dst1 = new MatOfFloat();
//
//              Imgproc.threshold(matHue1, matHue, 50, 180, Imgproc.THRESH_BINARY);//  低通滤波
//              Imgproc.threshold(matHue2, matHue1,310, 180, Imgproc.THRESH_BINARY_INV);//高通滤波
//              //选取合适的频段，
//              //以达到带通滤波的目的
//              for(int i=1;i<width*height;i++){
//                  hue1=matHue1.toArray();
//                  hue2=matHue1.toArray();
//                  if(hue1[i]==180 &&  hue2[i]==180){
//                      hue[i]=180;
//                  }
//                  else {
//                    hue[i]=0;
//                  }
//              }
//
//
//            //饱和度低通滤波
//              MatOfFloat matSatu= new MatOfFloat(satu);
//              Imgproc.threshold(matSatu,matSatu,0.4, 1, Imgproc.THRESH_BINARY);//  低通滤波
//              satu=matSatu.toArray();
//              //亮度低通滤波
//              MatOfFloat matVal = new MatOfFloat(val);
//              Imgproc.threshold(matVal,matVal,0.4, 1, Imgproc.THRESH_BINARY);//  低通滤波
//              val=matVal.toArray();
//
//            //将hsv格式转换为rgb
//
//              for(int i=0;i<width*height;i++)
//              {
//                  hsv[0]=hue[i];
//                  hsv[1]=satu[i];
//                  hsv[2]=val[i];
//
//                 int color= Color.HSVToColor(hsv);
//                  pixelR[i]=Color.red(color);
//                  pixelG[i]=Color.green(color);
//                  pixelB[i]=Color.blue(color);
//                  pixels[i]=Color.argb(255, pixelR[i], pixelG[i], pixelB[i])
//              }
//            // Imgproc.erode(pixelR,pixelR,null);
//              bmp.setPixels(pixels, 0, width, 0, 0, width, height);
//                Mat mat = new Mat();
//              mat.
//
//
//
//
//
//          }
