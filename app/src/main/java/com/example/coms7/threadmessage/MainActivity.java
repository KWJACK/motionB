package com.example.coms7.threadmessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int mainValue = 0;
    int backValue = 0;
    int state = 0;//0이면 랜덤 값 생성, 1이면 생성치 않음
    TextView mainText;
    TextView backText;
    String [] alphabet={"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    Button [] myBtn= new Button[3];
    Intent intentSubActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainText = (TextView)findViewById(R.id.mainvalue);
        backText = (TextView)findViewById(R.id.backvalue);
        myBtn[0] = (Button)findViewById(R.id.button);
        myBtn[1] = (Button)findViewById(R.id.button2);
        myBtn[2] = (Button)findViewById(R.id.button3);

        // ToDo 버튼 클릭리스너에서는 할 행동을 정의해야함. 버튼은 기본적으로 gone속성으로 눈에 보이지않고, 자리 차지도 안함
        myBtn[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intentSubActivity = new Intent(MainActivity.this, MapActivity.class);
                startActivityForResult(intentSubActivity, 2);
            }
        });
        myBtn[1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        myBtn[2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               finishActivity(2);   // 2번 액티비티(MAP 삭제)

            }
        });

        // 스레드 생성하고 시작
        WriteThread thread_write = new WriteThread();
        ReadThread thread_read = new ReadThread();
        thread_write.setDaemon(true);
        thread_write.start();

        thread_read.setDaemon(true);
        thread_read.start();
    }

    public void mOnClick(View v){
        mainValue++;
        mainText.setText("MainValue:" + mainValue);
    }

    class WriteThread extends Thread{   //ToDO 나중에 UDP 입력을 받고, 여기서 액션을 걸러내야함
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(true){
                if(state==0){
                    backValue++;
                    state=1;
                    // 메인에서 생성된 Handler 객체의 sendEmpryMessage 를 통해 Message 전달
                    handler1.sendEmptyMessage(0);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } // end while
        } // end run()
    } // end class BackThread

    class ReadThread extends Thread{    //ToDO WriteThread에서 액션을 주면 핸들러에서 작업. 버튼을 트리거시킴
        @Override
        public void run() {
            while(true){
                if(state==1){
                    state=0;
                    // 메인에서 생성된 Handler 객체의 sendEmpryMessage 를 통해 Message 전달
                    handler2.sendEmptyMessage(0);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } // end while
        } // end run()
    } // end class BackThread

    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {    //ToDo 스레드에서 UI스레드로의 접근은 핸들러를 통해서만 가능함
            if(msg.what == 0){   // Message id 가 0 이면
                backText.setText("Write에서 랜덤 생성값:" + backValue); // 메인스레드의 UI 내용 변경
            }
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {    //ToDo
            if(msg.what == 0){   // Message id 가 0 이면
                backText.setText("Read에서 매칭한 값:" + alphabet[backValue%10]); // 메인스레드의 UI 내용 변경
                myBtn[backValue%3].performClick();

                //음량조절
                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);   //현재 미디어 볼륨 값 수신
                am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / backValue, 0);    //미디어 볼륨 조절
            }
        }
    };

}
