package com.e.ewhazp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.e.ewhazp.facedetector.DetectingDrowsiness;
import com.e.ewhazp.facedetector.SetopenedearActivity;
import com.google.firebase.samples.apps.mlkit.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

/**
 * Demo app chooser which takes care of runtime permission requesting and allows you to pick from
 * all available testing Activities.
 */
public final class ChooserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemClickListener {
    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;
    public Button startButton;
    public Button setDrivebtn;
    //for crawling
    private static Elements contents;
    static TextToSpeech tts;

    private static Document doc = null;
    private static Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
    private final static String nowdate_s=nowdate.format(date);
    private final static String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;
    public static List<String> plz = new ArrayList<>();
    //public Button bluetoothBtn;
    //class 목록
    private static final Class<?>[] CLASSES =
            //메인화면에서 기능 선택할 때 액티비티 클래스들을 여기다가 넣으면 됨
            new Class<?>[]{
                    SetopenedearActivity.class,
                    DetectingDrowsiness.class,
                    BluetoothTest.class
            };
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        startButton = findViewById(R.id.start);
        setDrivebtn = findViewById(R.id.setbeforeDrive);
        //bluetoothBtn = findViewById(R.id.btnBlue);
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

        AsyncTask a = new AsyncTask() {//AsyncTask객체 생성
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    doc = Jsoup.connect(url).get();
                    contents = doc.select("div[class=\"list_body newsflash_body\"]");
                    for(Element e:contents.select("dt")){
                        if(e.className().equals("photo")){
                            continue;
                        }
                        Log.e(TAG,e.text());
                        plz.add(e.text());
                    }
                    Log.e(TAG,"plz has done: "+plz.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        a.execute();
        //연결버튼 눌러서 블루투스 연결
        //bluetoothBtn.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        //co2 bluetooth test 실행
        //        Class<?> clicked = CLASSES[2];
        //       startActivity(new Intent(getApplicationContext(), clicked));
        //  }
        //});
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
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