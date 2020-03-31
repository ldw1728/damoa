package com.project.damoa2020.ui.main.groupTab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.controller.DBC;
import com.project.damoa2020.ui.main.PlaceholderFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GroupTab {

    private MainActivity main;

    private FloatingActionButton fab_addGroup;
    private FloatingActionButton fab_searchGroup;
    private RecyclerView rv_groupList;
    private RV_GroupListAdapter adapter;

    private ArrayList<GroupInfo> groups;

    public ArrayList<GroupInfo> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<GroupInfo> groups) {
        this.groups = groups;
    }

    public GroupTab(MainActivity main) {
        this.main = main;

        init();

    }

    public void init() {
        fab_addGroup = main.findViewById(R.id.fab_addGroup);
        fab_searchGroup = main.findViewById(R.id.fab_searchGroup);
        rv_groupList = main.findViewById(R.id.rv_groupList);
        setEvent();
        initGroupRecyclerView();
        getGroupsFromDB();

    }

    public void initGroupRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(main);
        rv_groupList.setLayoutManager(lm);
        rv_groupList.addItemDecoration(new DividerItemDecoration(main, lm.getOrientation()));
        groups = new ArrayList<>();
        adapter = new RV_GroupListAdapter(main, groups);
        rv_groupList.setAdapter(adapter);

    }

    public void getGroupsFromDB() {
        groups.clear();
        Log.d("getGroupsFromDB : ", "enter the getGroupsFromDB");
        DBC.getDataFromDB("groups", new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                if (o == null) {
                    adapter.notifyDataSetChanged();
                } else {
                    /*for (GroupInfo g : (ArrayList<GroupInfo>) o) {
                        groups.add(g);
                    }*/
                    HashSet temp = new HashSet((ArrayList<GroupInfo>) o); //중복 방지를 위해
                    groups.clear();
                    groups.addAll(temp);
                    Log.d("getGroupsFromDB : ", groups.size() + "");
                    adapter.setGroups(groups);
                    adapter.notifyDataSetChanged();
                }
                main.notifyingChangedGroups();
            }
        });
    }

    public void setEvent() {

        fab_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main, AddGroupActivity.class);
                main.startActivityForResult(intent, 3);
            }
        });

        fab_searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void addGroup(GroupInfo group) {
        DBC.addDataToDB(main, "groups", group);
    }

    public void deleteGroup(final GroupInfo group) {
        HashMap<String, Boolean> tmp = group.getParticipants();
        tmp.remove(MainActivity.loginUser.getEmail());

        final Map<String, Object> data = new HashMap();
        data.put("participants", tmp);
        DBC.updateData(group, data, new Consumer<Boolean>() { //변경된 그룹정보를 db에 반영
            @Override
            public void accept(Boolean aBoolean) { //적용후 변경된 유저정보(해당그룹이 제외된 그룹리스트)를 db에 반영.
                if (aBoolean) {
                    ArrayList<String> tmp = MainActivity.loginUser.getGroups();
                    tmp.remove(group.getGroupID());
                    data.clear();
                    data.put("groups", tmp);
                    DBC.updateUser(MainActivity.loginUser, data, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (aBoolean)
                                Toast.makeText(main, "정상적으로 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
