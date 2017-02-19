package bobo.shanche.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;


import bobo.shanche.R;
import bobo.shanche.activity.DetailLineActivity;
import bobo.shanche.bean.BeanDetailSite;


/**
 * Created by bobo1 on 2017/2/8.
 */

public class AdapterDetailSite extends CommonAdapter<List<BeanDetailSite>> {

    private Context mContext;
    private List<List<BeanDetailSite>> mDatas;
    private boolean mIsRefresh = false;

    public void setmIsRefresh(boolean mIsRefresh) {
        this.mIsRefresh = mIsRefresh;
    }

    public void setmDatas(List<List<BeanDetailSite>> mDatas) {
        this.mDatas = mDatas;
    }

    public AdapterDetailSite(Context context, int layoutId, List<List<BeanDetailSite>> datas) {
        super(context, layoutId, datas);
        mContext=context;
        mDatas=datas;

    }

    @Override
    protected void convert(ViewHolder holder, final List<BeanDetailSite> beanDetailSites, int position) {
//        holder.setIsRecyclable(false);
        RecyclerViewPager viewPager = holder.getView(R.id.ViewPager_detail_site);
        //判断是否存在 若被回收的话必须重新设置
        if( viewPager.getAdapter() == null || !mIsRefresh ){
            AdapterViewPagerSIte adapterViewPager = new AdapterViewPagerSIte(mContext,R.layout.item_detail_site_content,mDatas.get(position));
            adapterViewPager.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    Intent intent = new Intent(mContext,DetailLineActivity.class);
                    intent.putExtra("id",beanDetailSites.get(position).getId());
                    intent.putExtra("lineName",beanDetailSites.get(position).getLineName());
                    intent.putExtra("upDown",position+1);
                    mContext.startActivity(intent);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    return false;
                }
            });
            viewPager.setAdapter(adapterViewPager);
            viewPager.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

        }else{
            ((AdapterViewPagerSIte)viewPager.getAdapter()).setmDatas(mDatas.get(position));
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}
