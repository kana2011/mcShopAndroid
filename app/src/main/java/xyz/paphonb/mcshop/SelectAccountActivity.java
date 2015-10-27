package xyz.paphonb.mcshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

import xyz.paphonb.mcshop.libs.McShop;
import xyz.paphonb.mcshop.utils.Util;

public class SelectAccountActivity extends AppCompatActivity {
    public String PREFS_NAME = "xyz.paphonb.mcShop";
    public static SharedPreferences settings;
    public static int currentCredential = 0;
    private JSONArray credentials;
    private ArrayList<Account> accounts;
    private AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        final ListView listview = (ListView) findViewById(R.id.listview);

        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SelectAccountActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        accounts = new ArrayList<>();

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONParser parser = new JSONParser();
        loadCredentials();
        adapter = new AccountAdapter(this, accounts);
        listview.setAdapter(adapter);
        listview.setDivider(null);
        listview.setDividerHeight(0);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(SelectAccountActivity.this)
                        .setTitle(R.string.account_delete)
                        .setMessage(R.string.settings_signout_content)
                        .setPositiveButton(R.string.account_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCredential(position);
                                loadCredentials();
                                adapter.setData(accounts);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return false;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {

                final Account account = accounts.get(position);
                final ProgressDialog progress = ProgressDialog.show(SelectAccountActivity.this, "Please wait...",
                        "Switching account", true, false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String token = account.getToken();
                        String res = checkAuth(token);
                        if(res.equals("success")) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("currentCredential", position);
                            editor.commit();

                            List<NameValuePair> nameValuePairList = new ArrayList<>();
                            final String userInfo = McShop.postData(McShop.getCurrentCredential(SelectAccountActivity.this), "/api/user:shop", nameValuePairList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    Intent homeIntent = new Intent(SelectAccountActivity.this, HomeActivity.class);
                                    homeIntent.putExtra("userInfo", userInfo);
                                    startActivity(homeIntent);
                                    finish();
                                    MainActivity.getInstance().finish();
                                }
                            });
                        } else if(res.equals("error")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectAccountActivity.this);
                                    builder.setMessage("No connection.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectAccountActivity.this);
                                    builder.setMessage("Session expired.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    deleteCredential(position);
                                                    loadCredentials();
                                                    adapter.setData(accounts);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }
                    }
                }).start();
            }

        });
    }

    private void loadCredentials() {
        JSONParser parser = new JSONParser();
        accounts = new ArrayList<>();
        try {
            credentials = (JSONArray) parser.parse(settings.getString("credentials", "[]"));
            for (int i = 0; i < credentials.size(); i++) {
                Account account = new Account((String) ((JSONObject) credentials.get(i)).get("username"), (String) ((JSONObject) credentials.get(i)).get("address"), (String) ((JSONObject) credentials.get(i)).get("token"));
                accounts.add(account);
            }
        } catch (Exception e) {

        }
    }

    class Account {
        private String username;
        private String address;
        private String token;

        public Account(String username, String address, String token) {
            this.username = username;
            this.address = address;
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public String getAddress() {
            return address;
        }

        public String getToken() {
            return token;
        }
    }

    class AccountAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Account> accounts;

        public AccountAdapter(Context context, ArrayList<Account> accounts) {
            this.context = context;
            this.accounts = accounts;
        }

        @Override
        public int getCount() {
            return accounts.size();
        }

        @Override
        public Object getItem(int position) {
            return accounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TwoLineListItem twoLineListItem;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);

            if(position == McShop.getCurrentCredentialIndex(SelectAccountActivity.this)) {
                twoLineListItem.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            TextView text1 = twoLineListItem.getText1();
            TextView text2 = twoLineListItem.getText2();

            text1.setTextColor(getResources().getColor(R.color.md_white_1000));
            text2.setTextColor(getResources().getColor(R.color.md_white_1000));
            text1.setText(accounts.get(position).getUsername());
            text2.setText(accounts.get(position).getAddress());

            return twoLineListItem;
        }

        public void setData(ArrayList<Account> data) {
            this.accounts = data;
        }
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

    private void deleteCredential(int position) {
        JSONArray list = new JSONArray();
        JSONArray jsonArray = credentials;
        int len = jsonArray.size();
        if (jsonArray != null) {
            for (int i = 0; i < len; i++) {
                //Excluding the item at position
                if (i != position) {
                    list.add(jsonArray.get(i));
                }
            }
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("credentials", list.toString());
        if(position == McShop.getCurrentCredentialIndex(SelectAccountActivity.this)) {
            editor.putInt("currentCredential", 0);
        }
        editor.commit();
    }

}
