package com.e.ewhazp.facedetector;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ewhazp.preprocessing.CameraSource;
import com.e.ewhazp.preprocessing.CameraSourcePreview;
import com.e.ewhazp.preprocessing.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SetopenedearActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String TAG = "SetopenedearActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final String FACE_DETECTION = "Face Detection";
    public Button againbtn;
    public Button confirmbtn;


    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setopenedear);

        preview = findViewById(R.id.firePreview);
        if (preview == null) {
            Log.e(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        if (graphicOverlay == null) {
            Log.e(TAG, "graphicOverlay is null");
        }

        if (allPermissionsGranted()) {
            createCameraSource(FACE_DETECTION);
            startCameraSource();
        } else {
            getRuntimePermissions();
        }

        SetDrowsinessState.setOpenedEAR(0);
        final CountDownTimer CalopendEAR = new CountDownTimer(3300,300) {

            double sum = 0;
            int num = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if(SetDrowsinessState.EAR != 0){
                    num++;
                    sum += SetDrowsinessState.EAR;
                }

                Log.e(TAG,"opendEAR\nEAR: "+SetDrowsinessState.EAR+"num: "+num);
            }

            @Override
            public void onFinish() {
                Log.e(TAG,"OpenedEAR/num = "+sum/num);
                SetDrowsinessState.setOpenedEAR((sum/num)*0.9); //EAR 평균값에 오차범위 +-10%
                Log.e(TAG,"OpenedEAR set: "+SetDrowsinessState.openedEAR);
                showpopup();
            }
        };

        CountDownTimer delay2s = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                CalopendEAR.start();
            }
        }.start();

        //btn events
        //다시 설정하기
        againbtn = findViewById(R.id.Again);
        againbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onRestart();
            }
        });

        //제대로 설정됨
        confirmbtn = findViewById(R.id.Confirm);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SetclosedearActivity.class));
                finish();
            }
        });
    }

    private void showpopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내")
                .setMessage("측정이 완료되었습니다. : "+SetDrowsinessState.openedEAR+"\n확인을 눌러주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void createCameraSource(String model) {
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
            createCameraSource(FACE_DETECTION);
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
}
