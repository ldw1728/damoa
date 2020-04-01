package com.project.damoa2020.ui.main.groupTab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.project.damoa2020.R;
import com.project.damoa2020.controller.DBC;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SearchGroupActivity extends AppCompatActivity {

    private String groupName;
    private TextView txt_loding;
    private ArrayList<GroupInfo> foundGroups;
    private SearchGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        init();
        groupName = getIntent().getStringExtra("groupName");
        groupName += "search "+groupName;

        DBC.getDataFromDB(groupName, new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                txt_loding.setVisibility(View.INVISIBLE);
                foundGroups = (ArrayList<GroupInfo>)o;
                if(foundGroups.size() > 0){

                }else{
                    txt_loding.setText("그룹을 찾을 수 없습니다.");
                    txt_loding.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void init(){
        txt_loding = findViewById(R.id.txt_loding);
        foundGroups = new ArrayList<>();
    }

    public void setAdapter(){

    }
}
