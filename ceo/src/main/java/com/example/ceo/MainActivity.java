package com.example.ceo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // 지연 : 메뉴등록 버튼
    Button registerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 지연 : 메뉴등록 버튼을 누르면 intent로 화면 이동
        registerbtn=(Button)findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
