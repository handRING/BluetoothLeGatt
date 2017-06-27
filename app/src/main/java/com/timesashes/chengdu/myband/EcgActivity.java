package com.timesashes.chengdu.myband;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.timesashes.chengdu.myband.Thread.PaintThread;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.timesashes.chengdu.myband.MainActivity.CONNECT_STATE;
import static com.timesashes.chengdu.myband.MainActivity.CONNECT_SUCCESS;

/**
 * Created by w_haizhou on 2017/6/3.
 */

public class EcgActivity extends Activity {
    /*画图程序*/
    private PaintThread paintThread; 	//绘图线程
    Timer timer = new Timer();			// 创建图像刷新定时器
    private MyTimerTask task = null;	//定时器任务
    final int time 	= 10;	// 定时器触发时间，10ms，每次画20个点，适于200Hz
    final int count = 20;	// 每次画图推的数目，就是每次从List中取Count个数据并画图

    int flag12 = 0;
    int[] dataCount;	// 定时器从List获取的数据，准备画图

    int counterAllPre = 0;
    int counterAll = 0;
    int DrawPoint_pre = 0;	//前一组dataCount中最后一个点，一起传送给绘图线程，使线程DrawLine可以接上一组数据
    int maxnum = 512; // 绘画的最大点数

    private SurfaceView surfaceView = null;
    public SurfaceHolder holder;
    LinearLayout LinearLayoutMain;
    Canvas canvas = null;

    /*获取屏幕参数，屏幕参数，不是整块屏幕的参数，而是当前这个应用所占用的大小*/
    int Viewheight=0;	//当前应用屏幕高度
    int Viewwidth=0;	//当前应用屏幕宽度
    int SurfaceViewwidth=0;		/*SurfaceView的高度和宽度*/
    int SurfaceViewheight=0;

    /*广播，用于接收数据*/
    BroadcastDataReceiver dataReceiver;
    int ReceiveDataCount=0;		//广播接收到的数据个数
    int ReceiveDataCountSEC=0;	//广播接收到的数据个数，根据Hz判断接收时间，调试用
    List<Integer> mlist;			//吧数据存入List中

    //public TextView Recieve_count;

    private RelativeLayout back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);

        //Recieve_count=(TextView)findViewById(R.id.Receive_count);
        LinearLayoutMain=(LinearLayout)findViewById(R.id.ecg);
        mlist = Collections.synchronizedList(new LinkedList<Integer>());// 初始化ArrayList

        dataCount = new int[count];

        surfaceView = (SurfaceView) findViewById(R.id.SurfaceViewId);
        // 得到holder，为holder添加回调结构SurfaceHolder.Callback
        holder = surfaceView.getHolder();
        holder.addCallback(new MyCallBack());

        back = (RelativeLayout) findViewById(R.id.rel_back);
        title = (TextView) findViewById(R.id.tv_tbb_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("ECG");

        startPaintTimer();
        OpenBroadcastReceiver();

    }

    /* 重写函数，获得当前图层的高宽值，并绘制背景线--------------------------------------------------*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Dimension dimen3 = getAreaThree(this);//区域
            Viewheight=dimen3.mHeight;//View区域的高度
//				Log.i(TAG, "Viewheight:"+Viewheight);
            Viewwidth=dimen3.mWidth;//View区域的宽度
//				Log.i(TAG, "Viewwidth:"+Viewwidth);

            int Lheight=LinearLayoutMain.getHeight();//图层高
//				Log.i(TAG, "Lwidth:"+Lwidth);
            SurfaceViewwidth=Viewwidth;
            SurfaceViewheight=Viewheight-Lheight;
            maxnum = SurfaceViewwidth / PaintThread.width;

//			Log.i(TAG, "信息maxnum的值："+maxnum);
            //当为连接蓝牙接收数据时，如果focus变化，重画背景
//			if (CONNECT_STATE != CONNECT_SUCCESS) {
//				new PaintThread(DrawPoint_pre, holder, dataCount, counterAll,
//						counterAllPre, SurfaceViewwidth, SurfaceViewheight)
//						.drawBack1(holder);
//			}
            if (CONNECT_STATE != CONNECT_SUCCESS) {
                new PaintThread(DrawPoint_pre, holder, dataCount, counterAll,
                        counterAllPre, SurfaceViewwidth, SurfaceViewheight)
                        .drawBack1(holder);
            }

        }
    }
    /*-----------计算各个参数-----*/
    private class Dimension {  //类：保存宽度，高度值
        public int mWidth ;
        public int mHeight ;
        public Dimension(){}
    }
    private Dimension getAreaThree(Activity activity){
        Dimension dimen = new Dimension();
        // 用户绘制区域
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        dimen.mWidth = outRect.width() ;
        dimen.mHeight = outRect.height();
        // end
        return dimen;
    }
/*--------------------------------------------------------------------------------*/

    /*------------------------定义MyCallBack函数，SURFACEVIEW用的，不知道什么意思--------------------------------------*/
    class MyCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    /*-----------------------------------广播接受监听线程的信息----------------------------*/

    public class BroadcastDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int data=0;

            ReceiveDataCount++;
            //Recieve_count.setText(ReceiveDataCount+"");
            data=intent.getIntExtra("DataSend",0);// 得到新进来的数组
            mlist.add(data);


            //Log.i(TAG, "List长度"+list.size()+"数据是"+data+"");
            if(ReceiveDataCount%200==0){
                ReceiveDataCountSEC++;
//				Log.i(TAG, "接收数据-------------"+ReceiveDataCount+"");

                //ReceiveData_TextView.setText(ReceiveDataCount+"");
            }

            if(ReceiveDataCountSEC==30)
                ReceiveDataCountSEC=0;

        }

    }

/*------注册广播--------*/

    public void OpenBroadcastReceiver() {

        // 生成BroadcastReceiver对象
        dataReceiver = new BroadcastDataReceiver();
        // 生成过滤器IntentFilter对象
        IntentFilter filter = new IntentFilter();
        // 为过滤器添加识别标签
        filter.addAction("android.intent.action.EDIT");
        // 接收广播状态
        EcgActivity.this.registerReceiver(dataReceiver, filter);

    }

    /*-------------------定时器类------------------------------------*/
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            while (task != null) {
                if (!mlist.isEmpty()) {
                    if (mlist.size() >= count)
                        PaintOperation();
                }
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    // 开启定时器
    public void startPaintTimer() {
        if (task != null) {
            task.cancel(); // 将原任务从队列中移除
        }
        task = new MyTimerTask();
        timer = new Timer();
        // 周期执行task,从0时开始，每隔time个ms进行一次
        try {
            // timer.schedule(task, 0, time);
            timer.schedule(task, time);
        } catch (NullPointerException e) {
            //System("startPaintTimer处空指针异常");
        }
    }

    // 停止定时器
    public void stopPaintTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    /*画图程序，在作为Timer的任务，定时读取List中的count个数据，并开启Paint线程绘制这些数据到画布上---------------*/

    void PaintOperation() {
//		Log.i("draw", "PaintOperation ��ʼ");
        if (flag12 == 1) {
            int[] dataCount1 = new int[maxnum - counterAllPre];
            // ȡ��maxnum����ʣ�µ�
            for (int i = 0; i < (maxnum - counterAllPre); i++) {
                try {
                    dataCount1[i] = mlist.get(0);
                    mlist.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    //System("�����쳣");
                    return;
                }
            }
            counterAll += (maxnum - counterAllPre);
            // ��ͼ
//			Log.v("draw", "PaintThread 1:" + DrawPoint_pre + ";"
//					+ dataCount1.length + ";" + counterAll + ";"
//					+ counterAllPre);
            paintThread = new PaintThread(DrawPoint_pre, holder, dataCount1,
                    counterAll, counterAllPre, Viewwidth, Viewheight);
            paintThread.start();

            DrawPoint_pre = dataCount1[dataCount1.length-1];
            counterAll = 0;
            counterAllPre = 0;
            flag12 = 0;
        } else {
            // ��list��count������ȡ����,dataCountΪһ����СΪcount������
            for (int i = 0; i < count; i++) {
                try {
                    dataCount[i] = mlist.get(0);
                    mlist.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("list", ":" + mlist.size());
                    return;
                }
            }
            // ͳ������
            counterAll += dataCount.length;

//			Log.v("draw", "PaintThread 2:" + DrawPoint_pre + ";" + dataCount.length
//					+ ";" + counterAll + ";" + counterAllPre);

            paintThread = new PaintThread(DrawPoint_pre, holder, dataCount,
                    counterAll, counterAllPre, Viewwidth, Viewheight);
            paintThread.start();
            // ȡ�����һ��Ԫ��
            DrawPoint_pre = dataCount[(dataCount.length - 1)];

            if (counterAll == maxnum) {
                Log.i("draw", "counterAll == maxnum  ��"+counterAll);
                counterAll = 0;
                counterAllPre = 0;
                flag12 = 0;
            } else {
                counterAllPre += dataCount.length;
                if (counterAllPre == maxnum) {
                    flag12 = 0;
                } else if ((counterAllPre + count > maxnum)
                        && (counterAllPre < maxnum)) {
                    flag12 = 1;
                }
            }
        }

    }

}
