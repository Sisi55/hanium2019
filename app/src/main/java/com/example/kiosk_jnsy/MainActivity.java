package com.example.kiosk_jnsy;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.kiosk_jnsy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



        // 메뉴판 클릭하면 화면 이동
        binding.btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
                startActivity(intent);
            }
        });


        // 주문기록 클릭하면 화면 이동
        binding.btnOrderedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderedListActivity.class);
                startActivity(intent);
            }
        });



    }//end onCreate



}//end Activity
