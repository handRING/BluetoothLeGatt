package com.timesashes.chengdu.myband;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by w_haizhou on 2017/6/4.
 */

public class PressureActivity extends Activity {

    /*广播，用于接收数据*/
    BroadcastDataReceiverPre dataReceiver;

    int Pre_high=0;			//吧数据存入List中
    int Pre_low=0;

    public TextView pre_high_data;
    public TextView pre_low_data;
    public TextView tv_pre;

    private RelativeLayout back;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        pre_high_data=(TextView)findViewById(R.id.high_count) ;
        pre_low_data=(TextView)findViewById(R.id.low_count) ;
        tv_pre=(TextView)findViewById(R.id.tv_pre_normal_or_false) ;
        back = (RelativeLayout) findViewById(R.id.rel_back);
        title = (TextView) findViewById(R.id.tv_tbb_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("血压");
        OpenBroadcastReceiverPre();
    }
    /*-----------------------------------广播接受监听线程的信息----------------------------*/

    public class BroadcastDataReceiverPre extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Pre_high=intent.getIntExtra("Pre_highData",0);// 得到新进来的数组
            pre_high_data.setText(Pre_high+"");

            Pre_low=intent.getIntExtra("Pre_lowData",0);
            pre_low_data.setText(Pre_low+"");

            if(Pre_high>120||Pre_low>90)
                tv_pre.setText("血压偏高");
            else if(Pre_low<60||Pre_high<90)
                tv_pre.setText("血压偏低");
            else
                tv_pre.setText("血压正常");
        }

    }

//------注册广播--------

    public void OpenBroadcastReceiverPre() {

        // 生成BroadcastReceiver对象
        dataReceiver = new BroadcastDataReceiverPre();
        // 生成过滤器IntentFilter对象
        IntentFilter filter = new IntentFilter();
        // 为过滤器添加识别标签
        filter.addAction("android.intent.action.EDIT_Pre_high");
        // 接收广播状态
        PressureActivity.this.registerReceiver(dataReceiver, filter);

    }
}
