package com.kana2011.mcshop;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.kana2011.mcshop.drawer.NavigationDrawerFragment;
import com.kana2011.mcshop.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeActivity extends ActionBarActivity {
    public String PREFS_NAME = "com.kana2011.mcShop";
    public static SharedPreferences settings;
    private NavigationDrawerFragment drawerFragment;
    private Toolbar toolbar;
    private static HomeActivity instance;
    private JSONObject userinfo;

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

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONParser parser = new JSONParser();
        try {
            JSONArray credentials = (JSONArray) parser.parse(settings.getString("credentials", "[]"));
            JSONObject credential = (JSONObject) credentials.get(settings.getInt("currentCredential", 0));
            String address = (String) credential.get("address");
            String token = (String) credential.get("token");

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(2);
            nameValuePairList.add(new BasicNameValuePair("token", token));
            userinfo = (JSONObject)parser.parse(Util.postData(address + "/api/user:shop", nameValuePairList));

            SharedPreferences.Editor editor = settings.edit();
            credential.put("username", userinfo.get("username"));
            editor.putString("credentials", credentials.toString());
            editor.commit();

            JSONArray groups = (JSONArray)userinfo.get("shop");
            ShopFragment fragment = new ShopFragment();
            fragment.setGroupsInfo(groups);
            fragments.add(fragment);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.main_fragment, fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

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
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
