package com.example.change;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.example.change.model.CafeItem;
import com.example.change.model.Order;
import com.example.change.setting.AppSetting;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentListFragment extends Fragment {

// 시현

    // 자기자신 - 싱글톤
    public static PaymentListFragment fragment = new PaymentListFragment();


    public static PaymentListFragment getFragment(){
        return fragment;
    }
    //end method


    public PaymentListFragment() {
        // Required empty public constructor
    }
    //end 생성자


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_main_list);
        add_menu=(Button)view.findViewById(R.id.add_menu_btn);
        order_btn=(Button)view.findViewById(R.id.order_btn);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        // MainActivity에서 RecyclerView의 데이터에 접근시 사용됩니다.
        // mArrayList = new ArrayList<>();
        // 지연 : intet 값 받아오기
        // 시현 : getIntent 부분은 해당 클래스의 멤버함수로 대체될듯. 이동 전에.
//        mArrayList=(ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist");
        Toast.makeText(getActivity(), mArrayList.size()+"", Toast.LENGTH_SHORT).show();
        // RecyclerView를 위해 CustomAdapter를 사용합니다.
        mAdapter = new PaymentListAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        // add_button을 클릭하면 메뉴판으로 넘어간다.
        add_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Intent intent = new Intent(PaymentListActivity.this, MenuListActivity.class);
                intent.putExtra("shoplist",mArrayList); // 지연 : 담은 메뉴 상태 유지
                startActivity(intent);
*/

                // 시현 acivity : intent > fragment 간 이동으로 임시 변경
                MenuListFragment fragment = MenuListFragment.getFragment();
                fragment.setCafeItemArrayList(mArrayList); // arrayList ? payment list fragment 에서 온다

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.body, fragment)
                        .commit();

            }
        });


        // 결제 버튼을 누르면 파이어베이스에 Order을 올린다.
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아무 데이터 넣을게요
                Map<String,Double> emotion=new HashMap<String,Double>();
                emotion.put("happiness",0.9);
                // 감정 최고기록 받아오기ㅠㅠㅠㅠ
                //AppSetting.faceServiceClient.detect(AppSetting.faceServiceClient.getPerson(),true,true, FaceServiceClient.FaceAttributeType.Emotion);

                Map<String,Double> weather=new HashMap<String,Double>();
                weather.put("humidity",0.5);
                // calender로 현재 날짜 알아오기
                SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
                String format_time1 = format1.format (System.currentTimeMillis());
                String today=format_time1;
                String guest= AppSetting.personUUID;
                Order order = new Order(emotion,weather,mArrayList,today,guest);
                FirebaseDatabase.getInstance().getReference().child("order").push().setValue(order);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setMessage("주문되었습니다")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                builder.show();
            }
        });

        return view;
    }
    //end on create view

    public void setCafeItemArrayList(ArrayList<CafeItem> cafeItemArrayList){
        mArrayList = cafeItemArrayList;
    }
    //end method




// 유빈

    private RecyclerView mRecyclerView;
    private ArrayList<CafeItem> mArrayList; // 인텐트로 받아올 값
    private PaymentListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Button add_menu;
    private Button order_btn;
    StorageReference mStorageRef; // 지연 : 파이어베이스

    private int count = -1;


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


    }//end class


}//end class
