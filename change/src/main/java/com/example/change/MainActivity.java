package com.example.change;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.change.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 첫번째 Fragment가 출력되도록 초기화 : 메뉴판 메인 화면
        getSupportFragmentManager().beginTransaction()
//                .addToBackStack(null)
                .add(R.id.body, MainFragment.getFragment())
                .commit();

        // camera
        getSupportFragmentManager().beginTransaction()
//                .addToBackStack(null)
                .replace(R.id.head, Camera2BasicFragment.newInstance())
                .commit();
    }//end onCreate
}//end Activity
