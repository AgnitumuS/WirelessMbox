package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.AlbumListAdapter;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailFragment extends Fragment {
    private static final String TAG = "AlbumFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private TextView tvAlbumIntro;
    private TextView tvAnnouncer;
    private ListView mRecyclerView;
    private AlbumListAdapter mAlbumsAdapter;
    private List<Album> mAlbumList;

    private boolean isLoading;

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
        View view = inflater.inflate(R.layout.xm_fragment_album_detail, container, false);
        tvAlbumIntro = (TextView) view.findViewById(R.id.tvAlbumIntro);
        tvAlbumIntro.setText(mAlbum.getAlbumIntro());
        tvAnnouncer = (TextView) view.findViewById(R.id.tvAnnouncer);

        mRecyclerView = (ListView) view.findViewById(R.id.rvAlbumDetail);
        mAlbumList = new ArrayList<>();
        mAlbumsAdapter = new AlbumListAdapter(mActivity, mAlbumList);
        mRecyclerView.setAdapter(mAlbumsAdapter);

        doLoadAlbumDetail();
        return view;
    }

    private void doLoadAlbumDetail() {
        if (isLoading)
            return;
        isLoading = true;
        //获取某个专辑的相关推荐
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUMID, mAlbum.getId() + "");
        CommonRequest.getRelativeAlbums(map, new IDataCallBack<RelativeAlbums>() {
            @Override
            public void onSuccess(RelativeAlbums relativeAlbums) {
                isLoading = false;
                if (relativeAlbums != null && relativeAlbums.getRelativeAlbumList() != null) {
                    mAlbumList.addAll(relativeAlbums.getRelativeAlbumList());
                    mAlbumsAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(int i, String s) {
                isLoading = false;
            }
        });
    }

    public static AlbumDetailFragment newInstance(String title) {
        AlbumDetailFragment tabFragment = new AlbumDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

}
