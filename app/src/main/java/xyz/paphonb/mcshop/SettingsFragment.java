package xyz.paphonb.mcshop;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

import xyz.paphonb.mcshop.libs.McShop;
import xyz.paphonb.mcshop.utils.Util;


public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences settings;
    public String PREFS_NAME = "xyz.paphonb.mcShop";
    private JSONArray credentialsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setPreferenceScreen(createPreferenceHierarchy());
    }


    public SettingsFragment() {
        // Required empty public constructor
    }

    private PreferenceScreen createPreferenceHierarchy() {

        final Activity context = getActivity();

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(context);

        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(context);
        dialogBasedPrefCat.setTitle(R.string.settings_authentication);
        root.addPreference(dialogBasedPrefCat);

        ListPreference accountsPreference = new ListPreference(context);
        accountsPreference.setTitle(R.string.settings_accounts);
        accountsPreference.setSummary(R.string.settings_accounts_summary);
        accountsPreference.setDialogTitle(R.string.settings_accounts);
        accountsPreference.setDefaultValue(settings.getInt("currentCredential", 0) + "");

        Preference signoutPreference = new Preference(context);
        signoutPreference.setTitle(R.string.settings_signout);
        signoutPreference.setSummary(R.string.settings_signout_summary);

        List<String> accounts = new ArrayList<>();
        List<String> accountsIndex = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONArray credentials = null;
        try {
            credentials = (JSONArray) parser.parse(settings.getString("credentials", "[]"));
            for(int i = 0; i < credentials.size(); i++) {
                accounts.add((String)((JSONObject)credentials.get(i)).get("username") + " (" + (String)((JSONObject)credentials.get(i)).get("address") + ")");
                accountsIndex.add(i + "");
            }
            accounts.add(getString(R.string.settings_accounts_new));
            accountsIndex.add(credentials.size() + "");
        } catch (Exception e) {

        }

        accountsPreference.setEntries(accounts.toArray(new String[accounts.size()]));
        accountsPreference.setEntryValues(accountsIndex.toArray(new String[accountsIndex.size()]));
        dialogBasedPrefCat.addPreference(accountsPreference);

        dialogBasedPrefCat.addPreference(signoutPreference);

        credentialsList = credentials;
        accountsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Integer.parseInt((String) newValue) == credentialsList.size()) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                    getActivity().finish();
                    HomeActivity.getInstance().finish();
                } else {
                    final int newCurrentCredential = Integer.parseInt((String) newValue);

                    final ProgressDialog progress = ProgressDialog.show(context, "Please wait...",
                            "Switching account", true, false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String token = (String)((JSONObject)credentialsList.get(newCurrentCredential)).get("token");
                            String res = checkAuth(token);
                            if(res.equals("success")) {
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt("currentCredential", newCurrentCredential);
                                editor.commit();

                                List<NameValuePair> nameValuePairList = new ArrayList<>();
                                final String userInfo = McShop.postData(McShop.getCurrentCredential(context), "/api/user:shop", nameValuePairList);
                                runOnUiThread(context, new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Intent homeIntent = new Intent(context, HomeActivity.class);
                                        homeIntent.putExtra("userInfo", userInfo);
                                        startActivity(homeIntent);
                                        context.finish();
                                        HomeActivity.getInstance().finish();
                                    }
                                });
                            } else if(res.equals("error")) {
                                runOnUiThread(context, new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        HomeActivity.getInstance().finish();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("No connection.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //context.finish();
                                                        //HomeActivity.getInstance().finish();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
                            } else {
                                deleteCurrentCredential();
                                runOnUiThread(context, new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(mainIntent);
                                        getActivity().finish();
                                        HomeActivity.getInstance().finish();
                                    }
                                });
                            }
                        }
                    }).start();

                }
                return true;
            }
        });
        signoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.settings_signout)
                        .setMessage(R.string.settings_signout_content)
                        .setPositiveButton(R.string.settings_signout, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //server-side token deletion not implemented.
                                deleteCurrentCredential();
                                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                startActivity(mainIntent);
                                getActivity().finish();
                                HomeActivity.getInstance().finish();
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

        return root;
    }

    private void deleteCredential(int position) {
        //int position = settings.getInt("currentCredential", 0);
        JSONArray list = new JSONArray();
        JSONArray jsonArray = credentialsList;
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
        editor.putInt("currentCredential", 0);
        editor.commit();
    }

    private void deleteCurrentCredential() {
        int position = settings.getInt("currentCredential", 0);
        deleteCredential(position);
    }

    public static void runOnUiThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getMainLooper());

        mainHandler.post(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
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

}
