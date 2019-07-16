package com.example.kiosk_jnsy;
// 유빈
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiosk_jnsy.model.CafeItem;

import java.util.ArrayList;

public class PaymentListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<CafeItem> mArrayList; // 인텐트로 받아올 값
    private PaymentListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Button add_menu;

    private int count = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);



        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_main_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // MainActivity에서 RecyclerView의 데이터에 접근시 사용됩니다.
        // mArrayList = new ArrayList<>();
        // 지연 : intet 값 받아오기
        mArrayList=(ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist");
        Toast.makeText(this, mArrayList.size()+"", Toast.LENGTH_SHORT).show();
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
                startActivity(intent);
            }
        });
    }

    class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ListViewHolder> {

        private ArrayList<CafeItem> mList;

        public class ListViewHolder extends RecyclerView.ViewHolder
        {
            protected TextView order;
            protected Button delete;

            public ListViewHolder(View view){
                super(view);
                order=(TextView)view.findViewById(R.id.list_tv);
                delete=(Button)view.findViewById(R.id.delete_btn);
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

            viewholder.order.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

            viewholder.order.setGravity(View.TEXT_ALIGNMENT_CENTER); // 지연 : CENTER 수정

            viewholder.order.setText(mList.get(position).getName()); // 지연 : getOrder() 수정

            viewholder.delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    deleteItemFromList(v,pos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != mList ? mList.size() : 0);
        }

        private void deleteItemFromList(View v, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setMessage("메뉴를 삭제하겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("CONFIRM",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mList.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }


    }
}
