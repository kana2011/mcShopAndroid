package xyz.paphonb.mcshop.shop;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.tabs.SlidingTabLayout;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {
    private FragmentTabHost mTabHost;
    private ViewPager mPager;
    private TabLayout mTabs;
    private JSONArray groupsInfo;

    public void setGroupsInfo(JSONArray info) {
        groupsInfo = info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            JSONParser parser = new JSONParser();
            try {
                groupsInfo = (JSONArray)parser.parse(savedInstanceState.getString("groupsInfo", "{}"));
            } catch (Exception e) {
                getActivity().finish();
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_shop,container, false);

        mPager = (ViewPager)rootView.findViewById(R.id.pager);
        mPager.setAdapter(new McShopPagerAdapter(getChildFragmentManager()));
        mTabs = (TabLayout)rootView.findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mPager);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupsInfo", groupsInfo.toString());
    }

    class McShopPagerAdapter extends FragmentPagerAdapter {

        public McShopPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            ShopGroupFragment fragment = new ShopGroupFragment();
            fragment.setGroupInfo((JSONObject)groupsInfo.get(position));
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (String)((JSONObject)groupsInfo.get(position)).get("dispname");
        }

        @Override
        public int getCount() {
            return groupsInfo.size();
        }
    }

}
