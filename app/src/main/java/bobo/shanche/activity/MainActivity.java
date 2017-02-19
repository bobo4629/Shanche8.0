package bobo.shanche.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IUnreadCountCallback;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;


import java.util.HashMap;
import java.util.List;


import bobo.shanche.R;

import bobo.shanche.adapter.AdapterMain;
import bobo.shanche.base.BaseMainActivity;
import bobo.shanche.bean.BeanNearByStop;
import bobo.shanche.bean.BeanUpdate;
import bobo.shanche.utils.CommonUtil;
import bobo.shanche.utils.NetUtils;

public class MainActivity extends BaseMainActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AMapLocationListener,
        WeatherSearch.OnWeatherSearchListener

{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AMapLocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //侧滑抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        initBarButton();

        //下啦刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.SwipeRefreshLayout_main);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //定位
        initLocation();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(getString(R.string.pre_about_key_update),true)){
            checkUpdate();
        }

        FeedbackAPI.init(getApplication(), "23625065");
        FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
            @Override
            public void onSuccess(int i) {
                if(i>0) {
                    navigationView.getMenu().findItem(R.id.nav_feedback).setTitle("反馈 - 收到回复");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initBarButton(){
        Button button_fav = (Button)findViewById(R.id.Button_main_collection);
        button_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CollectionActivity.class));
            }
        });

        Button button_home = (Button)findViewById(R.id.Button_main_home);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).
                        setMessage("建设中...")
                        .setCancelable(true)
                        .show();
            }
        });

        Button button_work = (Button)findViewById(R.id.Button_main_work);
        button_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).
                        setMessage("建设中...")
                        .setCancelable(true)
                        .show();
            }
        });

    }

    private  void checkUpdate(){
        if(NetUtils.isNetworkConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String postBack = NetUtils.get("http://api.fir.im/apps/latest/58a1b91f959d69286800000e?api_token=4979f858ca445853a487ad97c6af48ad");
                    final BeanUpdate beanUpdate = new Gson().fromJson(postBack, BeanUpdate.class);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (Integer.valueOf(beanUpdate.getVersion()) > getResources().getInteger(R.integer.app_versioncode)) {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("有新版本可更新")
                                        .setMessage("更新说明：\n" + beanUpdate.getChangelog())
                                        .setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Uri uri = Uri.parse(beanUpdate.getInstallUrl());
                                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                            }
                                        })
                                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true).show();
                            }
                        }
                    });


                }
            }).start();
        }
    }

    private void initLocation(){

        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption locationOption  = new AMapLocationClientOption();
        locationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationListener(this);
        locationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(locationOption);
        mLocationClient.startLocation();


        //天气
        WeatherSearchQuery mquery = new WeatherSearchQuery("汕头", WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mweathersearch=new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_collection:
                startActivity(new Intent(this,CollectionActivity.class));
                break;
            case R.id.nav_guide:
                startActivity(new Intent(this,GuideActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this,SettingActivity.class));
                break;
            case R.id.nav_feedback:
                item.setTitle("反馈");
                FeedbackAPI.openFeedbackActivity();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //下啦刷新方法
    @Override
    public void onRefresh() {
        initLocation();
    }


    private void showThing(String postBack){

        final List<BeanNearByStop> listNearByStop = new Gson().fromJson(postBack, new TypeToken<List<BeanNearByStop>>() {
        }.getType());
        //关闭搜索ing的提示 然后显示列表
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.SwipeRefreshLayout_main);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.searchingLayout_main);
        linearLayout.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.RecyclerView_main);
        AdapterMain adapterMain = new AdapterMain(this, R.layout.item_nearby_busstop, listNearByStop);
        recyclerView.setAdapter(adapterMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapterMain.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                //-1是因为多了Header
                Intent intent = new Intent(MainActivity.this,DetailSiteActivity.class);
                intent.putExtra("siteName",listNearByStop.get(position-1).getSiteName());
                intent.putExtra("id",listNearByStop.get(position-1).getId());

                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });



        //增加表头以及EmptyView
        EmptyWrapper<BeanNearByStop> emptyWrapper = new EmptyWrapper<>(adapterMain);
        emptyWrapper.setEmptyView(R.layout.empty_main);
        TextView t1 = new TextView(this);
        HeaderAndFooterWrapper headerAndFooterWrapper = new HeaderAndFooterWrapper(emptyWrapper);
        t1.setText(getString(R.string.main_nearby_stop_header));
        headerAndFooterWrapper.addHeaderView(t1);
        recyclerView.setAdapter(headerAndFooterWrapper);
        headerAndFooterWrapper.notifyDataSetChanged();


    }

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = MainActivity.this.getString(R.string.url_getNearByStop);

                    //MMP这个居然需要地球坐标
                    Double[] d = CommonUtil.GCJ02ToWGS84(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                    HashMap<String, String> postContent = new HashMap<>();
                    postContent.put("lng", Double.toString(d[0]));
                    postContent.put("lat", Double.toString(d[1]));
                    final String postBack = NetUtils.post(MainActivity.this,url, postContent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showThing(postBack);
                        }
                    });
                }
            }).start();
        }else {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_main);
            Snackbar.make(coordinatorLayout,getString(R.string.mistake),Snackbar.LENGTH_SHORT).show();
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        TextView textView_weather = (TextView)findViewById(R.id.nav_tv_weather);
        TextView textView_humidity = (TextView)findViewById(R.id.nav_tv_humidity);
        TextView textView_temperature = (TextView)findViewById(R.id.nav_tv_temperature);
        TextView textView_wind = (TextView)findViewById(R.id.nav_tv_wind);

        textView_humidity.setText(getString(R.string.drawer_header_weather_humidity,localWeatherLiveResult.getLiveResult().getHumidity()));

        textView_weather.setText(localWeatherLiveResult.getLiveResult().getWeather());
        textView_wind.setText(getString(R.string.drawer_header_weather_wind,localWeatherLiveResult.getLiveResult().getWindDirection(),localWeatherLiveResult.getLiveResult().getWindPower()));
        textView_temperature.setText(getString(R.string.drawer_header_weather_temperature,localWeatherLiveResult.getLiveResult().getTemperature()));
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
