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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Ben on 5/19/2014.
 */
public class ItemView extends android.support.v4.app.Fragment {
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.v("Bundle size onCreate",""+getArguments().size());
        super.onCreate(savedInstanceState);
        Log.v("Bundle size onCreate",""+getArguments().size());
        Log.v("onCreate", "Called");
    }

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
                showWebsiteInDefaultBrowser((String) bundle.get("link"));
            }
        });

        final ImageButton removeFromCartButt = (ImageButton)view.findViewById(R.id.removeFromCartButton);
        final ImageButton addItemToCartButt = (ImageButton)view.findViewById(R.id.addToCartButton);

        removeFromCartButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManMainActivity.removeItemFromCart(position);
                //if (bundle.get("type").equals("ItemView")) {

                    /*
                    if (!MiddleManMainActivity.addItemToCart(position)){
                        Toast.makeText(getActivity(), "Item already in cart", Toast.LENGTH_LONG).show();
                    }*/
                //}
                if (bundle.get("type").equals("RouteView")) {
                    getFragmentManager().popBackStack();
                } else {
                    removeFromCartButt.setVisibility(View.INVISIBLE);
                    addItemToCartButt.setVisibility(View.VISIBLE);
                }
            }
        });

        addItemToCartButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManMainActivity.addItemToCart(position);
                removeFromCartButt.setVisibility(View.VISIBLE);
                addItemToCartButt.setVisibility(View.INVISIBLE);
            }
        });

        if (bundle.get("type").equals("RouteView") || isInCart((String)bundle.get("itemTitle"))){
            view.findViewById(R.id.addToCartButton).setVisibility(View.INVISIBLE);
        } else {
            view.findViewById(R.id.removeFromCartButton).setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void showWebsiteInDefaultBrowser(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private void removeThis() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void finishActivityAndRemoveThisItemFromCart() {
        MiddleManMainActivity.removeItemFromCart(position);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private boolean isInCart (String thisTitle)
    {
        for (CraigslistItem thisItem : MiddleManMainActivity.itemsCart)
        {
            String cartItemTitle = thisItem.itemTitle;
            if (cartItemTitle.equals(thisTitle))
                return true;
        }
        return false;
    }

}
