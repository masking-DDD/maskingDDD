package com.e.ewhazp.facedetector;

import android.os.CountDownTimer;
import android.util.Log;

import static com.e.ewhazp.facedetector.SetDrowsinessState.EAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.closedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.openedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.setState;
import static com.e.ewhazp.facedetector.SetDrowsinessState.state;


public class cd3Timer extends CountDownTimer {
    private static final String TAG = "cd3Timer";
    private boolean cd3locked = false;

    static private int counttimes = 0;
    private static boolean count3sisRunning = false;


    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */

    private cd3Timer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    private static class cd3TimerHolder{
        private static final cd3Timer HCD_3_TIMER = new cd3Timer(3000,30);
    }

    public static cd3Timer getInstancecd3(){
        return cd3TimerHolder.HCD_3_TIMER;
    }
    public boolean isCount3sisRunning(){
        return count3sisRunning;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        count3sisRunning = true;
        if(!((EAR<openedEAR)&&(EAR>closedEAR))){ //매 틱마다 검사
            count3sisRunning = false;
            cancel();
        }
    }

    @Override
    public void onFinish() {
        Log.e(TAG,"cd3Timer onFinish");
        if(!cd3locked){ //5초 덜지남 cd3locked 구현
            counttimes++;
            if(state == 1) setState(2); //1단계에서 게슴츠레하게 뜬 상태로 3초 지속 시 2단계 진입, 3번 반복하면 3단계 진입
            else if((state==2)&&(counttimes == 3)){ //2단계&&3번 울림
                setState(3); //3단계 진입
                counttimes = 0; //count 초기화
            }
            else if(counttimes == 3){ //3단계 이상&&3번 울림
                setState(4); //4단계 진입
                counttimes = 0; //count 초기화
            }
            count3sisRunning = false;
        }
    }
}
