package com.e.ewhazp.facedetector;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.e.ewhazp.ChooserActivity;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
class noticetoDriver {
    private static boolean isAlertRinging = false;
    static MediaPlayer media;
    static ProgressDialog pd=DetectingDrowsiness.initpd;
    //private static final int PERMISSIONS_REQUEST_READ_CONTACTS=100;
    static List<String> newsarray = ChooserActivity.plz;
    //이 밑으로 기사 읽어오는 tts 기능을 위한 변수들
    private static Elements contents;
    private static Document doc = null;
    private static Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
    private final static String nowdate_s=nowdate.format(date);
    private final static String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;
    private static List<String> plz = new ArrayList<>();
    private static final String TAG = "noticetoDriver";
    //알람 모아놓기
    static void ringingAlert(MediaPlayer media){
        Log.e(TAG, "ringingAlert");
        if(!isAlertRinging) { //현재 알람이 울리고 있지 않다면
            isAlertRinging = true;
            //소리알람 구현
            media.start();
            Log.e(TAG,"ringringringring~~");
            CountDownTimer count10s = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    isAlertRinging = false;
                }
            }.start();
        }
        else{
            Log.e(TAG,"Alert is ringing and the request is rejected ");
        }
        Log.e(TAG, "ringingAlert");
        //사용자가 설정한 소리 알람 중 하나를 무작위로 재생
    }
    static void randomCalling(final TextToSpeech tts){ //무작위로 전화하기 on일 때
        Log.e(TAG, "randomCalling");
        //IninPersonalSetting에서 정보 받아옴
        //음성 알림: 위험합니다! 15초 이후 등록된 연락처에서 무작위로 전화를 겁니다.
        String startcalling = "위험합니다! 15초 이후 등록된 연락처에서 무작위로 전화를 겁니다.";
        tts.speak(startcalling,TextToSpeech.QUEUE_ADD, null);
        tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
        //15초 대기 시작 countdowntimer()
        //onFinish(): 50%의 확률로 전화걸기 or 꽝
        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();
    }
    static void newstts(final TextToSpeech tts){ //은진언니가 구현
        Log.e(TAG,"newstts");
        //음성 알림: 많이 졸리신가요? 오늘 사람들이 가장 많이 본 뉴스를 읽어드릴게요.
        //뉴스 읽어주기 시작, 중간에 트리거 넣어서 중단될 수 있도록
        String starting_annae = "많이 졸리신가요? 오늘 올라온 정치기사를 읽어드릴게요.";
        tts.speak(starting_annae, TextToSpeech.QUEUE_ADD, null);
        tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
        AsyncTask t = new AsyncTask() {//AsyncTask객체 생성
            @Override
            protected Object doInBackground(Object[] params) {
                Log.e(TAG,"isitRunning?");
                for(int i=0;i<newsarray.size();i++){
                    tts.speak(newsarray.get(i),TextToSpeech.QUEUE_ADD, null);
                    tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
                }
                return null;
            }
        };
        t.execute();
    }
    static class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = ProgressDialog.show(DetectingDrowsiness.mcontext, "Loading Contacts",
                    "Please Wait");
        }
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<>();
            Cursor c = DetectingDrowsiness.mcontext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            assert c != null;
            while (c.moveToNext()) {
                String phNumber = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(phNumber);
            }
            Collections.sort(contacts);
            c.close();
            return contacts;
        }
        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);
            pd.cancel();
            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            Random random = new Random();
            //random하게 전화걸기
            int rand = random.nextInt(contacts.size()-1)+1;
            callIntent.setData(Uri.parse("tel:" + contacts.get(rand)));
            if (ActivityCompat.checkSelfPermission(DetectingDrowsiness.mcontext.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            DetectingDrowsiness.mcontext.startActivity(callIntent);
            if(pd!=null)
                pd.dismiss();
        }
    }
}