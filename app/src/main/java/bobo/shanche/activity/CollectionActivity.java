package bobo.shanche.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.adapter.AdapterViewPgerCollection;
import bobo.shanche.fragmengt.CollectionLineFragment;
import bobo.shanche.fragmengt.CollectionSiteFragment;

/**
 * Created by bobo1 on 2017/2/14.
 */

public class CollectionActivity extends AppCompatActivity {


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar_collection);
        toolbar.setTitle(R.string.search_collection);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //初始化viewpager跟tabLayout
        String[] titles = new String[]{"路线", "站点"};
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CollectionLineFragment());
        fragments.add(new CollectionSiteFragment());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout_collection);
        ViewPager viewPager = (ViewPager) findViewById(R.id.ViewPager_collection);
        viewPager.setAdapter(new AdapterViewPgerCollection(getSupportFragmentManager(), titles, fragments));
        tabLayout.setupWithViewPager(viewPager);


    }
}