package com.example.change;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.model.CafeItem;
import com.example.change.model.Order;
import com.example.change.setting.AppSetting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.valueOf;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderedListFragment extends Fragment {

    List<Person> people;
    String guest="";
    // 시현
    // 자기자신 - 싱글톤
    public static OrderedListFragment fragment = new OrderedListFragment();


    public static OrderedListFragment getFragment(){
        return fragment;
    }
    //end method


    public OrderedListFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView; View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        people=new ArrayList<Person>();
        // 파이어베이스에서 읽어오기

        final OrderedListFragment.OrderAdapter adapter;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ordered_list, container, false);

        //어댑터 만들기
        adapter=new OrderedListFragment.OrderAdapter(new OrderedListFragment.OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClicked(Person model) {
                // 주문기록에 있는 메뉴를 선택하면

                // 상세메뉴 페이지로 넘어감
/*
                Intent intent = new Intent(OrderedListActivity.this, DetailMenuItemActivity.class);
                startActivity(intent);
*/

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.body, DetailMenuItemFragment.getFragment())
                        .commit();

            }
        });
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);

        // 배열에 음료 데이터 넣기

        // 2. 읽어와서 이 배열에 넣자

        Toast.makeText(getActivity(), "음료 추가", Toast.LENGTH_SHORT).show();
        // 파이어베이스에서 읽
        // 데이터베이스 읽기 #2. Single ValueEventListener
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
                        Person p=new Person(items,today);
                        people.add(p);
                        Toast.makeText(getActivity(), "현재"+today, Toast.LENGTH_SHORT).show();
                    }


                }
                // for문 다 수행 후 어댑터 설정
                adapter.setItems(people);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Toast.makeText(getActivity(), "종료"+people.size(), Toast.LENGTH_SHORT).show();
        Log.d("백지연","dd");

//        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
        return view;
    }
    //end on create view


//

    @Override // on create view 로 대체합니다 오류나서
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_menu_list);
    }
    private static class OrderAdapter extends RecyclerView.Adapter<OrderedListFragment.OrderAdapter.OrderViewHolder> {
        interface OnOrderClickListener {
            void onOrderClicked(Person model);
        }

        private OrderedListFragment.OrderAdapter.OnOrderClickListener mListener;

        private List<Person> mItems = new ArrayList<>();

        public OrderAdapter() {}

        public OrderAdapter(OrderedListFragment.OrderAdapter.OnOrderClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Person> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderedListFragment.OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            final OrderedListFragment.OrderAdapter.OrderViewHolder viewHolder = new OrderedListFragment.OrderAdapter.OrderViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Person item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onOrderClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull OrderedListFragment.OrderAdapter.OrderViewHolder holder, int position) {
            Person item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
           // //////////////////////////// holder.guest.setText(item.get("today")+""); // 주문날짜
            String s="";
            CafeItem ca;
            //ArrayList<CafeItem> list=item.getItems();

            //holder.emotion.setText(item.getEmotion()+"");

            //holder.items.setText(list.get("샷")+"");
            //int t=list.indexOf("title");
            //String title=

            // arraylist에는 cafeitem 여러개가 있다
            //

            //JSONArray jarray = new JSONObject(item);
            /*
            JSONObject jObject = jarray.getJSONObject(0);
            description = jObject.optString("description");

            JSONObject main=new JSONObject(result).getJSONObject("main");
            humidity = main.optString("humidity");
            temp = main.optString("temp");

            */
            // 자기 주문기록에는 날짜와 메뉴만 나오면 될듯
            //String s="";
            // CafeItem ca;
/*
            for(int i=0;i<list.size();i++){
                try {
                    Log.d("타입", (list.get(i)).getName());
                }catch (Exception e){
                    Log.d("오류",e.getMessage());
                }
            }
*/
          //  Object[] tttt=item.getItems().toArray();
            holder.guest.setText(item.date);

             Map<String,Double> op=(Map)item.map.get("options");

            holder.items.setText(item.map.get("name")+"(휘핑 :"+op.get("휘핑")+" / 샷 : "+op.get("샷")+")");
             // holder.items.setText(tttt[0]+"");
            //holder.emotion.setText(item.getEmotion()+"");
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class OrderViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            TextView guest;
            TextView items;

            TextView emotion;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                guest=itemView.findViewById(R.id.name_text);
                items=itemView.findViewById(R.id.price_text);
                //emotion=itemView.findViewById(R.id.img_text);
            }
        }//end class
    }//end class

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
}//end fragment