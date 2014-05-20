package com.team.hv.middleman.middleman;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.team.hv.middleman.middleman.XmlReader;

public class MiddleManMainActivity extends FragmentActivity {

    public static boolean clComplete = false;
    public static boolean ebayComplete = false;

    public static Double ebayAvg = 0.0;
    public static  Double ebayHighPrice = 0.0;
    public static Double ebayLowPrice = 0.0;

    public static ArrayList<CraigslistItem> craigsItems;
    public static ArrayList<CraigslistItem> itemsCart;

    private static ProgressDialog dialog;
    private static ListView selectedItemsListView;
    private static ArrayAdapter listViewAdapter;


    private static Context thisContext;

    private static View thisLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_middle_man_main);
        setContentView(R.layout.results_list_view);
        //setContentView(R.layout.default_empty_view);


        thisContext = getApplicationContext();
        thisLayout = getWindow().getDecorView();

        itemsCart = new ArrayList<CraigslistItem>();

        selectedItemsListView = (ListView)findViewById(R.id.selectedItemsListView);
        selectedItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                Log.w("item", ""+position);
                //Toast.makeText(getBaseContext(), ""+position, Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                ItemView itemView = new ItemView();

                CraigslistItem thisItem = craigsItems.get(position);
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
                bundle.putString("type","ItemView");
                itemView.setArguments(bundle);

                startFragment(itemView);
            }
        });

        craigsItems = new ArrayList<CraigslistItem>();
        dialog = new ProgressDialog(this);
        Log.v("Starting:","it's goin");

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                craigsItems.clear();
                checkInputs();
            }
        });
        checkInputs();
        //searchCraigslistAndEBay("ipod", "Rochester");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.default_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_cart:
                startFragment(new RouteView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startFragment(Fragment fragToPassIn) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragToPassIn);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        EditText locationField = (EditText)findViewById(R.id.locationEditText);
        EditText itemField = (EditText)findViewById(R.id.itemEditText);
        String location = locationField.getText().toString().trim();
        String item = itemField.getText().toString().trim();

        if ( (location.equals("") || location == null) && (item.equals("")||item == null)){
            Toast.makeText(this, "A location and item to search are required", Toast.LENGTH_LONG).show();
        } else if (location.equals("") || location == null) {
            Toast.makeText(this, "A location is required", Toast.LENGTH_LONG).show();
        } else if (item.equals("")||item == null){
            Toast.makeText(this, "An item name to search is required", Toast.LENGTH_LONG).show();
        } else {
            searchCraigslistAndEBay(item, location);
        }
    }

    public void searchCraigslistAndEBay(String itemName, String location) {
        ebayComplete = false;
        Object[] params = {itemName, location, this.getContext()};
        XmlReader reader = new XmlReader();
        reader.execute(params);
        //clComplete = false;
        //CraigslistXmlReader cxr = new CraigslistXmlReader();
        //cxr.execute(params);

        //while (!clComplete && !ebayComplete){

        //}



        Log.v("Both are done", "DONE!");
    }

    public Context getContext() {
        return this;
    }

    public static void displayLoadingDialog() {
        dialog.setMessage("Loading data from Craigslist and eBay. Sit tight, grab a cold drink.");
        dialog.show();
    }

    public static void closeLoadingDialog() {
        addCraigsItemsToListView();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private static void addCraigsItemsToListView() {
        ArrayList<String> empty = new ArrayList<String>();
        selectedItemsListView.setAdapter(new ArrayAdapter<String>(thisContext, android.R.layout.simple_list_item_1, empty));
        listViewAdapter = new ArrayAdapter<CraigslistItem>(thisContext, android.R.layout.simple_list_item_1, craigsItems);
        selectedItemsListView.setAdapter(listViewAdapter);

        Log.v("ListViewAdapter", ""+listViewAdapter.getCount());
        if (listViewAdapter.getCount()==0){
            Toast.makeText(thisContext, "No results returned",Toast.LENGTH_LONG).show();
        }
    }

    public static void removeItemFromCart(int index){
        itemsCart.remove(index);
    }

    public static boolean addItemToCart(int index){
        if (!itemsCart.contains(craigsItems.get(index))) {
            itemsCart.add(craigsItems.get(index));
            return true;
        } else {
            return false;
        }
    }


}

