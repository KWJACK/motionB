package com.example.coms7.wifidirect;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity  {

    private Socket socket;
    private static final String SERVER_IP = "192.168.0.20";
    private EditText textField;
    private Button button;
    private TextView textView;
    private Socket client;
    private PrintWriter printwriter;
    private BufferedReader bufferedReader;

    int i = 0;
    ImageView imageview;
    String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);

        imageview = (ImageView)findViewById(R.id.imageView);

        ChatOperator chatOperator = new ChatOperator();
        chatOperator.execute();
    }

    private class ChatOperator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client = new Socket(SERVER_IP, 8090); // Creating the server socket.

                if (client != null) {
                    //자동 flushing 기능이 있는 PrintWriter 객체를 생성한다.
                    //client.getOutputStream() 서버에 출력하기 위한 스트림을 얻는다.
                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());

                    //입력 스트림 inputStreamReader에 대해 기본 크기의 버퍼를 갖는 객체를 생성한다.
                    bufferedReader = new BufferedReader(inputStreamReader);
                } else {
                    System.out.println("Server has not bean started on port 9999.");
                }
            } catch (UnknownHostException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Faild to connect server " + SERVER_IP);
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Following method is executed at the end of doInBackground method.
         */
        @Override
        protected void onPostExecute(Void result) {

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    i=1-i;

                    if ( i == 1 ) {
                        imageview.setImageResource(R.drawable.a);
                        button.setText("Turn Off");
                        str="1";
                    }
                    else {
                        imageview.setImageResource(R.drawable.b);
                        button.setText("Turn On");
                        str="2";
                    }


                    final Sender messageSender = new Sender(); // Initialize chat sender AsyncTask.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        messageSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
                    } else {
                        messageSender.execute(str);
                    }
                }
            });
        }
    }

    /**
     * This AsyncTask sends the chat message through the output stream.
     */
    private class Sender extends AsyncTask<String, String, Void> {

        private String message;

        @Override
        protected Void doInBackground(String... params) {
            message = params[0];

            //문자열을 스트림에 기록한다.
            printwriter.write(message + "\n");

            //스트림을 플러쉬한다.
            printwriter.flush();

            return null;
        }

        //클라이언트에서 입력한 문자열을 화면에 출력한다.
        @Override
        protected void onPostExecute(Void result) {

        }
    }

}
