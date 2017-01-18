package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.AlbumListAdapter;
import com.shenqu.wirelessmbox.ximalaya.adapter.TrackListAdapter;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumTracksFragment extends Fragment {
    private static final String TAG = "AlbumFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private PullToRefreshListView tracksView;
    private TrackListAdapter mTrackAdapter;
    private List<Track> mTrackList;

    private boolean isLoading;
    private int iTracksPage;

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

        tracksView = (PullToRefreshListView) view.findViewById(R.id.tracksView);
        mTrackList = new ArrayList<>();
        mTrackAdapter = new TrackListAdapter(mActivity, mTrackList);
        tracksView.setAdapter(mTrackAdapter);

        iTracksPage = 1;
        doLoadAlbumDetail();
        return view;
    }

    private void doLoadAlbumDetail() {
        if (isLoading)
            return;
        isLoading = true;
        //获取某个专辑的相关推荐
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, mAlbum.getId() + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, ""+iTracksPage);
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                isLoading = false;
                mTrackList.addAll(trackList.getTracks());
                mTrackAdapter.notifyDataSetChanged();
            }
            @Override
            public void onError(int i, String s) {
                isLoading = false;
            }
        });
    }

    public static AlbumTracksFragment newInstance(String title) {
        AlbumTracksFragment tabFragment = new AlbumTracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

}
