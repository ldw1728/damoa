package com.project.damoa2020.ui.main.groupTab;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.VH> {

    private ArrayList<GroupInfo> groups;

    public SearchGroupAdapter(ArrayList<GroupInfo> g){
        groups = g;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
