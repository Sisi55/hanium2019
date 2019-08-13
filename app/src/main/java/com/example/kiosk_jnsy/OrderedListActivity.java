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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiosk_jnsy.model.CafeItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderedListActivity extends AppCompatActivity {

    List<Person> people;
    String guest="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        people=new ArrayList<Person>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_list);
        // 리사이클러뷰
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        //어댑터 만들기
        final OrderedListActivity.CafeItemAdapter adapter=new OrderedListActivity.CafeItemAdapter(new OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener() {
            @Override
            public void onCafeItemClicked(Person model) {
                // 주문기록에 있는 메뉴를 선택하면
                //Toast.makeText(OrderedListActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                // 상세메뉴 페이지로 넘어감
                Intent intent = new Intent(OrderedListActivity.this, DetailMenuItemActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        // 실제 데이터 넣기
        // 현재 커피공장에 있는 메뉴
        CafeItem realmenu[]=new CafeItem[9];

        //final List<CafeItem> people=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("order").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // uuid가 아래와 같은 경우만 add함///////////////////////////////////////////////////

                                guest = (String) (String)document.getData().get("guest");
                                if(guest.equals("63cf6c6d-86ef-4647-b941-1b0bf187065f")){
                                   // Map items = (Map)snapshot.child("items").child("0").getValue();
                                    /////////////////////////////////////////////////////////////////////////////////////
                                    //Map items = document.getData().get("items").collection("0");
                                    List list = (List) document.getData().get("items");
                                    HashMap items = (HashMap) list.get(0);


                                   ///////////////////////////////////////////////////////////////////////////////////////
                                    String today=(String)document.getData().get("today");
                                    // 필요 없음
                        /*
                        Map<String,Double> emotion=(Map)snapshot.child("emotion").getValue();
                        Map<String,Double> weather=(Map)snapshot.child("weather").getValue();
                        */
                                    // Map tt = (Map)snapshot.child("items").child("0").getValue();
                                    // Toast.makeText(getActivity(), tt.get("name")+"메뉴임", Toast.LENGTH_SHORT).show();


                                    //String imageUrl=(String)snapshot.child("imageUrl").getValue();

                                    // 객체 형태로 받아와야 함. 오류...
                                    //Order ciObject = dataSnapshot.getValue(Order.class);
                                    Person p=new Person(items,today);
                                    people.add(p);
                                    Toast.makeText(getApplicationContext(), "현재"+today, Toast.LENGTH_SHORT).show();
                                }
                            }
                            // for문 다 수행 후 어댑터 설정
                            adapter.setItems(people);


                        } else {
                            Log.d("dd", "Error getting documents: ", task.getException());
                        }
                    }
                });
 /*
        FirebaseDatabase.getInstance().getReference().child("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // uuid가 아래와 같은 경우만 add함///////////////////////////////////////////////////
                    guest = (String) snapshot.child("guest").getValue();
                    if(guest.equals("63cf6c6d-86ef-4647-b941-1b0bf187065f")){
                        Map items = (Map)snapshot.child("items").child("0").getValue();
                        String today=(String)snapshot.child("today").getValue();

                        // 필요 없음
                        /*
                        Map<String,Double> emotion=(Map)snapshot.child("emotion").getValue();
                        Map<String,Double> weather=(Map)snapshot.child("weather").getValue();
                        */
                        // Map tt = (Map)snapshot.child("items").child("0").getValue();
                        // Toast.makeText(getActivity(), tt.get("name")+"메뉴임", Toast.LENGTH_SHORT).show();


                        //String imageUrl=(String)snapshot.child("imageUrl").getValue();

                        // 객체 형태로 받아와야 함. 오류...
                        //Order ciObject = dataSnapshot.getValue(Order.class);
        /*
                        Person p=new Person(items,today);
                        people.add(p);
                        Toast.makeText(getApplicationContext(), "현재"+today, Toast.LENGTH_SHORT).show();
                    }


                }
                // for문 다 수행 후 어댑터 설정
                adapter.setItems(people);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/

        Toast.makeText(this, "음료 추가", Toast.LENGTH_SHORT).show();

        adapter.setItems(people);
        Log.d("백지연","dd");
    }
    private static class CafeItemAdapter extends RecyclerView.Adapter<OrderedListActivity.CafeItemAdapter.CafeItemViewHolder> {
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
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.guest.setText(item.date);

            Map<String,Double> op=(Map)item.map.get("options");

            holder.items.setText(item.map.get("name")+"(휘핑 :"+op.get("휘핑")+" / 샷 : "+op.get("샷")+")");
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
    }
}
