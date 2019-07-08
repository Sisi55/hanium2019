package com.example.kiosk_jnsy;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kiosk_jnsy.databinding.ActivityDetailMenuItemBinding;

public class DetailMenuItemActivity extends AppCompatActivity {


    ActivityDetailMenuItemBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_menu_item);

        // 해시 키값 알면 해당 데이터 가져올 수 있나 ?
        // 테스트




    }//end onCreate



}//end Activity
