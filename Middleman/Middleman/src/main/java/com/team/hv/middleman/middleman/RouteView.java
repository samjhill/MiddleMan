package com.team.hv.middleman.middleman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Ben on 5/20/2014.
 */
public class RouteView extends Fragment {
    ArrayList<CraigslistItem> cartItems;
    ListView cartListView;
    ArrayAdapter<CraigslistItem> cartListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_view, container, false);

        cartItems = MiddleManMainActivity.itemsCart;

        cartListView = (ListView)view.findViewById(R.id.cartItemsListView);
        addCartItemsToListView();

        cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                Log.w("item", ""+position);
                //Toast.makeText(getBaseContext(), ""+position, Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                ItemView itemView = new ItemView();

                CraigslistItem thisItem = cartItems.get(position);
                bundle.putString("itemTitle",thisItem.itemTitle);
                Log.v("itemTitle", thisItem.itemTitle);
                bundle.putString("link", thisItem.link);
                Log.v("itemTitle", thisItem.link);
                bundle.putString("description", thisItem.description);
                Log.v("itemTitle", thisItem.description);
                bundle.putString("location", thisItem.location);
                Log.v("itemTitle", thisItem.location);
                bundle.putDouble("average", thisItem.average);
                Log.v("itemTitle", "" + thisItem.average);
                bundle.putDouble("profit", thisItem.expectedProfit);
                Log.v("itemTitle", "" + thisItem.expectedProfit);
                bundle.putDouble("price", thisItem.price);
                Log.v("itemTitle", "" + thisItem.price);
                bundle.putInt("index", position);
                bundle.putString("type","RouteView");
                itemView.setArguments(bundle);

                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, itemView);
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
            }
        });



        return view;
    }

    private void addCartItemsToListView() {
        cartListViewAdapter = new ArrayAdapter<CraigslistItem>(getActivity(), android.R.layout.simple_list_item_1, cartItems);
        cartListView.setAdapter(cartListViewAdapter);

        Log.v("cartListViewAdapter", "" + cartListViewAdapter.getCount());
        if (cartListViewAdapter.getCount()==0){
            Toast.makeText(getActivity(), "Cart is Empty", Toast.LENGTH_LONG).show();
        }
    }
    /*
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_view, container, false);
        final Bundle bundle = getArguments();
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

        cartItems = MiddleManMainActivity.itemsCart;


        Button listingButt = (Button)view.findViewById(R.id.viewListingButton);
        listingButt.setText("Add to Cart");
        listingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebsiteInDefaultBrowser((String)bundle.get("link"));
            }
        });

        ImageButton removeFromCart = (ImageButton)view.findViewById(R.id.removeOrAddToCartButton);
        removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivityAndRemoveThisItemFromCart();
            }
        });

        return view;
    }

    private void finishActivityAndRemoveThisItemFromCart() {
        MiddleManMainActivity.removeItemFromCart(position);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void showWebsiteInDefaultBrowser(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
    */
}
