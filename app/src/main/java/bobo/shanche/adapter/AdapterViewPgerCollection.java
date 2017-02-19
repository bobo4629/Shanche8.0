package bobo.shanche.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by bobo1 on 2017/2/14.
 */

public class AdapterViewPgerCollection extends FragmentPagerAdapter {
    private String[] mTitles;
    private List<Fragment> mFragments;
    public AdapterViewPgerCollection(FragmentManager fm,String[] titles , List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
