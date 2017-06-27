package com.timesashes.chengdu.myband.Thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import com.timesashes.chengdu.myband.EcgActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaintThread extends Thread {
    /*------------------------变量的声明---------------------------------------------*/
    // 两个成员变量
    public SurfaceHolder holder;
    Canvas canvas = null;
    Paint p = new Paint();
    // 变量
    EcgActivity context;// Activity定义
    float pts[]; // 要画的数据
    // SurfaceView的高度和宽度
    int SurfaceViewwidth = 0;
    int SurfaceViewheight = 0;
    // 背景坐标数组
    float[] pts_backgroud_Y;
    // 定义要画的数组
    int[] dataCount;
    int centerY = 0;
    // 判断数据是否全大于0
    int j1 = 0;
    int sizeOfSurfaceViewheight = 0;
    int[] dataDraw;
    int counterAll = 0;
    int counterAllPre = 0;
    int indexTemp = 0;
    
    public static final int width =1; //点距离
    public static final int linewidth =3; //线的厚度

    /*---------------------------------------------------------------------------------*/

    /*------------------------构造方法---------------------------------------------*/
    // 成员方法初始化
    public PaintThread(int indexTemp, SurfaceHolder holder, int[] dataCount,
                       int counterAll, int counterAllPre, int SurfaceViewwidth,
                       int SurfaceViewheight) {
        
        this.indexTemp = indexTemp;
        this.holder = holder;
        this.SurfaceViewwidth = SurfaceViewwidth;
        this.SurfaceViewheight = SurfaceViewheight;
        this.dataCount = dataCount;// 进来的是要画的数组
        this.counterAll = counterAll;
        this.counterAllPre = counterAllPre;
        centerY = this.SurfaceViewheight / 2;
        sizeOfSurfaceViewheight = SurfaceViewheight / 18;
        //dataExchange();
        changeTopts();
//      Log.i(EcgActivity.TAG, "PainThread构造");    
    }

    /*---------------------------------------------------------------------------------*/

    /*-------------------------------线程运行时执行的情况--------------------------------------------------*/
    // 线程运行时进行处理
    @Override
    public void run() {
//      Log.i(EcgActivity.TAG, "PainThread开始RUN"); 
        super.run();
        
        // 画笔的设置

        p.setColor(Color.BLACK);// 画笔颜色为蓝色
        p.setStrokeWidth(linewidth);// 画笔的粗细
        p.setAntiAlias(true);// 设置抗锯齿
        p.setDither(true);
    
        Rect rect = new Rect((counterAllPre-1 ) * width, 0, (counterAll + 33) * width,
                SurfaceViewwidth);
        
        canvas = holder.lockCanvas(rect);// 特别注意的地方
        
        if( canvas !=null){
            drawBack(holder);
            canvas.drawLine((counterAllPre-1 ) * width, indexTemp, counterAllPre * width,
                    dataDraw[0], p);
            canvas.drawLines(pts, p);
            holder.unlockCanvasAndPost(canvas); 
        }
    }

    /*---------------------------------------------------------------------------------*/

    /*----------------------------------坐标轴背景-----------------------------------------------------------------------*/
    // 绘制背景
    public void drawBack(SurfaceHolder holder) {
        // 绘制黑色背景
        canvas.drawColor(Color.rgb(190, 209, 199));
        // 画笔设置
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        float[] pts_backgroud_Y = new float[72];
        int j = -1;
        for (int i = 0; i < pts_backgroud_Y.length; i++) {
            if (i % 4 == 0) {
                j++;
                pts_backgroud_Y[i] = 0;
            }
            if (i % 4 == 1)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
            if (i % 4 == 2)
                pts_backgroud_Y[i] = SurfaceViewwidth;
            if (i % 4 == 3)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
        }
        canvas.drawLines(pts_backgroud_Y, p);
    }

    // 绘制背景
    public void drawBack1(SurfaceHolder holder) {
        
//      Log.i(EcgActivity.TAG, "PainThread开始drawBack1");   
        canvas = holder.lockCanvas();
        // 绘制黑色背景
//      Log.i(EcgActivity.TAG, "PainThread开始drawBack1--2");    
        
        canvas.drawColor(Color.rgb(190, 209, 199));
        // 画笔设置
//      Log.i(EcgActivity.TAG, "PainThread开始drawBack1--3");
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        // 与X轴平行的网格线,注意SurfaceView的横纵坐标从它的区域开始
        float[] pts_backgroud_Y = new float[72];
        int j = -1;
        for (int i = 0; i < pts_backgroud_Y.length; i++) {
            if (i % 4 == 0) {
                j++;
                pts_backgroud_Y[i] = 0;
            }
            if (i % 4 == 1)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
            if (i % 4 == 2)
                pts_backgroud_Y[i] = SurfaceViewwidth;
            if (i % 4 == 3)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
        }
        canvas.drawLines(pts_backgroud_Y, p);
        holder.unlockCanvasAndPost(canvas);
    }


/*

    public void dataExchange()
    {

        List listData=new ArrayList();
        listData= Arrays.asList(dataCount);
        int m=20;
        for(int i=m;i<listData.size()-m;i++)
        {
            List<Float> sortList=new ArrayList<Float>(listData.subList(i-m,i+m));
            Collections.sort(sortList);
            float mid=sortList.get(sortList.size()/2);
            dataCount[i]=(int)((float)listData.get(i)-mid);
        }
    }
*/
  /*
    public void dataExchange()
    {
        double v[]=new double[2];
        v[1]=0;
        for(int i=0;i<dataCount.length;i++)
        {
            v[0]=v[1];
            v[1]=(0.2452372752527856026 * dataCount[i]) + (0.50952544949442879485*v[0]);
            dataCount[i]=(int)(v[0]+v[1]);
        }
    }
*/


    /*----------------------------------把数组转化成要画的坐标-------------------------------------*/

    public void changeTopts() {
        dataDraw = new int[dataCount.length];
        for (int i = 0; i < dataDraw.length; i++) {
           // dataDraw[i] = centerY - (dataCount[i] / 6);
            dataDraw[i] = SurfaceViewheight
                    - (dataCount[i] * SurfaceViewheight / 4096);
        }

         //indexTemp = centerY - (indexTemp / 6);
        indexTemp = SurfaceViewheight - (indexTemp * SurfaceViewheight / 4096);
        j1 = -1;
        pts = new float[4 * (dataCount.length - 1)];
        for (int i = 0; i < pts.length; i++) {
            if (i % 4 == 0) {
                j1++;
                pts[i] = (counterAllPre + j1) * width;
            }
            if (i % 4 == 1)
                pts[i] = dataDraw[j1];
            if (i % 4 == 2)
                pts[i] = (counterAllPre + j1 + 1) * width;
            if (i % 4 == 3)
                pts[i] = dataDraw[j1 + 1];
        }
        
    }

    /*-------------------------------------------------------------------------------------------------*/

    /*-----------------------------------------当蓝牙停止时要执行-----------------------------------------*/
    public void drawCBack(SurfaceHolder holder) {
        canvas = holder.lockCanvas();
        // 绘制黑色背景
        canvas.drawColor(Color.rgb(190, 209, 199));
        // 画笔设置
        Paint p = new Paint();
        p.setColor(Color.WHITE);

        Paint p1 = new Paint();
        p1.setColor(Color.BLACK);// 画笔颜色为蓝色
        p1.setStrokeWidth(linewidth);// 画笔的粗细
        p1.setAntiAlias(true);// 设置抗锯齿
        p1.setDither(true);
        // 与X轴平行的网格线,注意SurfaceView的横纵坐标从它的区域开始
        float[] pts_backgroud_Y = new float[72];
        int j = -1;
        for (int i = 0; i < pts_backgroud_Y.length; i++) {
            if (i % 4 == 0) {
                j++;
                pts_backgroud_Y[i] = 0;
            }
            if (i % 4 == 1)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
            if (i % 4 == 2)
                pts_backgroud_Y[i] = SurfaceViewwidth;
            if (i % 4 == 3)
                pts_backgroud_Y[i] = (j + 1) * sizeOfSurfaceViewheight;
        }
        canvas.drawLines(pts_backgroud_Y, p);
        canvas.drawLine(0, centerY, SurfaceViewwidth, centerY, p1);
        holder.unlockCanvasAndPost(canvas);
    }

    /*-------------------------------------------------------------------------------------------------*/

}
