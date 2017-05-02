package com.example.coms7.threadmessage;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;

public class MapActivity extends FragmentActivity {

    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "mePRJxFXCilrYbvJINuX";// 애플리케이션 클라이언트 아이디 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mMapView = new NMapView(this);
//        setContentView(mMapView);
//        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
//        mMapView.setClickable(true);
//        mMapView.setEnabled(true);
//        mMapView.setFocusable(true);
//        mMapView.setFocusableInTouchMode(true);
//        mMapView.requestFocus();
        setContentView(R.layout.activity_map);
        Fragment1 fragment1 = new Fragment1();
        fragment1.setArguments(new Bundle());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHere, fragment1);
        fragmentTransaction.commit();
    }
}
