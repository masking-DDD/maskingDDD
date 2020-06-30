package com.e.ewhazp.facedetector;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.e.ewhazp.facedetector.noticetoDriver.plz;

public class Async_getDoc extends AsyncTask<Void,Void,Void> {
    private static Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
    private final static String nowdate_s=nowdate.format(date);
    private final static String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;
    private static final String TAG = "Asycn_getDoc";
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements contents = doc.select("div[class=\"list_body newsflash_body\"]");
            for(Element e: contents.select("dt")){
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
}
