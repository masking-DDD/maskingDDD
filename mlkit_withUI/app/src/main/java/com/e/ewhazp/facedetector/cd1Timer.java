package com.e.ewhazp.facedetector;

import android.os.CountDownTimer;
import android.util.Log;

import static com.e.ewhazp.facedetector.SetDrowsinessState.EAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.closedEAR;
import static com.e.ewhazp.facedetector.SetDrowsinessState.setState;

public class cd1Timer extends CountDownTimer {
    private static final String TAG = "cd1Timer";


    private static boolean count1sisRunning = false;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */

    private cd1Timer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    private static class cd1TimerHolder{
        private static final cd1Timer HCD_1_TIMER = new cd1Timer(1000,30);
    }

    public boolean isCount1sisRunning(){
        return count1sisRunning;
    }

    public static cd1Timer getInstancecd1(){
        return cd1TimerHolder.HCD_1_TIMER;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        count1sisRunning = true;
        if(!(EAR < closedEAR)){
            count1sisRunning = false;
            cancel();
        }
    }

    @Override
    public void onFinish() {
        Log.e(TAG,"cd1Timer onFinish");
        setState(4); //1초 동안 눈을 감고 있으면 4단계 진입
        count1sisRunning = false;
    }
}
