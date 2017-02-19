package bobo.shanche.adapter;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanSearchSite;

/**
 * Created by bobo1 on 2017/2/7.
 */

public class AdapterSearchSite extends CommonAdapter<BeanSearchSite> {
    public AdapterSearchSite(Context context, int layoutId, List<BeanSearchSite> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, BeanSearchSite beanSearchSite, int position) {
        holder.setImageResource(R.id.ImageView_searchItem,R.drawable.ic_com_site);
        holder.setText(R.id.TextView_searchItem,beanSearchSite.getSiteName());
    }
}
