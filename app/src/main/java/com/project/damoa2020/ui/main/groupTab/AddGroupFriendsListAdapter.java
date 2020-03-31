package com.project.damoa2020.ui.main.groupTab;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.damoa2020.R;

import java.util.ArrayList;

public class AddGroupFriendsListAdapter extends RecyclerView.Adapter<AddGroupFriendsListAdapter.ViewHolder> {

    private AddGroupActivity activity;
    private ArrayList<String> friends;

    public AddGroupFriendsListAdapter(AddGroupActivity activity, ArrayList<String> friends){
        this.friends = friends;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_addgroupfriends_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String email = friends.get(position);
        holder.cb_friend.setText(email);

        holder.cb_friend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(activity.getGroupInfo().getPON() <= activity.getGroupInfo().getParticipants().size()+1)
                        buttonView.setChecked(false);
                    else
                        activity.getGroupInfo().addParticipant(email);
                }
                else{
                    activity.getGroupInfo().deleteParticipant(email);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb_friend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_friend = itemView.findViewById(R.id.cb_friend);
        }
    }
}
