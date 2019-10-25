package com.example.kiosk_jnsy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.model.RecomDTO;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.Locale;

public class RecomMenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView1;
    TextView textView1_price;
    TextView textView2;
    TextView textView2_price;
    ImageView imageView1;
    ImageView imageView2;
    TextToSpeech mTTS;

    @Override
    public void onClick(View v) {
        // 클릭하면 인텐트
        if(v==textView1 || v==textView1_price || v==imageView1){
            Intent intent = new Intent(RecomMenuActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item1);
            startActivity(intent);

        }else if(v==textView2 || v==textView2_price || v==imageView2){
            Intent intent = new Intent(RecomMenuActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item2);
            startActivity(intent);

        }
    }
    private void speak(String text){

        /*
      //  float speed=(float)mSeekBarSpeed.getProgress()/50;
      //  if(speed<0.1) speed=0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH,null);
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
            mTTS = null;
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

        // tts
//        playTTS();


    }

    void playTTS(){

        // tts
        String tts_recom;

        if(AppSetting.emotion.containsKey("happiness") || AppSetting.emotion.containsKey("neutral") || AppSetting.emotion.containsKey("surprise")){
            Log.e("   감정", "긍정");

            tts_recom= getResources().getString(R.string.recom_good_start) + // getResources().getString(R.string)
                    /*메뉴이름*/"" + // 위,아래 결정해야 해
                    getResources().getString(R.string.recom_good_end);
            Toast.makeText(RecomMenuActivity.this, getResources().getString(R.string.identify_good), Toast.LENGTH_SHORT).show();

        }else{
            Log.e("   감정", "부정");

            tts_recom= ""/*메뉴이름*/ + getResources().getString(R.string.recom_bad);
        }

        // tts 읽어주세용  tts_recom

    }

    void printRecom(String itemName){

        FirebaseFirestore.getInstance().collection("cre_menu"/*"menu"*/)
                .whereEqualTo("name", itemName) // 쿼리 조건
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
                                // 지연 : AppSetting에 추가
                                AppSetting.ttsRecoItem1=item1.getName();
                                textView1_price.setText(item1.getPrice()+"");
                                Glide.with(RecomMenuActivity.this)
                                        .load(item1.getImageUrl())
                                        .into(imageView1);

                            }
                            if(item2 != null){
                                writeRecomContent(item2.getName());
                            }

                        }
                    }
                });


    }


    CafeItem item1;
    private void print_xgbRecom_result() {

        String xgb_json = AppSetting.response_xgb_personalize;

        Log.e("  is json? 2", xgb_json);
        if(xgb_json.equals("")){
            // 다른 정보가 나오도록 한다
            // 딸기라떼
            printRecom("딸기라떼");

        }else{
            try {

                JSONObject jsonObj = new JSONObject(xgb_json);
                String xgb_result_itemName = jsonObj.get("item").toString(); // 메뉴 이름


                FirebaseFirestore.getInstance().collection("cre_menu")
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
                                        if(item1 == null){
                                            textView1.setVisibility(View.INVISIBLE);
                                            textView1_price.setVisibility(View.INVISIBLE);
                                            imageView1.setVisibility(View.INVISIBLE);
                                        }else{
                                            textView1.setText(item1.getName());
                                            // 지연 : AppSetting에 추가
                                            AppSetting.ttsRecoItem1=item1.getName();
                                            textView1_price.setText(item1.getPrice()+"");
                                            Glide.with(RecomMenuActivity.this)
                                                    .load(item1.getImageUrl())
                                                    .into(imageView1);

                                        }

                                    }


                                    // db
                                    if(item1 != null){
                                        writeRecomContent(item1.getName());
                                    }

                                }

                            }
                        });

//            textView1.setText(xgb_result_itemName );


            } catch (Exception e) {
                Log.e(" recom activity", 2+e.getMessage());

                textView1.setVisibility(View.INVISIBLE);
                textView1_price.setVisibility(View.INVISIBLE);
                imageView1.setVisibility(View.INVISIBLE);

                textView1.setOnClickListener(null);
                textView1_price.setOnClickListener(null);
                imageView1.setOnClickListener(null);


            }

        }



    }

    private void writeRecomContent(String itemName){

        FirebaseFirestore.getInstance().collection("recom")
                // .whereEqualTo("name", xgb_result_itemName)
                .add(new RecomDTO(AppSetting.personName, itemName))
                ;
    }

    private void ttsPlease(){
        // tts 객체 생성
        mTTS= new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.KOREAN);
                if (status == TextToSpeech.SUCCESS) {

                    if(AppSetting.emotion!=null){
                        if (AppSetting.emotion.containsKey("happiness") || AppSetting.emotion.containsKey("neutral") || AppSetting.emotion.containsKey("surprise")) {
                            Log.e("   감정", "긍정0");
                            //mTTS.speak("기분이 더 좋아지는 " + item2.getName() + " 어떠세요?", TextToSpeech.QUEUE_FLUSH, null);
                            mTTS.speak("기분이 더 좋아지는 " + item2.getName() + " 어떠세요?", TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(RecomMenuActivity.this, "기분이 더 좋아지는 " + item2.getName() + " 어떠세요?", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("   감정", "부정0");
                            mTTS.speak(getResources().getString(R.string.identify_bad), TextToSpeech.QUEUE_FLUSH, null);
                            mTTS.speak( item2.getName() + " 로 힐링~해보세요~", TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(RecomMenuActivity.this, item2.getName() + " 로 힐링~해보세요~", Toast.LENGTH_SHORT).show();
                        }}else{
                        mTTS.speak("안나와요 널값이에요", TextToSpeech.QUEUE_FLUSH, null);

                    }

                }
            }
        });
    }
    CafeItem item2;
    private void print_CFRecom_result() {

        String cf_json = AppSetting.response_CF_overall;
        Log.e("  is json? 1", cf_json);

        try {

            JSONObject jsonObj = new JSONObject(cf_json);
            String cf_result_itemName  = jsonObj.get("user_cf").toString();

            FirebaseFirestore.getInstance().collection("cre_menu")
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
                                    if(item2 == null){
                                        textView2.setVisibility(View.INVISIBLE);
                                        textView2_price.setVisibility(View.INVISIBLE);
                                        imageView2.setVisibility(View.INVISIBLE);
                                    }else{
                                        textView2.setText(item2.getName());
                                        // 지연 : AppSetting에 추가
                                        AppSetting.ttsRecoItem2=item2.getName();
                                        textView2_price.setText(item2.getPrice()+"");
                                        Glide.with(RecomMenuActivity.this)
                                                .load(item2.getImageUrl())
                                                .into(imageView2);

                                    }

                                    writeRecomContent(item2.getName());

                                }
                                ttsPlease();
                            }
                        }
                    });

        }catch (Exception e){
            Log.e(" recom activity", 1+e.getMessage());
            textView2.setVisibility(View.INVISIBLE);
            textView2_price.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);

            //  .setOnClickListener(null);
            textView2.setOnClickListener(null);
            textView2_price.setOnClickListener(null);
            imageView2.setOnClickListener(null);


        }

    }
}