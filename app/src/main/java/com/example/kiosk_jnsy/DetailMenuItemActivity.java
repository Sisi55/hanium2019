package com.example.kiosk_jnsy;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kiosk_jnsy.databinding.ActivityDetailMenuItemBinding;
import com.example.kiosk_jnsy.model.CafeItem;

public class DetailMenuItemActivity extends AppCompatActivity {


    ActivityDetailMenuItemBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_menu_item);

        // 해시 키값 알면 해당 데이터 가져올 수 있나 ?
        // 테스트

        // 지연 : 인텐트 테스트/////
        CafeItem model=(CafeItem)getIntent().getSerializableExtra("detail");
        Toast.makeText(DetailMenuItemActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(DetailMenuItemActivity.this, model.getPrice()+"", Toast.LENGTH_SHORT).show();
        Log.d("이미지",model.getImageUrl());
        Log.d("이미지",model.getBody());
        // 지연 : 인텐트 테스트/////


    }//end onCreate



}//end Activity
