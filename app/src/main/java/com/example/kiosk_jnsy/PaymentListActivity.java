/*인증오류 얼마나 걸릴지를 모르겠어서요ㅠㅠ 미안
// paymentlist activity입니다.. 아까 appsetting.emotion을 new order부분만 수정하면 됩니다..
*/
package com.example.kiosk_jnsy;
// 유빈
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.model.Order;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class PaymentListActivity extends AppCompatActivity {

    private ArrayList<CafeItem> over; // 하나씩 더 더해지는 것을 방지
    private RecyclerView mRecyclerView;
    private ArrayList<CafeItem> mArrayList; // 인텐트로 받아올 값
    private PaymentListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Button add_menu;
    private Button order_btn;
    private Button plus_btn;
    private TextView price_tv;
    private TextView count_tv;
    private HashMap<CafeItem,Integer> menucount; // 개수

    private int count = -1;
    // 지연 : 날씨 타이머에 대한 변수들///////
    int desIndex;
    TextView tv;
    TimerTask addTask;
    private int Interval=10; // 10분마다 날씨 받아오는 동작함.
    Timer timer;

    // 지연 : DB에 저장할 Order객체 속 날씨 정보
    String description=null;
    String temp=null;
    String speed;
    String humidity;

    void playTTS(){
        // tts
        String[] str_random = getResources().getStringArray(R.array.order_random);
        int num = str_random.length; /*// index*/

        Random random = new Random();
//        random.nextInt(num); // [) 범위 라서!

        // tts 읽어주세용
//        str_random[random.nextInt(num)] 이거 읽어주세용

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);


        order_btn=(Button)findViewById(R.id.order_btn);
        if(AppSetting.personUUID == null) {// 사용자가 얼굴인식을 하지 않는 경우를 생각한다
            // 주문할 수도 없게 ?
            order_btn.setEnabled(false);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_main_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // MainActivity에서 RecyclerView의 데이터에 접근시 사용됩니다.
        // mArrayList = new ArrayList<>();
        // 지연 : intet 값 받아오기 일단 받아오기
        menucount=(HashMap<CafeItem, Integer>)getIntent().getSerializableExtra("m_count");
        mArrayList=(ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist");


        // 지연 : plus 위한 처리
        if(menucount==null){
            // menucount가 없다면== 해시맵이 만들어진적이 없는 첫 주문이라면?
            menucount=new HashMap<CafeItem,Integer>();
            //Toast.makeText(this, "완전 첫주문", Toast.LENGTH_LONG).show();
            // 중복 제거할 게 없지. 주문에 한개도 없었던 거니까
            // 방금 선택한 객체를 해시맵에 넣자/
            for(int i=0;i<mArrayList.size();i++){
                // 해당 객체가 한번도 세지지 않은 상황이라면...
                if(!(menucount.containsKey(mArrayList.get(i)))){
                    // 해당객체 : 1 의 형태로 map에 넣는다.
                 //   Toast.makeText(this, "이건 처음넣는다!", Toast.LENGTH_LONG).show();
                    menucount.put(mArrayList.get(i),1);
                }// 해당 객체가 한번이라도 세어진 상황이라면... value=1이겠지

                else{
                    int n=menucount.get(mArrayList.get(i))+1;
                    Log.d("띠용","1111111111");
                    menucount.put(mArrayList.get(i),n);
                  //  Toast.makeText(this, "주문기록 있는 상황에서 메뉴판에서 다시 추가", Toast.LENGTH_LONG).show();
                }

            }

        }
        else{
            over=new ArrayList<CafeItem>();
            // menucount가 있다면== 해시맵이 만들어진적 있는 주문이라면?
            // 방금 선택되서 결정된 리스트에서 각각의 수를 해시맵에 저장하자.
            // 만약 없다면 해시맵에 "이름:1"로 저장하고
            // 만약 있다면 해시맵에 "이름:#"로 저장하자
            for(int i=0;i<mArrayList.size();i++){
                // 해당 객체가 한번도 세지지 않은 상황이라면...
                // 해당 객체가 한번이라도 세어진 상황이라면... value=1이겠지
                if((menucount.containsKey(mArrayList.get(i)))){
                    int n=menucount.get(mArrayList.get(i));

                    Log.d("띠용","222222222"+mArrayList.get(i).getName());
                    menucount.put(mArrayList.get(i),n+1);
                    over.add(mArrayList.get(i));// 넣어둠
                    Log.d("시발","시발");
                   // Toast.makeText(this, "주문기록 있는 상황에서 메뉴판에서 다시 추가", Toast.LENGTH_SHORT).show();

                }
                else{
                    // 해당객체 : 1 의 형태로 map에 넣는다.
                    Log.d("띠용","33333333333"+mArrayList.get(i).getName());
                //    Toast.makeText(this, "이건 처음넣는다!", Toast.LENGTH_SHORT).show();
                    menucount.put(mArrayList.get(i),1);
                }


            }//endfor

            // over 중복 제거
            // 지연 : mArraylist의 중복 객체들을 제거
            List<CafeItem> correct_over =over; // ProxyBean들을 담고 있는 proxy list
            ArrayList<CafeItem> corrected_over = new ArrayList<CafeItem>(new HashSet<CafeItem>(correct_over));
            over=corrected_over;

            for(int i=0;i<over.size();i++){
                if(menucount.containsKey(over.get(i))){
                    int x=menucount.get(over.get(i));
                    menucount.put(over.get(i),x-1);
                }
            }
        }



        // 어댑터에 넣기 전 중복 된건 지우고 보여주자!!
        // 지연 : mArraylist의 중복 객체들을 제거
        Log.d("시발","시발전"+mArrayList.size());
    //    Toast.makeText(this, "전전:"+mArrayList.size(), Toast.LENGTH_LONG).show();

        // 지연 : mArraylist의 중복 객체들을 제거
        List<CafeItem> plist =mArrayList; // ProxyBean들을 담고 있는 proxy list

       // System.out.println("before size:"+ plist.size());

        ArrayList<CafeItem> sliet = new ArrayList<CafeItem>(new HashSet<CafeItem>(plist));
        mArrayList=sliet;

        Log.d("시발","시발후"+mArrayList.size());
     //  Toast.makeText(this, "개수: "+mArrayList.size(), Toast.LENGTH_LONG).show();

        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        mAdapter = new PaymentListAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        // add_button을 클릭하면 메뉴판으로 넘어간다.
        add_menu=(Button)findViewById(R.id.add_menu_btn);

        add_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentListActivity.this, MenuListActivity.class);
                intent.putExtra("shoplist",mArrayList); // 지연 : 담은 메뉴 상태 유지
                intent.putExtra("m_count",menucount); // 지연 : 담은 메뉴 개수상태 유지
                startActivity(intent);
            }
        });
        // 결제 버튼을 누르면 파이어베이스에 Order을 올린다.
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("결과온","설명 >>"+
                        ""+"\n온도 >>"+
                        AppSetting.recom_weather_temp+"\n습도 >>"+
                        AppSetting.recom_weather_humidity+"\n풍속 >>"+
                        AppSetting.recom_weather_speed);

                // tts 로딩시간 걸린다니까 클릭하자마자 tts 함수 호출할게요
                playTTS();

                //tv.setText("설명 >>"+description+"\n온도 >>"+temp+"\n습도 >>"+humidity+"\n풍속 >>"+speed);
                // 아무 데이터 넣을게요
                Map<String,Double> emotion=new HashMap<String,Double>();
                // 잠시만여-지연
                emotion.put("happiness",0.9);
                // 감정 최고기록 받아오기ㅠㅠㅠㅠ
                //String result_emotion = getEmotion(faces[0].faceAttributes.emotion);


                //Toast.makeText(getActivity().getApplicationContext(), temp_d+"있을까", Toast.LENGTH_SHORT).show();
                // 날씨 정보 처리
                Map<String,Double> weather=new HashMap<String,Double>();
                /// 설명은 string형이라 따로 변수르 만들어야 함!!!!!!!!!!!!!!!!111111111111
                //weather.put("description",Double.valueOf(description).doubleValue());
//                Log.d("결과온",Double.parseDouble(temp)+"");
                if(AppSetting.recom_weather_humidity == null){
                    weather.put("humidity",Double.parseDouble("80"/*AppSetting.recom_weather_humidity)*/));
                    weather.put("temp",Double.parseDouble("1"/*AppSetting.recom_weather_temp*/));
                    weather.put("speed",Double.parseDouble("300"/*AppSetting.recom_weather_speed*/));
                }else{
                    weather.put("humidity",Double.parseDouble(/*"80"*/AppSetting.recom_weather_humidity));
                    weather.put("temp",Double.parseDouble(/*"1"*/AppSetting.recom_weather_temp));
                    weather.put("speed",Double.parseDouble(/*"300"*/AppSetting.recom_weather_speed));

                }

                // calender로 현재 날짜 알아오기
                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
                String format_time1 = format1.format(System.currentTimeMillis());
                String today=format_time1;

                // 파이어베이스DB에 주문 기록 추가
                try {
                    String guest= AppSetting.personUUID;
                    String orderToString="";
                    String name;
                    Map<String,Double> m;
                    CafeItem it;
                    for(int x=0;x<mArrayList.size();x++){
                        it=mArrayList.get(x);
                        name=(String)it.getName();
                        m=(Map)it.getOptions();
                        double o1=m.get("휘핑");
                        double o2=m.get("샷");
                        double o3=m.get("온도");
                        double o4=m.get("텀블러");
                        double o5=m.get("빨대");
                        double o6=m.get("얼음");
                        //orderToString=orderToString+name+"/휘핑:"+o1+"/샷:"+o2+"/"+"온도"+o3+"/텀블러:"+o4+"/"+"빨대"+o5+"/얼음:"+o6+"/";

                        String temp="",takeout="";
                        if(o3 == 1.0){
                            temp = "핫";
                        }else{
                            temp = "아이스";
                        }
                        if(o4 == 0.0){
                            takeout = "매장";
                        }
                        else{
                            takeout = "takeout";
                        }
                        orderToString=orderToString+name+"/휘핑:"+o1+"/샷:"+o2+"/온도: "+temp+"/테이크아웃: "+takeout+"/"+"/"+"빨대"+o5+"/얼음:"+o6+"/";
                    }
                    Order order = new Order(AppSetting.emotion,weather,mArrayList,today,guest,orderToString);
                    //
                    //FirebaseDatabase.getInstance().getReference().child("order").push().setValue(order);
                    FirebaseFirestore.getInstance().collection("cre_order").add(order);
                }catch(Exception e){
                    Log.d("예외",e.getMessage());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setMessage("주문되었습니다")
                        .setCancelable(false)
                        .setPositiveButton("확인",

                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                builder.show();

                if(AppSetting.personUUID != null) {// 사용자가 얼굴인식을 하지 않는 경우를 생각한다
                    // 선호도
                    for(CafeItem item:mArrayList){
                        incrementPreferences(item.getName(), AppSetting.PREFERENCE_ORDER); // 선호도 +5 증가
                    }
                    // DB 갱신
                    updateDB();
                }

                // 주문하기 버튼을 누르면 로그아웃 처리
                AppSetting.personUUID = null;
                AppSetting.personName = null;
                AppSetting.camefromCamera = false; // main으로 돌아가서 얼굴인식 다시 한다~

                // 주문하면 Main으로 이동한다
                startActivity(new Intent(PaymentListActivity.this, MainActivity.class));
            }
        });

        addTask = new TimerTask() {
            @Override
            public void run() {
                //주기적으로 실행할 작업 추가
                new GetDataJSON().execute();
                //Log.d("결과x",speed);
            }

        };
        Timer timer = new Timer();
        timer.schedule(addTask, 0, Interval*60*1000); // 0초후 첫실행, Iterval 분마다 실행


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

    private void incrementPreferences(String ItemName, int score){
        // 멤버 model과 AppSetting.itemPreferences 이용한다

        if(AppSetting.itemPreferences.keySet().contains(ItemName)==true){
            long value = AppSetting.itemPreferences.get(ItemName);
            AppSetting.itemPreferences.put(ItemName, value+score); // 1 증가
        }else{
            AppSetting.itemPreferences.put(ItemName, (long)score); // 1 할당
        }
    }


    class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ListViewHolder> {

        private ArrayList<CafeItem> mList;

        public class ListViewHolder extends RecyclerView.ViewHolder
        {
            protected TextView order;
            protected Button delete;
            protected TextView count;
            protected TextView price;
            protected Button plus;

            public ListViewHolder(View view){
                super(view);
                order=(TextView)view.findViewById(R.id.list_tv);

                delete=(Button)view.findViewById(R.id.delete_btn);
                plus=(Button)view.findViewById(R.id.plus_btn);
                price=(TextView)view.findViewById(R.id.price_tv);
                count=(TextView)view.findViewById(R.id.count_tv);
            }
        }

        public PaymentListAdapter(ArrayList<CafeItem> list){
            this.mList=list;
        }

        // RecylcreView에 새로운 데이터를 보여주기 위해 필요한 viewHolder를 생성해야 할 때 호출
        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_item_list,viewGroup,false);
            ListViewHolder viewHolder = new ListViewHolder(view);

            return viewHolder;
        }



        // Adapter의 특정 위치에 있는 데이터를 보여줘야 할 때 호출
        @Override
        public void onBindViewHolder(@NonNull ListViewHolder viewholder, int position) {

            final int pos = position;

            viewholder.order.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            viewholder.order.setGravity(View.TEXT_ALIGNMENT_CENTER); // 지연 : CENTER 수정
            viewholder.order.setText(mList.get(position).getName()+mList.get(position).getOptions()); // 지연 : getOrder() 수정
            viewholder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    deleteItemFromList(v,pos);
                }
            });
            viewholder.plus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    plusThisItem(v,pos);
                }
            });

            // 현재 수량

            viewholder.count.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            viewholder.count.setGravity(View.TEXT_ALIGNMENT_CENTER); // 지연 : CENTER 수정
            viewholder.count.setText(menucount.get(mList.get(position))+""); // 지연 : getOrder() 수정


            // 해당메뉴가격
            viewholder.price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            viewholder.price.setGravity(View.TEXT_ALIGNMENT_CENTER); // 지연 : CENTER 수정
            viewholder.price.setText(mList.get(position).getPrice()+"");
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        private void deleteItemFromList(View v, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setMessage("메뉴를 삭제하겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //mList.remove(position);
                                    // 리스트에 해당 메뉴 추가하기

                                    CafeItem current2=mList.get(position);
                                    //mArrayList.remove(current2);

                                    //mList.add(current);
                                    int c=menucount.get(current2);
                                    menucount.put(current2,c-1);
                                    if(menucount.get(current2)==0){
                                        mArrayList.remove(current2);
                                        menucount.remove(current2);
                                    }// 0이라고 아예 없어지는게 아님 키는 남아있으므로 키지워줌


                                    mAdapter = new PaymentListAdapter(mArrayList);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }

        private void plusThisItem(View v, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setMessage("이 메뉴를 추가 하겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 리스트에 해당 메뉴 추가하기
                                    CafeItem current=mList.get(position);
                                    mArrayList.add(current);
                                    //mList.add(current);
                                    int c=menucount.get(current);
                                    menucount.put(current,c+1);


                                    // 지연 : mArraylist의 중복 객체들을 제거
                                    ArrayList<CafeItem> result=new ArrayList<CafeItem>();
                                    for(int i=0;i<mArrayList.size();i++){
                                        if(!result.contains(mArrayList.get(i))){
                                            result.add(mArrayList.get(i));
                                        }

                                    }
                                    mArrayList=result;

                                    mAdapter = new PaymentListAdapter(mArrayList);
                                    mRecyclerView.setAdapter(mAdapter);
                                  //  mList.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }

    }
    // 지연 : 날씨 json 받아오는 클래스////////////////////////////////////////////////////////
    // 호출은
    class GetDataJSON extends AsyncTask<Void, Void, String> {
        // public GetDataJSON()
        @Override
        protected String doInBackground(Void... params) {
            //
            // 날씨 받아올 url
            String uri = "http://api.openweathermap.org/data/2.5/weather?lat=37.652490&lon=127.013178&mode=json&APPID=d4246e2d41b8660df3b7086d27e865ae";
            BufferedReader bufferedReader = null;
            StringBuilder sb = new StringBuilder();
            String json;
            try{
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                return sb.toString().trim();
            }catch(Exception e){
                Log.d("오류",e.getMessage());
                return "e.getMessage()";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // myJSON = result; // Activity 멤버 변수
            try{
                //JSONObject jsonObj = ggggnew JSONObject(result);
                //peoples = jsonObj.getJSONArray(TAG_RESULTS); // peoples는 Activity 멤버 JsonArray
                // tag results는 json dict의 key겠지 ? value-리스트를 가져온다는 소리
                // 일단 매개 result 를 출력하는 정도로만 시도해보자

                //Toast.makeText(getActivity().getApplicationContext(), "날씨 갱신함", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                //description="초기";


                JSONArray jarray = new JSONObject(result).getJSONArray("weather");
                JSONObject jObject = jarray.getJSONObject(0);
                description = jObject.optString("description");

                JSONObject main=new JSONObject(result).getJSONObject("main");
                humidity = main.optString("humidity");
                temp = main.optString("temp");

                JSONObject wind=new JSONObject(result).getJSONObject("wind");
                speed= wind.optString("speed");


                //Toast.makeText(getActivity().getApplicationContext(), temp_d+"입니다.", Toast.LENGTH_SHORT).show();

                Log.d("날씨 갱신결과","설명 >>"+description+"\n온도 >>"+temp+"\n습도 >>"+humidity+"\n풍속 >>"+speed);
                //tv.setText("설명 >>"+description+"\n온도 >>"+temp+"\n습도 >>"+humidity+"\n풍속 >>"+speed);
            }catch(Exception e){

            }
        }
    }

}