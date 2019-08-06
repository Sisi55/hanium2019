package com.example.change;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyRecoFragment extends Fragment {

    public static MyRecoFragment fragment = new MyRecoFragment();

    public static Fragment getFragment() {
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_reco, container, false);
        return view;
    }
}

