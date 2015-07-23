package com.kana2011.mcshop;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class HomeActivity extends MaterialNavigationDrawer implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] drawerItems;
    private ActionBarDrawerToggle drawerListener;

    @Override
    public void init(Bundle savedInstanceState) {
        this.addSection(newSection("Shop", new ShopFragment()));
        allowArrowAnimation();
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        drawerItems = getResources().getStringArray(R.array.drawerItems);
        listView = (ListView)findViewById(R.id.drawerList);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerItems));
        listView.setOnItemClickListener(this);
    }
    */

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, drawerItems[position] + " was selected", Toast.LENGTH_LONG).show();
        selectItem(position);
    }

    public void selectItem(int position) {
        listView.setItemChecked(position, true);
        setTitle(drawerItems[position]);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
