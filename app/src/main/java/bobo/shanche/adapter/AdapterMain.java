package bobo.shanche.adapter;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanNearByStop;

/**
 * Created by bobo1 on 2017/1/27.
 */

public class AdapterMain extends CommonAdapter<BeanNearByStop> {

    private Context mContext;
    public AdapterMain(Context context, int layoutId, List<BeanNearByStop> datas) {
        super(context, layoutId, datas);
        mContext =context;
    }

    @Override
    protected void convert(ViewHolder holder, BeanNearByStop beanNearByStop, int position) {
        holder.setText(R.id.TextView_Main_NearByStop_SiteName,beanNearByStop.getSiteName());
        holder.setText(R.id.TextView_Main_NearByStop_Distance,mContext.getString(R.string.item_busstop_distance,(int)beanNearByStop.getDistance()));
    }
}
