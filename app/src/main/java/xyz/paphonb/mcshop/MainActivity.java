package xyz.paphonb.mcshop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import xyz.paphonb.mcshop.libs.McShop;
import xyz.paphonb.mcshop.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private static MainActivity instance;
    public String PREFS_NAME = "xyz.paphonb.mcShop";
    public static SharedPreferences settings;
    public static int currentCredential = 0;
    private JSONArray credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        final MainActivity context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                if(settings.contains("credentials")) {
                    JSONParser parser = new JSONParser();
                    try {
                        credentials = (JSONArray)parser.parse(settings.getString("credentials", "[]"));
                        if (credentials.size() >= (settings.getInt("currentCredential", 0) + 1)) {
                            String token = (String)((JSONObject)credentials.get(settings.getInt("currentCredential", 0))).get("token");
                            String res = context.checkAuth(token);
                            if(res.equals("success")) {
                                List<NameValuePair> nameValuePairList = new ArrayList<>();
                                final String userInfo = McShop.postData(McShop.getCurrentCredential(context), "/api/user:shop", nameValuePairList);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                                        homeIntent.putExtra("userInfo", userInfo);
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                });
                            } else if(res.equals("error")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        selectAccountDialog();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Session expired. Please login again.")
                                                .setCancelable(false)
                                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        int position = settings.getInt("currentCredential", 0);
                                                        JSONArray list = new JSONArray();
                                                        int len = credentials.size();
                                                        if (credentials != null) {
                                                            for (int i = 0; i < len; i++) {
                                                                //Excluding the item at position
                                                                if (i != position) {
                                                                    list.add(credentials.get(i));
                                                                }
                                                            }
                                                        }
                                                        SharedPreferences.Editor editor = settings.edit();
                                                        editor.putString("credentials", list.toString());
                                                        editor.putInt("currentCredential", 0);
                                                        editor.commit();
                                                        context.showLogin();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
                            }
                        } else {
                            context.resetCredentials();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.showLogin();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        context.resetCredentials();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                context.showLogin();
                            }
                        });
                    }
                } else {
                    context.resetCredentials();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.showLogin();
                        }
                    });
                }
            }
        }).start();
    }

    public void resetCredentials() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("credentials", "[]");
        editor.remove("currentCredential");
        editor.commit();
    }

    public String checkAuth(String token) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray credentials = (JSONArray)parser.parse(settings.getString("credentials", "[]"));
            String address = (String)((JSONObject)credentials.get(settings.getInt("currentCredential", 0))).get("address");
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(2);
            nameValuePairList.add(new BasicNameValuePair("token", token));
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

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void selectAccountDialog() {
        Intent selectAccountIntent = new Intent(this, SelectAccountActivity.class);
        startActivity(selectAccountIntent);
        finish();
    }
}
