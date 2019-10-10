package com.example.kiosk_jnsy;
//import com.sidemenu.model.SlideMenuItem;
import com.example.kiosk_jnsy.face.AboutPersonGroup;
import com.example.kiosk_jnsy.util.ViewAnimator;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.widget.ViewAnimator;

//import com.example.kiosk_jnsy.databinding.ActivityMainBinding;
import com.example.kiosk_jnsy.face.AboutPerson;
import com.example.kiosk_jnsy.fragment.ContentFragment;
import com.example.kiosk_jnsy.interfaces.Resourceble;
import com.example.kiosk_jnsy.interfaces.ScreenShotable;
import com.example.kiosk_jnsy.model.CafeItem;

import com.example.kiosk_jnsy.model.SlideMenuItem;

import com.example.kiosk_jnsy.model.Order;

import com.example.kiosk_jnsy.model.UserDTO;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.example.kiosk_jnsy.util.Recom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import io.codetail.animation.ViewAnimationUtils;


public class MainActivity extends AppCompatActivity  {

    private TextToSpeech mTTS;
    String tts_identify;
    //ActivityMainBinding binding;
    boolean iamRegistered = false;
    View alertView;
    String name;
    EditText editName;
    String ttsRecoResult; //tts 추천메뉴
    String result; // 나 추천 결과
    String myname; // 나 추천 결과 이름
    String arr[];// 나 추천 split
    Intent intent; // 나 추천 인텐트
    Map<String,Double> map;
    CafeItem myresult;//  나 추천 인텐트로 보낼 객체
    HashMap<String,Integer> rank;

    ImageButton btnOrderedList;
    ImageButton btnMyReco;
    // 왼쪽 바
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;
    private int res = R.drawable.content_music;
    private LinearLayout linearLayout;
    ///////////

    private void showRegisterDialog() {
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
    private void showNewRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this/*getContext()*/)
                .setTitle("얼굴 등록하시겠어요?")
                //.setView(alertView)
                //.setMessage("10번 사진 찍습니다!")
                .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
/*
                        AppSetting.registeredPersonFlag = true;
                        showGetPermissionDialog();
*/
                    }
                }).setNegativeButton("네", new DialogInterface.OnClickListener() {
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
        tempEmotion.put("neutral"/*"contempt"*/,0.1);




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


                                for(int i=0;i<3;i++){ // 임시 주문 기록 10개씩 넣기

                                    Order order = new Order(
                                            tempEmotion, // Map<String,Double>
                                            weather, // Map<String,Double>
                                            tempArray, // ArrayList<CafeItem>
                                            today, // String
                                            "54abaaa4-b8af-429f-baed-9047e6c0561e" /*시현*/
                                            /*"82d44f92-6626-44a8-8766-57d627b99269"*/ /*지연*/, // AppSetting.personUUID
                                            item.getName());

                                    FirebaseFirestore.getInstance().collection("order").add(order);

                                }
                                Log.e("  temp input", ""+item.getName());

                            }
                        }
                    }
                });
    }

/*
    private void tempForYB(){
        // 유빈쓰를 위해 임시 생성
        AppSetting.personUUID = "a056b551-622b-46f8-8620-731a66bc5be8";

        Map<String, Double> emotionMap = new HashMap<>();
        emotionMap.put("happiness",0.97);
        emotionMap.put("neutral",0.03);
        AppSetting.emotion = emotionMap;

        UserDTO user = new UserDTO("유빈", AppSetting.personUUID);

        // 객체 DB에 저장 user 밑에 personName, personId
        FirebaseFirestore.getInstance().collection("user")
                .add(user);

        // 추천 데이터 관련
        if(AppSetting.personUUID != null && AppSetting.isSetPersonalRecom==false) {

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
                                    AppSetting.itemPreferences = (Map<String, Integer>) document.getData().get("itemPreference");
                                    Log.e("   record", AppSetting.itemPreferences + " id:" + AppSetting.documentID);
                                    break; // 한번만 실행되게 한다
                                }
                            }
                        }
                    });
            // 메뉴가 동적으로 추가될 수 있으니까
            // 메뉴를 클릭하면 키로 유무 확인하고, 있으면 값 증가, 없으면 키 생성해서 값 할당
            AppSetting.isSetPersonalRecom = true; // Map 가져오는 것도 한번만
        }

    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



       // binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



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

        // 유빈쓰를 위해 임시 생성
        /*tempForYB();*/
        //tempForYB();
        //


        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        alertView = inflater.inflate(R.layout.alertview_name, null); // 이게 되려나 ?

        editName = (EditText) alertView.findViewById(R.id.edit_name);


        btnOrderedList=(ImageButton)findViewById(R.id.btn_orderedList);
        btnMyReco=(ImageButton)findViewById(R.id.btn_my_reco);
        // person UUID 없으면 버튼 비활성화
        if(AppSetting.personUUID == null){
            btnOrderedList.setEnabled(false);
            btnMyReco.setEnabled(false);
//            PaymentListActivity.order_btn.setEnabled(false);
        }else{

            btnOrderedList.setEnabled(true);
            btnMyReco.setEnabled(true);

        }


        checkFromCamera();

        intentClickBtn();




    }//end onCreate

    private void checkFromCamera(){
        if(AppSetting.camefromCamera != true){ // false
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
                                        AppSetting.itemPreferences = (Map<String,Long/*Integer*/>) document.getData().get("itemPreference");
                                        Log.e("   record", AppSetting.itemPreferences+" id:"+AppSetting.documentID);
                                        break; // 한번만 실행되게 한다
                                    }
                                }
                            }
                        });
                // 메뉴가 동적으로 추가될 수 있으니까
                // 메뉴를 클릭하면 키로 유무 확인하고, 있으면 값 증가, 없으면 키 생성해서 값 할당
                AppSetting.isSetPersonalRecom=true; // Map 가져오는 것도 한번만

                // tts
                tts_identify = getResources().getString(R.string.after_identify_start) + AppSetting.personName + getResources().getString(R.string.after_identify_end);
                ttsRecoResult=null;
                if(AppSetting.ttsRecoItem1!=null&AppSetting.ttsRecoItem2!=null) {
                    ttsRecoResult=AppSetting.ttsRecoItem1+"와 " +AppSetting.ttsRecoItem2;
                }else if(AppSetting.ttsRecoItem1==null){
                    ttsRecoResult=AppSetting.ttsRecoItem2;
                }else if(AppSetting.ttsRecoItem2==null){
                    ttsRecoResult=AppSetting.ttsRecoItem1;
                }else{
                    ttsRecoResult="추천없음";
                }
                // tts 객체 생성
                mTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int status) {
                        mTTS.setLanguage(Locale.KOREAN);
                        if (status == TextToSpeech.SUCCESS) {
                            mTTS.speak("안녕하세요"+AppSetting.personName+"님!", TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(AppSetting.emotion.containsKey("happiness") || AppSetting.emotion.containsKey("neutral") || AppSetting.emotion.containsKey("surprise")){
                                Log.e("   감정", "긍정0");
                                // tts추가

                                mTTS.speak(getResources().getString(R.string.identify_good),TextToSpeech.QUEUE_FLUSH, null);
                                if(ttsRecoResult.equals("추천없음")){

                                }else{
                                    mTTS.speak("기분이 더 좋아지는 " + AppSetting.ttsRecoItem2+ " 어떠세요?", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                            else{
                                Log.e("   감정", "부정0");
                                mTTS.speak(getResources().getString(R.string.identify_bad),TextToSpeech.QUEUE_FLUSH, null);
                                if(ttsRecoResult.equals("추천없음")){

                                }else{
                                mTTS.speak(ttsRecoResult+" 로 힐링~해보세요~", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }

                        }
                    }
                });
                // 이름 확인하는 대화상자 출력하기
                showPersonName();

            }
        }

    }
    private void showPersonName(){ // 안녕하세요!


        AlertDialog.Builder builder = new AlertDialog.Builder(this/*getContext()*/)
                .setTitle(AppSetting.personName+"님 안녕하세요!")
                //.setView(alertView)
                .setMessage(AppSetting.personName+"님이 아니시라면 '다시'를 눌러주세요")
                .setPositiveButton("hello", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        AppSetting.registeredPersonFlag = true;
//                        showGetPermissionDialog();
                    }
                }).setNegativeButton("다시", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                AppSetting.registeredPersonFlag = false;
//                                showGetPermissionDialog();
                            }
                        }
                );
        builder.show();



        // tts 이거 얼굴인식하는 곳에 잘라 가져가세용
        String tts_identify = getResources().getString(R.string.after_identify_start) + // getResources().getString(R.string)
                AppSetting.personName +
                getResources().getString(R.string.after_identify_end);
        if(AppSetting.emotion.containsKey("happiness") || AppSetting.emotion.containsKey("neutral") || AppSetting.emotion.containsKey("surprise")){
            Log.e("   감정", "긍정");
            tts_identify += tts_identify + getResources().getString(R.string.identify_good);
        }else{
            Log.e("   감정", "부정");
            tts_identify += tts_identify + getResources().getString(R.string.identify_bad);
        }
        // tts_identify  읽어주세용

        // tts



        /*
        if(AppSetting.emotion.containsKey("happiness") || AppSetting.emotion.containsKey("neutral") || AppSetting.emotion.containsKey("surprise")){
            Log.e("   감정", "긍정0");
            // tts추가

            mTTS.speak("오늘 기분이 좋아보이시네요!",TextToSpeech.QUEUE_FLUSH, null);
        }else{
            Log.e("   감정", "부정0");
            mTTS.speak("내일은 오늘보다 나은 하루일거에요!",TextToSpeech.QUEUE_FLUSH, null);
        }
        */


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


    private void intentClickBtn(){

        // 카메라 아이콘 클릭하면 얼굴등록하기
        ImageButton btnOtherCamera=(ImageButton)findViewById(R.id.btn_camera);
        btnOtherCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intentToCameraActivity();
                /*showGetPermissionDialog();*/
                showNewRegisterDialog();
/*
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
*/
            }
        });

        // 타 추천 클릭하면 화면 이동
        ImageButton btnOtherReco=(ImageButton)findViewById(R.id.btn_other_reco);
        btnOtherReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecomMenuActivity.class);
                startActivity(intent);
            }
        });


        // 메뉴판 클릭하면 화면 이동
        ImageButton btnMenuList=(ImageButton)findViewById(R.id.btn_menuList);
        btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
                startActivity(intent);
            }
        });

        // 주문기록 클릭하면 화면 이동
        ImageButton btnOrderedList=(ImageButton)findViewById(R.id.btn_orderedList);
        btnOrderedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderedListActivity.class);

                startActivity(intent);
                Log.d("23일","액티비티 넘어감");
            }
        });
/*
        // 나 추천 누르면 화면 이동- 시현쓰코드
        Button btnHomeRecom=(Button)findViewById(R.id.btn_my_reco);
        btnHomeRecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  */
        // 나 추천 누르면 화면 이동
        ImageButton btnMyReco=(ImageButton)findViewById(R.id.btn_my_reco);
        btnMyReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore.getInstance().collection("order").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    rank=new HashMap<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String str = (String)document.getData().get("guest");
                                        // 잠시만여-지연
                                        // 카메라 연동되면 AppSetting.personUUID로 바꿀예정
                                        if(str.equals(AppSetting.personUUID)){
                                            String orderToString=(String)document.getData().get("orderToString");

                                            // rank 맵에 이미 있다면 있는값에 추가
                                            if(rank.get(orderToString)!=null){
                                                int num=rank.get(orderToString);
                                                Toast.makeText(MainActivity.this, orderToString+"추가", Toast.LENGTH_SHORT).show();
                                                rank.put(orderToString,num+1);
                                            }// 없다면 1로 추가
                                            else {
                                                rank.put(orderToString, 1);
                                            }
                                        }
                                    }
                                    // rank 맵에서 value가 가장 큰 엔트리 찾기. 정렬하는 코드는 삭제함.
                                    Map.Entry<String, Integer> maxEntry = null;
                                    for (Map.Entry<String,Integer> entry : rank.entrySet())
                                    {
                                        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                                        {
                                            maxEntry = entry;
                                        }
                                    }
                                    result=maxEntry.getKey();
                                    // 엔트리 결과를 처리
                                    arr=result.split("/");
                                    // arr[0] : 메뉴이름

                                    // 이 메뉴의 손님의 옵션 설정
                                    String[] op1=arr[1].split(":"); // option1 휘핑:0.0
                                    final double op1d=Double.parseDouble(op1[1]);
                                    String[] op2=arr[2].split(":"); // option2
                                    final double op2d=Double.parseDouble(op2[1]);

                                    // 객체 생성해서 인텐트로 보냄
                                    map=new HashMap<String,Double>();
                                    map.put("휘핑",op1d);
                                    map.put("샷",op2d);


                                    // 이메뉴의 이미지 url 필요
                                    FirebaseFirestore.getInstance().collection("menu").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // 메뉴정보 추출
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            // 메뉴 이름 바꿔야 함..
                                                            if((myname=(String)document.getData().get("name")).equals(arr[0])) {
                                                                Long pricen = (Long) document.getData().get("price");
                                                                String body = (String) document.getData().get("body");
                                                                int price = pricen.intValue();
                                                                String imageUrl = (String) document.getData().get("imageUrl");
                                                                myresult=new CafeItem(myname, price, imageUrl, body);
                                                                Toast.makeText(MainActivity.this, "빠른 주문", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(MainActivity.this, DetailMenuItemActivity.class);

                                                                intent.putExtra("detail",myresult);
                                                                startActivity(intent);
                                                                break;
                                                            }
                                                        }
                                                    } else { }
                                                }
                                            });
                                } else {
                                    Log.d("dd", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });




     //   viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);

        //titanic.cancel();
    }//end onCreate
    // 왼쪽 바



    // 왼쪽 바





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
                                            AppSetting.personName/*name */= editName.getText().toString();

                                            // 이름 받고 이름 토대로 사람 생성
                                            new AboutPerson.CreatePersonTask(AppSetting.personName/*name*/, MainActivity.this).execute("");

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



    public void intentToCameraActivity(){
        AppSetting.camefromMain = true;
        startActivity(new Intent(this,CameraActivity.class));
    }


/*
    class GetPreferenceMapTask extends AsyncTask<Void,Void,M>{

    }
*/


}//end Activity
