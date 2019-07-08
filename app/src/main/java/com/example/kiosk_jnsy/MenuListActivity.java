package com.example.kiosk_jnsy;
// 지연
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

import com.example.kiosk_jnsy.model.MenuItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        // 리사이클러뷰
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        //어댑터 만들기
        MenuItemAdapter adapter=new MenuItemAdapter(new MenuItemAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(MenuItem model) {
                // 메뉴판에 있는 메뉴를 선택하면
                Toast.makeText(MenuListActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                // 상세메뉴 페이지로 넘어감
                Intent intent = new Intent(MenuListActivity.this, DetailMenuItemActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        // 실제 데이터 넣기
        // 현재 커피공장에 있는 메뉴
        MenuItem realmenu[]=new MenuItem[9];

        List<MenuItem> people=new ArrayList<>();

        // 배열에 음료 데이터 넣기
        // 1. 파이어베이스에 이 정보를 넣자
        DatabaseReference mRootRef= FirebaseDatabase.getInstance().getReference();
        DatabaseReference mConditionRef=mRootRef.child("condition1");

        // 2. 읽어와서 이 배열에 넣자
        // 근데 메뉴판은 실시간데이터베이스 일 필요가?
        people.add(new MenuItem("아메리카노",2000,""));
        people.add(new MenuItem("카페라떼",3000,""));
        people.add(new MenuItem("바닐라라떼",3000,""));
        people.add(new MenuItem("헤이즐넛 라떼",2000,""));
        people.add(new MenuItem("캐러멜 라떼",3000,""));
        people.add(new MenuItem("초콜릿 라떼",3000,""));
        people.add(new MenuItem("허니 라떼",2000,""));
        people.add(new MenuItem("유기농 아가베라떼",3000,""));
        people.add(new MenuItem("카페 하노이",3000,""));


        Toast.makeText(this, "음료 추가", Toast.LENGTH_SHORT).show();

        adapter.setItems(people);
        Log.d("백지연","dd");
    }
    private static class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {
        interface OnMenuItemClickListener {
            void onMenuItemClicked(MenuItem model);
        }

        private OnMenuItemClickListener mListener;

        private List<MenuItem> mItems = new ArrayList<>();

        public MenuItemAdapter() {}

        public MenuItemAdapter(OnMenuItemClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<MenuItem> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            final MenuItemViewHolder viewHolder = new MenuItemViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final MenuItem item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onMenuItemClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
            MenuItem item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.name.setText(item.getName());
            holder.price.setText(item.getPrice()+"");
            holder.img.setText(item.getImg()+"");
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class MenuItemViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            TextView name;
            TextView price;
            TextView img;

            public MenuItemViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                name=itemView.findViewById(R.id.name_text);
                price=itemView.findViewById(R.id.price_text);
                img=itemView.findViewById(R.id.img_text);
            }
        }
    }
}
