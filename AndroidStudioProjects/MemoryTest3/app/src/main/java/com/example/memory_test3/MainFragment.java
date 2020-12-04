package com.example.memory_test3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    //別にFragmentに何かを渡したいと思ったことないんすけどね　なぜFragmentはこの形式でインスタンス生成するのかがよくわかっていない
    public static com.example.memory_test3.MainFragment newInstance(String param1, String param2) {
        com.example.memory_test3.MainFragment fragment = new com.example.memory_test3.MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(),getChildFragmentManager());
        //ここの第2引数をgetFragmentManager()とすると、最初は表示されるが、MainFragmentでreplaceしてバックキーで戻ると
        //ViewPager自体は描画されるものの、中身がどのタブでも真っ白になってしまう。これはViewPagerの中の子Fragmentをどこかで保持しており、
        //バックスタックで戻ってもSectionPagerAdapterのgetItemが呼ばれないからのようだ。
        //FragmentManagerで管理する場合は、replaceの内部でTab1やTab1AddFragmentのadd,removeはできても、Tab1にネストされているViewPager下の
        //子Fragmentはただ持ちっぱなしで、getItemとも連携出来ていないといったところだろうか？
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        //TabLayoutや、fragment_mainの app:layout_behavior="@string/appbar_scrolling_view_behavior" は
        //build.gradle(Module:app) の dependencies の implementationに 'com.google.android.material:material:1.2.1' を追加しないと認識されない
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Log.e("onViewCreated","CALL");
    }

}
