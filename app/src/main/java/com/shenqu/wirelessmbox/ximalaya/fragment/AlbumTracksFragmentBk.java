package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.widget.LoadMoreFooterView;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.IRecyclerAdapter;
import com.shenqu.wirelessmbox.ximalaya.adapter.OnItemClickListener;
import com.shenqu.wirelessmbox.ximalaya.adapter.TrackListAdapter;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumTracksFragmentBk extends Fragment implements OnItemClickListener<Track>, OnRefreshListener, OnLoadMoreListener {
    private static final String TAG = "AlbumFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private IRecyclerView iRecyclerView;
    private LoadMoreFooterView loadMoreFooterView;
    private IRecyclerAdapter mTrackAdapter;
    private List<Track> mTrackList;

    private boolean isLoading;
    private int iTracksPage;

    public static AlbumTracksFragmentBk newInstance(String title) {
        AlbumTracksFragmentBk tabFragment = new AlbumTracksFragmentBk();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
        }
        mActivity = (AlbumFragmentActivity) getActivity();
        mAlbum = mActivity.mAlbum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xm_fragment_album_tracks, container, false);

        iRecyclerView = (IRecyclerView) view.findViewById(R.id.iRecyclerView);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //iRecyclerView.addHeaderView(bannerView);

        loadMoreFooterView = (LoadMoreFooterView) iRecyclerView.getLoadMoreFooterView();

        mTrackList = new ArrayList<>();
        mTrackAdapter = new IRecyclerAdapter(mTrackList);
        iRecyclerView.setAdapter(mTrackAdapter);

        mTrackAdapter.setOnItemClickListener(this);
        iRecyclerView.setOnRefreshListener(this);
        iRecyclerView.setOnLoadMoreListener(this);

        iTracksPage = 1;
        doLoadAlbumTracks();
        return view;
    }

    private void doLoadAlbumTracks() {
        if (isLoading)
            return;
        isLoading = true;
        //获取某个专辑的相关推荐
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbum.getId() + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, "" + iTracksPage);
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList == null || trackList.getTracks() == null || trackList.getTracks().size() == 0)
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                else {
                    iRecyclerView.setRefreshing(false);
                    isLoading = false;
                    if (iTracksPage == 1)
                        mTrackList.clear();
                    mTrackList.addAll(trackList.getTracks());
                    mTrackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                iRecyclerView.setRefreshing(false);
                isLoading = false;
            }
        });
    }

    @Override
    public void onItemClick(int position, Track track, View v) {
        JLLog.LOGI(TAG, "" + mTrackList.get(position).getDownloadUrl());
        JLLog.LOGI(TAG, "" + mTrackList.get(position).getPlayUrl32());
        MyApplication.getControler().setHandler(mActivity.mHandler);
        MyApplication.getControler().setPlayURI(mTrackList.get(position).getPlayUrl64());
    }

    @Override
    public void onRefresh() {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
        iTracksPage = 1;
        doLoadAlbumTracks();
    }

    @Override
    public void onLoadMore() {
        if (loadMoreFooterView.canLoadMore() && mTrackAdapter.getItemCount() > 0) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            iTracksPage++;
            doLoadAlbumTracks();
        }
    }
}
