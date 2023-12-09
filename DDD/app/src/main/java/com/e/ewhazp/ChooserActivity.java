package com.e.ewhazp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.ewhazp.facedetector.BTActivity;
import com.e.ewhazp.facedetector.DetectingDrowsiness;
import com.e.ewhazp.facedetector.SetopenedearActivity;
import com.google.firebase.samples.apps.mlkit.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Demo app chooser which takes care of runtime permission requesting and allows you to pick from
 * all available testing Activities.
 */
public final class ChooserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;
    public Button startButton;
    public Button setDrivebtn;
    public Button btbtn;

    private static final Class<?>[] CLASSES =
            new Class<?>[]{
                    SetopenedearActivity.class,
                    DetectingDrowsiness.class,
                    BTActivity.class
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.start);
        setDrivebtn = findViewById(R.id.setbeforeDrive);
        btbtn = findViewById(R.id.BTbtn);

        setDrivebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[0];
                startActivity(new Intent(getApplicationContext(), clicked));
            }
        });
        //졸음 감시
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[1];
                startActivity(new Intent(getApplicationContext(), clicked));
            }
        });
        btbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[2];
                startActivity(new Intent(getApplicationContext(), clicked));

            }
        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
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


