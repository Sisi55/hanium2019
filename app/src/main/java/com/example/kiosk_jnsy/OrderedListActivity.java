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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        // 리사이클러뷰
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        //어댑터 만들기
        OrderedListActivity.CafeItemAdapter adapter=new OrderedListActivity.CafeItemAdapter(new OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener() {
            @Override
            public void onCafeItemClicked(CafeItem model) {
                // 주문기록에 있는 메뉴를 선택하면
                Toast.makeText(OrderedListActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                // 상세메뉴 페이지로 넘어감
                Intent intent = new Intent(OrderedListActivity.this, DetailMenuItemActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        // 실제 데이터 넣기
        // 현재 커피공장에 있는 메뉴
        CafeItem realmenu[]=new CafeItem[9];

        List<CafeItem> people=new ArrayList<>();

        // 배열에 음료 데이터 넣기
        // 1. 파이어베이스에 이 정보를 넣자
        DatabaseReference mRootRef= FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef=mRootRef.child("condition1");

        // 2. 읽어와서 이 배열에 넣자
        // 근데 메뉴판은 파베인 이유가 뭐지
        people.add(new CafeItem("아메리카노",2000,"","aaa"));
        people.add(new CafeItem("카페라떼",3000,"","bbb"));


        Toast.makeText(this, "음료 추가", Toast.LENGTH_SHORT).show();

        adapter.setItems(people);
        Log.d("백지연","dd");
    }
    private static class CafeItemAdapter extends RecyclerView.Adapter<OrderedListActivity.CafeItemAdapter.CafeItemViewHolder> {
        interface OnCafeItemClickListener {
            void onCafeItemClicked(CafeItem model);
        }

        private OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener mListener;

        private List<CafeItem> mItems = new ArrayList<>();

        public CafeItemAdapter() {}

        public CafeItemAdapter(OrderedListActivity.CafeItemAdapter.OnCafeItemClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<CafeItem> items) {
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
                        final CafeItem item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onCafeItemClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull OrderedListActivity.CafeItemAdapter.CafeItemViewHolder holder, int position) {
            CafeItem item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.name.setText(item.getName());
            holder.price.setText(item.getPrice()+"");
            holder.img.setText(item.getImageUrl()+"");
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class CafeItemViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            TextView name;
            TextView price;
            TextView img;

            public CafeItemViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                name=itemView.findViewById(R.id.name_text);
                price=itemView.findViewById(R.id.price_text);
                img=itemView.findViewById(R.id.img_text);
            }
        }
    }
}
