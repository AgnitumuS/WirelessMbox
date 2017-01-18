package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.AlbumFragmentActivity;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailFragmentBk extends Fragment {
    private static final String TAG = "AlbumFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private TextView tvAlbumIntro;
    private TextView tvAnnouncer;
    private RecyclerView mRecyclerView;

    private CommonAdapter<String> mAlbumsAdapter;
    private List<String> mDatas = new ArrayList<String>();

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
        View view = inflater.inflate(R.layout.xm_fragment_album_detailbk, container, false);
//        tvAlbumIntro = (TextView) view.findViewById(R.id.tvAlbumIntro);
//        tvAlbumIntro.setText(mAlbum.getAlbumIntro());
//        tvAnnouncer = (TextView) view.findViewById(R.id.tvAnnouncer);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvAlbumDetail);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAlbumsAdapter = new CommonAdapter<String>(getActivity(), R.layout.xm_item_album_fragment, mDatas) {
            @Override
            public void convert(ViewHolder holder, String o) {
                holder.setText(R.id.id_info, o);
            }
        };
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
                    for (Album album : relativeAlbums.getRelativeAlbumList()) {
                        mDatas.add(album.getAlbumTitle());
                    }
                    mAlbumsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
            }
        });
    }

    public static AlbumDetailFragmentBk newInstance(String title) {
        AlbumDetailFragmentBk tabFragment = new AlbumDetailFragmentBk();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

}
