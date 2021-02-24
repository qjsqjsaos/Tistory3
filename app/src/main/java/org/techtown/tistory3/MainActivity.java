package org.techtown.tistory3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    private Thread timeThread = null;
    private final Boolean isRunning = true;

    private TextView textTime, textDate;

    private Button checkBtn;

    private long finallyDate;
    private long finallyTime;

    private Calculate_Date calculate_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBtn = findViewById(R.id.checkBtn); //시간 계산


        calculate_date = new Calculate_Date(); //Calculate_Date 객체 만들기

        textDate = findViewById(R.id.textView_date); //날짜결과 나타내는 텍스트뷰
        textTime = findViewById(R.id.textView_time); //시간결과 나타내는 텍스트뷰

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeNow = calculate_date.WhatTimeIsItTime(); //현재 시간
                String myTime = "12:30:00";// 지정한 시간 //HH:mm:ss 형식으로 적어줘야한다.(원하면 Calculate_Date)에서 수정가능.

                String dateNow = calculate_date.WhatTimeIsItDate(); //현재 날짜
                String myDate = "2021-01-12"; //지정한 날짜 //yyyy-MM-dd 형식 수정가능


                try {
                    finallyTime = calculate_date.calTimeBetweenAandB(myTime, timeNow); //첫번째 인자는 지정한 시간이고, 두번 째는 현재시간이다.

                    finallyDate = calculate_date.calDateBetweenAandB(myDate, dateNow); //첫번째 인자는 지정한 날짜이고, 두번 째는 현재날짜이다.
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });



    }

    Handler handler = new Handler(Looper.myLooper()) { //실시간 날짜를 출력해주는 핸들러
        @Override
        public void handleMessage(Message msg) {

            //(msg.arg1 / 100) 이 1초이다. 1초는 1000밀리세컨드 이므로,
            //int min = (msg.arg1 / 100) / 60 같은 경우는 1/60이니까 분이다. (시간도 마찬가지)
            int sec = (msg.arg1 / 100) % 60; //초
            int min = (msg.arg1 / 100) / 60 % 60; //분
            int hour = (msg.arg1 / 100) / 3600 % 24; //시
            int day = (msg.arg2 / 100) / 86400; //하루

            String result = String.format("%02d:%02d:%02d", hour, min, sec);
            Log.d("리절트", result);

            String oneDay = String.format("%d", day);
            Log.d("원데이", oneDay);

            /** result 실시간 시간초이다.*/
            textTime.setText("오늘을 기준으로\n\n" + result + "버틴 시간"); //시간표시
            textDate.setText("오늘 기준으로" + oneDay + "일 지났습니다."); //날짜표시
        }
    };




    public class timeThread implements Runnable {
        //타이머 쓰레드
        @Override
        public void run() {
            int i = (int) finallyTime; //여기에 몇 초인지 넣어야 그 때부터 타이머가 시작된다.
            int day = (int) finallyDate; //여기에는 날짜를 넣는데, 마찬가지로 초 형식으로 넣는다.
            Log.d("뭐야", String.valueOf(i));
            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    msg.arg2 = day++;
                    handler.sendMessage(msg); //인자 넣기(
                    try {
                        Thread.sleep(10); //혹시나 멈췄을 경우를 대비해 0.01초마다 쓰레드실행
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}