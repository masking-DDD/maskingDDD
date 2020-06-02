package com.e.ewhazp.facedetector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ewhazp.ChooserActivity;
import com.e.ewhazp.preprocessing.CameraSource;
import com.e.ewhazp.preprocessing.CameraSourcePreview;
import com.e.ewhazp.preprocessing.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.e.ewhazp.facedetector.BTActivity.bt;
import static com.e.ewhazp.facedetector.BTActivity.intent;
import static com.e.ewhazp.facedetector.BTActivity.mcontext;
import static com.e.ewhazp.facedetector.SetDrowsinessState.closedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.openedEAR;
import static com.e.ewhazp.facedetector.noticetoDriver.plz;


public class DetectingDrowsiness extends AppCompatActivity //가장 오래 사용될 화면
        implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "DetectingDrowsiness";
    private static final int PERMISSION_REQUESTS = 1;

    static boolean getCallStatus = false;
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    @SuppressLint("StaticFieldLeak")
    static TextToSpeech tts;
    @SuppressLint("StaticFieldLeak")
    public Button gotoInit;
    final Intent MDintent = new Intent(this, MonitorDrowsiness.class);
    @SuppressLint("StaticFieldLeak")

    static MediaPlayer media;
    @SuppressLint("StaticFieldLeak")
    public static ProgressDialog initpd;
    @SuppressLint("StaticFieldLeak")
    static ImageView imageview = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detecting_drowsiness);

        mcontext = this;
        tts = new TextToSpeech(mcontext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                    Log.e(TAG,"tts init");
                }
            }
        });

        preview = findViewById(R.id.firePreview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.fireFaceOverlay);

        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        if (allPermissionsGranted()) {
            createCameraSource();
            startCameraSource();
        } else {
            getRuntimePermissions();
        }

        final Intent MDintent = new Intent(this, MonitorDrowsiness.class);

        if(openedEAR == 0 || closedEAR == 0){//둘 중 하나라도 설정이 안 되어 있다면
            showpopup();
        }

        if(plz.size() == 0){
            Async_getDoc a = new Async_getDoc();
            a.execute();
        }
        
        media = MediaPlayer.create(this, R.raw.alarm);

        startService(MDintent);

        gotoInit = findViewById(R.id.GotoInitpage);
        gotoInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(MDintent);
                SetDrowsinessState.resetState();
                startActivity(new Intent(getApplicationContext(), ChooserActivity.class));
                tts.shutdown();
                finish();
            }
        });
    }

    static void CallingpopupinDD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("전화 대기중!")
                .setMessage("15초 안에 취소를 누르지 않으면 전화를 겁니다.")
                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getCallStatus = true; //취소
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        CountDownTimer count15s = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                dialog.cancel();
                Log.e(TAG,"15초 경과: 팝업 종료");
            }
        }.start();
    }

    static void TTSpopupinDD(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("뉴스 브리핑 중")
                .setMessage("뉴스 브리핑을 취소하시려면 취소하기를 눌러 주세요.")
                .setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tts.stop();
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showpopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고")
                .setMessage("EAR값이 설정되지 않았습니다. 메인 화면으로 돌아갑니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        stopService(MDintent);
                        tts.shutdown();
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        Log.i(TAG, "Using Face Detector Processor");
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor());
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return false;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return true;
    }

    protected void onDestroy(){
        if(initpd != null) initpd.dismiss();
        stopService(MDintent);
        tts.shutdown();
        stopService(intent);
        bt.stopService(); //블루투스 중지
        super.onDestroy();
    }

    public static void SetImageYellow(){
        imageview.setImageResource(R.drawable.yellowcircle);
    }

    public static void SetImageGreen(){
        imageview.setImageResource(R.drawable.greencircle);
    }

}
