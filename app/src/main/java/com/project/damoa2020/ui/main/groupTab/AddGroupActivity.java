package com.project.damoa2020.ui.main.groupTab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.project.damoa2020.MainActivity;
import com.project.damoa2020.R;
import com.project.damoa2020.controller.Common;
import com.project.damoa2020.ui.main.PlaceholderFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddGroupActivity extends AppCompatActivity {

    private Calendar calendar;

    private GroupInfo groupInfo; //이 객체를 하나 생성하여 그룹관련 정보들을 기입.

    private FrameLayout fragmentLayoutHolder;
    private FragmentTransaction ft;

    private Fragment groupInfoFragment;
    private Fragment groupFriendsFragment;

    private ArrayList<String> friendsEmail;

    private ImageView iv_groupIMG;
    private EditText et_groupTitle;
    private EditText et_groupDetail;
    private Spinner sp_groupYear, sp_groupMonth, sp_groupDay, sp_groupNOP, sp_groupHour, sp_groupMinutes;

    private RecyclerView rv_addGroupFriendsList;
    private ImageButton btn_addGroupFriendsComplete;
    private ImageButton ib_addGroupNext;
    private ImageButton ib_gallery;

    private Bitmap groupIMG = null;


    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        friendsEmail = MainActivity.loginUser.getFriends();

        createFragment();

    }

    public void createFragment() {
        groupInfoFragment = PlaceholderFragment.newInstance(4);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_addGroupFragmentHolder, groupInfoFragment);
        ft.commitAllowingStateLoss();
    }

    public void InitGroupInfoFragment() { //각 컴포넌트들을 초기화하는 작업은 각 프래그먼트 onViewCreated 함수에서 호출된다.
        fragmentLayoutHolder = findViewById(R.id.fl_addGroupFragmentHolder);
        ib_addGroupNext = findViewById(R.id.ib_addGroupNext);
        iv_groupIMG = findViewById(R.id.iv_groupImage);
        et_groupTitle = findViewById(R.id.et_groupTitle);
        et_groupDetail = findViewById(R.id.et_groupDetail);
        sp_groupYear = findViewById(R.id.sp_groupYear);
        sp_groupMonth = findViewById(R.id.sp_groupmonth);
        sp_groupDay = findViewById(R.id.sp_groupDay);
        sp_groupNOP = findViewById(R.id.sp_groupNOP);
        sp_groupHour = findViewById(R.id.sp_addgroup_hour);
        sp_groupMinutes = findViewById(R.id.sp_addgroup_minutes);
        iv_groupIMG = findViewById(R.id.iv_groupImage);
        ib_gallery = findViewById(R.id.ib_gallery);

        GroupInfoFragmentSetEvent();
        initSpinners();
    }

    public void initSpinners() { //그룹 추가 액티비티 내부의 스피너들을 초기화 시키기 위한 메소드.
        final ArrayList<Integer> temp = new ArrayList<>();
        calendar = Calendar.getInstance();
        Date ct = calendar.getTime();

        String yearString = new SimpleDateFormat("yyyy", Locale.getDefault()).format(ct);

        sp_groupYear.setAdapter(new ArrayAdapter<Integer>( // yearSpinner setting
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{Integer.valueOf(yearString), Integer.valueOf(yearString) + 1}
        ));

        sp_groupMonth.setAdapter(new ArrayAdapter<Integer>( // monthSpinner setting
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}
        ));

        sp_groupMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // monthSpinner에서 다른 Month를 선택 할 때마다 days의 요소들도 바뀜
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar.set((Integer) sp_groupYear.getSelectedItem(), position, 1);

                Log.d("selectedPosition", position + 1 + "");
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 선택된 년, 월의 일수를 받아옴

                Log.d("max---------", max + "");

                for (int i = 0; i < max; i++) {
                    temp.add(i + 1);
                }
                sp_groupDay.setAdapter(new ArrayAdapter<Integer>(  // daySpinner setting
                        AddGroupActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        temp
                ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //---------날짜입력스피너-----------

        for (int i = 0; i < 50; i++) {
            temp.add(i + 1);
        }
        sp_groupNOP.setAdapter(new ArrayAdapter<Integer>(  // parcitipantSpinner setting
                AddGroupActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                temp
        ));

        sp_groupHour.setAdapter(new ArrayAdapter<Integer>( //그룹의 시간과 분을 설정 할 수있는 스피너를 세팅.
                AddGroupActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}
        ));
        sp_groupHour.setSelection(calendar.get(Calendar.HOUR) - 1);

        sp_groupMinutes.setAdapter(new ArrayAdapter<Integer>(
                AddGroupActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                new Integer[]{00, 10, 20, 30, 40, 50}
        ));

    }


    public void InitGroupFriendsFragment() {

        rv_addGroupFriendsList = findViewById(R.id.rv_addGroupFriendsList);
        btn_addGroupFriendsComplete = findViewById(R.id.btn_addGroupFriends_Complete);

        if (friendsEmail != null) { //친구가 존재한다면 어댑터를 생성하여 친구목록을 나타냄.
            AddGroupFriendsListAdapter adapter = new AddGroupFriendsListAdapter(this, friendsEmail);
            RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
            rv_addGroupFriendsList.setLayoutManager(lm);
            rv_addGroupFriendsList.setAdapter(adapter);
            GroupFriendsFragmentSetEvent();

        } else { // 존재하지 않다면 텍스트뷰로 알림.
            TextView txt_friendsEmailNull = findViewById(R.id.txt_friendsEmailNull);
            txt_friendsEmailNull.setVisibility(View.VISIBLE);
        }
    }

    public void GroupInfoFragmentSetEvent() {
        ib_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        ib_addGroupNext.setOnClickListener(new View.OnClickListener() { //다음 버튼 클릭 시 프래그먼트 전환
            @Override
            public void onClick(View v) {
                if (groupInfoFragmentCheck()) {
                    groupFriendsFragment = PlaceholderFragment.newInstance(5);
                    //프래그먼트 생성 및 전환
                    ft = getSupportFragmentManager().beginTransaction(); //새로운 프래그먼트로 변경하기위해서는 다시 프래그먼트 트랜잭션을 시작해야한다.
                    ft.replace(R.id.fl_addGroupFragmentHolder, groupFriendsFragment);
                    ft.commitAllowingStateLoss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //갤러리에서 가져온 사진을 이미지뷰에 표시
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    groupIMG = img;
                    iv_groupIMG.setImageBitmap(img);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean groupInfoFragmentCheck() {

        String title = et_groupTitle.getText().toString().trim();
        String detail = et_groupDetail.getText().toString().trim();

        if (!title.equals("") && !detail.equals("")) {

            int year = (int) sp_groupYear.getSelectedItem();
            int month = (int) sp_groupMonth.getSelectedItem();
            int day = (int) sp_groupDay.getSelectedItem();
            int hour = (int) sp_groupHour.getSelectedItem();
            //지정 날짜

            calendar = Calendar.getInstance();
            int cmonth = calendar.get(Calendar.MONTH);
            int cday = calendar.get(Calendar.DATE);
            //현재 날짜

            if (calendar.get(Calendar.YEAR) == year) {
                if (month >= cmonth + 1) {
                    if (month == cmonth + 1 && day == cday) { //월, 날이 현재와 같다면 시간을 비교하여 지금보다 큰 시간인지 아닌지 체크.
                        Log.d("hour---", calendar.get(Calendar.HOUR_OF_DAY) + "");
                        if (calendar.get(Calendar.HOUR_OF_DAY) >= hour) {

                            Common.showMessage(this, "시간을 다시 설정 하세요.");
                            return false;
                        }
                    } else if (month == cmonth + 1 && day < cday) {
                        Common.showMessage(this, "날짜를 다시 설정 하세요.");
                        return false;
                    }

                    String date = year + "0" + month + "0" + day;
                    String time = (hour < 10) ? "0" + hour : "" + hour;
                    time += ((int) sp_groupMinutes.getSelectedItem() > 0) ? "" + sp_groupMinutes.getSelectedItem() : "0" + sp_groupMinutes.getSelectedItem();
                    groupInfo = new GroupInfo(title, detail, date, time, (Integer) sp_groupNOP.getSelectedItem(), null, null);
                    if(groupIMG != null)
                    groupInfo.setImageString(Common.bitmapToString(groupIMG));
                    // groupInfo 객체를 생성 하고 정보들을 기입.
                    return true;

                } else {
                    Common.showMessage(this, "날짜를 다시 설정 하세요.");
                    return false;
                }
            } else {
                String date = year + "0" + month + "0" + day;
                String time = (hour < 10) ? "0" + hour : "" + hour;
                time += ((int) sp_groupMinutes.getSelectedItem() > 0) ? "" + sp_groupMinutes.getSelectedItem() : "0" + sp_groupMinutes.getSelectedItem();
                groupInfo = new GroupInfo(title, detail, date, time, (Integer) sp_groupNOP.getSelectedItem(), null, null);
                if(groupIMG != null)
                    groupInfo.setImageString(Common.bitmapToString(groupIMG));
                // groupInfo 객체를 생성 하고 정보들을 기입.
                return true;
            }
        } else {
            Common.showMessage(this, "빈칸을 채워주세요..");
            return false;
        }
    }

    public void GroupFriendsFragmentSetEvent() {
        btn_addGroupFriendsComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //완료 버튼 클릭 시 GroupInfo 객체 리턴.
                if (groupInfo != null) {
                    groupInfo.getParticipants().put(MainActivity.loginUser.getEmail(), false);
                    Intent intent = new Intent();
                    intent.putExtra("group", groupInfo);
                    setResult(3, intent);
                    finish();

                } else {
                    Common.showMessage(AddGroupActivity.this, "error..");
                    finish();
                }
            }
        });
    }
}


