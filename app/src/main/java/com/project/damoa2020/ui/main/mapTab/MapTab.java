package com.project.damoa2020.ui.main.mapTab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.controller.DBC;
import com.project.damoa2020.ui.main.PlaceholderFragment;
import com.project.damoa2020.ui.main.groupTab.GroupInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapTab implements OnMapReadyCallback{

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private MainActivity main;

    private GoogleMap gMap;

    private Fragment fragment;

    private MapController mapController;

    private ImageButton ib_searchLocation;
    private EditText et_searchLocation;
    private TextView txt_selectLocation;
    private ImageButton ib_selLoCom;
    private ImageButton ib_arrival;

    private GroupInfo creatingGroup = null;

    public MapTab(MainActivity main, Fragment fragment){
        this.main = main;
        this.fragment = fragment;
        mapController = new MapController(main, this);
    }

    public MapController getMapController(){
        return this.mapController;
    }
    public TextView getTxt_selectLocation(){ return this.txt_selectLocation;}
    public Fragment getFragment(){ return this.fragment;}
    public ImageButton getIb_selLoCom(){ return this.ib_selLoCom;}
    public ImageButton getIb_arrival(){ return this.ib_arrival;}

    public void init(){
        ib_searchLocation = main.findViewById(R.id.ib_searchLocation);
        txt_selectLocation = main.findViewById(R.id.txt_selectLocation);
        et_searchLocation = main.findViewById(R.id.et_searchLocation);
        ib_selLoCom = main.findViewById(R.id.ib_selLoCom);
        ib_arrival = main.findViewById(R.id.ib_arrival);
        SupportMapFragment mapFragment = (SupportMapFragment)fragment.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setEvent();
    }

    private void setEvent(){

        ib_searchLocation.setOnClickListener(new View.OnClickListener() { //검색기능 구현
            @Override
            public void onClick(View v) {
                String locationStr = et_searchLocation.getText().toString().trim();
                if(!locationStr.equals("")){
                    LatLng latLng = mapController.addressToLatlng(locationStr);
                    mapController.setCurrentLocation(latLng);
                    et_searchLocation.setText("");
                }
                else{
                    Toast.makeText(main, "주소를 입력하세요..", Toast.LENGTH_LONG).show();
                }
            }
        });

        ib_selLoCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatingGroup != null){
                    main.addGroup(creatingGroup);
                    creatingGroup = null;
                    Toast.makeText(main, "그룹이 생성되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void setIb_arrival(final GroupInfo g){
        if(g != null){
            ib_arrival.setVisibility(View.VISIBLE);
            ib_arrival.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!g.getParticipants().get(MainActivity.loginUser.getEmail())){ //만약 자신이 도착 false상태면
                        g.getParticipants().put(MainActivity.loginUser.getEmail(), true);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("participants", g.getParticipants());
                        DBC.updateData(g, data, new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) {
                                mapController.defaultMapStatus(false);
                                main.getGroupTab().getGroupsFromDB();
                                Toast.makeText(main, "you arrivaled", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(main, "이미 목적지 도착하셨습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {//지도 불러올시 수행될 작업,
        gMap = googleMap;
        mapController.setgMap(gMap);
        gMap.setMyLocationEnabled(true);
        mapController.executeOperation();
        //mapController.getDeviceLocation();
    }

    public void setComponentVisibility(){
        ib_selLoCom.setVisibility(View.INVISIBLE);
        txt_selectLocation.setVisibility(View.INVISIBLE);
        ib_arrival.setVisibility(View.INVISIBLE);
    }

    public GroupInfo getCreatingGroup() {
        return creatingGroup;
    }

    public void setCreatingGroup(GroupInfo creatingGroup) {
        this.creatingGroup = creatingGroup;
    }
}
