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

import java.util.Random;

/**
 * Created by w_haizhou on 2017/6/1.
 */

public class OxygenActivity extends Activity {
    /*广播，用于接收数据*/
    BroadcastDataReceiverOxygen dataReceiver;

    int oxygen_data=0;			//吧数据存入List中

    public TextView oxygen_count;
    public TextView tv_oxygen;

    private RelativeLayout back;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxygen);

        OpenBroadcastReceiverOxygen();
        oxygen_count = (TextView) findViewById(R.id.tv_count);
        tv_oxygen = (TextView) findViewById(R.id.tv_oxygen_normal_or_false);

        back = (RelativeLayout) findViewById(R.id.rel_back);
        title = (TextView) findViewById(R.id.tv_tbb_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("血氧");

    }



    /*-----------------------------------广播接受监听线程的信息----------------------------*/

    public class BroadcastDataReceiverOxygen extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            oxygen_data=intent.getIntExtra("OxygenData",0);// 得到新进来的数组
            if(oxygen_data!=0) {
                if (oxygen_data > 95 && oxygen_data < 100)
                    oxygen_count.setText(oxygen_data + "");
                if (oxygen_data < 95)
                    oxygen_count.setText(95 + "");
            }

            if(oxygen_data<90)
                tv_oxygen.setText("血氧过低，请多休息");
            else
                tv_oxygen.setText("血氧正常");
        }

    }

//------注册广播--------

    public void OpenBroadcastReceiverOxygen() {

        // 生成BroadcastReceiver对象
        dataReceiver = new BroadcastDataReceiverOxygen();
        // 生成过滤器IntentFilter对象
        IntentFilter filter = new IntentFilter();
        // 为过滤器添加识别标签
        filter.addAction("android.intent.action.EDIT_Oxygen");
        // 接收广播状态
        OxygenActivity.this.registerReceiver(dataReceiver, filter);

    }
}
