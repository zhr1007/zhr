package com.parrot.freeflight.activities.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
/**
 * Created by shisy13 on 16/8/22.
 */
public class ImageProcessor {

    static Bitmap processImage(Bitmap image) {

        image = showPicRedBlack(image);//


        return image;
    }

    /**
     * 图片锐化（拉普拉斯变换）
     *
     * @param bmp
     * @return，
     */
    static private Bitmap sharpenImageAmeliorate(Bitmap bmp) {
        long start = System.currentTimeMillis();
        // 拉普拉斯矩阵
        int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1};

        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int pixR = 0;
        int pixG = 0;
        int pixB = 0;

        int pixColor = 0;

        int newR = 0;
        int newG = 0;
        int newB = 0;

        int idx = 0;
        float alpha = 0.3F;
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);

                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        idx++;
                    }
                }

                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));

                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long end = System.currentTimeMillis();
        Log.d("may", "used time=" + (end - start));
        return bitmap;
    }

    /**
     * 判断红色像素点，单独显示，其他点显示为黑色
     */

    static public Bitmap showPicRedBlack(Bitmap bmp) {
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        double disttemp = 0;                         //临时变量，距离值
        int pixColor = 0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // int[] dist = new int[width * height];    //计算每个像素点与标准红色的欧氏距离
        int RedDistThreshold = 80;               //若距离大于此阈值，则全设为黑色，否则原样输出
        int[] pixelR = new int[width * height];  //R通道
        int[] pixelG = new int[width * height];  //G通道
        int[] pixelB = new int[width * height];  //B通道
        int[] pixels = new int[width * height];   //记录每个像素点的rgb值
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);  //提取图像的RGB值

        /**
         * for循环，依次读取图像的R,G,B 通道
         * 并计算与标准红色距离
         *大于阈值，设为黑色
         */

        for (int i = 0, length = height - 1; i < length; i++) {
            for (int j = 0, len = width - 1; j < len; j++) {

                pixColor = pixels[i * width + j];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                pixelR[i * width + j] = pixR;
                pixelG[i * width + j] = pixG;
                pixelB[i * width + j] = pixB;
                //计算与红色的欧氏距离
                disttemp = (255 - pixR) * (255 - pixR) + pixG * pixG + pixB * pixB;
                disttemp = Math.sqrt(disttemp);
                //与红色距离大的设为黑色
                if (disttemp > RedDistThreshold) {
                    pixelR[i * width + j] = 0;
                    pixelG[i * width + j] = 0;
                    pixelB[i * width + j] = 0;


                }

                //将修改后的三个通道合并
                pixels[i * width + j] = Color.argb(255, pixelR[i * width + j], pixelG[i * width + j], pixelB[i * width + j]);
            }

        }
        //返回修改后的图像
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    /**
     *
     * 路径在图像中一般呈平行四边形，计算形心的位置
     * 目的是：根据形心与图像中心的差，动态调整四旋翼的路径
     * 输入：经过处理后的二色图像（红与黑）
     *以图形中心为坐标中心，x轴向右，y轴向上，各自范围[-1,1]
     * 返回路径中心的坐标，[-1,1]之间
     */

    static public PointF centroid(Bitmap  bmp) {
        PointF pointF = new PointF();
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int pixColor = 0; //像素信息
        int pixR = 0;
        int redNum = 0; //红黑图中，红点的总个数

        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);   //读取像素信息
        float   centerx = 0;  //形心的x坐标
        float   centery = 0; //形心的y坐标


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                pixColor = pixels[j * width + i];
                pixR = Color.red(pixColor);

                //如果红色通道大于0，则为红色，则累加centerx，centery,
                //否则为黑色，不累加
                if (pixR > 0) {
                    redNum = redNum + 1;
                    centerx += centerx;
                    centery += centery;
                }

            }
        }
        centerx = 2 * centerx / redNum / width - 1;
        centery = 2 * centery / redNum / height - 1;
        pointF.x = centerx;
        pointF.y = centery;


        if (redNum<10){
            pointF.x = (float)-2.0;
            pointF.y = (float)-2.0;

        }
        return pointF;
    }

    /**
     * 利用九宫格矫正图像
     * 当某个黑点周围8个点有5个红点，就把该点改为红色
     * 防止红色板块中出现黑斑
     * 进行二次迭代
     * @param bmp  处理过的红黑图像
     * @return   去除黑色斑点的红黑图像
     */
    static public  Bitmap nineCorrect(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int pixColor = 0; //像素信息
        //  int pixR=0;
        int redNum = 0;   //九宫格中
        int[] pixels = new int[width * height];
        int[] pixelR = new int[width * height];   //记录每个像素点的R通道值
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);   //读取像素信息

        //提取每个像素点R通道值，记在pixelR中
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixColor = pixels[j * width + i];
                pixelR[j * width + i] = Color.red(pixColor);
            }
        }

        //矫正黑点
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                redNum = 0; //对每个像素点初始化九宫格的红点计数
                //只对中心的黑点矫正
                if (pixelR[j * width + i] == 0) {
                    //边缘点不进行矫正
                    if (i != 0 && i != width - 1 && j != 0 && j != height) {

                        //对九宫格进行循环，计数红点的个数
                        for (int k = -1; k < 2; k++) {

                            for (int m = -1; m < 2; m++) {

                                if (pixelR[(j+ m) * width + i+k] == 255) { //红色
                                    redNum = redNum + 1;
                                }
                            }

                        }
                    }

                }

                if (redNum > 4) {
                    pixels[j * width + i] = Color.argb(255, 255, 0, 0);  //设为红色
                }


            }

        }

        //重新设置RGB通道
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);

        return bmp;

    }

    /**
     * @param bmp 需要用九宫格矫正的红黑图像
     * @param n   迭代利用nineCorrect进行矫正的次数
     * @return   矫正后的红黑图
     */
    static public  Bitmap IterNineCorrect(Bitmap bmp, int n) {
        for (int i = 0; i < n; i++) {
            bmp = nineCorrect(bmp);
        }
        return bmp;
    }


    /**
     * 图像首先经高斯
     * 滤波，去除杂波
     * 然后
     *
     * @param bmp   摄像头采集的图像
     * @return      处理后的图像
     */
//    public Bitmap imagePreProces(Bitmap bmp)
//    {
//
//    }

    /**
     *
     * @param pointf 来自函数centroid: 路径中心相对于图片中心的位置，x,y均为[-1,1]之间，
     */
    static public  void keepToPath (PointF pointf) {
        double x = pointf.x;
        double y = pointf.y;
        if (x > 0) {
            //飞行器右移
        } else if (x < 0) {
            //飞行器左移
        }
        if (y > 0) {
            //飞行器前进
        } else if (y < 0) {
            //飞行器后退
        }
    }

}
