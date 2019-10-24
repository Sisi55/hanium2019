package com.example.kiosk_jnsy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiosk_jnsy.model.CafeItem;
import com.example.kiosk_jnsy.setting.AppSetting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class OrderedListActivity extends AppCompatActivity {

    List<Person> people;
    String guest="";
    Button Datebtn, Orderbtn;

    Map<String,Double> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        people=new ArrayList<Person>();
        super.onCreate(savedInstanceState);
        Log.d("23일","액티비티 넘어왔다!!");

        setContentView(R.layout.activity_ordered_list);
        // 리사이클러뷰
        final RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        //어댑터 만들기
        final OrderedListActivity.CafeItemAdapter adapter=new OrderedListActivity.CafeItemAdapter(new OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener() {
            @Override
            public void onCafeItemClicked(Person model) {
                String date_menu=(String)model.map.get("name");
                String dep="\\(";
                String x[]=date_menu.split(dep);
                final String here_name=x[0];
             //   Toast.makeText(getApplicationContext(), here_name+"ddszzzzzzzzzzzzzzzzz", Toast.LENGTH_SHORT).show();
                FirebaseFirestore.getInstance().collection("menu").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // 메뉴정보 추출
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // 메뉴 이름 바꿔야 함..
                                        if(((String)document.getData().get("name")).equals(here_name)) {
                                            Long pricen = (Long) document.getData().get("price");
                                            String body = (String) document.getData().get("body");
                                            int price = pricen.intValue();
                                            String imageUrl = (String) document.getData().get("imageUrl");
                                            CafeItem myresult=new CafeItem(here_name, price, imageUrl, body);

                                            Intent intent = new Intent(OrderedListActivity.this, DetailMenuItemActivity.class);

                                            intent.putExtra("detail",myresult);
                                            startActivity(intent);
                                            break;
                                        }
                                    }
                                } else { }
                            }
                        });

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        // 실제 데이터 넣기
        // 현재 커피공장에 있는 메뉴
        CafeItem realmenu[]=new CafeItem[9];


        // 날짜순
        Datebtn =(Button)findViewById(R.id.bydatebtn);
        // 주문 많이 시킨순
        Orderbtn = (Button)findViewById(R.id.orderedlotbtn);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        //final List<CafeItem> people=new ArrayList<>();
        db1.collection("order")
                .orderBy("today", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                guest = (String) (String)document.getData().get("guest");
                                if(guest.equals(AppSetting.personUUID)){


                                    List allList=(List) document.getData().get("items");
                                    ///////////////////////////////////////////////////////////////////////////////////////
                                    String today=(String)document.getData().get("today");
                                    // 필요 없음
                                    for(int k=0;k<allList.size();k++){
                                        Person p =new Person((Map)allList.get(k),today);
                                        people.add(p);
                                    }

                                  //  Toast.makeText(getApplicationContext(), "현재"+today, Toast.LENGTH_SHORT).show();
                                }
                            }
                            // 데이터 새로고침
                            adapter.notifyDataSetChanged();
                            // for문 다 수행 후 어댑터 설정
                            adapter.setItems(people);

                        } else {
                            Log.d("dd", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // 날짜순 정렬 (코드 깨끗하게 하는 방법 모름)
        Datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("23일","날짜 순 버튼 누름");
                // 리스트 초기화
                people.clear();
                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                //final List<CafeItem> people=new ArrayList<>();
                db1.collection("order")
                        .orderBy("today", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        guest = (String) (String)document.getData().get("guest");
                                        if(guest.equals(AppSetting.personUUID)){


                                            List allList=(List) document.getData().get("items");
                                            ///////////////////////////////////////////////////////////////////////////////////////
                                            String today=(String)document.getData().get("today");
                                            // 필요 없음
                                            for(int k=0;k<allList.size();k++){
                                                Person p =new Person((Map)allList.get(k),today);
                                                people.add(p);
                                            }

                                    //        Toast.makeText(getApplicationContext(), "현재"+today, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    // 데이터 새로고침
                                    adapter.notifyDataSetChanged();
                                    // for문 다 수행 후 어댑터 설정
                                    adapter.setItems(people);

                                } else {
                                    Log.d("dd", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        // 주문 많이 시킨순
        Orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 리스트 초기화
                Log.d("23일","주문많이시킨 순 버튼 누름");
                TextView items=(TextView)findViewById(R.id.price_text);
                // items.setVisibility(View.GONE);
                people.clear();
                FirebaseFirestore.getInstance().collection("order").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String,Integer> rank=new HashMap<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String str = (String)document.getData().get("guest");
                                        Log.e("  many order", str);
                                        // 잠시만여-지연
                                        // 카메라 연동되면 AppSetting.personUUID로 바꿀예정



                                        if(str.equals(AppSetting.personUUID)){

                                            String orderToString=(String)document.getData().get("orderToString");

                                            // rank 맵에 이미 있다면 있는값에 추가
                                            if(rank.get(orderToString)!=null){
                                                int num=rank.get(orderToString);
                                                // Toast.makeText(MainActivity.this, orderToString+"추가", Toast.LENGTH_SHORT).show();
                                                rank.put(orderToString,num+1);
                                            }// 없다면 1로 추가
                                            else {
                                                rank.put(orderToString, 1);
                                            }
                                        }
                                    }

                                    // value 내림차순으로 정렬하고, value가 같으면 key 오름차순으로 정렬
                                    List<Map.Entry<String, Integer>> list = new LinkedList<>(rank.entrySet());

                                    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                                        @Override
                                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                                            int comparision = (o1.getValue() - o2.getValue()) * -1;
                                            return comparision == 0 ? o1.getKey().compareTo(o2.getKey()) : comparision;
                                        }
                                    });

                                    // 순서유지를 위해 LinkedHashMap을 사용
                                    Map<String, Integer> sortedMap = new LinkedHashMap<>();
                                    for(Iterator<Map.Entry<String, Integer>> iter = list.iterator(); iter.hasNext();){
                                        Map.Entry<String, Integer> entry = iter.next();
                                        sortedMap.put(entry.getKey(), entry.getValue());
                                    }
                                    for ( String key : sortedMap.keySet() ) {
                                        Log.d("출려쿠",key);
                                        people.add(new Person(key));
                                    }
                                    // 데이터 새로고침
                                    adapter.notifyDataSetChanged();
                                    // for문 다 수행 후 어댑터 설정
                                    adapter.setItems(people);





                                } else {
                                    Log.d("dd", "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        });



        //Toast.makeText(this, "음료 추가", Toast.LENGTH_SHORT).show();

        adapter.setItems(people);
        Log.d("백지연","dd");

    }
    private static class CafeItemAdapter extends RecyclerView.Adapter<OrderedListActivity.CafeItemAdapter.CafeItemViewHolder> {
        Person globalName;

        CafeItem myresult; // 나 추천 결과
        String myname; // 나 추천 결과 이름
        String arr[];// 나 추천 split
        interface OnCafeItemClickListener {
            void onCafeItemClicked(Person model);
        }

        private OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener mListener;

        private List<Person> mItems = new ArrayList<>();

        public CafeItemAdapter() {}

        public CafeItemAdapter(OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Person> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderedListActivity.CafeItemAdapter.CafeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            final OrderedListActivity.CafeItemAdapter.CafeItemViewHolder viewHolder = new OrderedListActivity.CafeItemAdapter.CafeItemViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Person item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onCafeItemClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull OrderedListActivity.CafeItemAdapter.CafeItemViewHolder holder, int position) {
            Person item = mItems.get(position);

            globalName=item;
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.guest.setText(item.date);

            // 날짜순 출력
            if((Map)item.map!=null) {

                Map<String, Double> op = (Map) item.map.get("options");
                holder.items.setText(item.map.get("name") + "(휘핑 :" + op.get("휘핑") + " / 샷 : " + op.get("샷")
                        + " / 온도 : "+op.get("온도")+" / 텀블러 : "+op.get("텀블러")+" / 빨대 : "+op.get("빨대")+" / 얼음 : "+op.get("얼음"));
            }else{
                // 꼼수라면
                //많이먹은 순

                //holder.items.setVisibility(View.GONE);
                holder.items.setText("↑ 메뉴 보기");
                holder.items.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String date=globalName.date;
                        arr=date.split("/");
                        // arr[0] : 메뉴이름

                        // 이 메뉴의 손님의 옵션 설정
                        String[] op1=arr[1].split(":"); // option1 휘핑:0.0
                        final double op1d=Double.parseDouble(op1[1]);
                        String[] op2=arr[2].split(":"); // option2
                        final double op2d=Double.parseDouble(op2[1]);
                        String[] op3=arr[2].split(":"); // option2
                        final double op3d=Double.parseDouble(op3[1]);
                        String[] op4=arr[2].split(":"); // option2
                        final double op4d=Double.parseDouble(op4[1]);
                        String[] op5=arr[2].split(":"); // option2
                        final double op5d=Double.parseDouble(op5[1]);
                        String[] op6=arr[2].split(":"); // option2
                        final double op6d=Double.parseDouble(op6[1]);





                        // 객체 생성해서 인텐트로 보냄
                        HashMap map=new HashMap<String,Double>();
                        map.put("휘핑",op1d);
                        map.put("샷",op2d);
                        map.put("온도",op3d);
                        map.put("텀블러",op4d);
                        map.put("빨대",op5d);
                        map.put("얼음",op6d);

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

                                                    break;
                                                }

                                            }

                                            //Toast.makeText(getApplicationContext(), "이동시키고싶어", Toast.LENGTH_SHORT).show();
                                                    /*
                                                    Intent intent = new Intent(getApplicationContext(), DetailMenuItemActivity.class);

                                                    intent.putExtra("detail",myresult);
                                                    startActivity(intent);
                                                    */
                                        } else { }
                                    }
                                });

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class CafeItemViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            TextView guest;
            TextView items;
            //TextView img;

            public CafeItemViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                guest=itemView.findViewById(R.id.name_text);
                items=itemView.findViewById(R.id.price_text);

                //  img=itemView.findViewById(R.id.img_text);
            }
        }
    }
    // 테스트
    class Person {
        Map map;
        String date="aa";
        public Person(){}
        public Person(Map map,String date){
            this.map=map;
            this.date=date;
        }
        // 생성자를 하나더 만들겠따.
        public Person(String date){
            // 원래 date는 날짜를 위한 변수였지만, 여기서는 메뉴 orderToString을 적겠다.꼼수...
            this.date=date;
            this.map=null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person person = (Person) o;
            return Objects.equals(map, person.map) &&
                    Objects.equals(date, person.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(map, date);
        }
    }


    public static List sortByValue(final Map map) {

        Log.d("시바","나오냐");
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
        Collections.sort(list,new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }

        });

        Collections.reverse(list); // 주석시 오름차순

        return list;

    }



}
