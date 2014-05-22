package com.team.hv.middleman.middleman;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

//encapsulating class which stores the arraylist of cart items
class SaveCartItem implements Serializable {

    ArrayList<CraigslistItem> savedCart;

    public SaveCartItem(ArrayList<CraigslistItem> cart){
        savedCart = cart;
    }

    public ArrayList<CraigslistItem> getCart(){
        return savedCart;
    }
}

public class MiddleManMainActivity extends FragmentActivity {

    public static Double ebayAvg = 0.0;
    public static  Double ebayHighPrice = 0.0;
    public static Double ebayLowPrice = 0.0;

    public static ArrayList<CraigslistItem> craigsItems; //array of items returned from query to Craiglsist
    public static ArrayList<CraigslistItem> itemsCart; //items user has saved

    private static ProgressDialog dialog;
    private static ListView selectedItemsListView;
    private static ArrayAdapter listViewAdapter;

    private static Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_list_view);

        //Log.v("savedINstanceState",""+savedInstanceState.isEmpty());

        thisContext = getApplicationContext();

        if (savedInstanceState != null){ //if bundle contains something, get the cart
            SaveCartItem theCartContainer = (SaveCartItem) savedInstanceState.getSerializable("cart");
            itemsCart = theCartContainer.getCart();
        }else {
            itemsCart = new ArrayList<CraigslistItem>();
        }

        selectedItemsListView = (ListView)findViewById(R.id.selectedItemsListView);
        selectedItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                Log.w("item", ""+position);

                Bundle bundle = new Bundle();
                ItemView itemView = new ItemView();

                // get the clicked item's information
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

                //start new item view fragment for this item
                startFragment(itemView);
            }
        });

        craigsItems = new ArrayList<CraigslistItem>();
        dialog = new ProgressDialog(this);

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                craigsItems.clear();
                checkInputs();
            }
        });
        //display message onload to show users they have to input a location and item
        checkInputs();
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

    //start the passed in fragment
    private void startFragment(Fragment fragToPassIn) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragToPassIn);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    //check inputs and display Toast if an edit text is blank
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
        } else { // both edit texts have values
            searchCraigslistAndEBay(item, location);
        }
    }

    // get the start a new XmlReader, passing in the user specified location and item
    public void searchCraigslistAndEBay(String itemName, String location) {
        Object[] params = {itemName, location, this.getContext()};
        XmlReader reader = new XmlReader();
        reader.execute(params);
    }

    //display the loading dialog while app is processing Xml Docs from Craigslist and eBay
    public static void displayLoadingDialog() {
        dialog.setMessage("Loading data from Craigslist and eBay. Sit tight, grab a cold drink.");
        dialog.show();
    }

    // close the loading dialog
    public static void closeLoadingDialog() {
        addCraigsItemsToListView();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }
    //add returned craigslist items to listview
    private static void addCraigsItemsToListView() {
        //erase the list view
        ArrayList<String> empty = new ArrayList<String>();
        selectedItemsListView.setAdapter(new ArrayAdapter<String>(thisContext, android.R.layout.simple_list_item_1, empty));
        //update the listview
        listViewAdapter = new ArrayAdapter<CraigslistItem>(thisContext, android.R.layout.simple_list_item_1, craigsItems);
        selectedItemsListView.setAdapter(listViewAdapter);

        Log.v("ListViewAdapter", ""+listViewAdapter.getCount());
        if (listViewAdapter.getCount()==0){ //if no results were returned
            Toast.makeText(thisContext, "No results returned",Toast.LENGTH_LONG).show();
        }
    }

    // remove the indicated item from the cart
    public static void removeItemFromCart(int index){
        CraigslistItem itemToSearch = craigsItems.get(index);
        for (int i = 0; i<itemsCart.size(); i++){
            if (itemToSearch.itemTitle.equals(itemsCart.get(i).itemTitle)){
                itemsCart.remove(i);
                break;
            }
        }
    }

    //add the item in the craigslist item array atthe given index to the cart
    public static boolean addItemToCart(int index){
        if (!itemsCart.contains(craigsItems.get(index))) {
            itemsCart.add(craigsItems.get(index));
            return true;
        } else {
            return false;
        }
    }

    //onPause, save the cart
    @Override
    protected void onPause(){
        Bundle saveBundle = new Bundle();
        saveBundle.putSerializable("cart",new SaveCartItem(itemsCart));
        onSaveInstanceState(saveBundle);
    }

    public Context getContext() {
        return this;
    }


}

