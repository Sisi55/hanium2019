package com.example.ceo;

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

import com.example.ceo.model.CafeItem;
import com.example.ceo.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        public PaymentListAdapter(ArrayList<Order> list){
            this.mList=list;
            Log.e("  adapter", ""+mList.size());
        }
        public PaymentListAdapter(){
            // 멤버 mList -- ArrayList<Order>
//            mList

            FirebaseFirestore.getInstance().collection("order")
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



        // Adapter의 특정 위치에 있는 데이터를 보여줘야 할 때 호출
        @Override
        public void onBindViewHolder(@NonNull ListViewHolder viewholder, int position) {

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
        }

        @Override
        public int getItemCount() {
            return  mList.size();//(null != mList ? mList.size() : 0);
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


    }

    private void/*ArrayList<Order>*/ getData(){

        FirebaseFirestore.getInstance().collection("order")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            // 성공
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                                Log.e("  ceo_create", document.toObject(Order.class).getOrderToString());
//                                    order.setOrderToString(((Map)document.getData()).get("orderToString").toString());
                                mArrayList.add(/*order*/document.toObject(Order.class));
                                Log.e("  ceo_back list size", mArrayList.size()+"");
                            }
                            mAdapter.setItems(mArrayList);
                        }
                    }
                });

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
