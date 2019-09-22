package com.example.kiosk_jnsy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

public class RecomMenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView1;
    TextView textView1_price;
    TextView textView2;
    TextView textView2_price;
    ImageView imageView1;
    ImageView imageView2;

    @Override
    public void onClick(View v) {
        // 클릭하면 인텐트
        if(v==textView1 || v==textView1_price || v==imageView1){
            Intent intent = new Intent(RecomMenuActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item1);
            startActivity(intent);

        }else if(v==textView2 || v==textView2_price || v==imageView2){
            Intent intent = new Intent(RecomMenuActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item1);
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom_menu);

        // 파싱해서 출력  tv_item2_name   tv_item2_price
        textView1 = (TextView) findViewById(R.id.tv_item1_name);
        textView1_price = (TextView) findViewById(R.id.tv_item1_price);
        textView2 = (TextView) findViewById(R.id.tv_item2_name);
        textView2_price = (TextView) findViewById(R.id.tv_item2_price);

        imageView1 = (ImageView) findViewById(R.id.imageview1);
        imageView2 = (ImageView) findViewById(R.id.imageview2);

        // 클릭 이벤트 tv이름, tv가격, 이미지 누르면 전부
        textView1.setOnClickListener(this);
        textView1_price.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView2_price.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);


        print_xgbRecom_result();
        print_CFRecom_result();

    }

    CafeItem item1;
    private void print_xgbRecom_result() {

        String xgb_json = AppSetting.response_xgb_personalize;

        try {

            JSONObject jsonObj = new JSONObject(xgb_json);
            String xgb_result_itemName = jsonObj.get("item").toString(); // 메뉴 이름

            FirebaseFirestore.getInstance().collection("menu")
                    .whereEqualTo("name", xgb_result_itemName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) { // document.toObject(Order.class)
//                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    // 쿼리 가져온거 반복
                                    item1 = document.toObject(CafeItem.class);
                                    textView1.setText(item1.getName());
                                    textView1_price.setText(item1.getPrice()+"");
                                    Glide.with(RecomMenuActivity.this)
                                            .load(item1.getImageUrl())
                                            .into(imageView1);

                                }
                            }
                        }
                    });

//            textView1.setText(xgb_result_itemName );

        } catch (Exception e) {
            Log.e(" recom activity", e.getMessage());
        }

    }

    CafeItem item2;
    private void print_CFRecom_result() {

        String cf_json = AppSetting.response_CF_overall;

        try {

            JSONObject jsonObj = new JSONObject(cf_json);
            String cf_result_itemName  = jsonObj.get("user_cf").toString();

            FirebaseFirestore.getInstance().collection("menu")
                    .whereEqualTo("name", cf_result_itemName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) { // document.toObject(Order.class)
                                    Log.d("  타추천-item2", document.getId() + " => " + document.getData());

                                    // 쿼리 가져온거 반복
                                    item2 = document.toObject(CafeItem.class);
                                    textView2.setText(item2.getName());
                                    textView2_price.setText(item2.getPrice()+"");
                                    Glide.with(RecomMenuActivity.this)
                                            .load(item2.getImageUrl())
                                            .into(imageView2);

                                }
                            }
                        }
                    });

        }catch (Exception e){
            Log.e(" recom activity", e.getMessage());
        }

    }
}