package com.kana2011.mcshop.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kana2011.mcshop.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

public class ShopDetailActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ImageView mItemPhoto;
    private TextView mItemName;
    private JSONObject itemInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        itemInfo = null;
        JSONParser parser = new JSONParser();
        try {
            itemInfo = (JSONObject)parser.parse(getIntent().getStringExtra("itemInfo"));
        } catch(Exception e) {

        }

        toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels;

        mItemPhoto = (ImageView)findViewById(R.id.item_photo);
        mItemPhoto.getLayoutParams().width = (int)dpWidth;
        mItemPhoto.getLayoutParams().height = (int)dpWidth;

        mItemName = (TextView)findViewById(R.id.item_name);
        mItemName.setText((String)itemInfo.get("dispname"));

        ViewCompat.setTransitionName(mItemPhoto, "item_photo");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_detail, menu);
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

    public static void launch(Activity activity, View photoView, JSONObject itemInfo) {
        //ActivityOptionsCompat options =
        //        ActivityOptionsCompat.makeSceneTransitionAnimation(
        //                activity, transitionView, EXTRA_IMAGE);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, Pair.create(photoView, "item_photo"));
        Intent intent = new Intent(activity, ShopDetailActivity.class);
        intent.putExtra("itemInfo", itemInfo.toString());
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
