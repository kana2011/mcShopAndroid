package com.kana2011.mcshop.drawer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kana2011.mcshop.HomeActivity;
import com.kana2011.mcshop.R;
import com.kana2011.mcshop.SettingsActivity;
import com.kana2011.mcshop.SettingsFragment;

import java.util.Collections;
import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private final LayoutInflater inflator;
    List<DrawerItem> data = Collections.emptyList();
    private Context context;

    public DrawerAdapter(Context context, List<DrawerItem> data) {
        this.context = context;
        inflator = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.drawer_row, parent, false);
        DrawerViewHolder holder = new DrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        DrawerItem current = data.get(position);
        holder.mTitleView.setText(current.title);
        holder.mIconView.setImageResource(current.iconId);
        if(position == 0) {
            holder.mItemView.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleView;
        ImageView mIconView;
        View mItemView;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mTitleView = (TextView)itemView.findViewById(R.id.drawer_list_text);
            mIconView = (ImageView)itemView.findViewById(R.id.drawer_list_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(getPosition()) {
                case 4:
                    Intent settingsIntent = new Intent(HomeActivity.getInstance(), SettingsActivity.class);
                    HomeActivity.getInstance().startActivity(settingsIntent);
                    break;
                default:
                    HomeActivity.getInstance().showFragment(getPosition());
                    break;
            }
            HomeActivity.getInstance().getDrawerFragment().getDrawerLayout().closeDrawers();
        }
    }
}
