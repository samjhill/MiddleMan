package com.team.hv.middleman.middleman;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Ben on 5/20/2014.
 */
public class RouteView extends Fragment {
    ArrayList<CraigslistItem> cartItems;
    ListView cartListView;
    ImageView mapImageView;
    ArrayAdapter<CraigslistItem> cartListViewAdapter;
    private double totalCost;
    private double totalProfit;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_view, container, false);
        cartItems = MiddleManMainActivity.itemsCart;
        totalCost = 0;
        totalProfit = 0;
        for(int i = 0; i < cartItems.size(); i++){
            totalCost += cartItems.get(i).price;
            totalProfit += cartItems.get(i).expectedProfit;
        }
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
        TextView totalCostTextView = (TextView) view.findViewById(R.id.costUpfrontTextview);
        totalCostTextView.setText("Upfront cost: " + n.format(totalCost));
        TextView totalProfitTextView = (TextView) view.findViewById(R.id.estimatedProfitTextView);
        totalProfitTextView.setText("Total profit: " + n.format(totalProfit) + "");
        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapImageView);

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
}
