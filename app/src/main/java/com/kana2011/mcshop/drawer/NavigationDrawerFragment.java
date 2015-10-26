package com.kana2011.mcshop.drawer;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kana2011.mcshop.HomeActivity;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.libs.SimpleSectionedRecyclerViewAdapter;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements DrawerLayout.DrawerListener {
    private RecyclerView recyclerView;
    public static final String PREFS_NAME = "com.kana2011.mcShop";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private JSONObject userInfo;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private DrawerAdapter adapter;
    private TextView mUsernameView;
    private TextView mMoneyView;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if(savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView)layout.findViewById(R.id.drawer_list);

        mUsernameView = (TextView)layout.findViewById(R.id.username_view);
        mMoneyView = (TextView)layout.findViewById(R.id.money_view);

        adapter = new DrawerAdapter(getActivity(), getData());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<>();

        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(3,"Section 1"));

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getActivity(),R.layout.section,R.id.section_text,adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        recyclerView.setAdapter(mSectionedAdapter);

        return layout;
    }

    public void setUsername(String username) {
        mUsernameView.setText(username);
    }

    public void setMoney(String money) {
        mMoneyView.setText("Money: " + money);
    }

    public static List<DrawerItem> getData() {
        List<DrawerItem> data = new ArrayList<>();
        int[] icons = {R.drawable.ic_shopping_cart, R.drawable.ic_credit_card, R.drawable.ic_view_headline, R.drawable.ic_settings};
        String[] titles = {"Shop", "Topup", "Transaction", "Settings"};
        for(int i = 0; i < icons.length; i++) {
            DrawerItem current = new DrawerItem();
            current.iconId = icons[i];
            current.title = titles[i];
            data.add(current);
        }
        return data;
    }

    public void setUp(int fragmentId, final DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        if(!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(HomeActivity.getInstance().getCurrentFragment() == 0) {
                mDrawerLayout.openDrawer(getView());
            } else {
                HomeActivity.getInstance().showShop();
            }
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if(HomeActivity.getInstance().getCurrentFragment() == 0) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        } else {
            mDrawerToggle.onDrawerSlide(drawerView, 1);
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
