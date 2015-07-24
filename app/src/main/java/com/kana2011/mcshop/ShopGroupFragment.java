package com.kana2011.mcshop;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.view.MaterialListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopGroupFragment extends Fragment {
    private JSONObject groupInfo;
    private MaterialListView mListView;

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

        mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);

        JSONArray items = (JSONArray)groupInfo.get("items");
        for(int i = 0; i < items.size(); i++) {
            JSONObject item = (JSONObject)items.get(i);
            BasicButtonsCard card = new BasicButtonsCard(getActivity());
            card.setTitle((String)item.get("dispname"));
            card.setLeftButtonText("Buy for " + item.get("price"));
            mListView.add(card);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupInfo", groupInfo.toString());
    }


}
