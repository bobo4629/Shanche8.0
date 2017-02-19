package bobo.shanche.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.adapter.AdapterDetailLine;
import bobo.shanche.bean.BeanDetailLine;
import bobo.shanche.bean.BeanDetailLineSiteInfo;
import bobo.shanche.bean.BeanDetailSite;
import bobo.shanche.db.DbHelper;
import bobo.shanche.utils.CommonUtil;
import bobo.shanche.utils.NetUtils;

/**
 * Created by bobo1 on 2017/2/9.
 */

public class DetailLineActivity extends AppCompatActivity {

    private int UP = 1;
    private int DOWN = 2;

    private String mId;
    private int mUpDown;
    private String mLineName;

    private Handler mHandler = new Handler();
    private Context mContext;
    private Runnable mTask ;
    private boolean mIsRefresh = false;
    private static ArrayList<Boolean> mListNotice = new ArrayList<>();


    private boolean mIsVibrate;
    private boolean mIsRingtone;
    private int mRefreshTime;

    private boolean mIsCollected = false;
    private FloatingActionMenu fab;
    private FloatingActionButton fab_fav;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_line);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        mContext = this;

        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        mLineName = intent.getStringExtra("lineName");
        mUpDown = intent.getIntExtra("upDown",UP);

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar_detail_line);
        toolbar.setTitle(mLineName);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        fab = (FloatingActionMenu)findViewById(R.id.fab_detail_line);
        initCardView();
        initFAB();
        checkCollected();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mIsVibrate = sharedPreferences.getBoolean(getString(R.string.pre_key_way_vibrate),true);
        mIsRingtone = sharedPreferences.getBoolean(getString(R.string.pre_key_way_ringtone),false);
        mRefreshTime = Integer.valueOf(sharedPreferences.getString(getString(R.string.pre_key_refresh_time),"15000"));

        mTask = new task();
        mHandler.post(mTask);

    }
    private void checkCollected(){
        DbHelper db = new DbHelper(this);

        if(db.isCollected(getString(R.string.db_table_line),mId,mUpDown)){
            fab_fav.setLabelText(getString(R.string.fab_fav));
            fab_fav.setImageResource(R.drawable.ic_line_yes_fav);
            mIsCollected = true;
        }else {
            fab_fav.setLabelText(getString(R.string.fab_no_fav));
            fab_fav.setImageResource(R.drawable.ic_line_no_fav);
            mIsCollected = false;
        }
        db.close();
    }
    private void changeCollected(boolean aimCollected){
        DbHelper db = new DbHelper(this);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_detail_line);
        if(aimCollected){
            db.deleteCollection(getString(R.string.db_table_line),mId,mUpDown);
            db.addCollection(getString(R.string.db_table_line),mId,mLineName,mUpDown);
            fab_fav.setLabelText(getString(R.string.fab_fav));
            fab_fav.setImageResource(R.drawable.ic_line_yes_fav);
            mIsCollected = true;
            Snackbar.make(coordinatorLayout,getString(R.string.fav_done),Snackbar.LENGTH_SHORT).show();
        }else {
            db.deleteCollection(getString(R.string.db_table_line),mId,mUpDown);
            fab_fav.setLabelText(getString(R.string.fab_no_fav));
            fab_fav.setImageResource(R.drawable.ic_line_no_fav);
            mIsCollected = false;
            Snackbar.make(coordinatorLayout,getString(R.string.fav_remove),Snackbar.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void initFAB(){


        fab_fav = new FloatingActionButton(this);
        fab_fav.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab_fav.setImageResource(R.drawable.ic_line_no_fav);
        fab_fav.setLabelText(getString(R.string.fab_no_fav));
        fab_fav.setColorNormalResId(android.R.color.white);
        fab_fav.setColorPressedResId(android.R.color.white);
        fab_fav.setColorRippleResId(R.color.fab_press_ripple);
        fab.addMenuButton(fab_fav);
        fab_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsCollected){
                    changeCollected(false);
                }else {
                    changeCollected(true);
                }
                fab.close(false);
            }
        });

        FloatingActionButton fab_swap = new FloatingActionButton(this);
        fab_swap.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab_swap.setImageResource(R.drawable.ic_line_swap);
        fab_swap.setLabelText(getString(R.string.fab_updown_change));
        fab_swap.setColorNormalResId(android.R.color.white);
        fab_swap.setColorPressedResId(android.R.color.white);
        fab_swap.setColorRippleResId(R.color.fab_press_ripple);
        fab.addMenuButton(fab_swap);
        fab_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mTask);
                mUpDown = mUpDown == UP ? DOWN : UP;
                initCardView();
                mIsRefresh = false;
                mHandler.post(mTask);
                checkCollected();
                fab.close(false);
            }
        });

        FloatingActionButton fab_refresh = new FloatingActionButton(this);
        fab_refresh.setButtonSize(FloatingActionButton.SIZE_MINI);
        fab_refresh.setImageResource(R.drawable.ic_line_refresh);
        fab_refresh.setLabelText(getString(R.string.fab_refresh));
        fab_refresh.setColorNormalResId(android.R.color.white);
        fab_refresh.setColorPressedResId(android.R.color.white);
        fab_refresh.setColorRippleResId(R.color.fab_press_ripple);
        fab.addMenuButton(fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mTask);
                mHandler.post(mTask);
                fab.close(false);
            }
        });


    }



    private void initCardView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String,String> postContent = new HashMap<>();
                postContent.put("lineId",mId);
                final String postBack = NetUtils.post(getApplicationContext(),getApplicationContext().getString(R.string.url_getLineById),postContent);
                final BeanDetailSite beanDetailSite = new Gson().fromJson(postBack,BeanDetailSite.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView_lineName = (TextView)findViewById(R.id.textView_detail_site_lineName);
                        TextView textView_startTime = (TextView)findViewById(R.id.textView_detail_site_start_time);
                        TextView textView_endTime = (TextView)findViewById(R.id.textView_detail_site_end_time);
                        TextView textView_fare = (TextView)findViewById(R.id.textView_detail_site_fare);
                        TextView textView_startSite = (TextView)findViewById(R.id.textView_detail_site_start_site);
                        TextView textView_endSite = (TextView)findViewById(R.id.textView_detail_site_end_site);
                        TextView textView_upDown = (TextView)findViewById(R.id.textView_detail_site_upDown);
                        textView_lineName.setText(beanDetailSite.getLineName());
                        textView_startTime.setText(beanDetailSite.getDownStartTime());
                        textView_endTime.setText(beanDetailSite.getDownEndTime());
                        textView_fare.setText(String.valueOf(beanDetailSite.getFare()));
                        textView_startSite.setText(CommonUtil.getStartSiteName(mUpDown,beanDetailSite.getStartEndSites()));
                        textView_endSite.setText(CommonUtil.getEndSiteName(mUpDown,beanDetailSite.getStartEndSites()));
                        String upDown ;
                        if(mUpDown == UP){
                            upDown="上行";
                        }else {
                            upDown="下行";
                        }
                        textView_upDown.setText(upDown);

                    }
                });
            }
        }).start();
    }

    class task implements Runnable{

        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap<String,String> postContent = new HashMap<>();
                    postContent.put("lineId",mId);
                    postContent.put("upDown",String.valueOf(mUpDown));
                    postContent.put("siteId","");
                    String postBack = NetUtils.post(mContext,mContext.getString(R.string.url_getLineDetail),postContent);
                    BeanDetailLine beanDetailLine = new Gson().fromJson(postBack, new TypeToken<BeanDetailLine>(){}.getType());
                    final List<BeanDetailLineSiteInfo> list = beanDetailLine.getList();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.RecyclerView_detail_line);

                            if(!mIsRefresh){
                                recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
                                mListNotice.clear();
                                for(int i = 0 ; i<list.size();i++){
                                    mListNotice.add(false);
                                }
                                AdapterDetailLine adapterDetailLine = new AdapterDetailLine(mContext,R.layout.item_detail_line,list,mListNotice);
                                adapterDetailLine.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                                        mListNotice.set(position,!mListNotice.get(position));
                                        ImageView imageView = (ImageView)view.findViewById(R.id.ImageView_detail_line);
                                        if(mListNotice.get(position)){
                                            imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_line_notice));
                                        }else {
                                            imageView.setBackground(ContextCompat.getDrawable(mContext,android.R.color.transparent));
                                        }

                                    }


                                    @Override
                                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                                        return false;
                                    }
                                });
                                recyclerView.setAdapter(adapterDetailLine);
                                mIsRefresh= true;
                            }else {
                                AdapterDetailLine adapterDetailLine = (AdapterDetailLine)recyclerView.getAdapter();
                                adapterDetailLine.setmDatas(list);

                                adapterDetailLine.notifyDataSetChanged();
                            }

                            //提醒部分
                            boolean noticed = false;
                            for (int i = 0 ;i < list.size() ; i++){
                                if(!list.get(i).getBusList().isEmpty() && mListNotice.get(i)){
                                    //提醒方式
                                    if(mIsVibrate){
                                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                        vibrator.vibrate(500);
                                    }
                                    if(mIsRingtone){
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                        r.play();
                                    }
                                    noticed = true;
                                }
                                if(noticed){
                                    break;
                                }
                            }
                        }
                    });

                    mHandler.removeCallbacks(mTask);
                    //520321314是永不的值
                    if(mRefreshTime != 520321314){
                        mHandler.postDelayed(mTask,mRefreshTime);
                    }
                }
            }).start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTask);
    }
}
