package com.kana2011.mcshop;

import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.kana2011.mcshop.drawer.NavigationDrawerFragment;
import com.kana2011.mcshop.libs.McShop;
import com.kana2011.mcshop.shop.ShopFragment;
import com.kana2011.mcshop.topup.TopupFragment;
import com.kana2011.mcshop.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends ActionBarActivity {
    public String PREFS_NAME = "com.kana2011.mcShop";
    public static SharedPreferences settings;
    private NavigationDrawerFragment drawerFragment;
    private Toolbar toolbar;
    private static HomeActivity instance;
    private JSONObject userInfo;
    private int currentFragment = 0;

    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.instance = this;

        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        List<NameValuePair> nameValuePairList = new ArrayList<>();
        userInfo = McShop.getJsonObject(McShop.postData(McShop.getCurrentCredential(this), "/api/user:shop", nameValuePairList));

        drawerFragment.setUsername((String)userInfo.get("username"));
        drawerFragment.setMoney("" + userInfo.get("money"));

        McShop.saveUsername(this, (String)userInfo.get("username"));

        JSONArray groups = (JSONArray)userInfo.get("shop");
        ShopFragment fragment = new ShopFragment();
        fragment.setGroupsInfo(groups);
        fragments.add(fragment);
        fragments.add(new TopupFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    public NavigationDrawerFragment getDrawerFragment() {
        return drawerFragment;
    }

    public void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment, fragments.get(index));

        transaction.commit();

        currentFragment = index;
    }

    public void showShop() {
        showFragment(0);
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                drawerFragment.getDrawerToggle().onDrawerSlide(drawerFragment.getView(), slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.start();
    }

    public int getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == 0) {
            super.onBackPressed();
        } else {
            showShop();
        }
    }
}
