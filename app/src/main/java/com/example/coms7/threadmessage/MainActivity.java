package com.example.coms7.threadmessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    int mainValue = 0;
    int backValue = 0;
    int state = 0;//0이면 랜덤 값 생성, 1이면 생성치 않음
    TextView mainText;
    TextView backText;
    String [] alphabet={"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    Button [] myBtn= new Button[3];
    Intent intentSubActivity;
    PhoneStateCheckListener phoneCheckListener;
    private Context mCtx;
    private MediaSessionManager mSessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        phoneCheckListener = new PhoneStateCheckListener(this);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneCheckListener, PhoneStateListener.LISTEN_CALL_STATE);

        mainText = (TextView)findViewById(R.id.mainvalue);
        backText = (TextView)findViewById(R.id.backvalue);
        myBtn[0] = (Button)findViewById(R.id.button);
        myBtn[1] = (Button)findViewById(R.id.button2);
        myBtn[2] = (Button)findViewById(R.id.button3);

        // ToDo 버튼 클릭리스너에서는 할 행동을 정의해야함. 버튼은 기본적으로 gone속성으로 눈에 보이지않고, 자리 차지도 안함
        myBtn[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                intentSubActivity = new Intent(MainActivity.this, MapActivity.class); // 예제로 네이버 지도를 다른 액티비티에서 띄워보기
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

//    public void mOnClick(View v){
//        mainValue++;
//        mainText.setText("MainValue:" + mainValue);
//    }

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


    public class PhoneStateCheckListener extends PhoneStateListener {   //전화오면 번호띄우는거까지댐
        MainActivity mainActivity;
        PhoneStateCheckListener(MainActivity _main){
            mainActivity = _main;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                Toast.makeText(mainActivity,"STATE_IDLE : Incoming number "
                        + incomingNumber,Toast.LENGTH_SHORT).show();
            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                Toast.makeText(mainActivity,"STATE_RINGING : Incoming number "
                        + incomingNumber,Toast.LENGTH_SHORT).show();//수신 부분 입니다.

//                interruptCall interrupt = new interruptCall();    //ToDo 전화오면 수신.. 안되무
//                interrupt.start();

            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Toast.makeText(mainActivity,"STATE_OFFHOOK : Incoming number "
                        + incomingNumber,Toast.LENGTH_SHORT).show();
            }
        }

//        public class interruptCall extends Thread{
//            public void run(){
//                try{
//                    sleep(1000);
//                }catch(InterruptedException e){
//                    e.printStackTrace();
//                }
//                try
//                {
////                    acceptCall();
////                    Intent new_intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
////                    new_intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
////                    mainActivity.sendOrderedBroadcast(new_intent, null);
////                    new_intent = null;
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                try
//                {
//                    Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
//                    buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
//                    mainActivity.sendOrderedBroadcast(buttonUp, null);
//                    buttonUp = null;
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
    }



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


//    private Method getMethodDispatchMediaKey() { http://sherl.tistory.com/43
//        mSessionManager = (MediaSessionManager) mCtx.getSystemService(Context.MEDIA_SESSION_SERVICE);
//        Method m = null;
//        try {
//            m = Class.forName(mSessionManager.getClass().getName()).getDeclaredMethod("dispatchMediaKeyEvent", KeyEvent.class);
//            m.setAccessible(true);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return m;
//    }
//
//    public void acceptCall(){
//        try {
//            getMethodDispatchMediaKey().invoke(mSessionManager, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
//            getMethodDispatchMediaKey().invoke(mSessionManager, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }




}
