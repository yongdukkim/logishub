package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPICE on 2016-10-26.
 */

public class IntegrationFNSActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String mTitle;
    private String mMenuOid;
    public static ArrayList<MenuData> mArrMenuList = null;
    public static ArrayList<UserData> mArrUserList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrationfns);

        setLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setLayout() {
        Intent intent = getIntent();
        mTitle = intent.getExtras().getString(Define.ACT_PUT_REQ_ACT_TITLE);
        mArrMenuList = (ArrayList<MenuData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_MENU_LIST);
        mMenuOid = intent.getExtras().getString(Define.ACT_PUT_REQ_MENU_OID);
        mArrUserList = (ArrayList<UserData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_USER_LIST);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        IntegrationFNSActivity.ViewPagerAdapter adapter = new IntegrationFNSActivity.ViewPagerAdapter(getSupportFragmentManager());

        int mMenuSize = mArrMenuList.size();

        for (int i =0; i < mMenuSize; i++) {
            if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL3) && mArrMenuList.get(i).getParentId().equals(mMenuOid)) {
                if(mArrMenuList.get(i).getName().equals(Define.MENU_NAME_INTEGRATIONFNS_LIST)) {
                    adapter.addFragment(new IntegrationFNSListFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                } else if(mArrMenuList.get(i).getName().equals(Define.MENU_NAME_DISPATCHER_LIST)) {
                    adapter.addFragment(new MainFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                } else if(mArrMenuList.get(i).getName().equals(Define.MENU_NAME_READY_TO_VEHICLE)) {
                    adapter.addFragment(new MainFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                }
            }
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String menuId, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
