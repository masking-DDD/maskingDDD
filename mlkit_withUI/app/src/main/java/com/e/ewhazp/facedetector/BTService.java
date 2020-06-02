package com.e.ewhazp.facedetector;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

import static com.e.ewhazp.facedetector.BTActivity.bt;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.SetImageGreen;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.SetImageYellow;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.tts;

public class BTService extends Service {
    private static final String TAG = "BTService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG,"BT start onStartCommand");
        return super.onStartCommand(intent, flags, startId );
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"BT Service onDestroy");

        super.onDestroy();
    }

    public BTService() {
        Log.e(TAG,"Service start");

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            int highCnt=0;
            int alarmCnt = 0;
            public void onDataReceived(byte[] data, String message) {
                int ppm = Integer.parseInt(message);
                if (ppm>=2000) {
                    highCnt++;
                    //임계값보다 높은 경우 노란색으로 시각적으로 알림
                    SetImageYellow();
                }
                else {
                    highCnt = 0;
                    //정상 색으로 변경
                    SetImageGreen();
                }
                if ((highCnt==6)&&(alarmCnt<8)) { //co2가 30초마다 오니까 highcount=6 (3분 지속되면)
                    //tts로 소리알람
                    String text = "환기하세요";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ttsGreater21(text);
                    } else {
                        ttsUnder20(text);
                    }
                    highCnt =0;
                    alarmCnt++;
                }
                if(alarmCnt==8){ //8번째 알람에는 소리알람 안하고 한번 쉬고 리셋
                    alarmCnt=0;
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate() {
        Log.e(TAG,"BtService onCreate");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}
