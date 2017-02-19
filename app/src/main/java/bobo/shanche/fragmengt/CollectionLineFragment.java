package bobo.shanche.fragmengt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.activity.DetailLineActivity;
import bobo.shanche.bean.BeanCollection;
import bobo.shanche.db.DbHelper;

/**
 * Created by bobo1 on 2017/2/14.
 */

public class CollectionLineFragment extends Fragment {
    private List<BeanCollection> mDatas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection,container,false);
        DbHelper db = new DbHelper(getContext());
        mDatas = new ArrayList<>();
        mDatas.addAll(db.getCollection(getString(R.string.db_table_line)));
        db.close();

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.RecyclerView_collection);
        AdapterCollectionLine adapterCollection = new AdapterCollectionLine(getContext(),R.layout.item_collection,mDatas);
        adapterCollection.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getActivity(), DetailLineActivity.class);
                intent.putExtra("id", mDatas.get(position).getId());
                intent.putExtra("lineName",mDatas.get(position).getName());
                intent.putExtra("upDown",mDatas.get(position).getUpDown());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapterCollection);
        return view;
    }

    class AdapterCollectionLine extends CommonAdapter<BeanCollection> {

        AdapterCollectionLine(Context context, int layoutId, List<BeanCollection> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, BeanCollection beanCollection, int position) {
            holder.setImageResource(R.id.ImageView_collection,R.drawable.ic_com_line);
            holder.setText(R.id.TextView_collection,beanCollection.getName());
            String upDown;
            if(beanCollection.getUpDown() == 1){
                upDown = "上行";
            }else {
                upDown = "下行";
            }
            holder.setText(R.id.TextView_collection_upDown,upDown);
        }
    }

}
