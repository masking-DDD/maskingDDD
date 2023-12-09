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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.e.ewhazp.facedetector.BTActivity.mcontext;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.CallingpopupinDD;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.TTSpopupinDD;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.getCallStatus;

class noticetoDriver {
    private static boolean isAlertRinging = false;
    private static ProgressDialog pd = DetectingDrowsiness.initpd;
    static LoadContactsAyscn lca = new LoadContactsAyscn();

    //이 밑으로 기사 읽어오는 tts 기능을 위한 변수들
    static Async_TTS t = new Async_TTS();
    @SuppressLint("SimpleDateFormat")
    static List<String> plz = new ArrayList<>();
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
        CallingpopupinDD();
        CountDownTimer count15s = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(getCallStatus){
                    getCallStatus = false;
                    Log.e(TAG,"전화 취소");
                    cancel();
                }
            }
            @Override
            public void onFinish() {
                lca.execute();
                getCallStatus = false;
            }
        }.start();

    }

    static void newstts(final TextToSpeech tts){ //은진언니가 구현
        Log.e(TAG,"newstts");
        //음성 알림: 많이 졸리신가요? 오늘 사람들이 가장 많이 본 뉴스를 읽어드릴게요.
        //뉴스 읽어주기 시작, 중간에 트리거 넣어서 중단될 수 있도록
        String starting_annae = "많이 졸리신가요? 오늘 올라온 정치기사를 읽어드릴게요.";
        tts.speak(starting_annae, TextToSpeech.QUEUE_ADD, null);
        tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);

        TTSpopupinDD();
        t.execute();
    }

    static class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = ProgressDialog.show(mcontext, "Loading Contacts",
                    "Please Wait");
        }
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<String> contacts = new ArrayList<>();
            Cursor c = mcontext.getContentResolver().query(
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
            super.onPostExecute(contacts);
            pd.cancel();
            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            Random random = new Random();
            //random하게 전화걸기
            int rand = random.nextInt(contacts.size()-1)+1;
            callIntent.setData(Uri.parse("tel:" + contacts.get(rand)));
            if (ActivityCompat.checkSelfPermission(mcontext.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mcontext.startActivity(callIntent);
            if(pd!=null)
                pd.dismiss();
        }
    }
}