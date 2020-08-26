package com.example.lanyatest;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.functions.Consumer;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

public class DoorActivity extends AppCompatActivity {
    Context context;
    BleDevice myBleDerice;
    BluetoothGatt myGatt;
    UUID uuid_chara_write=UUID.fromString("000036f5-0000-1000-8000-00805f9b34fb");
    UUID uuid_service_write ;
//    Byte[] opendoor = { 0x06,0x30, 0x30, 0x30 ,0x30 ,0x30 ,0x30,0x08 ,0x66 ,0x84 ,0x23};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);
        context = this;
    }
    //    获取权限
    private void getBlueTooth() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    Log.e("结果", aBoolean.toString());
                    cheakBlueTooth();
                } else {
                    Log.e("结果", aBoolean.toString());
                    Toast.makeText(context, "no permission", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //    检查蓝牙是否打开
    private void cheakBlueTooth() {
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        //支持蓝牙模块
        if (blueadapter != null) {
            if (blueadapter.isEnabled()) {
                aboutBlueTooth();
            } else {
                Toast.makeText(context, "BlueTooth off , please open BlueTooth", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "BlueTooth off , please open BlueTooth", Toast.LENGTH_LONG).show();
        }
    }

    private void aboutBlueTooth() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, "yijie")
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanFinished(BleDevice scanResult) {
                // 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）
                if (null != scanResult) {
//                    Toast.makeText(context, "发现设备", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "not shearch device", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStartConnect() {
                // 开始扫描（主线程）
                Toast.makeText(context, "start connect", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Toast.makeText(context, "BlueTooth off , please open BlueTooth", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
                Toast.makeText(context, "connect success", Toast.LENGTH_LONG).show();
                myBleDerice = bleDevice;
                myGatt = gatt;
                List<BluetoothGattService> serviceList = gatt.getServices();
                for(int i = 0;i<serviceList.size();i++){
                    for(int j=0;j<serviceList.get(i).getCharacteristics().size();j++){
                       if(serviceList.get(i).getCharacteristics().get(j).getUuid().equals(uuid_chara_write)){
                           uuid_service_write = serviceList.get(j).getUuid();
                       }
                    }
                }

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
                Toast.makeText(context, "连接断开", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onScanStarted(boolean success) {
                Log.e("扫描开始", String.valueOf(success));
                Toast.makeText(context, "start scan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.e("正在扫描", String.valueOf(bleDevice.toString()));
            }
        });


    }


    //    写入功能
    private void postData(byte[] data) {
        if (cheakConnect()) {
            BleManager.getInstance().write(
                    myBleDerice,
                    uuid_service_write.toString(),
                    uuid_chara_write.toString(),
                    data,
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
//                            Toast.makeText(context, "post data success", Toast.LENGTH_LONG).show();
                            Log.e("post data success", "post data success");
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            // 发送数据到设备失败
//                            Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                            Log.e("post data 失败", exception.toString());
                        }
                    });
        }
    }


    //    检查连接
    private boolean cheakConnect() {
        if (null == myBleDerice) {
            Toast.makeText(context, "no connect", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (STATE_CONNECTED == BleManager.getInstance().getConnectState(myBleDerice)) {
                return true;
            } else {
                Toast.makeText(context, "connect error", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }
}