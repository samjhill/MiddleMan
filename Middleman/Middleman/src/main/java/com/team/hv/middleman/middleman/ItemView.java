package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ben on 5/19/2014.
 */
public class ItemView extends Fragment {
    private String itemTitle;
    private String link;
    private Double price;
    private String description;
    private String location;
    private Double average;
    private Double expectedProfit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        Log.v("onCreateView","Called");
        Log.v("Bundle size",""+getArguments().size());
        Bundle bundle = getArguments();
        //super.onCreateView(savedInstanceState);
        View view = inflater.inflate(R.layout.item_view, container, false);

        TextView titleView = (TextView)view.findViewById(R.id.itemNameTextView);
        titleView.setText((String)bundle.get("itemTitle"));

        TextView descView = (TextView)view.findViewById(R.id.itemDescTextView);
        descView.setText((String)bundle.get("description"));

        TextView locationView = (TextView)view.findViewById(R.id.itemLocationTextView);
        locationView.setText((String)bundle.get("location"));

        //TextView linkView = (TextView)view.findViewById(R.id.itemNameTextView);
        //linkView.setText((String)bundle.get("link"));

        TextView priceView = (TextView)view.findViewById(R.id.itemOfferedPriceTextView);
        priceView.setText("$"+bundle.get("price"));

        TextView avgView = (TextView)view.findViewById(R.id.itemAveragePriceTextView);
        avgView.setText("$"+bundle.get("average"));

        TextView profitView = (TextView)view.findViewById(R.id.itemExpectedProfitTextView);
        profitView.setText("$"+bundle.get("profit"));



        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.v("Bundle size onCreate",""+getArguments().size());
        super.onCreate(savedInstanceState);
        Log.v("Bundle size onCreate",""+getArguments().size());
        Log.v("onCreate","Called");
    }

}
