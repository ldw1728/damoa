package com.project.damoa2020.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.ui.main.groupTab.AddGroupActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            View root = null;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                 root = inflater.inflate(R.layout.fragment_map, container, false);
                break;
            case 2 :
                root = inflater.inflate(R.layout.fragment_group, container, false);
                break;
            case 3 :
                root = inflater.inflate(R.layout.fragment_friends, container, false);
                break;
            case 4:
                root = inflater.inflate(R.layout.fragment_addgroup, container, false);
                break;
            case 5 :
                root = inflater.inflate(R.layout.fragment_addgroupfriends, container, false);
                break;
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { //view가 생성되고 각 해당 탭의 객체를 생성.
        super.onViewCreated(view, savedInstanceState);
        switch (getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                ((MainActivity)getActivity()).createTabs(1, this);
                break;
            case 2:
                ((MainActivity)getActivity()).createTabs(2, null);
                break;
            case 3:
                ((MainActivity)getActivity()).createTabs(3, null);
                break;
            case 4:
                ((AddGroupActivity)getActivity()).InitGroupInfoFragment();
                break;
            case 5:
                ((AddGroupActivity)getActivity()).InitGroupFriendsFragment();
                break;

        }
    }
}