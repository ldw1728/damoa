package com.project.damoa2020.ui.main.mapTab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.ui.main.groupTab.GroupInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapController {

    private MainActivity main;
    private GoogleMap gMap;
    private MapTab mapTab;
    private Geocoder geocoder;

    public static boolean makingGroup = false;

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;

    public static boolean mLocationPermission = false;

    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);

    public MapController(MainActivity main, MapTab mapTab){
        this.main = main;
        this.mapTab = mapTab;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(main);
        getLocationPermission();
        geocoder = new Geocoder(main);
    }

    public void setgMap(GoogleMap gMap){
        this.gMap = gMap;
    }

    public void getLocationPermission(){ //위치권한요청
        if(ContextCompat.checkSelfPermission(main.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLocationPermission = true;
        } else {
            ActivityCompat.requestPermissions(main,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getDeviceLocation(final Boolean moveCamera, final Consumer<LatLng> consumer){ //현재 기기의 위치를 불러옴
        try{

            if(mLocationPermission){
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(main, new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful() && (mLastKnownLocation = task.getResult()) != null){
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude()
                                    ,mLastKnownLocation.getLongitude());
                            if(moveCamera)
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(  //현재위치로 카메라 이동
                                    latLng,
                                    15));
                            MainActivity.loginUser.setLatLng(latLng);
                            if(consumer != null)
                                consumer.accept(latLng);
                        } else {
                            Log.d("currentLo : ", "Current location is null");
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,
                                    15));
                            gMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch(SecurityException e) {

        }
    }

    public void executeOperation(){

        if(makingGroup){ //그룹생성중일때의 맵 작업라인
            gMap.clear();
            Log.d("executeOperation : ", "entered executeOperation");
            final GroupInfo g = mapTab.getCreatingGroup();
            if(g != null){
                mapTab.getTxt_selectLocation().setVisibility(View.VISIBLE);
                gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        gMap.clear();
                        Log.d("executeOperation : ", "entered executeOperation2");
                        gMap.addMarker(new MarkerOptions().position(latLng).title("확인"));
                        String selectedLocation = LatlngToAddress(latLng);
                        g.setLatitude(latLng.latitude);
                        g.setLongitude(latLng.longitude);
                        g.setAddress(selectedLocation);
                        mapTab.setCreatingGroup(g);
                        Toast.makeText(main, selectedLocation, Toast.LENGTH_LONG ).show();
                        mapTab.getTxt_selectLocation().setVisibility(View.INVISIBLE);
                        mapTab.getIb_selLoCom().setVisibility(View.VISIBLE);
                    }
                });
            }
            else{
                Toast.makeText(main, "Error! group == null", Toast.LENGTH_LONG ).show();
                main.createTabs(1, mapTab.getFragment());
            }
        }
        else {
           defaultMapStatus(true);
        }
    }

    public void defaultMapStatus(Boolean currentLocation){
        gMap.clear();
        gMap.setOnMapClickListener(null);
        mapTab.setComponentVisibility();

        if(currentLocation)
            getDeviceLocation(true, null); //현재위치를 표시
        main.notifyingChangedGroups();

    }

    public void markingGroupLocation(ArrayList<GroupInfo> groups){
        gMap.clear();

        for(final GroupInfo g : groups){
            gMap.addMarker(new MarkerOptions().position(new LatLng(g.getLatitude(), g.getLongitude())).title(g.getTitle()));
            setMarkingEvent();
            Log.d("defaultMapStatus : ", g.getTitle());
        }
    }

    public void setMarkingEvent(){
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                gMap.clear();
                mapTab.getIb_arrival().setVisibility(View.INVISIBLE);

                main.notifyingChangedGroups();
                gMap.addCircle(new CircleOptions().center(marker.getPosition()).radius(80).strokeColor(Color.RED));

                getDeviceLocation(false, new Consumer<LatLng>() {
                    @Override
                    public void accept(LatLng latLng) {

                        if(measurementDistance(marker.getPosition())){
                            Toast.makeText(main, "도착을 알리려면 좌측하단 아이콘을 클릭", Toast.LENGTH_LONG).show();
                            mapTab.setIb_arrival(findingMarkedGroup(marker.getPosition()));
                        }
                    }
                });

                gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        defaultMapStatus(false);
                    }
                });
                return false;
            }
        });
    }

    public GroupInfo findingMarkedGroup(LatLng latLng){
        for(GroupInfo g: main.getGroupTab().getGroups()){
            if(g.getLatitude() == latLng.latitude && g.getLongitude() == latLng.longitude){
                return g;
            }
        }
        return null;
    }

    public Boolean measurementDistance(LatLng latLng){
         Location locationA = new Location("a");
         Location locationB = new Location("b");

        locationA.setLatitude(MainActivity.loginUser.getLatLng().latitude);
        locationA.setLongitude(MainActivity.loginUser.getLatLng().longitude); //자신의 위치

        locationB.setLatitude(latLng.latitude);
        locationB.setLongitude(latLng.longitude);

            float distance = locationB.distanceTo(locationA);
                if(distance <= 80.0){
                return true;
            }
                return false;
    }


    public void setCurrentLocation(LatLng latLng){
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public LatLng addressToLatlng(String locationStr){
         LatLng selectedLocation = null;
        List<Address> addressList = null;
        try{
            addressList = geocoder.getFromLocationName(locationStr, 10);

            Double lat = addressList.get(0).getLatitude();
            Double lng = addressList.get(0).getLongitude();

            selectedLocation = new LatLng(lat, lng);

        }catch (IOException e){
            e.printStackTrace();
        }catch (NullPointerException n){
            Toast.makeText(main, "찾을 수 없습니다.", Toast.LENGTH_LONG ).show();
        }
        return selectedLocation;
    }

    public String LatlngToAddress(LatLng latLng){
        List<Address> addressList = null;
        try{
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);

        }catch(IOException e){
            e.printStackTrace();
        }
        String addressStr = addressList.get(0).getAddressLine(0);

        return addressStr;
    }
}
