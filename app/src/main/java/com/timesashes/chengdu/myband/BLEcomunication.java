package com.timesashes.chengdu.myband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static com.timesashes.chengdu.myband.MainActivity.TAG;
import static com.timesashes.chengdu.myband.MainActivity.mBluetoothAdapter;

/**
 * Created by Administrator on 2017/7/3.
 */

public class BLEcomunication {
    private BluetoothGatt mBluetoothGatt;
    int dataInt_ECG,dataInt_Heart,dataInt_Oxygen,dataInt_Pre_high,dataInt_Pre_low;
    private Context context;
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Queue<Byte> queue = new LinkedList<Byte>();
    private byte[] data=new byte[4];
    private byte EE = (byte) 0xEE; // 开始位
    private byte FF = (byte) 0xFF; // 结束位
    private byte[] sendByte={EE};

    private BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState== BluetoothProfile.STATE_CONNECTED){
                mBluetoothGatt.discoverServices();//连接成功
                MainActivity.mHandler.obtainMessage(MainActivity.CONNECT_SUCCESS).sendToTarget();

            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                MainActivity.mHandler.obtainMessage(MainActivity.CONNECT_BREAK).sendToTarget();
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.d(TAG,"发现Services");
                List<BluetoothGattService> mCustomService = mBluetoothGatt.getServices();

                if(mCustomService!=null)
                    Log.d(TAG,"gattServiceList不为空");
                BluetoothGattService mBluetoothGattService=mCustomService.get(0);

                List<BluetoothGattCharacteristic> gattCharacteristicsList=mBluetoothGattService.getCharacteristics();
                BluetoothGattCharacteristic gattCharacteristic = gattCharacteristicsList.get(0);
                if(gattCharacteristic!=null)
                    Log.d(TAG,"Characteristic不为空");


                final int charaProp=gattCharacteristic.getProperties();
                if((charaProp|BluetoothGattCharacteristic.PROPERTY_NOTIFY)>0){
                    setCharacteristicNotification(gattCharacteristic,true);
                }

                boolean Rssi=mBluetoothGatt.readRemoteRssi();
                if(Rssi)
                    Log.d(TAG,"Rssi可读为true");

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //  broadcastUpdate(ACTION_DATA_AVAILABLE, ch
                Log.d(TAG,"Read有数据");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.d(TAG,"the given characteristic has changed");

            if(characteristic.getValue()!=null)
                Log.d(TAG,"可以得到数据");
            final byte[] datar=characteristic.getValue();
            if (datar != null && datar.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(datar.length);
                for(byte byteChar : datar)
                {
                    stringBuilder.append(String.format("%02X ", byteChar));
                    queue.offer(byteChar);
                }
                Log.d(TAG,"数据转字符串为："+stringBuilder.toString());
                Log.d(TAG,"长度："+queue.size());
            }
            while(queue.size()>=4d)
            {

                for (int i=0;i<4;i++)
                {
                    data[i]=queue.poll();
                }
                final StringBuilder stringBuilder1 = new StringBuilder(data.length);
                for(byte byteChar : data)
                {
                    stringBuilder1.append(String.format("%02X ", byteChar));
                }
                Log.d(TAG,"待传数据为："+stringBuilder1.toString());
                if(data[0]==EE&&data[3]==FF)
                {
                    // 转换为整数 ECG
                    dataInt_ECG = (  (0x0FF &  data[1]  ) << 8   )
                            + (  0x0FF & (data[2]<<1) );
                    dataInt_ECG >>=1;
                    SendBroadCastOfData() ;
                    data[0]=(byte)0xFE;
                    data[3]=(byte)0xFE;
                }

            }



            characteristic.setValue(sendByte);
            writeCharacteristic(characteristic);

/*

                // 转换为整数 ECG
                dataInt_ECG = (  (0x0FF &  data[1]  ) << 8   )
                        + (  0x0FF & (data[2]<<1) );
                dataInt_ECG >>=1;

                // 转换为整数 Heart
                dataInt_Heart = (  (0x0FF &  data[3]  ) << 8   )
                        + (  0x0FF & (data[4]<<1) );
                dataInt_Heart >>=1;

                // 转换为整数 Oxygen
                dataInt_Oxygen = (  (0x0FF &  data[5]  ) << 8   )
                        + (  0x0FF & (data[6]<<1) );
                dataInt_Oxygen >>=1;
               // 转换为整数 Pre_high
                dataInt_Pre_high = (  (0x0FF &  data[7]  ) << 8   )
                        + (  0x0FF & (data[8]<<1) );
                dataInt_Pre_high >>=1;

                // 转换为整数 Pre_low
                dataInt_Pre_low = (  (0x0FF &  data[9]  ) << 8   )
                        + (  0x0FF & (data[10]<<1) );
                dataInt_Pre_low >>=1;

                SendBroadCastOfData() ;
            */

        }



        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status){
            if(status==BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Write有数据------>");
            }
        }
    };


    void SendBroadCastOfData() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra("DataSend", dataInt_ECG);                // ECG
        context.sendBroadcast(intent);
/*
        intent.setAction("android.intent.action.EDIT_Heart");
        intent.putExtra("HeartData",dataInt_Heart);             //心率
        context.sendBroadcast(intent);

        intent.setAction("android.intent.action.EDIT_Oxygen");
        intent.putExtra("OxygenData",dataInt_Oxygen);         //血氧
        context.sendBroadcast(intent);

        intent.setAction("android.intent.action.EDIT_Pre_high");
        intent.putExtra("Pre_highData",dataInt_Pre_high);       //收缩压
        intent.putExtra("Pre_lowData",dataInt_Pre_low);
        context.sendBroadcast(intent);*/
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        Log.d(TAG,"calling readCharacteristic ");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }


    public void connectBLE(BluetoothDevice device){
        mBluetoothGatt = device.connectGatt(context,false,mGattCallback);
        MainActivity.mHandler.obtainMessage(MainActivity.CONNECTING).sendToTarget();
    }
}
