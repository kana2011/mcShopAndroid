package xyz.paphonb.mcshop.shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.paphonb.mcshop.HomeActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.libs.McShop;

public class ShopViewAdapter extends RecyclerView.Adapter<ShopViewAdapter.ShopItemViewHolder>{
    private final Bitmap blankBmp;
    private JSONArray groupItems;

    public ShopViewAdapter(JSONArray groupItems) {
        this.groupItems = groupItems;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        blankBmp = Bitmap.createBitmap(1, 1, conf);
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
        holder.mItemPhoto.setImageBitmap(blankBmp);
        new DownloadImageTask(holder).execute(McShop.getAddress(McShop.getCurrentCredential(HomeActivity.getInstance())) + "/assets/item/" + item.get("id") + ".jpg");
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
        ImageView mItemPhoto;
        private Bitmap imageBitmap;

        ShopItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.cards_list);
            mItemName = (TextView)itemView.findViewById(R.id.item_name);
            mItemPhoto = (ImageView)itemView.findViewById(R.id.item_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.getInstance(), ShopDetailActivity.class);
            intent.putExtra("itemInfo", itemInfo.toString());
            intent.putExtra("itemPhoto", imageBitmap);
            HomeActivity.getInstance().startActivity(intent);
        }

        public void setImageBitmap(Bitmap imageBitmap) {
            this.imageBitmap = imageBitmap;
            mItemPhoto.setImageBitmap(imageBitmap);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ShopItemViewHolder holder;

        public DownloadImageTask(ShopItemViewHolder holder) {
            this.holder = holder;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            holder.setImageBitmap(result);
        }
    }

}