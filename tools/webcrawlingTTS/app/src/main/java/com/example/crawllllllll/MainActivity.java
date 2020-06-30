package com.example.crawllllllll;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;

public class MainActivity extends AppCompatActivity {
    TextView textView; //결과를 띄어줄 TextView
    TextView reload; //reload버튼
    Elements contents;
    Document doc = null;
    String Top10;//결과를 저장할 문자열변수
    private List<String> plz = new ArrayList<>();
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textBox);
        reload = (TextView) findViewById(R.id.reload);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {//onclicklistener를 연결하여 터치시 실행됨
            Date date = new Date();
            SimpleDateFormat nowdate = new SimpleDateFormat("yyyyMMdd");
            String nowdate_s=nowdate.format(date);
            String url = "https://news.naver.com/main/list.nhn?mode=LS2D&mid=shm&sid2=269&sid1=100&date="+nowdate_s;

            @Override
            public void onClick(View v) {
                new AsyncTask() {//AsyncTask객체 생성
                    @Override
                    protected Object doInBackground(Object[] params) {

                        try {
                            doc = Jsoup.connect(url).get();
                            contents = doc.select("div[class=\"list_body newsflash_body\"]");
                            //doc = Jsoup.connect("https://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=100&date=").get(); //naver페이지를 불러옴
                            for(Element e:contents.select("dt")){
                                if(e.className().equals("photo")){
                                    continue;
                                }
                                System.out.println(e.text());
                                plz.add(e.text());
                            }

                            for(int i=0;i<plz.size();i++){
                                tts.speak(plz.get(i),TextToSpeech.QUEUE_ADD, null);
                                tts.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        Log.i("TAG",""+Top10);
                        textView.setText(Top10);
                    }
                }.execute();
            }
        });
    }
}

/*public class MainActivity extends AppCompatActivity {

    private Button getBtn;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.result);
        getBtn = (Button) findViewById(R.id.getBtn);

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebsite();
            }
        });

    }
    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder sb = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://www.naver.com").get();
                    System.out.println("여기까지 들어왔습니다");
                    String title = doc.title();
                    Elements links = doc.select("a[href]");

                    sb.append(title).append("\n");
                } catch (IOException e) {
                    sb.append("error");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(sb.toString());
                    }
                });
            }

        }).start();
    }
}
*/

