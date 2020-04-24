package com.example.lanyatest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
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
import com.clj.fastble.callback.BleIndicateCallback;
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
    UUID uuid_service;
    UUID uuid_chara;
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

    //电源 关
    private String writePowerClose = "FF02010055";
    //电源 开启;
    private String writePowerOpen = "FF02010155";

    //风罩开 关
    private String writeWindCloseAndOpen = "FF02020055";
    //风罩开 开
    private String writeWindOpenAndOpen = "FF02020155";


    //风罩关 开
    private String writeWindOpenAndClose = "FF02080155";
    //风罩关 关
    private String writeWindCloseAndClose = "FF02080055";


    //雨量 开
    private String writeRainOpen = "FF02030155";
    //雨量 关
    private String writeRainClose = "FF02030055";

    //进风 开
    private String writeAirIntakeOpen = "FF02040155";
    //进风 关
    private String writeAirIntakeClose = "FF02040055";

    //排风 开
    private String writeExhaustAirOpen = "FF02050155";
    //排风 关
    private String writeExhaustAirClose = "FF02050055";


    //风速 + 开
    private String writeWindSpeedPlusOpen = "FF02060155";
    //风速 + 关
    private String writeWindSpeedPlusClose = "FF02060055";


    //风速 - 开
    private String writeWindSpeedReductionOpen = "FF02070155";
    //风速 - 关
    private String writeWindSpeedReductionClose = "FF02070055";


    //状态查询 - 查询
    private String writeStateQueryOpen = "FF02100155";
    //状态查询 - 关
    private String writeStateQueryClose = "FF02100055";


    //温度 - 关 代表 关闭
    private String writeTemperatureClose = "FF02110055";
    private boolean leidaOpen = false, upOpen = false, downOpen = false;

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
                    postData(writePowerOpen);
                } else {
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
            }else{
                Toast.makeText(context, "please open BlueTooth", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(context, "please open BlueTooth", Toast.LENGTH_LONG).show();
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

    //    展示设置pop
    private void showPopSetting() {
        popupWindowSetting.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
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
        connectName = viewConnect.findViewById(R.id.name);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindowConnect.dismiss();
            }
        });
    }

    //    展示链接pop
    private void showPopConnect() {
        popupwindowConnect.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
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
                postData(writeWindSpeedReductionOpen);
                break;
            case R.id.speed_height:
                postData(writeWindSpeedPlusOpen);
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
                if (leidaOpen) {
                    leidaOpen = false;
                    postData(writeRainClose);
                    leida.setBackgroundResource(R.mipmap.xh);
                } else {
                    leidaOpen = true;
                    postData(writeRainOpen);
                    leida.setBackgroundResource(R.mipmap.xh_1);
                }
                break;
            case R.id.up:
                if (upOpen) {
                    upOpen = false;
                    postData(writeExhaustAirClose);
                    up.setBackgroundResource(R.mipmap.jf);
                } else {
                    upOpen = true;
                    postData(writeExhaustAirOpen);
                    leida.setBackgroundResource(R.mipmap.jf_1);
                }
                break;
            case R.id.down:
                if (downOpen) {
                    downOpen = false;
                    postData(writeAirIntakeClose);
                } else {
                    downOpen = true;
                    postData(writeAirIntakeOpen);
                }
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
                showPopConnect();
                getBlueTooth();
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



    //    关于蓝牙的设定
    private void aboutBlueTooth() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(new UUID[]{serviceUuids})      // 只扫描指定的服务的设备，可选
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanFinished(BleDevice scanResult) {
                // 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）
                if (null != scanResult) {
                    Toast.makeText(context, "发现设备", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "未发现设备", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStartConnect() {
                // 开始扫描（主线程）
                Toast.makeText(context, "start connect", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                connectName.setText(bleDevice.getName() + "connect failed");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
                Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                myBleDerice = bleDevice;
                connectName.setText(bleDevice.getName() + "conncected");
                myGatt = gatt;
                List<BluetoothGattService> serviceList = gatt.getServices();
                for (BluetoothGattService service : serviceList) {
                    uuid_service = service.getUuid();
                    List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicList) {
                        uuid_chara = characteristic.getUuid();
                    }
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
            }

            @Override
            public void onScanStarted(boolean success) {
                Log.e("扫描开始", String.valueOf(success));
                Toast.makeText(context, "start scan", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.e("正在扫描", String.valueOf(bleDevice.toString()));
            }
        });

        startIndicate();
    }

    //    打开通知
    private void startIndicate() {
        BleManager.getInstance().indicate(
                myBleDerice,
                uuid,
                uuid,
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        // 打开通知操作成功
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        // 打开通知操作失败
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        // 打开通知后，设备发过来的数据将在这里出现
                        Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    //    写入功能
    private void postData(String data) {
        if (cheakConnect()) {
            BleManager.getInstance().write(
                    myBleDerice,
                    uuid,
                    uuid,
                    data.getBytes(),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                            Toast.makeText(context, "post data success", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                            // 发送数据到设备失败
                            Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
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
