package com.example.lanyatest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

public class MainActivity extends AppCompatActivity {
    Context context;
    UUID uuid_service_notiyf, uuid_service_write, uuid_service_read;
    UUID uuid_chara_notiyf, uuid_chara_write, uuid_chara_read;
    PopupWindow popupWindowSpeak, popupWindowSetting, popupwindowConnect;
    View viewSpeak, viewSetting, viewConnect;
    TextView englishTxt, francaisTxt, deutschTxt, espanolTxt, settingData, connectName;
    ImageView englishImg, francaisImg, deutschImg, espanolImg, settingClose, backButton;
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
    String uuid = "00002B11-0000-1000-8000-00805F9B34FB";
    UUID serviceUuids = UUID.fromString(uuid);
    BleDevice myBleDerice;
    BluetoothGatt myGatt;
    Adapter adapter = new Adapter(R.layout.layout);
    TextView pop_name;
    @BindView(R.id.v_speed1)
    View vSpeed1;
    @BindView(R.id.v_speed2)
    View vSpeed2;
    @BindView(R.id.v_speed3)
    View vSpeed3;
    //电源 关
    private byte[] writePowerClose = {-1, 2, 1, 0, 85};
    //电源 开启;
    private byte[] writePowerOpen = {-1, 2, 1, 1, 85};

    //风罩关 开
    private byte[] writeWindCloseAndOpen = {-1, 2, 8, 1, 85};
    //风罩关 关
    private byte[] writeWindCloseAndClose = {-1, 2, 8, 0, 85};


    //风罩开 开
    private byte[] writeWindOpenAndOpen = {-1, 2, 2, 1, 85};
    //风罩开 关
    private byte[] writeWindOpenAndClose = {-1, 2, 2, 0, 85};


    //雨量 开
    private byte[] writeRainOpen = {-1, 2, 3, 1, 85};
    //雨量 关
    private byte[] writeRainClose = {-1, 2, 3, 0, 85};

    //进风 开
    private byte[] writeAirIntakeOpen = {-1, 2, 4, 1, 85};
    //进风 关
    private byte[] writeAirIntakeClose = {-1, 2, 4, 0, 85};

    //排风 开
    private byte[] writeExhaustAirOpen = {-1, 2, 5, 1, 85};
    //排风 关
    private byte[] writeExhaustAirClose = {-1, 2, 5, 0, 85};


    //风速 + 开
    private byte[] writeWindSpeedPlusOpen = {-1, 2, 6, 1, 85};
    //风速 + 关
    private byte[] writeWindSpeedPlusClose = {-1, 2, 6, 0, 85};


    //风速 - 开
    private byte[] writeWindSpeedReductionOpen = {-1, 2, 7, 1, 85};
    //风速 - 关
    private byte[] writeWindSpeedReductionClose = {-1, 2, 7, 0, 85};


    //状态查询 - 查询
    private byte[] writeStateQueryOpen = {-1, 2, 10, 1, 85};
    //状态查询 - 关
    private byte[] writeStateQueryClose = {-1, 2, 10, 0, 85};


    //温度 - 开
    private byte[] writeTemperatureClose = {-1, 2, 11, 1, 85};
    //温度 - 关
    private byte[] writeTemperatureOpen = {-1, 2, 11, 0, 85};
    private boolean leidaOpen = false, upOpen = false, downOpen = false, closeDoorRoundOpen = false, openDoorRoundOpen = false;
    private int height = 0, width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        setPopupWindowSpeak();
        setPopwindowSetting();
        setPopupwindowConnect();
        setData();
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
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    leidaOpen = true;
                    leida.setBackgroundResource(R.mipmap.xh - 1);
                    postData(writePowerOpen);
                } else {
                    leidaOpen = false;
                    leida.setBackgroundResource(R.mipmap.xh);
                    postData(writePowerClose);
                }
            }
        });
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
                    Toast.makeText(context, "权限被禁止", Toast.LENGTH_LONG).show();
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

    //    祖册语音pop
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

    //    打开语言pop
    private void showPopSpeak() {
        popupWindowSpeak.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }

    //注册设置pop
    private void setPopwindowSetting() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
        popupWindowSetting = new PopupWindow(this);
        viewSetting = LayoutInflater.from(this).inflate(R.layout.pop_setting, null);
        popupWindowSetting.setContentView(viewSetting);
        popupWindowSetting.setWidth((width / 5) * 4);// 设置弹出窗口的宽
        popupWindowSetting.setHeight((height / 5) * 3);// 设置弹出窗口的高
        popupWindowSetting.setOutsideTouchable(true);//点击空白键取消
        popupWindowSetting.setFocusable(true); //点击返回键取消
        popupWindowSetting.setBackgroundDrawable(new BitmapDrawable());
        seekBar = viewSetting.findViewById(R.id.seekBar);
        settingData = viewSetting.findViewById(R.id.setting_data);
        settingClose = viewSetting.findViewById(R.id.setting_close);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("更新进度", String.valueOf(progress));
                double c = 10 + progress * 0.35;
                double f = c * 1.8 + 32;
                c = (double) Math.round(c * 10) / 10;
                f = (double) Math.round(c * 10) / 10;
                settingData.setText(c + "℃" + "/" + f + "℉");
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

    //    展示设置pop
    private void showPopSetting() {
        if (cheakConnect()) {
            popupWindowSetting.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    }

    //    注册链接pop
    private void setPopupwindowConnect() {
        popupwindowConnect = new PopupWindow(context);
        viewConnect = LayoutInflater.from(this).inflate(R.layout.pop_wiondow, null);
        popupwindowConnect.setContentView(viewConnect);
        popupwindowConnect.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        popupwindowConnect.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的高
        popupwindowConnect.setOutsideTouchable(true);//点击空白键取消
        popupwindowConnect.setFocusable(true); //点击返回键取消
        backButton = viewConnect.findViewById(R.id.img_bt);
        pop_name = viewConnect.findViewById(R.id.tv_name);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowConnect.dismiss();
            }
        });
        getBlueTooth();
    }

    //    展示链接pop
    private void showPopConnect() {
        if (cheakConnect()) {
            popupwindowConnect.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    }

    @OnClick({R.id.img_speak, R.id.img_sitting, R.id.speed_low, R.id.speed_height, R.id.open_door_round, R.id.close_door_round, R.id.leida, R.id.up, R.id.down, R.id.fun_speed, R.id.fun_cover, R.id.rain_sensor, R.id.air_flow, R.id.img_bt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_speak:
                if (cheakConnect()) {
                    showPopSpeak();
                }
                break;
            case R.id.img_sitting:
                if (cheakConnect()) {
                    showPopSetting();
                }
                break;
            case R.id.speed_low:
                if (cheakConnect()) {
                    postData(writeWindSpeedReductionOpen);
                    setSpeed(false);
                }
                break;
            case R.id.speed_height:
                if (cheakConnect()) {
                    postData(writeWindSpeedPlusOpen);
                    setSpeed(true);
                }
                break;
            case R.id.open_door_round:
                if (cheakConnect()) {
                    if (openDoorRoundOpen == true) {
                        openDoorRoundOpen = false;
                        openDoorRound.clearAnimation();
                        postData(writeWindOpenAndClose);
                    } else {
                        openDoorRoundOpen = true;
                        openDoorRound.startAnimation(animation);
                        postData(writeWindOpenAndOpen);
                    }
                    closeDoorRound.clearAnimation();
                }
                break;
            case R.id.close_door_round:
                if (cheakConnect()) {
                    if (closeDoorRoundOpen == true) {
                        closeDoorRoundOpen = false;
                        closeDoorRound.clearAnimation();
                        postData(writeWindCloseAndClose);
                    } else {
                        closeDoorRoundOpen = true;
                        closeDoorRound.startAnimation(animation);
                        postData(writeWindCloseAndOpen);
                    }
                    openDoorRound.clearAnimation();
                }
                break;
            case R.id.leida:
                break;
            case R.id.up:
                if (cheakConnect()) {
                    if (upOpen) {
                        upOpen = false;
                        postData(writeExhaustAirClose);
                        up.setImageResource(R.mipmap.cf);
                    } else {
                        upOpen = true;
                        postData(writeExhaustAirOpen);
                        up.setImageResource(R.mipmap.cf_1);
                    }
                }
                break;
            case R.id.down:
                if (cheakConnect()) {
                    if (downOpen) {
                        downOpen = false;
                        postData(writeAirIntakeClose);
                        down.setImageResource(R.mipmap.jf);
                    } else {
                        downOpen = true;
                        down.setImageResource(R.mipmap.jf_1);
                        postData(writeAirIntakeOpen);
                    }
                }
                break;
            case R.id.fun_speed:
                if (cheakConnect()) {
                    setButton(0);
                }
                break;
            case R.id.fun_cover:
                if (cheakConnect()) {
                    setButton(1);
                }
                break;
            case R.id.rain_sensor:
                if (cheakConnect()) {
                    setButton(2);
                }
                break;
            case R.id.air_flow:
                if (cheakConnect()) {
                    setButton(3);
                }
                break;
            case R.id.img_bt:
                showPopConnect();
                break;
        }
    }

    //设置下方四个大按钮的点击事件
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

    private void setSpeed(boolean add) {
        if (add) {
            if (vSpeed1.getVisibility() == View.INVISIBLE) {
                vSpeed1.setVisibility(View.VISIBLE);
            } else if (vSpeed2.getVisibility() == View.INVISIBLE) {
                vSpeed2.setVisibility(View.VISIBLE);
            } else {
                vSpeed3.setVisibility(View.VISIBLE);
            }
        } else {
            if (vSpeed3.getVisibility() == View.VISIBLE) {
                vSpeed3.setVisibility(View.INVISIBLE);
            } else if (vSpeed2.getVisibility() == View.VISIBLE) {
                vSpeed2.setVisibility(View.INVISIBLE);
            } else {
                vSpeed1.setVisibility(View.INVISIBLE);
            }


        }

    }


    private void aboutBlueTooth() {
        adapter.setNewData(new ArrayList<>());
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, "COWIN")
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
                connectName.setText("COWIN connect failed");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
                Toast.makeText(context, "connect success", Toast.LENGTH_LONG).show();
                myBleDerice = bleDevice;
                pop_name.setText("COWIN conncected");
                myGatt = gatt;
                List<BluetoothGattService> serviceList = gatt.getServices();
                uuid_service_notiyf = serviceList.get(1).getUuid();
                uuid_chara_notiyf = serviceList.get(1).getCharacteristics().get(0).getUuid();
                startIndicate();
                uuid_service_write = serviceList.get(1).getUuid();
                uuid_chara_write = serviceList.get(1).getCharacteristics().get(1).getUuid();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
                pop_name.setText("COWIN Unconnected");
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

    //    打开通知
    private void startIndicate() {
        BleManager.getInstance().notify(
                myBleDerice,
                uuid_service_notiyf.toString(),
                uuid_chara_notiyf.toString(),
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        // 打开通知操作成功
                        Log.e("打开通知成功", "打开通知成功");
                        postData(writeStateQueryOpen);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        // 打开通知操作失败
                        Log.e("打开通知失败", exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        String str = bytesToString(data, data.length);
                        setButtonState(str);
                    }
                }
        );


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


    private String bytesToString(byte[] arg, int length) {
        String result = new String();

        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }


    //判断按钮状态
    private void setButtonState(String data) {
        String str = data.replace(" ", "");
        //rain/sensor（雷达）
        if (String.valueOf(str.charAt(13)).equals("1")) {
            leidaOpen = true;
            leida.setImageResource(R.mipmap.xh_1);
            switcher.setChecked(true);
        } else {
            leidaOpen = false;
            leida.setImageResource(R.mipmap.xh);
            switcher.setChecked(false);
        }

        //exhaust(排风)
        if (String.valueOf(str.charAt(17)).equals("1")) {
            upOpen = true;
            up.setImageResource(R.mipmap.cf_1);

        } else {
            upOpen = false;
            up.setImageResource(R.mipmap.cf);
        }

        //intake
        if (String.valueOf(str.charAt(15)).equals("1")) {
            downOpen = true;
            down.setImageResource(R.mipmap.jf_1);
        } else {
            downOpen = false;
            down.setImageResource(R.mipmap.jf);
        }
        //open\pause(开门的冰箱)
        if (String.valueOf(str.charAt(9)).equals("1")) {
            openDoorRoundOpen = true;
            openDoorRound.startAnimation(animation);
            closeDoorRound.clearAnimation();
        } else {
            openDoorRoundOpen = false;
            openDoorRound.clearAnimation();
        }
        //close\pause(关门的冰箱)
        if (String.valueOf(str.charAt(11)).equals("1")) {
            closeDoorRoundOpen = true;
            closeDoorRound.startAnimation(animation);
            openDoorRound.clearAnimation();
        } else {
            closeDoorRoundOpen = false;
            closeDoorRound.clearAnimation();
        }

        //速度
        if (String.valueOf(str.charAt(21)).equals("0")) {
            vSpeed1.setVisibility(View.INVISIBLE);
            vSpeed2.setVisibility(View.INVISIBLE);
            vSpeed3.setVisibility(View.INVISIBLE);
        } else if (String.valueOf(str.charAt(17)).equals("1")) {
            vSpeed1.setVisibility(View.VISIBLE);
            vSpeed2.setVisibility(View.INVISIBLE);
            vSpeed3.setVisibility(View.INVISIBLE);
        } else if (String.valueOf(str.charAt(17)).equals("2")) {
            vSpeed1.setVisibility(View.VISIBLE);
            vSpeed2.setVisibility(View.VISIBLE);
            vSpeed3.setVisibility(View.INVISIBLE);
        } else if (String.valueOf(str.charAt(17)).equals("3")) {
            vSpeed1.setVisibility(View.VISIBLE);
            vSpeed2.setVisibility(View.VISIBLE);
            vSpeed3.setVisibility(View.VISIBLE);
        }

    }

}