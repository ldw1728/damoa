package com.project.damoa2020.ui.main.friendsTab;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.User;
import com.project.damoa2020.controller.DBC;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.damoa2020.MainActivity.loginUser;

public class FriendsTab {

    private MainActivity main;

    private RecyclerView rv_friendsList;
    private AutoCompleteTextView et_searchFriend;
    private ImageButton ib_addFriend;


    private ArrayList<String> userEmailList; //전체 유저의 이메일
    private ArrayList<User> allUserList; //전체 유저 정보
    private ArrayList<User> friendsUserList; //친구 유저 정보

    private FriendsListAdapter friendsListAdapter;

    public ArrayList<User> getFriendsUserList() {
        return friendsUserList;
    }

    public void setFriendsUserList(ArrayList<User> friendsUserList) {
        this.friendsUserList = friendsUserList;
    }

    public FriendsTab(){}

    public FriendsTab(MainActivity main){
        this.main = main;

        init();
        setEvent();
    }

    public void init(){

        allUserList = new ArrayList<>();
        userEmailList = new ArrayList<>();
        rv_friendsList =main.findViewById(R.id.rv_friendsList);
        et_searchFriend = main.findViewById(R.id.et_searchFriend);
        ib_addFriend = main.findViewById(R.id.ib_addFriend);

        setFriendsListFromRV();
        getFriendsFromDB(); //친구 정보를 db에서 불러옴
        getUsersList(); //모든 유저들을 불러옴.
    }

    public void setFriendsListFromRV(){

        friendsUserList = new ArrayList<>();
        LinearLayoutManager lm = new LinearLayoutManager(main);
        rv_friendsList.setLayoutManager(lm);

        rv_friendsList.addItemDecoration(
                new DividerItemDecoration(main,lm.getOrientation()));

            friendsListAdapter = new FriendsListAdapter(this, main, friendsUserList); //rv의 어댑터 생성.
            rv_friendsList.setAdapter(friendsListAdapter);

    }

    public void getFriendsFromDB(){
        friendsUserList.clear();

        ArrayList<String> friendsEmail = loginUser.getFriends();

        for(String email : friendsEmail){ //로그인 유저의 친구리스트안의 이메일을 이용하여 db에서 정보를 찾아온다.
            DBC.getUser(email, new Consumer<User>() {
                @Override
                public void accept(User user) {
                    friendsUserList.add(user);
                    friendsListAdapter.notifyDataSetChanged(); //어댑터 생성보다 늦게 받아올 수 있기 때문에 받아올 시 즉시 어댑터에 반영.
                }
            });
        }
    }

    public void getUsersList(){

        DBC.getDataFromDB("users", new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                ArrayList<User> users =  (ArrayList<User>) o;
                allUserList = users; //받아온 유저들의 정보

                for(User user : users){
                    if(!user.getEmail().equals(loginUser.getEmail()))
                    userEmailList.add(user.getEmail()); //이메일만 나타나게 하기위해
                }
                et_searchFriend.setAdapter(new ArrayAdapter<String>(main, android.R.layout.simple_dropdown_item_1line, userEmailList)); //자동완성검색창의 어댑터 설정.
            }
        });
    }

    public void setEvent(){
        ib_addFriend.setOnClickListener(new View.OnClickListener() { //추가 버튼 이벤트 생성.
            @Override
            public void onClick(View v) {
                String userEmail = et_searchFriend.getText().toString();
                if(!userEmail.equals("")){
                    DBC.getUser(userEmail, new Consumer<User>() {
                        @Override
                        public void accept(final User user) {
                            addFriend(user);
                        }
                    });
                }
            }
        });
    }

    public void addFriend(final User user){
        Map<String, Object> data = new HashMap<>();
        ArrayList<String> temp = new ArrayList<>();
        temp = loginUser.getFriends();
        temp.add(user.getEmail());
        data.put("friends", temp);

        DBC.updateUser(loginUser,data , new Consumer<Boolean>() { //로그인유저 객체 db상에 업데이트.
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(main, "추가되었습니다.", Toast.LENGTH_LONG).show();
                    //loginUser.addFriend(user.getEmail()); //로그인유저객체에 추가
                    //friendsUserList.add(user); //친구 rv에 업데이트 시키기위해 추가
                    //friendsListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(main, "추가 실패 !.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteFriend(final User user){

        Map<String, Object> data = new HashMap<>();
        ArrayList<String> temp = new ArrayList<>();
        temp = loginUser.getFriends();
        temp.remove(user.getEmail());
        data.put("friends", temp);

        DBC.updateUser(loginUser,data, new Consumer<Boolean>() { //로그인유저 객체 db상에 업데이트.
            @Override
            public void accept(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(main, "삭제되었습니다.", Toast.LENGTH_LONG).show();
                    //loginUser.removeFriend(user.getEmail());
                    friendsUserList.remove(user);
                    friendsListAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(main, "삭제 실패 !.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
