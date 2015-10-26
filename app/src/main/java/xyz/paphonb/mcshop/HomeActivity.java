package xyz.paphonb.mcshop;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xyz.paphonb.mcshop.libs.McShop;
import xyz.paphonb.mcshop.shop.ShopFragment;
import xyz.paphonb.mcshop.topup.TopupFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    public String PREFS_NAME = "xyz.paphonb.mcShop";
    public static SharedPreferences settings;
    private Toolbar toolbar;
    private static HomeActivity instance;
    private JSONObject userInfo;
    private int currentFragment = 0;

    private List<Fragment> fragments = new ArrayList<>();
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(this);
        toggle.syncState();

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_home, null);
        TextView mUsername = (TextView) headerView.findViewById(R.id.username);
        TextView mMoney = (TextView) headerView.findViewById(R.id.money);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addHeaderView(headerView);
        navigationView.setCheckedItem(R.id.nav_shop);

        if(getIntent().hasExtra("userInfo")) {
            userInfo = McShop.getJsonObject(getIntent().getStringExtra("userInfo"));
        } else {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            userInfo = McShop.getJsonObject(McShop.postData(McShop.getCurrentCredential(this), "/api/user:shop", nameValuePairList));
        }

        mUsername.setText((String)userInfo.get("username"));
        mMoney.setText("Money: " + userInfo.get("money"));

        McShop.saveUsername(this, (String) userInfo.get("username"));

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFragment == 0) {
                super.onBackPressed();
            } else {
                showShop();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shop) {
            showShop();
        } else if (id == R.id.nav_topup) {
            showFragment(1);
        } else if (id == R.id.nav_transactions) {
            showFragment(2);
        } else if (id == R.id.nav_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    public void showFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment, fragments.get(index));

        transaction.commit();

        currentFragment = index;

        toggle.onDrawerSlide(drawer, 1);
    }

    public void showShop() {
        navigationView.setCheckedItem(R.id.nav_shop);
        showFragment(0);
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                toggle.onDrawerSlide(drawer, slideOffset);
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
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if(currentFragment == 0) {
            toggle.onDrawerSlide(drawerView, slideOffset);
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
