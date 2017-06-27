package com.timesashes.chengdu.myband;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * Created by w_haizhou on 2017/6/1.
 */

public class HeartActivity extends Activity{

    /*广播，用于接收数据*/
    BroadcastDataReceiverHeart dataReceiver;

    int heart_data=0;			//传过来的心率数据

    public TextView heart_count;
    public TextView tv_heart;

    private RelativeLayout back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        OpenBroadcastReceiverHeart();
        back = (RelativeLayout) findViewById(R.id.rel_back);
        title = (TextView) findViewById(R.id.tv_tbb_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("心率");
        heart_count=(TextView)findViewById(R.id.heart_count) ;
        tv_heart=(TextView)findViewById(R.id.tv_heart_normal_or_false);

    }
/*-----------------------------------广播接受监听线程的信息----------------------------*/

    public class BroadcastDataReceiverHeart extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            heart_data=intent.getIntExtra("HeartData",0);// 得到新进来的数组

            if (heart_data > 50 && heart_data < 90)
                      heart_count.setText(heart_data + "");

        }

    }

//------注册广播--------

    public void OpenBroadcastReceiverHeart() {

        // 生成BroadcastReceiver对象
        dataReceiver = new BroadcastDataReceiverHeart();
        // 生成过滤器IntentFilter对象
        IntentFilter filter = new IntentFilter();
        // 为过滤器添加识别标签
        filter.addAction("android.intent.action.EDIT_Heart");
        // 接收广播状态
        HeartActivity.this.registerReceiver(dataReceiver, filter);

    }


}
