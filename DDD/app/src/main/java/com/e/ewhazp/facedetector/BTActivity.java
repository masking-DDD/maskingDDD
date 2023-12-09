package com.e.ewhazp.facedetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.samples.apps.mlkit.R;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BTActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static BluetoothSPP bt;
    public static Context mcontext;
    private static final String TAG = "BTActivity";
    public static Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        mcontext = this;
        bt = new BluetoothSPP(mcontext); //Initializing
        intent = new Intent(mcontext,BTService.class);
        startService(intent);
        Log.e(TAG,"Hi");

        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.disconnect();
        } else {
            Intent intent1 = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent1, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            Log.e(TAG,"onStart !isBluetoothEnabled");
        } else {
            if (!bt.isServiceAvailable()) {
                Log.e(TAG,"onStart isBluetoothEnabled");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"Bye");
    }
}
