package com.example.kiosk_jnsy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Camera;
import android.os.AsyncTask;
import android.service.media.CameraPrewarmService;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.kiosk_jnsy.databinding.ActivityMainBinding;
import com.example.kiosk_jnsy.face.AboutPerson;
import com.example.kiosk_jnsy.face.AboutPersonGroup;
import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.model.Order;
import com.example.kiosk_jnsy.model.UserDTO;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.example.kiosk_jnsy.util.Recom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    boolean iamRegistered = false;
    View alertView;
    String name;
    EditText editName;

    // temp
    CafeItem item;
    Map<String,Double> weather;
    Map<String,Double> tempEmotion;

    private void inputTempOrderList(){

        weather=new HashMap<String,Double>();
//        80.0/300.0/1.00
        weather.put("humidity",new Double(80.0));
        weather.put("temp",new Double(300.0));
        weather.put("speed",new Double(1.00));
//        "happiness" "neutral"
        tempEmotion=new HashMap<String,Double>();
        tempEmotion.put("happiness",0.9); // "happiness" "neutral"
        tempEmotion.put(/*"neutral"*/"contempt",0.1);




        FirebaseFirestore.getInstance().collection("menu").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                                item = document.toObject(CafeItem.class);
                                ArrayList<CafeItem> tempArray = new ArrayList<CafeItem>();
                                tempArray.add(item);

                                String today = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());


                                for(int i=0;i<10;i++){ // 임시 주문 기록 10개씩 넣기

                                    Order order = new Order(
                                            tempEmotion, // Map<String,Double>
                                            weather, // Map<String,Double>
                                            tempArray, // ArrayList<CafeItem>
                                            today, // String
                                            /*"54abaaa4-b8af-429f-baed-9047e6c0561e" 시현*/
                                            "82d44f92-6626-44a8-8766-57d627b99269" /*지연*/, // AppSetting.personUUID
                                            item.getName());

                                    FirebaseFirestore.getInstance().collection("order").add(order);

                                }
                                Log.e("  temp input", ""+item.getName());

                            }
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



//        inputTempOrderList(); // 임시 order data 삽입

        // 추천 내역 전역으로 저장한다
//        new Recom.RecomXGBTask().execute();
//        new Recom.RecomItemCFTask().execute();
        new Recom.RecomWeatherEmotionTask().execute();

        // create person group
//        new AboutPersonGroup.CreatePersonGroupTask(this).execute("");

        // 그룹이 없으면 자동으로 생성해줄거야
//        new AboutPersonGroup.GetPersonGroupTask(MainActivity.this);
//        new AboutPersonGroup.CreatePersonGroupTask(this).execute(AppSetting.personGroupId); // 그룹id 전달

//        Log.e("  Main group id", AppSetting.personGroupId);

        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        alertView = inflater.inflate(R.layout.alertview_name, null); // 이게 되려나 ?

        editName = (EditText) alertView.findViewById(R.id.edit_name);


        // person UUID 없으면 버튼 비활성화
        if(AppSetting.personUUID == null){
            binding.btnOrderedList.setEnabled(false);
            binding.btnHomeRecom.setEnabled(false);
//            PaymentListActivity.order_btn.setEnabled(false);
        }else{
            binding.btnOrderedList.setEnabled(true);
            binding.btnHomeRecom.setEnabled(true);

        }


        checkFromCamera();

        intentClickBtn();

    }//end onCreate

    private void checkFromCamera(){
        if(AppSetting.camefromCamera != true){
            // 시나리오: Main > Camera > Main
            // 자칫하면 무한루프가 발생할 수 있으므로 체크한다
            // 앱이 처음 시작된 상태

            // 대화상자 출력한다
            showRegisterDialog(); // 얼굴 등록했는지 -> 얼굴 인식할건지
            AppSetting.camefromCamera = false; // 사용하고 초기화
        } else{
            //카메라에서 인텐트 발생해서 온거면

            // 카메라 화면에서 이동해서 AppSetting에 UUID 저장되있으면
            // 서버에 보내서 추천 정보 가져와야한다.

            if(AppSetting.personUUID != null && AppSetting.isSetPersonalRecom==false){
                // 컬렉션에서 여러 문서 가져오기
                FirebaseFirestore.getInstance().collection("user")
                        .whereEqualTo("personUUID", AppSetting.personUUID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        AppSetting.documentID = document.getId();
                                        AppSetting.itemPreferences = (Map<String,Integer>) document.getData().get("itemPreference");
                                        Log.e("   record", AppSetting.itemPreferences+" id:"+AppSetting.documentID);
                                        break; // 한번만 실행되게 한다
                                    }
                                }
                            }
                        });
                // 메뉴가 동적으로 추가될 수 있으니까
                // 메뉴를 클릭하면 키로 유무 확인하고, 있으면 값 증가, 없으면 키 생성해서 값 할당
                AppSetting.isSetPersonalRecom=true; // Map 가져오는 것도 한번만

            }
        }

    }

    private void intentClickBtn(){
        // 타 추천 클릭하면 화면 이동
        binding.btnOtherReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecomMenuActivity.class);
                startActivity(intent);
            }
        });


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

        // 나 추천 누르면 화면 이동
        binding.btnHomeRecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomeRecomActivity.class));
            }
        });

    }

    private void showGetPermissionDialog(){
        // 얼굴 등록했다고 말했으면 iamRegistered =true,
        // 얼굴 인식 한다고 하면 한번만 찍어서 결과 보여주면 된다

        // 얼굴 등록 안했으면 iamRegistered = false,
        // 얼굴 인식한다고 하면 이름 등록하고 10번 찍어서 메뉴판 보여주기


        // 대화상자 출력한다
        new AlertDialog.Builder(this/*getContext()*/)
                .setTitle("얼굴 인식하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(AppSetting.registeredPersonFlag == true){// 한번만 찍어서 결과 보여주면 된다

                            // registeredPersonFlag == true
                            intentToCameraActivity();

                        }else{// 이름 등록하고 10번 찍어서 메뉴판 보여주기  registeredPersonFlag == false
                            // 대화상자 출력한다
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("이름 등록")
                                    .setView(alertView)
                                    //.setMessage("10번 사진 찍습니다!")
                                    .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 가져온다
                                            name = editName.getText().toString();
                                            // 이름 받고 이름 토대로 사람 생성
                                            new AboutPerson.CreatePersonTask(name, MainActivity.this).execute("");

//                                            intentToCameraActivity();

                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("10번 사진 찍습니다!\n카메라에 얼굴이 나오게 해주세요!")
                                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            // 10번 스레드
                                                            // registeredPersonFlag == false
                                                            intentToCameraActivity();
                                                        }
                                                    }).show();


                                        }
                                    }).show();

                        }
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }
        ).show();
    }

    private void showRegisterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this/*getContext()*/)
                .setTitle("얼굴 등록하셨나요?")
                //.setView(alertView)
                //.setMessage("10번 사진 찍습니다!")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppSetting.registeredPersonFlag = true;
                        showGetPermissionDialog();
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppSetting.registeredPersonFlag = false;
                                showGetPermissionDialog();
                            }
                        }
                );


        // 되려나 된다 모달리스
        builder.show().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    public void intentToCameraActivity(){
        AppSetting.camefromMain = true;
        startActivity(new Intent(this,CameraActivity.class));
    }


/*
    class GetPreferenceMapTask extends AsyncTask<Void,Void,M>{

    }
*/


}//end Activity
