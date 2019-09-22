package com.example.kiosk_jnsy;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.kiosk_jnsy.databinding.ActivityDetailMenuItemBinding;
import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DetailMenuItemActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityDetailMenuItemBinding binding;
    Map<String,Double> options;
    ArrayList<CafeItem> shoplist;
    // 지연 세부 메뉴
    private Spinner spinner1;
    ArrayList<String> shotList;
    ArrayAdapter<String> arrayAdapter;
    private Spinner spinner2;
    ArrayList<String> whipList;
    ArrayAdapter<String> arrayAdapter2;
    // 장바구니 배열
    // 단,!!!!!!!! 사용자가 바뀌면 초기화 해주어야 한다.

    CafeItem model; // 클릭한 메뉴

    CafeItem item2;
    private void print_CFRecom_result() {

        String cf_json = AppSetting.response_CF_overall;

        try {

            Log.e("  print recom detail2", AppSetting.response_CF_overall);

            JSONObject jsonObj = new JSONObject(cf_json);
            String sim_json  = jsonObj.get("items_sim").toString(); // dict
            jsonObj = new JSONObject(sim_json); // {"꼬미": "삼계탕", "라떼": "꼬미", "삼계탕": "꼬미", "아메리카노": "꼬미"}
            String cf_result_itemName = jsonObj.get(model.getName()).toString();

            FirebaseFirestore.getInstance().collection("menu")
                    .whereEqualTo("name", cf_result_itemName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) { // document.toObject(Order.class)
//                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    // 쿼리 가져온거 반복
                                    item2 = document.toObject(CafeItem.class);
                                    if(item2 == null){
                                        binding.tvItemCFName.setVisibility(View.INVISIBLE);
                                        binding.tvItemCFPrice.setVisibility(View.INVISIBLE);
                                        binding.imageviewCf.setVisibility(View.INVISIBLE);

                                    }else{
                                        binding.tvItemCFName.setText(item2.getName());
                                        binding.tvItemCFPrice.setText(item2.getPrice()+"");
                                        Glide.with(DetailMenuItemActivity.this)
                                                .load(item2.getImageUrl())
                                                .into(binding.imageviewCf);
                                    }

                                }
                            }
                        }
                    });

        }catch (Exception e){
            Log.e(" recom activity", e.getMessage());
            binding.tvItemCFName.setVisibility(View.INVISIBLE);
            binding.tvItemCFPrice.setVisibility(View.INVISIBLE);
            binding.imageviewCf.setVisibility(View.INVISIBLE);
            // .setOnClickListener(null);
            binding.tvItemCFName.setOnClickListener(null);
            binding.tvItemCFPrice.setOnClickListener(null);
            binding.imageviewCf.setOnClickListener(null);

        }

    }

    CafeItem item1;
    private void print_weSim_result() {

        String we_sim_json = AppSetting.response_weather_emotion_matrix; // {"꼬미": "삼계탕", "라떼": "삼계탕", "삼계탕": "라떼", "아메리카노": "삼계탕"}

        try {

            Log.e("  print recom detail1", AppSetting.response_weather_emotion_matrix);

            JSONObject jsonObj = new JSONObject(we_sim_json);
            String result_itemName = jsonObj.get(model.getName()).toString(); // 메뉴 이름

            FirebaseFirestore.getInstance().collection("menu")
                    .whereEqualTo("name", result_itemName)
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
                                        binding.tvWeSimName.setVisibility(View.INVISIBLE);
                                        binding.tvWeSimPrice.setVisibility(View.INVISIBLE);
                                        binding.imageviewWeSim.setVisibility(View.INVISIBLE); // GONE

                                    }else{
                                        binding.tvWeSimName.setText(item1.getName());
                                        binding.tvWeSimPrice.setText(item1.getPrice()+"");
                                        Glide.with(DetailMenuItemActivity.this)
                                                .load(item1.getImageUrl())
                                                .into(binding.imageviewWeSim);
                                    }


                                }
                            }
                        }
                    });

//            textView1.setText(xgb_result_itemName );

        } catch (Exception e) {
            Log.e(" recom activity", e.getMessage());
            binding.tvWeSimName.setVisibility(View.INVISIBLE);
            binding.tvWeSimPrice.setVisibility(View.INVISIBLE);
            binding.imageviewWeSim.setVisibility(View.INVISIBLE); // GONE

            // .setOnClickListener(null);
            binding.tvWeSimName.setOnClickListener(null);
            binding.tvWeSimPrice.setOnClickListener(null);
            binding.imageviewWeSim.setOnClickListener(null); // GONE


        }

    }

    @Override
    public void onClick(View v) {
        if(v==binding.tvItemCFName || v==binding.tvItemCFPrice || v==binding.imageviewCf) {
            Intent intent = new Intent(DetailMenuItemActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item2);
            startActivity(intent);
        }else if(v==binding.tvWeSimName || v==binding.tvWeSimPrice || v==binding.imageviewWeSim){
            Intent intent = new Intent(DetailMenuItemActivity.this, DetailMenuItemActivity.class);
            intent.putExtra("detail", item1);
            startActivity(intent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_menu_item);
        options=new HashMap<String,Double>();


        // 나추천 설정 : 제일 많이 먹은 메뉴를 보여줄때
        // 지연 : 인텐트 테스트/////
        if((String)getIntent().getStringExtra("test")!=null) {
            Log.d("test", (String) getIntent().getStringExtra("test"));
            String name=(String) getIntent().getStringExtra("name");
            int price=(Integer)getIntent().getIntExtra("price",0);
            double op1d=(Double)getIntent().getDoubleExtra("op1d",0);


        }
        model=(CafeItem)getIntent().getSerializableExtra("detail");
        // 나추천 설정


        // 이벤트 등록
        binding.tvWeSimName.setOnClickListener(this);
        binding.tvWeSimPrice.setOnClickListener(this);
        binding.imageviewWeSim.setOnClickListener(this);
        binding.tvItemCFName.setOnClickListener(this);
        binding.tvItemCFPrice.setOnClickListener(this);
        binding.imageviewCf.setOnClickListener(this);



        // 지연 샷에 대한 스피너 추가
        shotList = new ArrayList<>();
        shotList.add("0.5");
        shotList.add("1.0");
        shotList.add("1.5");
        shotList.add("2.0");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, shotList);

        spinner1=(Spinner)findViewById(R.id.spinner1);
        spinner1.setAdapter(arrayAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),shotList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
                options.put("샷",Double.parseDouble(shotList.get(i)));
                model.setOptions(options);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 지연 휘핑에 대한 스피너 추가
        whipList = new ArrayList<>();
        whipList.add("0.0");
        whipList.add("0.5");
        whipList.add("1.0");
        whipList.add("2.0");

        arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, whipList);

        spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner2.setAdapter(arrayAdapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),whipList.get(i)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
                options.put("휘핑",Double.parseDouble(whipList.get(i)));
                model.setOptions(options);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 해시 키값 알면 해당 데이터 가져올 수 있나 ?
        // 테스트

        // 지연 : 인텐트 테스트/////
        model=(CafeItem)getIntent().getSerializableExtra("detail");

        // 추천 내역 파싱
        print_CFRecom_result(); // model 받아오고 실행해야 한다
        print_weSim_result();

        if(AppSetting.personUUID != null){// 사용자가 얼굴인식을 하지 않는 경우를 생각한다
            // 선호도 +1
            // 잠시만여
      //      incrementPreferences(AppSetting.PREFERENCE_CLICK);

        }

        // 지연 : 인텐트 테스트/////
        // 인텐트에서 받아온 정보 출력
        String menuTitle=model.getName();
        int menuPrice=model.getPrice();

        binding.tvMenuTitle.setText(menuTitle);
        binding.tvMenuPrice.setText(menuPrice+"");
        // 이미지
        Glide.with(binding.imgMenu.getContext())
                .load(model.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter())
                .into(binding.imgMenu);


        // 목록 버튼 누르면 뒤로 가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        shoplist=new ArrayList<CafeItem>();

        // 담기 버튼 누르면 인텐트 발생
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AppSetting.personUUID != null) {// 사용자가 얼굴인식을 하지 않는 경우를 생각한다
                    // 잠시만여
   //                 incrementPreferences(AppSetting.PREFERENCE_SHOPLIST); // 선호도 3 증가
                }


                // 세부 옵션 선택은 나중에 하자.
                // 해당 메뉴를 arraylist에 넣는다.
                if((ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist")!=null){
                    // 결제페이지에서 넘어왔다면,
                    // 위에 생성했던 shoplist에 결제목록창에 있던 메뉴들을 넣어준다.
                    ArrayList<CafeItem> newList = new ArrayList<CafeItem>(shoplist);
                    newList.addAll((ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist"));
                    // 이번에 담은 메뉴를 추가한다.
                    newList.add(model);
                    // 그다음에 shoplist를 intent에 추가한다.
                    Intent intent = new Intent(DetailMenuItemActivity.this, PaymentListActivity.class);

                    intent.putExtra("shoplist",newList);

                    //Log.d("로깅","메뉴리스트액티비티:shoplist인텐트에 담긴 개수"+shoplist.size());

                    // 메뉴 개수...
                    HashMap<CafeItem,Integer> m=(HashMap<CafeItem,Integer>)getIntent().getSerializableExtra("m_count");
                    intent.putExtra("m_count",m);
                    startActivity(intent);
                    // 이 리스트에 추가하면 되고
                }else{
                    // 처음 담기를 하는 거라면,
                    shoplist.add(model);
                    // 위에서 생성한 리스트를 넣으면 된다...
                    Intent intent = new Intent(DetailMenuItemActivity.this, PaymentListActivity.class);
                    intent.putExtra("shoplist",shoplist);
                    HashMap<CafeItem,Integer> m=(HashMap<CafeItem,Integer>)getIntent().getSerializableExtra("m_count");
                    intent.putExtra("m_count",m);
                    startActivity(intent);
                }

                // 현재 상태의 리스트 받아옴.
                // 리스트에 담은 메뉴 추가
                Toast.makeText(DetailMenuItemActivity.this, shoplist.size()+"", Toast.LENGTH_SHORT).show();
                //shoplist.add(model);
                // 결제로 넘어간다. 메뉴를 더 고르고 싶다면, paymentlistactivity에서 메뉴추가 버튼을 눌러 메뉴판으로 이동한다.

            }
        });


    }//end onCreate

    private void incrementPreferences(int score){
        // 멤버 model과 AppSetting.itemPreferences 이용한다

        if(AppSetting.itemPreferences.keySet().contains(model.getName())==true){
            int value = AppSetting.itemPreferences.get(model.getName());
            AppSetting.itemPreferences.put(model.getName(), value+score); // 1 증가
        }else{
            AppSetting.itemPreferences.put(model.getName(), score); // 1 할당
        }
        // 잠시만여
  //      updateDB(); // 갱신하고 바로 업로드 - 주문까지 갈거라는 보장이 없더라구요
    }

    private void updateDB(){

        FirebaseFirestore.getInstance().collection("user")
                .document(AppSetting.documentID)
                .update("itemPreference", AppSetting.itemPreferences)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("pay_db_update", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("pay_db_update", "Error updating document", e);
                    }
                });

    }


}//end Activity
