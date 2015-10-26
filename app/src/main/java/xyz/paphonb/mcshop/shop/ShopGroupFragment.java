package xyz.paphonb.mcshop.shop;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.paphonb.mcshop.R;
import xyz.paphonb.mcshop.libs.DividerItemDecoration;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopGroupFragment extends Fragment {
    private JSONObject groupInfo;
    private RecyclerView mRecyclerView;

    public ShopGroupFragment() {
        // Required empty public constructor
    }

    public void setGroupInfo(JSONObject info) {
        this.groupInfo = info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            JSONParser parser = new JSONParser();
            try {
                groupInfo = (JSONObject)parser.parse(savedInstanceState.getString("groupInfo", "{}"));
            } catch (Exception e) {
                getActivity().finish();
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_shop_group,container, false);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.cards_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(layoutManager);

        ShopViewAdapter shopViewAdapter = new ShopViewAdapter((JSONArray)groupInfo.get("items"));
        mRecyclerView.setAdapter(shopViewAdapter);

        //int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        //mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        JSONArray items = (JSONArray)groupInfo.get("items");
        for(int i = 0; i < items.size(); i++) {
            JSONObject item = (JSONObject)items.get(i);
            //BasicButtonsCard card = new BasicButtonsCard(getActivity());
            //card.setTitle((String)item.get("dispname"));
            //card.setLeftButtonText("Buy for " + item.get("price"));
            //mListView.add(card);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupInfo", groupInfo.toString());
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

}
