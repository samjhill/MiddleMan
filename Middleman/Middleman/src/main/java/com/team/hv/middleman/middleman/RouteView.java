package com.team.hv.middleman.middleman;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Ben on 5/20/2014.
 */
public class RouteView extends Fragment {
    ArrayList<CraigslistItem> cartItems;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_view, container, false);

        cartItems = MiddleManMainActivity.itemsCart;


        Button listingButt = (Button)view.findViewById(R.id.viewListingButton);
        listingButt.setText("Add to Cart");
        listingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebsiteInDefaultBrowser((String)bundle.get("link"));
            }
        });

        return view;
    }

    private void finishActivityAndRemoveThisItemFromCart() {
        MiddleManMainActivity.removeItemFromCart(position);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void removeThisItemFromCart(int index){
        cartItems.remove(index);
        MiddleManMainActivity.removeItemFromCart(index);
    }
}
