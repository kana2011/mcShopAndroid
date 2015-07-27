package com.kana2011.mcshop.shop;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kana2011.mcshop.HomeActivity;
import com.kana2011.mcshop.R;
import com.kana2011.mcshop.SettingsActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ShopViewAdapter extends RecyclerView.Adapter<ShopViewAdapter.ShopItemViewHolder>{
    private JSONArray groupItems;

    public ShopViewAdapter(JSONArray groupItems) {
        this.groupItems = groupItems;
    }

    @Override
    public ShopItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ShopItemViewHolder viewHolder = new ShopItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShopItemViewHolder holder, int position) {
        JSONObject item = (JSONObject)groupItems.get(position);
        holder.mItemName.setText((String) item.get("dispname"));
        holder.itemInfo = (JSONObject)groupItems.get(position);
        //holder.mItemPhoto.setImageResource(0);
    }

    @Override
    public int getItemCount() {
        return groupItems.size();
    }

    public static class ShopItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView mCardView;
        TextView mItemName;
        JSONObject itemInfo;
        //ImageView mItemPhoto;

        ShopItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.cards_list);
            mItemName = (TextView)itemView.findViewById(R.id.item_name);
            //mItemPhoto = (ImageView)itemView.findViewById(R.id.item_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            System.out.println(getPosition());
            ShopDetailActivity.launch(HomeActivity.getInstance(), v.findViewById(R.id.item_photo), itemInfo);
        }
    }

}