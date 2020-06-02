package com.e.ewhazp.facedetector;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;

import static com.e.ewhazp.facedetector.DetectingDrowsiness.tts;
import static com.e.ewhazp.facedetector.DetectingDrowsiness.media;


//EAR 값을 통해 졸음 상태 분석
//운전자의 상태에 따라 알맞은 알람 기능 호출
class SetDrowsinessState{
    private static final String TAG = "SetDrowsinessState";
    @SuppressLint("StaticFieldLeak")
    static int state = 1;
    private static int countnum = 0;
    private static boolean isRunning = false;
    //static ProgressDialog pd=DetectingDrowsiness.initpd;

    static double EAR = 0;
    static double openedEAR = 0;
    static double closedEAR = 0;
    static void setEARvalue(double ear) {
        SetDrowsinessState.EAR = ear;
    }
    static void setOpenedEAR(double openedEAR) {
        SetDrowsinessState.openedEAR = openedEAR;
    }
    static void setClosedEAR(double closedEAR) {
        SetDrowsinessState.closedEAR = closedEAR;
    }
    private static void reduceState(){state=-1;}

    //setState가 실행되면 timer 시작, 호출될 때마다 5분 리셋. 5분마다 reduceState() 실행
    private static CountDownTimer count5m = new CountDownTimer(300000,5000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if(state>1) {
                reduceState();
                if(state>1) count5m.start();
            }
        }
    };

    private static CountDownTimer count15s = new CountDownTimer(15000,5000) {//
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (isRunning) {
                isRunning = false;
            }
        }
    };
    private static CountDownTimer wait5sandTTS = new CountDownTimer(5000,1000) { //소리알람 다음 tts 기능 실행
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            noticetoDriver.newstts(tts);
        }
    };
    private static CountDownTimer wait5sandRC = new CountDownTimer(5000,1000) { //소리알람 다음 tts 기능 실행
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            noticetoDriver.randomCalling(tts);
        }
    };


    synchronized static void setState(final int st) {
        //state 1에서 countnum을 셀 수 있도록 변경함 05261839
        state = st;
        count5m.start(); //5분 count 시작
        if(count5m != null){ //count5m이 이미 실행중이었다면 취소하고 다시 시작
            Log.e(TAG,"count5m != null, count5m 재실행");
            count5m.cancel();
            count5m.start();
        }

        if(!isRunning){ //알람 기능 실행중이 아님
            if(count15s != null) count15s.cancel();
            Log.e(TAG,"state: "+st);
            switch (state){
                case 1: //맨 처음 시작했을 때(cd3Timer 앱 실행 이후 첫 번째 실행시)
                    Log.e(TAG,"first cd3Timer onFinished");
                    state = 2;
                    noticetoDriver.newstts(tts);
                    //noticetoDriver.ringingAlert(media); //소리알람 울림
                    countnum++;
                case 2: //2단계 진입
                    if(countnum != 3) countnum++; //3번까지는 괜찮다
                    else{ //3단계 진입
                        state = 3;
                        countnum = 0;
                        noticetoDriver.ringingAlert(media);
                        wait5sandTTS.start();
                    }
                    noticetoDriver.ringingAlert(media); //소리알람 울림
                    break;
                case 3:
                    if(countnum <= 3) countnum++; //3번까지는 괜찮다
                    else{
                        countnum = 0;
                        state = 4;
                        noticetoDriver.ringingAlert(media);
                        wait5sandTTS.start();
                    }
                    noticetoDriver.ringingAlert(media);
                    wait5sandTTS.start();
                    break;
                case 4:
                    noticetoDriver.ringingAlert(media);
                    wait5sandRC.start();
                    break;
                default:
                    break;
            }
            isRunning = true;
            count15s.start(); //15초 다시 셈, onFinish에서 알람 기능 실행중이 아니라고 설정, setState 호출시 다시 실행됨
        }
    }

    static void resetState(){
        state = 0;
        countnum = 0;
    }
}