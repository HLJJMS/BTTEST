package com.example.lanyatest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    Context context;
    String[] permissions = new String[3];
    PopupWindow popupWindowSpeak, popupWindowSetting;
    View viewSpeak, viewSetting;
    TextView englishTxt, francaisTxt, deutschTxt, espanolTxt, settingData;
    ImageView englishImg, francaisImg, deutschImg, espanolImg, settingClose;
    SeekBar seekBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.top)
    ImageView top;
    @BindView(R.id.switcher)
    Switch switcher;
    @BindView(R.id.img_speak)
    ImageView imgSpeak;
    @BindView(R.id.img_sitting)
    ImageView imgSitting;
    @BindView(R.id.speed_low)
    ImageView speedLow;
    @BindView(R.id.speed_height)
    ImageView speedHeight;
    @BindView(R.id.ll_speed)
    LinearLayout llSpeed;
    @BindView(R.id.open_door)
    ImageView openDoor;
    @BindView(R.id.open_door_round)
    ImageView openDoorRound;
    @BindView(R.id.close_door_round)
    ImageView closeDoorRound;
    @BindView(R.id.close_door)
    ImageView closeDoor;
    @BindView(R.id.ll_cover)
    ConstraintLayout llCover;
    @BindView(R.id.leida)
    ImageView leida;
    @BindView(R.id.ll_rain)
    LinearLayout llRain;
    @BindView(R.id.up)
    ImageView up;
    @BindView(R.id.down)
    ImageView down;
    @BindView(R.id.ll_air)
    ConstraintLayout llAir;
    @BindView(R.id.fun_speed)
    ImageView funSpeed;
    @BindView(R.id.fun_cover)
    ImageView funCover;
    @BindView(R.id.rain_sensor)
    ImageView rainSensor;
    @BindView(R.id.air_flow)
    ImageView airFlow;
    @BindView(R.id.img_fun_speed)
    ImageView imgFunSpeed;
    @BindView(R.id.tv_fun_speed)
    TextView tvFunSpeed;
    @BindView(R.id.img_fun_cover)
    ImageView imgFunCover;
    @BindView(R.id.tv_fun_cover)
    TextView tvFunCover;
    @BindView(R.id.img_rain_sensor)
    ImageView imgRainSensor;
    @BindView(R.id.tv_rain_sensor)
    TextView tvRainSensor;
    @BindView(R.id.img_air_flow)
    ImageView imgAirFlow;
    @BindView(R.id.tv_air_flow)
    TextView tvAirFlow;
    @BindView(R.id.img_bt)
    ImageView imgBt;
    List<ImageView> imgList = new ArrayList<>();
    List<View> viewList = new ArrayList<>();
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        permissions[0] = Manifest.permission.BLUETOOTH;
        permissions[1] = Manifest.permission.BLUETOOTH_ADMIN;
//        permissions[2]= Manifest.permission.BLUETOOTH_PRIVILEGED;
        getBlueTooth();
        setPopupWindowSpeak();
        setPopwindowSetting();
        setData();
//        aboutBlueTooth();

    }


    private void setData() {
        imgList.add(funSpeed);
        imgList.add(funCover);
        imgList.add(rainSensor);
        imgList.add(airFlow);
        viewList.add(llSpeed);
        viewList.add(llCover);
        viewList.add(llRain);
        viewList.add(llAir);
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rote);
    }

    private void getBlueTooth() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Boolean>() {
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


    private void setPopupWindowSpeak() {
        popupWindowSpeak = new PopupWindow(this);
        viewSpeak = LayoutInflater.from(this).inflate(R.layout.pop_speak, null);
        popupWindowSpeak.setContentView(viewSpeak);
        popupWindowSpeak.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的宽
        popupWindowSpeak.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindowSpeak.setOutsideTouchable(true);//点击空白键取消
        popupWindowSpeak.setFocusable(true); //点击返回键取消
        englishTxt = viewSpeak.findViewById(R.id.English);
        francaisTxt = viewSpeak.findViewById(R.id.Francais);
        deutschTxt = viewSpeak.findViewById(R.id.Deutsch);
        espanolTxt = viewSpeak.findViewById(R.id.Espanol);
        englishImg = viewSpeak.findViewById(R.id.ic_English);
        francaisImg = viewSpeak.findViewById(R.id.ic_Francais);
        deutschImg = viewSpeak.findViewById(R.id.ic_Deutsch);
        espanolImg = viewSpeak.findViewById(R.id.ic_Espanol);
        englishTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFunSpeed.setText("Fun Speed");
                tvFunCover.setText("Fun Cover");
                tvRainSensor.setText("Rain Sensor");
                tvAirFlow.setText("Air Flow");
                englishImg.setVisibility(View.VISIBLE);
                francaisImg.setVisibility(View.INVISIBLE);
                deutschImg.setVisibility(View.INVISIBLE);
                espanolImg.setVisibility(View.INVISIBLE);

            }
        });
        francaisTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFunSpeed.setText("Vitesse intéressante");
                tvFunCover.setText("Une couverture intéressante.");
                tvRainSensor.setText("Capteur de pluie");
                tvAirFlow.setText("Courant d 'air");
                englishImg.setVisibility(View.INVISIBLE);
                francaisImg.setVisibility(View.VISIBLE);
                deutschImg.setVisibility(View.INVISIBLE);
                espanolImg.setVisibility(View.INVISIBLE);
            }
        });
        deutschTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFunSpeed.setText("Geschwindigkeit des Spiels");
                tvFunCover.setText("Fun Cover");
                tvRainSensor.setText("Sensoren für Regen");
                tvAirFlow.setText("Strömung der Luft");
                englishImg.setVisibility(View.INVISIBLE);
                francaisImg.setVisibility(View.INVISIBLE);
                deutschImg.setVisibility(View.VISIBLE);
                espanolImg.setVisibility(View.INVISIBLE);
            }
        });
        espanolTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFunSpeed.setText("Velocidad interesante");
                tvFunCover.setText("Interesante portada.");
                tvRainSensor.setText("Sensor de lluvia");
                tvAirFlow.setText("Corriente");
                englishImg.setVisibility(View.INVISIBLE);
                francaisImg.setVisibility(View.INVISIBLE);
                deutschImg.setVisibility(View.INVISIBLE);
                espanolImg.setVisibility(View.VISIBLE);
            }
        });
        popupWindowSpeak.setTouchable(true);

    }

    private void showPopSpeak() {
        popupWindowSpeak.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }

    private void setPopwindowSetting() {
        popupWindowSetting = new PopupWindow(this);
        viewSetting = LayoutInflater.from(this).inflate(R.layout.pop_setting, null);
        popupWindowSetting.setContentView(viewSetting);
        popupWindowSetting.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的宽
        popupWindowSetting.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        popupWindowSetting.setOutsideTouchable(true);//点击空白键取消
        popupWindowSetting.setFocusable(true); //点击返回键取消
        seekBar = viewSetting.findViewById(R.id.seekBar);
        settingData = viewSetting.findViewById(R.id.setting_data);
        settingClose = viewSetting.findViewById(R.id.setting_close);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("更新进度", String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        settingClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowSetting.dismiss();
            }
        });
        popupWindowSetting.setTouchable(true);


    }

    private void showPopSetting() {
        popupWindowSetting.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }


    @OnClick({R.id.img_speak, R.id.img_sitting, R.id.speed_low, R.id.speed_height, R.id.open_door_round, R.id.close_door_round, R.id.leida, R.id.up, R.id.down, R.id.fun_speed, R.id.fun_cover, R.id.rain_sensor, R.id.air_flow, R.id.img_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_speak:
                showPopSpeak();
                break;
            case R.id.img_sitting:
                showPopSetting();
                break;
            case R.id.speed_low:
                break;
            case R.id.speed_height:
                break;
            case R.id.open_door_round:
                openDoorRound.startAnimation(animation);
                closeDoorRound.clearAnimation();
                break;
            case R.id.close_door_round:
                closeDoorRound.startAnimation(animation);
                openDoorRound.clearAnimation();
                break;
            case R.id.leida:
                break;
            case R.id.up:
                break;
            case R.id.down:
                break;
            case R.id.fun_speed:
                setButton(0);
                break;
            case R.id.fun_cover:
                setButton(1);
                break;
            case R.id.rain_sensor:
                setButton(2);
                break;
            case R.id.air_flow:
                setButton(3);
                break;
            case R.id.img_bt:
                aboutBlueTooth();
                break;
        }
    }


    private void setButton(int position) {
        for (int i = 0; i < 4; i++) {
            if (i == position) {
                viewList.get(i).setVisibility(View.VISIBLE);
            } else {
                viewList.get(i).setVisibility(View.GONE);
            }
        }
        switch (position) {
            case 0:
                funSpeed.setBackgroundResource(R.drawable.left_top_on);
                funCover.setBackgroundResource(R.drawable.right_top);
                rainSensor.setBackgroundResource(R.drawable.left_bottom);
                airFlow.setBackgroundResource(R.drawable.right_bottom);
                break;
            case 1:
                funSpeed.setBackgroundResource(R.drawable.left_top);
                funCover.setBackgroundResource(R.drawable.right_top_on);
                rainSensor.setBackgroundResource(R.drawable.left_bottom);
                airFlow.setBackgroundResource(R.drawable.right_bottom);
                break;
            case 2:
                funSpeed.setBackgroundResource(R.drawable.left_top);
                funCover.setBackgroundResource(R.drawable.right_top);
                rainSensor.setBackgroundResource(R.drawable.left_bottom_on);
                airFlow.setBackgroundResource(R.drawable.right_bottom);
                break;
            case 3:
                funSpeed.setBackgroundResource(R.drawable.left_top);
                funCover.setBackgroundResource(R.drawable.right_top);
                rainSensor.setBackgroundResource(R.drawable.left_bottom);
                airFlow.setBackgroundResource(R.drawable.right_bottom_on);
                break;

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
//                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
//                .setDeviceName(true, "Honor Play")         // 只扫描指定广播名的设备，可选
//                .setDeviceMac("7c:76:68:db:8c:b0")                  // 只扫描指定mac的设备，可选
//                .setAutoConnect(isAutoConnect)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
//        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
//            @Override
//            public void onScanFinished(BleDevice scanResult) {
//                // 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）
//                if (null != scanResult) {
//                    Log.e("扫描结束", scanResult.toString());
//                } else {
//                    Log.e("啥也，没有", "meiy ");
//                }
//            }
//
//            @Override
//            public void onStartConnect() {
//                // 开始扫描（主线程）
//                Log.e("链接", "开始");
//            }
//
//            @Override
//            public void onConnectFail(BleDevice bleDevice, BleException exception) {
//                Log.e("连接失败", exception.toString());
//            }
//
//            @Override
//            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
//                Log.e("成功链接", bleDevice.toString());
//            }
//
//            @Override
//            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
//                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
//            }
//
//            @Override
//            public void onScanStarted(boolean success) {
//                Log.e("扫描开始", String.valueOf(success));
//            }
//
//            @Override
//            public void onScanning(BleDevice bleDevice) {
//                Log.e("正在骚麦", String.valueOf(bleDevice.toString()));
//            }
//        });
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                // 开始扫描（主线程）
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                // 扫描到一个符合扫描规则的BLE设备（主线程）
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）
                if(scanResultList.size()==0){

                }else{

                }
            }
        });


    }
}
