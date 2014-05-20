package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Ben on 5/19/2014.
 */
public class ItemView extends android.support.v4.app.Fragment {
    private int position;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        Log.v("onCreateView","Called");
        Log.v("Bundle size",""+getArguments().size());
        final Bundle bundle = getArguments();
        //super.onCreateView(savedInstanceState);
        View view = inflater.inflate(R.layout.item_view, container, false);

        position = (Integer)bundle.get("index");

        TextView titleView = (TextView)view.findViewById(R.id.itemNameTextView);
        titleView.setText((String)bundle.get("itemTitle"));

        TextView descView = (TextView)view.findViewById(R.id.itemDescTextView);
        descView.setText((String)bundle.get("description"));

        TextView locationView = (TextView)view.findViewById(R.id.itemLocationTextView);
        locationView.setText((String)bundle.get("location"));

        //TextView linkView = (TextView)view.findViewById(R.id.itemNameTextView);
        //linkView.setText((String)bundle.get("link"));

        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);

        TextView priceView = (TextView)view.findViewById(R.id.itemOfferedPriceTextView);
        priceView.setText(n.format(bundle.get("price")));

        TextView avgView = (TextView)view.findViewById(R.id.itemAveragePriceTextView);
        avgView.setText(n.format(bundle.get("average")));

        TextView profitView = (TextView)view.findViewById(R.id.itemExpectedProfitTextView);
        profitView.setText(n.format(bundle.get("profit")));

        Button listingButt = (Button)view.findViewById(R.id.viewListingButton);
        listingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebsiteInDefaultBrowser((String)bundle.get("link"));
            }
        });

        Button removeItemButt = (Button)view.findViewById(R.id.removeThisItemButton);
        removeItemButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivityAndRemoveThisItem();
            }
        });



        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.v("Bundle size onCreate",""+getArguments().size());
        super.onCreate(savedInstanceState);
        Log.v("Bundle size onCreate",""+getArguments().size());
        Log.v("onCreate","Called");
    }

    private void showWebsiteInDefaultBrowser(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private void finishActivityAndRemoveThisItem() {
        MiddleManMainActivity.removeThisItemFromListView(position);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}
