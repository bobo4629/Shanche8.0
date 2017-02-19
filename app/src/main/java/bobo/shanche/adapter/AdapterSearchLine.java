package bobo.shanche.adapter;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanSearchLine;

/**
 * Created by bobo1 on 2017/2/7.
 */

public class AdapterSearchLine extends CommonAdapter<BeanSearchLine> {
    public AdapterSearchLine(Context context, int layoutId, List<BeanSearchLine> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, BeanSearchLine beanSearchLine, int position) {
        holder.setImageResource(R.id.ImageView_searchItem,R.drawable.ic_com_line);
        holder.setText(R.id.TextView_searchItem,beanSearchLine.getLineName());
    }
}
