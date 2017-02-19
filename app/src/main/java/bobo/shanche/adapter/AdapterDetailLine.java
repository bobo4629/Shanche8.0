package bobo.shanche.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanDetailLineSiteInfo;

/**
 * Created by bobo1 on 2017/2/11.
 */

public class AdapterDetailLine extends CommonAdapter<BeanDetailLineSiteInfo> {
    public void setmDatas(List<BeanDetailLineSiteInfo> mDatas) {
        this.mDatas = mDatas;
    }

    private List<BeanDetailLineSiteInfo> mDatas = null;
    private Context mContext;
    private List<Boolean> mListBoolean;


    public AdapterDetailLine(Context context, int layoutId, List<BeanDetailLineSiteInfo> datas,List<Boolean> booleens) {
        super(context, layoutId, datas);
        mContext = context;
        mListBoolean = booleens;
    }

    @Override
    protected void convert(ViewHolder holder, BeanDetailLineSiteInfo beanDetailLineSiteInfo, int position) {
        ImageView imageView = holder.getView(R.id.ImageView_detail_line);
        if(mDatas != null){
            beanDetailLineSiteInfo = mDatas.get(position);
        }
        holder.setText(R.id.TextView_detail_line_site_name,beanDetailLineSiteInfo.getSiteName());

        if(!beanDetailLineSiteInfo.getBusList().isEmpty()){
            holder.setBackgroundRes(R.id.View_detail_line_circle,R.drawable.circle_bus);
            if(mListBoolean.get(position)){
                imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_line_notice_when_bus));
            }else {
               imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_line_bus));
           }
        }else {
            holder.setBackgroundRes(R.id.View_detail_line_circle,R.drawable.circle);

            if(mListBoolean.get(position)){
                imageView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.ic_line_notice));
            }else {
                imageView.setBackground(ContextCompat.getDrawable(mContext,android.R.color.transparent));
            }




        }
    }


}
