package com.project.damoa2020.ui.main.groupTab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class RV_GroupListAdapter extends RecyclerView.Adapter<RV_GroupListAdapter.ViewHolder> {

    private ArrayList<GroupInfo> groups;
    private MainActivity main;

    public RV_GroupListAdapter(MainActivity main, ArrayList<GroupInfo> groups){
        this.groups = groups;
        this.main = main;
    }

    public void setGroups(ArrayList<GroupInfo> groups){
        this.groups = groups;
    }

    @NonNull
    @Override
    public RV_GroupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_grouplist_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final GroupInfo group = groups.get(position);

        if(group.getImageString() != null){
            holder.iv_groupImage.setImageBitmap(null);
        }

        holder.tv_title.setText(group.getTitle());

        final String dateStr = group.getDate().substring(0,4) + " _ " +  group.getDate().substring(4,6) + " _ " +  group.getDate().substring(6,8)+" _ "+group.getTime().substring(0,2)+" : "+group.getTime().substring(2,4);
        holder.tv_date.setText(dateStr);

        String nopStr = group.getParticipants().size() + " / " + group.getPON();
        holder.tv_nop.setText(nopStr);



        holder.ib_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo(group, dateStr);
            }
        });

        holder.layout_groupListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.focusingClickedGroup(group);
            }
        });





    }

    public void showInfo(final GroupInfo group, String date){
        String infoStr;
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle(group.getTitle());

        infoStr = group.getDetail_info()+"\n\n" +"시간 : " +date + "\n";

        infoStr += "장소 : "+group.getAddress()+"\n\n";

        infoStr += "참여인원 : \n";

        Iterator<String> iter = group.getParticipants().keySet().iterator();

        while(iter.hasNext()){
           String email = iter.next();
           if(group.getParticipants().get(email)){
               email += "   도착";
           }
            infoStr += email +"\n";
        }

        builder.setMessage(infoStr);

        builder.setPositiveButton("그룹나가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.getGroupTab().deleteGroup(group);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_groupImage;
        TextView tv_title;
        TextView tv_date;
        TextView tv_nop;
        ImageButton ib_info;
        LinearLayout layout_groupListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_groupImage = itemView.findViewById(R.id.iv_groupImage);
            tv_title = itemView.findViewById(R.id.tv_groupTitle);
            tv_date = itemView.findViewById(R.id.tv_groupDate);
            tv_nop = itemView.findViewById(R.id.tv_groupNOP);
            ib_info = itemView.findViewById(R.id.ib_info);
            layout_groupListItem = itemView.findViewById(R.id.layout_groupListItem);
        }
    }
}
