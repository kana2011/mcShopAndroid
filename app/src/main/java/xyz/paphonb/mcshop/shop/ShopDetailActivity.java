package xyz.paphonb.mcshop.shop;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.libs.McShop;

public class ShopDetailActivity extends AppCompatActivity {

    private JSONObject itemInfo;
    private String itemName;
    private Button mBuyButton;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContentView = findViewById(android.R.id.content);

        itemInfo = null;
        JSONParser parser = new JSONParser();
        try {
            itemInfo = (JSONObject)parser.parse(getIntent().getStringExtra("itemInfo"));
        } catch(Exception e) {

        }

        itemName = (String)itemInfo.get("dispname");

        getSupportActionBar().setTitle(itemName);

        final ShopDetailActivity context = this;
        mBuyButton = (Button)findViewById(R.id.buy_button);
        if((int)(long)itemInfo.get("price") != 0) {
            mBuyButton.setText(McShop.getCurrencyUnit() + Integer.toString((int) (long) itemInfo.get("price")));
        }
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                McShop.Shop.buy(context, (int) (long) itemInfo.get("id"), (String) itemInfo.get("dispname"));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public View getContentView() {
        return mContentView;
    }
}
