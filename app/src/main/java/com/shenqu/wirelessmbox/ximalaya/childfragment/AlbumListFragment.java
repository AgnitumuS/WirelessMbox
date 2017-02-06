package com.shenqu.wirelessmbox.ximalaya.childfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.AlbumListAdapter;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by JongLim on 2017/1/9.
 * MdataFragmentActivity 下面 SmartTabLayout 对应的 Fragment
 */

public class AlbumListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "TracksFra";
    private Context mContext;
    private PullToRefreshListView mListView;
    private AlbumListAdapter mAlbumsAdapter;
    private List<Album> mAlbumList = new ArrayList<Album>();
    private int iAlbumPage = 1;

    private boolean isLoading = false;

    private String mTagName = "";
    private String mCategoryId = "";

    private void doLoadAlbumsData() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        Map<String ,String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID ,mCategoryId);
        map.put(DTransferConstants.TAG_NAME ,mTagName);
        map.put(DTransferConstants.CALC_DIMENSION ,"3");
        map.put(DTransferConstants.PAGE ,"" + iAlbumPage);
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>(){
            @Override
            public void onSuccess(AlbumList albumList) {
                if (albumList != null && albumList.getAlbums() != null && albumList.getAlbums().size() > 0) {
                    mAlbumList.addAll(albumList.getAlbums());
                    mListView.onRefreshComplete();
                    mAlbumsAdapter.notifyDataSetChanged();
                }
                isLoading = false;
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
        View view = inflater.inflate(R.layout.xm_fragment_mdata, container, false);
        mListView = (PullToRefreshListView) view.findViewById(R.id.albumsView);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                iAlbumPage++;
                doLoadAlbumsData();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mTagName = getArguments().getString("TAGNAME");
        mCategoryId = getArguments().getString("CATEGORYID");
        mAlbumsAdapter = new AlbumListAdapter(mActivity, mAlbumList);
        mListView.setAdapter(mAlbumsAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        doLoadAlbumsData();
    }

    public void refresh() {
        iAlbumPage = 1;
        mAlbumList.clear();
        doLoadAlbumsData();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JLLog.LOGI(TAG, "You clicked the " + position + " item.");

        Intent intent = new Intent(getActivity(), AlbumFragmentActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("mAlbum", mAlbumList.get(position - 1));
        intent.putExtras(b);
        startActivity(intent);
    }
}
