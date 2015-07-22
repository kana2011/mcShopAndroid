package com.kana2011.mcshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends ActionBarActivity {
    public String PREFS_NAME = "com.kana2011.mcShop";
    public static SharedPreferences settings;
    public static Boolean logged = false;
    public static int credentialsIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PREFS_NAME, 0);
        if(settings.contains("credentials")) {
            JSONParser parser = new JSONParser();
            try {
                JSONArray credentials = (JSONArray)parser.parse(settings.getString("credentials", "[]"));
                if (credentials.size() > 0) {
                    System.out.println(((JSONObject)credentials.get(0)).get("token"));
                } else {
                    this.showLogin();
                }
            } catch (Exception e) {

            }
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("credentials", "{}");
            editor.commit();
            this.showLogin();
        }
    }

    public void showLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(loginIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
