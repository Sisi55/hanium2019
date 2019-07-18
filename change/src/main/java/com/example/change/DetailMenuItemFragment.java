package com.example.change;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.change.databinding.FragmentDetailMenuItemBinding;
import com.example.change.model.CafeItem;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMenuItemFragment extends Fragment {

// 시현
    // 자기자신 - 싱글톤
    public static DetailMenuItemFragment fragment = new DetailMenuItemFragment();


    public static DetailMenuItemFragment getFragment(){
        return fragment;
    }
    //end method



    public DetailMenuItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_menu_item, container, false);
    }
    //end on create view


//    CafeItem model;
    public void setCafeItem(CafeItem cafeItem){
        this.model = cafeItem;
    }
    //end method


//    ArrayList<CafeItem> cafeItemArrayList;
    public void setCafeItemArrayList(ArrayList<CafeItem> cafeItemArrayList){
        shoplist = cafeItemArrayList;
    }
    //end method





//
// 장바구니 배열
// 단,!!!!!!!! 사용자가 바뀌면 초기화 해주어야 한다.
    FragmentDetailMenuItemBinding binding;

    ArrayList<CafeItem> shoplist;
    CafeItem model; // 클릭한 메뉴
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_detail_menu_item);

        // 해시 키값 알면 해당 데이터 가져올 수 있나 ?
        // 테스트

        // 지연 : 인텐트 테스트/////
//        model=(CafeItem)getIntent().getSerializableExtra("detail");
        // 시현 : 테스트인가요? 그래도 일단 옮겨놓겠습니다


        // 지연 : 인텐트 테스트/////
        // 인텐트에서 받아온 정보 출력
        String menuTitle=model.getName();
        int menuPrice=model.getPrice();

        binding.tvMenuTitle.setText(menuTitle);
        binding.tvMenuPrice.setText(menuPrice+"");
        // 이미지
        Glide.with(binding.imgMenu.getContext())
                .load(model.getImageUrl())
                .into(binding.imgMenu);

        // 목록 버튼 누르면 뒤로 가기. < MenuList fragment 로 이동한다 ?
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();

                // 시현
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.body, MenuListFragment.getFragment())
                        .commit();

            }
        });
        shoplist=new ArrayList<CafeItem>();

        // 담기 버튼 누르면 인텐트 발생
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 세부 옵션 선택은 나중에 하자.
                // 해당 메뉴를 arraylist에 넣는다.
//                if((ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist")!=null){

                // 시현 : 이걸로 분기할거면 화면 이동할 때 shoplist에 null 할당해놓으면 되겠죠? 아닌가봐요..
                if(shoplist != null){
                    // 결제페이지에서 넘어왔다면,
                    // 위에 생성했던 shoplist에 결제목록창에 있던 메뉴들을 넣어준다.
                    ArrayList<CafeItem> newList = new ArrayList<CafeItem>(shoplist); // 시현 : 기존에 추가하는 로직 ?
                    // 시현 : payment에서 넘어올 때 기존에 추가하는 로직 넣겠습니다!
                    newList.addAll((ArrayList<CafeItem>) getIntent().getSerializableExtra("shoplist"));
                    // 이번에 담은 메뉴를 추가한다.
                    newList.add(model);

                    // 그다음에 shoplist를 intent에 추가한다.
/*
                    Intent intent = new Intent(DetailMenuItemActivity.this, PaymentListActivity.class);
                    intent.putExtra("shoplist",newList);
                    startActivity(intent);
*/
                    // 이 리스트에 추가하면 되고

                    // 시현 : DB로 대체합니다. ArrayList 바로 위 3줄도 교체
                    intent.putExtra("shoplist",newList);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.body, PaymentListFragment.getFragment())
                            .commit();



//                    shoplist = null;
                }else{
                    // 처음 담기를 하는 거라면,
                    shoplist.add(model);
                    // 위에서 생성한 리스트를 넣으면 된다...
/*
                    Intent intent = new Intent(DetailMenuItemActivity.this, PaymentListActivity.class);
                    intent.putExtra("shoplist",shoplist);
                    startActivity(intent);
*/

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.body, PaymentListFragment.getFragment())
                            .commit();

                }

                // 현재 상태의 리스트 받아옴.
                // 리스트에 담은 메뉴 추가
                Toast.makeText(getActivity(), shoplist.size()+"", Toast.LENGTH_SHORT).show();
                //shoplist.add(model);
                // 결제로 넘어간다. 메뉴를 더 고르고 싶다면, paymentlistactivity에서 메뉴추가 버튼을 눌러 메뉴판으로 이동한다.

            }
        });


    }//end onCreate


}//end fragment
