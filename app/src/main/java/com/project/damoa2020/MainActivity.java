package com.project.damoa2020;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.project.damoa2020.controller.DBC;
import com.project.damoa2020.ui.LoginActivity;
import com.project.damoa2020.ui.main.SectionsPagerAdapter;
import com.project.damoa2020.ui.main.friendsTab.FriendsTab;
import com.project.damoa2020.ui.main.groupTab.GroupInfo;
import com.project.damoa2020.ui.main.groupTab.GroupTab;
import com.project.damoa2020.ui.main.mapTab.MapController;
import com.project.damoa2020.ui.main.mapTab.MapTab;

import static com.project.damoa2020.controller.DBC.callOnChangeData;
import static com.project.damoa2020.ui.main.mapTab.MapTab.PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    public static User loginUser; //앱에서 전체적으로 로드될 사용자 정보.

    private MapTab mapTab;
    private FriendsTab friendsTab;
    private GroupTab groupTab;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUser = (User) getIntent().getSerializableExtra("user"); //로그인유저의 정보.

        if (!callOnChangeData)
            DBC.onChangeData(this);

        //viewPager생성.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Object o = data.getSerializableExtra("group");
            switch (requestCode) {
                case 3:
                    MapController.makingGroup = true;
                    viewPager.setCurrentItem(0);
                    createTabs(1, mapTab.getFragment());
                    mapTab.setCreatingGroup((GroupInfo)o);
                    break;
                case 4:
                    groupTab.addFoundGroup((GroupInfo)o);
                    break;
            }
        }
    }

    public void addGroup(GroupInfo g){
        groupTab.createGroup(g);
        MapController.makingGroup = false;
        createTabs(1, mapTab.getFragment());
    }

    public void refresh_LoginUserInfo() {

        Log.d("refresh_LoginUserInfo() : ", "called refresh_LoginUserInfo");

        if (friendsTab != null)
            friendsTab.getFriendsFromDB();

        if (groupTab != null) {
            Log.d("refresh_LoginUserInfo : ", "groupTab");
            groupTab.getGroupsFromDB();
        }
    }

    public void createTabs(int index, Fragment frag) {
        switch (index) {
            case 1:
                mapTab = new MapTab(this, frag);
                mapTab.init();
                Log.d("mapTab : ", "created mapTab");
                break;

            case 2:
                groupTab = new GroupTab(this);
                //DBC.onChangeGroupsData(groupTab);
                Log.d("groups : ", "created groupTap");
                break;

            case 3:
                friendsTab = new FriendsTab(this);
                Log.d("friendsTab : ", "created friendsTab");
                break;
        }
    }

    public FriendsTab getFriendsTab() {
        return friendsTab;
    }

    public void setFriendsTab(FriendsTab friendsTab) {
        this.friendsTab = friendsTab;
    }

    public GroupTab getGroupTab() {
        return groupTab;
    }

    public void setGroupTab(GroupTab groupTab) {
        this.groupTab = groupTab;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MapController.mLocationPermission = false;
        switch(requestCode){
            case MapController.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    MapController.mLocationPermission = true;
                }
            }
        }
    }

    public void cancelCreateGroup(){
        MapController.makingGroup = false;
        mapTab.setCreatingGroup(null);
        createTabs(1, mapTab.getFragment());
        Toast.makeText(this, "그룹생성이 취소 되었습니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(MapController.makingGroup){
            cancelCreateGroup();
        }
        else{
            startActivity(new Intent( this, LoginActivity.class));
            finish();

        }
    }
    public void notifyingChangedGroups(){
        if(groupTab != null)
        mapTab.getMapController().markingGroupLocation(groupTab.getGroups());
    }

    public void focusingClickedGroup(GroupInfo group){
        LatLng latLng = new LatLng(group.getLatitude(), group.getLongitude());
        mapTab.getMapController().setCurrentLocation(latLng);
        viewPager.setCurrentItem(0);
    }
}