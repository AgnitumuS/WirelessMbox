package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.base.ViewHolder;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import org.xutils.x;

/**
 * Created by JongLim on 2017/1/9.
 */

public class CategoryFragment extends BaseFragment {
    private static final String TAG = "CategoryFra";
    private Context mContext;

    //热门声音分类
    private ListView mListView;
    private CategoryList mCategoryList = null;
    private CategoryAdapter mCategoryAdapter;

    private boolean mLoading = false;

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        //Map<String, String> param = new HashMap<String, String>();
        CommonRequest.getCategories(null, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                JLLog.LOGV(TAG, "GetCategories onSuccess " + (categoryList != null));
                if (categoryList != null && categoryList.getCategories() != null && categoryList.getCategories().size() != 0) {
                    if (mCategoryList == null) {
                        mCategoryList = categoryList;
                    } else {
                        mCategoryList.getCategories().addAll(categoryList.getCategories());
                    }
                    //mTrackAdapter.notifyDataSetChanged();
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                JLLog.LOGE(TAG, "GetCategories onError " + i + ", " + s);
                mLoading = false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_category, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mCategoryAdapter = new CategoryAdapter ();
        mListView.setAdapter(mCategoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Collections.shuffle(mTrackHotList.getTracks());
            }
        });

        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class CategoryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mCategoryList == null || mCategoryList.getCategories() == null) {
                return 0;
            }
            return mCategoryList.getCategories().size();
        }

        @Override
        public Object getItem(int position) {
            return mCategoryList.getCategories().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_category_track, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Category category = mCategoryList.getCategories().get(position);
            holder.content.setBackgroundColor(Color.WHITE);
            holder.title.setText(category.getCategoryName());
            x.image().bind(holder.cover, category.getCoverUrlSmall());
            return convertView;
        }
    }

}
