package com.shenqu.wirelessmbox.ximalaya;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.fragment.AlbumDetailFragment;
import com.shenqu.wirelessmbox.ximalaya.fragment.AlbumDetailFragmentBk;
import com.shenqu.wirelessmbox.ximalaya.fragment.AlbumTracksFragment;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.xutils.x;

public class AlbumFragmentActivity extends BaseFragmentActivity {
    private static final String TAG = "AlbumActi";

    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAnnouncer;
    private TextView tvCacl;
    private TextView tvCategory;

    /**
     * 因为这里的 tabLayout 位于view的中部，且要悬浮的效果，所以要用到 StickyNavLayout
     * StickyNavLayout 需要计算tab高度，这里的tabLayout必须使用其里面的id
     * */
    private TabLayout tabLayout;
    private String[] mTitles = new String[]{"详情", "节目"};

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private Fragment[] mFragments = new Fragment[mTitles.length];

    public Album mAlbum;

    private void initBaseView() {
        setTitleViewBackground(R.color.grey_light_pro_alpha);
        setTitle("专辑详情");
        setTitleColor(R.color.text_grey);
        getbtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hidebtn_right();
    }

    private void findViews() {
        ivCover = (ImageView) findViewById(R.id.ivCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAnnouncer = (TextView) findViewById(R.id.tvAnnouncer);
        tvCacl = (TextView) findViewById(R.id.tvCacl);
        tvCategory = (TextView) findViewById(R.id.tvCategory);

        tabLayout = (TabLayout) findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
    }

    private void initDatas() {
        x.image().bind(ivCover, mAlbum.getCoverUrlMiddle());
        tvTitle.setText(mAlbum.getAlbumTitle());
        tvAnnouncer.setText("主播：" + mAlbum.getAnnouncer().getNickname());
        long count = mAlbum.getPlayCount();
        if (count > 10000) {
            tvCacl.setText("播放：" + count / 10000 + "万");
        } else {
            tvCacl.setText("播放：" + count);
        }
        tvCategory.setText("分类：" + mAlbum.getAlbumTags());

        mFragments[0] = AlbumDetailFragment.newInstance(mTitles[0]);
        mFragments[1] = AlbumTracksFragment.newInstance(mTitles[1]);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position % mTitles.length];
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void initEvents() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.setScrollPosition(position, positionOffset, true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.xm_activity_album);

        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("mAlbum");

        initBaseView();

        findViews();

        initDatas();

        initEvents();
    }

}
