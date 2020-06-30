package com.e.ewhazp.facedetector;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import static com.e.ewhazp.facedetector.DetectingDrowsiness.tts;
import static com.e.ewhazp.facedetector.noticetoDriver.plz;


class Async_TTS extends AsyncTask<Void, Void,Void> {
    private static final String TAG = "Async_TTS";

    @Override
    protected Void doInBackground(Void... voids) {
        for(int i=0;i<plz.size();i++){
            tts.speak(plz.get(i), TextToSpeech.QUEUE_ADD, null);
            tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
            if(isCancelled()) {
                Log.e(TAG,"Async_TTS isCancelled");
                break;
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
