package bobo.shanche.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;

import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.HashMap;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.adapter.AdapterSearchLine;
import bobo.shanche.adapter.AdapterSearchSite;
import bobo.shanche.bean.BeanSearchLine;
import bobo.shanche.bean.BeanSearchSite;
import bobo.shanche.utils.CommonUtil;
import bobo.shanche.utils.NetUtils;

/**
 * Created by bobo1 on 2017/2/5.
 */

public class SearchActivity extends AppCompatActivity
        implements EditText.OnEditorActionListener
        {

    private final int SEARCH_LINE = 0;
    private final int SEARCH_SITE = 1;
    private int searchType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText editText = (EditText)findViewById(R.id.EditText_search);
        editText.setOnEditorActionListener(this);
        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        Button buttonCollection = (Button) findViewById(R.id.Button_Collection);
        buttonCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开收藏activity
                startActivity(new Intent(SearchActivity.this,CollectionActivity.class));
            }
        });
        Button buttonSelFromMap = (Button)findViewById(R.id.Button_SelFromMap);
        buttonSelFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开地图activity
            }
        });
    }

    @Override
    public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {

        if(!v.getText().toString().isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url;
                    HashMap<String, String> postContent = new HashMap<>();

                    if (Character.isDigit(v.getText().charAt(0))) {
                        url = getString(R.string.url_getLines);
                        searchType = SEARCH_LINE;
                        postContent.put("lineName",v.getText().toString());
                    } else {
                        url = getString(R.string.url_getSites);
                        searchType = SEARCH_SITE;
                        postContent.put("siteName",v.getText().toString());
                    }
                    final String postBack = NetUtils.post(SearchActivity.this,url, postContent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showThing(postBack, searchType,true);//临时显示所有
                        }
                    });
                }
            }).start();
        }else {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout_search);
            Snackbar.make(coordinatorLayout,getString(R.string.search_whenNoinput),Snackbar.LENGTH_SHORT).show();
        }

        CommonUtil.closeKeyboard(v);

        return false;//返回false 执行一次  true两次
    }

    private void showThing(final String postBack, final int searchType, boolean isRefresh){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.RecyclerView_search_top);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EmptyWrapper emptyWrapper;
        if(searchType == SEARCH_LINE){
            final List<BeanSearchLine> listLine = new Gson().fromJson(postBack,new TypeToken<List<BeanSearchLine>>(){}.getType());
            AdapterSearchLine adapterSearchLine;
            if(isRefresh){
                adapterSearchLine = new AdapterSearchLine(this,R.layout.item_search,listLine);
            }else {
                adapterSearchLine = new AdapterSearchLine(this,R.layout.item_search,listLine.size()>=5 ? listLine.subList(0,5) : listLine);
            }
            adapterSearchLine.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(SearchActivity.this,DetailLineActivity.class);
                    intent.putExtra("id",listLine.get(position-1).getId());
                    intent.putExtra("lineName",listLine.get(position-1).getLineName());
                    startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });

            emptyWrapper = new EmptyWrapper(adapterSearchLine);
        }else {
            //SEARCH_SITE
            final List<BeanSearchSite>listSite = new Gson().fromJson(postBack,new TypeToken<List<BeanSearchSite>>(){}.getType());

            final AdapterSearchSite adapterSearchSite ;
            if(isRefresh){
                adapterSearchSite = new AdapterSearchSite(this,R.layout.item_search,listSite);
            }else {
                adapterSearchSite = new AdapterSearchSite(this,R.layout.item_search,listSite.size()>=5 ? listSite.subList(0,5) : listSite);
            }
            adapterSearchSite.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(SearchActivity.this,DetailSiteActivity.class);
                    intent.putExtra("siteName",listSite.get(position-1).getSiteName());
                    intent.putExtra("id",listSite.get(position-1).getId());
                    startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });


            emptyWrapper = new EmptyWrapper(adapterSearchSite);

        }

        //增加emptyView 表头 以及 查看更多
        emptyWrapper.setEmptyView(R.layout.empty_search);


        TextView header = new TextView(this);


        header.setText(getString(R.string.search_header));
        HeaderAndFooterWrapper headerAndFooterWrapper = new HeaderAndFooterWrapper(emptyWrapper);
        headerAndFooterWrapper.addHeaderView(header);
        recyclerView.setAdapter(headerAndFooterWrapper);


    }


}
