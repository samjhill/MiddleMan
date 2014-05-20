package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.item_view, container, false);

        TextView titleView = (TextView)view.findViewById(R.id.itemNameTextView);
        titleView.setText((String)savedInstanceState.get("itemTitle"));

        TextView descView = (TextView)view.findViewById(R.id.itemDescTextView);
        descView.setText((String)savedInstanceState.get("description"));

        TextView locationView = (TextView)view.findViewById(R.id.itemLocationTextView);
        locationView.setText((String)savedInstanceState.get("location"));

        //TextView linkView = (TextView)view.findViewById(R.id.itemNameTextView);
        //linkView.setText((String)savedInstanceState.get("link"));

        TextView priceView = (TextView)view.findViewById(R.id.itemOfferedPriceTextView);
        priceView.setText((String)savedInstanceState.get("price"));

        TextView avgView = (TextView)view.findViewById(R.id.itemAveragePriceTextView);
        avgView.setText((String)savedInstanceState.get("average"));

        TextView profitView = (TextView)view.findViewById(R.id.itemProfitTextView);
        profitView.setText((String)savedInstanceState.get("profit"));



        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){

    }
}
