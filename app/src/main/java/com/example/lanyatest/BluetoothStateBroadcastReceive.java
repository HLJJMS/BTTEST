package com.example.lanyatest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothStateBroadcastReceive extends BroadcastReceiver {

    CallBackBlueState callBackBlueState ;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
//            case BluetoothAdapter.ACTION_STATE_CHANGED:
//                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
//                switch (blueState) {
//                    case BluetoothAdapter.STATE_OFF:
//                        Toast.makeText(context, "Bluetooth close", Toast.LENGTH_SHORT).show();
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        Toast.makeText(context, "Bluetooth open", Toast.LENGTH_SHORT).show();
//                        callBackBlueState.open();
//                        break;
//                }
//                break;
        }
    }

}

