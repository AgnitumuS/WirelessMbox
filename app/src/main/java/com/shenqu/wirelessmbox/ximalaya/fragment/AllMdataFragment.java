package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.MdataFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaData;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 * 同样包含的热门分类
 */

public class AllMdataFragment extends BaseFragment {
    private static final String TAG = "AllMdataFra";
    private LayoutInflater mInflater;

    private PullToRefreshListView mListView;
    private AlbumListAdapter mAlbumAdapter;
    private List<Album> mAlbumList;

    private String mCategoryId;
    private String mCategoryName;
    private List<MetaData> mMetaDatas = new ArrayList<>();
    /**
     * 创建标签列表 加上手动创建的 无过滤 项
     */
    private List<List<Attributes>> mAttrList = new ArrayList<>();
    private int[] indexOfAttrs;
    private final String[] ATTR_PAY = {/*"不限", */"付费", "免费"};
    private final String[] ATTR_DIMENSION = {/*"默认", */"最火", "最新", "经典"};
    private final String[] CALC_DIMENSION = {"1", "1", "2", "3"};
    private int iAlbumPage = 1;

    /**
     * 初始化各种layout
     */
    private void initAlbumListView() {
        mAlbumAdapter.notifyDataSetChanged();
    }

    private void initMetaView() {
        MetaData m = new MetaData();
        //手动添加 ATTR_PAY 和 ATTR_DIMENSION Meta类型
        List<Attributes> as = new ArrayList<>();
        for (String st : ATTR_DIMENSION) {
            Attributes a = new Attributes();
            a.setDisplayName(st);
            a.setAttrKey("");
            a.setAttrValue("");
            as.add(a);
        }
        m.setAttributes(as);
        m.setDisplayName("默认");
        mMetaDatas.add(m);

        //各种标签栏的 content
        LinearLayout scroll_tab = (LinearLayout) mInflater.inflate(R.layout.scroll_tablayout, (ViewGroup) getView(), false);
        LinearLayout tabContent = (LinearLayout) scroll_tab.findViewById(R.id.tabContent);

        int i = 0;
        for (MetaData meta : mMetaDatas) {
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            //包含标签栏
            View view = mInflater.inflate(R.layout.xm_item_tabholder, tabContent, false);
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rcTabContent);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            /**将 无过滤 选项手动加入*/
            Attributes attr = new Attributes();
            attr.setAttrKey("");
            attr.setAttrValue("");
            attr.setDisplayName(meta.getDisplayName()); //全部

            List<Attributes> attrs = new ArrayList<>();
            attrs.add(attr);
            attrs.addAll(meta.getAttributes());
            mRecyclerView.setAdapter(new TabAdapter(getContext(), attrs, i++));

            mAttrList.add(attrs);

            tabContent.addView(view);
        }

        mListView.getRefreshableView().addHeaderView(scroll_tab);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JLLog.LOGI(TAG, "You clicked the " + position + "item.");
                startActivity(new Intent(getActivity(), AlbumFragmentActivity.class));
            }
        });
        /**初始化metas对应的index数组*/
        indexOfAttrs = new int[i];
    }

    private boolean isLoading = false;

    private void loadMetaData() {
        if (isLoading)
            return;
        isLoading = true;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        CommonRequest.getMetadataList(map, new IDataCallBack<MetaDataList>() {
            @Override
            public void onSuccess(MetaDataList metaDataList) {
                isLoading = false;
                if (metaDataList != null && metaDataList.getMetaDatas() != null) {
                    mMetaDatas.clear();
                    mMetaDatas.addAll(metaDataList.getMetaDatas());
                    initMetaView();
                    loadAlbumsData(true);
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                JLLog.showToast(getActivity(), "加载失败~");
            }
        });
    }

    private void loadAlbumsData(boolean isInit) {
        if (isLoading)
            return;
        isLoading = true;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        int i = 0;
        if (!isInit) {
            String attrStr = "";
            for (; i < mAttrList.size(); i++) {
                Attributes attr = mAttrList.get(i).get(indexOfAttrs[i]);
                if (attr.getAttrKey().length() > 0) {
                    //attr_key1:attr_value1;attr_key2:attr_value2
                    attrStr += attr.getAttrKey() + ":" + attr.getAttrValue() + ";";
                }
            }
            if (attrStr.length() > 2) {
                JLLog.LOGI(TAG, "attrStr = " + CALC_DIMENSION[indexOfAttrs[indexOfAttrs.length - 1]]);
                map.put(DTransferConstants.METADATA_ATTRIBUTES, attrStr.substring(0, attrStr.length() - 1));
            }
        }

        map.put(DTransferConstants.CALC_DIMENSION, CALC_DIMENSION[indexOfAttrs[indexOfAttrs.length - 1]]);//计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        map.put(DTransferConstants.PAGE, "" + iAlbumPage);
        CommonRequest.getMetadataAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                isLoading = false;
                if (albumList != null && albumList.getAlbums() != null && albumList.getAlbums().size() > 0) {
                    if (mAlbumList == null)
                        mAlbumList = albumList.getAlbums();
                    else
                        mAlbumList.addAll(albumList.getAlbums());
                    mListView.onRefreshComplete();
                    initAlbumListView();
                }
            }

            @Override
            public void onError(int i, String s) {
                JLLog.showToast(getActivity(), "加载失败~");
                isLoading = false;
                mListView.onRefreshComplete();
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = inflater;
        View view = inflater.inflate(R.layout.xm_fragment_mdata, container, false);
        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                //mListView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                iAlbumPage++;
                loadAlbumsData(false);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        mCategoryId = getActivity().getIntent().getStringExtra("CategoryId");
        mCategoryName = getActivity().getIntent().getStringExtra("CategoryName");

        mAlbumAdapter = new AlbumListAdapter();
        mListView.setAdapter(mAlbumAdapter);
//        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//            @Override
//            public void onLastItemVisible() {
//                JLLog.LOGV(TAG, "onLatItemVisible refresh the list.");
//            }
//        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = mAlbumList.get(position);
                Map<String, String> map = new HashMap<String, String>();
                JLLog.LOGI(TAG, "you clicked the xm_item_album_fragment " + album.getAlbumTitle());
                isLoading = true;
                //获取某个专辑的相关推荐
                map.put(DTransferConstants.ALBUMID, album.getId() + "");
                CommonRequest.getRelativeAlbums(map, new IDataCallBack<RelativeAlbums>() {
                    @Override
                    public void onSuccess(RelativeAlbums relativeAlbums) {
                        isLoading = false;
                        if (relativeAlbums != null && relativeAlbums.getRelativeAlbumList() != null) {
                            String name = "";
                            for (Album album : relativeAlbums.getRelativeAlbumList()) {
                                name += album.getAlbumTitle() + "/";
                            }
                            JLLog.LOGV(TAG, "RelativeAlbums = " + name);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        isLoading = false;
                    }
                });
            }
        });
        loadMetaData();
    }

    public void refresh() {
        loadMetaData();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    /**
     * 发现页 配置的 分类维度 -- ListView
     */
    class AlbumListAdapter extends BaseAdapter {

        class AlbumHolder {
            ViewGroup content;
            ImageView ivCover;
            TextView tvTitle;
            TextView tvIntro;
            TextView tvCacl;
            TextView tvInclude;
        }

        @Override
        public int getCount() {
            if (mAlbumList == null) {
                return 0;
            }
            return mAlbumList.size();
        }

        @Override
        public Album getItem(int position) {
            if (mAlbumList != null && mAlbumList.size() > 0)
                return mAlbumList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.xm_item_meta_album, parent, false);
                holder = new AlbumHolder();
                holder.content = (ViewGroup) convertView;
                holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
                holder.tvCacl = (TextView) convertView.findViewById(R.id.tvCacl);
                holder.tvInclude = (TextView) convertView.findViewById(R.id.tvInclude);

                convertView.setTag(holder);
            } else {
                holder = (AlbumHolder) convertView.getTag();
            }

            Album album = getItem(position);
            if (album != null) {
                holder.tvTitle.setText(album.getAlbumTitle());
                if (album.getAlbumIntro() == null || album.getAlbumIntro().length() == 0)
                    holder.tvIntro.setText(album.getLastUptrack().getTrackTitle());
                else
                    holder.tvIntro.setText(album.getAlbumIntro());
                long count = album.getPlayCount();
                if (count > 10000) {
                    holder.tvCacl.setText("" + count / 10000 + "万");
                } else {
                    holder.tvCacl.setText("" + count);
                }
                holder.tvInclude.setText(album.getIncludeTrackCount() + "集");
                x.image().bind(holder.ivCover, album.getCoverUrlSmall());
            }
            return convertView;
        }
    }

    /**
     * 横向listView 即：RecycleView 的 Adapter
     */
    class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabHolder> implements View.OnClickListener {

        class TabHolder extends RecyclerView.ViewHolder {
            TabHolder(View itemView) {
                super(itemView);
            }

            CheckedTextView tvMeta;
        }

        private int iRow;
        private LayoutInflater mInflater;
        private List<Attributes> mAttrs;

        /**
         * 构造函数
         */
        TabAdapter(Context context, List<Attributes> a, int i) {
            mInflater = LayoutInflater.from(context);
            mAttrs = a;
            iRow = i;
        }

        @Override
        public int getItemCount() {
            return mAttrs.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public TabHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.xm_item_hortab, viewGroup, false);
            TabHolder holder = new TabHolder(view);
            holder.tvMeta = (CheckedTextView) view.findViewById(R.id.tvTitle);
            holder.tvMeta.setOnClickListener(this);
            return holder;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(TabHolder holder, int i) {
            //JLLog.LOGI(TAG, "------ onBindViewHolder() " + i);
            holder.tvMeta.setText(mAttrs.get(i).getDisplayName());
            holder.tvMeta.setTag(i);
            if (i == indexOfAttrs[iRow])
                holder.tvMeta.setChecked(true);
            else
                holder.tvMeta.setChecked(false);
        }

        @Override
        public void onClick(View v) {
            //JLLog.LOGI(TAG, "onClicked the indexParent = " + iRow + ", " + (int) v.getTag());
            indexOfAttrs[iRow] = (int) v.getTag();
            notifyDataSetChanged();
            if (mAlbumList != null)
                mAlbumList.clear();
            iAlbumPage = 1;
            loadAlbumsData(false);
        }
    }
}