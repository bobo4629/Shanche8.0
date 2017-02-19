package bobo.shanche.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanDetailSite;
import bobo.shanche.utils.CommonUtil;

/**
 * Created by bobo1 on 2017/2/8.
 */

 class AdapterViewPagerSIte extends CommonAdapter<BeanDetailSite> {
    private final int UP = 1;
    private final int DOWN = 2;
    private List<BeanDetailSite> mDatas;

    public void setmDatas(List<BeanDetailSite> mDatas) {
        this.mDatas = mDatas;
    }

    AdapterViewPagerSIte(Context context, int layoutId, List<BeanDetailSite> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, BeanDetailSite beanDetailSite, int position) {
        if(mDatas != null)
            beanDetailSite = mDatas.get(position);
        holder.setText(R.id.textView_detail_site_lineName,beanDetailSite.getLineName());
        holder.setText(R.id.textView_detail_site_start_time,beanDetailSite.getDownStartTime());
        holder.setText(R.id.textView_detail_site_end_time,beanDetailSite.getDownEndTime());
        holder.setText(R.id.textView_detail_site_fare,Integer.toString(beanDetailSite.getFare()));
        holder.setText(R.id.textView_detail_site_start_site, CommonUtil.getStartSiteName(++position,beanDetailSite.getStartEndSites()));
        holder.setText(R.id.textView_detail_site_end_site, CommonUtil.getEndSiteName(position,beanDetailSite.getStartEndSites()));

        String upDown ;
        if(position==1){
            upDown="上行";
        }else {
            upDown="下行";
        }
        holder.setText(R.id.textView_detail_site_upDown,upDown);

        String extend;
        int extendTextSize;
        switch (beanDetailSite.getExtend()){
            case -1:
                extend = "未发车";
                extendTextSize = 20;
                break;
            case 0:
                extend = "已到站";
                extendTextSize = 20;
                break;
            default:
                extend = Integer.toString( beanDetailSite.getExtend());
                extendTextSize = 32;
        }
        TextView textView_extend = holder.getView(R.id.textView_detail_site_extend);
        textView_extend.setText(extend);
        textView_extend.setTextSize(TypedValue.COMPLEX_UNIT_SP,extendTextSize);
    }
}
