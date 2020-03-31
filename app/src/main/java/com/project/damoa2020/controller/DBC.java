package com.project.damoa2020.controller;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.damoa2020.MainActivity;
import com.project.damoa2020.User;
import com.project.damoa2020.ui.main.groupTab.GroupInfo;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import com.google.android.gms.maps.model.LatLng;
import com.project.damoa2020.ui.main.groupTab.GroupTab;

import static com.project.damoa2020.MainActivity.loginUser;

public class DBC {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static boolean callOnChangeData = false;

    public static void onChangeData(final MainActivity main) { //유저 정보가 업데이트 되면 자동으로 호출.
        callOnChangeData = true;
        db.collection("users").document(MainActivity.loginUser.getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("onChange()snapshot : ", e);
                }
                User u = documentSnapshot.toObject(User.class);
                loginUser = u;
                main.refresh_LoginUserInfo();

            }
        });
    }
/*
    public static void onChangeGroupsData(final GroupTab groupTab){

        for(GroupInfo g : groupTab.getGroups()){
            db.collection("groups").document(g.getGroupID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("onChange()snapshot : ", e);
                    }
                    Log.d("onChangeGroupsData", "onChangeGroupsData----------");
                   groupTab.getGroupsFromDB();
                }
            });
        }


    }
    */


    public static void addDataToDB(final MainActivity main, String collection, final Object o) {

        if (o instanceof User) {
            db.collection(collection).document(((User) o).getEmail()).set(o);
            Log.d("addDataToDB", ": success");
        } else if (o instanceof GroupInfo) {
            db.collection(collection).add(o).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(final DocumentReference documentReference) {
                    //그룹을 전체db에 저장, 완료되면 유저객체 정보에 그룹을 추가하여 유저정보도 다시 업데이트한다.
                    Map<String, Object> data = new HashMap();
                    data.put("groupID", documentReference.getId());
                    updateData("groups", data, new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            addGroupToParticipants((GroupInfo) o,
                                    main.getFriendsTab().getFriendsUserList(),
                                    documentReference.getId());
                            Log.d("addDataToDB", ": success");
                        }
                    });
                }
            });
        }
    }

    public static void addGroupToParticipants(GroupInfo groupInfo, ArrayList<User> friends, String groupID) {

        Map<String, Object> data = new HashMap<>();
        ArrayList<String> temp;

        temp = loginUser.getGroups();
        temp.add(groupID);
        data.put("groups", temp);
        updateUser(loginUser, data, null);

        Iterator<String> iter = groupInfo.getParticipants().keySet().iterator();

        while(iter.hasNext()){
            String parEmail = iter.next();
            for (int j = 0; j < friends.size(); j++) {
                if (parEmail.equals(friends.get(j).getEmail())) {
                    temp = friends.get(j).getGroups();
                    temp.add(groupID);
                    data.put("groups", temp);
                    updateUser(friends.get(j), data, null);
                }
            }
        }
    }

    public static void getDataFromDB(String collection, final Consumer<Object> consumer) {
        if (collection.equals("users")) {
            final ArrayList<User> users = new ArrayList<>();
            db.collection(collection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    consumer.accept(users);
                }
            });
        } else if (collection.equals("groups")) {
            if (loginUser.getGroups().size() > 0) {
                Log.d("getDataFromDB : ", "groups1");
                final ArrayList<GroupInfo> groups = new ArrayList<>();
                for (int i = 0; i < loginUser.getGroups().size(); i++) {
                    db.collection(collection).document(loginUser.getGroups().get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            GroupInfo temp = task.getResult().toObject(GroupInfo.class);
                            groups.add(temp);
                            Log.d("getDataFromDB : ", temp.getTitle());
                            if (groups.size() == loginUser.getGroups().size()) {
                                Log.d("getDataFromDB : ", "groups2");
                                consumer.accept(groups);
                            }
                        }
                    });
                }
            } else {
                consumer.accept(null);
                Log.d("getDataFromDB : ", "groups3");
            }
        }
    }

    public static void getUser(String email, final Consumer<User> consumer) {
        db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                consumer.accept(user);
            }
        });
    }

    public static void updateUser(User user, Map<String, Object> data, final Consumer<Boolean> consumer) {

        db.collection("users").document(user.getEmail()).update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (consumer != null) {
                            if (task.isSuccessful()) {
                                consumer.accept(true);
                            } else
                                consumer.accept(false);
                        }
                    }
                });
    }

    public static void updateData(Object o, Map<String, Object> data, final Consumer<Boolean> consumer) {
        if (o instanceof GroupInfo) {
            GroupInfo group = (GroupInfo) o;
            db.collection("groups").document(group.getGroupID()).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    consumer.accept(true);
                    Log.d("updateGroupID : ", "Success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    consumer.accept(false);
                    Log.d("updateGroupID : ", "Failed");
                }
            });
        } else if (o instanceof String) {
            String tmp = String.valueOf(o);
            if (tmp.equals("groups")) {
                db.collection(tmp).document(String.valueOf(data.get("groupID"))).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        consumer.accept(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        consumer.accept(false);
                    }
                });
            }
        }
    }
}
