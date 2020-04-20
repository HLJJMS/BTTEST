package com.example.lanyatest;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    Context context;
    String[] permissions = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        permissions[0] = Manifest.permission.BLUETOOTH;
        permissions[1] = Manifest.permission.BLUETOOTH_ADMIN;
//        permissions[2]= Manifest.permission.BLUETOOTH_PRIVILEGED;
        getBlueTooth();
    }

    private void getBlueTooth() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    Log.e("结果", aBoolean.toString());
                } else {
                    Log.e("结果", aBoolean.toString());
                }
            }
        });
    }

    private void cheakBTOpen() {
        //获取蓝牙适配器实例
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //若蓝牙未开启，则开启蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            //使能蓝牙
            bluetoothAdapter.enable();
        }
    }

}
