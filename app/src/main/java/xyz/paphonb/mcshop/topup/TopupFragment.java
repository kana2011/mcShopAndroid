package xyz.paphonb.mcshop.topup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.paphonb.mcshop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopupFragment extends Fragment {


    public TopupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topup, container, false);
    }


}
