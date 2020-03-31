package com.project.damoa2020.ui.main.friendsTab;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.damoa2020.R;
import com.project.damoa2020.User;

import java.util.ArrayList;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private Activity activity;
    private FriendsTab friendsTab;
    private ArrayList<User> friends;

    public FriendsListAdapter(FriendsTab friendsTab, Activity activity, ArrayList<User> friends){ //생성자
        this.activity = activity;
        this.friends = friends;
        this.friendsTab = friendsTab;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friendslist_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User user = friends.get(position);

        holder.rv_item_name.setText(user.getName());
        holder.rv_item_email.setText(user.getEmail());

        holder.ib_deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsTab.deleteFriend(friends.get(position));
            }
        });

    }



    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{ //뷰홀더를 생성. 뷰홀더안의 컨텐츠들을 구성.

        TextView rv_item_name;
        TextView rv_item_email;
        ImageButton ib_deleteFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_item_email = itemView.findViewById(R.id.rv_item_email);
            rv_item_name = itemView.findViewById(R.id.rv_item_name);
            ib_deleteFriend = itemView.findViewById(R.id.ib_deleteFriend);
        }
    }



}

