package com.project.damoa2020.ui.main.groupTab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.controller.DBC;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SearchGroupActivity extends AppCompatActivity {

    private String groupName;
    private TextView txt_loding;
    private ArrayList<GroupInfo> foundGroups;
    private RV_GroupListAdapter adapter;
    private RecyclerView rv_foundGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        init();
        final String gName = getIntent().getStringExtra("groupsName");
        groupName = "search "+gName;
        Log.d("groupName : ", groupName);
        DBC.getDataFromDB(groupName, new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                foundGroups = (ArrayList<GroupInfo>)o;
                if(foundGroups.size() > 0){
                    setAdapter();
                    txt_loding.setText('"'+gName+'"' +"의 검색결과.");
                }else{
                    txt_loding.setText("그룹을 찾을 수 없습니다.");
                }
            }
        });
    }

    public void init(){
        txt_loding = findViewById(R.id.txt_loding);
        rv_foundGroup = findViewById(R.id.rv_foundGroup);
        foundGroups = new ArrayList<>();
    }

    public void setAdapter(){
        adapter = new RV_GroupListAdapter(this, foundGroups);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv_foundGroup.setLayoutManager(lm);
        rv_foundGroup.addItemDecoration(new DividerItemDecoration(this, lm.getOrientation()));
        rv_foundGroup.setAdapter(adapter);
    }

    public void setResult(GroupInfo g){
        Intent intent = new Intent();
        intent.putExtra("group", g);
        setResult(4, intent);
        finish();
    }
}
