package com.example.change;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    // 자기자신 - 싱글톤
    public static MainFragment fragment = new MainFragment();


    public static MainFragment getFragment(){
        return fragment;
    }
    //end method



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fragment 화면 뷰 가져오기
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        // Fragment 구성요소 뷰에 이벤트 등록

        // 메뉴 목록 화면으로 이동
        Button btnMenuList = (Button) view.findViewById(R.id.btn_menuList);
        btnMenuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.body, MenuListFragment.getFragment())
                        .commit();

            }
        });

        // 주문기록 화면으로 이동
        Button btnOrderedList = (Button) view.findViewById(R.id.btn_orderedList);
        btnOrderedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.body, OrderedListFragment.getFragment())
                        .commit();

            }
        });

        // 나추천 화면으로 이동
        Button btnMyReco = (Button) view.findViewById(R.id.btn_my_reco);
        btnMyReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.body, MyRecoFragment.getFragment())
                        .commit();

            }
        });




       return view;
    }
    //end on create view

}//end fragment
