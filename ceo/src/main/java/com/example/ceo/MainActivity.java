package com.example.ceo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
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

import com.example.ceo.model.CafeItem;
import com.example.ceo.model.Order;
import com.example.ceo.model.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 지연 : 메뉴등록 버튼
    Button registerbtn;

    private RecyclerView mRecyclerView;
    private ArrayList<Order> mArrayList = new ArrayList<>(); // 인텐트로 받아올 값
    private PaymentListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ListViewHolder> {

//        private ArrayList<CafeItem> mList;
        private ArrayList<Order> mList = new ArrayList<>();

        public void notifyAdapter(){
            notifyDataSetChanged();
        }

        public class ListViewHolder extends RecyclerView.ViewHolder
        {
            protected TextView order;
            protected Button delete;
            protected TextView name;
            protected TextView emotion;

            public ListViewHolder(View view){
                super(view);
                order=(TextView)view.findViewById(R.id.list_tv);
                delete=(Button)view.findViewById(R.id.delete_btn);

                name=(TextView)view.findViewById(R.id.tv_name);
                emotion=(TextView)view.findViewById(R.id.tv_emotion);
            }
        }

        public PaymentListAdapter(ArrayList<Order> list){
            this.mList=list;
            Log.e("  adapter", ""+mList.size());
        }
        public PaymentListAdapter(){
            // 멤버 mList -- ArrayList<Order>
//            mList

            FirebaseFirestore.getInstance().collection("cre_order")
                    .whereEqualTo("check",false)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // 성공
                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.e("  ceo", document.getId() + " => " + document.getData());
//                                    mList.add((Order)document.getData());//.getOrderToString();


//                                    Order order = new Order();
//                                    order.setOrderToString(document.toObject(Order.class).getOrderToString());
//                                    Log.e("  ceo_create", document.toObject(Order.class).getOrderToString());
//                                    order.setOrderToString(((Map)document.getData()).get("orderToString").toString());
                                    mArrayList.add(/*order*/document.toObject(Order.class));
                                    Log.e("  ceo_create", mList.size()+"");
                                }
                            } else {
                                // 실패
//                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            notifyDataSetChanged();
        }

        // RecylcreView에 새로운 데이터를 보여주기 위해 필요한 viewHolder를 생성해야 할 때 호출
        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_item_list,viewGroup,false);
            ListViewHolder viewHolder = new ListViewHolder(view);

            return viewHolder;
        }


        String userName="";
        // Adapter의 특정 위치에 있는 데이터를 보여줘야 할 때 호출
        @Override
        public void onBindViewHolder(@NonNull final ListViewHolder viewholder, int position) {

            final int pos = position;

            viewholder.order.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            viewholder.order.setGravity(View.TEXT_ALIGNMENT_CENTER); // 지연 : CENTER 수정

            viewholder.order.setText(mList.get(position).getOrderToString()); // 지연 : getOrder() 수정
            Log.e("   ceo_print", mList.get(position).getOrderToString());

            viewholder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    deleteItemFromList(v,pos);
                }
            });


            FirebaseFirestore.getInstance().collection("user")
                    .whereEqualTo("personUUID",mList.get(position).getGuest())

                    .get()

                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                // 성공
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    userName = document.toObject(UserDTO.class).getPersonName();
                                    viewholder.name.setText("손님: "+userName);
                                }
                            }
                        }
                    });


            viewholder.name.setText(userName);
            Log.e("   bind", userName);

            // 감정 잠시만 일단 테스트좀
//            viewholder.emotion.setText(mList.get(position).getOrderToString());
//            mList.get(position).getEmotion(); // HashMap
            // map 내림차 정렬
            Iterator iterator = sortByValue(mList.get(position).getEmotion()/* map */).iterator(); // 내림차 정렬
            // 값으로 내림차 정렬된 키가 들어있는 Iterator


//            Map<String,Double> temp = new HashMap<>();
            String maxValueKey = (String) iterator.next();//1번째
            String emotion;
            if(maxValueKey.equals("happiness")
                    || maxValueKey.equals("neutral")
                    || maxValueKey.equals("surprise")){
                emotion = "긍정";
            }else{
                emotion = "부정";
            }
            viewholder.emotion.setText("감정: "+emotion);
        }

        private List sortByValue(final Map map) {

            List<String> list = new ArrayList();
            list.addAll(map.keySet());

            Collections.sort(list,new Comparator() { // key를 내림차 value에 대해 정렬

                public int compare(Object o1, Object o2) { // (오름차: 1>2일 때 return 양수

                    Object v1 = map.get(o1);
                    Object v2 = map.get(o2);

                    return ((Comparable) v2).compareTo(v1); //compareTo: 인자가 더 크면 return 음수
                    //compare: 양수인 경우  두 객체의 자리가 바뀐다
                    // 뭐.. 잘 안나오면 1,2 바꿔봐..
                }

            });

            return list; // key 리스트 리턴한다
        }


        @Override
        public int getItemCount() {
            return  mList.size();//(null != mList ? mList.size() : 0);
        }

//        String docId="";

        private void deleteItemFromList(View v, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setTitle("음료 제작하시겠어요?")
                    .setMessage("목록에서 사라집니다!")
                    .setCancelable(false)
                    .setPositiveButton("네",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    final Order order = mList.get(position);
                                    mList.remove(position);
                                    notifyDataSetChanged();

                                    FirebaseFirestore.getInstance().collection("cre_order")
                                            .whereEqualTo("orderToString", order.getOrderToString())
                                            .whereEqualTo("guest", order.getGuest())
//                                            .update
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        // 성공
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                                            order = document.toObject(Order.class);
//                                                            viewholder.name.setText("손님: "+userName);
                                                            String docId = document.getId();
                                                            updateOrderDB(docId);
                                                        }

//                                                        order = task.(.class)
                                                    }
                                                }
                                            });

                                }
                            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }

        private void updateOrderDB(String docId){
            FirebaseFirestore.getInstance().collection("cre_order")
                    .document(docId)
                    .update("check",true);
        }

        public void setItems(ArrayList<Order> items) {
            this.mList = items;
            notifyDataSetChanged();
        }
    }


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

        // 시현: Recycler View
//        new GetDataTask().execute();
        getData();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_main_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new PaymentListAdapter(mArrayList); // 빈 생성자에서 DB 연동해서 가져오면 되겠지?
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setAdapter(mAdapter);


    }

    TextToSpeech mTTS;
    private void playTTS(final String MenuName){

        mTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                mTTS.setLanguage(Locale.KOREAN);
                if (status == TextToSpeech.SUCCESS) {
                    mTTS.speak(MenuName/*"말하고 싶은 말"*/, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    private void speak(String text){

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


    private void/*ArrayList<Order>*/ getData(){

/*
        FirebaseFirestore.getInstance().collection("cre_order")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            // 성공
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                                Log.e("  ceo_create", document.toObject(Order.class).getOrderToString());
//                                    order.setOrderToString(((Map)document.getData()).get("orderToString").toString());
                                mArrayList.add(*/
/*order*//*
document.toObject(Order.class));
                                Log.e("  ceo_back list size", mArrayList.size()+"");
                            }
                            mAdapter.setItems(mArrayList);
                        }
                    }
                });
*/
        FirebaseFirestore.getInstance().collection("cre_order")
//                .whereEqualTo("check",false)
//                .orderBy("today", Query.Direction.ASCENDING)
                .orderBy("today", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.e("  이벤트수신? null", "Listen failed.", e);
                            return;
                        }

                        mArrayList.clear();
                        for(Order order : snapshots.toObjects(Order.class)){
                            Log.e("  item", order.getItems().get(0).getName());

                            if(order.getCheck() == false){// 사장님이 체크 안했으면
                                playTTS(order.getItems().get(0).getName());
                                mArrayList.add(order);// = (ArrayList<Order>) snapshots.toObjects(Order.class);

                            }
                        }
                        mAdapter.setItems(mArrayList);
//                        Log.e("  이벤트수신? not", ""+snapshots.toObjects(Order.class).get(0).getOrderToString());

//                        mAdapter.notifyDataSetChanged();


                    }
                });


//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // 성공
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                mArrayList.add(/*order*/document.toObject(Order.class));
//                                Log.e("  ceo_create", mArrayList.size()+"");
//                            }
//                        }
//                    }
//                });
//

    }

/*
    public */
/*static*//*
 class GetDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_main_list);
            mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            Log.e("   post", ""+mArrayList.size());
            mAdapter = new PaymentListAdapter(mArrayList); // 빈 생성자에서 DB 연동해서 가져오면 되겠지?
            mRecyclerView.setAdapter(mAdapter);

        }

        @Override
        protected String doInBackground(Void... voids) {

            return getData();//null;
        }
    }
*/
}
