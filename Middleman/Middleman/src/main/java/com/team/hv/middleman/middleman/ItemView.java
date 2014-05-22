package com.team.hv.middleman.middleman;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Ben on 5/19/2014.
 * A view showing a detailed look at a returned item
 */
public class ItemView extends android.support.v4.app.Fragment {
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.v("Bundle size onCreate",""+getArguments().size());
        Log.v("Bundle size onCreate",""+getArguments().size());
        Log.v("onCreate", "Called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        Log.v("onCreateView","Called");
        Log.v("Bundle size",""+getArguments().size());
        final Bundle bundle = getArguments();
        //super.onCreateView(savedInstanceState);
        View view = inflater.inflate(R.layout.item_view, container, false);

        //get information on the item from the args
        position = (Integer)bundle.get("index");

        TextView titleView = (TextView)view.findViewById(R.id.itemNameTextView);
        titleView.setText((String)bundle.get("itemTitle"));

        TextView descView = (TextView)view.findViewById(R.id.itemDescTextView);
        descView.setText((String)bundle.get("description"));

        TextView locationView = (TextView)view.findViewById(R.id.itemLocationTextView);
        locationView.setText((String)bundle.get("location"));

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
                if (bundle.get("type").equals("RouteView")) {//if accessed from the route view class
                    new MyDialogFragment().show(getActivity().getFragmentManager(), "MyDialog");

                } else {
                    MiddleManMainActivity.removeItemFromCart(position);
                    removeFromCartButt.setVisibility(View.GONE);
                    addItemToCartButt.setVisibility(View.VISIBLE);
                }
            }
        });

        addItemToCartButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleManMainActivity.addItemToCart(position);
                removeFromCartButt.setVisibility(View.VISIBLE);
                addItemToCartButt.setVisibility(View.GONE);
                popThisStack();
            }
        });

        if (bundle.get("type").equals("RouteView") || isInCart((String)bundle.get("itemTitle"))){
            view.findViewById(R.id.addToCartButton).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.removeFromCartButton).setVisibility(View.GONE);
        }

        return view;
    }

    //open up the passed in uri in the device's default web browser
    private void showWebsiteInDefaultBrowser(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    //see if this item, represented by the passed in title, is in the cart
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

    //pop the backstack, removing this fragment from view
    protected void popThisStack(){
        getFragmentManager().popBackStack();
    }

    // From http://stackoverflow.com/questions/12912181/simplest-yes-no-dialog-fragment
    //show a confirm/cancel dialog when removing an item from the cart (when viewing this ItemView from the RouteView layout)
    class  MyDialogFragment extends DialogFragment{
        public MyDialogFragment() {
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setTitle("Remove this item from cart?");
            alertDialogBuilder.setMessage("Are you sure you want to remove this item from the cart?");
            //null should be your on click listener
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MyDialogFragment)getFragmentManager().findFragmentByTag("MyDialog")).getDialog().dismiss();
                    MiddleManMainActivity.removeItemFromCart(position);
                    popThisStack();
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MyDialogFragment)getFragmentManager().findFragmentByTag("MyDialog")).getDialog().dismiss();
                }
            });

            return alertDialogBuilder.create();
        }
    }

}
