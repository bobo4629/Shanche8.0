package bobo.shanche.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.adapter.AdapterDetailSite;
import bobo.shanche.bean.BeanDetailSite;
import bobo.shanche.db.DbHelper;
import bobo.shanche.utils.NetUtils;


/**
 * Created by bobo1 on 2017/2/8.
 */

public class DetailSiteActivity extends AppCompatActivity {


    private int UP = 1;
    private int DOWN = 2;
    private static Handler mHandler = new Handler();
    private String mId;
    private int mUpDown;

    private String mSiteName;
    private AdapterDetailSite mAdapterDetailSite;
    private boolean mIsRefresh = false;
    private Runnable mTask;
    private String postBack_Up;
    private String postBack_Down;

    private boolean mIsCollected = false;
    private FloatingActionMenu fab;
    private FloatingActionButton fab_fav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_site);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        Intent intent = getIntent();
        mSiteName = intent.getStringExtra("siteName");
        mId = intent.getStringExtra("id");
        mUpDown = intent.getIntExtra("upDown",UP);

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar_detail_site);
        toolbar.setTitle(mSiteName);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab = (FloatingActionMenu)findViewById(R.id.fab_detail_site);
        initFAB();
        checkCollected();

        mTask = new task();
        mHandler.post(mTask);
    }

    private void checkCollected(){
        DbHelper db = new DbHelper(this);

        if(db.isCollected(getString(R.string.db_table_site),mId,mUpDown)){
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
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_detail_site);

        if(aimCollected){
            db.deleteCollection(getString(R.string.db_table_site),mId,mUpDown);
            db.addCollection(getString(R.string.db_table_site),mId,mSiteName,mUpDown);
            fab_fav.setLabelText(getString(R.string.fab_fav));
            fab_fav.setImageResource(R.drawable.ic_line_yes_fav);
            mIsCollected = true;
            Snackbar.make(coordinatorLayout,getString(R.string.fav_done),Snackbar.LENGTH_SHORT).show();
        }else {
            db.deleteCollection(getString(R.string.db_table_site),mId,mUpDown);
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
                int tmp ;
                tmp = UP;
                UP = DOWN;
                DOWN = tmp;
                mIsRefresh = false;
                mHandler.post(mTask);
                mUpDown = UP;
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

    class task implements Runnable{
        private String post(String upDown){
            HashMap<String,String> postContent = new HashMap<>();
            postContent.put("siteId",mId);
            postContent.put("upDown",upDown);
            return NetUtils.post(DetailSiteActivity.this,getString(R.string.url_getSiteDetail),postContent);
        }
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    postBack_Up = post(Integer.toString(mUpDown));
                    postBack_Down = post(Integer.toString(mUpDown == UP ? DOWN : UP));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<BeanDetailSite> listUp = new Gson().fromJson(postBack_Up, new TypeToken<List<BeanDetailSite>>(){}.getType());
                            List<BeanDetailSite> listDown = new Gson().fromJson(postBack_Down, new TypeToken<List<BeanDetailSite>>(){}.getType());
                            List<List<BeanDetailSite>> lists = new ArrayList<>();
                            if (listUp!=null && listDown!=null) {
                                List<BeanDetailSite> list_more = listUp.size() >= listDown.size() ? listUp : listDown;
                                List<BeanDetailSite> list_less = list_more == listUp ? listDown : listUp;
                                for(int i=0;i<list_more.size();i++){
                                    List<BeanDetailSite> listAddTemp = new ArrayList<>();
                                    //逐一对比 不同的话 肯定会有多一个或者少一个 移动至最末
                                    if(list_more.get(i).getId().equals(list_less.get(i).getId())){
                                        listAddTemp.add(listUp.get(i));
                                        listAddTemp.add(listDown.get(i));
                                        lists.add(listAddTemp);
                                    }else {
                                        for(int n=i;n<list_more.size();n++){
                                            list_more.add(list_more.get(n));
                                            list_less.add(list_more.get(n));
                                            list_more.remove(n);
                                            if(listUp.get(n).getId().equals(listDown.get(n).getId())){
                                                i--;
                                                break;
                                            }
                                        }
                                    }
                                }

                                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.RecyclerView_detail_site);
                                mAdapterDetailSite = new AdapterDetailSite(DetailSiteActivity.this,R.layout.item_detail_site,lists);
                                if(!mIsRefresh){
                                    recyclerView.setAdapter(mAdapterDetailSite);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailSiteActivity.this,LinearLayoutManager.VERTICAL,false));
                                    mIsRefresh=true;
                                }else {
                                    ((AdapterDetailSite)recyclerView.getAdapter()).setmIsRefresh(true);
                                    ((AdapterDetailSite)recyclerView.getAdapter()).setmDatas(lists);
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }

                                mHandler.removeCallbacks(mTask);
                                //差个get时间
                                mHandler.postDelayed(mTask,30000);

                            }else {
                                CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_detail_site);
                                Snackbar.make(coordinatorLayout,getString(R.string.mistake),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).start();


        }
    }
}
