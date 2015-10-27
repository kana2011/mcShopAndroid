package xyz.paphonb.mcshop.libs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.shop.ShopDetailActivity;
import xyz.paphonb.mcshop.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class McShop {
    private static String PREFS_NAME = "xyz.paphonb.mcShop";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static JSONArray getCredentials(Context context) {
        return getJsonArray(getPrefs(context).getString("credentials", "[]"));
    }

    public static JSONObject getCurrentCredential(Context context) {
        return (JSONObject) getCredentials(context).get(getPrefs(context).getInt("currentCredential", 0));
    }

    public static int getCurrentCredentialIndex(Context context) {
        return getPrefs(context).getInt("currentCredential", 0);
    }

    public static String getAddress(JSONObject credential) {
        return (String)credential.get("address");
    }

    public static JSONArray getJsonArray(String json) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONArray)parser.parse(json);
        } catch(Exception e) {
            return null;
        }
    }

    public static JSONObject getJsonObject(String json) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject)parser.parse(json);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String postData(JSONObject credential, String url, List<NameValuePair> nameValuePairList) {
        nameValuePairList.add(new BasicNameValuePair("token", (String)credential.get("token")));
        return Util.postData(credential.get("address") + url, nameValuePairList);
    }

    public static void saveUsername(Context context, String username) {
        SharedPreferences settings = getPrefs(context);
        JSONArray credentials = getCredentials(context);
        JSONObject credential = (JSONObject)credentials.get(getCurrentCredentialIndex(context));
        SharedPreferences.Editor editor = settings.edit();
        credential.put("username", username);
        editor.putString("credentials", credentials.toString());
        editor.commit();
    }

    public static class Shop {

        public static void buy(final ShopDetailActivity context, final int itemId, String dispname) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            final ProgressDialog progress = ProgressDialog.show(context, "Please wait...",
                                    "Processing request", true, false);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                                    nameValuePairList.add(new BasicNameValuePair("itemid", Integer.toString(itemId)));
                                    String fuck = postData(McShop.getCurrentCredential(context), "/api/shop:buy", nameValuePairList);
                                    JSONObject result = getJsonObject(postData(McShop.getCurrentCredential(context), "/api/shop:buy", nameValuePairList));
                                    if(result != null) {
                                        if ((boolean) result.get("status")) {
                                            Snackbar snackbar = Snackbar.make(context.getContentView(), "Item bought",
                                                    Snackbar.LENGTH_LONG);
                                            View snackBarView = snackbar.getView();
                                            snackBarView.setBackgroundColor(context.getResources().getColor(R.color.md_green_500));
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(context.getContentView(), capitalizeFirstLetter(lodashToSpace((String) result.get("error"))) + ".",
                                                    Snackbar.LENGTH_LONG);
                                            View snackBarView = snackbar.getView();
                                            snackBarView.setBackgroundColor(context.getResources().getColor(R.color.md_red_500));
                                            snackbar.show();
                                        }
                                    } else {
                                        Snackbar snackbar = Snackbar.make(context.getContentView(), "No connection.",
                                                Snackbar.LENGTH_LONG);
                                        View snackBarView = snackbar.getView();
                                        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.md_red_500));
                                        snackbar.show();
                                    }

                                    runOnUiThread(context, new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            progress.dismiss();
                                        }
                                    });
                                }
                            }).start();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Buy " + dispname + "?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public static void runOnUiThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getMainLooper());

        mainHandler.post(runnable);
    }

    public static String lodashToSpace(String str) {
        return str.replaceAll("_", " ");
    }

    public static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getCurrencyUnit() {
        return "THB";
    }
}
