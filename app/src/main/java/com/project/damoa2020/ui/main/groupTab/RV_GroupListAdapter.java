package com.project.damoa2020.ui.main.groupTab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.controller.Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class RV_GroupListAdapter extends RecyclerView.Adapter<RV_GroupListAdapter.ViewHolder> {

    private ArrayList<GroupInfo> groups;

    private Activity activity = null;


    public RV_GroupListAdapter(Activity ac, ArrayList<GroupInfo> groups){
        this.groups = groups;
        activity = ac;
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
            Bitmap bitMap = Common.StringToBitmap(group.getImageString());
            holder.iv_groupImage.setImageBitmap(bitMap);
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
                if(activity instanceof MainActivity){
                    ((MainActivity)activity).focusingClickedGroup(group);
                }
            }
        });
    }

    public void showInfo(final GroupInfo group, String date){
        String infoStr;
        AlertDialog.Builder builder = null;
        if(activity instanceof MainActivity){
            builder = new AlertDialog.Builder((MainActivity)activity);
        }
        else if(activity instanceof SearchGroupActivity){
            builder = new AlertDialog.Builder((SearchGroupActivity)activity);
        }

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
        if(activity instanceof MainActivity){
            builder.setPositiveButton("그룹나가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity)activity).getGroupTab().deleteGroup(group);
                }
            });
        }
        else{
            builder.setPositiveButton("그룹 추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(group.getParticipants().containsKey(MainActivity.loginUser.getEmail())){
                        Toast.makeText(activity, "이미 소속된 그룹입니다.", Toast.LENGTH_LONG).show();
                    }else{
                        group.getParticipants().put(MainActivity.loginUser.getEmail(), false);
                        ((SearchGroupActivity)activity).setResult(group);
                    }

                }
            });
        }

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
