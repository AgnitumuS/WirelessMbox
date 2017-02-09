package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shenqu.wirelessmbox.R;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 */
public class AnnouncerFragment extends BaseFragment {
    private static final String TAG = "AnnouncerFra";
    private Context mContext;
    private ListView mListView;
    private boolean mLoading = false;

    private CommonRequest mXimalaya;

    public void refresh() {
        loadData();
    }

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> param = new HashMap<String, String>();
        CommonRequest.getAnnouncerList(param, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                mLoading = false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        mXimalaya = CommonRequest.getInstanse();
        //mListView.setAdapter(mTrackAdapter);

        loadData();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

}
