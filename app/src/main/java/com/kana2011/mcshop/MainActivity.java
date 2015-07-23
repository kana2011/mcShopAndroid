package com.kana2011.mcshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.kana2011.mcshop.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    public String PREFS_NAME = "com.kana2011.mcShop";
    public static SharedPreferences settings;
    public static Boolean logged = false;
    public static int currentCredential = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PREFS_NAME, 0);
        if(settings.contains("credentials")) {
            JSONParser parser = new JSONParser();
            try {
                JSONArray credentials = (JSONArray)parser.parse(settings.getString("credentials", "[]"));
                if (credentials.size() >= (settings.getInt("currentCredential", 0) + 1)) {
                    String token = (String)((JSONObject)credentials.get(settings.getInt("currentCredential", 0))).get("token");
                    String res = this.checkAuth(token);
                    if(res == "success") {
                        Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else if(res == "error") {
                        final MainActivity m = this;
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("No connection.")
                                .setCancelable(false)
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        m.finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {final MainActivity m = this;
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Session expired. Please login again.")
                                .setCancelable(false)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        m.resetCredentials();
                                        m.showLogin();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    this.showLogin();
                    this.resetCredentials();
                }
            } catch (Exception e) {
                this.resetCredentials();
                this.showLogin();
            }
        } else {
            this.resetCredentials();
            this.showLogin();
        }
    }

    public void resetCredentials() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("credentials", "[]");
        editor.remove("currentCredential");
        editor.commit();
    }

    public String checkAuth(String token) {
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(2);
        nameValuePairList.add(new BasicNameValuePair("token", token));
        try {
            JSONParser parser = new JSONParser();
            JSONArray credentials = (JSONArray)parser.parse(settings.getString("credentials", "[]"));
            String address = (String)((JSONObject)credentials.get(settings.getInt("currentCredential", 0))).get("address");
            JSONObject res = (JSONObject)parser.parse(Util.postData(address + "/api/auth:tokenLogin", nameValuePairList));
            if((Boolean)((JSONObject)parser.parse(Util.postData(address + "/api/auth:tokenLogin", nameValuePairList))).get("status")) {
                return "success";
            } else {
                return "failed";
            }
        } catch (Exception e) {
            return "error";
        }
    }

    public void showLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
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

        return super.onOptionsItemSelected(item);
    }
}
